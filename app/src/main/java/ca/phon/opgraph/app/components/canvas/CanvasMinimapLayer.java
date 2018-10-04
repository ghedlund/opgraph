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
