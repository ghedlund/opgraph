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
		
		final AddObjectReferenceCommand addObjectRef = new AddObjectReferenceCommand(doc, event.getPoint());
		addObjectRef.putValue(AddObjectReferenceCommand.NAME, "Add object reference...");
		menu.addMenuItem("add_object_ref", addObjectRef);
		
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
