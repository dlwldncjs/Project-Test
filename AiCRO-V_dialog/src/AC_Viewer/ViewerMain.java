package AC_Viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import AC_Dialog.AC_DicomHeaderDialog;
import AC_Dialog.AC_EnvironmentDialog;
import AC_DicomData.DicomIO.AC_DcmStructure;
import AC_DicomData.DicomIO.AC_DicomReader;
import AC_DicomData.ViewerStructure.AC_ViewrDcmData;
import AC_ROIComp.AC_ROI;
import AC_funcComp.AC_PenalSizeButton;
import SYB_LIB.SYBFileDrop;
import javafx.scene.web.WebEngine;









public  class ViewerMain  extends JFrame implements	 FocusListener, MouseListener, 
 KeyListener, MouseWheelListener, MouseMotionListener{
	
	/**javafx.scene.web.WebEngine
	 * 
	 */
	
	public static final int MM_IMAGE_MOVE =0;
	public static final int MM_IMAGE_ZOOM =1;
	public static final int MM_IMAGE_WINDOW =2;
	public static final int MM_ROI_DISTANCE = 3;
	public static final int MM_ROI_OVAL = 4;
	public static final int MM_ROI_ANGLE = 5;
	private int MOUSE_MODE = Environment.g_INI_MOUSE_MODE;;

	
	private double m_dRefSliceLocation = 0.0;
	
	
	private static final long serialVersionUID = 1L;


	
	private final static String  m_sVersion = "1.0";
	
	
	private JToolBar m_Toolbar;
	
	private AC_ViewrDcmData m_DCMdata = new AC_ViewrDcmData();

	
	//web
	String m_sUserIdx = "";
	
	
	WebEngine m_WebEngine = null;
	
	//imagePanel

	//Toolbar 
	JButton btn_ScreenSelect;
	JPopupMenu m_ScreenSelectPopUp ;
	JPopupMenu m_SeriesTumbPopUp; 
	JPanel m_ToolBarPanel;
	JPanel m_ScreenPanel;
	AC_ImagePanel[] m_ImagePanel;
	AC_SeriesBar m_SeriesBar;
	//
	private ButtonGroup m_btnMouseModeGroup= new ButtonGroup();
	private JToggleButton btn_MovingImg;
	private JToggleButton btn_ZoomingImg;
	private JToggleButton btn_windowingImg;

	//private ButtonGroup m_btnROIGroup= new ButtonGroup();

	private JToggleButton btn_ROIOval;
	private JToggleButton btn_ROIDtistance;

	
	
	
	List<File[]> m_DcmList;
	
	
	 
	
/*	private int m_iIniPanelNum= 2;
	
	private int m_nImgPanelRow = 1;
	private int m_nImgPanelColumn = 1;
	private int m_nImgPanelTotal = 1;*/
	

	
	

	
	
	///ui관련 
//	private boolean bSeriesDrag =false;
	private int iSelectionScreendx =0;
	
	int m_pLastMousex=0;
	int m_pLastMousey=0;



	public static void main(String[] args) {
		new ViewerMain(true, "");
		
		System.out.println("done..........");
		
	}
	
	private void runFileDrop()
	{
		new  SYBFileDrop( this, new SYBFileDrop.Listener()
		{   
			public void  filesDropped(File[] file )
			{  

				new Thread() {
					public void run() 
					{
						inputDCMFiles(file);
						
					}
				}.start();
			}
			

		});
	}
	
	private void inputDCMFiles(File[] input) {
		for(File tmp : input) {
			if(tmp.isFile()) {
				try {
					AC_DicomReader dcmRedader = new AC_DicomReader(tmp);
					AC_DcmStructure tmpDCM= dcmRedader.getAttirbutes();
					System.out.println(tmp.getName());

					m_DCMdata.addPatient(tmpDCM);
					m_SeriesBar.drawPanel();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {
				inputDCMFiles(tmp.listFiles());
			}
		}	
	}
	
		
	public ViewerMain(boolean exitOnCLose, String fileNames) {
		super("AICRO Viewr ver." + m_sVersion);
		//쓰레드종료
		AC_EnvironmentDialog.loadEnvxml();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addMouseListener(this);
		this.addFocusListener(this);
		this.setFocusable(true);
		this.addKeyListener(this);
		this.getContentPane().setBackground(Color.DARK_GRAY);
		this.buildPanel();
		this.setExtendedState( this.getExtendedState()|JFrame.MAXIMIZED_BOTH );
		this.toFront();
		
		runFileDrop();
		
		File[] tmpFile = new File[1];
		tmpFile[0] =new File("D:\\98_data\\99_TestData\\20140703_DCM");
		inputDCMFiles(tmpFile);
	}


	
	public ImageIcon reSizeImageICON(String imgURL, int size) {
		
		ImageIcon ic = new ImageIcon(imgURL);
		Image changedImg= ic.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH );
	
		return new ImageIcon(changedImg);
	}
	

	
	private void buildPanel() {
       //main setup 		
		this.setIconImage(reSizeImageICON(DefultURL.ICON_MAIN,40).getImage());
		this.getContentPane().setLayout(new BorderLayout());
		this.setBackground(Color.GRAY);
		
		//ToolbarSetting
		m_ToolBarPanel = new JPanel(new BorderLayout());
		m_Toolbar = new JToolBar();
		//m_toolbar.setSize(1,50);
		m_Toolbar.setBackground(Color.GRAY);
	
		btn_ScreenSelect = new AC_PenalSizeButton(this);
			
		m_Toolbar.add(btn_ScreenSelect);	
		m_Toolbar.setBorderPainted(false);
		
		m_SeriesBar = new AC_SeriesBar();
		m_SeriesBar.setDCMdata(m_DCMdata);
		
		m_SeriesBar.addMouseListener(this);
		m_SeriesBar.addMouseMotionListener(this);
	
		m_ScreenPanel = new JPanel(new GridLayout(1, 1));
		createImagePanels(1,1,1);
		createEditImgBTN();
		createROIbtn();
		//GO ASANJ
		createETCbtn();

		m_ToolBarPanel.add(m_Toolbar, BorderLayout.CENTER);

		this.getContentPane().add(m_ScreenPanel,BorderLayout.CENTER);
		this.getContentPane().add(m_ToolBarPanel,BorderLayout.NORTH);
		this.getContentPane().add(m_SeriesBar,BorderLayout.WEST);

		this.pack();
		this.setVisible(true);
	}
	
	private void createImagePanels(int anz, int iRow, int iColum) {
		m_ScreenPanel.setLayout(new GridLayout(iRow,iColum));
		m_ImagePanel = new AC_ImagePanel[anz];
		for (int i = 0; i < anz; i++) 
		{
			m_ImagePanel[i] = new AC_ImagePanel();
			m_ImagePanel[i].addMouseListener(this);
			m_ImagePanel[i].setName("ImgPanel_"+i);
			//m_ImagePanel[i].addMouseMotionListener(this);
			m_ImagePanel[i].addMouseWheelListener(this);
			//m_ImagePanel[i].setOverlayROI(m_overlay);
			m_ScreenPanel.add(m_ImagePanel[i]);
		}
	}
	
	private void createEditImgBTN() {
		Color clr_btnBack = Color.gray;
		btn_MovingImg = new JToggleButton("");
		btn_MovingImg.setBackground(clr_btnBack);
		btn_MovingImg.setBorderPainted(false);
		
		URL icon = getClass().getClassLoader().getResource(DefultURL.ICON_Move);
		btn_MovingImg.setIcon(AC_ImageIconHeadler.reSizeImageICON(icon));
 		
		//btn_MovingImg.setIcon(AC_ImageIconHeadler.reSizeImageICON(DefultURL.ICON_Move));
		btn_MovingImg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MOUSE_MODE = MM_IMAGE_MOVE;
				setStateImagePanels(0);
			}
		});
		btn_ZoomingImg = new JToggleButton("");
		btn_ZoomingImg.setBackground(clr_btnBack);
		btn_ZoomingImg.setBorderPainted(false);
		icon = getClass().getClassLoader().getResource(DefultURL.ICON_Zooming);
		btn_ZoomingImg.setIcon(AC_ImageIconHeadler.reSizeImageICON(icon));
		//btn_ZoomingImg.setIcon(AC_ImageIconHeadler.reSizeImageICON(DefultURL.ICON_Zooming));
		btn_ZoomingImg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MOUSE_MODE = MM_IMAGE_ZOOM;
				setStateImagePanels(1);
			}
		});
		btn_windowingImg = new JToggleButton("");
		btn_windowingImg.setBackground(clr_btnBack);
		btn_windowingImg.setBorderPainted(false);
		icon = getClass().getClassLoader().getResource(DefultURL.ICON_Window);
		btn_windowingImg.setIcon(AC_ImageIconHeadler.reSizeImageICON(icon));
	//	btn_windowingImg.setIcon(AC_ImageIconHeadler.reSizeImageICON(DefultURL.ICON_Window));
		btn_windowingImg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MOUSE_MODE = MM_IMAGE_WINDOW;
				setStateImagePanels(2);
			}
		});
		
		
		
		
		
		m_Toolbar.add(btn_MovingImg);
		m_Toolbar.add(btn_ZoomingImg);
		m_Toolbar.add(btn_windowingImg);
	                 
		m_btnMouseModeGroup.add(btn_MovingImg);   
		m_btnMouseModeGroup.add(btn_ZoomingImg);  
		m_btnMouseModeGroup.add(btn_windowingImg);
		
		/*btn_DicomInfo = new JToggleButton("btn_DicomInfo");
		btn_DicomInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setShowDicomInfo();
			}
		});
		
		m_Toolbar.add(btn_DicomInfo);*/
	}

	private void createROIbtn()
	{

		Color clr_btnBack = Color.gray;
		
	/*	btn_ROIRectangle = new JToggleButton("Ractangle");
		btn_ROIRectangle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setROIStateImagePanels(AC_ROI.RECTANGLE);
			}
		});*/
		
		
		btn_ROIOval = new JToggleButton("");
		
		btn_ROIOval.setBackground(clr_btnBack);
		btn_ROIOval.setBorderPainted(false);
		URL icon = getClass().getClassLoader().getResource(DefultURL.ICON_OvalROI);
		btn_ROIOval.setIcon(AC_ImageIconHeadler.reSizeImageICON(icon));
		
		btn_ROIOval.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MOUSE_MODE = MM_ROI_OVAL;
				setStateImagePanels(MOUSE_MODE);
			}
		});
		
		
		
		btn_ROIDtistance = new JToggleButton("");
		btn_ROIDtistance.setBackground(clr_btnBack);
		btn_ROIDtistance.setBorderPainted(false);
		icon = getClass().getClassLoader().getResource(DefultURL.ICON_Distance);
		btn_ROIDtistance.setIcon(AC_ImageIconHeadler.reSizeImageICON(icon));
		//btn_ROIDtistance.setIcon(AC_ImageIconHeadler.reSizeImageICON(DefultURL.ICON_Distance));
		btn_ROIDtistance.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MOUSE_MODE = MM_ROI_DISTANCE;
				setStateImagePanels(MOUSE_MODE);
			}
		});
		
		JToggleButton btn_ROIAngle = new JToggleButton("");
		btn_ROIAngle.setBackground(clr_btnBack);
		btn_ROIAngle.setBorderPainted(false);
		icon = getClass().getClassLoader().getResource(DefultURL.ICON_AngleROI);
		btn_ROIAngle.setIcon(AC_ImageIconHeadler.reSizeImageICON(icon));
		//btn_ROIDtistance.setIcon(AC_ImageIconHeadler.reSizeImageICON(DefultURL.ICON_Distance));
		btn_ROIAngle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MOUSE_MODE = MM_ROI_ANGLE;
				setStateImagePanels(MOUSE_MODE);
			}
		}); 


		//m_Toolbar.add(btn_ROIRectangle);
		if(Environment.g_flagOvalROI)
		{
			m_Toolbar.add(btn_ROIOval);
			m_btnMouseModeGroup.add(btn_ROIOval);  
		}
		
		if(Environment.g_flagLienROI)
		{
			m_Toolbar.add(btn_ROIDtistance);
			m_btnMouseModeGroup.add(btn_ROIDtistance);  
		}
		if(Environment.g_flagAngleROI)
		{
			m_Toolbar.add(btn_ROIAngle);
			m_btnMouseModeGroup.add(btn_ROIAngle);  
		}
	
	
	}
	
	private void createETCbtn()
	{

		/*Color clr_btnBack = Color.gray;
		
		
		btn_flipped = new JButton("");
		
		btn_flipped.setBackground(clr_btnBack);
		btn_flipped.setBorderPainted(false);
		URL icon = getClass().getClassLoader().getResource(DefultURL.ICON_OvalROI);
		btn_flipped.setIcon(AC_ImageIconHeadler.reSizeImageICON(icon));
		
		btn_flipped.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				for(AC_ImagePanel tmp : m_ImagePanel)
				{
					if(tmp.isFocus())
					{
						tmp.setFilpped();
					}
				}
			}
		});
		
		m_Toolbar.add(btn_flipped);
		
	/*	btn_ROIRectangle = new JToggleButton("Ractangle");
		btn_ROIRectangle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setROIStateImagePanels(AC_ROI.RECTANGLE);
			}
		});*/
	/*	btn_GoAasnJ = new JButton("");
		
		btn_GoAasnJ.setBackground(clr_btnBack);
		btn_GoAasnJ.setBorderPainted(false);
		URL icon = getClass().getClassLoader().getResource(DefultURL.ICON_GOASAN);
		btn_GoAasnJ.setIcon(AC_ImageIconHeadler.reSizeImageICON(icon));
		
		btn_GoAasnJ.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				for(AC_ImagePanel tmp : m_ImagePanel)
				{
					if(tmp.isMultiSlice())
					{
						
						String[] splitPaht =  tmp.getRootPath().split("\\\\");
						
						AC_SimpleSeries seriesTmp = tmp.getSeries();
						String renamePaht = "";
						String splitChar = "_____";
						
						for(int i=0; i<splitPaht.length-1;i++)
						{
							renamePaht += splitPaht[i]+"\\\\\\\\";
						}
						renamePaht+= splitPaht[splitPaht.length-1];
					
						String sAicroURL = Environment.g_AiCROUploadUrl;
						String skey = seriesTmp.getKey();
						String  sUserID= m_sUserIdx;	
						
					
							String sCallPath = "C:\\Users\\shin_yongbin\\Desktop\\AsanJ_Sarcopenia\\jre\\bin\\java "+
							"-cp \"C:\\Users\\shin_yongbin\\Desktop\\AsanJ_Sarcopenia\\ij.jar;C:\\Users\\shin_yongbin\\Desktop\\AsanJ_Sarcopenia\\lib\\*\" ij.ImageJ  -ijpath C:\\Users\\shin_yongbin\\Desktop\\AsanJ_Sarcopenia "
						+ "-eval \"call(\\\"ij.plugin.FolderOpener.open\\\", \\\""+splitChar+renamePaht +splitChar+sAicroURL+splitChar+skey
						+ splitChar+seriesTmp.getUID()+splitChar+sUserID+"\\\")\"";
							
						
						
						System.out.println(sCallPath);
						
			
						
						
						 try {
							Runtime.getRuntime().exec(sCallPath); 
							
							
							
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						 
			
						
						
						System.out.println("GO ASanJ" + tmp.getRootPath().replaceAll("\\\\", "\\\\\\\\"));
					}
				}
				
			}
		});
		
		m_Toolbar.add(btn_GoAasnJ);*/
		
	}

	private void setStateImagePanels(int input)
	{
		MOUSE_MODE = input;
		
		for(AC_ImagePanel tmp: m_ImagePanel)
			tmp.setMouseMOOD(input);
	}
	
	
	private void setROIStateImagePanels(int ROIMODE)
	{
		
	/*	for(AC_ImagePanel tmp: m_ImagePanel)
			tmp.setROIMOOD(ROIMODE);*/
	}
	
	private void setShowDicomInfo()
	{
		
		for(AC_ImagePanel tmp: m_ImagePanel)
			tmp.DcmInfoShow();
		
	}
	public void updateImagePanels(int anz, int iRow, int iColum)
	{
		
		/*m_nImgPanelRow = iRow;
		m_nImgPanelColumn = iColum;
		m_nImgPanelTotal = anz;*/
		
		m_ScreenPanel.removeAll();
		
		createImagePanels(anz, iRow, iColum);

		m_ScreenPanel.revalidate();
		m_ScreenPanel.repaint();

	
	}
		
		
		
		
	
	
		
	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		System.out.println(" main Gained");
	
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		System.out.println(" main focuslost");
		requestFocusInWindow(true);
		
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		/*int iComID = Integer.parseInt(e.getComponent().getName().split("_")[1]);*/
	
		System.out.println("btn_ScreenSelect : Main mousechickID" + 0 + iSelectionScreendx);
	
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	//	System.out.println("Main mmousChick" + e.getSource().toString());
		m_pLastMousex = e.getX();
		m_pLastMousey = e.getY();
	
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
		System.out.println("mouseReleased : Main mousechickID" + 0 + iSelectionScreendx);
		System.out.println("com : "+e.getComponent().getName());

		if(e.getComponent().getName().contains("SeriresThumb"))
		{   
			
					String sSeiresUID = e.getComponent().getName().split("_")[1];
					//iSelectionScreendx = iComID;
					
					m_ImagePanel[iSelectionScreendx].iniImagePanel(m_DCMdata.getSeries(sSeiresUID));
			
			
			//m_ImagePanel[iSelectionScreendx].setRefSliceLocation(m_dRefSliceLocation);
			/*m_ImagePanel[iSelectionScreendx].setRefSliceLocation(m_dRefSliceLocation);
			m_ImagePanel[iSelectionScreendx].setSeries(m_SeriesList.get(iComID));*/
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		System.out.println("moustin"+ e.getComponent().getName());
		if(e.getComponent().getName().contains("ImgPanel"))
		{
			int iComID = Integer.parseInt(e.getComponent().getName().split("_")[1]);
			iSelectionScreendx = iComID;
	
		}
	}






	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}






	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
		System.out.println(e.getSource().toString());
		
		
		if(e.getComponent().getName().contains("SeriresThumb"))
		{   
			
			
			
			System.out.println("main mouse drag SeriresThumb!! : " + MOUSE_MODE);
		}else {

		
		
		
		System.out.println("main mouse drag!! : " + MOUSE_MODE);
		
		
		
		int iMouseMoveX = m_pLastMousex-e.getX();
		int iMouseMoveY = m_pLastMousey-e.getY();	
		

		System.out.println("iMouseMoveX "+iMouseMoveX);
		System.out.println("iMouseMoveY "+iMouseMoveY);
		
		
		for(AC_ImagePanel tmp : m_ImagePanel)
		{
			if(tmp.isMultiSlice() || tmp.isFocus())
			{
				if( MOUSE_MODE== MM_IMAGE_MOVE )
					tmp.movingImage(iMouseMoveX , iMouseMoveY);
				else if( MOUSE_MODE== MM_IMAGE_ZOOM )
					tmp.zoomingImage(iMouseMoveX , iMouseMoveY);
				else if( MOUSE_MODE== MM_IMAGE_WINDOW )
					tmp.windowingImage(iMouseMoveX , iMouseMoveY);
			}
		}
		m_pLastMousex = e.getX();
		m_pLastMousey = e.getY();
		}
		
		
		
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	//	System.out.println("main mouse mpove!! : " + MOUSE_MODE);
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Main vewer key keyPressed!!");
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Main vewer key keyReleased!!");
		
		
		
		if(e.getKeyCode()==KeyEvent.VK_DELETE)
		{
			for(AC_ImagePanel tmp : m_ImagePanel)
			{
				if(tmp.isFocus())
				{
					//tmp.removeROIinSlice();
					break;
				}
				
			}

			
		}
		else if(e.getKeyCode()==KeyEvent.VK_ESCAPE)
		{
			System.out.println("insert esc");
			
			for(AC_ImagePanel tmp : m_ImagePanel)
			{
				//if(tmp.isFocus() || tmp.isMultiSlice())
				{
					tmp.setMultiSlice(false);
				}
			}
		}
		
		
		else if(e.getKeyCode()==KeyEvent.VK_Q)
		{
			//System.out.println("insert 4");
			for(AC_ImagePanel tmp : m_ImagePanel)
			{
				if(tmp.isFocus()&&tmp.isMultiSlice())
				{
					AC_ROI overtay = null;

					for(AC_ImagePanel tmp2 : m_ImagePanel)
					{
						/*if(!tmp2.isFocus()&&tmp2.isMultiSlice())
						{
							
							try {
								//overtay = tmp2.getRefROI().copy();
							} catch (CloneNotSupportedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}*/

					}
					//if(overtay!=null)
					//	tmp.setOverlayROI(overtay);
					/*if(tmp.hasOverlay())
						tmp.setOverlayROI(null);
					else
						tmp.setOverlayROI(overtay);*/
				}
			}
		}
		else if(e.getKeyCode()==KeyEvent.VK_I)
		{
			String sFilepath = null;
			for(AC_ImagePanel tmp : m_ImagePanel)
			{
				if(tmp.isFocus())
				{
				    sFilepath = tmp.getSliceFilePath();
					break;
				}
			}
			AC_DicomHeaderDialog dicomTable = new AC_DicomHeaderDialog(sFilepath);
			dicomTable.setVisible(true);
		}

		if(e.getModifiers()==2)//2 cntrol
		{

			System.out.println("control");
			if(e.getKeyCode()==KeyEvent.VK_1)
			{
				System.out.println("insert 1");
				for(AC_ImagePanel tmp : m_ImagePanel)
				{
					if(tmp.isFocus() || tmp.isMultiSlice())
					{
						tmp.updateWindowCW(Environment.g_WindowSettingValue[0][0],
								Environment.g_WindowSettingValue[0][1]);
					}
				}

				;
			}
			else if(e.getKeyCode()==KeyEvent.VK_2)
			{
				//System.out.println("insert 4");


				for(AC_ImagePanel tmp : m_ImagePanel)
				{
					if(tmp.isFocus() || tmp.isMultiSlice())
					{
						tmp.updateWindowCW(Environment.g_WindowSettingValue[1][0],
								Environment.g_WindowSettingValue[1][1]);
					}
				}



			}
			else if(e.getKeyCode()==KeyEvent.VK_3)
			{
				for(AC_ImagePanel tmp : m_ImagePanel)
				{
					if(tmp.isFocus() || tmp.isMultiSlice())
					{		
						tmp.updateWindowCW(Environment.g_WindowSettingValue[2][0],
							Environment.g_WindowSettingValue[2][1]);tmp.updateWindowCW(109, 84);
					}
				}

				//System.out.println("insert 3");

			}
			else if(e.getKeyCode()==KeyEvent.VK_4)
			{
				//System.out.println("insert 4");
				for(AC_ImagePanel tmp : m_ImagePanel)
				{
					if(tmp.isFocus() || tmp.isMultiSlice())
					{
						tmp.updateWindowCW(Environment.g_WindowSettingValue[3][0],
								Environment.g_WindowSettingValue[3][1]);
					}
				}



			}
			else if(e.getKeyCode()==KeyEvent.VK_A)
			{
				//System.out.println("insert 4");
				for(AC_ImagePanel tmp : m_ImagePanel)
				{
					//if(tmp.isFocus() || tmp.isMultiSlice())
					{
						tmp.setMultiSlice(true);
					}
				}
			}

		}
	}






	@Override
	public void keyTyped(KeyEvent e) {
		
		// TODO Auto-generated method stub
		System.out.println("Main vewer key keyTyped!!");
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		
		boolean blinkChk = false;
		
		System.out.println(e.toString());
		
	/*	for(AC_ImagePanel tmp : m_ImagePanel)
		{
			if((tmp.isFocus())&&tmp.isMultiSlice())
			{
				blinkChk = true;
				break;
			}
		}*/

		
		for(AC_ImagePanel tmp : m_ImagePanel)
		{
			if((!tmp.isFocus())&&tmp.isMultiSlice())
			{
				tmp.moveMutilSlice(e);
			}
			else if(tmp.isFocus())
			{
				tmp.moveMutilSlice(e);
				m_dRefSliceLocation = tmp.getRefSliceLocation();
			}
		}
		
		/*try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
	
	}
	
	public void close()
	{
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}





	

	
	

}
