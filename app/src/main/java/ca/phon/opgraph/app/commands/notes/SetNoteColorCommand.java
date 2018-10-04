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
package ca.phon.opgraph.app.commands.notes;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.commands.GraphCommand;
import ca.phon.opgraph.app.edits.notes.SetNoteColorEdit;
import ca.phon.opgraph.app.extensions.Note;

/**
 * Sets the color of a note.
 */
public class SetNoteColorCommand extends GraphCommand {
	/** The initial x-coordinate for the note */
	private Note note;

	/** The initial y-coordinate for the note */
	private Color color;

	/**
	 * Constructs a command to set the color of a specified note.
	 * 
	 * @param note  the note whose color shall be set
	 * @param color  the color
	 * @param name  the name of this command
	 */
	public SetNoteColorCommand(GraphDocument doc, Note note, Color color, String name) {
		super(name, doc);

		this.note = note;
		this.color = color;

		// Create an icon
		final int PAD = 2;
		final int SZ = 12;
		final BufferedImage iconImage = new BufferedImage(SZ + 2*PAD, SZ + 2*PAD, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = iconImage.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		g.setColor(color);
		g.fillRoundRect(PAD, PAD, SZ - 1, SZ - 1, SZ / 2, SZ / 2);
		g.setColor(Color.BLACK);
		g.drawRoundRect(PAD, PAD, SZ - 1, SZ - 1, SZ / 2, SZ / 2);

		putValue(SMALL_ICON, new ImageIcon(iconImage));
	}

	//
	// AbstractAction
	//

	@Override
	public void hookableActionPerformed(ActionEvent e) {
		if(document != null)
			document.getUndoSupport().postEdit(new SetNoteColorEdit(note, color));
	}
}
