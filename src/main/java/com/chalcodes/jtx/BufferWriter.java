package com.chalcodes.jtx;

/**
 *  An interface for classes that can write character sequences to a buffer.
 *  Implementations may have different line wrap behavior, etc. 
 */
public interface BufferWriter {
	public void write(int column, int row, char[] chars, int off, int len, int attr);
	public void write(int column, int row, CharSequence seq, int off, int len, int attr);
	public void write(int column, int row, CharSequence seq, int attr);
}
