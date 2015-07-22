package ilrp.protocol.packet;

import ilrp.protocol.common.Request;

import java.util.Date;

public class IncomeRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8642618043200868769L;

	public static final byte RECEIVE_PUBLIC_RED_PACKET = 1;
	
	public final byte type;			// 类型
	public final int amount;		// 金额
	public final String sender;		// 发红包的人
	public final String group;		// 所在群主
	public final Date time;			// client端的时间

	public IncomeRequest(byte type, int amount, String sender, String group, Date time) {
		super();
		this.type = type;
		this.amount = amount;
		this.sender = sender;
		this.group = group;
		this.time = time;
	}

	@Override
	public String toString() {
		return "IncomeRequest [type=" + type + ", amount=" + amount
				+ ", sender=" + sender + ", group=" + group + ", time=" + time
				+ "]";
	}
	
}
