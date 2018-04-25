/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
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
