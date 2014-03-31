package com.chalcodes.jtx;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.ViewportLayout;

/**
 * The brains of a {@link StickyScrollPane}.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class StickyViewportLayout extends ViewportLayout {
	private static final long serialVersionUID = -4574299896415565272L;

	protected final Point offset = new Point();
	protected Rectangle visible = new Rectangle();
	
	@Override
	public void layoutContainer(Container parent) {
		JViewport viewport = (JViewport) parent;
		Component view = viewport.getView();
		
		if(view instanceof StickyScrollable) {
			if(view instanceof JComponent) {
				((JComponent)view).computeVisibleRect(visible);
			}
			else {
				visible = viewport.getViewRect();
			}			
			((StickyScrollable) view).getPreferredScrollOffset(offset);
			if(offset.y != 0 || offset.x != 0) {
				visible.translate(offset.x, offset.y);
				if(visible.x < 0) visible.x = 0;
				if(visible.y < 0) visible.y = 0;
				viewport.setViewPosition(visible.getLocation());
			}
		}
		super.layoutContainer(parent);
	}
}
