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
package ca.phon.opgraph.app.edits.graph;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.undo.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.util.*;

public class AlignNodesEdit extends CompoundEdit {
	
	private static final long serialVersionUID = -1354846148677787171L;

	private Collection<OpNode> nodes;
	
	private int side;
	
	/**
	 * 
	 * @param graph
	 * @param side one of SwingConstants.TOP/BOTTOM/LEFT/RIGHT
	 */
	public AlignNodesEdit(Collection<OpNode> nodes, int side) {
		super();
		
		this.nodes = nodes;
		this.side = side;
		
		alignSelectedNodes();
	}
	

	public void alignSelectedNodes() {
		final Rectangle boundingRect = GraphUtils.getBoundingRect(nodes);
		
		for(OpNode node:nodes) {
			final JComponent comp = node.getExtension(JComponent.class);
			Dimension prefSize = comp.getPreferredSize();

			int deltaX = 0;
			int deltaY = 0;
			switch(side) {
			case SwingConstants.TOP:
				// adjust y values only
				deltaY = (int)Math.round(boundingRect.getY() - comp.getY());
				deltaX = 0;
				break;
				
			case SwingConstants.BOTTOM:
				deltaY = (int)Math.round(boundingRect.getMaxY() - comp.getY() - prefSize.getHeight());
				deltaX = 0;
				break;
			
			case SwingConstants.LEFT:
				deltaY = 0;
				deltaX = (int)Math.round(boundingRect.getX() - comp.getX());
				break;
				
			case SwingConstants.RIGHT:
				deltaY = 0;
				deltaX = (int)Math.round(boundingRect.getMaxX() - comp.getX() - prefSize.getWidth());
				break;
				
			default:
				break;
			}
			addEdit(new MoveNodesEdit(Collections.singleton(node), deltaX, deltaY));
		}
		
		end();
	}
	
	@Override
	public String getPresentationName() {
		return "Align nodes";
	}

}
