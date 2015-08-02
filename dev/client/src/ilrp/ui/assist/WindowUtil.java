package ilrp.ui.assist;

import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;

/**
 * 与 Windows 相关的一些应用
 * 
 * @author aleck
 *
 */
public class WindowUtil {
	/**
	 * 查找包含 keyword 的所有 Window 的位置
	 * 需要查找所有窗口时：locateWindow("", false)
	 * @param keyword
	 * @param exactMatch
	 * @return
	 */
	public static List<Rectangle> locateWindow(final String keyword, final boolean exactMatch) {
		final User32 user32 = User32.INSTANCE;
		final List<Rectangle> results = new ArrayList<Rectangle>();
		user32.EnumWindows(new WNDENUMPROC() {
			public boolean callback(HWND hWnd, Pointer arg1) {
				char[] windowText = new char[512];
				user32.GetWindowText(hWnd, windowText, 512);
				String wText = Native.toString(windowText);
				RECT rectangle = new RECT();
				user32.GetWindowRect(hWnd, rectangle);
				// The following code is used to filter out:
				// 1. windows with empty text title
				// 2. invisible or minimized window
				if (wText.isEmpty() || !(User32.INSTANCE.IsWindowVisible(hWnd) && rectangle.left > -32000)) {
					return true;
				}
				// match title
				if (exactMatch && wText.equals(keyword) || !exactMatch && wText.contains(keyword)) {
					Rectangle rect = new Rectangle(rectangle);
					results.add(rect);
					System.out.println("[WindowUtil] Found: " + wText);
				}
				return true;
			}
		}, null);
		return results;
	}
}
