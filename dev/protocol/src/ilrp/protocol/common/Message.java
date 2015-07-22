package ilrp.protocol.common;

import java.io.Serializable;

/**
 * Base class
 * @author Min
 *
 */
public abstract class Message implements Serializable {
	private static final long serialVersionUID = 5919088460780080987L;

	public static final int PROTOCOL_VERSION = 1;

	public final int version = PROTOCOL_VERSION;
}
