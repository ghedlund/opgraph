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
package ca.phon.opgraph.dag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

/**
 * Topologically orders the vertices in a DAG. A topological ordering
 * is an ordering of a DAG's vertices such that for any edge
 * <tt>{u, v}</tt>, the vertex <tt>u</tt> comes before the vertex
 * <tt>v</tt> in the ordering.
 *  
 * @see <a href="http://en.wikipedia.org/wiki/Topological_sorting">Wikipedia Article</a>
 */
public class TopologicalSort<V extends Vertex, E extends DirectedEdge<V>> {
	
	/**
	 * List of ordered vertices
	 */
	private List<V> orderedVertices;
	
	/**
	 * Map of vertex levels
	 */
	private Map<V, Integer> vertexLevels;
	
	/**
	 * Vertex comparator used for nodes within the same level
	 */
	private final Comparator<V> defaultVertexComparator = (v1, v2) -> {
		final String name1 = v1.toString();
		final String name2 = v2.toString();
		return name1.compareTo(name2);
	};
	private Comparator<V> vertexComparator = defaultVertexComparator;
	
	
	public TopologicalSort() {
		this(null);
	}
	
	public TopologicalSort(Comparator<V> vertexComparator) {
		super();
		
		orderedVertices = new ArrayList<>();
		vertexLevels = new LinkedHashMap<>();
		
		if(vertexComparator != null)
				this.vertexComparator = vertexComparator;
	}
	
	public List<V> getVertexOrder() {
		return Collections.unmodifiableList(orderedVertices);
	}
	
	public Map<V, Integer> getVertexLevels() {
		return Collections.unmodifiableMap(vertexLevels);
	}
	
	public Comparator<V> getVertexComparator() {
		return this.vertexComparator;
	}
	
	public void setVertexComparator(Comparator<V> vertexComparator) {
		this.vertexComparator = vertexComparator;
	}
	
	public void reset() {
		orderedVertices.clear();
		vertexLevels.clear();
	}

	public void sort(DirectedAcyclicGraph<V, E> graph) throws CycleDetectedException {
		sort(graph.vertices, graph.edges);
	}
	
	public void sort(List<V> vertices, Set<E> edges) throws CycleDetectedException {
		reset();
		
		final ArrayList<V> orderedVertices = new ArrayList<V>();
		final WeakHashMap<V, Integer> newLevels = new WeakHashMap<V, Integer>();
		final HashMap<V, Integer> incomingEdgeCount = new HashMap<V, Integer>();

		for(V vertex : vertices)
			incomingEdgeCount.put(vertex, 0);

		// Gather initial incoming edge count
		for(E edge : edges) {
			int count = incomingEdgeCount.get(edge.getDestination());
			incomingEdgeCount.put(edge.getDestination(), count + 1);
		}

		// Ordering
		for(int level = 0; orderedVertices.size() < vertices.size(); ++level) {
			// Find a vertex with zero incoming edges
			List<V> verticesToProcess = 
					incomingEdgeCount.entrySet().parallelStream()
						.filter( (e) -> e.getValue() == 0 )
						.map( (e) -> e.getKey() )
						.collect(Collectors.toList());

			if(verticesToProcess.size() == 0)
				break;

			final List<V> levelOrdering = new ArrayList<>();
			for(V vertex : verticesToProcess) {
				// Prevent reuse of this vertex
				levelOrdering.add(vertex);
				newLevels.put(vertex, level);
				incomingEdgeCount.put(vertex, -1);

				Set<E> outgoingEdges = 
						edges.parallelStream()
						.filter( e -> e.getSource() == vertex )
						.collect(Collectors.toSet());
				// Reduce incoming edge count after removing vertex
				for(E edge : outgoingEdges) {
					V out = edge.getDestination();
					incomingEdgeCount.put(out, incomingEdgeCount.get(out) - 1);
				}
			}
			levelOrdering.sort(getVertexComparator());
			orderedVertices.addAll(levelOrdering);
		}

		boolean cycleExists = false;
		for(Integer value : incomingEdgeCount.values()) {
			if(value > 0) {
				cycleExists = true;
				break;
			}
		}

		if(cycleExists)
			throw new CycleDetectedException();
		
		this.vertexLevels = newLevels;
		this.orderedVertices = orderedVertices;
	}
	
}
