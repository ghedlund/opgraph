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
package ca.phon.opgraph.app.commands.notes;

import java.awt.event.ActionEvent;

import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.commands.GraphCommand;
import ca.phon.opgraph.app.edits.notes.RemoveNoteEdit;
import ca.phon.opgraph.app.extensions.*;

/**
 * A command for removing a note from the active model.
 */
public class RemoveNoteCommand extends GraphCommand {
	/** The note to remove */
	private final Note note;

	/**
	 * Constructs a command that removes a given note.
	 * 
	 * @param note  the note to remove
	 */
	public RemoveNoteCommand(GraphDocument doc, Note note) {
		super("Remove Note", doc);
		this.note = note;
	}

	//
	// AbstractAction
	//

	@Override
	public void hookableActionPerformed(ActionEvent e) {
		if(document != null && document.getGraph() != null) {
			final Notes notes = document.getGraph().getExtension(Notes.class);
			if(notes != null)
				document.getUndoSupport().postEdit(new RemoveNoteEdit(notes, note));
		}
	}
}
