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
/**
 * 
 */
package ca.phon.opgraph.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ca.phon.opgraph.OpGraph;

/**
 * An interface for any class providing OpGraph serialization services.
 */
public interface OpGraphSerializer {
	
	/**
	 * Validate a graph
	 * 
	 * @param file
	 * 
	 * @throws IOException if a validation error occurs
	 */
	public abstract void validate(File file) throws IOException;
	
	/**
	 * Validate a graph
	 * 
	 * @param stream
	 * 
	 * @throws IOException if a validation error occurs
	 */
	public abstract void validate(InputStream stream) throws IOException;
	
	/**
	 * Writes a graph to a file.
	 * 
	 * @param graph  the graph to write
	 * @param file  the stream to write to
	 * 
	 * @throws IOException  if any I/O errors occur
	 */
	public abstract void write(OpGraph graph, File file) throws IOException;
	
	/**
	 * Writes a graph to a stream.
	 * 
	 * @param graph  the graph to write
	 * @param stream  the stream to write to
	 * 
	 * @throws IOException  if any I/O errors occur
	 */
	public abstract void write(OpGraph graph, OutputStream stream) throws IOException;

	/**
	 * Reads a graph from a given file.
	 * 
	 * @param file  the stream to read from
	 * 
	 * @return the {@link OpGraph} that was read from the given stream
	 * 
	 * @throws IOException  if any I/O errors occur
	 */
	public abstract OpGraph read(File file) throws IOException;
	
	/**
	 * Reads a graph from a given stream.
	 * 
	 * @param stream  the stream to read from
	 * 
	 * @return the {@link OpGraph} that was read from the given stream
	 * 
	 * @throws IOException  if any I/O errors occur
	 */
	public abstract OpGraph read(InputStream stream) throws IOException;
}
