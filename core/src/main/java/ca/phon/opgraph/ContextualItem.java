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

/**
 * A descriptor for a input/output field in an {@link OpNode}.
 */
public interface ContextualItem {
	/**
	 * Gets the reference key for the field that this descriptor describes.
	 * 
	 * @return the reference key
	 */
	public abstract String getKey();

	/**
	 * Sets the reference key for the field that this descriptor describes.
	 * 
	 * @param key  the reference key
	 */
	public abstract void setKey(String key);

	/**
	 * Gets the description for the field that this descriptor describes.
	 * 
	 * @return the description
	 */
	public abstract String getDescription();

	/**
	 * Sets the description for the field that this descriptor describes.
	 * 
	 * @param description  the description
	 */
	public abstract void setDescription(String description);

}
