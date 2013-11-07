package krum.jtx.extensions;

public class Selection {
	/** [row][column] */
	private final int[][] values;
	
	public Selection(int[][] values) {
		final int rows;
		final int cols;
		if(values.length == 0) {
			rows = 0;
			cols = 0;
		}
		else {
			rows = values.length;
			cols = values[0].length;
		}
		this.values = new int[rows][cols];
		for(int r = 0; r < rows; ++r) {
			if(values[r].length == cols) {
				System.arraycopy(values[r], 0, this.values[r], 0, cols);
			}
			else {
				throw new IllegalArgumentException("argument must not be a ragged array");
			}
		}
	}
}
