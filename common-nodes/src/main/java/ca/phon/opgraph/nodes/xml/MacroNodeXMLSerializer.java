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
package ca.phon.opgraph.nodes.xml;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.extensions.Extendable;
import ca.phon.opgraph.io.xml.XMLSerializer;
import ca.phon.opgraph.io.xml.XMLSerializerFactory;
import ca.phon.opgraph.nodes.general.MacroNode;
import ca.phon.opgraph.nodes.iteration.ForEachNode;

/**
 * A default serializer for reading/writing {@link OpNode} to/from XML.
 */
public class MacroNodeXMLSerializer implements XMLSerializer {
	static final String NAMESPACE = "https://www.phon.ca/ns/opgraph-common-nodes";
	static final String PREFIX = "ogcn";

	// qualified names
	static final QName MACRO_QNAME = new QName(NAMESPACE, "macro", PREFIX);

	@Override
	public void write(XMLSerializerFactory serializerFactory, Document doc, Element parentElem, Object obj)
		throws IOException
	{
		if(obj == null)
			throw new IOException("Null object given to serializer");

		if(!(obj instanceof MacroNode))
			throw new IOException(MacroNodeXMLSerializer.class.getName() + " cannot write objects of type " + obj.getClass().getName());

		// setup namespace for document
		final Element rootEle = doc.getDocumentElement();
		rootEle.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
				XMLConstants.XMLNS_ATTRIBUTE + ":" + PREFIX, NAMESPACE);

		// Create node element
		final MacroNode macro = (MacroNode)obj;
		final Element macroElem = doc.createElementNS(NAMESPACE, PREFIX + ":" + MACRO_QNAME.getLocalPart());

		macroElem.setAttribute("id", macro.getId());
		macroElem.setAttribute("type", obj.getClass().getName());

		if(!macro.getName().equals(macro.getDefaultName()))
			macroElem.setAttribute("name", macro.getName());

		if(!macro.getDescription().equals(macro.getDefaultDescription())) {
			final Element descriptionElem = doc.createElementNS(NAMESPACE, PREFIX + ":description");
			descriptionElem.setTextContent(macro.getDescription());
			macroElem.appendChild(descriptionElem);
		}

		// Macro graph
		final XMLSerializer graphSerializer = serializerFactory.getHandler(OpGraph.class);
		if(graphSerializer == null)
			throw new IOException("No handler for graph");

		graphSerializer.write(serializerFactory, doc, macroElem, macro.getGraph());

		// Input fields
		for(InputField field : macro.getInputFields()) {
			final XMLSerializer serializer = serializerFactory.getHandler(field.getClass());
			if(serializer == null)
				throw new IOException("Cannot get handler for input field: " + field.getClass().getName());

			serializer.write(serializerFactory, doc, macroElem, field);
		}

		// Output fields
		for(OutputField field : macro.getOutputFields()) {
			final XMLSerializer serializer = serializerFactory.getHandler(field.getClass());
			if(serializer == null)
				throw new IOException("Cannot get handler for output field: " + field.getClass().getName());

			serializer.write(serializerFactory, doc, macroElem, field);
		}

		// Extensions last
		if(macro.getExtensionClasses().size() > 0) {
			final XMLSerializer serializer = serializerFactory.getHandler(Extendable.class);
			if(serializer == null)
				throw new IOException("No XML serializer for extensions");

			serializer.write(serializerFactory, doc, macroElem, macro);
		}

		//
		parentElem.appendChild(macroElem);
	}

	@Override
	public Object read(XMLSerializerFactory serializerFactory, OpGraph graph, Object parent, Document doc, Element elem)
		throws IOException
	{
		MacroNode macro = null;
		if(MACRO_QNAME.equals(XMLSerializerFactory.getQName(elem))) {
			// Read graph
			final XMLSerializer graphSerializer = serializerFactory.getHandler(OpGraph.class);
			if(graphSerializer == null)
				throw new IOException("No handler for graph");

			// Get the type of macro node
			Class<? extends MacroNode> cls = MacroNode.class;
			if(elem.hasAttribute("type")) {
				try {
					final Class<?> type = Class.forName(elem.getAttribute("type"));
					cls = type.asSubclass(MacroNode.class);
				} catch(ClassCastException exc) {
					throw new IOException("Macro node type not castable to MacroNode");
				} catch(ClassNotFoundException exc) {
					throw new IOException("Macro node type unknown");
				}
			}

			// Get the OpGraph constructor
			Constructor<? extends MacroNode> constructor = null;
			try {
				constructor = cls.getConstructor(OpGraph.class);
			} catch(SecurityException exc) {
				throw new IOException("Cannot construct macro node with given type: " + cls.getName());
			} catch(NoSuchMethodException exc) {
				throw new IOException("Cannot construct macro node with given type: " + cls.getName());
			}

			// Read children
			final NodeList children = elem.getChildNodes();
			for(int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
				final Node node = children.item(childIndex);
				if(node instanceof Element) {
					final Element childElem = (Element)node;
					final QName name = XMLSerializerFactory.getQName(childElem);
					if(graphSerializer.handles(name)) {
						final Object objRead = graphSerializer.read(serializerFactory, graph, macro, doc, childElem);
						if(objRead == null || !(objRead instanceof OpGraph))
							throw new IOException("Could not read graph for macro");

						try {
							macro = constructor.newInstance(objRead);
						} catch(IllegalArgumentException exc) {
							throw new IOException("Could not instantiate macro node");
						} catch(InstantiationException exc) {
							throw new IOException("Could not instantiate macro node");
						} catch(IllegalAccessException exc) {
							throw new IOException("Could not instantiate macro node");
						} catch(InvocationTargetException exc) {
							throw new IOException("Could not instantiate macro node");
						}
					} else {
						if(macro == null)
							throw new IOException("Reading other macro data before macro graph read");

						// Get a handler for the element
						final XMLSerializer serializer = serializerFactory.getHandler(name);
						if(serializer == null)
							throw new IOException("Could not get handler for element: " + name);

						// Published fields and extensions all take care of adding
						// themselves to the passed in object
						//
						serializer.read(serializerFactory, graph, macro, doc, childElem);
					}
				}
			}

			// Set attributes
			if(macro != null) {
				if(elem.hasAttribute("id"))
					macro.setId(elem.getAttribute("id"));

				if(elem.hasAttribute("name"))
					macro.setName(elem.getAttribute("name"));
			}
		}

		return macro;
	}

	@Override
	public boolean handles(Class<?> cls) {
		return (cls == MacroNode.class || cls == ForEachNode.class);
	}

	@Override
	public boolean handles(QName name) {
		return MACRO_QNAME.equals(name);
	}
}
