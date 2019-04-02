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
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.components.*;
import ca.phon.opgraph.app.edits.graph.*;
import ca.phon.opgraph.app.edits.notes.MoveNoteEdit;
import ca.phon.opgraph.app.extensions.*;
import ca.phon.opgraph.app.util.GUIHelper;
import ca.phon.opgraph.dag.*;
import ca.phon.opgraph.exceptions.ItemMissingException;
import ca.phon.opgraph.extensions.*;
import ca.phon.opgraph.library.NodeData;
import ca.phon.opgraph.util.*;

public class DefaultGraphCanvasUI extends GraphCanvasUI {
	
	private final static Logger LOGGER = Logger.getLogger(DefaultGraphCanvasUI.class.getName());
	
	private GraphCanvas canvas;
	
	private CanvasMinimapLayer minimapLayer;
	
	/** The layer that displays a grid */
	private GridLayer gridLayer;

	/** The layer that displays links between nodes */
	private LinksLayer linksLayer;

	/** The layer that overlays the whole canvas */
	private CanvasOverlay canvasOverlay;

	/** The debug layer that overlays the whole canvas */
	private DebugOverlay canvasDebugOverlay;
	
	/**
	 * If link dragging is happening, <code>null</code> if this should be a new
	 * link, or a reference to an existing link if editing a link.
	 */
	private OpLink currentlyDraggedLink;

	/** If link dragging is happening, the input field from which this link originates */
	private CanvasNodeField currentlyDraggedLinkInputField;

	/** If link dragging is happening, the current location of the destination end */
	private Point currentDragLinkLocation;

	/**
	 * If link dragging is started, specifies whether or not the current
	 * position of the drag is a valid drop location for the link.
	 */
	private boolean dragLinkIsValid;

	/** The selection rectangle, or <code>null</code> if none */
	private Rectangle selectionRect;

	/** The initial click point (screen coordinates) within the canvas */
	private Point clickLocation;

	/** Component(s) whose location(s) will move during a mouse drag operation */
	private List<Pair<Component, Point>> componentsToMove = new ArrayList<Pair<Component, Point>>();
	
	public DefaultGraphCanvasUI() {
		super();
	}
	
	public DefaultGraphCanvasUI(GraphCanvas canvas) {
		super();
		
		this.canvas = canvas;
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		if(!(c instanceof GraphCanvas))
			throw new IllegalArgumentException("Component must be of type GraphCanvas");
		this.canvas = (GraphCanvas)c;
		
		// Components
		this.gridLayer = new GridLayer(canvas);
		this.linksLayer = new LinksLayer(canvas);
		this.canvasOverlay = new CanvasOverlay(canvas);
		this.canvasDebugOverlay = new DebugOverlay(canvas);
		this.minimapLayer = new CanvasMinimapLayer(canvas);
		
		// Initialize component
		canvas.setDoubleBuffered(true);
		canvas.setLayout(new NullLayout());
		canvas.setFocusable(true);
		canvas.setOpaque(false);
//		canvas.setBackground(Color.white);
		canvas.setFocusCycleRoot(true);
		
		installListeners();
		installActions();
		
		// add layers
		canvas.add(gridLayer, GRID_LAYER);
		canvas.add(linksLayer, LINKS_LAYER);
		canvas.add(canvasOverlay, OVERLAY_LAYER);
		canvas.add(canvasDebugOverlay, DEBUG_OVERLAY_LAYER);
		canvas.add(minimapLayer, MINIMAP_LAYER);
	}
	
	private void installListeners() {
		canvas.setDropTarget(new DropTarget(canvas, DnDConstants.ACTION_COPY, dropTargetAdapter, true));
		
		canvas.addMouseListener(mouseAdapter);
		canvas.addMouseMotionListener(mouseMotionAdapter);

		final long eventMask = AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK;
		Toolkit.getDefaultToolkit().addAWTEventListener(awtEventListener, eventMask);
	}
	
	private void installActions() {
		canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
		canvas.getActionMap().put("cancel", new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent e) {
				// If dragging link, null out these guys so selected nodes
				// don't get moved on mouse release
				if(currentlyDraggedLinkInputField != null)
					clickLocation = null;

				selectionRect = null;
				currentlyDraggedLink = null;
				currentlyDraggedLinkInputField = null;
				currentDragLinkLocation = null;

				canvas.repaint();
			}
		});
		
		canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
			KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "select_all");
		canvas.getActionMap().put("select_all", new AbstractAction("Select All") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				canvas.selectAll();
			}
		});
		
		canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
			KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "copy");
		canvas.getActionMap().put("copy", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canvas.copy();
			}
		});
		
		canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "paste");
		canvas.getActionMap().put("paste", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canvas.paste();
			}
		});
		
		canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "cut");
		canvas.getActionMap().put("cut", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canvas.cut();
			}
		});
	}
	
	@Override
	public CanvasMinimapLayer getMinimapLayer() {
		return minimapLayer;
	}
	
	/**
	 * @return grid layer
	 */
	@Override
	public GridLayer getGridLayer() {
		return gridLayer;
	}
	
	/**
	 * @return links layer
	 */
	@Override
	public LinksLayer getLinksLayer() {
		return linksLayer;
	}
	
	/**
	 * @return canvas overlay
	 */
	@Override
	public CanvasOverlay getCanvasOverlay() {
		return canvasOverlay;
	}
	
	/**
	 * @return debug overlay
	 */
	@Override
	public DebugOverlay getDebugOverlay() {
		return canvasDebugOverlay;
	}
	
	/**
	 * Gets the link currently being dragged.
	 * 
	 * @return the link, or <code>null</code> if no link being dragged 
	 */
	@Override
	public OpLink getCurrentlyDraggedLink() {
		return currentlyDraggedLink;
	}

	/**
	 * Gets the input field of the current drag link.
	 * 
	 * @return the input field, or <code>null</code> if no link being dragged
	 */
	@Override
	public CanvasNodeField getCurrentlyDraggedLinkInputField() {
		return currentlyDraggedLinkInputField;
	}

	/**
	 * Gets the location of the current link being dragged.
	 * 
	 * @return the location of the drag link, or <code>null</code> if no link
	 *         being dragged
	 */
	@Override
	public Point getCurrentDragLinkLocation() {
		return currentDragLinkLocation;
	}

	/**
	 * Gets whether or not the currently dragged link is at a valid drop spot.
	 * 
	 * @return <code>true</code> if the currently dragged link can be dropped
	 *         at the curent drag location, <code>false</code> otherwise 
	 */
	@Override
	public boolean isDragLinkValid() {
		return dragLinkIsValid;
	}
	
	@Override
	public Rectangle getGraphBoundingRect() {
		Rectangle retVal = new Rectangle(0, 0, 1, 1);
		int x = 1;
		int y = 1;
		
		final OpGraph graph = canvas.getDocument().getGraph();
		for(OpNode node:graph.getVertices()) {
			final CanvasNode cn = canvas.getNode(node);
			if(cn != null) {
				x = Math.max(x, cn.getX() + cn.getWidth());
				y = Math.max(y, cn.getY() + cn.getHeight());
			}
		}
		final Notes notes = graph.getExtension(Notes.class);
		if(notes != null) {
			for(Note note:notes) {
				final JComponent noteComp = note.getExtension(JComponent.class);
				if(noteComp != null) {
					x = Math.max(x, noteComp.getX() + noteComp.getWidth());
					y = Math.max(y, noteComp.getY() + noteComp.getHeight());
				}
			}
		}
		
		if(canvas.isVisible()) {
			x = Math.max(x, canvas.getVisibleRect().x + canvas.getVisibleRect().width);
			y = Math.max(y, canvas.getVisibleRect().y + canvas.getVisibleRect().height);
		}
		
		retVal.width = x;
		retVal.height = y;
		
		return retVal;
	}
	
	/**
	 * Gets the selection rectangle.
	 * 
	 * @return the selection rectangle, or <code>null</code> if there is
	 *         currently no selection rectangle
	 */
	public Rectangle getSelectionRect() {
		Rectangle ret = null;
		if(selectionRect != null) {
			int x = selectionRect.x;
			int y = selectionRect.y;
			int w = selectionRect.width;
			int h = selectionRect.height;

			if(w < 0) {
				x += w;
				w = -w;
			}

			if(h < 0) {
				y += h;
				h = -h;
			}

			ret = new Rectangle(x, y, w, h);
		}
		return ret;
	}
	
	
	
	/**
	 * Start a drag operation for a given field.
	 * 
	 * @param fieldComponent  the field
	 */
	@Override
	public void startLinkDrag(CanvasNodeField fieldComponent) {
		if(!canvas.isEnabled()) return;

		currentlyDraggedLink = null;
		currentlyDraggedLinkInputField = null;
		currentDragLinkLocation = canvas.getMousePosition();
		
		if(canvas.getZoomLevel() != 1.0f) {
			final AffineTransform at = new AffineTransform();
			at.scale(canvas.getZoomLevel(), canvas.getZoomLevel());
			// convert mouse position to logical coords
			Point2D mousePt = new Point2D.Double(currentDragLinkLocation.getX(), currentDragLinkLocation.getY());
			Point2D zoomedPt = mousePt;
			try {
				zoomedPt = at.inverseTransform(mousePt, null);
			} catch (NoninvertibleTransformException e1) {
			}
			currentDragLinkLocation = new Point((int)Math.round(zoomedPt.getX()), (int)Math.round(zoomedPt.getY()));
		}
		
		CanvasNode node = (CanvasNode)SwingUtilities.getAncestorOfClass(CanvasNode.class, fieldComponent);
		if(node != null) {
			ContextualItem field = fieldComponent.getField();
			if(field instanceof InputField) {
				// Check if link exists and, if so, start editing it
				for(OpLink e : canvas.getDocument().getGraph().getIncomingEdges(node.getNode())) {
					if(e.getDestinationField() == field) {
						currentlyDraggedLink = e;
						break;
					}
				}

				// Found a link, but currentlyDraggedLinkField needs to be on
				// source end, so use the link we found to update those fields
				if(currentlyDraggedLink != null) {
					node = canvas.getNodeMap().get(currentlyDraggedLink.getSource());
					if(node != null) {
						fieldComponent = node.getFieldsMap().get(currentlyDraggedLink.getSourceField());
						if(field != null) {
							currentlyDraggedLinkInputField = fieldComponent;
							canvas.repaint();
						}
					}
				}
			} else if(field instanceof OutputField) {
				currentlyDraggedLinkInputField = fieldComponent;
				canvas.repaint();
			}
		}
	}
	
	private Point convertPointToCanvas(Component src, Point p) {
		final AffineTransform at = new AffineTransform();
		at.scale(canvas.getZoomLevel(), canvas.getZoomLevel());
		
		Point canvasPt = SwingUtilities.convertPoint(src, p, canvas);
		if(canvas.getZoomLevel() != 1.0f) {
			try {
				Point2D zoomedPt = at.inverseTransform(canvasPt, null);
				canvasPt.setLocation(zoomedPt.getX(), zoomedPt.getY());
			} catch (NoninvertibleTransformException e1) {
			}
		}
		
		return canvasPt;
	}

	/**
	 * Called to update link dragging status.
	 * 
	 * @param p  the current point of the drag, in the coordinate system of this component
	 */
	public void updateLinkDrag(MouseEvent me) {
		if(!canvas.isEnabled()) return;

		if(currentlyDraggedLinkInputField == null) {
			dragLinkIsValid = false;
			return;
		}

		// convert me.getPoint() to canvas coord space
		Point p = me.getPoint();
		if(me.getSource() != canvas) {
			p = SwingUtilities.convertPoint((Component)me.getSource(), me.getPoint(), canvas);
		}
		
		currentDragLinkLocation = p;
		dragLinkIsValid = true;

		// Get the source node
		final CanvasNode source = (CanvasNode)SwingUtilities.getAncestorOfClass(CanvasNode.class, currentlyDraggedLinkInputField);
		if(source == null)
			return;

		// Find the destination node
		for(Component comp : canvas.getComponentsInLayer(NODES_LAYER)) {
			final CanvasNode dest = (CanvasNode)comp;
			Point nodeP = SwingUtilities.convertPoint(canvas, p, dest);
			
			if(dest.contains(nodeP)) {
				// See if we're hovering over a field
				CanvasNodeField field = dest.getFieldAt(nodeP);
				if(field != null) {
					// At this point, we default to an invalid link. If we're
					// not hovering over an InputField, then it isn't valid
					dragLinkIsValid = false;
					if(field.getField() instanceof InputField) {
						try {
							final OutputField out = (OutputField)currentlyDraggedLinkInputField.getField();
							final InputField in = (InputField)field.getField();
							final OpLink link = new OpLink(source.getNode(), out, dest.getNode(), in);

							// Now make sure the link can be added, and that it is a valid link
							dragLinkIsValid = (canvas.getDocument().getGraph().canAddEdge(link) && link.isValid());
						} catch(ItemMissingException exc) {}
					}
				}
				break;
			}
		}

		canvas.repaint();
	}

	/**
	 * Called when link dragging should end.
	 * 
	 * @param p  the end point of the drag, in the coordinate system of this component
	 */
	public void endLinkDrag(MouseEvent me) {
		if(!canvas.isEnabled()) return; 
		
		updateLinkDrag(me);
		
		// convert me.getPoint() to canvas coord space
		Point p = me.getPoint();
		if(me.getSource() != canvas) {
			p = SwingUtilities.convertPoint((Component)me.getSource(), me.getPoint(), canvas);
		}
		
		final GraphDocument document = canvas.getDocument();
		final OpGraph graph = document.getGraph();
		// If the drag link is valid, check to see which field this link
		// was fed into and try to add a new link
		if(currentlyDraggedLinkInputField != null) {
			if(dragLinkIsValid) {
				final CanvasNode sourceNode = (CanvasNode)SwingUtilities.getAncestorOfClass(CanvasNode.class, currentlyDraggedLinkInputField);
				if(sourceNode == null)
					return;

				boolean destinationFound = false;
				for(Component comp : canvas.getComponentsInLayer(NODES_LAYER)) {
					final CanvasNode destinationNode = (CanvasNode)comp;
					final Point nodeP = SwingUtilities.convertPoint(canvas, p, destinationNode);
					if(destinationNode.contains(nodeP)) {
						final CanvasNodeField destinationField = destinationNode.getFieldAt(nodeP);
						if(destinationField != null && destinationField.getField() instanceof InputField) {
							final OpNode source = sourceNode.getNode();
							final OpNode destination = destinationNode.getNode();
							final OutputField sourceField = (OutputField)currentlyDraggedLinkInputField.getField();
							final InputField destField = (InputField)destinationField.getField();

							// If no link being edited, just add the new link,
							// otherwise we need to see if any changes made
							try {
								final OpLink link = new OpLink(source, sourceField, destination, destField);
								if(currentlyDraggedLink == null) {
									document.getUndoSupport().postEdit(new AddLinkEdit(graph, link));
								} else {
									document.getUndoSupport().beginUpdate();
									document.getUndoSupport().postEdit(new RemoveLinkEdit(graph, currentlyDraggedLink));
									document.getUndoSupport().postEdit(new AddLinkEdit(graph, link));
									document.getUndoSupport().endUpdate();
								}
							} catch(ItemMissingException exc) {
								ErrorDialog.showError(exc);
							} catch(VertexNotFoundException exc) {
								ErrorDialog.showError(exc);
							} catch(CycleDetectedException exc) {
								ErrorDialog.showError(exc);
							}

							destinationFound = true;
						}

						break;
					}
				}

				// No destination found, so this means we were dragging over
				// the canvas area. If we were editing an existing link,
				// remove it
				if(!destinationFound) {
					if(currentlyDraggedLink != null) {
						if(!graph.contains(currentlyDraggedLink)) {
							try {
								graph.add(currentlyDraggedLink);
							} catch (VertexNotFoundException e) {
							} catch (CycleDetectedException e) {
							}
						}
						document.getUndoSupport().postEdit(new RemoveLinkEdit(graph, currentlyDraggedLink));
					} else {
						// call abandoned link handlers
						final List<Class<? extends AbandonedLinkHandler>> handlers = ServiceDiscovery.getInstance().findProviders(AbandonedLinkHandler.class);
						for(Class<? extends AbandonedLinkHandler> handler:handlers) {
							try {
								final AbandonedLinkHandler linkHandler = handler.newInstance();
								linkHandler.dragLinkAbandoned(canvas, sourceNode, currentlyDraggedLinkInputField, p);
							} catch (InstantiationException e) {
								LOGGER.log(Level.SEVERE,
										e.getLocalizedMessage(), e);
							} catch (IllegalAccessException e) {
								LOGGER.log(Level.SEVERE,
										e.getLocalizedMessage(), e);
							}
						}
					}
				}

			} else if(currentlyDraggedLink != null) {
				if(!graph.contains(currentlyDraggedLink)) {
					try {
						graph.add(currentlyDraggedLink);
					} catch (VertexNotFoundException e) {
					} catch (CycleDetectedException e) {
					}
				}
				// Invalid link, and we are editing an existing link, so remove it
				document.getUndoSupport().postEdit(new RemoveLinkEdit(graph, currentlyDraggedLink));
			}
		}

		currentlyDraggedLink = null;
		currentlyDraggedLinkInputField = null;
		currentDragLinkLocation = null;

		canvas.repaint();
	}
	
	/**
	 * Gets the bounding rectangle of the items currently being moved.
	 * 
	 * @return the bounding rectangle
	 */
	private Rectangle getBoundingRectOfMoved() {
		int xmin = Integer.MAX_VALUE;
		int xmax = Integer.MIN_VALUE;
		int ymin = Integer.MAX_VALUE;
		int ymax = Integer.MIN_VALUE;

		for(Pair<Component, Point> compLoc : componentsToMove) {
			final Point loc = compLoc.getSecond();
			final Component comp = compLoc.getFirst();
			final Dimension pref = comp.getPreferredSize();
			xmin = Math.min(xmin, loc.x);
			xmax = Math.max(xmax, loc.x + pref.width);
			ymin = Math.min(ymin, loc.y);
			ymax = Math.max(ymax, loc.y + pref.height);
		}

		return new Rectangle(xmin, ymin, xmax-xmin, ymax-ymin);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);
	}
	
	/**
	 * Constructs a popup menu for a node.
	 * 
	 * @param event  the mouse event that created the popup
	 * 
	 * @return an appropriate popup menu for the given node
	 */
	private JPopupMenu constructPopup(MouseEvent event) {
		final GraphDocument document = canvas.getDocument();
		Object context = document.getGraph();

		// Try to find a more specific context
		final CanvasNode node = GUIHelper.getAncestorOrSelfOfClass(CanvasNode.class, event.getComponent());
		if(node != null) {
			context = node.getNode();
		} else {
			final NoteComponent note = GUIHelper.getAncestorOrSelfOfClass(NoteComponent.class, event.getComponent());
			if(note != null)
				context = note.getNote();
		}

		final JPopupMenu popup = new JPopupMenu();
		if(context != null) {
			final PathAddressableMenuImpl addressable = new PathAddressableMenuImpl(popup);
			final MenuManager manager = new MenuManager();
			for(MenuProvider menuProvider : manager.getMenuProviders())
				menuProvider.installPopupItems(context, event, document, addressable);
		}

		if(popup.getComponentCount() == 0)
			return null;

		return popup;
	}
	
	private void showContextMenu(MouseEvent me) {
		final JPopupMenu popup = constructPopup(me);
		if(popup != null) {
			Point pt = me.getPoint();
			if(me instanceof CustomMouseEvent) {
				CustomMouseEvent cme = (CustomMouseEvent)me;
				pt = SwingUtilities.convertPoint(cme.getOrigSource(), cme.getOrigPt(), (Component)me.getSource());
			}
			popup.show((Component)me.getSource(), pt.x, pt.y);
		}
	}
	
	//
	// DropTargetListener
	//
	private static DataFlavor accepted = new DataFlavor(NodeData.class, "NodeData");

	private final DropTargetAdapter dropTargetAdapter = new DropTargetAdapter() {		
		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
			if(!dtde.isDataFlavorSupported(accepted))
				dtde.rejectDrag();
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			canvas.scrollRectToVisible(new Rectangle(dtde.getLocation(), new Dimension(1, 1)));
			if(!dtde.isDataFlavorSupported(accepted))
				dtde.rejectDrag();
		}

		@Override
		public void drop(final DropTargetDropEvent dtde) {
			if(dtde.isDataFlavorSupported(accepted)) {
				NodeData info = null;
				try {
					info = (NodeData)dtde.getTransferable().getTransferData(accepted);

					// Set up the initial location metadata and post the edit
					final int x = dtde.getLocation().x;
					final int y = dtde.getLocation().y;
					final OpGraph graph = canvas.getDocument().getGraph();
					canvas.getDocument().getUndoSupport().postEdit(new AddNodeEdit(graph, info, x, y));
				} catch(UnsupportedFlavorException e) {
					LOGGER.warning("Drop event says it supports NodeData flavor, but can't get data for that flavor");
				} catch(IOException e) {
					LOGGER.warning("IOException on drop, which should never happen");
				} catch(InstantiationException e) {
					LOGGER.warning("Could not instantiate node '" + info.name + "' from drop");
				}

				// Drag complete!
				dtde.acceptDrop(DnDConstants.ACTION_COPY);
				dtde.dropComplete(true);
			} else {
				dtde.rejectDrop();
			}
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent dtde) {
			if(!dtde.isDataFlavorSupported(accepted))
				dtde.rejectDrag();
		}
	};
	
	//
	// AWTEventListener
	//

	private final AWTEventListener awtEventListener = new AWTEventListener() {
		@Override
		public void eventDispatched(AWTEvent e) {
			// Only registered for mouse events
			if(!(e instanceof MouseEvent))
				return;
			
			final Component source = (Component)e.getSource();
			final MouseEvent me = (MouseEvent)e;

			if(!SwingUtilities.isDescendingFrom(source, canvas))
				return;
			
			
			// re-dispatch events with zoomed coords if necessary
			if(canvas.getZoomLevel() != 1.0f && !(me instanceof CustomMouseEvent)) {
				final AffineTransform at = new AffineTransform();
				at.scale(canvas.getZoomLevel(), canvas.getZoomLevel());
				
				// location in source coords
				final Point mp = me.getPoint();
				// location in canvas coords
				final Point canvasPt = SwingUtilities.convertPoint(source, mp, canvas);
				
				// zoomed canvas coords
				Point2D toConvert = new Point2D.Double(canvasPt.getX(), canvasPt.getY());
				Point2D zoomedCoords = toConvert;
				try {
					zoomedCoords = at.inverseTransform(toConvert, null);
				} catch (NoninvertibleTransformException e1) {
				}
				final Point zoomedPt = new Point((int)Math.round(zoomedCoords.getX()), (int)Math.round(zoomedCoords.getY()));
				
				Component newSrc = canvas;
				Point newPt = zoomedPt;
			
				if(e.getID() == MouseEvent.MOUSE_DRAGGED) {
					// always use canvas coords
				} else {
					if(getMinimapLayer().getMinimap().getBounds().contains(zoomedPt)) {
						newSrc = getMinimapLayer().getMinimap().getLabel();
						newPt = SwingUtilities.convertPoint(canvas, zoomedPt, newSrc);
					} else {
						List<Component> selectableComponents = new ArrayList<>();
						selectableComponents.addAll(Arrays.asList(canvas.getComponentsInLayer(NOTES_LAYER)));
						selectableComponents.addAll(Arrays.asList(canvas.getComponentsInLayer(NODES_LAYER)));
						for(Component c:selectableComponents) {
							if(c.getBounds().contains(zoomedPt)) {
								// convert zoomedPt to canvas node coords
								Point canvasNodePt = SwingUtilities.convertPoint(canvas, zoomedPt, c);
								// find deepest component at canvasNodePt
								newSrc = SwingUtilities.getDeepestComponentAt(c, canvasNodePt.x, canvasNodePt.y);
								// location in srcCmp coords
								newPt = SwingUtilities.convertPoint(c, canvasNodePt, newSrc);
							}
						}
					}
				}
				final CustomMouseEvent newMe = new CustomMouseEvent(newSrc, me.getID(), System.currentTimeMillis(), me.getModifiersEx(), 
						newPt.x, newPt.y, me.getClickCount(), me.isPopupTrigger(), me.getButton());
				newMe.setOrigPt(mp);
				newMe.setOrigSource(source);
				
				Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(newMe);
				
				me.consume();
				return;
			}
			
			if((me.getID() == MouseEvent.MOUSE_RELEASED || me.getID() == MouseEvent.MOUSE_PRESSED) && me.isPopupTrigger()) {
				showContextMenu(me);
				return;
			}
			
			if(e.getID() == MouseEvent.MOUSE_PRESSED && me.getButton() == MouseEvent.BUTTON1) {
				// If the mouse is pressed, update the selection. If pressed
				// on the canvas area, clear the selection. If on a node, and
				// it isn't already selected, select it. Otherwise, do nothing.
				//

				// Request focus if not clicked in text field that is being edited
				if(source instanceof JTextComponent) {
					if( ((JTextComponent)source).hasFocus() == false )
						canvas.requestFocusInWindow();
				} else {
					canvas.requestFocusInWindow();
				}

				// Make sure the component the event was dispatched to is a child
				clickLocation = me.getLocationOnScreen();
				componentsToMove.clear();
				
				final boolean addToSelection = 
						((me.getModifiersEx() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx())
								== Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());

				// No CanvasNode parent? Select nothing, otherwise select its node
				final CanvasNode canvasNode = GUIHelper.getAncestorOrSelfOfClass(CanvasNode.class, source);
				if(canvasNode == null) {
					if(!addToSelection)
						canvas.getSelectionModel().setSelectedNode(null);

					// see if we have a note
					final NoteComponent note = GUIHelper.getAncestorOrSelfOfClass(NoteComponent.class, source);
					if(note != null) {
						canvas.moveToFront(note);

						if(source instanceof ResizeGrip) {
							((ResizeGrip)source).saveSize();
							final Point initialLocation = me.getLocationOnScreen();
							componentsToMove.add(new Pair<Component, Point>(source, initialLocation));
						} else {
							final Point initialLocation = note.getLocation();
							componentsToMove.add(new Pair<Component, Point>(note, initialLocation));
							
							for(Component comp:canvas.getComponentsInLayer(NODES_LAYER)) {
								if(note.getBounds().contains(comp.getBounds())) {
									componentsToMove.add(new Pair<Component, Point>(comp, comp.getLocation()));
								}
							}
						}
						
						selectionRect = null;
					} else {
						selectionRect = new Rectangle(me.getPoint());
					}
				} else {
					// If it's not already selected, then select it
					if(!canvas.getSelectionModel().getSelectedNodes().contains(canvasNode.getNode())) {
						if(addToSelection)
							canvas.getSelectionModel().addNodeToSelection(canvasNode.getNode());
						else
							canvas.getSelectionModel().setSelectedNode(canvasNode.getNode());
					} else {
						// remove from selection if control is down
						if(addToSelection)
							canvas.getSelectionModel().removeNodeFromSelection(canvasNode.getNode());
					}

					// Bring it to the top of the nodes layer
					canvas.moveToFront(canvasNode);

					// Set the selected nodes as the components to move on drag
					for(OpNode node : canvas.getSelectionModel().getSelectedNodes()) {
						final JComponent comp = node.getExtension(JComponent.class);
						if(comp != null) {
							final Point initialLocation = comp.getLocation();
							componentsToMove.add(new Pair<Component, Point>(comp, initialLocation));
						}
					}
					
					selectionRect = null;
				}
					
				if(source instanceof CanvasNodeField) {
					startLinkDrag((CanvasNodeField)source);
				}
			} else if(e.getID() == MouseEvent.MOUSE_CLICKED && ((MouseEvent)e).getClickCount() == 2) {
				// If double clicked, we'll descend into a composite node
				boolean shouldDescend = true;

				// Check to see if this is an editable text component, and if
				// it isn't, we can go into a composite node
				if(source instanceof JTextComponent) 
					shouldDescend = !((JTextComponent)source).isEditable();

				// If double-clicked on a composite node, start editing it
				if(shouldDescend) {
					final CanvasNode node = GUIHelper.getAncestorOrSelfOfClass(CanvasNode.class, source);
					if(node != null) {
						final CompositeNode composite = node.getNode().getExtension(CompositeNode.class);
						final GraphDocument document = canvas.getDocument();
						if(composite != null) {
							// Put the publishing extension in the graph (even if it's null)
							final Publishable publishable = node.getNode().getExtension(Publishable.class);
							composite.getGraph().putExtension(Publishable.class, publishable);

							// Set up the breadcrumb 
							document.getBreadcrumb().addState(composite.getGraph(), node.getNode().getName());
						}
					}
				}
			}

			// All other events won't be processed if the canvas is disabled 
			if(!canvas.isEnabled())
				return;
			
			if(e.getID() == MouseEvent.MOUSE_DRAGGED) {
				boolean snapToGrid = 
					(me.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK;
				
				// Move the selected nodes, if not creating a link
				if(clickLocation != null && currentlyDraggedLinkInputField == null && selectionRect == null) {
					int deltaX = me.getLocationOnScreen().x - clickLocation.x;
					int deltaY = me.getLocationOnScreen().y - clickLocation.y;

					if(snapToGrid) {
						// Snap to grid if top left corner of selection is close to grid
						final Point topLeftBound = getBoundingRectOfMoved().getLocation();
						topLeftBound.translate(deltaX, deltaY);
	
						final Point snapDelta = gridLayer.snap(topLeftBound);
						deltaX += snapDelta.x;
						deltaY += snapDelta.y;
					}

					// First, make sure that one of the components to move is
					// an ancestor of the source of the drag
					for(Pair<Component, Point> compLoc : componentsToMove) {
						final Component comp = compLoc.getFirst();
						if(!(comp instanceof ResizeGrip)) {
							final Point initialLoc = compLoc.getSecond();
							comp.setLocation(initialLoc.x + deltaX, initialLoc.y + deltaY);
						} else {
							final Point initialLoc = compLoc.getSecond();
							((ResizeGrip)comp).resize(initialLoc, me.getLocationOnScreen());
						}
					}
					canvas.repaint();
				}
				
				if(selectionRect != null) {
					final Point src = selectionRect.getLocation();
					selectionRect.setSize(me.getPoint().x - src.x, me.getPoint().y - src.y);
					
					canvas.repaint();
				} else if(currentlyDraggedLinkInputField != null) {
					updateLinkDrag(me);
				}
			} else if(e.getID() == MouseEvent.MOUSE_RELEASED) {
				// Post an undoable event for any dragging that occurred
				if(clickLocation != null && currentlyDraggedLinkInputField == null && selectionRect == null) {
					// XXX Right now this works, but generalizing this would be better. What if
					//     we could select both nodes and notes and move them simultaneously?
					int deltaX = me.getXOnScreen() - clickLocation.x;
					int deltaY = me.getYOnScreen() - clickLocation.y;
					if(deltaX != 0 || deltaY != 0) {
						List<OpNode> movedNodes = new ArrayList<>();
						List<Note> movedNotes = new ArrayList<>();
						for(Pair<Component, Point> compPair:componentsToMove) {
							final Component c = compPair.getFirst();
							
							if(c instanceof CanvasNode) {
								movedNodes.add(((CanvasNode)c).getNode());
							} else if(c instanceof NoteComponent) {
								movedNotes.add(((NoteComponent)c).getNote());
								
								final Point p = new Point(c.getX()-deltaX, c.getY()-deltaY);
								c.setLocation(p);
							}
						}
						
						canvas.getDocument().getUndoSupport().beginUpdate();
						if(movedNodes.size() > 0)
							canvas.getDocument().getUndoSupport().postEdit(new MoveNodesEdit(movedNodes, deltaX, deltaY));

						movedNotes.forEach( (note) -> canvas.getDocument().getUndoSupport().postEdit(new MoveNoteEdit(note, deltaX, deltaY)) );
						
						canvas.getDocument().getUndoSupport().endUpdate();
					}
				}
				
				if(selectionRect != null) {
					final Rectangle rect = getSelectionRect(); 
					Set<OpNode> selected = new HashSet<OpNode>();
					for(Component comp : canvas.getComponentsInLayer(NODES_LAYER)) {
						final Rectangle compRect = comp.getBounds();
						if((comp instanceof CanvasNode) && rect.intersects(compRect))
							selected.add( ((CanvasNode)comp).getNode() );
					}
					
					if((me.getModifiersEx() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx())
							== Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()) {
						final HashSet<OpNode> currentSelection = new HashSet<>(canvas.getSelectionModel().getSelectedNodes());
						currentSelection.addAll(selected);
						selected = currentSelection;
					}
					
					canvas.getSelectionModel().setSelectedNodes(selected);
				} else if (currentlyDraggedLinkInputField != null) {
					endLinkDrag(me);
				}

				// Reset variables
				selectionRect = null;
				canvas.repaint();
				
				if(me.isPopupTrigger()) {
					showContextMenu(me);
				}

				clickLocation = null;
			}
		}
	};
	
	//
	// MouseAdapter
	//

	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	};
	
	private class CustomMouseEvent extends MouseEvent {
		
		private Component origSource;
		
		private Point origPt;

		public CustomMouseEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount,
				boolean popupTrigger, int button) {
			super(source, id, when, modifiers, x, y, clickCount, popupTrigger, button);
		}

		public CustomMouseEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount,
				boolean popupTrigger) {
			super(source, id, when, modifiers, x, y, clickCount, popupTrigger);
		}

		public CustomMouseEvent(Component source, int id, long when, int modifiers, int x, int y, int xAbs, int yAbs,
				int clickCount, boolean popupTrigger, int button) {
			super(source, id, when, modifiers, x, y, xAbs, yAbs, clickCount, popupTrigger, button);
		}

		public Component getOrigSource() {
			return origSource;
		}

		public void setOrigSource(Component origSource) {
			this.origSource = origSource;
		}

		public Point getOrigPt() {
			return origPt;
		}

		public void setOrigPt(Point origPt) {
			this.origPt = origPt;
		}
		
	}

	//
	// MouseMotionListener
	//

	private final MouseMotionAdapter mouseMotionAdapter = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {
			
		}
	};
	
}
