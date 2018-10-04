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

import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.KeyStroke;

import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.commands.GraphCommand;
import ca.phon.opgraph.app.components.canvas.GraphCanvas;
import ca.phon.opgraph.app.edits.graph.DeleteNodesEdit;

/**
 * Deletes selected nodes in a {@link GraphCanvas}.
 */
public class DeleteCommand extends GraphCommand {
	
	/**
	 * Default constructor.
	 */
	public DeleteCommand(GraphDocument document) {
		super("Delete", document);
		
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("DELETE"));
	}

	//
	// AbstractAction
	//

	@Override
	public void hookableActionPerformed(ActionEvent e) {
		if(document != null) {
			final Collection<OpNode> nodes = document.getSelectionModel().getSelectedNodes();
			document.getUndoSupport().postEdit(new DeleteNodesEdit(document.getGraph(), nodes));
		}
	}
}
