/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
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
package ca.phon.opgraph.app.commands.core;

import java.awt.Toolkit;
import java.awt.event.*;

import javax.swing.*;

import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.commands.GraphCommand;

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
