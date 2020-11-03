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
package ca.phon.opgraph.nodes.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.app.extensions.NodeSettings;
import ca.phon.opgraph.exceptions.ProcessingException;
import ca.phon.opgraph.util.ReflectUtil;

/**
 * Node for object constructors.
 */
public class ConstructorNode extends AbstractReflectNode {
	
	private final List<InputField> argFields = new ArrayList<InputField>();
	
	private OutputField outputField;
	
	public ConstructorNode() {
		super();
	}
	
	public ConstructorNode(Constructor<?> constructor) {
		super();
		setClassMember(constructor);
	}
	
	@Override
	public void setClassMember(Member member) {
		if(member instanceof Constructor) {
			setConstructor((Constructor<?>)member);
		}
	}
	
	public void setConstructor(Constructor<?> constructor) {
		super.setDeclaredClass(constructor.getDeclaringClass());
		super.setClassMember(constructor);
		setName(constructor.getDeclaringClass().getSimpleName() + "#" + ReflectUtil.getSignature(constructor, false));
		
		final Class<?> type = constructor.getDeclaringClass();
		outputField = new OutputField("value", "constructor", true, type);
		putField(outputField);
		
		argFields.clear();
		for(int i = 0; i < constructor.getParameterTypes().length; i++) {
			final Class<?> paramType = constructor.getParameterTypes()[i];
			final InputField inputField = new InputField("arg" + (i+1), "", paramType);
			inputField.setOptional(true);
			putField(inputField);
			argFields.add(inputField);
		}
	}
	
	public Constructor<?> getConstructor() {
		final Member member = getClassMember();
		Constructor<?> retVal = null;
		if(member instanceof Constructor) {
			retVal = (Constructor<?>)member;
		}
		return retVal;
	}

	@Override
	public void operate(OpContext context) throws ProcessingException {
		final Constructor<?> constructor = getConstructor();

		final Object[] args = new Object[argFields.size()];
		for(int i = 0; i < argFields.size(); i++) {
			final InputField inputField = argFields.get(i);
			args[0] = context.get(inputField);
		}
		
		Object val = null;
		try {
			if(constructor.getParameterTypes().length > 0)
				val = constructor.newInstance(args);
			else
				val = constructor.newInstance();
		} catch (IllegalArgumentException e) {
			throw new ProcessingException(null, e);
		} catch (InstantiationException e) {
			throw new ProcessingException(null, e);
		} catch (IllegalAccessException e) {
			throw new ProcessingException(null, e);
		} catch (InvocationTargetException e) {
			throw new ProcessingException(null, e);
		}
		context.put(outputField.getKey(), val);
	}
	
}
