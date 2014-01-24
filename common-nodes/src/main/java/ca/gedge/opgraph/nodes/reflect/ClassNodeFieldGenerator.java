package ca.gedge.opgraph.nodes.reflect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class used to add input/output fields based on 
 * class definitions.
 */
public class ClassNodeFieldGenerator {
	
	private final List<ClassInputField> inputFields = new ArrayList<ClassInputField>();
	
	private final List<ClassOutputField> outputFields = new ArrayList<ClassOutputField>();
	
	public ClassNodeFieldGenerator() {
		super();
	}

	public List<ClassInputField> getInputFields() {
		return inputFields;
	}

	public List<ClassOutputField> getOutputFields() {
		return outputFields;
	}
	
	/**
	 * Scan the given class and generate input/ouput fields.
	 * 
	 * @param clazz
	 */
	public void scanClass(Class<?> clazz) {
		inputFields.addAll(scanClassSetters(clazz));
		outputFields.addAll(scanClassGetters(clazz));
	}

	/*
	 * Scan class for get methods and return list of 
	 * created output fields
	 * 
	 * @return outputs
	 */
	private List<ClassOutputField> scanClassGetters(Class<?> clazz) {
		final List<ClassOutputField> retVal = new ArrayList<ClassOutputField>();
		final Method[] methods = clazz.getMethods();
		
		for(Method method:methods) {
			String propName = null;
			Class<?> outputType = null;
			if(method.getName().startsWith("get") 
					&& method.getParameterTypes().length == 0) {
				propName = method.getName().substring(3);
				propName = propName.replace(propName.charAt(0), Character.toLowerCase(propName.charAt(0)));
				
				outputType = method.getReturnType();
				
			} else if(method.getName().startsWith("is")
					&& method.getParameterTypes().length == 0
					&& method.getReturnType() == Boolean.class) {
				propName = method.getName().substring(2);
				propName = propName.replace(propName.charAt(0), Character.toLowerCase(propName.charAt(0)));
				
				outputType = method.getReturnType();
			}
			if(propName == null || propName.length() == 0 || propName.equals("class")) continue;
			
			if(outputType.isPrimitive()) {
				outputType = wrapperClassForPrimitive(outputType);
			}
			
			final ClassOutputField outputField = new ClassOutputField(propName, "", outputType, method);
			outputField.setFixed(true);
//			putField(outputField);
			retVal.add(outputField);
		}
		return retVal;
	}
	
	private List<ClassInputField> scanClassSetters(Class<?> clazz) {
		final List<ClassInputField> retVal = new ArrayList<ClassInputField>();
		final Method[] methods = clazz.getMethods();
		for(Method method:methods) {
			if(method.getName().startsWith("set")
					&& method.getParameterTypes().length == 1) {
				String propName = method.getName().substring(3);
				propName = propName.replace(propName.charAt(0), Character.toLowerCase(propName.charAt(0)));
				
				Class<?> inputType = method.getParameterTypes()[0];
				if(inputType.isPrimitive()) {
					inputType = wrapperClassForPrimitive(inputType);
				}
				
				final ClassInputField propField = new ClassInputField(propName, "", inputType, method);
				propField.setOptional(true);
//				putField(propField);
				retVal.add(propField);
			}
		}
		return retVal;
	}
	
	private Class<?> wrapperClassForPrimitive(Class<?> primitive) {
		Class<?> retVal = null;
		
		if(primitive == boolean.class) {
			retVal = Boolean.class;
		} else if(primitive == char.class) {
			retVal = Character.class;
		} else if(primitive == byte.class) {
			retVal = Byte.class;
		} else if(primitive  == short.class) {
			retVal = Short.class;
		} else if(primitive == int.class) {
			retVal = Integer.class;
		} else if(primitive == long.class) {
			retVal = Long.class;
		} else if(primitive == float.class) {
			retVal = Float.class;
		} else if(primitive == double.class) {
			retVal = Double.class;
		}
		
		return retVal;
	}
	
}
