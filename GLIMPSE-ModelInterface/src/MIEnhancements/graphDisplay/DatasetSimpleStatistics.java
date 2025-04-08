/*
* LEGAL NOTICE
* This computer software was prepared by US EPA.
* THE GOVERNMENT MAKES NO WARRANTY, EXPRESS OR IMPLIED, OR ASSUMES ANY
* LIABILITY FOR THE USE OF THIS SOFTWARE. This notice including this
* sentence must appear on any copies of this computer software.
* 
* EXPORT CONTROL
* User agrees that the Software will not be shipped, transferred or
* exported into any country or used in any manner prohibited by the
* United States Export Administration Act or any other applicable
* export laws, restrictions or regulations (collectively the "Export Laws").
* Export of the Software may require some form of license or other
* authority from the U.S. Government, and failure to obtain such
* export control license may result in criminal liability under
* U.S. laws. In addition, if the Software is identified as export controlled
* items under the Export Laws, User represents and warrants that User
* is not a citizen, or otherwise located within, an embargoed nation
* (including without limitation Iran, Syria, Sudan, Cuba, and North Korea)
*     and that User is not otherwise prohibited
* under the Export Laws from receiving the Software.
*
* SUPPORT
* For the GLIMPSE project, GCAM development, data processing, and support for 
* policy implementations has been led by Dr. Steven J. Smith of PNNL, via Interagency 
* Agreements 89-92423101 and 89-92549601. Contributors * from PNNL include 
* Maridee Weber, Catherine Ledna, Gokul Iyer, Page Kyle, Marshall Wise, Matthew 
* Binsted, and Pralit Patel. Coding contributions have also been made by Aaron 
* Parks and Yadong Xu of ARA through the EPA’s Environmental Modeling and 
* Visualization Laboratory contract. 
* 
*/
package graphDisplay;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 * The class to handle displaying data with simple statistics functions in display data panel. 
 * You can be compute in row and column fashion
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class DatasetSimpleStatistics {
	final String[] options = { "Sum", "Average", "Max", "Min", "Change", "Std", };
	JTable table;
	double[][] tableData;
	String[] changeCol;
	int changeColIndex;

	public DatasetSimpleStatistics(String[][] tData, JTable table) {
		this.table = table;
		changeColIndex = -1;
		int cc = table.getColumnModel().getColumnCount();
		changeCol = new String[cc];
		for (int i = 0; i < cc; i++)
			changeCol[i] = (String) table.getColumnModel().getColumn(i).getHeaderValue();
		tableData = new double[tData.length][tData[0].length - 1];
		for (int i = 0; i < tableData.length - 1; i++) {
			for (int j = 0; j < tableData[i].length - 1; j++)
				tableData[i][j] = Double.valueOf(tData[i][j + 1]);
			tableData[i][tableData[i].length - 1] = 0;
		}
		for (int j = 0; j < tableData[0].length - 1; j++)
			tableData[tableData.length - 1][j] = 0;
		JList<String> list = new JList<String>(options);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				JList<?> list = (JList<?>) e.getSource();
				boolean adjust = e.getValueIsAdjusting();
				if (!adjust)
					doFunction(list.getSelectedIndex());
			}

		});
		String options[] = { "ok" };
		JOptionPane pane0 = new JOptionPane(new JScrollPane(list), -1, 0, null,
				options, options[0]);
		JDialog dialog = pane0.createDialog("Please select a function");
		dialog.setLayout(null);
		dialog.setResizable(true);
		dialog.setVisible(true);
	}

	protected void doFunction(int index) {
		double[] col = null;
		double[] row = null;
		switch (index) {
		case 0:// sum
			col = doColSum(tableData.length);
			row = doRowSum(tableData[0].length);
			updateTable(row, col, "Sum");
			break;
		case 1:// average
			col = doColAvg(tableData.length);
			row = doRowAvg(tableData[0].length);
			updateTable(row, col, "Average");
			break;
		case 2:// max
			col = doColMax(tableData.length);
			row = doRowMax(tableData[0].length);
			updateTable(row, col, "Max");
			break;
		case 3:// min
			col = doColMin(tableData.length);
			row = doRowMin(tableData[0].length);
			updateTable(row, col, "Min");
			break;
		case 4:// change
			int[] colRow = setChangeCol();
			col = doColChange(colRow[0]-1, colRow[1]-1 , tableData.length);//base-1, rel-1
			row = doRowChange(colRow[0]-1, colRow[1]-1, tableData[0].length);
			updateTable(row, col, "Relative Change%");
			break;
		case 5:// std
			col = doColStd(tableData.length);
			row = doRowStd(tableData[0].length);
			updateTable(row, col, "Stand Diviation");
			break;
		}
	}

	protected double[] doColSum(int rowCount) {
		double[] col = new double[tableData[0].length - 1];
		for (int i = 0; i < col.length; i++) {
			double temp = 0;
			for (int j = 0; j < rowCount; j++)
				temp = temp + tableData[j][i];
			col[i] = temp;
		}
		return col;
	}

	protected double[] doRowSum(int colCount) {
		double[] row = new double[tableData.length - 1];
		for (int i = 0; i < row.length; i++) {
			double temp = 0;
			for (int j = 0; j < colCount; j++)
				temp = temp + tableData[i][j];
			row[i] = temp;
		}
		return row;
	}

	protected double[] doColAvg(int rowCount) {
		double[] col = new double[tableData[0].length - 1];
		for (int i = 0; i < col.length; i++) {
			double temp = 0;
			for (int j = 0; j < rowCount; j++)
				temp = temp + tableData[j][i];
			col[i] = temp / rowCount;
		}
		return col;
	}

	protected double[] doRowAvg(int colCount) {
		double[] row = new double[tableData.length - 1];
		for (int i = 0; i < row.length; i++) {
			double temp = 0;
			for (int j = 0; j < colCount; j++)
				temp = temp + tableData[i][j];
			row[i] = temp / colCount;
		}
		return row;
	}

	protected double[] doColMax(int rowCount) {
		double[] col = new double[tableData[0].length - 1];
		for (int i = 0; i < col.length; i++) {
			double temp = 0;
			for (int j = 0; j < rowCount; j++)
				temp = Math.max(temp, tableData[j][i]);
			col[i] = temp;
		}
		return col;
	}

	protected double[] doRowMax(int colCount) {
		double[] row = new double[tableData.length - 1];
		for (int i = 0; i < row.length; i++) {
			double temp = 0;
			for (int j = 0; j < colCount; j++)
				temp = Math.max(temp, tableData[i][j]);
			row[i] = temp;
		}
		return row;
	}

	protected double[] doColMin(int rowCount) {
		double[] col = new double[tableData[0].length - 1];
		for (int i = 0; i < col.length; i++) {
			double temp = 0;
			for (int j = 0; j < rowCount; j++)
				temp = Math.min(temp, tableData[j][i]);
			col[i] = temp;
		}
		return col;
	}

	protected double[] doRowMin(int colCount) {
		double[] row = new double[tableData.length - 1];
		for (int i = 0; i < row.length; i++) {
			double temp = 0;
			for (int j = 0; j < colCount; j++)
				temp = Math.min(temp, tableData[i][j]);
			row[i] = temp;
		}
		return row;
	}

	protected int[] setChangeCol() {
		int[] selected = {-1, -1};
		JComponent jc = colHeaderList();
		Object options[] = { "Ok" };
		JOptionPane pane0 = new JOptionPane(jc, -1, 0, null, options,
				options[0]);
		JDialog dialog = pane0.createDialog("Select Base Column");
		dialog.setPreferredSize(new Dimension(600, 200));
		dialog.setLayout(null);
		dialog.setResizable(true);
		dialog.setVisible(true);
		if (changeColIndex != -1) {
			selected[0] = changeColIndex;
			dialog.dispose();
			changeColIndex = -1;
		}
		dialog = pane0.createDialog("Select Relative Column");
		dialog.setPreferredSize(new Dimension(780, 200));
		dialog.setLayout(null);
		dialog.setResizable(true);
		dialog.setVisible(true);
		if (changeColIndex != -1) {
			selected[1] = changeColIndex;
			dialog.dispose();
			changeColIndex = -1;
		}
		return selected;
	}

	protected JList<Object> colHeaderList() {
		JList<Object> list = new JList<Object>(changeCol);
		list.setSelectionMode(0);
		ListSelectionListener listener = new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				JList<?> list = (JList<?>) e.getSource();
				boolean adjust = e.getValueIsAdjusting();
				if (!adjust)
					changeColIndex = list.getSelectedIndex();
			}

		};
		list.addListSelectionListener(listener);
		return list;
	}
	
	protected double[] doColChange(int base, int rel, int rowCount) {
		double[] col = new double[tableData[0].length];
		System.arraycopy(doColSum(rowCount), 0, col, 0, col.length - 1);
		col[col.length - 1] = conversionUtil.DataConversion.roundDouble(
				((col[rel] - col[base]) / col[base]) * 100, 0);
		return col;
	}

	protected double[] doRowChange(int base, int rel, int colCount) {
		double[] row = new double[tableData.length - 1];
		for (int i = 0; i < row.length; i++)
			row[i] = conversionUtil.DataConversion.roundDouble(
					((tableData[i][rel] - tableData[i][base])
							/ tableData[i][base]) * 100, 0);
		return row;
	}

	protected double[] doColStd(int rowCount) {
		double[] col = new double[tableData[0].length - 1];
		for (int i = 0; i < col.length; i++) {
			double[] temp = new double[rowCount];
			for (int j = 0; j < rowCount; j++)
				temp[j] = tableData[j][i];
			col[i] = computeOneSampleStd(temp);
		}
		return col;
	}

	protected double[] doRowStd(int colCount) {
		double[] row = new double[tableData.length - 1];
		for (int i = 0; i < row.length; i++)
			row[i] = computeOneSampleStd(tableData[i]);
		return row;
	}

	protected double computeOneSampleStd(double samples[]) {
		double sampleTotalErr = computeOneSampleTotalErr(samples);
		return Math.sqrt(sampleTotalErr / samples.length);
	}

	protected double computeOneSampleTotalErr(double samples[]) {
		double sampleMean = computeOneSampleMean(samples);
		double sampleTotalErr = 0.0D;
		for (int i = 0; i < samples.length; i++)
			sampleTotalErr += Math.pow(samples[i] - sampleMean, 2D);
		return sampleTotalErr;
	}

	public static double computeOneSampleMean(double samples[]) {
		double sampleMean = 0.0D;
		for (int i = 0; i < samples.length; i++)
			sampleMean += samples[i];
		sampleMean /= samples.length;
		return sampleMean;
	}

	protected void updateTable(double[] row, double[] col, String funcName) {
		String[][] newData = new String[tableData.length][tableData[0].length + 1];
		for (int i = 0; i < newData.length - 1; i++) {
			String[] temp = double2String(tableData[i]);
			System.arraycopy(temp, 0, newData[i], 1, temp.length);
			newData[i][0] = (String) table.getValueAt(i, 0);
			newData[i][newData[i].length - 1] = String.valueOf(row[i]);
		}

		String[] temp = double2String(col);
		System.arraycopy(temp, 0, newData[newData.length - 1], 1, temp.length);
		newData[newData.length - 1][0] = funcName;

		String[] colName = new String[tableData[0].length + 1];
		for (int i = 0; i < colName.length - 1; i++)
			colName[i] = table.getColumnName(i);
		colName[colName.length - 1] = funcName;
		((DefaultTableModel) table.getModel()).setDataVector(newData, colName);
	}

	protected String[] double2String(double[] d) {
		String[] temp = new String[d.length];
		for (int j = 0; j < temp.length; j++)
			temp[j] = String.valueOf(d[j]);
		return temp;
	}
}
