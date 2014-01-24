package ca.gedge.opgraph.nodes.reflect;

import java.lang.reflect.Method;

import ca.gedge.opgraph.OutputField;

class ClassOutputField extends OutputField {
	
	final Method getMethod;
	
	public ClassOutputField(String key, String description, Class<?> type, Method method) {
		super(key, description, false, type);
		this.getMethod = method;
	}
	
}