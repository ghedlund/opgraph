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
package ca.phon.opgraph.app.edits.graph;

import javax.swing.undo.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.dag.*;

/**
 * Adds a link between two fields.
 */
public class AddLinkEdit extends AbstractUndoableEdit {
	/** The graph to which this edit was applied  */
	private OpGraph graph;

	/** The node whose name is changing */
	private final OpLink link;

	/**
	 * Constructs an edit that creates a link between two fields.
	 * 
	 * @param graph  the graph to which this edit will be applied
	 * @param link  the link to add 
	 * 
	 * @throws CycleDetectedException  if creation of the link creates a cycle
	 * @throws VertexNotFoundException  if either/both of the source/destination of
	 *                                  the link is not a member of the graph 
	 * @throws InvalidEdgeException 
	 */
	public AddLinkEdit(OpGraph graph, OpLink link)
		throws VertexNotFoundException, CycleDetectedException, InvalidEdgeException
	{
		this.graph = graph;
		this.link = link;
		perform();
	}

	/**
	 * Performs this edit.
	 */
	private void perform() throws VertexNotFoundException, CycleDetectedException, InvalidEdgeException {
		graph.add(link);
	}

	//
	// AbstractUndoableEdit
	//

	@Override
	public String getPresentationName() {
		return "Add Link";
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		try {
			perform();
		} catch(VertexNotFoundException | CycleDetectedException | InvalidEdgeException exc) {
			throw new CannotRedoException();
		}
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		graph.remove(link);
	}
}
