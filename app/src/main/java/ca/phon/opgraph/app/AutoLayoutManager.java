/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * This file is part of the OpGraph project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.phon.opgraph.app;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JComponent;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.OpLink;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.app.components.canvas.CanvasNode;
import ca.phon.opgraph.app.edits.graph.MoveNodesEdit;
import ca.phon.opgraph.extensions.NodeMetadata;

/**
 * Layout nodes in a graph in a logical manner.  Nodes
 * are laid out horizontally, with nodes traversed depth first.
 *
 */
public class AutoLayoutManager {

	private final static int DEFAULT_SPACE = 15;

	private CompoundEdit cmpEdit = new CompoundEdit();

	private int prefWidth = -1;

	public AutoLayoutManager() {

	}

	public void layoutGraph(OpGraph graph) {
		// ensure correct sorting
		graph.invalidateSort();
		graph.topologicalSort();

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
				maxYRef.set(yRef.get() + (int)size.getHeight());
				followOutputs(graph, node, xRef.get() + size.width + DEFAULT_SPACE,
						yRef.get(), xRef.get() + size.width + DEFAULT_SPACE, maxYRef, placedNodes);

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

	/**
	 * Follow outputs for a node, placing each node in depth first
	 * ordering.
	 *
	 * @param graph
	 * @param node
	 * @param x currentX
	 * @param y currentY
	 * @param xoff indent location (used during node wrapping)
	 * @param maxYRef reference for storing maxY value
	 * @param placedNodes list of already placed nodes
	 */
	private void followOutputs(OpGraph graph, OpNode node, int x, int y,
			int xoff, AtomicReference<Integer> maxYRef, List<OpNode> placedNodes) {
		OpNode lastNode = null;
		for(OpLink link:graph.getOutgoingEdges(node)) {
			final OpNode dest = link.getDestination();
			if(!placedNodes.contains(dest)) {
				final Dimension size = placeNode(dest, x, y);
				placedNodes.add(dest);

				if(getPreferredWidth() > 0 && (x+size.width) > getPreferredWidth()) {
					// wrap node
					x = xoff;
					y = maxYRef.get() + DEFAULT_SPACE;
					placeNode(dest, x, y);
				}

				maxYRef.set(Math.max(maxYRef.get(), y + size.height));

				int nextX = x + size.width + DEFAULT_SPACE;
				if(lastNode != null &&
						(graph.getLevel(lastNode) != graph.getLevel(dest))) {
					x = nextX;
				}
				followOutputs(graph, dest, nextX, y, xoff, maxYRef, placedNodes);
				y += size.height + DEFAULT_SPACE;
				if(y < maxYRef.get()) {
					y = maxYRef.get() + DEFAULT_SPACE;
				}
			}
			lastNode = dest;
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
