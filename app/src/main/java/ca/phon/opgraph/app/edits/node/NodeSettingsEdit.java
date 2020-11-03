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
package ca.phon.opgraph.app.edits.node;

import java.util.Properties;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ca.phon.opgraph.app.extensions.NodeSettings;

/**
 * Changes a node's settings.
 */
public class NodeSettingsEdit extends AbstractUndoableEdit {
	/** The settings for the node */
	private NodeSettings settings;

	/** The new settings for the node */
	private Properties newSettings;

	/** The old settings for the node */
	private Properties oldSettings;

	/**
	 * Constructs a node settings edit.
	 * 
	 * @param settings  the settings object to modify
	 * @param newSettings  the new settings to set
	 */
	public NodeSettingsEdit(NodeSettings settings, Properties newSettings) {
		this.settings = settings;
		this.oldSettings = settings.getSettings();
		this.newSettings = newSettings;
		perform();
	}

	/**
	 * Performs the edit
	 */
	private void perform() {
		settings.loadSettings(newSettings);
	}

	//
	// AbstractUndoableEdit
	//

	@Override
	public String getPresentationName() {
		return "Change Node Settings";
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		perform();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		settings.loadSettings(oldSettings);
	}
}
