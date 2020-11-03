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

import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OpNodeInfo;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.exceptions.ProcessingException;

/**
 * A node that outputs a random boolean.
 */
@OpNodeInfo(
	name = "Random Boolean",
	description = "Outputs random booleans",
	category="Data Generation"
)
public class RandomBooleanNode extends OpNode {
	/** Output field for the random boolean */
	public final OutputField VALUE_OUTPUT = new OutputField("value", "random boolean", true, Boolean.class);

	/**
	 * Default constructor
	 */
	public RandomBooleanNode() {
		putField(VALUE_OUTPUT);
	}

	//
	// OpNode
	//

	@Override
	public void operate(OpContext context) throws ProcessingException {
		context.put(VALUE_OUTPUT, Math.random() < 0.5f);
	}
}
