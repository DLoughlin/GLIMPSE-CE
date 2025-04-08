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
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;


/**
 * The class handle to create a statistics JFreeChart with all properties stored in Chart. 
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class CategoryBoxAndWhiskerChart extends CategoryChart {
	private DefaultBoxAndWhiskerCategoryDataset dataset;

	public CategoryBoxAndWhiskerChart(String path, String graphName, String id,
			String[] titles, String[] axisName_unit, String column,	String legend,		
			String[][] annotationText, ArrayList<List<String[]>> data) {
		super(path, graphName, id, titles, axisName_unit, column, null, data, -1);
		chartClassName = "chart.CategoryBoxAndWhiskerChart";
		this.legend = legend;
		dataset = (DefaultBoxAndWhiskerCategoryDataset) new MyDataset()
				.createBoxAndWhiskerCategoryDataset(data, legend.split(","),
						column.split(","));
		crtChart();
	}

	private void crtChart() {
		chart = ChartFactory.createBoxAndWhiskerChart(titles[0], "", "value",
				dataset, true);
		plot = (CategoryPlot) chart.getPlot();
		plot.setDataset(0, dataset);
		setPlotProperty();
		setLegendProperty();
		setAxisProperty();
		RendererUtil.setRendererProperty(plot.getRenderer());
		chart.getLegend().setPosition(RectangleEdge.RIGHT);
	}
}
