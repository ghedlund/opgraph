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

import java.util.*;

import ca.phon.opgraph.extensions.*;

/**
 * A {@link ContextualItem} with only a key and description.
 */
public class SimpleItem implements ContextualItem, Extendable {
	/** The key for this field */
	private String key;

	/** The description for this field */
	private String description;

	/**
	 * Constructs a field with a key and empty description.
	 * 
	 * @param key  the key
	 */
	public SimpleItem(String key) {
		this(key, "");
	}

	/**
	 * Constructs a field with a key and empty description.
	 * 
	 * @param key  the key
	 * @param description  the description
	 */
	public SimpleItem(String key, String description) {
		setKey(key);
		setDescription(description);
	}

	//
	// Overrides
	//

	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;
		if(obj == null) return false;

		if(getClass() == obj.getClass())
			return key.equals( ((ContextualItem)obj).getKey() );

		return false;
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	//
	// ContextualItem
	//

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setKey(String key) {
		this.key = (key == null ? "" : key);
	}

	@Override
	public void setDescription(String description) {
		this.description = (description == null ? "" : description);
	}
	
	//
	// Extendable
	//

	private ExtendableSupport extendableSupport = new ExtendableSupport(SimpleItem.class);

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
