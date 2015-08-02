package ilrp.ui.assist;

import com.sun.jna.platform.win32.WinDef.RECT;

public class Rectangle {
	public Rectangle(RECT rectangle) {
		this.left = rectangle.left;
		this.top = rectangle.top;
		this.right = rectangle.right;
		this.bottom = rectangle.bottom;
	}
	
	public int left;
	public int top;
	public int right;
	public int bottom;
}
