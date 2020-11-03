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
package ca.phon.opgraph.app.commands.core;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.commands.*;

/**
 * A command which resets the editor state to an empty graph.
 */
public class NewCommand extends GraphCommand {
	/**
	 * Constructs a new command.
	 */
	public NewCommand(GraphDocument doc) {
		super("New", doc);

		final int CTRL = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, CTRL));
	}

	@Override
	public void hookableActionPerformed(ActionEvent e) {
		if(document != null)
			document.reset(null, null);
	}
}
