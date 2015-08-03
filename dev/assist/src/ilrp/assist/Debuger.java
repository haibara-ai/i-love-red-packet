package ilrp.assist;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Debuger {

	private static Map<String, Long> timerMap = new HashMap<String, Long>();
	private static Map<String, Long> startMap = new HashMap<String, Long>();
	private static Map<String, Long> stopMap = new HashMap<String, Long>();
	
	private static Stroke middleStroke = new BasicStroke(2.0f);
	
	public static void startTimer(String timerName) {
		startMap.put(timerName, System.currentTimeMillis());
	}
	
	public static void stopTimer(String timerName) {
		stopMap.put(timerName, System.currentTimeMillis());
		if (!startMap.containsKey(timerName)) {
			System.err.println("error timer set");
		} else {
			if (startMap.get(timerName) > stopMap.get(timerName)) {
				System.err.println("error timer set");
			} else {
				timerMap.put(timerName, stopMap.get(timerName) - startMap.get(timerName));
//				System.out.println(timerName + ":" + timerMap.get(timerName));
			}
		}
	}

	public static void printTimeGap(String timerName) {
//		System.out.println(timerName + ":" + timerMap.get(timerName));
	}

	public static BufferedImage loadImage(String imageFile) {
		try {
			return ImageIO.read(new File(imageFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void writeImage(BufferedImage image, String filename) {
		try {
			ImageIO.write(image, "bmp", new File("D:\\github\\i-love-red-packet\\dev\\assist\\" + filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void drawPointOnImage(BufferedImage image, Point location) {
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setColor(Color.RED);
		g2d.setStroke(middleStroke);
		g2d.drawRect((int)location.getX()-2, (int)location.getY()-2, 4, 4);
		g2d.dispose();
	}
	
	public static void drawPointOnImage(BufferedImage image, Rectangle rec) {
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setColor(Color.RED);
		g2d.setStroke(middleStroke);
		g2d.drawRect((int)rec.getX(), (int)rec.getY(), (int)rec.getWidth(), (int)rec.getHeight());
		g2d.dispose();
	}
	
	public static void drawLineOnImage(BufferedImage image, Point startPoint, Point endPoint) {
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setColor(Color.RED);
		g2d.setStroke(middleStroke);
		g2d.drawLine((int)startPoint.getX(), (int)startPoint.getY(), (int)endPoint.getX(), (int)endPoint.getY());
		g2d.dispose();
		
	}
}


