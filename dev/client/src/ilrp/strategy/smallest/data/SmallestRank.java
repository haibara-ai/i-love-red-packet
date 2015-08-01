package ilrp.strategy.smallest.data;

import ilrp.strategy.smallest.GameUtil;
import ilrp.strategy.smallest.gen.DataPrecision;
import ilrp.strategy.smallest.gen.SmallestRankGenerator;

import java.io.IOException;
import java.util.Arrays;

// assume x1+x2+x3+x4=1
// let event S = min(x1,x2) < min(x3,x4)
// P(S) is obtained from SmallestProbility
// let E = (x1+x2-1)*P(S) + (x1+x2)*P(not S)
// this is a collection of evenly sample E
public class SmallestRank {
	private static final double[][][] data;
	
	static {
		System.out.print("Loading rank ... ");
		int maxTax = GameUtil.TAXES[0];
		for (int tax : GameUtil.TAXES) {
			if (tax > maxTax)
				maxTax = tax;
		}
		data = new double[GameUtil.MAX_PLAYERS + 1][maxTax + 1][];
		for (int n = GameUtil.MIN_PLAYERS; n <= GameUtil.MAX_PLAYERS; n++) {
			for (int tax : GameUtil.TAXES) {
				String filename = SmallestRankGenerator.getRankFilename(
						n,
						tax,
						DataPrecision.RANK_SAMPLES,
						DataPrecision.RANK_RAW_SAMPLES
						);
				try (DataLoader loader = new DataLoader(filename)) {
					data[n][tax] = loader.readColumn();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException("error loading data file: " + filename);
				}
			}
		}
		System.out.println("OK");
	}

	/**
	 * compute rank of the current solution
	 * i.e. return = 0.1 => it's among the best 10%.
	 * 
	 * @param the expectation
	 * @param total
	 * @return
	 */
	public static double rank(int n, int tax, double exp) {
		double[] samples = data[n][tax];
		int index = Arrays.binarySearch(samples, exp);
		int position = (index >= 0) ? index : -(index + 1);	// [0, length]
		return 1.0 - 1.0 * position / samples.length;
	}
	
	/**
	 * A dummy method, just to force initialization of this class
	 */
	public static void initialize() {
	}
};
