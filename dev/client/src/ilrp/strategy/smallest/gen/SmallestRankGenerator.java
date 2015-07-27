package ilrp.strategy.smallest.gen;
import ilrp.strategy.smallest.GameUtil;
import ilrp.strategy.util.RedPacketGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;


public class SmallestRankGenerator extends AbstractDataGenerator{
	/**
	 * 前提，4人分红包
	 * 返回一个表格 p[i]
	 * 表示当第1个元素占i（百分比），后面存在一个更小元素的概率。
	 * 
	 * @param tax 是指群主抽水
	 *  
	 * 继续发红包时产生的利益损失，假定为 penalty
	 * 通常来说，amount + penalty < 0，因为群主需要抽水
	 * 
	 * tax = (1 - total / penalty) * 100%，即红包中百分之多少被拿走了？
	 * 
	 * 在近似情况下选取最接近的方案
	 * 
	 * @return
	 */
	public static double[] buildTable1(int n, int tax, double amount) {
		int RAW_SAMPLES = DataPrecision.RANK_RAW_SAMPLES;
		int SAMPLES = DataPrecision.RANK_SAMPLES;
		
		double[] raw = new double[RAW_SAMPLES];
		for (int i = 0; i < RAW_SAMPLES; i++) {
			double[] x = RedPacketGenerator.generate(amount, n);
			raw[i] = GameUtil.expectation(tax, n, x[0] / amount);
		}
		// sort
		Arrays.sort(raw);
		// resample:
		// e[0] = raw[0];
		// e[last] = raw[last'];
		// => e[i] = i * (raw.length - 1) / (e.length - 1);
		double[] e = new double[SAMPLES];
		for (int i = 0; i < SAMPLES; i++) {
			int idx = (int) (1.0 * i * (RAW_SAMPLES - 1) / (SAMPLES - 1));
			if (idx >= RAW_SAMPLES) {
				System.out.println("warning: out of bound when resampling.");
				idx = RAW_SAMPLES - 1;
			}
			e[i] = raw[idx];
		}
		return e;
	}
	
	/**
	 * 前提，4人分红包
	 * 返回一个表格 p[i,j]
	 * 表示当第1个元素占i（百分比），第2个元素占j（百分比）时，后面存在一个更小元素的概率。
	 * 
	 * @param tax 是指群主抽水
	 *  
	 * 继续发红包时产生的利益损失，假定为 penalty
	 * 通常来说，amount + penalty < 0，因为群主需要抽水
	 * 
	 * tax = (1 - total / penalty) * 100%，即红包中百分之多少被拿走了？
	 * 
	 * 在近似情况下选取最接近的方案
	 * 
	 * @return
	 */
	public static double[] buildTable2(int n, int tax, double amount) {
		int RAW_SAMPLES = DataPrecision.RANK_RAW_SAMPLES;
		int SAMPLES = DataPrecision.RANK_SAMPLES;
		
		double[] raw = new double[RAW_SAMPLES];
		for (int i = 0; i < RAW_SAMPLES; i++) {
			double[] x = RedPacketGenerator.generate(amount, n);
			raw[i] = GameUtil.expectation(tax, n, x[0] / amount, x[1] / amount);
		}
		// sort
		Arrays.sort(raw);
		// resample:
		// e[0] = raw[0];
		// e[last] = raw[last'];
		// => e[i] = i * (raw.length - 1) / (e.length - 1);
		double[] e = new double[SAMPLES];
		for (int i = 0; i < SAMPLES; i++) {
			int idx = (int) (1.0 * i * (RAW_SAMPLES - 1) / (SAMPLES - 1));
			if (idx >= RAW_SAMPLES) {
				System.out.println("warning: out of bound when resampling.");
				idx = RAW_SAMPLES - 1;
			}
			e[i] = raw[idx];
		}
		return e;
	}
	
	public static String getRankFilename(int n, int m, int tax, int samples, int raw) {
		return getDataPathPrefix() + "/rank/" + n + "-" + m + "-" + tax + "/" + samples + "-" + raw + ".txt";
	}
	
	public static void generate() throws IOException {
		for (int n = GameUtil.MIN_PLAYERS; n <= GameUtil.MAX_PLAYERS; n++) {
			for (int tax : GameUtil.TAXES) {
				for (int m : GameUtil.ACTED_PLAYERS) {
					System.out.println("[SmallestRank] generating: " + m + " out of " + n + " players acted, tax = " + tax + "%.");
					
					long start = System.currentTimeMillis();
					File file = new File(getRankFilename(n, m, tax, DataPrecision.RANK_SAMPLES, DataPrecision.RANK_RAW_SAMPLES));
					ensureFile(file);

					String s = null;
					if (m == 1) {
						s = arrayToColumn(buildTable1(n, tax, 1));
						writeToFile(file, s);
					} else if (m == 2) {
						s = arrayToColumn(buildTable2(n, tax, 1));
						writeToFile(file, s);
					} else {
						System.out.println("[SmallestRank] Error: unhandled acted player number: " + m);
					}
					
					
					System.out.println("[SmallestRank] time = " + (System.currentTimeMillis() - start) + "ms.");
				}
			}
		}
	}
}
