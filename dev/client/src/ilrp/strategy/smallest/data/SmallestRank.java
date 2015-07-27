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
	// acted players = 1
	private static final double[][][] data1;
	// acted players = 2
	private static final double[][][] data2;
	
	static {
		System.out.print("Loading rank ... ");
		int maxTax = GameUtil.TAXES[0];
		for (int tax : GameUtil.TAXES) {
			if (tax > maxTax)
				maxTax = tax;
		}
		data1 = new double[GameUtil.MAX_PLAYERS + 1][maxTax + 1][];
		data2 = new double[GameUtil.MAX_PLAYERS + 1][maxTax + 1][];
		for (int n = GameUtil.MIN_PLAYERS; n <= GameUtil.MAX_PLAYERS; n++) {
			for (int tax : GameUtil.TAXES) {
				for (int m : GameUtil.ACTED_PLAYERS) {
					String filename = SmallestRankGenerator.getRankFilename(
							n,
							m,
							tax,
							DataPrecision.RANK_SAMPLES,
							DataPrecision.RANK_RAW_SAMPLES
							);
					try (DataLoader loader = new DataLoader(filename)) {
						if (m == 1) {
							data1[n][tax] = loader.readColumn();
						} else if (m == 2) {
							data2[n][tax] = loader.readColumn();
						} else {
							System.err.println("[SmallestRank] Error: Unhandled number of acted players " + m);
						}
					} catch (IOException e) {
						e.printStackTrace();
						throw new RuntimeException("error loading data file: " + filename);
					}
				}
			}
		}
		System.out.println("OK");
	}

	/**
	 * compute rank of the current solution
	 * i.e. return = 0.1 => it's among the best 10%.
	 *
	 * @param tax
	 * @param n		#players
	 * @param m		#acted players
	 * @return
	 */
	public static double rank(int tax, int n, int m, double exp) {
		double[] samples;
		if (m == 1) {
			samples = data1[n][tax];
		} else if (m == 2) {
			samples = data2[n][tax];
		} else {
			throw new IllegalArgumentException("[SmallestRank] Error: unhandled number of acted players: " + m);
		}
		int index = Arrays.binarySearch(samples, exp);
		int position = (index >= 0) ? index : -(index + 1);	// [0, length]
		return 1.0 - 1.0 * position / samples.length;
	}
	
	/**
	 * π¿À„∏≈¬ 
	 * @param m 
	 * @param n 
	 * @param tax 
	 * @return
	 */
	public static double estimateExpectation(int tax, int n, int m) {
		double[] r = null;
		if (m == 1) {
			r = data1[n][tax];
		} else if (m == 2) {
			r = data2[n][tax];
		} else {
			throw new RuntimeException();
		}
		double sum = 0;
		for (int i = 0; i < r.length; i++) {
			sum += r[i];
		}
		return (sum / r.length);
	}
	
	/**
	 * A dummy method, just to force initialization of this class
	 */
	public static void initialize() {
	}

	/**
	 * Warning: return the data reference directly, do NOT modify the return value
	 * @param tax
	 * @param n
	 * @param m
	 * @return
	 */
	public static double[] getData(int tax, int n, int m) {
		if (m == 1) {
			return data1[n][tax];
		} else if (m == 2) {
			return data2[n][tax];
		} else {
			throw new RuntimeException();
		}
	}
};
