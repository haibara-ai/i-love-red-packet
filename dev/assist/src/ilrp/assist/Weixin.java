package ilrp.assist;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

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
	
	private boolean inited = false;
	
	private Point backButton = null;
	private Point postButton = null;
	private Point redPacketButton = null;
	private Point redCountTextField = null;
	private Point redAmountTextField = null;
	private Point foldKeybroadButton = null;
	private Map<String,Point> keybroadButton = null;
	private Point setupRedPacketButton = null;
	private Point injectRedPacketButton = null;
	private Point writePageBlankButton = null;
	private Point openRedPacketButton = null;
	private Point transferRedPacketButton = null;
	private Point top1GroupLocation = null;
	private Point sendTransferRedPacketButton = null;

	public Weixin() {
		
	}
	
	public Weixin(int x, int y, int fullWidth, int height) {
		this.init(x,y,fullWidth,height);
	}
	
	public void init(int x, int y, int fullWidth, int height) {
		this.x = x;
		this.y = y;
		this.fullWidth = fullWidth;
		this.width = fullWidth - 10;
		this.height = height;
		this.location = new Point(x,y);
		this.area = new Rectangle(x, y, width, height);
		this.inited = true;
		this.backButton = new Point(x+2,y-2);
		this.writePageBlankButton = new Point(x+fullWidth-2,y+(int)(height*25/46));
		this.postButton = new Point(x+fullWidth-(int)(height/22),y+height+(int)(height/22));
		this.redPacketButton = new Point(x+fullWidth-(int)(height/11),y+height-(int)(height*6/23));
		this.redCountTextField = new Point(x+fullWidth/2,y+height*2/23);
		this.redAmountTextField = new Point(x+fullWidth/2,y+height*6/23);
		this.keybroadButton = new HashMap<String,Point>();
		this.foldKeybroadButton = new Point(x+fullWidth/2,y+height*29/46-height*15/460);
		// 10 --> .
		// 11 --> delete
		this.keybroadButton.put("1", new Point(x+fullWidth/6,y+height*29/46+height*5/92));
		this.keybroadButton.put("2", new Point(x+fullWidth/2,y+height*29/46+height*5/92));
		this.keybroadButton.put("3", new Point(x+fullWidth*5/6,y+height*29/46+height*5/92));
		this.keybroadButton.put("4", new Point(x+fullWidth/6,y+height*29/46+height*15/92));
		this.keybroadButton.put("5", new Point(x+fullWidth/2,y+height*29/46+height*15/92));
		this.keybroadButton.put("6", new Point(x+fullWidth*5/6,y+height*29/46+height*15/92));
		this.keybroadButton.put("7", new Point(x+fullWidth/6,y+height*29/46+height*25/92));
		this.keybroadButton.put("8", new Point(x+fullWidth/2,y+height*29/46+height*25/92));
		this.keybroadButton.put("9", new Point(x+fullWidth*5/6,y+height*29/46+height*25/92));
		this.keybroadButton.put(".", new Point(x+fullWidth/6,y+height*29/46+height*35/92));
		this.keybroadButton.put("0", new Point(x+fullWidth/2,y+height*29/46+height*35/92));
		this.keybroadButton.put("delete", new Point(x+fullWidth*5/6,y+height*29/46+height*35/92));
		
		this.setupRedPacketButton = new Point(x+fullWidth/2,y+height*17/23);
		this.injectRedPacketButton = new Point(x+fullWidth/2,y+height*11/23);
		this.transferRedPacketButton = new Point(x+fullWidth/2,y+height*20/23);
		this.openRedPacketButton = new Point(x+fullWidth/2,y+height*16/23);
		this.top1GroupLocation = new Point(x+fullWidth/2,y+height*7/23);
		this.sendTransferRedPacketButton = new Point(x+fullWidth*11/16,y+height*16/23);
	}
	
	public boolean inited() {
		return this.inited;
	}
	
	public Point getBackButton() {
		return this.backButton;
	}
	
	public Point getPostButton() {
		return this.postButton;
	}
	
	public Point getOpenRedPacketButton() {
		return this.openRedPacketButton;
	}
	
	public Point getFoldKeybroadButton() {
		return this.foldKeybroadButton;
	}
	
	public Point getRedPacketButton() {
		return this.redPacketButton;
	}
	
	public Point getRedCountTextField() {
		return this.redCountTextField;
	}
	
	public Point getRedAmountTextField() {
		return this.redAmountTextField;
	}
	
	public Point getTop1GroupLocation() {
		return this.top1GroupLocation;
	}
	
	public Point getKeyPoint(String key) {
		return this.keybroadButton.get(key);
	}
	
	public Point getSendTransferRedPacketButton() {
		return this.sendTransferRedPacketButton;
	}
	
	public Point getSetupRedPacketButton() {
		return this.setupRedPacketButton;
	}
	
	public Point getInjectRedPacketButton() {
		return this.injectRedPacketButton;
	}
	
	public Point getWritePageBlankButton() {
		return this.writePageBlankButton;
	}
	
	public Point getTransferRedPacketButton() {
		return this.transferRedPacketButton;
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
		if (right == -1) {
			return;
		}
		// find left line
		for (int i = 0; i < image.getWidth(); i++) {
			if (image.getRGB(i, image.getHeight() / 2) == wxBGColor.getRGB()) {
				left = i;
				break;
			}
		}
		if (left == -1) {
			return;
		}
		// find top line
		for (int i = 0; i < image.getHeight(); i++) {
			if (image.getRGB(left, i) == wxBGColor.getRGB()) {
				top = i;
				break;
			}
		}
		if (top == -1) {
			return;
		}
		// find bottom line
		for (int i = image.getHeight() - 1; i >= 0; i--) {
			if (image.getRGB(left, i) == wxBGColor.getRGB()) {
				bottom = i;
				break;
			}
		}
		if (bottom == -1) {
			return;
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
