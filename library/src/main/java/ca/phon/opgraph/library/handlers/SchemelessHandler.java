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
import java.util.HashMap;
import java.util.List;

import ca.phon.opgraph.library.NodeData;

/**
 * A handler that handles URIs with everything <code>null</code> except for
 * the fragment part. This is achieved by storing a fixed set of node info
 * instances.
 * 
 * @see URI
 */
public class SchemelessHandler implements URIHandler<List<NodeData>> {
	/** The fixed set of items */
	private HashMap<String, NodeData> items = new HashMap<String, NodeData>();

	/**
	 * Associate the fragment component of a given info's URI with the info.
	 * 
	 * @param info  the info
	 * 
	 * @throws NullPointerException  if the given info instance is <code>null</code>
	 * @throws NullPointerException  if the given info's uri has a <code>null</code> fragment
	 */
	public void put(NodeData info) {
		if(info == null)
			throw new NullPointerException("Given info cannot be null");

		if(info.uri == null || info.uri.getFragment() == null)
			throw new NullPointerException("Given uri has a null fragment component");

		items.put(info.uri.getFragment(), info);
	}

	//
	// URIHandler<List<NodeData>>
	//

	@Override
	public boolean handlesURI(URI uri) {
		if(uri != null && (uri.getScheme() == null || uri.getScheme().trim().length() == 0))
			return items.containsKey(uri.getFragment());
		return false;
	}

	@Override
	public List<NodeData> load(URI uri) throws IOException {
		// Make sure we can handle URI
		if(!handlesURI(uri))
			throw new IllegalArgumentException("Cannot handle uri '" + uri + "'");

		ArrayList<NodeData> ret = new ArrayList<NodeData>();
		ret.add(items.get(uri.getFragment()));
		return ret;
	}
}
