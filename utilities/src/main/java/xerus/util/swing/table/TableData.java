package xerus.util.swing.table;

import xerus.util.helpers.Row;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TableData extends AbstractTableModel {
	
	String[] columnNames;
	private List<Row> data;
	/**
	 * When true, you have to manually fire Data update events when modifying the table-data.<br>
	 * Exceptions are {@link #setColumnNames(String...)} and {@link #setData(List)}
	 */
	public boolean silent;
	
	public TableData(String... columnames) {
		data = new ArrayList<>();
		columnNames = columnames;
	}
	
	public void clearData() {
		data = new ArrayList<>();
		if (!silent)
			fireTableDataChanged();
	}
	
	public void setData(List<Row> newdata) {
		data = newdata;
	}
	
	public void setColumnNames(String... columnNames) {
		this.columnNames = columnNames;
		fireTableStructureChanged();
	}
	
	public String getColumnName(int col) {
		return columnNames[col];
	}
	
	public int getColumnCount() {
		return columnNames.length;
	}
	
	public int getRowCount() {
		return data.size();
	}
	
	public String getValueAt(int row, int col) {
		return data.get(row).get(col);
	}
	
	public void setValueAt(String value, int row, int col) {
		data.get(row).set(col, value);
		if (!silent)
			fireTableCellUpdated(row, col);
	}
	
	public void addRows(List<String[]> rows) {
		for (String[] value : rows)
			data.add(new Row(value));
		if (!silent)
			fireTableRowsInserted(data.size() - rows.size(), data.size() - 1);
	}
	
	public void addRow(String... value) {
		data.add(new Row(value));
		if (!silent)
			fireTableRowsInserted(data.size() - 1, data.size() - 1);
	}
	
	public void addRow(Object... value) {
		String[] insertData = new String[value.length];
		for (int i = 0; i < value.length; i++)
			insertData[i] = value[i].toString();
		data.add(new Row(insertData));
		if (!silent)
			fireTableRowsInserted(data.size() - 1, data.size() - 1);
	}
	
	public void replaceRow(int row, String... value) {
		data.set(row, new Row(value));
		if (!silent)
			fireTableRowsUpdated(row, row);
	}
	
	public Row getRow(int row) {
		return data.get(row);
	}
	
}