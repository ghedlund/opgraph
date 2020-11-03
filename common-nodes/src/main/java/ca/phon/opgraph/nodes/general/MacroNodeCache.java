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
package ca.phon.opgraph.nodes.general;

import java.io.*;
import java.lang.ref.*;
import java.net.*;
import java.util.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.io.*;

public class MacroNodeCache {
	
	private final Map<URI, WeakReference<OpGraph>> graphMap = new HashMap<URI, WeakReference<OpGraph>>();
	
	public MacroNodeCache() {
		super();
	}
	
	/**
	 * Get graph from cache or load it.
	 * 
	 * @param graphURL
	 * @return
	 * @throws IOException
	 */
	public OpGraph getGraph(URI graphURI) throws IOException {
		WeakReference<OpGraph> graphRef = graphMap.get(graphURI);
		if(graphRef != null) {
			if(graphRef.get() != null)
				return graphRef.get();
			else
				graphMap.remove(graphURI);
		}
		
		URL graphURL = uriToUrl(graphURI);
		
		if(graphURL != null) {
			final OpGraphSerializer serializer = OpGraphSerializerFactory.getDefaultSerializer();
			OpGraph graph = serializer.read(graphURL.openStream());
			
			graphMap.put(graphURI, new WeakReference<OpGraph>(graph));
			
			return graph;
		} else {
			throw new IOException("Unable to location graph at " + graphURI.toASCIIString());
		}
	}
	
	private URL uriToUrl(URI uri) {
		switch(uri.getScheme()) {
		case "classpath":
			return ClassLoader.getSystemResource(uri.getSchemeSpecificPart());
			
		default:
			try {
				return uri.toURL();
			} catch (MalformedURLException e) {
				// log error
			}
		}
		return null;
	}
	
}
