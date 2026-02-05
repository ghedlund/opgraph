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
package ca.phon.opgraph.io;

import java.util.*;
import java.util.logging.*;
import java.util.stream.*;

/**
 * A factory for discovering and constructing {@link OpGraphSerializer}s.
 */
public abstract class OpGraphSerializerFactory {
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(OpGraphSerializerFactory.class.getName());

	/** System property for defining the default serializer */
	public static final String DEFAULT_SERIALIZER_PROPERTY = "ca.phon.defaultSerializer";

	/**
	 * Gets a default serializer.
	 * 
	 * @return the serializer that is registered to be the default, or 
	 *         <code>null</code> if no such serializer exists
	 */
	public static OpGraphSerializer getDefaultSerializer() {
		OpGraphSerializer serializer = null;

		// See if there's a default one defined via a system property first...
		final String defaultClass = System.getProperty(DEFAULT_SERIALIZER_PROPERTY);
		if(defaultClass != null) {
			try {
				Class<? extends OpGraphSerializer> serializerCls =
					Class.forName(defaultClass).asSubclass(OpGraphSerializer.class);
				serializer = serializerCls.getDeclaredConstructor().newInstance();
			} catch(ClassNotFoundException exc) {
				LOGGER.severe("Service '" + defaultClass + "' does not provide an empty constructor!");
			} catch(ReflectiveOperationException exc) {
				LOGGER.severe("Service '" + defaultClass + "' does not provide an accessible empty constructor!");
			}
		}

		// ...No? Try to discover one, and take the first
		if(serializer == null) {
			final Iterator<OpGraphSerializer> it = ServiceLoader.load(OpGraphSerializer.class).iterator();
			if(it.hasNext())
				serializer = it.next();
		}

		return serializer;
	}

	/**
	 * Gets a serializer by the file extension it understands.
	 * 
	 * @param extension  the extension
	 * 
	 * @return the serializer that reads and write files with the given
	 *         extension, or <code>null</code> if no such serializer exists
	 */
	public static OpGraphSerializer getSerializerByExtension(String extension) {
		for(OpGraphSerializer serializer : ServiceLoader.load(OpGraphSerializer.class)) {
			final OpGraphSerializerInfo info = serializer.getClass().getAnnotation(OpGraphSerializerInfo.class);
			if(info != null && info.extension().equalsIgnoreCase(extension))
				return serializer;
		}
		return null;
	}

	/**
	 * Gets all registered serializers.
	 * 
	 * @return list of serializers
	 */
	public static List<Class<? extends OpGraphSerializer>> getSerializers() {
		return ServiceLoader.load(OpGraphSerializer.class).stream()
			.map(ServiceLoader.Provider::type)
			.collect(Collectors.toList());
	}
}
