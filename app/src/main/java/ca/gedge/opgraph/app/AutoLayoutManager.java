package ca.gedge.opgraph.app;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JComponent;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import ca.gedge.opgraph.OpGraph;
import ca.gedge.opgraph.OpLink;
import ca.gedge.opgraph.OpNode;
import ca.gedge.opgraph.app.components.canvas.CanvasNode;
import ca.gedge.opgraph.app.edits.graph.MoveNodesEdit;
import ca.gedge.opgraph.app.extensions.NodeMetadata;

public class AutoLayoutManager {
	
	private final static int DEFAULT_SPACE = 15;
	
	private CompoundEdit cmpEdit = new CompoundEdit();
	
	private int prefWidth = -1;
	
	public AutoLayoutManager() {
		
	}
	
	public void layoutGraph(OpGraph graph) {
		final List<OpNode> modifiedNodes = checkNodes(graph);

		cmpEdit = new CompoundEdit();
		final List<OpNode> nodes = graph.getVertices();
		final List<OpNode> placedNodes = new ArrayList<>();
		final AtomicReference<Integer> xRef = new AtomicReference<>(15);
		final AtomicReference<Integer> yRef = new AtomicReference<>(15);
		final AtomicReference<Integer> maxYRef = new AtomicReference<>(15);
		nodes.stream()
			.filter(node -> graph.getLevel(node) == 0)
			.forEach(node -> {
				final Dimension size = placeNode(node, xRef.get(), yRef.get());
				placedNodes.add(node);
				maxYRef.set(Math.max(maxYRef.get(), yRef.get() + size.height));
				followOutputs(graph, node, xRef.get() + size.width + DEFAULT_SPACE, yRef.get(), maxYRef, placedNodes);
				
				yRef.set(maxYRef.get() + DEFAULT_SPACE);
			});
		cmpEdit.end();
		
		modifiedNodes.forEach(node -> node.putExtension(JComponent.class, null));
	}
	
	private List<OpNode> checkNodes(OpGraph graph) {
		final List<OpNode> modifiedNodes = new ArrayList<>();
		for(OpNode node:graph) {
			if(node.getExtension(JComponent.class) == null) {
				node.putExtension(JComponent.class, new CanvasNode(node));
				modifiedNodes.add(node);
			}
		}
		return modifiedNodes;
	}
	
	private void followOutputs(OpGraph graph, OpNode node, int x, int y,
			AtomicReference<Integer> maxYRef, List<OpNode> placedNodes) {
		for(OpLink link:graph.getOutgoingEdges(node)) {
			final OpNode dest = link.getDestination();
			if(!placedNodes.contains(dest)) {
				final Dimension size = placeNode(dest, x, y);
				placedNodes.add(dest);
				
				if(getPreferredWidth() > 0 && (x+size.width) > getPreferredWidth()) {
					// wrap node
					x = 15;
					y = maxYRef.get() + DEFAULT_SPACE;
					placeNode(dest, x, y);
				}
				
				maxYRef.set(Math.max(maxYRef.get(), y + size.height));
				x += size.width + DEFAULT_SPACE;
				followOutputs(graph, dest, x, y, maxYRef, placedNodes);
				y += size.height + DEFAULT_SPACE;
			}
		}
	}
	
	public int getPreferredWidth() {
		return this.prefWidth;
	}
	
	public void setPreferredWidth(int width) {
		this.prefWidth = width;
	}
	
	/**
	 * Place node a given {x, y} point and return
	 * the dimension of the node.
	 * 
	 * @param node
	 * @param x
	 * @param y
	 * @return
	 */
	private Dimension placeNode(OpNode node, int x, int y) {
		Dimension retVal = new Dimension(0, 0);
		final JComponent comp = node.getExtension(JComponent.class);
		if(comp == null) return retVal;
		retVal = comp.getPreferredSize();

		NodeMetadata meta = node.getExtension(NodeMetadata.class);
		if(meta == null) {
			meta = new NodeMetadata(x, y);
			node.putExtension(NodeMetadata.class, meta);
		}
		
		final int deltaX = x - meta.getX();
		final int deltaY = y - meta.getY();
		if(deltaX != 0 || deltaY != 0)
			addEdit(new MoveNodesEdit(Collections.singletonList(node), deltaX, deltaY));
		
		return retVal;
	}
	
	private void addEdit(UndoableEdit edit) {
		cmpEdit.addEdit(edit);
	}

	public UndoableEdit getUndoableEdit() {
		return this.cmpEdit;
	}
	
}
