package ca.gedge.opgraph.nodes.objects;

import ca.gedge.opgraph.OpNodeInfo;
import ca.gedge.opgraph.nodes.reflect.ClassNode;

@OpNodeInfo(
		name="String",
		description="java.lang.String object",
		category="Objects")
public class StringNode extends ClassNode {

	public StringNode() {
		super(String.class);
	}
	
}
