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

import javax.swing.*;
import javax.swing.border.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.extensions.*;

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
