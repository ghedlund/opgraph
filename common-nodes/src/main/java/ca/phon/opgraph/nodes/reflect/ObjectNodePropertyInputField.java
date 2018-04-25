package ca.phon.opgraph.nodes.reflect;

import java.lang.reflect.Method;

import ca.phon.opgraph.InputField;

class ObjectNodePropertyInputField extends InputField {
	
	final Method setMethod;
	
	public ObjectNodePropertyInputField(String key, String description, Class<?> type, Method method) {
		super(key, description, type);
		this.setMethod = method;
	}
	
}
