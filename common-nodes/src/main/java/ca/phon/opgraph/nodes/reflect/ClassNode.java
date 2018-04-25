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
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.exceptions.ProcessingException;

/**
 * Output the {@link Class} object for a given type.
 *
 */
public class ClassNode extends AbstractReflectNode {
	
	private OutputField classOutput = new OutputField("class", "Class object", true, Class.class);

	public ClassNode() {
		this(Object.class);
	}
	
	public ClassNode(Class<?> type) {
		super(type, null);
		putField(classOutput);
	}
	
	@Override
	public void operate(OpContext context) throws ProcessingException {
		context.put(classOutput, super.getDeclaredClass());
	}

}
