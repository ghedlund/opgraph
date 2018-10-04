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

import java.util.Collection;

import javax.swing.JComponent;

import ca.phon.opgraph.extensions.*;

/**
 * A note, containing a textual title and body. Also contains display info
 * such as color, location, and size.
 */
public class Note implements Extendable {
	/** The title string */
	private String title;

	/** The body string */
	private String body;

	/**
	 * Constructs a note with a given title, body, and color.
	 * 
	 * @param title  the title
	 * @param body  the body
	 */
	public Note(String title, String body) {
		this.title = title;
		this.body = body;
		
		this.extendableSupport.putExtension(JComponent.class, new NoteComponent(this));
	}

	/**
	 * Gets the note's title.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the note's title.
	 * 
	 * @param title  the title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the note's body text.
	 * 
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Sets the note's body text.
	 * 
	 * @param body  the body text
	 */
	public void setBody(String body) {
		this.body = body;
	}

	//
	// Extendable
	//

	private final ExtendableSupport extendableSupport = new ExtendableSupport(Note.class);

	@Override
	public <T> T getExtension(Class<T> type) {
		return extendableSupport.getExtension(type);
	}

	@Override
	public Collection<Class<?>> getExtensionClasses() {
		return extendableSupport.getExtensionClasses();
	}

	@Override
	public <T> T putExtension(Class<T> type, T extension) {
		return extendableSupport.putExtension(type, extension);
	}
}
