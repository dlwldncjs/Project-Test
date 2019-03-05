package AC_DicomData.ViewerStructure;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import AC_DicomData.AC_DataConverter;
import AC_DicomData.AC_Modality;
import AC_DicomData.DicomIO.AC_DCMStandard;
import AC_DicomData.DicomIO.AC_DcmStructure;
import AC_DicomData.DicomIO.AC_DicomReader;
import AC_DicomData.DicomIO.AC_Tag;
import AC_Viewer.AC_ImageIconHeadler;
import javafx.scene.input.MouseDragEvent;

public class AC_SeriesInfo {
	
	private String m_sSeriesUID = "N/A";
	private String m_sSeriesData = "N/A";
	private String m_sSeriesDescription = "N/A";
	private String m_sSeriesNUM = "N/A";
	private int m_iModality = AC_Modality.UNKNOWN;
	private double m_dSliceThickness = -1.0;
	
	private String[] m_sarrImagePanelInfo = new String[7];

	//Key : SeriesUID 
	private HashMap<String, AC_SliceInfo> m_hsSlice = new HashMap<>();
	private TreeMap<String, String>m_tmSort = new TreeMap<>();
	
	private int ICON_SIZE = 100;
	JToggleButton m_jtbThumbnail  = null;
	private BufferedImage m_bfFisrtImage = null;
	
	
	public AC_SeriesInfo(AC_DcmStructure input)
	{
		
		setSeriesInfo(input);
	}
	
	private void makeImagePanelInfo(AC_DcmStructure input)
	{
		m_sarrImagePanelInfo[0] = input.getTagValue(AC_Tag.PatientsName);
		m_sarrImagePanelInfo[1] = input.getTagValue(AC_Tag.PatientID);
		m_sarrImagePanelInfo[2] = input.getTagValue(AC_Tag.PatientsBirthDate) +" "+input.getTagValue(AC_Tag.PatientsSex) ;
		m_sarrImagePanelInfo[3] = input.getTagValue(AC_Tag.InstitutionName);
		m_sarrImagePanelInfo[4] = input.getTagValue(AC_Tag.StudyID);
		m_sarrImagePanelInfo[5] = input.getTagValue(AC_Tag.StudyDescription);
		m_sarrImagePanelInfo[6] = input.getTagValue(AC_Tag.SeriesDescription);
		
	}
	
	public String[] getPanelInfo()
	{
		return m_sarrImagePanelInfo;
	}
	public String getSeriresNUM()
	{
		return m_sSeriesNUM;
	}
	public void setSeriesInfo(AC_DcmStructure input)
	{
		
		m_sSeriesNUM = input.getTagValue(AC_Tag.SeriesNumber);
		m_sSeriesUID = input.getTagValue(AC_Tag.SeriesInstanceUID);
		m_sSeriesData = input.getTagValue(AC_Tag.SeriesDate);
		m_sSeriesDescription =  input.getTagValue(AC_Tag.SeriesDescription);
		String sSliceThickness = input.getTagValue(AC_Tag.SliceThickness);
		if(!(sSliceThickness == null))
			m_dSliceThickness = Double.parseDouble(input.getTagValue(AC_Tag.SliceThickness)); 
		m_iModality = AC_Modality.chkModality(input.getTagValue(AC_Tag.Modality));
		
		makeImagePanelInfo(input);
		
		byte[]  barrPixelData = null;
		
		addSlice(input);
		
		if(m_jtbThumbnail==null)
		{
			AC_DicomReader tmp = new AC_DicomReader(input.getFilePath());
			try {
				barrPixelData = tmp.getPixelData();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			//AC_SliceInfo tmp = m_hsSlice.get( m_hsSlice.keySet().toArray()[0]);
			
			makeThumbnail(barrPixelData, input);
		}
			
	}
	
	
	
	
	public String getSeriesUID()
	{
		return m_sSeriesUID;
	}
	
	public String getSeriesData()
	{
		return m_sSeriesData;
	}
	public String getSeriesDescription()
	{
		return m_sSeriesDescription;
	}
	public double getSliceThickness()
	{
		return m_dSliceThickness;
	}
	public int getModality()
	{
		return m_iModality;
	}
	public int getTotalSliceNUM()
	{
		return m_hsSlice.size();
	}

	
	
	
	public JToggleButton getThumbnail()
	{
		return m_jtbThumbnail;
	}
/*	public AC_Slice[] getSeries()
	{
		if(m_hsSeries.size() ==0)
			return null;
		
		AC_SeriesInfo[] output = m_hsSeries.values().toArray(new AC_SeriesInfo[m_hsSeries.size()]);
				 
		return output;
	}*/
	private void makeThumbnail(byte[] barrPixeldata, AC_DcmStructure input)
	{
		m_jtbThumbnail = new JToggleButton();
		
		double[] darrSignalData = AC_DataConverter.DCMPixelData2Singnal(barrPixeldata, input);
		

		
		switch (m_iModality) {
		case AC_Modality.MR : 
			m_bfFisrtImage  = AC_DataConverter.FastSignal2bffImg(darrSignalData,
					Integer.parseInt(input.getTagValue(AC_Tag.Columns)), 
					Integer.parseInt(input.getTagValue(AC_Tag.Rows)),
					Double.parseDouble(input.getTagValue(AC_Tag.WindowCenter)), 
					Double.parseDouble(input.getTagValue(AC_Tag.WindowWidth)),
					Integer.parseInt(input.getTagValue(AC_Tag.SamplesperPixel)));

			break;
		case AC_Modality.CT : 	case AC_Modality.UNKNOWN : default :
			m_bfFisrtImage  = AC_DataConverter.FastSignal2bffImg(darrSignalData,
					Integer.parseInt(input.getTagValue(AC_Tag.Columns)), 
					Integer.parseInt(input.getTagValue(AC_Tag.Rows)),
					200, 
					1000,
					Integer.parseInt(input.getTagValue(AC_Tag.SamplesperPixel)));
			break;
		case AC_Modality.US : 	case AC_Modality.EN : 
			m_bfFisrtImage  = AC_DataConverter.FastSignal2bffImg(darrSignalData,
					Integer.parseInt(input.getTagValue(AC_Tag.Columns)), 
					Integer.parseInt(input.getTagValue(AC_Tag.Rows)),
					126, 
					256,
					Integer.parseInt(input.getTagValue(AC_Tag.SamplesperPixel)));
			break;
		}
		
	
		
		
		
		
		String buttonText = "<html><body><p align=\"left\" >" + 
				m_sSeriesDescription+"("+m_hsSlice.size()+")"+ "</p>" ;

		m_jtbThumbnail.setHorizontalTextPosition ( SwingConstants.CENTER ) ;


		m_jtbThumbnail.setBorder(new EtchedBorder(EtchedBorder.LOWERED));//라벨에 적용시킨다.

		m_jtbThumbnail.setIcon(AC_ImageIconHeadler.reSizeImageICON(new ImageIcon(m_bfFisrtImage),ICON_SIZE));
			
		
		m_jtbThumbnail.setName("SeriresThumb_"+getSeriesUID());


		m_jtbThumbnail.setBackground(Color.gray);
		m_jtbThumbnail.setForeground(new Color(223,223,223));
		m_jtbThumbnail.setVerticalTextPosition   ( SwingConstants.TOP ) ;
		m_jtbThumbnail.setVerticalAlignment      ( SwingConstants.TOP ) ;
		m_jtbThumbnail.setFont(m_jtbThumbnail.getFont().deriveFont(10.0F));


		m_jtbThumbnail.setText(buttonText);
		
		

	}
	public void addSlice(AC_DcmStructure input)
	{
		
		String sSOPUID =input.getTagValue(AC_Tag.SOPInstanceUID);
		
		
		
	
		
		//System.out.println("instance : " + iNumber);

		if(!m_hsSlice.containsKey(sSOPUID))
		{
			AC_SliceInfo tmpSlice = new AC_SliceInfo(input);
			addSlice(sSOPUID, tmpSlice);
		}
	}
	
	public void addSlice(String iInstanceNUM, AC_SliceInfo input)
	{
		if(!m_hsSlice.containsKey(iInstanceNUM))
		{
			//AC_SliceInfo tmpSlice = new AC_SliceInfo(input);
			m_hsSlice.put(iInstanceNUM, input);
		//	double dSortkey = input.getImagePosition()[2];
			String sSortkey = iInstanceNUM;//input.getSOPInstanceUID();
			m_tmSort.put(sSortkey, iInstanceNUM);
			
			
			
		//	m_tmSort.navigableKeySet();
			
			
			
			if(m_jtbThumbnail!=null)
			{
			String buttonText = "<html><body><p align=\"left\" >" + 
					m_sSeriesDescription+"("+m_hsSlice.size()+")"+ "</p>" ;
			
			m_jtbThumbnail.setText(buttonText);
			}
		}
	}
	
	
	
	
	public AC_SliceInfo getSlice(int idx)
	{
		String[] tmpSOPUID = m_tmSort.values().toArray(new String[m_tmSort.size()]);
		System.out.println("Start UID key");
		
		for(String tmp : m_tmSort.keySet())
		{
			System.out.println(tmp);
		}
		
		System.out.println("Start UID");
		for(String tmp : tmpSOPUID)
		{
			System.out.println(tmp);
		}
		System.out.println("End UID");
		return m_hsSlice.get(tmpSOPUID[idx]);
	}
	
	
	


}
