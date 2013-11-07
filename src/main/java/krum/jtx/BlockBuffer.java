package krum.jtx;

/**
 * An extension of the {@link Buffer} interface that supports copying
 * two-dimensional regions in and out of the buffer.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public interface BlockBuffer extends Buffer {

	public abstract void getContent(int column, int row, int len, int[] result);
	
	public abstract int[] getContent(int column, int row, int len);
	
	public abstract void getContent(int column, int row, int width, int height, int[][] result);

	public abstract int[][] getContent(int column, int row, int width, int height);
	
	/**
	 * Sets a rectangular range of character and attribute values.  Buffer
	 * observers should be notified only once of the content change.
	 * 
	 * @param column the destination column
	 * @param row the destination row
	 * @param values the values to copy, indexed as [row][column]
	 * @param srcColumn the column within <tt>values</tt> to copy to <tt>column</tt>
	 * @param srcRow the row within <tt>values</tt> to copy to <tt>row</tt>
	 * @param width the width of the region to be copied
	 * @param height the height of the region to be copied
	 */
	public abstract void setContent(int column, int row, int[][] values, int srcColumn, int srcRow, int width, int height);
}
