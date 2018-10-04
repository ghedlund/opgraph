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
