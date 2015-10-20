package ca.gedge.opgraph.app.edits.graph;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.undo.CompoundEdit;

import ca.gedge.opgraph.OpNode;
import ca.gedge.opgraph.app.extensions.NodeMetadata;
import ca.gedge.opgraph.app.util.GraphUtils;

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
