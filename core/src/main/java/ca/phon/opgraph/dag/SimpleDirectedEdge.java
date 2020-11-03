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
package ca.phon.opgraph.dag;

/**
 * An edge that only knows about its source and destination vertices.
 * 
 * @param <V>  the type of vertex used in the edge
 */
public class SimpleDirectedEdge<V> implements DirectedEdge<V> {
	/** The source vertex of this edge */
	protected final V source;

	/** The destination vertex of this edge */
	protected final V destination;

	/**
	 * Constructs an edge with a specified source/destination vertex.
	 * 
	 * @param source  source vertex
	 * @param destination  destination vertex
	 * 
	 * @throws NullPointerException  if either source/dest is <code>null</code> 
	 */
	public SimpleDirectedEdge(V source, V destination) {
		if(source == null || destination == null)
			throw new NullPointerException("source/destination cannot be null");

		this.source = source;
		this.destination = destination;
	}

	//
	// DirectedEdge
	//

	@Override
	public V getSource() {
		return source;
	}

	@Override
	public V getDestination() {
		return destination;
	}

	@Override
	public int compareTo(DirectedEdge<V> o) {
		if(o == null)
			return 1;
		return (equals(o) ? 0 : (new Integer(System.identityHashCode(this))).compareTo(System.identityHashCode(o)));
	}
}
