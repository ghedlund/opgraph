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
import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.Processor;

/**
 * An exception that is thrown during the processing of an {@link OpGraph}
 * when a given input value isn't accepted by an {@link InputField}. 
 */
public final class InvalidTypeException extends ProcessingException {
	
	private static final long serialVersionUID = -8536724095510372155L;

	/** The field given bad input*/
	private InputField field;

	/** The given input */
	private Object value;

	/**
	 * Constructs an exception with the input field that did not accept
	 * the given object.
	 * 
	 * @param field  the input field descriptor
	 * @param value  the value that was not accepted by the given field
	 */
	public InvalidTypeException(Processor context, InputField field, Object value) {
		super(context, "Field '" + field.getKey() + "' doesn't accept value '" + value + "'");
		this.field = field;
		this.value = value;
	}

	/**
	 * Gets the field that requires input, but received none.
	 * 
	 * @return  the input field descriptor
	 */
	public InputField getField() {
		return field;
	}

	/**
	 * Gets the value the input field was given
	 * 
	 * @return  the value
	 */
	public Object getValue() {
		return value;
	}
}
