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
package ca.phon.opgraph.nodes.menu;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.components.canvas.*;
import ca.phon.opgraph.nodes.menu.edits.*;

/**
 * A command for creating a macro from the selected nodes in the active editor's canvas.
 */
public class CreateMacroCommand extends AbstractAction {
	
	private GraphDocument document;
	
	/**
	 * Constructs a create macro command that automatically creates a macro
	 * from the selected nodes in the active editor's canvas.
	 */
	public CreateMacroCommand(GraphDocument doc) {
		super("Create Macro From Selected Nodes");

		this.document = doc;
		
		final int CTRL = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, CTRL));
	}

	//
	// AbstractAction
	//

	@Override
	public void actionPerformed(ActionEvent e) {
		if(document != null) {
			final GraphCanvasSelectionModel selectionModel = document.getSelectionModel();
			final Collection<OpNode> selectedNodes = selectionModel.getSelectedNodes();
			document.getUndoSupport().postEdit(new CreateMacroEdit(document.getGraph(), selectedNodes));
		}
	}
}
