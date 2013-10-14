package krum.jtx;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.ViewportLayout;

/**
 * A scroll pane viewport layout manager that keeps its viewport scrolled to
 * the bottom and right edge of its client component if it was positioned at
 * those edges before the component's size increased.
 * <p>
 * The common way of achieving this is by queueing a <tt>Runnable</tt> task to
 * call {@link JComponent#scrollRectToVisible(Rectangle)} in the Swing event
 * thread.  But this approach often causes the scroll bars to be painted at
 * the original viewport position before the visible rectangle is adjusted,
 * then painted again at the new position, resulting in "bouncing" scroll bar
 * knobs.  <tt>StickyViewportLayout</tt> adjusts the viewport position while
 * laying out the scroll pane, so the scroll bars are painted only once and
 * the knobs do not bounce.
 * <p>
 * Additionally, if the client component implements {@link StickyScrollable},
 * the layout will honor the client's preferred scroll offset when it is not
 * stuck to the bottom or right edge.
 * 
 * @author Kevin Krumwiede (kjkrum@gmail.com)
 */
public class StickyViewportLayout extends ViewportLayout {
	private static final long serialVersionUID = 1L;
	
	protected final Dimension oldSize = new Dimension();
	protected final Dimension newSize = new Dimension();
	//protected Rectangle visible = new Rectangle();
	protected final Rectangle oldVisible = new Rectangle();
	protected Rectangle newVisible = new Rectangle();
	protected Point offset = new Point();

	@Override
	public void layoutContainer(Container owner) {
		super.layoutContainer(owner);
		JViewport viewport = (JViewport) owner;
		Component view = viewport.getView();
		if(view instanceof JComponent) {
			((JComponent)view).computeVisibleRect(newVisible);
		}
		else newVisible = viewport.getViewRect();
		view.getSize(newSize);
		boolean doScroll = false;
		boolean stickyClient = false;
		
		if(view instanceof StickyScrollable) {
			((StickyScrollable) view).getPreferredScrollOffset(offset);
			stickyClient = true;
		}

		// y axis - scroll to the bottom if:
		// the viewport was previously positioned at the bottom, and the client height changed; or
		// the viewport was previously positioned at the bottom, and the viewport height changed; or
		// the client height was smaller than the viewport height, and is now larger
		if((oldVisible.y + oldVisible.height == oldSize.height
				&& (oldSize.height != newSize.height || oldVisible.height != newVisible.height))
				|| (newSize.height > newVisible.height && oldSize.height <= oldVisible.height)) {
			newVisible.y = newSize.height - newVisible.height;
			doScroll = true;			
		}
		// else honor requested offset
		else if(stickyClient && offset.y != 0) {
			newVisible.y += offset.y;
			if(newVisible.y < 0) newVisible.y = 0;
			doScroll = true;
		}
		
		// x axis - honor requested offset
		if(stickyClient && offset.x != 0) {
			newVisible.x += offset.x;
			if(newVisible.x < 0) newVisible.x = 0;
			doScroll = true;
		}
		
		if(doScroll) viewport.setViewPosition(newVisible.getLocation());
		oldSize.setSize(newSize);
		oldVisible.setBounds(newVisible);
	}
}
