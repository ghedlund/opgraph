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
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.extensions.*;

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
			new InputField("objects", "array of objects for formatted strings", true, true, Object[].class);
	
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
		} else {
			context.put(VALUE_OUTPUT_FIELD, super.getValue().toString());
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
