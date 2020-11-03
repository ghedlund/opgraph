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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.extensions.Extendable;

/**
 * A default serializer for reading/writing {@link Extendable}s to/from XML.
 */
public class DefaultExtendableXMLSerializer implements XMLSerializer {
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(DefaultExtendableXMLSerializer.class.getName());

	// qualified names
	static final QName EXTENDABLE_QNAME = new QName(DEFAULT_NAMESPACE, "extensions", XMLConstants.DEFAULT_NS_PREFIX);

	@Override
	public void write(XMLSerializerFactory serializerFactory, Document doc, Element parentElem, Object obj) 
		throws IOException 
	{
		if(obj == null)
			throw new IOException("Null object given to serializer");

		if(!(obj instanceof Extendable))
			throw new IOException(DefaultExtendableXMLSerializer.class.getName() + " cannot write objects of type " + obj.getClass().getName());

		// Create extensions element
		final Extendable ext = (Extendable)obj;
		final Element extensionsElem = doc.createElementNS(EXTENDABLE_QNAME.getNamespaceURI(), EXTENDABLE_QNAME.getLocalPart());
		for(Class<?> extension : ext.getExtensionClasses()) {
			final XMLSerializer serializer = serializerFactory.getHandler(extension);
			if(serializer == null) {
				LOGGER.log(Level.INFO, "Extendable contains a serializable extension with no handler: " + extension.getName());
			} else {
				serializer.write(serializerFactory, doc, extensionsElem, ext.getExtension(extension));
			}
		}

		// Only add extensions element if necessary
		if(extensionsElem.getChildNodes().getLength() > 0)
			parentElem.appendChild(extensionsElem);
	}

	@Override
	public Object read(XMLSerializerFactory serializerFactory, OpGraph graph, Object parent, Document doc, Element elem)
		throws IOException 
	{
		if(EXTENDABLE_QNAME.equals(XMLSerializerFactory.getQName(elem))) {
			if(!(parent instanceof Extendable))
				throw new IOException("Reading extensions from a parent that is not Extendable");

			final Extendable ext = (Extendable)parent;
			final NodeList children = elem.getChildNodes();
			for(int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
				final Node childNode = children.item(childIndex);
				if(childNode instanceof Element) {
					// Find serializer for extension
					final Element childElem = (Element)childNode;
					final QName qname = XMLSerializerFactory.getQName(childElem);
					final XMLSerializer serializer = serializerFactory.getHandler(qname);

					// If no serializer, we'll just issue a warning
					if(serializer == null) {
						// TODO perhaps allow errors to be added to the serializer factory
						//      so that the outside world knows an extension couldn't be
						//      read. Another option is to have an extension that holds
						//      all of the extensions that couldn't be read so that if we
						//      write this Extendable back to XML, those extensions won't
						//      be lost.
						//
						LOGGER.info("Extension element has no handler: " + qname);
					} else {
						serializer.read(serializerFactory, graph, ext, doc, childElem);
					}
				}
			}
		}

		return null;
	}

	@Override
	public boolean handles(Class<?> cls) {
		return (cls == Extendable.class);
	}

	@Override
	public boolean handles(QName name) {
		return EXTENDABLE_QNAME.equals(name);
	}
}
