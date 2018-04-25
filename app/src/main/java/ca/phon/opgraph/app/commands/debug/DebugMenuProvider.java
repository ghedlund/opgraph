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
/**
 * 
 */
package ca.phon.opgraph.app.commands.debug;

import java.awt.event.MouseEvent;
import java.beans.*;

import javax.swing.JMenuItem;

import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.components.PathAddressableMenu;

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
