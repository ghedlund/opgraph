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
package ca.phon.opgraph.exceptions;

import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.Processor;

/**
 * An exception that is thrown during the processing of an {@link OpGraph}
 * when a required input field is given no input.
 */
public final class RequiredInputException extends ProcessingException {

	private static final long serialVersionUID = -7354818113151303950L;

	/** The node from which the field comes from */
	private OpNode node;

	/** The field with required input */
	private InputField field;

	/**
	 * Constructs exception with the input field that required
	 * 
	 * @param node  the node containing the input field
	 * @param field  the input field descriptor
	 */
	public RequiredInputException(Processor context, OpNode node, InputField field) {
		super(context, "Required field '" + field.getKey() + "' in node '" + node.getId() + "' has no input");
		this.node = node;
		this.field = field;
	}

	/**
	 * Gets the node which contains the field that received no input.
	 * 
	 * @return  the node
	 */
	public OpNode getnode() {
		return node;
	}

	/**
	 * Gets the field that requires input, but received none.
	 * 
	 * @return  the input field descriptor
	 */
	public InputField getField() {
		return field;
	}
}
