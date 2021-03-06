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
package ca.phon.opgraph.nodes.reflect;

import ca.phon.opgraph.library.instantiators.*;

/**
 * Create new object nodes in the graph.  The class will prompt the user
 * to provide a class name.
 *
 */
public class ObjectNodeInstantiator implements Instantiator<ObjectNode> {

	@Override
	public ObjectNode newInstance(Object... params) throws InstantiationException {
		return null;
	}
	
}
