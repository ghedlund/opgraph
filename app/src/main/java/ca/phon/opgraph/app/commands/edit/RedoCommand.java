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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;

import ca.phon.opgraph.app.commands.HookableCommand;

/**
 * Sends a redo command to a given {@link UndoManager}.
 */
public class RedoCommand extends HookableCommand {
	/** The undo manager to send undo commands to */
	private UndoManager manager;

	/**
	 * Constructs a redo command for a given undo manager.
	 * 
	 * @param manager  the undo manager
	 */
	public RedoCommand(UndoManager manager) {
		this.manager = manager;

		final int CTRL = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, CTRL | InputEvent.SHIFT_MASK));

		update();
	}

	/**
	 * Updates the text and enabled state of this command.
	 */
	public void update() {
		setEnabled(manager.canRedo());
		putValue(NAME, manager.getRedoPresentationName());
	}

	//
	// AbstractAction
	//

	@Override
	public void hookableActionPerformed(ActionEvent e) {
		if(manager.canRedo())
			manager.redo();
	}
}
