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
import java.io.*;
import java.util.*;
import java.util.logging.*;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.extensions.*;

/**
 * A class containing styling information for a node.
 * 
 * TODO perhaps move this over to javax.swing.UIManager
 */
public class NodeStyle {
	public static final NodeStyle DEFAULT;
	public static final NodeStyle COMPOSITE;
	public static final NodeStyle OBJECT;
	public static final NodeStyle ITERATION;
	
	private static final Map<Class<? extends OpNode>, NodeStyle> installedStyles;

	static {
		DEFAULT = new NodeStyle();
		try {
			DEFAULT.NodeIcon = new ImageIcon(ImageIO.read(NodeStyle.class.getClassLoader().getResourceAsStream("data/icons/16x16/opgraph/bricks.png")));
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.WARNING, e.getLocalizedMessage(), e);
		}
		
		DEFAULT.NodeBorderColor = Color.GRAY;
		DEFAULT.NodeBackgroundColor = new Color(255, 255, 255, 200);
		DEFAULT.NodeFocusColor = new Color(255, 200, 0, 255);
		DEFAULT.NodeNameTextColor = Color.BLACK;
		DEFAULT.NodeNameTextShadowColor = Color.LIGHT_GRAY;
		DEFAULT.NodeNameTopColor = new Color(200, 200, 200, 255);
		DEFAULT.NodeNameBottomColor = new Color(150, 150, 150, 255);
		DEFAULT.FieldsTextColor = Color.BLACK;
		DEFAULT.AnchorLinkFillColor = Color.ORANGE;
		DEFAULT.AnchorDefaultFillColor = new Color(100, 150, 255, 100);
		DEFAULT.AnchorPublishedFillColor = new Color(50, 255, 50, 150);

		COMPOSITE = new NodeStyle(DEFAULT);
		try {
			COMPOSITE.NodeIcon = new ImageIcon(ImageIO.read(NodeStyle.class.getClassLoader().getResourceAsStream("data/icons/16x16/opgraph/graph.png")));
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.WARNING, e.getLocalizedMessage(), e);
		}
		COMPOSITE.NodeBorderColor = new Color(100, 155, 100);
		COMPOSITE.NodeBackgroundColor = new Color(200, 255, 200, 200);
		COMPOSITE.NodeNameTopColor = new Color(150, 200, 100, 255);
		COMPOSITE.NodeNameBottomColor = new Color(100, 150, 50, 255);
		
		OBJECT = new NodeStyle(DEFAULT);
		
		ITERATION = new NodeStyle(DEFAULT);
		ITERATION.NodeBorderColor = Color.yellow;
		ITERATION.NodeBackgroundColor = new Color(100, 255, 200, 200);
		ITERATION.NodeNameTopColor = new Color(30, 220, 180, 255);
		ITERATION.NodeNameBottomColor = new Color(0,  180, 150, 255);
		try {
			ITERATION.NodeIcon = new ImageIcon(ImageIO.read(NodeStyle.class.getClassLoader().getResourceAsStream("data/icons/16x16/opgraph/graph-loop.png")));
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.WARNING, e.getLocalizedMessage(), e);
		}

		installedStyles = new HashMap<Class<? extends OpNode>, NodeStyle>();
	}

	/**
	 * Installs a node style for a specfied {@link OpNode} class.
	 * 
	 * @param cls  the {@link OpNode} class to register the style for
	 * @param style  the node style
	 */
	public static void installStyleForNode(Class<? extends OpNode> cls, NodeStyle style) {
		installedStyles.put(cls, style);
	}

	/**
	 * Gets the node style for a given node.
	 * 
	 * @param node  the node
	 * 
	 * @return the node style for the given node, or the default style if no node
	 *         style is installed for the given node class (or any of its superclasses)
	 */
	public static NodeStyle getStyleForNode(OpNode node) {
		if(node != null) {
			// Go through superclasses to see if we can find something
			Class<?> cls = node.getClass();
			while(cls != null) {
				if(installedStyles.containsKey(cls))
					return installedStyles.get(cls);

				cls = cls.getSuperclass();
			}
			
			// CompositeNode extension is fixed
			if(node.getExtension(CompositeNode.class) != null)
				return COMPOSITE;
		}
		return DEFAULT;
	}
	
	/** The top color for the background of the node name section */ 
	public Color NodeNameTopColor = Color.WHITE;

	/** The bottom color for the background of the node name section */ 
	public Color NodeNameBottomColor = Color.WHITE;
	
	/** Icon shown with the node name */
	public Icon NodeIcon = null;

	/** The color for the node name text */
	public Color NodeNameTextColor = Color.BLACK;

	/** The color for the node name text shadow */
	public Color NodeNameTextShadowColor = Color.LIGHT_GRAY;

	/** The color for the node's bg */
	public Color NodeBackgroundColor = Color.WHITE;

	/** The color for the node's border */
	public Color NodeBorderColor = Color.BLACK;

	/** The color for the node's focus ring */
	public Color NodeFocusColor = Color.WHITE;

	/** The color for the node's input/output fields */
	public Color FieldsTextColor = Color.BLACK;

	/** The color for the fill in the anchor points for links when an link is attached */
	public Color AnchorLinkFillColor = Color.GRAY;

	/** The color for the fill in the anchor points for links when a default value is available */
	public Color AnchorDefaultFillColor = Color.GRAY;

	/** The color for the fill in the anchor points for links when it is a published input/output in a macro */
	public Color AnchorPublishedFillColor = Color.GRAY;

	/** The border used for rendering a node. */
	public Border NodeBorder = new DefaultNodeBorder();

	/** Whether or not to show the enabled field of a node. */
	public boolean ShowEnabledField = true;
	
	public NodeStyle() {
		super();
	}
	
	public NodeStyle(NodeStyle style) {
		super();
		
		this.NodeIcon = style.NodeIcon;
		
		this.AnchorDefaultFillColor = style.AnchorDefaultFillColor;
		this.AnchorLinkFillColor = style.AnchorLinkFillColor;
		this.AnchorPublishedFillColor = style.AnchorPublishedFillColor;
		
		this.FieldsTextColor = style.FieldsTextColor;
		
		this.NodeBackgroundColor = style.NodeBackgroundColor;
		this.NodeBorder = style.NodeBorder;
		this.NodeBorderColor = style.NodeBorderColor;
		this.NodeFocusColor = style.NodeFocusColor;
		this.NodeNameBottomColor = style.NodeNameBottomColor;
		this.NodeNameTextColor = style.NodeNameTextColor;
		this.NodeNameTextShadowColor = style.NodeNameTextShadowColor;
		this.NodeNameTopColor = style.NodeNameTopColor;
		
		this.ShowEnabledField = style.ShowEnabledField;
	}
}
