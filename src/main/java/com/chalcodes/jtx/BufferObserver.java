package com.chalcodes.jtx;

/**
 * Receives change events from a {@link Buffer}.
 */
public interface BufferObserver {
	/**
	 * Fired when a {@link Buffer}'s extents change.
	 * 
	 * @param buffer the source of the event
	 * @param x the new x coordinate of the top left corner
	 * @param y the new y coordinate of the top left corner
	 * @param width the new width
	 * @param height the new height
	 */
	public abstract void extentsChanged(Buffer buffer, int x, int y, int width, int height);
	
	/**
	 * Fired when a {@link Buffer}'s cells are written to.
	 * 
	 * @param buffer the source of the event
	 * @param x the x coordinate of the top left corner of the changed region
	 * @param y the y coordinate of the top left corner of the changed region
	 * @param width the width of the changed region
	 * @param height the height of the changed region
	 */
	public abstract void contentChanged(Buffer buffer, int x, int y, int width, int height);
	
	// this would allow circular buffers with fixed extents to notify their
	// observers of content scrolling.  leaving it out for now...
	//public abstract void contentScrolled(Buffer buffer, int x, int y);
}
