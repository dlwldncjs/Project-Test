package AC_Viewer;

import java.awt.BorderLayout;
import java.awt.Color;


import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;

import javax.swing.JPanel;

import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;

import AC_DicomData.ViewerStructure.AC_PatientInfo;
import AC_DicomData.ViewerStructure.AC_SeriesInfo;

import AC_DicomData.ViewerStructure.AC_StudyInfo;
import AC_DicomData.ViewerStructure.AC_ViewrDcmData;


public class AC_SeriesBar extends JPanel {
	
	/**
	 * 
	 */
	private Color clrPatientIDTXT = Color.LIGHT_GRAY;
	private Color clrSeriesValueTXT = Color.LIGHT_GRAY;
	private Color clrPatientIDBACK =  new Color(109, 85, 85);
	private Color clrSeriesValueBACK = new Color(95, 65, 65);
	private JPanel m_pThumbNailBar;
	private JScrollPane m_sbTNBScroll;
	private static final long serialVersionUID = 1L;
	private ButtonGroup m_btnGroup= new ButtonGroup();
	
	private AC_ViewrDcmData m_VDcmData = null;
	

	public AC_SeriesBar()
	{
		this.setBackground(new Color(100,100,100));
		this.setLayout(new BorderLayout());
		this.setSize(200, 300);
		init();
		
		
	}
	 
	public void reset()
	{
		this.removeAll();
		m_btnGroup= new ButtonGroup();
		
		


		init();
	}
	
	private void init()
	{
		
		

		m_pThumbNailBar = new JPanel();
		
		m_pThumbNailBar.setLayout(new BoxLayout(m_pThumbNailBar, BoxLayout.Y_AXIS ));
		m_pThumbNailBar.setBackground(new Color(100,100,100));
		
		m_sbTNBScroll = 
				new JScrollPane(m_pThumbNailBar, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		m_sbTNBScroll.getVerticalScrollBar().setUnitIncrement(10);
		m_sbTNBScroll.setAutoscrolls(true);
		this.add(m_sbTNBScroll,BorderLayout.CENTER);
	
			
		
	}

	public void drawPanel()
	{
		for(AC_PatientInfo tmpPatient : m_VDcmData.getPatients())
		{
			m_pThumbNailBar.add(tmpPatient.getLabel());
			for(AC_StudyInfo tmpStudy : tmpPatient.getStudies())
			{
				m_pThumbNailBar.add(tmpStudy.getLabel());
				for(AC_SeriesInfo tmpSeries : tmpStudy.getSeries())
				{
					JToggleButton tmp = tmpSeries.getThumbnail();
					if(tmp.getMouseListeners().length==1)
						tmp.addMouseListener(this.getMouseListeners()[0]);
					if(tmp.getMouseMotionListeners().length==1)
						tmp.addMouseMotionListener(this.getMouseMotionListeners()[0]);
		
					
					m_pThumbNailBar.add(tmpSeries.getThumbnail());
				}
			}
		}
		
		this.revalidate();
		this.repaint();
	}
	
	
	public void setDCMdata(AC_ViewrDcmData input)
	{
		m_VDcmData  = input;
	}
	
	
	
	
	public void addSaveRoiButton(JButton input)
	{
		if(Environment.g_flagStandAlone)
			this.add(input,BorderLayout.SOUTH);
		
	}


	

}
