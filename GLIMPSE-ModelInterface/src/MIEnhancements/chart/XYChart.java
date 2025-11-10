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
 * <p>
 * Author: TWU
 * Created: 1/2/2016
 */
public class XYChart extends Chart {
    /** The plot for the chart. */
    protected XYPlot plot;
    /** Array of pointer annotations for the chart. */
    protected XYPointerAnnotation[] annotation;
    /** The dataset for the chart. */
    protected XYDataset dataset;

    /**
     * Constructs an XYChart using a DefaultXYDataset and various chart properties.
     *
     * @param path              Path for chart output
     * @param graphName         Name of the chart
     * @param meta              Metadata for the chart
     * @param titles            Titles for the chart
     * @param axisName_unit     Axis names and units
     * @param legend            Legend string
     * @param color             Colors for series
     * @param pColor            Paint colors for series
     * @param pattern           Patterns for series
     * @param lineStrokes       Line stroke styles
     * @param annotationText    Text for annotations
     * @param dataset2          The dataset
     * @param relativeColIndex  Relative column index for data
     * @param ShowLineAndShape  Whether to show lines and shapes
     */
    public XYChart(String path, String graphName, String meta, String[] titles, String[] axisName_unit, String legend,
                   int[] color, int[] pColor, int[] pattern, int[] lineStrokes, String[][] annotationText,
                   DefaultXYDataset dataset2, int relativeColIndex, boolean ShowLineAndShape) {
        super(path, graphName, meta, titles, axisName_unit, legend, color, pColor, pattern, lineStrokes, annotationText,
                ShowLineAndShape);
        this.relativeColIndex = relativeColIndex;
        // If relativeColIndex is valid, create a custom dataset, else use the provided dataset
        if (relativeColIndex > -1)
            this.dataset = new MyDataset().createXYDataset(dataset2, relativeColIndex);
        else
            this.dataset = dataset2;
    }

    /**
     * Constructs an XYChart using raw data and legend/column info.
     *
     * @param path              Path for chart output
     * @param graphName         Name of the chart
     * @param meta              Metadata for the chart
     * @param titles            Titles for the chart
     * @param axis_name_unit    Axis names and units
     * @param legend            Legend string
     * @param column            Column string
     * @param annotationText    Text for annotations
     * @param data              Raw data
     * @param relativeColIndex  Relative column index for data
     */
    public XYChart(String path, String graphName, String meta, String[] titles, String[] axis_name_unit, String legend,
                   String column, String[][] annotationText, String[][] data, int relativeColIndex) {
        super(path, graphName, meta, titles, axis_name_unit, legend, annotationText);
        this.relativeColIndex = relativeColIndex;
        // Build dataset from legend and column info if legend is present
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

    /**
     * Sets visual properties for the plot, including background, gridlines, and axis visibility.
     */
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

        // Extract X values for chartColumn
        int itemCount = plot.getDataset().getItemCount(0);
        String[] col = new String[itemCount];
        for (int i = 0; i < itemCount; i++)
            col[i] = String.valueOf(plot.getDataset().getX(0, i));
        chartColumn = conversionUtil.ArrayConversion.array2String(col);

        // Extract series keys for chartRow
        int seriesCount = plot.getDataset().getSeriesCount();
        String[] row = new String[seriesCount];
        for (int i = 0; i < seriesCount; i++)
            row[i] = String.valueOf(plot.getDataset().getSeriesKey(i));
        chartRow = conversionUtil.ArrayConversion.array2String(row);
    }

    /**
     * Sets legend properties, including color and paint for series.
     */
    protected void setLegendProperty() {
        if (color == null) {
            // Auto-populate series paint if color is not set
            for (int i = 0; i < plot.getRendererCount(); i++) {
                ((AbstractRenderer) plot.getRenderer(i)).setAutoPopulateSeriesPaint(true);
                ((AbstractRenderer) plot.getRenderer(i)).setAutoPopulateSeriesFillPaint(true);
            }
            color = LegendUtil.getLegendColor(plot.getLegendItems());
            initLegendPattern(null, null, null);
            if (debug)
                System.out.println("XYChart::setLegendProperty:legend: " + legend + " color: " + Arrays.toString(color));
        }
        // If legend is not set, use chartRow
        if (legend == null)
            legend = this.chartRow;
        getlegendInfo(legend.split(","));
        buildPaint();
        plot.setFixedLegendItems(LegendUtil.crtLegenditemcollection(legend.split(","), this.paint));
        Paint[] paint = LegendUtil.getLegendPaint(plot.getFixedLegendItems());
        ChartUtil.paintSeries(plot, paint);
    }

    /**
     * Sets axis properties, including bounds, tick units, and label angle.
     */
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
        // Rotate domain axis label if there are many items
        if (plot.getDataset().getItemCount(0) > 16)
            domainAxis.setLabelAngle(90);
    }

    /**
     * Returns the dataset for the chart.
     * @return XYDataset
     */
    public XYDataset getDataset() {
        return dataset;
    }
}