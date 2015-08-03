package ilrp.assist;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.text.StyleContext.SmallAttributeSet;

import ilrp.strategy.smallest.SmallestDecision;

public class Driver implements Runnable{

	protected CaptureWindow cw = null;
	protected AssistRobot ar = null;
	protected Weixin wx1 = null;
	protected Weixin wx2 = null;
	
	protected SmallestDecision sd = new SmallestDecision();
	
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
		wx1 = new Weixin("alice");
		// config wx location
		this.initWXArea(wx1);
		if(!wx1.inited()) {
			System.err.println("Weixin1 initialized error");
			return;
		}
//		wx2 = new Weixin("bob");
//		this.initWXArea(wx2);
//		if (!wx2.inited()) {
//			System.err.println("Weixin2 initialized error");
//			return;
//		}
//		
//		ar.waitForRedPacketPage(wx1);
//		System.out.println(ar.getRedPacketOverState(wx1, ar.shotScreen(wx1.getArea())));
//		ar.waitForWXHomePage(wx1);
//		ar.enterChatGroup(wx1, "zhaohongbao");
//		ar.waitForSearchPage(wx1);
//		System.out.println("search page");
//		ar.waitForSearchResultPage(wx1);
//		System.out.println("search result here");
		this.rushRedPacket(wx1,wx2);
//		ar.viewAndTransferRedPacket(wx1, ar.waitForChatPage(wx1), "zhaohongbao");
//		this.rushRedPacket(wx1);
//		this.postRedPacket(wx1, "5", "0.05");
//		this.clickRedPacket(wx1);
		
//		while(true) {
//			BufferedImage focusImage = ar.waitForChatPage(wx1);
//			long startTime = System.currentTimeMillis();
//			ar.viewAndTransferRedPacket(wx1, focusImage);
//			System.out.println("view and transfer:"+(System.currentTimeMillis()-startTime));
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			break;
//		}
//		ar.postRedPacket(wx1,"1","0.01");	
//		ar.clickRedPacket(wx1);
	}
	
	public void clickRedPacket(Weixin wx) {
		ar.clickRedPacket(wx);
	}
	
	public void postRedPacket(Weixin wx, String count, String amount) {
		ar.postRedPacket(wx, count, amount);
	}
	
	public void setupWeixin() {
		wx1 = new Weixin("alice");
		this.initWXArea(wx1);
		if(!wx1.inited()) {
			return;
		}
	}
	
	public boolean turnMaster = true;
	public boolean shouldTransfer = true;
	
	private void rushRedPacket(Weixin wx1, Weixin wx2) {
		BufferedImage focusImage = ar.shotScreen(wx1.getArea());
		Situation sit = null;
		boolean firstRound = true;
		do {
			ar.delay(1000);
			focusImage = ar.waitForNewRedPacket(wx1, focusImage);			
			System.out.println(wx1.getName() + " get new red packet");			
			sit = ar.processNewRedPacket(wx1, focusImage, true);
			
			System.out.println(sit.toString());
			ar.backWX(wx1);
			focusImage = ar.waitForChatPage(wx1);
			if (sit.getMin() == sit.getMyPacket()) {
				turnMaster = true;
			}
			if (turnMaster) {
				System.out.println("turn master");
				int round = 0;
				int n = 4;
				float amount = 0.1f;
				ar.backWX(wx1);
				System.out.println("first round:"+firstRound);
				if (!firstRound) {
					ar.backWX(wx1);
				}
				ar.waitForWXHomePage(wx1);				
				ar.enterChatGroup(wx1, "privateGroup");
				do {
					round++;
					System.out.println("round:"+round);

//					ar.clickPos(wx1.getTop2ChatGroupLocation());
					ar.waitForChatPage(wx1);
					ar.postRedPacket(wx1, n+"", amount+"");
					ar.delay(100);
//					ar.backWX(wx2);
//					ar.clickPos(wx2.getTop2ChatGroupLocation());
//					ar.clickRedPacket(wx2);
//					ar.backWX(wx1);
					sit = ar.processNewRedPacket(wx1, ar.waitForChatPage(wx1), false);
					ar.backWX(wx1);
					System.out.println(sit.toString());
				} while (sd.decide2(round, n, 0, sit.getRedpackets().get(0), sit.getRedpackets().get(1), amount));
				ar.viewAndTransferRedPacket(wx1, ar.waitForChatPage(wx1), "zhaohongbao");
				ar.backWX(wx1);
				ar.waitForChatPage(wx1);
				ar.backWX(wx1);
				ar.waitForSearchPage(wx1);
				ar.backWX(wx1);
				ar.waitForWXHomePage(wx1);
//				ar.backWX(wx1);
				ar.enterChatGroup(wx1, "zhaohongbao");
				firstRound = false;
			}
		} while (working);
	}
	
	private boolean working = true;
	
	public static void main(String[] args) {
		Driver ad = new Driver();
		ad.run();
	}

}
