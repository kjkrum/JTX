package com.chalcodes.jtx;

/**
 *  An interface for classes that write character sequences to a buffer.  The 
 *  {@link XXXScrollbackBuffer} class already has its own methods for this.  This
 *  interface is intended to allow {@link Buffer}-independent implementations
 *  with different line wrap behaviors, etc. 
 */
public interface BufferWriter {
	public void write(int column, int row, char[] chars, int off, int len, int attr);
	public void write(int column, int row, CharSequence seq, int off, int len, int attr);
	public void write(int column, int row, CharSequence seq, int attr);
}
