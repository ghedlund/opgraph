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

import java.util.List;

import ca.phon.opgraph.*;

/**
 * A node that outputs a constant value. 
 */
@OpNodeInfo(
	name="Range",
	description="Outputs a range of integers.",
	category="Objects"
)
public class RangeNode extends OpNode {
	/** Input field for the start of the output range */
	public final InputField START_INPUT_FIELD = new InputField("start", "Start value of range", false, true, Number.class);

	/** Input field for the end of the output range */
	public final InputField END_INPUT_FIELD = new InputField("end", "End value of range", false, true, Number.class);

	/** Output field for the range */
	public final OutputField RANGE_OUTPUT_FIELD = new OutputField("range", "Range list", true, List.class);

	/**
	 * Default constructor.
	 */
	public RangeNode() {
		putField(START_INPUT_FIELD);
		putField(END_INPUT_FIELD);
		putField(RANGE_OUTPUT_FIELD);
	}

	@Override
	public void operate(OpContext context) {
		final int start = ((Number)context.get(START_INPUT_FIELD)).intValue();
		final int end = ((Number)context.get(END_INPUT_FIELD)).intValue();
		context.put(RANGE_OUTPUT_FIELD, new IntRangeList(start, end));
	}
}
