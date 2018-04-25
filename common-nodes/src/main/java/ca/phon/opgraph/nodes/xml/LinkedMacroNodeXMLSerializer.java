/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * This file is part of the OpGraph project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.phon.opgraph.nodes.xml;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.extensions.Extendable;
import ca.phon.opgraph.io.xml.XMLSerializer;
import ca.phon.opgraph.io.xml.XMLSerializerFactory;
import ca.phon.opgraph.nodes.general.LinkedMacroNode;
import ca.phon.opgraph.nodes.general.MacroNode;

public class LinkedMacroNodeXMLSerializer implements XMLSerializer {
	static final String NAMESPACE = "https://www.phon.ca/ns/opgraph-common-nodes";
	static final String PREFIX = "ogcn";

	// qualified names
	static final QName MACRO_QNAME = new QName(NAMESPACE, "linked_macro", PREFIX);
	
	@Override
	public void write(XMLSerializerFactory serializerFactory, Document doc, Element parentElem, Object obj)
			throws IOException {
		if(obj == null)
			throw new IOException("Null object given to serializer");

		if(!(obj instanceof LinkedMacroNode))
			throw new IOException(LinkedMacroNodeXMLSerializer.class.getName() + " cannot write objects of type " + obj.getClass().getName());

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

		// Macro graph uri
		final Element uriElem = doc.createElementNS(NAMESPACE, PREFIX + ":uri");
		uriElem.setTextContent(((LinkedMacroNode)obj).getUri().toASCIIString());
		macroElem.appendChild(uriElem);

		// Extensions last
		if(macro.getExtensionClasses().size() > 0) {
			final XMLSerializer serializer = serializerFactory.getHandler(Extendable.class);
			if(serializer == null)
				throw new IOException("No XML serializer for extensions");

			serializer.write(serializerFactory, doc, macroElem, macro);
		}

		parentElem.appendChild(macroElem);
	}

	@Override
	public Object read(XMLSerializerFactory serializerFactory, OpGraph graph, Object parent, Document doc, Element elem)
			throws IOException {
		LinkedMacroNode macro = null;
		if(MACRO_QNAME.equals(XMLSerializerFactory.getQName(elem))) {
			// Read children
			final NodeList children = elem.getChildNodes();
			for(int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
				final Node node = children.item(childIndex);
				if(node instanceof Element) {
					final Element childElem = (Element)node;
					final QName name = XMLSerializerFactory.getQName(childElem);
					if(name.getLocalPart().equals("uri")) {
						final String uriTxt = childElem.getTextContent();
						final URI uri = URI.create(uriTxt);
						macro = new LinkedMacroNode(uri);
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
		return (cls == LinkedMacroNode.class);
	}

	@Override
	public boolean handles(QName name) {
		return MACRO_QNAME.equals(name);
	}

}
