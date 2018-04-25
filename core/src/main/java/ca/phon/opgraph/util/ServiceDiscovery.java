/*
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * This file is part of the OpGraph project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.phon.opgraph.util;

import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

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
