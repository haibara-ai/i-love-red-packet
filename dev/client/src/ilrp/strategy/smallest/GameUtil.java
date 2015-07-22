package ilrp.strategy.smallest;

import ilrp.strategy.smallest.data.SmallestProbility;
import ilrp.strategy.smallest.data.SmallestRank;

import java.util.Arrays;

public class GameUtil {
	/**
	 * Red Packet to String
	 * @param x
	 * @return
	 */
	public static String redPacketToString(double[] x) {
		StringBuilder sb = new StringBuilder();
		sb.append("RedPacket [");
		for (int i = 0; i < x.length; i++) {
			if (i != 0)
				sb.append(", ");
			sb.append(String.format("%6.2f\t", x[i]));
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * assume x1 + x2 + x3 + x4 = total
	 * let S = min(x1, x2) < min(x3, x4)
	 * compute: (x1+x2-total)*P(S) + (x1+x2)*P(not S)
	 * @param x1
	 * @param x2
	 * @return
	 */
	public static double expectation(double x1, double x2, double total) {
		int i = (int) (x1 / total * SmallestProbility.M);
		int j = (int) (x2 / total * SmallestProbility.M);
		double p = SmallestProbility.DATA[i][j];
		return (x1 + x2 - total) * p + (x1 + x2) * (1 - p);
	}
	

	/**
	 * compute rank of the current solution
	 * i.e. return = 0.1 => it's among the best 10%.
	 * @param x1
	 * @param x2
	 * @param total
	 * @return
	 */
	public static double rank(double x1, double x2, double total) {
		double exp = expectation(x1 / total, x2 / total, 1);
		int index = Arrays.binarySearch(SmallestRank.SAMPLES, exp);
		int position = (index >= 0) ? index : -(index + 1);	// [0, lenfth]
		return 1.0 - 1.0 * position / SmallestRank.SAMPLES.length;
	}
}
