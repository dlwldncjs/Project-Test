import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import AC_DicomData.DicomIO.AC_DcmStructure;
import AC_DicomData.DicomIO.AC_DicomReader;
import AC_DicomData.DicomIO.AC_Tag;
import AC_Viewer.Environment;
import AC_Viewer.ViewerMain;

public class strat {
	

	public static void main(String[] args) throws IOException
	{
	
		/*String lineSeperator = System.getProperty("line.separator");
		
		System.out.print("kkk"+lineSeperator+"sdfe");*/
		
	//	splitFiles();
		
		/*AC_DicomReader tmp = new AC_DicomReader("D:\\98_data\\99_TestData\\456456\\10001.dcm");
		AC_DcmStructure tmp2 =tmp.getAttirbutes2();*/
		
		double[] tmnp = new double[2];
		for(double tmp : tmnp)
			System.out.println(tmp);

		//tmp2.printInfo("");
		
		
	}
	
	public static HashMap<String ,List<File>> splitFiles() throws IOException
	{
		HashMap<String, List<File>> output = new HashMap<>();
		HashMap<String, List<double[]>> orient = new HashMap<>();
		
		
		
		File[] dir  = new File("D:\\98_data\\99_TestData\\LSC").listFiles();
		
	
		List<File> tmpfile2 = new ArrayList<>();
		
		List<File> tmpfile3 = new ArrayList<>();
		
		for(File tmpFile : dir)
		{
			AC_DicomReader dcmIO = new AC_DicomReader();
			dcmIO.readDCMFile(tmpFile);
			String sSereis = dcmIO.getAttirbutes().getTagValue(AC_Tag.SeriesNumber);
			String[] sOrient = dcmIO.getAttirbutes().getTagValue(AC_Tag.ImageOrientationPatient).split("\\\\");
			for(String tmpStringttt : sOrient)
				System.out.println(tmpStringttt);
			double[] dOrient = new double[6];
			
			for(int i=0; i<sOrient.length;i++)
			{
				dOrient[i] = Double.parseDouble(sOrient[i]);
			}
			
			for(double tmpStringttt : dOrient)
				System.out.println(tmpStringttt);
			
			if(!output.containsKey(sSereis))
			{	
				List<File> tmpfile1 = new ArrayList<>();
				List<double[]> tmpfile21 = new ArrayList<>();
			
				tmpfile1.add(tmpFile);
				tmpfile21.add(dOrient);
				orient.put(sSereis, tmpfile21);
				output.put(sSereis, tmpfile1);
			}else
			{
				List<File> tmpfile1  = output.get(sSereis);
				tmpfile1.add(tmpFile);
				
				
				
				List<double[]> tmpfile21 = orient.get(sSereis);
				tmpfile21.add(dOrient);
				//orient.put(sSereis, tmpfile21);
			}
			
			
		}
		
		String[] keySet = orient.keySet().toArray(new String[output.size()]);
		
		for(String key : keySet)
		{
			System.out.println("key : "+ key);
			int idx = 1;
			for(double[] tmpFile : orient.get(key))
			{
				System.out.println(idx++);
				for(double tmpd : tmpFile)
				    System.out.print(tmpd+ " ");//Math.toDegrees(Math.acos(tmpd))-90+" ");
				System.out.println(" ");
				for(double tmpd : tmpFile)
				    System.out.print(Math.toDegrees(Math.acos(tmpd))+" ");
				System.out.println(" ");
				
				double[] tmp1 = new double[3];
				double[] tmp2 = new double[3];
				for(int i=0; i<3;i++)
				{
					tmp1[i] = tmpFile[i];
					tmp2[i] = tmpFile[i+3];
				}
				System.out.println(getOrientation(tmp1));
				System.out.println(getOrientation(tmp2));
			}
			
		}
	
		
		
		

		return output;
		
	}
	
	public static String getOrientation(double[] ipnut)
	{

        String orientationX = ipnut[0] < 0 ? "R" : "L";
        String orientationY = ipnut[1] < 0 ? "A" : "P";
        String orientationZ = ipnut[2] < 0 ? "F" : "H";
        
        double absX = Math.abs(ipnut[0]);
        double absY = Math.abs(ipnut[1]);
        double absZ = Math.abs(ipnut[2]);
        
        String sString = "";
        
        int i;
        
        for (i=0; i<3; i++) 
        {
                if (absX>0.4 && absX>absY && absX>absZ) {
                //	System.out.println(absX);
                		sString += orientationX;
                        absX=0;
                }
                else if (absY>0.4 && absY>absX && absY>absZ) {
                	sString +=  orientationY;
                        absY=0;
                }
                else if (absZ>0.4 && absZ>absX && absZ>absY) {
                	sString += orientationZ;
                       absZ=0;
                }
                else
                	break;
                //sString[i] = "\0";
        }
	
	
	
	  //  System.out.print(sString);//Math.toDegrees(Math.acos(tmpd))+" ");
	    return sString;
	}
	
	public static final String getOrientation(double[] orientation, boolean quadruped) {
        StringBuilder strbuf = new StringBuilder();
      //  if (orientation != null && orientation.length == 3)
        {
            String orientationX = orientation[0] < 0 ? (quadruped ? "Rt" : "R") : (quadruped ? "Le" : "L"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            String orientationY = orientation[1] < 0 ? (quadruped ? "V" : "A") : (quadruped ? "D" : "P"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            String orientationZ = orientation[2] < 0 ? (quadruped ? "Cd" : "F") : (quadruped ? "Cr" : "H"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

            double absX = Math.abs(orientation[0]);
            double absY = Math.abs(orientation[1]);
            double absZ = Math.abs(orientation[2]);
            for (int i = 0; i < 3; ++i) {
                if (absX > 0.0001 && absX > absY && absX > absZ) {
                    strbuf.append(orientationX);
                    absX = 0;
                } else if (absY > 0.0001 && absY > absX && absY > absZ) {
                    strbuf.append(orientationY);
                    absY = 0;
                } else if (absZ > 0.0001 && absZ > absX && absZ > absY) {
                    strbuf.append(orientationZ);
                    absZ = 0;
                }
            }
        }
        return strbuf.toString();
    }

	


	

}
