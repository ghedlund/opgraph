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
package ca.phon.opgraph.nodes.random;

import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OpNodeInfo;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.exceptions.ProcessingException;

/**
 * A node that outputs a random number.
 */
@OpNodeInfo(
	name = "Random Integer",
	description = "Outputs random integers",
	category="Data Generation"
)
public class RandomIntegerNode extends OpNode {
	/** Input field for the minimum possible value of the random integer */
	public final InputField MIN_INPUT = new InputField("min", "smallest possible value (inclusive)", true, true, Number.class);

	/** Input field for the maximum possible value of the random integer */
	public final InputField MAX_INPUT = new InputField("max", "largest possible value (inclusive)", true, true, Number.class);

	/** Output field for the random integer */
	public final OutputField VALUE_OUTPUT = new OutputField("value", "random integer", true, Integer.class);

	/**
	 * Default constructor.
	 */
	public RandomIntegerNode() {
		putField(MIN_INPUT);
		putField(MAX_INPUT);
		putField(VALUE_OUTPUT);
	}

	//
	// OpNode
	//

	@Override
	public void operate(OpContext context) throws ProcessingException {
		final int MIN = (context.containsKey(MIN_INPUT) ? ((Number)context.get(MIN_INPUT)).intValue() : Integer.MIN_VALUE);
		final int MAX = (context.containsKey(MAX_INPUT) ? ((Number)context.get(MAX_INPUT)).intValue() : Integer.MAX_VALUE);
		final double t = Math.random();
		final int value = (int)((1 - t)*MIN + t*MAX);
		context.put(VALUE_OUTPUT, value);
	}
}
