module ca.phon.opgraph.xml {
	requires java.logging;
	requires java.xml;
	requires transitive ca.phon.opgraph.core;

	exports ca.phon.opgraph.io.xml;

	uses ca.phon.opgraph.io.xml.XMLSerializer;
	uses ca.phon.opgraph.io.xml.SchemaProvider;

	provides ca.phon.opgraph.io.OpGraphSerializer
		with ca.phon.opgraph.io.xml.XMLSerializerFactory;

	provides ca.phon.opgraph.io.xml.XMLSerializer
		with ca.phon.opgraph.io.xml.DefaultGraphXMLSerializer,
			ca.phon.opgraph.io.xml.DefaultNodeXMLSerializer,
			ca.phon.opgraph.io.xml.DefaultLinkXMLSerializer,
			ca.phon.opgraph.io.xml.DefaultFieldXMLSerializer,
			ca.phon.opgraph.io.xml.DefaultExtendableXMLSerializer;
}
