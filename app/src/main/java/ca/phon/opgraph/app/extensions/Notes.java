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
/**
 * 
 */
package ca.phon.opgraph.app.extensions;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ca.phon.opgraph.app.util.CollectionListener;
import ca.phon.opgraph.app.util.CollectionListenerSupport;

/**
 * An extension that stores a set of notes. Notes are stored in memory as
 * components so that they can be placed and manipulated in a GUI.
 */
public class Notes implements Iterable<Note> {
	/** The collection of notes */
	private Collection<Note> notes;

	/**
	 * Default constructor. 
	 */
	public Notes() {
		this.notes = new ArrayList<Note>();
	}

	/**
	 * Adds a note.
	 * 
	 * @param note  the note to add
	 */
	public void add(Note note) {
		notes.add(note);
		listenerSupport.fireElementAdded(this, note);
	}

	/**
	 * Removes a note.
	 * 
	 * @param note  the note to remove
	 */
	public void remove(Note note) {
		if(notes.contains(note)) {
			notes.remove(note);
			listenerSupport.fireElementRemoved(this, note);
		}
	}

	/**
	 * Gets the number of notes.
	 * 
	 * @return the number of notes
	 */
	public int size() {
		return notes.size();
	}

	//
	// Iterable<Notes.Note>
	//

	@Override
	public Iterator<Note> iterator() {
		return notes.iterator();
	}

	//
	// CollectionListener support
	//

	private final CollectionListenerSupport<Notes, Note> listenerSupport = new CollectionListenerSupport<Notes, Note>();

	/**
	 * Adds a collection listener.
	 * 
	 * @param listener  the listener
	 */
	public void addCollectionListener(CollectionListener<Notes, Note> listener) {
		listenerSupport.addCollectionListener(listener);
	}

	/**
	 * Removes a collection listener.
	 * 
	 * @param listener  the listener
	 */
	public void removeCollectionListener(CollectionListener<Notes, Note> listener) {
		listenerSupport.removeCollectionListener(listener);
	}
}
