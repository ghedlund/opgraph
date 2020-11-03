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
package ca.phon.opgraph.app.components;

import java.awt.*;

import javax.swing.*;

/**
 * A menu that provides path-based addressing, to make menu-building a simpler task.
 *
 * A menu path is a sequence of alphanumeric strings separated by forward slashes
 * (the '/' character). Examples:
 * <ul>
 *   <li><code>file/open recent</code></li>
 *   <li><code>edit/foo/bar/test</code></li>
 * </ul>
 */
public interface PathAddressableMenu {
	/**
	 * Gets the menu for a specified path.
	 * 
	 * @param path  the path
	 * 
	 * @return the menu associated with the given path, or <code>null</code>
	 *         if no menu exists for the given path
	 */
	public abstract JMenu getMenu(String path);

	/**
	 * Gets the menu item for a specified path.
	 * 
	 * @param path  the path
	 * 
	 * @return the menu item associated with the given path, or <code>null</code>
	 *         if no menu exists for the given path
	 */
	public abstract JMenuItem getMenuItem(String path);

	/**
	 * Gets the menu element for a specified path.
	 * 
	 * @param path  the path
	 * 
	 * @return the menu element associated with the given path, or <code>null</code>
	 *         if no menu exists for the given path
	 */
	public abstract MenuElement getMenuElement(String path);

	/**
	 * Adds a submenu to the menu at a specified path.
	 * 
	 * @param path  the path
	 * @param text  the text to use for the submenu 
	 * 
	 * @return the newly created submenu
	 */
	public abstract JMenu addMenu(String path, String text);

	/**
	 * Adds a menu item to the menu at a specified path.
	 * 
	 * @param path  the path
	 * @param action  the action to use for the menu item 
	 * 
	 * @return the newly created menu item
	 */
	public abstract JMenuItem addMenuItem(String path, Action action);

	/**
	 * Adds a component to the menu at a specified path.
	 * 
	 * @param path  the path
	 * @param component  the component to add 
	 * 
	 * @return the component
	 */
	public abstract Component addComponent(String path, Component component);

	/**
	 * Adds a separator to the menu at a specified path.
	 * 
	 * @param path  the path
	 */
	public abstract void addSeparator(String path);
}
