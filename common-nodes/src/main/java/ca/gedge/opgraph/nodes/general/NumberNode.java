package ca.gedge.opgraph.nodes.general;

import java.awt.*;
import java.text.*;
import java.util.Properties;

import javax.swing.*;

import ca.gedge.opgraph.*;
import ca.gedge.opgraph.app.GraphDocument;
import ca.gedge.opgraph.app.extensions.NodeSettings;

@OpNodeInfo(category="Objects", description="Number value", name="Number", showInLibrary=true)
public class NumberNode extends ConstantValueNode implements NodeSettings {
	
	/* UI */
	private JPanel settingsPanel;
	private JFormattedTextField numberField;
	
	private OutputField intOut = new OutputField("intValue", "integer value", true, Integer.class);
	
	private OutputField floatOut = new OutputField("floatValue", "floating point value", true, Float.class);

	public NumberNode() {
		this(new Double(0.0));
	}
	
	public NumberNode(Number number) {
		super(number);
		
		putField(intOut);
		putField(floatOut);
		
		putExtension(NodeSettings.class, this);
	}
	
	@Override
	public void operate(OpContext context) {
		super.operate(context);
		
		if(context.isActive(intOut))
			context.put(intOut, new Integer(getNumber().intValue()));
		if(context.isActive(floatOut))
			context.put(floatOut, new Float(getNumber().floatValue()));
	}
	
	public Number getNumber() {
		return (numberField != null ? (Number)numberField.getValue() : (Number)getValue());
	}
	
	public void setNumber(Number number) {
		setValue(number);
		if(numberField != null) numberField.setValue(number);
	}

	@Override
	public Component getComponent(GraphDocument document) {
		if(settingsPanel == null) {
			settingsPanel = new JPanel(new BorderLayout());
			
			final NumberFormat nf = NumberFormat.getNumberInstance();
			numberField = new JFormattedTextField(nf);
			numberField.setValue(super.getValue());
			numberField.addPropertyChangeListener("value", (e) -> {
				setNumber((Number)numberField.getValue());
			});
			
			settingsPanel.add(numberField, BorderLayout.NORTH);
		}
		return settingsPanel;
	}

	@Override
	public Properties getSettings() {
		Properties props = new Properties();
		props.setProperty(NumberNode.class.getName() + ".number", NumberFormat.getNumberInstance().format(getNumber()));
		return props;
	}

	@Override
	public void loadSettings(Properties properties) {
		try {
			setNumber(NumberFormat.getNumberInstance().parse(properties.getProperty(NumberNode.class.getName() + ".number", "0.0")));
		} catch (ParseException e) {
			
		}
	}

}
