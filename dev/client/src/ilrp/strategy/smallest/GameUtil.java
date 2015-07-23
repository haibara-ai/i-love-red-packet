package ilrp.strategy.smallest;

import ilrp.strategy.smallest.data.SmallestProbility;

public class GameUtil {
	// 最多玩家
	public static final int MIN_PLAYERS = 4;
	// 最少玩家
	public static final int MAX_PLAYERS = 5;
	// 抽水百分数
	public static final int[] TAXES 	= new int[] {
		0, 5, 10, 15
	};
	
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
	 * NOTE: all values are unified with respect to the amount (1.0).
	 * assume sigma_i x_i = 1
	 * if S = min(x1, x2) < min(others) happens, penalty is 'penalty' (usually, amount + penalty < 0)
	 * compute: (x1+x2-penalty)*P(S) + (x1+x2)*P(not S)
	 * @param x1
	 * @param x2
	 * @return
	 */
	public static double expectation(int n, int tax, double p1, double p2) {
		double penalty = 1 / (1 - tax / 100.0);
		double p = SmallestProbility.getValue(n, p1, p2);
		return (p1 + p2 - penalty) * p + (p1 + p2) * (1 - p);
	}
}
