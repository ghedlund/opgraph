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
package ca.phon.opgraph.app.components.canvas;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

public class CanvasMinimap extends JComponent {
	
	private final WeakReference<GraphCanvas> canvasRef;
	
	public final static int MAX_LENGTH = 200;
	
	private JLabel minimapLabel;
	
	private Rectangle cursorRect = null;
	
	private final Color cursorColor = new Color(255, 255, 0, 100);

	public CanvasMinimap(GraphCanvas canvas) {
		super();
		
		canvasRef = new WeakReference<GraphCanvas>(canvas);
		
		setOpaque(false);
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		
		minimapLabel = new JLabel();
		minimapLabel.setHorizontalAlignment(SwingConstants.LEFT);
		minimapLabel.setVerticalAlignment(SwingConstants.TOP);
		minimapLabel.setOpaque(false);
		
		getCanvas().addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				updateMinimap();
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
				
			}
			
		});
		
		final MouseInputAdapter adapter = new MouseInputAdapter() {
			
			@Override
			public void mousePressed(MouseEvent me) {
				if(cursorRect != null) {
					final Rectangle cRect = cursorRect;
					
					final Point topLeft = minimapToGraph(new Point(cRect.x, cRect.y));
					final Point dim = minimapToGraph(new Point(cRect.width, cRect.height));
					
					getCanvas().scrollRectToVisible(new Rectangle(topLeft.x, topLeft.y, dim.x, dim.y));
				}
			}
			
			@Override
			public void mouseMoved(MouseEvent me) {
				final Point p = me.getPoint();
				final Rectangle rect = getMinimapViewRect();
				rect.translate(p.x - (rect.width/2), p.y - (rect.height/2));
				
				cursorRect = rect;
				repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent me) {
				cursorRect = null;
				repaint();
			}
			
		};
		
		minimapLabel.addMouseListener(adapter);
		minimapLabel.addMouseMotionListener(adapter);
		
		add(minimapLabel, BorderLayout.CENTER);
	}
	
	public JLabel getLabel() {
		return this.minimapLabel;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(cursorRect != null) {
			g.setColor(cursorColor);
			g.fillRect(cursorRect.x, cursorRect.y, cursorRect.width, cursorRect.height);
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(MAX_LENGTH, MAX_LENGTH);
	}
	
	public GraphCanvas getCanvas() {
		return canvasRef.get();
	}
	
	public void updateMinimap() {
		final BufferedImage minimapImage = createMinimapImage();
		minimapLabel.setIcon(new ImageIcon(minimapImage));
	}
	
	public Dimension getMinimapSize(Rectangle graphBounds) {
		int w = 0;
		int h = 0;
		
		if(graphBounds.getWidth() > graphBounds.getHeight()) {
			w = MAX_LENGTH;
			h = (int)Math.floor((w * graphBounds.getHeight()) / graphBounds.getWidth());
		} else {
			h = MAX_LENGTH;
			w = (int)Math.floor((h * graphBounds.getWidth()) / graphBounds.getHeight());
		}
		
		return new Dimension(w, h);
	}
	
	public Point graphToMinimap(Point p) {
		final Rectangle graphBounds = getCanvas().getUI().getGraphBoundingRect();
		final Dimension minimapSize = getMinimapSize(graphBounds);
		
		final double sx = minimapSize.getWidth() / graphBounds.getWidth();
		final double sy = minimapSize.getHeight() / graphBounds.getHeight();
		
		return new Point((int)(p.x * sx), (int)(p.y * sy));
	}
	
	public Point minimapToGraph(Point p) {
		final Rectangle graphBounds = getCanvas().getUI().getGraphBoundingRect();
		final Dimension minimapSize = getMinimapSize(graphBounds);
		
		final double sx = graphBounds.getWidth() / minimapSize.getWidth();
		final double sy = graphBounds.getHeight() / minimapSize.getHeight();
		
		return new Point((int)(p.x * sx), (int)(p.y * sy));
	}
	
	public Rectangle getMinimapViewRect() {
		final Rectangle viewRect = getCanvas().getVisibleRect();
		final Rectangle graphBounds = getCanvas().getUI().getGraphBoundingRect();
		final Dimension minimapSize = getMinimapSize(graphBounds);
		
		final double sx = minimapSize.getWidth() / graphBounds.getWidth();
		final double sy = minimapSize.getHeight() / graphBounds.getHeight();
		
		return new Rectangle(0, 0, (int)(viewRect.width * sx), (int)(viewRect.height * sy));	
	}
	
	public BufferedImage createMinimapImage() {
		final AffineTransform at = new AffineTransform();
		at.scale(getCanvas().getZoomLevel(), getCanvas().getZoomLevel());
		
		Rectangle graphBounds = getCanvas().getUI().getGraphBoundingRect();
		Dimension minimapSize = getMinimapSize(graphBounds);
		try {
			Point2D zoomedSize = at.inverseTransform(new Point2D.Double(minimapSize.getWidth(), minimapSize.getHeight()), null);
			minimapSize.setSize(zoomedSize.getX(), zoomedSize.getY());
		} catch (NoninvertibleTransformException e) {
		}
		
		final double sx = minimapSize.getWidth() / graphBounds.getWidth();
		final double sy = minimapSize.getHeight() / graphBounds.getHeight();		
		
		final BufferedImage img = new BufferedImage(minimapSize.width, minimapSize.height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = (Graphics2D)img.getGraphics();
		final AffineTransform transform = AffineTransform.getScaleInstance(sx, sy);
		g.setTransform(transform);
		g.setClip(graphBounds);
		
		int rule = AlphaComposite.SRC_OVER;
		Composite comp = AlphaComposite.getInstance(rule, 0.75f);
		g.setComposite(comp);
		
		getCanvas().getUI().getGridLayer().setVisible(false);
		getCanvas().getUI().getMinimapLayer().setVisible(false);
		float zl = getCanvas().getZoomLevel();
		getCanvas().setZoomLevel(1.0f);
		getCanvas().paint(g);
		getCanvas().setZoomLevel(zl);
		getCanvas().getUI().getMinimapLayer().setVisible(true);
		getCanvas().getUI().getGridLayer().setVisible(true);
		
		// paint current view rect
		Rectangle viewRect = getCanvas().getVisibleRect();
		try {
			Point2D viewTopLeft = at.inverseTransform(viewRect.getLocation(), null);
			Point2D viewDimensions = at.inverseTransform(new Point2D.Double(viewRect.getWidth(), viewRect.getHeight()), null);
			
			viewRect.setBounds((int)viewTopLeft.getX(), (int)viewTopLeft.getY(), (int)viewDimensions.getX(), (int)viewDimensions.getY());
		} catch (NoninvertibleTransformException e) {
		}
		
		g.setColor(new Color(255, 255, 255, 100));
		g.fillRect(viewRect.x, viewRect.y, viewRect.width, viewRect.height);
		
		return img;
	}

}
