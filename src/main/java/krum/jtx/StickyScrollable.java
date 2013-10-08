package krum.jtx;

import java.awt.Point;

import javax.swing.Scrollable;

/**
 * Allows a <tt>StickyScrollPane</tt> to determine its client's preferred
 * scroll position.
 * 
 * @author Kevin Krumwiede (kjkrum@gmail.com)
 * @see StickyViewportLayout
 */
public interface StickyScrollable extends Scrollable {
	/**
	 * Gets the offset by which this component's visible rectangle should be
	 * scrolled.  As a side effect, this method should reset the component's
	 * preferred scroll offset. 
	 */
	public Point getPreferredScrollOffset();
	
	/**
	 * Gets the offset by which this component's visible rectangle should be
	 * scrolled, storing the result in the specified <tt>Point</tt>.  As a
	 * side effect, this method should reset the component's preferred scroll
	 * offset. 
	 */
	public void getPreferredScrollOffset(Point offset);
}
