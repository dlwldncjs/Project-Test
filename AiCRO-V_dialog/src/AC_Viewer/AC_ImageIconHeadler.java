package AC_Viewer;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

public class AC_ImageIconHeadler {
	
	static int defult_Size = 40;
	
	
	 
	 public static ImageIcon reSizeImageICON(ImageIcon imgURL, int size) 
	 {
			
			ImageIcon ic = imgURL;
			int orisizeW = ic.getImage().getWidth(ic.getImageObserver());
			int orisizeH = ic.getImage().getHeight(ic.getImageObserver());
			double dAcpet= (double)orisizeW/(double)orisizeH;
			
			//double ireSizeW = (100*dAcpet);
			
			Image changedImg= ic.getImage().getScaledInstance((int)(size*dAcpet), size, Image.SCALE_SMOOTH );
		
			return new ImageIcon(changedImg);
		}
	 
	public static ImageIcon reSizeImageICON(String imgURL, int size) {
		
		ImageIcon ic = new ImageIcon(imgURL);
		Image changedImg= ic.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH );
	
		return new ImageIcon(changedImg);
	}
	
	public static ImageIcon reSizeImageICON(String imgURL) {
		
		ImageIcon ic = new ImageIcon(imgURL);
		Image changedImg= ic.getImage().getScaledInstance(defult_Size, defult_Size, Image.SCALE_SMOOTH );
	
		return new ImageIcon(changedImg);
	}
	

	public static ImageIcon reSizeImageICON(URL imgURL) {
		
		ImageIcon ic = new ImageIcon(imgURL);
		Image changedImg= ic.getImage().getScaledInstance(defult_Size, defult_Size, Image.SCALE_SMOOTH );
	
		return new ImageIcon(changedImg);
	}

}
