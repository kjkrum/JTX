package krum.jtx.extensions;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import krum.jtx.Buffer;
import krum.jtx.BufferObserver;
import krum.jtx.SwingDisplay;
import krum.jtx.SwingScrollbackBuffer;

public class SelectionControl {
	public static final int MODE_BLOCK = 0;
	public static final int MODE_LINE = 1;
	
	private final SwingDisplay display;
	private final SwingScrollbackBuffer buffer;
	// buffer coordinates where the mouse was clicked to begin the selection
	private final Point origin = new Point();
	// buffer coordinates where the mouse was last dragged (may be outside the buffer extents)
	private final Point limit = new Point();
	// true if a selection is active (selected bits are on in the buffer)
	private boolean active = false;
	// selection mode in use (block or line)
	private int mode;

	public SelectionControl(SwingDisplay display, SwingScrollbackBuffer buffer) {
		this.display = display;
		this.buffer = buffer;
		final MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == 0) {
					active = false;
					SelectionControl.this.display.getBufferCoordinates(e.getPoint(), origin);
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO recalculate selection; enable context menu items
			}
		};
		display.addMouseListener(mouseAdapter);
		display.addMouseMotionListener(mouseAdapter);
		final BufferObserver bufferObserver = new BufferObserver() {
			@Override
			public void extentsChanged(Buffer buffer, int x, int y, int width, int height) {
				// TODO recalculate selection
			}

			@Override
			public void contentChanged(Buffer buffer, int x, int y, int width, int height) {
				// TODO recalculate selection
			}
		};
		buffer.addBufferObserver(bufferObserver);
	}
	
	// TODO method that takes buffer coordinates and calculates effective selection limit
	// (probably works differently depending on selection mode)
}
