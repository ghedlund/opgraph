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

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.edits.graph.AddNodeEdit;
import ca.phon.opgraph.nodes.reflect.ConstructorNode;
import ca.phon.opgraph.util.ReflectUtil;

public class AddObjectCommand extends AbstractAction {
	
	private static final Logger LOGGER = Logger
			.getLogger(AddObjectCommand.class.getName());
	
	private GraphDocument document;
	
	private final Point point;
	
	public AddObjectCommand(GraphDocument doc, Point p) {
		super();
		this.document = doc;
		this.point = p;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(GraphicsEnvironment.isHeadless())
			return;
		
		if(document != null) {
			final String className = JOptionPane.showInputDialog("Enter a class name:");
			if(className != null) {
				try {
					final Class<?> clazz = Class.forName(className);
					final Constructor<?>[] constructors = clazz.getConstructors();
					
					final JComboBox<Constructor<?>> comboBox = new JComboBox<>(constructors);
					comboBox.setRenderer(new DefaultListCellRenderer() {
						
						@Override
						public Component getListCellRendererComponent(JList arg0, Object arg1,
								int arg2, boolean arg3, boolean arg4) {
							final Constructor<?> c = (Constructor<?>)arg1;
							final JLabel retVal = (JLabel)super.getListCellRendererComponent(arg0, arg1, arg2, arg3, arg4);
							retVal.setText(ReflectUtil.getSignature(c));
							return retVal;
						}
					});
					int retVal = 
							JOptionPane.showConfirmDialog(null, comboBox, "Select constructor:", JOptionPane.OK_CANCEL_OPTION);
					if(retVal == JOptionPane.OK_OPTION) {
						final Constructor<?> cstr = (Constructor<?>)comboBox.getSelectedItem();
						
						final ConstructorNode node = new ConstructorNode(cstr);
						final AddNodeEdit edit = new AddNodeEdit(document.getGraph(), node, point.x, point.y);
						document.getUndoSupport().postEdit(edit);
					}
					
				} catch (ClassNotFoundException ex) {
					LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
				}
				
			}
		}
	}

}
