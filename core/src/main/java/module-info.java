module ca.phon.opgraph.core {	
	requires java.logging;
	requires transitive java.desktop;
	
	exports ca.phon.opgraph;
	exports ca.phon.opgraph.dag;
	exports ca.phon.opgraph.exceptions;
	exports ca.phon.opgraph.extensions;
	exports ca.phon.opgraph.io;
	exports ca.phon.opgraph.util;
	exports ca.phon.opgraph.validators;
}
