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
package ca.phon.opgraph;

/**
 * A descriptor for an output field of an {@link OpNode} in
 * an {@link OpGraph}.
 * 
 * TODO how about if output type is not known till runtime (e.g., PassThroughNode)?
 *      Perhaps use <code>null</code> to specify this?
 */
public class OutputField extends SimpleItem {
	/** If an output field, the type of object this field outputs. */
	private Class<?> outputType; // XXX Maybe create a type system that can alleviate type erasure

	/** Whether or not this field is a fixed field. Fixed fields cannot be removed. */
	private boolean fixed;

	/**
	 * Constructs an output descriptor with a key, output type, and description.
	 * 
	 * @param key  the reference key
	 * @param description  a description for the field
	 * @param isFixed  whether or not this field is fixed
	 * @param outputType  the type of object this field outputs 
	 */
	public OutputField(String key, String description, boolean isFixed, Class<?> outputType) {
		super(key, description);
		this.fixed = isFixed;
		this.outputType = outputType;
	}

	/**
	 * Gets the type of output for this field.
	 * 
	 * @return the type of output for this field
	 */
	public Class<?> getOutputType() {
		return outputType;
	}

	/**
	 * Sets the type of output for this field.
	 * 
	 * @param outputType  the type of output for this field
	 */
	public void setOutputType(Class<?> outputType) {
		this.outputType = outputType;
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

	@Override
	public String toString() {
		return getKey();
	}
}
