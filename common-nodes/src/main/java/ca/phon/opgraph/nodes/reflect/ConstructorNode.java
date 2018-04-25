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
