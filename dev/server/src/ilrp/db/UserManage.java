package ilrp.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * We don't modify any user in the server side
 * Instead, we modifiy it directly from file.
 * 
 * Therefore, any modification to the UserManage will not be persisted.
 * 
 * @author aleck
 *
 */
@Root(name="users")
public class UserManage {
	@ElementList(inline=true)
	private List<User> users;
	
	private Map<String, User> lookup = new HashMap<String, User>();
	
	public UserManage() {
		users = new ArrayList<User>();
	}

	/**
	 * validate and build the lookup table
	 * @return the error message
	 */
	public String organize() {
		lookup.clear();
		for (User user : users) {
			if (lookup.containsKey(user.code)) {
				return "duplicated code: " + user.code;
			} else {
				lookup.put(user.code, user);
			}
		}
		return null;
	}
	
	public User find(String code) {
		return lookup.get(code);
	}
	
	public static void main(String[] args) {
		UserManage um = new UserManage();
		um.users.add(new User("alice", "000001", true, Calendar.getInstance().getTime()));
		um.users.add(new User("bob", "000002", true, Calendar.getInstance().getTime()));
		DbAccess.saveUsers(um);
		um = DbAccess.loadUsers();
	}
}
