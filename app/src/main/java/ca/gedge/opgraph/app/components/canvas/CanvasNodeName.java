/*
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
package ca.gedge.opgraph.app.components.canvas;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEditSupport;

import ca.gedge.opgraph.OpNode;
import ca.gedge.opgraph.OpNodeAdapter;
import ca.gedge.opgraph.app.components.DoubleClickableTextField;
import ca.gedge.opgraph.app.edits.node.ChangeNodeNameEdit;
import ca.gedge.opgraph.app.util.GUIHelper;

/**
 * Name panel for nodes, includes 
 *  - A left decoration - default is the node icon
 *  - A text field that displays the name of a node. Supports undo/redo.
 *  - A right decoration - default is an empty {@link JLabel}
 */
public class CanvasNodeName extends JPanel {
	
	private JLabel iconDecoration = new JLabel();
	private JComponent leftDecoration = iconDecoration;
	 
	private JLabel defaultRightDecoration = new JLabel();
	private JComponent rightDecoration = defaultRightDecoration;
	
	private JTextField nameField;
	
	/** Double-click support */
	private DoubleClickableTextField doubleClickSupport;

	/** The node whose name this component is showing */
	private OpNode node;

	/** The style used for this component */
	private NodeStyle style;

	/** Undo support for node name edits */
	private UndoableEditSupport undoSupport;

	/**
	 * Constructs a name component that references the given node.
	 * 
	 * @param node  the node
	 * @param style  the node style
	 * 
	 * @throws NullPointerException  if the specified node is <code>null</code> 
	 */
	public CanvasNodeName(OpNode node, NodeStyle style) {
		super();
		
		init(node, style);
		setOpaque(false);
	}
	
	private void init(OpNode node, NodeStyle style) {
		setLayout(new BorderLayout());
		
		this.nameField = new JTextField() {
			@Override
			protected void paintComponent(Graphics gfx) {
				if(doubleClickSupport.isEditing()) {
					super.paintComponent(gfx);
				} else {
					final Graphics2D g = (Graphics2D)gfx;
					final Rectangle rect = GUIHelper.getInterior(this);
					final int halign = nameField.getHorizontalAlignment();
					final int valign = SwingConstants.CENTER;
					final Point p = GUIHelper.placeTextInRectangle(g, nameField.getText(), rect, halign, valign);

					// Draw shadow under text, if necessary
					if(style.NodeNameTextShadowColor != null) {
						g.setColor(style.NodeNameTextShadowColor);
						g.drawString(nameField.getText(), p.x + 1, p.y + 1);
					}

					g.setColor(nameField.getForeground());
					g.drawString(nameField.getText(), p.x, p.y);
				}
			}
		};
		this.nameField.setOpaque(false);
		this.doubleClickSupport = new DoubleClickableTextField(this.nameField);
		
		this.undoSupport = new UndoableEditSupport();
		
		nameField.setHorizontalAlignment(JTextField.CENTER);
		nameField.setFont(getFont().deriveFont(Font.BOLD));
		
		setNode(node);
		setStyle(style);
		
		this.doubleClickSupport.addPropertyChangeListener(DoubleClickableTextField.TEXT_PROPERTY, textListener);
		
		add(leftDecoration, BorderLayout.WEST);
		add(nameField, BorderLayout.CENTER);
		add(rightDecoration, BorderLayout.EAST);
	}
	
	public JTextField getNameField() {
		return this.nameField;
	}

	public JComponent getLeftDecoration() {
		return this.leftDecoration;
	}
	
	public JComponent getRightDecoration() {
		return this.rightDecoration;
	}
	
	/**
	 * Gets the node whose name is displayed by this component.
	 *  
	 * @return the node
	 */
	public OpNode getNode() {
		return node;
	}

	/**
	 * Sets the node whose name is displayed by this component.
	 * 
	 * @param node  the node
	 * 
	 * @throws NullPointerException  if the specified node is <code>null</code>
	 */
	public void setNode(OpNode node) {
		if(node == null) throw new NullPointerException("node cannot be null");

		if(this.node != null)
			this.node.removeNodeListener(nodeListener);

		this.node = node;
		this.node.addNodeListener(nodeListener);

		// Update component
		nameField.setBorder(new EmptyBorder(2, 5, 2, 5));
		nameField.setText(node.getName());
		nameField.setToolTipText(node.getDescription());
	}

	/**
	 * Sets the style used for this node.
	 * 
	 * @param style  the node style
	 */
	public void setStyle(NodeStyle style) {
		this.style = (style == null ? new NodeStyle() : style);

		iconDecoration.setIcon(style.NodeIcon);
		
//		nameField.setBackground(this.style.NodeNameTopColor);
		nameField.setForeground(this.style.NodeNameTextColor);

		revalidate();
		repaint();
	}

	//
	// Overrides
	//

	

	//
	// OpNodeListener
	//

	final OpNodeAdapter nodeListener = new OpNodeAdapter() {
		@Override
		public void nodePropertyChanged(String propertyName, Object oldValue, Object newValue) {
			if(propertyName.equals(OpNode.NAME_PROPERTY))
				nameField.setText((String)newValue);
		}
	}; 

	//
	// DoubleClickableTextField property listener
	//

	private final PropertyChangeListener textListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent e) {
			if(node != null) {
				String t = (String)e.getNewValue();
				if(t == null || t.trim().length() == 0)
					t = node.getName();

				undoSupport.postEdit(new ChangeNodeNameEdit(node, t));
			}
		}
	};

	//
	// UndoableEdit support
	//

	/**
	 * Adds an undoable edit listener to this component.
	 * 
	 * @param listener  the listener to add
	 */
	public void addUndoableEditListener(UndoableEditListener listener) {
		undoSupport.addUndoableEditListener(listener);
	}

	/**
	 * Removes an undoable edit listener from this component.
	 * 
	 * @param listener  the listener to remove
	 */
	public void removeUndoableEditListener(UndoableEditListener listener) {
		undoSupport.removeUndoableEditListener(listener);
	}
}
