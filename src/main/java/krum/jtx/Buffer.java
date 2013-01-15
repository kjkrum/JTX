package krum.jtx;

import java.awt.Rectangle;

/**
 * Contains characters and attributes.
 * <p>
 * Implementations may handle attempts to access column or row coordinates
 * outside the buffer extents by changing the extents, truncating or wrapping
 * the data, or throwing an <tt>IndexOutOfBoundsException</tt>. 
 *
 * @author Kevin Krumwiede (kjkrum@gmail.com)
 */
public interface Buffer {
	/**
	 * Gets the character and attribute value at the specified coordinates.
	 * 
	 * @param column
	 * @param row
	 * @return
	 */
	public abstract int getContent(int column, int row);
	
	/**
	 * Sets the character and attribute value at the specified coordinates.
	 * Buffer observers should be notified of the content change.  
	 * 
	 * @param column
	 * @param row
	 * @param value
	 */
	public abstract void setContent(int column, int row, int value);
	
	/**
	 * Sets a range of character and attribute values.  Buffer observers
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
	 * Gets the character cell extents of the buffer.
	 */
	public abstract Rectangle getExtents();
	
	/**
	 * Returns true if the extents contain the specified cell coordinates. 
	 */
	public abstract boolean containsCell(int column, int row);
	
	/**
	 * Adds an observer to this buffer.  Implementations should notify
	 * observers in the order in which they were added.
	 * 
	 * @param observer
	 */
	public abstract void addBufferObserver(BufferObserver observer);
	
	/**
	 * Removes the first occurrence of the specified observer from this
	 * buffer.
	 * 
	 * @param observer
	 */
	public abstract void removeBufferObserver(BufferObserver observer);
}