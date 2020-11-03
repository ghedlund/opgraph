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
package ca.phon.opgraph.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReflectUtil {

	public static String parametersAsString(Method method) {
		return parametersAsString(method, false);
	}

	/**
	 * Get constructor signature.
	 *
	 * @param constructor
	 * @param longTypeNames
	 * @return
	 */
	public static String getSignature(Constructor<?> constructor, boolean longTypeNames) {
		return "<init>(" + parametersAsString(constructor, longTypeNames) + ")";
	}

	/**
	 * Get method signature
	 *
	 * @param method
	 * @param longTypeNames
	 * @return
	 */
	public static String getSignature(Method method, boolean longTypeNames) {
		return method.getName() + "("
				+ parametersAsString(method, longTypeNames) + ")";
	}

	/**
	 *
	 * @param method
	 * @param longTypeNames
	 * @return
	 */
	public static String parametersAsString(Method method, boolean longTypeNames) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length == 0)
			return "";
		StringBuilder paramString = new StringBuilder();
		paramString.append(longTypeNames ? parameterTypes[0].getName()
				: parameterTypes[0].getSimpleName());
		for (int i = 1; i < parameterTypes.length; i++) {
			paramString.append(",").append(
					longTypeNames ? parameterTypes[i].getName()
							: parameterTypes[i].getSimpleName());
		}
		return paramString.toString();
	}

	/**
	 *
	 * @param method
	 * @param longTypeNames
	 * @return
	 */
	public static String parametersAsString(Constructor method, boolean longTypeNames) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length == 0)
			return "";
		StringBuilder paramString = new StringBuilder();
		paramString.append(longTypeNames ? parameterTypes[0].getName()
				: parameterTypes[0].getSimpleName());
		for (int i = 1; i < parameterTypes.length; i++) {
			paramString.append(",").append(
					longTypeNames ? parameterTypes[i].getName()
							: parameterTypes[i].getSimpleName());
		}
		return paramString.toString();
	}

	/**
	 * Get paramters from signature
	 *
	 * @param sig
	 * @return
	 * @throws ClassNotFoundException
	 *
	 */
	public static Class<?>[] getParametersFromSignature(String sig) throws ClassNotFoundException {
		Class<?> retVal[] = new Class<?>[0];
		final String paramString =
				sig.substring(sig.indexOf('(')+1, sig.lastIndexOf(')')).trim();
		if(paramString.length() > 0) {
			final String[] paramClassNames = paramString.split(",");
			retVal = new Class<?>[paramClassNames.length];
			for(int i = 0; i < paramClassNames.length; i++) {
				final String paramClassName = paramClassNames[i];

				if(paramClassName.equals("int")) {
					retVal[i] = int.class;
				} else if(paramClassName.equals("double")) {
					retVal[i] = double.class;
				} else if(paramClassName.equals("float")) {
					retVal[i] = float.class;
				} else {
					retVal[i] = Class.forName(paramClassName.trim());
				}
			}
		}
		return retVal;
	}

	/**
	 * Get method name from signature
	 *
	 * @param sig
	 * @return
	 */
	public static String getMethodNameFromSignature(String sig) {
		return sig.substring(0, sig.indexOf('('));
	}

	/**
	 * Get method from signature
	 *
	 * @param clazz
	 * @param methodSig
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static Method getMethodFromSignature(Class<?> clazz, String methodSig) throws ClassNotFoundException, SecurityException, NoSuchMethodException {
		Method retVal = null;

		final String methodName = getMethodNameFromSignature(methodSig);
		final Class<?>[] paramTypes = getParametersFromSignature(methodSig);

		retVal = clazz.getMethod(methodName, paramTypes);

		return retVal;
	}

	/**
	 * Get constructor from signature
	 *
	 * @param clazz
	 * @param cstrSig
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static Constructor<?> getConstructorFromSignature(Class<?> clazz, String cstrSig) throws ClassNotFoundException, SecurityException, NoSuchMethodException {
		Constructor<?> retVal = null;

		final String methodName = getMethodNameFromSignature(cstrSig);
		if(!methodName.equals("<init>")) {
			throw new IllegalArgumentException("Not a constructor signature");
		}

		final Class<?>[] paramTypes = getParametersFromSignature(cstrSig);

		retVal = clazz.getConstructor(paramTypes);

		return retVal;
	}

	public static String getSignature(Constructor<?> cstr) {
		return getSignature(cstr, false);
	}
	
	public static String getSignature(Method method) {
		return getSignature(method, false);
	}

	/**
	 * Get all static methods for the given class
	 *
	 * @param clazz
	 * @return list of all static method members
	 */
	public static List<Method> getStaticMethods(Class<?> clazz) {
		List<Method> methods = new ArrayList<Method>();
	    for (Method method : clazz.getMethods()) {
	        if (Modifier.isStatic(method.getModifiers())) {
	            methods.add(method);
	        }
	    }
	    return Collections.unmodifiableList(methods);
	}

	/**
	 * Get all non-static methods for the given class
	 *
	 * @param clazz
	 * @return list of all non-static method members
	 */
	public static List<Method> getInstanceMethods(Class<?> clazz) {
		List<Method> methods = new ArrayList<Method>();
		for(Method method:clazz.getMethods()) {
			if(!Modifier.isStatic(method.getModifiers())) {
				methods.add(method);
			}
		}
		return Collections.unmodifiableList(methods);
	}

	/**
	 * Get all static fields for the given class
	 *
	 * @param clazz
	 * @return list of static field members
	 */
	public static List<Field> getStaticFields(Class<?> clazz) {
		List<Field> retVal = new ArrayList<Field>();
		for(Field field:clazz.getFields()) {
			if(Modifier.isStatic(field.getModifiers())) {
				retVal.add(field);
			}
		}
		return Collections.unmodifiableList(retVal);
	}

	/**
	 * Get all non-static fields for the given class.
	 *
	 * @param clazz
	 * @return list of non-static field members
	 */
	public static List<Field> getInstanceFields(Class<?> clazz) {
		List<Field> retVal = new ArrayList<Field>();
		for(Field field:clazz.getFields()) {
			if(!Modifier.isStatic(field.getModifiers())) {
				retVal.add(field);
			}
		}
		return Collections.unmodifiableList(retVal);
	}

	/**
	 * Look for any {@link ParameterizedType} classes/interfaces
	 * directly extended by the given class.
	 *
	 * @param clazz
	 * @return list of all parameterized types
	 */
	public static List<ParameterizedType> getParameterizedTypesForClass(Class<?> clazz) {
		final List<ParameterizedType> retVal = new ArrayList<ParameterizedType>();

		final Type genericSuperclass = clazz.getGenericSuperclass();
		if(genericSuperclass != null && genericSuperclass instanceof ParameterizedType) {
			retVal.add((ParameterizedType)genericSuperclass);
		}

		for(Type genericInterface:clazz.getGenericInterfaces()) {
			if(genericInterface instanceof ParameterizedType) {
				retVal.add((ParameterizedType)genericInterface);
			}
		}

		return retVal;
	}

	/**
	 * Return the wrapper type for the given primitive type.
	 *
	 * @param type
	 * @return wrapper type or the given type if it is not
	 *  a primitive type
	 */
	public static Class<?> wrapperClassForPrimitive(Class<?> primitive) {
		Class<?> retVal = primitive;

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
