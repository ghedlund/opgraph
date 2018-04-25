/*
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
/**
 * 
 */
package ca.phon.opgraph.app.components.canvas;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.util.GraphUtils;
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
