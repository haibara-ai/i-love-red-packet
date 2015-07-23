package ilrp.strategy.smallest.gen;
import ilrp.strategy.smallest.GameParam;
import ilrp.strategy.smallest.GameUtil;
import ilrp.strategy.util.RedPacketGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;


public class SmallestRankGenerator {
	private static final int RAW_SAMPLES 	= 100000000;	// 100000000
	private static final int SAMPLES 		= 1000;			// 1000
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
		double[] raw = new double[RAW_SAMPLES];
		for (int i = 0; i < RAW_SAMPLES; i++) {
			double[] x = RedPacketGenerator.generate(total, n);
			raw[i] = GameUtil.expectation(x[1] / total, x[2] / total, 1);
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
		StringBuilder sb = new StringBuilder();
		sb.append("package ilrp.strategy.smallest.data;\n");
		sb.append("\n");
		// assume x1+x2+x3+x4=1
		// let event S = min(x1,x2) < min(x3,x4)
		// P(S) is obtained from SmallestProbility
		// let E = (x1+x2-1)*P(S) + (x1+x2)*P(not S)
		// this is a collection of evenly sample E
		sb.append("// *** Automatic generated file, do not edit ***\n");
		sb.append("// assume x1+x2+x3+x4=1\n");
		sb.append("// let event S = min(x1,x2) < min(x3,x4)\n");
		sb.append("// P(S) is obtained from SmallestProbility\n");
		sb.append("// let E = (x1+x2-1)*P(S) + (x1+x2)*P(not S)\n");
		sb.append("// this is a collection of evenly sample E\n");
		
		sb.append("public class SmallestRank {\n");
		sb.append("\tpublic static final double[] SAMPLES = new double[] {");
		for (int i = 0; i < SAMPLES; i++) {
			sb.append(String.format("%1.4f, ", e[i]));
		}
		sb.append("};\n");
		
		sb.append("};\n");
		return sb.toString();
	}
	
	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();
		File file = new File("./data/rank.txt");
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		if (!file.exists())
			file.createNewFile();
		String s = buildTable();
		FileWriter writer = new FileWriter(file);
		writer.write(s);
		writer.close();
		System.out.println("time = " + (System.currentTimeMillis() - start) + "ms.");
	}
}
