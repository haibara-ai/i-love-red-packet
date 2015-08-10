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
			robot.setAutoWaitForIdle(true);
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
		Point latestPoint = null;
		do {
			newImage = this.shotScreen(wx.getArea());
			this.delay(50);
			latestPoint = this.getLatestRedPacketPoint(wx, newImage);
			if (latestPoint == null) {
				wx.setLatestRedpacketLocation(-1);
				continue;
			} else {
				if (wx.getLatestRedpacketLocation() == -1) {
					wx.setLatestRedpacketLocation((int)latestPoint.getY());
					break;
				} else {
					if (latestPoint.getY() > wx.getLatestRedpacketLocation()) {
						wx.setLatestRedpacketLocation((int)latestPoint.getY());
						break;
					} else {
						
					}
				}
			}
		} while (true);
		return newImage;
	}

	public void viewAndTransferRedPacket(Weixin wx, BufferedImage focusImage, String transferGroup) {
		this.waitForChatPage(wx);
		Point clickRedPacketPoint = this.getLatestRedPacketPoint(wx, focusImage);
		this.clickPos(clickRedPacketPoint);
		boolean openedRedPacket = false;
		do {
			this.delay(50);
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
				this.delay(50);
			} while ((transferButton.getY() - wx.getY()) < wx.getHeight() / 2);
			this.clickPos(transferButton);
			this.waitForTransferRedpacketPage(wx);
			this.delay(200);
			this.clickPos(new Point(wx.getWidth() / 2, wx.getHeight() * 25 / 462));
			transferGroup = transferGroup.toUpperCase();
			for (int i = 0; i < transferGroup.length(); i++) {
				this.pressKey(transferGroup.charAt(i));
			}

			this.clickPos(new Point(wx.getX() + wx.getWidth() / 2, wx.getY() + wx.getHeight() * 100 / 462));
			this.delay(200);
			this.clickPos(wx.getSendTransferRedPacketButton());
			this.waitForRedPacketPage(wx);
			this.backWX(wx);
		} else {
			focusImage = waitForRedPacketPage(wx);
			// this.mouseWheel();
			Point transferButton = null;
			do {
				transferButton = wx.getTransferRedPacketButton(focusImage);
				focusImage = this.shotScreen(wx.getArea());
				this.delay(50);
			} while ((transferButton.getY() - wx.getY()) < wx.getHeight() / 2);
			this.clickPos(transferButton);
			this.waitForTransferRedpacketPage(wx);
			this.delay(200);
			this.clickPos(new Point(wx.getX() + wx.getWidth() / 2, wx.getY() + wx.getHeight() * 25 / 462));
			this.delay(200);
			transferGroup = transferGroup.toUpperCase();
			for (int i = 0; i < transferGroup.length(); i++) {
				this.pressKey(transferGroup.charAt(i));
			}
			this.delay(200);
			this.clickPos(new Point(wx.getX() + wx.getWidth() / 2, wx.getY() + wx.getHeight() * 100 / 462));
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
			this.delay(50);
			newImage = this.shotScreen(wx.getTopArea());
			Debuger.drawPointOnImage(newImage, new Point((int) wx.getWXHomeIconLocation().getX() - wx.getX(),
					(int) wx.getWXHomeIconLocation().getY() - wx.getY()));
			Debuger.writeImage(newImage, "transferpage." + System.currentTimeMillis() + ".bmp");
		}
		return newImage;
	}

	public BufferedImage waitForRedPacketPage(Weixin wx) {
		BufferedImage focusImage = null;
		do {
			this.delay(50);
			focusImage = this.shotScreen(wx.getArea());
		} while (!this.assertRedPacketPage(focusImage));
		return focusImage;
	}
	
	public int assertOpenRedpacketRet(BufferedImage image) {
		if (image == null) {
			return -1;
		}
		boolean isUnopenedPage = false;
		boolean isOpenedpacketButton = false;
		boolean isOpenedPage = false;
		int startX = image.getWidth()/2;
		for (int i = image.getHeight()-1; i >= 0; i--) {
			if (image.getRGB(startX, i) == Weixin.openRedPacketBGColor.getRGB()) {
				isUnopenedPage = true;
			}
			if (isUnopenedPage) {
				if (image.getRGB(startX, i) == Weixin.openRedPacketButtonColor.getRGB()) {
					isOpenedpacketButton = true;
					break;
				}
			} else {
				if (image.getRGB(startX, i) == Weixin.redPacketPageColor.getRGB()) {
					isOpenedPage = true;
					break;
				}
			}
		}
		if (isOpenedPage) {
			return 1;
		} else {
			if (isOpenedpacketButton) {
				return 0;
			} else if (isUnopenedPage) {
				return 2;
			}
		}
		return -1;
	}

	public void processNewRedPacket(Weixin wx, boolean openGroup, Situation sit, boolean needParseAllPackets) {
		Debuger.startTimer("process new red packet");
		this.clickPos(this.getLatestRedPacketPoint(wx, this.shotScreen(wx.getArea())));
		int clickRedpacketRet = -1;
		// 0 unopen; 1 opened; 2 failed;
		do {
			this.delay(50);
			clickRedpacketRet = this.assertOpenRedpacketRet(this.shotScreen(wx.getArea()));
		} while (clickRedpacketRet == -1);
		
		if (clickRedpacketRet == 0) {
			this.clickPos(wx.getOpenRedPacketButton());
			this.waitForRedPacketPage(wx);
		} else if (clickRedpacketRet == 1) {
			
		} else if (clickRedpacketRet == 2) {
			System.out.println("redpacket has been rushed");
			sit.setRushedByOthers(true);
			return;
		}

		Debuger.stopTimer("process new red packet");
		if (sit == null) {
			sit = new Situation();
		}
		if (sit.getMyPacket() == -1) {
			String myPacket = this.recognize(this.shotScreen(wx.getX() + (int) wx.getRedPacketArea().getX(),
					wx.getY() + (int) wx.getRedPacketArea().getY(), (int) wx.getRedPacketArea().getWidth(),
					(int) wx.getRedPacketArea().getHeight()), true);
			sit.setMyPacket(Float.parseFloat(myPacket));
		}
		this.mouseDrag(wx.getX() + wx.getFullWidth() / 2, wx.getY() + wx.getHeight(), wx.getX() + wx.getFullWidth() / 2,
				wx.getY());
		this.delay(300);
		if (needParseAllPackets) {
			this.parseRedPacketPage(wx, this.waitForStaticPage(wx), sit, openGroup);
		}
		System.out.println(sit);
	}

	public boolean assertSearchPage(BufferedImage image) {
		if (image == null) {
			return false;
		}
		boolean start = false;
		for (int i = image.getHeight() - 1; i >= 0; i--) {
			if (image.getRGB(image.getWidth() / 2, i) == Weixin.topAreaBGColor.getRGB()) {
				if (start == false) {
					start = true;
				}
			} else {
				if (start == true) {
					return true;
				} else {

				}
			}

		}
		return false;
	}

	public BufferedImage waitForSearchPage(Weixin wx) {
		BufferedImage curImage = null;
		BufferedImage newImage = this.shotScreen(wx.getTopArea());
		while (!this.assertSearchPage(curImage) || !assertSameImage(curImage, newImage)) {
			curImage = newImage;
			this.delay(30);
			newImage = this.shotScreen(wx.getTopArea());
		}

		return newImage;
	}

	public boolean assertMostUsedRetPage(BufferedImage image) {
		if (image == null) {
			return false;
		}
		for (int i = 0; i < image.getWidth(); i++) {
			if (image.getRGB(i, image.getHeight() * 80 / 462) != Weixin.whiteColor.getRGB()) {
				return true;
			}
		}

		return false;
	}

	public boolean assertSearchResultPage(BufferedImage image) {
		if (image == null) {
			return false;
		}
		for (int i = 0; i < image.getWidth(); i++) {
			if (image.getRGB(i, image.getHeight() * 130 / 462) != Weixin.whiteColor.getRGB()) {
				return true;
			}
		}

		return false;
	}

	public BufferedImage waitForMostUsedRetPage(Weixin wx) {
		BufferedImage curImage = null;
		BufferedImage newImage = this.shotScreen(wx.getArea());
		while (!this.assertMostUsedRetPage(curImage) || !assertSameImage(curImage, newImage)) {
			curImage = newImage;
			this.delay(10);
			newImage = this.shotScreen(wx.getArea());
		}
		return newImage;
	}

	public BufferedImage waitForSearchResultPage(Weixin wx) {
		BufferedImage curImage = null;
		BufferedImage newImage = this.shotScreen(wx.getArea());
		while (!this.assertSearchResultPage(curImage) || !assertSameImage(curImage, newImage)) {
			curImage = newImage;
			this.delay(10);
			newImage = this.shotScreen(wx.getArea());
		}
		return newImage;
	}

	public void enterChatGroup(Weixin wx, String groupName, boolean mostUsed) {
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
			this.delay(10);
		}
		if (mostUsed) {
			this.waitForMostUsedRetPage(wx);
			this.clickPos(wx.getMostUsedRetLocation());
		} else {
			this.waitForSearchResultPage(wx);
			this.clickPos(wx.getSearchedFirstRetLocation());
		}
		this.waitForChatPage(wx);
	}

	public void printRGB(int rgb) {
		System.out.println(((rgb & 0xff0000) >> 16) + ":" + ((rgb & 0x00ff00) >> 8) + ":" + ((rgb & 0x0000ff)));
	}

	public boolean getRedPacketOverState(Weixin wx, BufferedImage image) {
		int startX = wx.getFullWidth() - wx.getBestHandLocation();
		for (int i = wx.getHeight() - 1; i >= 0; i--) {
			if (image.getRGB(startX, i) == Weixin.bestHandColor.getRGB()) {
				return true;
			}
		}
		return false;
	}

	public void parseRedPacketPage(Weixin wx, BufferedImage image, Situation sit, boolean openGroup) {
		if (sit == null) {
			sit = new Situation();
		}
		boolean isOver = this.getRedPacketOverState(wx, image);
		sit.setOver(isOver);
		if (openGroup && !isOver) {
			return;
		}
		sit.clearPackets();
		int top = -1;
		int bottom = -1;
		int times = 1;

		int startX = wx.getFullWidth() / 2;
		for (int i = wx.getHeight() - 1; i >= 0; i--) {
			if (image.getRGB(startX, i) == Weixin.whiteColor.getRGB()) {
				if (bottom == -1) {
					bottom = i;
				}
			} else {
				if (bottom != -1) {
					top = i + 1;
					BufferedImage focusImage = this.shotScreen(wx.getX() + wx.getFullWidth() * 2 / 3, wx.getY() + top,
							wx.getFullWidth() / 3 - wx.getHeight() * 29 / 460, (bottom - top) / 2);
					String regret = OCRTask.recognize(OCRTask.shrinkImage(focusImage, 2.0), false);
					Debuger.writeImage(OCRTask.shrinkImage(focusImage, 2.0), "ocr."+System.currentTimeMillis()+".bmp");
//					if (regret.indexOf('.') == -1) {
//						regret = regret.replace(" ", ".");
//					} else {
//						regret = regret.replace(" ", "");
//					}
					sit.addRedpacket(Float.parseFloat(regret));
					bottom = top = -1;
					if (image.getRGB(wx.getFullWidth() / 2, i - 2) != Weixin.whiteColor.getRGB()) {
						break;
					}
				}

			}
		}

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
			this.delay(50);
			newImage = this.shotScreen(wx.getAreaWithBottom());
		}
		return newImage;
	}

	public boolean assertWXHomePage(Weixin wx, BufferedImage image) {
		if (wx == null || image == null) {
			return false;
		}
		if (image.getRGB((int) wx.getWXHomeIconLocation().getX() - wx.getX(),
				(int) wx.getWXHomeIconLocation().getY() - wx.getY()) == Weixin.HomeIconColor.getRGB()) {
			return true;
		}
		return false;
	}

	public BufferedImage waitForChatPage(Weixin wx) {
		BufferedImage curImage = null;
		BufferedImage newImage = this.shotScreen(wx.getArea());
		while (!this.assertWXChatPage(curImage) || !assertSameImage(curImage, newImage)) {
			curImage = newImage;
			this.delay(10);
			newImage = this.shotScreen(wx.getArea());
		}
		return newImage;
	}

	public void mouseDrag(int startX, int startY, int endX, int endY) {
		this.robot.mouseMove(startX, startY);
		this.robot.mousePress(KeyEvent.BUTTON1_MASK);
		this.robot.mouseMove(endX, endY);
		this.robot.mouseRelease(KeyEvent.BUTTON1_MASK);
	}

	public boolean waitForPayPage(Weixin wx) {
		BufferedImage curImage = null;
		BufferedImage newImage = this.shotScreen(wx.getArea());
		while (!this.getWXPayPage(wx, curImage) || !assertSameImage(curImage, newImage)) {
			curImage = newImage;
			this.delay(50);
			newImage = this.shotScreen(wx.getArea());
		}
		return true;
	}

	public boolean getWXPayPage(Weixin wx, BufferedImage image) {
		if (image == null) {
			return false;
		}
		if (image.getRGB((int) wx.getInjectRedPacketButton().getX() - wx.getX(),
				(int) wx.getInjectRedPacketButton().getY() - wx.getY()) == Weixin.payPageColor.getRGB()) {
			return true;
		}
		return false;
	}

	public BufferedImage waitForSetupRedPacketPage(Weixin wx) {
		BufferedImage curImage = null;
		BufferedImage newImage = this.shotScreen(wx.getArea());
		while (!this.getWXSetupRedPacketPage(wx, curImage) || !assertSameImage(curImage, newImage)) {
			curImage = newImage;
			this.delay(50);
			newImage = this.shotScreen(wx.getArea());
		}
		return newImage;
	}

	public boolean getWXSetupRedPacketPage(Weixin wx, BufferedImage image) {
		if (image == null) {
			return false;
		}
		if (image.getRGB((int) wx.getSetupRedPacketButton().getX() - wx.getX(),
				(int) wx.getSetupRedPacketButton().getY() - wx.getY()) == Weixin.setupRedPacketColor.getRGB()) {
			return true;
		}
		return false;
	}

	public Point getLatestRedPacketPoint(Weixin wx, BufferedImage image) {
		Debuger.startTimer("test get latest red packet");
		for (int i = image.getHeight() - 1; i >= 0; i--) {
			if (image.getRGB(image.getWidth() / 2, i) == Weixin.redPacketBGColor.getRGB()) {
				return new Point(wx.getX() + image.getWidth() / 2, wx.getY() + i - 10);
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

	public void backWX(Weixin wx) {
		this.clickPos(wx.getBackButton());
	}

	public String recognize(BufferedImage bi, boolean preProcessed) {
		return OCRTask.recognize(bi, preProcessed);
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
			if (image.getRGB(wxCentralX, image.getHeight() / 2 + i) == Weixin.openRedPacketButtonColor.getRGB()) {
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
		if (image.getRGB(1, image.getHeight() - 2) == Weixin.wxBGColor.getRGB()) {
			return true;
		}
		return false;
	}

	public boolean assertWXPayPasswordPage(BufferedImage image) {
		if (image == null) {
			return false;
		}
		if (image.getRGB(1, image.getHeight() - 2) == Weixin.keyboardBGColor.getRGB()) {
			return true;
		}
		return false;
	}

	public BufferedImage waitForWXPayPasswordPage(Weixin wx) {
		BufferedImage curImage = null;
		BufferedImage newImage = this.shotScreen(wx.getAreaWithBottom());
		while (!this.assertWXPayPasswordPage(curImage) || !assertSameImage(curImage, newImage)) {
			curImage = newImage;
			this.delay(50);
			newImage = this.shotScreen(wx.getAreaWithBottom());
			Debuger.writeImage(newImage, "payPage."+System.currentTimeMillis()+".bmp");
		}
		return newImage;
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
		this.delay(100);
		this.clickPos(wx.getRedPacketButton());
		this.delay(100);
		this.pressNumber(count);
		this.delay(100);
		this.clickPos(wx.getRedAmountTextField());
		this.pressNumber(amount);
		this.delay(200);
		System.out.println("wait for setup red packet");
		this.clickPos(wx.getSetupRedPacketButton());
		if (!wx.isNeedPassword()) {
			this.waitForPayPage(wx);
		} else {
			this.waitForWXPayPasswordPage(wx);
			this.inputNumber(wx, wx.getPassword());
			this.waitForStaticPage(wx);
		}
		this.clickPos(wx.getInjectRedPacketButton());
		this.waitForRedpacketChatpage(wx);
		this.pressKey(KeyEvent.VK_ESCAPE);
	}

	public boolean findRedpacketChatpage(BufferedImage image) {
		int startX = image.getWidth() / 2;
		for (int i = image.getHeight() - 1; i >= 0; i--) {
			if (image.getRGB(startX, i) == Weixin.redPacketBGColor.getRGB()) {
				return true;
			}
		}
		return false;
	}

	public BufferedImage waitForRedpacketChatpage(Weixin wx) {
		BufferedImage curImage = null;
		BufferedImage newImage = this.shotScreen(wx.getArea());
		while (!this.findRedpacketChatpage(newImage) || !assertSameImage(curImage, newImage)) {
			curImage = newImage;
			this.delay(10);
			newImage = this.shotScreen(wx.getArea());
		}
		return newImage;
	}

	public void pressKey(int keyCode) {
		this.robot.keyPress(keyCode);
		this.robot.keyRelease(keyCode);
	}

	public void inputNumber(Weixin wx, String number) {
		for (int i = 0; i < number.length(); i++) {
			this.clickPos(wx.getKeyPoint(number.charAt(i) + ""));
		}
	}

	public void pressNumber(String number) {
		for (int i = 0; i < number.length(); i++) {
			this.pressKey(number.charAt(i));
			this.delay(30);
		}
	}

	public static void main(String... args) {
		AssistRobot ar = new AssistRobot();
		System.out.println(KeyEvent.VK_E);
		System.out.println((int) 'E');
	}
}
