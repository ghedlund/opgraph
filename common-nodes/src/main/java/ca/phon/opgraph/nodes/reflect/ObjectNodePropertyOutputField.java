package ca.phon.opgraph.nodes.reflect;

import java.lang.reflect.Method;

import ca.phon.opgraph.OutputField;

class ObjectNodePropertyOutputField extends OutputField {
	
	final Method getMethod;
	
	public ObjectNodePropertyOutputField(String key, String description, Class<?> type, Method method) {
		super(key, description, false, type);
		this.getMethod = method;
	}
	
}