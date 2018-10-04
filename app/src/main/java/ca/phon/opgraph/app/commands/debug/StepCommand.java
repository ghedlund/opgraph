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
