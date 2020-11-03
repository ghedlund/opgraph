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
package ca.phon.opgraph.nodes.canvas;

import java.awt.Point;

import ca.phon.opgraph.ContextualItem;
import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpLink;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.components.canvas.AbandonedLinkHandler;
import ca.phon.opgraph.app.components.canvas.CanvasNode;
import ca.phon.opgraph.app.components.canvas.CanvasNodeField;
import ca.phon.opgraph.app.components.canvas.GraphCanvas;
import ca.phon.opgraph.app.edits.graph.AddLinkEdit;
import ca.phon.opgraph.app.edits.graph.AddNodeEdit;
import ca.phon.opgraph.dag.CycleDetectedException;
import ca.phon.opgraph.dag.InvalidEdgeException;
import ca.phon.opgraph.dag.VertexNotFoundException;
import ca.phon.opgraph.exceptions.ItemMissingException;
import ca.phon.opgraph.nodes.reflect.IterableClassNode;
import ca.phon.opgraph.nodes.reflect.ObjectNode;

public class ReflectAbandonedLinkHandler implements AbandonedLinkHandler {

	@Override
	public void dragLinkAbandoned(GraphCanvas canvas, CanvasNode sourceNode,
			CanvasNodeField sourceField, Point p) {
		final GraphDocument document = canvas.getDocument();
		final ContextualItem field = sourceField.getField();
		if(field instanceof OutputField) {
			final OutputField outputField = (OutputField)field;
		
			// create a new node based on the type of the source at the drop point
			final Class<?> type = outputField.getOutputType();
			
			OpNode newNode = null;
			String fieldName = "obj";
			if(Iterable.class.isAssignableFrom(type)) {
				newNode = new IterableClassNode(type);
				fieldName = "collection";
			} else {
				newNode = new ObjectNode(type);
			}

			final AddNodeEdit addNodeEdit = new AddNodeEdit(document.getGraph(), newNode, p.x, p.y);
			document.getUndoSupport().postEdit(addNodeEdit);
			
			// setup link to new node
			final InputField inputField = newNode.getInputFieldWithKey(fieldName);
			try {
				final OpLink link = new OpLink(sourceNode.getNode(), outputField, newNode, inputField);
				final AddLinkEdit addLinkEdit = new AddLinkEdit(document.getGraph(), link);
				document.getUndoSupport().postEdit(addLinkEdit);
			} catch (ItemMissingException | VertexNotFoundException | CycleDetectedException | InvalidEdgeException e) {
			}
		}
	}

}
