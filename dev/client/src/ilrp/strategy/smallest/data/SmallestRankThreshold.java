package ilrp.strategy.smallest.data;

public class SmallestRankThreshold {
	/**
	 * Strategy: we can modify this table
	 * 
	 * 0 <= x[i] <= 1
	 * x[i] < x[i + 1]
	 * last(x) = 1
	 * 
	 * Assume the array is given as:
	 * [0.33, 0.50, 0.66, 1.00]
	 * It means we require that
	 * 33% cases are complete in 1 round
	 * 50% cases are complete in 2 round
	 * 66% cases are complete in 3 round
	 * 100% cases are complete in 4 round
	 * i.e., all red packets are sent in at most 4 rounds
	 */
	private static final double[] COMPLETION_RATIO = new double[] {
		// strategy 1
		0.20,
		0.50,
		1.00,
		// strategy 2
//		0.33,
//		0.50,
//		0.66,
//		1.00,
	};
	
	private static final double[] DEFAULT_THRESHOLDS = new double[] {
		0.20,		// round 1
		0.30,		// round 2
		0.40,		// round 3
		1.00,		// round 4
	};

	public static final double[] THRESHOLDS;
	
	// build threshold
	static {
		double[] result;
		try {
			result = new double[COMPLETION_RATIO.length];
			result[0] = COMPLETION_RATIO[0];
			// CR[i - 1] + (1 - CR[i - 1]) * T[i] = CR[i]
			for (int i = 1; i < result.length; i++) {
				result[i] = (COMPLETION_RATIO[i] - COMPLETION_RATIO[i - 1]) / (1 - COMPLETION_RATIO[i - 1]);
			}
		} catch (Exception e) {
			System.err.println("Warning: using default threshold for decision making, due to the exception: " + e.getMessage());
			result = DEFAULT_THRESHOLDS;
		}
		THRESHOLDS = result;
		printThreshold();
	}

	public static void printThreshold() {
		System.out.println("---- thresholds ----");
		for (int i = 0; i < THRESHOLDS.length; i++) {
			System.out.println(String.format("%2d round, choosse best %3.2f%%, completes %3.2f%% cases.", i, THRESHOLDS[i] * 100, COMPLETION_RATIO[i] * 100));
		}
		System.out.println("---- the end ----");
	}
}
