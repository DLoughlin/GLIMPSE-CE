package mapOptions;

import java.awt.Color;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapColor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean debug = false;
	
	private MapColorPalette palette;
	private double min, max;
	private double[] intervals;
	
	private PaletteType paletteType = PaletteType.DIVERGING;
	private String formatString = null;
	
	private ScaleType scaleType = ScaleType.LINEAR;

	public enum ScaleType {
		LINEAR
	}

	public enum PaletteType {
		SEQUENTIAL, QUALITATIVE, DIVERGING
	}

	public static PaletteType getPaletteType(String type) {
		for (PaletteType pType : PaletteType.values()) {
			if (pType.toString().equalsIgnoreCase(type))
				return pType;
		}

		return null;
	}
	
	public static ScaleType getScaleType(String type) {
		for (ScaleType iType : ScaleType.values()) {
			if (iType.toString().equalsIgnoreCase(type))
				return iType;
		}

		return null;
	}	

	public MapColor() {
		palette = new MapColorPalette(new Color[0], "", false);
	}
	
	public MapColor(MapColorPalette palette, double min, double max) { // NOT for logarithm
// 2014 if this function is NOT for logarithm, why does it call logIntervals?
		this.min = min;
		this.max = max;
		this.palette = new MapColorPalette(palette);
		calcIntervals(palette, this.min, this.max);
		if (debug)
		System.out.println("in constructor for MapColor using Palette, min, max");
	}	

	private void calcIntervals(MapColorPalette palette, double min, double max) {
		int colorCount = palette.getColorCount();
		this.intervals = new double[colorCount];
		double interval = (max - min) / colorCount;
		for (int i = 0; i < colorCount; i++) {
			double toBeRounded = min + (i * interval);
			//double roundedDouble = new BigDecimal(toBeRounded).setScale(1,BigDecimal.ROUND_HALF_EVEN).doubleValue();
			intervals[i] = toBeRounded; //roundedDouble;
		}
		if (debug)
		System.out.println("finished with calcIntervals using Palette, min, max");
	}
	
	public MapColor(MapColorPalette palette, List<Double> steps, List<Double> logSteps, ScaleType scaleType) { // NOT for logarithm
		
		this.scaleType = scaleType;
		Collections.sort(steps);
		Collections.sort(logSteps);
		int logStepSize = logSteps.size();
		int stepSize = steps.size();
		if ( this.scaleType == ScaleType.LINEAR ) {
			this.min = steps.get(0);
			this.max = steps.get(stepSize - 1);
		} else {
			
		}
		this.palette = new MapColorPalette(palette);
		int colorCount = palette.getColorCount();
		
		//setup default color map step/range intervals
		this.intervals = new double[colorCount + 1];
		for (int i = 0; i < stepSize; i++) {
			this.intervals[i] = steps.get(i);
		}
	}

	public int getMaxIndex() {
		return intervals.length - 1;
	}

	public double getStep(int index) throws Exception {
			if (index > intervals.length - 1)
				return intervals[intervals.length - 1];

			if (index < 0)
				return intervals[0];

			return intervals[index];
				
	}

	public double getMax() throws Exception {			
		return max;				
	}

	public double getMin() throws Exception {	
		return min;		
	}

	public double[] getIntervals() throws Exception {		
		return intervals;
	}

	public PaletteType getPaletteType() {
		return paletteType;
	}

	public void setPaletteType(PaletteType paletteType) {
		this.paletteType = paletteType;
	}

	public MapColorPalette getPalette() {
		return palette;
	}

	public void setPalette(MapColorPalette palette) {
		int prevIntervals = this.palette.getColorCount();
		int curIntervals = palette.getColorCount();
		this.palette = new MapColorPalette(palette); //palette;
		if (prevIntervals != curIntervals) {
			calcIntervals(palette, min, max);
		}
	}

	public Color getColor(int index) {
		return palette.getColor(index);
	}

	public void setColor(int index, Color color) {
		palette.setColor(index, color);
	}

	public int getColorCount() {
		return palette.getColorCount();
	}

	public double getIntervalStart(int index) throws Exception {
		return intervals[index];			

	}

	public void setIntervalStart(int index, double start)
			throws Exception { 
		if ( this.scaleType == ScaleType.LINEAR) {
			int last = intervals.length - 1;

			if (paletteType == PaletteType.SEQUENTIAL && index > 0) {

				if (index == 0)
					min = start;

				if (index == last)
					max = start + (start - intervals[index - 1]);
			}

			intervals[index] = start;
			Arrays.sort(intervals);
		} else {
					
		}			
	}

	public ScaleType getScaleType() {
		return scaleType;
	}

	public void setScaleType(ScaleType scaleType) {
		this.scaleType = scaleType;
	}	
	
	public String getFormatString() throws Exception {
		return formatString;
	}
	
	public void setMinMax(double min, double max) {
		this.min = min;
		this.max = max;
		calcIntervals(palette, min, max);
	}

	public void setMinMax(double min, double max, boolean keepOverridenIntervals) {
		this.min = min;
		this.max = max;
		if (keepOverridenIntervals) {
			if (intervals[intervals.length - 1] == 0)
				intervals[intervals.length - 1]  = max;
			return;
		}
		calcIntervals(palette, min, max);
	}

}

