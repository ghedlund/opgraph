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
package ca.phon.opgraph.library.handlers;

import java.io.*;
import java.net.*;

/**
 * An interface for classes that want to load information from a given URI.
 * 
 * @param <T>  the type of information that this handler loads
 * 
 * @see URI
 */
public interface URIHandler<T> {
	/**
	 * Checks whether or not this handler can load data from a given uri.
	 * 
	 * @param uri  the uri to check
	 * 
	 * @return <code>true</code> if this handler can handle the given uri,
	 *         <code>false</code> otherwise
	 */
	public abstract boolean handlesURI(URI uri);

	/**
	 * Loads node information from a given {@link URI}.
	 * 
	 * @param uri  the {@link URI} to load NodeData from
	 * 
	 * @return the node info
	 * 
	 * @throws IllegalArgumentException  if the scheme of the specified uri is unsupported
	 * @throws IOException  if there were any errors loading data from the specified uri
	 */
	public abstract T load(URI uri) throws IOException; 
}
