package ilrp.protocol.packet;

import ilrp.protocol.common.Request;

import java.util.Date;

/**
 * I missed a red packet
 * @author aleck
 *
 */
public class MissPacketRequest extends Request {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6630420743776199887L;

	public final Date time;			// client¶ËµÄÊ±¼ä

	public MissPacketRequest(Date time) {
		super();
		this.time = time;
	}

	@Override
	public String toString() {
		return "MissPacketRequest [time=" + time + "]";
	}

}
