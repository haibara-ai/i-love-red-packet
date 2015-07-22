package ilrp.protocol.packet;

import ilrp.protocol.common.Response;

public class AuthResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7768929597193863659L;
	
	public static final byte LICENSE_OK			= 0;
	public static final byte LICENSE_NOT_EXISTS = 1;
	public static final byte LICENSE_EXPIRED	= 2;
	public static final byte LICENSE_DISABLED	= 3;
	public static final byte DUP_AUTH 			= 4;
	public static final byte SERVER_ERROR 		= 5;
	
	public final byte status;

	public AuthResponse(byte status) {
		super();
		this.status = status;
	}

	@Override
	public String toString() {
		return "AuthResponse [status=" + status + "]";
	}
	
}
