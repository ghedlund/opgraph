/*
 * Copyright (C) 2012-2020 Gregory Hedlund <https://www.phon.ca>
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.phon.opgraph.app.components.canvas;

import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;

import ca.phon.opgraph.app.util.*;

/**
 * A grid component intended for displaying on the background of a
 * {@link GraphCanvas} component.
 */
public class GridLayer extends JComponent {
	/** Grid line spacing */
	public static final int DEFAULT_GRID_SPACING = 10;

	/** Snap distance */
	public static final int DEFAULT_SNAP_DISTANCE = 5;

	private final GraphCanvas canvas;
	
	/**
	 * Constructs a viewport for the specified canvas.
	 */
	public GridLayer(GraphCanvas canvas) {
		setOpaque(true);
		setBackground(Color.DARK_GRAY);
		
		this.canvas = canvas;
	}

	/**
	 * Snaps a point to this grid.
	 * 
	 * @param p  the point which will be snapped
	 * 
	 * @return the delta to apply to the given point to enforce snapping   
	 */
	public Point snap(Point p) {
		final int mx = p.x % DEFAULT_GRID_SPACING;
		final int my = p.y % DEFAULT_GRID_SPACING;
		final Point snapped = new Point();

		if(Math.abs(mx) <= DEFAULT_SNAP_DISTANCE) {
			snapped.x = -mx;
		} else if(Math.abs(DEFAULT_GRID_SPACING - mx) <= DEFAULT_SNAP_DISTANCE) {
			snapped.x = DEFAULT_GRID_SPACING - mx;
		}

		if(Math.abs(my) <= DEFAULT_SNAP_DISTANCE) {
			snapped.y = -my;
		} else if(Math.abs(DEFAULT_GRID_SPACING - my) <= DEFAULT_SNAP_DISTANCE) {
			snapped.y = DEFAULT_GRID_SPACING - my;
		}

		return snapped;
	}

	//
	// Overrides
	//

	@Override
	public Dimension getPreferredSize() {
		return null;
	}

	@Override
	protected void paintComponent(Graphics gfx) {
		Graphics2D g = (Graphics2D)gfx;
		//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		super.paintComponent(g);

		// Draw grid lines
		final Rectangle view = getVisibleRect();
		
		final AffineTransform at = new AffineTransform();
		at.scale(canvas.getZoomLevel(), canvas.getZoomLevel());
				
		int startx = ((view.x / DEFAULT_GRID_SPACING - 1) * DEFAULT_GRID_SPACING); 
		int starty = ((view.y / DEFAULT_GRID_SPACING - 1) * DEFAULT_GRID_SPACING); 
		int endx = view.x + view.width + 1;
		int endy = view.y + view.height + 1;
		
		try {
			Point2D topLeft = at.inverseTransform(new Point2D.Double(startx, starty), null);
			Point2D btmRight = at.inverseTransform(new Point2D.Double(endx, endy), null);
			
			startx = (int)Math.round(topLeft.getX());
			starty = (int)Math.round(topLeft.getY());
			endx = (int)Math.round(btmRight.getX());
			endy = (int)Math.round(btmRight.getY());
			
			// Fill background
			g.setColor(getBackground());
			g.fillRect(0, 0, canvas.getSize().width, canvas.getSize().height);
			
			g.setColor(GUIHelper.highlightColor(getBackground()));
			for(int y = starty; y < endy; y += DEFAULT_GRID_SPACING)
				g.drawLine(startx, y, endx, y);
			
			for(int x = startx; x < endx; x += DEFAULT_GRID_SPACING)
				g.drawLine(x, starty, x, endy);
		} catch (NoninvertibleTransformException e) {
		}
	}
}
