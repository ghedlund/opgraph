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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import ca.phon.opgraph.OpLink;
import ca.phon.opgraph.OpNode;

/**
 * A full-canvas component to draw links between node fields
 */
public class LinksLayer extends JComponent {
	/** Thin stroke for drawing links */
	static final Stroke THIN = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);

	/** Thick stroke for drawing links */
	static final Stroke THICK = new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);

	/** The mapping of links to link shapes */
	private TreeMap<OpLink, Shape> links;

	/** The canvas parent */
	private GraphCanvas canvas;

	/**
	 * Default constructor.
	 * 
	 * @param canvas  the parent canvas
	 */
	public LinksLayer(GraphCanvas canvas) {
		this.canvas = canvas;
		this.links = new TreeMap<OpLink, Shape>();

		setOpaque(false);
		setBackground(null);
	}

	/**
	 * Creates a smooth curve between two points.
	 * 
	 * @param p1  a point
	 * @param p2  a point
	 * 
	 * @return the path representing the smooth curve
	 */
	public static Path2D createSmoothLink(Point p1, Point p2) {
		final double cx1 = (p1.x + p2.x) * 0.5;
		final double cy1 = p1.y;
		final double cx2 = (p1.x + p2.x) * 0.5;
		final double cy2 = p2.y;

		final Path2D link = new Path2D.Double();
		link.moveTo(p1.x, p1.y);
		link.curveTo(cx1, cy1, cx2, cy2, p2.x, p2.y);
		return link;
	}

	/**
	 * Updates the path shape for a given link.
	 * 
	 * @param link  the link whose path should be updated
	 */
	void updateLink(OpLink link) {
		final CanvasNode source = canvas.getNode(link.getSource());
		final CanvasNode dest = canvas.getNode(link.getDestination());
		if(source != null && dest != null) {
			final CanvasNodeField sourceField = source.getFieldsMap().get(link.getSourceField());
			final CanvasNodeField destField = dest.getFieldsMap().get(link.getDestinationField());
			if(sourceField != null && destField != null) {
				// Get the anchoring points
				final Ellipse2D sourceAnchor = sourceField.getAnchor();
				final Ellipse2D destAnchor = destField.getAnchor();

				// Convert to our coordinate system
				Point sourceLoc = new Point((int)sourceAnchor.getCenterX(), (int)sourceAnchor.getCenterY());
				Point destLoc = new Point((int)destAnchor.getCenterX(), (int)destAnchor.getCenterY());
				sourceLoc = SwingUtilities.convertPoint(sourceField, sourceLoc, canvas);
				destLoc = SwingUtilities.convertPoint(destField, destLoc, canvas);

				links.put(link, createSmoothLink(sourceLoc, destLoc));

				repaint();
			}
		}
	}

	/**
	 * Removes the path shape from a given link.
	 * 
	 * @param link  the link whose path should be removed
	 */
	void removeLink(OpLink link) {
		if(links.containsKey(link)) {
			links.remove(link);
			repaint();
		}
	}

	/**
	 * Removes path shapes for all links.
	 * 
	 * @param link  the link whose path should be removed
	 */
	void removeAllLinks() {
		links.clear();
		repaint();
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
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		final Color SELECTED_FILL = Color.ORANGE;
		final Color REGULAR_FILL = Color.ORANGE.darker().darker();

		final IdentityHashMap<OpLink, Boolean> connectedLinks = new IdentityHashMap<OpLink, Boolean>();
		final List<OpNode> selectedNodes = new ArrayList<>();
		if(canvas.getDocument().getProcessingContext() != null) {
			selectedNodes.add(canvas.getDocument().getProcessingContext().getCurrentNode());
		} else {
			selectedNodes.addAll(canvas.getSelectionModel().getSelectedNodes());
		}

		// Draw links between nodes
		final Stroke oldStroke = g.getStroke();
		for(OpNode node : selectedNodes) {
			for(OpLink link : canvas.getDocument().getGraph().getIncomingEdges(node))
				connectedLinks.put(link, true);
			
			for(OpLink link : canvas.getDocument().getGraph().getOutgoingEdges(node))
				connectedLinks.put(link, true);
		}

		// Draw links
		for(OpLink link:links.keySet()) {
			Color strokeColor = Color.BLACK;
			Color fillColor = REGULAR_FILL;
			if(connectedLinks.containsKey(link))
				fillColor = SELECTED_FILL;

			// Link fill
			g.setColor(fillColor);
			g.setStroke(THIN);
			g.draw(links.get(link));

			// Link outline
			g.setColor(strokeColor);
			g.setStroke(oldStroke);
			g.draw(THICK.createStrokedShape(links.get(link)));
		}

		g.setStroke(oldStroke);
	}
}
