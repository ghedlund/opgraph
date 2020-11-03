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
package ca.phon.opgraph.app.edits.notes;

import javax.swing.undo.*;

import ca.phon.opgraph.app.extensions.*;

/**
 * Removes a note from a note collection.
 */
public class RemoveNoteEdit extends AbstractUndoableEdit {
	/** The graph to which this edit was applied */
	private Notes notes;

	/** The note that was removed */
	private Note note;

	/**
	 * Constructs an edit that removes a given note from a notes collection.
	 * 
	 * @param notes  the notes to which this edit will be applied
	 * @param note  the note to remove
	 * 
	 * @throws NullPointerException  if <code>notes<code> is null
	 */
	public RemoveNoteEdit(Notes notes, Note note) {
		if(notes == null)
			throw new NullPointerException();

		this.notes = notes;
		this.note = note;
		perform();
	}

	/**
	 * Performs this edit.
	 */
	private void perform() {
		notes.remove(note);
	}

	//
	// AbstractUndoableEdit
	//

	@Override
	public String getPresentationName() {
		return "Remove Note";
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		perform();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		notes.add(note);
	}
}
