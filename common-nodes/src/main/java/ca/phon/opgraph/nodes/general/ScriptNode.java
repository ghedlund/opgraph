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
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.script.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.edits.node.NodeSettingsEdit;
import ca.phon.opgraph.app.extensions.NodeSettings;
import ca.phon.opgraph.exceptions.ProcessingException;
import ca.phon.opgraph.nodes.general.script.*;

/**
 * A node that runs a script. 
 */
@OpNodeInfo(
	name="Script",
	description="Executes a script.",
	category="General",
	showInLibrary=false
)
public class ScriptNode 
	extends OpNode
	implements NodeSettings
{
	private static final Logger LOGGER = Logger.getLogger(ScriptNode.class.getName());

	/** The script engine manager being used */
	private ScriptEngineManager manager;

	/** The script engine being used */
	private ScriptEngine engine;

	/** The scripting language of this node */
	private String language;

	/** The script source */
	private String script;

	/**
	 * Constructs a script node that uses Javascript as its language.
	 */
	public ScriptNode() {
		this(null);
	}

	/**
	 * Constructs a script node that uses a given scripting language.
	 * 
	 * @param language  the name of the language
	 */
	public ScriptNode(String language) {
		this.manager = new ScriptEngineManager();
		this.script = "";
		setScriptLanguage(language);
		putExtension(NodeSettings.class, this);
	}

	/**
	 * Gets the scripting language of this node.
	 * 
	 * @return the name of the scripting language currently used
	 */
	public String getScriptLanguage() {
		return language;
	}

	/**
	 * Sets the scripting language of this node.
	 * 
	 * @param language  the name of a supported language
	 */
	public void setScriptLanguage(String language) {
		language = (language == null ? "" : language);
		if(!language.equals(this.language)) {
			this.language = language;
			this.engine = manager.getEngineByName(language);
			if(this.engine == null) engine = manager.getEngineByExtension(language);
			
			// Only work with invocable script engines
			if(this.engine == null || !(this.engine instanceof Invocable)) {
				this.engine = null;
			} else {
				this.engine.put("Logging", new LoggingHelper());
			}

			reloadFields();
		}
	}

	/**
	 * Gets the script source used in this node.
	 * 
	 * @return the script source
	 */
	public String getScriptSource() {
		return script;
	}

	/**
	 * Sets the script source used in this node.
	 * 
	 * @param script  the script source
	 */
	public void setScriptSource(String script) {
		script = (script == null ? "" : script);
		if(!script.equals(this.script)) {
			this.script = script;
			reloadFields();
		}
	}

	/**
	 * Reload the input/output fields from the script. 
	 */
	private void reloadFields() {
		if(engine != null) {
			try {
				engine.eval(script);

				final List<InputField> fixedInputs =
						getInputFields().stream().filter( f -> f.isFixed() && f != ENABLED_FIELD ).collect( Collectors.toList() );
				final List<OutputField> fixedOutputs =
						getOutputFields().stream().filter( OutputField::isFixed ).collect( Collectors.toList() );
				
				removeAllInputFields();
				removeAllOutputFields();
				
				for(InputField field:fixedInputs) {
					putField(field);
				}
				for(OutputField field:fixedOutputs) {
					putField(field);
				}

				final InputFields inputFields = new InputFields(this);
				final OutputFields outputFields = new OutputFields(this);
				try {
					((Invocable)engine).invokeFunction("init", inputFields, outputFields);
				} catch(NoSuchMethodException exc) {
					LOGGER.fine(exc.getLocalizedMessage());
				}
			} catch(ScriptException exc) {
				LOGGER.warning("Script error: " + exc.getLocalizedMessage());
			}
		}
	}

	//
	// Overrides
	//

	@Override
	public void operate(OpContext context) throws ProcessingException {
		if(engine != null) {
			try {
				// Creating bindings from context
				for(String key : context.keySet())
					engine.put(key, context.get(key));

				// provide logger for script as 'logger'
				Logger logger = Logger.getLogger(Processor.class.getName());
				engine.put("logger", logger);

				// Execute run() method in script
				((Invocable)engine).invokeFunction("run", context);

//				// Put output values in context
//				for(OutputField field : getOutputFields())
//					context.put(field, engine.get(field.getKey()));

				// Erase values
				for(String key : context.keySet())
					engine.put(key, null);
			} catch(ScriptException exc) {
				throw new ProcessingException(null, "Could not execute script script", exc);
			} catch(NoSuchMethodException exc) {
				throw new ProcessingException(null, "No run() method in script", exc);
			}
		}
	}

	//
	// NodeSettings
	//

	/**
	 * Constructs a math expression settings for the given node.
	 */
	public static class ScriptNodeSettings extends JPanel {
		
		private GraphDocument document;
		
		/**
		 * Constructs a component for editing a {@link ScriptNode}'s settings.
		 * 
		 * @param node  the {@link ScriptNode}
		 */
		public ScriptNodeSettings(final ScriptNode node, final GraphDocument document) {
			super(new GridBagLayout());
			
			this.document = document;

			// Script source components
			final JEditorPane sourceEditor = new JEditorPane() {
				@Override
				public boolean getScrollableTracksViewportWidth() {
					// Only track width if the preferred with is less than the viewport width
					if(getParent() != null)
						return (getUI().getPreferredSize(this).width <= getParent().getSize().width);
					return super.getScrollableTracksViewportWidth();
				}

				@Override
				public Dimension getPreferredSize() {
					// Add a little for the cursor
					final Dimension dim = super.getPreferredSize();
					//dim.width += 5;
					return dim;
				}
			};

			sourceEditor.setText(node.getScriptSource());
			sourceEditor.setCaretPosition(0);

			sourceEditor.addCaretListener(new CaretListener() {
				@Override
				public void caretUpdate(CaretEvent e) {
					try {
						final Rectangle rect = sourceEditor.modelToView(e.getMark());
						if(rect != null) {
							rect.width += 5;
							rect.height += 5;
							sourceEditor.scrollRectToVisible(rect);
						}
					} catch(BadLocationException exc) {}
				}
			});

			sourceEditor.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent e) {
					// Post an undoable edit
					if(document != null) {
						final Properties settings = new Properties();
						settings.put(SCRIPT_KEY, sourceEditor.getText());
						document.getUndoSupport().postEdit(new NodeSettingsEdit(node, settings));
					} else {
						node.setScriptSource(sourceEditor.getText());
					}
				}

				@Override
				public void focusGained(FocusEvent e) {}
			});

			final JScrollPane sourcePane = new JScrollPane(sourceEditor);
			sourcePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			sourcePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			// Script language components
			final Vector<ScriptEngineFactory> factories = new Vector<ScriptEngineFactory>();
			final Vector<String> languageChoices = new Vector<String>();

			factories.add(null);
			languageChoices.add("<no language>");
			for(ScriptEngineFactory factory : (new ScriptEngineManager()).getEngineFactories()) {
				factories.add(factory);
				languageChoices.add(factory.getLanguageName());
			}

			final JComboBox<String> languageBox = new JComboBox<>(languageChoices);
			languageBox.setEditable(false);
			languageBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// Post an undoable edit
					final ScriptEngineFactory factory = factories.get(languageBox.getSelectedIndex());
					if(document != null) {
						final Properties settings = new Properties();
						settings.put(LANGUAGE_KEY, factory == null ? "" : factory.getLanguageName());
						document.getUndoSupport().postEdit(new NodeSettingsEdit(node, settings));
					} else {
						node.setScriptLanguage(factory == null ? null : factory.getLanguageName());
					}

					// Update editor kit
					final int ss = sourceEditor.getSelectionStart();
					final int se = sourceEditor.getSelectionEnd();
					final String source = sourceEditor.getText();

					// TODO editor kit with syntax highlighting
					sourceEditor.setContentType("text/plain");					
					sourceEditor.setText(source);
					sourceEditor.select(ss, se);
				}
			});

			languageBox.setSelectedItem(node.getScriptLanguage());

			// Add components
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.EAST;
			add(new JLabel("Script Language: "), gbc);

			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.EAST;
			add(languageBox, gbc);

			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.gridwidth = 2;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.fill = GridBagConstraints.BOTH;
			add(sourcePane, gbc);

			// Put the cursor at the beginning of the document
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					sourceEditor.select(0, 0);
				}
			});
		}
	}

	private static final String LANGUAGE_KEY = "scriptLanguage";
	private static final String SCRIPT_KEY = "scriptSource";

	@Override
	public Component getComponent(GraphDocument document) {
		return new ScriptNodeSettings(this, document);
	}

	@Override
	public Properties getSettings() {
		final Properties props = new Properties();
		props.setProperty(LANGUAGE_KEY, getScriptLanguage());
		props.setProperty(SCRIPT_KEY, getScriptSource());
		return props;
	}

	@Override
	public void loadSettings(Properties properties) {
		if(properties.containsKey(LANGUAGE_KEY))
			setScriptLanguage(properties.getProperty(LANGUAGE_KEY));

		if(properties.containsKey(SCRIPT_KEY))
			setScriptSource(properties.getProperty(SCRIPT_KEY));
	}
}
