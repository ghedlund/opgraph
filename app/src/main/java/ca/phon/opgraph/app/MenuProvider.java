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
package ca.phon.opgraph.app;

import java.awt.event.MouseEvent;

import ca.phon.opgraph.app.components.PathAddressableMenu;

/**
 * An interface for menu providers.    
 */
public interface MenuProvider {
	/**
	 * Installs the items associated with this provider to the orimary menu
	 * of the requesting GUI.
	 * 
	 * @param model  an application model that menu items can act upon
	 * @param menu  the menu to install things to
	 */
	public abstract void installItems(GraphEditorModel model, PathAddressableMenu menu);

	/**
	 * Installs the items associated with this provider to a popup menu.
	 * An object is given for context, and the provider should determine if
	 * it needs to install any items on that menu based on the context object. 
	 * 
	 * @param context  the object used as context
	 * @param event  the mouse event that created the popup
	 * @param document the graph document
	 * @param menu  the menu to install things to
	 */
	public abstract void installPopupItems(Object context, MouseEvent event, GraphDocument document, PathAddressableMenu menu);
}
