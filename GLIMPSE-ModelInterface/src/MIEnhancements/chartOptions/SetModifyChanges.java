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
package chartOptions;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import chart.Chart;
import chart.LegendUtil;
import conversionUtil.ArrayConversion;

/**
 * The class to handle a legend has been modified.
 * 
 * Author Action Date Flag
 * ======================================================================= TWU
 * created 1/2/2016
 */

public class SetModifyChanges {
	protected static boolean debug = false;

	public static void setLegendChanges(Chart[] chart, int id, JTextField tf) {
		for (int i = 0; i < chart.length; i++) {
			String[] legend = chart[i].getLegend().split(",");
			//int idx = Arrays.asList(legend).indexOf(legend[Integer.valueOf(tf.getName().trim()).intValue()].trim());
			//int idx = Arrays.asList(legend).indexOf(
			chart[i].setLegend(ArrayConversion.array2String(legend));
			
			for(int j=0;j<legend.length;j++) {
				//legend[j] = tf.getText().trim();
				
				setLegenditemcollection(chart[i], legend, chart[i].getPaint());
				setModifyChanges(chart[i], chart[i].getPaint());
				if (debug)
					System.out.println("SetModifyChanges::setLegendChanges:legend " + chart[i].getLegend());
				ChartUtils.applyCurrentTheme(chart[id].getChart());
			}
		}
	}

	public static void setColorChanges(Chart[] chart) {
		//changing the color and tp are handled in the window eve
		//here we just need to loop over and apply explicilty to
		//each graph
		for (int i = 0; i < chart.length; i++) {
			String[] legend = chart[i].getLegend().split(",");
			TexturePaint[] tp_color = chart[i].getPaint();
			
		
			setLegenditemcollection(chart[i], legend, tp_color);
			setModifyChanges(chart[i], tp_color);
			//chart[i].getChart().fireChartChanged();
			if (debug)
				System.out.println(
						"SetModifyChanges::setLegendChanges:color " + Arrays.toString(chart[i].getColor()));
		
			
		}
	}

	public static void setPatternChanges(Chart[] chart, HashMap<String,JComboBox> comboLookup, int[] patternNums) {

		for (int i = 0; i < chart.length; i++) {
			String[] legend = chart[i].getLegend().split(",");
			TexturePaint[] tp_color = chart[i].getPaint();
			//int idx = Arrays.asList(legend).indexOf(changeColLegend);

			//if (idx > -1) {
			for(int j=0;j<legend.length;j++) {
				//int pattern = Integer.parseInt(tf.getText().trim());
				//int sel=comboLookup.get(legend[j]).getSelectedIndex();
				int sel=0;
				if(comboLookup.get(legend[j].trim())!=null) {
					sel=comboLookup.get(legend[j].trim()).getSelectedIndex();
				}
				Color c=new Color(chart[i].getColor()[j]);
				Color contrast=new Color(255-c.getRed(),255-c.getGreen(),255-c.getBlue());
				TexturePaint tpMerge=LegendUtil.getTexturePaint(new Color(chart[i].getColor()[j]),contrast,patternNums[sel],1);
				
				tp_color[j] = tpMerge;
				//chart[i].setPaint(tp);
				chart[i].setPattern(patternNums[sel], j);
				//if (i == id)
				//	SetModifyChanges.updateButton(jb, chart[i].getPaint()[idx]);
				setLegenditemcollection(chart[i], legend, tp_color);
				setModifyChanges(chart[i], tp_color);
			}
		}
	}

	public static JFreeChart setModifyChanges(Chart chart, TexturePaint[] tp) {
		AbstractRenderer renderer = null;
		JFreeChart jfchart = chart.getChart();

		//if (jfchart.getPlot().getPlotType().contains("Pie")) {
		//	PiePlot plot = (PiePlot) jfchart.getPlot();
		//	String label = plot.getLegendItems().get(idx).getLabel();
		//	plot.setSectionPaint(label, tp[idx]);
		//} else {
			if (jfchart.getPlot().getPlotType().equalsIgnoreCase("Category Plot")) {
				CategoryPlot plot = jfchart.getCategoryPlot();
				renderer = (AbstractRenderer) plot.getRenderer();
			} else if (jfchart.getPlot().getPlotType().equalsIgnoreCase("XY Plot")) {
				XYPlot plot = (XYPlot) jfchart.getPlot();
				renderer = (AbstractRenderer) plot.getRenderer();
			}

			//if (idx < tp.length)
			//	renderer.setSeriesPaint(idx, tp[idx]);
			for(int idx=0;idx<tp.length;idx++) {
				renderer.setSeriesPaint(idx, tp[idx]);
			}
		//}
		//jfchart.getLegend().visible = true;
		ChartUtils.applyCurrentTheme(jfchart);
		return jfchart;
	}

	public static void setLegenditemcollection(Chart chart, String[] legend, TexturePaint[] color) {

		if (chart.getChartClassName().contains("Category")) {
			chart.getChart().getCategoryPlot().setFixedLegendItems(LegendUtil.crtLegenditemcollection(legend, color));
		} else if (chart.getChartClassName().contains("XY")) {
			chart.getChart().getXYPlot().setFixedLegendItems(LegendUtil.crtLegenditemcollection(legend, color));
		} //else  //pie
			//for (int i = 0; i < color.length; i++)
			//	((PiePlot)chart.getChart().getPlot()).setSectionPaint(legend[i], color[i]);
	}

	public static void setStrokeChanges(Chart[] chart, HashMap<String,JComboBox> strokeLookup, BasicStroke[] stroke, int[] stokeVals) {
		for(int j=0;j<chart.length;j++) {
		
			if (!chart[j].getChartClassName().contains("Line"))
				continue;
	
			String[] legend = chart[j].getLegend().split(",");
			//int idx = Integer.valueOf(tf.getName().trim());
			int[] ls = chart[j].getLineStrokes();
			//ls[idx] = Integer.parseInt(tf.getText().trim());
			JFreeChart jfchart = chart[j].getChart();
			for (int i = 0; i < legend.length; i++) {
				//chart[i].setLineStrokes(ls);
				
				int idx=strokeLookup.get(legend[i].trim()).getSelectedIndex();
				if (jfchart.getPlot().getPlotType().equalsIgnoreCase("Category Plot")) {
					CategoryPlot plot = jfchart.getCategoryPlot();
					LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
					
					renderer.setSeriesStroke(i, stroke[idx]);
					ls[i]=stokeVals[idx];
				} else if (jfchart.getPlot().getPlotType().equalsIgnoreCase("XY Plot")) {
					XYPlot plot = jfchart.getXYPlot();
					XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
					renderer.setSeriesStroke(i, stroke[idx]);
					ls[i]=stokeVals[idx];
				}
				ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
				ChartUtils.applyCurrentTheme(jfchart);
			}
		}
	}

	public static void updateButton(JButton jb, Paint paint) {
		ImageIcon icon = new ImageIcon(((TexturePaint) paint).getImage());
		Image image = icon.getImage();
		image = image.getScaledInstance(80, 20, 4);
		icon.setImage(image);
		jb.setIcon(icon);
	}

	public static void setLineAndShapeChanges(JFreeChart jfchart, boolean lineAndShape) {
		if (jfchart.getPlot().getPlotType().equalsIgnoreCase("Category Plot")) {
			CategoryPlot plot = jfchart.getCategoryPlot();
			LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
			for (int i = 0; i < plot.getLegendItems().getItemCount(); i++)
				((LineAndShapeRenderer) renderer).setSeriesShapesVisible(i, lineAndShape);
		} else if (jfchart.getPlot().getPlotType().equalsIgnoreCase("XY Plot")) {
			XYPlot plot = jfchart.getXYPlot();
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
			for (int i = 0; i < plot.getLegendItems().getItemCount(); i++)
				((XYLineAndShapeRenderer) renderer).setSeriesShapesVisible(i, lineAndShape);
		}
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		ChartUtils.applyCurrentTheme(jfchart);
	}

}
