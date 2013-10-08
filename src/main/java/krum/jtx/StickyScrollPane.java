package krum.jtx;

import java.awt.Component;

import javax.swing.JScrollPane;

/**
 * A scroll pane with its normal viewport layout manager replaced by a
 * <tt>StickyViewportLayout</tt>.
 * 
 * @author Kevin Krumwiede (kjkrum@gmail.com)
 * @see StickyViewportLayout
 */
public class StickyScrollPane extends JScrollPane {
	private static final long serialVersionUID = 1L;
	
	public StickyScrollPane() {
		super();
		replaceViewportLayout();
	}

	public StickyScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
		super(view, vsbPolicy, hsbPolicy);
		replaceViewportLayout();
	}

	public StickyScrollPane(Component view) {
		super(view);
		replaceViewportLayout();
	}

	public StickyScrollPane(int vsbPolicy, int hsbPolicy) {
		super(vsbPolicy, hsbPolicy);
		replaceViewportLayout();
	}

	protected void replaceViewportLayout() {
		viewport.setLayout(new StickyViewportLayout());
	}
}
