package ilrp.assist;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class AssistRobot {

	protected Robot robot = null;
	protected BufferedImage lastImage = null;
	protected BufferedImage curImage = null;

	public AssistRobot() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage shotScreen(int x, int y, int width, int height) {
		return robot.createScreenCapture(new Rectangle(x, y, width, height));
	}
	
	public BufferedImage shotScreen(Rectangle area) {
		return robot.createScreenCapture(area);
	}

	public boolean assertSameImage(BufferedImage image1, BufferedImage image2) {
		if (image1 == null || image2 == null) {
			return false;
		}
		try {
			PixelGrabber grab1 = new PixelGrabber(image1, 0, 0, -1, -1, false);
			PixelGrabber grab2 = new PixelGrabber(image2, 0, 0, -1, -1, false);

			int[] data1 = null;

			if (grab1.grabPixels()) {
				int width = grab1.getWidth();
				int height = grab1.getHeight();
				data1 = new int[width * height];
				data1 = (int[]) grab1.getPixels();
			}

			int[] data2 = null;

			if (grab2.grabPixels()) {
				int width = grab2.getWidth();
				int height = grab2.getHeight();
				data2 = new int[width * height];
				data2 = (int[]) grab2.getPixels();
			}			
			return java.util.Arrays.equals(data1, data2);

		} catch (InterruptedException ie) {
		}
		return false;
	}
	
	public void mouseWheel() {
		this.robot.mouseWheel(-100);
	}
	
	public BufferedImage waitForNewRedPacket(Weixin wx, BufferedImage baseImage) {
		BufferedImage newImage = null;
		do {
			newImage = this.shotScreen(wx.getArea());
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (this.assertSameImage(baseImage, newImage) || this.getLatestRedPacketPoint(wx, newImage) == null);
		return newImage;
	}

	public void viewAndTransferRedPacket(Weixin wx, BufferedImage focusImage, String transferGroup) {
		Point clickRedPacketPoint = this.getLatestRedPacketPoint(wx, focusImage);
		this.clickPos(clickRedPacketPoint);
		boolean openedRedPacket = false;
		do {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			focusImage = this.shotScreen(wx.getArea());
			if (this.assertOpenRedPacketPage(focusImage)) {
				openedRedPacket = false;
				break;
			}
			if (this.assertRedPacketPage(focusImage)) {
				openedRedPacket = true;
				break;
			}
		} while (true);
		
		if (!openedRedPacket) {
			// not opened red packet
			this.clickPos(wx.getViewRedPacketDetailButton());
			focusImage = waitForRedPacketPage(wx);
			this.mouseWheel();
			Point transferButton = null;
			do {
				transferButton = wx.getTransferRedPacketButton(focusImage);
				focusImage = this.shotScreen(wx.getArea());
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while ((transferButton.getY()-wx.getY()) < wx.getHeight() / 2);
			this.clickPos(transferButton);
			this.waitForTransferRedpacketPage(wx);
			this.delay(200);
			this.clickPos(new Point(wx.getWidth()/2,wx.getHeight()*25/462));
			transferGroup = transferGroup.toUpperCase();
			for (int i = 0; i < transferGroup.length(); i++) {
				this.pressKey(transferGroup.charAt(i));
			}
			
			this.clickPos(new Point(wx.getX()+wx.getWidth()/2,wx.getY()+wx.getHeight()*100/462));
			this.delay(300);
			this.clickPos(wx.getSendTransferRedPacketButton());
			this.waitForRedPacketPage(wx);
			this.backWX(wx);
		} else {
			focusImage = waitForRedPacketPage(wx);
//			this.mouseWheel();
//			this.robot.waitForIdle();
			Point transferButton = null;
			do {
				transferButton = wx.getTransferRedPacketButton(focusImage);
				focusImage = this.shotScreen(wx.getArea());
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while ((transferButton.getY()-wx.getY()) < wx.getHeight() / 2);
			this.clickPos(transferButton);
			this.waitForTransferRedpacketPage(wx);
			this.delay(200);
			this.clickPos(new Point(wx.getX()+wx.getWidth()/2,wx.getY()+wx.getHeight()*25/462));
			this.delay(200);
			transferGroup = transferGroup.toUpperCase();
			for (int i = 0; i < transferGroup.length(); i++) {
				this.pressKey(transferGroup.charAt(i));
			}
			this.delay(200);
			this.clickPos(new Point(wx.getX()+wx.getWidth()/2,wx.getY()+wx.getHeight()*100/462));
			this.delay(300);
			this.clickPos(wx.getSendTransferRedPacketButton());
			this.waitForRedPacketPage(wx);
			this.backWX(wx);
		}		
	}
	
	public boolean assertTransferRedpacketPage(BufferedImage image) {
		return true;
	}
	
	public BufferedImage waitForTransferRedpacketPage(Weixin wx) {
		BufferedImage curImage = null;
		BufferedImage newImage = this.shotScreen(wx.getTopArea());
		while (!this.assertTransferRedpacketPage(curImage) || !assertSameImage(curImage, newImage)) {
			curImage = newImage;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			newImage = this.shotScreen(wx.getTopArea());
			Debuger.drawPointOnImage(newImage, new Point((int)wx.getWXHomeIconLocation().getX()-wx.getX(), (int)wx.getWXHomeIconLocation().getY()-wx.getY()));
			Debuger.writeImage(newImage, "transferpage."+System.currentTimeMillis()+".bmp");
		}
		return newImage;
	}
	
	public BufferedImage waitForRedPacketPage(Weixin wx) {
		BufferedImage focusImage = null;
		do {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			focusImage = this.shotScreen(wx.getArea());
		} while (!this.assertRedPacketPage(focusImage));
		return focusImage;
	}
	
	public Situation processNewRedPacket(Weixin wx, BufferedImage focusImage, boolean openGroup) {
		Debuger.startTimer("process new red packet");
		Point clickRedPacketPoint = this.getLatestRedPacketPoint(wx, focusImage);
		this.clickPos(clickRedPacketPoint);
		boolean openedRedPacket = false;
		do {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			focusImage = this.shotScreen(wx.getArea());
			if (this.assertOpenRedPacketPage(focusImage)) {
				openedRedPacket = false;
				break;
			}
			if (this.assertRedPacketPage(focusImage)) {
				openedRedPacket = true;
				break;
			}
		} while (true);
		
		if (!openedRedPacket) {
			this.clickPos(wx.getOpenRedPacketButton());
			focusImage = waitForRedPacketPage(wx);
			openedRedPacket = true;
		}		
		Debuger.stopTimer("process new red packet");
		
		String myPacket = this.recognize(
				focusImage.getSubimage((int)wx.getRedPacketArea().getX(),(int)wx.getRedPacketArea().getY(),(int)wx.getRedPacketArea().getWidth(),(int)wx.getRedPacketArea().getHeight()),true);
		this.mouseDrag(wx.getX()+wx.getFullWidth()/2, wx.getY()+wx.getHeight(), wx.getX()+wx.getFullWidth()/2, wx.getY());
		Situation sit = this.parseRedPacketPage(wx, this.waitForStaticPage(wx));
		if (openGroup) {
			while (!sit.getOver()) {
				this.backWX(wx);
				focusImage = this.waitForChatPage(wx);
				sit = this.processNewRedPacket(wx, focusImage, openGroup);
			}
		} else {
			System.out.println("small group " + sit.toString());
			while (sit.getPacketsCount() != 2) {
				this.backWX(wx);
				focusImage = this.waitForChatPage(wx);
				sit = this.processNewRedPacket(wx, focusImage, openGroup);
			}
		}
		
		sit.setMyPacket(Float.parseFloat(myPacket));
		return sit;
	}
	
	public boolean assertSearchPage(BufferedImage image) {
		if (image == null) {
			return false;
		}
		for (int i = image.getHeight()-1; i >= 0; i--) {
			if (image.getRGB(image.getWidth()/2, i) != Weixin.topAreaBGColor.getRGB()) {
				return true;
			}
		}
		return false;
	}
	
	public BufferedImage waitForSearchPage(Weixin wx) {
		BufferedImage curImage = null;
		BufferedImage newImage = this.shotScreen(wx.getTopArea());
		while (!this.assertSearchPage(curImage) || !assertSameImage(curImage, newImage)) {
			curImage = newImage;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			newImage = this.shotScreen(wx.getTopArea());
//			Debuger.drawPointOnImage(newImage, new Point((int)wx.getWXHomeIconLocation().getX()-wx.getX(), (int)wx.getWXHomeIconLocation().getY()-wx.getY()));
//			Debuger.writeImage(newImage, "homepage."+System.currentTimeMillis()+".bmp");
		}
		return newImage;
	}
	
	public boolean assertSearchResultPage(BufferedImage image) {
		if (image == null) {
			return false;
		}
		for (int i = 0; i < image.getWidth(); i++) {
//			if (image.getRGB(i, image.getHeight()*71/462) == Weixin.searchResultUnderlineColor.getRGB()) {
			if (image.getRGB(i, image.getHeight()*71/462) != Weixin.whiteColor.getRGB()) {
				return true;
			}
		}	
		
		return false;
	}
	
	public BufferedImage waitForSearchResultPage(Weixin wx) {
		BufferedImage curImage = null;
		BufferedImage newImage = this.shotScreen(wx.getArea());
		while (!this.assertSearchResultPage(curImage) || !assertSameImage(curImage, newImage)) {
			curImage = newImage;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			newImage = this.shotScreen(wx.getArea());
			Debuger.drawLineOnImage(newImage,new Point(0,wx.getHeight()*71/462),new Point(wx.getWidth(),wx.getHeight()*71/462));
			Debuger.writeImage(newImage, "searchResultImage" + System.currentTimeMillis() + ".bmp");
		}
		return newImage;
	}
	
	public void enterChatGroup(Weixin wx, String groupName) {
		if (wx == null || groupName == null) {
			return;
		}
		groupName = groupName.toUpperCase();
		// suppose at wx home page
		// click search button
		this.clickPos(wx.getSearchButton());
		// wait for search page
		this.waitForSearchPage(wx);
		for (int i = 0; i < groupName.length(); i++) {
			this.pressKey(groupName.charAt(i));
		}
		this.waitForSearchResultPage(wx);
//		this.delay(200);
		this.clickPos(wx.getSearchedFirstRetLocation());
		this.waitForChatPage(wx);
	}
	

	
	public void printRGB(int rgb) {
		System.out.println(((rgb&0xff0000)>>16) + ":" + ((rgb&0x00ff00)>>8) + ":" + ((rgb&0x0000ff)));
	}
	
	public boolean getRedPacketOverState(Weixin wx, BufferedImage image) {
		int startX = wx.getFullWidth() - wx.getBestHandLocation();
		for (int i = wx.getHeight()-1; i>=0; i--) {
			if (image.getRGB(startX, i) == Weixin.bestHandColor.getRGB()) {
				return true;
			}
		}
		return false;
	}
	
	public Situation parseRedPacketPage(Weixin wx, BufferedImage image) {
		Situation ret = new Situation();
		int top = -1;
		int bottom = -1;
		int times = 1;
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setColor(Color.red);
//		try {
//			ImageIO.write(image, "bmp", new File("D:\\github\\i-love-red-packet\\dev\\assist\\aaa"+times+".bmp"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}		
		int startX = wx.getFullWidth()/2;
		for (int i = wx.getHeight() - 1; i>=0 ;i--) {
			if (image.getRGB(startX, i) == Weixin.whiteColor.getRGB()) {
				if (bottom == -1) {
					bottom = i;
				}
			} else {
				if (bottom != -1) {
					top = i+1;
					BufferedImage focusImage = this.shotScreen(wx.getX()+wx.getFullWidth()*2/3,wx.getY()+top,wx.getFullWidth()/3-wx.getHeight()*29/460,(bottom - top) / 2);
					g2d.drawRect(wx.getFullWidth()*2/3,top,wx.getFullWidth()/3-wx.getHeight()*7/115, bottom - top);
					try {
						ImageIO.write(focusImage, "png", new File("D:\\github\\i-love-red-packet\\dev\\assist\\test"+times+".png"));
					} catch (IOException e) {
						e.printStackTrace();
					}
					times++;
					focusImage = OCRTask.shrinkImage(focusImage, 2.0);
					String regret = OCRTask.recognize(focusImage,false);
					System.out.println(regret);
					if (regret.indexOf('.') == -1) {
						regret = regret.replace(" ", ".");
					} else {
						regret = regret.replace(" ", "");
					}
					System.out.println(regret);
					ret.addRedpacket(Float.parseFloat(regret));
					bottom = top = -1;
					if (image.getRGB(wx.getFullWidth()/2, i-2) != Weixin.whiteColor.getRGB()) {
						break;
					}
				}
				
			}
		}
		g2d.dispose();
		ret.setOver(this.getRedPacketOverState(wx, image));
//		try {
//			ImageIO.write(image, "png", new File("D:\\github\\i-love-red-packet\\dev\\assist\\bbbb.png"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		return ret;
	}
	
	public static void writeImage(BufferedImage image, String filename) {
		try {
			ImageIO.write(image, "png", new File("D:/github/i-love-red-packet/dev/assist/" + filename + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage waitForStaticPage(Weixin wx) {
		BufferedImage curImage = null;
		BufferedImage newImage = this.shotScreen(wx.getArea());
		while (!assertSameImage(curImage, newImage)) {
			curImage = newImage;
			this.delay(50);
			newImage = this.shotScreen(wx.getArea());
		}
		return newImage;
	}
	
	public BufferedImage waitForWXHomePage(Weixin wx) {
		BufferedImage curImage = null;
		BufferedImage newImage = this.shotScreen(wx.getAreaWithBottom());
		Debuger.writeImage(newImage, "homepage.bmp");		
		while (!this.assertWXHomePage(wx, curImage) || !assertSameImage(curImage, newImage)) {
			curImage = newImage;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			newImage = this.shotScreen(wx.getAreaWithBottom());
//			Debuger.drawPointOnImage(newImage, new Point((int)wx.getWXHomeIconLocation().getX()-wx.getX(), (int)wx.getWXHomeIconLocation().getY()-wx.getY()));
//			Debuger.writeImage(newImage, "homepage."+System.currentTimeMillis()+".bmp");
		}
		return newImage;
	}
	
	public boolean assertWXHomePage(Weixin wx, BufferedImage image) {
		if (wx == null || image == null) {
			return false;
		}
		if (image.getRGB((int)wx.getWXHomeIconLocation().getX()-wx.getX(), (int)wx.getWXHomeIconLocation().getY()-wx.getY()) == Weixin.HomeIconColor.getRGB()) {
			return true;
		}
		return false;
	}
	
	public BufferedImage waitForChatPage(Weixin wx) {
		BufferedImage curImage = null;
		BufferedImage newImage = this.shotScreen(wx.getArea());
		while (!this.assertWXChatPage(curImage) || !assertSameImage(curImage, newImage)) {
			curImage = newImage;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			newImage = this.shotScreen(wx.getArea());
		}
		return newImage;
	}
	
	public Situation clickRedPacket(Weixin wx1) {
		BufferedImage focusImage = this.waitForChatPage(wx1);
		System.out.println("get new red packet");
		this.processNewRedPacket(wx1, focusImage, false);	
		this.robot.waitForIdle();
		this.mouseDrag(wx1.getX()+wx1.getFullWidth()/2, wx1.getY()+wx1.getHeight(), wx1.getX()+wx1.getFullWidth()/2, wx1.getY());
		this.waitForStaticPage(wx1);
		return this.parseRedPacketPage(wx1, this.shotScreen(wx1.getArea()));
	}
	
	public void mouseDrag(int startX, int startY, int endX, int endY) {
		this.robot.mouseMove(startX, startY);
		this.robot.mousePress(KeyEvent.BUTTON1_MASK);
		this.robot.mouseMove(endX, endY);
		this.robot.mouseRelease(KeyEvent.BUTTON1_MASK);
	}
	
	public void directTransferRedPacket(Weixin wx1) {
		BufferedImage focusImage = this.waitForChatPage(wx1);
		this.processNewRedPacket(wx1, focusImage, false);		
	}
	
	public void openAndTransferRedPacket(Weixin wx1) {
		this.clickRedPacket(wx1);
		// transfer red packet
		this.clickPos(wx1.getTransferRedPacketButton());
		this.delay(100);
		this.clickPos(wx1.getRedPacketTop1GroupLocation());
		this.delay(300);
		this.clickPos(wx1.getSendTransferRedPacketButton());
	}
	
	public boolean waitForPayPage(Weixin wx) {
		BufferedImage curImage = null;
		BufferedImage newImage = this.shotScreen(wx.getArea());
		while (!this.getWXPayPage(wx, curImage) || !assertSameImage(curImage, newImage)) {
			curImage = newImage;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			newImage = this.shotScreen(wx.getArea());
		}
		return true;
	}
	
	public boolean getWXPayPage(Weixin wx, BufferedImage image) {
		if (image == null) {
			return false;
		}
		if (image.getRGB((int)wx.getInjectRedPacketButton().getX()-wx.getX(), (int)wx.getInjectRedPacketButton().getY()-wx.getY()) == Weixin.payPageColor.getRGB()) {
			return true;
		}
		return false;
	}
	
	public boolean waitForSetupRedPacketPage(Weixin wx) {
		BufferedImage curImage = null;
		BufferedImage newImage = this.shotScreen(wx.getArea());
		while (!this.getWXSetupRedPacketPage(wx, curImage) || !assertSameImage(curImage, newImage)) {
			curImage = newImage;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			newImage = this.shotScreen(wx.getArea());
		}
		return true;
	}
	
	public boolean getWXSetupRedPacketPage(Weixin wx, BufferedImage image) {
		if (image == null) {
			return false;
		}
		if (image.getRGB((int)wx.getSetupRedPacketButton().getX()-wx.getX(), (int)wx.getSetupRedPacketButton().getY()-wx.getY()) == Weixin.setupRedPacketColor.getRGB()) {
			return true;
		}
		return false;
	}
	
	public Point getLatestRedPacketPoint(Weixin wx, BufferedImage image) {
		Debuger.startTimer("test get latest red packet");
		for (int i = image.getHeight() - 1; i >= 0; i--) {
			if (image.getRGB(image.getWidth() / 2, i) == Weixin.redPacketBGColor.getRGB()) {
				return new Point(wx.getX() + image.getWidth() / 2, wx.getY() + i);
			}
		}
		Debuger.stopTimer("test get latest red packet");
		return null;
	}

	public void delay(int ms) {
		robot.delay(ms);
	}

	public Point getCenterPoint(Rectangle input) {
		return new Point(input.x + input.width / 2, input.y + input.height / 2);
	}

//	public Point locateOpenRedPacketPos(Weixin wx, BufferedImage image) {
//		for (int i = 0; i < image.getHeight() / 2; i++) {
//			if (image.getRGB(image.getWidth() / 2, image.getHeight() - i - 1) == Weixin.openRedPacketBGColor.getRGB()) {
//				return new Point(wx.getX() + image.getWidth() / 2, wx.getY() + image.getHeight() - i -1);
//			}
//		}
//		return null;
//	}

	public void backWX(Weixin wx) {
		this.clickPos(wx.getBackButton());
	}

//	public Rectangle extractRedPacketArea(BufferedImage image) {
//		int bottom = -1;
//		int top = -1;
//		// find bottom
//		for (int i = 0; i < image.getHeight() / 2; i++) {
//			if (bottom == -1) {
//				for (int j = 0; j < image.getWidth() / 2; j++) {
//					int red = (image.getRGB(image.getWidth() / 2 + j, image.getHeight() / 2 + i) & 0xff0000) >> 16;
//					if (red != 255) {
//						break;
//					} else if (j == image.getWidth() / 2 - 1) {
//						bottom = image.getHeight() / 2 + i;
//					}
//				}
//			}
//
//			if (top == -1) {
//				for (int j = 0; j < image.getWidth() / 2; j++) {
//					int red = (image.getRGB(image.getWidth() / 2 + j, image.getHeight() / 2 - i) & 0xff0000) >> 16;
//					if (red != 255) {
//						break;
//					} else if (j == image.getWidth() / 2 - 1) {
//						top = image.getHeight() / 2 - i;
//					}
//				}
//			}
//			if (bottom != -1 && top != -1) {
//				break;
//			}
//		}
//
//		return new Rectangle(0, top, image.getWidth(), bottom - top + 1);
//	}
	


	public String recognize(BufferedImage bi, boolean preProcessed) {
		return OCRTask.recognize(bi,preProcessed);
	}

	public void clickPos(Point pos) {
		if (pos == null) {
			return;
		}
		robot.mouseMove(pos.x, pos.y);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	
	
	
	public boolean assertOpenRedPacketPage(BufferedImage image) {
		Debuger.startTimer("test open red packet page");
		if (image == null) {
			return false;
		}
		int wxCentralX = image.getWidth() / 2;
		for (int i = 0; i < image.getHeight() / 2; i++) {
			if (image.getRGB(wxCentralX, image.getHeight() / 2 + i) == Weixin.openRedPacketBGColor.getRGB()) {
				return true;
			}
		}
		Debuger.stopTimer("test open red packet page");		
		return false;
	}

	public boolean assertWXChatPage(BufferedImage image) {
		if (image == null) {
			return false;
		}
		if (image.getRGB(1, 1) == Weixin.wxBGColor.getRGB()) {
			return true;
		}
		return false;
	}

	public boolean assertRedPacketPage(BufferedImage image) {
		Debuger.startTimer("test red packet page");
		if (image == null) {
			return false;
		}
		for (int i = 0; i < image.getHeight() / 2; i++) {
			if (image.getRGB(image.getWidth() / 2, i) == Weixin.redPacketPageColor.getRGB()) {
				return true;
			}
		}
		Debuger.stopTimer("test red packet page");
		return false;
	}
	
	public void postRedPacket(Weixin wx, String count, String amount) {
		this.clickPos(wx.getPostButton());
		this.delay(200);
		this.clickPos(wx.getRedPacketButton());
		this.delay(500);
		this.clickPos(wx.getRedCountTextField());
		this.delay(500);
		this.inputNumber(wx, count);
		this.clickPos(wx.getFoldKeybroadButton());
		this.delay(200);		
		this.clickPos(wx.getRedAmountTextField());
		this.delay(500);
		this.inputNumber(wx, amount);
		this.clickPos(wx.getFoldKeybroadButton());
		this.waitForSetupRedPacketPage(wx);
//		this.delay(200);
		this.clickPos(wx.getSetupRedPacketButton());
		this.waitForPayPage(wx);
		this.clickPos(wx.getInjectRedPacketButton());
		this.waitForChatPage(wx);
		this.pressKey(KeyEvent.VK_ESCAPE);
	}
	
	public void pressKey(int keyCode) {
		this.robot.keyPress(keyCode);
		this.robot.keyRelease(keyCode);
	}
	
	public void inputNumber(Weixin wx, String number) {
		for (int i = 0 ; i < number.length(); i++) {
			this.clickPos(wx.getKeyPoint(number.charAt(i)+""));
		}
	}
	
	public static void main(String... args) {
		AssistRobot ar = new AssistRobot();
		System.out.println(KeyEvent.VK_E);
		System.out.println((int)'E');
	}
}
