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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.annotations.CategoryPointerAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnitSource;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import conversionUtil.ArrayConversion;
import graphDisplay.OptionsArea;

/**
 * The base class for category JFreeChart. Subclasses are divided into the chart
 * with category dataset. It holds methods of category chart in common. 
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class CategoryChart extends Chart {

	protected DefaultCategoryDataset dataset;
	protected CategoryPointerAnnotation[] annotation;
	protected CategoryPlot plot;
	
	// Change Chart type from Option panel
	public CategoryChart(String path, String graphName, String meta, String[] titles,
			String[] axisName_unit, String legend, int[] color, int[] pColor,
			int[] pattern, int[] lineStrokes, String[][] annotationText,
			DefaultCategoryDataset dataset, int relativeColIndex, boolean ShowLineAndShape,String graphType) {
		
		super(path, graphName, meta, titles, axisName_unit, legend,  color, 
				pColor, pattern, lineStrokes, annotationText, ShowLineAndShape);
		this.relativeColIndex = relativeColIndex;
		if(dataset!=null){
		if (relativeColIndex > -1)
			if(graphType.compareTo(OptionsArea.REL_RATIO_LINE)==0) {
				this.dataset = new MyDataset().createRatioCategoryDataset(dataset,
						relativeColIndex);
			}else {
				this.dataset = new MyDataset().createDiffCategoryDataset(dataset,
						relativeColIndex);
			}
		else
			this.dataset = dataset;
		}
	}

	public CategoryChart(String path, String graphName, String meta, String[] titles,
			String[] axis_name_unit, String legend, String column, String[][] annotationText,
			String[][] data, int relativeColIndex) {
		
		super(path, graphName, meta, titles, axis_name_unit, legend, annotationText);
		this.relativeColIndex = relativeColIndex;
		
		if (this.legend != null) {
			if (relativeColIndex > -1)
				dataset = new MyDataset().createCategoryDataset(data,
						this.legend.split(","), column.split(","), relativeColIndex);
			else
				dataset = new MyDataset().createCategoryDataset(data,
						this.legend.split(","), column.split(","));
		} else {
			System.out.println("chart::CategoryChart:con - Legends are null.");
		}
	}
	
	public CategoryChart(String path, String graphName, String meta, String[] titles,
			String[] axis_name_unit, String legend, int relativeColIndex) {
		super(path, graphName, meta, titles, axis_name_unit, legend, null);
		this.relativeColIndex = relativeColIndex;
	}
	
	// Statistics data set
	public CategoryChart(String path, String graphName, String meta, String[] titles,
			String[] axis_name_unit, String legend, String[][] annotationText,
			ArrayList<List<String[]>> data, int relativeColIndex) {//column
		super(path, graphName, meta, titles, axis_name_unit, legend, null);
	}

	protected void setPlotProperty() {
		
		plot.setBackgroundPaint(Color.white);
		// plot.setBackgroundAlpha(0.5F);
		plot.setDomainCrosshairVisible(false);
		plot.setRangeCrosshairLockedOnData(false);
		plot.setRangeCrosshairVisible(false);

		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);
		plot.setOutlineVisible(false);
		plot.setRangeZeroBaselineVisible(true);
		plot.getDomainAxis().setAxisLineVisible(false);
		plot.getRangeAxis().setAxisLineVisible(true);
		plot.getDomainAxis().setVisible(true);
		
		int l = plot.getDataset().getColumnCount();
		String[] col = new String[l];
		for (int i = 0; i < l; i++)
			col[i] = (String) plot.getDataset().getColumnKey(i);
		chartColumn =  conversionUtil.ArrayConversion.array2String(col);
		chartRow = conversionUtil.ArrayConversion.array2String(ArrayConversion.list2Array(plot.getDataset().getRowKeys()));
	}

	protected void setLegendProperty() {
		if (color == null) {
			for (int i = 0; i < plot.getRendererCount(); i++) {
				((AbstractRenderer) plot.getRenderer(i))
						.setAutoPopulateSeriesPaint(true);
				((AbstractRenderer) plot.getRenderer(i))
						.setAutoPopulateSeriesFillPaint(true);
			}

			this.color = LegendUtil.getLegendColor(plot.getLegendItems());
			initLegendPattern(null, null, null);

			if (debug)
				System.out.println("CategoryChart::setLegendProperty:legend: " + legend
						+ " color: " + Arrays.toString(color));
		}
		
		// In pie chart does not have legend stored, so get row keys as legend
		if (legend==null)
			legend = conversionUtil.ArrayConversion.list2String(dataset.getRowKeys());
		getlegendInfo(legend.split(","));
		buildPaint();
		plot.setFixedLegendItems(LegendUtil.crtLegenditemcollection(
				legend.split(","), this.paint));
		Paint[] paint = LegendUtil.getLegendPaint(plot.getFixedLegendItems());
		ChartUtil.paintSeries(plot, paint);
	}

	protected void setAxisProperty() {
		
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setNegativeArrowVisible(true);
		rangeAxis.setAutoRangeIncludesZero(true);
		rangeAxis.setUpperMargin(0.12);
		rangeAxis.setStandardTickUnits(new NumberTickUnitSource());
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setLowerMargin(0.01);
		if (plot.getDataset().getColumnCount() > 12)
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);

	}

	public DefaultCategoryDataset getDataset() {
		return dataset;
	}

}
