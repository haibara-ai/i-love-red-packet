package ilrp.assist;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.text.StyleContext.SmallAttributeSet;

import ilrp.strategy.smallest.SmallestDecision;
import ilrp.ui.assist.Rectangle;
import ilrp.ui.assist.WindowUtil;

public class Driver implements Runnable {

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
		wx.init(cw.mouseStartPoint, ar.shotScreen(cw.mouseStartPoint.x, cw.mouseStartPoint.y,
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
			wx1.init(new Point(s1.left, s1.top),
					ar.shotScreen(s1.left, s1.top, s1.right - s1.left + 1, s1.bottom - s1.top + 1));
			if (!wx1.inited()) {
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
			wx1.init(new Point(s1.left, s1.top),
					ar.shotScreen(s1.left, s1.top, s1.right - s1.left + 1, s1.bottom - s1.top + 1));
			if (!wx1.inited()) {
				System.err.println("Weixin1 initialized error");
				return;
			}
			wx1.setUserProfile("babytoo2228", true, "890228");
			wx2 = new Weixin("bob");
			wx2.init(new Point(s2.left, s2.top),
					ar.shotScreen(s2.left, s2.top, s2.right - s2.left + 1, s2.bottom - s2.top + 1));
			if (!wx2.inited()) {
				System.err.println("Weixin2 initialized error");
				return;
			}
			wx2.setUserProfile("blackthree", false, "");
		} else {
			System.err.println("too many haimawan simulators!");
			return;
		}
//		ar.postRedPacket(wx2, "2", "0.03");
		this.rushRedPacket(wx1,wx2);
	}

	public void setupWeixin() {
		wx1 = new Weixin("alice");
		this.initWXArea(wx1);
		if (!wx1.inited()) {
			return;
		}
	}

	public boolean turnMaster = true;
	public boolean shouldTransfer = true;

	private void rushRedPacket(Weixin wx1) {
		boolean firstRound = true;
		ar.waitForNewRedPacket(wx1, ar.shotScreen(wx1.getArea()));
		do {
			Situation sit = new Situation();
			System.out.println(wx1.getName() + " get new red packet");
			do {
				ar.processNewRedPacket(wx1, true, sit, true);
				ar.backWX(wx1);
				ar.waitForChatPage(wx1);
			} while (!sit.getOver());

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
				float amount = 1f;
				ar.backWX(wx1);
				System.out.println("first round:" + firstRound);
				if (!firstRound) {
					ar.backWX(wx1);
				}
				ar.waitForWXHomePage(wx1);
				ar.enterChatGroup(wx1, "innergroup", true);
				Debuger.startTimer("private group red packet:");
				do {
					round++;
					System.out.println("round:" + round);
					ar.waitForChatPage(wx1);
					ar.postRedPacket(wx1, n + "", amount + "");
					sit = new Situation();
					while (sit.getPacketsCount() != 2) {
						ar.waitForChatPage(wx1);
						ar.processNewRedPacket(wx1, false, sit, true);
						ar.backWX(wx1);
					}
					System.out.println("private group situation:" + sit.toString());
				} while (sd.decide2(round, n, 0, sit.getRedpackets().get(0), sit.getRedpackets().get(1), amount));
				Debuger.stopTimer("private group red packet:", true);
				ar.viewAndTransferRedPacket(wx1, ar.waitForChatPage(wx1), "zhaohongbao");
				ar.backWX(wx1);
				ar.waitForChatPage(wx1);
				ar.backWX(wx1);
				ar.waitForSearchPage(wx1);
				ar.backWX(wx1);
				ar.waitForWXHomePage(wx1);
				ar.enterChatGroup(wx1, "zhaohongbao", true);
				firstRound = false;
			}
		} while (working);
	}

	private void rushRedPacket(Weixin wx1, Weixin wx2) {
		boolean firstRound = true;
		do {			
			Situation sit = new Situation();
			Situation sit2 = new Situation();
			System.out.println(wx1.getName() + " get new red packet");
			ar.waitForNewRedPacket(wx1, ar.shotScreen(wx1.getArea()));
			boolean rushAG = false;
			do {
				ar.processNewRedPacket(wx1, true, sit, true);
				if (sit.isRushedByOthers()) {
					ar.pressKey(KeyEvent.VK_ESCAPE);
				} else {
					ar.backWX(wx1);
					ar.waitForChatPage(wx1);
				}
				ar.processNewRedPacket(wx2, true, sit2, false);				
				if (sit2.isRushedByOthers()) {
					ar.pressKey(KeyEvent.VK_ESCAPE);
				} else {
					ar.backWX(wx2);
					ar.waitForChatPage(wx2);
				}
				
				if (sit.isRushedByOthers() && sit2.isRushedByOthers()) {
					rushAG = true;
					break;
				}				
			} while (!sit.getOver());
			if (rushAG) {
				continue;
			}
			System.out.println("open group situation:" + sit.toString());
			if (sit.getMin() == sit.getMyPacket()) {
				turnMaster = true;
			} else if (sit.getMin() == sit2.getMyPacket()) {
				System.out.println("turn around");
				turnMaster = true;
				Weixin wx3 = wx1;
				wx1 = wx2;
				wx2 = wx3;
			} else {
				continue;
			}
			if (turnMaster) {
				System.out.println(wx1.getName() + " turn master");
				int round = 0;
				int n = 4;
				float amount = 0.1f;
				ar.backWX(wx1);
				System.out.println("first round:" + firstRound);
				if (!firstRound) {
					ar.backWX(wx1);
				}
				ar.waitForWXHomePage(wx1);
				ar.enterChatGroup(wx1, "innergroup", true);
				ar.backWX(wx2);
				ar.waitForWXHomePage(wx2);
				ar.enterChatGroup(wx2, "innergroup", true);

				Debuger.startTimer("private group red packet:");
				do {
					System.out.println("round:" + round);

					ar.waitForChatPage(wx1);
					ar.postRedPacket(wx1, n + "", amount + "");
					ar.waitForChatPage(wx2);
					sit = new Situation();
					sit2 = new Situation();
//					ar.waitForNewRedPacket(wx2, ar.shotScreen(wx2.getArea()));
					ar.delay(100);
					ar.processNewRedPacket(wx2, false, sit2, false);
					ar.backWX(wx2);
					ar.waitForChatPage(wx2);
					
					ar.processNewRedPacket(wx1, false, sit, true);
					ar.backWX(wx1);
					ar.waitForChatPage(wx1);
					System.out.println("private group situation:" + sit.toString());
				} while (sd.decide2(round++, n, 0, sit.getRedpackets().get(0), sit.getRedpackets().get(1), amount));
				Debuger.stopTimer("private group red packet:", true);
				ar.viewAndTransferRedPacket(wx1, ar.waitForChatPage(wx1), "zhaohongbao");
				ar.backWX(wx1);
				ar.waitForChatPage(wx1);
				ar.backWX(wx1);
				ar.waitForSearchPage(wx1);
				ar.backWX(wx1);
				ar.waitForWXHomePage(wx1);				
				ar.enterChatGroup(wx1, "zhaohongbao", true);
				
				ar.backWX(wx2);
				ar.waitForSearchPage(wx2);
				ar.backWX(wx2);
				ar.waitForWXHomePage(wx2);
				ar.enterChatGroup(wx2, "zhaohongbao", true);
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
