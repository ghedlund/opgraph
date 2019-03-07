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
/**
 * 
 */
package ca.phon.opgraph;

/**
 *
 */
public interface OpGraphListener {
	/**
	 * Called when a nodenode was added to a graph.
	 *  
	 * @param graph  the source graph to which the node was added
	 * @param node  the node that was added
	 */
	public abstract void nodeAdded(OpGraph graph, OpNode node);

	/**
	 * Called when a node was removed from a graph.
	 * 
	 * @param graph  the source graph from which the node was removed
	 * @param node  the node that was removed
	 */
	public abstract void nodeRemoved(OpGraph graph, OpNode node);
	
	/**
	 * Called when a node instance was swapped with another copy.
	 * This event will be processed after the node removed and
	 * node added events.
	 *  
	 * @param graph
	 * @param oldNode
	 * @param newNode
	 */
	public abstract void nodeSwapped(OpGraph graph, OpNode oldNode, OpNode newNode);

	/**
	 * Called when an link was added to a graph.
	 *  
	 * @param graph  the source graph to which the link was added
	 * @param link  the link that was added
	 */
	public abstract void linkAdded(OpGraph graph, OpLink link);

	/**
	 * Called when an link was removed from a graph.
	 * 
	 * @param graph  the source graph from which the link was removed
	 * @param link  the link that was removed
	 */
	public abstract void linkRemoved(OpGraph graph, OpLink link);
	
}
