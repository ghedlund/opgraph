/*
 * Copyright (C) 2012-2020 Gregory Hedlund <https://www.phon.ca>
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
package ca.phon.opgraph.app.components;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.logging.*;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.undo.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.components.canvas.*;
import ca.phon.opgraph.app.edits.graph.*;
import ca.phon.opgraph.dag.*;
import ca.phon.opgraph.extensions.*;
import ca.phon.ui.jbreadcrumb.*;

/**
 * Provides an outline component for {@link OpGraph}s.  The outline
 * is displayed as a tree, clicking on nodes in the tree will
 * display the relevent node in the {@link GraphCanvas} and select it.
 *
 */
public class GraphOutline extends JPanel implements ClipboardOwner {

	private static final long serialVersionUID = -1208476967494976769L;
	
	private final static Logger LOGGER = Logger.getLogger(GraphOutline.class.getName());
	
	private final EventListenerList listeners = new EventListenerList();
	
	private JPopupMenu contextMenu;
	private JTree tree;
	private OpGraphTreeModel model;
	
	private JToolBar toolbar;
	
	private final GraphDocument graphDocument;
	
	private final GraphCanvas canvas;
	
	public GraphOutline(GraphDocument graphDocument, GraphCanvas canvas) {
		this.graphDocument = graphDocument;
		this.canvas = canvas;
		
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		add(toolbar, BorderLayout.NORTH);
		
		this.tree = new JTree();
		this.model = new OpGraphTreeModel(tree, graphDocument.getGraph());
		this.tree.setModel(this.model);
		this.tree.setCellRenderer(new OpGraphTreeCellRenderer());
		this.tree.addMouseListener(treeClickListener);

		final Action copyAct = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				copySelectionToClipboard(e);
			}
			
		};
		final ActionMap am = tree.getActionMap();
		am.put("copy", copyAct);
		
		// custom selection model
		final GraphSelectionModel selectionModel = new GraphSelectionModel();
		selectionModel.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		this.tree.setSelectionModel(selectionModel);
		
		contextMenu = new JPopupMenu();
		contextMenu.addPopupMenuListener(new PopupMenuListener() {
			
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				setupContextMenu();
				
				for(PopupMenuListener listener:listeners.getListeners(PopupMenuListener.class)) {
					listener.popupMenuWillBecomeVisible(e);
				}
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				for(PopupMenuListener listener:listeners.getListeners(PopupMenuListener.class)) {
					listener.popupMenuWillBecomeInvisible(e);
				}
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				for(PopupMenuListener listener:listeners.getListeners(PopupMenuListener.class)) {
					listener.popupMenuCanceled(e);
				}
			}
		});
		tree.setComponentPopupMenu(contextMenu);
		
		final JScrollPane scroller = new JScrollPane(tree);
		add(scroller, BorderLayout.CENTER);
		
		graphDocument.getUndoSupport().addUndoableEditListener( (e) -> {
			final UndoableEdit edit = e.getEdit();
			if(edit instanceof MoveNodesEdit) {
				// update order of nodes in current graph
				final OpGraph graph = graphDocument.getGraph();
				final DefaultMutableTreeNode treeNode = this.model.getMutableNode(graph);
				if(treeNode != null)
					this.model.updateChildOrder(treeNode, graph);
			}
		});
		
		graphDocument.getBreadcrumb().addBreadcrumbListener( (e) -> {
			if(e.getEventType() == BreadcrumbEvent.BreadcrumbEventType.GOTO_STATE) {
				// make sure new state is expanded in tree
				final DefaultMutableTreeNode treeNode = model.getMutableNode(e.getState());
				if(treeNode != null) {
					SwingUtilities.invokeLater(() -> {
						final TreePath path = new TreePath(model.getPathToRoot(treeNode));
						if(!tree.isExpanded(path)) {
							tree.expandPath(path);

						}
						final Rectangle pathBounds = tree.getPathBounds(path);
						if(!scroller.getViewport().getViewRect().contains(pathBounds)) {
							// display as much of the new context as possible
							final TreePath lastChildPath = path.pathByAddingChild(treeNode.getChildAt(treeNode.getChildCount()-1));
							final Rectangle lastChildBounds = tree.getPathBounds(lastChildPath);
							
							final Rectangle graphRect = pathBounds.union(lastChildBounds);
							
							tree.scrollRectToVisible(graphRect);
						}
					});
				}
			}
		});
	}
	
	protected void setupContextMenu() {
		contextMenu.removeAll();
		
		if(tree.getSelectionCount() > 0) {
			// add copy & paste items
			final Action copyAct = tree.getActionMap().get("copy");
			copyAct.putValue(Action.NAME, "Copy");
			copyAct.putValue(Action.SHORT_DESCRIPTION, "Copy selected nodes to clipboard.");
			
			contextMenu.add(copyAct);
		}
		
		if(tree.getSelectionCount() == 1) {
			final TreePath selectedPath = tree.getSelectionPath();
			final DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode)selectedPath.getLastPathComponent();
			if(lastNode.getUserObject() instanceof CompositeNode) {
				// TODO add paste command
			}
		}
	}
	
	public void addContextMenuListener(PopupMenuListener listener) {
		listeners.add(PopupMenuListener.class, listener);
	}
	
	public void removeContextMenuListener(PopupMenuListener listener) {
		listeners.remove(PopupMenuListener.class, listener);
	}
	
	/**
	 * Copies the selected nodes in the outline to the clipboard
	 */
	public void copySelectionToClipboard(ActionEvent evt) {
		if(tree.getSelectionCount() == 1 && tree.getSelectionRows()[0] == 0) {
			// create transferable object
			final SubgraphClipboardContents clipboardContents = 
					new SubgraphClipboardContents(canvas, graphDocument.getRootGraph());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(clipboardContents, this);
		} else {
			// create a new graph with the selected nodes
			final OpGraph graph = new OpGraph();
			final List<DefaultMutableTreeNode> nodesToCopy = new ArrayList<>();
			final GraphSelectionModel selectionModel = (GraphSelectionModel)getTree().getSelectionModel();
			for(TreePath selectedPath:selectionModel.getSelectionPaths()) {
				final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)selectedPath.getLastPathComponent();
				if(treeNode.getUserObject() instanceof OpNode) {
					nodesToCopy.add(treeNode);
				}
			}
			
			if(nodesToCopy.size() > 0) {
				final DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)nodesToCopy.get(0).getParent();
				if(parentNode.getUserObject() instanceof CompositeNode
						|| parentNode.getUserObject() instanceof OpGraph) {
					final OpGraph parentGraph = 
							(parentNode.getUserObject() instanceof OpGraph ? (OpGraph)parentNode.getUserObject() 
									: ((CompositeNode)parentNode.getUserObject()).getGraph());
					
					for(DefaultMutableTreeNode treeNode:nodesToCopy) {
						final OpNode node = (OpNode)treeNode.getUserObject();
						graph.add(node);
					}
					
					for(OpLink link:parentGraph.getEdges()) {
						if(graph.contains(link.getSource()) && graph.contains(link.getDestination())) {
							try {
								graph.add(link);
							} catch (VertexNotFoundException | CycleDetectedException | InvalidEdgeException e) {
								LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
							}
						}
					}
				}
				
				// create transferable object
				final SubgraphClipboardContents clipboardContents = 
						new SubgraphClipboardContents(canvas, graph);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(clipboardContents, this);
			}
		}
	}
	
	public OpGraphTreeModel getModel() {
		return this.model;
	}
	
	public JTree getTree() {
		return this.tree;
	}

	private final MouseListener treeClickListener = new MouseInputAdapter() {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
				final TreePath path = tree.getPathForLocation(e.getX(), e.getY());
				if(path != null) {
					// update document state
					final DefaultMutableTreeNode selectedTreeNode = ((DefaultMutableTreeNode)path.getLastPathComponent());
					if(selectedTreeNode.getUserObject() instanceof OpNode) {
						final OpNode selectedNode = (OpNode)selectedTreeNode.getUserObject();
						
						if(selectedTreeNode.isLeaf()) {
							final List<OpNode> graphPath = model.getGraph().getNodePath(selectedNode.getId());
							final OpNode parentNode = (graphPath.size() > 1 ? graphPath.get(graphPath.size()-2) : null);
							final OpGraph parentGraph = 
									(parentNode == null ? null : parentNode instanceof CompositeNode 
											? ((CompositeNode)parentNode).getGraph() : null);
							if(graphDocument.getBreadcrumb().getCurrentState() == parentGraph) {
								// do nothing
							} else if(parentGraph != null && graphDocument.getBreadcrumb().containsState(parentGraph)) {
								graphDocument.getBreadcrumb().gotoState(parentGraph);
							} else {
								graphDocument.getBreadcrumb().gotoState(graphDocument.getBreadcrumb().getStates().get(0));
								
								for(int i = 0; i < graphPath.size()-1; i++) {
									final OpNode node = graphPath.get(i);
									if(node instanceof CompositeNode) {
										graphDocument.getBreadcrumb().addState(((CompositeNode)node).getGraph(), node.getName());
									}
								}
							}
							graphDocument.getSelectionModel().setSelectedNode(selectedNode);
						}
					}
				}
			}
		}
		
	};
	
	private class OpGraphTreeCellRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = -823509020400941004L;
		
		private ImageIcon rootIcon;
		
		public OpGraphTreeCellRenderer() {
			try {
				rootIcon = new ImageIcon(ImageIO.read(NodeStyle.class.getClassLoader().getResourceAsStream("data/icons/16x16/opgraph/graph.png")));
			} catch (IOException e) {
				Logger.getAnonymousLogger().log(Level.WARNING, e.getLocalizedMessage(), e);
			}
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			final JLabel retVal = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			
			final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)value;
			
			if(treeNode.getUserObject() instanceof OpGraph) {
				// root component
				retVal.setText("root");
				retVal.setIcon(rootIcon);
			} else if(treeNode.getUserObject() instanceof OpNode) {
				final OpNode node = (OpNode)treeNode.getUserObject();
				final NodeStyle nodeStyle = NodeStyle.getStyleForNode(node);
				retVal.setText(node.getName());
				retVal.setIcon(nodeStyle.NodeIcon);
				retVal.setBackground(nodeStyle.NodeBackgroundColor);
				retVal.setForeground(nodeStyle.NodeNameTextColor);
			}
			return retVal;
		}
		
	}
	
	private class GraphSelectionModel extends DefaultTreeSelectionModel {

		/*
		 * All selected paths must have the same parent
		 * if we have any selection, the root path is the
		 * path to its parent
		 */
		private TreePath rootPath() {
			TreePath root = null;
			if(super.getSelectionPath() != null) {
				return super.getSelectionPath().getParentPath();
			}
			return root;
		}
		
		@Override
		public void addSelectionPaths(TreePath[] paths) {
			TreePath rootPath = rootPath();
			if(rootPath == null && paths.length > 0) {
				rootPath = paths[0].getParentPath();
				// case when first selection is the root path
				if(rootPath == null && paths.length > 1)
					rootPath = paths[1].getParentPath();
			}

			List<TreePath> sameParent = new ArrayList<>();
			if(rootPath != null) {
				for(int i = 0; i < paths.length; i++) {
					if(rootPath.equals(paths[i].getParentPath())) {
						sameParent.add(paths[i]);
					}
				}
			} else {
				// can only happen when we have a single selection of root
				sameParent.add(paths[0]);
			}
			
			if(sameParent.size() > 0)
				super.addSelectionPaths(sameParent.toArray(new TreePath[0]));
		}
		
		@Override
		public void addSelectionPath(TreePath path) {
			TreePath rootPath = rootPath();
			if(rootPath == null || rootPath.equals(path.getParentPath())) {
				super.addSelectionPath(path);
			}
		}

		@Override
		public void setSelectionPath(TreePath path) {
			super.clearSelection();
			
			addSelectionPath(path);
		}

		@Override
		public void setSelectionPaths(TreePath[] pPaths) {
			super.clearSelection();
			
			addSelectionPaths(pPaths);
		}
		
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		
	}
	
}
