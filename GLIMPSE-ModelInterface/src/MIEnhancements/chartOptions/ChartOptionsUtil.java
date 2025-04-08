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
package chartOptions;

import java.awt.Paint;
import java.awt.TexturePaint;
import java.util.Arrays;

import javax.swing.JOptionPane;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.util.TableOrder;
//import org.jfree.chart.renderer.category.StackedBarRenderer3D;
//import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYDataset;

import chart.CategoryPieChart;
import chart.DatasetUtil;
import chart.LegendUtil;

/**
 * The class to handle utility functions for a chart options.
 * Referenced classes of package graphDisplay: AChartDisplay, DataPanel
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class ChartOptionsUtil {

	public static JFreeChart showPieChart(String path, String graphName, String meta, String[] axis_name_unit,
			JFreeChart jfchart) {
		JFreeChart jfreechart = null;
		String[] row = null;
		String[] column = null;
		Paint[] paint = null;
		int selIndex;

		String chartType = chartTypeDialog();
		TableOrder tableOrder = tableOrderDialog();

		if (jfchart.getPlot().getPlotType().contains("XY")) {
			XYDataset[] xyds = { jfchart.getXYPlot().getDataset() };
			DefaultCategoryDataset dataset = DatasetUtil.XYDataset2CategoryDataset(xyds[0]);
			row = conversionUtil.ArrayConversion.list2Array(dataset.getRowKeys());
			column = conversionUtil.ArrayConversion.list2Array(dataset.getColumnKeys());
			paint = getPaintArray(LegendUtil.getLegendPaint(jfchart.getXYPlot().getFixedLegendItems()));
			selIndex = selectedDialog(chartType, tableOrder, row, column);
			jfreechart = new CategoryPieChart(path, graphName, meta, jfchart.getTitle().getText(), axis_name_unit, row,
					paint, tableOrder, selIndex, dataset, chartType).getChart();
		} else if (jfchart.getPlot().getPlotType().contains("Category")) {
			DefaultCategoryDataset dataset = (DefaultCategoryDataset) jfchart.getCategoryPlot().getDataset();
			row = conversionUtil.ArrayConversion.list2Array(dataset.getRowKeys());
			column = conversionUtil.ArrayConversion.list2Array(dataset.getColumnKeys());
			paint = getPaintArray(LegendUtil.getLegendPaint(jfchart.getCategoryPlot().getFixedLegendItems()));
			selIndex = selectedDialog(chartType, tableOrder, row, column);
			jfreechart = new CategoryPieChart(path, graphName, meta, jfchart.getTitle().getText(), axis_name_unit, row,
					paint, tableOrder, selIndex, dataset, chartType).getChart();
		} else if (jfchart.getPlot().getPlotType().contains("Pie")) {
			PieDataset dataset = ((PiePlot) jfchart.getPlot()).getDataset();
			row = conversionUtil.ArrayConversion.list2Array(dataset.getKeys());
			column = row.clone();
			paint = getPaintArray(LegendUtil.getLegendPaint(((PiePlot) jfchart.getPlot()).getLegendItems()));
			selIndex = selectedDialog(chartType, tableOrder, row, column);
			jfreechart = new CategoryPieChart(path, graphName, meta, jfchart.getTitle().getText(), axis_name_unit, row,
					paint, tableOrder, selIndex, dataset, chartType).getChart();
		}

		return jfreechart;
	}

	private static String chartTypeDialog() {
		String[] data = { "Pie Chart", "3D Pie Chart", "Multiple 3D Pie Chart" };
		return (String) JOptionPane.showInputDialog(null, "Choose one", "Select a Pie Chart",
				JOptionPane.INFORMATION_MESSAGE, null, data, data[0]);
	}

	private static TableOrder tableOrderDialog() {
		TableOrder extract = TableOrder.BY_COLUMN;
		if (JOptionPane.showConfirmDialog(null, "choose one", "Select a Column?",
				JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.NO_OPTION)
			extract = TableOrder.BY_ROW;
		return extract;
	}

	private static int selectedDialog(String chartType, TableOrder tableOrder, String[] row, String[] column) {
		if (!chartType.equals("Multiple 3D Pie Chart"))
			if (tableOrder == TableOrder.BY_COLUMN)
				return Arrays.asList(column).indexOf((String) JOptionPane.showInputDialog(null, "Choose Categories",
						"Select Categories", JOptionPane.INFORMATION_MESSAGE, null, column, column[0]));
			else
				return Arrays.asList(row).indexOf((String) JOptionPane.showInputDialog(null, "Choose Categories",
						"Select Categories", JOptionPane.INFORMATION_MESSAGE, null, row, row[0]));
		else
			return 0;
	}

	private static Paint[] getPaintArray(Paint[] temp) {
		Paint[] paint = new Paint[temp.length];
		for (int i = paint.length - 1; i > -1; i--)
			paint[i] = temp[paint.length - 1 - i];
		return paint;
	}

	//in moving to JFreeChart 1.5.X, 3d graphs are no longer included.
	public static boolean is3DChart(JFreeChart jfchart) {
		return false;
		/*
		boolean is3D = false;
		if (jfchart.getPlot().getPlotType().contains("Category"))
			if (jfchart.getCategoryPlot().getRenderer() instanceof StackedBarRenderer3D
					|| jfchart.getCategoryPlot().getRenderer() instanceof BarRenderer3D
					|| jfchart.getCategoryPlot().getRenderer() instanceof LineRenderer3D)
				is3D = true;
			else if (jfchart.getPlot().getPlotType().contains("XY"))
				if (jfchart.getXYPlot().getRenderer() instanceof XYLine3DRenderer)
					is3D = true;
		return is3D;*/
	}

	//in moving to JFreeChart 1.5.X, 3d graphs are no longer included.
	public static void changeChartType(TexturePaint[] tp, JFreeChart jfchart, int stateChange) {
		JOptionPane.showMessageDialog(null, "No 3D View", "Information", JOptionPane.INFORMATION_MESSAGE);
		return;
		/*
		if (stateChange == 1) {
			if (jfchart.getPlot().getPlotType().contains("Category")) {
				if (jfchart.getCategoryPlot().getRenderer() instanceof StackedBarRenderer) {
					StackedBarRenderer3D r = new StackedBarRenderer3D();
					RendererUtil.setRendererProperty(r);
					jfchart.getCategoryPlot().setRenderer(0, r);
				} else if (jfchart.getCategoryPlot().getRenderer() instanceof BarRenderer) {
					BarRenderer3D r = new BarRenderer3D();
					RendererUtil.setRendererProperty(r);
					jfchart.getCategoryPlot().setRenderer(0, r);
				} else if (jfchart.getCategoryPlot().getRenderer() instanceof LineAndShapeRenderer) {
					LineRenderer3D r = new LineRenderer3D();
					RendererUtil.setRendererProperty(r);
					jfchart.getCategoryPlot().setRenderer(0, r);
				} else {
					JOptionPane.showMessageDialog(null, "No 3D View", "Information", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			} else {
				JOptionPane.showMessageDialog(null, "No 3D View", "Information", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		} else if (stateChange == 2) {
			if (jfchart.getPlot().getPlotType().contains("Category")) {
				if (jfchart.getCategoryPlot().getRenderer() instanceof StackedBarRenderer3D) {
					StackedBarRenderer r = new StackedBarRenderer();
					RendererUtil.setRendererProperty(r);
					jfchart.getCategoryPlot().setRenderer(0, r);
				} else if (jfchart.getCategoryPlot().getRenderer() instanceof BarRenderer3D) {
					BarRenderer r = new BarRenderer();
					RendererUtil.setRendererProperty(r);
					jfchart.getCategoryPlot().setRenderer(0, r);
				} else if (jfchart.getCategoryPlot().getRenderer() instanceof LineRenderer3D) {
					LineAndShapeRenderer r = new LineAndShapeRenderer();
					for (int i = 0; i < jfchart.getCategoryPlot().getDataset().getRowCount(); i++)
						r.setSeriesShapesVisible(i, false);
					RendererUtil.setRendererProperty(r);
					jfchart.getCategoryPlot().setRenderer(0, r);
				}
			} else {
				JOptionPane.showMessageDialog(null, "No 3D View", "Information", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		repaint(tp, jfchart);*/
	}

	public static void repaint(TexturePaint[] tp, JFreeChart jfchart) {
		AbstractRenderer renderer = null;
		if (jfchart.getPlot().getPlotType().equalsIgnoreCase("Category Plot"))
			renderer = (AbstractRenderer) jfchart.getCategoryPlot().getRenderer();
		else if (jfchart.getPlot().getPlotType().equalsIgnoreCase("XY Plot"))
			renderer = (AbstractRenderer) jfchart.getXYPlot().getRenderer();
		else
			return;

		for (int i = 0; i < tp.length; i++)
			renderer.setSeriesPaint(i, tp[i]);
	}
}
