package ca.gedge.opgraph.app.components.canvas;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

public class CanvasMinimap extends JComponent {
	
	private final WeakReference<GraphCanvas> canvasRef;
	
	public final static int MAX_LENGTH = 200;
	
	private JLabel minimapLabel;

	public CanvasMinimap(GraphCanvas canvas) {
		super();
		
		canvasRef = new WeakReference<GraphCanvas>(canvas);
		
		setOpaque(false);
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		
		minimapLabel = new JLabel();
		minimapLabel.setHorizontalAlignment(SwingConstants.CENTER);
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
		
		minimapLabel.addMouseListener(new MouseInputAdapter() {
			
			@Override
			public void mousePressed(MouseEvent me) {
				System.out.println(me.getPoint());
			}
			
		});
		
		add(minimapLabel, BorderLayout.CENTER);
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
	
	public BufferedImage createMinimapImage() {
		final Rectangle graphBounds = getCanvas().getUI().getGraphBoundingRect();
		final Dimension minimapSize = getMinimapSize(graphBounds);
		
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
		getCanvas().paint(g);
		getCanvas().getUI().getMinimapLayer().setVisible(true);
		getCanvas().getUI().getGridLayer().setVisible(true);
		
		// paint view rect
		final Rectangle viewRect = getCanvas().getVisibleRect();
		g.setColor(new Color(200, 200, 200, 150));
		g.fillRect(viewRect.x, viewRect.y, viewRect.width, viewRect.height);
		
		return img;
	}

}
