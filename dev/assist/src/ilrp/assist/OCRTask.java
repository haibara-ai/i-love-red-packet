package ilrp.assist;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCRTask implements Runnable{

	private BufferedImage image = null;
	private static ITesseract tess = new Tesseract();
	static {
		 tess.setTessVariable("tessedit_char_whitelist", ".0123456789");
	}
	private String ocrRet = "";
	
	public OCRTask(BufferedImage image) {
		this.image = image;
	}
	
	public OCRTask() {
		
	}
	
	@Override
	public void run() {
		try {
			ocrRet = tess.doOCR(image).trim();
		} catch (TesseractException e) {
			e.printStackTrace();
		}
		System.out.println(ocrRet);
	}
	
	public static String recognize(BufferedImage image, boolean preProcessed) {
		String ret = "";
		Debuger.startTimer("ocr");
//		image = binarizedImage(image);
		if (preProcessed) {
			image = normalizedImage(image);
		}
//		try {
//			ImageIO.write(image, "png", new File("D:\\github\\i-love-red-packet\\dev\\assist\\ocr.png"));
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		long start = System.currentTimeMillis();
		try {
			ret = tess.doOCR(image).trim();
		} catch (TesseractException e) {
			e.printStackTrace();
		}
		Debuger.stopTimer("ocr");
		return ret;
	}
	
	public static BufferedImage normalizedImage(BufferedImage image) {
		binarizedImage(image);		
		return shrinkImage(image, 0.5);
	}
	
	public static BufferedImage shrinkImage(BufferedImage image, double scale) {
		BufferedImage resizedImage = new BufferedImage((int)(image.getWidth()*scale), (int)(image.getHeight()*scale), image.getType());
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);

		if (scale > 1.0) {
			g.setComposite(AlphaComposite.Src);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.dispose();
		
		return resizedImage;
	}
	
	private static int blankRGB = (new Color(255,255,255)).getRGB();
	private static int blackRGB = (new Color(0,0,0)).getRGB();
	
	public static BufferedImage binarizedImage(BufferedImage image) {
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				if (image.getRGB(i, j) != blackRGB) {
					image.setRGB(i, j, blankRGB);
				}
			}
		}
		return image;		
	}
	
	public static void main(String... args) {
		OCRTask ot = new OCRTask();
		try {
			BufferedImage image = ImageIO.read(new File("D:\\github\\i-love-red-packet\\dev\\assist\\ocr.png"));
			long start = System.currentTimeMillis();
//			ot.binarizedImage(image);
			System.out.println(ot.recognize(image,false));
			long end = System.currentTimeMillis();
			System.out.println("ocr gap:"+((end-start)));
			start = System.currentTimeMillis();
			ot.binarizedImage(image);
			end = System.currentTimeMillis();
			System.out.println("binarize gap:"+(end-start));
			ImageIO.write(image, "png", new File("D:\\github\\i-love-red-packet\\dev\\assist\\binary.png"));
			start = System.currentTimeMillis();
			System.out.println(ot.recognize(image,false));
			end = System.currentTimeMillis();
			System.out.println("binary ocr gap:"+((end-start)));
			//
			start = System.currentTimeMillis();
			BufferedImage newImage = ot.shrinkImage(image,0.5);
			end = System.currentTimeMillis();
			System.out.println("shrink gap:"+(end-start));
			ImageIO.write(newImage, "png", new File("D:\\github\\i-love-red-packet\\dev\\assist\\shrink.png"));
			start = System.currentTimeMillis();
			System.out.println(ot.recognize(newImage,false));
			end = System.currentTimeMillis();
			System.out.println("shrink ocr gap:"+((end-start)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
