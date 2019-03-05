package AC_Table;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class AC_EnvrionmentXmlModel {
	public AC_EnvrionmentXmlModel(String array[]) {
		try {
			DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder xmlBuilder;
			xmlBuilder = xmlFactory.newDocumentBuilder();
			Document doc = xmlBuilder.newDocument();
			Element nHeader = doc.createElement("Value");
			nHeader.setAttribute("Envrionment", "Value");

			Element element1 = doc.createElement("INI_MOUSE_MODE");
			Element element2 = doc.createElement("SaveWindowCenter");
			Element element3 = doc.createElement("SaveWindowWidth");
			Element element4 = doc.createElement("flagStandAlone");
			Element element5 = doc.createElement("flagOvalROI");
			Element element6 = doc.createElement("flagLienROI");
			Element element7 = doc.createElement("flagAngleROI");
			Element element8 = doc.createElement("FontSize");
			Element element9 = doc.createElement("nROIlimit");
			Element element10 = doc.createElement("CheckBoxSize");
			Element element11 = doc.createElement("ROIStokeSize");
			Element element12 = doc.createElement("WebFileDownloadPath");
			Element element13 = doc.createElement("AiCROUploadUrl");
			
			Text T1 = doc.createTextNode(array[0]);
			Text T2 = doc.createTextNode(array[1]);
			Text T3 = doc.createTextNode(array[2]);
			Text T4 = doc.createTextNode(array[3]);
			Text T5 = doc.createTextNode(array[4]);
			Text T6 = doc.createTextNode(array[5]);
			Text T7 = doc.createTextNode(array[6]);
			Text T8 = doc.createTextNode(array[7]);
			Text T9 = doc.createTextNode(array[8]);
			Text T10 = doc.createTextNode(array[9]);
			Text T11 = doc.createTextNode(array[10]);
			Text T12 = doc.createTextNode(array[11]);
			Text T13 = doc.createTextNode(array[12]);
			
			element1.appendChild(T1);
			element2.appendChild(T2);
			element3.appendChild(T3);
			element4.appendChild(T4);
			element5.appendChild(T5);
			element6.appendChild(T6);
			element7.appendChild(T7);
			element8.appendChild(T8);
			element9.appendChild(T9);
			element10.appendChild(T10);
			element11.appendChild(T11);
			element12.appendChild(T12);
			element13.appendChild(T13);
			nHeader.appendChild(element1);
			nHeader.appendChild(element2);
			nHeader.appendChild(element3);
			nHeader.appendChild(element4);
			nHeader.appendChild(element5);
			nHeader.appendChild(element6);
			nHeader.appendChild(element7);
			nHeader.appendChild(element8);
			nHeader.appendChild(element9);
			nHeader.appendChild(element10);
			nHeader.appendChild(element11);
			nHeader.appendChild(element12);
			nHeader.appendChild(element13);
			doc.appendChild(nHeader);
			
			////////// SAVE
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult resultxml = new StreamResult(
					new FileOutputStream(new File("C:\\Users\\JW\\Desktop\\Workspace\\EnvXml.xml")));
			transformer.transform(source, resultxml);
			System.out.println("Success Create Environment XML Model");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
