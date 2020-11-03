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
package ca.phon.opgraph.validators;

/**
 * An interface for classes who want to provide validation on objects.
 */
public interface TypeValidator {
	/**
	 * Gets whether or not this validator accepts a given object.
	 *  
	 * @param obj  the object to check
	 * 
	 * @return  <code>true</code> if this validator accepts the given object,
	 *          <code>false</code> otherwise
	 */
	public abstract boolean isAcceptable(Object obj);

	/**
	 * Gets whether or not this validator can potentially accept instances
	 * of a given class.
	 *  
	 * @param cls  the class to check
	 * 
	 * @return  <code>true</code> if this validator <em>map</em> accept the
	 *          given class, <code>false</code> otherwise
	 *          
	 * @throws NullPointerException  if the specified class is <code>null</code>
	 */
	public abstract boolean isAcceptable(Class<?> cls);
}
