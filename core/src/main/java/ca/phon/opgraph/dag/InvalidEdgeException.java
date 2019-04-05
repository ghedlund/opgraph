package ca.phon.opgraph.dag;

import ca.phon.opgraph.OpLink;

public class InvalidEdgeException extends Exception {
	
	private OpLink invalidLink;

	public InvalidEdgeException(OpLink link) {
		this("A cycle was detected", link);
	}

	/**
	 * Constructs this exception with a given detail message.
	 * 
	 * @param message  the detail message
	 */
	public InvalidEdgeException(String message, OpLink link) {
		super(message);
	}
	
}
