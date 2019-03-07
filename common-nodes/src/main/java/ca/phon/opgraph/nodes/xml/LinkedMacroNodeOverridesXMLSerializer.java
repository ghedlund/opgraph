package ca.phon.opgraph.nodes.xml;

import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.dag.CycleDetectedException;
import ca.phon.opgraph.dag.VertexNotFoundException;
import ca.phon.opgraph.exceptions.ItemMissingException;
import ca.phon.opgraph.extensions.CompositeNode;
import ca.phon.opgraph.extensions.Extendable;
import ca.phon.opgraph.io.xml.XMLSerializer;
import ca.phon.opgraph.io.xml.XMLSerializerFactory;
import ca.phon.opgraph.nodes.general.LinkedMacroNodeOverrides;
import ca.phon.opgraph.nodes.general.MacroNode;

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
					List<OpNode> nodePath = macroGraph.getNodePath(overrideNode.getId());
					
					if(nodePath.size() == 0 || !nodePath.get(nodePath.size()-1).getId().equals(overrideNode.getId())) {
						throw new IOException("Override node not found in macro");
					}
					
					OpGraph parentGraph = macro.getGraph();
					if(nodePath.size() > 1) {
						CompositeNode cnode = (CompositeNode)nodePath.get(nodePath.size()-2);
						parentGraph = cnode.getGraph();
						
						cnode.setGraph(new OpGraph(parentGraph));
						parentGraph = cnode.getGraph();
					}
					try {
						parentGraph.swap(overrideNode);
					} catch (VertexNotFoundException | CycleDetectedException | ItemMissingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
