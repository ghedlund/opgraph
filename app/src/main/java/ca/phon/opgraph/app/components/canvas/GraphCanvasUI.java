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
package ca.phon.opgraph.app.components.canvas;

import java.awt.*;

import javax.swing.plaf.ComponentUI;

import ca.phon.opgraph.OpLink;

public abstract class GraphCanvasUI extends ComponentUI {
	
	//
	// Layers
	//

	public static final Integer GRID_LAYER = 1;

	@SuppressWarnings("unused")
	public static final Integer BACKGROUND_LAYER = 2;

	public static final Integer NOTES_LAYER = 100;
	public static final Integer LINKS_LAYER = 200;
	public static final Integer NODES_LAYER = 300;
	public static final Integer OVERLAY_LAYER = 10000;
	public static final Integer DEBUG_OVERLAY_LAYER = 10001;
	
	public static final Integer MINIMAP_LAYER = 20000;

	@SuppressWarnings("unused")
	public static final Integer FOREGROUND_LAYER = Integer.MAX_VALUE;
	
	public abstract CanvasMinimapLayer getMinimapLayer();
	
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
	 * Get the bounding rectangle for the entire graph.
	 * The top-left coord will always be (0,0) and the bottom-right
	 * coord will be the bottom-right coord of the bottom-right
	 * node/note.
	 * 
	 * @return the bounding rectangle for the graph
	 */
	public abstract Rectangle getGraphBoundingRect();
	
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
