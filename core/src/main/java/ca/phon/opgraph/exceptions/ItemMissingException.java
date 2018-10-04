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
package ca.phon.opgraph.exceptions;

import ca.phon.opgraph.ContextualItem;

/**
 * An exception thrown whenever a given {@link ContextualItem} does not exist, but
 * is required in some context.
 */
public final class ItemMissingException extends Exception {
	/** The item that was missing */
	private ContextualItem item;

	/**
	 * Constructs exception with the given item that was missing.
	 * 
	 * @param item  the item that was missing
	 */
	public ItemMissingException(ContextualItem item) {
		this(item, "Contextual item with key '" + item.getKey() + "' missing");
	}

	/**
	 * Constructs exception with the given item that was missing, and a
	 * custom detail message for the exception.
	 * 
	 * @param item  the item that was missing
	 * @param message  the detail message
	 */
	public ItemMissingException(ContextualItem item, String message) {
		super(message);
		this.item = item;
	}

	/**
	 * Gets the {@link ContextualItem} that was missing.
	 * 
	 * @return  the contextual item
	 */
	public ContextualItem getItem() {
		return item;
	}
}
