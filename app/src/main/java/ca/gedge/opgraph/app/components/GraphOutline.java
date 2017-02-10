package ca.gedge.opgraph.app.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.undo.UndoableEdit;

import ca.gedge.opgraph.OpGraph;
import ca.gedge.opgraph.OpNode;
import ca.gedge.opgraph.app.GraphDocument;
import ca.gedge.opgraph.app.components.canvas.GraphCanvas;
import ca.gedge.opgraph.app.components.canvas.NodeStyle;
import ca.gedge.opgraph.app.edits.graph.MoveNodesEdit;
import ca.gedge.opgraph.app.edits.node.ChangeNodeNameEdit;
import ca.gedge.opgraph.extensions.CompositeNode;
import ca.gedge.opgraph.util.BreadcrumbListener;

/**
 * Provides an outline component for {@link OpGraph}s.  The outline
 * is displayed as a tree, clicking on nodes in the tree will
 * display the relevent node in the {@link GraphCanvas} and select it.
 *
 */
public class GraphOutline extends JPanel {

	private static final long serialVersionUID = -1208476967494976769L;
	
	private JTree tree;
	private OpGraphTreeModel model;
	
	private JToolBar toolbar;
	
	private final GraphDocument graphDocument;
	
	public GraphOutline(GraphDocument graphDocument) {
		this.graphDocument = graphDocument;
		
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
		// TODO selection should be one or more items under a single tree (i.e., graph)
		final GraphSelectionModel selectionModel = new GraphSelectionModel();
		selectionModel.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		this.tree.setSelectionModel(selectionModel);
		
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
			} else if (edit instanceof ChangeNodeNameEdit) {
				// TODO this doesn't work - this edit is not passed to this undo support instance
				final OpNode node = ((ChangeNodeNameEdit)edit).getNode();
				final DefaultMutableTreeNode treeNode = this.model.getMutableNode(node);
				if(treeNode != null)
					this.model.nodeChanged(treeNode);
			}
		});
		
		graphDocument.getBreadcrumb().addBreadcrumbListener(new BreadcrumbListener<OpGraph, String>() {
			
			@Override
			public void stateChanged(OpGraph oldState, OpGraph newState) {
				// make sure new state is expanded in tree
				final DefaultMutableTreeNode treeNode = model.getMutableNode(newState);
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
			
			@Override
			public void stateAdded(OpGraph state, String value) {
				
			}
			
		});
	}
	
	public OpGraphTreeModel getModel() {
		return this.model;
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
								graphDocument.getBreadcrumb().gotoState(graphDocument.getBreadcrumb().getStates().get(graphDocument.getBreadcrumb().size()-1));
								
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
			if(rootPath == null) {
				rootPath = paths[0].getParentPath();
			}
			
			boolean sameParent = true;
			for(int i = 0; i < paths.length; i++) {
				sameParent &= rootPath.equals(paths[i].getParentPath());
			}
			
			if(sameParent)
					super.addSelectionPaths(paths);
		}

	}
	
}
