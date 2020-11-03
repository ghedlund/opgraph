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
package ca.phon.opgraph.app.util;

/**
 * A listener interface for collection-oriented events.
 * 
 * @param <P> the type of the collection element
 * @param <E> the type of element stored in the collection
 */
public interface CollectionListener<P, E> {
	/**
	 * Called when an element was added to a collection.
	 *  
	 * @param source  the source object to which this element was added
	 * @param element  the element that was added
	 */
	public abstract void elementAdded(P source, E element);

	/**
	 * Called when an element was removed from a collection.
	 * 
	 * @param source  the source object from which this element was removed
	 * @param element  the element that was removed
	 */
	public abstract void elementRemoved(P source, E element);
}
