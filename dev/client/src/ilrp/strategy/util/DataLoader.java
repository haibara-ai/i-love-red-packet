package ilrp.strategy.util;

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
	
}
