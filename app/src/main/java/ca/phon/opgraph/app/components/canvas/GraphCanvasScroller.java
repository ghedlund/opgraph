package ca.phon.opgraph.app.components.canvas;

import java.awt.BorderLayout;import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

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
