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
package ca.phon.opgraph.nodes.xml;

import java.io.*;

import javax.xml.namespace.*;

import org.w3c.dom.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.dag.*;
import ca.phon.opgraph.exceptions.*;
import ca.phon.opgraph.io.xml.*;
import ca.phon.opgraph.nodes.general.*;

public class LinkedMacroNodeOverridesXMLSerializer implements XMLSerializer {
	
	static final String NAMESPACE = "https://www.phon.ca/ns/opgraph-common-nodes";
	static final String PREFIX = "ogcn";

	// qualified names
	static final QName OVERRIDES_QNAME = new QName(NAMESPACE, "overrides", PREFIX);

	@Override
	public void write(XMLSerializerFactory serializerFactory, Document doc, Element parentElem, Object obj)
			throws IOException {
		if(obj == null)
			throw new IOException(new NullPointerException("obj"));
		
		Element overridesElem = doc.createElementNS(OVERRIDES_QNAME.getNamespaceURI(), 
				OVERRIDES_QNAME.getPrefix() + ":" + OVERRIDES_QNAME.getLocalPart());
		parentElem.appendChild(overridesElem);
		
		LinkedMacroNodeOverrides nodeOverrides = (LinkedMacroNodeOverrides)obj;
		for(OpNode node:nodeOverrides.getNodeOverrides()) {
			final XMLSerializer serializer = serializerFactory.getHandler(node.getClass());
			if(serializer == null) {
				throw new IOException("No serializer found for node: " + node.getClass().getName());
			}
			
			serializer.write(serializerFactory, doc, overridesElem, node);
		}
	}

	@Override
	public Object read(XMLSerializerFactory serializerFactory, OpGraph graph, Object parent, Document doc, Element elem)
			throws IOException {
		if(!OVERRIDES_QNAME.equals(XMLSerializerFactory.getQName(elem))) {
			throw new IOException("Wrong element");
		}
		
		final LinkedMacroNodeOverrides overrides = new LinkedMacroNodeOverrides();
		
		MacroNode macro = null;
		if(parent instanceof MacroNode) {
			macro = (MacroNode)parent;
			macro.putExtension(LinkedMacroNodeOverrides.class, overrides);
		}
		
		final NodeList children = elem.getChildNodes();
		for(int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
			final Node node = children.item(childIndex);
			if(node instanceof Element) {
				final Element childElem = (Element)node;
				final QName name = XMLSerializerFactory.getQName(childElem);
				final XMLSerializer serializer = serializerFactory.getHandler(name);
				if(serializer == null) {
					throw new IOException("No serializer found for name: " + name);
				}
				OpGraph tempGraph = new OpGraph();
				OpNode overrideNode = (OpNode)serializer.read(serializerFactory, tempGraph, tempGraph, doc, childElem);
			
				overrides.getNodeOverrides().add(overrideNode);
				
				if(macro != null) {
					OpGraph macroGraph = macro.getGraph();
					try {
						macroGraph.swap(overrideNode);
					} catch (VertexNotFoundException | CycleDetectedException | ItemMissingException | InvalidEdgeException e) {
						throw new IOException(e);
					}
				}
			}
		}
		
		return overrides;
	}

	@Override
	public boolean handles(Class<?> cls) {
		return LinkedMacroNodeOverrides.class.equals(cls);
	}

	@Override
	public boolean handles(QName name) {
		return OVERRIDES_QNAME.equals(name);
	}

}
