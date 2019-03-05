package AC_DicomData.ViewerStructure;

import java.util.HashMap;

import AC_DicomData.DicomIO.AC_DcmStructure;
import AC_DicomData.DicomIO.AC_Tag;


public class AC_ViewrDcmData {
	
	private HashMap<String, AC_PatientInfo> m_hsPatientsInfo= new HashMap<String, AC_PatientInfo>();
	
	
	public AC_ViewrDcmData()
	{
		
	}
	
	
	
	public AC_PatientInfo[] getPatients()
	{
		if(m_hsPatientsInfo.size() ==0)
			return null;
		
		 AC_PatientInfo[] output = m_hsPatientsInfo.values().toArray(new AC_PatientInfo[m_hsPatientsInfo.size()]);
				 
		return output;
	}
	
	public void addPatient(AC_DcmStructure input)
	{
		String sPatientID = input.getTagValue(AC_Tag.PatientID);

		if(m_hsPatientsInfo.containsKey(sPatientID))
		{
			AC_PatientInfo tmp = m_hsPatientsInfo.get(sPatientID);
			tmp.addStudy(input);
		}else
		{
			AC_PatientInfo tmpPatient = new AC_PatientInfo(input);
			addPatient(sPatientID, tmpPatient);
		}
	}
	
	public void addPatient(String PatientID, AC_PatientInfo input)
	{
		
		if(!m_hsPatientsInfo.containsKey(PatientID))
		{
			m_hsPatientsInfo.put(PatientID, input);
		}
		
	}
	
	public AC_SeriesInfo getSeries(String sSeriesUID)
	{
		for(String patientKey : m_hsPatientsInfo.keySet())
		{
			AC_StudyInfo[] sStudyList = m_hsPatientsInfo.get(patientKey).getStudies();
			for(AC_StudyInfo tmp : sStudyList )
			{
				if(tmp.hasSeries(sSeriesUID))
				{
					return tmp.getSeries(sSeriesUID);
				}
			}
		}
			
		
		return null;	
	}

}
