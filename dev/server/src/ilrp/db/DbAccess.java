package ilrp.db;

import ilrp.util.DateUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;


public class DbAccess {
	private static final String LOG_FILE_DIR	= "data/log";
	private static final String XML_FILE_PATH 	= "data/db/users.xml";
	private static final Object XML_LOCK = new Object();
	
	private static final DateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	public static UserManage loadUsers() {
		synchronized (XML_LOCK) {
			File file = new File(XML_FILE_PATH);
			Serializer serializer = new Persister();
			UserManage um;
			try {
				um = serializer.read(UserManage.class, file);
				String msg = um.organize();
				if (msg == null) {
					return um;
				} else {
					System.err.println("[ACCOUNT] Error loading accounts: " + msg);
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	/**
	 * This is for debugging only.
	 * Modification to UserManage should not be persisted.
	 * 
	 * @param users
	 * @return
	 */
	public static boolean saveUsers(UserManage users) {
		synchronized (XML_LOCK) {
			File file = new File(XML_FILE_PATH);
			try {
				ensureFile(file);
				Serializer serializer = new Persister();
				serializer.write(users, file);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	
	private static void ensureFile(File file) throws IOException {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			file.createNewFile();
		}
	}

	/**
	 * Append logs to user
	 * @param code
	 * @param line
	 */
	public static void appendLog(String code, String msg) {
		// no lock is needed, since we are writting separate files
		Calendar cal = DateUtil.getCSTCalendar();
		String filename = code + "/" + LOG_DATE_FORMAT.format(cal.getTime()) + ".txt";
		File file = new File(LOG_FILE_DIR + "/" + filename);
		try {
			ensureFile(file);
			FileWriter writer = new FileWriter(file, true);
			String line = DateUtil.getCSTTimeStr(cal) + " | " + msg + "\n";
			writer.write(line);
			writer.close();
			System.out.println("[LOG] " + line);
		} catch (IOException e) {
			System.err.println("[SERVER] Error creating file: " + file.getAbsolutePath());
			e.printStackTrace();
		}
	}
}
