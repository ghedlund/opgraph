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
package ca.phon.opgraph.app.components.canvas;

import java.awt.*;
import java.util.*;

import javax.swing.*;

public final class GraphCanvasScroller extends JScrollPane {

	private static final long serialVersionUID = -4076533416176955083L;
	
	private final GraphCanvas canvas;
	
	private JLabel zoomLbl;
	
	private JSlider zoomSlider;
	
	public GraphCanvasScroller(GraphCanvas canvas) {
		super(canvas);
		
		this.canvas = canvas;
		
		init();
	}
	
	private void init() {
		String zoomTxt = (int)Math.round(canvas.getZoomLevel() * 100.0f) + "%";
		zoomLbl = new JLabel(zoomTxt);
		
		canvas.addPropertyChangeListener("zoomLevel", (e) -> {
			var zt = (int)Math.round(canvas.getZoomLevel() * 100.0f) + "%";
			zoomLbl.setText(zt);
		});
		
		zoomSlider = createZoomSlider();
		zoomSlider.setOrientation(JSlider.VERTICAL);
		
		JPanel topPanel = new JPanel(new BorderLayout());
//		topPanel.add(zoomLbl, BorderLayout.EAST);
		topPanel.add(zoomSlider, BorderLayout.CENTER);
		
		setRowHeaderView(topPanel);
	}
	
	private JSlider createZoomSlider() {
		JSlider retVal = new JSlider();
		
		retVal.setMinimum((int)GraphCanvas.MINIMUM_ZOOM_LEVEL * 100);
		retVal.setMaximum((int)GraphCanvas.MAXIMUM_ZOOM_LEVEL * 100);
		
		retVal.setValue((int)(canvas.getZoomLevel() * 100));
		retVal.addChangeListener( (e) -> {
			canvas.setZoomLevel( (float)retVal.getValue() / 100.0f );
			canvas.repaint();
		});
		retVal.setPaintTicks(true);
		retVal.setMajorTickSpacing(10);
		retVal.setMinorTickSpacing(5);
		retVal.setSnapToTicks(true);
		
		// put label on 100% for reference
		retVal.setPaintLabels(true);
		Hashtable<Integer, JComponent> labelMap = new Hashtable<>();
		labelMap.put(100, new JLabel("*"));
		retVal.setLabelTable(labelMap);
		
		retVal.setPaintTrack(true);
		
		return retVal;
	}

}
