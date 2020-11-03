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

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.OpGraphListener;
import ca.phon.opgraph.OpLink;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OpNodeInfo;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.Processor;
import ca.phon.opgraph.ProcessorListener;
import ca.phon.opgraph.exceptions.ProcessingException;
import ca.phon.opgraph.extensions.CompositeNode;
import ca.phon.opgraph.extensions.CustomProcessing;
import ca.phon.opgraph.extensions.Publishable;

/**
 * A node that contains a macro operation: a collection of nodes that behave
 * as a single {@link OpNode}.  Node graphs may be linked or embedded.
 */
@OpNodeInfo(
	name="Macro",
	description="A set of nodes that behave as a single operation.",
	category="General"
)
public class MacroNode
	extends OpNode
	implements CompositeNode, CustomProcessing, Publishable
{
	/** The uri of the macro graph (optional) */
	protected URI graphURI;
	
	/** Should the graph be embedded or linked (default: embedded) */
	protected boolean isGraphEmbedded = true;
	
	/** The graph representing this macro */
	protected OpGraph graph;

	/** A list from published inputs */
	protected List<PublishedInput> publishedInputs;

	/** A list of published outputs */
	protected List<PublishedOutput> publishedOutputs;
	
	private Processor currentProcessor;
	
	private final List<ProcessorListener> processorListeners = new ArrayList<>();

	/**
	 * Constructs a new macro with no source file and a default graph.
	 */
	public MacroNode() {
		this(null, new OpGraph(), true);
	}
	
	/**
	 * Constructs a new macro with no source file and a specified graph.
	 * 
	 * @param graph  the graph
	 * 
	 * @throws NullPointerException  if the graph is <code>null</code>
	 */
	public MacroNode(OpGraph graph) {
		this(null, graph, true);
	}

	/**
	 * Constructs a new macro with source url and a specified graph.
	 * 
	 * @param graph  the graph
	 * 
	 * @throws NullPointerException  if the graph is <code>null</code>
	 */
	public MacroNode(URI graphURI, OpGraph graph, boolean isGraphEmbedded) {
		this.graphURI = graphURI;
		this.isGraphEmbedded = isGraphEmbedded;
		this.graph = (graph == null ? new OpGraph() : graph);
		this.publishedInputs = new ArrayList<PublishedInput>();
		this.publishedOutputs = new ArrayList<PublishedOutput>();
		
		this.graph.addGraphListener( new OpGraphListener() {
			
			@Override
			public void nodeRemoved(OpGraph graph, OpNode node) {
				if(graph != getGraph()) return;
				
				// unpublish fields if node is deleted
				for(PublishedInput pi:getPublishedInputs().toArray(new PublishedInput[0])) {
					if(pi.destinationNode == node) {
						unpublish(pi.destinationNode, pi.nodeInputField);
					}
				}
				for(PublishedOutput po:getPublishedOutputs().toArray(new PublishedOutput[0])) {
					if(po.sourceNode == node) {
						unpublish(po.sourceNode, po.nodeOutputField);
					}
				}
			}
			
			@Override
			public void nodeAdded(OpGraph graph, OpNode node) {
			}
			
			@Override
			public void linkRemoved(OpGraph graph, OpLink link) {
			}
			
			@Override
			public void linkAdded(OpGraph graph, OpLink link) {
			}

			@Override
			public void nodeSwapped(OpGraph graph, OpNode oldNode, OpNode newNode) {
			}
			
		});

		putExtension(CompositeNode.class, this);
		putExtension(CustomProcessing.class, this);
		putExtension(Publishable.class, this);
	}

	/**
	 * Add a processor listener which will be added to any processors
	 * created by the macro node.
	 * 
	 * @param listener
	 */
	public void addProcessorListener(ProcessorListener listener) {
		if(!processorListeners.contains(listener))
			processorListeners.add(listener);
	}
	
	public void removeProcessorListener(ProcessorListener listener) {
		processorListeners.remove(listener);
	}
	
	public List<ProcessorListener> getProcessorListeners() {
		return Collections.unmodifiableList(this.processorListeners);
	}

	/**
	 * Constructs a context mapping for this macro's published inputs. Inputs contained
	 * in the given context will be mapped to their appropriate node/input field in the
	 * internal graph this macro is using. The returned mapping will have the given context
	 * as the global context (i.e., the context mapped to by the <code>null</code> key).
	 * 
	 * @param context  the macro's local context
	 */
	protected void mapInputs(OpContext context) {
		for(PublishedInput publishedInput : publishedInputs) {
			final OpContext local = context.getChildContext(publishedInput.destinationNode);
			local.put(publishedInput.nodeInputField, context.get(publishedInput));
		}
	}

	/**
	 * Maps published outputs from a given context mapping to a given context.
	 * 
	 * @param context  the context to map outputs to
	 */
	protected void mapOutputs(OpContext context) {
		// Grab mapped outputs and put them in our context
		for(PublishedOutput publishedOutput : publishedOutputs) {
			OpContext sourceContext = context.findChildContext(publishedOutput.sourceNode);
			if(sourceContext != null)
				context.put(publishedOutput, sourceContext.get(publishedOutput.nodeOutputField));
		}
	}

	//
	// Overrides
	//

	@Override
	public void operate(OpContext context) throws ProcessingException {
		if(graph != null) {
			// First set up processor
			currentProcessor = new Processor(graph);
			currentProcessor.reset(context);
			for(ProcessorListener listener:getProcessorListeners())
				currentProcessor.addProcessorListener(listener);

			// The reset call above could clear out the context, so map after
			mapInputs(context);

			// Now run the graph
			currentProcessor.stepAll();
			if(currentProcessor.getError() != null)
				throw currentProcessor.getError();

			// Map the published outputs from the child nodes back into context
			mapOutputs(context);
			
			// free macro processor memory
			currentProcessor.getContext().clearChildContexts();
		}
	}
	
	@Override
	public void setCanceled(boolean canceled) {
		super.setCanceled(canceled);
		if(currentProcessor != null)
			currentProcessor.stop();
	}

	//
	// CompositeNode
	//
	
	public URI getGraphURI() {
		return this.graphURI;
	}
	
	public void setGraphURI(URI uri) {
		this.graphURI = uri;
	}
	
	public boolean isGraphEmbedded() {
		return getGraphURI() == null || isGraphEmbedded;
	}
	
	public void setGraphEmbedded(boolean graphEmbedded) {
		this.isGraphEmbedded = graphEmbedded;
	}

	@Override
	public OpGraph getGraph() {
		return graph;
	}

	@Override
	public void setGraph(OpGraph graph) {
		this.graph = graph;
	}

	//
	// CustomProcessing
	//

	@Override
	public CustomProcessor getCustomProcessor() {
		final Iterator<OpNode> nodeIter = graph.getVertices().iterator();
		return new CustomProcessor() {
			@Override
			public void remove() {
				throw new UnsupportedOperationException("remove not supported");
			}

			@Override
			public OpNode next() {
				return nodeIter.next();
			}

			@Override
			public boolean hasNext() {
				return nodeIter.hasNext();
			}

			@Override
			public void initialize(OpContext context) {
				mapInputs(context);
			}

			@Override
			public void terminate(OpContext context) {
				mapOutputs(context);
			}
		};
	}

	//
	// Publishable
	//

	@Override
	public InputField publish(String key, OpNode destination, InputField field) {
		// First, check to see if the field isn't already published
		InputField publishedInput = getPublishedInput(destination, field);

		// If no existing published input field for the given, create a new one.
		// Otherwise, set the key of the old one to the newly specified key.
		if(publishedInput == null) {
			final PublishedInput newInputField = new PublishedInput(key, destination, field);
			publishedInputs.add(newInputField);
			putField(newInputField);
			publishedInput = newInputField;
		} else {
			publishedInput.setKey(key);
		}

		return publishedInput;
	}

	@Override
	public OutputField publish(String key, OpNode source, OutputField field) {
		// First, check to see if the field isn't already published
		OutputField publishedOutput = getPublishedOutput(source, field);

		// If no existing published output field for the given, create a new one.
		// Otherwise, set the key of the old one to the newly specified key.
		if(publishedOutput == null) {
			final PublishedOutput newOutputField = new PublishedOutput(key, source, field);
			publishedOutputs.add(newOutputField);
			putField(newOutputField);
			publishedOutput = newOutputField;
		} else {
			publishedOutput.setKey(key);
		}

		return publishedOutput;
	}

	@Override
	public void unpublish(OpNode destination, InputField field) {
		Iterator<PublishedInput> iter = publishedInputs.iterator();
		while(iter.hasNext()) {
			PublishedInput publishedInput = iter.next();
			if(publishedInput.destinationNode == destination
					&& publishedInput.nodeInputField == field)
			{
				removeField(publishedInput);
				iter.remove();
				break;
			}
		}
	}

	@Override
	public void unpublish(OpNode destination, OutputField field) {
		Iterator<PublishedOutput> iter = publishedOutputs.iterator();
		while(iter.hasNext()) {
			PublishedOutput publishedOutput = iter.next();
			if(publishedOutput.sourceNode == destination
					&& publishedOutput.nodeOutputField == field)
			{
				removeField(publishedOutput);
				iter.remove();
				break;
			}
		}
	}

	@Override
	public List<PublishedInput> getPublishedInputs() {
		return Collections.unmodifiableList(publishedInputs);
	}

	@Override
	public List<PublishedOutput> getPublishedOutputs() {
		return Collections.unmodifiableList(publishedOutputs);
	}

	@Override
	public PublishedInput getPublishedInput(OpNode destination, InputField field) {
		PublishedInput foundInput = null;
		for(PublishedInput publishedInput : publishedInputs) {
			if(publishedInput.destinationNode == destination
					&& publishedInput.nodeInputField == field)
			{
				foundInput = publishedInput;
				break;
			}
		}

		return foundInput;
	}

	@Override
	public PublishedOutput getPublishedOutput(OpNode source, OutputField field) {
		PublishedOutput foundOutput = null;
		for(PublishedOutput publishedOutput : publishedOutputs) {
			if(publishedOutput.sourceNode == source
					&& publishedOutput.nodeOutputField == field)
			{
				foundOutput = publishedOutput;
				break;
			}
		}

		return foundOutput;
	}
}
