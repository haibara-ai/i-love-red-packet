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
 * �� Windows ��ص�һЩӦ��
 * 
 * @author aleck
 *
 */
public class WindowUtil {
	/**
	 * ���Ұ��� keyword ������ Window ��λ��
	 * ��Ҫ�������д���ʱ��locateWindow("", false)
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
	 * ���Ұ��� keyword ������ Window�������ؾ��
	 * ��Ҫ�������д���ʱ��locateWindow("", false)
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
	 * ��ȡ��ǰ���ڵ�λ��
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
	 * �������ô��ڵĴ�С��λ��
	 * @param hWnd
	 */
	public static boolean relocateWindow(HWND hWnd, int x, int y, int width, int height) {
		final boolean repaint = true;
		return User32.INSTANCE.MoveWindow(hWnd, x, y, width, height, repaint);
	}
	
	/**
	 * �����ڷŵ���ǰ
	 * @param hWnd
	 */
	public static boolean bringToFront(HWND hWnd) {
		return User32.INSTANCE.SetForegroundWindow(hWnd);
	}
	
	public static void main(String[] args) {
		List<HWND> wins = findWindow("������ģ����(Droid4X)", false);
		for (HWND win : wins) {
			bringToFront(win);
			Rectangle rect = getWindowRect(win);
			System.out.println(rect);
		}
	}
}
