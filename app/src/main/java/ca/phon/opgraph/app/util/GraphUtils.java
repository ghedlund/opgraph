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
package ca.phon.opgraph.app.util;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

import javax.swing.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.extensions.*;
import ca.phon.opgraph.dag.*;
import ca.phon.opgraph.exceptions.*;
import ca.phon.opgraph.extensions.*;
import ca.phon.opgraph.extensions.Publishable.*;
import ca.phon.opgraph.io.*;

/**
 * Helper methods for graphs.
 *
 * TODO Need to generalize cloning of extensions. This would be best done through implementing
 *      Cloneable and Object#clone() for everything, and throwing up a warning dialog whenever
 *      an extension is encountered which doesn't implement Cloneable.
 */
public class GraphUtils {
	/** Logger */
	private final static Logger LOGGER = Logger.getLogger(GraphUtils.class.getName());

	/**
	 * Gets the bounding rectangle of a given set of nodes.
	 *
	 * @return the bounding rectangle of the given collection of nodes
	 */
	public static Rectangle getBoundingRect(Collection<OpNode> nodes) {
		int xmin = Integer.MAX_VALUE;
		int xmax = Integer.MIN_VALUE;
		int ymin = Integer.MAX_VALUE;
		int ymax = Integer.MIN_VALUE;

		for(OpNode node : nodes) {
			final JComponent comp = node.getExtension(JComponent.class);
			if(comp != null) {
				final Dimension pref = comp.getPreferredSize();
				xmin = Math.min(xmin, comp.getX());
				xmax = Math.max(xmax, comp.getX() + pref.width);
				ymin = Math.min(ymin, comp.getY());
				ymax = Math.max(ymax, comp.getY() + pref.height);
			}
		}

		return new Rectangle(xmin, ymin, xmax-xmin, ymax-ymin);
	}

	/**
	 * Change id for all nodes in a graph.  This method is recursive
	 * and will traverse nodes with the CompositeNode extension.
	 *
	 * @param graph
	 */
	public static void changeNodeIds(OpGraph graph) {
		for(OpNode node:graph) {
			final String newId = Long.toHexString(UUID.randomUUID().getMostSignificantBits());
			node.setId(newId);

			final CompositeNode cmpNode = node.getExtension(CompositeNode.class);
			if(cmpNode != null && cmpNode.isGraphEmbedded()) {
				final OpGraph subGraph = cmpNode.getGraph();
				changeNodeIds(subGraph);
			}
		}
		graph.updateNodeMap();
	}

	/**
	 * Clone a node along with {@link NodeSettings}, {@link NodeMetadata},
	 * {@link CompositeNode}, and {@link Publishable} extensions cloned.
	 * The new node will have a new unique id.
	 *
	 * @param node  the node to clone
	 *
	 * @return the cloned node, or <code>null</code> if the node could not be cloned
	 *
	 * @throws NullPointerException  if node is <code>null</code>
	 */
	public static OpNode cloneNode(OpNode node) {
		if(node == null)
			throw new NullPointerException();

		final Class<? extends OpNode> nodeClass = node.getClass();

		final OpGraphSerializer serializer =
				OpGraphSerializerFactory.getDefaultSerializer();

		// using the serializer ensure we clone all data necessary
		// to rebuild the object
		if(serializer !=  null) {
			try {
				final OpGraph tempGraph = new OpGraph();
				tempGraph.setId("root");
				tempGraph.add(node);

				final ByteArrayOutputStream bout = new ByteArrayOutputStream();
				serializer.write(tempGraph, bout);

				final ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
				final OpGraph inGraph = serializer.read(bin);
				changeNodeIds(inGraph);

				final OpNode clonedNode = inGraph.getVertices().get(0);
				return clonedNode;
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		} else {
			try {
				final OpNode newNode = nodeClass.newInstance();
				newNode.setName(node.getName());

				// copy node settings (if available)
				final NodeSettings nodeSettings = node.getExtension(NodeSettings.class);
				final NodeSettings newNodeSettings = newNode.getExtension(NodeSettings.class);
				if(nodeSettings != null && newNodeSettings != null) {
					newNodeSettings.loadSettings(nodeSettings.getSettings());
				}

				// copy meta data (if available)
				final NodeMetadata metaData = node.getExtension(NodeMetadata.class);
				if(metaData != null) {
					final NodeMetadata newMetaData = new NodeMetadata(metaData.getX(), metaData.getY());
					newNode.putExtension(NodeMetadata.class, newMetaData);
				}

				// if a composite node, clone graph
				final CompositeNode compositeNode = node.getExtension(CompositeNode.class);
				final CompositeNode newCompositeNode = newNode.getExtension(CompositeNode.class);
				if(compositeNode != null && newCompositeNode != null) {
					final Map<String, String> nodeMap = new HashMap<String, String>();
					final OpGraph graph = compositeNode.getGraph();
					final OpGraph newGraph = cloneGraph(graph, null, nodeMap);
					newCompositeNode.setGraph(newGraph);

					// setup published fields (if available)
					final Publishable publishable = node.getExtension(Publishable.class);
					final Publishable newPublishable = newNode.getExtension(Publishable.class);
					if(publishable != null && newPublishable != null) {
						for(PublishedInput pubInput:publishable.getPublishedInputs()) {
							final OpNode destNode = newGraph.getNodeById(nodeMap.get(pubInput.destinationNode.getId()), false);
							if(destNode != null) {
								final InputField destField = destNode.getInputFieldWithKey(pubInput.nodeInputField.getKey());
								newPublishable.publish(pubInput.getKey(), destNode, destField);
							}
						}

						for(PublishedOutput pubOutput:publishable.getPublishedOutputs()) {
							final OpNode srcNode = newGraph.getNodeById(nodeMap.get(pubOutput.sourceNode.getId()), false);
							if(srcNode != null) {
								final OutputField srcField = srcNode.getOutputFieldWithKey(pubOutput.nodeOutputField.getKey());
								newPublishable.publish(pubOutput.getKey(), srcNode, srcField);
							}
						}
					}
				}

				// XXX Other extensions. See note attached to class javadoc.

				return newNode;
			} catch (InstantiationException e) {
				LOGGER.severe(e.getMessage());
			} catch (IllegalAccessException e) {
				LOGGER.severe(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * Clone the given graph.
	 *
	 * TODO Deal with cloning custom extensions
	 *
	 * @param graph  the graph to clone
	 * @param newGraph  graph to modify. If <code>null</code> a new graph will be
	 *                  created and returned. If not <code>null</code>, the return
	 *                  value will be the same object as this variable.
	 * @param nodeMap  mapping from node id to cloned node id
	 *
	 * @return the cloned graph
	 */
	public static OpGraph cloneGraph(OpGraph graph, OpGraph newGraph, Map<String, String> nodeMap) {
		final OpGraph retVal = (newGraph != null ? newGraph : new OpGraph());

		// Clone nodes
		for(OpNode node : graph.getVertices()) {
			final OpNode clonedNode = cloneNode(node);
			nodeMap.put(node.getId(), clonedNode.getId());
			retVal.add(clonedNode);
		}

		// Clone links
		for(OpLink link : graph.getEdges()) {
			final OpNode origSource = link.getSource();
			final OpNode newSource = retVal.getNodeById(nodeMap.get(origSource.getId()), false);
			final OutputField sourceField = newSource.getOutputFieldWithKey(link.getSourceField().getKey());
			final OpNode origDest = link.getDestination();
			final OpNode newDest = retVal.getNodeById(nodeMap.get(origDest.getId()), false);
			final InputField destField = newDest.getInputFieldWithKey(link.getDestinationField().getKey());

			try {
				final OpLink newLink = new OpLink(newSource, sourceField.getKey(), newDest, destField.getKey());
				retVal.add(newLink);
			} catch (ItemMissingException | VertexNotFoundException | CycleDetectedException | InvalidEdgeException e) {
				LOGGER.severe(e.getMessage());
			}
		}

		// Clone notes
		final Notes notes = graph.getExtension(Notes.class);
		if(notes != null) {
			final Notes newNotes = new Notes();

			for(Note note:notes) {
				final Note newNote = cloneNote(note);
				newNotes.add(newNote);
			}

			retVal.putExtension(Notes.class, newNotes);
		}

		// XXX Other extensions. See note attached to class javadoc.

		return retVal;
	}

	/**
	 * Clones the given graph.
	 *
	 * @param graph  the graph to clone
	 *
	 * @return the cloned graph
	 */
	public static OpGraph cloneGraph(OpGraph graph) {
		return cloneGraph(graph, null, new HashMap<String, String>());
	}

	/**
	 * Clone a graph note.
	 *
	 * @param note  the note to clone
	 *
	 * @return the cloned note
	 */
	public static Note cloneNote(Note note) {
		final Note retVal = new Note(note.getTitle(), note.getBody());
		final JComponent oldComp = note.getExtension(JComponent.class);
		final JComponent newComp = retVal.getExtension(JComponent.class);
		if(newComp != null && oldComp != null) {
			newComp.setBackground(oldComp.getBackground());
			newComp.setLocation(oldComp.getLocation());
			newComp.setPreferredSize(newComp.getPreferredSize());
		}

		return retVal;
	}
}
