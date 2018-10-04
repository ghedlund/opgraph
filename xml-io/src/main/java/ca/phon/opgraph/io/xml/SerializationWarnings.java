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
package ca.phon.opgraph.io.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Container for a list of non-fatal exceptions which occured
 * during serialization of a graph.
 *
 */
public class SerializationWarnings implements Iterable<Throwable> {
	
	private final List<Throwable> thrown = new ArrayList<Throwable>();

	public int size() {
		return thrown.size();
	}

	public boolean contains(Object o) {
		return thrown.contains(o);
	}

	public Iterator<Throwable> iterator() {
		return thrown.iterator();
	}

	public boolean add(Throwable e) {
		return thrown.add(e);
	}

	public boolean remove(Object o) {
		return thrown.remove(o);
	}

	public boolean addAll(Collection<? extends Throwable> c) {
		return thrown.addAll(c);
	}

	public boolean removeAll(Collection<?> c) {
		return thrown.removeAll(c);
	}

	public void clear() {
		thrown.clear();
	}

	public Throwable get(int index) {
		return thrown.get(index);
	}

	public Throwable remove(int index) {
		return thrown.remove(index);
	}

}
