package ca.gedge.opgraph.nodes.objects;

import ca.gedge.opgraph.OpNodeInfo;
import ca.gedge.opgraph.nodes.reflect.ClassNode;

@OpNodeInfo(
		name="Boolean",
		description="java.lang.Boolean object",
		category="Objects")
public class BooleanNode extends ClassNode {

	public BooleanNode() {
		super(Boolean.class);
	}
	
}
