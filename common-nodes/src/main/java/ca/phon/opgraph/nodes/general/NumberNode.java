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
