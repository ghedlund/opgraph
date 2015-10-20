package ca.gedge.opgraph.app.edits.graph;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.undo.CompoundEdit;

import ca.gedge.opgraph.OpNode;
import ca.gedge.opgraph.app.util.GraphUtils;

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
