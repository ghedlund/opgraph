package ca.gedge.opgraph.nodes.menu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.*;

import javax.swing.*;

import ca.gedge.opgraph.app.GraphDocument;
import ca.gedge.opgraph.app.edits.graph.AddNodeEdit;
import ca.gedge.opgraph.nodes.reflect.StaticFieldNode;
import ca.gedge.opgraph.util.ReflectUtil;

public class AddStaticFieldCommand extends AbstractAction {

	private static final long serialVersionUID = -6653398455608738678L;
	
	private static final Logger LOGGER = Logger
			.getLogger(AddStaticMethodCommand.class.getName());

	private GraphDocument document;
	
	private final Point point;
	
	public AddStaticFieldCommand(GraphDocument doc, Point p) {
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
					final List<Field> staticFields = ReflectUtil.getStaticFields(clazz);
					
					final JComboBox<Field> comboBox = new JComboBox<>(staticFields.toArray(new Field[0]));
					comboBox.setRenderer(new DefaultListCellRenderer() {
						
						@Override
						public Component getListCellRendererComponent(JList arg0, Object arg1,
								int arg2, boolean arg3, boolean arg4) {
							final Field method = (Field)arg1;
							final JLabel retVal = (JLabel)super.getListCellRendererComponent(arg0, arg1, arg2, arg3, arg4);
							retVal.setText(method.getName());
							return retVal;
						}
					});
					int retVal = 
							JOptionPane.showConfirmDialog(null, comboBox, "Select static field:", JOptionPane.OK_CANCEL_OPTION);
					if(retVal == JOptionPane.OK_OPTION) {
						final Field selectedMethod = (Field)comboBox.getSelectedItem();
						
						final StaticFieldNode node = new StaticFieldNode(selectedMethod);
						final AddNodeEdit edit = new AddNodeEdit(document.getGraph(), node, point.x, point.y);
						document.getUndoSupport().postEdit(edit);
					}
					
				} catch (ClassNotFoundException e) {
					LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
			}
		}
	}
	
}
