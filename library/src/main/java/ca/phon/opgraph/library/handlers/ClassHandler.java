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
package ca.phon.opgraph.library.handlers;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OpNodeInfo;
import ca.phon.opgraph.library.NodeData;
import ca.phon.opgraph.library.instantiators.ClassInstantiator;

/**
 * A {@link URIHandler} that loads node information from a given class. Handles
 * URIs of the form <code>class:&lt;classpath&gt;</code>
 */
public class ClassHandler implements URIHandler<List<NodeData>> {
	@Override
	public boolean handlesURI(URI uri) {
		return (uri != null && "class".equals(uri.getScheme()));
	}

	@Override
	public List<NodeData> load(URI uri) throws IOException {
		// Make sure we can handle URI
		if(!handlesURI(uri))
			throw new IllegalArgumentException("Cannot handle uri '" + uri + "'");

		// Load class
		ArrayList<NodeData> ret = new ArrayList<NodeData>();
		try {
			final String className = uri.getSchemeSpecificPart();
			final Class<?> clz = Class.forName(className, false, getClass().getClassLoader());
			final Class<? extends OpNode> ovClz = clz.asSubclass(OpNode.class);

			// If a node info annotation is present then we don't need to instantiate the class
			final OpNodeInfo info = ovClz.getAnnotation(OpNodeInfo.class);
			if(info != null) {
				ret.add(new NodeData(uri,
				                     info.name(),
				                     info.description(),
				                     info.category(),
				                     new ClassInstantiator<OpNode>(ovClz)));
			} else {
				// XXX should we create a new instance or require annotation?
				final OpNode node = ovClz.newInstance();
				ret.add(new NodeData(uri,
				                     node.getName(),
				                     node.getDescription(),
				                     node.getCategory(),
				                     new ClassInstantiator<OpNode>(ovClz)));
			}
		} catch(ClassCastException exc) {
			throw new IOException("Given class '" + uri.getPath() + "' does not extend OpNode", exc);
		} catch(ClassNotFoundException exc) {
			throw new IOException("Unknown class: " + uri.getPath(), exc);
		} catch(InstantiationException exc) {
			throw new IOException("Class could not be instantiated: " + uri.getPath(), exc);
		} catch(IllegalAccessException exc) {
			throw new IOException("Class could not be instantiated: " + uri.getPath(), exc);
		}

		return ret;
	}
}
