package AC_DicomData.ViewerStructure;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import AC_DicomData.AC_DataConverter;
import AC_DicomData.AC_Modality;
import AC_DicomData.DicomIO.AC_DcmStructure;
import AC_DicomData.DicomIO.AC_Tag;
import AC_ROIComp.AC_ROI;
import AC_Viewer.Environment;

public class AC_SliceInfo {
	byte[] m_pixelData = null;
	
	int m_iRows = -1;
	int m_iColumns = -1;
	int m_iPixelRepresentation = -1;
	int m_iBitsAllocated = -1;
	int m_iBitsStored = -1;
	int m_iSamplesperPixel = -1;
	//int m_iPixelRepresentation = -1;
	int m_iModality = AC_Modality.UNKNOWN;
	double m_dRescaleIntercept = 0.0;
	double m_dRescaleSlope = 1.0; 
	
	String m_sSOPInstaceUID = null;
	
	int m_iWindowWidth = -1;
	int m_iWindowCenter = -1;
	
	double[] m_darrImagePosition = new double[3];
	double[] m_darrPixelSpacing = new double[2];
	double[][] m_darrImageOrient = new double[3][3];
	String[] m_sRowDirection = new String[2];
	String[] m_sColunmDirection = new String[2];
	
	int m_iInstanceNumber = -1;
	//CT parameter
	double m_dXRayTubeCurrent = -1;
	double m_dKVP = -1;
	//MR Paramater
	double m_dMagneticFieldStrength = -1;
	double m_dRepetitionTime = -1;
	double m_dEchoTime = -1;
	
	File m_sDCMFile = null;
	
	String[] m_sarrViewrPanelInfo = new String[3];
	List<AC_ROI> m_ROIs = null;

	public AC_SliceInfo(AC_DcmStructure input) {
		setSliceInfo(input);
	}
	
	public String[] getPanelInfo() {
		return m_sarrViewrPanelInfo;
	}

	private void makeImagePanelInfo(AC_DcmStructure input) {
		int iModality = AC_Modality.chkModality(input.getTagValue(AC_Tag.Modality));
		switch (iModality) {
		case AC_Modality.CT:
			String sXRayTubeCurrent = String.format("%.1f", Double.parseDouble(input.getTagValue(AC_Tag.XRayTubeCurrent)));
			String sKVP = String.format("%.1f", Double.parseDouble(input.getTagValue(AC_Tag.KVP)));
			String sSumString = sXRayTubeCurrent + "mA "+sKVP+"kVp";
			
			m_sarrViewrPanelInfo[0] = "";
			m_sarrViewrPanelInfo[1] = sSumString;
			break;
		case AC_Modality.MR:
			String sMagneticFieldStrength = String.format("%f",Double.parseDouble(input.getTagValue(AC_Tag.MagneticFieldStrength))); 
			String sRepetitionTime = String.format("%f",Double.parseDouble(input.getTagValue(AC_Tag.RepetitionTime))); 
		
			String EchoTimes = String.format("%.1f", Double.parseDouble(input.getTagValue(AC_Tag.EchoTime))); 
		
			//g.drawString(sSumString , xe-fontMetrics.stringWidth(sSumString)-offset, (ye-offset)-step*1);
			
			m_sarrViewrPanelInfo[0] = "RT : " + sRepetitionTime +"ET : "+EchoTimes;
			m_sarrViewrPanelInfo[1] = "MFS : "+sMagneticFieldStrength;

		default:
			break;
		}
		String sSliceData = input.getTagValue(AC_Tag.AcquisitionDate);
		String sSliceTime = input.getTagValue(AC_Tag.AcquisitionTime);
		
		m_sarrViewrPanelInfo[2] = AC_DataConverter.margeDataTime(sSliceData, sSliceTime);
	}
	
	public void setSliceInfo(AC_DcmStructure input)	{

		m_pixelData  = input.getPixelData();
		
		m_sDCMFile = new File(input.getFilePath());
		
		m_iModality = AC_Modality.chkModality(input.getTagValue(AC_Tag.Modality));
		
		m_sSOPInstaceUID = input.getTagValue(AC_Tag.SOPInstanceUID);
		
		m_iRows = input.getTagIntValue(AC_Tag.Rows);                    
		m_iColumns = input.getTagIntValue(AC_Tag.Columns);                                   
		m_iPixelRepresentation =input.getTagIntValue(AC_Tag.PixelRepresentation);                        
		m_iBitsAllocated = input.getTagIntValue(AC_Tag.BitsAllocated);                              
		m_iBitsStored = input.getTagIntValue(AC_Tag.BitsStored);                                  
		m_iSamplesperPixel = input.getTagIntValue(AC_Tag.SamplesperPixel); 
		m_iBitsStored = input.getTagIntValue(AC_Tag.BitsStored);                                  

		m_iWindowWidth =  input.getTagIntValue(AC_Tag.WindowWidth); 
		m_iWindowCenter =  input.getTagIntValue(AC_Tag.WindowCenter); 
		
		
		m_dRescaleIntercept = input.getTagDoubleValue(AC_Tag.RescaleIntercept);                        
		m_dRescaleSlope =  input.getTagDoubleValue(AC_Tag.RescaleSlope); 
		
		String sImagePostison = input.getTagValue(AC_Tag.ImagePositionPatient);
		
		if(sImagePostison!=null)
		{
			String[] tmpString =  sImagePostison.split("\\\\");
			for(int i=0; i<tmpString.length; i++)
			m_darrImagePosition[i] = Double.parseDouble(tmpString[i]);
		}
		

		
		String sPixelSpacing = input.getTagValue(AC_Tag.PixelSpacing);
		
		if(sPixelSpacing!=null)
		{
			String[] tmpString =  sPixelSpacing.split("\\\\");
			for(int i=0; i<tmpString.length; i++)
				m_darrPixelSpacing[i] = Double.parseDouble(tmpString[i]);
		}else
		{
			double dReconstructionDiameter = input.getTagDoubleValue(AC_Tag.ReconstructionDiameter);
			m_darrPixelSpacing[0] = dReconstructionDiameter/m_iRows;
			m_darrPixelSpacing[1] = dReconstructionDiameter/m_iColumns;
			
		}
		
		String sImageOrientationPatient = input.getTagValue(AC_Tag.ImageOrientationPatient);
		
		if(sImageOrientationPatient!=null)
		{
			String[] tmpString =  sImageOrientationPatient.split("\\\\");
			for(int i=0; i<3; i++)
			{
				m_darrImageOrient[0][i] = Double.parseDouble(tmpString[i]);
				m_darrImageOrient[1][i] = Double.parseDouble(tmpString[i+3]);
			}
			
			m_sRowDirection = covOrientation2Direction(m_darrImageOrient[0]);
		//	System.out.println("!!!!!!!!!!!!!!!!!! " + m_sRowDirection[0]+m_sRowDirection[1]);
			m_sColunmDirection = covOrientation2Direction(m_darrImageOrient[1]);
		//	System.out.println("!!!!!!!!!!!!!!!!!! " + m_sColunmDirection[0]+m_sColunmDirection[1]);
		}
		
		chkDicomInfo();
		makeImagePanelInfo(input);
	}
	
	private String[] covOrientation2Direction(double[] ipnut)
	{

        String orientationFX = ipnut[0] <= 0 ? "R" : "L";
        String orientationFY = ipnut[1] <= 0 ? "A" : "P";
        String orientationFZ = ipnut[2] <= 0 ? "F" : "H";
        
        String orientationBX = ipnut[0] >= 0 ? "R" : "L";
        String orientationBY = ipnut[1] >= 0 ? "A" : "P";
        String orientationBZ = ipnut[2] >= 0 ? "F" : "H";
        
        double absX = Math.abs(ipnut[0]);
        double absY = Math.abs(ipnut[1]);
        double absZ = Math.abs(ipnut[2]);
        
        String[] sDirection = new String[2];
        sDirection[0] = sDirection[1] = "";
      
     
        int i;
        
        for (i=0; i<3; i++) 
        {
                if (absX>0.4 && absX>absY && absX>absZ) {
                //	System.out.println(absX);
                	sDirection[1] += orientationFX;
                	sDirection[0] += orientationBX;
                        absX=0;
                }
                else if (absY>0.4 && absY>absX && absY>absZ) {
                	sDirection[1] +=  orientationFY;
                	sDirection[0] +=  orientationBY;
                        absY=0;
                }
                else if (absZ>0.4 && absZ>absX && absZ>absY) {
                	sDirection[1] +=  orientationFZ;
                	sDirection[0] +=  orientationBZ;
                       absZ=0;
                }
                else
                	break;
                //sString[i] = "\0";
        }
	
	    return sDirection;
	}
	private void chkDicomInfo()
	{

		
		
		if(m_iWindowWidth==Environment.g_nullVale)
		{
			m_iWindowWidth = 125;
		}
		if(m_iWindowCenter==Environment.g_nullVale)
			m_iWindowCenter = 255;
		if(m_dRescaleIntercept==Environment.g_nullVale)
			m_dRescaleIntercept = 0;
		if(m_dRescaleSlope==Environment.g_nullVale)
			m_dRescaleSlope = 1;
		
	
		
		                       

		
	
	}
	
	//byte[] m_pixelData = null;
	
	public void addROI(AC_ROI input)
	{
		if(m_ROIs ==null || m_ROIs.isEmpty())
			m_ROIs = new ArrayList<>();
		input.setName("ROI_Panel_"+m_ROIs.size());
	
		m_ROIs.add(input);
	}
	public AC_ROI[] getROIs()
	{
		if(m_ROIs ==null|| m_ROIs.isEmpty())
			return null;
		return m_ROIs.toArray(new AC_ROI[m_ROIs.size()]);
	}
	public boolean hasROIs()
	{
		if(m_ROIs==null)
			return false;
		else
			return true;
	}

	public File getFile()
	{
		return m_sDCMFile;
	}
	
	public int getRows()
	{
		return m_iRows;
	}
	public int getColumns()
	{
		return m_iColumns;
	}
	public int getPixelRepresentation()
	{
		return m_iPixelRepresentation;
	}
	public int getBitsAllocated()
	{
		return m_iBitsAllocated;
	}
	public int getBitsStored()
	{
		return m_iBitsStored;
	}
	public int getSamplesperPixel()
	{
		return m_iSamplesperPixel;
	}
	public int getModality()
	{
		return m_iModality;
	}
	public double getRescaleIntercept()
	{
		return m_dRescaleIntercept;
	}
	public double getRescaleSlope()
	{
		return m_dRescaleSlope;
	}
	public int getWindowWidth()
	{
		return m_iWindowWidth;
	}
	public int getWindowCenter()
	{
		return m_iWindowCenter;
	}
	public double[] getImagePosition()
	{
		return m_darrImagePosition;
	}
	public double[][] getImageOrient()
	{
		return m_darrImageOrient;
	}
	public int getInstanceNumber()
	{
		return m_iInstanceNumber;
	}
	public double getXRayTubeCurrent()
	{
		return m_dXRayTubeCurrent;
	}
	public double getKVP()
	{
		return m_dKVP;
	}
	public double getMagneticFieldStrength()
	{
		return m_dMagneticFieldStrength;
	}
	public double getRepetitionTime()
	{
		return m_dRepetitionTime;
	}
	public double getEchoTime()
	{
		return m_dEchoTime;
	}
	public double[] getPixelSpacing()
	{
		return m_darrPixelSpacing;
	}
	public String[] getRowDirection()
	{
		return m_sRowDirection;
	}
	public String[] getColunmDirection()
	{
		return m_sColunmDirection;
	}
	public String getSOPInstanceUID()
	{
		return m_sSOPInstaceUID;
	}

}
