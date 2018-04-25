package ca.phon.opgraph.exceptions;

import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.Processor;

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
