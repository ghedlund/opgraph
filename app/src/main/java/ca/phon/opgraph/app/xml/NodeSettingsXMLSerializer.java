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
import java.util.*;

import javax.xml.*;
import javax.xml.namespace.*;

import org.w3c.dom.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.extensions.*;
import ca.phon.opgraph.io.xml.*;

/**
 */
public class NodeSettingsXMLSerializer implements XMLSerializer {
	static final String NAMESPACE = "https://www.phon.ca/ns/opgraph-app";
	static final String PREFIX = "oga";
	static final QName SETTINGS_QNAME = new QName(NAMESPACE, "settings", PREFIX);

	@Override
	public void write(XMLSerializerFactory serializerFactory, Document doc, Element parentElem, Object obj)
		throws IOException
	{
		if(obj == null)
			throw new IOException("Null object given to serializer");

		if(!(obj instanceof NodeSettings))
			throw new IOException(NodeSettingsXMLSerializer.class.getName() + " cannot write objects of type " + obj.getClass().getName());

		// setup namespace for document
		final Element rootEle = doc.getDocumentElement();
		rootEle.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
				XMLConstants.XMLNS_ATTRIBUTE + ":" + PREFIX, NAMESPACE);

		// Create metadata element
		final Properties props = ((NodeSettings)obj).getSettings();
		final Element settingsElem = doc.createElementNS(NAMESPACE, PREFIX + ":" + SETTINGS_QNAME.getLocalPart());

		// Create elements for default values
		for(Map.Entry<Object, Object> entry : props.entrySet()) {
			if(entry.getKey() != null) {
				final Element propertyElem = doc.createElementNS(NAMESPACE, PREFIX + ":property");
				propertyElem.setAttribute("key", entry.getKey().toString());
				if(entry.getValue() != null)
					propertyElem.appendChild( doc.createCDATASection(entry.getValue().toString()) );

				settingsElem.appendChild(propertyElem);
			}
		}

		parentElem.appendChild(settingsElem);
	}

	@Override
	public Object read(XMLSerializerFactory serializerFactory, OpGraph graph, Object parent, Document doc, Element elem)
		throws IOException
	{
		if(SETTINGS_QNAME.equals(XMLSerializerFactory.getQName(elem))) {
			// Try to get the parent node
			if(parent == null || !(parent instanceof OpNode))
				throw new IOException("Node metadata requires parent node");

			final OpNode node = (OpNode)parent;
			final NodeSettings settings = node.getExtension(NodeSettings.class);
			if(settings == null)
				throw new IOException("Parent node does not have settings extension, but is specified in XML");

			// Read in properties
			final Properties properties = new Properties();
			final NodeList children = elem.getChildNodes();
			for(int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
				final Node defaultNode = children.item(childIndex);
				if(defaultNode instanceof Element) {
					final Element propertyElem = (Element)defaultNode;
					final String key = propertyElem.getAttribute("key");

					if(propertyElem.hasChildNodes()) {
						StringBuffer buffer = new StringBuffer();
						NodeList nodeList = propertyElem.getChildNodes();
						for(int i = 0; i < nodeList.getLength(); i++) {
							Node n = nodeList.item(i);
							if(n instanceof CDATASection) {
								buffer.append(n.getTextContent());
							}
						}
						// if no CDATA section, use text content of element
						if(buffer.length() == 0)
							buffer.append(propertyElem.getTextContent());

						properties.setProperty(key, buffer.toString());
					}
				}
			}

			settings.loadSettings(properties);
		}

		return null;
	}

	@Override
	public boolean handles(Class<?> cls) {
		return (cls == NodeSettings.class);
	}

	@Override
	public boolean handles(QName name) {
		return SETTINGS_QNAME.equals(name);
	}
}
