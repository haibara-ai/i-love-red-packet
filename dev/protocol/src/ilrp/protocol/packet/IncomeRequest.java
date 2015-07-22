package ilrp.protocol.packet;

import ilrp.protocol.common.Request;

import java.util.Date;

public class IncomeRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8642618043200868769L;

	public static final byte RECEIVE_PUBLIC_RED_PACKET = 1;
	
	public final byte type;			// ����
	public final int amount;		// ���
	public final String sender;		// ���������
	public final String group;		// ����Ⱥ��
	public final Date time;			// client�˵�ʱ��

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
