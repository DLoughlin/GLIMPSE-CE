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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;

import conversionUtil.ArrayConversion;

/**
 * The class handle utility functions for legends. 
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class LegendUtil {
	private static boolean debug = false;
	public static final String[] patternList = { "-4162", "-4126", "11", "14", "16", "17" };
	public static final String[] strokeList = { "0", "5", "10", "20", "30", "40" };

	public static LegendItemCollection crtLegenditemcollection(String[] legend, int[] color, int[] pattern,
			int[] pColor, int[] stroke_i) {
		LegendItemCollection legenditemcollection = new LegendItemCollection();
		for (int i = legend.length - 1; i > -1; i--) {
			String key = legend[i].trim();
			LegendItem legenditem = null;
			java.awt.TexturePaint tp = null;
			if (pattern != null && pattern[i] != -4105 && pattern[i] != 1) {
				tp = LegendUtil.getTexturePaint(new Color(color[i]), new Color(pColor[i]), pattern[i], stroke_i[i]);
				legenditem = new LegendItem(key, "-", null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, tp);
			} else
				legenditem = new LegendItem(key, "-", null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, new Color(color[i]));
			legenditemcollection.add(legenditem);
		}
		return legenditemcollection;
	}

	public static LegendItemCollection crtLegenditemcollection(String[] legend, int[] color) {
		LegendItemCollection legenditemcollection = new LegendItemCollection();
		for (int i = legend.length - 1; i > -1; i--) {
			String key = legend[i].trim();
			LegendItem legenditem = null;
			legenditem = new LegendItem(key, "-", null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, new Color(color[i]));
			legenditemcollection.add(legenditem);
		}
		return legenditemcollection;
	}

	public static LegendItemCollection crtLegenditemcollection(String[] legend, TexturePaint[] tp, HashMap <String,JComboBox> jcbLookup) {
		LegendItemCollection legenditemcollection = new LegendItemCollection();
		for (int i = legend.length - 1; i > -1; i--) {
			String key = legend[i].trim();
			//hack to address problem with non-matching series in graphics when transposing...
			TexturePaint paintstyle=null;
			JComboBox jcb=jcbLookup.get(legend[i]);
			int j =jcb.getSelectedIndex();
			try{
				paintstyle=tp[j];
			} catch(Exception e) {
				paintstyle=tp[0];
			}
			LegendItem legenditem = new LegendItem(key, "-", null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, paintstyle);
			legenditemcollection.add(legenditem);
		}
		return legenditemcollection;
	}

	public static LegendItemCollection crtLegenditemcollection(String[] legend, Paint[] tp) {
		LegendItemCollection legenditemcollection = new LegendItemCollection();
		for (int i = legend.length - 1; i > -1; i--) {
		
			String key = legend[i].trim();
			LegendItem legenditem = new LegendItem(key, "-", null, null, Plot.DEFAULT_LEGEND_ITEM_BOX,
					tp[i]);
			legenditemcollection.add(legenditem);
		}
		return legenditemcollection;
	}

	public static LegendItemCollection crtLegenditemcollection(XYPlot plot, String[] legend, Paint[] tp) {
		LegendItemCollection legenditemcollection = new LegendItemCollection();
		for (int i = legend.length - 1; i > -1; i--) {
			if (plot.getRenderer().isSeriesVisible(i)) {
				String key = legend[i].trim();			
				if (debug)
					System.out.println("crtLegenditemcollection::key: " + key);
				LegendItem legenditem = new LegendItem(key, "-", null, null, Plot.DEFAULT_LEGEND_ITEM_BOX,
						tp[legend.length - 1 - i]);
				legenditemcollection.add(legenditem);
			}
		}
		return legenditemcollection;
	}

	public static int checkIndex(String target, String[] result) {
		List<String> l = Arrays.asList(result);
		return l.indexOf(target);
	}

	public static LegendItemCollection getLegendItemCollectionFromChart(JFreeChart chart) {
		if (chart.getPlot().getPlotType().contains("Category"))
			return chart.getCategoryPlot().getLegendItems();
		else
			return chart.getXYPlot().getLegendItems();
	}

	public static LegendTitle getDistLegendItemCollectionFromChart(JFreeChart chart) {
		JFreeChart chartC = null;
		try {
			chartC = (JFreeChart) chart.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		LegendItemCollection lc = null;
		if (chart.getPlot().getPlotType().contains("Category"))
			lc = chart.getCategoryPlot().getFixedLegendItems();
		else
			lc = chart.getXYPlot().getFixedLegendItems();

		Map<String, String> lgdMap = new LinkedHashMap<String, String>();
		for (int i = 0; i < lc.getItemCount(); i++)
			lgdMap.put(lc.get(i).getLabel(), String.valueOf(i));
		String[] l = new String[lgdMap.size()];
		TexturePaint[] tp = new TexturePaint[lgdMap.size()];
		int[] c = new int[lgdMap.size()];
		String[] key = lgdMap.keySet().toArray(new String[0]);
		for (int i = 0; i < key.length; i++) {
			l[i] = key[i];
			c[i] = lc.get(Integer.valueOf(lgdMap.get(key[i])).intValue()).getFillPaint().hashCode();
			tp[i] = (TexturePaint) lc.get(Integer.valueOf(lgdMap.get(key[i])).intValue()).getFillPaint();
		}

		lc = crtLegenditemcollection(l, tp);
		chartC.getCategoryPlot().setFixedLegendItems(lc);
		LegendTitle lt = new LegendTitle(chartC.getCategoryPlot());
		return lt;
	}

	public static String[] getLegendLabels(JFreeChart jfchart) {
		if (jfchart.getPlot().getPlotType().equals("Category Plot"))
			return getLegendLabels(jfchart.getCategoryPlot().getLegendItems());
		else if (jfchart.getPlot().getPlotType().equals("XY Plot"))
			return getLegendLabels(jfchart.getXYPlot().getLegendItems());
		else if (jfchart.getPlot().getPlotType().equals("Pie Plot"))
			return getLegendLabels(((PiePlot) jfchart.getPlot()).getLegendItems());
		else
			return null;
	}

	public static String getLegendLabel(JFreeChart jfchart) {
		if (jfchart.getPlot().getPlotType().equals("Category Plot"))
			return getLegendLabel(jfchart.getCategoryPlot().getFixedLegendItems());
		else
			return getLegendLabel(jfchart.getXYPlot().getLegendItems());
	}

	public static String[] getLegendLabels(LegendItemCollection lc) {
		String[] ll = new String[lc.getItemCount()];
		for (int i = 0; i < lc.getItemCount(); i++)
			ll[i] = lc.get(i).getLabel();
		return ll;
	}

	public static String getLegendLabel(LegendItemCollection lc) {
		return ArrayConversion.array2String(getLegendLabels(lc));
	}

	public static int[] getLegendColor(JFreeChart jfchart) {
		if (jfchart.getPlot().getPlotType().equals("Category Plot"))
			return getLegendColor(jfchart.getCategoryPlot().getFixedLegendItems());
		else
			return getLegendColor(jfchart.getXYPlot().getLegendItems());
	}

	public static int[] getLegendColor(LegendItemCollection lc) {
		int[] tp = new int[lc.getItemCount()];
		for (int i = 0; i < lc.getItemCount(); i++) {
			Color c = (Color) lc.get(i).getFillPaint();
			if (c.getRed()==255 && c.getGreen()==255 && c.getBlue() > 153) {
				//we don't want any colors to close to white, won't be visible on graph
				c = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*153));
			}
			tp[i] = c.getRGB();
		}
		return tp;
	}

	public static Paint[] getLegendPaint(JFreeChart jfchart) {
		if (jfchart.getPlot().getPlotType().equals("Category Plot"))
			return getLegendPaint(jfchart.getCategoryPlot().getLegendItems());
		else if (jfchart.getPlot().getPlotType().contains("Pie"))
			return getLegendPaint(((PiePlot) jfchart.getPlot()).getLegendItems());
		else if (jfchart.getPlot().getPlotType().equals("XY Plot"))
			return getLegendPaint(jfchart.getXYPlot().getLegendItems());
		else
			return null;
	}

	public static Paint[] getLegendPaint(LegendItemCollection lc) {
		Paint[] tp = new Paint[lc.getItemCount()];
		for (int i = 0; i < lc.getItemCount(); i++) {
			tp[i] = lc.get(i).getFillPaint();
		}
		return tp;
	}

	public static LegendItemCollection adjLenend(int[] indexs, LegendItemCollection lc) {
		LegendItemCollection legendItemCollection = new LegendItemCollection();
		Stack<LegendItem> s = new Stack<LegendItem>();
		for (int i = 0; i < indexs.length; i++) {
			int c = lc.getItemCount() - 1 - indexs[i];
			if (debug)
				System.out.println("LegendUtil::LegendItemCollection:i: " + i + " index: " + indexs[i] + " lc count: "
						+ lc.getItemCount());
			LegendItem legenditem = lc.get(c);
			s.push(legenditem);
		}
		int sc = s.size();
		for (int i = 0; i < sc; i++) {
			LegendItem legenditem = s.pop();
			legendItemCollection.add(legenditem);
		}
		return legendItemCollection;
	}

	public static Color getColorbyRGB(int r, int g, int b) {
		Color color = new Color(r, g, b);
		return color;
	}

	public static int getRGB(Color color) {
		int c = color.getRGB();
		return c;
	}

	public static Color getRGB(int c) {
		Color color = new Color(c);
		return color;
	}

	public static TexturePaint getTexturePaint(BufferedImage image, Color color) {
		int imW = 10;
		int imH = 10;
		Graphics2D g2im = image.createGraphics();
		g2im.setColor(color);
		g2im.fillRect(0, 0, imW, imH);
		g2im.setStroke(new BasicStroke(1.0F));
		g2im.drawImage(image, null, 0, 0);
		return new TexturePaint(image, new Rectangle(imW, imH));
	}

	public static TexturePaint getTexturePaint(Color color, int pattern) {
		TexturePaint tp = null;
		String name = "";
		FileSystemView fsv = FileSystemView.getFileSystemView();
		JFileChooser chooser = new JFileChooser(fsv);
		chooser.setDialogTitle("Select files location");

		int returnVal = chooser.showOpenDialog(chooser);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			name = chooser.getSelectedFile().getAbsolutePath();

		if (name != null) {
			BufferedImage image = null;
			try {
				File input = new File(name);
				image = ImageIO.read(input);
			} catch (IOException ex) {
				System.out.println("error: " + ex.getMessage());
			}
			tp = getTexturePaint(image, color);
		}
		return tp;
	}

	public static BufferedImage getBufferedImage(Color baseColor, Color color, int size) {
		int imW = size;
		int imH = size;
		BufferedImage im = new BufferedImage(imW, imH, 1);
		Graphics2D g2im = im.createGraphics();
		g2im.setColor(baseColor);
		g2im.fillRect(0, 0, imW, imH);
		g2im.setColor(color);
		g2im.setStroke(new BasicStroke(2.0F));
		g2im.drawLine(0, imH, imW, 0);
		g2im.drawRect(0, 0, imH, imW);
		return im;
	}

	public static BufferedImage getBufferedImage(Color baseColor, Color color) {
		int imW = 10;
		int imH = 10;
		BufferedImage im = new BufferedImage(imW, imH, 1);
		Graphics2D g2im = im.createGraphics();
		g2im.setColor(baseColor);
		g2im.fillRect(0, 0, imW, imH);
		g2im.setColor(color);
		g2im.setStroke(new BasicStroke(2.0F));
		//g2im.drawLine(0, imH, imW, 0);
		g2im.drawRect(0, 0, imH, imW);
		return im;
	}
	
	public static TexturePaint setTexturePaintColor(TexturePaint paint, Color color) {
		Graphics2D g2im = paint.getImage().createGraphics();
		g2im.setColor(color);
		return paint;
	}

	public static int Hex2RGB(String h) {
		int r = Integer.parseInt(h.substring(0, 2), 16);
		int g = Integer.parseInt(h.substring(2, 4), 16);
		int b = Integer.parseInt(h.substring(4, 6), 16);
		Color color = new Color(r, g, b);
		return color.getRGB();
	}

	static String getPatternFile(int pattern) {
		String rs = null;
		switch (pattern) {
		case -4162:
			rs = "PatternedFill26.jpg";
			break;
		case 11:
			rs = "PatternedFill13.jpg";
			break;
		case 14:
			rs = "PatternedFill44.jpg";
			break;
		case 16:
			rs = "PatternedFill38.jpg";
			break;
		case 17:
			rs = "PatternedFill37.jpg";
			break;
		}
		return rs;
	}

	public static TexturePaint getTexturePaint(Color color, Color pcolor, int pattern, int stroke_i) {
		TexturePaint tp = null;
		BasicStroke stroke_w = getLineStroke(stroke_i);
		//if (color.getRed() == color.getGreen() && color.getGreen() == color.getBlue() && color.getRed() < 100)
		//	pcolor = Color.magenta;

		switch (pattern) {
		case -4126:
			tp = getBufferedImageO(color, pcolor, stroke_w);
			break;
		case -4162:
			tp = getBufferedImageU(color, pcolor, stroke_w);
			break;
		case 11:
			tp = getBufferedImageH(color, pcolor, stroke_w);
			break;
		case 14:
			tp = getBufferedImageV(color, pcolor, stroke_w);
			break;
		case 16:
			tp = getBufferedImageX(color, pcolor, stroke_w);
			break;
		case 17:
			tp = getBufferedImageD(color, pcolor, stroke_w);
			break;
		default:
			tp = getDefaultTP(color, stroke_w);
			break;
		}
		return tp;
	}

	public static TexturePaint getDefaultTP(Color paint, BasicStroke stroke_w) {
		int imW = 10;
		int imH = 10;
		BufferedImage im = new BufferedImage(imW, imH, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2im = im.createGraphics();
		g2im.setStroke(stroke_w);
		g2im.setColor(paint);
		g2im.fillRect(0, 0, imW, imH);
		return new TexturePaint(im, new Rectangle(imW, imH));
	}

	public static TexturePaint getBufferedImageU(Color paint, Color stk_paint, BasicStroke stroke_w) {
		int imW = 10;
		int imH = 10;
		BufferedImage im = new BufferedImage(imW, imH, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2im = im.createGraphics();
		g2im.setColor(paint);
		g2im.setStroke(stroke_w);
		g2im.fillRect(0, 0, imW, imH);
		g2im.setColor(stk_paint);
		g2im.drawLine(0, imH, imW, 0);
		return new TexturePaint(im, new Rectangle(imW, imH));
	}

	public static TexturePaint getBufferedImageD(Color paint, Color stk_paint, BasicStroke stroke_w) {
		int imW = 10;
		int imH = 10;
		BufferedImage im = new BufferedImage(imW, imH, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2im = im.createGraphics();
		g2im.setColor(paint);
		g2im.setStroke(stroke_w);
		g2im.fillRect(0, 0, imW, imH);
		g2im.setColor(stk_paint);
		g2im.drawLine(0, 0, imW, imH);
		return new TexturePaint(im, new Rectangle(imW, imH));
	}

	public static TexturePaint getBufferedImageX(Color paint, Color stk_paint, BasicStroke stroke_w) {
		int imW = 10;
		int imH = 10;
		BufferedImage im = new BufferedImage(imW, imH, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2im = im.createGraphics();
		g2im.setColor(paint);
		g2im.setStroke(stroke_w);
		g2im.fillRect(0, 0, imW, imH);
		g2im.setColor(stk_paint);
		g2im.drawLine(0, imH, imW, 0);
		g2im.drawLine(0, 0, imW, imH);
		return new TexturePaint(im, new Rectangle(imW, imH));
	}

	public static TexturePaint getBufferedImageH(Color paint, Color stk_paint, BasicStroke stroke_w) {
		int imW = 10;
		int imH = 10;
		BufferedImage im = new BufferedImage(imW, imH, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2im = im.createGraphics();
		g2im.setColor(paint);
		g2im.setStroke(stroke_w);
		g2im.fillRect(0, 0, imW, imH);
		g2im.setColor(stk_paint);
		g2im.drawLine(0, imH / 2, imW, imH / 2);
		return new TexturePaint(im, new Rectangle(imW, imH));
	}

	public static TexturePaint getBufferedImageV(Color paint, Color stk_paint, BasicStroke stroke_w) {
		int imW = 10;
		int imH = 10;
		BufferedImage im = new BufferedImage(imW, imH, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2im = im.createGraphics();
		g2im.setColor(paint);
		g2im.setStroke(stroke_w);
		g2im.fillRect(0, 0, imW, imH);
		g2im.setColor(stk_paint);
		g2im.drawLine(imW / 2, 0, imW / 2, imH);
		return new TexturePaint(im, new Rectangle(imW, imH));
	}

	public static TexturePaint getBufferedImageO(Color paint, Color stk_paint, BasicStroke stroke_w) {
		int imW = 10;
		int imH = 10;
		BufferedImage im = new BufferedImage(imW, imH, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2im = im.createGraphics();
		g2im.setColor(paint);
		g2im.setStroke(stroke_w);
		g2im.fillRect(0, 0, imW, imH);
		g2im.setColor(stk_paint);
		g2im.fillOval(imW / 8, imW / 8, 3 * imW / 4, 3 * imH / 4);
		return new TexturePaint(im, new Rectangle(imW, imH));
	}

	public static BasicStroke getLineStroke(int dashIndex) {
		BasicStroke stroke = null;
		switch (dashIndex) {
		case 0:
			stroke = new BasicStroke(1.0F);
			break;
		case 5:
			stroke = new BasicStroke(2.0F);
			break;
		case 10:
			stroke = new BasicStroke(3.0F);
			break;
		case 20:
			stroke = new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 2.0F, 6.0F }, 0.0F);
			break;
		case 30:
			stroke = new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 4.0F, 6.0F }, 0.0F);
			break;
		case 40:
			stroke = new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 6.0F, 6.0F }, 0.0F);
			break;
		default:
			stroke = new BasicStroke(2.0F);

		}
		return stroke;
	}
}
