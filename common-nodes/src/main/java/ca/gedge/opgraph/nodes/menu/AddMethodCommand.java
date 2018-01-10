package ca.gedge.opgraph.nodes.menu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

import javax.swing.AbstractAction;

import ca.gedge.opgraph.app.GraphDocument;
import ca.gedge.opgraph.app.edits.graph.AddNodeEdit;
import ca.gedge.opgraph.nodes.reflect.MethodNode;
import ca.gedge.opgraph.util.ReflectUtil;

public class AddMethodCommand extends AbstractAction {
	
	private static final long serialVersionUID = -469196233555490912L;
	
	private GraphDocument document;
	
	private final Method method;
	
	private final Point point;
	
	public AddMethodCommand(GraphDocument document, Method method, Point p) {
		super();
		this.document = document;
		this.method = method;
		this.point = p;
		putValue(NAME, ReflectUtil.getSignature(method));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(GraphicsEnvironment.isHeadless())
			return;
		
		if(document != null) {
			final MethodNode methodNode = new MethodNode(method);
			final AddNodeEdit edit = new AddNodeEdit(document.getGraph(), methodNode, point.x, point.y);
			document.getUndoSupport().postEdit(edit);
		}
	}

}
