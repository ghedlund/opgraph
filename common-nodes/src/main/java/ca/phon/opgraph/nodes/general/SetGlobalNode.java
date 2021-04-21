package ca.phon.opgraph.nodes.general;

import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OpNodeInfo;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.extensions.NodeSettings;
import ca.phon.opgraph.exceptions.ProcessingException;
import org.w3c.dom.Node;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

@OpNodeInfo(name="Set Global", description = "Set global variable", category = "General", showInLibrary = true)
public class SetGlobalNode extends OpNode implements NodeSettings {

	private final InputField globalNameInput = new InputField("globalName", "Global variable name",
		true, true, String.class);

	private final InputField globalValueInput = new InputField("value", "Value for global variable",
			false, true, Object.class);

	private JPanel settingsPanel;
	private JTextField globalNameField;

	public SetGlobalNode() {
		super();

		putField(globalNameInput);
		putField(globalValueInput);

		putExtension(NodeSettings.class, this);
	}

	@Override
	public void operate(OpContext context) throws ProcessingException {
		OpContext globalContext = context;
		while(globalContext.getParent() != null)
			globalContext = globalContext.getParent();

		String globalName = context.get(globalNameInput).toString();
		Object value = context.get(globalValueInput);

		globalContext.put(globalName, value);
	}

	public String getGlobalName() {
		return (this.globalNameField != null ? this.globalNameField.getText().trim() :  this.globalName);
	}

	public void setGlobalName(String globalName) {
		this.globalName = globalName.trim();
		if(this.globalNameField != null)
			this.globalNameField.setText(this.globalName);
	}

	@Override
	public Component getComponent(GraphDocument document) {
		if(this.settingsPanel == null) {
			this.settingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

			this.settingsPanel.add(new JLabel("Global name:"));
			this.globalNameField = new JTextField();
			this.globalNameField.setColumns(30);
			this.globalNameField.setText(this.globalName);
			this.settingsPanel.add(this.globalNameField);
		}
		return this.settingsPanel;
	}

	@Override
	public Properties getSettings() {
		Properties props = new Properties();
		props.put("globalName", getGlobalName());
		return props;
	}

	@Override
	public void loadSettings(Properties properties) {
		String globalName = properties.getProperty("globalName");
		setGlobalName(globalName);
	}

}
