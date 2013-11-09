package com.chalcodes.jtx.extensions;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JViewport;

import com.chalcodes.jtx.Buffer;
import com.chalcodes.jtx.BufferObserver;
import com.chalcodes.jtx.Display;
import com.chalcodes.jtx.ScrollbackBuffer;


public class SelectionControl {
	private final Display display;
	private final ScrollbackBuffer buffer;
	private final JViewport viewport;
	// buffer coordinates where the mouse was clicked to begin the selection
	private final Point origin = new Point();
	// buffer coordinates where the mouse was last dragged (may be outside the buffer extents)
	private final Point limit = new Point();
	// true if a selection is active (i.e., selected bits are on in the buffer)
	private boolean active = false;

	public SelectionControl(Display display, ScrollbackBuffer buffer, JViewport viewport) {
		this.display = display;
		this.buffer = buffer;
		this.viewport = viewport;
		final MouseAdapter mouseAdapter = new SelectionMouseAdapter();
		viewport.addMouseListener(mouseAdapter);
		viewport.addMouseMotionListener(mouseAdapter);
		// TODO add change listener to viewport
		final BufferObserver bufferObserver = new SelectionBufferObserver();
		buffer.addBufferObserver(bufferObserver);
	}
	
	private class SelectionMouseAdapter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getButton() == 0) {
				active = false;
				// offset by viewport position
				Point click = e.getPoint();
				Point offset = SelectionControl.this.viewport.getViewPosition();
				click.x += offset.x;
				click.y += offset.y;
				SelectionControl.this.display.getBufferCoordinates(click, origin);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO recalculate selection
		}
	}
	
	private class SelectionBufferObserver implements BufferObserver {
		@Override
		public void extentsChanged(Buffer buffer, int x, int y, int width, int height) {
			// TODO recalculate selection
		}

		@Override
		public void contentChanged(Buffer buffer, int x, int y, int width, int height) {
			// TODO recalculate selection
		}
	}
	
	// TODO method that takes buffer coordinates and calculates effective selection limit
	// (probably works differently depending on selection mode)
}
