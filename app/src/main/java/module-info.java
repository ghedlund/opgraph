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
}
