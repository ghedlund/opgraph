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
package ca.phon.opgraph.app.commands.notes;

import java.awt.*;
import java.awt.event.MouseEvent;

import javax.swing.*;

import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.components.PathAddressableMenu;
import ca.phon.opgraph.app.extensions.Note;

/**
 * Menu provider for core functions.
 */
public class NotesMenuProvider implements MenuProvider {
	@Override
	public void installItems(final GraphEditorModel model, PathAddressableMenu menu) {
		// 
	}

	@Override
	public void installPopupItems(Object context, MouseEvent event, GraphDocument doc, PathAddressableMenu menu) {
		final boolean isGraph = (context instanceof OpGraph);
		final boolean isNote = (context instanceof Note);

		if(isGraph || isNote) {
			final Point loc = event.getPoint();
			if(loc != null) {
				menu.addSeparator("");
				menu.addMenuItem("add_note", new AddNoteCommand(doc, loc.x, loc.y));
			}
		} 

		if(isNote) {
			final Note note = (Note)context;

			final Object [] colors = new Object[] {
				new Color(255, 150, 150), "Red",
				new Color(150, 255, 150), "Green",
				new Color(150, 150, 255), "Blue",
				new Color(255, 255, 150), "Yellow",
				new Color(255, 150, 255), "Magenta",
				new Color(255, 200, 100), "Orange",
				new Color(200, 200, 200), "Gray"
			};

			menu.addMenuItem("remove_note", new RemoveNoteCommand(doc, note));
			menu.addSeparator("");

			final JMenu colorsMenu = menu.addMenu("colors", "Colors");
			for(int index = 0; index < colors.length; index += 2) {
				final Color color = (Color)colors[index];
				final String name = (String)colors[index + 1];
				colorsMenu.add(new JMenuItem(new SetNoteColorCommand(doc, note, color, name)));  
			}
		}
	}
}
