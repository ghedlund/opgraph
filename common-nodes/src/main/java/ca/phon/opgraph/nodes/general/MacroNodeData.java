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

import java.net.URI;
import java.net.URL;

import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.library.NodeData;
import ca.phon.opgraph.library.instantiators.Instantiator;

public class MacroNodeData extends NodeData {
	
	private URL graphURL;
	
	private boolean isGraphEmbedded = true;

	public MacroNodeData(URL analysisURL, URI uri, String name, String description, String category,
			Instantiator<? extends OpNode> instantiator, boolean isGraphEmbedded) {
		super(uri, name, description, category, instantiator);
		this.graphURL = analysisURL;
		this.isGraphEmbedded = isGraphEmbedded;
	}

	public URL getGraphURL() {
		return this.graphURL;
	}
	
	public void setGraphURL(URL url) {
		this.graphURL = url;
	}

	public boolean isGraphEmbedded() {
		return isGraphEmbedded;
	}

	public void setGraphEmbedded(boolean isGraphEmbedded) {
		this.isGraphEmbedded = isGraphEmbedded;
	}
	
}
