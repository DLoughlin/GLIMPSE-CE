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
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

/**
 * Creates an XY line chart using JFreeChart with properties from the parent XYChart.
 * <p>
 * Author: TWU
 * Date: 1/2/2016
 */
public class XYLineChart extends XYChart {
    /**
     * Constructs an XYLineChart with dataset and rendering options.
     *
     * @param path              Path for output or chart file
     * @param graphName         Name of the chart
     * @param meta              Metadata string
     * @param titles            Chart titles
     * @param axis_name_unit    Axis names and units
     * @param legend            Legend text
     * @param color             Series colors
     * @param pColor            Paint colors
     * @param pattern           Line patterns
     * @param lineStrokes       Line stroke styles
     * @param annotationText    Annotation text for chart
     * @param dataset           XY dataset
     * @param relativeColIndex  Relative column index for data
     * @param ShowLineAndShape  Show lines and shapes flag
     */
    public XYLineChart(String path, String graphName, String meta, String[] titles, String[] axis_name_unit, String legend,
                       int[] color, int[] pColor, int[] pattern, int[] lineStrokes, String[][] annotationText, DefaultXYDataset dataset,
                       int relativeColIndex, boolean ShowLineAndShape) {
        super(path, graphName, meta, titles, axis_name_unit, legend, color, pColor, pattern, lineStrokes, annotationText, dataset,
                relativeColIndex, ShowLineAndShape);
        chartClassName = "chart.XYLineChart";
        crtChart();
    }

    /**
     * Constructs an XYLineChart with raw data and annotation options.
     *
     * @param path              Path for output or chart file
     * @param graphName         Name of the chart
     * @param meta              Metadata string
     * @param titles            Chart titles
     * @param axis_name_unit    Axis names and units
     * @param legend            Legend text
     * @param column            Data column
     * @param annotationText    Annotation text for chart
     * @param data              Raw data
     * @param relativeColIndex  Relative column index for data
     */
    public XYLineChart(String path, String graphName, String meta, String[] titles, String[] axis_name_unit, String legend,
                       String column, String[][] annotationText, String[][] data, int relativeColIndex) {
        super(path, graphName, meta, titles, axis_name_unit, legend, column, annotationText, data, relativeColIndex);
        chartClassName = "chart.XYLineChart";
        crtChart();
    }

    /**
     * Creates and configures the XY line chart plot and renderer.
     */
    private void crtChart() {
        // Set up drawing supplier for custom colors and strokes
        DrawingSupplier supplier;
        if (this.color != null) {
            supplier = ChartUtil.setDrawingSupplier(this.chartClassName, this.color, this.lineStrokes);
        } else {
            supplier = new DefaultDrawingSupplier(
                    DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE,
                    DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
                    DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                    DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                    DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
        }

        // Create axes
        NumberAxis domainAxis = new NumberAxis(verifyAxisName_unit(0));
        ValueAxis rangeAxis = new NumberAxis(verifyAxisName_unit(1));

        // Create renderer for lines and shapes
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Create plot and set drawing supplier
        plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);
        plot.setDrawingSupplier(supplier);

        // Set plot, axis, and renderer properties
        setPlotProperty();
        setAxisProperty();
        RendererUtil.setRendererProperty(renderer);

        // Create chart and set legend
        chart = new JFreeChart("", plot);
        setLegendProperty();

        // Configure renderer for each series
        for (int i = 0; i < getLineStrokes().length; i++) {
            renderer.setSeriesShapesVisible(i, isShowLineAndShape());
            renderer.setSeriesStroke(i, LegendUtil.getLineStroke(getLineStrokes()[i]));
        }

        // Apply legacy chart theme
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        setChartProperty();
    }
}