package ilrp.strategy.smallest.analytic;

import ilrp.strategy.smallest.GameUtil;
import ilrp.strategy.smallest.SmallestDecision;
import ilrp.strategy.smallest.data.SmallestRank;
import ilrp.strategy.smallest.gen.AbstractDataGenerator;
import ilrp.strategy.smallest.gen.DataPrecision;

import java.io.File;
import java.io.IOException;

/**
 * Plot expectation distribution graph
 * @author aleck
 *
 */
public class ExpectationPlot {
	private static final int DIV	=	DataPrecision.RANK_SAMPLES / 10;

	/**
	 * Assume tax == 0
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		SmallestDecision.initialize();
		int tax = 0;
		for (int n = GameUtil.MIN_PLAYERS; n <= GameUtil.MAX_PLAYERS; n++) {
			for (int m : GameUtil.ACTED_PLAYERS) {
				System.out.println(String.format("Plotting [n=%d,m=%d,tax=%d%%]", n, m, tax));
				// values are from [-100%, 100%];
				double[] exps = SmallestRank.getData(tax, n, m);
				int[] data = new int[DIV + 1];		// x, y
				for (double e : exps) {
					// divide [-1,1] to DIV parts
					int i = (int) ((e + 1) / 2 * DIV);
					data[i] ++;
				}
				double[] result = new double[DIV + 1];
				for (int i = 0; i < result.length; i++) {
					result[i] = 1.0 * data[i] / data.length;
				}
				String filename = "./data/plot/dist/" + n + "-" + m + "-" + tax + ".txt";
				File file = new File(filename);
				AbstractDataGenerator.ensureFile(file);
				String s = AbstractDataGenerator.arrayToColumn(result);
				AbstractDataGenerator.writeToFile(file, s);
			}
		}
	}

}
