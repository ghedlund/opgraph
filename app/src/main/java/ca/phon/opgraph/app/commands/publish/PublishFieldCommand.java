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
/**
 * 
 */
package ca.phon.opgraph.app.commands.publish;

import java.awt.event.*;
import java.util.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.commands.*;
import ca.phon.opgraph.app.edits.graph.*;
import ca.phon.opgraph.app.edits.node.*;
import ca.phon.opgraph.extensions.*;

/**
 * A command to publish a field in a node.
 */
class PublishFieldCommand extends HookableCommand {
	/** The publishable object */
	private Publishable publishable;

	/** The node with a field to be published */
	private OpNode node;

	/** The field to publish */
	private ContextualItem field;
	
	/** The graph document */
	private GraphDocument document;

	/**
	 * Constructs a command that publishes a field of a given node.
	 * 
	 * @param publishable  the publishing extension
	 * @param node  the node with a field to publish
	 * @param field  the field to publish
	 */
	public PublishFieldCommand(GraphDocument document, Publishable publishable, OpNode node, ContextualItem field) {
		super(field.getKey());

		this.document = document;
		this.publishable = publishable;
		this.node = node;
		this.field = field;

		putValue(SHORT_DESCRIPTION, field.getDescription());
		putValue(LONG_DESCRIPTION, field.getDescription());
	}

	/**
	 * If currently editing , publish the given field of the selected node.
	 * 
	 * @param field  the field to publish
	 */
	public void publishFieldOfSelected(ContextualItem field) {
		if(publishable != null && field != null && node != null) {
			document.getUndoSupport().postEdit(new PublishFieldEdit(document.getGraph(), publishable, node, field.getKey(), field));
			document.firePropertyChange("anchorFillStates", new Object(), node);
		}
	}

	/**
	 * If currently editing a macro, unpublish the given field of the selected node.
	 * 
	 * @param field  the field to unpublish
	 */
	public void unpublishFieldOfSelected(ContextualItem field) {
		if(publishable != null && field != null && node != null) {
			final OpGraph graphOfMacroParent = document.getBreadcrumb().peekState(1);
			final OpNode publishNode = ((publishable instanceof OpNode) ? (OpNode)publishable : null);

			// If there is a parent graph, then check to see which links will
			// be removed if this field is unpublished
			final Collection<OpLink> linksToRemove = new ArrayList<OpLink>();
			if(graphOfMacroParent != null) {
				// For input fields, check incoming links, otherwise outgoing
				if(field instanceof InputField) {
					final InputField publishedField = publishable.getPublishedInput(node, (InputField)field);
					for(OpLink link : graphOfMacroParent.getIncomingEdges(publishNode)) {
						if(link.getDestinationField().equals(publishedField))
							linksToRemove.add(link);
					}
				} else if(field instanceof OutputField) {
					final OutputField publishedField = publishable.getPublishedOutput(node, (OutputField)field);
					for(OpLink link : graphOfMacroParent.getOutgoingEdges(publishNode)) {
						if(link.getSourceField().equals(publishedField))
							linksToRemove.add(link);
					}
				}
			}

			// Compound edit if there are links to remove
			if(linksToRemove.size() == 0) {
				document.getUndoSupport().postEdit(new PublishFieldEdit(document.getGraph(), publishable, node, null, field));
			} else {
				document.getUndoSupport().beginUpdate();
				for(OpLink link : linksToRemove)
					document.getUndoSupport().postEdit(new RemoveLinkEdit(graphOfMacroParent, link));
				document.getUndoSupport().postEdit(new PublishFieldEdit(document.getGraph(), publishable, node, null, field));
				document.getUndoSupport().endUpdate();
			}

			document.firePropertyChange("anchorFillStates", new Object(), node);
//			model.getCanvas().updateAnchorFillStates(node);
		}
	}

	//
	// Overrides
	//

	@Override
	public void hookableActionPerformed(ActionEvent e) {
		boolean isPublishing = true;
		if((field instanceof InputField) && publishable.getPublishedInput(node, (InputField)field) != null)
			isPublishing = false;
		else if((field instanceof OutputField) && publishable.getPublishedOutput(node, (OutputField)field) != null)
			isPublishing = false;

		if(isPublishing)
			publishFieldOfSelected(field);
		else
			unpublishFieldOfSelected(field);
	}
}
