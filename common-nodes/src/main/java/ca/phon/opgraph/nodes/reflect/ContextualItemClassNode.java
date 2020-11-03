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
package ca.phon.opgraph.nodes.reflect;

import java.lang.reflect.*;
import java.util.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.exceptions.*;

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
