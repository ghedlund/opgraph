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
package ca.phon.opgraph.app.commands.core;

import java.awt.Toolkit;
import java.awt.event.*;
import java.io.*;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.commands.GraphCommand;
import ca.phon.opgraph.app.components.ErrorDialog;
import ca.phon.opgraph.io.*;

/**
 * A command which loads a graph from file.
 */
public class OpenCommand extends GraphCommand {
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(OpenCommand.class.getName());

	/** File chooser */
	private final JFileChooser chooser = new JFileChooser();

	/**
	 * Constructs an open command.
	 */
	public OpenCommand(GraphDocument doc) {
		super("Open...", doc);

		final int CTRL = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, CTRL));
	}

	@Override
	public void hookableActionPerformed(ActionEvent e) {
		// Get the serializer
		final OpGraphSerializer serializer = OpGraphSerializerFactory.getDefaultSerializer();
		if(serializer == null) {
			final String message = "No default serializer available";
			LOGGER.severe(message);
			ErrorDialog.showError(null, message);
			return;
		}

		// Get the serializer's info
		String description = "Opgraph Files";
		String extension = "";

		final OpGraphSerializerInfo info = serializer.getClass().getAnnotation(OpGraphSerializerInfo.class);
		if(info != null) {
			description = info.description();
			extension = info.extension();
		}

		// Save the graph
		if(document != null) {
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileFilter(new FileNameExtensionFilter(description, extension));
			chooser.setDialogTitle("Open Graph");

			final int retVal = chooser.showOpenDialog(null);
			if(retVal == JFileChooser.APPROVE_OPTION) {
				try {
					final FileInputStream stream = new FileInputStream(chooser.getSelectedFile());
					final OpGraph graph = serializer.read(stream);
					document.reset(chooser.getSelectedFile(), graph);
				} catch(IOException exc) {
					LOGGER.severe("Could not read graph from file: " + exc.getMessage());
				}
			}
		}
	}
}
