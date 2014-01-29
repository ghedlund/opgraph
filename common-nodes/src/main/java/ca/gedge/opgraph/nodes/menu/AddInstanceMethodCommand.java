package ca.gedge.opgraph.nodes.menu;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
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

import ca.gedge.opgraph.OutputField;
import ca.gedge.opgraph.app.GraphDocument;
import ca.gedge.opgraph.app.GraphEditorModel;
import ca.gedge.opgraph.app.edits.graph.AddNodeEdit;
import ca.gedge.opgraph.nodes.reflect.MethodNode;
import ca.gedge.opgraph.nodes.reflect.StaticMethodNode;
import ca.gedge.opgraph.util.ReflectUtil;

public class AddInstanceMethodCommand extends AbstractAction {
	
	private static final Logger LOGGER = Logger
			.getLogger(AddInstanceMethodCommand.class.getName());
	
	private final Point point;
	
	public AddInstanceMethodCommand(Point p) {
		super();
		this.point = p;
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
					final List<Method> staticMethods = ReflectUtil.getInstanceMethods(clazz);
					
					final JComboBox comboBox = new JComboBox(staticMethods.toArray(new Method[0]));
					comboBox.setRenderer(new DefaultListCellRenderer() {
						
						@Override
						public Component getListCellRendererComponent(JList arg0, Object arg1,
								int arg2, boolean arg3, boolean arg4) {
							final Method method = (Method)arg1;
							final JLabel retVal = (JLabel)super.getListCellRendererComponent(arg0, arg1, arg2, arg3, arg4);
							retVal.setText(ReflectUtil.getSignature(method));
							return retVal;
						}
					});
					int retVal = 
							JOptionPane.showConfirmDialog(null, comboBox, "Select instance method:", JOptionPane.OK_CANCEL_OPTION);
					if(retVal == JOptionPane.OK_OPTION) {
						final Method selectedMethod = (Method)comboBox.getSelectedItem();
						
						final MethodNode node = new MethodNode(selectedMethod);
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
