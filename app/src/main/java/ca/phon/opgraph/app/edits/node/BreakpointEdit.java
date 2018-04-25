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
package ca.phon.opgraph.app.edits.node;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ca.phon.opgraph.OpNode;

public class BreakpointEdit extends AbstractUndoableEdit {

	private static final long serialVersionUID = -5367065464117238764L;
	
	private OpNode node;
	
	private boolean shouldBreak;

	public BreakpointEdit(OpNode node, boolean shouldBreak) {
		super();
		
		this.node = node;
		this.shouldBreak = shouldBreak;
	}
	
	public void perform() {
		node.setBreakpoint(shouldBreak);
	}
	
	@Override
	public boolean isSignificant() {
		return node.isBreakpoint() != shouldBreak;
	}
	
	@Override
	public String getPresentationName() {
		return "Toggle breakpoint";
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		perform();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		node.setBreakpoint(!shouldBreak);
	}
	
}
