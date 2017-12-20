package ca.gedge.opgraph.nodes.menu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.logging.*;

import javax.swing.*;

import ca.gedge.opgraph.app.GraphDocument;
import ca.gedge.opgraph.app.edits.graph.AddNodeEdit;
import ca.gedge.opgraph.nodes.reflect.ObjectCastNode;

/**
 * Add a new {@link ObjectCastNode} to the graph.
 * 
 */
public class AddCastCommand extends AbstractAction {

	private static final long serialVersionUID = -5964240069884374978L;

	private static final Logger LOGGER = Logger
			.getLogger(AddCastCommand.class.getName());
	
	private GraphDocument document;
	
	private final Point point;
	
	public AddCastCommand(GraphDocument doc, Point p) {
		super();
		this.document = doc;
		this.point = p;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(GraphicsEnvironment.isHeadless())
			return;
		
		if(document != null) {
			// request a class name
			// TODO create a better UI for this
			final String className = JOptionPane.showInputDialog("Enter a class name:");
			if(className != null) {
				try {
					final Class<?> clazz = Class.forName(className);
					final ObjectCastNode castNode = new ObjectCastNode(clazz);
					
					final AddNodeEdit addNodeEdit = new AddNodeEdit(document.getGraph(), castNode, point.x, point.y);
					document.getUndoSupport().postEdit(addNodeEdit);
				} catch (ClassNotFoundException e) {
					LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
			}
		}
	}
		
}
