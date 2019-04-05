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
package ca.phon.opgraph.nodes.menu.edits;

import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.OpLink;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.dag.CycleDetectedException;
import ca.phon.opgraph.dag.InvalidEdgeException;
import ca.phon.opgraph.dag.VertexNotFoundException;
import ca.phon.opgraph.exceptions.ItemMissingException;
import ca.phon.opgraph.extensions.Publishable.PublishedInput;
import ca.phon.opgraph.extensions.Publishable.PublishedOutput;
import ca.phon.opgraph.nodes.general.MacroNode;

/**
 * An edit that creates a macro from a given collection of nodes in a graph.
 */
public class ExplodeMacroEdit extends AbstractUndoableEdit {
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(ExplodeMacroEdit.class.getName());

	/** The graph to which this edit was applied  */
	private OpGraph graph;

	/** The constructed macro node */
	private MacroNode macro;

	/** Links within the macro, and for published fields */
	private Set<OpLink> newLinks;

	/** Links attached to the macro */
	private Set<OpLink> oldLinks;

	/**
	 * Constructs a macro-explosion edit which acts upon a given macro node.
	 * 
	 * @param graph  the graph to which this edit will be applied
	 * @param macro  the macro node to explode 
	 */
	public ExplodeMacroEdit(OpGraph graph, MacroNode macro) {
		this.graph = graph;
		this.macro = macro;

		this.oldLinks = new TreeSet<OpLink>();
		this.oldLinks.addAll(graph.getIncomingEdges(macro));
		this.oldLinks.addAll(graph.getOutgoingEdges(macro));

		this.newLinks = new TreeSet<OpLink>();
		this.newLinks.addAll(macro.getGraph().getEdges());

		for(OpLink link : graph.getIncomingEdges(macro)) {
			// If an incoming link is linked to a node that isn't a published
			// input, we can't reliably explode this node 
			if(!(link.getDestinationField() instanceof PublishedInput))
				throw new IllegalArgumentException("Macro node contains an incoming link linked to a node that isn't a published input.");

			final PublishedInput input = (PublishedInput)link.getDestinationField();
			try {
				this.newLinks.add(new OpLink(link.getSource(),
				                             link.getSourceField(),
				                             input.destinationNode,
				                             input.nodeInputField));
			} catch(ItemMissingException exc) {
				throw new IllegalArgumentException("A link connected to this MacroNode is in an impossible state");
			}
		}

		for(OpLink link : graph.getOutgoingEdges(macro)) {
			// If an outgoing link is linked to a node that isn't a published
			// output, we can't reliably explode this node 
			if(!(link.getSourceField() instanceof PublishedOutput))
				throw new IllegalArgumentException("Macro node contains an outgoing link linked to a node that isn't a published output. Cannot reliably explode.");

			final PublishedOutput output = (PublishedOutput)link.getSourceField();
			try {
				this.newLinks.add(new OpLink(output.sourceNode,
				                             output.nodeOutputField,
				                             link.getDestination(),
				                             link.getDestinationField()));
			} catch(ItemMissingException exc) {
				throw new IllegalArgumentException("A link connected to this MacroNode is in an impossible state");
			}
		}

		perform();
	}

	/**
	 * Performs this edit.
	 */
	private void perform() {
		// Remove macro node
		graph.remove(macro);

		// Add all nodes from macro
		for(OpNode nodes : macro.getGraph().getVertices())
			graph.add(nodes);

		// Add new links
		for(OpLink link : newLinks) {
			try {
				graph.add(link);
			} catch(VertexNotFoundException | CycleDetectedException | InvalidEdgeException exc) {
				LOGGER.severe(exc.getLocalizedMessage());
			}
		}
	}

	//
	// AbstractEdit overrides
	//

	@Override
	public String getPresentationName() {
		return "Create Macro";
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		perform();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();

		// Add all nodes from macro
		for(OpNode node : macro.getGraph().getVertices())
			graph.remove(node);

		// Add macro node
		graph.add(macro);

		for(OpLink link : oldLinks) {
			try {
				graph.add(link);
			} catch(VertexNotFoundException | CycleDetectedException | InvalidEdgeException exc) {
				LOGGER.severe(exc.getLocalizedMessage());
			}
			
		}
	}
}
