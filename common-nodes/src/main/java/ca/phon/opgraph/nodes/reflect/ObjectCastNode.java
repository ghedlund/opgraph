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
