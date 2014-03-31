package com.chalcodes.jtx;

import java.awt.Point;

import javax.swing.Scrollable;

/**
 * Allows a component to provide its preferred scroll offset to a
 * {@link StickyScrollPane}.
 * 
 * @author Kevin Krumwiede (kjkrum@gmail.com)
 */
public interface StickyScrollable extends Scrollable {
	/**
	 * Gets the offset by which this component's visible rectangle should be
	 * scrolled, storing the result in <tt>offset</tt>.  As a side effect,
	 * this method should reset the component's preferred scroll offset.
	 * 
	 * @param offset the preferred offset relative to the current position
	 */
	public void getPreferredScrollOffset(Point offset);
}
