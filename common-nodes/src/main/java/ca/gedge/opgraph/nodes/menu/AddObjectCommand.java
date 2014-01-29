package ca.gedge.opgraph.nodes.menu;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import ca.gedge.opgraph.OpNode;
import ca.gedge.opgraph.app.GraphDocument;
import ca.gedge.opgraph.app.GraphEditorModel;
import ca.gedge.opgraph.app.edits.graph.AddNodeEdit;
import ca.gedge.opgraph.nodes.reflect.ObjectNode;
import ca.gedge.opgraph.nodes.reflect.ConstructorNode;
import ca.gedge.opgraph.nodes.reflect.IterableClassNode;
import ca.gedge.opgraph.util.ReflectUtil;

public class AddObjectCommand extends AbstractAction {
	
	private static final Logger LOGGER = Logger
			.getLogger(AddObjectCommand.class.getName());

	private static final long serialVersionUID = -2443166989357102209L;

	private final Point point;
	
	public AddObjectCommand(Point p) {
		super();
		this.point = p;
		putValue(ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | KeyEvent.VK_ALT));
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(GraphicsEnvironment.isHeadless())
			return;
		
		final GraphDocument document = GraphEditorModel.getActiveDocument();
		if(document != null) {
			// request a class name
			// TODO create a better UI for this
			final String className = JOptionPane.showInputDialog("Enter a class name:");
			if(className != null) {
					
					try {
						final Class<?> clazz = Class.forName(className);
						
						final Constructor<?> constructors[] = clazz.getConstructors();
						
						if(constructors.length == 0) return;
						
						Constructor<?> selectedConstructor = constructors[0];
						if(constructors.length > 1 || constructors[0].getParameterTypes().length > 0) {
							// show constructor option dialog
							final JComboBox comboBox = new JComboBox(constructors);
//							comboBox.setRenderer(new DefaultListCellRenderer() {
//								
//								@Override
//								public Component getListCellRendererComponent(JList arg0, Object arg1,
//										int arg2, boolean arg3, boolean arg4) {
//									final Constructor<?> method = (Constructor<?>)arg1;
//									final JLabel retVal = (JLabel)super.getListCellRendererComponent(arg0, arg1, arg2, arg3, arg4);
//									retVal.setText(RE);
//									return retVal;
//								}
//							});
							int retVal = 
									JOptionPane.showConfirmDialog(null, comboBox, "Select constructor:", JOptionPane.OK_CANCEL_OPTION);
							if(retVal == JOptionPane.OK_OPTION) {
								selectedConstructor = (Constructor<?>)comboBox.getSelectedItem();
							} else {
								return;
							}
						}
						
//						OpNode node = null;
//						if(Iterable.class.isAssignableFrom(clazz)) {
//							node = new IterableClassNode(clazz);
//						} else {
//							node = new ClassNode(clazz);
//						}
						
						final ConstructorNode node = new ConstructorNode(selectedConstructor);
						final AddNodeEdit edit = new AddNodeEdit(document.getGraph(), node, point.x, point.y);
						document.getUndoSupport().postEdit(edit);
					} catch (ClassNotFoundException e) {
						LOGGER.log(Level.SEVERE, e.getLocalizedMessage(),
								e);
					}
			}
		}
	}

}
