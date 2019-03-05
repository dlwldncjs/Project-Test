package AC_Dialog;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import SYB_LIB.SYBFileIO;

public class AC_DCMFolderStructur {
	HashMap<String, ArrayList<String>> m_FoderTable = new HashMap<String, ArrayList<String>>();
	String m_sRootPath = "";
	
	public void addRootFolder(String sPath) {
		m_sRootPath = sPath;
		
		File[] fFirstPath = new File(sPath).listFiles();
		
		for(File fFiresTmp : fFirstPath) {
			File[] fSecndFile = fFiresTmp.listFiles();
			ArrayList<String> sSecondFolderName = new ArrayList<String>();
			
			for(File fSecondTmp : fSecndFile)
				sSecondFolderName.add(fSecondTmp.getName());
			
			sortFisrtIntegerName(sSecondFolderName);
			m_FoderTable.put(fFiresTmp.getName(), sSecondFolderName);
		}
	}
	
	private void sortFisrtIntegerName(ArrayList<String> aInput) {
		Collections.sort(aInput, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				// TODO Auto-generated method stub

				int iObj1 =  Integer.parseInt(o1.split("_")[0]);
				int iObj2 =  Integer.parseInt(o2.split("_")[0]);

				return (iObj1-iObj2);// ? 1 :0;//o1[1].toString().compareTo(o2[1].toString());
			}}
				);

	}
	
	public String[] getPathList(String sCaseNane) {
		ArrayList<String> sFileList = m_FoderTable.get(sCaseNane);
		String[] sOutput = new String[sFileList.size()];
		for(int i=0; i<sFileList.size(); i++)
		{
			sOutput[i] = m_sRootPath+File.separator+sCaseNane+File.separator+sFileList.get(i);
		}
		return sOutput;
		
	}
	
	public String getBuilFilePath(String fileName) {
		return m_sRootPath+File.separator+fileName;
	}
	
	public void bulidTableData(List<Object[]> tableData, int iColumSize ) {
		
		ArrayList<String> keyList = new ArrayList<String>(m_FoderTable.keySet());
		
		Collections.sort(keyList);
		 for(String tmp1 : keyList) {
			 ArrayList<String> alSencondFfile = m_FoderTable.get(tmp1);
			 for(String tmp2 : alSencondFfile) {
				 Object[] tmp = new Object[iColumSize];
				 
				 tmp[0] = tmp1;
				 tmp[1] = tmp2;
				 tmp[2] = "";
				
				 File file = new File(m_sRootPath+File.separator+tmp1+File.separator+tmp2);
				 tmp[3] =  SYBFileIO.fileNUM(file);
				 tmp[4] = new Boolean(false);
				 tableData.add(tmp);
			 }
		 }
	}
}
