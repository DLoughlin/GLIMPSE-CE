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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.CategoryPointerAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataset;


/**
 * The class handle utility functions for JFreechart. 
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class ChartUtil {
	private static boolean debug = false;

	public static DefaultDrawingSupplier setDrawingSupplier(String className, int color[], int[] ls) {
		DefaultDrawingSupplier supplier = null;
		BasicStroke[] s = new BasicStroke[ls.length];
		Paint paint[] = new Paint[color.length];
		for (int i = 0; i < paint.length; i++)
			paint[i] = new Color(color[i]);
		for (int i = 0; i < s.length; i++) {
			s[i] = LegendUtil.getLineStroke(ls[i]);
		}
		if (className.contains("LineChart")) {
			supplier = (new DefaultDrawingSupplier(paint,
					// DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
					// DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
					s, DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
		} else {
			supplier = (new DefaultDrawingSupplier(paint,
					// DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, s,
					// DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
					DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
		}
		return supplier;
	}

	public static Object creatNewInstance(Class<?> t, Object[] param) {
		Object jc = null;

		Constructor<?>[] con = t.getConstructors();

		for (int i = 0; i < con.length; i++) {
			Class<?>[] pl = con[i].getParameterTypes();
			if (pl.length == 0) {
				try {
					jc = con[i].newInstance();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			if (pl.length == param.length) {
				try {
					jc = con[i].newInstance(param);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		return jc;
	}

	public static int[] findDataset(JFreeChart chart, String key) {
		int[] datasetSeries = { -1, -1 };
		if (chart.getPlot().getPlotType().contains("XY")) {
			XYPlot plot = chart.getXYPlot();
			for (int i = 0; i < plot.getDatasetCount(); i++) {
				XYDataset dataset = plot.getDataset(i);
				for (int j = 0; j < dataset.getSeriesCount(); j++) {
					if (dataset.indexOf(key) != -1) {
						datasetSeries[0] = i;
						datasetSeries[1] = dataset.indexOf(key);
						break;
					}
				}
			}
		} else if (chart.getPlot().getPlotType().contains("Category")) {
			CategoryPlot plot = chart.getCategoryPlot();
			for (int i = 0; i < plot.getDatasetCount(); i++) {
				CategoryDataset dataset = plot.getDataset(i);
				for (int j = 0; j < dataset.getRowCount(); j++) {
					if ((datasetSeries[1] = dataset.getRowIndex(key.trim())) != -1) {
						datasetSeries[0] = i;
						break;
					}
				}
			}
		}
		return datasetSeries;
	}

	public static int[] findDataset(XYDataset[] dataset, String key) {
		int[] datasetSeries = { -1, -1 };
		for (int i = 0; i < dataset.length; i++) {
			for (int j = 0; j < dataset[i].getSeriesCount(); j++) {
				if ((datasetSeries[1] = dataset[i].indexOf(key)) != -1) {
					datasetSeries[0] = i;
					break;
				}
			}
		}
		return datasetSeries;
	}

	public static int[] findDataset(CategoryDataset[] dataset, String key) {
		int[] datasetSeries = { -1, -1 };
		for (int i = 0; i < dataset.length; i++) {
			for (int j = 0; j < dataset[i].getRowCount(); j++) {
				if ((datasetSeries[1] = dataset[i].getRowIndex(key.trim())) != -1) {
					datasetSeries[0] = i;
					break;
				}
			}
		}
		return datasetSeries;
	}

	public static int findSeries(XYDataset dataset, String key) {
		int series = 0;
		for (int j = 0; j < dataset.getSeriesCount(); j++) {
			if (dataset.indexOf(key) != -1) {
				series = dataset.indexOf(key);
				break;
			}
		}
		return series;
	}

	public static int findSeries(CategoryDataset dataset, String key) {
		int series = 0;
		for (int j = 0; j < dataset.getRowCount(); j++) {
			if (dataset.getRowIndex(key) != -1) {
				series = dataset.getRowIndex(key);
				break;
			}
		}
		return series;
	}

	public static Integer[] getVisibleDatasetSeries(JFreeChart chart) {
		ArrayList<Integer> al = new ArrayList<Integer>();
		if (chart.getPlot().getPlotType().contains("XY")) {
			XYPlot plot = chart.getXYPlot();
			for (int i = 0; i < plot.getDatasetCount(); i++) {
				XYDataset dataset = plot.getDataset(i);
				for (int j = 0; j < dataset.getSeriesCount(); j++) {
					XYItemRenderer renderer = chart.getXYPlot().getRenderer(i);
					if (renderer.getItemVisible(j, 0))
						al.add(Integer.valueOf(j));
				}
			}
		} else if (chart.getPlot().getPlotType().contains("Category")) {
			CategoryPlot plot = chart.getCategoryPlot();
			for (int i = 0; i < plot.getDatasetCount(); i++) {
				CategoryDataset dataset = plot.getDataset(i);
				for (int j = 0; j < dataset.getRowCount(); j++) {
					CategoryItemRenderer renderer = chart.getCategoryPlot().getRenderer(i);
					if (renderer.getItemVisible(j, 0))
						al.add(Integer.valueOf(j));
				}
			}
		}
		return al.toArray(new Integer[0]);
	}

	public static void setVisibleDatasetSeries(JFreeChart chart) {
		if (chart.getPlot().getPlotType().contains("XY")) {
			XYPlot plot = chart.getXYPlot();
			for (int i = 0; i < plot.getDatasetCount(); i++) {
				XYDataset dataset = plot.getDataset(i);
				for (int j = 0; j < dataset.getSeriesCount(); j++) {
					XYItemRenderer renderer = chart.getXYPlot().getRenderer(i);
					if (renderer.getSeriesVisible(j)) {
						renderer.setSeriesVisible(j, true);
						renderer.setSeriesVisibleInLegend(j, true);
					}
				}
			}
		} else if (chart.getPlot().getPlotType().contains("Category")) {
			CategoryPlot plot = chart.getCategoryPlot();
			for (int i = 0; i < plot.getDatasetCount(); i++) {
				CategoryDataset dataset = plot.getDataset(i);
				for (int j = 0; j < dataset.getRowCount(); j++) {
					CategoryItemRenderer renderer = chart.getCategoryPlot().getRenderer(i);
					if (renderer.getSeriesVisible(j)) {
						renderer.setSeriesVisible(j, true);
						renderer.setSeriesVisibleInLegend(j, true);
					}
				}
			}
		}
	}

	public static void setDatasetSeriesVisible(JFreeChart chart) {
		if (chart.getPlot().getPlotType().contains("XY")) {
			XYPlot plot = chart.getXYPlot();
			for (int i = 0; i < plot.getDatasetCount(); i++) {
				XYDataset dataset = plot.getDataset(i);
				for (int j = 0; j < dataset.getSeriesCount(); j++) {
					XYItemRenderer renderer = chart.getXYPlot().getRenderer(i);
					renderer.setSeriesVisible(j, true);
					renderer.setSeriesVisibleInLegend(j, true);
				}
			}

		} else if (chart.getPlot().getPlotType().contains("Category")) {
			CategoryPlot plot = chart.getCategoryPlot();
			for (int i = 0; i < plot.getDatasetCount(); i++) {
				CategoryDataset dataset = plot.getDataset(i);
				for (int j = 0; j < dataset.getRowCount(); j++) {
					CategoryItemRenderer renderer = chart.getCategoryPlot().getRenderer(i);
					renderer.setSeriesVisible(j, true);
					renderer.setSeriesVisibleInLegend(j, true);
				}
			}
		}
	}

	public static boolean isDatasetSeriesInvisible(JFreeChart chart) {
		boolean b = false;
		if (chart.getPlot().getPlotType().contains("XY")) {
			XYPlot plot = chart.getXYPlot();
			for (int i = 0; i < plot.getDatasetCount(); i++) {
				XYDataset dataset = plot.getDataset(i);
				for (int j = 0; j < dataset.getSeriesCount(); j++) {
					XYItemRenderer renderer = chart.getXYPlot().getRenderer(i);
					if (renderer.isSeriesVisible(j)) {
						b = true;
						break;
					}
				}
			}

		} else if (chart.getPlot().getPlotType().contains("Category")) {
			CategoryPlot plot = chart.getCategoryPlot();
			for (int i = 0; i < plot.getDatasetCount(); i++) {
				CategoryDataset dataset = plot.getDataset(i);
				for (int j = 0; j < dataset.getRowCount(); j++) {
					CategoryItemRenderer renderer = chart.getCategoryPlot().getRenderer(i);
					if (renderer.isSeriesVisible(j)) {
						b = true;
						break;
					}
				}
			}
		}
		return b;
	}

	public static void setSubTitle(JFreeChart chart, String[] subTitle) {
		if (subTitle[0] != null)
			chart.setTitle(subTitle[0]);

		for (int i = 1; subTitle != null && i < subTitle.length; i++) {
			TextTitle title = new TextTitle(subTitle[i], new Font("SansSerif", 1, 14));
			title.visible = true;
			chart.addSubtitle(i - 1, title);
		}
	}

	public static void removeSubTitle(JFreeChart chart, String[] subTitle) {
		TextTitle title = null;
		if (subTitle[0] != null)
			chart.setTitle("");

		for (int i = 1; subTitle != null && i < subTitle.length; i++) {
			title = new TextTitle(subTitle[i], new Font("SansSerif", 1, 14));
			chart.removeSubtitle(title);
		}
	}

	public static int getSubTitleIndex(JFreeChart chart, Title subTitle) {
		return chart.getSubtitles().indexOf(subTitle);
	}

	public static CategoryPointerAnnotation createAnnotation(String text, String column, double yValue) {
		CategoryPointerAnnotation annotation = null;
		try {
			annotation = new CategoryPointerAnnotation(text, column.trim(), yValue, -2.356194490192345); //-0.78539816339744828D);// 
			Font font = new Font("SansSerif", 0, 10);
			annotation.setFont(font);
			annotation.setTextAnchor(TextAnchor.BOTTOM_LEFT);
		} catch (java.lang.IllegalArgumentException e) {
			System.out.println("ChartUtil::CategoryPointerAnnotation:Apply Annotation Failed");
		}
		return annotation;
	}

	public static XYPointerAnnotation createAnnotation(String text, double xValue, double yValue) {
		if (debug)
			System.out.println("x: " + xValue + " y: " + yValue + " text: " + text);
		XYPointerAnnotation annotation = null;
		try {
			annotation = new XYPointerAnnotation(text, xValue, yValue, -0.78539816339744828D);// -2.356194490192345);
			Font font = new Font("SansSerif", 0, 10);
			annotation.setFont(font);
			annotation.setTextAnchor(TextAnchor.BOTTOM_LEFT);
		} catch (java.lang.IllegalArgumentException e) {
			System.out.println("ChartUtil::CategoryPointerAnnotation:Apply Annotation Failed");
		}
		return annotation;
	}

	// Followings are Annotation per item for all series
	public static CategoryPointerAnnotation[] createAnnotation(String[] text, String[] column, String chartClassName,
			double[][] data) {
		CategoryPointerAnnotation[] annotation = new CategoryPointerAnnotation[text.length];
		double[] y = null;
		try {
			if (chartClassName.contains("Stacked"))
				y = getAnnotationTextTableLocation(data);
			else if (chartClassName.contains("Line"))
				y = getAnnotationTextMaxLocation(data);
			else {
				if (chartClassName.contains("3D"))
					y = getAnnotationTextLocation(data[0], true);
				else
					y = getAnnotationTextLocation(data[0], false);
			}
			for (int i = 0; i < text.length; i++) {
				Font font = new Font("SansSerif", 0, 9);
				annotation[i] = new CategoryPointerAnnotation(text[i], column[i].trim(), y[i], -2.356194490192345);
				annotation[i].setFont(font);
				annotation[i].setTextAnchor(TextAnchor.TOP_LEFT);
			}
		} catch (java.lang.IllegalArgumentException e) {
			System.out.println("ChartUtil::CategoryPointerAnnotation:Apply Annotation Failed");
		}
		return annotation;
	}

	public static XYPointerAnnotation[] createAnnotation(String[] text, String[] x, String[] column,
			String chartClassName, double[][] data) {
		XYPointerAnnotation[] annotation = new XYPointerAnnotation[text.length];
		double[] y = getAnnotationTextMaxLocation(data);
		try {
			for (int i = 0; i < text.length; i++) {
				Font font = new Font("SansSerif", 0, 9);
				if (text[i] == null)
					text[i] = "";
				double temp = Double.valueOf(x[i].trim()).doubleValue();
				if (debug)
					System.out.println("x: " + temp + " y: " + y[i] + " text: " + text);
				annotation[i] = new XYPointerAnnotation(
						column[i]+" "+text[i], temp, y[i],
						-2.356194490192345);
				annotation[i].setFont(font);
				annotation[i].setTextAnchor(TextAnchor.TOP_LEFT);
			}
		} catch (java.lang.IllegalArgumentException e) {
			System.out.println("ChartUtil::CategoryPointerAnnotation:Apply Annotation Failed");
		}
		return annotation;
	}

	public static double[] getAnnotationTextLocation(double[] data, boolean line3D) {
		double[] annotationLoc = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			if (line3D) {
				annotationLoc[i] = new Double(data[i]).doubleValue() * 1.1;
			} else
				annotationLoc[i] = new Double(data[i]).doubleValue();
		}
		return annotationLoc;
	}

	public static double[] getAnnotationTextMaxLocation(double[][] data) {
		double[] annotationLoc = new double[data[0].length];
		for (int i = 0; i < data[0].length; i++) {
			double temp = 0.0;
			for (int j = 0; j < data.length; j++)
				temp = Math.max(temp, new Double(data[j][i]).doubleValue());
			annotationLoc[i] = temp;
		}
		return annotationLoc;
	}

	public static double[] getAnnotationTextTableLocation(double[][] data) {
		double[] annotationLoc = new double[data[0].length];
		for (int i = 0; i < data[0].length; i++) {
			double temp = 0.0;
			for (int j = 0; j < data.length; j++)
				temp += new Double(data[j][i]).doubleValue();
			annotationLoc[i] = temp;
		}
		return annotationLoc;
	}

	public static double[] getAnnotationTextTableLocation(double[][] data, int level) {
		double[] annotationLoc = new double[data[0].length];
		for (int i = 0; i < data[0].length; i++) {
			double temp = 0.0;
			for (int j = 0; j <= level; j++)
				temp += new Double(data[j][i]).doubleValue();
			annotationLoc[i] = temp;
		}
		return annotationLoc;
	}

	public static void paintSeries(CategoryPlot plot, Paint[] paint) {
		if (paint != null) {
			int k = 0;
			for (int j = plot.getRendererCount() - 1; j >= 0; j--) {
				AbstractRenderer renderer = (AbstractRenderer) plot.getRenderer(j);
				renderer.setAutoPopulateSeriesPaint(false);
				renderer.setAutoPopulateSeriesFillPaint(false);
				int count = plot.getDataset(j).getRowCount();
				for (int i = 0; i < count; i++)
					renderer.setSeriesPaint(count - 1 - i, paint[k + i]);
				k += count;
			}
		}
	}

	public static void paintaSeries(AbstractRenderer renderer, int series, Paint paint) {
		if (paint != null)
			renderer.setSeriesPaint(series, paint);
	}

	public static void paintSeries(XYPlot plot, Paint[] paint) {
		if (paint != null) {
			int k = 0;
			for (int j = plot.getRendererCount() - 1; j >= 0; j--) {
				AbstractRenderer renderer = (AbstractRenderer) plot.getRenderer(j);
				renderer.setAutoPopulateSeriesPaint(false);
				renderer.setAutoPopulateSeriesFillPaint(false);
				int count = plot.getDataset(j).getSeriesCount();
				for (int i = 0; i < count; i++)
					renderer.setSeriesPaint(count - 1 - i, paint[k + i]);
				k += count;
			}
		}
	}

	public static LegendItemCollection getLegendItemsFromChart(JFreeChart chart) {
		LegendItemCollection legenditemcollection = null;
		if (chart.getPlot().getPlotType().contains("XY"))
			legenditemcollection = chart.getXYPlot().getFixedLegendItems();
		else
			legenditemcollection = chart.getCategoryPlot().getFixedLegendItems();
		return legenditemcollection;
	}

	public static ArrayList<Object[]> getLegendValueFromChart(String graphName,
			LegendItemCollection legenditemcollection) {
		ArrayList<Object[]> legendValue = new ArrayList<Object[]>();
		if (legenditemcollection != null) {
			Paint[] paint = new Paint[legenditemcollection.getItemCount()];
			String[] legend = new String[legenditemcollection.getItemCount()];
			Integer[] pattern = new Integer[legenditemcollection.getItemCount()];
			Iterator<?> k = legenditemcollection.iterator();
			int i = 0;
			while (k.hasNext()) {
				LegendItem l = (LegendItem) k.next();
				legend[i] = l.getLabel();
				paint[i] = l.getFillPaint();
				i++;
			}
			int[] temp = { 1, 1 };
			for (int j = 0; j < temp.length; j++)
				pattern[j] = new Integer(temp[j]);
			legendValue.add(legend);
			legendValue.add(paint);
			legendValue.add(pattern);
		}
		return legendValue;
	}

	public static double findRangeMaxForCharts(ValueAxis yAxis, double curMax) {
		return Math.max(yAxis.getUpperBound(), curMax);
	}

	public static double findRangeMinForCharts(ValueAxis yAxis, double curMin) {
		return curMin = Math.min(yAxis.getUpperBound(), curMin);
	}

}
