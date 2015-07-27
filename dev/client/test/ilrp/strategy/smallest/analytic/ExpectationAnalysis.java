package ilrp.strategy.smallest.analytic;

import ilrp.strategy.smallest.GameUtil;
import ilrp.strategy.smallest.SmallestDecision;
import ilrp.strategy.smallest.data.SmallestRank;

/**
 * Expectation of benefit
 * @author aleck
 *
 */
public class ExpectationAnalysis {

	public static void main(String[] args) {
		SmallestDecision.initialize();
		for (int n = GameUtil.MIN_PLAYERS; n <= GameUtil.MAX_PLAYERS; n++) {
			for (int tax : GameUtil.TAXES) {
				for (int m : GameUtil.ACTED_PLAYERS) {
					double expectation = SmallestRank.estimateExpectation(tax, n, m);
					System.out.println(String.format("EXP[n=%d,m=%d,tax=%d%%]=%1.4f", n, m, tax, expectation));
				}
			}
		}
	}

}
