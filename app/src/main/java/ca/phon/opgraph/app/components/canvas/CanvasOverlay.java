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
/**
 * 
 */
package ca.phon.opgraph.app.components.canvas;

import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;

/**
 * A full-canvas component to draw things on top of everything else. This
 * currently includes:
 * <ul>
 *   <li>the link currently being edited, and<li>
 *   <li>the selection rectangle</li>
 * </ul>
 */
public class CanvasOverlay extends JComponent {
	/** The parent canvas */
	private final GraphCanvas canvas;

	/**
	 * Default constructor.
	 * 
	 * @param canvas  the parent canvas.
	 */
	public CanvasOverlay(GraphCanvas canvas) {
		this.canvas = canvas;

		setOpaque(false);
		setBackground(null);
	}

	@Override
	public Dimension getPreferredSize() {
		return null;
	}

	@Override
	protected void paintComponent(Graphics gfx) {
		Graphics2D g = (Graphics2D)gfx;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		// The drag link
		if(canvas.getUI().getCurrentlyDraggedLinkInputField() != null) {
			Ellipse2D anchor = canvas.getUI().getCurrentlyDraggedLinkInputField().getAnchor();
			Point p = new Point((int)anchor.getCenterX(), (int)anchor.getCenterY());
			p = SwingUtilities.convertPoint(canvas.getUI().getCurrentlyDraggedLinkInputField(), p, canvas);

			if(p != null && canvas.getUI().getCurrentDragLinkLocation() != null) {
				final Shape link = LinksLayer.createSmoothLink(p, canvas.getUI().getCurrentDragLinkLocation());
				final Stroke oldStroke = g.getStroke();
	
				if(link != null) {
					g.setColor(canvas.getUI().isDragLinkValid() ? Color.WHITE : Color.RED);
					g.setStroke(LinksLayer.THIN);
					g.draw(link);
	
					g.setColor(Color.BLACK);
					g.setStroke(oldStroke);
					g.draw(LinksLayer.THICK.createStrokedShape(link));
				}
			}
		}

		// the selection rect
		final Rectangle selectionRect = canvas.getUI().getSelectionRect();
		if(selectionRect != null) {
			int x = selectionRect.x;
			int y = selectionRect.y;
			int w = selectionRect.width;
			int h = selectionRect.height;

			if(w < 0) {
				x += w;
				w = -w;
			}

			if(h < 0) {
				y += h;
				h = -h;
			}

			g.setColor(new Color(255, 255, 255, 50));
			g.fillRect(x, y, w, h);
			g.setColor(Color.WHITE);
			g.drawRect(x, y, w, h);
		}
	}
}
