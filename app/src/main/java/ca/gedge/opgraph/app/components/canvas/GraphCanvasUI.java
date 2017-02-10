package ca.gedge.opgraph.app.components.canvas;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.plaf.ComponentUI;

import ca.gedge.opgraph.OpLink;

public abstract class GraphCanvasUI extends ComponentUI {
	
	//
	// Layers
	//

	public static final Integer GRID_LAYER = 1;

	@SuppressWarnings("unused")
	public static final Integer BACKGROUND_LAYER = 2;

	public static final Integer NOTES_LAYER = 10;
	public static final Integer LINKS_LAYER = 100;
	public static final Integer NODES_LAYER = 200;
	public static final Integer OVERLAY_LAYER = 10000;
	public static final Integer DEBUG_OVERLAY_LAYER = 10001;

	@SuppressWarnings("unused")
	public static final Integer FOREGROUND_LAYER = Integer.MAX_VALUE;
	
	/**
	 * @return grid layer
	 */
	public abstract GridLayer getGridLayer();
	
	/**
	 * @return links layer
	 */
	public abstract LinksLayer getLinksLayer();
	
	/**
	 * @return canvas overlay
	 */
	public abstract CanvasOverlay getCanvasOverlay();
	
	/**
	 * @return debug overlay
	 */
	public abstract DebugOverlay getDebugOverlay();
	
	/**
	 * Gets the link currently being dragged.
	 * 
	 * @return the link, or <code>null</code> if no link being dragged 
	 */
	public abstract OpLink getCurrentlyDraggedLink();

	/**
	 * Gets the input field of the current drag link.
	 * 
	 * @return the input field, or <code>null</code> if no link being dragged
	 */
	public abstract CanvasNodeField getCurrentlyDraggedLinkInputField();

	/**
	 * Gets the location of the current link being dragged.
	 * 
	 * @return the location of the drag link, or <code>null</code> if no link
	 *         being dragged
	 */
	public abstract Point getCurrentDragLinkLocation();

	/**
	 * Gets whether or not the currently dragged link is at a valid drop spot.
	 * 
	 * @return <code>true</code> if the currently dragged link can be dropped
	 *         at the curent drag location, <code>false</code> otherwise 
	 */
	public abstract boolean isDragLinkValid();
	
	/**
	 * Gets the selection rectangle.
	 * 
	 * @return the selection rectangle, or <code>null</code> if there is
	 *         currently no selection rectangle
	 */
	public abstract Rectangle getSelectionRect();
	
	/**
	 * Start a drag operation for a given field.
	 * 
	 * @param fieldComponent  the field
	 */
	public abstract void startLinkDrag(CanvasNodeField fieldComponent);
	
	/**
	 * Called to update link dragging status.
	 * 
	 * @param p  the current point of the drag, in the coordinate system of this component
	 */
	public abstract void updateLinkDrag(Point p);
	
	/**
	 * Called when link dragging should end.
	 * 
	 * @param p  the end point of the drag, in the coordinate system of this component
	 */
	public abstract void endLinkDrag(Point p);
	

}