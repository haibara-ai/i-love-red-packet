package ilrp.strategy.smallest.data;

import ilrp.strategy.smallest.GameUtil;
import ilrp.strategy.smallest.gen.DataPrecision;
import ilrp.strategy.smallest.gen.SmallestProbabilityGenerator;

import java.io.IOException;

public class SmallestProbility {
	private static final double[][][] data;
	
	static {
		System.out.print("Loading probability matrix... ");
		data = new double[GameUtil.MAX_PLAYERS + 1][][];
		for (int n = GameUtil.MIN_PLAYERS; n <= GameUtil.MAX_PLAYERS; n++) {
			String filename = SmallestProbabilityGenerator.getProbFilename(
					n, 
					DataPrecision.PROB_DIV, 
					DataPrecision.PROB_AVERAGE_DUPLICATION
					);
			try (DataLoader loader = new DataLoader(filename)) {
				data[n] = loader.readTable();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("error loading data file: " + filename);
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
			return data[n][i][j];
		} else {
			throw new IllegalArgumentException("p1=" + p1 + ", p2=" + p2);
		}
	}

	/**
	 * A dummy method, just to force initialization of this class
	 */
	public static void initialize() {
	}
};
