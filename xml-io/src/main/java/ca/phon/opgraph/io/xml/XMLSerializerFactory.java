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

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

import javax.xml.*;
import javax.xml.namespace.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.extensions.*;
import ca.phon.opgraph.io.*;
import ca.phon.opgraph.util.*;

/**
 * A factory that maps qualified names to serializers that handle them.
 */
@OpGraphSerializerInfo(extension="xml", description="XML Files")
public final class XMLSerializerFactory implements Extendable, OpGraphSerializer {
	/** The default namespace */
	public static final String DEFAULT_NAMESPACE = "https://www.phon.ca/ns/opgraph";

	/** The default prefix used for writing */
	static final String DEFAULT_PREFIX = "og";

	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(XMLSerializerFactory.class.getName());

	/** The serializers to use */
	private Collection<XMLSerializer> serializers;

	/** XML Validator */
	private Validator validator;
	
	private final ExtendableSupport extSupport = new ExtendableSupport(XMLSerializerFactory.class);

	/**
	 * Default constructor.
	 */
	public XMLSerializerFactory() {
		this.serializers = new ArrayList<XMLSerializer>();
		
		initialize();
	}

	public void initialize() {
		// Load XML serialization providers
		serializers.clear();

		for(Class<? extends XMLSerializer> provider : ServiceDiscovery.getInstance().findProviders(XMLSerializer.class)) {
			try {
				serializers.add( provider.newInstance() );
			} catch(InstantiationException exc) {
				LOGGER.warning("Could not instantiate XMLSerializer provider: " + provider.getName());
			} catch(IllegalAccessException exc) {
				LOGGER.warning("Could not instantiate XMLSerializer provider: " + provider.getName());
			}
		}

		// Construct a validator
		// XXX perhaps just do this once in a static block/function?
		validator = null;
		try {
			// Find a list of all schemas
			final List<URL> schemaLists = ServiceDiscovery.getInstance().findResources("META-INF/schemas/list");
			final List<URL> schemas = new ArrayList<URL>();
			for(URL schemaListURL : schemaLists) {
				final BufferedReader br = new BufferedReader(new InputStreamReader(schemaListURL.openStream()));
				String line = null;
				while((line = br.readLine()) != null)
					if(line.trim().length() > 0)
						schemas.addAll( ServiceDiscovery.getInstance().findResources("META-INF/schemas/" + line) );
			}

			// Load up extension schemas
			final Source [] schemaSource = new Source[schemas.size() + 1];
			for(int index = 0; index < schemas.size(); ++index)
				schemaSource[index + 1] = new StreamSource(schemas.get(index).openStream());

			// Ensure core OpGraph schema comes first
			schemaSource[0] = new StreamSource(XMLSerializerFactory.class.getResource("/META-INF/schemas/opgraph.xsd").openStream());

			final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			final Schema schema = sf.newSchema(schemaSource);
			validator = schema.newValidator();
		} catch(SAXException exc) {
			LOGGER.warning("SAXException while initializing validator: " + exc.getLocalizedMessage());
		} catch(IOException exc) {
			LOGGER.warning("IOException while initializing validator: " + exc.getLocalizedMessage());
		}
	}

	/**
	 * Gets the qualified name of an element.
	 *
	 * @param elem  the element
	 *
	 * @return the qualified name of the element
	 */
	public static QName getQName(Element elem) {
		String localName = elem.getLocalName();
		String prefix = (elem.getPrefix() == null ? XMLConstants.DEFAULT_NS_PREFIX : elem.getPrefix());
		return new QName(elem.getNamespaceURI(), localName, prefix);
	}

	/**
	 * Writes an element's extensions to a parent element.
	 *
	 * @param doc  the document
	 * @param parent  the parent element to write to
	 * @param ext  the {@link Extendable}
	 *
	 * @throws IOException  if any errors occur when serializing
	 */
	public void writeExtensions(Document doc, Element parent, Extendable ext) throws IOException {
		final Element extensionsElem = doc.createElementNS(DEFAULT_NAMESPACE, "extensions");
		for(Class<?> extension : ext.getExtensionClasses()) {
			final XMLSerializer serializer = getHandler(extension);
			if(serializer == null)
				LOGGER.warning("Node contains an unwritable extension: " + extension.getName());
			else
				serializer.write(this, doc, extensionsElem, ext.getExtension(extension));
		}

		if(extensionsElem.getChildNodes().getLength() > 0)
			parent.appendChild(extensionsElem);
	}

	/**
	 * Gets the handler for a specified qualified name.
	 *
	 * @param name  qualified name for which a serializer is needed
	 *
	 * @return an XML serializer for the given qualified name, or <code>null</code>
	 *         if no handler is registered for the given qualified name
	 */
	public XMLSerializer getHandler(QName name) {
		for(XMLSerializer serializer : serializers) {
			if(serializer.handles(name))
				return serializer;
		}
		return null;
	}

	/**
	 * Gets the handler for a specified class. Ascends the inheritance chain
	 * of the given class to see if there is a handler for a super class.
	 *
	 * @param cls  class for which a serializer is needed
	 *
	 * @return an XML serializer for the given class, or <code>null</code> if
	 *         no handler is registered for the class
	 */
	public XMLSerializer getHandler(Class<?> cls) {
		while(cls != null) {
			for(XMLSerializer serializer : serializers) {
				if(serializer.handles(cls))
					return serializer;
			}

			cls = cls.getSuperclass();
		}
		return null;
	}
	
	private Document documentFromFile(File file) throws IOException {
		Document doc;
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			
			doc = factory.newDocumentBuilder().parse(file);
			return doc;
		} catch(SAXException exc) {
			throw new IOException("Could not parse stream as XML", exc);
		} catch(ParserConfigurationException exc) {
			throw new IOException("Could not create document builder", exc);
		}
	}
	
	private Document documentFromStream(InputStream stream) throws IOException {
		Document doc;
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			
			doc = factory.newDocumentBuilder().parse(stream);
			return doc;
		} catch(SAXException exc) {
			throw new IOException("Could not parse stream as XML", exc);
		} catch(ParserConfigurationException exc) {
			throw new IOException("Could not create document builder", exc);
		}
	}
	
	private Document documentFromGraph(OpGraph graph) throws IOException {
		Document doc;
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);

			// Construct a DOM document
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			final DOMImplementation domImpl = docBuilder.getDOMImplementation();

			// use NAMESPACE as default namespace for document
			doc = domImpl.createDocument(DEFAULT_NAMESPACE, "opgraph", null);
		} catch(ParserConfigurationException exc) {
			throw new IOException("Could not create document builder", exc);
		}

		final Element root = doc.getDocumentElement();

		final XMLSerializer serializer = getHandler(graph.getClass());
		if(serializer != null)
			serializer.write(this, doc, root, graph);

		doc.normalize();
		
		return doc;
	}

	//
	// Overrides
	//
	
	private void validate(Document doc) throws IOException {
		if(validator != null) {
			try {
				final Source source = new DOMSource(doc);
				final DOMResult result = new DOMResult();
				validator.validate(source, result);
	
				// Get the schema-transformed document
				final Node resultNode = result.getNode();
				if(resultNode instanceof Document)
					doc = (Document)resultNode;
			} catch(SAXException exc) {
				exc.printStackTrace();
				throw new IOException("Given stream is not a valid OpGraph XML document", exc);
			}
		}
	}

	@Override
	public void validate(File file) throws IOException {
		Document doc = documentFromFile(file);
		validate(doc);
	}

	@Override
	public void validate(InputStream stream) throws IOException {
		// Create document
		Document doc = documentFromStream(stream);
		validate(doc);
	}

	private OpGraph read(Document doc) throws IOException {
		// Read from stream
		OpGraph ret = null;
	
		final XMLSerializer serializer = getHandler(getQName(doc.getDocumentElement()));
		if(serializer != null) {
			final Object objRead = serializer.read(this, null, null, doc, doc.getDocumentElement());
			if(objRead instanceof OpGraph)
				ret = (OpGraph)objRead;
		}
	
		if(ret == null)
			throw new IOException("Graph could not be read from stream");
	
		return ret;
	}

	@Override
	public OpGraph read(File file) throws IOException {
		Document doc = documentFromFile(file);
		validate(doc);
	
		return read(doc);
	}

	/**
	 * Reads a graph from a stream.
	 *
	 * @param stream  the stream to read from
	 *
	 * @throws IOException  if any I/O errors occur
	 */
	@Override
	public OpGraph read(InputStream stream) throws IOException {
		Document doc = documentFromStream(stream);
		validate(doc);
	
		return read(doc);
	}

	private void write(Document doc, OutputStream stream) throws IOException {
		try {
			final Source source = new DOMSource(doc);
			final Result result = new StreamResult(stream);
			final Transformer transformer = TransformerFactory.newInstance().newTransformer();

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			transformer.transform(source, result);
		} catch(TransformerConfigurationException exc) {
			throw new IOException("Could not write DOM tree to stream", exc);
		} catch(TransformerFactoryConfigurationError exc) {
			throw new IOException("Could not write DOM tree to stream", exc);
		} catch(TransformerException exc) {
			throw new IOException("Could not write DOM tree to stream", exc);
		}
	}
	
	@Override
	public void write(OpGraph graph, File file) throws IOException {
		Document doc = documentFromGraph(graph);
		validate(doc);
		write(doc, new FileOutputStream(file));
	}

	/**
	 * Writes a graph to a stream.
	 *
	 * @param graph  the graph to write
	 * @param stream  the stream to write to
	 *
	 * @throws IOException  if any I/O errors occur
	 */
	@Override
	public void write(OpGraph graph, OutputStream stream) throws IOException {
		Document doc = documentFromGraph(graph);
		validate(doc);
		write(doc, stream);
	}
	
	public <T> T getExtension(Class<T> type) {
		return extSupport.getExtension(type);
	}

	public Collection<Class<?>> getExtensionClasses() {
		return extSupport.getExtensionClasses();
	}

	public <T> T putExtension(Class<T> type, T extension) {
		return extSupport.putExtension(type, extension);
	}

}
