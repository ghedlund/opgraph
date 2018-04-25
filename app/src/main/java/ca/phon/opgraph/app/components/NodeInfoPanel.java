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
