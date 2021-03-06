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
package ca.phon.opgraph.app;

import java.io.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.io.*;

/**
 * Utility class providing easy access to open/save methods for opgraph
 * files.
 * 
 */
public class OpgraphIO {

	public static OpGraph read(File file) throws IOException {
		final OpGraphSerializer serializer = OpGraphSerializerFactory.getDefaultSerializer();
		return serializer.read(file);
	}
	
	public static OpGraph read(InputStream in) throws IOException {
		final OpGraphSerializer serializer = OpGraphSerializerFactory.getDefaultSerializer();
		return serializer.read(in);
	}
	
	public static void write(OpGraph graph, File file) throws IOException {
		final OpGraphSerializer serializer = OpGraphSerializerFactory.getDefaultSerializer();
		serializer.write(graph, file);
	}
	
	public static void write(OpGraph graph, OutputStream stream) throws IOException {
		final OpGraphSerializer serializer = OpGraphSerializerFactory.getDefaultSerializer();
		serializer.write(graph, stream);
	}
	
	/**
	 * Round-trip
	 * 
	 * Write the given {@link OpGraph} to a temporary output stream and then 
	 * parse that stream as a new {@link OpGraph} instance.  This method
	 * can be used to clone graph objects.
	 * 
	 * @param graph
	 * 
	 * @return a cloned instance of the given graph
	 * 
	 * @throws IOException
	 */
	public static OpGraph roundtrip(OpGraph graph) throws IOException {
		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		write(graph, bout);
		
		final ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		return read(bin);
	}
	
}
