package ca.gedge.opgraph.nodes.reflect;

import ca.gedge.opgraph.library.instantiators.Instantiator;

/**
 * Create new object nodes in the graph.  The class will prompt the user
 * to provide a class name.
 *
 */
public class ObjectNodeInstantiator implements Instantiator<ObjectNode> {

	@Override
	public ObjectNode newInstance(Object... params) throws InstantiationException {
		return null;
	}
	
}
