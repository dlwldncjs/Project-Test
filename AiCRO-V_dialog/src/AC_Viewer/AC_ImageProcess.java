package AC_Viewer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class AC_ImageProcess {
	
	
	public static BufferedImage createFlipped_H(BufferedImage image)
	{
		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getScaleInstance(1, -1));
		at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
		return createTransformed(image, at);
	}
	
	public static BufferedImage createFlipped_V(BufferedImage image)
	{
		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getScaleInstance(-1, 1));
		at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(),0));
		return createTransformed(image, at);
	}

	private static BufferedImage createTransformed(
			BufferedImage image, AffineTransform at)
	{
		BufferedImage newImage = new BufferedImage(
				image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.transform(at);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}

}
