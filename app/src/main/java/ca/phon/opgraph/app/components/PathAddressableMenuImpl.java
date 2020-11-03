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

import ca.phon.opgraph.util.*;

/**
 * An implementation of {@link PathAddressableMenu} that operates on a given
 * menu element.
 */
public class PathAddressableMenuImpl implements PathAddressableMenu {
	/** The root element to base paths off of */
	private MenuElement root;

	/**
	 * Constructs a path-adressable menu with a given menu element as its root.
	 * 
	 * @param root  the menu element to use as root
	 */
	public PathAddressableMenuImpl(MenuElement root) {
		this.root = root;
	}

	//
	// PathAddressableMenu
	//

	@Override
	public JMenu getMenu(String path) {
		final MenuElement elem = getMenuElement(path);
		return (elem != null && (elem instanceof JMenu) ? (JMenu)elem : null);
	}

	@Override
	public JMenuItem getMenuItem(String path) {
		final MenuElement elem = getMenuElement(path);
		return (elem != null && (elem instanceof JMenuItem) ? (JMenuItem)elem : null);
	}

	@Override
	public MenuElement getMenuElement(String path) {
		final Pair<String, MenuElement> deepest = getDeepestMenuElement(root, path);
		return ((deepest.getFirst().length() == path.length()) ? deepest.getSecond() : null);
	}

	@Override
	public JMenu addMenu(String path, String text) {
		final Pair<String, MenuElement> deepest = getDeepestMenuElement(root, path);
		final String name = path.substring(deepest.getFirst().length());

		JMenu ret = null;
		if(name.indexOf('/') == -1) {
			if(deepest.getSecond() instanceof JMenu) {
				ret = new JMenu(text);
				ret.setName(name);
				((JMenu)deepest.getSecond()).add(ret);
			} else if(deepest.getSecond() instanceof JPopupMenu) {
				ret = new JMenu(text);
				ret.setName(name);
				ret.setIcon(null);
				((JPopupMenu)deepest.getSecond()).add(ret);
			} else if(deepest.getSecond() instanceof JMenuBar) {
				ret = new JMenu(text);
				ret.setName(name);
				((JMenuBar)deepest.getSecond()).add(ret);
			}
		}
		return ret;
	}

	@Override
	public JMenuItem addMenuItem(String path, Action action) {
		final Pair<String, MenuElement> deepest = getDeepestMenuElement(root, path);
		final String name = path.substring(deepest.getFirst().length());

		JMenuItem ret = null;
		if(name.indexOf('/') == -1) {
			if(deepest.getSecond() instanceof JMenu) {
				ret = new JMenuItem(action);
				ret.setName(name);
				ret.setIcon(null);
				((JMenu)deepest.getSecond()).add(ret);
			} else if(deepest.getSecond() instanceof JPopupMenu) {
				ret = new JMenuItem(action);
				ret.setName(name);
				ret.setIcon(null);
				((JPopupMenu)deepest.getSecond()).add(ret);
			} else if(deepest.getSecond() instanceof JMenuBar) {
				ret = new JMenuItem(action);
				ret.setName(name);
				ret.setIcon(null);
				((JMenuBar)deepest.getSecond()).add(ret);
			}
		}
		return ret;
	}

	@Override
	public Component addComponent(String path, Component component) {
		final Pair<String, MenuElement> deepest = getDeepestMenuElement(root, path);
		final String name = path.substring(deepest.getFirst().length());

		if(name.indexOf('/') == -1) {
			component.setName(name);
			if(deepest.getSecond() instanceof JMenu) {
				((JMenu)deepest.getSecond()).add(component);
			} else if(deepest.getSecond() instanceof JPopupMenu) {
				((JPopupMenu)deepest.getSecond()).add(component);
			} else if(deepest.getSecond() instanceof JMenuBar) {
				((JMenuBar)deepest.getSecond()).add(component);
			}
		} else component = null;

		return component;
	}

	@Override
	public void addSeparator(String path) {
		final Pair<String, MenuElement> deepest = getDeepestMenuElement(root, path);
		if(deepest.getFirst().length() == path.length()) {
			if(deepest.getSecond().getSubElements().length > 0) {
				if(deepest.getSecond() instanceof JMenu) {
					((JMenu)deepest.getSecond()).addSeparator();
				} else if(deepest.getSecond() instanceof JPopupMenu) {
					((JPopupMenu)deepest.getSecond()).addSeparator();
				}
			}
		}
	}

	private Pair<String, MenuElement> getDeepestMenuElement(MenuElement elem, String path) {
		int position = 0;
		if(elem != null && path != null) {
			final String [] components = path.split("/");
			int index = 0;

			// Go as deep as we can go
			while(index < components.length) {
				final int oldIndex = index;
				for(MenuElement subelem : elem.getSubElements()) {
					if(components[index].equals(subelem.getComponent().getName())) {
						position += components[index].length() + 1;
						++index;
						elem = subelem;
						break;
					}
				}

				// If we didn't move, stop
				if(index == oldIndex)
					break;
			}

			if(index == components.length)
				--position;
		}

		return new Pair<String, MenuElement>(path.substring(0, position), elem);
	}
}
