package ca.phon.opgraph;

/**
 * Event type for {@link Processor} events.
 */
public class ProcessorEvent {
	
	public static enum Type {
		BEGIN_NODE,
		FINISH_NODE,
		COMPLETE
	};
	
	private Type type;
	
	private Processor processor;
	
	private OpNode node;
	
	public ProcessorEvent() {
		
	}

	public ProcessorEvent(Type type, Processor processor) {
		super();
		this.type = type;
		this.processor = processor;
	}
	
	public ProcessorEvent(Type type, Processor processor, OpNode node) {
		super();
		this.type = type;
		this.processor = processor;
		this.node = node;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Processor getProcessor() {
		return processor;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public OpNode getNode() {
		return node;
	}

	public void setNode(OpNode node) {
		this.node = node;
	}
	
}
