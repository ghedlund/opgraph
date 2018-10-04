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
package ca.phon.opgraph.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import ca.phon.opgraph.util.Pair;

/**
 * A library for some default icons that are rendered programmatically.
 */
public class IconLibrary {
	/** The types of icons this library stores */
	public static enum IconType {
		/** Empty icon */
		EMPTY,

		/** Debug run */
		DEBUG_RUN,

		/** Debug stop */
		DEBUG_STOP,

		/** Debug step */
		DEBUG_STEP,

		/** Debug step level */
		DEBUG_STEP_LEVEL,

		/** Tree expanded icon */
		TREE_EXPANDED,

		/** Tree collapsed icon */
		TREE_COLLAPSED,
	}

	/** The mapping of icons */
	private final static HashMap<Pair<IconType, Dimension>, WeakReference<Icon>> icons =
			new HashMap<Pair<IconType, Dimension>, WeakReference<Icon>>();

	/**
	 * Gets the icon associated with a given icon type.
	 * 
	 * @param type  the type of icon to fetch
	 * @param width  the width of the icon to fetch
	 * @param height  the height of the icon to fetch
	 * 
	 * @return the icon
	 * 
	 * @throws NullPointerException  if type is <code>null</code>
	 * @throws IllegalArgumentException  if width or height is less than 8
	 */
	public static Icon getIcon(IconType type, int width, int height) {
		if(type == null)
			throw new NullPointerException("Type/dim cannot be null");

		if(width < 8 || height < 8)
			throw new NullPointerException("Cannot render icon of dimension less than 8");

		final Pair<IconType, Dimension> key = new Pair<IconType, Dimension>(type, new Dimension(width, height));

		Icon icon = null;
		if(icons.containsKey(key))
			icon = icons.get(key).get();

		if(icon == null) {
			icon = renderIcon(type, width, height);
			icons.put(key, new WeakReference<Icon>(icon));
		}

		return icon;
	}

	/**
	 * Renders the icon of a given type.
	 * 
	 * @param type  the type of icon to render
	 * 
	 * @return  the rendered icon
	 */
	private static Icon renderIcon(IconType type, int width, int height) {
		final int sz = Math.min(width, height);
		final int pad = sz / 8;

		final BufferedImage iconImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = iconImage.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		switch(type) {
		case DEBUG_RUN: {
			final int xoff = (3*pad) / 2;
			final Path2D.Double path = new Path2D.Double(); 
			path.moveTo(xoff, pad);
			path.lineTo(sz - xoff, sz / 2);
			path.lineTo(xoff, sz - pad);
			path.closePath();

			g.setColor(Color.LIGHT_GRAY);
			g.fill(path);
			g.setColor(Color.GRAY);
			g.draw(path);

			break;
		}
		case TREE_EXPANDED: {
			final int xoff = (3*pad) / 2;
			final Path2D.Double path = new Path2D.Double(); 
			path.moveTo(pad, xoff);
			path.lineTo(sz / 2, sz - xoff);
			path.lineTo(sz - pad, xoff);
			path.closePath();

			g.setColor(Color.GRAY);
			g.fill(path);

			break;
		}
		case TREE_COLLAPSED: {
			final int xoff = (3*pad) / 2;
			final Path2D.Double path = new Path2D.Double(); 
			path.moveTo(xoff, pad);
			path.lineTo(sz - xoff, sz / 2);
			path.lineTo(xoff, sz - pad);
			path.closePath();

			g.setColor(Color.GRAY);
			g.fill(path);

			break;
		}
		case DEBUG_STOP: {
			break;
		}
		case DEBUG_STEP: {
			break;
		}
		case DEBUG_STEP_LEVEL: {
			break;
		}
		case EMPTY:
			break;
		};

		return new ImageIcon(iconImage);
	}
}
