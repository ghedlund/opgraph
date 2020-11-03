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
package ca.phon.opgraph.nodes.general;

import static org.junit.Assert.*;

import org.junit.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.exceptions.*;
import ca.phon.opgraph.nodes.reflect.*;

/**
 * Tests {@link MacroNode}.
 */
public class TestMacroNode {
	static class AddNode extends OpNode {
		public final static InputField X_FIELD = new InputField("x", "", false, true, Double.class);
		public final static InputField Y_FIELD = new InputField("y", "", false, true, Double.class);
		public final static OutputField RESULT_FIELD = new OutputField("result", "", true, Double.class);

		public AddNode() {
			super("Add", "Computes x + y");
			putField(X_FIELD);
			putField(Y_FIELD);
			putField(RESULT_FIELD);
		}

		@Override
		public void operate(OpContext context) {
			double x = (Double)context.get(X_FIELD);
			double y = (Double)context.get(Y_FIELD);
			context.put(RESULT_FIELD, x + y);
		}
	}

	static class MultiplyNode extends OpNode {
		public final static InputField X_FIELD = new InputField("x", "", false, true, Double.class);
		public final static InputField Y_FIELD = new InputField("y", "", true, true, Double.class);
		public final static OutputField RESULT_FIELD = new OutputField("result", "", true, Double.class);

		public MultiplyNode() {
			super("Multiply", "Computes x*y");
			putField(X_FIELD);
			putField(Y_FIELD);
			putField(RESULT_FIELD);
		}

		@Override
		public void operate(OpContext context) {
			double x = (Double)context.get(X_FIELD);
			double y = 1.0;
			if(context.containsKey(Y_FIELD))
				y = (Double)context.get(Y_FIELD);

			context.put(RESULT_FIELD, x * y);
		}
	}

	static class LessThanNode extends OpNode {
		public final static InputField X_FIELD = new InputField("x", "", false, true, Double.class);
		public final static InputField Y_FIELD = new InputField("y", "", false, true, Double.class);
		public final static OutputField RESULT_FIELD = new OutputField("result", "", true, Boolean.class);

		public LessThanNode() {
			super("Less Than", "Computes x < y");
			putField(X_FIELD);
			putField(Y_FIELD);
			putField(RESULT_FIELD);
		}

		@Override
		public void operate(OpContext context) {
			double x = (Double)context.get(X_FIELD);
			double y = (Double)context.get(Y_FIELD);
			context.put(RESULT_FIELD, x < y);
		}
	}

	/**
	 * Processes an operable graph.
	 * 
	 * @param graph  the graph to process
	 * @param context  the operating context, or <code>null</code> to use a default one
	 * 
	 * @return  the operating context used for processing
	 * 
	 * @throws ProcessingException  if any errors occurred during processing
	 */
	public static OpContext process(OpGraph graph, OpContext context)
		throws ProcessingException
	{
		final Processor processor = new Processor(graph);
		processor.reset(context);
		processor.stepAll();
		if(processor.getError() != null)
			throw processor.getError();

		return processor.getContext();
	}

	/**
	 * Gets a result from the execution of a graph.
	 * 
	 * @param cls  the type of result
	 * @param graph  the graph to execute
	 * @param context  the operating context, or <code>null</code> to use a default one
	 * @param resultNode  the node containing the result
	 * @param resultField  the field containing the result
	 * 
	 * @return the result
	 * 
	 * @throws ProcessingException  if any errors occurred during processing
	 */
	public static <T> T getResult(Class<T> cls,
	                              OpGraph graph,
	                              OpContext context,
	                              OpNode resultNode,
	                              OutputField resultField)
		throws ProcessingException
	{
		return cls.cast(process(graph, context).findChildContext(resultNode).get(resultField));
	}

	/**
	 * Constructs an operable graph that computes the minimum of two values.
	 * 
	 * @param inputs  an array for returning the two nodes for inputs
	 * @param outputs  an array for returning the single node containing the output
	 * 
	 * @return the graph
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	private static OpGraph createMinDAG(PassThroughNode[] inputs, PassThroughNode[] outputs) throws NoSuchMethodException, SecurityException {
		//
		// Constructs a dag that computes the minimum of two values, making
		// use of the ENABLED_FIELD feature of OpNode 
		//
		final OpGraph minDAG = new OpGraph();
		minDAG.setId("min");

		final PassThroughNode pt1 = new PassThroughNode();
		final PassThroughNode pt2 = new PassThroughNode();		
		final PassThroughNode ov1 = new PassThroughNode();
		
		final StaticMethodNode minNode = new StaticMethodNode(Math.class.getMethod("min", double.class, double.class));

		// Return values
		inputs[0] = pt1;
		inputs[1] = pt2;
		outputs[0] = ov1;

		// Add nodes
		minDAG.add(pt1);
		minDAG.add(pt2);
		minDAG.add(ov1);
		minDAG.add(minNode);

		// Add link
		assertNotNull(minDAG.connect(pt1, PassThroughNode.OUTPUT, minNode, minNode.getInputFieldWithKey("arg1")));
		assertNotNull(minDAG.connect(pt2, PassThroughNode.OUTPUT, minNode, minNode.getInputFieldWithKey("arg2")));

		assertNotNull(minDAG.connect(minNode, minNode.getOutputFieldWithKey("value"), ov1, PassThroughNode.INPUT));

		return minDAG;
	}

	/** Tests the correctness of a macro 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException */
	@Test
	public void testMacro() throws NoSuchMethodException, SecurityException {
		PassThroughNode [] inputs1 = new PassThroughNode[2];
		PassThroughNode [] outputs1 = new PassThroughNode[1];

		PassThroughNode [] inputs2 = new PassThroughNode[2];
		PassThroughNode [] outputs2 = new PassThroughNode[1];

		OpGraph dag = new OpGraph();
		OpGraph minDAG1 = createMinDAG(inputs1, outputs1);
		OpGraph minDAG2 = createMinDAG(inputs2, outputs2);

		ConstantValueNode cv1 = new ConstantValueNode(1.0);
		ConstantValueNode cv2 = new ConstantValueNode(2.0);
		ConstantValueNode cv3 = new ConstantValueNode(3.0);

		MacroNode min1 = new MacroNode(minDAG1);
		MacroNode min2 = new MacroNode(minDAG2);

		dag.add(cv1);
		dag.add(cv2);
		dag.add(cv3);
		dag.add(min1);
		dag.add(min2);

		// Publish inputs/outputs from macros
		InputField min1_in1 = min1.publish("x", inputs1[0], PassThroughNode.INPUT);
		InputField min1_in2 = min1.publish("y", inputs1[1], PassThroughNode.INPUT);
		OutputField min1_out1 = min1.publish("result", outputs1[0], PassThroughNode.OUTPUT);

		InputField min2_in1 = min2.publish("x", inputs2[0], PassThroughNode.INPUT);
		InputField min2_in2 = min2.publish("y", inputs2[1], PassThroughNode.INPUT);
		OutputField min2_out1 = min2.publish("result", outputs2[0], PassThroughNode.OUTPUT);

		try {			
			assertNotNull(dag.connect(cv1, cv1.VALUE_OUTPUT_FIELD, min1, min1_in1));
			assertNotNull(dag.connect(cv2, cv2.VALUE_OUTPUT_FIELD, min1, min1_in2));
			assertNotNull(dag.connect(min1, min1_out1, min2, min2_in1));
			assertNotNull(dag.connect(cv3, cv3.VALUE_OUTPUT_FIELD, min2, min2_in2));

			for(int i = 0; i < 5; ++i) {
				for(int j = 0; j < 5; ++j) {
					for(int k = 0; k < 5; ++k) {
						double minVal = Math.min(Math.min(i, j), k);
						cv1.setValue(1.0*i);
						cv2.setValue(1.0*j);
						cv3.setValue(1.0*k);

						double result = getResult(Double.class, dag, null, min2, min2_out1);
						assertEquals(minVal, result, 1e-10);
					}
				}
			}
		} catch(ProcessingException exc) {
			if(exc.getCause() != null)
				exc.getCause().printStackTrace();
			else
				exc.printStackTrace();

			fail("Should be no errors when processing");
		}
	}
}
