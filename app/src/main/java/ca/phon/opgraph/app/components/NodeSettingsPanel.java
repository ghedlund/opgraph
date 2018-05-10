/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
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
package ca.phon.opgraph.app.components;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.extensions.NodeSettings;

/**
 * A panel for displaying and editing settings for a node. Settings are
 * available whenever the node has the {@link NodeSettings} extension. 
 * 
 */
public class NodeSettingsPanel extends JPanel {
	/** The node currently being viewed */
	private OpNode node;
	
	private GraphDocument document;

	/**
	 * Default constructor.
	 */
	public NodeSettingsPanel(GraphDocument doc) {
		super(new BorderLayout());
		
		this.document = doc;
		
		setNode(null);
		setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	/**
	 * Gets the node this info panel is currently viewing.
	 * 
	 * @return the node
	 */
	public OpNode getNode() {
		return node;
	}
	
	protected Component getNodeSettingsComponent(NodeSettings settings) {
		final Component settingsComp = settings.getComponent(document);
		return settingsComp;
	}

	/**
	 * Sets the node this panel is currently viewing.
	 * 
	 * @param node  the node to display
	 */
	public void setNode(OpNode node) {
		if(this.node != node || getComponentCount() == 0) {
			this.node = node;

			// Clear all current components and add in new ones
			removeAll();

			// Get the settings component
			Component settingsComp = null;
			if(node == null) {
				final JLabel label = new JLabel("No node selected", SwingConstants.CENTER);
				label.setFont(label.getFont().deriveFont(Font.ITALIC));
				settingsComp = label;
			} else {
				final NodeSettings settings = node.getExtension(NodeSettings.class);
				if(settings != null)
					settingsComp = getNodeSettingsComponent(settings);

				if(settingsComp == null) {
					final JLabel label = new JLabel("No settings available", SwingConstants.CENTER);
					label.setFont(label.getFont().deriveFont(Font.ITALIC));
					settingsComp = label;
				}
			}

			add(settingsComp, BorderLayout.CENTER);

			revalidate();
			repaint();
		}
	}
}
