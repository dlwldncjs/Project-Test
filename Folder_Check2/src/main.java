import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class main {
	@SuppressWarnings("null")
	public static void main(String[] args) {
		String temp1 = null;
		String[] temp2 = null;
		File f = new File("E:\\김홍규 new_chest(추출)");
		File[] Folder_List = f.listFiles();
		BufferedWriter bw = null;
		

		
		

		try {
			bw = Files.newBufferedWriter(Paths.get("C:\\Users\\JW\\Desktop\\chest.csv"));
			List<List<String>> allData = readCSV();
			Charset.forName("UTF-8");
			for (File file : Folder_List) {
				if (file.isDirectory()) {
					temp2 = file.getName().split("_");
					bw.write(temp2[0] + "," + temp2[1] + "," + temp2[2]);
					bw.newLine();
				}
			}
			
			// System.out.println(csv_List);
			/*for (j = 0; j < csv_List.size(); j++) {
				idx_List.remove(csv_List.get(j));
			}*/
			// System.out.println(allData.get(0).get(0));
			/*for (String idx : idx_List) {
				bw.write(idx + "-" + i);
				bw.newLine();
				i++;
			}*/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	
	private static List<List<String>> readCSV() {
		List<List<String>> ret = new ArrayList<List<String>>();
		BufferedReader br = null;

		try {
			br = Files.newBufferedReader(Paths.get("C:\\Users\\JW\\Desktop\\chest.csv"));
			Charset.forName("UTF-8");
			String line = "";

			while ((line = br.readLine()) != null) {
				// CSV 1행을 저장하는 리스트
				List<String> tmpList = new ArrayList<String>();
				String array[] = line.split(",");
				// 배열에서 리스트 반환
				tmpList = Arrays.asList(array);
				ret.add(tmpList);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
}

