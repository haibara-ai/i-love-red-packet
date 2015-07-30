package ilrp.strategy.smallest;

import ilrp.strategy.smallest.data.SmallestProbility;
import ilrp.strategy.smallest.data.SmallestRank;
import ilrp.strategy.smallest.data.SmallestRankThreshold;

/**
 * Decision maker for the game:
 * 
 * 		The one gets the smallest amount continues to send red packet.
 * 
 * @author Min
 *
 */
public class SmallestDecision {
	private static final double MINORITY = 0.05;
	
	static {
		// force initialize of required data classes.
		initialize();
	}
	
	/**
	 * To avoid unexpected initialization of static fields
	 * Initialize it now
	 */
	public static void initialize() {
		SmallestProbility.initialize();
		SmallestRank.initialize();
	}
	
	/**
	 * Whether the current one is good enough
	 * @param round 		0th, 1st, 2nd, 3rd, etc., it starts from 0
	 * @param n				number of players
	 * @param tax			the percentage of tax, e.g. 80 out of 100, tax = 20
	 * @param x1			alice or bob
	 * @param amount		amount of the red packet (after tax)
	 * @return whether to send this one?
	 */
	public boolean decide1(int round, int n, int tax, double x1, double amount) {
		double threshold = SmallestRankThreshold.THRESHOLDS[round];
		double p1 = x1 / amount;
		if (p1 < MINORITY) {
			// e.g. 0.02
			// I can not make good decision in this case
			if (threshold + 1e-8 > 1) {
				// I have no choice
				return true;
			} else {
				// refuse
				return false;
			}
		} else {
			// normal decision
			double exp = GameUtil.expectation(tax, n, p1);
			double rank = SmallestRank.rank(tax, n, 1, exp);
			// use 1e-8, therefore, it is safe to use 1.00 in the threshold.
			return rank <= threshold + 1e-8;
		}
	}
	
	/**
	 * Whether the current one is good enough
	 * @param round 		0th, 1st, 2nd, 3rd, etc., it starts from 0
	 * @param n				number of players
	 * @param tax			the percentage of tax, e.g. 80 out of 100, tax = 20
	 * @param x1			alice
	 * @param x2			bob
	 * @param amount		amount of the red packet (after tax)
	 * @return whether to send this one?
	 */
	public boolean decide2(int round, int n, int tax, double x1, double x2, double amount) {
		double threshold = SmallestRankThreshold.THRESHOLDS[round];
		double p1 = x1 / amount;
		double p2 = x2 / amount;
		if (p1 + p2 < 1 - MINORITY && (p1 < MINORITY || p2 < MINORITY)) {
			// e.g. 0.92 + 0.02
			// I can not make good decision in this case
			if (threshold + 1e-8 > 1) {
				// I have no choice
				return true;
			} else {
				// refuse
				return false;
			}
		} else {
			// normal decision
			double exp = GameUtil.expectation(tax, n, p1, p2);
			double rank = SmallestRank.rank(tax, n, 2, exp);
			// use 1e-8, therefore, it is safe to use 1.00 in the threshold.
			return rank <= threshold + 1e-8;
		}
	}
}
