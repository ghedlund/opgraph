package ca.gedge.opgraph.nodes.reflect;

import java.lang.reflect.Method;

import ca.gedge.opgraph.InputField;

class ObjectNodePropertyInputField extends InputField {
	
	final Method setMethod;
	
	public ObjectNodePropertyInputField(String key, String description, Class<?> type, Method method) {
		super(key, description, type);
		this.setMethod = method;
	}
	
}
