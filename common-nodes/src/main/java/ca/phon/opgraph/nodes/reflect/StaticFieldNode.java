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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class StaticFieldNode extends FieldNode {

	public StaticFieldNode() {
		super();
	}

	public StaticFieldNode(Field field) {
		super(field);
	}

	@Override
	public void setField(Field field) {
		if(!Modifier.isStatic(field.getModifiers()))
			throw new IllegalArgumentException("Field must be static");
		super.setField(field);
		super.removeField(objInputField);
	}
	
}
