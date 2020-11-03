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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ca.phon.opgraph.app.extensions.Note;

/**
 * Sets the size of a note.
 */
public class ResizeNoteEdit extends AbstractUndoableEdit {
	/** The note's UI component */
	private JComponent noteComp;

	/** Old note size */
	private Dimension oldSize;

	/** New note size */
	private Dimension newSize;

	/**
	 * Constructs an edit that sets the size of a specified note.
	 * 
	 * @param note  the note whose size will be set
	 * @param size  the new size
	 * 
	 * @throws NullPointerException  if <code>note</code> is <code>null</code>,
	 *                               or <code>note.getExtension(JComponent.class)</code> is
	 *                               <code>null</code>
	 */
	public ResizeNoteEdit(Note note, Dimension size) {
		if(note == null)
			throw new NullPointerException();

		noteComp = note.getExtension(JComponent.class);
		if(noteComp == null)
			throw new NullPointerException();

		this.oldSize = noteComp.getPreferredSize();
		this.newSize = size;
		perform();
	}

	/**
	 * Constructs an edit that sets the size of a specified note, with a given
	 * initial size.
	 * 
	 * @param note  the note whose size will be set
	 * @param initialSize  the initial size of the note
	 * @param size  the new size
	 * 
	 * @throws NullPointerException  if <code>note</code> is <code>null</code>,
	 *                               or <code>note.getExtension(JComponent.class)</code> is
	 *                               <code>null</code>
	 */
	public ResizeNoteEdit(Note note, Dimension initialSize, Dimension size) {
		if(note == null)
			throw new NullPointerException();

		noteComp = note.getExtension(JComponent.class);
		if(noteComp == null)
			throw new NullPointerException();

		this.oldSize = initialSize;
		this.newSize = size;
		perform();
	}

	// XXX assumes noteComp doesn't change for the note

	/**
	 * Performs this edit.
	 */
	private void perform() {
		noteComp.setPreferredSize(newSize);
		noteComp.revalidate();
	}

	//
	// AbstractUndoableEdit
	//

	@Override
	public String getPresentationName() {
		return "Resize Note";
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		perform();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		noteComp.setPreferredSize(oldSize);
		noteComp.revalidate();
	}
}
