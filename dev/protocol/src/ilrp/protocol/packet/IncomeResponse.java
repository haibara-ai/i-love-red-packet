package ilrp.protocol.packet;

import ilrp.protocol.common.Response;

public class IncomeResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = 43062892789034928L;
	
	public static final byte STATUS_OK 	= 0;
	public static final byte ERROR		= 1;
	
	public byte status;

	public IncomeResponse(byte status) {
		super();
		this.status = status;
	}

	@Override
	public String toString() {
		return "IncomeResponse [status=" + status + "]";
	}

}
