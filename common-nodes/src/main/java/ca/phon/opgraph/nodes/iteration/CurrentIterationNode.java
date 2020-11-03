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
package ca.phon.opgraph.nodes.iteration;

import ca.phon.opgraph.OpNodeInfo;
import ca.phon.opgraph.nodes.general.GetContextValueNode;

/**
 * A node that outputs a constant value. 
 */
@OpNodeInfo(
	name="Current Iteration",
	description="Gets the current iteration number.",
	category="Iteration"
)
public class CurrentIterationNode extends GetContextValueNode {
	/**
	 * Default constructor.
	 */
	public CurrentIterationNode() {
		super(ForEachNode.CURRENT_ITERATION_KEY, Number.class);
	}
}
