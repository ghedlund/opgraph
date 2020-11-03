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

import java.util.regex.Pattern;

import ca.phon.opgraph.library.NodeData;

/**
 * A filter for {@link NodeData} instances.
 */
public abstract class NodeInfoFilter {
	/** The filter to use */
	protected String filter;

	/** The pattern for the filter */
	protected Pattern filterPattern;

	/**
	 * Sets the filter this renderer uses.
	 * 
	 * @param filter  the filter to set
	 */
	public void setFilter(String filter) {
		this.filter = filter;
		this.filterPattern = null;

		if(this.filter != null && this.filter.length() > 0)
			this.filterPattern = Pattern.compile(Pattern.quote(this.filter), Pattern.CASE_INSENSITIVE);
	}

	/**
	 * Gets whether or not this filter accepts a given {@link NodeData} instance.
	 * 
	 * @param info  the {@link NodeData} instance
	 * 
	 * @return <code>true</code> if this filter accepts the given instance,
	 *         <code>false</code> otherwise
	 */
	public abstract boolean isAccepted(NodeData info);
}
