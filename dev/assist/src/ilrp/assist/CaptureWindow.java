package ilrp.assist;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

public class CaptureWindow extends JFrame{  
    /**
	 * 
	 */
	private static final long serialVersionUID = -6561389639931501061L;
	
	protected Point mouseStartPoint = null;
	protected Point mouseEndPoint = null;
	protected int height = -1;
	protected int width = -1;
	protected boolean ready = false;
	
	public CaptureWindow() {   

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.height = dimension.height;
        this.width = dimension.width;
        this.setBounds(0, 0, width, height);	   
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.setAlwaysOnTop(true);
        this.setUndecorated(true);
        // add key event
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyWatcher(this));
        // add mouse event
        MouseWatcher mouseWatcher = new MouseWatcher(this);
        this.addMouseListener(mouseWatcher);
        this.addMouseMotionListener(mouseWatcher); 
		this.setVisible(true);
		this.setWindowOpacity(0.8f);
    }  
	
	@SuppressWarnings("restriction")
	public void setWindowOpacity(float opacity) {
		com.sun.awt.AWTUtilities.setWindowOpacity(this, opacity);          
	}
	
	public boolean getReady() {
		return ready;
	}
}

// mouse listener
class MouseWatcher implements MouseListener, MouseMotionListener{

	private CaptureWindow window = null;
	
	public MouseWatcher(CaptureWindow window) {
		this.window = window;
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		
		Graphics2D g = (Graphics2D) window.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
		g.setStroke(new BasicStroke(5.0f));
		g.setColor(new Color(1.0f, 0.5f, 0.5f, 1.0f));
		g.clearRect(0, 0, window.width, window.height);
		g.drawRect((int)window.mouseStartPoint.getX(), (int)window.mouseStartPoint.getY(), (int)(arg0.getX() - window.mouseStartPoint.getX()), (int)(arg0.getY() - window.mouseStartPoint.getY()));		
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		window.mouseStartPoint = arg0.getPoint();		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		window.mouseEndPoint = arg0.getPoint();
	}
}

// key listener
class KeyWatcher implements KeyEventDispatcher {

	private CaptureWindow window = null;

	public KeyWatcher(CaptureWindow win) {
		this.window = win;
	}

	public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getID() == KeyEvent.KEY_PRESSED) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				System.exit(0);
			}
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (window.mouseStartPoint != null && window.mouseEndPoint != null) {
					this.window.setVisible(false);
					window.ready = true;
				}
			}
		}
		return false;
	}
}