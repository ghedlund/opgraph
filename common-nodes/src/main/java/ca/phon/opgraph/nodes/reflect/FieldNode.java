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
import java.lang.reflect.Member;

import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.app.extensions.NodeSettings;
import ca.phon.opgraph.exceptions.ProcessingException;

/**
 * Provide access to a non-static {@link Field} for an instance
 * of the declared class.
 *
 */
public class FieldNode extends AbstractReflectNode {
	
	protected InputField objInputField;
	
	protected OutputField outputField;
	
	private Field field;
	
	public FieldNode() {
		super();
	}
	
	public FieldNode(Field field) {
		super();
		setClassMember(field);
	}
	
	@Override
	public void setClassMember(Member member) {
		if(member instanceof Field) {
			super.setDeclaredClass(member.getDeclaringClass());
			super.setClassMember(member);
			setField((Field)member);
		}
	}
	
	public void setField(Field field) {
		this.field = field;
		setName(field.getDeclaringClass().getSimpleName() + "." + field.getName());
		
		objInputField = new InputField("obj", "", field.getDeclaringClass());
		putField(objInputField);
		
		outputField = new OutputField("value", "", true, field.getType());
		putField(outputField);
	}

	@Override
	public void operate(OpContext context) throws ProcessingException {
		final Object instance = context.get(objInputField);
		
		try {
			final Object outputVal = field.get(instance);
			context.put(outputField, outputVal);
		} catch (IllegalArgumentException e) {
			throw new ProcessingException(null, e);
		} catch (IllegalAccessException e) {
			throw new ProcessingException(null, e);
		}
	}

}
