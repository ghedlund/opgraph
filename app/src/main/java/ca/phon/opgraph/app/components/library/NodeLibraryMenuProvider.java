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
package ca.phon.opgraph.app.components.library;

import java.awt.Point;
import java.awt.event.*;
import java.util.*;
import java.util.logging.Logger;

import javax.swing.*;

import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.components.*;
import ca.phon.opgraph.app.edits.graph.AddNodeEdit;
import ca.phon.opgraph.library.*;

/**
 * Implementation of {@link MenuProvider} for {@link NodeLibrary}. Provides
 * menu items which allow the user to instantiate nodes known to the library  
 */
public class NodeLibraryMenuProvider implements MenuProvider {
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(NodeLibraryMenuProvider.class.getName());

	/**
	 * Returns a menu which reflects a given {@link NodeLibrary}.
	 * 
	 * @param menu  the menu to add the items to
	 * @param document  the document upon which the returned menu items will act
	 * @param library  the library to construct the menu from 
	 * @param point  the point at which a node will be instantiated
	 */
	private void addMenuItems(JMenu menu, final GraphDocument document, NodeLibrary library, final Point point) {
		if(library != null) {
			final Map<String, List<NodeData>> categoryMap = library.getCategoryMap();
			for(Map.Entry<String, List<NodeData>> entry : categoryMap.entrySet()) {
				final JMenu categoryMenu = new JMenu(entry.getKey());

				// For each NodeData in this category, create a menu item that will
				// instantiate that node into the document
				for(final NodeData info : entry.getValue()) {
					categoryMenu.add(new AbstractAction(info.name) {
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								final OpGraph graph = document.getGraph();
								document.getUndoSupport().postEdit(new AddNodeEdit(graph, info, point.x, point.y));
							} catch(InstantiationException exc) {
								final String message = "Unable to create '" + info.name + "'";
								LOGGER.severe(message);
								ErrorDialog.showError(exc, message);
							}
						}
					});
				}

				// Now add it to the parent menu
				menu.add(categoryMenu);
			}
		}
	}

	//
	// MenuProvider implementation
	//
	@Override
	public void installItems(GraphEditorModel model, PathAddressableMenu menu) {
		// TODO Auto-generated method stub
	}

	@Override
	public void installPopupItems(Object context, MouseEvent event, GraphDocument doc, PathAddressableMenu menu) {
		// add a new menu item for adding a new 'object'
		final NodeLibrary library = new NodeLibraryViewer(doc).getLibrary();
		final JMenu addNodeMenu = menu.addMenu("add_node", "Add");
		addMenuItems(addNodeMenu, doc, library, event.getPoint());
	}
}
