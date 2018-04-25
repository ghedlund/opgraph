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

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.undo.CompoundEdit;

import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.app.util.GraphUtils;
import ca.phon.opgraph.extensions.NodeMetadata;

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
