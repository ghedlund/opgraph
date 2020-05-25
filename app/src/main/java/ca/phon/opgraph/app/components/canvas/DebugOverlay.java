/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
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
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.IdentityHashMap;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import ca.phon.opgraph.OpLink;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.Processor;
import ca.phon.opgraph.app.util.GUIHelper;

/**
 * A full-canvas component to draw debug things on top of everything else.
 */
class DebugOverlay extends JComponent {
	/** Dark mask for shaded nodes */
	static final Paint DARK_MASK;

	/** Light mask for shaded nodes */
	static final Paint LIGHT_MASK;

	/** Mask for error node */
	static final Paint ERROR_MASK;
	
	static final Paint NEXT_NODE_MASK;
	static final Paint NEXT_NODE_BORDER_PAINT;

	static {
		final int W = 4;
		final int H = 4;

		// Creates a crosshatch texture
		final BufferedImage texture = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
		{
			final Graphics2D g = texture.createGraphics();
			g.setColor(new Color(0, 0, 0, 200));
			g.drawLine(0, 0, W - 1, H - 1);
			g.drawLine(0, H - 1, W - 1, 0);
		}
		
		DARK_MASK = new TexturePaint(texture, new Rectangle2D.Double(0, 0, W, H));
		NEXT_NODE_MASK = new Color(200, 200, 200, 127);
		NEXT_NODE_BORDER_PAINT = new Color(200, 200, 255, 255);
		LIGHT_MASK = new Color(0, 0, 0, 127);
		ERROR_MASK = new Color(255, 0, 0, 50);
	}

	/** The parent canvas */
	private final GraphCanvas canvas;

	/**
	 * Default constructor.
	 * 
	 * @param canvas  the parent canvas
	 */
	public DebugOverlay(GraphCanvas canvas) {
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
		final Processor context = canvas.getDocument().getProcessingContext();
		if(context != null) {
			// Get the context which is operating on the graph this
			// canvas is currently viewing
			Processor activeContext = context;
			while(activeContext.getMacroContext() != null && activeContext.getGraphOfContext() != canvas.getDocument().getGraph())
				activeContext = activeContext.getMacroContext();

			// If the current graph has an associated processing context
			if(activeContext != null) {
				// Find directly connected nodes
				final Graphics2D g = (Graphics2D)gfx;
				final IdentityHashMap<CanvasNode, Boolean> connectedNodes = new IdentityHashMap<CanvasNode, Boolean>();
				final OpNode currentNode = activeContext.getCurrentNodeOfContext();
				final OpNode nextNode = activeContext.getNodeToProcess();
				
				if(currentNode != null) {
					for(OpLink link : canvas.getDocument().getGraph().getIncomingEdges(currentNode))
						connectedNodes.put( canvas.getNode(link.getSource()), true );

					for(OpLink link : canvas.getDocument().getGraph().getOutgoingEdges(currentNode))
						connectedNodes.put( canvas.getNode(link.getDestination()), true );
				}

				// Draw masks over nodes
				final Paint oldPaint = g.getPaint();
				for(OpNode node : canvas.getDocument().getGraph().getVertices()) {
					final CanvasNode canvasNode = canvas.getNode(node);
					final Rectangle bounds = SwingUtilities.convertRectangle(canvasNode, GUIHelper.getInterior(canvasNode), this);

					--bounds.x;
					--bounds.y;
					bounds.width += 2;
					bounds.height += 2;

					if(node == currentNode) {
						if(context.getError() != null) {
							g.setPaint(ERROR_MASK);
							g.fill(bounds);
						}
					} else {
						if(nextNode != null && node == nextNode) {
							g.setPaint(NEXT_NODE_BORDER_PAINT);
							g.setStroke(new BasicStroke(5.0f));
							g.draw(bounds);
							g.setPaint(NEXT_NODE_MASK);
							g.fill(bounds);
						} else {
							g.setPaint(LIGHT_MASK);
							g.fill(bounds);
							if(!connectedNodes.containsKey(canvasNode)) {
								g.setPaint(DARK_MASK);
								g.fill(bounds);
							}
						}
					}
					
					
				}

				g.setPaint(oldPaint);
			}
		}
	}
}
