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
package ca.phon.opgraph.app.commands.debug;

import java.awt.Toolkit;
import java.awt.event.*;

import javax.swing.KeyStroke;

import ca.phon.opgraph.Processor;
import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.IconLibrary.IconType;
import ca.phon.opgraph.app.commands.GraphCommand;

/**
 * A command that steps the processing context of the active editor. If the
 * active editor has no context, one is created.
 */
public class StepCommand extends GraphCommand {
	/**
	 * Constructs a step command.
	 */
	public StepCommand(GraphDocument doc) {
		super("Step", doc);

		final int CTRL = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 
				KeyEvent.ALT_MASK));
		putValue(SMALL_ICON, IconLibrary.getIcon(IconType.DEBUG_STEP, 16, 16));
	}

	//
	// AbstractAction
	//

	@Override
	public void hookableActionPerformed(ActionEvent e) {
		if(document != null) {
			Processor context = document.getProcessingContext();
			if(context == null) {
				context = new Processor(document.getGraph());
				document.setProcessingContext(context);
			}

			if(context.hasNext()) {
				context.step();
				document.updateDebugState(context);
			}
		}
	}

}
