package ilrp.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * All time should be in CST
 * @author aleck
 *
 */
public class DateUtil {
	private static final DateFormat DATE_FORMAT	= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final TimeZone ZONE 			= TimeZone.getTimeZone("CST");
	
	public static Calendar getCSTCalendar() {
		return Calendar.getInstance(ZONE);
	}
	
	/**
	 * In CST
	 * @param time
	 * @return
	 */
	public static Date calcEndOfDay(Date time) {
		Calendar cal = Calendar.getInstance(ZONE);
		cal.setTime(time);
		cal.setTimeZone(ZONE);
		cal.set(Calendar.HOUR_OF_DAY, 	0);
		cal.set(Calendar.MINUTE, 		0);
		cal.set(Calendar.SECOND, 		0);
		return cal.getTime();
	}
	
	/**
	 * In CST
	 * @param time
	 * @return
	 */
	public static Date calcStartOfDay(Date time) {
		Calendar cal = Calendar.getInstance(ZONE);
		cal.setTime(time);
		cal.setTimeZone(ZONE);
		cal.set(Calendar.HOUR_OF_DAY, 	23);
		cal.set(Calendar.MINUTE, 		59);
		cal.set(Calendar.SECOND, 		59);
		return cal.getTime();
	}

	public static String getCSTTimeStr(Calendar cal) {
		return DATE_FORMAT.format(cal.getTime());
	}
}
