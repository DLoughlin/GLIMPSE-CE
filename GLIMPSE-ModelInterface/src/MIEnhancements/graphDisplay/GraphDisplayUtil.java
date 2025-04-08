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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JList;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.nfunk.jep.JEP;

import chart.Chart;
import chart.DatasetUtil;
import chart.LegendUtil;

/**
 * The class to handle utility functions for GraphDisplay package. 
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class GraphDisplayUtil {
	
	private static boolean debug = false;

	/**
	 * Build qualifiers for base selector - it is a multiple - level selection.
	 * 
	 * @param qualifier
	 *            array of qualifier names ({@code null} not permitted).
	 * @param data
	 *            values of qualifiers ({@code null} not permitted).
	 * @return list of values for each qualifier
	 */

	public static ArrayList<String[]> getUniqQualifierData(String[] qualifier, String[][] data) {
		ArrayList<String[]> al = new ArrayList<String[]>();

		for (int i = 0; i < qualifier.length; i++) {
			ArrayList<String> al1 = new ArrayList<String>();
			String[] temp = data[i];
			Arrays.sort(temp, null);
			String t = temp[0].trim();
			al1.add(t);
			for (int j = 1; j < temp.length; j++) {
				if (!t.equals(temp[j].trim())) {
					al1.add(temp[j]);
					t = temp[j].trim();
				}
			}
			al.add(i, al1.toArray(new String[0]));
		}
		return al;
	}

	
	/**
	 * Display JFreeChart instance of selected rows with subset of selected
	 * columns.
	 *
	 * @param row
	 *            indexes of selected rows ({@code null} not permitted).
	 * @param chart
	 *            chart which rows and columns selected upon ({@code null} not
	 *            permitted).
	 */

	/*public static void showSelectRow(int[] selectedC, int[] selectedR, JFreeChart[] chart, JFreeChart[] copyChart,
			LegendItemCollection copyLgd) {
		for (int i = 0; i < chart.length; i++)
			showSelectRow(selectedC, selectedR, chart[i], copyChart[i], copyLgd);
	}*/

	/**
	 * Display JFreeChart instance of selected rows and columns from data table.
	 *
	 * @param selectedC
	 *            indexes of selected columns ({@code null} not permitted).
	 * @param selectedR
	 *            indexes of selected rows ({@code null} not permitted).
	 * @param chart
	 *            chart which rows and columns selected upon ({@code null} not
	 *            permitted).
	 * @param copyChart
	 *            chart which hold original rows and columns ({@code null} not
	 *            permitted).
	 */

	/*public static void showSelectRow(int[] selectedC, int[] selectedR, JFreeChart chart, JFreeChart copyChart,
			LegendItemCollection copyLgd) {
		// Get subset of columns data set, then call showSelectRow to show only
		// selected rows
		if (chart.getPlot().getPlotType().contains("Category")) {
			if (!(chart.getCategoryPlot().getDataset() instanceof DefaultBoxAndWhiskerCategoryDataset)) {
				chart.getCategoryPlot()
						.setDataset((CategoryDataset) DatasetUtil.getSubsetColumnDataset(selectedC, copyChart));

				GraphDisplayUtil.showSelectRow(selectedR, chart);
				ChartUtils.applyCurrentTheme(chart);
				chart.getCategoryPlot().setFixedLegendItems(LegendUtil.adjLenend(selectedR, copyLgd));
			}
			CategoryAxis domainAxis = chart.getCategoryPlot().getDomainAxis();
			if (chart.getCategoryPlot().getDataset().getColumnCount() > 16)
				domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
			else
				domainAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
		} else if (chart.getPlot().getPlotType().contains("XY")) {
			if (chart.getXYPlot().getDataset() instanceof IntervalXYDataset)
				chart.getXYPlot().setDataset((IntervalXYDataset) DatasetUtil.getSubsetColumnXYDataset(selectedC,
						copyChart.getXYPlot().getDataset()));
			else
				chart.getXYPlot().setDataset(DatasetUtil.createXYDataset(
						(XYDataset) DatasetUtil.getSubsetColumnXYDataset(selectedC, copyChart.getXYPlot().getDataset()),
						-1));
			GraphDisplayUtil.showSelectRow(selectedR, chart);
			chart.getXYPlot().setFixedLegendItems(LegendUtil.adjLenend(selectedR, copyLgd));
		} else { // for Pie Chart
			return;
		}
	}*/

	/**
	 * Display JFreeChart instance of selected rows with subset of selected
	 * columns.
	 *
	 * @param row
	 *            indexes of selected rows ({@code null} not permitted).
	 * @param chart
	 *            chart which rows and columns selected upon ({@code null} not
	 *            permitted).
	 */

	public static void showSelectRow(int[] row, JFreeChart chart) {
		if (debug)
			System.out.println("GraphDisplayUtil::showSelectRow:row: " + Arrays.toString(row));
		if (chart.getPlot().getPlotType().contains("Category")) {
			CategoryItemRenderer renderer = chart.getCategoryPlot().getRenderer();
			if (row != null) {
				for (int i = 0; i < chart.getCategoryPlot().getDataset().getRowCount(); i++) {
					renderer.setSeriesVisible(i, Boolean.valueOf(false));
					renderer.setSeriesVisibleInLegend(i, Boolean.valueOf(false));
				}
				for (int i = 0; i < row.length; i++) {
					renderer.setSeriesVisible(row[i], Boolean.valueOf(true));
					renderer.setSeriesVisibleInLegend(row[i], Boolean.valueOf(true));
				}
			}
		} else {
			if (row != null) {
				XYItemRenderer renderer = chart.getXYPlot().getRenderer();
				for (int i = 0; i < chart.getXYPlot().getDataset().getSeriesCount(); i++)
					renderer.setSeriesVisible(i, Boolean.valueOf(false));
				for (int i = 0; i < row.length; i++)
					renderer.setSeriesVisible(row[i], Boolean.valueOf(true));
			}
		}
	}

	/**
	 * Display JFreeChart instance of selected rows with subset of selected
	 * columns.
	 *
	 * @param row
	 *            names of selected rows ({@code null} not permitted).
	 * @param chart
	 *            chart which rows and columns selected upon ({@code null} not
	 *            permitted).
	 */

	public static void showSelectRow(String[] row, JFreeChart chart) {
		// hide all rows then only show selected rows
		if (chart.getPlot().getPlotType().contains("Category")) {
			CategoryItemRenderer renderer = chart.getCategoryPlot().getRenderer();
			if (row != null) {
				for (int i = 0; i < chart.getCategoryPlot().getDataset().getRowCount(); i++) {
					renderer.setSeriesVisible(i, Boolean.valueOf(false));
					renderer.setSeriesVisibleInLegend(i, Boolean.valueOf(false));
				}
				for (int i = 0; i < row.length; i++) {
					renderer.setSeriesVisible(chart.getCategoryPlot().getDataset().getRowIndex(row[i]),
							Boolean.valueOf(true));
					renderer.setSeriesVisibleInLegend(chart.getCategoryPlot().getDataset().getRowIndex(row[i]),
							Boolean.valueOf(true));
				}
			}
		} else {
			if (row != null) {
				XYItemRenderer renderer = chart.getXYPlot().getRenderer();
				for (int i = 0; i < chart.getXYPlot().getDataset().getSeriesCount(); i++)
					renderer.setSeriesVisible(i, Boolean.valueOf(false));
				for (int i = 0; i < row.length; i++)
					renderer.setSeriesVisible(chart.getXYPlot().getDataset().indexOf(row[i]), Boolean.valueOf(true));
			}
		}
		ChartUtils.applyCurrentTheme(chart);
	}

	// used in BoxandWhisker
	public static void showSelectColumn(JFreeChart chart) {
		if (chart.getPlot().getPlotType().contains("Category")) {
			CategoryItemRenderer renderer = chart.getCategoryPlot().getRenderer();
			for (int i = 0; i < chart.getCategoryPlot().getDataset().getRowCount(); i++)
				renderer.setSeriesVisible(i, Boolean.valueOf(true));
		} else {
			XYItemRenderer renderer = chart.getXYPlot().getRenderer();
			for (int i = 0; i < chart.getXYPlot().getDataset().getSeriesCount(); i++)
				renderer.setSeriesVisible(i, Boolean.valueOf(true));
		}
	}

	public static long getDayLong(String s) {
		java.util.Date d = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		try {
			if (s != null && !s.equals(""))
				d = sdf.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d.getTime();
	}

	public static boolean computIt(int[] computeFC, int k) {
		for (int i = 0; i < computeFC.length; i++) {
			if (k == computeFC[i])
				return true;
		}
		return false;
	}

	public static String[][] computeFunctionColumn(String expression) {
		JEP myParser = new JEP();
		myParser.parseExpression(expression);
		return null;
	}

	public static JList<Object> metaList(Chart[] charts) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (int i = 0; i < charts.length; i++)
			map.put(charts[i].getMeta().replace(",", "_") + "," + String.valueOf(i), charts[i].getMeta().replace(",", "_") + "," + String.valueOf(i));

		Object selOption[] = map.values().toArray();
		JList<Object> list = new JList<Object>(selOption);
		return list;
	}

}
