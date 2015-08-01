package ilrp.strategy.smallest.gen;

/**
 * Used both for generation and decision making
 * They should be consistent, otherwise, the data file may not be found and an exception is expected.
 * @author aleck
 *
 */
public class DataPrecision {
	// ������ʱ�ʱ��ÿ����ƽ���ظ��������ٴΣ�
	// recommended = 10000000
	// 10000	-> 3min
	// 100000 	-> 30min
	// 1000000	-> 5hours
	public static final long	PROB_AVERAGE_DUPLICATION = 10000;
	// �Ժ�����ķ������ȣ�100��ʾ��ȷ���ܽ���1/100
	// recommended = 400
	public static final int 	PROB_DIV 			= 400;
	// ����Rankʱ���ܽ������������
	// recommended = 1000
	public static final int 	RANK_SAMPLES 		= 1000;
	// ����Rankʱ���ܲ���������
	// recommended = 100000000
	public static final int 	RANK_RAW_SAMPLES 	= 100000000;
}
