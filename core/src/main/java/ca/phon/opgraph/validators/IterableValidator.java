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
package ca.phon.opgraph.validators;

/**
 * A validator that will check if an {@link Iterable} object contains objects
 * that adhere to a given {@link TypeValidator}.
 */
public class IterableValidator implements TypeValidator {
	/** The validator used for iterated elements */
	private TypeValidator elementValidator;

	/**
	 * Constructs this validator with an element validator which accepts
	 * the given list of classes.
	 * 
	 * @param acceptedTypes  the classes to accept for iterated elements
	 * 
	 * @see #IterableValidator(TypeValidator)
	 */
	public IterableValidator(Class<?>... acceptedTypes) {
		this(new ClassValidator(acceptedTypes));
	}

	/**
	 * Constructs this validator with the given validator to use against
	 * iterated elements.
	 * 
	 * @param elementValidator  the {@link TypeValidator} to use on iterated elements
	 */
	public IterableValidator(TypeValidator elementValidator) {
		this.elementValidator = elementValidator;
	}

	//
	// TypeValidator
	//

	@Override
	public boolean isAcceptable(Object obj) {
		boolean ret = true;
		if(obj instanceof Iterable) {
			for(Object o : (Iterable<?>)obj) {
				if(!elementValidator.isAcceptable(o)) {
					ret = false;
					break;
				}
			}
		}
		return ret;
	}

	@Override
	public boolean isAcceptable(Class<?> cls) {
		if(cls == null)
			throw new NullPointerException("cls cannot be null");
		return Iterable.class.isAssignableFrom(cls);
	}
}
