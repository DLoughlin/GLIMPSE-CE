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
//import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.DefaultCategoryItemRenderer;
import org.jfree.chart.renderer.category.LevelRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
//import org.jfree.chart.renderer.category.LineRenderer3D;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
//import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.chart.renderer.xy.XYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
//import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;

/**
 * The class handle to utility functions for JFreeChart renderers. 
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class RendererUtil {
	//3d graphs are no longer supported in JFReechart 1.5
	public static CategoryItemRenderer getCategoryRenderer(String className) {
		if (className.equals("CategoryItemRenderer"))
			return new DefaultCategoryItemRenderer();
		//if (className.equals("StackedBarRenderer3D"))
		//	return new StackedBarRenderer3D();
		if (className.equals("StackedAreaRenderer3D"))
			return new StackedAreaRenderer();
		//if (className.equals("BarRenderer3D"))
		//	return new BarRenderer3D();
		if (className.equals("AreaRenderer"))
			return new AreaRenderer();
		if (className.equals("LevelRenderer"))
			return new LevelRenderer();
		if (className.equals("LineAndShapeRenderer"))
			return new LineAndShapeRenderer();
		return null;
	}

	//no 3d graphs in JFreechart 1.5
	//note that this function had lineRendeer and lineRendere3d switched for initialization
	public static CategoryItemRenderer getNewCategoryRenderer(
			CategoryItemRenderer renderer) {
		if (renderer.getClass().getName()
				.equals("org.jfree.chart.renderer.category.BarRenderer3D"))
			return new BarRenderer();
		//else if (renderer.getClass().getName()
		//		.equals("org.jfree.chart.renderer.category.BarRenderer"))
		//	return new BarRenderer3D();
		//else if (renderer.getClass().getName()
		//		.equals("org.jfree.chart.renderer.category.LineRenderer3D"))
		//return new LineRenderer3D();
		
		else if (renderer.getClass().getName()
				.equals("org.jfree.chart.renderer.category.LineAndShapeRenderer"))
			return new LineAndShapeRenderer();
		
		else if (renderer.getClass().getName()
				.equals("org.jfree.chart.renderer.category.StackedBarRenderer3D"))
			return new StackedBarRenderer();
		//else if (renderer.getClass().getName()
		//		.equals("org.jfree.chart.renderer.category.StackedBarRenderer"))
		//	return new StackedBarRenderer3D();
		return renderer;
	}

	public static void setRendererProperty(CategoryItemRenderer renderer) {
		//renderer.setBaseSeriesVisible(true);
		renderer.setDefaultSeriesVisible(true);
		((AbstractRenderer) renderer).setAutoPopulateSeriesPaint(false);
		((AbstractRenderer) renderer).setAutoPopulateSeriesFillPaint(false);
		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(
				"{2}", NumberFormat.getIntegerInstance()));
		renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator(
				"({0}, {1}) = {2}", new DecimalFormat("0.00")));

	}

	//no 3d graphs in JFreeChart 1.5
	/*
	public static void setRendererProperty(StackedBarRenderer3D renderer) {
		BarRenderer.setDefaultShadowsVisible(false);
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setRenderAsPercentages(false);
		renderer.setDrawBarOutline(false);
		renderer.setShadowVisible(false);
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
				"{2}", NumberFormat.getIntegerInstance()));
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator(
				"({0}, {1}) = {2}", new DecimalFormat("0.00")));
	}
*/
	public static void setRendererProperty(StackedBarRenderer renderer) {
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setRenderAsPercentages(false);
		renderer.setShadowVisible(false);
		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(
				"{2}", NumberFormat.getIntegerInstance()));
		renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator(
				"({0}, {1}) = {2}", new DecimalFormat("0.00")));
	}

	public static void setRendererProperty(StackedAreaRenderer renderer) {
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setDataBoundsIncludesVisibleSeriesOnly(true);
		renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.BASELINE_CENTER));
		renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator(
				"({0}, {1}) = {2}", new DecimalFormat("0.00")));
	}

	public static void setRendererProperty(LineAndShapeRenderer renderer,
			int[] storkeIndex) {
		for (int i = 0; i < storkeIndex.length; i++)
			renderer.setSeriesStroke(i,
					LegendUtil.getLineStroke(storkeIndex[i]));
		setRendererProperty(renderer);
	}

	public static void setRendererProperty(LineAndShapeRenderer renderer) {
		renderer.setDefaultShapesVisible(true);
		renderer.setDefaultSeriesVisible(true);
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(
				"{2}", NumberFormat.getIntegerInstance()));
		renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator(
				"({0}, {1}) = {2}", new DecimalFormat("0.00")));
	}

	/*
	public static void setRendererProperty(LineRenderer3D renderer) {
		renderer.setBaseShapesVisible(true);
		renderer.setBaseSeriesVisible(true);
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
				"{2}", NumberFormat.getIntegerInstance()));
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator(
				"({0}, {1}) = {2}", new DecimalFormat("0.00")));
	}

	public static void setRendererProperty(AreaRenderer renderer) {
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
				"{2}", NumberFormat.getIntegerInstance()));
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator(
				"({0}, {1}) = {2}", new DecimalFormat("0.00")));
	}

	public static void setRendererProperty(BarRenderer3D renderer) {
		BarRenderer.setDefaultShadowsVisible(false);
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setDrawBarOutline(false);
		renderer.setShadowVisible(false);
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator(
				"({0}, {1}) = {2}", new DecimalFormat("0.00")));
	}
*/
	public static void setRendererProperty(BarRenderer renderer) {
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setDrawBarOutline(true);
		renderer.setShadowVisible(false);
		renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator(
				"({0}, {1}) = {2}", new DecimalFormat("0.00")));
	}

	public static BarPainter setBarPainter(BarRenderer renderer,
			final Paint[] paint) {
		BarPainter painter = new BarPainter() {

			@Override
			public void paintBar(Graphics2D g2, BarRenderer renderer, int row,
					int column, RectangularShape bar, RectangleEdge base) {
				bar.setFrame(bar.getX(), bar.getY(), bar.getWidth() + 8,
						bar.getHeight());
				g2.setPaint(paint[row]);
				g2.fill(bar);
				g2.draw(bar);
			}

			@Override
			public void paintBarShadow(Graphics2D g2, BarRenderer renderer,
					int row, int column, RectangularShape bar,
					RectangleEdge base, boolean pegShadow) {
			}
		};
		return painter;
	}

	public static void setRendererProperty(LevelRenderer renderer) {
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesFillPaint(false);
		//converting to Jfreechart 1.5
		renderer.setDefaultSeriesVisible(true);
		renderer.setSeriesStroke(0, new BasicStroke(2.0F));
		renderer.setSeriesStroke(1, new BasicStroke(2.0F));
	}

	public static XYItemRenderer getXYRenderer(String className) {
		if (className.equals("XYLineAndShapeRenderer"))
			return new XYLineAndShapeRenderer();
		if (className.equals("XYDotRenderer"))
			return new XYDotRenderer();
		if (className.equals("XYBarRenderer"))
			return new XYBarRenderer();
		if (className.equals("XYAreaRenderer"))
			return new XYAreaRenderer2();
		if (className.equals("XYDifferenceRenderer"))
			return new XYDifferenceRenderer();
		return null;
	}

	public static void setRendererProperty(XYLineAndShapeRenderer renderer) {
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setUseOutlinePaint(true);
		renderer.setDefaultShapesFilled(false);
		renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
	}
	
	/*
	public static void setRendererProperty(XYLine3DRenderer renderer) {
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setUseOutlinePaint(true);
		renderer.setBaseShapesFilled(true);
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
	}*/


	public static void setRendererProperty(XYItemRenderer renderer) {
		((AbstractRenderer) renderer).setAutoPopulateSeriesPaint(false);
		((AbstractRenderer) renderer).setAutoPopulateSeriesFillPaint(false);
		renderer.setDefaultSeriesVisible(true);
		renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
	}

	public static void setRendererProperty(XYBarRenderer renderer) {
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setDrawBarOutline(false);
		renderer.setUseYInterval(true);
		renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
	}

	public static void setRendererProperty(XYDifferenceRenderer renderer) {
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setRoundXCoordinates(true);
		renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		renderer.setDefaultItemLabelsVisible(true);
		renderer.setDefaultSeriesVisible(true);
		renderer.setSeriesItemLabelsVisible(0, true);
		renderer.setDefaultStroke(new BasicStroke(0.5F, 1, 1, 5.0F, new float[] {
				5.0F, 10.0F }, 0.0F));
		renderer.setSeriesItemLabelGenerator(0,
				new StandardXYItemLabelGenerator());
		renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
	}
}
