package AC_funcComp;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import AC_Viewer.Environment;


public class AC_TxtPanel extends JPanel  {
	

	private static final long serialVersionUID = 1L;

	
	public static final int LEFT_TOP = 0;
	public static final int LEFT = 1;
	public static final int LEFT_BOTTOM = 2;
	public static final int RIGHT = 3;
	public static final int RIGHT_TOP = 4;
	public static final int RIGHT_BOTTOM = 5;
	public static final int TOP = 6;
	public static final int BOTTOM = 7;

	
	private int m_iWidth = 0;
	private int m_iHeight = 0;

	
	private int m_iImgW =0;
	private int m_iImgH =0;
	

	
	private int m_AlignState = 0;
	String[] m_sTXT = null;
	AC_AutoLabel[] m_alTxt = new AC_AutoLabel[0];

	
	public AC_TxtPanel() {
		// TODO Auto-generated constructor stub
		
		init();
	}
	
	public AC_TxtPanel(String sTitle, int AlignState) {
		// TODO Auto-generated constructor stub

		this.setName(sTitle);
		m_AlignState = AlignState;
		init();
	}
	
	public AC_TxtPanel(int AlignState) {
		// TODO Auto-generated constructor stub
		m_AlignState = AlignState;
		init();
	}
	
	protected  void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		
		//System.out.println("txtPanel repaint" + this.toString());

		
		
		//drawTxt(g);*/
	}
	
	public void init()
	{
		this.setOpaque(false);
		this.setDoubleBuffered(true);	
	}
	
	
	public void setParentsSize(  int iWidth, int iHight)
	{
		
		System.out.println("Set ParentsSize : W :"+iWidth+"H : "+iHight);
		 m_iImgW =iWidth;
		 m_iImgH =iHight;
		this.setSize(m_iImgW/2,(int)(m_iImgH*0.02*m_alTxt.length));
		for(AC_AutoLabel tmp : m_alTxt)
		{
			tmp.setSize(m_iImgW/2,(int)(m_iImgH*0.02));
			tmp.resize();
		}
		setLocationAlign();	
		
		revalidate();
		repaint();
	
	}
	
	private void setLocationAlign()
	{
		int iOffset = 10;
		
		switch(m_AlignState)
		{
		case LEFT_BOTTOM : 
			this.setLocation(0,m_iImgH-this.getHeight()-iOffset);
			setLabelLocation(SwingConstants.LEFT);
			break;
		case LEFT_TOP : 
			this.setLocation(0, 0);
			setLabelLocation(SwingConstants.LEFT);
			break;
		case LEFT : 
			this.setLocation(0,  ( m_iImgH/2)-(this.getHeight()/2)-iOffset);
			setLabelLocation(SwingConstants.LEFT);
			break;
		case RIGHT_BOTTOM : 
			this.setLocation(m_iImgW/2-iOffset,m_iImgH-this.getHeight()-iOffset);
			setLabelLocation(SwingConstants.RIGHT);
			break;
		case RIGHT_TOP : 
			this.setLocation(m_iImgW/2-iOffset, 0);
			setLabelLocation(SwingConstants.RIGHT);
			break;
		case RIGHT : 
			this.setLocation(m_iImgW/2-iOffset, ( m_iImgH/2)-(this.getHeight()/2)-iOffset);
			setLabelLocation(SwingConstants.RIGHT);
			break;
		case TOP : 
			this.setLocation( (m_iImgW/2)-(this.getWidth()/2)-iOffset, 0);
			setLabelLocation(SwingConstants.CENTER);
			break;	
		case BOTTOM : 
			this.setLocation((m_iImgW/2)-(this.getWidth()/2),m_iImgH-this.getHeight()-iOffset);
			setLabelLocation(SwingConstants.CENTER);
			break;
		default :
			this.setLocation(0, 0);
		}
		
	
	}
	
	private void setLabelLocation(int input)
	{
		for(AC_AutoLabel tmp : m_alTxt)
		{
			tmp.setHorizontalAlignment(input);
		}
		
	}
	
	public void updateTxt(String[] sInput)
	{
		if(m_alTxt.length==0)
			return;

		for(int idx = 0; idx<sInput.length;idx++)
		{
			m_alTxt[idx].setText(sInput[idx]);
		}
	}
	
	public void setTxt(String[] sInput)
	{

		m_alTxt = new AC_AutoLabel[sInput.length];
		this.setBackground(Color.red);
		
		
		this.setLayout(new GridLayout(sInput.length,1));
		
		
		for(int idx = 0; idx<sInput.length;idx++)
		{
			m_alTxt[idx] = new AC_AutoLabel(sInput[idx]);
			m_alTxt[idx].setForeground(Color.YELLOW);

			this.add(m_alTxt[idx]);

		
		}
		
		
	
	}


}
