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
package ca.phon.opgraph.app.commands.graph;

import java.awt.Toolkit;
import java.awt.event.*;

import javax.swing.KeyStroke;

import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.commands.GraphCommand;

/**
 * A command for performing automatic layout of the active canvas' nodes.
 */
public class AutoLayoutCommand extends GraphCommand {
	/**
	 * Constructs a move command that moves the current node selection in the
	 * given graph canvas, with this edit posted in the given undo manager.
	 */
	public AutoLayoutCommand(GraphDocument document) {
		super("Auto Layout", document);

		final int CTRL = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, CTRL));
	}

	//
	// AbstractAction
	//

	@Override
	public void hookableActionPerformed(ActionEvent e) {
		if(document != null) {
			final AutoLayoutManager layoutManager = new AutoLayoutManager();
			layoutManager.layoutGraph(document.getGraph());
			document.getUndoSupport().postEdit(layoutManager.getUndoableEdit());
		}
	}
}
