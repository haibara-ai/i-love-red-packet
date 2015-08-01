package ilrp.strategy.smallest.gen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AbstractDataGenerator {
	private static final String DATA_DIR 		= "data/gen";

	public static String getDataPathPrefix() {
		return DATA_DIR;
	}
	
	public static String matrixToText(double[][] t) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < t.length; i++) {
			double[] r = t[i];
			for (int j = 0; j < r.length; j++) {
				sb.append(String.format("%1.4f", t[i][j]));
				sb.append('\t');
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	public static String arrayToColumn(double[] t) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < t.length; i++) {
			sb.append(String.format("%1.4f", t[i]));
			sb.append('\n');
		}
		return sb.toString();
	}

	public static void ensureFile(File file) throws IOException {
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		if (!file.exists())
			file.createNewFile();
	}

	public static void writeToFile(File file, String s) throws IOException {
		FileWriter writer = new FileWriter(file);
		writer.write(s);
		writer.close();
	}

}
