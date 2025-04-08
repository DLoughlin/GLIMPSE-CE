package mapOptions;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;


import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;

public class LegendPanel extends JComponent{
	
	private static final long serialVersionUID = -9100503749455967320L;
	private PaintScaleLegend legend;
	private LookupPaintScale paintScale;
	private boolean debug = false;
	
	public LegendPanel(MapColor mapColor,String units) {
		initLegend(mapColor,units);
		//initLegendTest(min,max,units);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		//System.out.println("in LegendPanel.paintComponent");
		if (isOpaque()) { //paint background
			g.setColor(getBackground());
			
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		Graphics2D g2d = (Graphics2D) g.create();
		legend.draw(g2d, g.getClipBounds());
		
		g2d.dispose();
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(200, 150);
	}
	
	public Dimension getMinimumSize() {
		return new Dimension(150, 150);
	}
	
	public void initLegend(MapColor mapColor, String units) {
		//LookupPaintScale scale = createPaintScale(mapColor, min, max);
		boolean verbose=false;
		LookupPaintScale scale = null;
		
		NumberAxis scaleAxis = new NumberAxis(units);
		scaleAxis.setTickMarkPaint(Color.BLACK);
		scaleAxis.setTickMarkStroke(new BasicStroke(1));
		scaleAxis.setTickLabelFont(new Font("Dialog",Font.PLAIN,14));
		
		try {
			//set the tick and tickLable at each color interval
			double interval = mapColor.getIntervalStart(1)-mapColor.getIntervalStart(0);
			double min = mapColor.getIntervalStart(0);
			double max = mapColor.getIntervalStart(mapColor.getColorCount()-1) + interval;
			if (verbose) System.out.println("the min used in legendPanel:"+min);
			if (verbose) System.out.println("the max used in legendPanel:"+max);
			if (verbose) System.out.println("the interval used in legendPanel:"+interval);
			scale = createPaintScale(mapColor,min, max);
			//System.out.println("the scale lowerBound in legendPanel:"+scale.getLowerBound());
			//System.out.println("the scale upperBound in legendPanel:"+scale.getUpperBound());
			if (min > 0) {
				//TickUnits theTickUnits = new TickUnits();
			    //theTickUnits.add(new NumberTickUnit((int)Math.round(min)));
			    //theTickUnits.add(new NumberTickUnit((int)Math.round(max)));
			    //scaleAxis.setStandardTickUnits(theTickUnits);
			    TickUnitSource mySource = scaleAxis.createStandardTickUnits();
			    scaleAxis.setStandardTickUnits(mySource);
			}else if (max <0){
				TickUnitSource mySource = scaleAxis.createStandardTickUnits();
			    scaleAxis.setStandardTickUnits(mySource);
			}else {
				NumberTickUnit myTickUnit = new NumberTickUnit(interval);
				scaleAxis.setAutoRange(false);
				scaleAxis.setTickUnit(myTickUnit);
				scaleAxis.setMinorTickMarksVisible(false);	
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (legend ==null) {
			legend = new PaintScaleLegend(scale,scaleAxis);
			legend.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
			legend.setAxisOffset(0);
			legend.setMargin(new RectangleInsets(2, 2, 2, 2));
			legend.setFrame(new BlockBorder(Color.red));
			legend.setPadding(new RectangleInsets(10, 10, 10, 10));
			legend.setStripWidth(90);
			legend.setPosition(RectangleEdge.RIGHT);
			legend.setBackgroundPaint(Color.WHITE);
		}else {
			legend.setScale(scale);
			legend.setAxis(scaleAxis);
		}
	}
	
	
	public PaintScaleLegend getLegend() {
		 if (debug)
			 System.out.println("in LegendPanel.getLegend");
		return legend;
	}
	

	// creates the legend scale from the map color
	protected LookupPaintScale createPaintScale(MapColor mapColor, double min, double max) {
			
		int colorCount = mapColor.getColorCount();
		LookupPaintScale paintScale = new LookupPaintScale(min, max, Color.GRAY);	
		double interval = (max - min) / colorCount;
		if (max == min) {
			paintScale.add(min, mapColor.getColor(0));
		}else {
		
				for (int i = 0; i < colorCount; i++) {
					double scale = min + (i * interval);
					//double roundedScale = new BigDecimal(scale).setScale(1,BigDecimal.ROUND_HALF_EVEN).doubleValue();
					//System.out.println("inside createPaintScale now,i is:"+i);
					//System.out.println("inside createPaintScale now,just added a scale at:"+scale);
					paintScale.add(scale, mapColor.getColor(i));
				}
		}
		return paintScale;
	}
	


}

