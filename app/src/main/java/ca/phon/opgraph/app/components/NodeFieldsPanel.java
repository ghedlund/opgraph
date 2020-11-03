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
package ca.phon.opgraph.app.components;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.List;
import java.util.logging.*;
import java.util.stream.*;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.components.canvas.*;
import ca.phon.opgraph.app.edits.graph.*;
import ca.phon.opgraph.dag.*;
import ca.phon.opgraph.exceptions.*;
import ca.phon.opgraph.extensions.*;
import ca.phon.opgraph.util.*;
import ca.phon.opgraph.validators.*;
import ca.phon.ui.jbreadcrumb.BreadcrumbEvent.*;

/**
 * A panel for displaying and editing default values for a node's input fields.
 *
 * TODO undoable edits for defaults
 */
public class NodeFieldsPanel extends JPanel {
	
	private static ImageIcon REMOVE_LINK_ICON;
	static {
		try {
			REMOVE_LINK_ICON = new ImageIcon(ImageIO.read(NodeStyle.class.getClassLoader().getResourceAsStream("data/icons/16x16/opgraph/remove.png")));
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	/** Logger */
	private final Logger LOGGER = Logger.getLogger(NodeFieldsPanel.class.getName());

	/** Graph document */
	private GraphDocument document;

	/** The node currently being viewed */
	private OpNode node;

	private OpGraphListener graphListener = new OpGraphListener() {

		@Override
		public void nodeRemoved(OpGraph graph, OpNode node) {

		}

		@Override
		public void nodeAdded(OpGraph graph, OpNode node) {

		}

		@Override
		public void linkRemoved(OpGraph graph, OpLink link) {
			if(graph != document.getGraph()) return;
			
			if(link.getSource() == getNode() || link.getDestination() == getNode()) {
				updatePanel();
			}
		}

		@Override
		public void linkAdded(OpGraph graph, OpLink link) {
			if(graph != document.getGraph()) return;
			
			if(link.getSource() == getNode() || link.getDestination() == getNode()) {
				updatePanel();
			}
		}

		@Override
		public void nodeSwapped(OpGraph graph, OpNode oldNode, OpNode newNode) {
			// TODO Auto-generated method stub
		}

	};

	/**
	 * Default constructor.
	 */
	public NodeFieldsPanel(GraphDocument document) {
		super(new GridBagLayout());
		setNode(null);

		this.document = document;

		document.getBreadcrumb().addBreadcrumbListener( (e) -> {
			if(e.getEventType() == BreadcrumbEventType.GOTO_STATE) {
				document.getGraph().addGraphListener(graphListener);
			}
		});
		document.getRootGraph().addGraphListener(graphListener);
	}
	
	private List<Pair<OpNode, OutputField>> getOutputsCompatibleWithInput(OpNode node, InputField inputField) {
		List<Pair<OpNode, OutputField>> retVal = new ArrayList<>();
		
		for(OpNode currentNode:document.getGraph()) {
			if(currentNode == node) continue;
			
			for(OutputField outputField:currentNode.getOutputFields()) {
				if(inputField.getValidator() == null || inputField.getValidator().isAcceptable(outputField.getOutputType())) {
					retVal.add(new Pair<>(currentNode, outputField));
				}
			}
		}
		
		return retVal;
	}
	
	private void updatePanel() {
		removeAll();

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;

		final OpGraph graph = document.getGraph();
		final OpNode node = getNode();

		if(node != null) {
			final NodeMetadata nodeMeta = node.getExtension(NodeMetadata.class);
			// input fields
			final JLabel inputFieldsLbl = new JLabel("Inputs");
			inputFieldsLbl.setFont(inputFieldsLbl.getFont().deriveFont(Font.BOLD));
			final JSeparator inputsSeparator = new JSeparator(SwingConstants.HORIZONTAL);

			gbc.weightx = 1.0;
			gbc.gridwidth = 2;
			gbc.fill = GridBagConstraints.HORIZONTAL;

			add(inputFieldsLbl, gbc);
			++gbc.gridy;
			add(inputsSeparator, gbc);

			gbc.insets.set(2, 5, 2, 2);

			for(InputField inputField:node.getInputFields()) {
				++gbc.gridy;
				gbc.gridx = 0;
				gbc.gridwidth = 1;
				gbc.weightx = 0.0;

				final JLabel keyLbl = new JLabel(inputField.getKey());
				keyLbl.setToolTipText(inputField.getDescription());
				keyLbl.setFont(keyLbl.getFont().deriveFont(Font.ITALIC));

				add(keyLbl, gbc);

				++gbc.gridx;
				gbc.weightx = 1.0;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				
				final OpLink currentLink = graph.getIncomingEdges(node)
					.stream().filter( (l) -> l.getDestinationField() == inputField )
					.findAny().orElse(null);

				final List<Pair<OpNode, OutputField>> compatibleOutputs = getOutputsCompatibleWithInput(node, inputField);
				@SuppressWarnings("unchecked")
				final Pair<OpNode, OutputField>[] outputArray = compatibleOutputs.toArray(new Pair[0]);
					
				final JComboBox<Pair<OpNode, OutputField>> connectionBox = new JComboBox<>(outputArray);
				if(currentLink != null) {
					connectionBox.setSelectedItem(new Pair<>(currentLink.getSource(), currentLink.getSourceField()));
				} else {
					connectionBox.setSelectedItem(null);
				}
					
				final JPanel linkPanel = new JPanel(new BorderLayout());
				
				final JButton removeLinkButton = new JButton();
				removeLinkButton.setIcon(REMOVE_LINK_ICON);
				removeLinkButton.setToolTipText("Clear connection");
				removeLinkButton.setEnabled(currentLink != null);
				removeLinkButton.addActionListener( (e) -> {
					final OpLink link = graph.getIncomingEdges(node)
						.stream().filter( (l) -> l.getDestinationField() == inputField )
						.findAny().orElse(null);
					final RemoveLinkEdit edit = new RemoveLinkEdit(document.getGraph(), link);
					document.getUndoSupport().postEdit(edit);
				});
				connectionBox.setRenderer(new LinkChoiceRenderer());
				connectionBox.addItemListener( (e) -> {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						final int selectedIdx = connectionBox.getSelectedIndex();
						final Pair<OpNode, OutputField> selectedLink = connectionBox.getModel().getElementAt(selectedIdx);
						
						final CompoundEdit edit = new CompoundEdit();
						if(currentLink != null) {
							final RemoveLinkEdit rmLinkEdit = new RemoveLinkEdit(graph, currentLink);
							edit.addEdit(rmLinkEdit);
						}
						try {
							final AddLinkEdit addLinkEdit = new AddLinkEdit(graph, new OpLink(selectedLink.getFirst(), selectedLink.getSecond(),
									node, inputField));
							edit.addEdit(addLinkEdit);
							
							removeLinkButton.setEnabled(true);
							edit.end();
							
							document.getUndoSupport().postEdit(edit);
						} catch (VertexNotFoundException | CycleDetectedException | ItemMissingException | InvalidEdgeException e1) {
							LOGGER.log(Level.WARNING, e1.getLocalizedMessage(), e1);
							connectionBox.setSelectedItem(null);
						}
					}
				});
				
				linkPanel.add(removeLinkButton, BorderLayout.WEST);
				linkPanel.add(connectionBox, BorderLayout.CENTER);
				add(linkPanel, gbc);

				if(inputField.isOptional() && nodeMeta != null) {
					final JComponent editorComp = getEditComponentForField(inputField, nodeMeta.getDefault(inputField));
					if(editorComp != null) {
						final JPanel defaultPanel = new JPanel(new BorderLayout());
						defaultPanel.add(new JLabel("default:"), BorderLayout.WEST);
						defaultPanel.add(editorComp, BorderLayout.CENTER);
						++gbc.gridy;
						add(defaultPanel, gbc);
					}
				}
			}

			++gbc.gridy;
			gbc.gridx = 0;
			// output fields
			final JLabel outputFieldsLbl = new JLabel("Outputs");
			outputFieldsLbl.setFont(outputFieldsLbl.getFont().deriveFont(Font.BOLD));
			final JSeparator outputsSeparator = new JSeparator(SwingConstants.HORIZONTAL);

			gbc.weightx = 1.0;
			gbc.gridwidth = 2;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets.set(0, 0, 0, 0);

			add(outputFieldsLbl, gbc);
			++gbc.gridy;
			add(outputsSeparator, gbc);

			gbc.insets.set(2, 5, 2, 2);

			for(OutputField outputField:node.getOutputFields()) {
				++gbc.gridy;
				gbc.gridx = 0;
				gbc.gridwidth = 1;
				gbc.weightx = 0.0;

				final JLabel keyLbl = new JLabel(outputField.getKey());
				keyLbl.setToolTipText(outputField.getDescription());
				keyLbl.setFont(keyLbl.getFont().deriveFont(Font.ITALIC));

				add(keyLbl, gbc);

				++gbc.gridx;
				gbc.weightx = 1.0;
				gbc.fill = GridBagConstraints.HORIZONTAL;

				final List<OpLink> outgoingConnections =
						graph.getOutgoingEdges(node)
						.stream().filter( (l) -> l.getSourceField() == outputField )
						.collect( Collectors.toList() );
				if(outgoingConnections.size() > 0) {
					for(final OpLink link:outgoingConnections) {
						final JButton removeLinkButton = new JButton();
						removeLinkButton.setIcon(REMOVE_LINK_ICON);
						removeLinkButton.setToolTipText("Clear connection");
						removeLinkButton.setEnabled(true);
						removeLinkButton.addActionListener( (e) -> {
							final RemoveLinkEdit edit = new RemoveLinkEdit(document.getGraph(), link);
							document.getUndoSupport().postEdit(edit);
						});
						
						final JLabel lbl = new JLabel(link.getDestination().getName() + "." + link.getDestinationField().getKey());
						
						final JPanel linkPanel = new JPanel(new BorderLayout());
						linkPanel.add(removeLinkButton, BorderLayout.WEST);
						linkPanel.add(lbl, BorderLayout.CENTER);
						add(linkPanel, gbc);
						++gbc.gridy;
					}
				} else {
					final JLabel noLinkLbl = new JLabel("No connection");
					add(noLinkLbl, gbc);
				}
			}
		}

		gbc.gridx = 0;
		++gbc.gridy;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 2;
		add(Box.createVerticalGlue(), gbc);

		revalidate();
		repaint();
	}

	/**
	 * Gets the node this info panel is currently viewing.
	 *
	 * @return the node
	 */
	public OpNode getNode() {
		return node;
	}

	/**
	 * Sets the node this info panel is currently viewing.
	 *
	 * @param node  the node to display
	 */
	public void setNode(OpNode node) {
		final OpNode oldNode = this.node;
		this.node = node;
		if(oldNode != node) {
			updatePanel();
		}
//		if(this.node != node || getComponentCount() == 0) {
//			this.node = node;
//
//			// Clear all current components and add in new ones
//			removeAll();
//			if(node == null) {
//				final GridBagConstraints gbc = new GridBagConstraints();
//				final JLabel label = new JLabel("No node selected", SwingConstants.CENTER);
//				label.setFont(label.getFont().deriveFont(Font.ITALIC));
//				add(label, gbc);
//			} else {
//				final GridBagConstraints gbc = new GridBagConstraints();
//				gbc.gridy = 0;
//
//				final NodeMetadata meta = node.getExtension(NodeMetadata.class);
//				if(meta != null) {
//					gbc.insets.set(2, 5, 2, 2);
//					gbc.gridwidth = 1;
//
//					for(InputField field : node.getInputFields()) {
//						final Object value = meta.getDefault(field);
//						final JComponent editComp = getEditComponentForField(field, value);
//						if(editComp != null) {
//							final JLabel fieldNameLabel = new JLabel(field.getKey() + ":");
//							fieldNameLabel.setFont(fieldNameLabel.getFont().deriveFont(Font.BOLD));
//							fieldNameLabel.setToolTipText(field.getDescription());
//
//							gbc.gridx = 0;
//							gbc.anchor = GridBagConstraints.EAST;
//							gbc.fill = GridBagConstraints.NONE;
//							gbc.weightx = 0;
//							add(fieldNameLabel, gbc);
//
//							editComp.setToolTipText(field.getDescription());
//
//							gbc.gridx = 1;
//							gbc.anchor = GridBagConstraints.WEST;
//							gbc.fill = GridBagConstraints.BOTH;
//							gbc.weightx = 1;
//							add(editComp, gbc);
//
//							++gbc.gridy;
//						}
//					}
//
//					if(getComponentCount() > 0) {
//						gbc.weighty = 1;
//						gbc.gridwidth = 2;
//						add(new JComponent() {}, gbc);
//					} else {
//						final JLabel label = new JLabel("No editable input fields", SwingConstants.CENTER);
//						label.setFont(label.getFont().deriveFont(Font.ITALIC));
//						add(label, gbc);
//					}
//				} else {
//					final JLabel label = new JLabel("Node does not support defaults", SwingConstants.CENTER);
//					label.setFont(label.getFont().deriveFont(Font.ITALIC));
//					add(label, gbc);
//				}
//			}
//
//			revalidate();
//			repaint();
//		}
	}

	/**
	 * Gets an editing component for an input field.
	 *
	 * @param field  the input field
	 *
	 * @return the editing component for the specified input field, or
	 *         <code>null</code> if there is no editable component for
	 *         the given input field
	 *
	 * TODO More user customization on editors for different types/validators.
	 *      Perhaps have an interface with a function similar to below.
	 */
	protected JComponent getEditComponentForField(final InputField field, Object defaultValue) {
		// XXX allow enabled field here?
		if(field.getKey() == OpNode.ENABLED_FIELD.getKey())
			return null;

		// Editable component returned is currently based on a fixed set of
		// classes, and only if the validator of the field is a class-based
		// validator which accepts a single class
		JComponent ret = null;
		if(field.getValidator() != null && field.getValidator() instanceof ClassValidator) {
			final ClassValidator validator = (ClassValidator)field.getValidator();
			if(validator.getClasses().size() == 1) {
				Class<?> cls = validator.getClasses().get(0);

				// Check default value against this class
				if(defaultValue != null) {
					if(!cls.isAssignableFrom(defaultValue.getClass())) {
						LOGGER.warning("Default value for input field '" + field.getKey() +"' should be '" + cls.getName() + "' but got '" + defaultValue.getClass().getName() + "' instead");
						defaultValue = null;
					}
				}

				if(cls == String.class) {
					// JTextArea for java.lang.String
					String initialText = "";
					if(defaultValue != null)
						initialText = (String)defaultValue;

					final JTextArea stringEditable = new JTextArea(initialText);
					stringEditable.setBorder(BorderFactory.createEtchedBorder());
					stringEditable.setLineWrap(true);
					stringEditable.setTabSize(4);
					stringEditable.getDocument().addDocumentListener(new DocumentListener() {
						@Override
						public void removeUpdate(DocumentEvent e) {
							if(node != null) {
								final NodeMetadata meta = node.getExtension(NodeMetadata.class);
								if(meta != null)
									meta.setDefault(field, stringEditable.getText());
							}
						}

						@Override
						public void insertUpdate(DocumentEvent e) {
							if(node != null) {
								final NodeMetadata meta = node.getExtension(NodeMetadata.class);
								if(meta != null)
									meta.setDefault(field, stringEditable.getText());
							}
						}

						@Override
						public void changedUpdate(DocumentEvent e) {
							if(node != null) {
								final NodeMetadata meta = node.getExtension(NodeMetadata.class);
								if(meta != null)
									meta.setDefault(field, stringEditable.getText());
							}
						}
					});

					ret = stringEditable;
				} else if(cls == Boolean.class) {
					// JTextBox for java.lang.Boolean
					boolean initial = false;
					if(defaultValue != null)
						initial = (Boolean)defaultValue;

					final JCheckBox booleanEditable = new JCheckBox();
					booleanEditable.setSelected(initial);
					booleanEditable.addItemListener(new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent e) {
							if(node != null) {
								final NodeMetadata meta = node.getExtension(NodeMetadata.class);
								if(meta != null)
									meta.setDefault(field, e.getStateChange() == ItemEvent.SELECTED);
							}
						}
					});

					ret = booleanEditable;
				} else if(Number.class.isAssignableFrom(cls) || cls == int.class || cls == double.class || cls == float.class) {
					Number initial = null;
					if(defaultValue != null)
						initial = (Number)defaultValue;

					final NumberFormat formatter = NumberFormat.getInstance();
					final JFormattedTextField numberEditable = new JFormattedTextField(formatter);
					numberEditable.setValue(initial);
					numberEditable.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent e) {
							if(node != null && e.getPropertyName().equals("value")) {
								final NodeMetadata meta = node.getExtension(NodeMetadata.class);
								if(meta != null)
									meta.setDefault(field, e.getNewValue());
							}
						}
					});

					ret = numberEditable;
				}
			}
		}

		return ret;
	}
	
	private class LinkChoiceRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			final JLabel retVal = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
			if(value != null) {
				@SuppressWarnings("unchecked")
				final Pair<OpNode, OutputField> pair = (Pair<OpNode, OutputField>)value;
				retVal.setText(pair.getFirst().getName() + "." + pair.getSecond().getKey());
			} else {
				retVal.setText("No connection");
			}
			
			return retVal;
		}
		
		
		
	}
	
}
