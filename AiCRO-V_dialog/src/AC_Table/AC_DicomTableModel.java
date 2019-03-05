
package AC_Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import AC_DicomData.DicomIO.*;

public class AC_DicomTableModel extends AbstractTableModel  {
		private String[] columnNames = {"Tag ID", "Description", "Value", "VR", "VM", "Length"};

		private List<Object[]> tableData = new ArrayList<Object[]>();
		
		public void buildDCMInfo(String dcmPath) throws IOException {
			 AC_DicomReader dcmio = new AC_DicomReader(dcmPath);
			 AC_DcmStructure dcmInfo = new AC_DcmStructure();
			 dcmio.getTableDicomInfo(dcmInfo);
			 tableData = dcmInfo.buildTableInfo(0);	////////////////////////////////////2012-02-19 
			 dcmInfo.printInfo();
			 System.out.println("sdf");
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return tableData.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			Object[] data = tableData.get(row);
			return data[col];
		}

		public Class<? extends Object> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			if (col < 5) {
				return false;
			} else {
				return true;
			}
		}

		public void setValueAt(Object value, int row, int col) {

			Object[] tmp  =null;
			tmp = tableData.get(row);
			tmp[col] = value;

			tableData.set(row, tmp);

			fireTableCellUpdated(row, col);

		}

		private void printDebugData() {
			int numRows = getRowCount();
			int numCols = getColumnCount();

			for (int i=0; i < numRows; i++) {
				System.out.print("    row " + i + ":");
				for (int j=0; j < numCols; j++) {
				}
				System.out.println();
			}
			System.out.println("--------------------------");
		}
	

}
