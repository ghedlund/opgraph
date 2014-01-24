package ca.gedge.opgraph.nodes.objects;

import ca.gedge.opgraph.OpNodeInfo;
import ca.gedge.opgraph.nodes.reflect.ClassNode;

@OpNodeInfo(
		name="Double",
		description="java.lang.Double object",
		category="Objects")
public class DoubleNode extends ClassNode {

	public DoubleNode() {
		super(Double.class);
	}
	
}
