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
package ca.phon.opgraph.nodes.general;

import ca.phon.opgraph.*;

/**
 * A node that fetches a value from the local context.
 */
@OpNodeInfo(
	name="Get Context Value",
	description="Gets a value from the local context."
)
public abstract class GetContextValueNode extends OpNode {
	/** The value output field for this node */
	private OutputField outputField;

	/** The key of the context value that will be fetched */
	private String key;

	/**
	 * Constructs a node that will grab a context value with a specified key.
	 * 
	 * @param key  the key of the item we'll be fetching
	 * @param valueType  the type of value this key references
	 */
	public GetContextValueNode(String key, Class<?> valueType) {
		this.key = key;
		putField(outputField = new OutputField("value", "context value", true, valueType));
	}

	//
	// Overrides
	//

	@Override
	public void operate(OpContext context) {
		context.put(outputField, context.get(key));
	}
}
