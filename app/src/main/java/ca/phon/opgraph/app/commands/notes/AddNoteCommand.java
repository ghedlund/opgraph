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
