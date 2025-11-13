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
import graphDisplay.ThumbnailUtilNew;

/**
 * Utility functions for JFreeChart datasets.
 * <p>
 * Author: TWU<br>
 * Created: 1/2/2016
 */
public class DatasetUtil {
	private static boolean debug = false;

	/**
	 * Converts a JFreeChart dataset to a 2D String array.
	 * 
	 * @param chart  JFreeChart object
	 * @param series Series index (-1 for all)
	 * @return 2D String array of data
	 */
	public static String[][] dataset2Data(JFreeChart chart, int series) {
		String[][] data = null;
		if (chart.getPlot() instanceof CategoryPlot) {
			CategoryDataset ds = chart.getCategoryPlot().getDataset();
			data = catDataset2Data(ds, series);
		} else if (chart.getPlot() instanceof XYPlot) {
			XYDataset ds = chart.getXYPlot().getDataset();
			data = xyDataset2Data(ds, series);
		}
		if (debug && data != null)
			System.out.println(
					"Dataset2Data:data " + data.length + " : " + data[0].length + " data: " + Arrays.toString(data[0]));
		return data;
	}

	/**
	 * Converts a CategoryDataset to a 2D String array.
	 * 
	 * @param ds     CategoryDataset
	 * @param series Series index (-1 for all)
	 * @return 2D String array
	 */
	public static String[][] catDataset2Data(CategoryDataset ds, int series) {
		String[][] data;
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

	/**
	 * Creates a normalized XY dataset from an existing XYDataset.
	 * 
	 * @param ds            XYDataset
	 * @param relativeIndex Index for normalization (-1 for no normalization)
	 * @return DefaultXYDataset
	 */
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

	/**
	 * Converts an XYDataset to a 2D String array.
	 * 
	 * @param ds     XYDataset
	 * @param series Series index (-1 for all)
	 * @return 2D String array
	 */
	public static String[][] xyDataset2Data(XYDataset ds, int series) {
		String[][] data;
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

	/**
	 * Converts a JFreeChart dataset to a 2D double array.
	 * 
	 * @param chart JFreeChart object
	 * @return 2D double array
	 */
	public static double[][] dataset2DataD(JFreeChart chart) {
		if (chart.getPlot() instanceof CategoryPlot) {
			return catDataset2DataD(chart.getCategoryPlot().getDataset());
		} else if (chart.getPlot() instanceof XYPlot) {
			return xyDataset2DataD(chart.getXYPlot().getDataset());
		} else {
			return null;
		}
	}

	/**
	 * Converts a CategoryDataset to a 2D double array.
	 * 
	 * @param ds CategoryDataset
	 * @return 2D double array
	 */
	public static double[][] catDataset2DataD(CategoryDataset ds) {
		double[][] dsD = new double[ds.getRowCount()][ds.getColumnCount()];
		for (int i = 0; i < ds.getRowCount(); i++)
			for (int j = 0; j < ds.getColumnCount(); j++)
				dsD[i][j] = ((Double) ds.getValue(i, j)).doubleValue();
		return dsD;
	}

	/**
	 * Converts an XYDataset to a 2D double array.
	 * 
	 * @param ds XYDataset
	 * @return 2D double array
	 */
	public static double[][] xyDataset2DataD(XYDataset ds) {
		double[][] data = new double[ds.getSeriesCount()][ds.getItemCount(0)];
		for (int i = 0; i < ds.getSeriesCount(); i++)
			for (int j = 0; j < ds.getItemCount(i); j++)
				data[i][j] = ds.getYValue(i, j);
		return data;
	}

	/**
	 * Converts a series from multiple Chart objects to a 2D String array.
	 * 
	 * @param chart  Array of Chart objects
	 * @param series Series index
	 * @return 2D String array
	 */
	public static String[][] oneSeriesDataset2Data(Chart[] chart, int series) {
		int idx = ThumbnailUtilNew.getFirstNonNullChart(chart);
		String[][] data = new String[chart.length][getChartColumnIndex(chart[idx]).length];
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

	/**
	 * Sums data from multiple Chart objects and returns as a 2D String array.
	 * 
	 * @param chart Array of Chart objects
	 * @return 2D String array
	 */
	public static String[][] dataset2Data(Chart[] chart) {
		int idx = ThumbnailUtilNew.getFirstNonNullChart(chart);
		double[][] sumData = dataset2DataD(chart[idx].getChart());
		for (int i = 1; i < chart.length; i++) {
			double[][] temp = dataset2DataD(chart[i].getChart());
			if (sumData.length != temp.length)
				return null;
			else
				sumData = sumDatasetData(sumData, temp);
		}
		return conversionUtil.DataConversion.Double2String(sumData);
	}

	/**
	 * Sums two 2D double arrays element-wise.
	 */
	private static double[][] sumDatasetData(double[][] sumdata, double[][] temp) {
		double[][] tot = sumdata.clone();
		for (int i = 0; i < tot.length; i++)
			for (int j = 0; j < tot[i].length; j++)
				tot[i][j] = tot[i][j] + temp[i][j];
		return tot;
	}

	/**
	 * Returns a subset of data from a 2D String array.
	 * 
	 * @param d Data array
	 * @param r Row indices
	 * @param c Column indices
	 * @return Subset 2D String array
	 */
	public static String[][] getSubsetData(String[][] d, int[] r, int[] c) {
		String[][] data = new String[r.length][c.length];
		for (int i = 0; i < r.length; i++)
			for (int j = 0; j < c.length; j++)
				data[i][j] = d[r[i]][c[j]];
		return data;
	}

	/**
	 * Returns a subset of columns from a CategoryDataset.
	 * 
	 * @param col   Column indices
	 * @param chart JFreeChart object
	 * @return Subset CategoryDataset
	 */
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

	/**
	 * Returns a subset of columns from an XYDataset.
	 * 
	 * @param col Column indices
	 * @param ds  XYDataset
	 * @return Subset AbstractDataset
	 */
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

	/**
	 * Returns a subset of rows from a JFreeChart dataset.
	 * 
	 * @param r     Row indices
	 * @param chart JFreeChart object
	 * @return Subset AbstractDataset
	 */
	public static AbstractDataset getSubsetRowDataset(int[] r, JFreeChart chart) {
		if (chart.getPlot() instanceof CategoryPlot) {
			CategoryDataset ds = chart.getCategoryPlot().getDataset();
			return getSubsetRowDataset(r, ds);
		} else if (chart.getPlot() instanceof XYPlot) {
			XYDataset ds = chart.getXYPlot().getDataset();
			return getSubsetRowDataset(r, ds);
		} else {
			return null;
		}
	}

	/**
	 * Returns a subset of rows from a CategoryDataset.
	 * 
	 * @param r  Row indices
	 * @param ds CategoryDataset
	 * @return Subset AbstractDataset
	 */
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

	/**
	 * Returns a subset of rows from an XYDataset.
	 * 
	 * @param r  Row indices
	 * @param ds XYDataset
	 * @return Subset AbstractDataset
	 */
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

	/**
	 * Returns a subset of columns from a DefaultBoxAndWhiskerCategoryDataset.
	 * 
	 * @param col   Column indices
	 * @param chart JFreeChart object
	 * @return Subset DefaultBoxAndWhiskerCategoryDataset
	 */
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

	/**
	 * Returns the difference between two CategoryDatasets for specified row keys.
	 * 
	 * @param ds1     First CategoryDataset
	 * @param ds2     Second CategoryDataset
	 * @param rowKeys List of row keys
	 * @return 2D String array of differences
	 */
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

	/**
	 * Returns the difference between two XYDatasets for specified row keys.
	 * 
	 * @param ds1     First XYDataset
	 * @param ds2     Second XYDataset
	 * @param rowKeys Array of row keys
	 * @return 2D String array of differences
	 */
	public static String[][] getDiffData(XYDataset ds1, XYDataset ds2, String[] rowKeys) {
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

	/**
	 * Helper to get data for specified row keys from a CategoryDataset.
	 */
	private static double[][] getCategoryData(CategoryDataset ds, List<String> l) {
		double[][] dsD = new double[l.size()][ds.getColumnCount()];
		List<String> l1 = ds.getRowKeys();
		for (int i = 0; i < l.size(); i++) {
			String o = l.get(i).trim();
			int n = l1.indexOf(o);
			if (n >= 0) {
				for (int j = 0; j < ds.getColumnCount(); j++)
					dsD[i][j] = ((Double) ds.getValue(n, j)).doubleValue();
			} else {
				for (int j = 0; j < ds.getColumnCount(); j++)
					dsD[i][j] = 0;
			}
		}
		return dsD;
	}

	/**
	 * Helper to get data for specified row keys from an XYDataset.
	 */
	private static double[][] getXYData(XYDataset ds, String[] l) {
		double[][] dsD = new double[l.length][ds.getItemCount(0)];
		String[] l1 = DatasetUtil.getChartRows(ds);
		int ic = ds.getItemCount(0);
		for (int i = 0; i < l.length; i++) {
			String o = l[i].trim();
			int n = Arrays.asList(l1).indexOf(o);
			if (n >= 0) {
				for (int j = 0; j < ic; j++)
					dsD[i][j] = ((Double) ds.getYValue(n, j)).doubleValue();
			} else {
				for (int j = 0; j < ic; j++)
					dsD[i][j] = 0;
			}
		}
		return dsD;
	}

	/**
	 * Returns a double array filled with zeros.
	 * 
	 * @param len Length of array
	 * @return double array
	 */
	public static double[] fillZero(int len) {
		double[] d = new double[len];
		Arrays.fill(d, 0);
		return d;
	}

	/**
	 * Returns statistics data from an array of Chart objects.
	 * 
	 * @param chart Array of Chart objects
	 * @return ArrayList of List of String arrays
	 */
	public static ArrayList<List<String[]>> getStatisticsData(Chart[] chart) {
		Chart[] chart1;
		if (chart[0].getChart().getPlot().getPlotType().contains("XY"))
			chart1 = ThumbnailUtilNew.createChart("chart.CategoryLineChart", -1, chart, null);
		else
			chart1 = chart.clone();
		ArrayList<List<String[]>> al = new ArrayList<>();
		for (int m = 0; m < chart1[0].getChart().getCategoryPlot().getDataset().getRowCount(); m++) {
			ArrayList<String[]> alC = new ArrayList<>();
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

	/**
	 * Converts an array of XYDatasets to an array of DefaultCategoryDatasets.
	 * 
	 * @param xydataset Array of XYDatasets
	 * @return Array of DefaultCategoryDatasets
	 */
	public static DefaultCategoryDataset[] XYDataset2CategoryDataset(XYDataset[] xydataset) {
		DefaultCategoryDataset[] dataset = new DefaultCategoryDataset[xydataset.length];
		for (int i = 0; i < dataset.length; i++)
			dataset[i] = XYDataset2CategoryDataset(xydataset[i]);
		return dataset;
	}

	/**
	 * Converts an XYDataset to a DefaultCategoryDataset.
	 * 
	 * @param xydataset XYDataset
	 * @return DefaultCategoryDataset
	 */
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

	/**
	 * Converts an array of CategoryDatasets to an array of DefaultXYDatasets.
	 * 
	 * @param catdataset Array of CategoryDatasets
	 * @return Array of DefaultXYDatasets
	 */
	public static DefaultXYDataset[] CategoryDataset2XYDataset(CategoryDataset[] catdataset) {
		DefaultXYDataset[] dataset = new DefaultXYDataset[catdataset.length];
		for (int i = 0; i < dataset.length; i++)
			dataset[i] = CategoryDataset2XYDataset(catdataset[i]);
		return dataset;
	}

	/**
	 * Converts a CategoryDataset to a DefaultXYDataset.
	 * 
	 * @param catdataset CategoryDataset
	 * @return DefaultXYDataset
	 */
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

	/**
	 * Returns row keys from a JFreeChart as a String array.
	 * 
	 * @param jfchart JFreeChart object
	 * @return Array of row keys
	 */
	public static String[] getChartRows1(JFreeChart jfchart) {
		String plotType = jfchart.getPlot().getPlotType();
		if (plotType.equals("Category Plot"))
			return getChartRows1(jfchart.getCategoryPlot());
		else if (plotType.equals("XY Plot"))
			return getChartRows1(jfchart.getXYPlot());
		else if (plotType.equals("Pie Plot"))
			return getChartRows1((PiePlot) jfchart.getPlot());
		else
			return null;
	}

	/**
	 * Returns row keys from a JFreeChart as a single String.
	 * 
	 * @param jfchart JFreeChart object
	 * @return String of row keys
	 */
	public static String getChartRow1(JFreeChart jfchart) {
		String plotType = jfchart.getPlot().getPlotType();
		if (plotType.equals("Category Plot"))
			return ArrayConversion.array2String(getChartRows1(jfchart.getCategoryPlot()));
		else if (plotType.equals("XY Plot"))
			return ArrayConversion.array2String(getChartRows1(jfchart.getXYPlot()));
		else if (plotType.equals("Pie Plot"))
			return ArrayConversion.array2String(getChartRows1((PiePlot) jfchart.getPlot()));
		else if (plotType.equals("Pie Plot 3D"))
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

	/**
	 * Returns row keys from an XYDataset as a String array.
	 * 
	 * @param ds XYDataset
	 * @return Array of row keys
	 */
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

	/**
	 * Returns row indices from a JFreeChart as an int array.
	 * 
	 * @param jfchart JFreeChart object
	 * @return Array of row indices
	 */
	public static int[] getChartRowIndex1(JFreeChart jfchart) {
		String plotType = jfchart.getPlot().getPlotType();
		if (plotType.equals("Category Plot"))
			return getChartRowIndex1(jfchart.getCategoryPlot());
		else if (plotType.equals("XY Plot"))
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

	/**
	 * Returns column keys from a JFreeChart as a single String.
	 * 
	 * @param jfchart JFreeChart object
	 * @return String of column keys
	 */
	public static String getChartColumn1(JFreeChart jfchart) {
		String plotType = jfchart.getPlot().getPlotType();
		if (plotType.equals("Category Plot"))
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
		int l = plot.getDataset().getItemCount(0);
		String[] col = new String[l];
		for (int i = 0; i < l; i++)
			col[i] = String.valueOf(plot.getDataset().getX(0, i));
		return conversionUtil.ArrayConversion.array2String(col);
	}

	/**
	 * Returns column indices from a JFreeChart as an int array.
	 * 
	 * @param jfchart JFreeChart object
	 * @return Array of column indices
	 */
	public static int[] getChartColumnIndex1(JFreeChart jfchart) {
		String plotType = jfchart.getPlot().getPlotType();
		if (plotType.equals("Category Plot"))
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
		int l = plot.getDataset().getItemCount(0);
		int[] col = new int[l];
		for (int i = 0; i < l; i++)
			col[i] = i;
		return col;
	}

	/**
	 * Returns X values from an XYPlot as a double array.
	 * 
	 * @param plot XYPlot
	 * @return Array of X values
	 */
	public static double[] getXValues(XYPlot plot) {
		int c = plot.getDataset().getItemCount(0);
		double[] x = new double[c];
		for (int i = 0; i < c; i++)
			x[i] = plot.getDataset(0).getXValue(0, i);
		if (debug)
			System.out.println("X: " + Arrays.toString(x));
		return x;
	}

	/**
	 * Returns Y values from an XYPlot as a 2D double array.
	 * 
	 * @param plot XYPlot
	 * @return 2D array of Y values
	 */
	public static double[][] getYValues(XYPlot plot) {
		int r = plot.getDataset(0).getSeriesCount();
		int c = plot.getDataset(0).getItemCount(0);
		double[][] y = new double[r][c];
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++)
				y[i][j] = plot.getDataset(0).getYValue(i, j);
			if (debug)
				System.out.println("Y: " + Arrays.toString(y[i]));
		}
		return y;
	}

	/**
	 * Sets the plot stroke properties for a JFreeChart Plot.
	 * 
	 * @param plot Plot object
	 */
	public static void setPlotStrokeProp(Plot plot) {
		org.jfree.chart.plot.DrawingSupplier supplier = new DefaultDrawingSupplier(
				DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE, DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
				DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
				DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
		plot.setDrawingSupplier(supplier);
	}
}
