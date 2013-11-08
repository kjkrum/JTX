package krum.jtx;

import static org.junit.Assert.assertEquals;

import java.awt.Rectangle;

import org.junit.Test;

public class SwingScrollbackBufferTest {

	@Test
	public void testReadWriteWithoutScroll() {
		final int cols = 10;
		final int rows = 5;
		final SwingScrollbackBuffer buffer = new SwingScrollbackBuffer(cols, rows);
		
		final Rectangle initialExtents = buffer.getExtents();		
		assertEquals("intial extents", initialExtents, new Rectangle(0, 0, cols, 0));
		
		buffer.setContent(0, 0, 'A');
		int a = buffer.getContent(0, 0);
		assertEquals("top left", a, 'A');
		
		buffer.setContent(cols - 1, rows - 1, 'B');
		int b = buffer.getContent(cols - 1, rows - 1);
		assertEquals("bottom right", b, 'B');
		
		final Rectangle finalExtents = buffer.getExtents();		
		assertEquals("final extents", finalExtents, new Rectangle(0, 0, cols, rows));
	}
	
	@Test
	public void testReadWriteWithScroll() {
		final int cols = 1;
		final int rows = 10;
		final SwingScrollbackBuffer buffer = new SwingScrollbackBuffer(cols, rows);
		
		buffer.setContent(0, 1, 'A');
		buffer.setContent(0, rows, 'B'); // this should cause scrolling
		
		int a = buffer.getContent(0, 1);
		assertEquals("head", a, 'A');
		int b = buffer.getContent(0, rows);
		assertEquals("tail", b, 'B');
		
		final Rectangle finalExtents = buffer.getExtents();		
		assertEquals("final extents", finalExtents, new Rectangle(0, 1, cols, rows));
	}
	
	@Test
	public void testRowIndexAfterScroll() {
		final int cols = 1;
		final int rows = 13;
		final SwingScrollbackBuffer buffer = new SwingScrollbackBuffer(cols, rows);
		for(int row = 0; row < 100; ++row) {
			buffer.setContent(0, row, row);
		}
		assertEquals("row indexing", 90, buffer.getContent(0, 90));
	}
	
	@Test (expected=IndexOutOfBoundsException.class)
	public void testReadBeforeHead() {
		final int cols = 1;
		final int rows = 10;
		final SwingScrollbackBuffer buffer = new SwingScrollbackBuffer(cols, rows);
		
		buffer.setContent(0, rows, 'A'); // this should cause scrolling
		buffer.getContent(0, 0); // this should throw an exception
	}
}
