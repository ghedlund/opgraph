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
package ca.phon.opgraph.app.components;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextField;

/**
 * Text field for create objects of the declared type
 * using the static <code>parseMyClassName</code> method.
 * 
 */
public class ParseableTypeField extends JTextField {

	private static final long serialVersionUID = 2222081041114430858L;

	private static final Logger LOGGER = Logger
			.getLogger(ParseableTypeField.class.getName());
	
	private Class<?> declaredClass;
	
	private Method parseMethod;
	
	/**
	 * Method to determine if the given type has the required methods.
	 * 
	 * @param clazz
	 */
	public static boolean isParseableType(Class<?> clazz) {
		boolean retVal = false;
		final String simpleName = clazz.getSimpleName();
		final String parseMethod = "parse" + simpleName;
		try {
			final Method method = clazz.getMethod(parseMethod, String.class);
			retVal = method != null && Modifier.isStatic(method.getModifiers());
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		return retVal;
	}
	
	public ParseableTypeField(Class<?> clazz) {
		super();
		setDeclaredClass(clazz);
	}
	
	public void setDeclaredClass(Class<?> clazz) {
		final String simpleName = clazz.getSimpleName();
		final String parseMethod = "parse" + simpleName;
		
		try {
			final Method method = clazz.getMethod(parseMethod, String.class);
			if(method != null && !Modifier.isStatic(method.getModifiers())) {
				throw new NoSuchMethodException(parseMethod + " is not static in type " + clazz.getName());
			}
			this.declaredClass = clazz;
			this.parseMethod = method;
		} catch (SecurityException e) {
			throw new IllegalArgumentException(e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public Class<?> getDeclaredClass() {
		return this.declaredClass;
	}
	
	public Method getParseMethod() {
		return this.parseMethod;
	}
	
	public Object getObject() {
		final String text = getText();
		final Method parseMethod = getParseMethod();
		
		try {
			final Object val = parseMethod.invoke(null, text);
			final Class<?> type = getDeclaredClass();
			return type.cast(val);
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (IllegalAccessException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (InvocationTargetException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		
		return null;
	}
	
}
