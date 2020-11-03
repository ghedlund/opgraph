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
package ca.phon.opgraph.nodes.general;

import ca.phon.opgraph.*;
import ca.phon.opgraph.exceptions.*;

/**
 * An {@link OpNode} that takes a value and outputs the same value.
 * Although this sounds useless, it is useful in cases where we need to
 * reuse the same value, in some manner, without creating a cycle.
 */
@OpNodeInfo(
	name="Pass-Through",
	description="A node that takes a value and outputs the same value. If no value is " +
	            "given as input, a null value is output.",
	category="General"
)
public class PassThroughNode extends OpNode {
	/** Input field for the value */
	public final static InputField INPUT = new InputField("input", "input value", true, true);

	/** Output field for the value*/
	public final static OutputField OUTPUT = new OutputField("output", "output value", true, Object.class);

	/**
	 * Default constructor.
	 */
	public PassThroughNode() {
		putField(INPUT);
		putField(OUTPUT);
	}

	@Override
	public void operate(OpContext context) throws ProcessingException {
		context.put(OUTPUT, context.get(INPUT));
	}
}
