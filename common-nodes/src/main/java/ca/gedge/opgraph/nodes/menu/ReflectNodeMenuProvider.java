package ca.gedge.opgraph.nodes.menu;

import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.swing.JMenu;

import ca.gedge.opgraph.app.GraphEditorModel;
import ca.gedge.opgraph.app.MenuProvider;
import ca.gedge.opgraph.app.components.PathAddressableMenu;
import ca.gedge.opgraph.nodes.reflect.ClassNodeProtocol;

public class ReflectNodeMenuProvider implements MenuProvider {

	@Override
	public void installItems(GraphEditorModel model, PathAddressableMenu menu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void installPopupItems(Object context, MouseEvent event,
			GraphEditorModel model, PathAddressableMenu menu) {
		final AddObjectCommand addObject = new AddObjectCommand();
		addObject.putValue(AddObjectCommand.NAME, "Add object...");
		menu.addMenuItem("add_object", addObject);
		
		final AddStaticFieldCommand addStaticField = new AddStaticFieldCommand();
		addStaticField.putValue(AddStaticFieldCommand.NAME, "Add static field...");
		menu.addMenuItem("add_static_field", addStaticField);
		
		final AddStaticMethodCommand addStaticMethod = new AddStaticMethodCommand();
		addStaticMethod.putValue(AddStaticMethodCommand.NAME, "Add static method...");
		menu.addMenuItem("add_static_method", addStaticMethod);
		
		final AddInstanceMethodCommand addInstanceMethod = new AddInstanceMethodCommand();
		addInstanceMethod.putValue(AddStaticMethodCommand.NAME, "Add instance method...");
		menu.addMenuItem("add_instance_method", addInstanceMethod);
		
		if(context instanceof ClassNodeProtocol) {
			// create a new menu
			final JMenu methodMenu = menu.addMenu("method_menu", "Add method...");
			
			final ClassNodeProtocol classNode = (ClassNodeProtocol)context;
			final Class<?> clazz = classNode.getDeclaredClass();
			
			for(Method m:clazz.getMethods()) {
				if(!Modifier.isStatic(m.getModifiers())) {
					final String methodName = m.getName();
					
					if(	    m.getParameterTypes().length > 0 || (
							(!methodName.startsWith("get") && !methodName.equals("get")) 
							&& !methodName.startsWith("is")
							&& !m.getName().startsWith("set"))) {
						final AddMethodCommand methodMenuItem = new AddMethodCommand(m);
						methodMenu.add(methodMenuItem);
					}
				}
			}
			
		}
	}

}
