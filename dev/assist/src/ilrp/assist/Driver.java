package ilrp.assist;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.text.StyleContext.SmallAttributeSet;

import ilrp.strategy.smallest.SmallestDecision;
import ilrp.ui.assist.Rectangle;
import ilrp.ui.assist.WindowUtil;

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
		
		List<Rectangle> simulators = WindowUtil.locateWindow("º£ÂíÍæ", false);
		// if simulator.count == 2, left is alice, right is bob
		// if simulator.count == 1, it is alice
		if (simulators.size() == 1) {
			Rectangle s1 = simulators.get(0);
			wx1 = new Weixin("alice");			
			wx1.init(new Point(s1.left,s1.top),ar.shotScreen(s1.left,s1.top,s1.right-s1.left+1,s1.bottom-s1.top+1));
			if(!wx1.inited()) {
				System.err.println("Weixin1 initialized error");
				return;
			}
		} else if (simulators.size() == 2) {			
			Rectangle s1 = simulators.get(0);
			Rectangle s2 = simulators.get(1);
			if (simulators.get(0).left < simulators.get(1).left) {
				s1 = simulators.get(1);
				s2 = simulators.get(0);
			}
			wx1 = new Weixin("alice");			
			wx1.init(new Point(s1.left,s1.top),ar.shotScreen(s1.left,s1.top,s1.right-s1.left+1,s1.bottom-s1.top+1));
			if(!wx1.inited()) {
				System.err.println("Weixin1 initialized error");
				return;
			}
			wx2 = new Weixin("bob");			
			wx2.init(new Point(s2.left,s2.top),ar.shotScreen(s2.left,s2.top,s2.right-s2.left+1,s2.bottom-s2.top+1));
			if(!wx2.inited()) {
				System.err.println("Weixin2 initialized error");
				return;
			}
		} else {
			System.err.println("too many haimawan simulators!");
			return;
		}
		// config wx location
//		this.initWXArea(wx1);

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
//		ar.backWX(wx1);
//		ar.waitForWXHomePage(wx1);
//		ar.enterChatGroup(wx1, "innergroup");
//		ar.postRedPacket(wx1, "1", "0.01");
		this.rushRedPacket(wx1);
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
	
//	public void clickRedPacket(Weixin wx) {
//		ar.clickRedPacket(wx);
//	}
	
//	public void postRedPacket(Weixin wx, String count, String amount) {
//		ar.postRedPacket(wx, count, amount);
//	}
	
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
		boolean firstRound = true;
		ar.waitForNewRedPacket(wx1, ar.shotScreen(wx1.getArea()));		
		do {
			Situation sit = new Situation();	
			System.out.println(wx1.getName() + " get new red packet");	
			do {
				ar.processNewRedPacket(wx1, true, sit);
				ar.backWX(wx1);
				ar.waitForChatPage(wx1);
			} while (!sit.getOver());
//			if (openGroup) {
//				while (!sit.getOver()) {
//					this.backWX(wx);
//					this.waitForChatPage(wx);
//					this.processNewRedPacket(wx, openGroup, sit);
//				}
//			} else {
//				System.out.println("small group " + sit.toString());
//				while (sit.getPacketsCount() != 2) {
//					this.backWX(wx);
//					this.waitForChatPage(wx);
//					sit = this.processNewRedPacket(wx, openGroup, sit);
//				}
//			}

			System.out.println("open group situation:" + sit.toString());
			ar.backWX(wx1);
			ar.waitForChatPage(wx1);
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
				ar.enterChatGroup(wx1, "innergroup",true);
				Debuger.startTimer("private group red packet:");
				do {
					round++;
					System.out.println("round:"+round);

//					ar.clickPos(wx1.getTop2ChatGroupLocation());
					ar.waitForChatPage(wx1);
					ar.postRedPacket(wx1, n+"", amount+"");
//					ar.delay(100);
//					ar.backWX(wx2);
//					ar.clickPos(wx2.getTop2ChatGroupLocation());
//					ar.clickRedPacket(wx2);
//					ar.backWX(wx1);
					ar.waitForChatPage(wx1);
					sit = new Situation();
					ar.processNewRedPacket(wx1, false, sit);
					ar.backWX(wx1);
					System.out.println("private group situation:"+sit.toString());
				} while (sd.decide2(round, n, 0, sit.getRedpackets().get(0), sit.getRedpackets().get(1), amount));
				Debuger.stopTimer("private group red packet:",true);
				ar.viewAndTransferRedPacket(wx1, ar.waitForChatPage(wx1), "zhaohongbao");
				ar.backWX(wx1);
				ar.waitForChatPage(wx1);
				ar.backWX(wx1);
				ar.waitForSearchPage(wx1);
				ar.backWX(wx1);
				ar.waitForWXHomePage(wx1);
//				ar.backWX(wx1);
				ar.enterChatGroup(wx1, "zhaohongbao",true);
				firstRound = false;
			}
		} while (working);
	}
	
	private void rushRedPacket(Weixin wx1) {
//		BufferedImage focusImage = null;
		boolean firstRound = true;
		ar.waitForNewRedPacket(wx1, ar.shotScreen(wx1.getArea()));		
		do {
			Situation sit = new Situation();	
			System.out.println(wx1.getName() + " get new red packet");			
			ar.processNewRedPacket(wx1, true, sit);
			
			System.out.println("open group situation:" + sit.toString());
			ar.backWX(wx1);
			ar.waitForChatPage(wx1);
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
				ar.enterChatGroup(wx1, "innergroup",true);
				Debuger.startTimer("private group red packet:");
				do {
					round++;
					System.out.println("round:"+round);

//					ar.clickPos(wx1.getTop2ChatGroupLocation());
					ar.waitForChatPage(wx1);
					ar.postRedPacket(wx1, n+"", amount+"");
//					ar.delay(100);
//					ar.backWX(wx2);
//					ar.clickPos(wx2.getTop2ChatGroupLocation());
//					ar.clickRedPacket(wx2);
//					ar.backWX(wx1);
					ar.waitForChatPage(wx1);
					sit = new Situation();
					ar.processNewRedPacket(wx1, false, sit);
					ar.backWX(wx1);
					System.out.println("private group situation:"+sit.toString());
				} while (sd.decide2(round, n, 0, sit.getRedpackets().get(0), sit.getRedpackets().get(1), amount));
				Debuger.stopTimer("private group red packet:",true);
				ar.viewAndTransferRedPacket(wx1, ar.waitForChatPage(wx1), "zhaohongbao");
				ar.backWX(wx1);
				ar.waitForChatPage(wx1);
				ar.backWX(wx1);
				ar.waitForSearchPage(wx1);
				ar.backWX(wx1);
				ar.waitForWXHomePage(wx1);
//				ar.backWX(wx1);
				ar.enterChatGroup(wx1, "zhaohongbao",true);
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
