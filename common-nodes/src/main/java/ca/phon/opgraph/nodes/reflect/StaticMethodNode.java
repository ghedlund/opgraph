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
package ca.phon.opgraph.nodes.reflect;

import java.lang.reflect.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.exceptions.*;

public class StaticMethodNode extends MethodNode {

	public StaticMethodNode() {
		super();
	}

	public StaticMethodNode(Method method) {
		super(method);
	}

	@Override
	public void setMethod(Method method) {
		super.setMethod(method);
		// remove 'obj' input field
		super.removeField(getInputFieldWithKey("obj"));
	}

	@Override
	public void operate(OpContext context) throws ProcessingException {
		// make sure 'obj' key is null
		context.put("obj", null);
		super.operate(context);
	}
	
}
