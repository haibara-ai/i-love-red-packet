package ilrp.protocol.packet;

import ilrp.protocol.common.Request;

import java.util.Date;

public class AuthRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6724370255776473366L;
	
	public final String code;		// 机器码
	public final Date time;			// client端的时间
	
	public AuthRequest(String code, Date time) {
		super();
		this.code = code;
		this.time = time;
	}

	@Override
	public String toString() {
		return "AuthRequest [code=" + code + ", time=" + time + "]";
	}
	
}
