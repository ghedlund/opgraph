package ca.phon.opgraph.app.components.canvas;

import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.app.GraphDocument;

/**
 * Extension interface for adding context menu items
 * specific to nodes.  This should be addeded as an extension
 * to the {@link OpNode} subclass instance
 *
 * @author ghedlund
 *
 */
public interface CanvasContextMenuExtension {

	/**
	 * Add items to provided menu
	 * 
	 * @param menu
	 * @oaran document
	 * @param me 
	 */
	public void addContextMenuItems(JPopupMenu menu, GraphDocument document, MouseEvent me);
	
}
