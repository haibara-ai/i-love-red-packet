package ilrp.assist;

import java.awt.image.BufferedImage;

public class Driver {

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
		
		BufferedImage focusImage = ar.shotScreen(wx1.getArea());
		do {
			System.out.println("once");
			focusImage = ar.waitForNewRedPacket(wx1, focusImage);
			ar.processNewRedPacket(wx1, focusImage);
			focusImage = ar.waitForChatPage(wx1);
		} while (true);
	}

	public static void main(String[] args) {
		Driver ad = new Driver();
		ad.run();
	}

}
