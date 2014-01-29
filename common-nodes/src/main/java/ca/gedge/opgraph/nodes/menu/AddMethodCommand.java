package ca.gedge.opgraph.nodes.menu;

import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

import ca.gedge.opgraph.app.GraphDocument;
import ca.gedge.opgraph.app.GraphEditorModel;
import ca.gedge.opgraph.app.edits.graph.AddNodeEdit;
import ca.gedge.opgraph.nodes.reflect.MethodNode;
import ca.gedge.opgraph.util.ReflectUtil;

public class AddMethodCommand extends AbstractAction {
	
	private static final long serialVersionUID = -469196233555490912L;
	
	private final Method method;
	
	private final Point point;
	
	public AddMethodCommand(Method method, Point p) {
		super();
		this.method = method;
		this.point = p;
		putValue(NAME, ReflectUtil.getSignature(method));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(GraphicsEnvironment.isHeadless())
			return;
		
		final GraphDocument document = GraphEditorModel.getActiveDocument();
		if(document != null) {
			final MethodNode methodNode = new MethodNode(method);
			final AddNodeEdit edit = new AddNodeEdit(document.getGraph(), methodNode, point.x, point.y);
			document.getUndoSupport().postEdit(edit);
		}
	}

}
