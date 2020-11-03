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

import static ca.phon.opgraph.io.xml.XMLSerializerFactory.*;

import java.io.*;
import java.util.logging.*;

import javax.xml.*;
import javax.xml.namespace.*;

import org.w3c.dom.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.dag.*;
import ca.phon.opgraph.extensions.*;

/**
 * A default serializer for reading/writing {@link OpGraph} to/from XML.
 */
public class DefaultGraphXMLSerializer implements XMLSerializer {

	private final static Logger LOGGER = Logger.getLogger(DefaultGraphXMLSerializer.class.getName());

	// qualified names
	static final QName OPGRAPH_QNAME = new QName(DEFAULT_NAMESPACE, "opgraph", XMLConstants.DEFAULT_NS_PREFIX);
	static final QName GRAPH_QNAME = new QName(DEFAULT_NAMESPACE, "graph", XMLConstants.DEFAULT_NS_PREFIX);

	@Override
	public void write(XMLSerializerFactory serializerFactory, Document doc, Element parentElem, Object obj)
		throws IOException
	{
		if(obj == null)
			throw new IOException("Null object given to serializer");

		if(!(obj instanceof OpGraph))
			throw new IOException(DefaultGraphXMLSerializer.class.getName() + " cannot write objects of type " + obj.getClass().getName());

		// Create graph element
		final OpGraph graph = (OpGraph)obj;
		final Element graphElem = doc.createElementNS(GRAPH_QNAME.getNamespaceURI(), GRAPH_QNAME.getLocalPart());

		final SerializationWarnings warnings = new SerializationWarnings();
		graph.putExtension(SerializationWarnings.class, warnings);

		graphElem.setAttribute("id", graph.getId());

		// Nodes first
		for(OpNode node : graph.getVertices()) {
			final XMLSerializer serializer = serializerFactory.getHandler(node.getClass());
			if(serializer == null) {
				final SerializerNotFound warning = new SerializerNotFound(node.getClass());
				LOGGER.log(Level.WARNING, warning.getLocalizedMessage(), warning);
				warnings.add(warning);
				continue;
			}

			try {
				serializer.write(serializerFactory, doc, graphElem, node);
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, e.getLocalizedMessage(), e);
				warnings.add(e);
			}
		}

		// Link next
		for(OpLink link : graph.getEdges()) {
			final XMLSerializer serializer = serializerFactory.getHandler(link.getClass());
			if(serializer == null) {
				final SerializerNotFound warning = new SerializerNotFound(link.getClass());
				LOGGER.log(Level.WARNING, warning.getLocalizedMessage(), warning);
				warnings.add(warning);
				continue;
			}

			try {
				serializer.write(serializerFactory, doc, graphElem, link);
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, e.getLocalizedMessage(), e);
				warnings.add(e);
			}
		}

		// Extensions last
		if(graph.getExtensionClasses().size() > 0) {
			final XMLSerializer serializer = serializerFactory.getHandler(Extendable.class);
			if(serializer == null) {
				final SerializerNotFound warning = new SerializerNotFound(Extendable.class);
				LOGGER.log(Level.WARNING, warning.getLocalizedMessage(), warning);
				warnings.add(warning);
			}

			try {
				serializer.write(serializerFactory, doc, graphElem, graph);
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, e.getLocalizedMessage(), e);
				warnings.add(e);
			}
		}

		parentElem.appendChild(graphElem);
	}

	/*
	 * When reading a graph, it's possible to get many IOException errors
	 * which are not fatal when reading the graph document.  Instead of
	 * allowing these exceptions to continue, which causes document serialization
	 * to fail, collect these exceptions as 'warnings' and attach
	 * these warnings to the resulting graph.
	 */
	@Override
	public Object read(XMLSerializerFactory serializerFactory, OpGraph graph, Object parent, Document doc, Element elem)
		throws IOException
	{
		final SerializationWarnings warnings = new SerializationWarnings();

		if(GRAPH_QNAME.equals(XMLSerializerFactory.getQName(elem))) {
			graph = new OpGraph();


			// Read children
			final NodeList children = elem.getChildNodes();
			for(int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
				final Node node = children.item(childIndex);
				if(node instanceof Element) {
					// Get a handler for the element
					final Element childElem = (Element)node;
					final QName name = XMLSerializerFactory.getQName(childElem);
					final XMLSerializer serializer = serializerFactory.getHandler(name);
					if(serializer == null) {
						final IOException warning = new IOException("Could not get handler for element: " + name);
						warnings.add(warning);
					} else {
						// Determine what kind of element was read. If the element represented a
						// node/link, add it to the graph, otherwise it should be the <extensions>
						// element, and the extendable serializer handles adding the extensions
						//
						try {
							final Object objRead = serializer.read(serializerFactory, graph, graph, doc, childElem);
							if(objRead != null) {
								if(objRead instanceof OpNode) {
									graph.add((OpNode)objRead);
								} else if(objRead instanceof OpLink) {
									try {
										graph.add( (OpLink)objRead );
									} catch(VertexNotFoundException | CycleDetectedException | NullPointerException | InvalidEdgeException exc) {
										warnings.add(exc);
									}
								}
							}
						} catch (IOException e) {
							warnings.add(e);
						}
					}
				}
			}
		} else if(OPGRAPH_QNAME.equals(XMLSerializerFactory.getQName(elem))) {
			final NodeList children = elem.getChildNodes();
			for(int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
				final Node childNode = children.item(childIndex);
				if(childNode instanceof Element) {
					final Element childElem = (Element)childNode;

					if(GRAPH_QNAME.equals(XMLSerializerFactory.getQName(childElem))
					   && "root".equals(childElem.getAttribute("id")))
					{
						graph = (OpGraph)read(serializerFactory, graph, null, doc, childElem);
						break;
					}
				}
			}
		}

		if(warnings.size() > 0) {
			graph.putExtension(SerializationWarnings.class, warnings);
		}
		return graph;
	}

	@Override
	public boolean handles(Class<?> cls) {
		return (cls == OpGraph.class);
	}

	@Override
	public boolean handles(QName name) {
		return (GRAPH_QNAME.equals(name) || OPGRAPH_QNAME.equals(name));
	}
}
