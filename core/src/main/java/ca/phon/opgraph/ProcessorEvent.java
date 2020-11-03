/*
 * Copyright (C) 2012-2020 Gregory Hedlund <https://www.phon.ca>
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
