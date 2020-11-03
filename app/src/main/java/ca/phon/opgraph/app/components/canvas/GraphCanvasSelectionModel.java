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
package ca.phon.opgraph.app.components.canvas;

import java.util.*;

import ca.phon.opgraph.*;

/**
 * Selection model used in a {@link GraphCanvas}.
 */
public class GraphCanvasSelectionModel {
	/** The currently selected nodes, or <code>null</code> if none selected */
	private ArrayList<OpNode> selectedNodes = new ArrayList<OpNode>();

	/**
	 * Gets the selected node.
	 * 
	 * @return the selected node, or <code>null</code> if
	 *         <code>{@link #getSelectedNodes()}.length != 1</code>
	 */
	public OpNode getSelectedNode() {
		return (selectedNodes.isEmpty() ? null : selectedNodes.get(0));
	}

	/**
	 * Sets the selected node.
	 * 
	 * @param node  the node to select 
	 */
	public void setSelectedNode(OpNode node) {
		final ArrayList<OpNode> old = new ArrayList<OpNode>(selectedNodes);
		selectedNodes.clear();
		if(node != null)
			selectedNodes.add(node);

		fireSelectionStateChanged(old);
	}

	/**
	 * Gets the list of selected nodes.
	 * 
	 * @return  the collection of selected nodes
	 */
	public Collection<OpNode> getSelectedNodes() {
		return Collections.unmodifiableCollection(selectedNodes);
	}

	/**
	 * Sets the nodes to select. If a specified node is not a member of
	 * the graph specified by this model, it is not selected.
	 * 
	 * @param newSelection  the new collection of nodes to select, or
	 *                      <code>null</code> to clear the selection
	 */
	public void setSelectedNodes(Collection<OpNode> newSelection) {
		// Create a set of all non-null nodes from given collection
		final ArrayList<OpNode> selected = new ArrayList<OpNode>();
		if(newSelection != null) {
			for(OpNode node : newSelection) {
				if(node != null)
					selected.add(node);
			}
		}

		// Only update selection if necessary
		//if(!newSelection.equals(selected)) {
			final Collection<OpNode> old = this.selectedNodes;
			selectedNodes = selected;
			fireSelectionStateChanged(old);
		//}
	}

	/**
	 * Adds a node to the selection, if it isn't already selected.
	 * 
	 * @param node  the node to add
	 */
	public void addNodeToSelection(OpNode node) {
		if(!selectedNodes.contains(node)) {
			final Collection<OpNode> old = new ArrayList<OpNode>(selectedNodes);
			selectedNodes.add(node);
			fireSelectionStateChanged(old);
		}
	}

	/**
	 * Remove a node from the selected nodes, if it is selected.
	 * 
	 * @param node  the node to remove
	 */
	public void removeNodeFromSelection(OpNode node) {
		if(selectedNodes.contains(node)) {
			final Collection<OpNode> old = new ArrayList<OpNode>(selectedNodes);
			selectedNodes.remove(node);
			fireSelectionStateChanged(old);
		}
	}

	/**
	 * Remove a collection of nodes from the selection.
	 * 
	 * @param nodes  the node to remove
	 */
	public void removeNodesFromSelection(Collection<OpNode> nodes) {
		if(nodes != null && !Collections.disjoint(nodes, selectedNodes)) {
			final Collection<OpNode> old = new ArrayList<OpNode>(selectedNodes);
			selectedNodes.removeAll(nodes);
			fireSelectionStateChanged(old);
		}
	}

	//
	// Listeners
	//

	private ArrayList<GraphCanvasSelectionListener> listeners = new ArrayList<GraphCanvasSelectionListener>();

	/**
	 * Adds a listener to this model.
	 * 
	 * @param listener  the listener to add
	 */
	public void addSelectionListener(GraphCanvasSelectionListener listener) {
		synchronized(listeners) {
			if(listener != null && !listeners.contains(listener))
				listeners.add(listener);
		}
	}

	/**
	 * Removes a listener from this model.
	 * 
	 * @param listener  the listener to remove
	 */
	public void removeSelectionListener(GraphCanvasSelectionListener listener) {
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}

	protected void fireSelectionStateChanged(Collection<OpNode> old) {
		synchronized(listeners) {
			for(GraphCanvasSelectionListener listener : listeners)
				listener.nodeSelectionChanged(old, selectedNodes);
		}
	}
}
