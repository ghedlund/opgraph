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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A validator that uses {@link Class} instances for validation.
 */
public class ClassValidator implements TypeValidator {
	/** The accepted classes */
	private List<Class<?>> classes;

	/**
	 * Constructs a validator that accepts the given classes.
	 * 
	 * @param classes  the classes which will be accepted by this validator
	 */
	public ClassValidator(Class<?>... classes) {
		this.classes = new ArrayList<Class<?>>();
		for(Class<?> clz : classes) {
			if(clz != null)
				this.classes.add(clz);
		}
	}

	/**
	 * Gets the list of classes which this validator accepts.
	 * 
	 * @return  the list of accepted classes (immutable)
	 */
	public List<Class<?>> getClasses() {
		return Collections.unmodifiableList(classes);
	}

	//
	// TypeValidator
	//

	@Override
	public boolean isAcceptable(Object obj) {
		if(obj == null) return true;
		return isAcceptable(obj.getClass());
	}

	@Override
	public boolean isAcceptable(Class<?> cls) {
		if(cls == null)
			throw new NullPointerException("cls cannot be null");

		boolean ret = false;
		for(Class<?> acceptedClass : classes) {
			if(acceptedClass.isAssignableFrom(cls)) {
				ret = true;
				break;
			}
		}

		return ret;
	}
}
