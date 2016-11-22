package ca.gedge.opgraph.nodes.reflect;

import ca.gedge.opgraph.OpContext;
import ca.gedge.opgraph.OpNode;
import ca.gedge.opgraph.OpNodeInfo;
import ca.gedge.opgraph.exceptions.ProcessingException;

@OpNodeInfo(
		name="Current Iteration",
		category="iteration",
		description="Current iteration value",
		showInLibrary=true
)
public class CurrentIterationNode extends ObjectNode {
	
	public CurrentIterationNode() {
		this(Object.class);
	}
	
	public CurrentIterationNode(Class<?> clazz) {
		super(clazz);
		removeField(super.inputValueField);
	}

	@Override
	public void operate(OpContext context) throws ProcessingException {
		
	}

}
