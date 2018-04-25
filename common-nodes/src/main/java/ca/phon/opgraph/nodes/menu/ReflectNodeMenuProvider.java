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

import java.awt.event.MouseEvent;
import java.lang.reflect.*;

import javax.swing.JMenu;

import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.components.PathAddressableMenu;
import ca.phon.opgraph.nodes.reflect.ReflectNode;

public class ReflectNodeMenuProvider implements MenuProvider {

	@Override
	public void installItems(GraphEditorModel model, PathAddressableMenu menu) {
	}

	@Override
	public void installPopupItems(Object context, MouseEvent event,
			GraphDocument doc, PathAddressableMenu menu) {
		final AddObjectCommand addObject = new AddObjectCommand(doc, event.getPoint());
		addObject.putValue(AddObjectCommand.NAME, "Add object...");
		menu.addMenuItem("add_object", addObject);
		
		final AddClassCommand addClass = new AddClassCommand(doc, event.getPoint());
		addClass.putValue(AddClassCommand.NAME, "Add class...");
		menu.addMenuItem("add_class", addClass);
		
		final AddStaticFieldCommand addStaticField = new AddStaticFieldCommand(doc, event.getPoint());
		addStaticField.putValue(AddStaticFieldCommand.NAME, "Add static field...");
		menu.addMenuItem("add_static_field", addStaticField);
		
		final AddStaticMethodCommand addStaticMethod = new AddStaticMethodCommand(doc, event.getPoint());
		addStaticMethod.putValue(AddStaticMethodCommand.NAME, "Add static method...");
		menu.addMenuItem("add_static_method", addStaticMethod);
		
		final AddInstanceMethodCommand addInstanceMethod = new AddInstanceMethodCommand(doc, event.getPoint());
		addInstanceMethod.putValue(AddStaticMethodCommand.NAME, "Add instance method...");
		menu.addMenuItem("add_instance_method", addInstanceMethod);
		
		final AddCastCommand addCastNode = new AddCastCommand(doc, event.getPoint());
		addCastNode.putValue(AddCastCommand.NAME, "Add cast...");
		menu.addMenuItem("add_cast", addCastNode);
		
		if(context instanceof ReflectNode) {
			// create a new menu
			final JMenu methodMenu = menu.addMenu("method_menu", "Add method...");
			
			final ReflectNode classNode = (ReflectNode)context;
			final Class<?> clazz = classNode.getDeclaredClass();
			
			for(Method m:clazz.getMethods()) {
				if(!Modifier.isStatic(m.getModifiers())) {
					final String methodName = m.getName();
					
					if(	    m.getParameterTypes().length > 0 || (
							(!methodName.startsWith("get") && !methodName.equals("get")) 
							&& !methodName.startsWith("is")
							&& !m.getName().startsWith("set"))) {
						final AddMethodCommand methodMenuItem = new AddMethodCommand(doc, m, event.getPoint());
						methodMenu.add(methodMenuItem);
					}
				}
			}
			
		}
	}

}
