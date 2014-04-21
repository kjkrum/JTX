package com.chalcodes.jtx.demo;

import java.io.IOException;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.chalcodes.jtx.VgaBufferElement;

/**
 * A demo that spawns a thread to write to the Buffer.
 */
public class SyncTest extends BasicDemo {
	private static final long serialVersionUID = 1L;
	private static final int BUFFER_LINES = 1000;

	public SyncTest() throws IOException {
		super(BUFFER_LINES, true);
		setTitle("JTX Synchronized Scrolling Test");
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					final SyncTest speedTest = new SyncTest();
					speedTest.setVisible(true);
					
					JOptionPane.showMessageDialog(
							speedTest,
							"Click to begin.",
							"Sync Test",
							JOptionPane.PLAIN_MESSAGE
						);
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							// set up
							final int lines = 1000000;
							final Random rand = new Random();
							final String msg = "All work and no play makes Jack a dull boy.";
							final int[] content = new int[msg.length()];
							for(int i = 0; i < content.length; ++i) {
								content[i] = VgaBufferElement.setCharacter(0, msg.charAt(i));
							}
							
							// main loop
							final long begin = System.currentTimeMillis();
							for(int i = 0; i < lines; ++i) {
//								if(i == BUFFER_LINES) {
//									System.out.println("buffer scrolling");
//								}
								// get a random color attribute
								int attr = rand.nextInt(0x7F) << 16;
								// ensure foreground and background aren't the same color
								if(!VgaBufferElement.isBright(attr) && VgaBufferElement.getForegroundColor(attr) == VgaBufferElement.getBackgroundColor(attr)) {
									// invert the foreground color if they are
									attr ^= 0x70000;
								}
								for(int j = 0; j < content.length; ++j) {
									content[j] = VgaBufferElement.setColor(content[j], attr);
								}
								speedTest.buffer.setContent(0, i, content, 0, content.length);
								
								// uncomment to experiment with scrolling behavior
//								try {
//									Thread.sleep(1);
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
							}
							final long end = System.currentTimeMillis();
							
							// show report
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									JOptionPane.showMessageDialog(
											speedTest,
											"Wrote " + lines + " lines in " + (end - begin) + " ms.",
											"Speed Test",
											JOptionPane.INFORMATION_MESSAGE
											);
								}
							});
						}						
					}).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
