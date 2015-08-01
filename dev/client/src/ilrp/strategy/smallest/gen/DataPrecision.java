package ilrp.strategy.smallest.gen;

/**
 * Used both for generation and decision making
 * They should be consistent, otherwise, the data file may not be found and an exception is expected.
 * @author aleck
 *
 */
public class DataPrecision {
	// 计算概率表时，每个点平均重复采样多少次？
	// recommended = 10000000
	// 10000	-> 3min
	// 100000 	-> 30min
	// 1000000	-> 5hours
	public static final long	PROB_AVERAGE_DUPLICATION = 10000;
	// 对红包金额的分析精度，100表示精确到总金额的1/100
	// recommended = 400
	public static final int 	PROB_DIV 			= 400;
	// 计算Rank时，总结果的样本数量
	// recommended = 1000
	public static final int 	RANK_SAMPLES 		= 1000;
	// 计算Rank时，总采样率数量
	// recommended = 100000000
	public static final int 	RANK_RAW_SAMPLES 	= 100000000;
}
