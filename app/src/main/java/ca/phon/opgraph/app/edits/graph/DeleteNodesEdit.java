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
package ca.phon.opgraph.app.edits.graph;

import java.util.*;
import java.util.logging.*;

import javax.swing.undo.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.components.*;
import ca.phon.opgraph.dag.*;

/**
 * Deletes a collection of nodes.
 */
public class DeleteNodesEdit extends AbstractUndoableEdit {
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(DeleteNodesEdit.class.getName());

	/** The graph to which this edit was applied  */
	private OpGraph graph;

	/** The nodes to remove */
	private List<OpNode> nodes;

	/** The links that get removed when the nodes get removed */
	private Set<OpLink> links;

	/**
	 * Constructs a delete edit that removes a collection of nodes from a
	 * specified canvas model.
	 *
	 * @param graph  the graph to which this edit will be applied
	 * @param nodes  the nodes to remove
	 */
	public DeleteNodesEdit(OpGraph graph, Collection<OpNode> nodes) {
		this.graph = graph;
		this.nodes = new ArrayList<OpNode>();
		this.links = new TreeSet<OpLink>();

		if(nodes != null)
			this.nodes.addAll(nodes);

		for(OpNode node : this.nodes) {
			if(graph.contains(node)) {
				links.addAll(graph.getIncomingEdges(node));
				links.addAll(graph.getOutgoingEdges(node));
			}
		}

		perform();
	}

	/**
	 * Performs this edit.
	 */
	private void perform() {
		for(OpNode link : nodes)
			graph.remove(link);
	}

	public List<OpNode> getNodes() {
		return Collections.unmodifiableList(this.nodes);
	}

	//
	// AbstractUndoableEdit
	//

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		perform();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();

		// Add nodes
		for(OpNode node : nodes)
			graph.add(node);

		// Add old links
		for(OpLink link : links) {
			try {
				graph.add(link);
			} catch(VertexNotFoundException | CycleDetectedException | InvalidEdgeException exc) {
				LOGGER.severe(exc.getLocalizedMessage());
				ErrorDialog.showError(exc);
			}
		}
	}

	@Override
	public String getPresentationName() {
		if(nodes.size() == 0)
			return "Delete";
		else if(nodes.size() == 1)
			return "Delete Node";
		else
			return "Delete Nodes";
	}

	@Override
	public boolean isSignificant() {
		return (nodes.size() > 0);
	}
}
