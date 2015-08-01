package ilrp.strategy.smallest.gen;
import ilrp.strategy.smallest.GameUtil;
import ilrp.strategy.util.ArrayUtil;
import ilrp.strategy.util.RedPacketGenerator;

import java.io.File;
import java.io.IOException;


public class SmallestProbabilityGenerator extends AbstractDataGenerator{
	/**
	 * ǰ�ᣬ4�˷ֺ��
	 * ����һ����� p[i]
	 * ��ʾ����1��Ԫ��ռi���ٷֱȣ����������һ����СԪ�صĸ��ʡ�
	 * @return
	 */
	private static double[] buildTable1(int n) {
		int M = DataPrecision.PROB_DIV;
		long iteration = 1L * M * DataPrecision.PROB_AVERAGE_DUPLICATION;
		long[] a;		// all cases
		long[] p;		// positive cases
		a = new long[M];
		p = new long[M];
		for (long k = 0; k < iteration; k++) {
			double[] x = RedPacketGenerator.generate(1, n);
			int i = (int) (x[0] * M);
			a[i] ++;
			p[i] += (x[0] <= ArrayUtil.min(x, 1, n)) ? 1 : 0;
		}
		double[] t = new double[M];
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < M; j++) {
				if (a[i] == 0) {
					// NOTE: this may be an error
					t[i] = 0;
				} else {
					t[i] = 1.0 * p[i] / a[i];
				}
			}
		}
		regulate(t);
		return t;
	}
	
	/**
	 * ǰ�ᣬ4�˷ֺ��
	 * ����һ����� p[i,j]
	 * ��ʾ����1��Ԫ��ռi���ٷֱȣ�����2��Ԫ��ռj���ٷֱȣ�ʱ���������һ����СԪ�صĸ��ʡ�
	 * @return
	 */
	private static double[][] buildTable2(int n) {
		int M = DataPrecision.PROB_DIV;
		long iteration = 1L * M * M * DataPrecision.PROB_AVERAGE_DUPLICATION;
		long[][] a;		// all cases
		long[][] p;		// positive cases
		a = new long[M][M];
		p = new long[M][M];
		for (long k = 0; k < iteration; k++) {
			double[] x = RedPacketGenerator.generate(1, n);
			int i = (int) (x[0] * M);
			int j = (int) (x[1] * M);
			a[i][j] ++;
			p[i][j] += (Math.min(x[0], x[1]) <= ArrayUtil.min(x, 2, n)) ? 1 : 0;
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
	
	public static String getProbFilename(int n, int m, int div, long dup) {
		return getDataPathPrefix() + "/table/" + n + "-" + m + "/" + div + "-" + dup + ".txt";
	}
	
	public static void generate() throws IOException {
		for (int n = GameUtil.MIN_PLAYERS; n <= GameUtil.MAX_PLAYERS; n++) {
			for (int m : GameUtil.ACTED_PLAYERS) {
				System.out.println("[SmallestProbability] generating: " + m + " out of " + n + " players acted.");
				
				File file = new File(getProbFilename(n, m, DataPrecision.PROB_DIV, DataPrecision.PROB_AVERAGE_DUPLICATION));
				ensureFile(file);
				long start = System.currentTimeMillis();
				String s = null;
				if (m == 1) {
					s = arrayToColumn(buildTable1(n));
					writeToFile(file, s);
				} else if (m == 2) {
					s = matrixToText(buildTable2(n));
					writeToFile(file, s);
				} else {
					System.out.println("[SmallestProbability] Error: unhandled acted player number: " + m);
				}
				
				System.out.println("[SmallestProbability] time=" + (System.currentTimeMillis() - start) + "ms.");
			}
		}
	}

	/**
	 * It holds that:
	 * 
	 * t[i1] >= t[i2] for i1 >= i2
	 * 
	 * The result is obtained from Monte-Carlo, which may violates this rule.
	 * We adjust the result.
	 * 
	 * @param t
	 * @return
	 */
	private static void regulate(double[] t) {
		int modified = 0;
		int M = t.length;
		int i;
		// first column
		for (i = 1; i + 1 < M; i++) {
			boolean violation = !(t[i - 1] >= t[i] && t[i] >= t[i + 1]);
			if (violation) {
				// assume local linear
				double tp = (t[i - 1] + t[i + 1]) / 2;
				System.out.print(String.format("adjust the %d-th element from %1.4f to %1.4f", i, t[i], tp));
				System.out.println(String.format("  col: %1.4f -> %1.4f -> %1.4f;", 
						t[i - 1], t[i], t[i + 1]
						));
				t[i] = tp;
				modified ++;
			}
		}
		System.out.println(String.format("Regulation: %d out of %d cells are modofied.", modified, M));
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
