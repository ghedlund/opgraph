module ca.phon.opgraph.nodes {
	requires java.logging;
	requires java.scripting;
	
	requires antlr.runtime;
	
	requires transitive ca.phon.opgraph.core;
	requires transitive ca.phon.opgraph.app;
	
	exports ca.phon.opgraph.nodes;
	exports ca.phon.opgraph.nodes.canvas;
	exports ca.phon.opgraph.nodes.general;
	exports ca.phon.opgraph.nodes.general.script;
	exports ca.phon.opgraph.nodes.iteration;
	exports ca.phon.opgraph.nodes.logic;
	exports ca.phon.opgraph.nodes.math;
	exports ca.phon.opgraph.nodes.menu;
	exports ca.phon.opgraph.nodes.menu.edits;
	exports ca.phon.opgraph.nodes.random;
	exports ca.phon.opgraph.nodes.reflect;
	exports ca.phon.opgraph.nodes.xml;
}
