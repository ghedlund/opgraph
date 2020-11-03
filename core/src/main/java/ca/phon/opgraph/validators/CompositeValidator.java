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
 * A validator which accepts any object in which at least one of its composed
 * validators accepts.
 */
public class CompositeValidator implements TypeValidator {
	/** The list of validators this validator uses */
	private TypeValidator[] validators;

	/**
	 * Constructs a composite validator from the given validators. Note
	 * that a <code>null</code> validator accepts all objects, so if such
	 * a validator is contained in <code>validators</code> then this
	 * validator will always return <code>true</code> from {@link #isAcceptable(Object)}.
	 * Also, if no validators are given, this validator will always return
	 * <code>false</code> from {@link #isAcceptable(Object)}. 
	 * 
	 * @param validators  {@link TypeValidator}s to use
	 */
	public CompositeValidator(TypeValidator... validators) {
		this.validators = validators;
	}

	//
	// TypeValidator
	//

	@Override
	public boolean isAcceptable(Object obj) {
		boolean ret = false;
		for(TypeValidator validator : validators) {
			if(validator == null || validator.isAcceptable(obj)) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	@Override
	public boolean isAcceptable(Class<?> cls) {
		if(cls == null)
			throw new NullPointerException("cls cannot be null");

		boolean ret = false;
		for(TypeValidator validator : validators) {
			if(validator == null || validator.isAcceptable(cls)) {
				ret = true;
				break;
			}
		}
		return ret;
	}
}
