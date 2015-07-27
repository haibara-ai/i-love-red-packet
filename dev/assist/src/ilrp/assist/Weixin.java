package ilrp.assist;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Weixin {
	
	private int x = -1;
	private int y = -1;
	// this width is fake width
	private int width = -1;
	// this width is full width;
	private int fullWidth = -1;
	private int height = -1;
	
	private Point location = null;
	private Rectangle area = null;
	
	public final static Color openRedPacketBGColor = new Color(255, 170, 59);
	public final static Color redPacketBGColor = new Color(250, 157, 59);
	public final static Color wxBGColor = new Color(235, 235, 235);
	public final static Color redPacketPageColor = new Color(214, 86, 69);
	
	public Weixin() {
		
	}
	
	public Weixin(int x, int y, int fullWidth, int height) {
		this.x = x;
		this.y = y;
		this.fullWidth = fullWidth;
		this.width = fullWidth - 10;
		this.height = height;
		this.location = new Point(x,y);
		this.area = new Rectangle(x, y, width, height);
	}
	
	public void init(int x, int y, int fullWidth, int height) {
		this.x = x;
		this.y = y;
		this.fullWidth = fullWidth;
		this.width = fullWidth - 10;
		this.height = height;
		this.location = new Point(x,y);
		this.area = new Rectangle(x, y, width, height);
	}
	
	public void init(Point imageLocation, BufferedImage image) {
		int top = -1, left = -1, bottom = -1, right = -1;

		// find right line
		for (int i = image.getWidth() - 1; i >= 0; i--) {
			if (image.getRGB(i, image.getHeight() / 2) == wxBGColor.getRGB()) {
				right = i;
				break;
			}
		}
		// find left line
		for (int i = 0; i < image.getWidth(); i++) {
			if (image.getRGB(i, image.getHeight() / 2) == wxBGColor.getRGB()) {
				left = i;
				break;
			}
		}
		// find top line
		for (int i = 0; i < image.getHeight(); i++) {
			if (image.getRGB(left, i) == wxBGColor.getRGB()) {
				top = i;
				break;
			}
		}

		// find bottom line
		for (int i = image.getHeight() - 1; i >= 0; i--) {
			if (image.getRGB(left, i) == wxBGColor.getRGB()) {
				bottom = i;
				break;
			}
		}

		this.init(imageLocation.x + left, imageLocation.y + top, right - left + 1, bottom - top + 1);
	}
	
	public Point getLocation() {
		return this.location;
	}
	
	public Rectangle getArea() {
		return this.area;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getFullWidth() {
		return this.fullWidth;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
}
