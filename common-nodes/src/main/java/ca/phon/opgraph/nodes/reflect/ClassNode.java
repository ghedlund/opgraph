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
package ca.phon.opgraph.nodes.reflect;

import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.exceptions.ProcessingException;

/**
 * Output the {@link Class} object for a given type.
 *
 */
public class ClassNode extends AbstractReflectNode {
	
	private OutputField classOutput = new OutputField("class", "Class object", true, Class.class);

	public ClassNode() {
		this(Object.class);
	}
	
	public ClassNode(Class<?> type) {
		super(type, null);
		putField(classOutput);
	}
	
	@Override
	public void operate(OpContext context) throws ProcessingException {
		context.put(classOutput, super.getDeclaredClass());
	}

}
