package ca.gedge.opgraph.nodes.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import ca.gedge.opgraph.OpContext;
import ca.gedge.opgraph.exceptions.ProcessingException;

/**
 * Node that grabs an instance of the specified class
 * from the current context.
 */
public class ContextualItemClassNode extends ObjectNode {
	
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
	public void setDeclaredClass(Class<?> clazz) {
		super.setDeclaredClass(clazz);
		removeField(inputValueField);
	}

	@Override
	public void operate(OpContext context) throws ProcessingException {
		final Object obj = context.get(key);
		
		if(obj == null)
			throw new ProcessingException(new NullPointerException(key));
		if(!getDeclaredClass().isInstance(obj)) 
			throw new ProcessingException("Context value not of correct type.");
		
		for(ObjectNodePropertyInputField classInput:classInputs) {
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
		
		for(ObjectNodePropertyOutputField classOutput:classOutputs) {
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
	
	private final static String CONTEXT_KEY_PROP = 
			ContextualItemClassNode.class.getName() + ".key";

	@Override
	public Properties getSettings() {
		final Properties retVal = super.getSettings();
		retVal.put(CONTEXT_KEY_PROP, key);
		return retVal;
	}

	@Override
	public void loadSettings(Properties properties) {
		super.loadSettings(properties);
		if(properties.containsKey(CONTEXT_KEY_PROP)) {
			setKey(properties.getProperty(CONTEXT_KEY_PROP));
		}
	}
	
}
