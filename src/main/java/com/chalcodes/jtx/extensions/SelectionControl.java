package com.chalcodes.jtx.extensions;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeSupport;

import javax.swing.JViewport;

import com.chalcodes.jtx.Buffer;
import com.chalcodes.jtx.Display;
import com.chalcodes.jtx.VgaBufferElement;

/**
 * A selection control for a {@link Display} attached to a {@link Buffer}
 * containing elements encoded with {@link VgaBufferElement}.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class SelectionControl {
	public static final String PROPERTY_SELECTION_ACTIVE = "com.chalcodes.jtx.PROPERTY_SELECTION_ACTIVE";
	private final Display display;
	private final Buffer buffer;
	private final JViewport viewport;
	private final PropertyChangeSupport propertyChangeSupport;
	/** Buffer coordinates where button was pressed; may be outside buffer extents */
	private final Point anchor = new Point();
	/** Buffer coordinates where pointer was dragged; may be outside buffer extents */
	private final Point handle = new Point();
	/** The previously calculated selection */
	private Rectangle activeSelection;

	/**
	 * Sets up a new selection control.  The control adds listeners to the
	 * viewport and its client, which must be a {link Display}.  This is
	 * because the client may be smaller than the viewport, and we want to
	 * respond to click-drags into the client from anywhere in the viewport.
	 * @param viewport the viewport
	 *  
	 * @throws IllegalArgumentException if the viewport's client is not a
	 * {@link Display}
	 */
	public SelectionControl(PropertyChangeSupport propertyChangeSupport, JViewport viewport) {
		if(viewport.getView() instanceof Display) {
			display = (Display) viewport.getView();
			buffer = display.getBuffer();				
		}
		else {
			throw new IllegalArgumentException("viewport's client is not a Display");
		}
		this.viewport = viewport;
		this.propertyChangeSupport = propertyChangeSupport;
		final MouseAdapter mouseAdapter = new SelectionMouseAdapter();
		viewport.addMouseListener(mouseAdapter);
		viewport.addMouseMotionListener(mouseAdapter);
		display.addMouseListener(mouseAdapter);
		display.addMouseMotionListener(mouseAdapter);
	}

	/**
	 * Copies the selected content from the buffer.  Returns an empty array if
	 * there is no active selection or the selection does not intersect with
	 * the buffer extents.
	 * 
	 * @return the selected buffer content
	 */
	public int[][] copySelection() {
		if(activeSelection == null) {
			return new int[0][0];
		}
		else {
			Rectangle inter = activeSelection.intersection(buffer.getExtents());
			return buffer.getContent(inter.x, inter.y, inter.width, inter.height);
		}
	}
	
	/**
	 * Clears the active selection.
	 */
	public void clearSelection() {
		if(activeSelection != null) {
			setSelection(activeSelection, false);
			activeSelection = null;
			propertyChangeSupport.firePropertyChange(PROPERTY_SELECTION_ACTIVE, true, false);
		}
	}
	
	private class SelectionMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON1) {
				clearSelection();
				Point p = e.getPoint();
				if(e.getComponent() == viewport) {
					transformToClientCoords(p);
				}
				display.getBufferCoordinates(p, anchor);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
				Point p = e.getPoint();
				if(e.getComponent() == viewport) {
					transformToClientCoords(p);
				}
				display.getBufferCoordinates(p, handle);
				if(activeSelection == null) {
					activeSelection = createRectangle(anchor, handle);
					setSelection(activeSelection, true);
					propertyChangeSupport.firePropertyChange(PROPERTY_SELECTION_ACTIVE, false, true);
				}
				else {
					Rectangle newSelection = createRectangle(anchor, handle);
					updateSelection(activeSelection, newSelection);
					activeSelection = newSelection;
				}
			}
		}
	}
	
	/**
	 * Transforms a point from the viewport's coordinate space into the
	 * client's coordinate space.
	 * 
	 * @param point
	 * @param result
	 */
	private void transformToClientCoords(Point point) {
		Point p = viewport.getViewPosition();
		point.x += p.x;
		point.y += p.y;
	}
	
	/**
	 * Use this to clear the old selection or create a new one.
	 * 
	 * @param selection
	 * @param selected
	 */
	private void setSelection(Rectangle selection, boolean selected) {
		Rectangle union = selection.intersection(buffer.getExtents());
		if(!union.isEmpty()) {
			int[][] copied = buffer.getContent(union.x, union.y, union.width, union.height);
			for(int r = 0; r < copied.length; ++r) {
				for(int c = 0; c < copied[r].length; ++c) {
					copied[r][c] = VgaBufferElement.setSelected(copied[r][c], selected);
				}
			}
			buffer.setContent(union.x, union.y, copied, union.width, union.height);
		}
	}
	
	/**
	 * Use this when selection already exists during drag.
	 * 
	 * @param oldSelection the current selection
	 * @param newSelection the new selection
	 */
	private void updateSelection(Rectangle oldSelection, Rectangle newSelection) {
		if(!oldSelection.equals(newSelection)) {
			Rectangle union = oldSelection.union(newSelection).intersection(buffer.getExtents());
			if(!union.isEmpty()) {
				int[][] copied = buffer.getContent(union.x, union.y, union.width, union.height);
				for(int r = 0; r < copied.length; ++r) {
					for(int c = 0; c < copied[r].length; ++c) {
						if(oldSelection.contains(union.x + c, union.y + r) && !newSelection.contains(union.x + c, union.y + r)) {
							copied[r][c] = VgaBufferElement.setSelected(copied[r][c], false);
						}
						else if(newSelection.contains(union.x + c, union.y + r) && !oldSelection.contains(union.x + c, union.y + r)) {
							copied[r][c] = VgaBufferElement.setSelected(copied[r][c], true);
						}
					}
				}
				buffer.setContent(union.x, union.y, copied, union.width, union.height);
			}
		}
	}
	
	/**
	 * Creates the smallest rectangle containing both of the specified points.
	 * The rectangle will be arranged so its width and height are positive.
	 * 
	 * @param p0 the first point
	 * @param p1 the second point
	 * @return the rectangle
	 */
	private Rectangle createRectangle(Point p0, Point p1) {
		Rectangle rect = new Rectangle(p0.x, p0.y, p1.x - p0.x, p1.y - p0.y);
		if(rect.width < 0) {
			rect.x += rect.width;
			rect.width = -rect.width;			
		}
		if(rect.height < 0) {
			rect.y += rect.height;
			rect.height = -rect.height;
		}
		++rect.width;
		++rect.height;
		return rect;
	}
}
