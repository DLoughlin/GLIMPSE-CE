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
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * CategoryStackedAreaChart creates a JFreeChart stacked area chart with all properties stored in Chart.
 * <p>
 * This class provides two constructors for different data input formats and handles chart creation and property setup.
 * </p>
 *
 * @author TWU
 * @since 1/2/2016
 */
public class CategoryStackedAreaChart extends CategoryChart {

    /**
     * Constructs a CategoryStackedAreaChart using a DefaultCategoryDataset and various chart properties.
     *
     * @param path              Path for chart output
     * @param graphName         Name of the chart
     * @param meta              Metadata for the chart
     * @param titles            Chart titles
     * @param axis_name_unit    Axis names and units
     * @param legend            Legend text
     * @param color             Series colors
     * @param pColor            Pattern colors
     * @param pattern           Series patterns
     * @param lineStrokes       Line stroke styles
     * @param annotationText    Annotation text for chart
     * @param dataset           Category dataset
     * @param relativeColIndex  Relative column index for data
     * @param ShowLineAndShape  Whether to show lines and shapes
     * @param graphType         Type of graph
     */
    public CategoryStackedAreaChart(String path, String graphName, String meta,
            String[] titles, String[] axis_name_unit, String legend, int[] color, int[] pColor,
            int[] pattern, int[] lineStrokes, String[][] annotationText,
            DefaultCategoryDataset dataset, int relativeColIndex, boolean ShowLineAndShape, String graphType) {
        super(path, graphName, meta, titles, axis_name_unit, legend, color, pColor,
                pattern, lineStrokes, annotationText, dataset,
                relativeColIndex, ShowLineAndShape, graphType);
        chartClassName = "chart.CategoryStackedAreaChart";
        crtChart();
    }

    /**
     * Constructs a CategoryStackedAreaChart using raw data and various chart properties.
     *
     * @param path              Path for chart output
     * @param graphName         Name of the chart
     * @param meta              Metadata for the chart
     * @param titles            Chart titles
     * @param axis_name_unit    Axis names and units
     * @param legend            Legend text
     * @param column            Data column name
     * @param annotationText    Annotation text for chart
     * @param data              Raw data for chart
     * @param relativeColIndex  Relative column index for data
     */
    public CategoryStackedAreaChart(String path, String graphName, String meta, String[] titles,
            String[] axis_name_unit, String legend, String column, String[][] annotationText, String[][] data,
            int relativeColIndex) {
        super(path, graphName, meta, titles, axis_name_unit, legend, column, annotationText,
                data, relativeColIndex);
        chartClassName = "chart.CategoryStackedAreaChart";
        crtChart();
    }

    /**
     * Creates and configures the stacked area chart and its properties.
     */
    private void crtChart() {
        // Create the stacked area chart with axis names and dataset
        chart = ChartFactory.createStackedAreaChart("", verifyAxisName_unit(0),
                verifyAxisName_unit(1), dataset, PlotOrientation.VERTICAL,
                true, true, false);
        plot = (CategoryPlot) chart.getPlot();
        plot.setDataset(0, dataset);
        // Use StackedAreaRenderer for rendering the chart
        StackedAreaRenderer renderer = (StackedAreaRenderer) plot.getRenderer(0);
        plot.setRenderer(0, renderer);
        // Set chart properties
        setPlotProperty();
        setLegendProperty();
        setAxisProperty();
        RendererUtil.setRendererProperty(renderer);
        setChartProperty();
    }
}