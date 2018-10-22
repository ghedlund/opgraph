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

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;

import ca.phon.opgraph.ContextualItem;
import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.app.components.DoubleClickableTextField;

/**
 * A component that displays either an {@link InputField} or an {@link OutputField}.
 */
public class CanvasNodeField extends JComponent {
	/**
	 * State for link anchor points on this field.
	 */
	public static enum AnchorFillState {
		/** No fill state */
		NONE,

		/** Link is attached to this field */
		LINK,

		/** A default value is attached to this field */
		DEFAULT,

		/** This field is published */
		PUBLISHED,
	}

	/** Stroke we use to show an optional input field */
	private final static BasicStroke optionalFieldStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1, new float[]{1}, 0);

	/** The input/output field being displayed */
	private ContextualItem field;

	/** The field's anchoring point for an link */
	private Ellipse2D anchor;

	/** The style used for this component */
	private NodeStyle style;

	/** The color used to fill this anchor */
	private AnchorFillState anchorFillState;

	/** The field's name */
	private FieldName name;

	/**
	 * Extension of {@link DoubleClickableTextField} that modifies the current
	 * field's key whenever the text changes.
	 */
	private class FieldName extends JTextField {
		/** Double-click support */
		private DoubleClickableTextField doubleClickSupport;

		/** The field this text field displays */
		private ContextualItem field;

		/** The default font */
		private final Font defaultFont;

		public FieldName() {
			this.doubleClickSupport = new DoubleClickableTextField(this);
			this.defaultFont = getFont();

			setBorder(new EmptyBorder(2, 5, 2, 0));

			this.doubleClickSupport.addPropertyChangeListener(DoubleClickableTextField.TEXT_PROPERTY, textListener);
		}

		public void setField(ContextualItem field) {
			this.field = field;
			super.setText(field == null ? "" : field.getKey());
			super.setToolTipText(field == null ? "" : field.getDescription());

			if(field instanceof InputField) {
				setHorizontalAlignment(SwingConstants.LEFT);
				setEditable( !((InputField)field).isFixed() );
				setFont(((InputField)field).isOptional() ? defaultFont.deriveFont(Font.ITALIC) : defaultFont);
			} else if(field instanceof OutputField) {
				setHorizontalAlignment(SwingConstants.RIGHT);
				setEditable( !((OutputField)field).isFixed() );
				setFont(defaultFont);
			} else {
				setEditable(true);
				setFont(defaultFont);
			}
		}

		private PropertyChangeListener textListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if(field != null)
					field.setKey((String)e.getNewValue());
			}
		};
	}

	/**
	 * Constructs a component that displays the given field.
	 * 
	 * @param field  the field
	 * 
	 * @throws NullPointerException  if specified field is <code>null</code>
	 */
	public CanvasNodeField(ContextualItem field) {
		this.name = new FieldName();
		this.anchor = new Ellipse2D.Double();
		this.anchorFillState = AnchorFillState.NONE;

		setLayout(null);
		setFont(UIManager.getLookAndFeelDefaults().getFont("Label.font"));
		setField(field);
		setOpaque(false);

//		addMouseListener(mouseAdapter);
//		addMouseMotionListener(mouseMotionAdapter);

		add(name);
	}

	/**
	 * Get the link anchoring area for this field.
	 * 
	 * @return the anchor
	 */
	public Ellipse2D getAnchor() {
		return (Ellipse2D)anchor.clone();
	}

	/**
	 * Sets the state used for the anchor fill.
	 * 
	 * If the given state is {@link AnchorFillState#DEFAULT}, and the current
	 * state is {@link AnchorFillState#LINK}, then the change does not occur.
	 * 
	 * @param anchorFillState  the fill state
	 */
	public void setAnchorFillState(AnchorFillState anchorFillState) {
		if(this.anchorFillState != anchorFillState) {
			this.anchorFillState = anchorFillState;
			repaint();
		}
	}

	/**
	 * Sets the anchor fill state to a specified state, but only if the
	 * current state is not {@link AnchorFillState#LINK}.
	 * 
	 * @param anchorFillState  the fill state
	 */
	public void updateAnchorFillState(AnchorFillState anchorFillState) {
		if(this.anchorFillState != AnchorFillState.LINK)
			setAnchorFillState(anchorFillState);
	}

	/**
	 * Gets the style used for this node.
	 * 
	 * @return the node style
	 */
	public NodeStyle getStyle() {
		return style;
	}

	/**
	 * Sets the style used for this node.
	 * 
	 * @param style  the node style
	 */
	public void setStyle(NodeStyle style) {
		this.style = (style == null ? new NodeStyle() : style);
		if(!style.ShowEnabledField && field == OpNode.ENABLED_FIELD) {
			if(getParent() != null)
				getParent().remove(this);
		} else {
			revalidate();
		}
	}

	/**
	 * Gets the field being displayed by this component.
	 * 
	 * @return the field
	 */
	public ContextualItem getField() {
		return field;
	}

	/**
	 * Sets the field being displayed by this component.
	 * 
	 * @param field  the field
	 * 
	 * @throws NullPointerException  if specified field is <code>null</code>
	 */
	public void setField(ContextualItem field) {
		if(field == null) throw new NullPointerException("field cannot be null");
		if(field != this.field) {
			this.field = field;

			name.setField(field);
			setToolTipText(field.getDescription());

			revalidate();
		}
	}

	//
	// Overrides
	//

	@Override
	public void setBounds(int newX, int newY, int newW, int newH) {
		super.setBounds(newX, newY, newW, newH);

		// Everything else based off of insets
		final Insets insets = getInsets();
		newW -= insets.left + insets.right + 1;
		newH -= insets.top + insets.bottom + 1;

		// Update anchor based on whether or not this is an input/output field
		final int pad = newH - 1;
		double x = insets.left;
		double y = insets.top + (pad + 5.0) / 6;
		double w = (2.0*pad) / 3;
		double h = (2.0*pad) / 3;

		if(field instanceof InputField) {
			name.setBounds((int)(x + w), insets.top, (int)(newW - 2*x - w), newH);
		} else if(field instanceof OutputField) {
			x += newW - w;
			name.setBounds(insets.left, insets.right, (int)(newW - w - 3), newH);
		}

		anchor.setFrame(x, y, w, h);
	}

	@Override
	public Dimension getPreferredSize() {
		final Dimension textPref = name.getPreferredSize();
		final int anchorSize = textPref.height;
		return new Dimension(textPref.width + anchorSize + 2, textPref.height);
	}

	@Override
	protected void paintComponent(Graphics gfx) {
		super.paintComponent(gfx);

		Graphics2D g = (Graphics2D)gfx;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		switch(anchorFillState) {
		case LINK:
			g.setColor(style.AnchorLinkFillColor);
			g.fill(anchor);
			break;
		case DEFAULT:
			g.setColor(style.AnchorDefaultFillColor);
			g.fill(anchor);
			break;
		case PUBLISHED:
			g.setColor(style.AnchorPublishedFillColor);
			g.fill(anchor);
			break;
		case NONE:
			break;
		}

		g.setColor(style.FieldsTextColor);
		if((field instanceof InputField) && ((InputField)field).isOptional()) {
			final Stroke oldStroke = g.getStroke();
			g.setStroke(optionalFieldStroke);
			g.draw(anchor);
			g.setStroke(oldStroke);
		} else {
			g.draw(anchor);
		}
	}

}
