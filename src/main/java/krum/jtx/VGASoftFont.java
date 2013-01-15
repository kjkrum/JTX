package krum.jtx;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * A <tt>SoftFont</tt> that blits VGA text mode characters from a pre-rendered
 * glyph sheet.  The glyph sheet is stored as a volatile image to enable
 * hardware acceleration.  The pixel dimensions of the glyph sheet determine
 * the glyph size of the font, and must be a multiple of 256 pixels in width
 * and 128 pixels in height.  The size and position of the underline overlay
 * is calculated automatically.
 * <p>
 * The glyph sheet contains 128 rows of 256 glyphs, rendered in all possible
 * combinations of foreground color, background color, and the "bright"
 * attribute.  The color order of the glyph sheet is black, red, green,
 * yellow, blue, magenta, cyan, and white.  For each background color, a row
 * of glyphs is rendered in each foreground color.  This produces the first 64
 * rows of the glyph sheet.  This sequence is then repeated for glyphs with
 * the "bright" attribute set.  With the rows in this order, the row number
 * for any attribute value is simply the value of its seven rightmost bits.
 *
 * @author Kevin Krumwiede (kjkrum@gmail.com)
 */
public class VGASoftFont implements SoftFont {

	protected final Dimension glyphSize;
	protected final BufferedImage bufferedImage;
	protected VolatileImage volatileImage;
	
	/**
	 * 
	 * @param glyphSheet
	 */
	public VGASoftFont(BufferedImage glyphSheet) {
		int width = glyphSheet.getWidth();
		int height = glyphSheet.getHeight();
		if(width == 0 || height == 0) {
			throw new IllegalArgumentException("glyph sheet must have non-zero width and height");
		}
		if(width % 256 != 0 || height % 128 != 0) {
			throw new IllegalArgumentException("glyph sheet dimensions must be a multiple of 256x128 pixels");
		}
		glyphSize = new Dimension(width / 256, height / 128);
		bufferedImage = glyphSheet;
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice gs = ge.getDefaultScreenDevice();
	    GraphicsConfiguration gc = gs.getDefaultConfiguration();
	    volatileImage = gc.createCompatibleVolatileImage(bufferedImage.getWidth(), bufferedImage.getHeight());
		Graphics2D graphics = (Graphics2D) volatileImage.getGraphics();
		graphics.drawImage(bufferedImage, 0, 0, null);
		graphics.dispose();
	}
	
	/**
	 * 
	 * @param resource
	 * @throws IOException
	 */
	public VGASoftFont(String resource) throws IOException {
		this(ImageIO.read(VGASoftFont.class.getResourceAsStream(resource)));
	}
	
	/**
	 * Creates a new <tt>VGASoftFont</tt> using the default 9x16 VGA glyph
	 * sheet included with JTX.
	 * 
	 * @throws IOException
	 */
	public VGASoftFont() throws IOException {
		this("/resources/jtx/glyphsheets/vga9x16.png");
	}
	
	@Override
	public Dimension getGlyphSize() {
		return glyphSize;
	}

	@Override
	public void drawGlyph(int value, boolean blinkOn, Graphics graphics, int x, int y) {
		int character = value & 0xFFFF;
		if(!blinkOn && (value & VGABufferElement.BLINKING) != 0) character = 0;
		else if(character > 255) character = '?';
		
		int colorAttr = (value & 0x7F0000) >> 16;
		if((value & VGABufferElement.INVERTED) != 0) {
			colorAttr ^= 0x3F;
		}

		int dx1 = x;
		int dy1 = y;
		int dx2 = dx1 + glyphSize.width;
		int dy2 = dy1 + glyphSize.height;
		int sx1 = character * glyphSize.width;
		int sy1 = colorAttr * glyphSize.height;
		int sx2 = sx1 + glyphSize.width;
		int sy2 = sy1 + glyphSize.height;

		Graphics2D g2d = (Graphics2D) graphics;
		GraphicsConfiguration gc = g2d.getDeviceConfiguration();
		
		do {
			switch(volatileImage.validate(gc)) {
			case VolatileImage.IMAGE_INCOMPATIBLE:
				volatileImage = gc.createCompatibleVolatileImage(bufferedImage.getWidth(), bufferedImage.getHeight());
				// intentionally no break
			case VolatileImage.IMAGE_RESTORED:
				Graphics2D volatileGraphics = (Graphics2D) volatileImage.getGraphics();
				volatileGraphics.drawImage(bufferedImage, 0, 0, null);
				volatileGraphics.dispose();
			}
			g2d.drawImage(volatileImage, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
			// make sure image wasn't invalidated during drawing
		} while(volatileImage.validate(gc) != VolatileImage.IMAGE_OK);
		
		// TODO: draw underline if needed 
	}
	
	

}
