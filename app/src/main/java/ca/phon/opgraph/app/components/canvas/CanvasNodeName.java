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
import java.beans.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.components.*;
import ca.phon.opgraph.app.edits.node.*;
import ca.phon.opgraph.app.util.*;

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
		public void nodePropertyChanged(OpNode node, String propertyName, Object oldValue, Object newValue) {
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
