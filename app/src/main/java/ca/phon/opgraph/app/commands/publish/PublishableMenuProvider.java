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
/**
 * 
 */
package ca.phon.opgraph.app.commands.publish;

import java.awt.event.MouseEvent;
import javax.swing.JMenu;

import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.GraphEditorModel;
import ca.phon.opgraph.app.MenuProvider;
import ca.phon.opgraph.app.commands.publish.PublishedFieldMenuHelper;
import ca.phon.opgraph.app.components.PathAddressableMenu;
import ca.phon.opgraph.extensions.Publishable;

/**
 * Menu provider for publishing input/output fields.
 */
public class PublishableMenuProvider implements MenuProvider {
	@Override
	public void installItems(final GraphEditorModel model, PathAddressableMenu menu) {
		// Nothing to do, unless the publish menus should be available beyond
		// the popup level
	}

	@Override
	public void installPopupItems(Object context, MouseEvent event, GraphDocument doc, PathAddressableMenu menu) {
		if(context != null && (context instanceof OpNode)) {
			final OpNode node = (OpNode)context;
			final Publishable publishable = doc.getGraph().getExtension(Publishable.class);

			if(publishable != null) {
				menu.addSeparator("");

				final JMenu inputs = menu.addMenu("published_inputs", "Publish Inputs");
				final JMenu outputs = menu.addMenu("published_outputs", "Publish Outputs");

				PublishedFieldMenuHelper.populate(inputs, doc, publishable, node, true);
				PublishedFieldMenuHelper.populate(outputs, doc, publishable, node, false);
			}
		}
	}
}
