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
package ca.phon.opgraph.nodes.menu;

import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.components.*;
import ca.phon.opgraph.app.components.canvas.*;
import ca.phon.opgraph.nodes.general.*;

/**
 * 
 */
public class CommonNodesMenuProvider implements MenuProvider {
	@Override
	public void installItems(final GraphEditorModel model, PathAddressableMenu menu) {
		menu.addSeparator("graph");

		final JMenuItem create = menu.addMenuItem("graph/create macro", new CreateMacroCommand(model.getDocument()));
		final JMenuItem explode = menu.addMenuItem("graph/explode macro", new ExplodeMacroCommand(model.getDocument()));

		create.setEnabled(false);
		explode.setEnabled(false);

		model.getDocument().addPropertyChangeListener(GraphDocument.PROCESSING_CONTEXT, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if(e.getNewValue() == null) {
					final Collection<OpNode> selected = model.getCanvas().getSelectionModel().getSelectedNodes();

					boolean canExplode = false;
					if(selected.size() == 1)
						canExplode = (selected.iterator().next() instanceof MacroNode);

					create.setEnabled(selected.size() > 0);
					explode.setEnabled(canExplode);
				} else { 
					create.setEnabled(false);
					explode.setEnabled(false);
				}
			}
		});

		final GraphCanvasSelectionModel selectionModel = model.getCanvas().getSelectionModel();
		selectionModel.addSelectionListener(new GraphCanvasSelectionListener() {
			@Override
			public void nodeSelectionChanged(Collection<OpNode> old, Collection<OpNode> selected) {
				boolean canExplode = false;
				if(selected.size() == 1)
					canExplode = (selected.iterator().next() instanceof MacroNode);

				create.setEnabled(selected.size() > 0);
				explode.setEnabled(canExplode);
			}
		});
	}

	@Override
	public void installPopupItems(Object context, MouseEvent event, GraphDocument model, PathAddressableMenu menu) {
		// Nothing to do
	}
}
