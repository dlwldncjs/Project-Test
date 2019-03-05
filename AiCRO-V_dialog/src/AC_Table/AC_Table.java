package AC_Table;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import AC_Dialog.AC_DCMFolderStructur;

/** 
 * TableToolTipsDemo is just like TableDemo except that it
 * sets up tool tips for both cells and column headers.
 */
public class AC_Table extends JPanel {
	AC_DCMFolderStructur m_dcmFS = null;


	JTable m_table = null;

	AbstractTableModel m_TableValue = null;

	protected String[] columnToolTips = {null,
			null,
			"The person's favorite sport to participate in",
			"The number of years the person has played the sport",
	"If checked, the person eats no meat"};

	public AC_Table(AbstractTableModel sFilepath ) {

		super(new GridLayout(1,0));

		this.setBackground(Color.GRAY);
		
		m_TableValue = sFilepath;

		m_table = new JTable();

		m_table.setModel(m_TableValue);

		m_table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		m_table.setFillsViewportHeight(true);

		JScrollPane scrollPane = new JScrollPane(m_table);

		add(scrollPane);

	}
	
	public void setColumSize(int iColumnNum, int iWidth) {
		m_table.getColumnModel().getColumn(iColumnNum).setPreferredWidth(iWidth);

	}
	
	public JTable getTableComponent() {
		return m_table;
	}
	
	public void addEvent(MouseListener a) {
		m_table.addMouseListener(a);
	}

}
