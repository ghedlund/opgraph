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
package ca.phon.opgraph.app.edits.graph;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.undo.CompoundEdit;

import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.app.util.GraphUtils;

/**
 * Distribute nodes in a graph evenly in the horizontal
 * or vertical direction.
 *
 */
public class DistributeNodesEdit extends CompoundEdit {

	private static final long serialVersionUID = -3988867961666736010L;
	
	private Collection<OpNode> nodes;
	
	private int direction;
	
	// space between nodes
	private final static int SPACE = 15;
	
	/**
	 * Constructor
	 * 
	 * @param nodes
	 * @param direction one of <code>SwingConstants.HORIZONTAL</code>
	 *  or <code>SwingConstants.VERTICAL</code>
 	 */
	public DistributeNodesEdit(Collection<OpNode> nodes, int direction) {
		super();
		
		this.nodes = nodes;
		this.direction = direction;
		
		distributeNodes();
	}
	
	protected void distributeNodes() {
		final Rectangle boundingRect = GraphUtils.getBoundingRect(nodes);
		
		int currentX = (int)boundingRect.getX();
		int currentY = (int)boundingRect.getY();
		for(OpNode node:nodes) {
			final JComponent comp = node.getExtension(JComponent.class);
			Dimension prefSize = comp.getPreferredSize();

			int deltaX = 0;
			int deltaY = 0;
			switch(direction) {
			case SwingConstants.HORIZONTAL:
				// only adjust x values
				deltaX = currentX - comp.getX();
				break;
				
			case SwingConstants.VERTICAL:
				deltaY = currentY - comp.getY();
				break;
				
			default:
				break;
			}
			addEdit(new MoveNodesEdit(Collections.singleton(node), deltaX, deltaY));
			currentX += prefSize.width + SPACE;
			currentY += prefSize.height + SPACE;
		}
		super.end();
	}
	
	@Override
	public String getPresentationName() {
		return "Distribute nodes";
	}
	
}
