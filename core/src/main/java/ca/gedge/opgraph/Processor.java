/*
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * This file is part of the OpGraph project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.gedge.opgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import ca.gedge.opgraph.ProcessorEvent.Type;
import ca.gedge.opgraph.exceptions.BreakpointEncountered;
import ca.gedge.opgraph.exceptions.InvalidTypeException;
import ca.gedge.opgraph.exceptions.ProcessingException;
import ca.gedge.opgraph.exceptions.RequiredInputException;
import ca.gedge.opgraph.extensions.CompositeNode;
import ca.gedge.opgraph.extensions.CustomProcessing;
import ca.gedge.opgraph.extensions.CustomProcessing.CustomProcessor;
import ca.gedge.opgraph.validators.TypeValidator;

/**
 * A processing context for {@link OpGraph} instances. A fine level of control
 * is given, allowing one to step through an operable graph in various ways.
 */
public class Processor {
	/** The graph this processor is operating on */
	private OpGraph graph;

	/** Custom processing needs of the given graph */
	private CustomProcessor customProcessor;

	/**
	 * An iterator that points to the node we are currently processing on,
	 * or <code>null</code> if processing hasn't begun/should be restarted.
	 */
	private Iterator<OpNode> nodeIter;

	/** The node we are operating on*/
	private OpNode currentNode;

	/** The context map used for processing */
	private OpContext globalContext;

	/** The error that happened in the last step, or <code>null</code> if no error */
	private ProcessingException currentError;

	/** If we stepped into a macro, the processing context for that macro */
	private Processor currentMacro;
	
	/** 
	 * If stopped at a breakpoint during the {{@link #step()} method this will
	 * point to the breakpoint node.
	 */
	private OpNode breakpointNode = null;
	
	/**
	 * Processor listener
	 */
	private List<ProcessorListener> listeners =
			Collections.synchronizedList(new ArrayList<>());

	/**
	 * Constructs a processing context for a given graph.
	 * 
	 * @param graph  the graph
	 * 
	 * @throws NullPointerException  if the specified graph is <code>null</code>
	 */
	public Processor(OpGraph graph) {
		this(graph, null, null);
	}

	/**
	 * Constructs a processing context for a given graph and a preset operating context.
	 * 
	 * @param graph  the graph
	 * @param context  the initial global context, or <code>null</code> to
	 *                 use an empty global context
	 * 
	 * @throws NullPointerException  if the specified graph is <code>null</code>
	 */
	public Processor(OpGraph graph, OpContext context) {
		this(graph, null, context);
	}

	/**
	 * Constructs a processing context for a given graph.
	 * 
	 * @param graph  the graph
	 * @param customProcessor  a custom processing instance, or <code>null</code>
	 *                         if no custom processing required
	 * @param context  the initial global context, or <code>null</code> to
	 *                 use an empty global context
	 * 
	 * @throws NullPointerException  if the specified graph is <code>null</code>
	 */
	public Processor(OpGraph graph, CustomProcessor customProcessor, OpContext context) {
		if(graph == null)
			throw new NullPointerException("Graph cannot be null");

		this.graph = graph;
		this.graph.invalidateSort();
		this.graph.topologicalSort();
		
		this.customProcessor = customProcessor;

		reset(context);
	}

	/**
	 * Resets this context so that further processing will start from the
	 * beginning.
	 */
	public void reset() {
		reset(globalContext == null ? null : globalContext);
	}

	/**
	 * Resets this context so that further processing will start from the
	 * beginning.
	 * 
	 * @param context  the global context that should be used for processing,
	 *                 or <code>null</code> if a default one should be used
	 */
	public void reset(OpContext context) {
		currentMacro = null;
		currentError = null;
		currentNode = null;

		// Set up node iteration
		nodeIter = null;
		if(customProcessor != null)
			nodeIter = customProcessor;

		if(nodeIter == null)
			nodeIter = graph.getVertices().iterator();

		// Set up context
		if(globalContext != null && globalContext == context)
			globalContext.clearChildContexts();

		globalContext = context;
		if(globalContext == null)
			globalContext = new OpContext();

		if(customProcessor != null)
			customProcessor.initialize(globalContext);
	}

	/**
	 * Gets the graph that is currently being operated on.
	 * 
	 * @return  the graph
	 */
	public OpGraph getGraph() {
		if(currentMacro != null)
			return currentMacro.getGraph();
		return graph;
	}

	/**
	 * Gets the graph this processing context is operating on.
	 * 
	 * @return  the graph
	 */
	public OpGraph getGraphOfContext() {
		return graph;
	}

	/**
	 * Gets the context used for processing. 
	 * 
	 * @return the context
	 */
	public OpContext getContext() {
		return globalContext;
	}

	/**
	 * Gets the error that was thrown since the last reset.
	 * 
	 * @return the error, or <code>null</code> if no error was thrown
	 */
	public ProcessingException getError() {
		ProcessingException error = currentError;
		if(error == null && currentMacro != null)
			error = currentMacro.getError();
		return error;
	}

	/**
	 * Gets the context that spawned the error returned by {@link #getError()}.
	 * 
	 * @return the error-spawning context, or <code>null</code> if
	 *         <code>{@link #getError()} == null</code>
	 */
	public Processor getErrorContext() {
		if(currentError != null)
			return this;
		return (currentMacro == null ? null : currentMacro.getErrorContext());
	}

	/**
	 * Gets the node that was most recently processed.
	 * 
	 * @return the node, or <code>null</code> if processing has yet to
	 *         start or no more nodes to process.
	 */
	public OpNode getCurrentNode() {
		if(currentMacro != null)
			return currentMacro.getCurrentNode();
		return currentNode;
	}

	/**
	 * Gets the node that was most recently processed in this context.
	 * 
	 * @return the node, or <code>null</code> if processing has yet to
	 *         start or no more nodes to process.
	 */
	public OpNode getCurrentNodeOfContext() {
		return currentNode;
	}

	/**
	 * Gets the processing context for the last macro that was stepped into.
	 * 
	 * @return the processing context, or <code>null</code> if no macro was
	 *         stepped into
	 */
	public Processor getMacroContext() {
		return currentMacro;
	}

	/**
	 * Gets whether or not there are any more nodes to process.
	 * 
	 * @return  <code>true</code> if there are more nodes to process,
	 *          <code>false</code> otherwise
	 */
	public boolean hasNext() {
		return (currentMacro != null || (nodeIter != null && nodeIter.hasNext()));
	}

	/**
	 * Moves the processing forward. If in a macro and the last node in that
	 * macro was already processed, the step will step out of the macro and
	 * back to its parent node, but processing will not move forward in the
	 * parent until this function is called again. If the next node to process
	 * is flagged as a breakpoint and <code>shouldBreak</code> is <code>true</code>
	 * this method will <em>not</em> process the next node and will throw a
	 * {@link BreakpointEncountered} exception.
	 * 
	 * @param shouldBreak
	 * 
	 * @throws NoSuchElementException  if there are no more nodes to process
	 * @throws BreakpointEncountered if the next node is a breakpoint and shouldBreak is true
	 */
	public void step(boolean shouldBreak) throws BreakpointEncountered {
		if(breakpointNode != null && currentNode == breakpointNode) {
			processCurrentNode();
			return;
		}
		if(currentMacro != null) {
			if(currentMacro.hasNext())
				currentMacro.step(shouldBreak);
			else
				stepOutOf();
		} else if(nodeIter == null) {
			throw new NoSuchElementException("No nodes to process");
		} else {
			// Step to the next node and process
			currentNode = nodeIter.next();
			
			if(currentNode != null && currentNode.isBreakpoint() && shouldBreak) {
				throw new BreakpointEncountered(this, currentNode);
			}
			
			processCurrentNode();
		}
	}
	
	/**
	 * Moves the processing forward. If in a macro and the last node in that
	 * macro was already processed, the step will step out of the macro and
	 * back to its parent node, but processing will not move forward in the
	 * parent until this function is called again.
	 * 
	 * @throws NoSuchElementException  if there are no more nodes to process
	 */
	public void step() {
		step(false);
	}

	/**
	 * Processes the current node.
	 * 
	 * @throws ProcessingException  if any errors occurred during proessing
	 */
	private void processCurrentNode() {
		try {
			final OpContext localContext = globalContext.getChildContext(currentNode);
			setupInputs(currentNode, localContext);

			Boolean enabled = (Boolean)localContext.get(OpNode.ENABLED_FIELD);
			if(enabled == null || enabled) {
				fireBeginNodeEvent();
				currentNode.operate(localContext);
				fireEndNodeEvent();
			}

			if(!hasNext() && customProcessor != null)
				customProcessor.terminate(globalContext);
		} catch(ProcessingException exc) {
			//LOGGER.log(Level.SEVERE, exc.getLocalizedMessage(), exc);
			currentError = exc;
			nodeIter = null; // prevent further processing
			throw currentError;
		} catch(Throwable exc) {
			//LOGGER.log(Level.SEVERE, exc.getLocalizedMessage(), exc);
			currentError = new ProcessingException(this, exc);
			nodeIter = null; // prevent further processing
			throw currentError;
		}
	}

	/**
	 * Processes the graph until we reach the next node level.
	 */
	public void stepToNextLevel() {
		if(currentMacro != null) {
			if(currentMacro.hasNext())
				currentMacro.stepToNextLevel();
			else
				stepOutOf();
		} else {
			final int level = graph.getLevel(currentNode);
			while(hasNext() && graph.getLevel(currentNode) == level)
				step();
		}
	}

	public boolean stepToNode(OpNode node, boolean shouldBreak) throws BreakpointEncountered {
		boolean found = false;
		if(currentMacro != null) {
			if(currentMacro.hasNext())
				found = currentMacro.stepToNode(node);
			
			if(!found && !currentMacro.hasNext())
				stepOutOf();
		}
		
		if(!found) {
			while(hasNext() && currentNode != node)
				step(shouldBreak);
			
			found = (currentNode == node);
		}
		
		return found;
	}
	
	/**
	 * Processes the graph until we hit the specified node. This method
	 * will step through the current macro (if one was stepped into), but
	 * will not automatically step into macros to find the specified node.
	 * 
	 * @param node  the node to stop processing at
	 * 
	 * @return <code>true</code> if the specified node was found,
	 *         <code>false</code> otherwise
	 */
	public boolean stepToNode(OpNode node) {
		return stepToNode(node, getContext().isDebug());
	}

	/**
	 * Step into a macro. If the current node isn't a macro, this
	 * function behaves exactly like {@link #step()}.
	 * 
	 * @throws NoSuchElementException  if no more nodes to process
	 */
	public void stepInto() {
		if(currentMacro != null) {
			if(currentMacro.hasNext())
				currentMacro.stepInto();
			else
				stepOutOf();
		} else {
			currentNode = nodeIter.next();

			final CompositeNode composite = currentNode.getExtension(CompositeNode.class);
			if(composite != null) {
				try {
					final OpContext context = globalContext.getChildContext(currentNode);
					setupInputs(currentNode, context);

					final CustomProcessing customProcessing = currentNode.getExtension(CustomProcessing.class);
					final CustomProcessor customProcessor = (customProcessing == null ? null : customProcessing.getCustomProcessor());
					currentMacro = new Processor(composite.getGraph(), customProcessor, context);
				} catch(ProcessingException error) {
					currentError = error;
					currentMacro = null; // we didn't properly step into the macro, so null it
					nodeIter = null; // prevent further processing
				}
			} else {
				processCurrentNode();
			}
		}
	}

	/**
	 * Steps out of the current macro, if one was previously stepped into. Any
	 * unprocessed nodes in the macro will be processed. If there was no macro
	 * being processed by this processing context, this function does nothing.
	 */
	public void stepOutOf() {
		if(currentMacro != null) {
			if(currentMacro.getMacroContext() == null) {
				currentMacro.stepAll();
				currentError = currentMacro.getError();
				currentMacro = null;
			} else {
				currentMacro.stepOutOf();
			}
		}
	}
	
	/**
	 * Processes the graph to completion or
	 * breakpoint if <code>shouldBreak</code> is <code>true</code>
	 * 
	 * @param shouldBreak
	 */
	public void stepAll(boolean shouldBreak) throws BreakpointEncountered {
		while(hasNext()) {
			step(shouldBreak);
		}
		fireCompleteEvent();
	}

	/**
	 * Processes the graph to completion. Ignore
	 * breakpoints.
	 * 
	 * @throws BreakpointEncountered
	 * @throws ProcessingException
	 */
	public void stepAll() {
		stepAll(getContext().isDebug());
	}

	/**
	 * Adds inputs from incoming links to a given node's context.  
	 * 
	 * @param node  the node to create the inputs for
	 * @param context  the working context for this node
	 * 
	 * @throws ProcessingException  if the node has no working context 
	 * @throws RequiredInputException  if a value flowing into an input has an unacceptable type
	 */
	private void setupInputs(OpNode node, OpContext context)
		throws ProcessingException, RequiredInputException
	{
		// Check required inputs
		checkInputs(node, context);

		// Now set up the inputs
		for(OpLink link : graph.getIncomingEdges(node)) {
			final OpContext srcContext = globalContext.findChildContext(link.getSource());
			if(srcContext != null && srcContext.containsKey(link.getSourceField())) {
				final Object val = srcContext.get(link.getSourceField());
				final InputField dest = link.getDestinationField();
				context.put(dest, val);
			}
		}
	}

	/**
	 * Check field optionalities and make sure all required fields have input.
	 * When input exists, make sure the input is of a valid type, as determined
	 * by the field's {@link TypeValidator}
	 * 
	 * @param node  the node to create the inputs for
	 * @param context  the working context for this node
	 * 
	 * @throws ProcessingException  if the node has no working context 
	 * @throws RequiredInputException  if a value flowing into an input has an unacceptable type
	 */
	private void checkInputs(OpNode node, OpContext context)
		throws InvalidTypeException, RequiredInputException
	{
		//
		for(InputField field : node.getInputFields()) {
			// Working context already has value, no need to check links
			if(context.containsKey(field))
				continue;

			if(!field.isOptional()) {
				boolean linkFound = false;
				for(OpLink link : graph.getIncomingEdges(node)) {
					if(link.getDestinationField() == field) {
						// Make sure this link actually has a value flowing through it
						final OpContext sourceContext = globalContext.findChildContext(link.getSource());
						if(sourceContext != null && sourceContext.containsKey(link.getSourceField())) {
							final Object val = sourceContext.get(link.getSourceField());
							linkFound = true;

							// Make sure value type is accepted at the destination field
							final TypeValidator validator = field.getValidator();
							if(validator != null && !validator.isAcceptable(val))
								throw new InvalidTypeException(this, link.getDestinationField(), val);

							break;
						}
					}
				}

				// No link for required input; throw exception!
				if(!linkFound)
					throw new RequiredInputException(this, node, field);
			}
		}
	}
	
	/*
	 * Events
	 */
	public void addProcessorListener(ProcessorListener listener) {
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void removeProcessorListener(ProcessorListener listener) {
		listeners.remove(listener);
	}
	
	public void fireProcessorEvent(ProcessorEvent pe) {
		for(ProcessorListener listener:listeners) {
			listener.processorEvent(pe);
		}
	}
	
	public void fireBeginNodeEvent() {
		fireProcessorEvent(new ProcessorEvent(Type.BEGIN_NODE, this, currentNode));
	}
	
	public void fireEndNodeEvent() {
		fireProcessorEvent(new ProcessorEvent(Type.FINISH_NODE, this, currentNode));
	}
	
	public void fireCompleteEvent() {
		fireProcessorEvent(new ProcessorEvent(Type.COMPLETE, this, currentNode));
	}
}
