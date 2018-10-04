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
/**
 * 
 */
package ca.phon.opgraph.nodes.general.script;

import java.util.ArrayList;
import java.util.List;

import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.validators.ClassValidator;
import ca.phon.opgraph.validators.CompositeValidator;
import ca.phon.opgraph.validators.TypeValidator;

/**
 * Wrapper for an {@link ArrayList} of {@link InputField}s to simplify adding
 * input fields from a script.
 */
public class InputFields extends ArrayList<InputField> {
	/** The node which this class will add input fields to */
	private OpNode node;

	/**
	 * Constructs an input fields collection which adds input fields to a
	 * given node.
	 * 
	 * @param node  the node to add input fields to 
	 */
	public InputFields(OpNode node) {
		this.node = node;
	}

	/**
	 * Adds a non-optional, fixed input field with a key and
	 * description that accepts all types.
	 * 
	 * @param key  the reference key
	 * @param description  a description for the field
	 */
	public void add(String key, String description) {
		add(key, description, false, true);
	}

	/**
	 * Adds an input field with a key, description, and optionality that
	 * accepts all types.
	 * 
	 * @param key  the reference key
	 * @param description  a description for the field
	 * @param isOptional  whether or not this field is optional
	 * @param isFixed  whether or not this field is fixed
	 */
	public void add(
			String key,
			String description,
			boolean isOptional,
			boolean isFixed)
	{
		addWithValidator(key, description, isOptional, isFixed, (TypeValidator)null);
	}
	
	public void add(
			String key,
			String description,
			Class<?> acceptedType) 
	{
		addWithTypes(key, description, new Class[]{acceptedType});
	}

	/**
	 * Adds a non-optional, fixed input field with a key and description
	 * that accepts a specified list of classes.
	 * 
	 * @param key  the reference key
	 * @param description  a description for the field
	 * @param acceptedTypes  the classes that are accepted
	 */
	public void addWithTypes(
			String key,
			String description,
			Class<?>... acceptedTypes)
	{
		addWithValidators(key, description, false, true, acceptedTypes);
	}

	/**
	 * Constructs a non-optional, fixed input field with a key, description
	 * and validator list.
	 * 
	 * @param key  the reference key
	 * @param description  a description for the field
	 * @param validators  a {@link List} of {@link TypeValidator}s to use
	 *                    for validating incoming values
	 */
	public void addWithValidators(
			String key,
			String description,
			TypeValidator... validators)
	{
		addWithValidator(key, description, false, false, validators.length == 1 ? validators[0] : new CompositeValidator(validators));
	}

	/**
	 * Adds an input descriptor that accepts a specified list of classes.
	 * 
	 * @param key  the reference key
	 * @param description  a description for the field
	 * @param isOptional  whether or not this field is optional
	 * @param isFixed  whether or not this field is fixed
	 * @param acceptedTypes  the classes that are accepted
	 */
	public void addWithValidators(
			String key, 
			String description, 
			boolean isOptional, 
			boolean isFixed, 
			Class<?>... acceptedTypes)
	{
		addWithValidator(key, description, isOptional, isFixed, new ClassValidator(acceptedTypes));
	}

	/**
	 * Adds an input descriptor with a specified list of validators.
	 * 
	 * @param key  the reference key
	 * @param description  a description for the field
	 * @param isOptional  whether or not this field is optional
	 * @param isFixed  whether or not this field is fixed
	 * @param validators  a {@link List} of {@link TypeValidator}s to use
	 *                    for validating incoming values
	 */
	public void addWithValidator(
			String key,
			String description,
			boolean isOptional,
			boolean isFixed,
			TypeValidator validators)
	{
		node.putField(new InputField(key, description, isOptional, isFixed, validators));
	}
}
