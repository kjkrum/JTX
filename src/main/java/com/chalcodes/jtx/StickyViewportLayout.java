package com.chalcodes.jtx;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.ViewportLayout;

/**
 * The brains of a {@link StickyScrollPane}.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class StickyViewportLayout extends ViewportLayout {
	private static final long serialVersionUID = -4574299896415565272L;
	
	@Override
	public void layoutContainer(Container parent) {
	    final JViewport viewport = (JViewport) parent;
	    final Component view = viewport.getView();

	    if(view == null) {
	    	return;
	    }
	    
	    final Scrollable scrollable = (view instanceof Scrollable) ? (Scrollable) view : null;
	    final StickyScrollable sticky = (view instanceof StickyScrollable) ? (StickyScrollable) view : null;
	    
	    // the current viewport size
	    final Dimension viewportSize = viewport.getSize();
	    // don't really grok this
	    final Dimension logicalViewportSize = viewport.toViewCoordinates(viewport.getSize());
	    // the size we're going to give the view
	    final Dimension viewSize = view.getPreferredSize();
	    // the position we're going give the viewport
	    final Point viewPosition = viewport.getViewPosition();

	    // respect Scrollable properties
		// (copied from superclass)
		if(scrollable != null) {
			if(scrollable.getScrollableTracksViewportWidth()) {
				viewSize.width = viewportSize.width;
			}
			if(scrollable.getScrollableTracksViewportHeight()) {
				viewSize.height = viewportSize.height;
			}
		}
		
		// get the preferred view position
		if(sticky != null) {
			viewPosition.setLocation(sticky.getPreferredViewPosition(new Rectangle(viewPosition, viewportSize)));
		}
	    
		// only allow empty space on the right and bottom if the view is smaller than the viewport
		// (copied from superclass)
		if (scrollable == null || viewport.getParent() == null || viewport.getParent().getComponentOrientation().isLeftToRight()) {
			if ((viewPosition.x + logicalViewportSize.width) > viewSize.width) {
				viewPosition.x = Math.max(0, viewSize.width - logicalViewportSize.width);
			}
		} else {
			if (logicalViewportSize.width > viewSize.width) {
				viewPosition.x = viewSize.width - logicalViewportSize.width;
			} else {
				viewPosition.x = Math.max(0, Math.min(viewSize.width - logicalViewportSize.width, viewPosition.x));
			}
		}
	    if ((viewPosition.y + logicalViewportSize.height) > viewSize.height) {
	        viewPosition.y = Math.max(0, viewSize.height - logicalViewportSize.height);
	    }

	    // resize non-scrollable components if they are smaller than the viewport
		// (copied from superclass)
	    if (scrollable == null) {
	        if ((viewPosition.x == 0) && (viewportSize.width > viewSize.width)) {
	            viewSize.width = viewportSize.width;
	        }
	        if ((viewPosition.y == 0) && (viewportSize.height > viewSize.height)) {
	            viewSize.height = viewportSize.height;
	        }
	    }	    
	    
	    // superclass set position before size... not sure if it matters
	    viewport.setViewSize(viewSize);
	    viewport.setViewPosition(viewPosition);
	}
}
