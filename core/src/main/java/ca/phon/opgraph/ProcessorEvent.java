/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * This file is part of the OpGraph project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
