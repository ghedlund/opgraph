package ca.phon.opgraph.nodes.general;

import java.util.ArrayList;
import java.util.List;

import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.nodes.xml.MacroNodeXMLSerializer;

/**
 * An {@link MacroNode} extension containing a list of nodes. These nodes
 * must exist in the graph of the {@link MacroNode} and indicate to the 
 * {@link MacroNodeXMLSerializer} to save these nodes if {@link MacroNode#isGraphEmbedded()} is
 * <code>true</code>. When reading the OpGraph the saved nodes will be used in place of their
 * linked counterpart.
 *
 */
public class LinkedMacroNodeOverrides {
	
	private List<OpNode> nodeOverrides = new ArrayList<>();
	
	public List<OpNode> getNodeOverrides() {
		return this.nodeOverrides;
	}
	
}
