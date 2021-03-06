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
package ca.phon.opgraph.nodes.menu.edits;

import java.util.*;
import java.util.logging.*;

import javax.swing.undo.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.dag.*;
import ca.phon.opgraph.exceptions.*;
import ca.phon.opgraph.extensions.*;
import ca.phon.opgraph.nodes.general.*;
import ca.phon.opgraph.nodes.reflect.*;
import ca.phon.opgraph.util.*;

/**
 * An edit that creates a macro from a given collection of nodes in a graph.
 */
public class CreateMacroEdit extends AbstractUndoableEdit {
	private static final Logger LOGGER = Logger.getLogger(CreateMacroEdit.class.getName());

	/** The graph to which this edit was applied  */
	private OpGraph graph;

	/** The constructed macro node */
	private MacroNode macro;

	/** Metadata for the newly created macro */
	private NodeMetadata macroMeta;

	/** Links that existed before the macro */
	private Set<OpLink> oldLinks;

	/** Links attached to the newly created macro */
	private Set<OpLink> newLinks;

	final Map<Pair<OpNode, OutputField>, OpNode> inputNodeMap = new HashMap<>();

	final Map<Pair<OpNode, InputField>, OpNode> outputNodeMap = new HashMap<>();

	/**
	 * Constructs a macro-creation edit which will automatically create a
	 * macro from a given collection of nodes.
	 *
	 * @param graph  the graph to which this edit will be applied
	 * @param nodes  the nodes from which the macro will be created
	 */
	public CreateMacroEdit(OpGraph graph, Collection<OpNode> nodes) {
		this.graph = graph;
		this.oldLinks = new TreeSet<OpLink>();
		this.newLinks = new TreeSet<OpLink>();

		// Construct the macro node
		final OpGraph macroGraph = new OpGraph();
		final Set<OpLink> links = new TreeSet<OpLink>();

		this.macroMeta = new NodeMetadata();
		this.macro = new MacroNode(macroGraph);
		this.macro.putExtension(NodeMetadata.class, this.macroMeta);

		macroGraph.setId("macro" + macro.getId());

		// First add all the nodes
		int numNodes = 0;
		for(OpNode node : nodes) {
			// Sanity check...
			if(!graph.contains(node)) {
				LOGGER.warning("node not in graph modeled by the given graph canvas");
				continue;
			}

			// Add the node
			macroGraph.add(node);

			final NodeMetadata nodeMeta = node.getExtension(NodeMetadata.class);
			if(nodeMeta != null) {
				macroMeta.setX(macroMeta.getX() + nodeMeta.getX());
				macroMeta.setY(macroMeta.getY() + nodeMeta.getY());
				++numNodes;
			}

			// Extend the link set
			links.addAll(graph.getIncomingEdges(node));
			links.addAll(graph.getOutgoingEdges(node));
		}

		// Macro's initial location is the centroid
		if(numNodes > 0) {
			macroMeta.setX(macroMeta.getX() / numNodes);
			macroMeta.setY(macroMeta.getY() / numNodes);
		}

		// These maps keep track of input/output keys that have already been
		// used, so that we don't publish two with the same key
		//
		final Map<String, Integer> publishedInputsMap = new HashMap<String, Integer>();
		final Map<String, Integer> publishedOutputsMap = new HashMap<String, Integer>();


		// Given all of the incoming/outgoing links, find which are internal
		// and which are external. If an links is external, publish the
		// appropriate field
		//
		for(OpLink link : links) {
			if(!nodes.contains(link.getSource())) {
				oldLinks.add(link);

				// Make sure no duplicate keys
				String name = link.getDestinationField().getKey();
				if(publishedInputsMap.containsKey(name)) {
					int val = publishedInputsMap.get(name);
					publishedInputsMap.put(name, val + 1);
					name += val;
				} else {
					publishedInputsMap.put(name, 1);
				}
				OpNode inputNode = inputNodeMap.get(new Pair<>(link.getSource(), link.getSourceField()));
				if(inputNode == null) {
					Class<?> outputType = link.getSourceField().getOutputType();
					final ObjectNode objectNode = new ObjectNode(outputType);
					objectNode.setName(name);

					macro.getGraph().add(objectNode);

					macro.publish(name, objectNode, objectNode.getInputFieldWithKey("obj"));

					inputNodeMap.put(new Pair<>(link.getSource(), link.getSourceField()), objectNode);
					inputNode = objectNode;
				}

				try {
					final OpLink objectLink = new OpLink(inputNode, inputNode.getOutputFieldWithKey("obj"),
							link.getDestination(), link.getDestinationField());
					macro.getGraph().add(objectLink);
				} catch (ItemMissingException | VertexNotFoundException | CycleDetectedException | InvalidEdgeException e) {
					LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}

				final InputField input = macro.getPublishedInput(inputNode, inputNode.getInputFieldWithKey("obj"));
				try {
					newLinks.add(new OpLink(link.getSource(), link.getSourceField(), macro, input));
				} catch(ItemMissingException exc) {
					LOGGER.severe("impossible exception");
				}
			} else if(!nodes.contains(link.getDestination())) {
				oldLinks.add(link);

				// Make sure no duplicate keys
				String name = link.getSourceField().getKey();
				if(publishedOutputsMap.containsKey(name)) {
					int val = publishedOutputsMap.get(name);
					publishedOutputsMap.put(name, val + 1);
					name += val;
				} else {
					publishedOutputsMap.put(name, 1);
				}

				// Publish output
				final OutputField output = macro.publish(name, link.getSource(), link.getSourceField());
				try {
					newLinks.add(new OpLink(macro, output, link.getDestination(), link.getDestinationField()));
				} catch(ItemMissingException exc) {
					LOGGER.severe("impossible exception");
				}
			} else {
				oldLinks.add(link);
				try {
					macroGraph.add(link);
				} catch(VertexNotFoundException exc) {
					LOGGER.severe("impossible exception");
				} catch(CycleDetectedException exc) {
					LOGGER.severe("impossible exception");
				} catch (InvalidEdgeException e) {
					LOGGER.severe("invalid link");
				}
			}
		}

		perform();
	}

	/**
	 * Performs this edit.
	 */
	private void perform() {
		// Remove all nodes, in turn removing all associated links
		for(OpNode node : macro.getGraph().getVertices())
			graph.remove(node);

		// Add macro node, and new external links attached to this macro node
		graph.add(macro);
		for(OpLink link : newLinks) {
			try {
				graph.add(link);
			} catch(VertexNotFoundException exc) {
				LOGGER.severe("impossible exception");
			} catch(CycleDetectedException exc) {
				LOGGER.severe("impossible exception");
			} catch (InvalidEdgeException e) {
				LOGGER.severe("Invalid link");
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

		// Remove macro
		graph.remove(macro);

		macro.getGraph().getVertices().stream()
			.filter( (n) -> !inputNodeMap.containsValue(n) )
			.forEach( graph::add );

		for(OpLink link : oldLinks) {
			try {
				graph.add(link);
			} catch(VertexNotFoundException exc) {
				LOGGER.severe("impossible exception");
			} catch(CycleDetectedException exc) {
				LOGGER.severe("impossible exception");
			} catch (InvalidEdgeException e) {
				LOGGER.severe("Invalid link");
			}
		}
	}
}
