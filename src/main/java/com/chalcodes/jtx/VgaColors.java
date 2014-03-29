package com.chalcodes.jtx;

import java.awt.Color;

public class VgaColors {
	public static final int BLACK = 0;
	public static final int RED = 1;
	public static final int GREEN = 2;
	public static final int YELLOW = 3;
	public static final int BLUE = 4;
	public static final int MAGENTA = 5;
	public static final int CYAN = 6;
	public static final int WHITE = 7;
	
	protected static final Color[] normalColors;
	protected static final Color[] brightColors;
	
	static {
		normalColors = new Color[8];
		normalColors[BLACK] = new Color(0, 0, 0);
		normalColors[RED] = new Color(170, 0, 0);
		normalColors[GREEN] = new Color(0, 170, 0);
		normalColors[YELLOW] = new Color(170, 85, 0);
		normalColors[BLUE] = new Color(0, 0, 170);
		normalColors[MAGENTA] = new Color(170, 0, 170);
		normalColors[CYAN] = new Color(0, 170, 170);
		normalColors[WHITE] = new Color(170, 170, 170);
		
		brightColors = new Color[8];
		brightColors[BLACK] = new Color(85, 85, 85);
		brightColors[RED] = new Color(255, 85, 85);
		brightColors[GREEN] = new Color(85, 255, 85);
		brightColors[YELLOW] = new Color(255, 255, 85);
		brightColors[BLUE] = new Color(85, 85, 255);
		brightColors[MAGENTA] = new Color(255, 85, 255);
		brightColors[CYAN] = new Color(85, 255, 255);
		brightColors[WHITE] = new Color(255, 255, 255);
	}
	
	public static Color foreground(int color, boolean bright) {
		if(bright) return VgaColors.brightColors[color];
		else return normalColors[color];
	}
	
	public static Color background(int color) {
		return normalColors[color];
	}
	
	private VgaColors() { }
}
