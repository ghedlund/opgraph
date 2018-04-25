package ca.phon.opgraph.exceptions;

import ca.phon.opgraph.Processor;

public class NodeCanceledException extends ProcessingException {

	private static final long serialVersionUID = -1733662300755910234L;

	public NodeCanceledException(Processor context, String message, Throwable cause) {
		super(context, message, cause);
	}

	public NodeCanceledException(Processor context, String message) {
		super(context, message);
	}

	public NodeCanceledException(Processor context, Throwable cause) {
		super(context, cause);
	}

}
