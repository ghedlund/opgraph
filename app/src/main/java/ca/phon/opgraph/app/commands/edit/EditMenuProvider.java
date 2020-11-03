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
package ca.phon.opgraph.app.commands.edit;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.components.*;
import ca.phon.opgraph.app.components.canvas.*;

/**
 * Menu provider for core functions.
 */
public class EditMenuProvider implements MenuProvider {
	@Override
	public void installItems(final GraphEditorModel model, PathAddressableMenu menu) {
		final JMenu edit = menu.addMenu("edit", "Edit");

		final UndoCommand undo = new UndoCommand(model.getDocument().getUndoManager());
		final RedoCommand redo = new RedoCommand(model.getDocument().getUndoManager());

		final CopyCommand copy = new CopyCommand(model.getDocument(), model.getCanvas());
		final PasteCommand paste = new PasteCommand(model.getDocument());
		final DuplicateCommand duplicate = new DuplicateCommand(model.getDocument());

		menu.addMenuItem("edit/copy", copy);
		menu.addMenuItem("edit/paste", paste);
		menu.addMenuItem("edit/duplicate", duplicate);
		menu.addSeparator("edit");

		menu.addMenuItem("edit/undo", undo);
		menu.addMenuItem("edit/redo", redo);
		menu.addSeparator("edit");
		final JMenuItem delete = menu.addMenuItem("edit/delete", new DeleteCommand(model.getDocument()));
		menu.addMenuItem("edit/select all", new SelectAllCommand(model.getDocument()));

		// Setup backspace keybinding for delete
		final KeyStroke bsKs = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0);
		final InputMap inputMap = delete.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(bsKs, inputMap.get(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)));

		delete.setEnabled(false);

		// Listen to property changes and update menu items to reflect new state
		model.getDocument().addPropertyChangeListener(GraphDocument.UNDO_STATE, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				undo.update();
				redo.update();
			}
		});

		model.getDocument().addPropertyChangeListener(GraphDocument.PROCESSING_CONTEXT, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				edit.setEnabled(evt.getNewValue() == null);
			}
		});

		model.getCanvas().getSelectionModel().addSelectionListener(new GraphCanvasSelectionListener() {
			@Override
			public void nodeSelectionChanged(Collection<OpNode> old, Collection<OpNode> selected) {
				delete.setEnabled(selected.size() > 0);
				duplicate.setEnabled(selected.size() > 0);
			}
		});
	}

	@Override
	public void installPopupItems(Object context, MouseEvent event, GraphDocument doc, PathAddressableMenu menu) {
		// Add copy and paste commands for nodes
		if(context != null && (context instanceof OpNode || context instanceof OpGraph)) {
			// Add copy command if selection is available
			if(doc.getSelectionModel().getSelectedNodes().size() > 0 && event.getSource() instanceof GraphCanvas)
				menu.addMenuItem("copy", new CopyCommand(doc, (GraphCanvas)event.getSource()));

			// Check clipboard
			if(!GraphicsEnvironment.isHeadless()) {
				final Transferable clipboardContents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(context);
				if(clipboardContents != null && clipboardContents.isDataFlavorSupported(SubgraphClipboardContents.copyFlavor)) {
					// Add paste command
					menu.addMenuItem("paste", new PasteCommand(doc));
				}
			}

			// Add duplicate command if selection is available
			if(doc.getSelectionModel().getSelectedNodes().size() > 0)
				menu.addMenuItem("duplicate", new DuplicateCommand(doc));
		}
	}
}
