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

public class AC_AddHeaderXml {
	public AC_AddHeaderXml(String xml_tagdata) {
		try {
			DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder xmlBuilder;
			File xmlfile = new File("C:\\Users\\JW\\Desktop\\Workspace\\HeaderXml.xml");
			xmlBuilder = xmlFactory.newDocumentBuilder();
			Document doc = xmlBuilder.parse(xmlfile);
			Element rootElement = doc.getDocumentElement();
			Element nHeader = doc.createElement("Tag_ID");
			nHeader.setAttribute("id", xml_tagdata);

			Element nTid = doc.createElement("Tag_ID");

			Text Tid = doc.createTextNode(xml_tagdata);


			nTid.appendChild(Tid);

			nHeader.appendChild(nTid);

			rootElement.appendChild(nHeader);
			
			//////////SAVE
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult resultxml = new StreamResult(
					new FileOutputStream(new File("C:\\Users\\JW\\Desktop\\Workspace\\HeaderXml.xml")));
			transformer.transform(source, resultxml);
			System.out.println("\nXML Add Success");

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
