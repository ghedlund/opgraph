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
			int newX = getVisibleRect().x + getVisibleRect().width - CanvasMinimap.MAX_LENGTH;
			int newY = getVisibleRect().y;
			
			if(x != newX || y != newY) {
				this.minimap.updateMinimap();
			}
			
			this.minimap.setBounds(newX, newY, CanvasMinimap.MAX_LENGTH, CanvasMinimap.MAX_LENGTH);
		}
	}
	
}
