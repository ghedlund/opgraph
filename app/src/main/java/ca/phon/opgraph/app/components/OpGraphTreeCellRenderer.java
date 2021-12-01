package ca.phon.opgraph.app.components;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.components.canvas.NodeStyle;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.*;

public class OpGraphTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = -823509020400941004L;

	private ImageIcon rootIcon;

	public OpGraphTreeCellRenderer() {
		try {
			rootIcon = new ImageIcon(ImageIO.read(NodeStyle.class.getClassLoader().getResourceAsStream("data/icons/16x16/opgraph/graph.png")));
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
	                                              boolean leaf, int row, boolean hasFocus) {
		final JLabel retVal = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;

		if (treeNode.getUserObject() instanceof OpGraph) {
			// root component
			retVal.setText("root");
			retVal.setIcon(rootIcon);
		} else if (treeNode.getUserObject() instanceof OpNode) {
			final OpNode node = (OpNode) treeNode.getUserObject();
			final NodeStyle nodeStyle = NodeStyle.getStyleForNode(node);
			retVal.setText(node.getName());
			retVal.setIcon(nodeStyle.NodeIcon);
			retVal.setBackground(nodeStyle.NodeBackgroundColor);
			retVal.setForeground(nodeStyle.NodeNameTextColor);
		}
		return retVal;
	}

}
