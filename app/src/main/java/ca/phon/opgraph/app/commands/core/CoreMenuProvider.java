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
package ca.phon.opgraph.app.commands.core;

import java.awt.event.*;
import java.beans.*;

import javax.swing.*;

import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.components.*;

/**
 * Menu provider for core functions.
 */
public class CoreMenuProvider implements MenuProvider {
	@Override
	public void installItems(final GraphEditorModel model, PathAddressableMenu menu) {
		menu.addMenu("file", "File");

		menu.addMenuItem("file/new", new NewCommand(model.getDocument()));
		menu.addMenuItem("file/open", new OpenCommand(model.getDocument()));
		final JMenuItem save = menu.addMenuItem("file/save", new SaveCommand(model.getDocument(), false));
		menu.addMenuItem("file/save as", new SaveCommand(model.getDocument(), true));
		menu.addSeparator("file");
		menu.addMenuItem("file/quit", new QuitCommand());

		model.getDocument().addPropertyChangeListener(GraphDocument.UNDO_STATE, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				save.setEnabled(model.getDocument().hasModifications());
			}
		});
	}

	@Override
	public void installPopupItems(Object context, MouseEvent event, GraphDocument doc, PathAddressableMenu menu) {

	}
	
}
