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
