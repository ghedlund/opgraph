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
package ca.phon.opgraph.exceptions;

import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.Processor;

/**
 * A general exception that can be thrown during the processing of an
 * {@link OpGraph}. The specifics that generated this exception
 * can be found in {@link #getCause()}.
 */
public class ProcessingException extends RuntimeException {
	
	private static final long serialVersionUID = -7700644850403543549L;

	private Processor context;
	
	/**
	 * Constructs a processing exception with a given detail message.
	 * 
	 * @param message  the detail message
	 */
	public ProcessingException(Processor context, String message) {
		super(message);
		this.context = context;
	}

	/**
	 * Constructs a processing exception with a given cause.
	 * 
	 * @param cause  the cause
	 */
	public ProcessingException(Processor context, Throwable cause) {
		super("An error occured during the processing of a graph", cause);
		this.context = context;
	}

	/**
	 * Constructs a processing exception with a given detail message and cause.
	 * 
	 * @param message  the detail message
	 * @param cause  the cause
	 */
	public ProcessingException(Processor context, String message, Throwable cause) {
		super(message, cause);
		this.context = context;
	}
	
	public Processor getContext() {
		return this.context;
	}
	
	public void setContext(Processor context) {
		this.context = context;
	}
	
}
