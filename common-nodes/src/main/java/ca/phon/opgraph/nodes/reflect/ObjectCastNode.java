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

import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.exceptions.InvalidTypeException;
import ca.phon.opgraph.exceptions.ProcessingException;

/**
 * Node for casting instances of an object to the type
 * specified by the declared class.
 */
public class ObjectCastNode extends AbstractReflectNode {
	
	private InputField inputField;
	
	private OutputField outputField;

	public ObjectCastNode() {
		super();
	}

	public ObjectCastNode(Class<?> declaredClass) {
		super(declaredClass, null);
	}

	@Override
	public void setDeclaredClass(Class<?> declaredClass) {
		super.setDeclaredClass(declaredClass);
		
		inputField = new InputField("obj", "object instance to cast", Object.class);
		inputField.setOptional(false);
		putField(inputField);
		
		outputField = new OutputField("obj", "object as casted instance", true, declaredClass);
		putField(outputField);
	}

	@Override
	public void operate(OpContext context) throws ProcessingException {
		final Object obj = context.get(inputField);
		if(obj == null)
			throw new ProcessingException(null, new NullPointerException());
		if(!getDeclaredClass().isInstance(obj)) 
			throw new InvalidTypeException(null, inputField, obj);
		
		context.put(outputField, getDeclaredClass().cast(obj));
	}

}
