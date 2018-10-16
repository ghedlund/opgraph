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
package ca.phon.opgraph.nodes.menu;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.*;

import javax.swing.*;

import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.edits.graph.AddNodeEdit;
import ca.phon.opgraph.nodes.reflect.ClassNode;

public class AddClassCommand extends AbstractAction {
	
	private static final Logger LOGGER = Logger
			.getLogger(AddObjectReferenceCommand.class.getName());

	private static final long serialVersionUID = -2443166989357102209L;

	private GraphDocument document;
	
	private final Point point;
	
	public AddClassCommand(GraphDocument doc, Point p) {
		super();
		this.document = doc;
		this.point = p;
		putValue(ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | KeyEvent.VK_ALT));
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
						
						final ClassNode node = new ClassNode(clazz);
						node.setName(clazz.getSimpleName());
						final AddNodeEdit edit = new AddNodeEdit(document.getGraph(), node, point.x, point.y);
						document.getUndoSupport().postEdit(edit);
					} catch (ClassNotFoundException e) {
						Toolkit.getDefaultToolkit().beep();
						LOGGER.log(Level.SEVERE, e.getLocalizedMessage(),
								e);
					}
			}
		}
	}

}
