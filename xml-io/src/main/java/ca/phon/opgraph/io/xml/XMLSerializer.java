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
package ca.phon.opgraph.io.xml;

import java.io.*;

import javax.xml.namespace.*;

import org.w3c.dom.*;

import ca.phon.opgraph.*;

/**
 * An extension for classes that require custom XML serialization.
 */
public interface XMLSerializer {	
	/**
	 * Writes an object to a parent element.
	 * 
	 * @param serializerFactory  a factory to fetch XML serializers
	 * @param doc  the DOM document
	 * @param parentElem  the parent DOM element
	 * @param obj  the object to write
	 * 
	 * @throws IOException  if the given object cannot be written by this handler
	 */
	public abstract void write(XMLSerializerFactory serializerFactory,
	                           Document doc,
	                           Element parentElem,
	                           Object obj) throws IOException;

	/**
	 * Reads an object from an XML event stream.
	 * 
	 * @param serializerFactory  a factory to fetch XML serializers
	 * @param graph  the graph currently being read
	 * @param parent  the parent object from which reading occured
	 * @param doc  the DOM document
	 * @param elem  the parent DOM element
	 * 
	 * @return the object described in the given element
	 * 
	 * @throws IOException  if the given stream does not contain XML data
	 *                      which this handler understands
	 */
	public abstract Object read(XMLSerializerFactory serializerFactory,
	                            OpGraph graph,
	                            Object parent,
	                            Document doc,
	                            Element elem) throws IOException;

	/**
	 * Gets whether or not this serializer writes the given class.
	 * 
	 * @param cls  the class to check
	 * 
	 * @return <code>true</code> if this serializer can write the given class,
	 *         <code>false</code> otherwise
	 */
	public abstract boolean handles(Class<?> cls);

	/**
	 * Gets whether or not this serializer can read a given qualified name.
	 * 
	 * @param name  the qualified name to check
	 * 
	 * @return <code>true</code> if this serializer can read the given
	 *         qualified name, <code>false</code> otherwise
	 */
	public abstract boolean handles(QName name);
}
