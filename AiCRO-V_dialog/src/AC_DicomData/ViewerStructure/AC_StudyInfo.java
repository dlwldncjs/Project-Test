package AC_DicomData.ViewerStructure;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import AC_DicomData.AC_DataConverter;
import AC_DicomData.AC_Modality;
import AC_DicomData.DicomIO.AC_DcmStructure;
import AC_DicomData.DicomIO.AC_Tag;

public class AC_StudyInfo {
	
	private String m_sStudyUID = "N/A";
	private String m_sStudyDate = "N/A";
	private String m_sStudyTime = "N/A";
	private String m_sStudyDescription = "N/A";

	private int m_iModality = AC_Modality.UNKNOWN;
	//Key : SeriesUID 
	private HashMap<String, AC_SeriesInfo> m_hsSeries = new HashMap<>();
	//jlable
	JLabel m_jlStudy = null;
	private Color m_clrStudyIDTXT = Color.LIGHT_GRAY;
	private Color m_clrStudyBACK =   new Color(95, 65, 65); 
	
	public AC_StudyInfo(AC_DcmStructure input)
	{
		setSeriesInfo(input);
	}
	
	public JLabel getLabel()
	{
		return m_jlStudy;
	}
	public AC_SeriesInfo[] getSeries()
	{
		if(m_hsSeries.size() ==0)
			return null;
		
		AC_SeriesInfo[] output = m_hsSeries.values().toArray(new AC_SeriesInfo[m_hsSeries.size()]);
				 
		return output;
	}
	
	public boolean hasSeries(String sSeriesUID)
	{
		return m_hsSeries.containsKey(sSeriesUID);
	}
	
	public AC_SeriesInfo getSeries(String sSeriesUID)
	{
		return m_hsSeries.get(sSeriesUID);
	}
	
	
	
	public void setSeriesInfo(AC_DcmStructure input)
	{
		m_sStudyUID = input.getTagValue(AC_Tag.StudyInstanceUID);
		m_sStudyDate = input.getTagValue(AC_Tag.StudyDate);
		m_sStudyDescription = input.getTagValue(AC_Tag.StudyDescription);
		m_sStudyTime = input.getTagValue(AC_Tag.StudyTime);
		
		m_iModality = AC_Modality.chkModality(input.getTagValue(AC_Tag.Modality));

		
		addSeries(input);
		
		buildDateLabellButton();
	}

	

	public void addSeries(AC_DcmStructure input)
	{
		String sSeriesUID = input.getTagValue(AC_Tag.SeriesInstanceUID);

		if(m_hsSeries.containsKey(sSeriesUID))
		{
			AC_SeriesInfo tmp = m_hsSeries.get(sSeriesUID);
			tmp.addSlice(input);
		}else
		{
			AC_SeriesInfo tmpSeries = new AC_SeriesInfo(input);
			addSeries(sSeriesUID, tmpSeries);
		}
	}
	
	public void addSeries(String sSeriesUID, AC_SeriesInfo simpleSries)
	{
		if(!m_hsSeries.containsKey(sSeriesUID))
		{
			m_hsSeries.put(sSeriesUID,simpleSries);	
		}
			
	}
	
	
	private void  buildDateLabellButton()
	{
		m_jlStudy = new JLabel();
		
		String sDateTime = AC_DataConverter.margeDataTime(m_sStudyDate, m_sStudyTime);
		String sModality = AC_Modality.parseSting(m_iModality);
		
	
		
	/*	int ichk = m_jlStudy.getFontMetrics(maekLable.getFont()).stringWidth(sStudyDescription);
		if(120<ichk)
		{
			sStudyDescription = sStudyDescription.substring(0, 10) + "...";
		}*/
		
		
		
			
		String sInText = sDateTime+"<br>"+
		m_sStudyDescription+"<br>"+
		sModality+" : "+m_hsSeries.size()+" Series"+
		"</p>";
		String buttonText = "<html><body><p align=\"center\" >" + 
				sInText;
		
		BevelBorder  border=new BevelBorder(BevelBorder.RAISED);//3차원적인 테두리 효과를 위한것이고 양각의 옵션을 준다.
		m_jlStudy.setBorder(border);//라벨에 적용시킨다.
	

		m_jlStudy.setOpaque(true);
		m_jlStudy.setBackground(m_clrStudyBACK);
		m_jlStudy.setForeground(m_clrStudyIDTXT);
		
		m_jlStudy.setHorizontalAlignment(SwingConstants.CENTER);
		
		m_jlStudy.setFont(m_jlStudy.getFont().deriveFont(11.0F));
		m_jlStudy.setPreferredSize(new Dimension(150, 60));



		m_jlStudy.setText(buttonText);
	}

	
	

}
