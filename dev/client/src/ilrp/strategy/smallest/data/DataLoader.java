package ilrp.strategy.smallest.data;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * ∂¡»Î±Ì∏Ò
 * @author Min
 *
 */
public class DataLoader implements Closeable {
	private BufferedReader br;
	
	public DataLoader(String filename) throws FileNotFoundException {
		br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
	}
	
	@Override
	public void close() throws IOException {
		if (br != null) {
			br.close();
		}
	}
	
	public double[] readUntilLineBreak() throws IOException {
		String line = br.readLine();
		if (line == null) {
			return null;
		} else {
			List<Double> list = new ArrayList<Double>();
			try (Scanner scanner = new Scanner(new ByteArrayInputStream(line.getBytes()))) {
				try {
					while (true)
						list.add(scanner.nextDouble());
				} catch (NoSuchElementException e) {
					// break on exhaust
				}
			}
			double[] ret = new double[list.size()];
			for (int i = 0; i < list.size(); i++)
				ret[i] = list.get(i);
			return ret;
		}
	}
	
	public double[][] readTable() throws IOException {
		List<double[]> rows = new ArrayList<double[]>();
		double[] r;
		while ((r = readUntilLineBreak()) != null) {
			rows.add(r);
		}
		double[][] ret = new double[rows.size()][];
		for (int i = 0; i < rows.size(); i++) {
			ret[i] = rows.get(i);
		}
		return ret;
	}

	/**
	 * Only cares the first column
	 * @return
	 * @throws IOException 
	 */
	public double[] readColumn() throws IOException {
		double[][] t = readTable();
		double[] ret = new double[t.length];
		for (int i = 0; i < t.length; i++) {
			ret[i] = t[i][0];
		}
		return ret;
	}
}
