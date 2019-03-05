package AC_Viewer;


import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;

public class Environment {
	
	
	public static final int g_nullVale = -12345;
	public static final boolean g_flagStandAlone = false;
	
	public static final int g_INI_MOUSE_MODE = ViewerMain.MM_IMAGE_WINDOW;
	public static final boolean g_flagOvalROI = true;
	public static final boolean g_flagLienROI = true;
	public static final boolean g_flagAngleROI = false;
	
	public static final int g_FontSize = 12;
	public static final int g_nROIlimit = 1;
	public static final int g_CheckBoxSize = 15;
	///ROI SETUP
	public static final float g_ROIStokeSize = (float)2.5;
	public static final Color g_ClrROINomarl = Color.GREEN;
	public static final Color g_ClrROIMouseON= Color.YELLOW;
	public static final Color g_ClrROISelected = Color.RED;
	public static final int g_ROIMouseOnRange = 20;
	
	public static final double[] g_IniWindowLevel = {50,400};
	
	
	
	public static final double[][] g_WindowSettingValue = {
			{200,1000},
			{31,95},
			{109,84},
			{-700,1500}
			
	};
	
	public static final double g_SaveWindowCenter = g_WindowSettingValue[1][0];
	public static final double g_SaveWindowWidth = g_WindowSettingValue[1][1];
	
	
	

	
	public static final String g_WebFileDownloadPath = "C:\\Users\\shin_yongbin\\AppData\\Local\\Temp\\PACS";
	//public static final String g_WebFileDownloadPath = "D:\\98_data\\03_AiCRO_Dev\\tst_CNS";
	






	public static final  String g_AiCROUploadUrl = 	  "https://study.aim-aicro.com";
	//XML  파일관련
	private static String XmlFilePath = DefultURL.URL_CONFIG;
	private static File fInputXML = new File(XmlFilePath);





	
	
	public static List<Object[]> buildTableInfo(String sTabName )
	{
		List<Object[]> output = new ArrayList<Object[]>();
		
		if(sTabName.toUpperCase().contains("INI"))
		{
			return getIniList();
		}
		
		
		return output;
	}
	private static List<Object[]> getIniList()
	{
		List<Object[]> output = new ArrayList<Object[]>();
		
		
		Object[] tmp1 = {"StandAlone", String.valueOf(g_flagStandAlone)};
		Object[] tmp2 = {"MouseMode", String.valueOf(g_INI_MOUSE_MODE)};
		Object[] tmp3 = {"WindowCenter", String.valueOf(g_IniWindowLevel[0])};
		Object[] tmp4 = {"WindowWidth", String.valueOf(g_IniWindowLevel[1])};
	
		output.add(tmp1);
		output.add(tmp2);
		output.add(tmp3);
		output.add(tmp4);
		
		
		return output;
	}
	
	
	
	
	
	public static void init()
	{
		Element nlAllElement =null;
			try {
				 nlAllElement = XMLReader(fInputXML);
			
				 System.out.println(" dd" + getBoolean(nlAllElement,"Ini","StandAlone"));
				 System.out.println(" dd" + getBoolean(nlAllElement,"Ini","StandAlone"));
				
				 
				 
				 				 
				 
				 
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
	
		
		
	}
	
	
	public static Element XMLReader(File fInput) throws ParserConfigurationException, SAXException, IOException  
	{
		File file = fInput;
		DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
		Document doc = docBuild.parse(file);
		doc.getDocumentElement().normalize();
		
	
		return	doc.getDocumentElement();
	}
	private static String getValue(Element nlRoot, String sFirstTag, String sSecond)
	{
		NodeList lsFirstTag = nlRoot.getElementsByTagName(sFirstTag);
		Element eFirstTag = (Element)lsFirstTag.item(0);
		NodeList lsSecondTag = eFirstTag.getElementsByTagName(sSecond);
		Element eSecondTag = (Element)lsSecondTag.item(0);
		Node nValue = eSecondTag.getFirstChild();
		String output = nValue.getNodeValue();

		//eFirstTag.getAttribute(sSecond);
		
		return output;
	}
	
	private static boolean getBoolean(Element nlRoot, String sFirstTag, String sSecond)
	{
		NodeList lsFirstTag = nlRoot.getElementsByTagName(sFirstTag);
		Element eFirstTag = (Element)lsFirstTag.item(0);
		NodeList lsSecondTag = eFirstTag.getElementsByTagName(sSecond);
		Element eSecondTag = (Element)lsSecondTag.item(0);
		Node nValue = eSecondTag.getFirstChild();
		String output = nValue.getNodeValue().trim();
		
	
		if(output.toUpperCase().equals("TRUE"))
			return true;

		//eFirstTag.getAttribute(sSecond);
		
		return false;
	}
	
	private static int getInteger(Element nlRoot, String sFirstTag, String sSecond)
	{
		NodeList lsFirstTag = nlRoot.getElementsByTagName(sFirstTag);
		Element eFirstTag = (Element)lsFirstTag.item(0);
		NodeList lsSecondTag = eFirstTag.getElementsByTagName(sSecond);
		Element eSecondTag = (Element)lsSecondTag.item(0);
		Node nValue = eSecondTag.getFirstChild();
		String output = nValue.getNodeValue();

		//eFirstTag.getAttribute(sSecond);
		
		return Integer.parseInt(output);
	}
	
	private static double getDouble(Element nlRoot, String sFirstTag, String sSecond)
	{
		NodeList lsFirstTag = nlRoot.getElementsByTagName(sFirstTag);
		Element eFirstTag = (Element)lsFirstTag.item(0);
		NodeList lsSecondTag = eFirstTag.getElementsByTagName(sSecond);
		Element eSecondTag = (Element)lsSecondTag.item(0);
		Node nValue = eSecondTag.getFirstChild();
		String output = nValue.getNodeValue();

		//eFirstTag.getAttribute(sSecond);
		
		return Double.parseDouble(output);
	}
	
	
	

}
