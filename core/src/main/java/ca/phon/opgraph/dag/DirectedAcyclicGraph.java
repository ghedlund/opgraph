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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

/**
 * A generic implementation of a directed acyclic graph (DAG). Topological
 * ordering is enforced on the vertices of this graph (see
 * <a href="http://en.wikipedia.org/wiki/Topological_sorting">Wikipedia Entry</a>).
 *
 * @param <V>  the vertex type, which implements {@link Vertex}
 * @param <E>  the edge type, which implements {@link DirectedEdge}
 */
public class DirectedAcyclicGraph<V extends Vertex, E extends DirectedEdge<V>>
	implements Iterable<V>
{
	/** The vertices in this DAG */
	List<V> vertices;

	/** The edges in this DAG */
	Set<E> edges;

	/**
	 * A mapping from vertex to its level.
	 *
	 * @see #getLevel(Object)
	 */
	private Map<V, Integer> vertexLevels;

	/** Whether or not the topological sorting needs to be performed */
	private boolean shouldSort;

	/** Comparator for ordering of nodes within a level (default by toString()) */
	private final Comparator<V> defaultVertexComparator = (v1, v2) -> {
		final String name1 = v1.toString();
		final String name2 = v2.toString();
		return name1.compareTo(name2);
	};
	private Comparator<V> vertexComparator = defaultVertexComparator;

	/**
	 * Default constructor.
	 */
	public DirectedAcyclicGraph() {
		this.vertices = new ArrayList<V>();
		this.edges = new LinkedHashSet<E>();
		this.vertexLevels = new WeakHashMap<V, Integer>();
		this.shouldSort = false;
	}

	/**
	 * Adds a vertex to this DAG.
	 *
	 * @param vertex  the vertex to add
	 */
	public void add(V vertex) {
		if(!vertices.contains(vertex)) {
			vertices.add(vertex);
			shouldSort = true;
		}
	}

	/**
	 * Removes a vertex from this DAG. Any {@link DirectedEdge}s in this DAG that
	 * reference this vertex will also be removed.
	 *
	 * @param vertex  the vertex to remove
	 *
	 * @return <code>true</code> if this graph contained the given vertex,
	 *         <code>false</code> otherwise
	 */
	public boolean remove(V vertex) {
		// Remove edges which reference this vertex
		final ArrayList<E> edgesCopy = new ArrayList<E>(edges);
		for(E edge : edgesCopy) {
			if(edge.getSource() == vertex || edge.getDestination() == vertex)
				remove(edge);
		}
		final boolean removed = vertices.remove(vertex);
		if(removed) {
			shouldSort = true;
		}
		return removed;
	}

	/**
	 * Gets whether or not this graph contains a specified vertex.
	 *
	 * @param vertex  the vertex
	 *
	 * @return <code>true</code> if this graph contains the specified vertex,
	 *         <code>false</code> otherwise
	 */
	public boolean contains(V vertex) {
		return vertices.contains(vertex);
	}

	/**
	 * Gets whether or not this graph contains a specified edge.
	 *
	 * @param edge  the edge
	 *
	 * @return <code>true</code> if this graph contains the specified edge,
	 *         <code>false</code> otherwise
	 */
	public boolean contains(E edge) {
		return edges.contains(edge);
	}

	/**
	 * Adds an edge to this DAG.
	 *
	 * @param edge  the edge to add
	 *
	 * @throws VertexNotFoundException  if <code>edge</code> contains vertices
	 *                                  that are not contained within this graph.
	 *
	 * @throws CycleDetectedException  if adding <code>edge</code> will induce a cycle
	 */
	public void add(E edge) throws VertexNotFoundException, CycleDetectedException {
		if(!vertices.contains(edge.getSource()))
			throw new VertexNotFoundException(edge.getSource());

		if(!vertices.contains(edge.getDestination()))
			throw new VertexNotFoundException(edge.getDestination());

		edges.add(edge);

		// Check if adding this edge created a cycle, and if so, remove it
		boolean oldShouldSort = shouldSort;
		shouldSort = true;
		if(!topologicalSort()) {
			edges.remove(edge);
			shouldSort = oldShouldSort;
			throw new CycleDetectedException("adding edge creates a cycle");
		}
	}

	/**
	 * Gets whether or not an edge can be added to this graph without raising
	 * any exception defined in {@link #add(DirectedEdge)}.
	 *
	 * @param edge  the edge to check
	 *
	 * @return <code>true</code> if the edge can be added without inducing a
	 *         cycle, <code>false</code> otherwise
	 */
	public boolean canAddEdge(E edge) {
		boolean canAdd = false;
		if(vertices.contains(edge.getSource()) && vertices.contains(edge.getDestination())) {
			final Set<E> testEdges = new HashSet<>(this.edges);
			testEdges.add(edge);

			// Test for cycle
			final TopologicalSort<V, E> sorter = new TopologicalSort<>(getVertexComparator());
			try {
				sorter.sort(this.vertices, testEdges);
				canAdd = true;
			} catch (CycleDetectedException e) {
				canAdd = false;
			}
		}
		return canAdd;
	}

	/**
	 * Removes an edge from this DAG.
	 *
	 * @param edge  the edge to remove
	 *
	 * @return <code>true</code> if this graph contained the given vertex,
	 *         <code>false</code> otherwise
	 */
	public boolean remove(E edge) {
		final int initalSize = edges.size();
		edges.remove(edge);
		final boolean removed = initalSize != edges.size();
		if(removed) {
			shouldSort = true;
		}
		return removed;
	}

	/**
	 * Gets the set of vertices in this DAG. The list of vertices will be
	 * ordered according to their topological ordering.
	 *
	 * @return An immutable {@link Set} of vertices.
	 */
	public List<V> getVertices() {
		topologicalSort();
		return Collections.unmodifiableList(vertices);
	}

	/**
	 * Gets the set of edges in this DAG.
	 *
	 * @return An immutable {@link Set} of edges.
	 */
	public Set<E> getEdges() {
		return Collections.unmodifiableSet(edges);
	}

	/**
	 * Gets the level of a vertex. The level of a vertex <code>v</code> is
	 * defined as:
	 * <ul>
	 *   <li>0, if <code>getIncomingEdges(v) == 0</code></li>
	 *   <li><code>min(level of u) for u in getIncomingEdges(v).getSource()</code></li>
	 * </ul>
	 *
	 * @param vertex  the vertex
	 *
	 * @return the level of the vertex, or -1 if the vertex is not in this graph
	 */
	public int getLevel(V vertex) {
		if(!vertices.contains(vertex))
			return -1;

		topologicalSort();

		int ret = -1;
		if(vertexLevels.get(vertex) != null)
			ret = vertexLevels.get(vertex);
		return ret;
	}

	/**
	 * Gets the incoming {@link DirectedEdge}s for a {@link Vertex}.
	 *
	 * @param vertex  the vertex
	 *
	 * @return a {@link Set} of {@link DirectedEdge}s in this graph whose destination
	 *         is <code>vertex</code>
	 */
	public Set<E> getIncomingEdges(V vertex) {
		if(!vertices.contains(vertex))
			return new LinkedHashSet<E>();

		return edges.parallelStream()
			.filter( (l) -> l.getDestination() == vertex )
			.sorted( (l1, l2) -> getVertexComparator().compare(l1.getSource(), l2.getSource()) )
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	/**
	 * Gets the outgoing {@link DirectedEdge}s for a {@link Vertex}.
	 *
	 * @param vertex  the vertex
	 *
	 * @return a {@link Set} of {@link DirectedEdge}s in this graph whose source is
	 *         the <code>vertex</code>
	 */
	public Set<E> getOutgoingEdges(V vertex) {
		if(!vertices.contains(vertex))
			return new LinkedHashSet<E>();

		return edges.parallelStream()
			.filter( (l) -> l.getSource() == vertex )
			.sorted( (l1, l2) -> getVertexComparator().compare(l1.getDestination(), l2.getDestination()) )
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	/**
	 * Set vertex comparator used during topologicalSort.  This comparator
	 * is used to determine ordering of vertices which are in the same level.
	 *
	 * @param comparator
	 */
	public void setVertexComparator(Comparator<V> comparator) {
		this.vertexComparator = comparator;
	}

	/**
	 * Get vertex comparator.
	 *
	 * @return comparator
	 */
	public Comparator<V> getVertexComparator() {
		return this.vertexComparator;
	}

	@Override
	public Iterator<V> iterator() {
		topologicalSort();

		return new Iterator<V>() {
			private Iterator<V> iter = vertices.iterator();

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public V next() {
				return iter.next();
			}

			@Override
			public void remove() {
				// TODO perhaps allow removal?
				throw new UnsupportedOperationException("Removal via iterator not supported in DAGs");
			}
		};
	}

	public void invalidateSort() {
		this.shouldSort = true;
	}

	/**
	 * Topologically orders the vertices in this DAG. A topological ordering
	 * is an ordering of a DAG's vertices such that for any edge
	 * <tt>{u, v}</tt>, the vertex <tt>u</tt> comes before the vertex
	 * <tt>v</tt> in the ordering.
	 *
	 * @return <code>true</code> if sorting was successful, <code>false</code>
	 *         otherwise (because a cycle exists).
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/Topological_sorting">Wikipedia Article</a>
	 */
	public boolean topologicalSort() {
		boolean ret = true;

		final TopologicalSort<V, E> sorter = new TopologicalSort<V, E>(getVertexComparator());
		try {
			sorter.sort(this);

			this.vertices = new ArrayList<>(sorter.getVertexOrder());
			this.vertexLevels = new WeakHashMap<>(sorter.getVertexLevels());
			shouldSort = false;
		} catch (CycleDetectedException e) {
			ret = false;
		}

		return ret;
	}

}
