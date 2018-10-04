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
package ca.phon.opgraph.io.xml;

import static ca.phon.opgraph.io.xml.XMLSerializerFactory.*;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.OpLink;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.exceptions.ItemMissingException;

/**
 * A default serializer for reading/writing {@link OpLink} to/from XML.
 */
public class DefaultLinkXMLSerializer implements XMLSerializer {
	// qualified names
	static final QName LINK_QNAME = new QName(DEFAULT_NAMESPACE, "link", XMLConstants.DEFAULT_NS_PREFIX);

	@Override
	public void write(XMLSerializerFactory serializerFactory, Document doc, Element parentElem, Object obj) 
		throws IOException 
	{
		if(obj == null)
			throw new IOException("Null object given to serializer");

		if(!(obj instanceof OpLink))
			throw new IOException(DefaultLinkXMLSerializer.class.getName() + " cannot write objects of type " + obj.getClass().getName());

		// Create link element
		final OpLink link = (OpLink)obj;
		final Element linkElem = doc.createElementNS(LINK_QNAME.getNamespaceURI(), LINK_QNAME.getLocalPart());

		linkElem.setAttribute("source", link.getSource().getId());
		linkElem.setAttribute("dest", link.getDestination().getId());
		linkElem.setAttribute("sourceField", link.getSourceField().getKey());
		linkElem.setAttribute("destField", link.getDestinationField().getKey());

		parentElem.appendChild(linkElem);
	}

	@Override
	public Object read(XMLSerializerFactory serializerFactory, OpGraph graph, Object parent, Document doc, Element elem)
		throws IOException 
	{
		OpLink link = null;
		if(LINK_QNAME.equals(XMLSerializerFactory.getQName(elem))) {
			final String sid = elem.getAttribute("source");
			final String did = elem.getAttribute("dest");
			final String sfkey = elem.getAttribute("sourceField");
			final String dfkey = elem.getAttribute("destField");

			final OpNode source = graph.getNodeById(sid, false);
			if(source == null)
				throw new IOException("Unknown source node in link: " + sid);

			final OpNode dest = graph.getNodeById(did, false);
			if(dest == null)
				throw new IOException("Unknown source node in link: " + did);

			final OutputField sourceField = source.getOutputFieldWithKey(sfkey);
			if(sourceField == null)
				throw new IOException("Unknown source field in link: " + sfkey);

			final InputField destField = dest.getInputFieldWithKey(dfkey);
			if(destField == null)
				throw new IOException("Unknown source node in link: " + dfkey);

			try {
				link = new OpLink(source, sourceField, dest, destField);
			} catch(ItemMissingException exc) {
				throw new IOException("Could not construct link", exc);
			}
		}

		return link;
	}

	@Override
	public boolean handles(Class<?> cls) {
		return (cls == OpLink.class);
	}

	@Override
	public boolean handles(QName name) {
		return LINK_QNAME.equals(name);
	}
}
