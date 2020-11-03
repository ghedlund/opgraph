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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

/**
 */
public class DefaultNodeBorder implements Border {
	static final int MAX_SIZE = 5;

	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(MAX_SIZE, MAX_SIZE, MAX_SIZE, MAX_SIZE);
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		if(c instanceof CanvasNode) {
			final CanvasNode canvasNode = (CanvasNode)c;
			if(canvasNode.isSelected()) {
				g.setColor(canvasNode.getStyle().NodeFocusColor);
				g.fillRect(x, y, w, MAX_SIZE);
				g.fillRect(x, y + MAX_SIZE, MAX_SIZE, h - MAX_SIZE);
				g.fillRect(w - MAX_SIZE, y + MAX_SIZE, MAX_SIZE, h - MAX_SIZE);
				g.fillRect(x + MAX_SIZE, h - MAX_SIZE, w - 2*MAX_SIZE, MAX_SIZE);
			}
			
			if(canvasNode.getNode().isBreakpoint()) {
				g.setColor(Color.red);
				g.fillOval(x, y, MAX_SIZE, MAX_SIZE);
			}
			
			x += MAX_SIZE - 1;
			y += MAX_SIZE - 1;
			w -= 2*MAX_SIZE - 1;
			h -= 2*MAX_SIZE - 1;

			g.setColor(canvasNode.getStyle().NodeBorderColor);
			g.drawRect(x, y, w, h);
		}
	}

}
