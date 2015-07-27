package ilrp.assist;

import java.awt.image.BufferedImage;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCRTask implements Runnable{

	private BufferedImage image = null;
	private static ITesseract tess = new Tesseract();
	private String ocrRet = "";
	
	public OCRTask(BufferedImage image) {
		this.image = image;
	}
	
	@Override
	public void run() {
		try {
			ocrRet = tess.doOCR(image).trim();
		} catch (TesseractException e) {
			e.printStackTrace();
		}
		ocrRet = ocrRet.substring(0, ocrRet.length() - 1);
		System.out.println(ocrRet);
	}
	
}
