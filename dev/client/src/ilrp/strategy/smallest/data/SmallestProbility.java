package ilrp.strategy.smallest.data;

import ilrp.strategy.smallest.GameUtil;
import ilrp.strategy.smallest.gen.DataPrecision;
import ilrp.strategy.smallest.gen.SmallestProbabilityGenerator;

import java.io.IOException;

public class SmallestProbility {
	// 1 acted player
	private static final double[][] data1;
	// 2 acted player
	private static final double[][][] data2;
	
	static {
		System.out.print("Loading probability matrix... ");
		data1 = new double[GameUtil.MAX_PLAYERS + 1][];
		data2 = new double[GameUtil.MAX_PLAYERS + 1][][];
		for (int n = GameUtil.MIN_PLAYERS; n <= GameUtil.MAX_PLAYERS; n++) {
			for (int m : GameUtil.ACTED_PLAYERS) {
				String filename = SmallestProbabilityGenerator.getProbFilename(
						n,	// all players
						m,	// acted players
						DataPrecision.PROB_DIV, 
						DataPrecision.PROB_AVERAGE_DUPLICATION
						);
				try (DataLoader loader = new DataLoader(filename)) {
					if (m == 1) {
						data1[n] = loader.readColumn();
					} else if (m == 2) {
						data2[n] = loader.readTable();
					} else {
						System.err.println("[SmallestProbability] Error: Unhandled number of acted players " + m);
					}
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException("error loading data file: " + filename);
				}
			}
		}
		System.out.println("OK");
	}

	/**
	 * p1, p2 are proportions
	 * @param n
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static double getValue(int n, double p1, double p2) {
		if (0 <= p1 && p1 <= 1 && 0 <= p2 && p2 <= 1) {
			int i = (int) (p1 * (DataPrecision.PROB_DIV - 1));
			int j = (int) (p2 * (DataPrecision.PROB_DIV - 1));
			return data2[n][i][j];
		} else {
			throw new IllegalArgumentException("p1=" + p1 + ", p2=" + p2);
		}
	}

	/**
	 * p1 is proportion
	 * @param n
	 * @param p1
	 * @return
	 */
	public static double getValue(int n, double p1) {
		if (0 <= p1 && p1 <= 1) {
			int i = (int) (p1 * (DataPrecision.PROB_DIV - 1));
			return data1[n][i];
		} else {
			throw new IllegalArgumentException("p1=" + p1);
		}
	}

	/**
	 * A dummy method, just to force initialization of this class
	 */
	public static void initialize() {
	}
};
