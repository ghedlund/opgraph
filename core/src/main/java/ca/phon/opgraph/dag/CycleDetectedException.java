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
package ca.phon.opgraph.dag;

/**
 * An {@link Exception} thrown when an operation on {@link DirectedAcyclicGraph}
 * creates a cycle.
 */
public class CycleDetectedException extends Exception {
	/**
	 * Constructs this exception with a default detail message.
	 */
	public CycleDetectedException() {
		this("A cycle was detected");
	}

	/**
	 * Constructs this exception with a given detail message.
	 * 
	 * @param message  the detail message
	 */
	public CycleDetectedException(String message) {
		super(message);
	}
}
