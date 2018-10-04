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
package ca.phon.opgraph.library;

import java.net.URI;

import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.library.instantiators.Instantiator;

/**
 * Information about nodes registered with a node library.
 * 
 * @see NodeLibrary
 */
public class NodeData {
	/** The {@link URI} for this node descriptor */
	public final URI uri;

	/** The name of this node */
	public final String name;

	/** The description of this node */
	public final String description;

	/** The category of this node */
	public final String category;

	/** An instantiator for this node */
	public final Instantiator<? extends OpNode> instantiator;

	/**
	 * Constructs a node info.
	 * 
	 * @param uri  the URI for the node
	 * @param name  the name of the node
	 * @param description  the description of the node
	 * @param category  the category of the node
	 * @param instantiator  an instantiator for the node
	 */
	public NodeData(URI uri,
	                String name,
	                String description,
	                String category,
	                Instantiator<? extends OpNode> instantiator) 
	{
		this.uri = uri;
		this.name = name;
		this.description = description;
		this.category = category;
		this.instantiator = instantiator;
	}

	@Override
	public String toString() {
		return name;
	}
}
