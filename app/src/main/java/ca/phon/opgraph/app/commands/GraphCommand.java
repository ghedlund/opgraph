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
package ca.phon.opgraph.app.commands;

import ca.phon.opgraph.app.*;

public abstract class GraphCommand extends HookableCommand {
	
	protected final GraphDocument document;

	public GraphCommand(GraphDocument document) {
		super();
		
		this.document = document;
	}
	
	public GraphCommand(String txt, GraphDocument document) {
		super(txt);
		
		this.document = document;
	}
	
	public GraphDocument getDocument() {
		return this.document;
	}

}
