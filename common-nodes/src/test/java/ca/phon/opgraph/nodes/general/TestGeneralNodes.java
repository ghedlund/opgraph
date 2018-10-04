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
package ca.phon.opgraph.nodes.general;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.TreeSet;

import org.junit.Test;

import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.nodes.general.RangeNode;

/**
 * Test nodes in {@link ca.phon.opgraph.nodes.general}.
 */
public class TestGeneralNodes {
	@Test
	public void testRange() {
		final RangeNode node = new RangeNode();
		final OpContext context = new OpContext();

		for(int test = 0; test < 10; ++test) {
			final int start = (int)((2*Math.random() - 1)*1000);
			final int end = start + (int)(Math.random()*50);

			final TreeSet<Integer> expected = new TreeSet<Integer>();
			for(int value = start; value <= end; ++value)
				expected.add(value);

			context.put(node.START_INPUT_FIELD, start);
			context.put(node.END_INPUT_FIELD, end);
			node.operate(context);

			assertTrue("Result exists", context.containsKey(node.RANGE_OUTPUT_FIELD));
			assertEquals(expected, new TreeSet<Object>((Collection<?>)context.get(node.RANGE_OUTPUT_FIELD)));
		}
	}
}
