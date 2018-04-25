package ca.phon.opgraph.nodes.canvas;

import java.awt.Point;
import java.util.Map;

import ca.phon.opgraph.OpNode;
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
