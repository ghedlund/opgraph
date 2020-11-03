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
 * An {@link Exception} thrown when an operation on {@link DirectedAcyclicGraph}
 * requires a {@link Vertex} to be contained in the graph, but is not.
 */
public class VertexNotFoundException extends Exception {
	/** The {@link Vertex} that wasn't in the DAG */
	private Vertex vertex;

	/**
	 * Construct exception with the given vertex that could not be found and
	 * a default detail message.
	 * 
	 * @param vertex  the vertex that could not be found
	 */
	public VertexNotFoundException(Vertex vertex) {
		this(vertex, "Vertex not found");
	}

	/**
	 * Construct exception with the given vertex that could not be found and
	 * a custom detail message.
	 * 
	 * @param vertex  the vertex that could not be found
	 * @param message the detail message
	 */
	public VertexNotFoundException(Vertex vertex, String message) {
		super(message);
		this.vertex = vertex;
	}

	/**
	 * Gets the {@link Vertex} that was not found in the {@link DirectedAcyclicGraph}.
	 * 
	 * @return the {@link Vertex}
	 */
	public Vertex getVertex() {
		return this.vertex;
	}
}
