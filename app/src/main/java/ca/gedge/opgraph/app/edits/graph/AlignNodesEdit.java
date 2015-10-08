package ca.gedge.opgraph.app.edits.graph;

import java.awt.Dimension;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.undo.CompoundEdit;

import ca.gedge.opgraph.OpNode;
import ca.gedge.opgraph.app.extensions.NodeMetadata;

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
	
	double x1 = Double.MAX_VALUE;
	double x2 = Double.MIN_VALUE;
	double y1 = Double.MAX_VALUE;
	double y2 = Double.MIN_VALUE;

	public void alignSelectedNodes() {
		for(OpNode node:nodes) {
			NodeMetadata nodeMeta = node.getExtension(NodeMetadata.class);
			if(nodeMeta == null) {
				nodeMeta = new NodeMetadata();
				node.putExtension(NodeMetadata.class, nodeMeta);
			}
			
			final JComponent comp = node.getExtension(JComponent.class);
			Dimension prefSize = comp.getPreferredSize();
			
			switch(side) {
			case SwingConstants.TOP:
				y1 = Math.min(y1, nodeMeta.getY());
				y2 = Math.max(y2, nodeMeta.getY());
				x1 = 0.0;
				x2 = 0.0;
				break;
				
			case SwingConstants.BOTTOM:
				y1 = Math.min(y1, nodeMeta.getY() + prefSize.height);
				y2 = Math.max(y2, nodeMeta.getY() + prefSize.height);
				x1 = 0.0;
				x2 = 0.0;
				break;
				
			case SwingConstants.LEFT:
				x1 = Math.min(x1, nodeMeta.getX());
				x2 = Math.max(x2, nodeMeta.getX());
				y1 = 0.0;
				y2 = 0.0;
				break;
				
			case SwingConstants.RIGHT:
				x1 = Math.min(x1, nodeMeta.getX() + prefSize.getWidth());
				x2 = Math.max(x2, nodeMeta.getX() + prefSize.getWidth());
				y1 = 0.0;
				y2 = 0.0;
				break;
				
			default:
				break;
			}
		}
		
		for(OpNode node:nodes) {
			NodeMetadata meta = node.getExtension(NodeMetadata.class);
			int x = (int)Math.round((x1+x2)/2.0);
			int y = (int)Math.round((y1+y2)/2.0);
			
			final JComponent comp = node.getExtension(JComponent.class);
			Dimension prefSize = comp.getPreferredSize();

			int deltaX = 0;
			int deltaY = 0;
			switch(side) {
			case SwingConstants.TOP:
				// adjust y values only
				deltaY = y - meta.getY();
				deltaX = 0;
				addEdit(new MoveNodesEdit(Collections.singleton(node), deltaX, deltaY));
				break;
				
			case SwingConstants.BOTTOM:
				deltaY = (int)Math.round(y - meta.getY() - prefSize.getHeight());
				deltaX = 0;
				addEdit(new MoveNodesEdit(Collections.singleton(node), deltaX, deltaY));
				break;
			
			case SwingConstants.LEFT:
				deltaY = 0;
				deltaX = (int)Math.round(x - meta.getX());
				addEdit(new MoveNodesEdit(Collections.singleton(node), deltaX, deltaY));
				break;
				
			case SwingConstants.RIGHT:
				deltaY = 0;
				deltaX = (int)Math.round(x - meta.getX() - prefSize.getWidth());
				addEdit(new MoveNodesEdit(Collections.singleton(node), deltaX, deltaY));
				break;
				
			default:
				break;
			}
		}
		
		end();
	}
	
	@Override
	public String getPresentationName() {
		return "Align nodes";
	}

}
