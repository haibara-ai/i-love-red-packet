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
		List<HWND> wins = findWindow(keyword, exactMatch);
		List<Rectangle> results = new ArrayList<Rectangle>();
		for (HWND win : wins) {
			results.add(getWindowRect(win));
		}
		return results;
	}
	
	/**
	 * 查找包含 keyword 的所有 Window，并返回句柄
	 * 需要查找所有窗口时：locateWindow("", false)
	 * @param keyword
	 * @param exactMatch
	 * @return
	 */
	public static List<HWND> findWindow(final String keyword, final boolean exactMatch) {
		final User32 user32 = User32.INSTANCE;
		final List<HWND> results = new ArrayList<HWND>();
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
					results.add(hWnd);
					System.out.println("[WindowUtil] Found: " + wText);
				}
				return true;
			}
		}, null);
		return results;
	}
	
	/**
	 * 获取当前窗口的位置
	 * @param hWnd
	 * @return
	 */
	public static Rectangle getWindowRect(HWND hWnd) {
		final User32 user32 = User32.INSTANCE;
		RECT rectangle = new RECT();
		user32.GetWindowRect(hWnd, rectangle);
		return new Rectangle(rectangle);
	}
	
	/**
	 * 重新设置窗口的大小和位置
	 * @param hWnd
	 */
	public static boolean relocateWindow(HWND hWnd, int x, int y, int width, int height) {
		final boolean repaint = true;
		return User32.INSTANCE.MoveWindow(hWnd, x, y, width, height, repaint);
	}
	
	/**
	 * 将窗口放到最前
	 * @param hWnd
	 */
	public static boolean bringToFront(HWND hWnd) {
		return User32.INSTANCE.SetForegroundWindow(hWnd);
	}
	
	public static void main(String[] args) {
		List<HWND> wins = findWindow("海马玩模拟器(Droid4X)", false);
		for (HWND win : wins) {
			bringToFront(win);
			Rectangle rect = getWindowRect(win);
			System.out.println(rect);
		}
	}
}
