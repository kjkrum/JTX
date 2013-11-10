package com.chalcodes.jtx;

import java.awt.Rectangle;

// TODO change the parameter order of setters to source, destination?

/**
 * Conceptually encapsulates a two-dimensional array of integers, typically
 * encoding characters and attributes.  Implementations may handle attempts to
 * access coordinates outside the buffer extents by changing the extents,
 * truncating or wrapping the data, or throwing an
 * <tt>IndexOutOfBoundsException</tt>.
 *
 * @author Kevin Krumwiede (kjkrum@gmail.com)
 */
public interface Buffer {
	/**
	 * Gets the value at the specified coordinates.
	 * 
	 * @param column
	 * @param row
	 * @return
	 */
	public abstract int getContent(int column, int row);
	
	/**
	 * Copies a one-dimensional array of values from a row, storing them in
	 * <tt>result</tt>.
	 * 
	 * @param column
	 * @param row
	 * @param len
	 * @param result
	 */
	public abstract void getContent(int column, int row, int len, int[] result);
	
	/**
	 * Copies a one-dimensional array of values from a row.
	 * 
	 * @param column
	 * @param row
	 * @param len
	 * @return
	 */
	public abstract int[] getContent(int column, int row, int len);
	
	/**
	 * Copies a two-dimensional array of values from a rectangular region,
	 * storing them in <tt>result</tt>.
	 *  
	 * @param column
	 * @param row
	 * @param width
	 * @param height
	 * @param result
	 */
	public abstract void getContent(int column, int row, int width, int height, int[][] result);

	/**
	 * Copies a two-dimensional array of values from a rectangular region.
	 * 
	 * @param column
	 * @param row
	 * @param width
	 * @param height
	 * @return
	 */
	public abstract int[][] getContent(int column, int row, int width, int height);
	
	/**
	 * Sets the value at the specified coordinates.  Buffer observers should
	 * be notified of the content change.  
	 * 
	 * @param column
	 * @param row
	 * @param value
	 */
	public abstract void setContent(int column, int row, int value);
	
	/**
	 * Copies a one-dimensional array of values into a row.  Buffer observers
	 * should be notified only once of the content change.
	 * 
	 * @param column
	 * @param row
	 * @param values
	 * @param off
	 * @param len
	 */
	public abstract void setContent(int column, int row, int[] values, int off, int len);

	/**
	 * Copies a two-dimensional array of values into a rectangular region.
	 * Buffer observers should be notified only once of the content change.
	 * 
	 * @param column the destination column
	 * @param row the destination row
	 * @param values the values to copy, indexed as [row][column]
	 * @param width the width of the region to be copied
	 * @param height the height of the region to be copied
	 */
	public abstract void setContent(int column, int row, int[][] values, int width, int height);

	/**
	 * Gets the buffer extents.
	 */
	public abstract Rectangle getExtents();
	
	/**
	 * Returns true if the buffer extents contain the specified coordinates. 
	 */
	public abstract boolean contains(int column, int row);
	
	/**
	 * Adds an observer to this buffer.  Implementations should notify
	 * observers in the order in which they were added.
	 * 
	 * @param observer the observer to add
	 */
	public abstract void addBufferObserver(BufferObserver observer);
	
	/**
	 * Removes the first occurrence of the specified observer from this
	 * buffer.
	 * 
	 * @param observer the observer to remove
	 * @return true if the collection of observers changed; otherwise false
	 */
	public abstract boolean removeBufferObserver(BufferObserver observer);
}