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
/**
 * 
 */
package ca.phon.opgraph.app.commands.debug;

import java.awt.event.*;
import java.beans.*;

import javax.swing.*;

import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.components.*;

/**
 * Menu provider for core functions.
 */
public class DebugMenuProvider implements MenuProvider {
	@Override
	public void installItems(final GraphEditorModel model, PathAddressableMenu menu) {
		menu.addMenu("debug", "Debug");

		menu.addMenuItem("debug/run", new RunCommand(model.getDocument()));
		final JMenuItem stop = menu.addMenuItem("debug/stop", new StopCommand(model.getDocument()));
		menu.addSeparator("debug");
		menu.addMenuItem("debug/step", new StepCommand(model.getDocument()));
		menu.addMenuItem("debug/step level", new StepLevelCommand(model.getDocument()));
		menu.addMenuItem("debug/step into", new StepIntoCommand(model.getDocument()));
		menu.addMenuItem("debug/step out of", new StepOutOfCommand(model.getDocument()));

		stop.setEnabled(false);

		model.getDocument().addPropertyChangeListener(GraphDocument.PROCESSING_CONTEXT, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				stop.setEnabled(evt.getNewValue() != null);
			}
		});
	}

	@Override
	public void installPopupItems(Object context, MouseEvent event, GraphDocument doc, PathAddressableMenu menu) {
		//
	}
}
