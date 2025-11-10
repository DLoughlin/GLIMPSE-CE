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
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

/**
 * CategoryBoxAndWhiskerChart creates a JFreeChart box-and-whisker chart with all properties stored in Chart.
 * <p>
 * Author: TWU
 * Date: 1/2/2016
 */
public class CategoryBoxAndWhiskerChart extends CategoryChart {
    /**
     * Dataset for the box-and-whisker chart.
     */
    private DefaultBoxAndWhiskerCategoryDataset dataset;

    /**
     * Constructs a CategoryBoxAndWhiskerChart.
     *
     * @param path              Path for saving the chart
     * @param graphName         Name of the chart
     * @param id                Chart identifier
     * @param titles            Chart titles
     * @param axisName_unit     Axis names and units
     * @param column            Data columns
     * @param legend            Legend string (comma-separated)
     * @param annotationText    Annotation text for the chart
     * @param data              Data for the chart
     */
    public CategoryBoxAndWhiskerChart(String path, String graphName, String id,
                                      String[] titles, String[] axisName_unit, String column, String legend,
                                      String[][] annotationText, ArrayList<List<String[]>> data) {
        super(path, graphName, id, titles, axisName_unit, column, null, data, -1);
        chartClassName = "chart.CategoryBoxAndWhiskerChart";
        this.legend = legend;
        // Create the dataset using provided data, legend, and columns
        dataset = (DefaultBoxAndWhiskerCategoryDataset) new MyDataset()
                .createBoxAndWhiskerCategoryDataset(data, legend.split(","), column.split(","));
        crtChart();
    }

    /**
     * Initializes and configures the box-and-whisker chart.
     */
    private void crtChart() {
        // Create the chart with title, empty category axis, and value axis
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