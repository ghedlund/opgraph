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
package ca.phon.opgraph.app.commands.edit;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.undo.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.commands.*;
import ca.phon.opgraph.app.components.canvas.*;
import ca.phon.opgraph.app.edits.graph.*;
import ca.phon.opgraph.app.util.*;
import ca.phon.opgraph.dag.*;
import ca.phon.opgraph.exceptions.*;
import ca.phon.opgraph.extensions.*;

/**
 * Paste copied nodes (if any) from the system clipboard into the current graph.
 */
public class PasteCommand extends HookableCommand {
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(PasteCommand.class.getName());
	
	private GraphDocument document;

	/**
	 * Default constructor.
	 */
	public PasteCommand(GraphDocument document) {
		super("Paste");
		
		this.document = document;
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	//
	// AbstractAction
	//

	@Override
	public void hookableActionPerformed(ActionEvent e) {
		if(GraphicsEnvironment.isHeadless())
			return;

		if(document != null) {
			// Check to make sure the clipboard has something we can paste
			final Transferable clipboardContents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
			if(clipboardContents != null && clipboardContents.isDataFlavorSupported(SubgraphClipboardContents.copyFlavor)) {
				try {
					final SubgraphClipboardContents nodeClipboardContents = 
							SubgraphClipboardContents.class.cast(clipboardContents.getTransferData(SubgraphClipboardContents.copyFlavor));

					final CompoundEdit cmpEdit = new CompoundEdit();
					final OpGraph graph = document.getGraph();
					final Map<String, String> nodeMap = new HashMap<String, String>();

					// Keep track of the number of times this graph has been pasted
					Integer timesDuplicated = nodeClipboardContents.getGraphDuplicates().get(graph);
					if(timesDuplicated == null) {
						timesDuplicated = 0;
					} else {
						timesDuplicated = timesDuplicated + 1;
					}

					nodeClipboardContents.getGraphDuplicates().put(graph, timesDuplicated);

					// Create a new node edit for each node in the contents
					final Collection<OpNode> newNodes = new ArrayList<OpNode>();
					for(OpNode node : nodeClipboardContents.getGraph().getVertices()) {
						// Clone the node
						final OpNode newNode = GraphUtils.cloneNode(node);
						newNodes.add(newNode);
						nodeMap.put(node.getId(), newNode.getId());

						// Offset to avoid pasting on top of current nodes
						final NodeMetadata metadata = newNode.getExtension(NodeMetadata.class);
						if(metadata != null) {
							metadata.setX(metadata.getX() + (50 * timesDuplicated));
							metadata.setY(metadata.getY() + (30 * timesDuplicated));
						}

						// Add an undoable edit for this node
						cmpEdit.addEdit(new AddNodeEdit(graph, newNode));
					}

					// Pasted node become the selection
					document.getSelectionModel().setSelectedNodes(newNodes);

					// Add copied node to graph
					for(OpLink link : nodeClipboardContents.getGraph().getEdges()) {
						final OpNode srcNode = graph.getNodeById(nodeMap.get(link.getSource().getId()), false);
						final OutputField srcField = srcNode.getOutputFieldWithKey(link.getSourceField().getKey());
						final OpNode dstNode = graph.getNodeById(nodeMap.get(link.getDestination().getId()), false);
						final InputField dstField = dstNode.getInputFieldWithKey(link.getDestinationField().getKey());

						try {
							final OpLink newLink = new OpLink(srcNode, srcField, dstNode, dstField);
							cmpEdit.addEdit(new AddLinkEdit(graph, newLink));
						} catch(ItemMissingException | VertexNotFoundException | CycleDetectedException | InvalidEdgeException exc) {
							LOGGER.severe(exc.getMessage());
						}
					}

					// Add the compound edit to the undo manager
					cmpEdit.end();
					document.getUndoSupport().postEdit(cmpEdit);
				} catch(UnsupportedFlavorException exc) {
					LOGGER.severe(exc.getMessage());
				} catch(IOException exc) {
					LOGGER.severe(exc.getMessage());
				}
			}
		}
	}
}
