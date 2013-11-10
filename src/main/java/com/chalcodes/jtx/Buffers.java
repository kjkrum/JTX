package com.chalcodes.jtx;

import java.awt.Rectangle;

public class Buffers {
	
	/**
	 * Returns a synchronized wrapper around a buffer.  If the buffer is
	 * already wrapped, it is returned unchanged.
	 * 
	 * @param buffer the buffer to wrap
	 * @return the wrapped buffer
	 */
	public static Buffer synchronizedBuffer(Buffer buffer) {
		if(buffer instanceof SynchronizedBuffer) {
			return buffer;
		}
		else {
			return new SynchronizedBuffer(buffer);
		}
	}
	
	private static class SynchronizedBuffer implements Buffer {
		private final Buffer wrapped;
		
		SynchronizedBuffer(Buffer buffer) {
			if(buffer == null) throw new NullPointerException();
			wrapped = buffer;
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

	private Buffers() { }
}
