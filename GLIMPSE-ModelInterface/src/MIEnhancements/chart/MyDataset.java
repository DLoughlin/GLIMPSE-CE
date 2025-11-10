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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYZDataset;

/**
 * Utility class for creating various JFreeChart datasets from tabular data.
 *
 * Author: TWU
 * Created: 1/2/2016
 */
public class MyDataset {
    private static boolean debug = false;

    /**
     * Default constructor.
     */
    public MyDataset() {}

    /**
     * Creates a DefaultCategoryDataset from a 2D String array.
     * @param data 2D array of data values
     * @param category Array of row keys
     * @param items Array of column keys
     * @return DefaultCategoryDataset
     */
    public DefaultCategoryDataset createCategoryDataset(String[][] data, String[] category, String[] items) {
        if (data == null) return null;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            if (debug)
                System.out.println("createCategoryDataset::r: " + " d:" + data.length + "  : " + data[0].length
                        + Arrays.toString(category) + " c: " + Arrays.toString(items));
            for (int i = 0; i < category.length; i++) {
                for (int j = 0; j < items.length; j++) {
                    if (debug)
                        System.out.println("createCategoryDataset::r: " + category[i] + " c: " + items[j] + " d:" + data[i][j]);
                    // Aggregate function
                    if (data[i].length == 1)
                        dataset.addValue(Double.parseDouble(data[i][0]), category[i].trim(), items[j].trim());
                    else
                        dataset.addValue(Double.parseDouble(data[i][j]), category[i].trim(), items[j].trim());
                }
            }
        } catch (Exception e) {
            // Log or handle exception if needed
        }
        return dataset;
    }

    /**
     * Creates a DefaultCategoryDataset from a 2D String array (legacy version).
     */
    public DefaultCategoryDataset createCategoryDatasetOld(String[][] data, String[] category, String[] items) {
        if (data == null) return null;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            if (debug)
                System.out.println("createCategoryDataset::r: " + " d:" + data.length + "  : " + data[0].length
                        + Arrays.toString(category) + " c: " + Arrays.toString(items));
            for (int i = 0; i < category.length; i++) {
                for (int j = 0; j < items.length; j++) {
                    if (debug)
                        System.out.println("createCategoryDataset::r: " + category[i] + " c: " + items[j] + " d:" + data[i][j]);
                    if (data[i].length == 1)
                        dataset.addValue(Double.parseDouble(data[i][0]), category[i].trim(), items[j].trim());
                    else
                        dataset.addValue(Double.parseDouble(data[i][j]), category[i].trim(), items[j].trim());
                }
            }
        } catch (Exception e) {
            // Log or handle exception if needed
        }
        return dataset;
    }

    /**
     * Creates a DefaultCategoryDataset with values divided by a reference column.
     */
    public DefaultCategoryDataset createCategoryDataset(String[][] data, String[] category, String[] items, int relativeIndex) {
        if (data == null) return null;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < category.length; i++) {
            double divData = Double.parseDouble(data[i][relativeIndex]);
            divData = divData == 0 ? 1 : divData;
            for (int j = 0; j < items.length; j++)
                dataset.addValue(Double.parseDouble(data[i][j]) / divData, category[i].trim(), items[j].trim());
        }
        return dataset;
    }

    /**
     * Creates a ratio DefaultCategoryDataset from an existing dataset.
     */
    public DefaultCategoryDataset createRatioCategoryDataset(DefaultCategoryDataset ds, int relativeIndex) {
        DefaultCategoryDataset ds1 = new DefaultCategoryDataset();
        for (int i = 0; i < ds.getRowCount(); i++) {
            double divData = ds.getValue(i, relativeIndex).doubleValue();
            divData = divData == 0 ? 1 : divData;
            for (int j = 0; j < ds.getColumnCount(); j++) {
                ds1.addValue(ds.getValue(i, j).doubleValue() / divData, ds.getRowKey(i).toString().trim(), ds.getColumnKey(j).toString().trim());
            }
        }
        return ds1;
    }

    /**
     * Creates a difference DefaultCategoryDataset from an existing dataset.
     */
    public DefaultCategoryDataset createDiffCategoryDataset(DefaultCategoryDataset ds, int relativeIndex) {
        DefaultCategoryDataset ds1 = new DefaultCategoryDataset();
        for (int i = 0; i < ds.getRowCount(); i++) {
            double divData = ds.getValue(i, relativeIndex).doubleValue();
            for (int j = 0; j < ds.getColumnCount(); j++) {
                ds1.addValue(ds.getValue(i, j).doubleValue() - divData, ds.getRowKey(i).toString().trim(), ds.getColumnKey(j).toString().trim());
            }
        }
        return ds1;
    }

    /**
     * Creates an XYDataset from a 2D String array.
     */
    public XYDataset createXYDataset(String[][] data, String[] series, String[] items) {
        XYSeriesCollection collection = new XYSeriesCollection();
        if (data == null) return null;
        for (int i = 0; i < series.length; i++) {
            XYSeries s1 = new XYSeries(series[i], true, true);
            for (int j = 0; j < items.length; j++) {
                if (data[i][j] != null && !data[i][j].equals("null"))
                    s1.add(Double.parseDouble(items[j].trim()), Double.parseDouble(data[i][j].trim()));
            }
            collection.addSeries(s1);
        }
        return collection;
    }

    /**
     * Creates a DefaultXYDataset from a 2D String array.
     */
    public DefaultXYDataset createXYDefaultDataset(String[][] data, String[] series, String[] items) {
        DefaultXYDataset collection = new DefaultXYDataset();
        if (data == null) return null;
        double[][] d1 = conversionUtil.DataConversion.String2Double(data);
        for (int i = 0; i < series.length; i++) {
            double[][] d = new double[2][items.length];
            for (int j = 0; j < items.length; j++) {
                d[0][j] = Double.valueOf(items[j].trim());
                d[1][j] = d1[i][j];
            }
            collection.addSeries(series[i], d);
        }
        return collection;
    }

    /**
     * Creates an XYDataset with values divided by a reference column.
     */
    public XYDataset createXYDataset(String[][] data, String[] series, String[] items, int rIndex) {
        XYSeriesCollection collection = new XYSeriesCollection();
        if (data == null) return null;
        for (int i = 0; i < series.length; i++) {
            XYSeries s1 = new XYSeries(series[i], true, true);
            double divData = Double.parseDouble(data[i][rIndex]);
            divData = divData == 0 ? 1 : divData;
            for (int j = 0; j < items.length; j++)
                s1.add(Double.parseDouble(items[j].trim()), Double.parseDouble(data[i][j].trim()) / divData);
            collection.addSeries(s1);
        }
        return collection;
    }

    /**
     * Creates an array of DefaultXYDataset objects from an array of XYDataset objects.
     */
    public DefaultXYDataset[] createXYDataset(XYDataset[] ds, int relativeIndex) {
        if (ds == null) return null;
        DefaultXYDataset[] dataset = new DefaultXYDataset[ds.length];
        for (int l = 0; l < ds.length; l++) {
            DefaultXYDataset collection = new DefaultXYDataset();
            for (int i = 0; i < ds[l].getSeriesCount(); i++) {
                double[][] d = new double[2][ds[0].getItemCount(0)];
                double divData = ds[l].getYValue(i, relativeIndex);
                divData = divData == 0 ? 1 : divData;
                for (int j = 0; j < ds[l].getItemCount(i); j++) {
                    d[0][j] = ds[l].getXValue(i, j);
                    d[1][j] = ds[l].getYValue(i, j) / divData;
                }
                collection.addSeries(ds[l].getSeriesKey(i), d);
            }
            dataset[l] = collection;
        }
        return dataset;
    }

    /**
     * Creates an XYDataset from a DefaultXYDataset, dividing values by a reference column.
     */
    public XYDataset createXYDataset(DefaultXYDataset ds, int relativeIndex) {
        if (ds == null) return null;
        XYSeriesCollection collection = new XYSeriesCollection();
        for (int i = 0; i < ds.getSeriesCount(); i++) {
            XYSeries s1 = new XYSeries(ds.getSeriesKey(i), true, true);
            double divData = ds.getYValue(i, relativeIndex);
            divData = divData == 0 ? 1 : divData;
            for (int j = 0; j < ds.getItemCount(i); j++)
                s1.add(ds.getXValue(i, j), ds.getYValue(i, j) / divData);
            collection.addSeries(s1);
        }
        return collection;
    }

    /**
     * Creates an XYZDataset from a 2D String array.
     */
    public XYZDataset createXYZDataset(String[][] data, String[] series, String[] items) {
        if (data == null) return null;
        DefaultXYZDataset dataset = new DefaultXYZDataset();
        for (int i = 0; i < series.length; i++) {
            double[] x = new double[items.length];
            double[] y = new double[items.length];
            double[] z = new double[items.length];
            for (int j = 0; j < items.length; j++) {
                x[j] = Double.parseDouble(items[j].trim());
                y[j] = Double.parseDouble(data[i][j].trim());
                // z[j] = Double.parseDouble(ConfigCache.year[j].trim()); // Uncomment if needed
            }
            double[][] seriesData = { x, y, z };
            dataset.addSeries(series[i], seriesData);
        }
        return dataset;
    }

    /**
     * Creates a TableXYDataset from a 2D String array.
     */
    public TableXYDataset createTableXYDataset(String[][] data, String[] series, String[] items) {
        if (data == null) return null;
        DefaultTableXYDataset dataset = new DefaultTableXYDataset();
        for (int i = 0; i < series.length - 1; i++) {
            XYSeries s1 = new XYSeries(series[i], true, true);
            for (int j = 0; j < items.length; j++)
                s1.add(Double.parseDouble(items[j].trim()), Double.parseDouble(data[i][j].trim()));
            dataset.addSeries(s1);
        }
        return dataset;
    }

    /**
     * Creates an IntervalXYDataset from a 2D String array.
     */
    public IntervalXYDataset createIntervalXYDataset(String[][] data, String[] series, String[] items) {
        if (data == null) return null;
        DefaultTableXYDataset dataset = new DefaultTableXYDataset();
        for (int i = 0; i < series.length - 1; i++) {
            XYSeries s1 = new XYSeries(series[i], true, true);
            for (int j = 0; j < items.length; j++)
                s1.add(Double.parseDouble(items[j].trim()), Double.parseDouble(data[i][j].trim()));
            dataset.addSeries(s1);
        }
        return dataset;
    }

    /**
     * Creates a HistogramDataset from a 2D double array.
     */
    public IntervalXYDataset createHistogramDataset(double[][] data, String[] series, int bin) {
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.RELATIVE_FREQUENCY);
        for (int i = 0; i < series.length; i++) {
            dataset.addSeries(series[i], data[i], bin);
        }
        return dataset;
    }

    /**
     * Creates a TimeSeriesCollection from a 2D String array.
     */
    public TimeSeriesCollection createTimeSeriesDataset(String[][] data, String[] series, String[] items) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        if (data == null) return null;
        for (int i = 0; i < series.length; i++) {
            TimeSeries s1 = new TimeSeries(series[i]);
            for (int j = 0; j < items.length; j++) {
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = formatter.parse(items[j].trim());
                    if (!data[i][j].equals("null")) {
                        s1.add(new Day(date), Double.parseDouble(data[i][j].trim()));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            dataset.addSeries(s1);
        }
        return dataset;
    }

    /**
     * Creates a BoxAndWhiskerCategoryDataset from a nested ArrayList of String arrays.
     */
    public BoxAndWhiskerCategoryDataset createBoxAndWhiskerCategoryDataset(ArrayList<List<String[]>> al, String[] series, String[] items) {
        if (al == null) return null;
        DefaultBoxAndWhiskerCategoryDataset result = new DefaultBoxAndWhiskerCategoryDataset();
        for (int s = 0; s < series.length; s++) {
            for (int c = 0; c < items.length; c++) {
                List<Double> values = getListValues(al.get(s).get(c));
                result.add(values, series[s], items[c]);
            }
        }
        return result;
    }

    /**
     * Converts a String array to a List of Doubles.
     */
    private List<Double> getListValues(String[] data) {
        List<Double> values = new ArrayList<>();
        for (String datum : data)
            values.add(Double.valueOf(datum));
        return values;
    }

    /**
     * Creates a PieDataset from a 2D String array.
     */
    public PieDataset createPieDataset(String[][] data, boolean column, String[] items, int idx) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        if (data == null) return null;
        if (column) {
            for (int i = 0; i < items.length; i++) {
                dataset.setValue(items[i], Double.parseDouble(data[i][idx]));
            }
        } else {
            for (int i = 0; i < items.length; i++) {
                dataset.setValue(items[i], Double.parseDouble(data[idx][i]));
            }
        }
        return dataset;
    }

    /**
     * Creates a PieDataset from a DefaultCategoryDataset.
     */
    public PieDataset createPieDataset(DefaultCategoryDataset ds, boolean column, int itemIndex) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        if (ds == null) return null;
        if (column) {
            for (int i = 0; i < ds.getRowCount(); i++) {
                dataset.setValue(ds.getRowKey(i), ds.getValue(i, itemIndex));
            }
        } else {
            for (int i = 0; i < ds.getColumnCount(); i++) {
                dataset.setValue(ds.getColumnKey(i), ds.getValue(itemIndex, i));
            }
        }
        return dataset;
    }
}