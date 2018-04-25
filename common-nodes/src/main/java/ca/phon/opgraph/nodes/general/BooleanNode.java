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
package ca.phon.opgraph.nodes.general;

import java.awt.*;
import java.util.Properties;

import javax.swing.*;

import ca.phon.opgraph.OpNodeInfo;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.extensions.NodeSettings;

@OpNodeInfo(category="Objects", name="Boolean", description="Boolean value", showInLibrary=true)
public class BooleanNode extends ConstantValueNode implements NodeSettings {

	/* UI */
	private JPanel settingsPanel;
	private JCheckBox checkBox;
	
	public BooleanNode() {
		this(Boolean.FALSE);
	}
	
	public BooleanNode(Boolean bool) {
		super(bool);
		
		putExtension(NodeSettings.class, this);
	}

	@Override
	public Component getComponent(GraphDocument document) {
		if(settingsPanel == null) {
			settingsPanel = new JPanel(new BorderLayout());
			
			checkBox = new JCheckBox("value");
			checkBox.setSelected(getBoolean());
			checkBox.addActionListener( (e) -> {
				setBoolean(checkBox.isSelected());
			});
			settingsPanel.add(checkBox, BorderLayout.NORTH);
		}
		return settingsPanel;
	}
	
	public Boolean getBoolean() {
		return (checkBox != null ? checkBox.isSelected() : (Boolean)getValue());
	}
	
	public void setBoolean(Boolean bool) {
		setValue(bool);
		if(checkBox != null) checkBox.setSelected(bool);
	}

	@Override
	public Properties getSettings() {
		Properties props = new Properties();
		props.setProperty(BooleanNode.class.getName() + ".bool", getBoolean().toString());
		return props;
	}

	@Override
	public void loadSettings(Properties properties) {
		setBoolean(Boolean.parseBoolean(properties.getProperty(BooleanNode.class.getName() + ".bool", "false")));
	}

}
