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
