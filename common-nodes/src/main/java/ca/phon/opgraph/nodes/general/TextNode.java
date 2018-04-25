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
import javax.swing.event.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.extensions.NodeSettings;

/**
 * Node for creating a new string.
 * 
 */
@OpNodeInfo(category="Objects", description="Text node", name="Text", showInLibrary=true)
public class TextNode extends ConstantValueNode implements NodeSettings {
	
	/* Settings UI */
	private JPanel settingsPanel;
	private JTextArea textArea;
	
	private InputField objectsInputs = 
			new InputField("objects", "array of objects for formatted strings", false, true, Object[].class);
	
	public TextNode() {
		this("");
	}
	
	public TextNode(String text) {
		super();
			
		setValue(text);
		
		putField(objectsInputs);
		
		putExtension(NodeSettings.class, this);
	}
	
	public String getText() {
		return (textArea != null ? textArea.getText() : getValue().toString());
	}
	
	public void setText(String text) {
		setValue(text);
		if(textArea != null) textArea.setText(text);
	}

	@Override
	public void operate(OpContext context) {
		super.operate(context);
		
		if(context.get(objectsInputs) != null) {
			Object[] objArray = (Object[])context.get(objectsInputs);
			
			final String value = String.format(super.getValue().toString(), objArray);
			context.put(VALUE_OUTPUT_FIELD, value);
		}
	}
	
	@Override
	public Component getComponent(GraphDocument document) {
		if(settingsPanel == null) {
			settingsPanel = new JPanel(new BorderLayout());
			
			textArea = new JTextArea(getText());
			final JScrollPane scroller = new JScrollPane(textArea);
			textArea.getDocument().addDocumentListener( new DocumentListener() {
				
				@Override
				public void removeUpdate(DocumentEvent e) {
					setValue(textArea.getText());
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					setValue(textArea.getText());
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
				}
				
			});
			
			settingsPanel.add(scroller, BorderLayout.CENTER);
		}
		return settingsPanel;
	}

	@Override
	public Properties getSettings() {
		final Properties props = new Properties();
		props.put(TextNode.class.getName() + ".text", getText());
		return props;
	}

	@Override
	public void loadSettings(Properties properties) {
		setText(properties.getProperty(TextNode.class.getName() + ".text", ""));
	}
	
}
