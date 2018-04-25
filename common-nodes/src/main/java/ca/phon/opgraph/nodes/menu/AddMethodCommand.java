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
package ca.phon.opgraph.nodes.menu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

import javax.swing.AbstractAction;

import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.edits.graph.AddNodeEdit;
import ca.phon.opgraph.nodes.reflect.MethodNode;
import ca.phon.opgraph.util.ReflectUtil;

public class AddMethodCommand extends AbstractAction {
	
	private static final long serialVersionUID = -469196233555490912L;
	
	private GraphDocument document;
	
	private final Method method;
	
	private final Point point;
	
	public AddMethodCommand(GraphDocument document, Method method, Point p) {
		super();
		this.document = document;
		this.method = method;
		this.point = p;
		putValue(NAME, ReflectUtil.getSignature(method));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(GraphicsEnvironment.isHeadless())
			return;
		
		if(document != null) {
			final MethodNode methodNode = new MethodNode(method);
			final AddNodeEdit edit = new AddNodeEdit(document.getGraph(), methodNode, point.x, point.y);
			document.getUndoSupport().postEdit(edit);
		}
	}

}
