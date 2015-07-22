package ilrp.strategy.util;
import java.util.Random;

/**
 * Generate red packet
 * @author aleck
 *
 */
public class RedPacketGenerator {
	private static Random rand = new Random();
	
	public static void setSeed(long seed) {
		rand.setSeed(seed);
	}
	
	public static double[] generate(double amount, int n) {
		double sum = 0;
		double[] x = new double[n];
		for (int i = 0; i < n; i++) {
			x[i] = rand.nextDouble();
			sum += x[i];
		}
		for (int i = 0; i < n; i++) {
			x[i] = x[i] / sum * amount;
		}
		return x;
	}
}
