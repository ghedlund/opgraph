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
package ca.phon.opgraph.extensions;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ca.phon.opgraph.InputField;

/**
 * The metadata this graph stores with its nodes.
 */
public class NodeMetadata {
	//
	// Properties
	//

	/** Key for the location property */
	public static final String LOCATION_PROPERTY = "nodeLocation";

	/** Key for the defaults property */
	public static final String DEFAULTS_PROPERTY = "nodeDefaults";

	/** Support member for property change listeners */
	private final PropertyChangeSupport changeSupport;

	//
	// NodeMetadata
	//

	/** Coordinates on a canvas */
	private Point p;

	/** A map of default values for a node's inputs */
	private Map<InputField, Object> defaults;

	/**
	 * Constructs metadata with a node located at the origin.
	 */
	public NodeMetadata() {
		this(0, 0);
	}

	/**
	 * Constructs metadata with a node located at the specified coordinates.
	 * 
	 * @param x  the initial x-coordinate
	 * @param y  the initial y-coordinate
	 */
	public NodeMetadata(int x, int y) {
		this.p = new Point(x, y);
		this.defaults = new HashMap<InputField, Object>();	
		this.changeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * Gets the y-coordinate.
	 * 
	 * @return the y-coordinate
	 */
	public int getY() {
		return p.y;
	}

	/**
	 * Sets the y-coordinate.
	 * 
	 * @param y  the y-coordinate
	 */
	public void setY(int y) {
		if(p.y != y) {
			Point oldP = p;
			p = new Point(p.x, y);
			changeSupport.firePropertyChange(LOCATION_PROPERTY, oldP, new Point(p.x, p.y));
		}
	}

	/**
	 * Gets the x-coordinate.
	 * 
	 * @return the x-coordinate
	 */
	public int getX() {
		return p.x;
	}

	/**
	 * Sets the x-coordinate.
	 * 
	 * @param x  the x-coordinate
	 */
	public void setX(int x) {
		if(p.x != x) {
			Point oldP = p;
			p = new Point(x, p.y);
			changeSupport.firePropertyChange(LOCATION_PROPERTY, oldP, new Point(p.x, p.y));
		}
	}

	/**
	 * Sets the location.
	 * 
	 * @param x  the x-coordinate
	 * @param y  the y-coordinate
	 */
	public void setLocation(int x, int y) {
		if(p.x != x || p.y != y) {
			Point oldP = p;
			p = new Point(x, y);
			changeSupport.firePropertyChange(LOCATION_PROPERTY, oldP, new Point(p.x, p.y));
		}
	}

	/**
	 * Gets the mapping of input fields to default values.
	 * 
	 * @return an immutable mapping of defaults
	 */
	public Map<InputField, Object> getDefaults() {
		// First we're going to remove any null values, so that if any class
		// checks for an empty defaults map, it'll be correct
		final ArrayList<InputField> fields = new ArrayList<InputField>(defaults.keySet());
		for(InputField field : fields) {
			if(defaults.get(field) == null)
				defaults.remove(field);
		}

		return Collections.unmodifiableMap(defaults);
	}

	/**
	 * Gets the default value for the given input field.
	 * 
	 * @param field  the input field
	 * 
	 * @return the default value, or <code>null</code> if no default value
	 *         exists for the given input field.
	 */
	public Object getDefault(InputField field) {
		return defaults.get(field);
	}

	/**
	 * Sets the default value for the given input field.
	 * 
	 * @param field  the input field
	 * @param value  the default value, or <code>null</code> if the given
	 *               input field should have no default value
	 */
	public void setDefault(InputField field, Object value) {
		final Object oldValue = defaults.get(field);
		if(oldValue == null && value != null) {
			defaults.put(field, value);
			changeSupport.firePropertyChange(DEFAULTS_PROPERTY, oldValue, value);
		} else if(oldValue != null && value == null) {
			defaults.remove(field);
			changeSupport.firePropertyChange(DEFAULTS_PROPERTY, oldValue, value);
		} else if(oldValue != null && value != null && !oldValue.equals(value)) {
			defaults.put(field, value);
			changeSupport.firePropertyChange(DEFAULTS_PROPERTY, oldValue, value);
		}
	}

	//
	// PropertyChangeListener support
	//

	/**
	 * Adds a property change listener to this metadata.
	 * 
	 * @param listener  the listener to add
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Removes a property change listener from this metadata.
	 * 
	 * @param listener  the listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}
}
