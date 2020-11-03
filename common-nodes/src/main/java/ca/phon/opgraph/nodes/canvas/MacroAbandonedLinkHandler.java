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
package ca.phon.opgraph.nodes.canvas;

import java.awt.*;
import java.util.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.components.canvas.*;

public class MacroAbandonedLinkHandler implements AbandonedLinkHandler {

	public MacroAbandonedLinkHandler() {
	}

	@Override
	public void dragLinkAbandoned(GraphCanvas canvas, CanvasNode sourceNode, CanvasNodeField sourceField, Point p) {
		final Map<OpNode, CanvasNode> nodeMap = canvas.getNodeMap();
		for(CanvasNode canvasNode:nodeMap.values()) {
			if(canvasNode.contains(p)) {
				
			}
		}
	}

}
