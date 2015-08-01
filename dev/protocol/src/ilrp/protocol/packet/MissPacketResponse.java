package ilrp.protocol.packet;

import ilrp.protocol.common.Response;

public class MissPacketResponse extends Response {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4759706361301394418L;
	
	public static final byte STATUS_OK 	= 0;
	public static final byte ERROR		= 1;
	
	public byte status;

	public MissPacketResponse(byte status) {
		super();
		this.status = status;
	}

	@Override
	public String toString() {
		return "MissPacketResponse [status=" + status + "]";
	}

}
