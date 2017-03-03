package ca.gedge.opgraph.nodes.reflect;

import ca.gedge.opgraph.OpContext;
import ca.gedge.opgraph.OutputField;
import ca.gedge.opgraph.exceptions.ProcessingException;

/**
 * Output the {@link Class} object for a given type.
 *
 */
public class ClassNode extends AbstractReflectNode {
	
	private OutputField classOutput = new OutputField("class", "Class object", true, Class.class);

	public ClassNode() {
		this(Object.class);
	}
	
	public ClassNode(Class<?> type) {
		super(type, null);
		putField(classOutput);
	}
	
	@Override
	public void operate(OpContext context) throws ProcessingException {
		context.put(classOutput, super.getDeclaredClass());
	}

}
