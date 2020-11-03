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
package ca.phon.opgraph.extensions;

import java.util.Iterator;
import java.util.Queue;

import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.Processor;

/**
 * An extension meant for any {@link OpNode} that requires custom initialization,
 * operation, and termination when being stepped into by a {@link Processor}.
 * {@link Processor} will only step into a node with a {@link CompositeNode} extension.
 * 
 * For example, a macro node may need to map inputs from the macro node's
 * {@link OpContext} to appropriate nodes  on initialization, and map outputs
 * from nodes to its {@link OpContext} on termination.
 */
public interface CustomProcessing {
	/**
	 * Interface for custom processors.
	 */
	public interface CustomProcessor extends Iterator<OpNode> {
		/**
		 * Initializes the given context for processing.
		 * 
		 * @param context  the context to initialize
		 */
		public abstract void initialize(OpContext context);

		/**
		 * Terminates processing, updating the given context as necessary.
		 * 
		 * @param context  the context to update, if necessary
		 */
		public abstract void terminate(OpContext context);
	}

	/**
	 * Gets a custom processor for a node.
	 * 
	 * @return a custom processor
	 */
	public abstract CustomProcessor getCustomProcessor();
}
