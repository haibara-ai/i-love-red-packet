package ilrp.protocol.packet;

import ilrp.protocol.common.Request;

import java.util.Date;

public class ExpenseRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1271737631215380287L;

	public static final byte SEND_RED_PACKET_TO_PUBLIC = 1;
	
	public final byte type;			// 类型
	public final int totalAmount;	// 总的金额
	public final int savedAmount;	// 被合作账号抢到的金额
	public final String intGroup;	// 内部群名称
	public final String extGroup;	// 外部群名称
	public final Date time;			// client端时间
	
	public ExpenseRequest(byte type, int totalAmount, int savedAmount,
			String intGroup, String extGroup, Date time) {
		super();
		this.type = type;
		this.totalAmount = totalAmount;
		this.savedAmount = savedAmount;
		this.intGroup = intGroup;
		this.extGroup = extGroup;
		this.time = time;
	}

	@Override
	public String toString() {
		return "ExpenseRequest [type=" + type + ", totalAmount="
				+ totalAmount + ", savedAmount=" + savedAmount
				+ ", intGroup=" + intGroup + ", extGroup=" + extGroup
				+ ", time=" + time + "]";
	}
	
}
