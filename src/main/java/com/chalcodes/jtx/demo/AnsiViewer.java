package com.chalcodes.jtx.demo;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.chalcodes.jtx.demo.lexer.DemoLexer;

/**
 * This class demonstrates attaching an emulation to JTX.  It uses a simple
 * emulation that recognizes a small subset of ANSI X3.64 and VT-series
 * terminal control sequences.  Each file opened is appended to the current
 * contents of the buffer.  Try finding several ANSI art files online and
 * opening them sequentially.
 * <p>
 * Note that the parser handles carriage returns and line feeds as separate
 * events.  The standard network newline includes one of each, and positioning
 * the cursor at the beginning of the next line requires both.  Some operating
 * systems use a different newline sequence for their text files, so if you
 * open a plain text file with this viewer, you may get strange results. 
 * <p>
 * Making this into a network terminal would require several things that are
 * not included in this demo for the sake of clarity.  At a minimum, it would
 * need a thread to read from a socket and write to the lexer, and a key
 * listener to send keystrokes back to the network.
 */
public class AnsiViewer extends BasicDemo {
	private static final long serialVersionUID = 1L;
	
	static protected final int BUFFER_SIZE = 1024 * 1024; // 1 MiB
	
	// demo emulation
	protected final DemoLexer lexer;
	protected final DemoEmulation parser;
	
	// ui stuff
	protected final JFileChooser fileChooser;
	
	public AnsiViewer() throws IOException, ClassNotFoundException {
		setTitle("ANSI Viewer");
		
		lexer = new DemoLexer();
		parser = new DemoEmulation(buffer);
		lexer.addEventListener(parser);
		
		fileChooser = new JFileChooser();
		
		// give the frame a menu
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		AbstractAction openAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			{
				putValue(NAME, "Open");
				putValue(MNEMONIC_KEY, KeyEvent.VK_O);
			}
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fileChooser.showOpenDialog(AnsiViewer.this) == JFileChooser.APPROVE_OPTION) {
					// read file
					ByteBuffer bb = ByteBuffer.allocateDirect(BUFFER_SIZE);
					CharBuffer cb = CharBuffer.allocate(BUFFER_SIZE);
					FileChannel fc;
					try {
						fc = new FileInputStream(fileChooser.getSelectedFile()).getChannel();
					} catch (FileNotFoundException ex) {
						JOptionPane.showMessageDialog(AnsiViewer.this,
								"File not found: " + fileChooser.getSelectedFile().getPath(),
								"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					// append each opened file to the buffer
					parser.clearScreen(null, 0, 0);
					try {
						fc.position(0);
						int bytes = fc.read(bb);
						while(bytes != -1) {							
							bb.flip();
							while(bb.hasRemaining() && cb.hasRemaining()) {
								// convert each byte directly to characters 0-255
								cb.put((char)(bb.get() & 0xff));
							}
							bb.compact();
							cb.flip();
							int lexed = lexer.lex(cb, cb.position(), cb.remaining(), false);
							cb.position(lexed);
							cb.compact();
							bytes = fc.read(bb);
						}
						// lex anything remaining in the buffer
						cb.flip();
						lexer.lex(cb, cb.position(), cb.remaining(), true);
						fc.close();
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(AnsiViewer.this, "I/O error.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
		};
		fileMenu.add(new JMenuItem(openAction));
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
		pack();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new AnsiViewer().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
