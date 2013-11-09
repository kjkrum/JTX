package com.chalcodes.jtx.demo;

import java.io.IOException;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.chalcodes.jtx.VgaBufferElement;


/**
 * A demo that spawns a thread to write to the Buffer, simulating network
 * activity or a large file read.
 * <p>
 * As you'll notice, you don't actually see every line scroll past.  That's
 * because the Display repaints asynchronously, only updating as often as your
 * graphics hardware and the Swing event dispatch thread can handle.  This is
 * how all proper Swing components work.  The advantage is that changes to a
 * component's state are not significantly impeded by repainting, which is
 * very slow by comparison.
 * <p>
 * The curious thing about this benchmark is that the longer it runs, the
 * faster it goes.  Writing 10,000 lines takes a little under three seconds on
 * my system, but writing 100,000 lines takes only four and a half seconds,
 * and writing 1,000,000 lines takes less than ten seconds.  I think this has
 * something to do with the self-optimizing features of modern JVMs. 
 */
public class SpeedTest extends BasicDemo {
	private static final long serialVersionUID = 1L;

	public SpeedTest() throws IOException {
		setTitle("JTX Speed Test");
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					final SpeedTest speedTest = new SpeedTest();
					speedTest.setVisible(true);
					
					JOptionPane.showMessageDialog(
							speedTest,
							"Click to begin.",
							"Speed Test",
							JOptionPane.PLAIN_MESSAGE
						);
					
					Thread t = new Thread() {
						@Override
						public void run() {
							final int lines = 1000000;
							final Random rand = new Random();
							final String msg = "All work and no play makes Jack a dull boy.";
							final int[] content = new int[msg.length()];
							
							for(int i = 0; i < content.length; ++i) {
								content[i] = VgaBufferElement.setCharacter(0, msg.charAt(i));
							}
							
							final long begin = System.currentTimeMillis();
							for(int i = 0; i < lines; ++i) {
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
								/*
								try {
									Thread.sleep(25);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								*/
							}
							final long end = System.currentTimeMillis();
							
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
					};
					t.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
