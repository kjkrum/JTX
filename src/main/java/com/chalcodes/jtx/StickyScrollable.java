package com.chalcodes.jtx;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Scrollable;

/**
 * Allows a component to provide its preferred view position to a
 * {@link StickyScrollPane}.
 * 
 * @author Kevin Krumwiede (kjkrum@gmail.com)
 */
public interface StickyScrollable extends Scrollable {
	public Point getPreferredViewPosition(Rectangle currentViewport);
}
