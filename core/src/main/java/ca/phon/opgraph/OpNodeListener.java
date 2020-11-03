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
package ca.phon.opgraph;

/**
 * A listener for {@link OpNode}.
 */
public interface OpNodeListener {
	/**
	 * Called when a basic property is changed on the node.
	 * 
	 * @param propertyName  the name of the property
	 * @param oldValue  the old value of the property
	 * @param newValue  the new value of the property
	 */
	public abstract void nodePropertyChanged(OpNode node, String propertyName, Object oldValue, Object newValue);

	/**
	 * Called when an input field was added to a node.
	 *  
	 * @param node  the source node to which the input field was added
	 * @param field  the input field that was added
	 */
	public abstract void fieldAdded(OpNode node, InputField field);

	/**
	 * Called when an input field was removed from a node.
	 * 
	 * @param node  the source node from which the input field was removed
	 * @param field  the input field that was removed
	 */
	public abstract void fieldRemoved(OpNode node, InputField field);

	/**
	 * Called when an output field was added to a node.
	 *  
	 * @param node  the source node to which the output field was added
	 * @param field  the output field that was added
	 */
	public abstract void fieldAdded(OpNode node, OutputField field);

	/**
	 * Called when an output field was removed from a node.
	 * 
	 * @param node  the source node from which the output field was removed
	 * @param field  the output field that was removed
	 */
	public abstract void fieldRemoved(OpNode node, OutputField field);
}
