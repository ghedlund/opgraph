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
package ca.phon.opgraph.app.components;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import javax.swing.text.*;

/**
 * A text field that begins editing on double-click. When the ENTER key is
 * pressed, editing finishes and the text is updated. When the ESCAPE key is
 * pressed, editing finishes and the text reverts to its state before editing
 * began.
 */
public class DoubleClickableTextField {
	/** The property name for the editing property */
	public static final String EDITING_PROPERTY = "editingValue";

	/** The property name for the text property */
	public static final String TEXT_PROPERTY = "textValue";

	/** Property change support */
	private final PropertyChangeSupport propertyChangeSupport;

	/** The text component we have attached to */
	private final JTextComponent textComponent;

	/** The currently set highlighter */
	private Highlighter highlighter;

	/** The currently set bg color */
	private Color bgColor;

	/** The text before editing began */
	private String oldText;
	
	/** Text color before editing */
	private Color oldTextColor;

	/** Whether or not we are currently editing */
	private boolean editing;

	/**
	 * Constructs a double-clickable text field.
	 * 
	 * @param textComponent  the text component to attach to
	 */
	public DoubleClickableTextField(JTextComponent textComponent) {
		this.editing = false;
		this.propertyChangeSupport = new PropertyChangeSupport(this);

		this.textComponent = textComponent;
		this.textComponent.setFocusable(false);
		this.textComponent.setOpaque(false);
		this.textComponent.setDragEnabled(false);
		this.textComponent.addMouseListener(mouseAdapter);
		this.textComponent.addKeyListener(keyAdapter);
		this.textComponent.addFocusListener(focusAdapter);

		this.highlighter = textComponent.getHighlighter();
		this.bgColor = textComponent.getBackground();
	}

	/**
	 * Gets whether or not we are currently editing.
	 * 
	 * @return  <code>true</code> if currently editing, <code>false</code> otherwise
	 */
	public boolean isEditing() {
		return editing;
	}

	/**
	 * Sets whether or not we are editing the name.
	 * 
	 * @param editing  <code>true</code> if editing, <code>false</code> otherwise
	 */
	public void setEditing(boolean editing) {
		if(textComponent.isEditable() && this.editing != editing) {
			this.editing = editing;

			if(editing) {
				oldText = textComponent.getText();
				oldTextColor = textComponent.getForeground();
			}

			textComponent.setFocusable(editing);
			textComponent.setOpaque(editing);
			textComponent.setForeground(editing ? Color.black : oldTextColor);
			textComponent.setBackground(editing ? bgColor : null);
			textComponent.setHighlighter(editing ? highlighter : null);

			if(editing) {
				textComponent.requestFocusInWindow();
				textComponent.selectAll();
			} else {
				propertyChangeSupport.firePropertyChange(TEXT_PROPERTY, oldText, textComponent.getText());
			}

			propertyChangeSupport.firePropertyChange(EDITING_PROPERTY, !editing, editing);
		}
	}

	//
	// MouseAdapter
	//

	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			if(e.getClickCount() > 1 && !e.isPopupTrigger()) {
				setEditing(true);
			}
		}
	};

	//
	// KeyAdapter
	//

	private final KeyAdapter keyAdapter = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				if((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != InputEvent.SHIFT_DOWN_MASK) {
					// Force an update of text
					//textComponent.setText(textComponent.getText()); 
					setEditing(false);
				}
			} else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				// Reset to text before editing
				textComponent.setText(oldText);
				setEditing(false);
			}
		}
	};

	//
	// FocusAdapter
	//

	private final FocusAdapter focusAdapter = new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			textComponent.setText(textComponent.getText());
			setEditing(false);
		}
	};

	//
	// PropertyChange listener support
	//

	/**
	 * Adds a property change listener to this component.
	 * 
	 * @param listener  the listener to add
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Adds a property change listener for a specific property to this component.
	 * 
	 * @param propertyName  the property name
	 * @param listener  the listener to add
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Removes a property change listener from this component.
	 * 
	 * @param listener  the listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * Removes a property change listener for a specific property from this component.
	 * 
	 * @param propertyName  the property name
	 * @param listener  the listener to remove
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}
}
