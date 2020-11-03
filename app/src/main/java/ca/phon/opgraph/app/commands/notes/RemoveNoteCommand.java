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
package ca.phon.opgraph.app.commands.notes;

import java.awt.event.*;

import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.commands.*;
import ca.phon.opgraph.app.edits.notes.*;
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
