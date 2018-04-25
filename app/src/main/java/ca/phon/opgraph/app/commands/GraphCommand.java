package ca.phon.opgraph.app.commands;

import ca.phon.opgraph.app.GraphDocument;

public abstract class GraphCommand extends HookableCommand {
	
	protected final GraphDocument document;

	public GraphCommand(GraphDocument document) {
		super();
		
		this.document = document;
	}
	
	public GraphCommand(String txt, GraphDocument document) {
		super(txt);
		
		this.document = document;
	}
	
	public GraphDocument getDocument() {
		return this.document;
	}

}
