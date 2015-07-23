package ilrp.strategy.smallest.gen;
import ilrp.strategy.smallest.GameParam;
import ilrp.strategy.util.RedPacketGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class SmallestProbabilityGenerator {
	// each block has xxx duplication samples in average
	// use 10000000 for accuracy
	private static final long AVERAGE_DUPLICATION = 1000000;
	// resolution
	private static final int M = 400;
	// total amount
	private static final double total = GameParam.TOTAL;
	// total partitions
	private static final int n = GameParam.N;

	/**
	 * 前提，4人分红包
	 * 返回一个表格 p[i,j]
	 * 表示当第1个元素占i（百分比），第2个元素占j（百分比）时，后面存在一个更小元素的概率。
	 * @return
	 */
	public static String buildTable() {
		long iteration = 1L * M * M * AVERAGE_DUPLICATION;
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
				t[i][j] = 1.0 * p[i][j] / a[i][j];
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("package ilrp.strategy.smallest.data;\n");
		sb.append("\n");
		sb.append("// *** Automatic generated file, do not edit ***\n");
		sb.append("public class SmallestProbility {\n");
		sb.append("\t// resolution is: resolution = amount / M\n");
		sb.append("\tpublic static final int M = " + M + ";\n");
		sb.append("\t// assume x1+x2+x3+x4=n, DATA[x1/n*m][x2/n*m] is the probability that min(x1,x2) > min(x3,x4)\n");
		sb.append("\tpublic static final double[][] DATA = new double[][] {\n");
		for (int i = 0; i < M; i++) {
			sb.append("\t\t{");
			for (int j = 0; j < M; j++) {
				if (a[i][j] == 0) {
					sb.append(String.format("%1.4f", 0d));
				} else {
					t[i][j] = 1.0 * p[i][j] / a[i][j];
					sb.append(String.format("%1.4f", t[i][j]));
				}
				sb.append(", ");
			}
			sb.append("}, \n");
		}
		sb.append("\t};\n");
		sb.append("};\n");
		return sb.toString();
	}
	
	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();
		File file = new File("./data/table.txt");
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		if (!file.exists())
			file.createNewFile();
		String s = buildTable();
		FileWriter writer = new FileWriter(file);
		writer.write(s);
		writer.close();
		System.out.println("time=" + (System.currentTimeMillis() - start) + "ms.");
	}
}
