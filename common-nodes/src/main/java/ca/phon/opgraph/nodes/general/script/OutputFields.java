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
/**
 * 
 */
package ca.phon.opgraph.nodes.general.script;

import java.util.ArrayList;

import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OutputField;

/**
 * Wrapper for an {@link ArrayList} of {@link OutputField}s to simplify adding
 * output fields from a script.
 */
public class OutputFields extends ArrayList<OutputField> {
	/** The node which this class will add output fields to */
	private OpNode node;

	/**
	 * Constructs an output fields collection which adds output fields to a
	 * given node.
	 * 
	 * @param node  the node to add input fields to 
	 */
	public OutputFields(OpNode node) {
		this.node = node;
	}

	/**
	 * Adds an output descriptor with a key, output type, and description.
	 * 
	 * @param key  the reference key
	 * @param description  a description for the field
	 * @param isFixed  whether or not this field is fixed
	 * @param outputType  the type of object this field outputs 
	 * 
	 * @return created output field 
	 */
	public OutputField add(String key, String description, boolean isFixed, Class<?> outputType) {
		OutputField retVal = new OutputField(key, description, isFixed, outputType);
		node.putField(retVal);
		return retVal;
	}
	
}
