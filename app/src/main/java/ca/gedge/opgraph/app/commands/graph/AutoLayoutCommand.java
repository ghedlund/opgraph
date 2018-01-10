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
package ca.gedge.opgraph.app.commands.graph;

import java.awt.Toolkit;
import java.awt.event.*;

import javax.swing.KeyStroke;

import ca.gedge.opgraph.app.*;
import ca.gedge.opgraph.app.commands.GraphCommand;

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
