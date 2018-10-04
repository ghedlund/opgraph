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
package ca.phon.opgraph.app.commands.edit;

import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.commands.GraphCommand;
import ca.phon.opgraph.app.components.canvas.*;
import ca.phon.opgraph.dag.*;

/**
 * Copy selected nodes to system clipboard.
 */
public class CopyCommand extends GraphCommand {
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(CopyCommand.class.getName());
	
	private GraphCanvas canvas;
	
	/**
	 * Default constructor.
	 */
	public CopyCommand(GraphDocument document, GraphCanvas canvas) {
		super("Copy", document);
		
		this.canvas = canvas;
		
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	@Override
	public void hookableActionPerformed(ActionEvent e) {
		if(GraphicsEnvironment.isHeadless())
			return;

		if(document != null) {
			// If a selection exists, create a copy of the selection
			final Collection<OpNode> selectedNodes = document.getSelectionModel().getSelectedNodes();
			if(selectedNodes.size() > 0) {
				final OpGraph graph = document.getGraph();

				// Copy selected nodes
				final OpGraph selectedGraph = new OpGraph();
				for(OpNode node : selectedNodes)
					selectedGraph.add(node);

				// For each selected node, copy outgoing links if they are fully contained in the selection
				for(OpNode selectedNode : selectedNodes) {
					final Collection<OpLink> outgoingLinks = graph.getOutgoingEdges(selectedNode);
					for(OpLink link : outgoingLinks) {
						if(selectedNodes.contains(link.getDestination())) {
							try {
								selectedGraph.add(link);
							} catch(VertexNotFoundException exc) {
								LOGGER.severe(exc.getMessage());
							} catch(CycleDetectedException exc) {
								LOGGER.severe(exc.getMessage());
							}
						}
					}
				}

				// Add to system clipboard
				final SubgraphClipboardContents clipboardContents = new SubgraphClipboardContents(
						canvas, selectedGraph);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(clipboardContents, null);
			}
		}
	}
}
