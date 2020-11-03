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
/**
 * 
 */
package ca.phon.opgraph.nodes.general.script;

import java.util.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.validators.*;

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
	 * 
	 * @return created input field
	 */
	public InputField add(String key, String description) {
		return add(key, description, false, false);
	}

	/**
	 * Adds an input field with a key, description, and optionality that
	 * accepts all types.
	 * 
	 * @param key  the reference key
	 * @param description  a description for the field
	 * @param isOptional  whether or not this field is optional
	 * @param isFixed  whether or not this field is fixed
	 * 
	 * @return created input field
	 */
	public InputField add(
			String key,
			String description,
			boolean isOptional,
			boolean isFixed)
	{
		return addWithValidator(key, description, isOptional, isFixed, (TypeValidator)null);
	}
	
	public InputField add(
			String key,
			String description,
			Class<?> acceptedType) 
	{
		return addWithTypes(key, description, new Class[]{acceptedType});
	}

	/**
	 * Adds a non-optional, fixed input field with a key and description
	 * that accepts a specified list of classes.
	 * 
	 * @param key  the reference key
	 * @param description  a description for the field
	 * @param acceptedTypes  the classes that are accepted
	 * 
	 * @return created input field
	 */
	public InputField addWithTypes(
			String key,
			String description,
			Class<?>... acceptedTypes)
	{
		return addWithValidators(key, description, false, false, acceptedTypes);
	}

	/**
	 * Constructs a non-optional, fixed input field with a key, description
	 * and validator list.
	 * 
	 * @param key  the reference key
	 * @param description  a description for the field
	 * @param validators  a {@link List} of {@link TypeValidator}s to use
	 *                    for validating incoming values
	 *                    
	 * @return created input field
	 */
	public InputField addWithValidators(
			String key,
			String description,
			TypeValidator... validators)
	{
		return addWithValidator(key, description, false, false, validators.length == 1 ? validators[0] : new CompositeValidator(validators));
	}

	/**
	 * Adds an input descriptor that accepts a specified list of classes.
	 * 
	 * @param key  the reference key
	 * @param description  a description for the field
	 * @param isOptional  whether or not this field is optional
	 * @param isFixed  whether or not this field is fixed
	 * @param acceptedTypes  the classes that are accepted
	 * 
	 * 
	 * @return created input field
	 */
	public InputField addWithValidators(
			String key, 
			String description, 
			boolean isOptional, 
			boolean isFixed, 
			Class<?>... acceptedTypes)
	{
		return addWithValidator(key, description, isOptional, isFixed, new ClassValidator(acceptedTypes));
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
	 *                    
	 * @return created input field
	 */
	public InputField addWithValidator(
			String key,
			String description,
			boolean isOptional,
			boolean isFixed,
			TypeValidator validators)
	{
		InputField retVal = new InputField(key, description, isOptional, isFixed, validators);
		node.putField(retVal);
		return retVal;
	}
}
