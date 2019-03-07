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
package ca.phon.opgraph.app.edits.graph;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.extensions.NodeMetadata;
import ca.phon.opgraph.library.NodeData;

/**
 * A canvas edit that moves a collection of nodes a specified amount.
 */
public class AddNodeEdit extends AbstractUndoableEdit {
	/** The graph to which this edit was applied  */
	private OpGraph graph;

	/** The node information */
	private NodeData info;

	/** The node that was added */
	private OpNode node;

	/**
	 * Constructs an edit that constructs a node described by a specified
	 * {@link NodeData} and adds it to the given canvas.
	 * 
	 * @param graph  the graph to which this edit will be applied
	 * @param info  the info used for constructing the node
	 * 
	 * @throws InstantiationException  if the node could not be instantiated
	 *                                 from the instantiator in the node info
	 * @throws NullPointerException  if any argument is <code>null</code>
	 */
	public AddNodeEdit(OpGraph graph, NodeData info)
		throws InstantiationException
	{
		this(graph, info, 0, 0);
	}

	/**
	 * Constructs an edit that constructs a node described by a specified
	 * {@link NodeData} and adds it to the given canvas at the given
	 * initial location.
	 * 
	 * @param graph  the graph to which this edit will be applied
	 * @param info  the info used for constructing the node
	 * @param x  the initial x-coordinate for the node
	 * @param y  the initial y-coordinate for the node
	 * 
	 * @throws InstantiationException  if the node could not be instantiated
	 *                                 from the instantiator in the node info
	 * @throws NullPointerException  if either the canvas or info is <code>null</code>
	 */
	public AddNodeEdit(OpGraph graph, NodeData info, int x, int y)
		throws InstantiationException
	{
		this.graph = graph;
		this.info = info;
		this.node = this.info.instantiator.newInstance(info, this.graph);
		this.node.putExtension(NodeMetadata.class, new NodeMetadata(x, y));
		perform();
	}

	/**
	 * Constructs an edit that adds a node to the given canvas at the given
	 * initial location.
	 * 
	 * @param graph  the graph to which this edit will be applied
	 * @param node  the node to add to the graph
	 */
	public AddNodeEdit(OpGraph graph, OpNode node) {
		this.graph = graph;
		this.node = node;
		perform();
	}

	/**
	 * Constructs an edit that adds a node to the given canvas at the given
	 * initial location.
	 * 
	 * @param graph  the graph to which this edit will be applied
	 * @param node  the node to add to the graph
	 */
	public AddNodeEdit(OpGraph graph, OpNode node, int x, int y) {
		this.graph = graph;
		this.node = node;
		this.node.putExtension(NodeMetadata.class, new NodeMetadata(x, y));
		perform();
	}
	
	/**
	 * Performs this edit.
	 */
	private void perform() {
		graph.add(node);
	}

	public OpNode getNode() {
		return this.node;
	}
	
	//
	// AbstractUndoableEdit
	//

	@Override
	public String getPresentationName() {
		return "Add Node";
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		perform();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		graph.remove(node);
	}
}
