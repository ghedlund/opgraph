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
/**
 * 
 */
package ca.phon.opgraph.app.edits.notes;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ca.phon.opgraph.app.extensions.Note;

/**
 * Sets the title and body of a note.
 */
public class SetNoteTextEdit extends AbstractUndoableEdit {
	/** The note whose color will be set */
	private Note note;

	/** Old note title */
	private String oldTitle;

	/** New note title */
	private String newTitle;

	/** Old note body */
	private String oldBody;

	/** New note body */
	private String newBody;

	/**
	 * Constructs an edit that sets the body and title of a note.
	 * 
	 * @param note  the note whose color will be set
	 * @param title  the title of the note
	 * @param body  the body text of the note
	 * 
	 * @throws NullPointerException  if <code>note</code> is <code>null</code>
	 */
	public SetNoteTextEdit(Note note, String title, String body) {
		if(note == null)
			throw new NullPointerException();

		this.note = note;
		this.oldTitle = note.getTitle();
		this.oldBody = note.getBody();
		this.newTitle = title;
		this.newBody = body;
		perform();
	}

	/**
	 * Performs this edit.
	 */
	private void perform() {
		note.setTitle(newTitle);
		note.setBody(newBody);
	}

	//
	// AbstractUndoableEdit
	//

	@Override
	public String getPresentationName() {
		return "Set Note Title/Body";
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		perform();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		note.setTitle(oldTitle);
		note.setBody(oldBody);
	}
}
