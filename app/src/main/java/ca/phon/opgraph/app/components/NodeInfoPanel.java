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
package ca.phon.opgraph.app.components;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ca.phon.opgraph.OpGraph;

/**
 * Provide basic information about a node including
 * type of node, inputs, outputs and current connections
 * to other nodes.
 */
public class NodeInfoPanel extends JPanel {

	private static final long serialVersionUID = 157792904031318737L;

	// the graph, it should be the root level graph
	private OpGraph graph;
	
	// basic node information
	private JLabel nodeNameLabel;
	private JLabel nodeTypeLabel;
	private JLabel nodePathLabel;
	
	// inputs
	
	
	public NodeInfoPanel() {
		super();
		
		init();
	}
	
	private void init() {
		
	}
	
}
