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

import javax.swing.*;
import javax.swing.undo.*;

import ca.phon.opgraph.app.commands.graph.*;
import ca.phon.opgraph.app.extensions.*;

/**
 * Sets the location of a note.
 */
public class MoveNoteEdit extends AbstractUndoableEdit {
	/** The note's UI component */
	private JComponent noteComp;

	/** The distance along the x-axis to move the node */
	private int deltaX;

	/** The distance along the y-axis to move the node */
	private int deltaY;

	/**
	 * Constructs an edit that sets the location of a specified note.
	 * 
	 * @param note  the note whose location will be set
	 * @param deltaX  the x-axis delta
	 * @param deltaY  the y-axis delta
	 * 
	 * @throws NullPointerException  if <code>note</code> is <code>null</code>,
	 *                               or <code>note.getExtension(JComponent.class)</code> is
	 *                               <code>null</code>
	 */
	public MoveNoteEdit(Note note, int deltaX, int deltaY) {
		if(note == null)
			throw new NullPointerException();

		noteComp = note.getExtension(JComponent.class);
		if(noteComp == null)
			throw new NullPointerException();

		this.deltaX = deltaX;
		this.deltaY = deltaY;
		perform();
	}

	// XXX assumes noteComp doesn't change for the note

	/**
	 * Performs this edit.
	 */
	private void perform() {
		noteComp.setLocation(noteComp.getX() + deltaX, noteComp.getY() + deltaY);
	}

	//
	// AbstractUndoableEdit
	//

	@Override
	public boolean replaceEdit(UndoableEdit anEdit) {
		if(anEdit instanceof MoveNoteEdit) {
			final MoveNoteEdit moveEdit = (MoveNoteEdit)anEdit;
			if(noteComp.equals(moveEdit.noteComp)) {
				final boolean xDirSame = ((deltaX <= 0 && moveEdit.deltaX <= 0) || (deltaX >= 0 && moveEdit.deltaX >= 0));
				final boolean yDirSame = ((deltaY <= 0 && moveEdit.deltaY <= 0) || (deltaY >= 0 && moveEdit.deltaY >= 0));
				if(xDirSame && yDirSame) {
					deltaX += moveEdit.deltaX;
					deltaY += moveEdit.deltaY;
					moveEdit.die();
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public String getPresentationName() {
		final String prefix = "Move Note";
		final String suffix = MoveNodeCommand.getMoveString(deltaX, deltaY);
		if(suffix.length() == 0)
			return prefix;

		return prefix + " " + suffix;
	}

	@Override
	public boolean isSignificant() {
		return (deltaX != 0 || deltaY != 0);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		perform();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		noteComp.setLocation(noteComp.getX() - deltaX, noteComp.getY() - deltaY);
	}
}
