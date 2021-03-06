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
package ca.phon.opgraph.app.components;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 * A grip component that will resize a given component when dragged over.
 */
public class ResizeGrip extends JComponent {
	/** The component whose size this resize grip will control */
	private Component component;

	/** The size of the grip */
	private Dimension size;

	/** The size of the component before this grip was clicked */
	private Dimension initialComponentSize;

	/**
	 * Constructs a default resize grip component.
	 * 
	 * @param component  the component which will be resized by this grip
	 */
	public ResizeGrip(Component component) {
		this(component, 10, 10);
	}

	/**
	 * Construct s a resize grip component with a given size.
	 * 
	 * @param component  the component which will be resized by this grip
	 * @param w  the width of the grip
	 * @param h  the height of the grip
	 */
	public ResizeGrip(Component component, int w, int h) {
		this.component = component;
		this.size = new Dimension(w, h);
		this.initialComponentSize = component.getSize();

		setOpaque(true);
		addMouseListener(new MouseInputAdapter() {
		});
	}

	/**
	 * Gets the component this grip resizes.
	 * 
	 * @return the component
	 */
	public Component getComponent() {
		return component;
	}

	/**
	 * Gets the size of the component before this grip was clicked.
	 * 
	 * @return the size of the component
	 */
	public Dimension getInitialComponentSize() {
		return initialComponentSize;
	}
	
	public void saveSize() {
		initialComponentSize = component.getSize();
	}

	public void resize(Point initialLocation, Point p) {
		final int dx = p.x - initialLocation.x;
		final int dy = p.y - initialLocation.y;
		if(component != null) {
			final Dimension dim = component.getMinimumSize();
			dim.width = Math.max(dim.width, initialComponentSize.width + dx);
			dim.height = Math.max(dim.height, initialComponentSize.height + dy);
			component.setPreferredSize(dim);
			component.invalidate();
			revalidate();
		}
	}
	
	//
	// Overrides
	//

	@Override
	public Dimension getPreferredSize() {
		return size;
	}

	@Override
	protected void paintComponent(Graphics gfx) {
		super.paintComponent(gfx);

		final Graphics2D g = (Graphics2D)gfx;
		final int w = getWidth();
		final int h = getHeight();

		g.setColor(getBackground());
		g.setStroke(new BasicStroke(1.5f));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.drawLine(0, h - 1, w - 1, 0);
		g.drawLine((w - 1) / 2, h - 1, w - 1, (h - 1) / 2);
		g.drawLine(w - 2, h - 1, w - 1, h - 2);
	}
}
