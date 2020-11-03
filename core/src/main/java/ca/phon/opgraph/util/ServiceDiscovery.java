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
package ca.phon.opgraph.util;

import java.net.*;
import java.util.*;
import java.util.logging.*;

/**
 * A class that provides service discovery facilities. By default, the
 * provider is {@link DefaultServiceDiscovery}, which provides discovery
 * facilities similar to that of {@link java.util.ServiceLoader}, but the
 * service discovery provider can be set by the system property
 * <code>ca.phon.opgraph.discoveryProvider</code>.
 */
public abstract class ServiceDiscovery {
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(ServiceDiscovery.class.getName());

	/** The provider instance */
	private static ServiceDiscovery provider;

	/**
	 * Gets an instance of a service discovery provider.
	 *
	 * @return service discovery instance
	 */
	public synchronized static final ServiceDiscovery getInstance() {
		if(provider == null) {
			final String discoveryProviderName = System.getProperty("ca.phon.opgraph.discoveryProvider");
			if(discoveryProviderName != null) {
				try {
					Class<?> cls = Class.forName(discoveryProviderName);
					provider = cls.asSubclass(ServiceDiscovery.class).newInstance();
				} catch(ClassCastException exc) {
					LOGGER.warning("Class '" + discoveryProviderName + "' is not a service discovery provider. Using default provider.");
				} catch(ClassNotFoundException exc) {
					LOGGER.warning("Could not find service discovery class '" + discoveryProviderName + "'. Using default provider.");
				} catch(InstantiationException exc) {
					LOGGER.warning("Could not instantiate service discovery class '" + discoveryProviderName + "'. Using default provider.");
				} catch(IllegalAccessException exc) {
					LOGGER.warning("Could not instantiate service discovery class '" + discoveryProviderName + "'. Using default provider.");
				}
			}

			// If still null, use the default provider
			if(provider == null)
				provider = new DefaultServiceDiscovery();
		}

		return provider;
	}

	/**
	 * Gets all providers of the requested interface.
	 *
	 * @param cls  the service interface being requested
	 *
	 * @return a list of discovered providers
	 */
	public abstract <T> List<Class<? extends T>> findProviders(Class<T> cls);

	/**
	 * Gets a list of URLs to all resources with the given name.
	 *
	 * @param name  the resource name
	 *
	 * @return a list of valid URLs pointing to resources with the given name
	 */
	public abstract List<URL> findResources(String name);
}
