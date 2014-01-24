package ca.gedge.opgraph.nodes.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ca.gedge.opgraph.OpContext;
import ca.gedge.opgraph.exceptions.ProcessingException;

/**
 * Node that grabs an instance of the specified class
 * from the current context.
 */
public class ContextualItemClassNode extends ClassNode {
	
	private String key;

	public ContextualItemClassNode() {
		super();
	}
	
	public ContextualItemClassNode(String key, Class<?> clazz) {
		super(clazz);
		setKey(key);
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public void setClass(Class<?> clazz) {
		super.setClass(clazz);
		removeField(inputValueField);
	}

	@Override
	public void operate(OpContext context) throws ProcessingException {
		final Object obj = context.get(key);
		
		if(obj == null)
			throw new ProcessingException(new NullPointerException(key));
		if(!getDeclaredClass().isInstance(obj)) 
			throw new ProcessingException("Context value not of correct type.");
		
		for(ClassInputField classInput:classInputs) {
			final Object val = context.get(classInput);
			if(val != null) {
				final Method setMethod = classInput.setMethod;
				try {
					setMethod.invoke(obj, val);
				} catch (IllegalArgumentException e) {
					throw new ProcessingException(e);
				} catch (IllegalAccessException e) {
					throw new ProcessingException(e);
				} catch (InvocationTargetException e) {
					throw new ProcessingException(e);
				}
			}
		}
		
		for(ClassOutputField classOutput:classOutputs) {
			try {
				final Object val = classOutput.getMethod.invoke(obj, new Object[0]);
				context.put(classOutput, val);
			} catch (IllegalArgumentException e) {
				throw new ProcessingException(e);
			} catch (IllegalAccessException e) {
				throw new ProcessingException(e);
			} catch (InvocationTargetException e) {
				throw new ProcessingException(e);
			}
		}
		
		context.put(outputValueField, obj);
	}
	
}
