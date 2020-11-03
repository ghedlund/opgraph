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
/**
 * 
 */
package ca.phon.opgraph.app.components.library;

import ca.phon.opgraph.library.NodeData;

/**
 * A {@link NodeInfoFilter} that filters on {@link NodeData#name},
 * {@link NodeData#description} and {@link NodeData#category}.
 */
public class NodeInfoFullTextFilter extends NodeInfoFilter {
	@Override
	public boolean isAccepted(NodeData info) {
		if(filterPattern == null)
			return true;

		if(info != null) {
			final String category = (info.category.length() == 0 ? "General" : info.category);
			return ((info.name != null && filterPattern.matcher(info.name).find())
			        || (info.description != null && filterPattern.matcher(info.description).find())
			        || filterPattern.matcher(category).find()
			        || (info.uri != null && filterPattern.matcher(info.uri.toString()).find()) );
		}

		return false;
	}
}
