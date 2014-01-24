package ca.gedge.opgraph.nodes.reflect;

import java.lang.reflect.Method;

import ca.gedge.opgraph.OpContext;
import ca.gedge.opgraph.exceptions.ProcessingException;

public class StaticMethodNode extends MethodNode {

	public StaticMethodNode() {
		super();
	}

	public StaticMethodNode(Method method) {
		super(method);
	}

	@Override
	public void setMethod(Method method) {
		super.setMethod(method);
		// remove 'obj' input field
		super.removeField(getInputFieldWithKey("obj"));
	}

	@Override
	public void operate(OpContext context) throws ProcessingException {
		// make sure 'obj' key is null
		context.put("obj", null);
		super.operate(context);
	}
	
}
