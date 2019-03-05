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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AC_DelHeaderXml {
	DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder xmlBuilder;

	public AC_DelHeaderXml(String xml_tagdata) {
		try {
			xmlBuilder = xmlFactory.newDocumentBuilder();
			File xmlfile = new File("C:\\Users\\JW\\Desktop\\Workspace\\HeaderXml.xml");
			Document doc = xmlBuilder.parse(xmlfile);
			Element rootElement = doc.getDocumentElement();
			NodeList list = rootElement.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				NodeList childlist = list.item(i).getChildNodes();
				if (childlist.getLength() > 0) {
					for (int u = 0; u < childlist.getLength(); u++) {
						if (Node.TEXT_NODE != childlist.item(u).getNodeType()) {
							if (childlist.item(u).getTextContent() != null) {
								if(childlist.item(u).getTextContent().equals(xml_tagdata)) {
									rootElement.removeChild(childlist.item(u).getParentNode());
								}
							}
						}
					}
				}
			}
			//////////SAVE
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult resultxml = new StreamResult(
					new FileOutputStream(new File("C:\\Users\\JW\\Desktop\\Workspace\\HeaderXml.xml")));
			transformer.transform(source, resultxml);
			System.out.println("\nXML Delete Success");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
