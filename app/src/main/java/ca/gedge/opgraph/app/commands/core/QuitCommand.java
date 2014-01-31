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
/**
 * 
 */
package ca.gedge.opgraph.app.commands.core;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import ca.gedge.opgraph.app.commands.HookableCommand;

/**
 * A command to quit the application.
 */
public class QuitCommand extends HookableCommand {
	/**
	 * Constructs a quit command.
	 */
	public QuitCommand() {
		super("Quit");

		final int CTRL = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, CTRL));
	}

	@Override
	public void hookableActionPerformed(ActionEvent e) {
		// Post closing event for frames, so frames can respond
		for(Frame frame : Frame.getFrames()) {
			final WindowEvent wev = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
			Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
		}
	}
}
