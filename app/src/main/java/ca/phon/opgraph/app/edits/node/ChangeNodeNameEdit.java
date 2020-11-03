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

/**
 * Modifies the name of a node.
 */
public class ChangeNodeNameEdit extends AbstractUndoableEdit {
	/** The node whose name is changing */
	private final OpNode node;

	/** The new name of the node */
	private final String newName;

	/** The previous name of the node */
	private String oldName;

	/**
	 * Constructs an edit that changes a node's name. 
	 * 
	 * @param node  the node whose name will be changed
	 * @param name  the new name
	 */
	public ChangeNodeNameEdit(OpNode node, String name) {
		this.node = node;
		this.newName = name;
		this.oldName = node.getName();
		perform();
	}
	
	public OpNode getNode() {
		return this.node;
	}

	/**
	 * Performs this edit.
	 */
	private void perform() {
		node.setName(newName);
	}

	//
	// AbstractUndoableEdit
	//

	@Override
	public boolean isSignificant() {
		return !oldName.equals(newName);
	}

	@Override
	public boolean replaceEdit(UndoableEdit anEdit) {
		boolean ret = false;
		if(anEdit instanceof ChangeNodeNameEdit) {
			final ChangeNodeNameEdit cnn = (ChangeNodeNameEdit)anEdit;
			ret = (cnn.node == node
			      && (newName != null && newName.equals(cnn.newName))
			      && (oldName != null && oldName.equals(cnn.oldName)) );

			if(ret)
				oldName = cnn.oldName;
		}
		return ret;
	}

	@Override
	public String getPresentationName() {
		return "Change Node Name";
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		perform();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		node.setName(oldName);
	}
}
