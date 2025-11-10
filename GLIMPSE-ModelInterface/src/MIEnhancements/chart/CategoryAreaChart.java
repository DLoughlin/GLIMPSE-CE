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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.AreaRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * CategoryAreaChart creates an area chart using JFreeChart with all properties stored in Chart.
 * Supports construction from dataset or raw data, and configures chart appearance and behavior.
 *
 * Author: TWU
 * Created: 1/2/2016
 */
public class CategoryAreaChart extends CategoryChart {
    /**
     * Constructs a CategoryAreaChart from a dataset and chart properties.
     * @param path Path for chart resources
     * @param graphName Chart name
     * @param meta Metadata string
     * @param titles Chart titles
     * @param axisName_unit Axis names and units
     * @param legend Legend string
     * @param color Series colors
     * @param pColor Pattern colors
     * @param pattern Pattern codes
     * @param lineStrokes Line stroke codes
     * @param annotationText Annotation text for chart
     * @param dataset Category dataset
     * @param relativeColIndex Relative column index
     * @param ShowLineAndShape Whether to show line and shape
     * @param graphType Chart type string
     */
    public CategoryAreaChart(String path, String graphName, String meta, String[] titles,
            String[] axisName_unit, String legend, int[] color, int[] pColor,
            int[] pattern, int[] lineStrokes, String[][] annotationText,
            DefaultCategoryDataset dataset, int relativeColIndex, boolean ShowLineAndShape, String graphType) {
        super(path, graphName, meta, titles, axisName_unit, legend, color, pColor,
                pattern, lineStrokes, annotationText, dataset,
                relativeColIndex, ShowLineAndShape, graphType);
        chartClassName = "chart.CategoryAreaChart";
        crtChart();
    }

    /**
     * Constructs a CategoryAreaChart from raw data and chart properties.
     * @param path Path for chart resources
     * @param graphName Chart name
     * @param meta Metadata string
     * @param titles Chart titles
     * @param axis_name_unit Axis names and units
     * @param legend Legend string
     * @param column Column names
     * @param annotationText Annotation text for chart
     * @param data Raw data for chart
     * @param relativeColIndex Relative column index
     */
    public CategoryAreaChart(String path, String graphName, String meta, String[] titles,
            String[] axis_name_unit, String legend, String column, String[][] annotationText, String[][] data,
            int relativeColIndex) {
        super(path, graphName, meta, titles, axis_name_unit, legend, column, annotationText,
                data, relativeColIndex);
        chartClassName = "chart.CategoryAreaChart";
        crtChart();
    }

    /**
     * Creates and configures the area chart and its properties.
     */
    private void crtChart() {
        // Create the area chart using JFreeChart
        chart = ChartFactory.createAreaChart("", verifyAxisName_unit(0),
                verifyAxisName_unit(1), dataset, PlotOrientation.VERTICAL,
                true, true, false);
        plot = (CategoryPlot) chart.getPlot();
        plot.setDataset(0, dataset);
        // Set up the area renderer for the plot
        AreaRenderer renderer = (AreaRenderer) plot.getRenderer(0);
        plot.setRenderer(0, renderer);
        // Configure plot, legend, axis, renderer, and chart properties
        setPlotProperty();
        setLegendProperty();
        setAxisProperty();
        RendererUtil.setRendererProperty(renderer);
        setChartProperty();
    }
}