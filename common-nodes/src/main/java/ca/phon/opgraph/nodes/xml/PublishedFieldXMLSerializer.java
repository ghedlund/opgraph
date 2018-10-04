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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ca.phon.opgraph.*;
import ca.phon.opgraph.extensions.Extendable;
import ca.phon.opgraph.extensions.Publishable.*;
import ca.phon.opgraph.io.xml.*;
import ca.phon.opgraph.nodes.general.MacroNode;

/**
 * A default serializer for reading/writing published {@link InputField}s and
 * {@link OutputField}s to/from XML.
 */
public class PublishedFieldXMLSerializer implements XMLSerializer {
	static final String NAMESPACE = "https://www.phon.ca/ns/opgraph-common-nodes";
	static final String PREFIX = "ogcn";

	// qualified names
	static final QName INPUT_QNAME = new QName(NAMESPACE, "published_input", PREFIX);
	static final QName OUTPUT_QNAME = new QName(NAMESPACE, "published_output", PREFIX);

	@Override
	public void write(XMLSerializerFactory serializerFactory, Document doc, Element parentElem, Object obj)
		throws IOException
	{
		if(obj == null)
			throw new IOException("Null object given to serializer");

		// setup namespace for document
		final Element rootEle = doc.getDocumentElement();
		rootEle.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
				XMLConstants.XMLNS_ATTRIBUTE + ":" + PREFIX, NAMESPACE);

		if(obj instanceof PublishedInput) {
			final PublishedInput field = (PublishedInput)obj;

			// Only write if field is non-fixed, or fixed but with extensions
			final Element fieldElem = doc.createElementNS(NAMESPACE, PREFIX + ":" + INPUT_QNAME.getLocalPart());
			fieldElem.setAttribute("name", field.getKey());
			fieldElem.setAttribute("ref", field.destinationNode.getId());
			fieldElem.setAttribute("field", field.nodeInputField.getKey());

			// Extensions
			if(field.getExtensionClasses().size() > 0) {
				final XMLSerializer serializer = serializerFactory.getHandler(Extendable.class);
				if(serializer == null)
					throw new IOException("No XML serializer for extensions");

				serializer.write(serializerFactory, doc, fieldElem, field);
			}

			parentElem.appendChild(fieldElem);
		} else if(obj instanceof PublishedOutput) {
			final PublishedOutput field = (PublishedOutput)obj;

			// Only write if field is non-fixed, or fixed but with extensions
			final Element fieldElem = doc.createElementNS(NAMESPACE, PREFIX + ":" + OUTPUT_QNAME.getLocalPart());
			fieldElem.setAttribute("name", field.getKey());
			fieldElem.setAttribute("ref", field.sourceNode.getId());
			fieldElem.setAttribute("field", field.nodeOutputField.getKey());

			// Extensions
			if(field.getExtensionClasses().size() > 0) {
				final XMLSerializer serializer = serializerFactory.getHandler(Extendable.class);
				if(serializer == null)
					throw new IOException("No XML serializer for extensions");

				serializer.write(serializerFactory, doc, fieldElem, field);
			}

			parentElem.appendChild(fieldElem);
		} else {
			throw new IOException(PublishedFieldXMLSerializer.class.getName() + " cannot write objects of type " + obj.getClass().getName());
		}
	}

	@Override
	public Object read(XMLSerializerFactory serializerFactory, OpGraph graph, Object parent, Document doc, Element elem)
		throws IOException
	{
		if(INPUT_QNAME.equals(XMLSerializerFactory.getQName(elem))) {
			if(!(parent instanceof MacroNode))
				throw new IOException("Trying to read published field, but parent object is not a macro");

			// Find published info
			final String key = elem.getAttribute("name");
			final String destNodeId = elem.getAttribute("ref");
			final String destFieldKey = elem.getAttribute("field");

			final MacroNode macro = (MacroNode)parent;
			final OpNode destNode = macro.getGraph().getNodeById(destNodeId, true);
			final InputField destField = destNode.getInputFieldWithKey(destFieldKey);

			if(destField == null) {
				throw new IOException("Cannot publish field " + destFieldKey + ", unable to located in destination node.");
			}
			
			// Create
			final InputField published = macro.publish(key, destNode, destField);

			// Read children
			final NodeList children = elem.getChildNodes();
			for(int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
				final Node node = children.item(childIndex);
				if(node instanceof Element) {
					final Element childElem = (Element)node;
					final QName name = XMLSerializerFactory.getQName(childElem);

					// Get a handler for the element
					final XMLSerializer serializer = serializerFactory.getHandler(name);
					if(serializer == null)
						throw new IOException("Could not get handler for element: " + name);

					serializer.read(serializerFactory, graph, published, doc, childElem);
				}
			}
		} else if(OUTPUT_QNAME.equals(XMLSerializerFactory.getQName(elem))) {
			if(!(parent instanceof MacroNode))
				throw new IOException("Trying to read published field, but parent object is not a macro");

			// Find published info
			final String key = elem.getAttribute("name");
			final String sourceNodeId = elem.getAttribute("ref");
			final String sourceFieldKey = elem.getAttribute("field");

			final MacroNode macro = (MacroNode)parent;
			final OpNode sourceNode = macro.getGraph().getNodeById(sourceNodeId, true);
			final OutputField sourceField = sourceNode.getOutputFieldWithKey(sourceFieldKey);

			if(sourceNode != null && sourceField != null) {
				// Create
				final OutputField published = macro.publish(key, sourceNode, sourceField);
				
				// Read children
				final NodeList children = elem.getChildNodes();
				for(int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
					final Node node = children.item(childIndex);
					if(node instanceof Element) {
						final Element childElem = (Element)node;
						final QName name = XMLSerializerFactory.getQName(childElem);
						
						// Get a handler for the element
						final XMLSerializer serializer = serializerFactory.getHandler(name);
						if(serializer == null)
							throw new IOException("Could not get handler for element: " + name);
						
						serializer.read(serializerFactory, graph, published, doc, childElem);
					}
				}
			}
		}

		return null;
	}

	@Override
	public boolean handles(Class<?> cls) {
		return ((cls == PublishedInput.class) || (cls == PublishedOutput.class));
	}

	@Override
	public boolean handles(QName name) {
		return (INPUT_QNAME.equals(name) || OUTPUT_QNAME.equals(name));
	}
}
