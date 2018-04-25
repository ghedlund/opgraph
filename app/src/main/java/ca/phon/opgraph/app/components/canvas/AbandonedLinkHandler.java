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
