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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import ca.phon.opgraph.library.NodeData;

/**
 * A {@link URIHandler} that loads node information from a file. Handles
 * URIs of the form:
 * <ul>
 *   <li><code>file:&lt;path&gt;</code>, for loading all macros from a file</li>
 *   <li><code>file:&lt;path&gt;#&lt;macro_id&gt</code>, for loading a specific macro</li>
 * </ul>
 */
public class FileHandler implements URIHandler<List<NodeData>> {
	//
	// URIHandler<List<NodeData>>
	//

	@Override
	public boolean handlesURI(URI uri) {
		return (uri != null && "file".equals(uri.getScheme()));
	}

	@Override
	public List<NodeData> load(URI uri) throws IOException {
		// Make sure we can handle URI
		if(!handlesURI(uri))
			throw new IllegalArgumentException("Cannot handle uri '" + uri + "'");

		// Make sure file exists
		final File source = new File(uri.getPath());
		if(!source.exists())
			throw new IOException("File '" + source.getPath() + "' does not exist");

		// If no fragment, load all macros, otherwise load specific macro
		final ArrayList<NodeData> ret = new ArrayList<NodeData>();

		// FIXME since maven
//		final InputStream stream = new FileInputStream(source);
//		if(uri.getFragment() == null) {
//			final XMLGraphIO io = new XMLGraphIO();
//			for(NodeData info : io.loadMacros(stream)) {
//				final URI nodeURI = URI.create(uri.toString() + "#" + info.uri.getFragment());
//				ret.add(new NodeData(nodeURI, info.name, info.description, info.category, info.instantiator));
//			}
//		} else {
//			final XMLGraphIO io = new XMLGraphIO();
//			final NodeData info = io.loadMacro(stream, uri.getFragment());
//			ret.add(new NodeData(uri, info.name, info.description, info.category, info.instantiator));
//		}

		return ret;
	}
}
