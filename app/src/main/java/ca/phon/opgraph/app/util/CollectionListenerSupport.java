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

import java.util.*;

/**
 * Support for collection-oriented classes that provide the listening
 * capabilities of {@link CollectionListener}.
 * 
 * @param <P> the type of the collection element
 * @param <E> the type of element stored in the collection
 */
public class CollectionListenerSupport<P, E> {
	private ArrayList<CollectionListener<P, E>> listeners = new ArrayList<CollectionListener<P, E>>();

	/**
	 * Adds a collection listener.
	 * 
	 * @param listener  the listener
	 */
	public void addCollectionListener(CollectionListener<P, E> listener) {
		synchronized(listeners) {
			listeners.add(listener);
		}
	}

	/**
	 * Removes a collection listener.
	 * 
	 * @param listener  the listener
	 */
	public void removeCollectionListener(CollectionListener<P, E> listener) {
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}

	/**
	 * Fires an {@link CollectionListener#elementAdded(Object, Object)} event to all listeners.
	 * 
	 * @param source  the source collection
	 * @param element  the element that was added
	 */
	public void fireElementAdded(P source, E element) {
		synchronized(listeners) {
			for(CollectionListener<P, E> listener : listeners)
				listener.elementAdded(source ,element);
		}
	}

	/**
	 * Fires an {@link CollectionListener#elementRemoved(Object, Object)} event to all listeners.
	 * 
	 * @param source  the source collection
	 * @param element  the element that was removed
	 */
	public void fireElementRemoved(P source, E element) {
		synchronized(listeners) {
			for(CollectionListener<P, E> listener : listeners)
				listener.elementRemoved(source, element);
		}
	}
}
