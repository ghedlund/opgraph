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
package ca.phon.opgraph.app.components.canvas;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.util.*;
import ca.phon.opgraph.io.*;

/**
 * Inner class for handling OpGraph clipboard content.
 */
public class SubgraphClipboardContents implements Transferable {
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(SubgraphClipboardContents.class.getName());

	/** Clipboard data flavor */
	public static final DataFlavor copyFlavor = new DataFlavor(SubgraphClipboardContents.class, "SubgraphClipboardContents");

	/** The canvas */
	private final GraphCanvas canvas;

	/** The copied sub-graph */
	private final OpGraph subGraph;

	/** Mapping from graph to the number of times it has been pasted into each graph */
	private final Map<OpGraph, Integer> graphDuplicates = new HashMap<OpGraph, Integer>();

	/** Cached data values */
	private final Map<DataFlavor, Object> cachedData = new HashMap<DataFlavor, Object>();

	/**
	 * Constructs a clipboard contents for a given document and the subgraph which is being copied. 
	 * 
	 * @param document  the document containing the graph 
	 * @param selectedGraph  the subgraph which is being copied
	 */
	public SubgraphClipboardContents(GraphCanvas canvas, OpGraph selectedGraph) {
		this.canvas = canvas;
		this.subGraph = selectedGraph;
		this.graphDuplicates.put(canvas.getDocument().getGraph(), new Integer(0));
	}
	
	public Map<OpGraph, Integer> getGraphDuplicates() {
		return this.graphDuplicates;
	}
	
	public OpGraph getGraph() {
		return this.subGraph;
	}
	
	public GraphCanvas getCanvas() {
		return this.canvas;
	}

	//
	// Transferable overrides
	//

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		// First, check to see if we've cached a value for this data flavor 
		Object retVal = cachedData.get(flavor);
		final GraphDocument document = canvas.getDocument();

		if(retVal == null) {
			if(flavor == copyFlavor) {
				retVal = this;
			} else if(flavor == DataFlavor.imageFlavor) {
				// XXX create a temp graph of the selected nodes and links?
				final Rectangle boundRect = GraphUtils.getBoundingRect(subGraph.getVertices());
				if(boundRect.width > 0 && boundRect.height > 0) {
					final Dimension fullSize = new Dimension(boundRect.x + boundRect.width, boundRect.y + boundRect.height);
					final BufferedImage img = new BufferedImage(boundRect.width, boundRect.height, BufferedImage.TYPE_INT_ARGB);
					final Graphics2D g = (Graphics2D)img.getGraphics();
					final Collection<OpNode> currentSelection = document.getSelectionModel().getSelectedNodes();

					// Set clip and paint into temp buffer
					final AffineTransform transform = AffineTransform.getTranslateInstance(-boundRect.getX(), -boundRect.getY());
					g.setColor(new Color(255,255,255,0));
					g.fill(new Rectangle(0, 0, fullSize.width, fullSize.height));
					g.setTransform(transform);
					g.setClip(boundRect);

					document.getSelectionModel().setSelectedNodes(null);
					canvas.getUI().getGridLayer().setVisible(false);
					canvas.getUI().getMinimapLayer().setVisible(false);
					canvas.paint(g);
					canvas.getUI().getMinimapLayer().setVisible(true);
					canvas.getUI().getGridLayer().setVisible(true);
					document.getSelectionModel().setSelectedNodes(currentSelection);

					// Create the image
					//final ByteArrayOutputStream bout = new ByteArrayOutputStream();
					//ImageIO.write(img, "png", bout);
					//retVal = Toolkit.getDefaultToolkit().createImage(bout.toByteArray());
					retVal = img;
				}
			} else if(flavor == DataFlavor.stringFlavor) {
				// Store XML data for string flavor
				final OpGraphSerializer serializer = OpGraphSerializerFactory.getDefaultSerializer();
				if(serializer == null) {
					// XXX Consider just spitting out the default java serializer byte data?
					LOGGER.severe("No default serializer available");
					return false;
				}

				// Write XML to byte stream
				final ByteArrayOutputStream bout = new ByteArrayOutputStream();
				try {
					serializer.write(subGraph, bout);
					final String graphString = bout.toString("UTF-8");
					retVal = graphString;
				} catch(IOException e) {
					LOGGER.severe(e.getMessage());
					retVal = "";
				}
			} else {
				throw new UnsupportedFlavorException(flavor);
			}

			if(retVal != null)
				cachedData.put(flavor, retVal);
		}

		return retVal;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{copyFlavor, DataFlavor.imageFlavor, DataFlavor.stringFlavor};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavor.equals(copyFlavor) || flavor.equals(DataFlavor.imageFlavor) || flavor.equals(DataFlavor.stringFlavor));
	}

}
