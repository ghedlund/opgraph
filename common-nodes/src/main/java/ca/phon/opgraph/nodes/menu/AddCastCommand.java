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
package ca.phon.opgraph.nodes.menu;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.*;

import javax.swing.*;

import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.edits.graph.*;
import ca.phon.opgraph.nodes.reflect.*;

/**
 * Add a new {@link ObjectCastNode} to the graph.
 * 
 */
public class AddCastCommand extends AbstractAction {

	private static final long serialVersionUID = -5964240069884374978L;

	private static final Logger LOGGER = Logger
			.getLogger(AddCastCommand.class.getName());
	
	private GraphDocument document;
	
	private final Point point;
	
	public AddCastCommand(GraphDocument doc, Point p) {
		super();
		this.document = doc;
		this.point = p;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(GraphicsEnvironment.isHeadless())
			return;
		
		if(document != null) {
			// request a class name
			// TODO create a better UI for this
			final String className = JOptionPane.showInputDialog("Enter a class name:");
			if(className != null) {
				try {
					final Class<?> clazz = Class.forName(className);
					final ObjectCastNode castNode = new ObjectCastNode(clazz);
					
					final AddNodeEdit addNodeEdit = new AddNodeEdit(document.getGraph(), castNode, point.x, point.y);
					document.getUndoSupport().postEdit(addNodeEdit);
				} catch (ClassNotFoundException e) {
					LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
			}
		}
	}
		
}
