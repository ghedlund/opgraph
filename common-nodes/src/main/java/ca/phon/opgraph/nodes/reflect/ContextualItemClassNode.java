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
package ca.phon.opgraph.nodes.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.exceptions.ProcessingException;

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
			throw new ProcessingException(null, new NullPointerException(key));
		if(!getDeclaredClass().isInstance(obj)) 
			throw new ProcessingException(null, "Context value not of correct type.");
		
		for(ObjectNodePropertyInputField classInput:classInputs) {
			final Object val = context.get(classInput);
			if(val != null) {
				final Method setMethod = classInput.setMethod;
				try {
					setMethod.invoke(obj, val);
				} catch (IllegalArgumentException e) {
					throw new ProcessingException(null, e);
				} catch (IllegalAccessException e) {
					throw new ProcessingException(null, e);
				} catch (InvocationTargetException e) {
					throw new ProcessingException(null, e);
				}
			}
		}
		
		for(ObjectNodePropertyOutputField classOutput:classOutputs) {
			try {
				final Object val = classOutput.getMethod.invoke(obj, new Object[0]);
				context.put(classOutput, val);
			} catch (IllegalArgumentException e) {
				throw new ProcessingException(null, e);
			} catch (IllegalAccessException e) {
				throw new ProcessingException(null, e);
			} catch (InvocationTargetException e) {
				throw new ProcessingException(null, e);
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
