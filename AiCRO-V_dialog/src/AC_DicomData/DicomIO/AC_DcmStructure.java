package AC_DicomData.DicomIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import AC_Viewer.Environment;

public class AC_DcmStructure {
	
	static Logger logger = Logger.getLogger(AC_DcmStructure.class);
	
	private String m_sFilePath = null;
	String MARGIN = "   ";
	
	private LinkedHashMap<Integer, String[]> m_attirbutes
	= new LinkedHashMap<Integer, String[]>() ;
	private LinkedHashMap<Integer, AC_DcmStructure> m_SequenceMap
	= new LinkedHashMap<Integer, AC_DcmStructure>();
	private byte[] m_pixelData = null;
	private int m_pixelVR = AC_VR.OW;

	public int getPixelVR()
	{
		return m_pixelVR;
	}
	public void setFilePath(String sFilePath)
	{
		m_sFilePath = sFilePath;
	}
	
	public String getFilePath()
	{
		return m_sFilePath ;
	}
	
	public void setPixelData(byte[] input, int inVR)
	{
		if( m_pixelData !=null)
			m_pixelData = null;
		m_pixelVR  = inVR;
		m_pixelData = input;
	}
	
	public void setSequenceValue(int iTag, AC_DcmStructure value)
	{
		if(value==null)
			value = new AC_DcmStructure();
		
		m_SequenceMap.put(iTag,  value);
	}
	
	public AC_DcmStructure getSequenceValue(int iTag)
	{
		
		
		return m_SequenceMap.get(iTag);
	}
	
	public LinkedHashMap<Integer, AC_DcmStructure> getSequence()
	{

		return m_SequenceMap;
	}
	
	public LinkedHashMap<Integer, String[]> getAttributes()
	{
		return m_attirbutes;
	}
	
	public String[] getAttribute(int input)
	{
		return m_attirbutes.get(input);
	}
	
	public int getTagIntValue(int input)
	{
		String sValue = getTagValue(input);
		if(sValue==null)
			return Environment.g_nullVale;
		else
			
			return Integer.parseInt(sValue); 
	}
	
	public double getTagDoubleValue(int input)
	{
		String sValue = getTagValue(input);
		if(sValue==null)
			return Environment.g_nullVale;
		else
			
			return Double.parseDouble(sValue); 
	}
	
	public String getTagValue(int input)
	{
		if(m_attirbutes.containsKey(input))
			return m_attirbutes.get(input)[1];
		else
			return null;
	}
	
	
	public byte[] getPixelData()
	{
		return m_pixelData;
	}

	public void setAttribute(int tag,String[] input)
	{
		try 
		{
			if(input.length!=2)
			{
		
				logger.error("Input Size Error : input size-> " + input.length );
				throw new Exception(); 
			}
			
			if(m_attirbutes.containsKey(tag))
				return;
			m_attirbutes.put(tag,input);

		}catch(Exception e)
		{
		   /// 예외 발생시 처리 부분
			logger.error(e);
			
		}
	}
	
	public void changeAttribute(int tag,String input)
	{
		if(m_attirbutes.get(tag) ==null)
			return;
		
		String[] tmp = m_attirbutes.get(tag);
		tmp[1] = input;
		m_attirbutes.put(tag,tmp);
	}
	
	public void setAttributes(LinkedHashMap<Integer, String[]> input)
	{
		try 
		{
			if(input.size()==0)
			{
		
				logger.error("Input Size Error : input size-> " + input.size() );
				throw new Exception(); 
			}
			
			m_attirbutes.putAll(input);
		}catch(Exception e)
		{
		   /// 예외 발생시 처리 부분
			logger.error(e);
		}
	}
	
	private boolean isSequnce(int inTag)
	{
		if(m_SequenceMap.get(inTag) !=null)
			return true;
		return false;
	}
	
	public List<Object[]> buildTableInfo(int iStep ) {	
		List<Object[]> output = new ArrayList<Object[]>();
		Set<Integer>keyset = m_attirbutes.keySet();
		
		List<Integer> SortKey = new ArrayList<>(keyset);
		java.util.Collections.sort(SortKey);
		
		AC_DicomDictionary dcmDic= new AC_DicomDictionary();
		Object[] itemTag = null;
		String sMargein = "";
		String sItemMargein = "";
		for(int i =0; i<iStep;i++)
			sMargein += MARGIN; 
		for(int i =0; i<iStep-1;i++)
			sItemMargein += MARGIN; 
		
		for(int i :SortKey)
		{
			Object[] objcttmp = new Object[6];
			String value = m_attirbutes.get(i).toString();
			
			objcttmp[0] = sMargein+"("+String.format("%08X", i).substring(0, 4)+","+ String.format("%08X", i).substring(4, 8)+")";
			objcttmp[3] = AC_VR.getVRName(dcmDic.getTagVR(i));
			if(!value.contains("\\\\"))
				objcttmp[4] = 1; 
			else {
				objcttmp[4] = value.split("\\\\").length;
			}
			objcttmp[5] = value.length();
			if(value.length()%2!=0)
				objcttmp[5] = value.length();
			
			objcttmp[1] = dcmDic.getDiscription(i);
			objcttmp[2] = value;
			
			if(i== AC_Tag.ItemDelimitationItem)
				continue;
			
			if(i==AC_Tag.Item) {
				
				objcttmp[0] =  sItemMargein+"("+String.format("%08X", i).substring(0, 4)+","+ String.format("%08X", i).substring(4, 8)+")";
				objcttmp[5] = value;
				itemTag = objcttmp;
				output.add(objcttmp);
				continue;
			}
			
			
			if(m_SequenceMap.containsKey(i)) {
				output.add(objcttmp);
				
				/*Object[] TagItem  = {Sqence+"(FFFF,E000)","",1,"Undefined","Item",""};
				output.add(TagItem);*/
				List<Object[]> tmpListOb = m_SequenceMap.get(i).buildTableInfo(iStep+2);
				//output.add(m_SqenceTable.get(i).getDicomHeaderFormat(AC_Tag.Item));
				
				
				output.addAll(tmpListOb);
				Object[] TagItemDelimitationItem  = {sMargein+"(FFFF,E00D)","",1,"Undefined","SequenceDelimitationItem",""};
				output.add(TagItemDelimitationItem);
				
				continue;
			}
			output.add(objcttmp);
		}
		
		if(itemTag!=null) {
			Object[] TagItemDelimitationItem  = {sItemMargein+"(FFFF,E00D)","",1,"Undefined","ItemDelimitationItem",""};
			output.add(TagItemDelimitationItem);
		}
		return output;
	}
	
	public void printInfo()
	{

		Set<Integer> keyset = m_attirbutes.keySet();
		Iterator<Integer> linkitr = keyset.iterator();
		
		while(linkitr.hasNext())
		{
			int tmpTag = linkitr.next();
			
			String[] tmp  = m_attirbutes.get(tmpTag);

	//	logger.info(String.format(input + "TAG : %08x  VR : %s Value : %s", tmpTag,AC_VR.getVRName(Integer.parseInt(tmp[0])),tmp[1]));
			System.out.println(String.format("TAG : %08x  VR : %s Value : %s", tmpTag,AC_VR.getVRName(Integer.parseInt(tmp[0])),tmp[1]));
			
		
			if(this.isSequnce(tmpTag))
			{
				AC_DcmStructure tmpSequnce = getSequenceValue(tmpTag);
				tmpSequnce.printInfo();
			}
	
		}

	}
	
	public void createNewStruES(File inJPG, String sStudyUID, String SeriesUID)
	{
		
		String inDate = new java.text.SimpleDateFormat("yyyyMMdd")
				.format(new java.util.Date());
		String inTime = new java.text.SimpleDateFormat("HHmmss.SSSSS")
				.format(new java.util.Date());
		
		int iTmpTag = AC_Tag.FileMetaInformationGroupLength;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), "198" });

		iTmpTag = AC_Tag.FileMetaInformationVersion;
		
//		System.out.println("VR : "+AC_VR.getVRName(AC_DicomDictionary.getTagVR(iTmpTag)) );
	//	System.out.println(AC_DicomDictionary.isSetup());
		
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), "0001" });
		
		iTmpTag = AC_Tag.MediaStorageSOPClassUID;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), "1.2.840.10008.5.1.4.1.1.7" });
		
		iTmpTag = AC_Tag.MediaStorageSOPInstanceUID;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), sStudyUID+".1.1" });
		
		iTmpTag = AC_Tag.TransferSyntaxUID;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), AC_DCMStandard.TransferSyntaxUID_LittleRaiden });
		
		iTmpTag = AC_Tag.ImplementationClassUID;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)),   sStudyUID+".1.1" });
		iTmpTag = AC_Tag.SOPClassUID;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)),  "1.2.840.10008.5.1.4.1.1.7" });
		
		iTmpTag = AC_Tag.SOPInstanceUID;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)),  sStudyUID+".1.1" });
		
		iTmpTag = AC_Tag.StudyDate;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), inDate});
		iTmpTag = AC_Tag.StudyTime;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), inTime});
		
		iTmpTag = AC_Tag.AccessionNumber;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), ""});
		
		iTmpTag = AC_Tag.Modality;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), "ES"});
		
		iTmpTag = AC_Tag.ConversionType;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), "DV"});
		
		iTmpTag = AC_Tag.ReferringPhysiciansName;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), ""});
		
		iTmpTag = AC_Tag.PatientsName;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), ""});
		
		iTmpTag = AC_Tag.PatientID;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), ""});
		
		iTmpTag = AC_Tag.PatientsBirthDate;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), ""});
		
		iTmpTag = AC_Tag.PatientsSex;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), ""});
		
		iTmpTag = AC_Tag.StudyInstanceUID;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), sStudyUID});
		
		iTmpTag = AC_Tag.SeriesInstanceUID;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), SeriesUID});
		
		
		iTmpTag = AC_Tag.StudyID;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), ""});
		
		
		iTmpTag = AC_Tag.SeriesNumber;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), ""});
		
		
		iTmpTag = AC_Tag.InstanceNumber;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), ""});
		
		iTmpTag = AC_Tag.SamplesperPixel;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), "3"});
		
		iTmpTag = AC_Tag.PhotometricInterpretation;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), "RGB"});
		
		iTmpTag = AC_Tag.Rows;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), ""});
		
		iTmpTag = AC_Tag.Columns;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), ""});
		
		iTmpTag = AC_Tag.BitsAllocated;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), "8"});
		
		
		iTmpTag = AC_Tag.BitsStored;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), "8"});
		
		iTmpTag = AC_Tag.HighBit;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), "7"});
		
		iTmpTag = AC_Tag.PixelRepresentation;
		m_attirbutes.put(iTmpTag,
				new String[] {Integer.toString(AC_DicomDictionary.getTagVR(iTmpTag)), "0"});
		
		byte[] bPixeldata = JPG2Bytes(inJPG);
		
		setPixelData(bPixeldata, AC_VR.OB);
		
	
		
	
		
	
	}
	
	 private  byte[] JPG2Bytes(File JPGFile)
	{
		
		
    	byte[] output = null;
		try {
			BufferedImage jpgImage = ImageIO.read(JPGFile);
			
			
			
			
			
			int iWidth = jpgImage.getWidth();
        	int iHeight = jpgImage.getHeight();
        	
        	changeAttribute(AC_Tag.Rows, Integer.toString(iHeight));
        	
        	changeAttribute(AC_Tag.Columns, Integer.toString(iWidth));
        	output = new byte[iWidth*iHeight*3];
        	
        	for(int i=0; i<iWidth;i++)
    		{
    			for(int j=0; j<iHeight;j++)
    			{
    				int iTmpRGB = jpgImage.getRGB(i,j);
    				int iByteIdx =  (j*iWidth+i)*3;
    				
    				output[iByteIdx+2] = (byte) (iTmpRGB & 0xff);
    				output[iByteIdx+1]  =  (byte) ((iTmpRGB & 0xff00) >> 8);
    				output[iByteIdx+0]  = (byte) ((iTmpRGB & 0xff0000) >> 16);
    			}
    		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;

	}
	
	
	private String doCreateUID() {
		String inDate = new java.text.SimpleDateFormat("yyyyMMdd")
				.format(new java.util.Date());
		String inTime = new java.text.SimpleDateFormat("HHmmss")
				.format(new java.util.Date());
		
		String time = inTime.replace(".", "");
		UUID uuid = UUID.randomUUID();
		byte[] b17 = new byte[9];
		fill(b17, 1, uuid.getMostSignificantBits());
		// fill(b17, 9, uuid.getLeastSignificantBits());
		return "1.2.410.301000" + '.' + inDate + time + new BigInteger(b17);
	}
	
	private void fill(byte[] bb, int off, long val) {
		int i = off;
		for (int shift = 56; shift >= 0; shift -= 8) {
			bb[i] = ((byte) (int) (val >>> shift));
			i++;
		}
	}

	
	public void AlianAttirbutes(boolean inSQ)
	{
		Set<Integer> keyset = m_attirbutes.keySet();
		Iterator<Integer> tagItr = keyset.iterator();
		
		
		LinkedHashMap<Integer, String[]> tmpAttri
		= new LinkedHashMap<Integer, String[]>() ;
		
		if(inSQ)
			tmpAttri.put(AC_Tag.Item, new String[] {Integer.toString(AC_VR.Undefined), "-1"});

		while(tagItr.hasNext())
		{
			
				
			
			
			int tmpTag = tagItr.next();
			
			if(tmpTag==AC_Tag.Item || tmpTag==AC_Tag.ItemDelimitationItem || tmpTag == AC_Tag.SequenceDelimitationItem)
				continue;
			String[] tmpVRnValue  = m_attirbutes.get(tmpTag);
			String sVR = tmpVRnValue[0];
			String sValue = tmpVRnValue[1];
			int iVR = Integer.parseInt(sVR);
			
		//	if(tmpTag.)
			
			if(Integer.parseInt(sVR)==AC_VR.Undefined)
			{
				iVR = AC_DicomDictionary.getTagVR(tmpTag);
			}
			
			if(m_SequenceMap.get(tmpTag)==null)
			{
				String[] tmpInput = {Integer.toString(AC_VR.Undefined), sValue };
				tmpAttri.put(tmpTag, tmpInput);
			}else
			{
				
				String[] tmpInput = {Integer.toString(AC_VR.Undefined), "-1" };
				tmpAttri.put(tmpTag, tmpInput);
				m_SequenceMap.get(tmpTag).AlianAttirbutes(true);
			}
		}
		

		if(inSQ)
			tmpAttri.put(AC_Tag.ItemDelimitationItem, new String[] {Integer.toString(AC_VR.Undefined), "-1"});

		
		m_attirbutes.clear();
		m_attirbutes.putAll(tmpAttri); 
		
	}
}