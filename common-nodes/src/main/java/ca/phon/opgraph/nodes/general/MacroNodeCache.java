package ca.phon.opgraph.nodes.general;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.io.OpGraphSerializer;
import ca.phon.opgraph.io.OpGraphSerializerFactory;
import ca.phon.opgraph.io.OpGraphSerializerInfo;

public class MacroNodeCache {
	
	private final Map<URI, WeakReference<OpGraph>> graphMap = new HashMap<URI, WeakReference<OpGraph>>();
	
	public MacroNodeCache() {
		super();
	}
	
	/**
	 * Get graph from cache or load it.
	 * 
	 * @param graphURL
	 * @return
	 * @throws IOException
	 */
	public OpGraph getGraph(URI graphURI) throws IOException {
		WeakReference<OpGraph> graphRef = graphMap.get(graphURI);
		if(graphRef != null) {
			if(graphRef.get() != null)
				return graphRef.get();
			else
				graphMap.remove(graphURI);
		}
		
		URL graphURL = uriToUrl(graphURI);
		
		if(graphURL != null) {
			final OpGraphSerializer serializer = OpGraphSerializerFactory.getDefaultSerializer();
			OpGraph graph = serializer.read(graphURL.openStream());
			
			graphMap.put(graphURI, new WeakReference<OpGraph>(graph));
			
			return graph;
		} else {
			throw new IOException("Unable to location graph at " + graphURI.toASCIIString());
		}
	}
	
	private URL uriToUrl(URI uri) {
		switch(uri.getScheme()) {
		case "classpath":
			return ClassLoader.getSystemResource(uri.getSchemeSpecificPart());
			
		default:
			try {
				return uri.toURL();
			} catch (MalformedURLException e) {
				// log error
			}
		}
		return null;
	}
	
}
