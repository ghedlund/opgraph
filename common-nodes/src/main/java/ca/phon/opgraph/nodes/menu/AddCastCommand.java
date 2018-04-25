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
package ca.phon.opgraph.nodes.menu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.logging.*;

import javax.swing.*;

import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.edits.graph.AddNodeEdit;
import ca.phon.opgraph.nodes.reflect.ObjectCastNode;

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
