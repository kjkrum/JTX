package com.chalcodes.jtx.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.chalcodes.jtx.Buffer;
import com.chalcodes.jtx.Display;
import com.chalcodes.jtx.ScrollbackBuffer;
import com.chalcodes.jtx.SoftFont;
import com.chalcodes.jtx.StickyScrollPane;
import com.chalcodes.jtx.SynchronizedBuffer;
import com.chalcodes.jtx.SynchronizedDisplay;
import com.chalcodes.jtx.VgaSoftFont;


public class BasicDemo extends JFrame {
	private static final long serialVersionUID = 1L;
	
	protected final Buffer buffer;
	protected final SoftFont font;
	protected final Display display;
	
	public BasicDemo(int bufferLines, boolean sync) throws IOException {
		setTitle("JTX Demo");
		font = new VgaSoftFont();
		if(sync) {
			buffer = new SynchronizedBuffer(new ScrollbackBuffer(80, bufferLines));
			synchronized(buffer) {
				display = new SynchronizedDisplay(buffer, font, 80, 25, true);
			}
		}
		else {
			buffer = new ScrollbackBuffer(80, bufferLines);
			display = new Display(buffer, font, 80, 25, true);			
		}
		
		// put the display in a scroll pane and add it to the frame
		JScrollPane scrollPane = new StickyScrollPane(display);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getViewport().setBackground(Color.BLACK);
		add(scrollPane, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new BasicDemo(1000, false).setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
