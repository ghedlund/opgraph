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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.phon.opgraph.util.ReflectUtil;

/**
 * Class used to add input/output fields based on
 * class definitions.
 */
public class ObjectNodeFieldGenerator {

	private final List<ObjectNodePropertyInputField> inputFields = new ArrayList<ObjectNodePropertyInputField>();

	private final List<ObjectNodePropertyOutputField> outputFields = new ArrayList<ObjectNodePropertyOutputField>();

	public ObjectNodeFieldGenerator() {
		super();
	}

	public List<ObjectNodePropertyInputField> getInputFields() {
		return inputFields;
	}

	public List<ObjectNodePropertyOutputField> getOutputFields() {
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
	private List<ObjectNodePropertyOutputField> scanClassGetters(Class<?> clazz) {
		final List<ObjectNodePropertyOutputField> retVal = new ArrayList<ObjectNodePropertyOutputField>();
		final Method[] methods = clazz.getMethods();

		for(Method method:methods) {
			String propName = null;
			Class<?> outputType = null;
			if(method.getName().startsWith("get")
					&& method.getParameterTypes().length == 0) {
				propName = method.getName().substring(3);
				propName = propName.replace(propName.charAt(0), Character.toLowerCase(propName.charAt(0)));

				if(propName.equals("enabled")) {
					continue;
				}

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
				outputType = ReflectUtil.wrapperClassForPrimitive(outputType);
			}

			final ObjectNodePropertyOutputField outputField = new ObjectNodePropertyOutputField(propName, "", outputType, method);
			outputField.setFixed(true);
//			putField(outputField);
			retVal.add(outputField);
		}
		return retVal;
	}

	private List<ObjectNodePropertyInputField> scanClassSetters(Class<?> clazz) {
		final List<ObjectNodePropertyInputField> retVal = new ArrayList<ObjectNodePropertyInputField>();
		final Method[] methods = clazz.getMethods();
		for(Method method:methods) {
			if(method.getName().startsWith("set")
					&& method.getParameterTypes().length == 1) {
				String propName = method.getName().substring(3);
				propName = propName.replace(propName.charAt(0), Character.toLowerCase(propName.charAt(0)));

				if(propName.equals("enabled")) continue;

				Class<?> inputType = method.getParameterTypes()[0];
				if(inputType.isPrimitive()) {
					inputType = ReflectUtil.wrapperClassForPrimitive(inputType);
				}

				final ObjectNodePropertyInputField propField = new ObjectNodePropertyInputField(propName, "", inputType, method);
				propField.setOptional(true);
//				putField(propField);
				retVal.add(propField);
			}
		}
		return retVal;
	}

}
