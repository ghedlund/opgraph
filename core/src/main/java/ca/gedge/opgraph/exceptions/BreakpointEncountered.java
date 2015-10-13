package ca.gedge.opgraph.exceptions;

import ca.gedge.opgraph.OpGraph;
import ca.gedge.opgraph.OpNode;
import ca.gedge.opgraph.Processor;

public class BreakpointEncountered extends ProcessingException {

	private static final long serialVersionUID = 8372381337826551335L;
	
	public final OpNode node;
	
	public BreakpointEncountered(Processor context, OpNode node) {
		super(context, "Breakpoint encountered @" + node.getId() + "(" + node.getName() + ")");
		this.node = node;
	}

	public OpNode getNode() {
		return node;
	}
	
}
