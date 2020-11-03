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
package ca.phon.opgraph.app.extensions;

import java.awt.Component;
import java.util.Properties;

import ca.phon.opgraph.app.GraphDocument;

/**
 * A node extension that supplies a component for modifying settings
 * in a node.
 */
public interface NodeSettings {
	/**
	 * Gets a component for editing the node's settings.
	 * 
	 * @param document  the document which the component can use (e.g., for
	 *                  posting undoable edits)
	 * 
	 * @return the component
	 */
	public abstract Component getComponent(GraphDocument document);
	
	/**
	 * Gets the node settings as a properties object.
	 * 
	 * @return the properties
	 */
	public abstract Properties getSettings();

	/**
	 * Loads node settings from a properties object. Implementations should
	 * not assume certain keys exist.
	 * 
	 * @param properties  the properties to load settings from
	 */
	public abstract void loadSettings(Properties properties);
	
}
