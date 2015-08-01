package ilrp.strategy.smallest.gen;
import ilrp.strategy.smallest.GameUtil;
import ilrp.strategy.util.RedPacketGenerator;

import java.io.File;
import java.io.IOException;


public class SmallestProbabilityGenerator extends AbstractDataGenerator{
	/**
	 * 前提，4人分红包
	 * 返回一个表格 p[i,j]
	 * 表示当第1个元素占i（百分比），第2个元素占j（百分比）时，后面存在一个更小元素的概率。
	 * @return
	 */
	public static double[][] buildTable(double total, int n) {
		int M = DataPrecision.PROB_DIV;
		long iteration = 1L * M * M * DataPrecision.PROB_AVERAGE_DUPLICATION;
		long[][] a;		// all cases
		long[][] p;		// positive cases
		a = new long[M][M];
		p = new long[M][M];
		for (long k = 0; k < iteration; k++) {
			double[] x = RedPacketGenerator.generate(total, n);
			int i = (int) ((x[0] / total) * M);
			int j = (int) ((x[1] / total) * M);
			a[i][j] ++;
			p[i][j] += (Math.min(x[0], x[1]) <= Math.min(x[2], x[3])) ? 1 : 0;
		}
		double[][] t = new double[M][M];
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < M; j++) {
				if (a[i][j] == 0) {
					// NOTE: this may be an error
					t[i][j] = 0;
				} else {
					t[i][j] = 1.0 * p[i][j] / a[i][j];
				}
			}
		}
		regulate(t);
		return t;
	}
	
	public static String getProbFilename(int n, int div, long dup) {
		return getDataPathPrefix() + "/table-" + n + "-" + div + "x" + dup + ".txt";
	}
	
	public static void generate() throws IOException {
		double total = 1;
		for (int n = GameUtil.MIN_PLAYERS; n <= GameUtil.MAX_PLAYERS; n++) {
			System.out.println("n=" + n);
			
			File file = new File(getProbFilename(n, DataPrecision.PROB_DIV, DataPrecision.PROB_AVERAGE_DUPLICATION));
			ensureFile(file);
			long start = System.currentTimeMillis();
			String s = matrixToText(buildTable(total, n));
			writeToFile(file, s);
			
			System.out.println("time=" + (System.currentTimeMillis() - start) + "ms.");
		}
	}
	
	/**
	 * It holds that:
	 * 
	 * t[i1][j1] >= t[i2][j2] for i1 >= i2 && j2 >= j2
	 * 
	 * The result is obtained from Monte-Carlo, which may violates this rule.
	 * We adjust the result.
	 * 
	 * @param t
	 * @return
	 */
	private static void regulate(double[][] t) {
		int modified = 0;
		int M = t.length;
		int i, j;
		// first column
		j = 0;
		for (i = 1; i + 1 < M; i++) {
			boolean violation = !(t[i - 1][j] >= t[i][j] && t[i][j] >= t[i + 1][j]);
			if (violation) {
				// assume local linear
				double tp = (t[i - 1][j] + t[i + 1][j]) / 2;
				System.out.print(String.format("adjust (%d,%d) from %1.4f to %1.4f", i, j, t[i][j], tp));
				System.out.println(String.format("  col: %1.4f -> %1.4f -> %1.4f;", 
						t[i - 1][j], t[i][j], t[i + 1][j]
						));
				t[i][j] = tp;
				modified ++;
			}
		}
		// first row
		i = 0;
		for (j = 1; j + 1 < M; j++) {
			boolean violation = !(t[i][j - 1] >= t[i][j] && t[i][j] >= t[i][j + 1]);
			if (violation) {
				// assume local linear
				double tp = (t[i][j - 1] + t[i][j + 1]) / 2;
				System.out.print(String.format("adjust (%d,%d) from %1.4f to %1.4f", i, j, t[i][j], tp));
				System.out.println(String.format("  row: %1.4f -> %1.4f -> %1.4f;", 
						t[i][j - 1], t[i][j], t[i][j + 1]
						));
				t[i][j] = tp;
				modified ++;
			}
		}
		// M x M matrix
		for (i = 1; i + 1 < M; i++) {
			for (j = 1; j + 1 < M; j++) {
				boolean violation = !(
						t[i - 1][j] >= t[i][j] && t[i][j] >= t[i + 1][j] &&
						t[i][j - 1] >= t[i][j] && t[i][j] >= t[i][j + 1]
						);
				if (violation) {
					// assume local linear
					double tp = (t[i - 1][j] + t[i + 1][j] + t[i][j - 1] + t[i][j + 1]) / 4;
					System.out.print(String.format("adjust (%d,%d) from %1.4f to %1.4f", i, j, t[i][j], tp));
					System.out.println(String.format("  row: %1.4f -> %1.4f -> %1.4f, col: %1.4f -> %1.4f -> %1.4f;", 
							t[i][j - 1], t[i][j], t[i][j + 1],
							t[i - 1][j], t[i][j], t[i + 1][j]
							));
					t[i][j] = tp;
					modified ++;
				}
			}
		}
		System.out.println(String.format("Regulation: %d out of %d cells are modofied.", modified, M * M));
	}
}
