package ilrp.assist;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

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

	public boolean testSame(BufferedImage image1, BufferedImage image2) {
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
	
	public BufferedImage waitForNewRedPacket(Weixin wx, BufferedImage baseImage) {
		BufferedImage newImage = null;
		do {
			newImage = this.shotScreen(wx.getArea());
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (this.testSame(baseImage, newImage));
		return newImage;
	}

	public void processNewRedPacket(Weixin wx, BufferedImage focusImage) {
		Point clickRedPacketPoint = this.getLatestRedPacketPoint(wx, focusImage);
		this.clickPos(clickRedPacketPoint);
		
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			focusImage = this.shotScreen(wx.getArea());
		} while (!this.getOpenRedPacketPage(focusImage));

		Point openRedPacketPos = this.locateOpenRedPacketPos(wx, focusImage);
		this.clickPos(openRedPacketPos);
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			focusImage = this.shotScreen(wx.getArea());
		} while (!this.getRedPacketPage(focusImage));

		Rectangle redPacketArea = this.extractRedPacketArea(focusImage);
		this.recognize(
				focusImage.getSubimage(redPacketArea.x, redPacketArea.y, redPacketArea.width, redPacketArea.height));
		this.backWX(wx.getArea());
	}

	public BufferedImage waitForChatPage(Weixin wx) {
		BufferedImage curImage = null;
		BufferedImage newImage = this.shotScreen(wx.getArea());
		while (!this.getWXChatPage(curImage) || !testSame(curImage, newImage)) {
			curImage = newImage;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			newImage = this.shotScreen(wx.getArea());
		}
		return newImage;
	}
	
	public Point getLatestRedPacketPoint(Weixin wx, BufferedImage image) {
		for (int i = image.getHeight() - 1; i >= 0; i--) {
			if (image.getRGB(image.getWidth() / 2, i) == Weixin.redPacketBGColor.getRGB()) {
				return new Point(wx.getX() + image.getWidth() / 2, wx.getY() + i);
			}
		}
		return null;
	}

	public void delay(int ms) {
		robot.delay(ms);
	}

	public Point getCenterPoint(Rectangle input) {
		return new Point(input.x + input.width / 2, input.y + input.height / 2);
	}

	public Point locateOpenRedPacketPos(Weixin wx, BufferedImage image) {
		for (int i = 0; i < image.getHeight() / 2; i++) {
			if (image.getRGB(image.getWidth() / 2, image.getHeight() - i - 1) == Weixin.openRedPacketBGColor.getRGB()) {
				return new Point(wx.getX() + image.getWidth() / 2, wx.getY() + image.getHeight() - i -1);
			}
		}
		return null;
	}

	public void backWX(Rectangle wxArea) {
		this.clickPos(new Point(wxArea.x, wxArea.y - 2));
	}

	public Rectangle extractRedPacketArea(BufferedImage image) {
		int bottom = -1;
		int top = -1;
		// find bottom
		for (int i = 0; i < image.getHeight() / 2; i++) {
			if (bottom == -1) {
				for (int j = 0; j < image.getWidth() / 2; j++) {
					int red = (image.getRGB(image.getWidth() / 2 + j, image.getHeight() / 2 + i) & 0xff0000) >> 16;
					if (red != 255) {
						break;
					} else if (j == image.getWidth() / 2 - 1) {
						bottom = image.getHeight() / 2 + i;
					}
				}
			}

			if (top == -1) {
				for (int j = 0; j < image.getWidth() / 2; j++) {
					int red = (image.getRGB(image.getWidth() / 2 + j, image.getHeight() / 2 - i) & 0xff0000) >> 16;
					if (red != 255) {
						break;
					} else if (j == image.getWidth() / 2 - 1) {
						top = image.getHeight() / 2 - i;
					}
				}
			}
			if (bottom != -1 && top != -1) {
				break;
			}
		}

		return new Rectangle(0, top, image.getWidth(), bottom - top + 1);
	}

	public void recognize(BufferedImage bi) {
		(new Thread(new OCRTask(bi))).start();
	}

	public void clickPos(Point pos) {
		if (pos == null) {
			return;
		}
		robot.mouseMove(pos.x, pos.y);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	public boolean getOpenRedPacketPage(BufferedImage image) {
		int wxCentralX = image.getWidth() / 2;
		for (int i = 0; i < image.getHeight() / 2; i++) {
			if (image.getRGB(wxCentralX, image.getHeight() / 2 + i) == Weixin.openRedPacketBGColor.getRGB()) {
				return true;
			}
		}
		// test if is opened redpacket
		
		return false;
	}

	public boolean getWXChatPage(BufferedImage image) {
		if (image == null) {
			return false;
		}
		if (image.getRGB(1, 1) == Weixin.wxBGColor.getRGB()) {
			return true;
		}
		return false;
	}

	public boolean getRedPacketPage(BufferedImage image) {
		for (int i = 0; i < image.getHeight() / 2; i++) {
			if (image.getRGB(image.getWidth() / 2, i) == Weixin.redPacketPageColor.getRGB()) {
				return true;
			}
		}
		return false;
	}
	
	public void postRedPacket(Weixin wx, String count, String amount) {
		System.out.println(wx.getPostButton().getX() + wx.getPostButton().getY());
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
		this.delay(500);
		this.clickPos(wx.getSetupRedPacketButton());
		this.delay(1000);
		this.clickPos(wx.getInjectRedPacketButton());
	}
	
	public void inputNumber(Weixin wx, String number) {
		for (int i = 0 ; i < number.length(); i++) {
			System.out.println(number.charAt(i));
			System.out.println(wx.getKeyPoint(number.charAt(i)+"").getX()+":"+wx.getKeyPoint(number.charAt(i)+"").getY());
			this.clickPos(wx.getKeyPoint(number.charAt(i)+""));
		}
	}
	
	public static void main(String... args) {
		AssistRobot ar = new AssistRobot();
	}
}
