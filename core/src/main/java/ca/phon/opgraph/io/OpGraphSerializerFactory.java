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
/**
 * 
 */
package ca.phon.opgraph.io;

import java.util.List;
import java.util.logging.Logger;

import ca.phon.opgraph.util.ServiceDiscovery;

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
			Class<? extends OpGraphSerializer> serializerCls = null;

			try {
				serializerCls = Class.forName(defaultClass).asSubclass(OpGraphSerializer.class); 
				serializer = serializerCls.newInstance();
			} catch(ClassNotFoundException exc) {
				LOGGER.severe("Service '" + defaultClass + "' does not provide an empty constructor!");
			} catch(InstantiationException exc) {
				LOGGER.severe("Service '" + defaultClass + "' does not provide an empty constructor!");
			} catch(IllegalAccessException exc) {
				LOGGER.severe("Service '" + defaultClass + "' does not provide an accessible empty constructor!");
			}
		}

		// ...No? Try to discover one, and take the first
		if(serializer == null) {
			for(Class<? extends OpGraphSerializer> serializerCls : getSerializers()) {
				try {
					serializer = serializerCls.newInstance();
				} catch(InstantiationException exc) {
					LOGGER.severe("Service '" + serializerCls.getName() + "' does not provide an empty constructor!");
				} catch(IllegalAccessException exc) {
					LOGGER.severe("Service '" + serializerCls.getName() + "' does not provide an accessible empty constructor!");
				}
			}
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
		OpGraphSerializer serializer = null;
		for(Class<? extends OpGraphSerializer> serializerCls : getSerializers()) {
			final OpGraphSerializerInfo info = serializerCls.getAnnotation(OpGraphSerializerInfo.class);
			if(info != null && info.extension().equalsIgnoreCase(extension)) {
				try {
					serializer = serializerCls.newInstance();
				} catch(InstantiationException exc) {
					LOGGER.severe("Service '" + serializerCls.getName() + "' does not provide an empty constructor!");
				} catch(IllegalAccessException exc) {
					LOGGER.severe("Service '" + serializerCls.getName() + "' does not provide an accessible empty constructor!");
				}
				break;
			}
		}

		return serializer;
	}

	/**
	 * Gets all registered serializers.
	 * 
	 * @return list of serializers
	 */
	public static List<Class<? extends OpGraphSerializer>> getSerializers() {
		return ServiceDiscovery.getInstance().findProviders(OpGraphSerializer.class);
	}
}
