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
package ca.phon.opgraph.library;

/**
 * An interface for people that want to listen to node library events.
 */
public interface NodeLibraryListener {
	/**
	 * Called whenever a node is first registered with a node library.
	 * 
	 * @param info  the info for the registered node
	 */
	public abstract void nodeRegistered(NodeData info);

	/**
	 * Called whenever a node is first registered with a node library.
	 * 
	 * @param info  the info for the unregistered node
	 */
	public abstract void nodeUnregistered(NodeData info);
}
