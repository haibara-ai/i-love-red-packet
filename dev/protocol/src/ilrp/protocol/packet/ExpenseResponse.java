package ilrp.protocol.packet;

import ilrp.protocol.common.Response;

public class ExpenseResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6912578647063046200L;
	
	public static final byte STATUS_OK = 0;
	public static final byte ERROR		= 1;
	
	public byte status;

	public ExpenseResponse(byte status) {
		super();
		this.status = status;
	}


	@Override
	public String toString() {
		return "ExpenseResponse [status=" + status + "]";
	}

}
