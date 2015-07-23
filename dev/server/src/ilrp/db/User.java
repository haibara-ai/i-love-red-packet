package ilrp.db;

import java.util.Date;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element
public class User {
	@Attribute
	public String name;		// ����
	@Attribute
	public String code;		// ����
	@Attribute
	public boolean enable;	// �Ƿ���Ч
	@Attribute
	public Date expiration;	// ����ʱ��

	// dummy constructor for Simple
	protected User() {}
	
	public User(String name, String code, boolean enable, Date expiration) {
		super();
		this.name = name;
		this.code = code;
		this.enable = enable;
		this.expiration = expiration;
	}
	
}
