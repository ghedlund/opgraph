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
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.*;

public class CanvasMinimapLayer extends JComponent {
	
	private CanvasMinimap minimap;
	
	public CanvasMinimapLayer(GraphCanvas canvas) {
		super();
		
		this.minimap = new CanvasMinimap(canvas);
		
		init();
	}

	public void update() {
		this.minimap.updateMinimap();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return null;
	}
	
	public CanvasMinimap getMinimap() {
		return this.minimap;
	}
	
	private void init() {
		setLayout(null);
		
		this.minimap.setBorder(BorderFactory.createEtchedBorder());
		this.minimap.setBounds(getVisibleRect().x + getVisibleRect().width - CanvasMinimap.MAX_LENGTH, getVisibleRect().y,
				CanvasMinimap.MAX_LENGTH, CanvasMinimap.MAX_LENGTH);
		
		add(this.minimap);
	}
	
	@Override
	protected void paintComponent(Graphics gfx) {
		super.paintComponent(gfx);
		if(isVisible()) {
			int x = this.minimap.getX();
			int y = this.minimap.getY();
			
			final AffineTransform at = new AffineTransform();
			at.scale(getMinimap().getCanvas().getZoomLevel(), getMinimap().getCanvas().getZoomLevel());
			
			final Rectangle viewRect = getVisibleRect();
			
			try {
				Point2D canvasTopLeft = at.inverseTransform(viewRect.getLocation(), null);
				Point2D canvasDimensions = at.inverseTransform(new Point2D.Double(viewRect.width, viewRect.height), null);
				
				Point2D minimapDimensions = at.inverseTransform(new Point2D.Double(CanvasMinimap.MAX_LENGTH, CanvasMinimap.MAX_LENGTH), null);
				
				int maxX = Math.min(getMinimap().getCanvas().getWidth(), (int)(canvasTopLeft.getX() + canvasDimensions.getX()) );
				int newX = (int)(maxX - minimapDimensions.getX());
				int newY = (int)Math.round(canvasTopLeft.getY());
				
				if(x != newX || y != newY) {
					this.minimap.updateMinimap();
				}
				
				this.minimap.setBounds(newX, newY, (int)minimapDimensions.getX(), (int)minimapDimensions.getY());
			} catch (NoninvertibleTransformException e) {
			}
			
			
		}
	}
	
}
