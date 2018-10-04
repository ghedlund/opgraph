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
package ca.phon.opgraph.nodes.general;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.io.OpGraphSerializer;
import ca.phon.opgraph.io.OpGraphSerializerFactory;

public class LinkedMacroNode extends MacroNode {
	
	private static final Logger LOGGER = Logger.getLogger(LinkedMacroNode.class.getName());
	
	private static final Map<URI, OpGraph> graphMap = new HashMap<>();

	/*
	 * Load graph at given uri or the in-memory graph if it has already been loaded.
	 * The last modifed time is checked and the graph is reloaded if necessary.
	 */
	private static OpGraph loadGraph(URI uri) throws IOException {
		OpGraph graph = null;
		boolean loadGraph = true;
		if(graphMap.containsKey(uri)) {
			graph = graphMap.get(uri);
			// TODO check timestamp
			loadGraph = false;
		}
		
		if(loadGraph) {
			final OpGraphSerializer serializer = OpGraphSerializerFactory.getDefaultSerializer();
			graph = serializer.read(uri.toURL().openStream());
			
			// check to make sure the graph has a single MacroNode
			if(graph.getVertices().size() != 1) {
				throw new IOException("Graph has incorrect number of nodes");
			}
			
			if(!(graph.getVertices().get(0) instanceof MacroNode)) {
				throw new IOException("Graph must consist of a single MacroNode");
			}
			
			graphMap.put(uri, graph);
		}
		return graph;
	}
	
	private URI uri;
	
	public LinkedMacroNode() {
		super();
	}
	
	public LinkedMacroNode(URI uri) {
		super();
		
		setUri(uri);
	}
	
	public void setUri(URI uri) {
		try {
			final OpGraph originalGraph = LinkedMacroNode.loadGraph(uri);
			final MacroNode node = (MacroNode)originalGraph.getVertices().get(0);
			
			final OpGraph graph = new OpGraph();
			graph.add(node);
			
			// publish inputs/outputs from macro
			for(PublishedInput pi:node.getPublishedInputs()) {
				publish(pi.getKey(), node, pi);
			}
			
			for(PublishedOutput po:node.getPublishedOutputs()) {
				publish(po.getKey(), node, po);
			}
			setGraph(graph);
			
			this.uri = uri;
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}		
	}
	
	public URI getUri() {
		return this.uri;
	}
	
}
