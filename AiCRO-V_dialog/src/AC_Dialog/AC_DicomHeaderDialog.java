package AC_Dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import AC_Table.AC_AddHeaderXml;
import AC_Table.AC_DelHeaderXml;
import AC_Table.AC_DicomTableModel;
import AC_Table.AC_Table;
import AC_Table.AC_HeaderXmlModel;

public class AC_DicomHeaderDialog extends JFrame implements MouseListener {
	private AC_Table m_Table = null;
	private AC_Table m_selectTable = null;
	private JScrollPane m_sbTNBScroll;
	Object input_data[] = new Object[6];
	private String[] columnNames = { "Tag ID", "Description", "Value", "VR", "VM", "Length"};
	DefaultTableModel dmodel = new DefaultTableModel(null, columnNames);
	AC_DicomTableModel model = new AC_DicomTableModel();
	String xml_tagdata = null;

	public AC_DicomHeaderDialog(String input) {
		// TODO Auto-generated constructor stub
		init(input);
	}

	private void init(String filepath) {
		this.setLayout(new BorderLayout());
		this.setLocation(200, 40);
		this.setSize(1400, 1000);
		this.setBackground(Color.GRAY);

		try {
			model.buildDCMInfo(filepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		m_Table = new AC_Table(model);
		m_selectTable = new AC_Table(model);

		JPanel tablepanel = new JPanel(new GridLayout(1, 1));
		tablepanel.add(m_Table);
		tablepanel.add(m_selectTable);
		m_Table.setColumSize(0, 110);
		m_Table.setColumSize(1, 174);
		m_Table.setColumSize(2, 300);
		m_Table.setColumSize(3, 30);
		m_Table.setColumSize(4, 30);
		m_Table.setColumSize(5, 30);

		m_Table.getTableComponent().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		m_selectTable.getTableComponent().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		m_Table.getTableComponent().addMouseListener(this);
		m_selectTable.getTableComponent().addMouseListener(this);
////////////////////////////// SELECT TABLE RESET
		m_selectTable.getTableComponent().setModel(dmodel);

		m_selectTable.setColumSize(0, 110);
		m_selectTable.setColumSize(1, 174);
		m_selectTable.setColumSize(2, 300);
		m_selectTable.setColumSize(3, 30);
		m_selectTable.setColumSize(4, 30);
		m_selectTable.setColumSize(5, 30);

////////////////////////////// SEARCH
		TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(m_Table.getTableComponent().getModel());
		JTextField jtfFilter = new JTextField();
		m_Table.getTableComponent().setRowSorter(rowSorter);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(jtfFilter, BorderLayout.CENTER);
		add(panel, BorderLayout.NORTH);
		add(tablepanel, BorderLayout.CENTER);

		jtfFilter.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				String text = jtfFilter.getText();
				if (text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				} else {
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				String text = jtfFilter.getText();
				if (text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				} else {
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new UnsupportedOperationException("error");
			}
		});

////////////////////////////// TABLE LOAD
		File xmlfile = new File("C:\\Users\\JW\\Desktop\\Workspace\\HeaderXml.xml");
		if (xmlfile.exists() == true) {
			loadxml();
		} else {
			input_data[0] = m_Table.getTableComponent().getValueAt(0, 0);
			xml_tagdata = input_data[0].toString();
			new AC_HeaderXmlModel(xml_tagdata);
			loadxml();
		}
	}

	public void loadxml() {
		DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder xmlBuilder;
		int table_rowcount = m_Table.getTableComponent().getRowCount();
		Object tag_data[] = new Object[table_rowcount];
		try {
			File xmlfile = new File("C:\\Users\\JW\\Desktop\\Workspace\\HeaderXml.xml");
			if (xmlfile.exists() == true) {
				xmlBuilder = xmlFactory.newDocumentBuilder();
				Document doc = xmlBuilder.parse(xmlfile);
				Element rootElement = doc.getDocumentElement();
				NodeList list = rootElement.getChildNodes();
				xmlFactory.setIgnoringElementContentWhitespace(true);
				if (list.getLength() > 0) {
					for (int i = 0; i < list.getLength(); i++) {
						NodeList childlist = list.item(i).getChildNodes();
						if (childlist.getLength() > 0) {
							for (int u = 0; u < childlist.getLength(); u++) {
								if (Node.TEXT_NODE != childlist.item(u).getNodeType()) {
									if (childlist.item(u).getTextContent() != null) {
										tag_data[u] = childlist.item(u).getTextContent();
										for (int j = 0; j < table_rowcount; j++) {
											if (m_Table.getTableComponent().getValueAt(j, 0).equals(tag_data[u])) {
												for (int k = 0; k < 6; k++) {
													input_data[k] = m_Table.getTableComponent().getValueAt(j, k);
												}
												dmodel.addRow(input_data);
											}
										}
									}
								}
							}
						}
					}
				}
			}

		} catch (Exception q) {
			q.printStackTrace();
		}

	}

	public void addrow() {
		int l_row = m_Table.getTableComponent().getSelectedRow();
		for (int i = 0; i < 6; i++) {
			input_data[i] = m_Table.getTableComponent().getValueAt(l_row, i);
		}
		xml_tagdata = input_data[0].toString();
		dmodel.addRow(input_data);
		new AC_AddHeaderXml(xml_tagdata);
	}

	public void removerow() {
		int r_row = m_selectTable.getTableComponent().getSelectedRow();
		for (int i = 0; i < 6; i++) {
			input_data[i] = m_selectTable.getTableComponent().getValueAt(r_row, i);
		}
		xml_tagdata = input_data[0].toString();
		dmodel.removeRow(r_row);
		new AC_DelHeaderXml(xml_tagdata);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			addrow();
		}
		if (e.getButton() == MouseEvent.BUTTON3) {
			removerow();
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
}
