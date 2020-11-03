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
import java.awt.event.*;

import javax.swing.*;

import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.commands.*;
import ca.phon.opgraph.app.components.canvas.*;

/**
 * Selects all nodes in the active document's {@link GraphCanvas}.
 */
public class SelectAllCommand extends GraphCommand {
	
	/**
	 * Default constructor.
	 */
	public SelectAllCommand(GraphDocument document) {
		super("Select All", document);
		
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	//
	// AbstractAction
	//

	@Override
	public void hookableActionPerformed(ActionEvent e) {
		if(document != null)
			document.getSelectionModel().setSelectedNodes(document.getGraph().getVertices());
	}
}
