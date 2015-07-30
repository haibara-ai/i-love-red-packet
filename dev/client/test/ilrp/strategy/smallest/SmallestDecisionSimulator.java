package ilrp.strategy.smallest;

import ilrp.strategy.smallest.data.SmallestRankThreshold;
import ilrp.strategy.util.RedPacketGenerator;

import java.util.Random;

/**
 * Try to see how much we can gain.
 * @author aleck
 *
 */
public class SmallestDecisionSimulator {
	// assume we can join in 80% games
	public static final double 	PARTICIPATION 	= 0.80;
	public static final double 	amount 			= 100;
	public static final int 	n 				= 4;
	public static final int 	tax 			= 10;
	private static final int 	MAX_ROUND 		= 10;
	private static final int 	MAX_ITERATION	= 1000;
	// possible acted players
	private static final int[] 	ACTED_PLAYERS	= new int[] {1};
	
	public static void main(String[] args) throws ClassNotFoundException {
//		ClassLoader.getSystemClassLoader().loadClass(SmallestProbility.class.getName());
//		ClassLoader.getSystemClassLoader().loadClass(SmallestRank.class.getName());
//		ClassLoader.getSystemClassLoader().loadClass(SmallestRankThreshold.class.getName());
		
		long seed = 1;
		Random rand = new Random(seed);
		RedPacketGenerator.setSeed(seed);
		
		SmallestDecision decision = new SmallestDecision();
		int dealerTimes		= 0;	// 参与的游戏总数
		double amountAll 	= 0;	// 总流水
		double amountJoined = 0;	// 参与的游戏
		double profit		= 0;	// 总收益
		double[] rp;
		boolean iAmDealer = false;
		int[] rounds = new int[MAX_ROUND];
		
		for (int iter = 0; iter < MAX_ITERATION; iter ++) {
			int acted = 0;
			// if I am still the dealer, I should deliver a red packet
			if (iAmDealer) {
				dealerTimes++;
				System.out.println("Hacking the system.");
				int round = -1;
				boolean decided = false;
				do {
					round++;
					rp = RedPacketGenerator.generate(amount, n);
					acted = ACTED_PLAYERS[rand.nextInt(ACTED_PLAYERS.length)];
					System.out.println(String.format("Try %d (act=%d): %s", round, acted, GameUtil.redPacketToString(rp)));
					if (acted == 1) {
						decided = decision.decide1(round, n, tax, rp[0], amount);
					} else if (acted == 2) {
						decided = decision.decide2(round, n, tax, rp[0], rp[1], amount);
					} else {
						throw new RuntimeException();
					}
				} while (!decided);
				rounds[round] ++;
				System.out.println("OK, I forward this one.");
				profit -= amount;
			} else {
				rp = RedPacketGenerator.generate(amount, n);
				System.out.println(String.format("New RP: %s", GameUtil.redPacketToString(rp)));
			}
			// there is a rp
			double min = rp[0];
			for (int j = 1; j < n; j++) {
				if (rp[j] < min)
					min = rp[j];
			}
			// race for the rp
			if (iAmDealer) {
				// I must be in the game
				amountAll += amount;
				amountJoined += amount;
				if (acted == 1) {
					profit = profit + rp[0];
					System.out.println(String.format("I got: %6.2f", rp[0]));
					iAmDealer = (rp[0] <= min + 1e-8);
					if (iAmDealer)
						System.out.println("Oops... I am still the dealer.");
				} else if (acted == 2) {
					profit = profit + rp[0] + rp[1];
					System.out.println(String.format("I got: %6.2f and %6.2f", rp[0], rp[1]));
					iAmDealer = (rp[0] <= min + 1e-8 || rp[1] <= min + 1e-8);
					if (iAmDealer)
						System.out.println("Oops... I am still the dealer.");
				} else {
					throw new RuntimeException();
				}
			} else {
				int i1 = -1;
				if (rand.nextDouble() < PARTICIPATION) {
					i1 = rand.nextInt(n);
				}
				int i2 = -1;
				if (acted == 2) {
					if (rand.nextDouble() < PARTICIPATION) {
						do {
							i2 = rand.nextInt(n);
						} while (i2 == i1);
					}
				}
				amountAll += amount;
				if (i1 == -1 && i2 == -1) {
					// others got this packet
					System.out.println("I missed this one.");
				} else {
					// I joined in the game
					amountJoined += amount;
					iAmDealer = false;
					if (i1 != -1) {
						profit += rp[i1];
						System.out.println(String.format("I got: %6.2f", rp[i1]));
						if (rp[i1] <= min + 1e-8) {
							iAmDealer = true;
							System.out.println("Oops... I am the dealer.");
						}
					}
					if (i2 != -1) {
						profit += rp[i2];
						System.out.println(String.format("I got: %6.2f", rp[i2]));
						if (rp[i2] <= min + 1e-8) {
							iAmDealer = true;
							System.out.println("Oops... I am the dealer.");
						}
					}
				}
			}
			System.out.println(String.format("--> profit=%6.2f", profit));
			System.out.println();
		}
		// print statistic
		System.out.println("---- Statistic ----");
		for (int i = 0; i < rounds.length; i++) {
			if (i > 0) {
				rounds[i] += rounds[i - 1];
			}
			System.out.println(String.format("hack in %2d round: %8d (%3.2f%%)", i, rounds[i], 100.0 * rounds[i] / dealerTimes));
		}
		System.out.println(String.format("amount.total  =%6.2f", amountAll));
		System.out.println(String.format("amount.joined =%6.2f", amountJoined));
		System.out.println(String.format("amount.profit =%6.2f", profit));
		System.out.println(String.format("dealer        =%d", dealerTimes));
		System.out.println(String.format("Profit ratio  =%3.2f%%", profit / amountJoined * 100));
		System.out.println("---- The End ----");
		SmallestRankThreshold.printThreshold();
	}
}
