package ilrp.assist;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Driver implements Runnable{

	protected CaptureWindow cw = null;
	protected AssistRobot ar = null;

	public Driver() {
		ar = new AssistRobot();
	}
	
	private void initWXArea(Weixin wx) {
		cw = new CaptureWindow();
		while (!cw.getReady()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// wx location ready
		wx.init(cw.mouseStartPoint,ar.shotScreen(cw.mouseStartPoint.x, cw.mouseStartPoint.y,
				cw.mouseEndPoint.x - cw.mouseStartPoint.x + 1, cw.mouseEndPoint.y - cw.mouseStartPoint.y + 1));
		cw.dispose();
	}
	
	public void run() {
		Weixin wx1 = new Weixin();
		// config wx location
		this.initWXArea(wx1);
		if(!wx1.inited()) {
			return;
		}
		System.out.println("1");
		ar.postRedPacket(wx1,"2","0.02");	
//		ar.delay(500);
//		ar.clickPos(wx1.getWritePageBlankButton());
		this.clickRedPacket(wx1);
	}
	
	private void clickRedPacket(Weixin wx1) {
		BufferedImage focusImage = ar.waitForChatPage(wx1);
		try {
			ImageIO.write(focusImage, "png", new File("D:\\github\\i-love-red-packet\\dev\\assist\\focusImage.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("get new red packet");
		long startTime = System.currentTimeMillis();
		ar.processNewRedPacket(wx1, focusImage);
		ar.clickPos(wx1.getTransferRedPacketButton());
		ar.delay(100);
		ar.clickPos(wx1.getTop1GroupLocation());
		ar.delay(300);
		ar.clickPos(wx1.getSendTransferRedPacketButton());
		long endTime = System.currentTimeMillis();
		System.out.println(endTime-startTime);
	}
	
	private void rushRedPacket(Weixin wx1) {
		BufferedImage focusImage = ar.shotScreen(wx1.getArea());
		do {
			focusImage = ar.waitForNewRedPacket(wx1, focusImage);
			
			System.out.println("get new red packet");
			long startTime = System.currentTimeMillis();
			ar.processNewRedPacket(wx1, focusImage);
			ar.backWX(wx1.getArea());
			long endTime = System.currentTimeMillis();
			System.out.println(endTime-startTime);
			focusImage = ar.waitForChatPage(wx1);
		} while (working);
	}
	
	private boolean working = true;
	
	public static void main(String[] args) {
		Driver ad = new Driver();
		ad.run();
	}

}
