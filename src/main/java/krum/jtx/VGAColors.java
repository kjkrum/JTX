package krum.jtx;

import java.awt.Color;

public class VGAColors {
	public static final int BLACK = 0;
	public static final int RED = 1;
	public static final int GREEN = 2;
	public static final int YELLOW = 3;
	public static final int BLUE = 4;
	public static final int MAGENTA = 5;
	public static final int CYAN = 6;
	public static final int WHITE = 7;
	
	protected static final Color[] normal;
	protected static final Color[] bright;
	
	static {
		normal = new Color[8];
		normal[BLACK] = new Color(0, 0, 0);
		normal[RED] = new Color(170, 0, 0);
		normal[GREEN] = new Color(0, 170, 0);
		normal[YELLOW] = new Color(170, 85, 0);
		normal[BLUE] = new Color(0, 0, 170);
		normal[MAGENTA] = new Color(170, 0, 170);
		normal[CYAN] = new Color(0, 170, 170);
		normal[WHITE] = new Color(170, 170, 170);
		
		bright = new Color[8];
		bright[BLACK] = new Color(85, 85, 85);
		bright[RED] = new Color(255, 85, 85);
		bright[GREEN] = new Color(85, 255, 85);
		bright[YELLOW] = new Color(255, 255, 85);
		bright[BLUE] = new Color(85, 85, 255);
		bright[MAGENTA] = new Color(255, 85, 255);
		bright[CYAN] = new Color(85, 255, 255);
		bright[WHITE] = new Color(255, 255, 255);
	}
	
	public static Color foreground(int color, boolean bright) {
		if(bright) return VGAColors.bright[color];
		else return normal[color];
	}
	
	public static Color background(int color) {
		return normal[color];
	}
	
	private VGAColors() { }
}
