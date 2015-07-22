package ilrp.protocol.common;



public interface Callback {
	public static final byte SUCCESS 	= 0;	// 得到 response
	public static final byte TIMEOUT 	= 1;	// 超时，未得到 response
	public static final byte ERROR		= 2;	// 由于其他错误，未得到 response
	
	/**
	 * When there is no response
	 * @param reqest
	 * @param response
	 */
	public void invoke(byte status, Request req, Response res);
}
