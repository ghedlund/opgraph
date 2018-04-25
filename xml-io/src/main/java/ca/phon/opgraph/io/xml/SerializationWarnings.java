/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * This file is part of the OpGraph project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
