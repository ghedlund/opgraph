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
package ca.phon.opgraph.nodes.logic;

import ca.phon.opgraph.*;
import ca.phon.opgraph.exceptions.*;

/**
 * An {@link OpNode} that computes the logical negation of its input.
 */
@OpNodeInfo(
	name="Logical NOT",
	description="Computes the logical negation of a boolean input.",
	category="Logic"
)
public class LogicalNotNode extends OpNode {
	/** Input field for one of the two boolean values */
	public final static InputField X_INPUT_FIELD = new InputField("x", "boolean input", false, true, Boolean.class);

	/** Output field for the logical NOT of the input */
	public final static OutputField RESULT_OUTPUT_FIELD =  new OutputField("result", "not x", true, Boolean.class);

	/**
	 * Default constructor.
	 */
	public LogicalNotNode() {
		putField(X_INPUT_FIELD);
		putField(RESULT_OUTPUT_FIELD);
	}

	@Override
	public void operate(OpContext context) throws ProcessingException {
		boolean x = (Boolean)context.get(X_INPUT_FIELD);
		context.put(RESULT_OUTPUT_FIELD, !x);
	}
}
