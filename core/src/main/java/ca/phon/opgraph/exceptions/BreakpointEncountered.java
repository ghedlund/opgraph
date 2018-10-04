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
package ca.phon.opgraph.exceptions;

import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.Processor;

public class BreakpointEncountered extends ProcessingException {

	private static final long serialVersionUID = 8372381337826551335L;
	
	public final OpNode node;
	
	public BreakpointEncountered(Processor context, OpNode node) {
		super(context, "Breakpoint encountered @" + node.getId() + "(" + node.getName() + ")");
		this.node = node;
	}

	public OpNode getNode() {
		return node;
	}
	
}
