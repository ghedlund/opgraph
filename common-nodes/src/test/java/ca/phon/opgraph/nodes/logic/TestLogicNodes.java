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
package ca.phon.opgraph.nodes.logic;

import static org.junit.Assert.*;

import org.junit.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.exceptions.*;

/**
 * Test nodes in {@link ca.phon.opgraph.nodes.logic}.
 */
public class TestLogicNodes {
	final OpContext context = new OpContext();

	private void testUnary(OpNode node,
	                       InputField x, OutputField result,
	                       boolean xval, boolean expected)
	{
		testBinary(node, x, null, result, xval, true, expected);
	}

	private void testBinary(OpNode node,
	                        InputField x, InputField y, OutputField result,
	                        boolean xval, boolean yval, boolean expected)
	{
		context.clear();
		context.put(x, xval);

		if(y != null)
			context.put(y, yval);

		try {
			node.operate(context);
		} catch(ProcessingException exc) {
			fail(exc.getMessage());
		}

		assertTrue("result exists", context.containsKey(result));
		assertEquals(context.get(result), expected);
	}

	@Test
	public void testAnd() {
		final OpNode node = new LogicalAndNode();
		final InputField x = LogicalAndNode.X_INPUT_FIELD;
		final InputField y = LogicalAndNode.Y_INPUT_FIELD;
		final OutputField result = LogicalAndNode.RESULT_OUTPUT_FIELD;

		testBinary(node, x, y, result, true, true, true);
		testBinary(node, x, y, result, true, false, false);
		testBinary(node, x, y, result, false, true, false);
		testBinary(node, x, y, result, false, false, false);
	}

	@Test
	public void testOr() {
		final OpNode node = new LogicalOrNode();
		final InputField x = LogicalOrNode.X_INPUT_FIELD;
		final InputField y = LogicalOrNode.Y_INPUT_FIELD;
		final OutputField result = LogicalOrNode.RESULT_OUTPUT_FIELD;

		testBinary(node, x, y, result, true, true, true);
		testBinary(node, x, y, result, true, false, true);
		testBinary(node, x, y, result, false, true, true);
		testBinary(node, x, y, result, false, false, false);
	}

	@Test
	public void testXor() {
		final OpNode node = new LogicalXorNode();
		final InputField x = LogicalXorNode.X_INPUT_FIELD;
		final InputField y = LogicalXorNode.Y_INPUT_FIELD;
		final OutputField result = LogicalXorNode.RESULT_OUTPUT_FIELD;

		testBinary(node, x, y, result, true, true, false);
		testBinary(node, x, y, result, true, false, true);
		testBinary(node, x, y, result, false, true, true);
		testBinary(node, x, y, result, false, false, false);
	}

	@Test
	public void testNot() {
		final OpNode node = new LogicalNotNode();
		final InputField x = LogicalNotNode.X_INPUT_FIELD;
		final OutputField result = LogicalNotNode.RESULT_OUTPUT_FIELD;

		testUnary(node, x, result, true, false);
		testUnary(node, x, result, false, true);
	}
}
