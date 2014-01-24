package ca.gedge.opgraph.nodes.objects;

import ca.gedge.opgraph.OpNodeInfo;
import ca.gedge.opgraph.nodes.reflect.ClassNode;

@OpNodeInfo(
		name="Integer",
		description="java.lang.Integer object",
		category="Objects")
public class IntegerNode extends ClassNode {

	public IntegerNode() {
		super(Integer.class);
	}
	
}
