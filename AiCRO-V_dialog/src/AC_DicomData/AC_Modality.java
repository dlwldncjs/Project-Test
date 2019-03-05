package AC_DicomData;

public class AC_Modality {
	
	public final static int UNKNOWN= -1;
	public final static int CT = 0;
	public final static int MR = 1;
	public final static int US = 2;
	public final static int EN = 3;
	public final static int PT = 4;
	


	
	public static int chkModality(String sinpput)
	{
		String sInputUpper = sinpput.toUpperCase();
		
		if(sInputUpper.contains("CT"))
			return CT; 
		if(sInputUpper.contains("MR"))
			return MR; 
		if(sInputUpper.contains("US"))
			return US; 
		if(sInputUpper.contains("PT"))
			return PT; 
		if(sInputUpper.contains("EN"))
			return EN; 
		

		return UNKNOWN; 
	
	}
	
	public static String parseSting(int iModality)
	{
		switch (iModality) 
		{
		case 0:
			return "CT";
		case 1:
			return "MR";
		case 2:
			return "US";
		case 3:
			return "EN";
		case 4:
			return "PT";

		default :
			return "UNKNOWN";
			
		}
		

	}
}
