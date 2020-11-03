/*
 * Copyright (C) 2012-2020 Gregory Hedlund <https://www.phon.ca>
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
