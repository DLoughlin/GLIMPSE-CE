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

import java.awt.Color;
import java.awt.Paint;
import java.util.Arrays;

import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 * The base class for XY JFreeChart. Subclasses are divided into the chart
 * with XY/XYZ dataset. It holds methods of XY chart in common. 
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class XYChart extends Chart {
	protected XYPlot plot;
	protected XYPointerAnnotation[] annotation;
	protected XYDataset dataset;

	public XYChart(String path, String graphName, String meta, String[] titles, String[] axisName_unit, String legend,
			int[] color, int[] pColor, int[] pattern, int[] lineStrokes, String[][] annotationText,
			DefaultXYDataset dataset2, int relativeColIndex, boolean ShowLineAndShape) {

		super(path, graphName, meta, titles, axisName_unit, legend, color, pColor, pattern, lineStrokes, annotationText,
				ShowLineAndShape);
		this.relativeColIndex = relativeColIndex;
		if (relativeColIndex > -1)
			this.dataset = new MyDataset().createXYDataset(dataset2, relativeColIndex);
		else
			this.dataset = dataset2;
	}

	public XYChart(String path, String graphName, String meta, String[] titles, String[] axis_name_unit, String legend,
			String column, String[][] annotationText, String[][] data, int relativeColIndex) {

		super(path, graphName, meta, titles, axis_name_unit, legend, annotationText);
		//this.legend = legend;
		this.relativeColIndex = relativeColIndex;

		if (this.legend != null) {
			if (relativeColIndex > -1)
				dataset = new MyDataset().createXYDataset(data, this.legend.split(","), column.split(","),
						relativeColIndex);
			else
				dataset = new MyDataset().createXYDataset(data, this.legend.split(","),
						column.split(","));
		} else {
			System.out.println("chart::XYChart:con - Legends are null.");
		}
	}

	protected void setPlotProperty() {

		plot.setBackgroundPaint(Color.white);
		plot.setBackgroundAlpha(0.5F);
		plot.setDomainCrosshairVisible(false);
		plot.setRangeCrosshairLockedOnData(false);
		plot.setRangeCrosshairVisible(false);

		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);
		plot.setOutlineVisible(false);
		plot.getDomainAxis().setAxisLineVisible(false);
		plot.getRangeAxis().setAxisLineVisible(true);
		plot.getDomainAxis().setVisible(true);
		
		String[] col = null;
		int l = plot.getDataset().getItemCount(0);
		col = new String[l];
		for (int i = 0; i < l; i++)
			col[i] = String.valueOf(plot.getDataset().getX(0, i));
		chartColumn = conversionUtil.ArrayConversion.array2String(col);
		l = plot.getDataset().getSeriesCount();
		String[] row = new String[l];
		for (int i = 0; i < l; i++)
			row[i] = String.valueOf(plot.getDataset().getSeriesKey(i));
		chartRow = conversionUtil.ArrayConversion.array2String(row);
	}

	protected void setLegendProperty() {

		if (color == null) {
			for (int i = 0; i < plot.getRendererCount(); i++) {
				((AbstractRenderer) plot.getRenderer(i)).setAutoPopulateSeriesPaint(true);
				((AbstractRenderer) plot.getRenderer(i)).setAutoPopulateSeriesFillPaint(true);
			}
			color = LegendUtil.getLegendColor(plot.getLegendItems());
			initLegendPattern(null, null, null);

			if (debug)
				System.out
						.println("XYChart::setLegendProperty:legend: " + legend + " color: " + Arrays.toString(color));
		}

		// In pie chart does not have legend stored, so get row keys as legend
		if (legend == null)
			legend = this.chartRow;//DatasetUtil.getChartRow(chart);

		getlegendInfo(legend.split(","));
		buildPaint();
		plot.setFixedLegendItems(LegendUtil.crtLegenditemcollection(legend.split(","), this.paint));
		Paint[] paint = LegendUtil.getLegendPaint(plot.getFixedLegendItems());
		ChartUtil.paintSeries(plot, paint);
	}

	protected void setAxisProperty() {

		ValueAxis domainAxis = plot.getDomainAxis();
		double low = plot.getDataRange(domainAxis).getLowerBound();
		double high = plot.getDataRange(domainAxis).getUpperBound();
		domainAxis.setLowerBound(low - domainAxis.getTickMarkOutsideLength());
		domainAxis.setUpperBound(high + domainAxis.getTickMarkOutsideLength());
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setAutoRangeIncludesZero(true);
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setUpperMargin(0.12);
		if (plot.getDataset().getItemCount(0)>16)
			domainAxis.setLabelAngle(90);
	}

	public XYDataset getDataset() {
		return dataset;
	}

}
