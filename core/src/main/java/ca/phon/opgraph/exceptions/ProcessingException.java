/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
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
