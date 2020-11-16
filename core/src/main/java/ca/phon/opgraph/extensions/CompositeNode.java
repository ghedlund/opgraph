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
package ca.phon.opgraph.extensions;

import ca.phon.opgraph.*;

/**
 * An extension meant for any {@link OpNode} that contains a graph (e.g., macro nodes).
 */
public interface CompositeNode {
	/**
	 * Gets the graph contained within this node.
	 * 
	 * @return the graph that composes this node
	 */
	public abstract OpGraph getGraph();

	/**
	 * Sets the graph contained within this node.
	 * 
	 * @param graph  the graph that composes this node
	 */
	public abstract void setGraph(OpGraph graph);
	
	/**
	 * Is graph linked or embedded
	 * 
	 * @return is graph linked or embedded
	 */
	public abstract boolean isGraphEmbedded();
	
}
