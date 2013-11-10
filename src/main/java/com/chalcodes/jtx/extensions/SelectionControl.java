package com.chalcodes.jtx.extensions;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
	private final Display display;
	private final Buffer buffer;
	private final JViewport viewport;
	/** Buffer coordinates of the selection anchor; may be outside buffer extents */
	private final Point anchor = new Point();
	/** Buffer coordinates of the selection drag handle; may be outside buffer extents */
	private final Point handle = new Point();
	/** The previously calculated selection */
	private final Rectangle oldSelection = new Rectangle();

	/**
	 * Sets up a new selection control.  The control adds listeners to the
	 * viewport and its client, which must be a {link Display}.  This is
	 * because the client may be smaller than the viewport, and we want to
	 * respond to click-drags into the client from anywhere in the viewport.
	 *  
	 * @param viewport the viewport
	 * @throws IllegalArgumentException if the viewport's client is not a
	 * {@link Display}
	 */
	public SelectionControl(JViewport viewport) {
		if(viewport.getView() instanceof Display) {
			display = (Display) viewport.getView();
			buffer = display.getBuffer();				
		}
		else {
			throw new IllegalArgumentException("viewport's client is not a Display");
		}
		this.viewport = viewport;
		final MouseAdapter mouseAdapter = new SelectionMouseAdapter();
		viewport.addMouseListener(mouseAdapter);
		viewport.addMouseMotionListener(mouseAdapter);
		display.addMouseListener(mouseAdapter);
		display.addMouseMotionListener(mouseAdapter);
	}
	
	private class SelectionMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON1) {
				if(!oldSelection.isEmpty()) {
					Rectangle newSelection = new Rectangle();
					replaceSelection(oldSelection, newSelection);
				}
				Point p = e.getPoint();
				if(e.getComponent() == viewport) {
					transformToClientCoords(p);
				}
				display.getBufferCoordinates(p, anchor);
				System.out.println("pressed at " + anchor);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			Point p = e.getPoint();
			if(e.getComponent() == viewport) {
				transformToClientCoords(p);
			}
			display.getBufferCoordinates(p, handle);
			Rectangle newSelection = createRectangle(anchor, handle);
			replaceSelection(oldSelection, newSelection);		
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
		p.x += point.x;
		p.y += point.y;
	}
	
	/**
	 * Redraws the selection and sets the bounds of <tt>oldSelection</tt> to
	 * those of <tt>newSelection</tt>.
	 * 
	 * @param oldSelection
	 * @param newSelection
	 */
	private void replaceSelection(Rectangle oldSelection, Rectangle newSelection) {
		if(!oldSelection.equals(newSelection)) {
			System.out.println("selecting " + newSelection);
			Rectangle union = oldSelection.union(newSelection).intersection(buffer.getExtents());
			if(!union.isEmpty()) {
				int[][] copied = buffer.getContent(union.x, union.y, union.width, union.height);
				for(int row = 0; row < copied.length; ++row) {
					for(int col = 0; col < copied[row].length; ++col) {
						if(oldSelection.contains(col, row) && !newSelection.contains(col, row)) {
							copied[row][col] = VgaBufferElement.setSelected(copied[row][col], false); 
						}
						else if(newSelection.contains(col, row) && !oldSelection.contains(col, row)) {
							copied[row][col] = VgaBufferElement.setSelected(copied[row][col], true); 
						}
					}
				}
				System.out.println("updating " + new Rectangle(union.x, union.y, union.width, union.height));
				buffer.setContent(union.x, union.y, copied, 0, 0, union.width, union.height);
			}
			oldSelection.setBounds(newSelection);
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
