package ca.phon.opgraph.nodes.menu;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.*;

import javax.swing.*;

import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.edits.graph.AddNodeEdit;
import ca.phon.opgraph.nodes.reflect.ObjectNode;

public class AddObjectCommand extends AbstractAction {
	
	private static final Logger LOGGER = Logger
			.getLogger(AddObjectCommand.class.getName());

	private static final long serialVersionUID = -2443166989357102209L;

	private GraphDocument document;
	
	private final Point point;
	
	public AddObjectCommand(GraphDocument doc, Point p) {
		super();
		this.document = doc;
		this.point = p;
		putValue(ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | KeyEvent.VK_ALT));
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(GraphicsEnvironment.isHeadless())
			return;
		
		if(document != null) {
			// request a class name
			final String className = JOptionPane.showInputDialog("Enter a class name:");
			if(className != null) {
					
					try {
						final Class<?> clazz = Class.forName(className);
						
						final ObjectNode node = new ObjectNode(clazz);
						final AddNodeEdit edit = new AddNodeEdit(document.getGraph(), node, point.x, point.y);
						document.getUndoSupport().postEdit(edit);
					} catch (ClassNotFoundException e) {
						Toolkit.getDefaultToolkit().beep();
						LOGGER.log(Level.SEVERE, e.getLocalizedMessage(),
								e);
					}
			}
		}
	}

}
