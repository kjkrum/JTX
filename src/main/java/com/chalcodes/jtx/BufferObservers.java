package com.chalcodes.jtx;

import javax.swing.SwingUtilities;

public class BufferObservers {
	
	public static BufferObserver swingObserver(BufferObserver observer) {
		if(observer instanceof SwingObserver) {
			return observer;
		}
		else {
			return new SwingObserver(observer);
		}
	}

	private static class SwingObserver implements BufferObserver {
		private final BufferObserver wrapped;
		
		private SwingObserver(BufferObserver observer) {
			wrapped = observer;
		}
		
		@Override
		public void extentsChanged(final Buffer buffer, final int x, final int y, final int width, final int height) {
			if(SwingUtilities.isEventDispatchThread()) {
				wrapped.extentsChanged(buffer, x, y, width, height);
			}
			else {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						wrapped.extentsChanged(buffer, x, y, width, height);
					}
				});
			}
		}

		@Override
		public void contentChanged(final Buffer buffer, final int x, final int y, final int width, final int height) {
			if(SwingUtilities.isEventDispatchThread()) {
				wrapped.contentChanged(buffer, x, y, width, height);
			}
			else {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						wrapped.contentChanged(buffer, x, y, width, height);
					}
				});
			}			
		}		
	}
	
	public BufferObservers() { }
}
