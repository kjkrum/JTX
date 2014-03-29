package com.chalcodes.jtx.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.chalcodes.jtx.Display;
import com.chalcodes.jtx.ScrollbackBuffer;
import com.chalcodes.jtx.SoftFont;
import com.chalcodes.jtx.StickyScrollPane;
import com.chalcodes.jtx.VgaSoftFont;


public class BasicDemo extends JFrame {
	private static final long serialVersionUID = 1L;
	
	protected final ScrollbackBuffer buffer;
	protected final SoftFont font;
	protected final Display display;
	
	public BasicDemo(int bufferLines) throws IOException {
		setTitle("JTX Demo");
		
		buffer = new ScrollbackBuffer(80, bufferLines);
		font = new VgaSoftFont();
		display = new Display(buffer, font, 80, 25);
		
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
					new BasicDemo(1000).setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
