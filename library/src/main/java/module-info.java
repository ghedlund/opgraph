module ca.phon.opgraph.library {
	requires java.logging;
	requires transitive ca.phon.opgraph.core;
	
	exports ca.phon.opgraph.library;
	exports ca.phon.opgraph.library.handlers;
	exports ca.phon.opgraph.library.instantiators;
}
