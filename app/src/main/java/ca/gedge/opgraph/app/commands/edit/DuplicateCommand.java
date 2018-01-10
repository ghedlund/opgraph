/*
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
package ca.gedge.opgraph.app.commands.edit;

import java.awt.Toolkit;
import java.awt.event.*;
import java.util.*;
import java.util.logging.Logger;

import javax.swing.KeyStroke;
import javax.swing.undo.CompoundEdit;

import ca.gedge.opgraph.*;
import ca.gedge.opgraph.app.GraphDocument;
import ca.gedge.opgraph.app.commands.GraphCommand;
import ca.gedge.opgraph.app.edits.graph.*;
import ca.gedge.opgraph.app.util.GraphUtils;
import ca.gedge.opgraph.dag.*;
import ca.gedge.opgraph.exceptions.ItemMissingException;
import ca.gedge.opgraph.extensions.NodeMetadata;

/**
 * Duplicate selected nodes within a graph.
 */
public class DuplicateCommand extends GraphCommand {
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(DuplicateCommand.class.getName());

	/**
	 * Default constructor.
	 */
	public DuplicateCommand(GraphDocument document) {
		super("Duplicate", document);
		
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	//
	// AbstractAction
	//

	@Override
	public void hookableActionPerformed(ActionEvent ae) {
		if(document != null) {
			// Check to make sure the clipboard has something we can paste
			final Collection<OpNode> selectedNodes = document.getSelectionModel().getSelectedNodes();
			if(isEnabled() && selectedNodes.size() > 0) {
				final CompoundEdit cmpEdit = new CompoundEdit();
				final OpGraph graph = document.getGraph();
				final Map<String, String> nodeMap = new HashMap<String, String>();

				// Create a new node edit for each node in the contents
				final Collection<OpNode> newNodes = new ArrayList<OpNode>();
				for(OpNode node : selectedNodes) {
					// Clone the node
					final OpNode newNode = GraphUtils.cloneNode(node);
					newNodes.add(newNode);
					nodeMap.put(node.getId(), newNode.getId());

					// Offset to avoid pasting on top of current nodes
					final NodeMetadata metadata = newNode.getExtension(NodeMetadata.class);
					if(metadata != null) {
						metadata.setX(metadata.getX() + 50);
						metadata.setY(metadata.getY() + 30);
					}

					// Add an undoable edit for this node
					cmpEdit.addEdit(new AddNodeEdit(graph, newNode));
				}

				// Duplicated nodes become the selection
				document.getSelectionModel().setSelectedNodes(newNodes);

				// For each selected node, copy outgoing links if they fully contained in the selection
				for(OpNode selectedNode : selectedNodes) {
					final Collection<OpLink> outgoingLinks = graph.getOutgoingEdges(selectedNode);
					for(OpLink link : outgoingLinks) {
						if(selectedNodes.contains(link.getDestination())) {
							final OpNode srcNode = graph.getNodeById(nodeMap.get(link.getSource().getId()), false);
							final OutputField srcField = srcNode.getOutputFieldWithKey(link.getSourceField().getKey());
							final OpNode dstNode = graph.getNodeById(nodeMap.get(link.getDestination().getId()), false);
							final InputField dstField = dstNode.getInputFieldWithKey(link.getDestinationField().getKey());

							try {
								final OpLink newLink = new OpLink(srcNode, srcField, dstNode, dstField);
								cmpEdit.addEdit(new AddLinkEdit(graph, newLink));
							} catch(VertexNotFoundException exc) {
								LOGGER.severe(exc.getMessage());
							} catch(CycleDetectedException exc) {
								LOGGER.severe(exc.getMessage());
							} catch(ItemMissingException exc) {
								LOGGER.severe(exc.getMessage());
							}
						}
					}
				}

				// Add the compound edit to the undo manager
				cmpEdit.end();
				document.getUndoSupport().postEdit(cmpEdit);
			}
		}
	}
}
