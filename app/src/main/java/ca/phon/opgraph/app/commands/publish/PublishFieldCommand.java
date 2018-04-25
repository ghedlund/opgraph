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
/**
 * 
 */
package ca.phon.opgraph.app.commands.publish;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;

import ca.phon.opgraph.ContextualItem;
import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.OpLink;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.GraphEditorModel;
import ca.phon.opgraph.app.commands.HookableCommand;
import ca.phon.opgraph.app.edits.graph.RemoveLinkEdit;
import ca.phon.opgraph.app.edits.node.PublishFieldEdit;
import ca.phon.opgraph.extensions.Publishable;

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
