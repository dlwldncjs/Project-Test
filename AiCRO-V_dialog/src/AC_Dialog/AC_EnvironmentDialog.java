package AC_Dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import AC_Table.AC_Table;
import AC_Table.AC_EnvironmentTableXmlModel;
import AC_Table.AC_EnvrionmentXmlModel;
import AC_Viewer.Environment;
import AC_Viewer.ViewerMain;

public class AC_EnvironmentDialog extends JFrame {

	private JTabbedPane m_TabPanel = new JTabbedPane();
	private AC_Table m_Table = null;
	private static File xmlfile = new File("C:\\Users\\JW\\Desktop\\Workspace\\EnvXml.xml");
	public static Object tag_data[] = new Object[13];
	public static String String_tag_data[] = new String[13];
	public static Boolean boolean_data[] = new Boolean[4];
	private static DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
	private static DocumentBuilder xmlBuilder;
	public static JButton button = new JButton("SAVE");
	
	Object env_data[] = new Object[13];
	String env_xmldata[] = new String[13];

	public AC_EnvironmentDialog(String input) {
		// TODO Auto-generated constructor stub
		init(input);
	}

	private void init(String filepath) {
		this.setLocation(500, 100);
		this.setSize(800, 1000);
		this.setBackground(Color.GRAY);
		this.setLayout(new BorderLayout());

		AC_EnvironmentTableXmlModel model = new AC_EnvironmentTableXmlModel();
		AC_EnvironmentTableXmlModel model1 = new AC_EnvironmentTableXmlModel();
		AC_EnvironmentTableXmlModel model2 = new AC_EnvironmentTableXmlModel();
		AC_EnvironmentTableXmlModel model3 = new AC_EnvironmentTableXmlModel();
		AC_EnvironmentTableXmlModel model4 = new AC_EnvironmentTableXmlModel();

		try {
			model.buildEnvInfo("ini");
			model1.buildBooleanCombo("boolean");
			model2.buildEnvCon("con");
			model3.buildEnvPath("path");
			model4.buildEnvSetting("window");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m_Table = new AC_Table(model);
		AC_Table tmp = new AC_Table(model);
		AC_Table tmp1 = new AC_Table(model1);
		AC_Table tmp2 = new AC_Table(model2);
		AC_Table tmp3 = new AC_Table(model3);
		AC_Table tmp4 = new AC_Table(model4);

		m_TabPanel.add("Ini", tmp);
		m_TabPanel.add("Roi", tmp1);
		m_TabPanel.add("Size", tmp2);
		m_TabPanel.add("Path", tmp3);
		m_TabPanel.add("Setting", tmp4);

		this.add(m_TabPanel);
		
		m_Table.setColumSize(0, 110);
		m_Table.setColumSize(1, 30);
		m_Table.getTableComponent().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.add((button), BorderLayout.NORTH);

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i = 0;
				for (int j = 0; j < tmp.getTableComponent().getRowCount(); j++) {
					env_data[i] = tmp.getTableComponent().getValueAt(j, 1);
					i++;
				}
				for (int k = 0; k < tmp1.getTableComponent().getRowCount(); k++) {
					env_data[i] = tmp1.getTableComponent().getValueAt(k, 1);
					i++;
				}
				for (int l = 0; l < tmp2.getTableComponent().getRowCount(); l++) {
					env_data[i] = tmp2.getTableComponent().getValueAt(l, 1);
					i++;
				}
				for (int m = 0; m < tmp3.getTableComponent().getRowCount(); m++) {
					env_data[i] = tmp3.getTableComponent().getValueAt(m, 1);
					i++;
				}
				for (i = 0; i < env_data.length; i++)
					env_xmldata[i] = env_data[i].toString();
				System.out.println("Success Load Environment XML");
				new AC_EnvrionmentXmlModel(env_xmldata);
				new ViewerMain(true, "");
				dispose();
				button.removeActionListener(this);				
			}
		});
	}

	public static void loadEnvxml() {
		try {
			if (xmlfile.exists() == true) {
				xmlBuilder = xmlFactory.newDocumentBuilder();
				Document doc = xmlBuilder.parse(xmlfile);
				Element rootElement = doc.getDocumentElement();
				NodeList list = rootElement.getChildNodes();
				xmlFactory.setIgnoringElementContentWhitespace(true);
				int j = 0;
				for (int i = 0; i < list.getLength(); i++) {
					NodeList childlist = list.item(i).getChildNodes();
					if (childlist.getLength() > 0) {
						for (int u = 0; u < childlist.getLength(); u++) {
							if (childlist.item(u).getTextContent() != null) {
								tag_data[j] = childlist.item(u).getTextContent();
								String_tag_data[j] = tag_data[j].toString();
								j++;
							}
						}
					}
				}
			}
			////// String -> Boolean
			for (int k = 0; k < 4; k++) {
				boolean_data[k] = Boolean.valueOf(String_tag_data[k+3]);
			}
			for(int k =0; k<4; k++) {
				
			}
		} catch (Exception q) {
			q.printStackTrace();
		}
	}

}
