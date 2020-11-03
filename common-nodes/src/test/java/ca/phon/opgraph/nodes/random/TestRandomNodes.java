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
package ca.phon.opgraph.nodes.random;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.exceptions.ProcessingException;
import ca.phon.opgraph.nodes.random.RandomBooleanNode;
import ca.phon.opgraph.nodes.random.RandomDecimalNode;
import ca.phon.opgraph.nodes.random.RandomIntegerNode;
import ca.phon.opgraph.nodes.random.RandomStringNode;

/**
 * Test nodes in {@link ca.phon.opgraph.nodes.random}.
 */
public class TestRandomNodes {
	@Test
	public void testRandomInteger() throws ProcessingException {
		final RandomIntegerNode node = new RandomIntegerNode();
		final OpContext context = new OpContext();

		node.operate(context);
		assertTrue("Output exists", context.containsKey(node.VALUE_OUTPUT));
		assertTrue("Output value is an integer", Integer.class.isInstance(context.get(node.VALUE_OUTPUT)));

		context.put(node.MIN_INPUT, 50);
		node.operate(context);
		assertTrue("Output exists", context.containsKey(node.VALUE_OUTPUT));
		assertTrue("Output value is an integer", Integer.class.isInstance(context.get(node.VALUE_OUTPUT)));
		assertTrue("Output greater than min", ((Integer)context.get(node.VALUE_OUTPUT)) >= 50);

		context.put(node.MAX_INPUT, 80);
		node.operate(context);
		assertTrue("Output exists", context.containsKey(node.VALUE_OUTPUT));
		assertTrue("Output value is an integer", Integer.class.isInstance(context.get(node.VALUE_OUTPUT)));

		final int value = (Integer)context.get(node.VALUE_OUTPUT);
		assertTrue("Output in range", (value >= 50) && (value <= 80));
	}

	@Test
	public void testRandomDecimal() throws ProcessingException {
		final RandomDecimalNode node = new RandomDecimalNode();
		final OpContext context = new OpContext();

		node.operate(context);
		assertTrue("Output exists", context.containsKey(node.VALUE_OUTPUT));
		assertTrue("Output value is a double", Double.class.isInstance(context.get(node.VALUE_OUTPUT)));

		context.put(node.MIN_INPUT, 50);
		node.operate(context);
		assertTrue("Output exists", context.containsKey(node.VALUE_OUTPUT));
		assertTrue("Output value is a double", Double.class.isInstance(context.get(node.VALUE_OUTPUT)));
		assertTrue("Output greater than min", ((Double)context.get(node.VALUE_OUTPUT)) >= 50);

		context.put(node.MAX_INPUT, 80);
		node.operate(context);
		assertTrue("Output exists", context.containsKey(node.VALUE_OUTPUT));
		assertTrue("Output value is a double", Double.class.isInstance(context.get(node.VALUE_OUTPUT)));

		final double value = (Double)context.get(node.VALUE_OUTPUT);
		assertTrue("Output in range", (value + 1e-10 >= 50) && (value <= 80 + 1e-10));
	}

	@Test
	public void testRandomBoolean() throws ProcessingException {
		final RandomBooleanNode node = new RandomBooleanNode();
		final OpContext context = new OpContext();

		node.operate(context);
		assertTrue("Output exists", context.containsKey(node.VALUE_OUTPUT));
		assertTrue("Output value is a boolean", Boolean.class.isInstance(context.get(node.VALUE_OUTPUT)));
	}

	@Test
	public void testRandomString() throws ProcessingException {
		final RandomStringNode node = new RandomStringNode();
		final OpContext context = new OpContext();

		for(int test = 0; test < 50; ++test) {
			final int LENGTH = (int)(Math.random() * 500) + 10;
			context.put(node.LENGTH_INPUT, LENGTH);
			node.operate(context);
			assertTrue("Output exists", context.containsKey(node.VALUE_OUTPUT));
			assertTrue("Output value is a string", String.class.isInstance(context.get(node.VALUE_OUTPUT)));
			assertEquals("Output value is correct length", LENGTH, context.get(node.VALUE_OUTPUT).toString().length());
		}
	}
}
