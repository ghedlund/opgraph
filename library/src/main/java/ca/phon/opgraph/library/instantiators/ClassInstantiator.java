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
package ca.phon.opgraph.library.instantiators;

/**
 * An instantiator that creates instances from a given class.
 * 
 * @param <T>  the type of class
 */
public class ClassInstantiator<T> implements Instantiator<T> {
	/** The class to use for instantiation */
	private Class<? extends T> clz;

	/**
	 * Constructs an instantiator that uses a specified class to create
	 * new instances.
	 * 
	 * @param clz  the class to use for instantiation.
	 */
	public ClassInstantiator(Class<? extends T> clz) {
		this.clz = clz;
	}

	@Override
	public T newInstance(Object... params) throws InstantiationException {
		try {
			return clz.newInstance();
		} catch(InstantiationException exc) {
			throw exc;
		} catch(IllegalAccessException exc) {
			throw new InstantiationException(exc.getLocalizedMessage());
		}
	}
}
