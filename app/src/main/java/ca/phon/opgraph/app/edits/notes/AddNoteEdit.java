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

import javax.swing.JComponent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ca.phon.opgraph.app.extensions.Note;
import ca.phon.opgraph.app.extensions.Notes;

/**
 * Adds a note to a note collection.
 */
public class AddNoteEdit extends AbstractUndoableEdit {
	/** The notes to which this edit was applied */
	private Notes notes;

	/** The note that was added */
	private Note note;

	/**
	 * Constructs an edit that adds a note to a graph at the origin.
	 * 
	 * @param notes  the notes to which this edit will be applied
	 * @param title  the initial title
	 * @param body  the initial body text
	 * 
	 * @throws NullPointerException  if <code>notes<code> is <code>null</code>
	 */
	public AddNoteEdit(Notes notes, String title, String body) {
		this(notes, title, body, 0, 0);
	}

	/**
	 * Constructs an edit that adds a note to a graph.
	 * 
	 * @param notes  the notes to which this edit will be applied
	 * @param title  the initial title
	 * @param body  the initial body text
	 * @param x  the initial x-coordinate of the note
	 * @param y  the initial y-coordinate of the note
	 * 
	 * @throws NullPointerException  if <code>notes<code> is <code>null</code>
	 */
	public AddNoteEdit(Notes notes, String title, String body, int x, int y) {
		if(notes == null)
			throw new NullPointerException();

		this.notes = notes;
		this.note = new Note(title, body);

		final JComponent noteComp = this.note.getExtension(JComponent.class);
		if(noteComp != null)
			noteComp.setLocation(x, y);

		perform();
	}

	/**
	 * Performs this edit.
	 */
	private void perform() {
		notes.add(note);
	}

	//
	// AbstractUndoableEdit
	//

	@Override
	public String getPresentationName() {
		return "Add Note";
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		perform();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		notes.remove(note);
	}
}
