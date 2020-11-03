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

import java.awt.*;

import javax.swing.*;
import javax.swing.undo.*;

import ca.phon.opgraph.app.extensions.*;

/**
 * Sets the color of a note.
 */
public class SetNoteColorEdit extends AbstractUndoableEdit {
	/** The note's UI component */
	private JComponent noteComp;

	/** Old note color */
	private Color oldColor;

	/** New note color */
	private Color newColor;

	/**
	 * Constructs an edit that sets the color of a specified note.
	 * 
	 * @param note  the note whose color will be set
	 * @param color  the new color
	 * 
	 * @throws NullPointerException  if <code>note</code> is <code>null</code>,
	 *                               or <code>note.getExtension(JComponent.class)</code> is
	 *                               <code>null</code>
	 */
	public SetNoteColorEdit(Note note, Color color) {
		if(note == null)
			throw new NullPointerException();

		noteComp = note.getExtension(JComponent.class);
		if(noteComp == null)
			throw new NullPointerException();

		this.oldColor = noteComp.getBackground();
		this.newColor = color;
		perform();
	}

	// XXX assumes noteComp doesn't change for the note

	/**
	 * Performs this edit.
	 */
	private void perform() {
		noteComp.setBackground(newColor);
	}

	//
	// AbstractUndoableEdit
	//

	@Override
	public String getPresentationName() {
		return "Set Note Color";
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		perform();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		noteComp.setBackground(oldColor);
	}
}
