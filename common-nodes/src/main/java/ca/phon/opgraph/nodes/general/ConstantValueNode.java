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
package ca.phon.opgraph.nodes.general;

import ca.phon.opgraph.*;

/**
 * A node that outputs a constant value. 
 */
@OpNodeInfo(
	name="Constant Value",
	description="Outputs a constant value.",
	category="Objects"
)
public class ConstantValueNode extends OpNode {
	/** Output field for the constant value */
	public final OutputField VALUE_OUTPUT_FIELD = new OutputField("value", "Constant value", true, Object.class);

	/** The value of this node */
	private Object value;

	/**
	 * Constructs a constant value node with a <code>null</code> value.
	 */
	public ConstantValueNode() {
		this(null);
	}

	/**
	 * Constructs a constant value node with the specified value.
	 * 
	 * @param value  the constant value
	 */
	public ConstantValueNode(Object value) {
		putField(VALUE_OUTPUT_FIELD);
		setValue(value);
	}

	/**
	 * Sets the value in this node.
	 * 
	 * @param value  the value
	 */
	public void setValue(Object value) {
		this.value = value;
		VALUE_OUTPUT_FIELD.setOutputType(value == null ? Object.class : value.getClass());
	}
	
	public Object getValue() {
		return value;
	}

	//
	// Overrides
	//
	@Override
	public void operate(OpContext context) {
		if(context.isActive(VALUE_OUTPUT_FIELD))
			context.put(VALUE_OUTPUT_FIELD, getValue());
	}
	
}
