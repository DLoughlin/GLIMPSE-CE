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
package chart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import conversionUtil.ArrayConversion;
import graphDisplay.GraphDisplayUtil;
import graphDisplay.ThumbnailUtil2;

/**
 * The class handle utility functions for JFreeChart's dataset.
 * 
 * Author Action Date Flag
 * ======================================================================= TWU
 * created 1/2/2016
 */

public class DatasetUtil {
	private static boolean debug = false;

	public static String[][] dataset2Data(JFreeChart chart, int series) {
		String[][] data = null;

		if (chart.getPlot() instanceof CategoryPlot) {
			CategoryDataset ds = chart.getCategoryPlot().getDataset();
			data = catDataset2Data(ds, series);
		} else if (chart.getPlot() instanceof XYPlot) {
			XYDataset ds = chart.getXYPlot().getDataset();
			data = xyDataset2Data(ds, series);
		}
		if (debug)
			System.out.println(
					"Dataset2Data:data " + data.length + " : " + data[0].length + " data: " + Arrays.toString(data[0]));
		return data;
	}

	public static String[][] catDataset2Data(CategoryDataset ds, int series) {
		String[][] data = null;

		if (series != -1) {
			data = new String[1][ds.getColumnCount()];
			for (int j = 0; j < ds.getColumnCount(); j++)
				data[0][j] = String.valueOf(ds.getValue(series, j));
		} else {
			data = new String[ds.getRowCount()][ds.getColumnCount()];
			for (int i = 0; i < ds.getRowCount(); i++)
				for (int j = 0; j < ds.getColumnCount(); j++)
					data[i][j] = String.valueOf(ds.getValue(i, j));
		}

		if (debug)
			System.out.println("catDataset2Data:data " + Arrays.toString(data[0]));
		return data;
	}

	public static DefaultXYDataset createXYDataset(XYDataset ds, int relativeIndex) {
		DefaultXYDataset collection = new DefaultXYDataset();
		for (int i = 0; i < ds.getSeriesCount(); i++) {
			double[][] d = new double[2][ds.getItemCount(0)];
			double divData = relativeIndex == -1 ? 1 : ds.getYValue(i, relativeIndex);
			for (int j = 0; j < ds.getItemCount(i); j++) {
				d[0][j] = ds.getXValue(i, j);
				d[1][j] = ds.getYValue(i, j) / divData;
			}
			collection.addSeries(ds.getSeriesKey(i), d);
		}
		return collection;
	}

	public static String[][] xyDataset2Data(XYDataset ds, int series) {
		String[][] data = null;
		if (series != -1) {
			data = new String[1][ds.getItemCount(series)];
			for (int j = 0; j < ds.getItemCount(series); j++) {
				data[0][j] = String.valueOf(ds.getYValue(series, j));
			}
		} else {
			data = new String[ds.getSeriesCount()][ds.getItemCount(0)];
			for (int i = 0; i < ds.getSeriesCount(); i++)
				for (int j = 0; j < ds.getItemCount(i); j++) {
					data[i][j] = String.valueOf(ds.getYValue(i, j));
				}
		}
		return data;
	}

	public static double[][] dataset2DataD(JFreeChart chart) {
		if (chart.getPlot() instanceof CategoryPlot) {
			return catDataset2DataD(chart.getCategoryPlot().getDataset());
		} else if (chart.getPlot() instanceof XYPlot) {
			return xyDataset2DataD(chart.getXYPlot().getDataset());
		} else
			return null;
	}

	public static double[][] catDataset2DataD(CategoryDataset ds) {
		double[][] dsD = new double[ds.getRowCount()][ds.getColumnCount()];
		for (int i = 0; i < ds.getRowCount(); i++)
			for (int j = 0; j < ds.getColumnCount(); j++)
				dsD[i][j] = ((Double) ds.getValue(i, j)).doubleValue();
		return dsD;
	}

	public static double[][] xyDataset2DataD(XYDataset ds) {
		double[][] data = new double[ds.getSeriesCount()][ds.getItemCount(0)];
		for (int i = 0; i < ds.getSeriesCount(); i++)
			for (int j = 0; j < ds.getItemCount(i); j++)
				data[i][j] = ds.getYValue(i, j);
		return data;
	}

	public static String[][] oneSeriesDataset2Data(Chart[] chart, int series) {
		//int idx = ThumbnailUtil.getFirstNonNullChart(chart);
		//Dan: using modified version (2)
		int idx = ThumbnailUtil2.getFirstNonNullChart(chart);
		String[][] data = new String[chart.length][getChartColumnIndex(chart[idx]).length];//.getChart()).length];
		if (debug)
			System.out.println(
					"oneSeriesDataset2Data:cl: " + data.length + " : " + data[0].length + " series: " + series + " ");
		int k = 0;
		for (int i = 0; i < chart.length && chart[i].getChart() != null; i++) {
			data[i] = dataset2Data(chart[i].getChart(), series)[0];
			k++;
			if (debug)
				System.out.println("oneSeriesDataset2Data:i " + i + " : " + " data: " + Arrays.toString(data[i]));
		}
		return Arrays.copyOfRange(data, 0, k);
	}

	public static String[][] dataset2Data(Chart[] chart) {
		//Dan: using modified version (2)
		int idx = ThumbnailUtil2.getFirstNonNullChart(chart);
		double[][] sumData = dataset2DataD(chart[idx].getChart());
		for (int i = 1; i < chart.length; i++) {
			double[][] temp = dataset2DataD(chart[i].getChart());
			if (sumData.length!=temp.length)
				return null;
			else
				sumData = sumDatasetData(sumData, temp);
		}
		return conversionUtil.DataConversion.Double2String(sumData);
	}

	private static double[][] sumDatasetData(double[][] sumdata, double[][] temp) {
		double[][] tot = sumdata.clone();
		for (int i = 0; i < tot.length; i++)
			for (int j = 0; j < tot[i].length; j++)
				tot[i][j] = tot[i][j] + temp[i][j];
		return tot;
	}

	public static String[][] getSubsetData(String[][] d, int[] r, int[] c) {
		String[][] data = new String[r.length][c.length];
		for (int i = 0; i < r.length; i++)
			for (int j = 0; j < c.length; j++)
				data[i][j] = d[r[i]][c[j]];
		return data;
	}

	public static CategoryDataset getSubsetColumnDataset1(int[] col, JFreeChart chart) {

		CategoryDataset ds = chart.getCategoryPlot().getDataset(0);
		String[][] data = new String[ds.getRowCount()][col.length];
		String[] column = new String[col.length];
		String[] row = new String[ds.getRowCount()];
		for (int i = 0; i < ds.getRowCount(); i++) {
			row[i] = (String) ds.getRowKey(i);
			for (int j = 0; j < col.length; j++) {
				column[j] = (String) ds.getColumnKey(col[j]);
				data[i][j] = String.valueOf(ds.getValue(i, col[j]));
			}
		}
		CategoryDataset subds = new MyDataset().createCategoryDataset(data, row, column);
		return subds;
	}

	/*public static AbstractDataset getSubsetColumnDataset(int[] col, JFreeChart chart) {
		if (chart.getPlot() instanceof CategoryPlot) {
			CategoryDataset ds = chart.getCategoryPlot().getDataset();
			return getSubsetColumnDataset(col, ds);
		} else if (chart.getPlot() instanceof XYPlot) {
			XYDataset ds = chart.getXYPlot().getDataset();
			return getSubsetColumnXYDataset(col, ds);
		} else
			return null;
	}*/

	/*public static AbstractDataset getSubsetColumnDataset(int[] col, CategoryDataset ds) {
		String[][] data = new String[ds.getRowCount()][col.length];
		String[] column = new String[col.length];
		String[] row = new String[ds.getRowCount()];
		for (int i = 0; i < ds.getRowCount(); i++) {
			row[i] = (String) ds.getRowKey(i);
			for (int j = 0; j < col.length; j++) {
				column[j] = (String) ds.getColumnKey(col[j]);
				data[i][j] = String.valueOf(ds.getValue(i, col[j]));
			}
		}
		AbstractDataset subds = new MyDataset().createCategoryDataset(data, row, column);
		return subds;
	}*/

	public static AbstractDataset getSubsetColumnXYDataset(int[] col, XYDataset ds) {
		String[][] data = new String[ds.getSeriesCount()][col.length];
		String[] column = new String[col.length];
		String[] row = new String[ds.getSeriesCount()];
		for (int i = 0; i < ds.getSeriesCount(); i++) {
			row[i] = (String) ds.getSeriesKey(i);
			for (int j = 0; j < col.length; j++) {
				column[j] = String.valueOf(ds.getXValue(i, col[j]));
				data[i][j] = String.valueOf(ds.getYValue(i, col[j]));
			}
		}
		AbstractDataset subds = (AbstractDataset) new MyDataset().createXYDataset(data, row, column);
		return subds;
	}

	public static AbstractDataset getSubsetRowDataset(int[] r, JFreeChart chart) {
		if (chart.getPlot() instanceof CategoryPlot) {
			CategoryDataset ds = chart.getCategoryPlot().getDataset();
			return getSubsetRowDataset(r, ds);
		} else if (chart.getPlot() instanceof XYPlot) {
			XYDataset ds = chart.getXYPlot().getDataset();
			return getSubsetRowDataset(r, ds);
		} else
			return null;
	}

	public static AbstractDataset getSubsetRowDataset(int[] r, CategoryDataset ds) {
		String[][] data = new String[r.length][ds.getColumnCount()];
		String[] column = new String[ds.getColumnCount()];
		String[] row = new String[r.length];
		for (int i = 0; i < r.length; i++) {
			row[i] = (String) ds.getRowKey(r[i]);
			for (int j = 0; j < ds.getColumnCount(); j++) {
				column[j] = (String) ds.getColumnKey(j);
				data[i][j] = String.valueOf(ds.getValue(r[i], j));
			}
		}
		AbstractDataset subds = new MyDataset().createCategoryDataset(data, row, column);
		return subds;
	}

	public static AbstractDataset getSubsetRowDataset(int[] r, XYDataset ds) {
		String[][] data = new String[r.length][ds.getItemCount(0)];
		String[] column = new String[ds.getItemCount(0)];
		String[] row = new String[r.length];
		for (int i = 0; i < r.length; i++) {
			row[i] = (String) ds.getSeriesKey(r[i]);
			for (int j = 0; j < ds.getItemCount(i); j++) {
				column[j] = String.valueOf(ds.getX(r[i], j));
				data[i][j] = String.valueOf(ds.getYValue(r[i], j));
			}
		}
		AbstractDataset subds = new MyDataset().createCategoryDataset(data, row, column);
		return subds;
	}

	public static DefaultBoxAndWhiskerCategoryDataset getSubsetColumnStaticsDataset(int[] col, JFreeChart chart) {
		DefaultBoxAndWhiskerCategoryDataset ds = ((DefaultBoxAndWhiskerCategoryDataset) chart.getCategoryPlot()
				.getDataset());
		DefaultBoxAndWhiskerCategoryDataset subds = new DefaultBoxAndWhiskerCategoryDataset();
		for (int i = 0; i < ds.getRowCount(); i++) {
			for (int j = 0; j < col.length; j++)
				subds.add(ds.getItem(i, col[j]), ds.getRowKey(i), ds.getColumnKey(col[j]));
		}
		return subds;
	}

	public static String[][] getDiffData(CategoryDataset ds1, CategoryDataset ds2, List<String> rowKeys) {
		String[][] data = new String[rowKeys.size()][ds1.getColumnCount()];
		double[][] ds1D = getCategoryData(ds1, rowKeys);
		double[][] ds2D = getCategoryData(ds2, rowKeys);

		for (int i = 0; i < ds1D.length; i++) {
			for (int n = 0; n < ds2.getColumnCount(); n++) {
				double d = ds1D[i][n] - ds2D[i][n];
				data[i][n] = String.valueOf(d);
			}
		}
		return data;
	}
	
	public static String[][] getDiffData(XYDataset ds1, XYDataset ds2,  String[] rowKeys) {
		String[][] data = new String[rowKeys.length][ds1.getItemCount(0)];
		double[][] ds1D = getXYData(ds1, rowKeys);
		double[][] ds2D = getXYData(ds2, rowKeys);

		for (int i = 0; i < ds1D.length; i++) {
			for (int n = 0; n < ds2.getItemCount(0); n++) {
				double d = ds1D[i][n] - ds2D[i][n];
				data[i][n] = String.valueOf(d);
			}
		}
		return data;
	}

	private static double[][] getCategoryData(CategoryDataset ds, List<String> l) {
		double[][] dsD = new double[l.size()][ds.getColumnCount()];
		List<String> l1 = ds.getRowKeys();

		for (int i = 0; i < l.size(); i++) {
			String o = l.get(i).trim();
			int n = l1.indexOf(o);
			if (n >= 0){
				for (int j = 0; j < ds.getColumnCount(); j++)
					dsD[i][j] = ((Double) ds.getValue(n, j)).doubleValue();
			} else {
				for (int j = 0; j < ds.getColumnCount(); j++)
					dsD[i][j] = 0;
			}
		}
		return dsD;
	}

	private static double[][] getXYData(XYDataset ds, String[] l) {
		double[][] dsD = new double[l.length][ds.getItemCount(0)];
		String[] l1 = DatasetUtil.getChartRows(ds);
		int ic = ds.getItemCount(0);
		
		for (int i = 0; i < l.length; i++) {
			String o = l[i].trim();
			int n = Arrays.asList(l1).indexOf(o);
			if (n >= 0){
				for (int j = 0; j < ic; j++)
					dsD[i][j] = ((Double) ds.getYValue(n, j)).doubleValue();
			} else {
				for (int j = 0; j < ic; j++)
					dsD[i][j] = 0;
			}
		}
		return dsD;
	}

	public static double[] fillZero(int len) {
		double[] d = new double[len];
		for (int i = 0; i < len; i++)
			d[i] = 0;
		return d;
	}

	public static ArrayList<List<String[]>> getStatisticsData(Chart[] chart) {
		
		Chart[] chart1 = null;
		if (chart[0].getChart().getPlot().getPlotType().contains("XY"))
				//Dan: using modified version (2)
				chart1 = ThumbnailUtil2.createChart("chart.CategoryLineChart", -1, chart,null);
		else
			chart1 = chart.clone();
		ArrayList<List<String[]>> al = new ArrayList<List<String[]>>();
		
		for (int m = 0; m < chart1[0].getChart().getCategoryPlot().getDataset().getRowCount(); m++) {
			ArrayList<String[]> alC = new ArrayList<String[]>();
			for (int n = 0; n < chart1[0].getChart().getCategoryPlot().getDataset().getColumnCount(); n++) {
				String[] data = new String[chart1.length];
				int k = 0;
				for (int i = 0; i < chart1.length && chart1[i].getChart() != null; i++) {
					data[i] = String.valueOf(chart1[i].getChart().getCategoryPlot().getDataset().getValue(m, n));
					k++;
				}
				alC.add(Arrays.copyOfRange(data, 0, k));
			}
			al.add(alC);
		} 
		return al;
	}
	
	public static DefaultCategoryDataset[] XYDataset2CategoryDataset(XYDataset[] xydataset) {
		DefaultCategoryDataset[] dataset = new DefaultCategoryDataset[xydataset.length];
		for (int i = 0; i < dataset.length; i++)
			dataset[i] = XYDataset2CategoryDataset(xydataset[i]);
		return dataset;
	}

	public static DefaultCategoryDataset XYDataset2CategoryDataset(XYDataset xydataset) {

		DefaultCategoryDataset ds = new DefaultCategoryDataset();
		for (int i = 0; i < xydataset.getSeriesCount(); i++) {
			for (int j = 0; j < xydataset.getItemCount(i); j++) {
				ds.addValue(xydataset.getYValue(i, j), xydataset.getSeriesKey(i),
						String.valueOf(xydataset.getXValue(i, j)));
			}
		}
		return ds;
	}

	public static DefaultXYDataset[] CategoryDataset2XYDataset(CategoryDataset[] catdataset) {
		DefaultXYDataset[] dataset = new DefaultXYDataset[catdataset.length];
		for (int i = 0; i < dataset.length; i++)
			dataset[i] = CategoryDataset2XYDataset(catdataset[i]);
		return dataset;
	}

	public static DefaultXYDataset CategoryDataset2XYDataset(CategoryDataset catdataset) {
		DefaultXYDataset ds = new DefaultXYDataset();
		String[] row = new String[catdataset.getRowCount()];
		for (int i = 0; i < catdataset.getRowCount(); i++) {
			row[i] = (String) catdataset.getRowKey(i);
			double[][] data = new double[2][catdataset.getColumnCount()];
			for (int j = 0; j < catdataset.getColumnCount(); j++) {
				String temp = (String) catdataset.getColumnKey(j);
				if (temp.contains("/") || temp.contains("-"))
					temp = String.valueOf(GraphDisplayUtil.getDayLong(temp));

				data[0][j] = Double.valueOf(temp);
				data[1][j] = (Double) catdataset.getValue(i, j);
			}
			ds.addSeries(row[i], data);
		}
		return ds;
	}
	
	public static String[] getChartRows1(JFreeChart jfchart) {
		if (jfchart.getPlot().getPlotType().equals("Category Plot"))
			return getChartRows1(jfchart.getCategoryPlot());
		else if (jfchart.getPlot().getPlotType().equals("XY Plot"))
			return getChartRows1(jfchart.getXYPlot());
		else if (jfchart.getPlot().getPlotType().equals("Pie Plot"))
			return getChartRows1((PiePlot) jfchart.getPlot());
		else
			return null;
	}

	public static String getChartRow1(JFreeChart jfchart) {
		if (jfchart.getPlot().getPlotType().equals("Category Plot"))
			return ArrayConversion.array2String(getChartRows1(jfchart.getCategoryPlot()));
		else if (jfchart.getPlot().getPlotType().equals("XY Plot"))
			return ArrayConversion.array2String(getChartRows1(jfchart.getXYPlot()));
		else if (jfchart.getPlot().getPlotType().equals("Pie Plot"))
			return ArrayConversion.array2String(getChartRows1((PiePlot) jfchart.getPlot()));
		else if (jfchart.getPlot().getPlotType().equals("Pie Plot 3D"))
			return ArrayConversion.array2String(getChartRows1((PiePlot3D) jfchart.getPlot()));
		else
			return null;
	}

	protected static String[] getChartRows1(CategoryPlot plot) {
		return ArrayConversion.list2Array(plot.getDataset().getRowKeys());
	}

	protected static String[] getChartRows1(XYPlot plot) {
		int l = plot.getDataset().getSeriesCount();
		String[] row = new String[l];
		for (int i = 0; i < l; i++)
			row[i] = String.valueOf(plot.getDataset().getSeriesKey(i));
		return row;
	}

	public static String[] getChartRows(XYDataset ds) {
		int l = ds.getSeriesCount();
		String[] row = new String[l];
		for (int i = 0; i < l; i++)
			row[i] = String.valueOf(ds.getSeriesKey(i));
		return row;
	}

	protected static String[] getChartRows1(PiePlot plot) {
		return ArrayConversion.list2Array(plot.getDataset().getKeys());
	}

	protected static String[] getChartRows1(PiePlot3D plot) {
		return ArrayConversion.list2Array(plot.getDataset().getKeys());
	}

	public static int[] getChartRowIndex1(JFreeChart jfchart) {
		if (jfchart.getPlot().getPlotType().equals("Category Plot"))
			return getChartRowIndex1(jfchart.getCategoryPlot());
		else if (jfchart.getPlot().getPlotType().equals("XY Plot"))
			return getChartRowIndex1(jfchart.getXYPlot());
		else
			return null;
	}

	protected static int[] getChartRowIndex1(CategoryPlot plot) {

		int l = plot.getDataset().getRowCount();
		int[] row = new int[l];
		for (int i = 0; i < l; i++)
			row[i] = i;
		return row;
	}

	protected static int[] getChartRowIndex1(XYPlot plot) {
		int l = plot.getDataset().getSeriesCount();
		int[] row = new int[l];
		for (int i = 0; i < l; i++)
			row[i] = i;
		return row;
	}

	public static String getChartColumn1(JFreeChart jfchart) {
		if (jfchart.getPlot().getPlotType().equals("Category Plot"))
			return getChartColumn1(jfchart.getCategoryPlot());
		else
			return getChartColumn1(jfchart.getXYPlot());
	}

	protected static String getChartColumn1(CategoryPlot plot) {
		int l = plot.getDataset().getColumnCount();
		String[] col = new String[l];
		for (int i = 0; i < l; i++)
			col[i] = (String) plot.getDataset().getColumnKey(i);
		return conversionUtil.ArrayConversion.array2String(col);
	}

	protected static String getChartColumn1(XYPlot plot) {
		String[] col = null;
		int l = plot.getDataset().getItemCount(0);
		col = new String[l];
		for (int i = 0; i < l; i++)
			col[i] = String.valueOf(plot.getDataset().getX(0, i));
		return conversionUtil.ArrayConversion.array2String(col);
	}

	public static int[] getChartColumnIndex1(JFreeChart jfchart) {
		if (jfchart.getPlot().getPlotType().equals("Category Plot"))
			return getChartColumnIndex1(jfchart.getCategoryPlot());
		else
			return getChartColumnIndex1(jfchart.getXYPlot());
	}

	protected static int[] getChartColumnIndex(Chart chart) {
		int[] col = new int[chart.getChartColumn().split(",").length];
		for (int i = 0; i < col.length; i++)
			col[i] = i;
		return col;
	}
	
	protected static int[] getChartColumnIndex1(CategoryPlot plot) {
		int l = plot.getDataset().getColumnCount();
		int[] col = new int[l];
		for (int i = 0; i < l; i++)
			col[i] = i;
		return col;
	}

	protected static int[] getChartColumnIndex1(XYPlot plot) {
		int[] col = null;
		int l = plot.getDataset().getItemCount(0);
		col = new int[l];
		for (int i = 0; i < l; i++)
			col[i] = i;
		return col;
	}

	public static double[] getXValues(XYPlot plot) {
		int c = plot.getDataset().getItemCount(0);
		double[] x = new double[c];
		for (int i = 0; i < c; i++)
			x[i] = plot.getDataset(0).getXValue(0, i);
		if (debug)
			System.out.println("X: " + Arrays.toString(x));
		return x;
	}

	public static double[][] getYValues(XYPlot plot) {
		int r = plot.getDataset(0).getSeriesCount();
		int c = plot.getDataset(0).getItemCount(0);
		double[] y[] = new double[r][c];
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++)
				y[i][j] = plot.getDataset(0).getYValue(i, j);
			if (debug)
				System.out.println("Y: " + Arrays.toString(y[i]));
		}
		return y;
	}

	public static void setPlotStrokeProp(Plot plot) {
		org.jfree.chart.plot.DrawingSupplier supplier = (new DefaultDrawingSupplier(
				DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE, DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
				DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
				DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
		plot.setDrawingSupplier(supplier);
	}

}
