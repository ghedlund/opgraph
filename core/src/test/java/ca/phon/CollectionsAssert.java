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
package ca.phon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

/**
 * A set of assertions that make comparing equality of collections and
 * arrays of objects simpler.
 */
public class CollectionsAssert extends Assert {
	protected CollectionsAssert() {}

	/**
	 * Assert if a collection of objects is equal to the given array. Order
	 * is insignificant in this comparison.
	 * 
	 * @param <T>  the type of elements contained in the collection
	 * 
	 * @param message  the message to display upon failure
	 * @param a  the collection of objects
	 * @param b  the array of objects
	 */
	public static <T> void assertCollectionEqualsArray(String message, Collection<T> a, T... b) {
		// Quick check to ensure sizes equal 
		if(a.size() != b.length)
			fail(message);

		// Go through all items in collection, and remove them from the list
		final List<T> listB = new ArrayList<T>(Arrays.asList(b));
		for(T value : a) {
			// If this item could not be removed from the list, the
			// collection and array are not equal
			if(!listB.remove(value))
				fail(message);
		}
	}

	/**
	 * Assert if a collection of objects is equal to the given array. Order
	 * is insignificant in this comparison. A default message is used.
	 * 
	 * @param <T>  the type of elements contained in the collection
	 * 
	 * @param a  the collection of objects
	 * @param b  the array of objects
	 * 
	 * @see #assertCollectionEqualsArray(String, Collection, Object...)
	 */
	public static <T> void assertCollectionEqualsArray(Collection<T> a, T... b) {
		assertCollectionEqualsArray("Collection and array not equal", a, b);
	}
}
