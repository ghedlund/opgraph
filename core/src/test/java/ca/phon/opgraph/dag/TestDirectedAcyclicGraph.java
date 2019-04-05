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

import static ca.phon.CollectionsAssert.assertCollectionEqualsArray;
import static org.junit.Assert.*;

import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;

import ca.phon.opgraph.dag.CycleDetectedException;
import ca.phon.opgraph.dag.DirectedAcyclicGraph;
import ca.phon.opgraph.dag.SimpleDirectedEdge;
import ca.phon.opgraph.dag.Vertex;
import ca.phon.opgraph.dag.VertexNotFoundException;

/**
 * Tests {@link DirectedAcyclicGraph}.
 */
public class TestDirectedAcyclicGraph {
	/**
	 * Basic vertex class for testing.
	 */
	private static class SimpleVertex implements Vertex {
		private String name;

		public SimpleVertex(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	//
	// Test data
	//

	private HashMap<String, SimpleVertex> vertexMap = new HashMap<String, SimpleVertex>();
	private HashMap<String, SimpleDirectedEdge<SimpleVertex>> edgeMap = new HashMap<String, SimpleDirectedEdge<SimpleVertex>>();

	@Before
	public void setUp() {
		char start = 'A';
		char end = 'Z';
		for(char s = start; s <= end; ++s)
			vertexMap.put("" + s, new SimpleVertex("" + s));

		for(char u = start; u <= end; ++u) {
			for(char v = (char)(u + 1); v <= end; ++v) {
				SimpleVertex uV = vertexMap.get("" + u);
				SimpleVertex vV = vertexMap.get("" + v);
				edgeMap.put(u + "" + v, new SimpleDirectedEdge<SimpleVertex>(uV, vV));
				edgeMap.put(v + "" + u, new SimpleDirectedEdge<SimpleVertex>(vV, uV));
			}
		}
	}

	/**
	 * Tests the correctness of topological sorting in a DAG
	 */
	@Test
	public void testTopologicalOrdering() {
		DirectedAcyclicGraph<SimpleVertex, SimpleDirectedEdge<SimpleVertex>> dag = new DirectedAcyclicGraph<SimpleVertex, SimpleDirectedEdge<SimpleVertex>>();
		dag.add(vertexMap.get("A"));
		dag.add(vertexMap.get("B"));
		dag.add(vertexMap.get("C"));
		dag.add(vertexMap.get("D"));

		try {
			dag.add(edgeMap.get("DC"));
			dag.add(edgeMap.get("CA"));
			dag.add(edgeMap.get("AB"));
			dag.add(edgeMap.get("DA"));
		} catch(VertexNotFoundException exc) {
			fail("Vertex not found, but should be: " + exc.getVertex());
		} catch(CycleDetectedException exc) {
			fail("Adding edge creates cycle, but this shouldn't happen");
		} catch (InvalidEdgeException e) {
			fail("Should not happen");
		}

		assertCollectionEqualsArray(dag.getVertices(), 
		                            vertexMap.get("A"), vertexMap.get("B"), vertexMap.get("C"), vertexMap.get("D"));
	}

	/**
	 * Tests cycle detection in a DAG
	 */
	@Test(expected=CycleDetectedException.class)
	public void testCycleException() throws CycleDetectedException {
		DirectedAcyclicGraph<SimpleVertex, SimpleDirectedEdge<SimpleVertex>> dag = new DirectedAcyclicGraph<SimpleVertex, SimpleDirectedEdge<SimpleVertex>>();
		dag.add(vertexMap.get("A"));
		dag.add(vertexMap.get("B"));
		dag.add(vertexMap.get("C"));
		dag.add(vertexMap.get("D"));

		try {
			dag.add(edgeMap.get("DC"));
			dag.add(edgeMap.get("CA"));
			dag.add(edgeMap.get("AB"));
			dag.add(edgeMap.get("DA"));
			dag.add(edgeMap.get("BD")); // creates a cycle
		} catch(VertexNotFoundException exc) {
			fail("Vertex not found, but should be: " + exc.getVertex());
		} catch (InvalidEdgeException e) {
			fail("Invalid link");
		}
	}

	/**
	 * Tests incoming/outgoing edges in a DAG
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testIncomingOutgoingEdges() {
		DirectedAcyclicGraph<SimpleVertex, SimpleDirectedEdge<SimpleVertex>> dag = new DirectedAcyclicGraph<SimpleVertex, SimpleDirectedEdge<SimpleVertex>>();
		dag.add(vertexMap.get("A"));
		dag.add(vertexMap.get("B"));
		dag.add(vertexMap.get("C"));
		dag.add(vertexMap.get("D"));
		dag.add(vertexMap.get("E"));
		dag.add(vertexMap.get("F"));
		dag.add(vertexMap.get("G"));

		try {
			dag.add(edgeMap.get("DC"));
			dag.add(edgeMap.get("CA"));
			dag.add(edgeMap.get("AB"));
			dag.add(edgeMap.get("AE"));
			dag.add(edgeMap.get("DA"));
			dag.add(edgeMap.get("EF"));
		} catch(CycleDetectedException exc) {
			fail("Adding edge creates cycle, but this shouldn't happen");
		} catch(VertexNotFoundException exc) {
			fail("Vertex not found, but should be: " + exc.getVertex());
		} catch (InvalidEdgeException e) {
			fail("Invalid link");
		}

		assertCollectionEqualsArray(dag.getIncomingEdges(vertexMap.get("A")), edgeMap.get("CA"), edgeMap.get("DA"));
		assertCollectionEqualsArray(dag.getOutgoingEdges(vertexMap.get("A")), edgeMap.get("AB"), edgeMap.get("AE"));

		assertCollectionEqualsArray(dag.getIncomingEdges(vertexMap.get("B")), edgeMap.get("AB"));
		assertCollectionEqualsArray(dag.getOutgoingEdges(vertexMap.get("B")));

		assertCollectionEqualsArray(dag.getIncomingEdges(vertexMap.get("C")), edgeMap.get("DC"));
		assertCollectionEqualsArray(dag.getOutgoingEdges(vertexMap.get("C")), edgeMap.get("CA"));

		assertCollectionEqualsArray(dag.getIncomingEdges(vertexMap.get("D")));
		assertCollectionEqualsArray(dag.getOutgoingEdges(vertexMap.get("D")), edgeMap.get("DA"), edgeMap.get("DC"));

		assertCollectionEqualsArray(dag.getIncomingEdges(vertexMap.get("E")), edgeMap.get("AE"));
		assertCollectionEqualsArray(dag.getOutgoingEdges(vertexMap.get("E")), edgeMap.get("EF"));

		assertCollectionEqualsArray(dag.getIncomingEdges(vertexMap.get("F")), edgeMap.get("EF"));
		assertCollectionEqualsArray(dag.getOutgoingEdges(vertexMap.get("F")));

		assertCollectionEqualsArray(dag.getIncomingEdges(vertexMap.get("G")));
		assertCollectionEqualsArray(dag.getOutgoingEdges(vertexMap.get("G")));
	}
}
