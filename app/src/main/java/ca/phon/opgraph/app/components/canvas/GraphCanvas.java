/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.phon.opgraph.app.components.canvas;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.beans.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.undo.CompoundEdit;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.components.*;
import ca.phon.opgraph.app.components.canvas.CanvasNodeField.AnchorFillState;
import ca.phon.opgraph.app.edits.graph.*;
import ca.phon.opgraph.app.edits.notes.ResizeNoteEdit;
import ca.phon.opgraph.app.extensions.*;
import ca.phon.opgraph.app.util.*;
import ca.phon.opgraph.dag.*;
import ca.phon.opgraph.exceptions.ItemMissingException;
import ca.phon.opgraph.extensions.*;
import ca.phon.opgraph.extensions.Publishable.*;
import ca.phon.ui.jbreadcrumb.*;
import ca.phon.ui.jbreadcrumb.BreadcrumbEvent.BreadcrumbEventType;

/**
 * A canvas for creating/modifying an {@link OpGraph}.
 * 
 * TODO Autoscrolling when moving a node, or moving/resizing notes, or really
 *      anytime when dealing with children components
 *      
 * XXX Use a delegate for handling some of this class' inner functionality.
 *     Some good places would be having the delegate handling the double
 *     clicking of nodes (specifically, macros). Then we could extract the
 *     breadcrumb from this class also, which doesn't really feel like it
 *     belongs here. This class should never set the model on itself.
 */
public class GraphCanvas extends JLayeredPane implements ClipboardOwner, Scrollable {
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(GraphCanvas.class.getName());
	
	/** uiClassID */
	private static final String uiClassID = "GraphCanvasUI";

	/** The document model this canvas uses */
	private GraphDocument document;

	/** The mapping of nodes to node components */
	private HashMap<OpNode, CanvasNode> nodes;
	
	private final static float DEFAULT_ZOOM_LEVEL = 1.0f;
	private final static float MINIMUM_ZOOM_LEVEL = 0.2f;
	private final static float MAXIMUM_ZOOM_LEVEL = 2.0f;
	/**
	 * Zoom level
	 */
	private float zoomLevel = DEFAULT_ZOOM_LEVEL;
	
	/**
	 * Minimap component
	 */
	private CanvasMinimap minimap;
	
	//
	// Listener objects
	//
	private class MetaListener implements PropertyChangeListener {
		private OpNode node;

		public MetaListener(OpNode node) {
			this.node = node;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getSource() instanceof NodeMetadata) {
				final NodeMetadata meta = (NodeMetadata)evt.getSource();
				final CanvasNode canvasNode = nodes.get(node);
				if(canvasNode != null) {
					if(evt.getPropertyName().equals(NodeMetadata.LOCATION_PROPERTY)) {
						canvasNode.setLocation(meta.getX(), meta.getY());
					} else if(evt.getPropertyName().equals(NodeMetadata.DEFAULTS_PROPERTY)) {
						updateAnchorFillStates(node);
					}
				}
			}
		}
	};

	/**
	 * Constructs a canvas that displays a given graph model.
	 * 
	 * @param model  the graph model
	 */
	public GraphCanvas(GraphDocument document) {
		super();
		updateUI();

		// Class setup
		this.nodes = new HashMap<OpNode, CanvasNode>();

		this.document = document;
		this.document.getBreadcrumb().addBreadcrumbListener(breadcrumbListener);
		this.document.getSelectionModel().addSelectionListener(canvasSelectionListener);
		this.document.addPropertyChangeListener("anchorFillStates", (e) -> updateAnchorFillStates((OpNode)e.getNewValue()) );
		this.document.getUndoSupport().addUndoableEditListener( (e) -> 
			SwingUtilities.invokeLater( () -> getUI().getMinimapLayer().update() ));
		
		changeGraph(null, this.document.getGraph());
	}
	
	/**
	 * Get the UI object.
	 */
	public GraphCanvasUI getUI() {
		return (GraphCanvasUI)ui;
	}
	
	/**
	 * Set the component UI.
	 */
	public void setUI(GraphCanvasUI ui) {
		super.setUI(ui);
	}
	
	/**
	 * Update UI
	 */
	@Override
	public void updateUI() {
		setUI(new DefaultGraphCanvasUI(this));
	}

	/**
	 * Get the zoom level for the canvas
	 */
	public float getZoomLevel() {
		return this.zoomLevel;
	}
	
	/**
	 * Set the zoom level for the canvas. The
	 * input value is constrained to the allowed range.
	 * 
	 * @param zoomLevel
	 */
	public void setZoomLevel(float zoomLevel) {
		float oldVal = this.zoomLevel;
		this.zoomLevel = zoomLevel;
		if(this.zoomLevel < MINIMUM_ZOOM_LEVEL)
			this.zoomLevel = MINIMUM_ZOOM_LEVEL;
		if(this.zoomLevel > MAXIMUM_ZOOM_LEVEL)
			this.zoomLevel = MAXIMUM_ZOOM_LEVEL;
		firePropertyChange("zoomLevel", oldVal, this.zoomLevel);
	}
	
	/**
	 * Gets the selection model this canvas is using.
	 * 
	 * @return the selection model 
	 */
	public GraphCanvasSelectionModel getSelectionModel() {
		return document.getSelectionModel();
	}

	/**
	 * Gets the document this canvas is editing.
	 * 
	 * @return the document
	 */
	public GraphDocument getDocument() {
		return document;
	}

	/**
	 * Gets a mapping from {@link OpNode} to the respective node
	 * component that displays that node.
	 * 
	 * @return the mapping
	 */
	public Map<OpNode, CanvasNode> getNodeMap() {
		return Collections.unmodifiableMap(nodes);
	}

	/**
	 * Gets the node displaying the given node. 
	 * 
	 * @param node  the node
	 * 
	 * @return the node displaying the given node, or <code>null</code>
	 *         if no such node exists
	 */
	public CanvasNode getNode(OpNode node) {
		return nodes.get(node);
	}

	/**
	 * Updates the debug state for this canvas. Currently, the debug state
	 * simply highlights the node being processed.
	 * 
	 * @param context  the processing context, or <code>null</code> if no debugging
	 */
	public void updateDebugState(Processor context) {
		if(context == null) {
			setEnabled(true);
		} else {
			setEnabled(false);

			if(document.getBreadcrumb().containsState(context.getGraph())) {
				document.getBreadcrumb().gotoState(context.getGraph());
			} else {
				// Given the current processing context, find the path that
				// gets to the current node
				final LinkedList<Breadcrumb.EntrySet<OpGraph, String>> path = new LinkedList<Breadcrumb.EntrySet<OpGraph, String>>();

				String id = context.getGraphOfContext().getId();
				Processor activeContext = context;
				while(activeContext != null) {
					final OpGraph graph = activeContext.getGraphOfContext();
					path.addLast(new Breadcrumb.EntrySet<OpGraph, String>(graph, id));

					if(activeContext.getCurrentNodeOfContext() != null)
						id = activeContext.getCurrentNodeOfContext().getName();
					else
						id = "Unknown";

					activeContext = activeContext.getMacroContext();
				}

				document.getBreadcrumb().set(path);
			}

			getSelectionModel().setSelectedNode(context.getCurrentNode());
		}

		repaint();
	}

	/**
	 * Updates the fill states of all anchors for a given node.
	 * 
	 * @param node  the node to update
	 */
	public void updateAnchorFillStates(OpNode node) {
		final CanvasNode canvasNode = nodes.get(node);
		if(canvasNode != null) {
			final Map<ContextualItem, CanvasNodeField> fields = canvasNode.getFieldsMap();
			final NodeMetadata meta = node.getExtension(NodeMetadata.class);
			final Publishable publishable = document.getGraph().getExtension(Publishable.class);

			for(InputField field : node.getInputFields()) {
				final CanvasNodeField canvasField = fields.get(field);
				if(canvasField != null) {
					// Check to see if we should fill for a published field first
					boolean isPublished = false;
					if(publishable != null) {
						for(PublishedInput input : publishable.getPublishedInputs()) {
							if(node == input.destinationNode && field.equals(input.nodeInputField)) {
								canvasField.updateAnchorFillState(AnchorFillState.PUBLISHED);
								isPublished = true;
								break;
							}
						}
					}

					// It's not published, so is there a default value?
					if(!isPublished) {
						if(meta == null || meta.getDefault(field) == null)
							canvasField.updateAnchorFillState(AnchorFillState.NONE);
						else
							canvasField.updateAnchorFillState(AnchorFillState.DEFAULT);	
					}
				}
			}

			// Fill for published output fields
			if(publishable != null) {
				for(OutputField field : node.getOutputFields()) {
					final CanvasNodeField canvasField = fields.get(field);
					if(canvasField != null) {
						for(PublishedOutput output : publishable.getPublishedOutputs()) {
							if(node == output.sourceNode && field == output.nodeOutputField) {
								canvasField.updateAnchorFillState(AnchorFillState.PUBLISHED);
								break;
							}
						}
					}
				}
			}
		}
	}

	//
	// Overrides
	//

	@Override
	public void setLayout(LayoutManager mgr) {
		if(mgr != null && !(mgr instanceof NullLayout))
			throw new UnsupportedOperationException("GraphCanvas cannot use a custom layout");
		super.setLayout(mgr);
	}

	/**
	 * Switches the graph this canvas is viewing.
	 * 
	 * @param oldGraph  the graph that was displayed previously
	 * @param graph  the new graph to display
	 */
	protected void changeGraph(OpGraph oldGraph, OpGraph graph) {
		synchronized(getTreeLock()) {
			// Remove old components
			if(oldGraph != null) {
				final Notes notes = oldGraph.getExtension(Notes.class);
				if(notes != null) {
					for(Note note : notes)
						notesAdapter.elementRemoved(notes, note);
				}

				for(OpLink link : oldGraph.getEdges())
					graphAdapter.linkRemoved(oldGraph, link);

				for(OpNode node : oldGraph.getVertices())
					graphAdapter.nodeRemoved(oldGraph, node);
			}

			// Add new ones
			if(graph != null) {
				graph.addGraphListener(graphAdapter);

				for(OpNode node : document.getGraph().getVertices())
					graphAdapter.nodeAdded(graph, node);

				for(OpLink link : document.getGraph().getEdges())
					graphAdapter.linkAdded(graph, link);

				// Add any notes, or if none exist, make sure the extension
				// exists on the given graph
				Notes notes = document.getGraph().getExtension(Notes.class);
				if(notes == null) {
					notes = new Notes();
					document.getGraph().putExtension(Notes.class, notes);
				} else {
					for(Note note : notes)
						notesAdapter.elementAdded(notes, note);
				}

				notes.addCollectionListener(notesAdapter);
			}

		}

		// Update selection 
		for(OpNode node : getSelectionModel().getSelectedNodes()) {
			if(nodes.containsKey(node))
				nodes.get(node).setSelected(true);
		}

		// Update anchor fill states
		if(graph != null) {
			for(OpNode node : document.getGraph().getVertices())
				updateAnchorFillStates(node);
		}

		revalidate();
		repaint();
		
		SwingUtilities.invokeLater( () -> getUI().getMinimapLayer().update() );
	}

	public void selectAll() {
		getSelectionModel().setSelectedNodes(getDocument().getGraph().getVertices());
	}
	
	public void clearSelection() {
		getSelectionModel().setSelectedNodes(new ArrayList<>());
	}
	
	public void copy() {
		final Collection<OpNode> selectedNodes = getSelectionModel().getSelectedNodes();
		if(selectedNodes.size() > 0) {
			final OpGraph graph = document.getGraph();

			// Copy selected nodes
			final OpGraph selectedGraph = new OpGraph();
			for(OpNode node : selectedNodes)
				selectedGraph.add(node);

			// For each selected node, copy outgoing links if they are fully contained in the selection
			for(OpNode selectedNode : selectedNodes) {
				final Collection<OpLink> outgoingLinks = graph.getOutgoingEdges(selectedNode);
				for(OpLink link : outgoingLinks) {
					if(selectedNodes.contains(link.getDestination())) {
						try {
							selectedGraph.add(link);
						} catch(VertexNotFoundException exc) {
							LOGGER.severe(exc.getMessage());
						} catch(CycleDetectedException exc) {
							LOGGER.severe(exc.getMessage());
						}
					}
				}
			}

			// Add to system clipboard
			final SubgraphClipboardContents clipboardContents = new SubgraphClipboardContents(this, selectedGraph);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(clipboardContents, this);
		}
	}
	
	public void paste() {
		// Check to make sure the clipboard has something we can paste
		final Transferable clipboardContents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
		if(clipboardContents != null && clipboardContents.isDataFlavorSupported(SubgraphClipboardContents.copyFlavor)) {
			try {
				final SubgraphClipboardContents nodeClipboardContents = 
						SubgraphClipboardContents.class.cast(clipboardContents.getTransferData(SubgraphClipboardContents.copyFlavor));

				final CompoundEdit cmpEdit = new CompoundEdit();
				final OpGraph graph = document.getGraph();
				final Map<String, String> nodeMap = new HashMap<String, String>();

				// Keep track of the number of times this graph has been pasted
				Integer timesDuplicated = nodeClipboardContents.getGraphDuplicates().get(graph);
				if(timesDuplicated == null) {
					timesDuplicated = 0;
				} else {
					timesDuplicated = timesDuplicated + 1;
				}

				nodeClipboardContents.getGraphDuplicates().put(graph, timesDuplicated);

				// Create a new node edit for each node in the contents
				final Collection<OpNode> newNodes = new ArrayList<OpNode>();
				for(OpNode node : nodeClipboardContents.getGraph().getVertices()) {
					// Clone the node
					final OpNode newNode = GraphUtils.cloneNode(node);
					newNodes.add(newNode);
					nodeMap.put(node.getId(), newNode.getId());

					// Offset to avoid pasting on top of current nodes
					final NodeMetadata metadata = newNode.getExtension(NodeMetadata.class);
					if(metadata != null) {
						metadata.setX(metadata.getX() + (50 * timesDuplicated));
						metadata.setY(metadata.getY() + (30 * timesDuplicated));
					}

					// Add an undoable edit for this node
					cmpEdit.addEdit(new AddNodeEdit(graph, newNode));
				}

				// Pasted node become the selection
				document.getSelectionModel().setSelectedNodes(newNodes);

				// Add copied node to graph
				for(OpLink link : nodeClipboardContents.getGraph().getEdges()) {
					final OpNode srcNode = graph.getNodeById(nodeMap.get(link.getSource().getId()), false);
					final OutputField srcField = srcNode.getOutputFieldWithKey(link.getSourceField().getKey());
					final OpNode dstNode = graph.getNodeById(nodeMap.get(link.getDestination().getId()), false);
					final InputField dstField = dstNode.getInputFieldWithKey(link.getDestinationField().getKey());

					try {
						final OpLink newLink = new OpLink(srcNode, srcField, dstNode, dstField);
						cmpEdit.addEdit(new AddLinkEdit(graph, newLink));
					} catch(ItemMissingException exc) {
						LOGGER.severe(exc.getMessage());
					} catch(VertexNotFoundException exc) {
						LOGGER.severe(exc.getMessage());
					} catch(CycleDetectedException exc) {
						LOGGER.severe(exc.getMessage());
					}
				}

				// Add the compound edit to the undo manager
				cmpEdit.end();
				document.getUndoSupport().postEdit(cmpEdit);
			} catch(UnsupportedFlavorException exc) {
				LOGGER.severe(exc.getMessage());
			} catch(IOException exc) {
				LOGGER.severe(exc.getMessage());
			}
		}
	}
	
	public void cut() {
		copy();
		getDocument().getUndoSupport().postEdit(new DeleteNodesEdit(document.getGraph(), getSelectionModel().getSelectedNodes()));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.scale(getZoomLevel(), getZoomLevel());
		super.paintComponent(g2);
	}

	//
	// GraphCanvasModelListener
	//

	private final GraphCanvasAdapter graphAdapter = new GraphCanvasAdapter();

	private class GraphCanvasAdapter implements OpGraphListener, OpNodeListener {
		@Override
		public void nodePropertyChanged(OpNode node, String propertyName, Object oldValue, Object newValue) {}

		@Override
		public void nodeAdded(final OpGraph graph, final OpNode v) {
			if(!nodes.containsKey(v)) {
				final CanvasNode node = new CanvasNode(v);
				final int cx = (int)getVisibleRect().getCenterX();
				final int cy = (int)getVisibleRect().getCenterY();

				// Place this node at the center if it has a negative location
				NodeMetadata meta = v.getExtension(NodeMetadata.class);
				if(meta == null) {
					meta = new NodeMetadata(cx, cy);
					v.putExtension(NodeMetadata.class, meta);
				} else {
					if(meta.getX() < 0) meta.setX(cx);
					if(meta.getY() < 0) meta.setY(cy);
				}

				node.setLocation(meta.getX(), meta.getY());
				meta.addPropertyChangeListener(new MetaListener(v));

				// Adjust links when component moves or resizes
				node.addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						for(OpLink link : document.getGraph().getIncomingEdges(v)) getUI().getLinksLayer().updateLink(link);
						for(OpLink link : document.getGraph().getOutgoingEdges(v)) getUI().getLinksLayer().updateLink(link);
						GraphCanvas.this.revalidate();
					}

					@Override
					public void componentMoved(ComponentEvent e) {
						for(OpLink link : document.getGraph().getIncomingEdges(v)) getUI().getLinksLayer().updateLink(link);
						for(OpLink link : document.getGraph().getOutgoingEdges(v)) getUI().getLinksLayer().updateLink(link);
						GraphCanvas.this.revalidate();		
					}
				});

				node.addUndoableEditListener(document.getUndoManager());
				nodes.put(v, node);
				add(node, GraphCanvasUI.NODES_LAYER, 0);

				v.addNodeListener(this);
				v.putExtension(JComponent.class, node);

				revalidate();
				repaint();
			}
		}

		@Override
		public void nodeRemoved(OpGraph graph, OpNode v) {
			if(nodes.containsKey(v)) {
				remove(nodes.get(v));
				nodes.get(v).removeUndoableEditListener(document.getUndoManager());
				nodes.remove(v);
				getSelectionModel().removeNodeFromSelection(v);

				v.removeNodeListener(this);
				v.putExtension(JComponent.class, null);

				revalidate();
				repaint();
			}
		}

		@Override
		public void linkAdded(OpGraph graph, OpLink e) {
			final CanvasNode src = nodes.get(e.getSource());
			final CanvasNode dst = nodes.get(e.getDestination());

			final CanvasNodeField srcField = src.getFieldsMap().get(e.getSourceField());
			final CanvasNodeField dstField = dst.getFieldsMap().get(e.getDestinationField());

			if(srcField != null) srcField.setAnchorFillState(AnchorFillState.LINK);
			if(dstField != null) dstField.setAnchorFillState(AnchorFillState.LINK);

			getUI().getLinksLayer().updateLink(e);

			repaint();
		}

		@Override
		public void linkRemoved(OpGraph graph, OpLink e) {
			final CanvasNode src = nodes.get(e.getSource());
			final CanvasNode dst = nodes.get(e.getDestination());

			// Multiple outgoing links can exist, so before removing this link, make
			// sure there are no more outgoing links
			if(graph.getOutgoingEdges(src.getNode()).size() == 0) {
				final CanvasNodeField field = src.getFieldsMap().get(e.getSourceField());
				if(field != null)
					field.setAnchorFillState(AnchorFillState.NONE);
			}

			// Decide whether the anchor fill state is to be set to NONE or DEFAULT
			final CanvasNodeField dstField = dst.getFieldsMap().get(e.getDestinationField());
			if(dstField != null) {
				final NodeMetadata meta = dst.getNode().getExtension(NodeMetadata.class);
				if(meta == null || meta.getDefault(e.getDestinationField()) == null)
					dstField.setAnchorFillState(AnchorFillState.NONE);
				else
					dstField.setAnchorFillState(AnchorFillState.DEFAULT);
			}

			getUI().getLinksLayer().removeLink(e);

			// Remove link reference and repaint
			repaint();
		}

		@Override
		public void fieldAdded(OpNode node, InputField field) {
			final CanvasNode canvasNode = nodes.get(node);
			if(canvasNode != null)
				repaint();
		}

		@Override
		public void fieldRemoved(OpNode node, InputField field) {
			final CanvasNode canvasNode = nodes.get(node);
			if(canvasNode != null)
				repaint();
		}

		@Override
		public void fieldAdded(OpNode node, OutputField field) {
			final CanvasNode canvasNode = nodes.get(node);
			if(canvasNode != null)
				repaint();
		}

		@Override
		public void fieldRemoved(OpNode node, OutputField field) {
			final CanvasNode canvasNode = nodes.get(node);
			if(canvasNode != null)
				repaint();
		}
	}

	//
	// GraphCanvasSelectionListener
	//

	private final GraphCanvasSelectionListener canvasSelectionListener = new GraphCanvasSelectionListener() {
		@Override
		public void nodeSelectionChanged(Collection<OpNode> old, Collection<OpNode> selected) {
			for(OpNode node : old) {
				if(nodes.containsKey(node))
					nodes.get(node).setSelected(false);
			}

			for(OpNode node : selected) {
				if(nodes.containsKey(node))
					nodes.get(node).setSelected(true);
			}

			repaint();
		}
	};

	//
	// CollectionListener<Notes.Note>
	//

	// TODO the xxxMouseListener calls below are ugly, so find a nicer way to do this 

	private final CollectionListener<Notes, Note> notesAdapter = new CollectionListener<Notes, Note>() {
		@Override
		public void elementAdded(Notes source, Note element) {
			final JComponent comp = element.getExtension(JComponent.class);
			if(comp != null) {
				add(comp, GraphCanvasUI.NOTES_LAYER);
				((NoteComponent)comp).getResizeGrip().addMouseListener(notesMouseAdapter);
				comp.revalidate();
			}
		}

		@Override
		public void elementRemoved(Notes source, Note element) {
			final JComponent comp = element.getExtension(JComponent.class);
			if(comp != null) {
				remove(comp);
				((NoteComponent)comp).getResizeGrip().removeMouseListener(notesMouseAdapter);
				repaint(comp.getBounds());
			}
		}
	};

	//
	// BreadcrumbListener
	//

	private final BreadcrumbListener<OpGraph, String> breadcrumbListener = new BreadcrumbListener<OpGraph, String>() {
		@Override
		public void breadCrumbEvent(BreadcrumbEvent<OpGraph, String> event) {
			if(event.getEventType() == BreadcrumbEventType.GOTO_STATE) {
				final OpGraph oldGraph = event.getOldState();
				final OpGraph newGraph = event.getState();
				// XXX Could this move to changeGraph instead?
				if(oldGraph != null){
					oldGraph.removeGraphListener(graphAdapter);
					
					final Notes notes = oldGraph.getExtension(Notes.class);
					if(notes != null)
						notes.removeCollectionListener(notesAdapter);
				}
				
				changeGraph(oldGraph, newGraph);
			}
		}
	};

	//
	// Adapter for creating undoable edits when notes are resized
	//

	private final MouseAdapter notesMouseAdapter = new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent e) {
			if(e.getComponent() instanceof ResizeGrip) {
				final ResizeGrip grip = (ResizeGrip)e.getComponent();
				if(grip.getComponent() instanceof NoteComponent) {
					final Note note = ((NoteComponent)grip.getComponent()).getNote();
					final Dimension initialSize = grip.getInitialComponentSize();
					final Dimension newSize = grip.getComponent().getSize();
					document.getUndoSupport().postEdit(new ResizeNoteEdit(note, initialSize, newSize));
				}
			}
		}
	};

	

	//
	// ClipboardOwner
	//

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) { }

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return null;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 20;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 100;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

}
