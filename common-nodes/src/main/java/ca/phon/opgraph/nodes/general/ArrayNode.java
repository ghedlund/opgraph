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
import java.util.*;
import java.util.List;

import javax.swing.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.extensions.*;
import ca.phon.opgraph.exceptions.*;

@OpNodeInfo(name="Array", category="Objects", description="Create an array of n objects", showInLibrary=true)
public class ArrayNode extends OpNode implements NodeSettings {
	
	private final OutputField arrayOutput =
			new OutputField("array", "array of objects", true, Object[].class);

	private final static String NUM_OBJECTS_PROP = ArrayNode.class.getName() + ".numObjects";
	private JPanel settingsPanel;
	private JFormattedTextField numObjectsField;
	
	private int numObjects = 0;
	
	public ArrayNode() {
		super();
		
		putField(arrayOutput);
		
		putExtension(NodeSettings.class, this);
	}
	
	public int getNumObjects() {
		return this.numObjects;
	}
	
	public void setNumObjects(int numObjects) {
		this.numObjects = numObjects;
		updateInputFields(numObjects);
		if(numObjectsField != null && ((Number)numObjectsField.getValue()).intValue() != numObjects) {
			numObjectsField.setValue(numObjects);
		}
	}
	
	private void updateInputFields(int numFields) {
		final List<InputField> inputs = getInputFields();
		int numCurrentInputs = inputs.size()-1;
		if(numCurrentInputs == numFields) return;
		
		if(numCurrentInputs < numFields) {
			while(numCurrentInputs < numFields) {
				final String inputName = "object" + (numCurrentInputs+1);
				final InputField inputField = new InputField(inputName, "object in array", false, false, Object.class);
				putField(inputField);
				++numCurrentInputs;
			}
		} else {
			while(numCurrentInputs > numFields) {
				removeField(getInputFields().get(getInputFields().size()-1));
				--numCurrentInputs;
			}
		}
		
	}

	@Override
	public Component getComponent(GraphDocument document) {
		if(settingsPanel == null) {
			settingsPanel = new JPanel(new BorderLayout());
		
			numObjectsField = new JFormattedTextField(NumberFormat.getIntegerInstance());
			numObjectsField.setValue(new Integer(numObjects));
			numObjectsField.addPropertyChangeListener("value", (e) -> {
				numObjects = ((Number)numObjectsField.getValue()).intValue();
				updateInputFields(numObjects);
			});
			
			settingsPanel.add(numObjectsField, BorderLayout.NORTH);
		}
		return settingsPanel;
	}

	@Override
	public Properties getSettings() {
		Properties retVal = new Properties();
		retVal.setProperty(NUM_OBJECTS_PROP, Integer.toString(getNumObjects()));
		return retVal;
	}

	@Override
	public void loadSettings(Properties properties) {
		setNumObjects(Integer.parseInt(properties.getProperty(NUM_OBJECTS_PROP, "0")));
	}

	@Override
	public void operate(OpContext context) throws ProcessingException {
		final Object[] value = new Object[getNumObjects()];
		
		final List<InputField> inputFields = getInputFields();
		for(int i = 1; i < inputFields.size(); i++) {
			value[i-1] = context.get(inputFields.get(i));
		}
		
		context.put(arrayOutput, value);
	}
	
}
