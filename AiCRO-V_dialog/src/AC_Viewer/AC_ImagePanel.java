package AC_Viewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import AC_DicomData.AC_DataConverter;
import AC_DicomData.AC_Modality;
import AC_DicomData.DicomIO.*;
import AC_DicomData.ViewerStructure.AC_SeriesInfo;
import AC_DicomData.ViewerStructure.AC_SliceInfo;
import AC_DicomData.ViewerStructure.AC_ViewrDcmData;
import AC_ROIComp.AC_ROI;
import AC_funcComp.AC_CheckBox;
import AC_funcComp.AC_IPPopUpMeum;
import AC_funcComp.AC_TxtPanel;
import SYB_LIB.SYBTOOLS;


public class AC_ImagePanel extends JPanel implements MouseListener,  MouseMotionListener
, ComponentListener{
	private String m_tmpssss = "";
	private int m_iSampleperPixel = 1;
	private static final long serialVersionUID = 1L;
	private BufferedImage showImg = null;
	private double[] m_dNowSignal = null;
	File[] m_filelist;
	
	private AC_SeriesInfo m_SeriesInfo = null;
	private AC_SliceInfo m_SliceInfo = null;
	
	private double[][] m_arrdPatientOriantation = new double[2][3];
	
	private boolean m_flagFipped = false;
	
	private double m_dRefSliceLocation = 0.0;
	private double m_dSliceLocation = 0.0;
	private double m_dOriWindowCenter = 0;
	private double m_dOriWindowWidth = 0;
	private double m_dIniiWindowCenter = Environment.g_IniWindowLevel[0];
	private double m_dIniiWindowWidth = Environment.g_IniWindowLevel[1];
	private double m_dWindowCenter = m_dIniiWindowCenter;
	private double m_dWindowWidth = m_dIniiWindowWidth;
	
	private int m_nSliceN = 0;
	private int m_iWidth = 0;
	private int m_iHeight = 0;
	
	private double[] m_dPixelSpacing = new double[2];
	
	private int m_iNowSliceN = 0;
	private int m_pShowImgSx = 0;
	private int m_pShowImgEx = 0;
	private int m_pShowImgSy = 10;
	private int m_pShowImgEy = 10;
	private int m_pLastMousex = 0;
	private int m_pLastMousey = 0;
	private int m_iMoveFactorX = 0;
	private int m_iMoveFactorY = 0;
	private int m_iZoomFactor = 0;
	private int m_Modality = AC_Modality.CT;
	private int m_iNowMosueX =0;
	private int m_iNowMosueY =0;
	private double m_dAspect = 0;
	
	private boolean m_bInfoDisable = true;
	private boolean m_bMultiSclie = false;
	private boolean m_bDrawROI = false;
	
	private AC_ROI m_drawROI = new AC_ROI();
	private AC_ROI m_overayROI = null;
	private AC_ROI m_refROI = new AC_ROI();
	
	private int m_pDragSx = 0;
	private int m_pDragSy = 0;
	
	private AC_ROI m_SelectionROI=null;
	private JPanel m_ROIMeasur =new JPanel();
	
	AC_TxtPanel m_tpTopLeft = new AC_TxtPanel();
	AC_TxtPanel m_tpTopRight = new AC_TxtPanel();
	AC_TxtPanel m_tpBottomLeft = new AC_TxtPanel();
	AC_TxtPanel m_tpBottomRight = new AC_TxtPanel();
	AC_TxtPanel m_tpROIMearPanel = new AC_TxtPanel();
	//orientation panel
	AC_TxtPanel m_tpTop= new AC_TxtPanel();
	AC_TxtPanel m_tpBottom= new AC_TxtPanel();
	AC_TxtPanel m_tpLeft =  new AC_TxtPanel();
	AC_TxtPanel m_tpRight = new AC_TxtPanel();
	
	AC_CheckBox m_cbMultiSlice = null;
	
	String[] m_sarrTopLeft     = new String[2];
	String[] m_sarrTopRight    = new String[7];
	String[] m_sarrBottomLeft  = new String[3];
	String[] m_sarrBottomRight = new String[3];
	
	private String[] m_sLeft     = new String[1];
	private String[] m_sTop      = new String[1];
	private String[] m_sRigth    = new String[1];
	private String[] m_sBottom   = new String[1];
	                            
	
	AC_IPPopUpMeum m_PopUp = new AC_IPPopUpMeum();
//	ACW_IPPopUpMeum m_wPopup = new ACW_IPPopUpMeum();
	
	
	
	

	private boolean m_bFoucs = false;
	
	
	
	

	
	private int MOUSE_MODE = Environment.g_INI_MOUSE_MODE;
	
	
	private int ROI_MODE = AC_ROI.OVAL;
	
	


	private static int OFFSET = 5;
	
	private Cursor CURSOR_DEFAULT = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	
	URL url_Window_Cursor = getClass().getClassLoader().getResource(DefultURL.Cursor_Window);
	URL url_Zooming_Cursor = getClass().getClassLoader().getResource(DefultURL.Cursor_Zooming);
	private Cursor CURSOR_WINDOW = getToolkit().createCustomCursor(getToolkit().getImage(url_Window_Cursor),new Point(16,16) ,"Window");
	private Cursor CURSOR_Zooming = getToolkit().createCustomCursor(getToolkit().getImage(url_Zooming_Cursor),new Point(16,16) ,"Zoom");
	private Cursor CURSOR_MOVE = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
	
	private boolean m_bDrawMousePoint;
	private int m_iSampling = 2;
	private int m_iDragLimit = 3;
	private int[] m_iStartPotin = new int[2];
	private AC_SliceInfo m_nowSliceInfo;


	private static int DOUBLE_OFFSET = OFFSET * 2;
	
	public AC_SeriesInfo getSeries()
	{
		return m_SeriesInfo;
	}
	
	public AC_SliceInfo getSliceInfo()
	{
		return m_nowSliceInfo;
	}
	
	
	public AC_ImagePanel( )
	{
		super();
		createImageArea(50,50);
	}
	
	public AC_ImagePanel(AC_SeriesInfo inputSeries)
	{
		super();
		iniImagePanel(inputSeries);

//		setSeries(inputSeries);

	}
	
	
	private void createImageArea(int width, int height) {
		

		this.setDoubleBuffered(false);	
		this.setCursor(CURSOR_DEFAULT);
		this.setLayout(null);
		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createLineBorder(Color.BLACK, 1), BorderFactory
				.createLineBorder(Color.DARK_GRAY, 2)));
	
		this.setBackground(Color.BLACK);

		this.setFocusable(false);

		this.addComponentListener(this);
		

		

	//	 buildROIMeasurement();
	}
	
	
	
	/*private void addBaseComp()
	{
		this.add(m_tpTopLeft);
		this.add(m_tpTopRight);
		this.add(m_tpBottomLeft);
		this.add(m_tpBottomRight);
		
		this.add(m_tpTop);
		this.add(m_tpRight);
		this.add(m_tpLeft);
		this.add(m_tpBottom);
		
		addMultiSliceBouttom();
	}*/
	
	
	private void buildOrientationPanel()
	{
		m_tpTop= new AC_TxtPanel("Top",AC_TxtPanel.TOP); 
		m_tpRight = new AC_TxtPanel("Rigth",AC_TxtPanel.RIGHT); 

		m_tpLeft = new AC_TxtPanel("Left",AC_TxtPanel.LEFT); 
		m_tpBottom = new AC_TxtPanel("Bottom",AC_TxtPanel.BOTTOM); 
		
		
		
	}
	
	
	
	public void setRefSliceLocation(double input)
	{
		m_dRefSliceLocation =  input;
	}
	
	public double getRefSliceLocation()
	{
		return m_dSliceLocation; //=  input;
	}
	
	public double getSliceLocation()
	{
		return m_dSliceLocation;
	}
	public boolean hasOverlay()
	{
		if(m_overayROI==null)
			return false;
		else
			return true;
	}

	public boolean isMultiSlice()
	{
		return m_bMultiSclie;
	}
	
	public double[] getSignalValue()
	{
		return m_dNowSignal;
	}
	
	public void setMultiSlice(boolean input)
	{
		if(m_dNowSignal ==null)
			return;
		m_bMultiSclie = input;
		repaint();
	}

	public void DcmInfoShow()
	{
		if(m_bInfoDisable)
		{
			m_bInfoDisable =false;
		}
		else
		{
			m_bInfoDisable =true;
		}
		repaint();
		
	}
	
	
	private void addMultiSliceBouttom()
	{
		
		m_cbMultiSlice =  new AC_CheckBox(m_bMultiSclie);
		m_cbMultiSlice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("!!!!!!!!!!!");
				// TODO Auto-generated method stub
				if(m_bMultiSclie)
					m_bMultiSclie = false;
				else
					m_bMultiSclie = true;

				System.out.println(m_bMultiSclie);
			}
		});
		
		m_cbMultiSlice.setParentsSize(this.getGraphics(), m_tpTopLeft,this.getWidth(), this.getHeight());
		this.add(m_cbMultiSlice);
	}
	
	
	
	public void buildTxtPenal()
	{
		
		System.out.println("bulid txt");
		m_tpTopLeft = new AC_TxtPanel("TopLeft",AC_TxtPanel.LEFT_TOP); 
		m_tpTopRight = new AC_TxtPanel("TopRight",AC_TxtPanel.RIGHT_TOP); 

		m_tpBottomLeft = new AC_TxtPanel("BottomLeft",AC_TxtPanel.LEFT_BOTTOM); 
		m_tpBottomRight = new AC_TxtPanel("BottomRigth",AC_TxtPanel.RIGHT_BOTTOM); 
		
		m_tpROIMearPanel = new AC_TxtPanel("ROI Measures",AC_TxtPanel.LEFT);
		
		m_tpTop= new AC_TxtPanel("Top",AC_TxtPanel.TOP); 
		m_tpRight = new AC_TxtPanel("Rigth",AC_TxtPanel.RIGHT); 

		m_tpLeft = new AC_TxtPanel("Left",AC_TxtPanel.LEFT); 
		m_tpBottom = new AC_TxtPanel("Bottom",AC_TxtPanel.BOTTOM); 
	



		
		m_tpTopLeft.setTxt(m_sarrTopLeft);
		m_tpTopLeft.setParentsSize (this.getWidth(), this.getHeight());
		this.add(m_tpTopLeft);
		
		m_tpTopRight.setTxt(m_sarrTopRight);
		m_tpTopRight.setParentsSize (this.getWidth(), this.getHeight());
		this.add(m_tpTopRight);

		
		m_tpBottomLeft.setTxt(m_sarrBottomLeft);
		m_tpBottomLeft.setParentsSize(this.getWidth(), this.getHeight());
		this.add(m_tpBottomLeft);

		
		m_tpBottomRight.setTxt(m_sarrBottomRight);
		m_tpBottomRight.setParentsSize( this.getWidth(), this.getHeight());
		this.add(m_tpBottomRight);
		
		
		m_tpTop.setTxt(m_sTop);
		m_tpTop.setParentsSize( this.getWidth(), this.getHeight());
		this.add(m_tpTop);
		
		m_tpLeft.setTxt(m_sLeft);
		m_tpLeft.setParentsSize( this.getWidth(), this.getHeight());
		this.add(m_tpLeft);

		
		m_tpRight.setTxt(m_sRigth);
		m_tpRight.setParentsSize( this.getWidth(), this.getHeight());
		this.add(m_tpRight);

		
		m_tpBottom.setTxt(m_sBottom);
		m_tpBottom.setParentsSize(this.getWidth(), this.getHeight());
		this.add(m_tpBottom);

		
		
		
		/*m_tpTopLeft.updateUI();
		m_tpBottomLeft.updateUI();
		m_tpTopRight.updateUI();
		m_tpBottomRight.updateUI();*/
		

	}
	
	public void setSeriesData(AC_SeriesInfo inputSeries)
	{
		m_SeriesInfo = inputSeries;
		//m_sSeriesDateTime = inputSeries.getSeriesData();
		m_Modality = inputSeries.getModality();
		m_nSliceN = inputSeries.getTotalSliceNUM();
		
		m_sarrTopRight = inputSeries.getPanelInfo().clone();
		m_sarrTopLeft[1] = "Se : " +inputSeries.getSeriresNUM();
		
		m_tpTopRight.updateTxt(m_sarrTopRight);
	
	}

	public void updateSliceTxtInfo(AC_SliceInfo inputSlice)
	{
		String[] tmp = inputSlice.getPanelInfo();
		String[] sRowDirection = inputSlice.getRowDirection();
		String[] sColunmDirection = inputSlice.getColunmDirection();
		String[] tmpString = new String[1];
		//set OrientationDirection
		m_sLeft[0] = sRowDirection[0];
		m_tpLeft.updateTxt(m_sLeft);
		m_sRigth[0] = sRowDirection[1];
		m_tpRight.updateTxt(m_sRigth);
		m_sTop[0] = sColunmDirection[0];
		m_tpTop.updateTxt(m_sTop);
		m_sBottom[0] = sColunmDirection[1];
		m_tpBottom.updateTxt(m_sBottom);
	
		
		
		
		m_sarrBottomRight[0] = tmp[0];
		m_sarrBottomRight[1] = tmp[1];
		m_sarrBottomRight[2] = tmp[2];

		m_sarrTopLeft[0] = "Im : "+(m_iNowSliceN+1)+"/"+m_SeriesInfo.getTotalSliceNUM();


	


		String sSliceThickness = String.format("%.1f", m_SeriesInfo.getSliceThickness());
		String sSliceLocation =  String.format("%.1f", inputSlice.getImagePosition()[2]);
		m_dSliceLocation =  inputSlice.getImagePosition()[2];

		m_sarrBottomLeft[2] =  "ST : "+ sSliceThickness +"mm  SL : "+ sSliceLocation+"mm";
		
		m_tpTopLeft.updateTxt(m_sarrTopLeft);
		m_tpBottomLeft.updateTxt(m_sarrBottomLeft);
		m_tpBottomRight.updateTxt(m_sarrBottomRight);
	}

	
	private void deleteROIPanel()
	{
		Component[] tmp = this.getComponents();
		
		for(Component tmpCom : tmp)
		{
			if(tmpCom.getName().contains("ROI_Panel"))
			{
				this.remove(tmpCom);
			}
		}
		
	}
	public void setSliceData(AC_SliceInfo inputSlice)
	{
		if(m_dNowSignal==null)
		{
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
		}
		
		m_nowSliceInfo = inputSlice;
	
	    m_arrdPatientOriantation = inputSlice.getImageOrient();
		m_iWidth = inputSlice.getColumns();
		m_iHeight = inputSlice.getRows();
				 
		m_dAspect =(double)m_iHeight/(double) m_iWidth;
		m_dOriWindowCenter = inputSlice.getWindowCenter();;
		m_dOriWindowWidth  = inputSlice.getWindowWidth();
		
		m_iSampleperPixel = inputSlice.getSamplesperPixel();
			
		m_dPixelSpacing = inputSlice.getPixelSpacing();
		
		deleteROIPanel();
		if(inputSlice.hasROIs())
		{
			AC_ROI[] tmpROIs = inputSlice.getROIs();
			for(AC_ROI tmpROI : tmpROIs)
			{
				
				tmpROI.setDrawPointROI(m_pShowImgSx, m_pShowImgSy, m_pShowImgEx, m_pShowImgEy);
				this.add(tmpROI);
			}
		}
			
		
		
		
		
		updateSliceTxtInfo(inputSlice);
		
		
		System.out.println("tset !!!!!!!!!!!! :"+m_sarrBottomRight[0]+"_"+m_sarrBottomRight[1]+"_"+m_sarrBottomRight[2]);
		try {
			m_dNowSignal = AC_DataConverter.SliceInfo2Signal(inputSlice);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//loger
			e.printStackTrace();
		}
		updateShowImg();
	
	}
	public void updateWindowCW(double dWindowCenter, double dWindowWidth)
	{
		m_dWindowCenter = dWindowCenter;
		m_dWindowWidth = dWindowWidth;
		updateShowImg();
	}
	private void updateShowImg()
	{
		String sWindowCenter  = Integer.toString((int)m_dWindowCenter);
		String sWindowWidth = Integer.toString((int)m_dWindowWidth);
		
		m_sarrBottomLeft[1] = "WC : "+ sWindowCenter +" WW : "+ sWindowWidth;

		showImg = AC_DataConverter.FastSignal2bffImg(m_dNowSignal , m_iWidth, 
				m_iHeight, m_dWindowCenter, m_dWindowWidth,m_iSampleperPixel);
	}
	
	private void iniParamter()
	{
		//
		this.removeAll();
		//iniParameter
		  m_pShowImgSx = 0;
		  m_pShowImgEx = 0;
		  m_pShowImgSy = 10;
		  m_pShowImgEy = 10;
		  m_pLastMousex = 0;
		  m_pLastMousey = 0;
		//Factor
		  m_iMoveFactorX = 0;
		  m_iMoveFactorY = 0;
		  m_iZoomFactor = 0;
		  
		  m_iNowSliceN = 0;
		  
		  
		  for(int idx = 0; idx<m_sarrBottomLeft.length;idx++)
			  m_sarrBottomLeft[idx] = "";
		  
		  for(int idx = 0; idx<m_sarrBottomRight.length;idx++)
			  m_sarrBottomRight[idx] = "";
		  for(int idx = 0; idx<m_sarrTopLeft.length;idx++)
			  m_sarrTopLeft[idx] = "";
		  for(int idx = 0; idx<m_sarrTopRight.length;idx++)
			  m_sarrTopRight[idx] = "";
		  m_sTop[0] = m_sRigth[0] = m_sLeft[0] = m_sBottom[0] = "";
	
	}
	
	
	public void iniImagePanel(AC_SeriesInfo inputSeries)
	{
		AC_SliceInfo tmpSlice =inputSeries.getSlice(0);
		
		iniParamter();
		setSeriesData(inputSeries);
		setSliceData(tmpSlice);
		calDrawImgDim();
		buildTxtPenal();
		//buildOrientationPanel();
		revalidate();
		repaint();
	}


	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(showImg==null)
			return;
	
		g.drawImage(showImg, m_pShowImgSx, m_pShowImgSy,m_pShowImgEx, m_pShowImgEy, this);
		if(m_bDrawROI)
			drawROI(g);

	}
	
	


	
		
	private void drawROI(Graphics g)
	{
	//	this.
		//Graphics g = this.getGraphics();
	//	super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Environment.g_ClrROINomarl
		);
		  g2.setStroke(new BasicStroke(Environment.g_ROIStokeSize,BasicStroke.CAP_ROUND,0));
		switch (MOUSE_MODE) {
		case ViewerMain.MM_ROI_DISTANCE:
			
			
		
			g.drawLine(m_pDragSx, m_pDragSy, m_pLastMousex, m_pLastMousey);
			break;
			
		case ViewerMain.MM_ROI_OVAL:
			int iSx = 0;
			int iSy = 0;
			int iEx = 0;
			int iEy = 0;
			
			if(m_pDragSx<=m_pLastMousex)
			{
				iSx = m_pDragSx;
				iEx = m_pLastMousex;
			}else
			{
				iSx =m_pLastMousex;
				iEx =  m_pDragSx;
			}
			
			if(m_pDragSy<=m_pLastMousey)
			{
				iSy = m_pDragSy;
				iEy = m_pLastMousey;
			}else
			{
				iSy =m_pLastMousey;
				iEy =  m_pDragSy;
			}
			
			
			
			g.drawOval(iSx, iSy, iEx-iSx, iEy-iSy);
			break;


		default: 
			break;
		}
	

		
		
		
		
	
			

	}

	private void calDrawImgDim()
	{
		
		
		int iHeight = (int)this.getHeight();
		int iWidth = (int)this.getWidth();
		

		
		int iLongLenght = 0;
		int iShortLenght = 0;
		
		int offset = 10;
		int doffset = 10*2;
		
		double dPanelAspect = (double)iHeight/(double)iWidth;
		
		
	

		if(m_dAspect<dPanelAspect)
		{
		

			iLongLenght = iHeight ;
			iShortLenght = iWidth-doffset;
	
			m_pShowImgEy = (int) (iShortLenght*m_dAspect);	
	
			m_pShowImgEx = iShortLenght;
		
			int iMargin = iLongLenght-m_pShowImgEy;
			
			m_pShowImgSy = iMargin/2;
			m_pShowImgSx = offset;
			
			
		}
		else
		{
		
			iLongLenght = iWidth;
			iShortLenght = iHeight-doffset;;
	
			m_pShowImgEx = (int) (iShortLenght/m_dAspect);	
	
			m_pShowImgEy = iShortLenght;
		
			int iMargin = iLongLenght-m_pShowImgEx;
			
			m_pShowImgSx = iMargin/2;
			m_pShowImgSy = offset;
		}
		
		
	
	
		m_pShowImgSx += m_iMoveFactorX;
		m_pShowImgSy += m_iMoveFactorY;
	
		//ZoomFatcor
		m_pShowImgEx += m_iZoomFactor*2/m_dAspect;
		m_pShowImgEy += m_iZoomFactor*2;
		m_pShowImgSx -= m_iZoomFactor/m_dAspect;
		m_pShowImgSy -= m_iZoomFactor;
		
		
		
		int iViewPort = m_pShowImgEx+m_pShowImgEy;
		int iImgSize = m_iWidth+m_iHeight;
		
		
		float ftmp = (float) 0.0;
		if(m_iWidth<m_iHeight)
		{
			 ftmp = ((float)m_iHeight/(float)m_pShowImgEy);
		}
		else
			ftmp = (int)((float)m_iWidth/(float)m_pShowImgEx);
		
		if(ftmp<1.5)
			m_iSampling = 1;
		else
			m_iSampling = (int)ftmp;
		
		if(m_nowSliceInfo!=null)
		{
		
		
		AC_ROI[] tmp = m_nowSliceInfo.getROIs();
		if(tmp!=null)
		{
			for(AC_ROI tmpROI : tmp)
			{
				tmpROI.setDrawPointROI(m_pShowImgSx, m_pShowImgSy, m_pShowImgEx, m_pShowImgEy);
			}
		}
		}
		

		            
	}
	

	

	@SuppressWarnings("deprecation")

	private void nextSlice() 
	{
		System.out.println("Next SliceNo : "+m_iNowSliceN);
		m_iNowSliceN++;
		m_SelectionROI = null;

		if(m_iNowSliceN>m_nSliceN-1)
			m_iNowSliceN =0;
		setSliceData(m_SeriesInfo.getSlice(m_iNowSliceN));

	
	}
	private void backSlice() 
	{
		System.out.println("Back SliceNo : "+m_iNowSliceN);
		m_iNowSliceN--;
		m_SelectionROI = null;
		
		if(m_iNowSliceN<0)
			m_iNowSliceN =m_nSliceN-1;
		setSliceData(m_SeriesInfo.getSlice(m_iNowSliceN));
		
	
		
	}
	public void movingImage(int imoveX, int imoveY)
	{
		m_iMoveFactorX -= imoveX;
		m_iMoveFactorY -= imoveY;	
		calDrawImgDim();
		repaint();
	}
	
	public void zoomingImage(int imoveX, int imoveY)
	{
	
		int chkOver = m_pShowImgSy - m_iZoomFactor;
		if(chkOver>(int)this.getWidth()/2)
		{
			if(imoveX+imoveY>0)
				m_iZoomFactor+= (int)(imoveX+imoveY)/2;
			return;
		}
		m_iZoomFactor += (int)(imoveX+imoveY)/2;
		calDrawImgDim();
		
		 repaint();
	}
	
	public void windowingImage(int imoveX, int imoveY)
	{
		double tuboFactor = 1;
		
		m_dWindowCenter +=imoveY*tuboFactor;
		m_dWindowWidth -= imoveX*tuboFactor; 
		updateShowImg();
		 repaint();
	}

	
	private int[] PanelAxis2ImgAxis(int pSx,int pSy,int pEx, int pEy)
	{
		//[0] : sPx
		//[1] : aPy
		//[2] : epx
		//[3] : epy
	//	System.out.println("Dsx : "+ pSx +"Dsy "+pSy+ "Dex : "+ pEx +"Dey"+pEy);
		//System.out.println("m_pShowImgSx : "+ m_pShowImgSx +"m_pShowImgSy :  "+m_pShowImgSy+
			//	"m_pShowImgEx : "+ m_pShowImgEx +"m_pShowImgEx "+m_pShowImgEx);
		pSx -= m_pShowImgSx;
		pSy -= m_pShowImgSy;
		pEx -= m_pShowImgSx;
		pEy -= m_pShowImgSy;
	
		
		
	//	System.out.println("psx : "+ pSx +"psy "+pSy+ "pEx : "+ pEx +"pEy "+pEy);

		
		
		pSx =    (pSx)*m_iWidth  ;
		pSy  =  (pSy)*m_iHeight ;
	                          
		pEx  =  (pEx)*m_iWidth  ;
		pEy  =  (pEy)*m_iHeight ;
	//	System.out.println("psx : "+ pSx +"psy "+pSy+ "pEx : "+ pEx +"pEy "+pEy);
		
		
		pSx /=   (m_pShowImgEx) ;
		pSy /= (m_pShowImgEy); 
	
		pEx  /= (m_pShowImgEx);
		pEy  /= (m_pShowImgEy); 
	//	System.out.println("psx : "+ pSx +"psy "+pSy+ "pEx : "+ pEx +"pEy "+pEy);    
		
		
		int[] output = {pSx,pSy,pEx,pEy};
		
		



		
		
		
		return output;
	}
	
	private double[] PanelAxis2DoubleAxis(int pSx,int pSy,int pEx, int pEy)
	{
		//[0] : sPx
		//[1] : aPy
		//[2] : epx
		//[3] : epy
		
		double iTmpSx =      	pSx - m_pShowImgSx;
		double iTmpSy =      	pSy - m_pShowImgSy;
		double iTmpEx =      	pEx - m_pShowImgSx;
		double iTmpEy =      	pEy - m_pShowImgSy;
		/*System.out.println("Dsx : "+ pSx +"Dsy "+pSy+ "Dex : "+ pEx +"Dey"+pEy);
		System.out.println("m_pShowImgSx : "+ m_pShowImgSx +"m_pShowImgSy :  "+m_pShowImgSy+
				"m_pShowImgEx : "+ m_pShowImgEx +"m_pShowImgEx "+m_pShowImgEx);
	
	
		
		
		System.out.println("psx : "+ pSx +"psy "+pSy+ "pEx : "+ pEx +"pEy "+pEy);*/

		
		
		iTmpSx =    (iTmpSx)*m_iWidth  ;
		iTmpSy  =  (iTmpSy)*m_iHeight ;
	                          
		iTmpEx  =  (iTmpEx)*m_iWidth  ;
		iTmpEy  =  (iTmpEy)*m_iHeight ;
	//	System.out.println("psx : "+ pSx +"psy "+pSy+ "pEx : "+ pEx +"pEy "+pEy);
		
		
		iTmpSx /=   (m_pShowImgEx) ;
		iTmpSy /= (m_pShowImgEy); 
	
		iTmpEx  /= (m_pShowImgEx);
		iTmpEy  /= (m_pShowImgEy); 
		//System.out.println("psx : "+ pSx +"psy "+pSy+ "pEx : "+ pEx +"pEy "+pEy);    
		
		
		double[] output = {iTmpSx,iTmpSy,iTmpEx,iTmpEy};
		                 
		



		
		
		
		return output;
	}
	

	public void setMouseMOOD(int input)
	{
		MOUSE_MODE = input;
		
	}
	
	private void setMouseCursor(boolean bFlag)
	{
		if(bFlag)
		{
			if(MOUSE_MODE == ViewerMain.MM_IMAGE_MOVE)
				this.setCursor(CURSOR_MOVE);
			else if(MOUSE_MODE == ViewerMain.MM_IMAGE_WINDOW)
				this.setCursor(CURSOR_WINDOW);
			else if(MOUSE_MODE == ViewerMain.MM_IMAGE_ZOOM)
				this.setCursor(CURSOR_Zooming);
		}
		else
			this.setCursor(CURSOR_DEFAULT);
	}
	
	/*public void setROIMOOD(int input)
	{
		MOUSE_MODE = m_;
		ROI_MODE = input;
	}*/
	
	private void buildROIMeasurement()
	{
		
		m_ROIMeasur = new JPanel();
		
		m_ROIMeasur.setOpaque(false);

		m_ROIMeasur.setBackground(new Color(255,0,0,125));
		
	}
	
	
	
	
	public boolean isFocus()
	{
		return m_bFoucs;
	}
	
	public void setFocus(boolean input)
	{
		m_bFoucs = input;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
		m_iStartPotin [0] = e.getX();
		m_iStartPotin [0] = e.getY();
	
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		System.out.println(e.isMetaDown());
		if(!e.isMetaDown())
		{
			m_pDragSx = m_pLastMousex =  e.getX(); 
			m_pDragSy  = m_pLastMousey = e.getY();  

			setMouseCursor(true);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

		System.out.println("ImagePenal : mouseReleased : "+e.getComponent().getName() );
		
		if(	m_bDrawROI)
		{
			AC_ROI tmp = new AC_ROI();
			
			double[] arrPoint = PanelAxis2DoubleAxis(m_pDragSx, m_pDragSy, m_pLastMousex, m_pLastMousey);
			
			tmp.setImageSize(m_iHeight, m_iWidth);
		    tmp.setROI(arrPoint[0],arrPoint[1],arrPoint[2],arrPoint[3],MOUSE_MODE);
		    tmp.setDrawPointROI(m_pShowImgSx, m_pShowImgSy, m_pShowImgEx, m_pShowImgEy);
		    this.add(tmp);
		    tmp.setMeasureValue();
		    ///EVET
		    System.out.println("ROI ADD Event");
		    System.out.println(this.getMouseWheelListeners().toString());
		    
	
		   
		    m_nowSliceInfo.addROI(tmp);
		    
		    
			  for(MouseWheelListener tmpMWE : this.getMouseWheelListeners())
			  {
				
				System.out.println("ajsdlkfjsdf : " +tmpMWE.toString());
				  tmp.addMouseWheelListener(tmpMWE);
			  }
		  
		    System.out.println("!!!!!!!!!!!!!!!"+tmp.getName());
		    m_bDrawROI=false;
			repaint();
		
		}
	/*	if(MOUSE_MODE==MM_DRAW_ROI && m_SelectionROI == null)
		{
			if( Math.abs(m_pDragSx- e.getX())>5
					&& Math.abs(m_pDragSy- e.getY())>5)
			{
				
				System.out.println("x : "+ (m_pDragSx- e.getX())+"y : "+(m_pDragSy- e.getY()));
				if(!m_drawROI.isAngleDraw())
				    addROI(m_pDragSx, m_pDragSy , e.getX(),e.getY());
			}
		}*/
	//	56
		setMouseCursor(false);
	

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("ImagePenal : mousEnterd");
	//	this.requestFocusInWindow();
		m_bFoucs =true;
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		//this.transferFocus();

		m_bFoucs =false;;
	}

	public void moveMutilSlice(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		
		System.out.println("!!!");
		
		if (e.getWheelRotation() > 0) 
		{
			backSlice();
			
		} else {
			
			
			nextSlice();
			
		}
		

		repaint();
		

		
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
		if(!e.isMetaDown())
		{
			/*if(MOUSE_MODE==MM_DRAW_ROI &&m_SelectionROI != null)
			{
				int [] iarrP = PanelAxis2ImgAxis(m_pLastMousex, m_pLastMousey, e.getX(),e.getY());

				m_SelectionROI.moveSP(iarrP[0]-iarrP[2], iarrP[1]-iarrP[3]);
				repaint();
			}
			
			if(MOUSE_MODE==MM_DRAW_ROI &&  m_SelectionROI == null) {
				if( Math.abs(m_pDragSx- e.getX())>m_iDragLimit 
						&& Math.abs(m_pDragSy- e.getY())>m_iDragLimit)
					setDrawROI(m_pDragSx, m_pDragSy , e.getX(),e.getY());
			}*/
			
			int iMouseMoveX = m_pLastMousex-e.getX();
			int iMouseMoveY = m_pLastMousey-e.getY();	
			
			
			
			if( MOUSE_MODE== ViewerMain.MM_IMAGE_MOVE )
				movingImage(iMouseMoveX , iMouseMoveY);
			else if( MOUSE_MODE== ViewerMain.MM_IMAGE_ZOOM )
				zoomingImage(iMouseMoveX , iMouseMoveY);
			else if( MOUSE_MODE== ViewerMain.MM_IMAGE_WINDOW )
				windowingImage(iMouseMoveX , iMouseMoveY);
			else if( MOUSE_MODE== ViewerMain.MM_ROI_DISTANCE ||
					MOUSE_MODE== ViewerMain.MM_ROI_OVAL)
			{
				m_bDrawROI = true;
				repaint();
				/*System.out.println("Draw Line");
				drawROI();*/
				
		
			}
			
			
			
			
			m_pLastMousex = e.getX();
			m_pLastMousey = e.getY();
		}
			
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		{
			m_iNowMosueX = e.getX();
			m_iNowMosueY = e.getY();
			
			
			/*m_iNowMosueX -= m_pShowImgSx;
			m_iNowMosueY -= m_pShowImgSy;
			m_iNowMosueX=   (int) (((double)(m_iNowMosueX)/(m_pShowImgEx))*m_iWidth) ;
			m_iNowMosueY  = (int) (((double)(m_iNowMosueY)/(m_pShowImgEy))*m_iHeight); */

			updataePixelVPanel(e.getX(),e.getY());

			/*else
			{

				m_sarrBottomLeft[0] = "";


				m_tpBottomLeft.setTxt(m_sarrBottomLeft);

				m_bDrawMousePoint = false;
			}*/
		}
			
	}
	
	public void updataePixelVPanel(int iMouseX, int iMoustY)
	{
		double[] arrPoint = PanelAxis2DoubleAxis(0, 0, iMouseX, iMoustY);
		
		 int inX = (int)arrPoint[2];
		 int inY =  (int)arrPoint[3];

		if(inX>=0 && inX<m_iWidth&&
				inY>=0 && inY<m_iHeight)
		{		
			int idx = m_iWidth*inY*m_iSampleperPixel+inX*m_iSampleperPixel;
			double dValue =m_dNowSignal[idx];
			String sValue  = String.format("%.1f",dValue);
			m_sarrBottomLeft[0] = "X : "+ (inX) 
					+" Y : "+ inY +" Value : "+ sValue;
			if(m_iSampleperPixel==3)
			{
				String dValueR =  String.format("%d",(int)m_dNowSignal[idx+0]);
				String dValueG =  String.format("%d",(int)m_dNowSignal[idx+1]);
				String dValueB =  String.format("%d",(int)m_dNowSignal[idx+2]);
				
				m_sarrBottomLeft[0] = "X : "+ (inX) 
						+" Y : "+ inY +" Value : "+ dValueR+","+ dValueG+","+ dValueB;
			}
			m_tpBottomLeft.updateTxt(m_sarrBottomLeft);
			//m_tpBottomLeft.updateUI();
			m_bDrawMousePoint = true;
		}
	}
	
	
	


	
	public void inpufile(File[] file)
	{
		m_filelist = file;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
		m_tpTopLeft.setParentsSize( this.getWidth(), this.getHeight());
		m_tpTopRight.setParentsSize( this.getWidth(), this.getHeight());
		m_tpBottomLeft.setParentsSize(this.getWidth(), this.getHeight());
		m_tpBottomRight.setParentsSize(this.getWidth(), this.getHeight());
		m_tpTop.setParentsSize(this.getWidth(), this.getHeight());
		m_tpLeft.setParentsSize(this.getWidth(), this.getHeight());
		m_tpBottom.setParentsSize(this.getWidth(), this.getHeight());
		m_tpRight.setParentsSize(this.getWidth(), this.getHeight());
		calDrawImgDim();
	
		
		
		
	
	
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
	
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void setSelectionROI(AC_ROI input) {
		// TODO Auto-generated method stub
		m_SelectionROI = input;
		
	}
	public String getSliceFilePath() {
		// TODO Auto-generated method stub
		//String output = "C:\\Users\\JW\\Desktop\\DCM\\1_R5033\\test_2-1_ANOMI.dcm";
		String output = m_SliceInfo.getFile().getAbsolutePath();
		return output;
	}
}
