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
package ca.phon.opgraph.nodes.math;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.exceptions.ProcessingException;
import ca.phon.opgraph.nodes.math.MathExpressionNode;
import ca.phon.opgraph.util.Pair;

/**
 * Test nodes in {@link ca.phon.opgraph.nodes.math}.
 */
public class TestMathNodes {
	@Test
	public void testMathExpression() throws ProcessingException {
		final MathExpressionNode node = new MathExpressionNode();
		final OpContext context = new OpContext();

		final double x = 5.00123124122;
		final double y = 10.98877841241;
		final double z = 37.2478231289379;

		context.put("x", x);
		context.put("y", y);
		context.put("z", z);

		final ArrayList<Pair<String, Double>> expressions = new ArrayList<Pair<String, Double>>();

		// Hide logging
		Logger.getLogger(MathExpressionNode.class.getName()).setLevel(Level.WARNING);

		// Some basic identities
		expressions.add(new Pair<String, Double>( "x + 0", x ));
		expressions.add(new Pair<String, Double>( "x - x", 0.0 ));
		expressions.add(new Pair<String, Double>( "x + -x", 0.0 ));
		expressions.add(new Pair<String, Double>( "-(-(x))", x ));
		expressions.add(new Pair<String, Double>( "((x))", x ));
		expressions.add(new Pair<String, Double>( "-x", -x ));
		expressions.add(new Pair<String, Double>( "x*1", x ));
		expressions.add(new Pair<String, Double>( "1*x", x ));
		expressions.add(new Pair<String, Double>( "x", x ));

		// Operations
		expressions.add(new Pair<String, Double>( "x+y", x + y ));
		expressions.add(new Pair<String, Double>( "x  - y", x - y ));
		expressions.add(new Pair<String, Double>( "x   *y", x * y ));
		expressions.add(new Pair<String, Double>( "   z%    y   ", z % y ));
		expressions.add(new Pair<String, Double>( "x +(y -   z)", x + (y - z) ));
		expressions.add(new Pair<String, Double>( "2*x+ -5*(-y + 5.102*z)", 2*x + -5*(-y + 5.102*z) ));
		expressions.add(new Pair<String, Double>( "1.0258*(x + 5*(y - 6124.293))", 1.0258*(x + 5*(y - 6124.293)) ));

		for(Pair<String, Double> expression : expressions) {
			node.setExpression(expression.getFirst());
			node.operate(context);

			assertTrue("Output exists", context.containsKey(node.RESULT_OUTPUT_FIELD));

			final double result = ((Number)context.get(node.RESULT_OUTPUT_FIELD)).doubleValue();
			assertEquals(expression.getFirst(), expression.getSecond(), result, 1e-10);
		}

		// Significant decimal places
		node.setExpression("x+y");
		{
			node.setSignificantDigits(5);
			node.operate(context);

			final double value = ((Number)context.get(node.RESULT_OUTPUT_FIELD)).doubleValue();
			final double expected = x + y;
			assertTrue("ensure within range of significance", Math.abs(value - expected) < 1e-5);
		}
		{
			node.setSignificantDigits(0);
			node.operate(context);

			final double value = ((Number)context.get(node.RESULT_OUTPUT_FIELD)).doubleValue();
			final double expected = x + y;
			assertTrue("loss of data expected", Math.abs(value - expected) < 1);
		}
	}
}
