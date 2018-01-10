package ca.gedge.opgraph.app.components.canvas;

import java.awt.*;

import javax.swing.*;

public class CanvasMinimapLayer extends JComponent {
	
	private CanvasMinimap minimap;
	
	public CanvasMinimapLayer(GraphCanvas canvas) {
		super();
		
		this.minimap = new CanvasMinimap(canvas);
		
		init();
	}

	public void update() {
		this.minimap.updateMinimap();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return null;
	}
	
	private void init() {
		setLayout(null);
		
		this.minimap.setBorder(BorderFactory.createEtchedBorder());
		this.minimap.setBounds(getVisibleRect().x + getVisibleRect().width - CanvasMinimap.MAX_LENGTH, getVisibleRect().y,
				CanvasMinimap.MAX_LENGTH, CanvasMinimap.MAX_LENGTH);
		
		add(this.minimap);
	}
	
	@Override
	protected void paintComponent(Graphics gfx) {
		super.paintComponent(gfx);
		if(isVisible()) {
			int x = this.minimap.getX();
			int y = this.minimap.getY();
			int newX = getVisibleRect().x + getVisibleRect().width - CanvasMinimap.MAX_LENGTH;
			int newY = getVisibleRect().y;
			
			if(x != newX || y != newY) {
				this.minimap.updateMinimap();
			}
			
			this.minimap.setBounds(newX, newY, CanvasMinimap.MAX_LENGTH, CanvasMinimap.MAX_LENGTH);
		}
	}
	
}
