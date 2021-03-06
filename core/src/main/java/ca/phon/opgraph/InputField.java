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
package ca.phon.opgraph;

import java.util.*;

import ca.phon.opgraph.validators.*;

/**
 * A descriptor for an input field of an {@link OpNode} in an {@link OpGraph}.
 */
public class InputField extends SimpleItem {
	/** An instance of a type validator, or <code>null</code> if this field accepts anything */
	private TypeValidator validator;

	/** Whether or not this field is optional */
	private boolean optional;

	/** Whether or not this field is a fixed field. Fixed fields cannot be removed. */
	private boolean fixed;

	/**
	 * Constructs a non-optional, fixed input field with a key and
	 * description that accepts all types.
	 * 
	 * @param key  the reference key
	 * @param description  a description for the field
	 */
	public InputField(String key, String description) {
		this(key, description, false, true);
	}

	/**
	 * Constructs an input field with a key, description, and optionality. The
	 * field that accepts all incoming types.
	 * 
	 * @param key  the reference key
	 * @param description  a description for the field
	 * @param isOptional  whether or not this field is optional
	 * @param isFixed  whether or not this field is fixed
	 */
	public InputField(
			String key,
			String description,
			boolean isOptional,
			boolean isFixed)
	{
		this(key, description, isOptional, isFixed, (TypeValidator)null);
	}

	/**
	 * Constructs a non-optional, fixed input field with a key and description
	 * that accepts a specified list of classes.
	 * 
	 * @param key  the reference key
	 * @param description  a description for the field
	 * @param acceptedTypes  the classes that are accepted
	 */
	public InputField(
			String key,
			String description,
			Class<?>... acceptedTypes)
	{
		this(key, description, false, true, acceptedTypes);
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
	public InputField(
			String key,
			String description,
			TypeValidator... validators)
	{
		this(key, description, false, false, validators);
	}

	/**
	 * Constructs an input descriptor that accepts a specified list of classes.
	 * 
	 * @param key  the reference key
	 * @param description  a description for the field
	 * @param isOptional  whether or not this field is optional
	 * @param isFixed  whether or not this field is fixed
	 * @param acceptedTypes  the classes that are accepted
	 */
	public InputField(
			String key, 
			String description, 
			boolean isOptional, 
			boolean isFixed, 
			Class<?>... acceptedTypes)
	{
		this(key, description, isOptional, isFixed, new ClassValidator(acceptedTypes));
	}

	/**
	 * Constructs an input descriptor with a specified list of validators.
	 * 
	 * @param key  the reference key
	 * @param description  a description for the field
	 * @param isOptional  whether or not this field is optional
	 * @param isFixed  whether or not this field is fixed
	 * @param validators  a {@link List} of {@link TypeValidator}s to use
	 *                    for validating incoming values
	 */
	public InputField(
			String key,
			String description,
			boolean isOptional,
			boolean isFixed,
			TypeValidator... validators)
	{
		super(key, description);
		this.optional = isOptional;
		this.fixed = isFixed;
		if(validators.length > 0)
			this.validator = (validators.length == 1 ? validators[0] : new CompositeValidator(validators));
	}

	/**
	 * Gets the type validator for this field.
	 * 
	 * @return the type validator. If <code>null</code>, this field will
	 *         accept any object. 
	 */
	public TypeValidator getValidator() {
		return validator;
	}

	/**
	 * Sets the type validator for this field.
	 * 
	 * @param validator  the type validator. If <code>null</code>, this field
	 *                   will accept any object.
	 */
	public void setValidator(TypeValidator validator) {
		this.validator = validator;
	}

	/**
	 * Gets whether or not this is a fixed field. Fixed fields cannot be removed.
	 * 
	 * @return <code>true</code> if a fixed field, <code>false</code> otherwise
	 */
	public boolean isFixed() {
		return fixed;
	}

	/**
	 * Sets whether or not this is a fixed field. Fixed fields cannot be removed.
	 * 
	 * @param fixed  <code>true</code> if a fixed field, <code>false</code> otherwise
	 */
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	/**
	 * Gets whether or not this field is an optional input field.
	 * 
	 * @return <code>true</code> if this is an optional field, <code>false</code> otherwise
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * Sets whether or not this field is optional.
	 * 
	 * @param optional  <code>true</code> if an optional field, <code>false</code> otherwise
	 */
	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	//
	// Overrides
	//

	@Override
	public String toString() {
		return getKey();
	}
}
