/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * This file is part of the OpGraph project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
