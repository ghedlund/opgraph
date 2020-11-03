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

import java.util.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.nodes.xml.*;

/**
 * An {@link MacroNode} extension containing a list of nodes. These nodes
 * must exist in the graph of the {@link MacroNode} and indicate to the 
 * {@link MacroNodeXMLSerializer} to save these nodes if {@link MacroNode#isGraphEmbedded()} is
 * <code>true</code>. When reading the OpGraph the saved nodes will be used in place of their
 * linked counterpart.
 *
 */
public class LinkedMacroNodeOverrides {
	
	private List<OpNode> nodeOverrides = new ArrayList<>();
	
	public List<OpNode> getNodeOverrides() {
		return this.nodeOverrides;
	}
	
}
