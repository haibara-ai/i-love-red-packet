package ilrp.db;

import java.util.Date;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element
public class User {
	@Attribute
	public String name;		// 名称
	@Attribute
	public String code;		// 代码
	@Attribute
	public boolean enable;	// 是否生效
	@Attribute
	public Date expiration;	// 过期时间

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
