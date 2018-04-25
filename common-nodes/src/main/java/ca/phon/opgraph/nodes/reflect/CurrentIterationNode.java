/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * This file is part of the OpGraph project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.phon.opgraph.nodes.reflect;

import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OpNodeInfo;
import ca.phon.opgraph.exceptions.ProcessingException;

@OpNodeInfo(
		name="Current Iteration",
		category="iteration",
		description="Current iteration value",
		showInLibrary=true
)
public class CurrentIterationNode extends ObjectNode {
	
	public CurrentIterationNode() {
		this(Object.class);
	}
	
	public CurrentIterationNode(Class<?> clazz) {
		super(clazz);
		removeField(super.inputValueField);
	}

	@Override
	public void operate(OpContext context) throws ProcessingException {
		
	}

}
