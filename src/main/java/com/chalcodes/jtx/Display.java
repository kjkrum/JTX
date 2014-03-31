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

/**
 * A Swing terminal display.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class Display extends JComponent implements BufferObserver, StickyScrollable {
	private static final long serialVersionUID = 4118501272135028272L;
	private static final int TEXT_BLINK_INTERVAL = 750;
	
	protected final Buffer buffer;
	protected final SoftFont font;
	protected final int glyphWidth;
	protected final int glyphHeight;
	protected final int initialViewportWidth;
	protected final int initialViewportHeight;

	// swing thread only
	protected final Rectangle paintClip = new Rectangle();
	protected boolean blinkOn = true;	
	
	// require synchronization
	protected final Rectangle extents;
	protected final Point preferredScrollOffset;
	protected boolean atBottom;
	
	/**
	 * Creates a new display.  If blinking is enabled, the entire component
	 * will be repainted every 750ms.
	 * 
	 * @param buffer the buffer to render
	 * @param font the font to render with
	 * @param columns initial preferred viewport size in columns
	 * @param rows initial preferred viewport size in rows 
	 * @param blink true if text with the blink attribute should blink; false
	 * if it should not
	 */
	public Display(Buffer buffer, SoftFont font, int columns, int rows, boolean blink) {
		buffer.addBufferObserver(this);
		this.buffer = buffer;
		this.font = font;
		glyphWidth = font.getGlyphSize().width;
		glyphHeight = font.getGlyphSize().height;
		initialViewportWidth = columns * glyphWidth;
		initialViewportHeight = rows * glyphHeight;
		extents = buffer.getExtents();
		preferredScrollOffset = new Point(0, 0);
		
		// autoscroll
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point p = getBufferCoordinates(e.getPoint());
				Rectangle r = new Rectangle(
						(extents.x + p.x) * glyphWidth,
						(extents.y + p.y) * glyphHeight,
						glyphWidth,
						glyphHeight);
				scrollRectToVisible(r);
				revalidate(); // TODO right thing to call?
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
	
	/**
	 * Returns the coordinates of the buffer cell corresponding to the
	 * specified coordinates on the component.
	 */
	public Point getBufferCell(Point pixel) {
		int px = pixel.x / glyphWidth;
		int py = pixel.y / glyphHeight;
		return new Point(px + extents.x, py + extents.y);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.getClipBounds(paintClip);
//		System.out.println("paint clip: " + paintClip);
		atBottom = paintClip.y + paintClip.height == getHeight();
		if(!atBottom) {
			preferredScrollOffset.y = 0;
		}

		// determine cell range touched by clip - relative, not absolute
		int cx = paintClip.x / glyphWidth;
		int cy = paintClip.y / glyphHeight;
		int cx2 = (paintClip.x + paintClip.width) / glyphWidth;
		int cy2 = (paintClip.y + paintClip.height) / glyphHeight;
		int cw = cx2 - cx;
		int ch = cy2 - cy;

//		System.out.printf("painting: %d, %d, %d, %d\n", cx + extents.x, cy + extents.y, cw, ch);

		g.setColor(Color.BLACK);

		// row & col are character coords relative to the position of the extents
		for(int row = cy; row < cy + ch; ++row) {
			for(int col = cx; col < cx + cw; ++col) {
				// determine where to draw
				int x = col * glyphWidth;
				int y = row * glyphHeight;
				// determine what to draw
				int bCol = col + extents.x;
				int bRow = row + extents.y;
//				if(extents.contains(bCol, bRow)) {
					// draw char from buffer
					int value = buffer.getContent(bCol, bRow);
					font.drawGlyph(value, blinkOn, g, x, y);
//				}
//				else {
//					// draw black box
//					g.fillRect(x, y, glyphWidth, glyphHeight);
//				}
			}
		}
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
		int col = (int) Math.floor((double)point.x / glyphWidth) + extents.x;
		int row = (int) Math.floor((double)point.y / glyphHeight) + extents.y;
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
	
	// BufferObserver
	
	@Override
	public void extentsChanged(Buffer source, int x, int y, int width, int height) {
		// TODO this should be smarter
		
//		System.out.printf("extents: %d, %d, %d, %d\n", x, y, width, height);
		
		// stick to the bottom as the component is growing
		if((extents.height != height || extents.width != width)) {
			if(atBottom) {
				preferredScrollOffset.y += height - extents.height;
			}
			extents.setSize(width, height);
			revalidate();
		}
		
		// hold position as the component is scrolling
		if(extents.y != y || extents.x != x) {
			if(!atBottom) {
				preferredScrollOffset.y -= y - extents.y;
			}
			extents.setLocation(x, y);
			revalidate();
			repaint();
		}
		
//		System.out.println("scroll offset: " + preferredScrollOffset);
	}
	
	// TODO separate updatePreferredScrollOffset method?

	@Override
	public void contentChanged(Buffer source, int x, int y, int width, int height) {
		// repaint only the changed region
		repaint((x - extents.x) * glyphWidth, (y - extents.y) * glyphHeight, width * glyphWidth, height * glyphHeight);
	}

	@Override
	public boolean isPreferredSizeSet() {
		return true;
	}

	// TODO reconsider whether the constructor params should determine the
	// initial preferred size or the initial preferred viewport size
	
	@Override
	public Dimension getPreferredSize() {
		// original
//		int w = Math.max(initialViewportWidth, extents.width * glyphWidth);
//		int h = Math.max(initialViewportHeight, extents.height * glyphHeight);
		
		// experimental
		int w = extents.width * glyphWidth;
		int h = extents.height * glyphHeight;
		
		return new Dimension(w, h);
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		// original
		//return getPreferredSize();
		
		// experimental
		int w = Math.max(initialViewportWidth, extents.width * glyphWidth);
		int h = Math.max(initialViewportHeight, extents.height * glyphHeight);
		return new Dimension(w, h);
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) {
		// orientation: SwingConstants.VERTICAL or SwingConstants.HORIZONTAL
		// direction: Less than zero to scroll up/left, greater than zero for down/right
		// return should always be positive
	
		int increment;
		int adjustment;
		
		if(orientation == SwingConstants.VERTICAL) {
			increment = glyphHeight;
			adjustment = (visible.y + visible.height) % glyphHeight; // align to bottom
		}
		else {
			increment = glyphWidth;
			adjustment = visible.x % glyphWidth; // align to left
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
			adjustment = (visible.y + visible.height) % glyphHeight; // align to bottom
		}
		else {
			increment = visible.width;
			adjustment = visible.x % glyphWidth; // align to left
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
	public void getPreferredScrollOffset(Point point) {
		point.setLocation(preferredScrollOffset.x * glyphWidth, preferredScrollOffset.y * glyphHeight);
		preferredScrollOffset.setLocation(0, 0);
	}	
}
