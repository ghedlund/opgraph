package ca.phon.opgraph.nodes.reflect;

import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OpNodeInfo;
import ca.phon.opgraph.exceptions.ProcessingException;

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
