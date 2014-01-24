package ca.gedge.opgraph.app.components.canvas;

import java.awt.Point;

/**
 * Handler for links which have been canceled by dragging onto
 * the canvas. Plug-ins can handle this event as they see fit.
 *
 */
public interface AbandonedLinkHandler {
	
	/**
	 * Handle the end drag link for abaondoned links.
	 * 
	 * @param canvas
	 * @param sourceNode
	 * @param sourceField
	 * @param p
	 */
	public void dragLinkAbandoned(GraphCanvas canvas, CanvasNode sourceNode, CanvasNodeField sourceField, Point p);

}
