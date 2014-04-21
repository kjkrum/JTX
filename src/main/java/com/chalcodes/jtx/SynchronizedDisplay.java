package com.chalcodes.jtx;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * A terminal display that supports calls to its {@link BufferObserver}
 * methods in threads other than the Swing thread.  It is designed for use
 * with a {@link SynchronizedBuffer}.  It synchronizes on the buffer itself,
 * which is the same lock the Swing thread will need to acquire to read the
 * buffer and the same lock that threads writing to the buffer will already
 * hold when they call the <tt>BufferObserver</tt> methods.
 * <p>
 * Only the <tt>BufferObserver</tt> methods are guaranteed thread-safe.  By
 * necessity, other methods that access the same fields as these methods are
 * also synchronized.  However, exactly which methods those are is an
 * implementation detail that is subject to change.
 * <p>
 * <b>Note:</b> The superclass constructor registers the display as an
 * observer of its buffer, leaking a reference to the display before it is
 * fully constructed.  This is not a problem when the buffer is written only
 * in the Swing thread, since no write can occur until the display constructor
 * returns.  But when the buffer is written by another thread, if there is any
 * possibility of a write occurring while the display is being constructed,
 * then it must be instantiated within a block synchronized on the buffer.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class SynchronizedDisplay extends Display {
	private static final long serialVersionUID = 2750284989794148709L;

	public SynchronizedDisplay(Buffer buffer, SoftFont font, int columns, int rows, boolean blink) {
		super(buffer, font, columns, rows, blink);
	}	

	@Override
	public void extentsChanged(Buffer source, int x, int y, int width, int height) {
		synchronized(buffer) {
			super.extentsChanged(source, x, y, width, height);
		}
	}

	@Override
	public void contentChanged(Buffer source, int x, int y, int width, int height) {
		synchronized(buffer) {
			super.contentChanged(source, x, y, width, height);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		synchronized(buffer) {
			super.paintComponent(g);
		}
	}

	@Override
	public void getBufferCoordinates(int x, int y, Point result) {
		synchronized(buffer) {
			super.getBufferCoordinates(x, y, result);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		synchronized(buffer) {
			return super.getPreferredSize();
		}
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		synchronized(buffer) {
			return super.getPreferredScrollableViewportSize();
		}
	}

	@Override
	public Point getPreferredViewPosition(Rectangle currentViewport, Rectangle preferredSize) {
		synchronized(buffer) {
			return super.getPreferredViewPosition(currentViewport, preferredSize);
		}
	}
}
