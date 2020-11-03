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
package ca.phon.opgraph.nodes.random;

import java.math.BigInteger;
import java.security.SecureRandom;

import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OpNodeInfo;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.exceptions.ProcessingException;

/**
 * A node that outputs a random string.
 */
@OpNodeInfo(
	name = "Random String",
	description = "Outputs random strings",
	category="Data Generation"
)
public class RandomStringNode extends OpNode {
	/** Input field for the output string's length */
	public final InputField LENGTH_INPUT = new InputField("length", "string length", false, true, Number.class);

	/** Output field for the random string */
	public final OutputField VALUE_OUTPUT = new OutputField("value", "random string", true, String.class);

	/** Random number generator to use */
	private SecureRandom random = new SecureRandom();

	/** 
	 * Default constructor 
	 */
	public RandomStringNode() {
		putField(LENGTH_INPUT);
		putField(VALUE_OUTPUT);
	}

	//
	// OpNode
	//

	@Override
	public void operate(OpContext context) throws ProcessingException {
		final int length = ((Number)context.get(LENGTH_INPUT)).intValue();
		context.put(VALUE_OUTPUT, (new BigInteger(6*length, random)).toString(32).substring(0, length));
	}
}
