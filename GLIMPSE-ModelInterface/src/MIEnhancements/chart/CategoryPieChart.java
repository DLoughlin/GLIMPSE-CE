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

import java.awt.Font;
import java.awt.Paint;
import java.text.AttributedString;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.util.Rotation;
import org.jfree.chart.util.TableOrder;
import org.jfree.data.category.CategoryToPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.PieDataset;

import conversionUtil.ArrayConversion;

/**
 * CategoryPieChart creates a JFreeChart pie chart for categorical data.
 * <p>
 * This class extends CategoryChart and provides constructors for initializing
 * the chart with various data sources and configuration options. It sets up
 * pie chart types, legend, paint, and custom label/tooltips for a customizable pie chart.
 * </p>
 *
 * Author: TWU
 * Date: 1/2/2016
 */
public class CategoryPieChart extends CategoryChart {
    protected DefaultCategoryDataset dataset;
    protected PieDataset piedataset;

    /**
     * Constructor for CategoryPieChart using a PieDataset.
     * @param path Path for chart output
     * @param graphName Chart name
     * @param meta Metadata
     * @param title Chart title
     * @param axis_name_unit Axis names and units
     * @param legend Legend array
     * @param paint Paint array for sectors
     * @param extract TableOrder for data extraction
     * @param selected Selected index
     * @param dataset PieDataset
     * @param piePlotType Pie chart type
     */
    public CategoryPieChart(String path, String graphName, String meta, String title, String[] axis_name_unit, String[] legend, Paint[] paint, TableOrder extract,
            int selected, PieDataset dataset, String piePlotType) {
        super(path, graphName, meta, new String[] { title }, axis_name_unit, ArrayConversion.array2String(legend), -1);
        piedataset = dataset;
        crtCategoryPieChart(legend, paint, extract, selected, piePlotType);
    }

    /**
     * Constructor for CategoryPieChart using a DefaultCategoryDataset.
     * @param path Path for chart output
     * @param graphName Chart name
     * @param meta Metadata
     * @param title Chart title
     * @param axis_name_unit Axis names and units
     * @param legend Legend array
     * @param paint Paint array for sectors
     * @param extract TableOrder for data extraction
     * @param selected Selected index
     * @param dataset DefaultCategoryDataset
     * @param piePlotType Pie chart type
     */
    public CategoryPieChart(String path, String graphName, String meta, String title, String[] axis_name_unit, String[] legend, Paint[] paint, TableOrder extract,
            int selected, DefaultCategoryDataset dataset, String piePlotType) {
        super(path, graphName, meta, new String[] { title }, axis_name_unit, ArrayConversion.array2String(legend), -1);
        this.dataset = dataset;
        crtCategoryPieChart(legend, paint, extract, selected, piePlotType);
    }

    /**
     * Creates and configures the pie chart, including legend, paint, and tooltips.
     * @param legend Legend array
     * @param paint Paint array for sectors
     * @param extract TableOrder for data extraction
     * @param selected Selected index
     * @param piePlotType Pie chart type
     */
    public void crtCategoryPieChart(String[] legend, Paint[] paint, TableOrder extract,
            int selected, String piePlotType) {
        chartClassName = "chart.CategoryPieChart";
        TextTitle stitle = null;

        // Handle multiple 3D pie chart type
        if (piePlotType.equals("Multiple 3D Pie Chart")) {
            if (dataset != null) {
                MultiplePiePlot plot = new MultiplePiePlot(dataset);
                plot.setDataExtractOrder(extract);
                chart = new JFreeChart(titles[0], plot);
                JFreeChart subchart = plot.getPieChart();
                PiePlot p = (PiePlot) subchart.getPlot();
                p.getPieIndex();
                p.setLabelGenerator(null);
                chartRow = conversionUtil.ArrayConversion.array2String(ArrayConversion.list2Array(p.getDataset().getKeys()));
                if (extract == TableOrder.BY_COLUMN) {
                    if (paint != null) {
                        p.setAutoPopulateSectionPaint(false);
                        paintSector(legend, paint, p);
                    }
                    p.setToolTipGenerator(new CustomToolTipGenerator(legend));
                } else {
                    p.setAutoPopulateSectionPaint(true);
                    p.setToolTipGenerator(new CustomToolTipGenerator(
                            conversionUtil.ArrayConversion.list2Array(dataset.getColumnKeys())));
                }
            }
        } else {
            PiePlot plot = null;
            if (dataset != null) {
                // Convert category dataset to pie dataset
                CategoryToPieDataset ds = new CategoryToPieDataset(dataset, extract, selected);
                if (piePlotType.equals("3D Pie Chart")) {
                    plot = new PiePlot3D(ds);
                    ((PiePlot3D) plot).setDepthFactor(0.05);
                    plot.setCircular(false);
                    plot.setDirection(Rotation.CLOCKWISE);
                } else {
                    plot = new PiePlot(ds);
                }
            } else if (piedataset != null) {
                plot = new PiePlot(piedataset);
            }

            // Set plot properties
            plot.setStartAngle(290);
            plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
            plot.setNoDataMessage("No data available");
            plot.setCircular(false);
            plot.setLabelGap(0.02);
            chartRow = conversionUtil.ArrayConversion.array2String(ArrayConversion.list2Array(plot.getDataset().getKeys()));
            chart = new JFreeChart(titles[0], plot);
            if (extract == TableOrder.BY_COLUMN && paint != null) {
                ((PiePlot) chart.getPlot()).setAutoPopulateSectionPaint(false);
                setPlotProperty(legend, (PiePlot) chart.getPlot());
                String[] cs = conversionUtil.ArrayConversion.list2Array(dataset.getColumnKeys());
                stitle = new TextTitle(cs[selected], new Font("SansSerif", Font.BOLD, 14));
                paintSector(legend, paint, plot);
            } else {
                ((PiePlot) chart.getPlot()).setAutoPopulateSectionPaint(true);
                setPlotProperty(conversionUtil.ArrayConversion.list2Array(dataset.getColumnKeys()),
                        (PiePlot) chart.getPlot());
                stitle = new TextTitle(legend[selected], new Font("SansSerif", Font.BOLD, 14));
            }
            stitle.visible = true;
            chart.addSubtitle(stitle);
        }
    }

    /**
     * Returns the category dataset.
     * @return DefaultCategoryDataset
     */
    @Override
    public DefaultCategoryDataset getDataset() {
        return dataset;
    }

    /**
     * Returns the pie dataset.
     * @return PieDataset
     */
    public PieDataset getPiedataset() {
        return piedataset;
    }

    /**
     * Sets custom label and tooltip generators for the pie plot.
     * @param s Legend array
     * @param plot PiePlot
     */
    private void setPlotProperty(String[] s, PiePlot plot) {
        plot.setLabelGenerator(new CustomLabelGenerator(s));
        plot.setToolTipGenerator(new CustomToolTipGenerator(s));
    }

    /**
     * Sets custom paint for each sector in the pie plot.
     * @param legend Legend array
     * @param paint Paint array
     * @param plot PiePlot
     */
    private void paintSector(String[] legend, Paint[] paint, PiePlot plot) {
        for (int i = 0; i < paint.length; i++) {
            if (plot.getSectionOutlinesVisible()) {
                plot.setSectionPaint(legend[i], paint[i]);
            }
        }
    }

    /**
     * Custom label generator for pie chart sections.
     */
    protected class CustomLabelGenerator implements PieSectionLabelGenerator {
        String[] legend;

        CustomLabelGenerator(String[] legend) {
            this.legend = legend.clone();
        }

        @Override
        public String generateSectionLabel(PieDataset dataset, @SuppressWarnings("rawtypes") Comparable key) {
            String result = null;
            if (dataset != null) {
                result = legend[dataset.getIndex(key)];
            }
            return result;
        }

        @Override
        public AttributedString generateAttributedSectionLabel(PieDataset dataset,
                @SuppressWarnings("rawtypes") Comparable key) {
            return null;
        }
    }

    /**
     * Custom tooltip generator for pie chart sections.
     */
    protected class CustomToolTipGenerator implements PieToolTipGenerator {
        String[] legend;

        CustomToolTipGenerator(String[] legend) {
            this.legend = legend.clone();
        }

        @Override
        public String generateToolTip(PieDataset dataset, @SuppressWarnings("rawtypes") Comparable key) {
            String result = null;
            Number tot = 0;
            // Calculate total value for percentage
            for (int i = 0; i < dataset.getItemCount() && dataset.getValue(i) != null; i++) {
                tot = tot.intValue() + dataset.getValue(i).intValue();
            }
            Number number = dataset.getValue(key);
            if (dataset != null) {
                int index = dataset.getIndex(key);
                result = legend[index] + " = " + number + " (" + (100 * number.byteValue() / tot.doubleValue()) + "% )";
            }
            return result;
        }
    }
}