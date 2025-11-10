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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.RectangularShape;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.AreaRenderer;
import org.jfree.chart.renderer.category.BarPainter;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.DefaultCategoryItemRenderer;
import org.jfree.chart.renderer.category.LevelRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;

/**
 * Utility functions for configuring JFreeChart renderers.
 * <p>
 * Author: TWU
 * Date: 1/2/2016
 */
public class RendererUtil {

    /**
     * Returns a CategoryItemRenderer instance based on the class name string.
     *
     * @param className the renderer class name
     * @return CategoryItemRenderer instance or null if not found
     */
    public static CategoryItemRenderer getCategoryRenderer(String className) {
        switch (className) {
            case "CategoryItemRenderer":
                return new DefaultCategoryItemRenderer();
            case "StackedAreaRenderer3D":
                return new StackedAreaRenderer();
            case "AreaRenderer":
                return new AreaRenderer();
            case "LevelRenderer":
                return new LevelRenderer();
            case "LineAndShapeRenderer":
                return new LineAndShapeRenderer();
            default:
                return null;
        }
    }

    /**
     * Returns a new CategoryItemRenderer based on the type of the input renderer.
     * Used for replacing unsupported 3D renderers with 2D equivalents.
     *
     * @param renderer the input renderer
     * @return new CategoryItemRenderer or the original renderer
     */
    public static CategoryItemRenderer getNewCategoryRenderer(CategoryItemRenderer renderer) {
        String name = renderer.getClass().getName();
        if (name.equals("org.jfree.chart.renderer.category.BarRenderer3D")) {
            return new BarRenderer();
        } else if (name.equals("org.jfree.chart.renderer.category.LineAndShapeRenderer")) {
            return new LineAndShapeRenderer();
        } else if (name.equals("org.jfree.chart.renderer.category.StackedBarRenderer3D")) {
            return new StackedBarRenderer();
        }
        return renderer;
    }

    /**
     * Sets common properties for CategoryItemRenderer.
     *
     * @param renderer the renderer to configure
     */
    public static void setRendererProperty(CategoryItemRenderer renderer) {
        renderer.setDefaultSeriesVisible(true);
        ((AbstractRenderer) renderer).setAutoPopulateSeriesPaint(false);
        ((AbstractRenderer) renderer).setAutoPopulateSeriesFillPaint(false);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getIntegerInstance()));
        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator("({0}, {1}) = {2}", new DecimalFormat("0.00")));
    }

    /**
     * Sets properties for StackedBarRenderer.
     *
     * @param renderer the renderer to configure
     */
    public static void setRendererProperty(StackedBarRenderer renderer) {
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        renderer.setAutoPopulateSeriesPaint(false);
        renderer.setAutoPopulateSeriesFillPaint(false);
        renderer.setRenderAsPercentages(false);
        renderer.setShadowVisible(false);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getIntegerInstance()));
        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator("({0}, {1}) = {2}", new DecimalFormat("0.00")));
    }

    /**
     * Sets properties for StackedAreaRenderer.
     *
     * @param renderer the renderer to configure
     */
    public static void setRendererProperty(StackedAreaRenderer renderer) {
        renderer.setAutoPopulateSeriesPaint(false);
        renderer.setAutoPopulateSeriesFillPaint(false);
        renderer.setDataBoundsIncludesVisibleSeriesOnly(true);
        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.BASELINE_CENTER));
        renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator("({0}, {1}) = {2}", new DecimalFormat("0.00")));
    }

    /**
     * Sets properties for LineAndShapeRenderer with custom stroke indices.
     *
     * @param renderer the renderer to configure
     * @param strokeIndex array of stroke indices
     */
    public static void setRendererProperty(LineAndShapeRenderer renderer, int[] strokeIndex) {
        for (int i = 0; i < strokeIndex.length; i++) {
            renderer.setSeriesStroke(i, LegendUtil.getLineStroke(strokeIndex[i]));
        }
        setRendererProperty(renderer);
    }

    /**
     * Sets properties for LineAndShapeRenderer.
     *
     * @param renderer the renderer to configure
     */
    public static void setRendererProperty(LineAndShapeRenderer renderer) {
        renderer.setDefaultShapesVisible(true);
        renderer.setDefaultSeriesVisible(true);
        renderer.setAutoPopulateSeriesPaint(false);
        renderer.setAutoPopulateSeriesFillPaint(false);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getIntegerInstance()));
        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator("({0}, {1}) = {2}", new DecimalFormat("0.00")));
    }

    /**
     * Sets properties for BarRenderer.
     *
     * @param renderer the renderer to configure
     */
    public static void setRendererProperty(BarRenderer renderer) {
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        renderer.setAutoPopulateSeriesPaint(false);
        renderer.setAutoPopulateSeriesFillPaint(false);
        renderer.setDrawBarOutline(true);
        renderer.setShadowVisible(false);
        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator("({0}, {1}) = {2}", new DecimalFormat("0.00")));
    }

    /**
     * Custom BarPainter for BarRenderer, using provided paint array.
     *
     * @param renderer the renderer to configure
     * @param paint array of Paint objects for each series
     * @return BarPainter instance
     */
    public static BarPainter setBarPainter(BarRenderer renderer, final Paint[] paint) {
        return new BarPainter() {
            @Override
            public void paintBar(Graphics2D g2, BarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base) {
                bar.setFrame(bar.getX(), bar.getY(), bar.getWidth() + 8, bar.getHeight());
                g2.setPaint(paint[row]);
                g2.fill(bar);
                g2.draw(bar);
            }
            @Override
            public void paintBarShadow(Graphics2D g2, BarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base, boolean pegShadow) {
                // No shadow painting
            }
        };
    }

    /**
     * Sets properties for LevelRenderer.
     *
     * @param renderer the renderer to configure
     */
    public static void setRendererProperty(LevelRenderer renderer) {
        renderer.setAutoPopulateSeriesPaint(false);
        renderer.setAutoPopulateSeriesFillPaint(false);
        renderer.setDefaultSeriesVisible(true);
        renderer.setSeriesStroke(0, new BasicStroke(2.0F));
        renderer.setSeriesStroke(1, new BasicStroke(2.0F));
    }

    /**
     * Returns an XYItemRenderer instance based on the class name string.
     *
     * @param className the renderer class name
     * @return XYItemRenderer instance or null if not found
     */
    public static XYItemRenderer getXYRenderer(String className) {
        switch (className) {
            case "XYLineAndShapeRenderer":
                return new XYLineAndShapeRenderer();
            case "XYDotRenderer":
                return new XYDotRenderer();
            case "XYBarRenderer":
                return new XYBarRenderer();
            case "XYAreaRenderer":
                return new XYAreaRenderer2();
            case "XYDifferenceRenderer":
                return new XYDifferenceRenderer();
            default:
                return null;
        }
    }

    /**
     * Sets properties for XYLineAndShapeRenderer.
     *
     * @param renderer the renderer to configure
     */
    public static void setRendererProperty(XYLineAndShapeRenderer renderer) {
        renderer.setAutoPopulateSeriesPaint(false);
        renderer.setAutoPopulateSeriesFillPaint(false);
        renderer.setUseOutlinePaint(true);
        renderer.setDefaultShapesFilled(false);
        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
    }

    /**
     * Sets properties for XYItemRenderer.
     *
     * @param renderer the renderer to configure
     */
    public static void setRendererProperty(XYItemRenderer renderer) {
        ((AbstractRenderer) renderer).setAutoPopulateSeriesPaint(false);
        ((AbstractRenderer) renderer).setAutoPopulateSeriesFillPaint(false);
        renderer.setDefaultSeriesVisible(true);
        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
    }

    /**
     * Sets properties for XYBarRenderer.
     *
     * @param renderer the renderer to configure
     */
    public static void setRendererProperty(XYBarRenderer renderer) {
        renderer.setAutoPopulateSeriesPaint(false);
        renderer.setAutoPopulateSeriesFillPaint(false);
        renderer.setDrawBarOutline(false);
        renderer.setUseYInterval(true);
        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
    }

    /**
     * Sets properties for XYDifferenceRenderer.
     *
     * @param renderer the renderer to configure
     */
    public static void setRendererProperty(XYDifferenceRenderer renderer) {
        renderer.setAutoPopulateSeriesPaint(false);
        renderer.setAutoPopulateSeriesFillPaint(false);
        renderer.setRoundXCoordinates(true);
        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultSeriesVisible(true);
        renderer.setSeriesItemLabelsVisible(0, true);
        renderer.setDefaultStroke(new BasicStroke(0.5F, 1, 1, 5.0F, new float[] { 5.0F, 10.0F }, 0.0F));
        renderer.setSeriesItemLabelGenerator(0, new StandardXYItemLabelGenerator());
        renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
    }
}