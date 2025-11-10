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
* Parks and Yadong Xu of ARA through the EPA�s Environmental Modeling and 
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
 * Handles displaying data with simple statistics functions in a data panel.
 * Supports computing statistics in row and column fashion for a JTable.
 *
 * Author: TWU
 * Created: 1/2/2016
 */
public class DatasetSimpleStatistics {
    // List of available statistics options
    final String[] options = { "Sum", "Average", "Max", "Min", "Change", "Std" };
    JTable table;
    double[][] tableData;
    String[] changeCol;
    int changeColIndex;

    /**
     * Constructs the statistics dialog and initializes table data.
     * @param tData Table data as String[][]
     * @param table JTable to operate on
     */
    public DatasetSimpleStatistics(String[][] tData, JTable table) {
        this.table = table;
        changeColIndex = -1;
        int cc = table.getColumnModel().getColumnCount();
        changeCol = new String[cc];
        for (int i = 0; i < cc; i++)
            changeCol[i] = (String) table.getColumnModel().getColumn(i).getHeaderValue();
        // Convert table data to double[][], skipping first column (assumed non-numeric)
        tableData = new double[tData.length][tData[0].length - 1];
        for (int i = 0; i < tableData.length - 1; i++) {
            for (int j = 0; j < tableData[i].length - 1; j++)
                tableData[i][j] = Double.valueOf(tData[i][j + 1]);
            tableData[i][tableData[i].length - 1] = 0;
        }
        for (int j = 0; j < tableData[0].length - 1; j++)
            tableData[tableData.length - 1][j] = 0;
        // Create option list and dialog for selecting statistic function
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

    /**
     * Executes the selected statistics function and updates the table.
     * @param index Index of selected function
     */
    protected void doFunction(int index) {
        double[] col = null;
        double[] row = null;
        switch (index) {
        case 0: // sum
            col = doColSum(tableData.length);
            row = doRowSum(tableData[0].length);
            updateTable(row, col, "Sum");
            break;
        case 1: // average
            col = doColAvg(tableData.length);
            row = doRowAvg(tableData[0].length);
            updateTable(row, col, "Average");
            break;
        case 2: // max
            col = doColMax(tableData.length);
            row = doRowMax(tableData[0].length);
            updateTable(row, col, "Max");
            break;
        case 3: // min
            col = doColMin(tableData.length);
            row = doRowMin(tableData[0].length);
            updateTable(row, col, "Min");
            break;
        case 4: // change
            int[] colRow = setChangeCol();
            col = doColChange(colRow[0] - 1, colRow[1] - 1, tableData.length); // base-1, rel-1
            row = doRowChange(colRow[0] - 1, colRow[1] - 1, tableData[0].length);
            updateTable(row, col, "Relative Change%");
            break;
        case 5: // std
            col = doColStd(tableData.length);
            row = doRowStd(tableData[0].length);
            updateTable(row, col, "Stand Diviation");
            break;
        }
    }

    /**
     * Computes column sums.
     * @param rowCount Number of rows
     * @return Array of column sums
     */
    protected double[] doColSum(int rowCount) {
        double[] col = new double[tableData[0].length - 1];
        for (int i = 0; i < col.length; i++) {
            double temp = 0;
            for (int j = 0; j < rowCount; j++)
                temp += tableData[j][i];
            col[i] = temp;
        }
        return col;
    }

    /**
     * Computes row sums.
     * @param colCount Number of columns
     * @return Array of row sums
     */
    protected double[] doRowSum(int colCount) {
        double[] row = new double[tableData.length - 1];
        for (int i = 0; i < row.length; i++) {
            double temp = 0;
            for (int j = 0; j < colCount; j++)
                temp += tableData[i][j];
            row[i] = temp;
        }
        return row;
    }

    /**
     * Computes column averages.
     * @param rowCount Number of rows
     * @return Array of column averages
     */
    protected double[] doColAvg(int rowCount) {
        double[] col = new double[tableData[0].length - 1];
        for (int i = 0; i < col.length; i++) {
            double temp = 0;
            for (int j = 0; j < rowCount; j++)
                temp += tableData[j][i];
            col[i] = temp / rowCount;
        }
        return col;
    }

    /**
     * Computes row averages.
     * @param colCount Number of columns
     * @return Array of row averages
     */
    protected double[] doRowAvg(int colCount) {
        double[] row = new double[tableData.length - 1];
        for (int i = 0; i < row.length; i++) {
            double temp = 0;
            for (int j = 0; j < colCount; j++)
                temp += tableData[i][j];
            row[i] = temp / colCount;
        }
        return row;
    }

    /**
     * Computes column maximums.
     * @param rowCount Number of rows
     * @return Array of column maximums
     */
    protected double[] doColMax(int rowCount) {
        double[] col = new double[tableData[0].length - 1];
        for (int i = 0; i < col.length; i++) {
            double temp = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < rowCount; j++)
                temp = Math.max(temp, tableData[j][i]);
            col[i] = temp;
        }
        return col;
    }

    /**
     * Computes row maximums.
     * @param colCount Number of columns
     * @return Array of row maximums
     */
    protected double[] doRowMax(int colCount) {
        double[] row = new double[tableData.length - 1];
        for (int i = 0; i < row.length; i++) {
            double temp = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < colCount; j++)
                temp = Math.max(temp, tableData[i][j]);
            row[i] = temp;
        }
        return row;
    }

    /**
     * Computes column minimums.
     * @param rowCount Number of rows
     * @return Array of column minimums
     */
    protected double[] doColMin(int rowCount) {
        double[] col = new double[tableData[0].length - 1];
        for (int i = 0; i < col.length; i++) {
            double temp = Double.POSITIVE_INFINITY;
            for (int j = 0; j < rowCount; j++)
                temp = Math.min(temp, tableData[j][i]);
            col[i] = temp;
        }
        return col;
    }

    /**
     * Computes row minimums.
     * @param colCount Number of columns
     * @return Array of row minimums
     */
    protected double[] doRowMin(int colCount) {
        double[] row = new double[tableData.length - 1];
        for (int i = 0; i < row.length; i++) {
            double temp = Double.POSITIVE_INFINITY;
            for (int j = 0; j < colCount; j++)
                temp = Math.min(temp, tableData[i][j]);
            row[i] = temp;
        }
        return row;
    }

    /**
     * Prompts user to select base and relative columns for change calculation.
     * @return Array with base and relative column indices
     */
    protected int[] setChangeCol() {
        int[] selected = { -1, -1 };
        JComponent jc = colHeaderList();
        Object options[] = { "Ok" };
        JOptionPane pane0 = new JOptionPane(jc, -1, 0, null, options, options[0]);
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

    /**
     * Creates a JList for column header selection.
     * @return JList of column headers
     */
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

    /**
     * Computes relative change for columns.
     * @param base Base column index
     * @param rel Relative column index
     * @param rowCount Number of rows
     * @return Array of column changes
     */
    protected double[] doColChange(int base, int rel, int rowCount) {
        double[] col = new double[tableData[0].length];
        System.arraycopy(doColSum(rowCount), 0, col, 0, col.length - 1);
        col[col.length - 1] = conversionUtil.DataConversion.roundDouble(
                ((col[rel] - col[base]) / col[base]) * 100, 0);
        return col;
    }

    /**
     * Computes relative change for rows.
     * @param base Base column index
     * @param rel Relative column index
     * @param colCount Number of columns
     * @return Array of row changes
     */
    protected double[] doRowChange(int base, int rel, int colCount) {
        double[] row = new double[tableData.length - 1];
        for (int i = 0; i < row.length; i++)
            row[i] = conversionUtil.DataConversion.roundDouble(
                    ((tableData[i][rel] - tableData[i][base]) / tableData[i][base]) * 100, 0);
        return row;
    }

    /**
     * Computes column standard deviations.
     * @param rowCount Number of rows
     * @return Array of column standard deviations
     */
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

    /**
     * Computes row standard deviations.
     * @param colCount Number of columns
     * @return Array of row standard deviations
     */
    protected double[] doRowStd(int colCount) {
        double[] row = new double[tableData.length - 1];
        for (int i = 0; i < row.length; i++)
            row[i] = computeOneSampleStd(tableData[i]);
        return row;
    }

    /**
     * Computes standard deviation for a sample array.
     * @param samples Array of sample values
     * @return Standard deviation
     */
    protected double computeOneSampleStd(double samples[]) {
        double sampleTotalErr = computeOneSampleTotalErr(samples);
        return Math.sqrt(sampleTotalErr / samples.length);
    }

    /**
     * Computes total error for a sample array.
     * @param samples Array of sample values
     * @return Total error
     */
    protected double computeOneSampleTotalErr(double samples[]) {
        double sampleMean = computeOneSampleMean(samples);
        double sampleTotalErr = 0.0D;
        for (int i = 0; i < samples.length; i++)
            sampleTotalErr += Math.pow(samples[i] - sampleMean, 2D);
        return sampleTotalErr;
    }

    /**
     * Computes mean for a sample array.
     * @param samples Array of sample values
     * @return Mean value
     */
    public static double computeOneSampleMean(double samples[]) {
        double sampleMean = 0.0D;
        for (int i = 0; i < samples.length; i++)
            sampleMean += samples[i];
        sampleMean /= samples.length;
        return sampleMean;
    }

    /**
     * Updates the JTable with new statistics data and column names.
     * @param row Array of row statistics
     * @param col Array of column statistics
     * @param funcName Name of the function/statistic
     */
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

    /**
     * Converts a double array to a String array.
     * @param d Array of doubles
     * @return Array of strings
     */
    protected String[] double2String(double[] d) {
        String[] temp = new String[d.length];
        for (int j = 0; j < temp.length; j++)
            temp[j] = String.valueOf(d[j]);
        return temp;
    }
}