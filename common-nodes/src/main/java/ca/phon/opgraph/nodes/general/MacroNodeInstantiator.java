/*
 * Copyright (C) 2012-2018 Gregory Hedlund & Yvan Rose
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

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.app.OpgraphIO;
import ca.phon.opgraph.app.util.GraphUtils;
import ca.phon.opgraph.extensions.Publishable.PublishedInput;
import ca.phon.opgraph.extensions.Publishable.PublishedOutput;
import ca.phon.opgraph.library.instantiators.Instantiator;

/**
 * This class will generate a new macro node given the location
 * of an {@link OpGraph} document.
 * 
 */
public class MacroNodeInstantiator implements Instantiator<MacroNode> {

	@Override
	public MacroNode newInstance(Object... params) throws InstantiationException {
		if(params.length < 1) 
			throw new InstantiationException("Incorrect number of parameters");
		final Object obj = params[0];
		if(!(obj instanceof MacroNodeData)) 
			throw new InstantiationException("Incorrect node data type");
		final MacroNodeData nodeData = (MacroNodeData)obj;
		
		OpGraph parentGraph = null;
		if(params.length == 2 && params[1] instanceof OpGraph) {
			parentGraph = (OpGraph)params[1];
		}
		
		MacroNode retVal = null;
		// read graph document
		try {
			OpGraph graph = new OpGraph();
			graph = OpgraphIO.read(nodeData.getGraphURL().openStream());
			
			// change node ids in graph if embedded to ensure multiple instances do not share ids
			if(nodeData.isGraphEmbedded())
				GraphUtils.changeNodeIds(graph);
			
			if(graph.getVertices().size() == 1 && graph.getVertices().get(0) instanceof MacroNode) {
				// use the macro node from the graph
				MacroNode origNode = (MacroNode)graph.getVertices().get(0);
				retVal = new MacroNode(origNode.getGraph());
				
				for(PublishedInput pubInput:origNode.getPublishedInputs()) {
					retVal.publish(pubInput.getKey(), pubInput.destinationNode, pubInput.nodeInputField);
				}
				for(PublishedOutput pubOutput:origNode.getPublishedOutputs()) {
					retVal.publish(pubOutput.getKey(), pubOutput.sourceNode, pubOutput.nodeOutputField);
				}
			} else {
				retVal = new MacroNode(graph);
				
				// find input nodes and publish fields
				final OpNode projectNode = graph.getNodesByName("Project").stream().findFirst().orElse(null);
				if(projectNode != null) {
					// publish obj field to project
					retVal.publish("project", projectNode, projectNode.getInputFieldWithKey("obj"));
				}
			}
			
			retVal.setName(nodeData.name);
			retVal.setGraphURI(nodeData.uri);
			retVal.setGraphEmbedded(nodeData.isGraphEmbedded());
		} catch (IOException e) {
			throw new InstantiationException(e.getLocalizedMessage());
		}
		
		return retVal;
	}

}
