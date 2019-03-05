package AC_DicomData.ViewerStructure;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import AC_DicomData.DicomIO.AC_DcmStructure;
import AC_DicomData.DicomIO.AC_Tag;

public class AC_PatientInfo {
	
	private String m_sPatientID = null;
	private HashMap<String, AC_StudyInfo> m_hsStudyInfo= new HashMap<String, AC_StudyInfo>();
	
	JLabel m_jlPatient = null;
	private Color m_clrPatientIDTXT = Color.LIGHT_GRAY;
	private Color m_clrPatientIDBACK =  new Color(109, 85, 85);
	
	
	public AC_PatientInfo(AC_DcmStructure input)
	{
		setPatientInfo(input);
	}
	
	public void setPatientInfo(AC_DcmStructure input)
	{
		m_sPatientID = input.getTagValue(AC_Tag.PatientID);
		addStudy(input);
		buildPatientIDLabellButton();
	}
	
	public boolean hasStudy(String sStudyUID)
	{
		return m_hsStudyInfo.containsKey(sStudyUID);
	}

	
	
	public void addStudy(AC_DcmStructure input)
	{
		String sStudyUID = input.getTagValue(AC_Tag.StudyInstanceUID);
		if(m_hsStudyInfo.containsKey(sStudyUID))
		{
			AC_StudyInfo tmp = m_hsStudyInfo.get(sStudyUID);
			tmp.addSeries(input);
		}else
		{
			AC_StudyInfo tmpStudy = new AC_StudyInfo(input);
			addStudy(sStudyUID, tmpStudy);
		}
		
	}
	
	public void addStudy(String sStudyUID, AC_StudyInfo StudyInfo)
	{
		if(!m_hsStudyInfo.containsKey(sStudyUID))
		{
			m_hsStudyInfo.put(sStudyUID, StudyInfo);
		}
		
	}
	
	public JLabel getLabel()
	{
		return m_jlPatient;
	}
	public AC_StudyInfo[] getStudies()
	{
		if(m_hsStudyInfo.size() ==0)
			return null;
		
		AC_StudyInfo[] output = m_hsStudyInfo.values().toArray(new AC_StudyInfo[m_hsStudyInfo.size()]);
				 
		return output;
	}
	
	private void buildPatientIDLabellButton()
	{
		m_jlPatient = new JLabel();

		String sInText = m_sPatientID;
		String buttonText = "<html><body><p align=\"center\" >" + 
				sInText + "</p>" ;
		
		BevelBorder  border=new BevelBorder(BevelBorder.RAISED);//3차원적인 테두리 효과를 위한것이고 양각의 옵션을 준다.
		m_jlPatient.setBorder(border);//라벨에 적용시킨다.
	    
		
		m_jlPatient.setOpaque(true);
		m_jlPatient.setBackground(m_clrPatientIDBACK);
		m_jlPatient.setForeground(m_clrPatientIDTXT);
		m_jlPatient.setHorizontalAlignment(SwingConstants.CENTER);
		m_jlPatient.setForeground(Color.WHITE);
		m_jlPatient.setFont(m_jlPatient.getFont().deriveFont(13.0F));
		m_jlPatient.setPreferredSize(new Dimension(150, 60));

		m_jlPatient.setText(buttonText);
		
		

	}

}
