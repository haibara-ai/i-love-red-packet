package ilrp.assist;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Weixin {
	
	private int x = -1;
	private int y = -1;
	// this width is fake width
	private int width = -1;
	// this width is full width;
	private int fullWidth = -1;
	private int height = -1;
	
	private String userName = null;
	public String getUserName() {
		return userName;
	}
	
	public void setUserProfile(String username, boolean needPassword, String password) {
		this.userName = username;
		this.needPassword = needPassword;
		this.password = password;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isNeedPassword() {
		return needPassword;
	}

	public void setNeedPassword(boolean needPassword) {
		this.needPassword = needPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private boolean needPassword = true;
	private String password = null;
	
	private Point location = null;
	private Rectangle area = null;
	private Rectangle areaWithBottom = null;
	private Rectangle bottomArea = null;
	private Rectangle topArea = null;
	
	public final static Color openRedPacketBGColor = new Color(255, 170, 59);
	public final static Color redPacketBGColor = new Color(250, 157, 59);
	public final static Color wxBGColor = new Color(235, 235, 235);
	public final static Color redPacketPageColor = new Color(214, 86, 69);
	public final static Color payPageColor = new Color(69,192,26);
	public final static Color setupRedPacketColor = new Color(250,99,82);
	public final static Color whiteColor = new Color(255,255,255);
	public final static Color bestHandColor = new Color(255,172,28);
	public final static Color HomeIconColor = new Color(69,192,26);
	public final static Color searchUnderlineColor = new Color(63,168,29);
	public final static Color topAreaBGColor = new Color(34,41,44);
	public final static Color searchResultUnderlineColor = new Color(69,192,26);
	public final static Color keyboardBGColor = new Color(224,224,224);
	
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
	private Point redPacketTop1GroupLocation = null;
	private Point redPacketTop2GroupLocation = null;
	private Point sendTransferRedPacketButton = null;
	private Rectangle redPacketArea = null;
	private Point viewRedPacketDetailButton = null;
	private String name = null;
	private Point top1ChatGroupLocation = null;
	private Point top2ChatGroupLocation = null;
	private int redPacketDetailBottom = -1;
	private int bestHandLocation = -1;
	private Point homeIconLocation = null;
	private Point searchButton = null;
	private Point searchedFirstRetLocation = null;
	private Point mostUsedRetLocation = null;
	
	public Point getMostUsedRetLocation() {
		return mostUsedRetLocation;
	}

	public void setMostUsedRetLocation(Point mostUsedRetLocation) {
		this.mostUsedRetLocation = mostUsedRetLocation;
	}

	public Point getSearchedFirstRetLocation() {
		return searchedFirstRetLocation;
	}

	public void setSearchedFirstRetLocation(Point searchedFirstRetLocation) {
		this.searchedFirstRetLocation = searchedFirstRetLocation;
	}

	public Point getSearchButton() {
		return searchButton;
	}

	public void setSearchButton(Point searchButton) {
		this.searchButton = searchButton;
	}

	public Weixin(String name) {
		this.name = name;
	}
	
	public Weixin(int x, int y, int fullWidth, int height) {
		this.init(x,y,fullWidth,height);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void init(int x, int y, int fullWidth, int height) {
		this.x = x;
		this.y = y;
		this.fullWidth = fullWidth;
		this.width = fullWidth - 10;
		this.height = height;
		this.location = new Point(x,y);
		this.area = new Rectangle(x, y, width, height);
		this.areaWithBottom = new Rectangle(x, y, width, height + height*44/460);
		this.bottomArea = new Rectangle(x,y+height,width,height*44/460);
		this.topArea = new Rectangle(x,y-height*42/460,width,height*42/460);
		this.inited = true;
		this.backButton = new Point(x+1,y-1);
		this.writePageBlankButton = new Point(x+fullWidth-2,y+(int)(height*25/46));
		this.postButton = new Point(x+fullWidth-(int)(height/22),y+height+(int)(height/22));
		this.redPacketButton = new Point(x+fullWidth-(int)(height/11),y+height-(int)(height*6/23));
		this.redCountTextField = new Point(x+fullWidth/2,y+height*2/23);
		this.redAmountTextField = new Point(x+fullWidth/2,y+height*6/23);
		this.keybroadButton = new HashMap<String,Point>();
		this.foldKeybroadButton = new Point(x+fullWidth/2,y+height*29/46-height*15/460);
		//
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
		
		this.setupRedPacketButton = new Point(x+fullWidth/2,y+height*33/46);
		this.injectRedPacketButton = new Point(x+fullWidth/2,y+height*21/46);
		this.transferRedPacketButton = new Point(x+fullWidth/2,y+height*20/23);
		this.openRedPacketButton = new Point(x+fullWidth/2,y+height*16/23);
		this.redPacketTop1GroupLocation = new Point(x+fullWidth/2,y+height*7/23);
		this.redPacketTop2GroupLocation = new Point(x+fullWidth/2,y+height*9/23);
		this.sendTransferRedPacketButton = new Point(x+fullWidth*11/16,y+height*16/23);
		this.redPacketArea = new Rectangle(0, height*19/46, width, height*3/23);
		this.viewRedPacketDetailButton = new Point(x+fullWidth/2,y+height*20/23);
		
		this.top1ChatGroupLocation = new Point(x+fullWidth/2,y+height*27/460);
		this.top2ChatGroupLocation = new Point(x+fullWidth/2,y+height*5/23);
		
		this.redPacketDetailBottom = height*39/46;
		this.bestHandLocation = height * 72 / 462;		
		this.homeIconLocation = new Point(x+height*2/23,y+height+height*15/462);		
		this.searchButton = new Point(x+fullWidth-height*78/460,y-(int)this.getTopArea().getHeight()/2);
		this.searchedFirstRetLocation = new Point(x + fullWidth/2,y+height*130/462);
		this.mostUsedRetLocation = new Point(x + fullWidth/2,y+height*80/462);
	}

	public Rectangle getAreaWithBottom() {
		return areaWithBottom;
	}

	public void setAreaWithBottom(Rectangle areaWithBottom) {
		this.areaWithBottom = areaWithBottom;
	}

	public Rectangle getBottomArea() {
		return bottomArea;
	}

	public void setBottomArea(Rectangle bottomArea) {
		this.bottomArea = bottomArea;
	}

	public Rectangle getTopArea() {
		return topArea;
	}

	public void setTopArea(Rectangle topArea) {
		this.topArea = topArea;
	}

	public Point getWXHomeIconLocation() {
		return homeIconLocation;
	}

	public void setWeixinHomeIcaonLocation(Point weixinHomeIcaonLocation) {
		this.homeIconLocation = weixinHomeIcaonLocation;
	}

	public int getBestHandLocation() {
		return bestHandLocation;
	}

	public void setBestHandLocation(int bestHandLocation) {
		this.bestHandLocation = bestHandLocation;
	}

	public int getRedPacketDetailBottom() {
		return this.redPacketDetailBottom;
	}
	
	public Point getTop1ChatGroupLocation() {
		return this.top1ChatGroupLocation;
	}
	
	public Point getTop2ChatGroupLocation() {
		return this.top2ChatGroupLocation;
	}
	
	public Point getViewRedPacketDetailButton(){
		return this.viewRedPacketDetailButton;
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
	
	public Point getRedPacketTop1GroupLocation() {
		return this.redPacketTop1GroupLocation;
	}
	
	public Point getRedPacketTop2GroupLocation() {
		return this.redPacketTop2GroupLocation;
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
	
	public static final Color transferPageBGColor = new Color(245,245,245);
	
	public Point getTransferRedPacketButton(BufferedImage image) {
		float[] hsbvals = new float[3];
		for (int i = image.getHeight() - 1; i >= 0 ; i--) {
			int rgb = image.getRGB(image.getWidth()/2, i);
			Color.RGBtoHSB((rgb&0xff0000)>>16, (rgb&0x00ff00)>>8, rgb&0x0000ff, hsbvals);
			if (hsbvals[0] != 0) {
				return new Point(x+image.getWidth()/2,y+i-2);
			}
		}	
		return null;
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
	
	
	public Rectangle getRedPacketArea() {
		return this.redPacketArea;
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
