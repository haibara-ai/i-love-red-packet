package ilrp.strategy.smallest;

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
	/**
	 * Whether the current on is good enough
	 * @param round 		0th, 1st, 2nd, 3rd, etc., it starts from 0
	 * @param n				number of players
	 * @param tax			the percentage of tax, e.g. 80 out of 100, tax = 20
	 * @param x1			alice
	 * @param x2			bob
	 * @param amount		amount of the red packet (after tax)
	 * @return whether to send this one?
	 */
	public boolean decide(int round, int n, int tax, double x1, double x2, double amount) {
		double threshold = SmallestRankThreshold.THRESHOLDS[round];
		double exp = GameUtil.expectation(n, tax, x1 / amount, x2 / amount);
		double rank = SmallestRank.rank(n, tax, exp);
		// use 1e-8, therefore, it is safe to use 1.00 in the threshold.
		return rank <= threshold + 1e-8;
	}
}
