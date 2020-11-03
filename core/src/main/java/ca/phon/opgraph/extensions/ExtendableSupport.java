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
/**
 * 
 */
package ca.phon.opgraph.extensions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Support class for the {@link Extendable} interface.
 */
public class ExtendableSupport implements Extendable {
	/** The mapping of extension type to extension */
	private HashMap<Class<?>, Object> extensions;

	/** The parent class that this class supports */
	@SuppressWarnings("unused")
	private Class<?> parentClass;

	/**
	 * Constructs a support object that targets a given parent class.
	 * 
	 * @param parentClass  the class that is making use of this one
	 */
	public ExtendableSupport(Class<?> parentClass) {
		this.extensions = new LinkedHashMap<Class<?>, Object>();
		this.parentClass = parentClass;
	}

	//
	// Extendable
	//

	@Override
	public <T> T getExtension(Class<T> type) {
		if(type == null)
			throw new NullPointerException("Extension type cannot be null");

		return type.cast(extensions.get(type));
	}

	@Override
	public Collection<Class<?>> getExtensionClasses() {
		return Collections.unmodifiableCollection(extensions.keySet());
	}

	@Override
	public <T> T putExtension(Class<T> type, T extension) {
		if(type == null)
			throw new NullPointerException("Extension type cannot be null");
		return type.cast(extension == null ? extensions.remove(type) : extensions.put(type, extension));
	}
}
