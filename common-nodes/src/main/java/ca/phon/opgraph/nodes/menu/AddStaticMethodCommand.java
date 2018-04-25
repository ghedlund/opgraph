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
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.*;

import javax.swing.*;

import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.edits.graph.AddNodeEdit;
import ca.phon.opgraph.nodes.reflect.StaticMethodNode;
import ca.phon.opgraph.util.ReflectUtil;

public class AddStaticMethodCommand extends AbstractAction {
	
	private static final Logger LOGGER = Logger
			.getLogger(AddStaticMethodCommand.class.getName());

	private static final long serialVersionUID = 4549741302418406320L;

	private GraphDocument document;
	
	private final Point point;
	
	public AddStaticMethodCommand(GraphDocument doc, Point p) {
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
					final List<Method> staticMethods = ReflectUtil.getStaticMethods(clazz);
					
					final JComboBox<Method> comboBox = new JComboBox<>(staticMethods.toArray(new Method[0]));
					comboBox.setRenderer(new DefaultListCellRenderer() {
						
						@Override
						public Component getListCellRendererComponent(JList arg0, Object arg1,
								int arg2, boolean arg3, boolean arg4) {
							final Method method = (Method)arg1;
							final JLabel retVal = (JLabel)super.getListCellRendererComponent(arg0, arg1, arg2, arg3, arg4);
							retVal.setText(ReflectUtil.getSignature(method));
							return retVal;
						}
					});
					int retVal = 
							JOptionPane.showConfirmDialog(null, comboBox, "Select static method:", JOptionPane.OK_CANCEL_OPTION);
					if(retVal == JOptionPane.OK_OPTION) {
						final Method selectedMethod = (Method)comboBox.getSelectedItem();
						
						final StaticMethodNode node = new StaticMethodNode(selectedMethod);
						final AddNodeEdit edit = new AddNodeEdit(document.getGraph(), node, point.x, point.y);
						document.getUndoSupport().postEdit(edit);
					}
					
				} catch (ClassNotFoundException e) {
					LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
				
			}
		}
	}

}
