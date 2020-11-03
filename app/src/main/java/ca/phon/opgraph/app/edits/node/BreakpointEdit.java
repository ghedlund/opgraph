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
package ca.phon.opgraph.app.edits.node;

import javax.swing.undo.*;

import ca.phon.opgraph.*;

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
