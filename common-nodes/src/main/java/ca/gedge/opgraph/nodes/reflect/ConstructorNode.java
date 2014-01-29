package ca.gedge.opgraph.nodes.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import ca.gedge.opgraph.InputField;
import ca.gedge.opgraph.OpContext;
import ca.gedge.opgraph.OutputField;
import ca.gedge.opgraph.app.extensions.NodeSettings;
import ca.gedge.opgraph.exceptions.ProcessingException;
import ca.gedge.opgraph.util.ReflectUtil;

/**
 * Node for object constructors.
 */
public class ConstructorNode extends AbstractReflectNode {
	
	private Constructor<?> constructor;
	
	private final List<InputField> argFields = new ArrayList<InputField>();
	
	private OutputField outputField;
	
	public ConstructorNode() {
		super();
		putExtension(NodeSettings.class, this);
	}
	
	public ConstructorNode(Constructor<?> constructor) {
		super();
		setConstructor(constructor);
		putExtension(NodeSettings.class, this);
	}
	
	@Override
	public void setClassMember(Member member) {
		if(member instanceof Constructor) {
			setConstructor((Constructor<?>)member);
		}
	}
	
	public void setConstructor(Constructor<?> constructor) {
		super.setDeclaredClass(constructor.getDeclaringClass());
		super.setClassMember(constructor);
		setName(constructor.getDeclaringClass().getSimpleName() + "#" + ReflectUtil.getSignature(constructor, false));
		
		final Class<?> type = constructor.getDeclaringClass();
		final OutputField outputField = new OutputField("value", "constructor", true, type);
		putField(outputField);
		
		argFields.clear();
		for(int i = 0; i < constructor.getParameterTypes().length; i++) {
			final Class<?> paramType = constructor.getParameterTypes()[i];
			final InputField inputField = new InputField("arg" + (i+1), "", paramType);
			inputField.setOptional(true);
			putField(inputField);
			argFields.add(inputField);
		}
	}

	@Override
	public void operate(OpContext context) throws ProcessingException {
		final Object[] args = new Object[argFields.size()];
		for(int i = 0; i < argFields.size(); i++) {
			final InputField inputField = argFields.get(i);
			args[0] = context.get(inputField);
		}
		
		try {
			final Object val = constructor.newInstance(args);
			context.put(outputField, val);
		} catch (IllegalArgumentException e) {
			throw new ProcessingException(e);
		} catch (InstantiationException e) {
			throw new ProcessingException(e);
		} catch (IllegalAccessException e) {
			throw new ProcessingException(e);
		} catch (InvocationTargetException e) {
			throw new ProcessingException(e);
		}
	}
	
}
