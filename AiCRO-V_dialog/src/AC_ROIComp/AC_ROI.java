package AC_ROIComp;

import java.awt.BasicStroke;
import java.awt.Color;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


import javax.swing.JPanel;


import AC_DicomData.ViewerStructure.AC_SliceInfo;
import AC_Viewer.AC_ImagePanel;
import AC_Viewer.Environment;
import AC_Viewer.ViewerMain;
import SYB_LIB.SYBTOOLS;

public class AC_ROI extends JPanel implements Cloneable, MouseMotionListener , MouseListener, FocusListener{



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int OVAL = ViewerMain.MM_ROI_OVAL;
	public static final int DISTANCE = ViewerMain.MM_ROI_DISTANCE;
	
	private  int MARGIN = (int) (Environment.g_ROIStokeSize*20);
	private float StokeSize = Environment.g_ROIStokeSize;
	private Color m_clrDrawColor = Environment.g_ClrROINomarl;
	
	private AC_ROIMeasuresPanel m_MeasurePanel= new AC_ROIMeasuresPanel();
	
	private double m_dOriSx = 0;
	private double m_dOriSy = 0;
	private double m_dOriEx = 0;
	private double m_dOriEy = 0;
	
	
	private int m_iDrawSx = 0;
	private int m_iDrawSy = 0;
	private int m_iDrawEx = 0;
	private int m_iDrawEy = 0;
	
	private int m_iImgPanelSx = 0;
	private int m_iImgPanelEx = 0;
	private int m_iImgPanelSy = 0;
	private int m_iImgPanelEy = 0;
	
	private int m_iImgRows = 0;
	private int m_iImgColunms = 0;

	private int m_Mode = 0;
	private boolean m_bMouseOn = false;
	private boolean m_bSeleted = false;
	
	private int m_pMouseLastX = 0;
	private int m_pMouseLastY = 0;
	
	private boolean m_bDragOn = false;
	private boolean m_bLineDirection =true;;
	
	


	
	

	public AC_ROI() {
		/*
		 * this.setSize(100, 100); this.setLocation(100,100); /* m_pDrawW = 100;
		 * m_pDrawH = 100;
		 */

		init();

	}

	public AC_ROI(int pSx, int pSy, int pEx, int pEy, int Mode) 
	{
		init();
		
	}
	
	public void setMeasureValue()
	{	
		AC_ImagePanel tmp = (AC_ImagePanel)this.getParent();
		
		
		if(m_Mode == AC_ROI.OVAL)
		{
		
			double[] tmpSignal = tmp.getSignalValue();
			calOvalMeasure(tmpSignal);
		}
		if(m_Mode == AC_ROI.DISTANCE)
			calDistance(tmp.getSliceInfo());
	}
	private void calDistance(AC_SliceInfo sliceInfo)
	{
		double[] darrPixelS = sliceInfo.getPixelSpacing(); 
		double dPixelSpacingX = darrPixelS[0];
		double dPixelSpacingY = darrPixelS[1];
		boolean bPixel = false;
		
		if(dPixelSpacingX==0.0||dPixelSpacingY==0.0)
		{
			dPixelSpacingX = dPixelSpacingY = 1.0;
			bPixel = true;
		}
		
		double dDistance  = Math.sqrt(Math.pow((m_dOriEy-m_dOriSy)*dPixelSpacingY,2)+
				Math.pow((m_dOriEx-m_dOriSx)*dPixelSpacingX,2));
		m_MeasurePanel.setMeasureValue(dDistance,bPixel);
	
	}
	
	private void calOvalMeasure(double[] dSignal) {
		int pSx = (int)m_dOriSx;
		int pSy = (int)m_dOriSy;

		int iWidth = (int)m_dOriEx- (int)m_dOriSx;
		int iHeight =  (int)m_dOriEy- (int)m_dOriSy;

		double dMin = 2000.0;
		double dMax = 0.0;
		double dMean = 0.0;
		double dSum = 0.0;
		int iTotalPixel = 0;

		final int iRoiCenterX = pSx + (iWidth / 2);
		final int iRoiCenterY = pSy + (iHeight / 2);

		for (int j = pSy; j < pSy + iHeight; j++) {

			for (int i = pSx; i < pSx + iWidth; i++) {

				int idx = j * m_iImgRows + i;

				if (idx < 0 || idx > dSignal.length)
					continue;

				double dTmpValue = dSignal[idx];

				double dchkInRoi = (Math.pow(i - iRoiCenterX, 2) / Math.pow(iWidth / 2, 2))
						+ (Math.pow(j - iRoiCenterY, 2) / Math.pow(iHeight / 2, 2));
				if (dchkInRoi <= 1.00) {
					if (dTmpValue > dMax)
						dMax = dTmpValue;
					else if (dTmpValue < dMin)
						dMin = dTmpValue;
					dSum += dTmpValue;
					iTotalPixel++;
				}
			}
		}

		dMean = dSum / iTotalPixel;
		
		
		
		m_MeasurePanel.setMeasureValue(dMin,dMax,dMean);
		
		

	}

	public void init() {

		
		this.setName("ROI_Panel");
		this.setOpaque(false);
	//	this.setBackground(Color.red);
	//	this.setBackground(new Color(255, 0, 0, 0));
		this.setSize(100, 100);
		this.setLocation(100, 100);
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addFocusListener(this);
		
		



		this.setDoubleBuffered(false);
		this.add(m_MeasurePanel);
		m_MeasurePanel.setLocation(0,0);
	//	m_MeasurePanel.setSize(m_iDrawEx-m_iDrawEy,MARGIN);

	}
	
	public void setImageSize(int iRows, int iColunms)
	{
		m_iImgRows = iRows;
		m_iImgColunms = iColunms;
	}
	
	
	public void setROI(double pSx, double pSy, double pEx, double pEy, int Mode) 
	{
		boolean  bLineChk = false;; 
		
		
		if(pSx>pEx)
		{
			m_dOriSx = pEx;
			m_dOriEx = pSx;
			bLineChk = true;
		}else
		{
			m_dOriSx = pSx;
			m_dOriEx = pEx;
		}
		
		if(pSy>pEy)
		{
			m_dOriSy = pEy;
			m_dOriEy = pSy;
			if(bLineChk)
				m_bLineDirection  = false; 
		}else
		{
			m_dOriSy = pSy;
			m_dOriEy = pEy;
			if(!bLineChk)
				m_bLineDirection  = false; 		
		}
		
		switch (Mode) {
		case ViewerMain.MM_ROI_DISTANCE:
			m_Mode = AC_ROI.DISTANCE;
			break;
		case ViewerMain.MM_ROI_OVAL:
			m_Mode = AC_ROI.OVAL;
			break;

		default:
			break;
		}
		

		
	}
	
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		drawROI(g);
		

	//	g.drawLine(0, 0, 20, 20);


		System.out.println("repaint! ROI");


	}
	
	public void drawROI(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		if(m_bMouseOn)
			m_clrDrawColor = Environment.g_ClrROIMouseON;
		else if( m_bSeleted)
			m_clrDrawColor = Environment.g_ClrROISelected;
		else
			m_clrDrawColor = Environment.g_ClrROINomarl;
		g2.setColor(m_clrDrawColor);
		  g2.setStroke(new BasicStroke(StokeSize,BasicStroke.CAP_ROUND,0));
		  
		  switch (m_Mode) {
		case AC_ROI.DISTANCE:
			if(!m_bLineDirection)
					g2.drawLine(MARGIN,MARGIN,m_iDrawEx-m_iDrawSx+MARGIN, m_iDrawEy-m_iDrawSy+MARGIN);
			else
				 g2.drawLine(m_iDrawEx-m_iDrawSx+MARGIN,MARGIN,MARGIN, m_iDrawEy-m_iDrawSy+MARGIN);
			break;
		case AC_ROI.OVAL:
			  g2.drawOval(MARGIN,MARGIN,m_iDrawEx-m_iDrawSx, m_iDrawEy-m_iDrawSy);
			break;

		default:
			break;
		}
		
			
	}
	
	private void moveOriPoint(int iMoveX, int iMoveY)
	{
		
		/*	m_iDrawSx -= iMoveX; 
			m_iDrawEx -= iMoveX; 
			                    
			m_iDrawSy -= iMoveY; 
			m_iDrawSy -= iMoveY; */
		
		double dTmpMx =      	iMoveX;// - m_iImgPanelSx;
		double dTmpMy =      	iMoveY;// - m_iImgPanelSy;
		dTmpMx = (dTmpMx*m_iImgRows)/m_iImgPanelEx;
		dTmpMy = (dTmpMy*m_iImgColunms)/m_iImgPanelEy;
		
		
		System.out.println("Move point dTmpMx :"+dTmpMx + " dTmpMy : "+dTmpMy);
		

		
		
		
	
		m_dOriSx -= dTmpMx;
		m_dOriEx -= dTmpMx;
		
		m_dOriSy -= dTmpMy;
		m_dOriEy -= dTmpMy;
		
		System.out.println("m_dOriSx :"+m_dOriSx + " m_dOriEx : "+m_dOriEx);
		System.out.println("m_dOriSy :"+m_dOriSy + " m_dOriEy : "+m_dOriEy);
		
		
		
		
		//return output;
	}
	
	
	public void setDrawPointROI(int iViewSx, int iViewSy, int iViewEx, int iViewEy)
	{
				
		
		 m_iImgPanelSx = iViewSx;  
		 m_iImgPanelEx = iViewEx;  
		 m_iImgPanelSy = iViewSy;  
		 m_iImgPanelEy = iViewEy;  
		//  g.setStroke(new BasicStroke(50,BasicStroke.CAP_ROUND,0));
		
		
	//	g.clearRect(0, 0, this.getWidth(), this.getHeight());
		

		/*System.out.println("m_pShowImgSx : "+ iViewSx +"m_pShowImgSy :  "+iViewSy+
				"m_pShowImgEx : "+ iViewEx +"m_pShowImgEx "+iViewEy);*/
		                         
			double iTmpSx       = m_dOriSx*iViewEx;
	    	double iTmpSy       = m_dOriSy*iViewEy;
	    	double iTmpEx       = m_dOriEx*iViewEx;
	    	double iTmpEy       = m_dOriEy*iViewEy;
	    	System.out.println("psx : "+ iTmpSx +"psy "+iTmpSy+ "pEx : "+ iTmpEx +"pEy "+iTmpEy);	
	    	
	    	
	    	 iTmpSx       /= m_iImgColunms;             
	    	 iTmpSy       /= m_iImgRows;                
	    	 iTmpEx       /= m_iImgColunms;            
	    	 iTmpEy       /= m_iImgRows;                
		                     System.out.println("psx : "+ iTmpSx +"psy "+iTmpSy+ "pEx : "+ iTmpEx +"pEy "+iTmpEy);
		m_iDrawSx = (int) iTmpSx+ iViewSx;
		m_iDrawSy = (int) iTmpSy+ iViewSy;
		m_iDrawEx =  (int)iTmpEx+ iViewSx;
		m_iDrawEy =  (int)iTmpEy+ iViewSy;
		
		this.setSize( m_iDrawEx-m_iDrawSx+(MARGIN*2), m_iDrawEy-m_iDrawSy+(MARGIN*2));
		this.setLocation(m_iDrawSx-(MARGIN), m_iDrawSy-(MARGIN));
//		this.locate(m_iDrawSx-(MARGIN), m_iDrawSx-(MARGIN));
		
	//	calPoint(m_iDrawSx,  m_iDrawSy,  m_iDrawEx,  m_iDrawEy) ;
		
	
		System.out.println("Set ROI Paenl  MARGIN : "+MARGIN+"  iDrawSx: "+ m_iDrawSx +"m_iDrawSy : "+m_iDrawSy+ "m_iDrawEx : "+ m_iDrawEx +"m_iDrawEy : "+m_iDrawEy);
	
	

	  
	  
	  

	}
	
	
	private void chkMouseOn(int pMx, int pMy)
	{
		
		boolean bChk = false;
		if (m_Mode == AC_ROI.OVAL) 
		{
			
			

			final int iRoiCenterX = MARGIN + ((m_iDrawEx-m_iDrawSx) / 2);
			final int iRoiCenterY = MARGIN + ((m_iDrawEy-m_iDrawSy) / 2);

			double dchkInRoi = (Math.pow(pMx - iRoiCenterX, 2) / Math.pow((m_iDrawEx-m_iDrawSx) / 2, 2))
					+ (Math.pow(pMy - iRoiCenterY, 2) / Math.pow((m_iDrawEy-m_iDrawSy) / 2, 2));
			
			double dSelcetionRange = (double) MARGIN* 0.01;
			
			
		

			if (dchkInRoi < 1.0 + dSelcetionRange && dchkInRoi > 1.0 - dSelcetionRange)
			{
				
		;
				bChk=true;
			}
				
		}else if(m_Mode == AC_ROI.DISTANCE)
		{
		double dSelcetionRange = (double) MARGIN/2;
			
		
		int chkEx = m_iDrawEx-m_iDrawSx+MARGIN;
		int chkEy =  m_iDrawEy-m_iDrawSy+MARGIN;

			double dchkInRoi = SYBTOOLS.calcDistance(MARGIN, MARGIN, chkEx,chkEy,
					pMx, pMy);
		

		

			if (dchkInRoi < 1.0 + dSelcetionRange && dchkInRoi > -1.0 - dSelcetionRange
					&& ((pMx > (MARGIN) - dSelcetionRange && pMx < chkEx + dSelcetionRange
							&& pMy > (MARGIN) - dSelcetionRange && pMy <chkEy + dSelcetionRange)
							|| (pMx < (MARGIN) + dSelcetionRange && pMx > chkEx - dSelcetionRange
									&& pMy < (MARGIN) - dSelcetionRange
									&& pMy >chkEy - dSelcetionRange)
							|| (pMx > (MARGIN) - dSelcetionRange && pMx < chkEx + dSelcetionRange
									&& pMy < (MARGIN) - dSelcetionRange
									&& pMy >chkEy - dSelcetionRange)
							|| (pMx < (MARGIN) + dSelcetionRange && pMx > chkEx - dSelcetionRange
									&& pMy > (MARGIN) - dSelcetionRange
									&& pMy <chkEy + dSelcetionRange))

			)
			bChk = true;
		
		}
		
		
		
		
		if (bChk)
		{
			System.out.println("in!!!!!!!!!!");
			if(!m_bMouseOn)
			{
				
				m_bMouseOn= true;
				repaint();
			}
		}
		else
		{
			if(m_bMouseOn)
			{
				
				m_bMouseOn = false;
				repaint();
			}
		}
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("x : "+e.getX()+" y : "+e.getY() );

		if(m_bMouseOn)
		{
			if(!m_bDragOn)
			{
				m_pMouseLastX =  e.getX();
				m_pMouseLastY =  e.getY();
				m_bDragOn=true;

			}else
			{




				int pMoveFx =  e.getX() - m_pMouseLastX;//-(m_iDrawEx-m_iDrawSx);
				int pMoveFy =  e.getY() - m_pMouseLastY;//-  (m_iDrawEy-m_iDrawSy);

				/*if(pMoveFx>10 || pMoveFy>10)
		{
			pMoveFx = pMoveFy = 0;

		}*/

				Point tmp = this.getLocation();
				System.out.println("Draw x  : "+(m_iDrawSx-MARGIN)+"Draw Y : "+(m_iDrawSy-MARGIN));
				
				this.setLocation((int)tmp.x+pMoveFx, (int)tmp.y+pMoveFy);
				
				System.out.println("Move x  : "+this.getLocation().x+"Move Y : " +this.getLocation().y);
			//	setMeasureValue();
			}
		}

		
	}
	

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
		System.out.println("ROI X : "+ e.getX() + " Y : "+ e.getY());
	//	if(!m_bDragOn)
		{
			chkMouseOn(e.getX(), e.getY());
			System.out.println("MouseOn");
		}
		m_pMouseLastX = e.getX();
		m_pMouseLastY = e.getY();
		
		AC_ImagePanel tmp = (AC_ImagePanel)this.getParent();

		tmp.updataePixelVPanel(e.getX()+this.getLocation().x,  e.getY()+ this.getLocation().y);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(m_bMouseOn)
		{
			if(m_bSeleted)
			{
				
				m_bSeleted = false;
				
			}
			else
			{
			
				m_bSeleted = true;
			}
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
		AC_ImagePanel tmp = (AC_ImagePanel)this.getParent();
		tmp.setFocus(true);
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	//	m_bMouseOn = false;
		//repaint();
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
		Point tmp = this.getLocation();
		System.out.println("Move ROI Now Loctation : x - "+(tmp.x)+" y : "+ (tmp.y));
		System.out.println("Move ROI Movew Panel : x - "+(m_iDrawSx-(MARGIN)-tmp.x)+" y : "+ (m_iDrawSy-(MARGIN)-tmp.y));
		
		if(m_bDragOn)
		{
			System.out.println("Before m_dOriSx :"+m_dOriSx + " m_dOriEx : "+m_dOriEx+" m_dOriSy :"+m_dOriSy + " m_dOriEy : "+m_dOriEy);
			
			int iMoveFatcorX = m_iDrawSx-(MARGIN)-this.getLocation().x;
			int iMoveFatcorY = m_iDrawSy-(MARGIN)-this.getLocation().y;

			
			moveOriPoint(iMoveFatcorX, iMoveFatcorY);
			m_iDrawSx -= iMoveFatcorX;
			m_iDrawEx -= iMoveFatcorX; 
			m_iDrawSy -= iMoveFatcorY;
			m_iDrawEy -= iMoveFatcorY;
			
			System.out.println("After m_dOriSx :"+m_dOriSx + " m_dOriEx : "+m_dOriEx+" m_dOriSy :"+m_dOriSy + " m_dOriEy : "+m_dOriEy);
			setMeasureValue();
			m_bDragOn=false;
		}
		
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
	//	System.out.println(x);
		m_bMouseOn = false;
		repaint();
		
	}


	
	
	
	
	


}
