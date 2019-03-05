package AC_ROIComp;


import java.awt.Color;

import javax.swing.JPanel;

import AC_Viewer.ViewerMain;
import AC_funcComp.AC_AutoLabel;


public class AC_ROIMeasuresPanel extends JPanel{



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int OVAL = ViewerMain.MM_ROI_OVAL;
	public static final int DISTANCE = ViewerMain.MM_ROI_DISTANCE;
	
	
	AC_AutoLabel m_AutoLabel = null;

	public AC_ROIMeasuresPanel() {
		


		init();

	}

	public AC_ROIMeasuresPanel(int pSx, int pSy, int pEx, int pEy, int Mode) 
	{
		init();
		
	}
	
	public void setROILocation()
	{
		
	}

	public void init() {

		
		this.setName("ROI_Measure_panel");
		this.setOpaque(false);
		this.setBackground(new Color(255, 255, 255, 0));
		this.setVisible(false);



		this.setDoubleBuffered(false);

	}
	
	public void setMeasureValue(double dDistance, boolean bPixel)
	{
		if(m_AutoLabel==null)
		{

			m_AutoLabel = new AC_AutoLabel("");
			m_AutoLabel.setForeground(Color.YELLOW);
			m_AutoLabel.setOpaque(true);
			m_AutoLabel.setBackground(new Color(255, 0, 0, 125));
			this.add(m_AutoLabel);
			this.setLocation(0,-10);
			this.setSize(200, 200);
			this.setVisible(true);
		}


		updateLabel(dDistance,bPixel);
	}
	
	
	public void setMeasureValue(double dMin, double dMax, double dMean)
	{

		if(m_AutoLabel==null)
		{

			m_AutoLabel = new AC_AutoLabel("");
			m_AutoLabel.setForeground(Color.YELLOW);
			m_AutoLabel.setOpaque(true);
			m_AutoLabel.setBackground(new Color(255, 0, 0, 125));
			this.add(m_AutoLabel);
			this.setLocation(0,-10);
			this.setSize(200, 200);
			this.setVisible(true);
		}

		updateLabel( dMin,  dMax,  dMean);

	}
	
	public void updateLabel(double dMin, double dMax, double dMean)
	{

		String sMin = String.format("%.1f", dMin);
		String sMax = String.format("%.1f", dMax);
		String sMean = String.format("%.1f", dMean);
		String tmp =    "<html><p>Min : "+sMin+" Max : "+sMax+"<br>"
				+ "Mean : "+sMean+"</p></html>";// "min :" + dMin + "Max : " + dMax+ lineSeperator+ "Mean : " + dMean;
		m_AutoLabel.setText(tmp);
		repaint();
	}
	
	public void updateLabel(double dDistance,boolean bPixel)
	{

		String sDistance = String.format("%.1f", dDistance);
	if(bPixel) {
		String tmp =    "<html><p>Distance : "+sDistance+" px </p></html>";// "min :" + dMin + "Max : " + dMax+ lineSeperator+ "Mean : " + dMean;
		m_AutoLabel.setText(tmp);
	}
		else {
		String tmp =    "<html><p>Distance : "+sDistance+" mm </p></html>";// "min :" + dMin + "Max : " + dMax+ lineSeperator+ "Mean : " + dMean;
		m_AutoLabel.setText(tmp);
		}
		repaint();
	}
	
	

	



	
	
	
	
	


}
