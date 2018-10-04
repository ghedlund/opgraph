/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
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
package ca.phon.opgraph.extensions;

import java.util.Collection;

/**
 * An interface for any class which would like to provide extensions to
 * their functionality, even if the class is defined as <code>final</code>. 
 */
public interface Extendable {
	/**
	 * Gets the extension of a given type.
	 * 
	 * @param type  the type of extension to get
	 * 
	 * @return an instance of that extension type, or <code>null</code> if
	 *         the specified extension type is not supported by the class.
	 *         
	 * @throws NullPointerException  if <code>type</code> is <code>null</code>
	 */
	public abstract <T> T getExtension(Class<T> type);

	/**
	 * Gets an iterable copy of the extensions.
	 * 
	 * @return an iterable copy of the extensions
	 */
	public abstract Collection<Class<?>> getExtensionClasses();

	/**
	 * Adds or removes an extension of a specified type to an extendable.
	 * 
	 * @param type  the type of extension to add
	 * @param extension  the extension instance to add, or <code>null</code>
	 *                   if the extension of the specified type should be
	 *                   removed from this extendable
	 *                   
	 * @return the old extension instance of the specified type, or
	 *         <code>null</code> if one did not previously exist
	 *         
	 * @throws NullPointerException  if <code>type</code> is <code>null</code>
	 */
	public abstract <T> T putExtension(Class<T> type, T extension);
}
