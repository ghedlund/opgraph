package ca.gedge.opgraph.nodes.menu;

import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.swing.JMenu;

import ca.gedge.opgraph.app.GraphDocument;
import ca.gedge.opgraph.app.GraphEditorModel;
import ca.gedge.opgraph.app.MenuProvider;
import ca.gedge.opgraph.app.components.PathAddressableMenu;
import ca.gedge.opgraph.nodes.reflect.AbstractReflectNode;
import ca.gedge.opgraph.nodes.reflect.ReflectNode;

public class ReflectNodeMenuProvider implements MenuProvider {

	@Override
	public void installItems(GraphEditorModel model, PathAddressableMenu menu) {
	}

	@Override
	public void installPopupItems(Object context, MouseEvent event,
			GraphDocument doc, PathAddressableMenu menu) {
		final AddObjectCommand addObject = new AddObjectCommand(event.getPoint());
		addObject.putValue(AddObjectCommand.NAME, "Add object...");
		menu.addMenuItem("add_object", addObject);
		
		final AddStaticFieldCommand addStaticField = new AddStaticFieldCommand(event.getPoint());
		addStaticField.putValue(AddStaticFieldCommand.NAME, "Add static field...");
		menu.addMenuItem("add_static_field", addStaticField);
		
		final AddStaticMethodCommand addStaticMethod = new AddStaticMethodCommand(event.getPoint());
		addStaticMethod.putValue(AddStaticMethodCommand.NAME, "Add static method...");
		menu.addMenuItem("add_static_method", addStaticMethod);
		
		final AddInstanceMethodCommand addInstanceMethod = new AddInstanceMethodCommand(event.getPoint());
		addInstanceMethod.putValue(AddStaticMethodCommand.NAME, "Add instance method...");
		menu.addMenuItem("add_instance_method", addInstanceMethod);
		
		final AddCastCommand addCastNode = new AddCastCommand(event.getPoint());
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
						final AddMethodCommand methodMenuItem = new AddMethodCommand(m, event.getPoint());
						methodMenu.add(methodMenuItem);
					}
				}
			}
			
		}
	}

}
