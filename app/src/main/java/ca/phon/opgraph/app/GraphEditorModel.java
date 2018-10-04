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
package ca.phon.opgraph.app;

import java.beans.*;
import java.util.*;
import java.util.logging.Logger;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.components.*;
import ca.phon.opgraph.app.components.canvas.*;
import ca.phon.opgraph.app.components.library.NodeLibraryViewer;
import ca.phon.opgraph.util.ServiceDiscovery;
import ca.phon.ui.jbreadcrumb.JBreadcrumb;

/**
 * A model for the graph editor.
 */
public class GraphEditorModel {
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(GraphEditorModel.class.getName());

//	/** The active editor model */
//	private static GraphEditorModel activeModel;
//
//	/**
//	 * Gets the {@link GraphEditorModel} that is currently active.
//	 * 
//	 * @return  the active editor model, or <code>null</code> if no model active
//	 * 
//	 * @deprecated
//	 */
//	public static GraphEditorModel getActiveEditorModel() {
//		return activeModel;
//	}
//
//	/**
//	 * Sets a given model as the active model for the application.
//	 * 
//	 * @param model  the new model
//	 * 
//	 * @deprecated
//	 */
//	public static void setActiveEditorModel(GraphEditorModel model) {
//		activeModel = model;
//	}
//
//	/**
//	 * Gets the {@link GraphDocument} that is currently active.
//	 * 
//	 * @return  the active document, or <code>null</code> if no document active
//	 * 
//	 * @deprecated
//	 */
//	public static GraphDocument getActiveDocument() {
//		final GraphEditorModel model = getActiveEditorModel();
//		return (model == null ? null : model.getDocument());
//	}
	
	/** The document */
	private GraphDocument document;

	/** The breadcrumb attached to the canvas */
	private JBreadcrumb<OpGraph, ?> breadcrumb;

	/** The canvas this window is viewing */
	private GraphCanvas canvas;

	/** The console panel */
	private ConsolePanel console;

	/** The panel that allows one to edit default values for node input fields */
	private NodeFieldsPanel nodeFields;

	/** The panel that allows one to modify node settings */
	private NodeSettingsPanel nodeSettings;

	/** The viewer component for the node library */
	private NodeLibraryViewer nodeLibrary;

	/** The viewer component for the node library */
	private ContextViewerPanel debugPanel;
	
	/** The outline component for the graph */
	private GraphOutline graphOutline;

	/** List of menu providers */
	private ArrayList<MenuProvider> menuProviders;

	/**
	 * 
	 */
	public GraphEditorModel() {
		this(new GraphDocument());
	}
	
	public GraphEditorModel(OpGraph graph) {
		this(new GraphDocument(graph));
	}
	
	public GraphEditorModel(GraphDocument doc) {
		this.document = doc;
		// Property change listeners
		document.addPropertyChangeListener(GraphDocument.PROCESSING_CONTEXT, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				final Processor context = (Processor)evt.getNewValue();
				
//				if(canvas != null)
//					// Let canvas redraw itself based on the given context
//					canvas.updateDebugState(context);
				
				// Update debug panel information
				if(debugPanel != null) {
					if(debugPanel.getProcessingContext() != context)
						debugPanel.setProcessingContext(context);
					else
						debugPanel.updateDebugInfo();
				}
			}
		});

		// Discover menu extensions
		this.menuProviders = new ArrayList<MenuProvider>();
		final ServiceDiscovery discovery = ServiceDiscovery.getInstance();
		for(Class<? extends MenuProvider> menu : discovery.findProviders(MenuProvider.class)) {
			try {
				menuProviders.add(menu.newInstance());
			} catch(InstantiationException exc) {
				LOGGER.warning("Could not instantiate menu provider: " + menu.getName());
			} catch(IllegalAccessException exc) {
				LOGGER.warning("Could not instantiate menu provider: " + menu.getName());
			}
		}
	}

	/**
	 * Gets the graph canvas this window is using.
	 * 
	 * @return the canvas, or <code>null</code> if no canvas is being viewed
	 */
	public GraphCanvas getCanvas() {
		if(canvas == null) {
			// Initialize components
			canvas = new GraphCanvas(this.document);
			canvas.getSelectionModel().addSelectionListener(graphSelectionListener);
		}
		return canvas;
	}

	/**
	 * Gets the console panel.
	 * 
	 * @return the console
	 */
	public ConsolePanel getConsolePanel() {
		if(console == null) {
			console = new ConsolePanel();
		}
		return console;
	}

	/**
	 * Gets the document from the canvas in this editor model.
	 * 
	 * @return  the document
	 */
	public GraphDocument getDocument() {
		return document;
	}

	/**
	 * @return the breadcrumb
	 */
	public JBreadcrumb<OpGraph, ?> getBreadcrumb() {
		if(breadcrumb == null) {
			breadcrumb = new JBreadcrumb<OpGraph, String>(document.getBreadcrumb());
		}
		return breadcrumb;
	}

	/**
	 * @return the nodeDefaults
	 */
	public NodeFieldsPanel getNodeFieldsPanel() {
		if(nodeFields == null) {
			nodeFields = new NodeFieldsPanel(document);
		}
		return nodeFields;
	}

	/**
	 * @return the debugPanel
	 */
	public ContextViewerPanel getDebugInfoPanel() {
		if(debugPanel == null) {
			debugPanel = new ContextViewerPanel();
		}
		return debugPanel;
	}

	/**
	 * @return the nodeLibrary
	 */
	public NodeLibraryViewer getNodeLibrary() {
		if(nodeLibrary == null) {
			nodeLibrary = new NodeLibraryViewer(getDocument());
		}
		return nodeLibrary;
	}

	/**
	 * @return the nodeSettings
	 */
	public NodeSettingsPanel getNodeSettings() {
		if(nodeSettings == null) {
			nodeSettings = new NodeSettingsPanel(getDocument());
		}
		return nodeSettings;
	}
	
	public GraphOutline getGraphOutline() {
		if(graphOutline == null) {
			graphOutline = new GraphOutline(getDocument(), getCanvas());
		}
		return graphOutline;
	}

	/**
	 * @return immutable list of menu providers 
	 */
	public List<MenuProvider> getMenuProviders() {
		return Collections.unmodifiableList(menuProviders);
	}

	
	
	//
	// GraphCanvasSelectionListener
	//

	private final GraphCanvasSelectionListener graphSelectionListener = new GraphCanvasSelectionListener() {
		@Override
		public void nodeSelectionChanged(Collection<OpNode> old, Collection<OpNode> selected) {
			OpNode node = null;
			if(selected.size() == 1)
				node = selected.iterator().next();

			getNodeFieldsPanel().setNode(node);
			getNodeSettings().setNode(node);
			getDebugInfoPanel().setNode(node);
		}
	};
	
}
