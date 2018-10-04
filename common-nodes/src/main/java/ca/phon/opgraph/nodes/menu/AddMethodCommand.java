/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
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
