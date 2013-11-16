package com.chalcodes.jtx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class Display extends JComponent implements BufferObserver, StickyScrollable {
	private static final long serialVersionUID = 1L;
	
	private static final int TEXT_BLINK_INTERVAL = 750;
	
	protected final Buffer buffer;
	protected final Rectangle extents;
	protected final SoftFont font;
	protected final Dimension glyphSize;
	protected final Dimension viewportSize;
	protected final Point preferredScrollOffset;
	protected final Rectangle paintClip;
	protected boolean blinkOn = true;
	
	public Display(Buffer buffer, SoftFont font, int columns, int rows) {
		this(buffer, font, columns, rows, true);
	}
	
	public Display(Buffer buffer, SoftFont font, int columns, int rows, boolean blink) {
		this.buffer = buffer;
		extents = buffer.getExtents();
		this.font = font;
		glyphSize = font.getGlyphSize();
		viewportSize = new Dimension(columns * glyphSize.width, rows * glyphSize.height);
		preferredScrollOffset = new Point(0, 0);
		paintClip = new Rectangle();
		
		buffer.addBufferObserver(this);
		
		// autoscroll
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point p = getBufferCoordinates(e.getPoint());
				Rectangle extents = Display.this.buffer.getExtents();
				Rectangle r = new Rectangle(
						(extents.x + p.x) * glyphSize.width,
						(extents.y + p.y) * glyphSize.height,
						glyphSize.width,
						glyphSize.height);
				scrollRectToVisible(r);
				validate();
			}
		});
		
		// this is what makes blinking text blink
		if(blink) {
			new Timer(TEXT_BLINK_INTERVAL, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					blinkOn = !blinkOn;
					repaint();
				}
			}).start();
		}
	}
	
	@Override
	public boolean isPreferredSizeSet() {
		return true;
	}
	
	@Override
	public Dimension getPreferredSize() {
		int w = Math.max(viewportSize.width, extents.width * glyphSize.width);
		int h = Math.max(viewportSize.height, extents.height * glyphSize.height);
		return new Dimension(w, h);
	}	
	
	/**
	 * Returns the coordinates of the buffer cell corresponding to the
	 * specified coordinates on the component.
	 */
	public Point getBufferCell(Point pixel) {
		int px = pixel.x / glyphSize.width;
		int py = pixel.y / glyphSize.height;
		return new Point(px + extents.x, py + extents.y);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.getClipBounds(paintClip);

		// determine cell range touched by clip - relative, not absolute
		int cx = paintClip.x / glyphSize.width;
		int cy = paintClip.y / glyphSize.height;
		int cx2 = (paintClip.x + paintClip.width) / glyphSize.width;
		int cy2 = (paintClip.y + paintClip.height) / glyphSize.height;
		int cw = cx2 - cx + 1;
		int ch = cy2 - cy + 1;

		g.setColor(Color.BLACK);

		// row & col are character coords relative to the position of the extents
		for(int row = cy; row < cy + ch; ++row) {
			for(int col = cx; col < cx + cw; ++col) {
				// determine where to draw
				int x = col * glyphSize.width;
				int y = row * glyphSize.height;
				// determine what to draw
				int bCol = col + extents.x;
				int bRow = row + extents.y;
				if(extents.contains(bCol, bRow)) {
					// draw char from buffer
					int value = buffer.getContent(bCol, bRow);
					font.drawGlyph(value, blinkOn, g, x, y);
				}
				else {
					// draw black box
					g.fillRect(x, y, glyphSize.width, glyphSize.height);
				}
			}
		}				
	}

	@Override
	public void extentsChanged(Buffer buffer, int x, int y, int width, int height) {
		if(extents.x != x) {
			preferredScrollOffset.x -= x - extents.x;
			extents.x = x;
		}
		if(extents.y != y) {
			preferredScrollOffset.y -= y - extents.y;
			extents.y = y;
		}
		extents.setSize(width, height);
		revalidate();
		repaint();
	}

	@Override
	public void contentChanged(Buffer buffer, int x, int y, int width, int height) {
		// just repaint changed region
		repaint((x - extents.x) * glyphSize.width, (y - extents.y) * glyphSize.height, width * glyphSize.width, height * glyphSize.height);
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(viewportSize);
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) {
		// orientation: SwingConstants.VERTICAL or SwingConstants.HORIZONTAL
		// direction: Less than zero to scroll up/left, greater than zero for down/right
		// return should always be positive
	
		int increment;
		int adjustment;
		
		if(orientation == SwingConstants.VERTICAL) {
			increment = glyphSize.height;
			adjustment = (visible.y + visible.height) % glyphSize.height; // align to bottom
		}
		else {
			increment = glyphSize.width;
			adjustment = visible.x % glyphSize.width; // align to left
		}
		
		if(adjustment == 0) return increment;
		else if(direction > 0) return increment - adjustment;
		else return adjustment;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
		// orientation: SwingConstants.VERTICAL or SwingConstants.HORIZONTAL
		// direction: Less than zero to scroll up/left, greater than zero for down/right
		// return should always be positive
		
		int increment;
		int adjustment;
		
		if(orientation == SwingConstants.VERTICAL) {
			increment = visible.height;
			adjustment = (visible.y + visible.height) % glyphSize.height; // align to bottom
		}
		else {
			increment = visible.width;
			adjustment = visible.x % glyphSize.width; // align to left
		}
		
		if(adjustment == 0) return increment;
		else if(direction > 0) return increment - adjustment;
		else return increment + adjustment;
		
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public Point getPreferredScrollOffset() {
		Point p = new Point();
		getPreferredScrollOffset(p);
		return p;
	}
	
	@Override
	public void getPreferredScrollOffset(Point point) {
		point.setLocation(preferredScrollOffset.x * glyphSize.width, preferredScrollOffset.y * glyphSize.height);
		preferredScrollOffset.setLocation(0, 0);
	}
	
	/**
	 * Calculates the buffer coordinates corresponding to a point in the
	 * component's coordinate space.  Note that if the point is outside the
	 * component, the value returned will be outside the buffer extents.
	 * 
	 * @param point the location relative to the component
	 * @return the corresponding buffer coordinates
	 */
	public Point getBufferCoordinates(Point point) {
		Point result = new Point();
		getBufferCoordinates(point, result);
		return result;
	}
	
	/**
	 * Calculates the buffer coordinates corresponding to a point in the
	 * component's coordinate space, storing the value in <tt>result</tt>.
	 * The point may be outside the buffer extents.
	 * 
	 * @param point the location relative to the component
	 * @param result the object in which to store the buffer coordinates
	 */
	public void getBufferCoordinates(Point point, Point result) {
		int col = (int) Math.floor((double)point.x / glyphSize.width) + extents.x;
		int row = (int) Math.floor((double)point.y / glyphSize.height) + extents.y;
		result.setLocation(col, row);
	}
	
	/**
	 * Gets the buffer this display is attached to.
	 * 
	 * @return the buffer
	 */
	public Buffer getBuffer() {
		return buffer;
	}
	
}
