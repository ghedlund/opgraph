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
package ca.phon.opgraph.app.commands.debug;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.IconLibrary.*;
import ca.phon.opgraph.app.commands.*;

/**
 * A command that runs the operation of the graph in the active editor.
 */
public class RunCommand extends GraphCommand {
	/**
	 * Constructs a run command.
	 */
	public RunCommand(GraphDocument doc) {
		super("Run", doc);

		final int CTRL = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, CTRL));
		putValue(SMALL_ICON, IconLibrary.getIcon(IconType.DEBUG_RUN, 16, 16));
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
				context.stepAll();
				document.updateDebugState(context);
			}
		}
	}

}
