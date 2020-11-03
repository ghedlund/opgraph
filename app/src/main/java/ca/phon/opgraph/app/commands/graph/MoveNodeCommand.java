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
package ca.phon.opgraph.app.commands.graph;

import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.KeyStroke;

import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.commands.GraphCommand;
import ca.phon.opgraph.app.components.canvas.GraphCanvas;
import ca.phon.opgraph.app.edits.graph.MoveNodesEdit;

/**
 * Moves selected nodes in the active {@link GraphCanvas}.
 */
public class MoveNodeCommand extends GraphCommand {
	/**
	 * Get a textual representation of the given deltas. More specifically:
	 * <ul>
	 *   <li>if &Delta;x == 0 and &Delta;y > 0, "Up"</li>
	 *   <li>if &Delta;x == 0 and &Delta;y < 0, "Down"</li>
	 *   <li>if &Delta;y == 0 and &Delta;x > 0, "Right"</li>
	 *   <li>if &Delta;y == 0 and &Delta;x < 0, "Left"</li>
	 *   <li>otherwise, ""</li>
	 * </ul>
	 * 
	 * @param xDelta  the x-axis delta
	 * @param yDelta  the y-axis delta
	 * 
	 * @return a textual representation that best represents the action of
	 *         the specified deltas
	 */
	public static String getMoveString(int xDelta, int yDelta) {
		String ret = "";
		if(xDelta == 0 && yDelta > 0)
			ret = "Down";
		else if(xDelta == 0 && yDelta < 0)
			ret = "Up";
		else if(yDelta == 0 && xDelta > 0)
			ret = "Right";
		else if(yDelta == 0 && xDelta < 0)
			ret = "Left";
		return ret;
	}

	/**
	 * Get a keystroke for the given deltas. More specifically:
	 * <ul>
	 *   <li>if &Delta;x == 0 and &Delta;y > 0, "Up"</li>
	 *   <li>if &Delta;x == 0 and &Delta;y < 0, "Down"</li>
	 *   <li>if &Delta;y == 0 and &Delta;x > 0, "Right"</li>
	 *   <li>if &Delta;y == 0 and &Delta;x < 0, "Left"</li>
	 *   <li>otherwise, <code>null</code></li>
	 * </ul>
	 * 
	 * @param xDelta  the x-axis delta
	 * @param yDelta  the y-axis delta
	 * 
	 * @return a keystroke for the specified deltas
	 */
	public static KeyStroke getMoveKeystroke(int xDelta, int yDelta) {
		KeyStroke ret = null;
		if(xDelta == 0 && yDelta > 0)
			ret = KeyStroke.getKeyStroke("shift DOWN");
		else if(xDelta == 0 && yDelta < 0)
			ret = KeyStroke.getKeyStroke("shift UP");
		else if(yDelta == 0 && xDelta > 0)
			ret = KeyStroke.getKeyStroke("shift RIGHT");
		else if(yDelta == 0 && xDelta < 0)
			ret = KeyStroke.getKeyStroke("shift LEFT");
		return ret;
	}

	/** The distance along the x-axis to move the node */
	private int deltaX;

	/** The distance along the y-axis to move the node */
	private int deltaY;

	/**
	 * Constructs a move command that moves the current node selection in the
	 * given graph canvas, with this edit posted in the given undo manager.
	 * 
	 * @param deltaX  the x-axis delta
	 * @param deltaY  the y-axis delta
	 */
	public MoveNodeCommand(GraphDocument doc, int deltaX, int deltaY) {
		super(doc);
		
		this.deltaX = deltaX;
		this.deltaY = deltaY;

		final KeyStroke keystroke = getMoveKeystroke(deltaX, deltaY);
		if(keystroke != null)
			putValue(ACCELERATOR_KEY, keystroke);

		final String suffix = getMoveString(deltaX, deltaY);
		if(suffix.length() == 0)
			putValue(NAME, "Move");
		else
			putValue(NAME, "Move " + suffix);

	}

	@Override
	public void hookableActionPerformed(ActionEvent e) {
		if(document != null) {
			final Collection<OpNode> nodes = document.getSelectionModel().getSelectedNodes();
			document.getUndoSupport().postEdit( new MoveNodesEdit(nodes, deltaX, deltaY) );
		}
	}

}
