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
package ca.phon.opgraph.app.edits.node;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ca.phon.opgraph.ContextualItem;
import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.extensions.Publishable;

/**
 * Publishes or unpublishes inputs/outputs of a node.
 */
public class PublishFieldEdit extends AbstractUndoableEdit {
	/** The graph to which this edit was applied  */
	private OpGraph graph;

	/** The publishable node */
	private Publishable publishable;

	/** The node owning the field to publish */
	private OpNode node;

	/** The published key */
	private String key;

	/** The field to publish */
	private ContextualItem field;

	/** Whether or not we are publishing or unpublishing */
	private boolean isPublishing;

	/**
	 * Constructs a field publishing edit that publishes or unpublishes a
	 * given field for a node in a given publishable extension.
	 * 
	 * @param graph  the graph in which publishing occurs
	 * @param publishable  the publishable extension
	 * @param node  the node owning the field to publish
	 * @param key  the key to use for publishing, or <code>null</code> if unpublishing
	 * @param field  the field to publish
	 */
	public PublishFieldEdit(OpGraph graph,
	                        Publishable publishable,
	                        OpNode node,
	                        String key,
	                        ContextualItem field)
	{
		this.graph = graph;
		this.publishable = publishable;
		this.node = node;
		this.field = field;
		this.isPublishing = (key != null);

		if(key == null) {
			ContextualItem item = null;
			if(field instanceof InputField)
				item = publishable.getPublishedInput(node, (InputField)field);
			else if(field instanceof OutputField)
				item = publishable.getPublishedOutput(node, (OutputField)field);

			key = (item == null ? field.getKey() : item.getKey());
		} else {
			final OpNode publishNode = ((publishable instanceof OpNode) ? (OpNode)publishable : null);

			// Find a key that isn't already taken
			if(field instanceof InputField) {
				int val = 1;
				String keyToUse = key;
				ContextualItem item = publishNode.getInputFieldWithKey(keyToUse);
				while(item != null) {
					keyToUse = key + val;
					item = publishNode.getInputFieldWithKey(keyToUse);
					++val;
				}
				key = keyToUse;
			} else if(field instanceof OutputField) {
				int val = 1;
				String keyToUse = key;
				ContextualItem item = publishNode.getOutputFieldWithKey(keyToUse);
				while(item != null) {
					keyToUse = key + val;
					item = publishNode.getOutputFieldWithKey(keyToUse);
					++val;
				}
				key = keyToUse;
			}
		}

		this.key = key;

		perform();
	}

	/**
	 * Performs this edit.
	 */
	private void perform() {
		if(isPublishing)
			publish();
		else
			unpublish();
	}

	/**
	 * Publishes a field of a node.
	 * 
	 * @param macro  the macro node on which we will publish
	 * @param key  the key to use for the published input 
	 * @param node  the node in the given macro node whose field will be published 
	 * @param field  the field to publish
	 */
	private void publish() {
		if(field instanceof InputField) {
			final InputField input = (InputField)field;
			if(publishable.getPublishedInput(node, input) == null)
				publishable.publish(key, node, input);
		} else if(field instanceof OutputField) {
			final OutputField output = (OutputField)field;
			if(publishable.getPublishedOutput(node, output) == null)
				publishable.publish(key, node, output);
		}
	}

	/**
	 * Unpublishes a field from a node.
	 */
	private void unpublish() {
		if(field instanceof InputField) {
			final InputField input = (InputField)field;
			final InputField publishedInput = publishable.getPublishedInput(node, input);
			if(publishedInput != null)
				publishable.unpublish(node, input);
		} else if(field instanceof OutputField) {
			final OutputField output = (OutputField)field;
			final OutputField publishedOutput = publishable.getPublishedOutput(node, output); 
			if(publishedOutput != null)
				publishable.unpublish(node, output);
		}
	}

	//
	// AbstractEdit overrides
	//

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		perform();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		if(isPublishing)
			unpublish();
		else
			publish();
	}

	@Override
	public String getPresentationName() {
		final StringBuilder builder = new StringBuilder();
		if(field instanceof InputField) {
			builder.append(isPublishing ? "Publish '" : "Unpublish '");
			builder.append(field.getKey());
			builder.append("' Input");
		} else if(field instanceof OutputField) {
			builder.append(isPublishing ? "Publish '" : "Unpublish '");
			builder.append(field.getKey());
			builder.append("' Output");
		}
		return builder.toString();
	}

	@Override
	public boolean isSignificant() {
		if(publishable != null && node != null && field != null && graph.contains(node)) {
			return ((field instanceof InputField) && node.getInputFields().contains(field))
					|| ((field instanceof OutputField) && node.getOutputFields().contains(field));
		}
		return false;
	}
}
