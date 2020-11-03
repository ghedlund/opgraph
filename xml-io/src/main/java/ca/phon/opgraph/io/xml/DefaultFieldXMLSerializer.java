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

import javax.xml.*;
import javax.xml.namespace.*;

import org.w3c.dom.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.extensions.*;

/**
 * A default serializer for reading/writing {@link InputField}s and
 * {@link OutputField}s to/from XML.
 */
public class DefaultFieldXMLSerializer implements XMLSerializer {
	// qualified names
	static final QName INPUT_QNAME = new QName(DEFAULT_NAMESPACE, "input", XMLConstants.DEFAULT_NS_PREFIX);
	static final QName OUTPUT_QNAME = new QName(DEFAULT_NAMESPACE, "output", XMLConstants.DEFAULT_NS_PREFIX);

	@Override
	public void write(XMLSerializerFactory serializerFactory, Document doc, Element parentElem, Object obj) 
		throws IOException 
	{
		if(obj == null)
			throw new IOException("Null object given to serializer");

		if(obj instanceof InputField) {
			final InputField field = (InputField)obj;

			// Only write if field is non-fixed, or fixed but with extensions
			if(!field.isFixed() || field.getExtensionClasses().size() > 0) {
				final Element fieldElem = doc.createElementNS(INPUT_QNAME.getNamespaceURI(), INPUT_QNAME.getLocalPart());
				fieldElem.setAttribute("name", field.getKey());
				fieldElem.setAttribute("optional", Boolean.toString(field.isOptional()));
				fieldElem.setTextContent(field.getDescription());
				
				if(field.isFixed()) {
					fieldElem.setAttribute("fixed", Boolean.toString(Boolean.TRUE));
				}

				// XXX Store type validators?

				// Extensions
				if(field.getExtensionClasses().size() > 0) {
					final XMLSerializer serializer = serializerFactory.getHandler(Extendable.class);
					if(serializer == null)
						throw new IOException("No XML serializer for extensions");

					serializer.write(serializerFactory, doc, fieldElem, field);
				}

				parentElem.appendChild(fieldElem);
			}
		} else if(obj instanceof OutputField) {
			final OutputField field = (OutputField)obj;

			// Only write if field is non-fixed, or fixed but with extensions
			if(!field.isFixed() || field.getExtensionClasses().size() > 0) {
				final Element fieldElem = doc.createElementNS(OUTPUT_QNAME.getNamespaceURI(), OUTPUT_QNAME.getLocalPart());
				fieldElem.setAttribute("name", field.getKey());
				fieldElem.setAttribute("type", field.getOutputType().getName());
				fieldElem.setTextContent(field.getDescription());
				
				if(field.isFixed()) {
					fieldElem.setAttribute("fixed", Boolean.toString(Boolean.TRUE));
				}

				// Extensions
				if(field.getExtensionClasses().size() > 0) {
					final XMLSerializer serializer = serializerFactory.getHandler(Extendable.class);
					if(serializer == null)
						throw new IOException("No XML serializer for extensions");

					serializer.write(serializerFactory, doc, fieldElem, field);
				}

				parentElem.appendChild(fieldElem);
			}
		} else {
			throw new IOException(DefaultFieldXMLSerializer.class.getName() + " cannot write objects of type " + obj.getClass().getName());
		}
	}

	@Override
	public Object read(XMLSerializerFactory serializerFactory, OpGraph graph, Object parent, Document doc, Element elem)
		throws IOException 
	{
		ContextualItem item = null;
		if(INPUT_QNAME.equals(XMLSerializerFactory.getQName(elem))) {
			Class<?> inputType = Object.class;
			if(elem.hasAttribute("type")) {
				final String outputTypeClassName = elem.getAttribute("type");
				try {
					inputType = Class.forName(outputTypeClassName);
				} catch(ClassNotFoundException exc) {
					throw new IOException("Unknown output type for field: " + outputTypeClassName);
				}
			}
			
			boolean fixed = (elem.hasAttribute("fixed")
					? Boolean.parseBoolean(elem.getAttributeNS(DEFAULT_NAMESPACE, "optional"))
					: false);
				
			boolean optional = (elem.hasAttribute("optional") 
					? Boolean.parseBoolean(elem.getAttributeNS(DEFAULT_NAMESPACE, "optional"))
					: false);
			// Create
			final String key = elem.getAttribute( "name");
			final String description = elem.getTextContent();
			final InputField field = new InputField(key, description, fixed, optional, inputType);
		
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
				}
			}

			item = field;
		} else if(OUTPUT_QNAME.equals(XMLSerializerFactory.getQName(elem))) {
			Class<?> outputType = Object.class;
			if(elem.hasAttribute("type")) {
				final String outputTypeClassName = elem.getAttribute("type");
				try {
					outputType = Class.forName(outputTypeClassName);
				} catch(ClassNotFoundException exc) {
					throw new IOException("Unknown output type for field: " + outputTypeClassName);
				}
			}
			
			boolean fixed = (elem.hasAttribute("fixed")
					? Boolean.parseBoolean(elem.getAttributeNS(DEFAULT_NAMESPACE, "optional"))
					: false);
				
			// Create
			final String key = elem.getAttribute( "name");
			final String description = elem.getTextContent();
			final OutputField field = new OutputField(key, description, fixed, outputType);
	
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
				}
			}

			item = field;
		}

		return item;
	}

	@Override
	public boolean handles(Class<?> cls) {
		return ((cls == InputField.class) || (cls == OutputField.class));
	}

	@Override
	public boolean handles(QName name) {
		return (INPUT_QNAME.equals(name) || OUTPUT_QNAME.equals(name));
	}
}
