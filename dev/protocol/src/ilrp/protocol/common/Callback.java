package ilrp.protocol.common;



public interface Callback {
	public static final byte SUCCESS 	= 0;	// �õ� response
	public static final byte TIMEOUT 	= 1;	// ��ʱ��δ�õ� response
	public static final byte ERROR		= 2;	// ������������δ�õ� response
	
	/**
	 * When there is no response
	 * @param reqest
	 * @param response
	 */
	public void invoke(byte status, Request req, Response res);
}
