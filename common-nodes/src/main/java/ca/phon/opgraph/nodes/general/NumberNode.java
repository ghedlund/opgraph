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
package ca.phon.opgraph.nodes.general;

import java.awt.*;
import java.text.*;
import java.util.Properties;

import javax.swing.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.extensions.NodeSettings;

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
