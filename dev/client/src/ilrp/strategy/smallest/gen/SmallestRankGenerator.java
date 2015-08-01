package ilrp.strategy.smallest.gen;
import ilrp.strategy.smallest.GameUtil;
import ilrp.strategy.util.RedPacketGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;


public class SmallestRankGenerator extends AbstractDataGenerator{
	/**
	 * ǰ�ᣬ4�˷ֺ��
	 * ����һ����� p[i,j]
	 * ��ʾ����1��Ԫ��ռi���ٷֱȣ�����2��Ԫ��ռj���ٷֱȣ�ʱ���������һ����СԪ�صĸ��ʡ�
	 * 
	 * @param tax ��ָȺ����ˮ
	 *  
	 * ���������ʱ������������ʧ���ٶ�Ϊ penalty
	 * ͨ����˵��amount + penalty < 0����ΪȺ����Ҫ��ˮ
	 * 
	 * tax = (1 - total / penalty) * 100%��������аٷ�֮���ٱ������ˣ�
	 * 
	 * �ڽ��������ѡȡ��ӽ��ķ���
	 * 
	 * @return
	 */
	public static double[] buildTable(int n, int tax, double amount) {
		int RAW_SAMPLES = DataPrecision.RANK_RAW_SAMPLES;
		int SAMPLES = DataPrecision.RANK_SAMPLES;
		
		double[] raw = new double[RAW_SAMPLES];
		for (int i = 0; i < RAW_SAMPLES; i++) {
			double[] x = RedPacketGenerator.generate(amount, n);
			raw[i] = GameUtil.expectation(n, tax, x[1] / amount, x[2] / amount);
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
		return e;
	}
	
	public static String getRankFilename(int n, int tax, int samples, int raw) {
		return getDataPathPrefix() + "/rank-" + n + "-" + tax + "-" + samples + "x" + raw + ".txt";
	}
	
	public static void generate() throws IOException {
		for (int n = GameUtil.MIN_PLAYERS; n <= GameUtil.MAX_PLAYERS; n++) {
			for (int tax : GameUtil.TAXES) {
				System.out.println("n=" + n + ", tax=" + tax + "%");
				
				long start = System.currentTimeMillis();
				File file = new File(getRankFilename(n, tax, DataPrecision.RANK_SAMPLES, DataPrecision.RANK_RAW_SAMPLES));
				ensureFile(file);
				String s = arrayToColumn(buildTable(n, tax, 1));
				writeToFile(file, s);
				
				System.out.println("time = " + (System.currentTimeMillis() - start) + "ms.");
			}
		}
	}
}
