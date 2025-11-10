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
 * <p>
 * Author: TWU
 * Created: 1/2/2016
 * </p>
 */
public class CategoryChart extends Chart {

    /** The dataset for the category chart. */
    protected DefaultCategoryDataset dataset;
    /** Array of pointer annotations for the chart. */
    protected CategoryPointerAnnotation[] annotation;
    /** The plot object for the chart. */
    protected CategoryPlot plot;

    /**
     * Constructs a CategoryChart with dataset and options for relative column index and chart type.
     *
     * @param path              Path for saving or referencing the chart
     * @param graphName         Name of the chart
     * @param meta              Metadata for the chart
     * @param titles            Titles for the chart
     * @param axisName_unit     Axis names and units
     * @param legend            Legend string
     * @param color             Colors for series
     * @param pColor            Paint colors for series
     * @param pattern           Patterns for series
     * @param lineStrokes       Line strokes for series
     * @param annotationText    Text for annotations
     * @param dataset           Category dataset
     * @param relativeColIndex  Index for relative column operations
     * @param ShowLineAndShape  Flag to show line and shape
     * @param graphType         Type of graph (e.g., ratio, diff)
     */
    public CategoryChart(String path, String graphName, String meta, String[] titles,
                        String[] axisName_unit, String legend, int[] color, int[] pColor,
                        int[] pattern, int[] lineStrokes, String[][] annotationText,
                        DefaultCategoryDataset dataset, int relativeColIndex, boolean ShowLineAndShape, String graphType) {
        super(path, graphName, meta, titles, axisName_unit, legend, color,
                pColor, pattern, lineStrokes, annotationText, ShowLineAndShape);
        this.relativeColIndex = relativeColIndex;
        if (dataset != null) {
            if (relativeColIndex > -1) {
                if (graphType.compareTo(OptionsArea.REL_RATIO_LINE) == 0) {
                    this.dataset = new MyDataset().createRatioCategoryDataset(dataset, relativeColIndex);
                } else {
                    this.dataset = new MyDataset().createDiffCategoryDataset(dataset, relativeColIndex);
                }
            } else {
                this.dataset = dataset;
            }
        }
    }

    /**
     * Constructs a CategoryChart from string data and legend information.
     *
     * @param path              Path for saving or referencing the chart
     * @param graphName         Name of the chart
     * @param meta              Metadata for the chart
     * @param titles            Titles for the chart
     * @param axis_name_unit    Axis names and units
     * @param legend            Legend string
     * @param column            Column string
     * @param annotationText    Text for annotations
     * @param data              Data for the chart
     * @param relativeColIndex  Index for relative column operations
     */
    public CategoryChart(String path, String graphName, String meta, String[] titles,
                        String[] axis_name_unit, String legend, String column, String[][] annotationText,
                        String[][] data, int relativeColIndex) {
        super(path, graphName, meta, titles, axis_name_unit, legend, annotationText);
        this.relativeColIndex = relativeColIndex;
        if (this.legend != null) {
            if (relativeColIndex > -1) {
                dataset = new MyDataset().createCategoryDataset(data,
                        this.legend.split(","), column.split(","), relativeColIndex);
            } else {
                dataset = new MyDataset().createCategoryDataset(data,
                        this.legend.split(","), column.split(","));
            }
        } else {
            System.out.println("chart::CategoryChart:con - Legends are null.");
        }
    }

    /**
     * Constructs a CategoryChart with minimal information and relative column index.
     *
     * @param path              Path for saving or referencing the chart
     * @param graphName         Name of the chart
     * @param meta              Metadata for the chart
     * @param titles            Titles for the chart
     * @param axis_name_unit    Axis names and units
     * @param legend            Legend string
     * @param relativeColIndex  Index for relative column operations
     */
    public CategoryChart(String path, String graphName, String meta, String[] titles,
                        String[] axis_name_unit, String legend, int relativeColIndex) {
        super(path, graphName, meta, titles, axis_name_unit, legend, null);
        this.relativeColIndex = relativeColIndex;
    }

    /**
     * Constructs a CategoryChart for statistics data set.
     *
     * @param path              Path for saving or referencing the chart
     * @param graphName         Name of the chart
     * @param meta              Metadata for the chart
     * @param titles            Titles for the chart
     * @param axis_name_unit    Axis names and units
     * @param legend            Legend string
     * @param annotationText    Text for annotations
     * @param data              Data for the chart
     * @param relativeColIndex  Index for relative column operations
     */
    public CategoryChart(String path, String graphName, String meta, String[] titles,
                        String[] axis_name_unit, String legend, String[][] annotationText,
                        ArrayList<List<String[]>> data, int relativeColIndex) {
        super(path, graphName, meta, titles, axis_name_unit, legend, null);
        // Implementation for statistics data set can be added here
    }

    /**
     * Sets properties for the plot, such as background, gridlines, axis visibility, and chart columns/rows.
     */
    protected void setPlotProperty() {
        plot.setBackgroundPaint(Color.white);
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

        // Get column keys and row keys for chart
        int l = plot.getDataset().getColumnCount();
        String[] col = new String[l];
        for (int i = 0; i < l; i++) {
            col[i] = (String) plot.getDataset().getColumnKey(i);
        }
        chartColumn = conversionUtil.ArrayConversion.array2String(col);
        chartRow = conversionUtil.ArrayConversion.array2String(ArrayConversion.list2Array(plot.getDataset().getRowKeys()));
    }

    /**
     * Sets legend properties, including colors, patterns, and legend items.
     */
    protected void setLegendProperty() {
        if (color == null) {
            for (int i = 0; i < plot.getRendererCount(); i++) {
                AbstractRenderer renderer = (AbstractRenderer) plot.getRenderer(i);
                renderer.setAutoPopulateSeriesPaint(true);
                renderer.setAutoPopulateSeriesFillPaint(true);
            }
            this.color = LegendUtil.getLegendColor(plot.getLegendItems());
            initLegendPattern(null, null, null);
            if (debug) {
                System.out.println("CategoryChart::setLegendProperty:legend: " + legend + " color: " + Arrays.toString(color));
            }
        }
        // If legend is null, get row keys as legend
        if (legend == null) {
            legend = conversionUtil.ArrayConversion.list2String(dataset.getRowKeys());
        }
        getlegendInfo(legend.split(","));
        buildPaint();
        plot.setFixedLegendItems(LegendUtil.crtLegenditemcollection(legend.split(","), this.paint));
        Paint[] paint = LegendUtil.getLegendPaint(plot.getFixedLegendItems());
        ChartUtil.paintSeries(plot, paint);
    }

    /**
     * Sets axis properties, such as margins, tick units, and label positions.
     */
    protected void setAxisProperty() {
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNegativeArrowVisible(true);
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setUpperMargin(0.12);
        rangeAxis.setStandardTickUnits(new NumberTickUnitSource());
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLowerMargin(0.01);
        if (plot.getDataset().getColumnCount() > 12) {
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
        }
    }

    /**
     * Returns the dataset for the category chart.
     *
     * @return the DefaultCategoryDataset
     */
    public DefaultCategoryDataset getDataset() {
        return dataset;
    }
}