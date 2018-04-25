/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * This file is part of the OpGraph project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.phon.opgraph.app.components;

import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.OpGraphListener;
import ca.phon.opgraph.OpLink;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OpNodeListener;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.extensions.CompositeNode;

/**
 * Tree model for {@link OpGraph} outline.
 */
public class OpGraphTreeModel extends DefaultTreeModel {
	
	private Map<OpGraph, OpNode> compositeNodeMap = new HashMap<>();
	
	private final JTree tree;
	
	public OpGraphTreeModel(JTree tree, OpGraph graph) {
		super(new DefaultMutableTreeNode(graph));
		this.tree = tree;
		setupTree((DefaultMutableTreeNode)getRoot(), graph);
		graph.addGraphListener(graphListener);
	}
	
	private void setupTree(DefaultMutableTreeNode node, OpGraph graph) {
		for(OpNode opnode:graph.getVertices()) {
			final DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(opnode);
			node.add(childNode);
			if(childNode.getUserObject() instanceof CompositeNode) {
				final OpGraph childGraph = ((CompositeNode)childNode.getUserObject()).getGraph();
				childGraph.addGraphListener(graphListener);
				setupTree(childNode, childGraph);
			}
			
			opnode.addNodeListener(nodeListener);
		}
	}
	
	public OpGraph getGraph() {
		final DefaultMutableTreeNode root = (DefaultMutableTreeNode)getRoot();
		return (OpGraph)root.getUserObject();
	}
	
	protected DefaultMutableTreeNode getMutableNode(OpGraph graph) {
		if(graph == getGraph()) {
			return (DefaultMutableTreeNode)getRoot();
		} else {
			return findMutableNode((DefaultMutableTreeNode)getRoot(), graph);
		}
	}
	
	protected DefaultMutableTreeNode findMutableNode(DefaultMutableTreeNode parent, OpGraph graph) {
		DefaultMutableTreeNode retVal = null;
		for(int i = 0; i < parent.getChildCount(); i++) {
			final DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)parent.getChildAt(i);
			if(childNode.getUserObject() instanceof CompositeNode) {
				final OpGraph childGraph = ((CompositeNode)childNode.getUserObject()).getGraph();
				if(childGraph == graph) {
					retVal = childNode;
					break;
				} else {
					retVal = findMutableNode(childNode, graph);
					if(retVal != null) break;
				}
			}
		}
		return retVal;
	}
	
	protected DefaultMutableTreeNode getMutableNode(OpNode node) {
		return findMutableNode((DefaultMutableTreeNode)getRoot(), node);
	}
	
	protected DefaultMutableTreeNode findMutableNode(DefaultMutableTreeNode parent, OpNode node) {
		DefaultMutableTreeNode retVal = null;
		for(int i = 0; i < parent.getChildCount(); i++) {
			final DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)parent.getChildAt(i);
			if(childNode.getUserObject() == node) {
				return childNode;
			}
			if(childNode.getUserObject() instanceof CompositeNode) {
				retVal = findMutableNode(childNode, node);
				if(retVal != null) break;
			}
		}
		return retVal;
	}
	
	public void nodeWasRemoved(OpGraph graph, OpNode node) {
		final DefaultMutableTreeNode treeNode = getMutableNode(node);
		if(treeNode != null) {
			super.removeNodeFromParent(treeNode);
		}
	}
	
	public void nodeWasAdded(OpGraph graph, OpNode node) {
		final int nodeIdx = graph.getVertices().indexOf(node);
		
		final DefaultMutableTreeNode parentNode = getMutableNode(graph);
		final DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);
		node.addNodeListener(nodeListener);
		
		if(node instanceof CompositeNode) {
			final OpGraph childGraph = ((CompositeNode)node).getGraph();
			childGraph.addGraphListener(graphListener);
			setupTree(childNode, childGraph);
		}
		
		parentNode.insert(childNode, nodeIdx);
		if(parentNode.getChildCount() == 1) {
			// notify structure has changed
			super.nodeStructureChanged(parentNode);
		}
		super.nodesWereInserted(parentNode, new int[]{ nodeIdx });
	}
	
	public void nodeChanged(OpNode node) {
		final DefaultMutableTreeNode treeNode = getMutableNode(node);
		super.nodeChanged(treeNode);
	}
	
	public void updateChildOrder(DefaultMutableTreeNode treeNode, OpGraph graph) {
		final TreeNode[] nodePath = super.getPathToRoot(treeNode);
		final TreePath treePath = new TreePath(nodePath);
		final Enumeration<TreePath> expandedPaths = tree.getExpandedDescendants(treePath);
		
		final Map<OpNode, DefaultMutableTreeNode> nodeMap = new HashMap<>();
		for(int i = 0; i < treeNode.getChildCount(); i++) {
			final DefaultMutableTreeNode childTreeNode = (DefaultMutableTreeNode)treeNode.getChildAt(i);
			final OpNode opNode = (OpNode)childTreeNode.getUserObject();
			nodeMap.put(opNode, childTreeNode);
		}
		
		treeNode.removeAllChildren();
		for(OpNode opNode:graph.getVertices()) {
			final DefaultMutableTreeNode childTreeNode = nodeMap.get(opNode);
			treeNode.add(childTreeNode);
		}
		nodeStructureChanged(treeNode);
		
		while(expandedPaths != null && expandedPaths.hasMoreElements()) {
			tree.expandPath(expandedPaths.nextElement());
		}
	}

	private final OpNodeListener nodeListener = new OpNodeListener() {

		@Override
		public void nodePropertyChanged(OpNode node, String propertyName, Object oldValue, Object newValue) {
			if(propertyName.equals(OpNode.NAME_PROPERTY))
				nodeChanged(node);
		}

		@Override
		public void fieldAdded(OpNode node, InputField field) {
		}

		@Override
		public void fieldRemoved(OpNode node, InputField field) {
		}

		@Override
		public void fieldAdded(OpNode node, OutputField field) {
		}

		@Override
		public void fieldRemoved(OpNode node, OutputField field) {
		}
		
	};

	private final OpGraphListener graphListener = new OpGraphListener() {
		
		@Override
		public void nodeRemoved(OpGraph graph, OpNode node) {
			nodeWasRemoved(graph, node);
		}
		
		@Override
		public void nodeAdded(OpGraph graph, OpNode node) {
			nodeWasAdded(graph, node);
		}
		
		@Override
		public void linkRemoved(OpGraph graph, OpLink link) {
			if(!graph.contains(link.getSource()) 
					|| !graph.contains(link.getDestination())) return;
			final DefaultMutableTreeNode treeNode = getMutableNode(graph);
			if(treeNode != null)
				updateChildOrder(treeNode, graph);
		}
		
		@Override
		public void linkAdded(OpGraph graph, OpLink link) {
			final DefaultMutableTreeNode treeNode = getMutableNode(graph);
			if(treeNode != null)
				updateChildOrder(treeNode, graph);
		}
		
	};

}
