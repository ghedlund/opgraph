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
/**
 *
 */
package ca.phon.opgraph.app.xml;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;

import javax.xml.*;
import javax.xml.namespace.*;

import org.w3c.dom.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.extensions.*;
import ca.phon.opgraph.io.xml.*;

/**
 */
public class NodeMetadataXMLSerializer implements XMLSerializer {
	static final String NAMESPACE = "https://www.phon.ca/ns/opgraph-app";
	static final String PREFIX = "oga";
	static final QName META_QNAME = new QName(NAMESPACE, "meta", PREFIX);

	static final Logger LOGGER = Logger.getLogger(NodeMetadataXMLSerializer.class.getName());

	@Override
	public void write(XMLSerializerFactory serializerFactory, Document doc, Element parentElem, Object obj)
		throws IOException
	{
		if(obj == null)
			throw new IOException("Null object given to serializer");

		if(!(obj instanceof NodeMetadata))
			throw new IOException(NodeMetadataXMLSerializer.class.getName() + " cannot write objects of type " + obj.getClass().getName());

		// setup namespace for document
		final Element rootEle = doc.getDocumentElement();
		rootEle.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
				XMLConstants.XMLNS_ATTRIBUTE + ":" + PREFIX, NAMESPACE);

		// Create metadata element
		final NodeMetadata meta = (NodeMetadata)obj;
		final Element metaElem = doc.createElementNS(NAMESPACE, PREFIX + ":" + META_QNAME.getLocalPart());
		metaElem.setAttribute("x", Integer.toString(meta.getX()));
		metaElem.setAttribute("y", Integer.toString(meta.getY()));

		// Create elements for default values
		for(Map.Entry<InputField, Object> entry : meta.getDefaults().entrySet()) {
			final InputField field = entry.getKey();
			final Object value = entry.getValue();

			final Element defaultElem = doc.createElementNS(NAMESPACE, PREFIX + ":default");
			defaultElem.setAttribute("for", field.getKey());
			defaultElem.setAttribute("type", value.getClass().getName());
			defaultElem.setTextContent(value.toString());

			metaElem.appendChild(defaultElem);
		}

		parentElem.appendChild(metaElem);
	}

	@Override
	public Object read(XMLSerializerFactory serializerFactory, OpGraph graph, Object parent, Document doc, Element elem)
		throws IOException
	{
		if(META_QNAME.equals(XMLSerializerFactory.getQName(elem))) {
			// Try to get the parent node
			if(parent == null || !(parent instanceof OpNode))
				throw new IOException("Node metadata requires parent node");

			final OpNode node = (OpNode)parent;

			// Read metadata
			final NodeMetadata meta = new NodeMetadata();
			meta.setX(Integer.parseInt(elem.getAttribute("x")));
			meta.setY(Integer.parseInt(elem.getAttribute("y")));

			// Read in defaults
			final NodeList children = elem.getChildNodes();
			for(int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
				final Node defaultNode = children.item(childIndex);
				if(defaultNode instanceof Element) {
					final Element defaultElem = (Element) defaultNode;
					final String fieldKey = defaultElem.getAttribute("for");
					final InputField field = node.getInputFieldWithKey(fieldKey);
					if(field == null) {
						LOGGER.log(Level.WARNING, "Default value references unknown input field: " + fieldKey);
						continue;
					}

					final String valueTypeClassName = defaultElem.getAttribute("type");
					final String valueString = defaultElem.getTextContent();
					try {
						boolean parsed = false;
						final Class<?> valueClass = Class.forName(valueTypeClassName);

						if(valueClass == String.class) {
							meta.setDefault(field, valueString);
							parsed = true;
						} else {
							final String parseMethodName = "parse" + valueClass.getSimpleName();
							final Method parseMethod = valueClass.getMethod(parseMethodName, String.class);

							if(parseMethod != null && Modifier.isStatic(parseMethod.getModifiers())) {
								meta.setDefault(field, parseMethod.invoke(null, valueString));
								parsed = true;
							}
						}

						if(!parsed) {
							LOGGER.log(Level.WARNING, "Couldn't parse default value " + valueString + " for key " + fieldKey);
						}
					} catch(ClassNotFoundException exc) {
						throw new Error("Unknown default value type: " + valueTypeClassName);
					} catch(IllegalArgumentException exc) {
						throw new Error("Couldn't parse default value", exc);
					} catch(IllegalAccessException exc) {
						throw new Error("Couldn't parse default value", exc);
					} catch(InvocationTargetException exc) {
						throw new Error("Couldn't parse default value", exc);
					} catch (SecurityException exc) {
						throw new Error("Couldn't parse default value", exc);
					} catch (NoSuchMethodException exc) {
						throw new Error("Couldn't parse default value", exc);
					}
				}
			}

			node.putExtension(NodeMetadata.class, meta);
		}

		return null;
	}

	@Override
	public boolean handles(Class<?> cls) {
		return (cls == NodeMetadata.class);
	}

	@Override
	public boolean handles(QName name) {
		return META_QNAME.equals(name);
	}
}
