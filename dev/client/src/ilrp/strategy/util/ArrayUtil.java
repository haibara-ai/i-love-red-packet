package ilrp.strategy.util;

public class ArrayUtil {
	// minimal value in [l, r)
	public static double min(double[] a, int l, int r) {
		if (l >= r) {
			throw new IllegalArgumentException("no element in the range [" + l + "," + r + ").");
		}
		double m = a[l];
		for (int i = l + 1; i < r && i < a.length; i++) {
			if (a[i] < m) {
				m = a[i];
			}
		}
		return m;
	}
}
