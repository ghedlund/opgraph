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
package ca.phon.opgraph.app.commands.publish;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.GraphEditorModel;
import ca.phon.opgraph.extensions.Publishable;

/**
 * A menu that allows one to control the publishing of inputs and outputs
 * of a given node.
 */
public class PublishedFieldMenuHelper {
	/**
	 * Constructs a published field menu
	 * 
	 * @param menu  the menu to populate
	 * @param publishable  the {@link Publishable} node
	 * @param node  the node with fields to publish
	 * @param isInputs  <code>true</code> to show published inputs,
	 *                  <code>false</code> to show outputs
	 */
	public static void populate(JMenu menu, GraphDocument document, Publishable publishable, OpNode node, boolean isInputs) {
		if(document != null && document.getBreadcrumb().peekState(1) != null) {
			if(isInputs) {
				for(InputField field : node.getInputFields()) {
					final AbstractAction action = new PublishFieldCommand(document, publishable, node, field);
					final JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
					item.setSelected(publishable.getPublishedInput(node, field) != null);
					menu.add(item);
				}
			} else {
				for(OutputField field : node.getOutputFields()) {
					final AbstractAction action = new PublishFieldCommand(document, publishable, node, field);
					final JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
					item.setSelected(publishable.getPublishedOutput(node, field) != null);
					menu.add(item);
				}
			}
		}
	}
}
