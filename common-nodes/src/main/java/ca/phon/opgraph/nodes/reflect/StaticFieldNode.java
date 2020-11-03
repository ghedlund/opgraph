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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class StaticFieldNode extends FieldNode {

	public StaticFieldNode() {
		super();
	}

	public StaticFieldNode(Field field) {
		super(field);
	}

	@Override
	public void setField(Field field) {
		if(!Modifier.isStatic(field.getModifiers()))
			throw new IllegalArgumentException("Field must be static");
		super.setField(field);
		super.removeField(objInputField);
	}
	
}
