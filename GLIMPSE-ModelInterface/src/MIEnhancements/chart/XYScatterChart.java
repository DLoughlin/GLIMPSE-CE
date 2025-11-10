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
 * Agreements 89-92423101 and 89-92549601. Contributors from PNNL include 
 * Maridee Weber, Catherine Ledna, Gokul Iyer, Page Kyle, Marshall Wise, Matthew 
 * Binsted, and Pralit Patel. Coding contributions have also been made by Aaron 
 * Parks and Yadong Xu of ARA through the EPA's Environmental Modeling and 
 * Visualization Laboratory contract. 
 */
package chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.DefaultXYDataset;

/**
 * XYScatterChart creates a JFreeChart scatter plot with all properties stored in Chart.
 * <p>
 * This class extends XYChart and provides constructors for initializing the chart
 * with either a dataset or raw data. It configures the chart, plot, renderer, and
 * applies properties such as axis, legend, and annotations.
 * </p>
 *
 * @author TWU
 * @since 1/2/2016
 */
public class XYScatterChart extends XYChart {
    /**
     * Constructs a scatter chart using a dataset and chart properties.
     *
     * @param path              Path for saving the chart
     * @param graphName         Name of the chart
     * @param meta              Metadata for the chart
     * @param titles            Titles for the chart
     * @param axis_name_unit    Axis names and units
     * @param legend            Legend text
     * @param color             Colors for series
     * @param pColor            Pattern colors
     * @param pattern           Patterns for series
     * @param lineStrokes       Line stroke styles
     * @param annotationText    Annotation text for the chart
     * @param dataset           XY dataset
     * @param relativeColIndex  Relative column index for data
     * @param ShowLineAndShape  Flag to show line and shape
     */
    public XYScatterChart(String path, String graphName, String meta, String[] titles,
                         String[] axis_name_unit, String legend, int[] color, int[] pColor,
                         int[] pattern, int[] lineStrokes, String[][] annotationText,
                         DefaultXYDataset dataset, int relativeColIndex, boolean ShowLineAndShape) {
        super(path, graphName, meta, titles, axis_name_unit, legend, color, pColor,
                pattern, lineStrokes, annotationText, dataset,
                relativeColIndex, ShowLineAndShape);
        chartClassName = "chart.XYScatterChart";
        crtChart();
    }

    /**
     * Constructs a scatter chart using raw data and chart properties.
     *
     * @param path              Path for saving the chart
     * @param graphName         Name of the chart
     * @param meta              Metadata for the chart
     * @param titles            Titles for the chart
     * @param axis_name_unit    Axis names and units
     * @param legend            Legend text
     * @param column            Data column name
     * @param annotationText    Annotation text for the chart
     * @param data              Raw data for the chart
     * @param relativeColIndex  Relative column index for data
     */
    public XYScatterChart(String path, String graphName, String meta, String[] titles,
                         String[] axis_name_unit, String legend, String column, String[][] annotationText, String[][] data,
                         int relativeColIndex) {
        super(path, graphName, meta, titles, axis_name_unit, legend, column, annotationText,
                data, relativeColIndex);
        chartClassName = "chart.XYScatterChart";
        crtChart();
    }

    /**
     * Initializes and configures the scatter chart, plot, renderer, and properties.
     */
    private void crtChart() {
        // Create scatter plot with axis names and dataset
        chart = ChartFactory.createScatterPlot("", verifyAxisName_unit(0),
                verifyAxisName_unit(1), dataset, PlotOrientation.VERTICAL,
                true, true, false);
        plot = (XYPlot) chart.getPlot();
        plot.setDataset(0, dataset);
        XYItemRenderer renderer = plot.getRenderer();
        plot.setRenderer(0, renderer);
        // Set plot, legend, axis, renderer, and chart properties
        setPlotProperty();
        setLegendProperty();
        setAxisProperty();
        RendererUtil.setRendererProperty(renderer);
        setChartProperty();
    }
}