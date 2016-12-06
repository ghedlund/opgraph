package ca.gedge.opgraph.nodes.canvas;

import java.awt.Point;

import ca.gedge.opgraph.ContextualItem;
import ca.gedge.opgraph.InputField;
import ca.gedge.opgraph.OpLink;
import ca.gedge.opgraph.OpNode;
import ca.gedge.opgraph.OutputField;
import ca.gedge.opgraph.app.GraphDocument;
import ca.gedge.opgraph.app.components.canvas.AbandonedLinkHandler;
import ca.gedge.opgraph.app.components.canvas.CanvasNode;
import ca.gedge.opgraph.app.components.canvas.CanvasNodeField;
import ca.gedge.opgraph.app.components.canvas.GraphCanvas;
import ca.gedge.opgraph.app.edits.graph.AddLinkEdit;
import ca.gedge.opgraph.app.edits.graph.AddNodeEdit;
import ca.gedge.opgraph.dag.CycleDetectedException;
import ca.gedge.opgraph.dag.VertexNotFoundException;
import ca.gedge.opgraph.exceptions.ItemMissingException;
import ca.gedge.opgraph.nodes.reflect.ObjectNode;
import ca.gedge.opgraph.nodes.reflect.IterableClassNode;

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
			} catch (ItemMissingException e) {
			} catch (VertexNotFoundException e) {
			} catch (CycleDetectedException e) {
			}
		}
	}

}
