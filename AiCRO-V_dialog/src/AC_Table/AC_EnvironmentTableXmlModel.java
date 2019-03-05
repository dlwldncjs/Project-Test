package AC_Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import AC_Viewer.Environment;

public class AC_EnvironmentTableXmlModel extends AbstractTableModel  {
		private String[] columnNames = {"Name", "Value"};

		private List<Object[]> tableData = new ArrayList<Object[]>();
		
		public void buildEnvInfo(String sTabTitle) throws IOException {
			tableData = Environment.buildTableInfo("ini");
		}
		
		public void buildBooleanCombo(String sTabTitle) throws IOException {
			tableData = Environment.buildTableInfo("boolean");
		}
		
		public void buildEnvCon(String sTabTitle) throws IOException {
			tableData = Environment.buildTableInfo("con");
		}
		
		public void buildEnvPath(String sTabTitle) throws IOException {
			tableData = Environment.buildTableInfo("path");
		}
		
		public void buildEnvSetting(String sTabTitle) throws IOException {
			tableData = Environment.buildTableInfo("window");
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
			if (col < 1) {
				return false;
			} else {
				return true;
			}
		}

		public void setValueAt(Object value, int row, int col) {

			Object[] tmp = null;
			tmp = tableData.get(row);
			tmp[col] = value;

			tableData.set(row, tmp);

			fireTableCellUpdated(row, col);

		}
}
