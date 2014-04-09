package com.chalcodes.jtx;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A circular buffer that behaves like a typical terminal scrollback buffer.
 * The number of columns is fixed, and the number of lines is initially zero.
 * Content can be added until the maximum number of lines is reached, at which
 * point the oldest content starts scrolling off the top of the buffer.
 * <p>
 * Before the buffer reaches its maximum capacity, the extents position is
 * (0, 0) and the height increases as content is added.  When the buffer
 * reaches capacity, the extents height stops changing and the y position
 * begins increasing.  Thus, any given row is addressable using the same row
 * number for as long as it remains in the buffer.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class ScrollbackBuffer implements Buffer {
	protected final int[][] values;
	protected final Rectangle extents;
	protected final List<BufferObserver> observers = new ArrayList<BufferObserver>();
	
	/**
	 * Creates a new <tt>ScrollbackBuffer</tt>.  The number of columns is
	 * fixed.  The number of rows is initially zero, and may increase to the
	 * specified number of rows before the content begins scrolling.
	 * 
	 * @param columns 
	 * @param rows
	 */
	public ScrollbackBuffer(int columns, int rows) {
		values = new int[rows][columns];
		extents = new Rectangle(0, 0, columns, 0);
		// extents.y is the head...
	}
	
	@Override
	public int getContent(int column, int row) {
		if(row < extents.y || row > extents.y + extents.height) {
			throw new IndexOutOfBoundsException(String.format("col %d, row %d, extents.y %d", column, row, extents.y));
		}
		return values[row % values.length][column];
	}

	@Override
	public void getContent(int column, int row, int len, int[] result) {
		if(row < extents.y || row > extents.y + extents.height) throw new IndexOutOfBoundsException();
		System.arraycopy(values[row % values.length], column, result, 0, len);
	}

	@Override
	public int[] getContent(int column, int row, int len) {
		int[] result = new int[len];
		getContent(column, row, len, result);
		return result;
	}

	@Override
	public void getContent(int column, int row, int width, int height, int[][] result) {
		for(int r = 0; r < height; ++r) {
			System.arraycopy(this.values[row + r], column, result[r], 0, width);
		}
	}

	@Override
	public int[][] getContent(int column, int row, int width, int height) {
		int[][] result = new int[height][width];
		getContent(column, row, width, height, result);
		return result;
	}

	@Override
	public void setContent(int column, int row, int value) {
		if(row < extents.y) throw new IndexOutOfBoundsException();
		extend(0, row);
		values[row % values.length][column] = value;
		fireContentChanged(column, row, 1, 1);	
		
	}

	@Override
	public void setContent(int column, int row, int[] values, int off, int len) {
		if(off < 0 || len < 0 || off + len > values.length) throw new IllegalArgumentException();
		extend(0, row);
		if(column < 0) {
			len += column;
			off -= column;
			column = 0;
		}
		if(column + len > this.values[0].length) {
			len -= column + len - this.values[0].length;
		}
		if(len <= 0) return;
		System.arraycopy(values, off, this.values[row % this.values.length], column, len);
		fireContentChanged(column, row, len, 1);
	}

	@Override
	public void setContent(int column, int row, int[][] values, int width, int height) {
		if(values.length < height || !extents.contains(column, row, width, height)) {
			throw new IndexOutOfBoundsException();
		}
		for(int i = 0; i < values.length; ++i) {
			if(values[i].length < width) {
				throw new IndexOutOfBoundsException();
			}
		}
		for(int r = 0; r < height; ++r) {
			System.arraycopy(values[r], 0, this.values[row + r], column, width);
		}
		fireContentChanged(column, row, width, height);
	}

	@Override
	public Rectangle getExtents() {
		return new Rectangle(extents);
	}

	@Override
	public boolean contains(int column, int row) {
		return extents.contains(column, row);
	}

	/**
	 * Adds the specified observer to the end of the observer list.
	 */
	@Override
	public void addBufferObserver(BufferObserver observer) {
		if(observer == null) throw new NullPointerException();		
		observers.add(observer);
	}

	/**
	 * Removes the first occurrence of the specified observer from the
	 * observer list.
	 */
	@Override
	public boolean removeBufferObserver(BufferObserver observer) {
		return observers.remove(observer);
	}
	
	/**
	 * Scrolls the buffer extents, if necessary, to include the specified row.
	 * Rows added to the bottom of the buffer are filled with a value
	 * representing the null/zero character and the default VGA attributes.
	 * <p>
	 * The buffer can only be scrolled forward.  Attempting to scroll backward
	 * (i.e., to a row less than <tt>getExtents().y</tt>) will result in a
	 * <tt>IndexOutOfBoundsException</tt>.
	 */
	@Override
	public void extend(int column, int row) {
		if(row < extents.y) throw new IndexOutOfBoundsException();
		int tail = extents.y + extents.height;		
		if(row < tail) return;
		int newRows = row + 1 - tail; // +1 because row is inclusive, tail is exclusive
		
		if(tail >= values.length) { // buffer is full and scrolling
			int clear = Math.min(newRows, values.length);
			for(int i = tail; i < tail + clear; ++i) {
				Arrays.fill(values[i % values.length], VgaBufferElement.DEFAULT_VALUE);				
			}
			extents.y += newRows;
			fireExtentsChanged(extents.x, extents.y, extents.width, extents.height);
		}
		else if(row < values.length) { // buffer is not full, and this will not make it scroll
			extents.height += newRows;
			fireExtentsChanged(extents.x, extents.y, extents.width, extents.height);
		}
		else {
			// split into two recursive calls that match the above conditions
			extend(0, values.length - 1);
			extend(0, row);
		}
	}

	void fireContentChanged(int column, int row, int width, int height) {
		int size = observers.size();
		for(int i = 0; i < size; ++i) {
			observers.get(i).contentChanged(this, column, row, width, height);
		}
	}	
	
	void fireExtentsChanged(int column, int row, int width, int height) {
		int size = observers.size();
		for(int i = 0; i < size; ++i) {
			observers.get(i).extentsChanged(this, column, row, width, height);
		}
	}	

}
