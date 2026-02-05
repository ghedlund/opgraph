module ca.phon.opgraph.app {
	requires java.desktop;
	requires java.logging;
	requires transitive ca.phon.jbreadcrumb;
	requires transitive ca.phon.opgraph.core;
	requires transitive ca.phon.opgraph.library;
	requires transitive ca.phon.opgraph.xml;

	exports ca.phon.opgraph.app;
	exports ca.phon.opgraph.app.commands;
	exports ca.phon.opgraph.app.commands.core;
	exports ca.phon.opgraph.app.commands.debug;
	exports ca.phon.opgraph.app.commands.edit;
	exports ca.phon.opgraph.app.commands.graph;
	exports ca.phon.opgraph.app.commands.notes;
	exports ca.phon.opgraph.app.commands.publish;
	exports ca.phon.opgraph.app.components;
	exports ca.phon.opgraph.app.components.canvas;
	exports ca.phon.opgraph.app.components.library;
	exports ca.phon.opgraph.app.edits.graph;
	exports ca.phon.opgraph.app.edits.node;
	exports ca.phon.opgraph.app.edits.notes;
	exports ca.phon.opgraph.app.extensions;
	exports ca.phon.opgraph.app.util;
	exports ca.phon.opgraph.app.xml;

	uses ca.phon.opgraph.OpNode;
	uses ca.phon.opgraph.app.MenuProvider;
	uses ca.phon.opgraph.app.commands.CommandHook;
	uses ca.phon.opgraph.app.components.canvas.AbandonedLinkHandler;

	provides ca.phon.opgraph.io.xml.XMLSerializer
		with ca.phon.opgraph.app.xml.NodeMetadataXMLSerializer,
			ca.phon.opgraph.app.xml.NodeSettingsXMLSerializer,
			ca.phon.opgraph.app.xml.NotesXMLSerializer;

	provides ca.phon.opgraph.app.MenuProvider
		with ca.phon.opgraph.app.commands.core.CoreMenuProvider,
			ca.phon.opgraph.app.commands.edit.EditMenuProvider,
			ca.phon.opgraph.app.commands.graph.GraphMenuProvider,
			ca.phon.opgraph.app.commands.debug.DebugMenuProvider,
			ca.phon.opgraph.app.commands.notes.NotesMenuProvider,
			ca.phon.opgraph.app.commands.publish.PublishableMenuProvider,
			ca.phon.opgraph.app.components.library.NodeLibraryMenuProvider;

	provides ca.phon.opgraph.io.xml.SchemaProvider
		with ca.phon.opgraph.app.xml.AppSchemaProvider;
}
