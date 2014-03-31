package com.chalcodes.jtx;

import java.awt.Rectangle;

/**
 * A synchronized wrapper around a buffer.  Synchronizes on itself.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class SynchronizedBuffer implements Buffer {
	private final Buffer wrapped;
	
	public SynchronizedBuffer(Buffer wrapped) {
		if(wrapped == null) {
			throw new NullPointerException();
		}
		this.wrapped = wrapped;
	}

	@Override
	synchronized public int getContent(int column, int row) {
		return wrapped.getContent(column, row);
	}

	@Override
	synchronized public void getContent(int column, int row, int len, int[] result) {
		wrapped.getContent(column, row, len, result);
	}

	@Override
	synchronized public int[] getContent(int column, int row, int len) {
		return wrapped.getContent(column, row, len);
	}

	@Override
	synchronized public void getContent(int column, int row, int width, int height, int[][] result) {
		wrapped.getContent(column, row, width, height, result);
	}

	@Override
	synchronized public int[][] getContent(int column, int row, int width, int height) {
		return wrapped.getContent(column, row, width, height);
	}

	@Override
	synchronized public void setContent(int column, int row, int value) {
		wrapped.setContent(column, row, value);
	}

	@Override
	synchronized public void setContent(int column, int row, int[] values, int off, int len) {
		wrapped.setContent(column, row, values, off, len);
	}

	@Override
	synchronized public void setContent(int column, int row, int[][] values, int width, int height) {
		wrapped.setContent(column, row, values, width, height);
	}

	@Override
	synchronized public Rectangle getExtents() {
		return wrapped.getExtents();
	}

	@Override
	synchronized public boolean contains(int column, int row) {
		return wrapped.contains(column, row);
	}

	@Override
	synchronized public void addBufferObserver(BufferObserver observer) {
		wrapped.addBufferObserver(observer);
	}

	@Override
	synchronized public boolean removeBufferObserver(BufferObserver observer) {
		return wrapped.removeBufferObserver(observer);
	}
}
