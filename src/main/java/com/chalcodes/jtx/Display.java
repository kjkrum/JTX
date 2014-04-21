package com.chalcodes.jtx;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.SwingConstants;

/**
 * A Swing terminal display.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class Display extends JComponent implements BufferObserver, StickyScrollable {
	private static final long serialVersionUID = 4118501272135028272L;
	@SuppressWarnings("unused")
	private static final int TEXT_BLINK_INTERVAL = 750;
	
	protected final Buffer buffer;
	protected final SoftFont font;
	protected final int glyphWidth;
	protected final int glyphHeight;
	protected final int initialViewportWidth;
	protected final int initialViewportHeight;
	/**
	 * Current buffer extents.
	 */
	protected final Rectangle extents;
	/**
	 * Change in <tt>extents.y</tt> since the last call to
	 * {@link #getPreferredViewPosition(Rectangle)}.  Used to calculate the
	 * preferred view position and compensate for extents changes between
	 * layout and painting.
	 */
	protected int deltaY = 0;
	/**
	 * True if the bottom of the component was visible the last time any part
	 * of the component was painted.  This is how
	 * {@link #getPreferredViewPosition(Point)} knows what to do.
	 */
	protected boolean atBottom = false;
	protected boolean blinkOn = true;
	
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
//		if(blink) {
//			new Timer(TEXT_BLINK_INTERVAL, new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					blinkOn = !blinkOn;
//					repaint();
//				}
//			}).start();
//		}		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		final Rectangle visible = getVisibleRect();
		atBottom = (visible.y + visible.height == getHeight());
//		System.out.println(System.currentTimeMillis() + " paintComponent: visible " + visible + ", height " + getHeight() + ", atBottom " + atBottom);

		final Rectangle paintClip = g.getClipBounds();
//		System.out.println("paint clip: " + paintClip);
		
		// determine the cell range touched by the clip
		final Point topLeftCell = getBufferCoordinates(paintClip.x, paintClip.y);
		final Point bottomRightCell = getBufferCoordinates(paintClip.x + paintClip.width - 1, paintClip.y + paintClip.height - 1);
		
		for(int row = topLeftCell.y; row <= bottomRightCell.y; ++row) {
			for(int col = topLeftCell.x; col <= bottomRightCell.x; ++col) {
				// row & col are absolute buffer coordinates - what to paint
				// compute where to paint it, taking advantage of the fact
				// that (extents.x, extents.y) of the buffer coordinate space
				// corresponds to (0, 0) of the graphics coordinate space 
				int x = (col - extents.x) * glyphWidth;
				int y = (row - extents.y) * glyphHeight;
				// TODO correct for deltaY
				int value = buffer.getContent(col, row);
				font.drawGlyph(value, blinkOn, g, x, y);
			}
		}
	}

	/**
	 * Calculates the buffer coordinates corresponding to a point in the
	 * component's coordinate space, storing the value in <tt>result</tt>.
	 * Note that if the point is outside the component, the result will be
	 * outside the buffer extents.  The other <tt>getBufferCoordinates</tt>
	 * methods are implemented by calling this version.
	 * 
	 * @param x the x coordinate relative to the component
	 * @param y the y coordinate relative to the component
	 * @param result the object in which to store the buffer coordinates
	 */
	public void getBufferCoordinates(int x, int y, Point result) {
		int col = (int) Math.floor(((double)x) / glyphWidth) + extents.x;
		int row = (int) Math.floor(((double)y) / glyphHeight) + extents.y;
		result.setLocation(col, row);
	}
	
	/**
	 * Convenience method for {@link #getBufferCoordinates(int, int, Point)}.
	 * 
	 * @param point
	 * @param result the object in which to store the buffer coordinates
	 */
	public final void getBufferCoordinates(Point point, Point result) {
		getBufferCoordinates(point.x, point.y, result);
	}
	
	/**
	 * Convenience method for {@link #getBufferCoordinates(int, int, Point)}.
	 * 
	 * @param x the x coordinate relative to the component
	 * @param y the y coordinate relative to the component
	 * @return a new point containing the buffer coordinates
	 */
	public final Point getBufferCoordinates(int x, int y) {
		Point result = new Point();
		getBufferCoordinates(x, y, result);
		return result;
	}
	
	/**
	 * Convenience method for {@link #getBufferCoordinates(int, int, Point)}.
	 * 
	 * @param point
	 * @return
	 */
	public final Point getBufferCoordinates(Point point) {
		return getBufferCoordinates(point.x, point.y);
	}
	
	/**
	 * Gets the buffer this display is attached to.
	 * 
	 * @return the buffer
	 */
	public Buffer getBuffer() {
		return buffer;
	}
	
	@Override
	public void extentsChanged(Buffer source, int x, int y, int width, int height) {
//		System.out.printf("extents: %d, %d, %d, %d\n", x, y, width, height);
		deltaY += y - extents.y;
		extents.setBounds(x, y, width, height);
		revalidate();
		repaint();
	}

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
	public Point getPreferredViewPosition(Rectangle currentViewport, Rectangle preferredSize) {
		// TODO compute atBottom here based on current size?
		
		
		final Point newPosition = currentViewport.getLocation();
//		System.out.println(System.currentTimeMillis() + " current position: " + newPosition);
		if(atBottom) {
//			System.out.println(System.currentTimeMillis() + " preferred size: " + getPreferredSize());
//			System.out.println(System.currentTimeMillis() + " viewport height: " + currentViewport.height);
			newPosition.y += preferredSize.height - currentViewport.height;
		}
		else {
//			System.out.println(System.currentTimeMillis() + " deltaY: " + deltaY);
			newPosition.y -= deltaY * glyphHeight;
		}		
		deltaY = 0;
		if(newPosition.y < 0) newPosition.y = 0;
//		System.out.println(System.currentTimeMillis() + " preferred position: " + newPosition);
		return newPosition;
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
	public void setSize(int width, int height) {
		super.setSize(width, height);
//		System.out.printf(System.currentTimeMillis() + " setSize: %d, %d\n", width, height);
	}
	
	
}
