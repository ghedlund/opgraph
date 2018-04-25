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
package ca.phon.opgraph.app.commands.notes;

import java.awt.event.ActionEvent;

import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.commands.GraphCommand;
import ca.phon.opgraph.app.edits.notes.AddNoteEdit;
import ca.phon.opgraph.app.extensions.Notes;

/**
 * A command for adding a note to the active model.
 */
public class AddNoteCommand extends GraphCommand {
	/** The initial x-coordinate for the note */
	private int x;

	/** The initial y-coordinate for the note */
	private int y;

	/**
	 * Constructs an add note command that adds a note at the origin.
	 */
	public AddNoteCommand(GraphDocument doc) {
		this(doc, 0, 0);
	}

	/**
	 * Constructs an add note command that adds a note at a given location.
	 * 
	 * @param x  the x-coordinate of the note
	 * @param y  the y-coordinate of the note
	 */
	public AddNoteCommand(GraphDocument doc, int x, int y) {
		super("Add Note", doc);
		this.x = x;
		this.y = y;
	}

	//
	// AbstractAction
	//

	@Override
	public void hookableActionPerformed(ActionEvent e) {
		if(document != null && document.getGraph() != null) {
			final Notes notes = document.getGraph().getExtension(Notes.class);
			if(notes != null)
				document.getUndoSupport().postEdit(new AddNoteEdit(notes, "Note", "", x, y));
		}
	}
}
