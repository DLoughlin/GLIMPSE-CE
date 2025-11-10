package mapOptions;

import java.awt.Color;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * MapColor manages color mapping for map visualizations, including intervals, palette, and scale type.
 * It supports linear scaling and several palette types for different visualization needs.
 */
public class MapColor implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean debug = false;
    private MapColorPalette palette;
    private double min, max;
    private double[] intervals;
    private PaletteType paletteType = PaletteType.DIVERGING;
    private String formatString = null;
    private ScaleType scaleType = ScaleType.LINEAR;

    /**
     * Supported scale types for color mapping.
     */
    public enum ScaleType {
        LINEAR
    }

    /**
     * Supported palette types for color mapping.
     */
    public enum PaletteType {
        SEQUENTIAL, QUALITATIVE, DIVERGING
    }

    /**
     * Returns the PaletteType enum for a given string.
     * @param type the palette type as a string
     * @return the PaletteType or null if not found
     */
    public static PaletteType getPaletteType(String type) {
        for (PaletteType pType : PaletteType.values()) {
            if (pType.toString().equalsIgnoreCase(type))
                return pType;
        }
        return null;
    }

    /**
     * Returns the ScaleType enum for a given string.
     * @param type the scale type as a string
     * @return the ScaleType or null if not found
     */
    public static ScaleType getScaleType(String type) {
        for (ScaleType iType : ScaleType.values()) {
            if (iType.toString().equalsIgnoreCase(type))
                return iType;
        }
        return null;
    }

    /**
     * Default constructor initializes with an empty palette.
     */
    public MapColor() {
        palette = new MapColorPalette(new Color[0], "", false);
    }

    /**
     * Constructs a MapColor with a palette, min, and max values (linear scale).
     * @param palette the color palette
     * @param min the minimum value
     * @param max the maximum value
     */
    public MapColor(MapColorPalette palette, double min, double max) {
        this.min = min;
        this.max = max;
        this.palette = new MapColorPalette(palette);
        calcIntervals(palette, this.min, this.max);
        if (debug)
            System.out.println("in constructor for MapColor using Palette, min, max");
    }

    /**
     * Calculates intervals for the color mapping based on palette and min/max.
     * @param palette the color palette
     * @param min the minimum value
     * @param max the maximum value
     */
    private void calcIntervals(MapColorPalette palette, double min, double max) {
        int colorCount = palette.getColorCount();
        this.intervals = new double[colorCount];
        double interval = (max - min) / colorCount;
        for (int i = 0; i < colorCount; i++) {
            double toBeRounded = min + (i * interval);
            intervals[i] = toBeRounded;
        }
        if (debug)
            System.out.println("finished with calcIntervals using Palette, min, max");
    }

    /**
     * Constructs a MapColor with palette and custom steps (linear scale).
     * @param palette the color palette
     * @param steps the step values for intervals
     * @param logSteps unused, for future logarithmic support
     * @param scaleType the scale type
     */
    public MapColor(MapColorPalette palette, List<Double> steps, List<Double> logSteps, ScaleType scaleType) {
        this.scaleType = scaleType;
        Collections.sort(steps);
        Collections.sort(logSteps);
        int stepSize = steps.size();
        if (this.scaleType == ScaleType.LINEAR) {
            this.min = steps.get(0);
            this.max = steps.get(stepSize - 1);
        }
        this.palette = new MapColorPalette(palette);
        int colorCount = palette.getColorCount();
        // Setup default color map step/range intervals
        this.intervals = new double[colorCount + 1];
        for (int i = 0; i < stepSize; i++) {
            this.intervals[i] = steps.get(i);
        }
    }

    /**
     * Returns the maximum index for intervals.
     */
    public int getMaxIndex() {
        return intervals.length - 1;
    }

    /**
     * Returns the step value at the given index, with bounds checking.
     * @param index the interval index
     * @return the step value
     * @throws Exception if index is out of bounds
     */
    public double getStep(int index) throws Exception {
        if (index > intervals.length - 1)
            return intervals[intervals.length - 1];
        if (index < 0)
            return intervals[0];
        return intervals[index];
    }

    /**
     * Returns the maximum value for the color mapping.
     */
    public double getMax() throws Exception {
        return max;
    }

    /**
     * Returns the minimum value for the color mapping.
     */
    public double getMin() throws Exception {
        return min;
    }

    /**
     * Returns the intervals array.
     */
    public double[] getIntervals() throws Exception {
        return intervals;
    }

    /**
     * Returns the palette type.
     */
    public PaletteType getPaletteType() {
        return paletteType;
    }

    /**
     * Sets the palette type.
     * @param paletteType the palette type
     */
    public void setPaletteType(PaletteType paletteType) {
        this.paletteType = paletteType;
    }

    /**
     * Returns the color palette.
     */
    public MapColorPalette getPalette() {
        return palette;
    }

    /**
     * Sets the color palette and recalculates intervals if color count changes.
     * @param palette the new palette
     */
    public void setPalette(MapColorPalette palette) {
        int prevIntervals = this.palette.getColorCount();
        int curIntervals = palette.getColorCount();
        this.palette = new MapColorPalette(palette);
        if (prevIntervals != curIntervals) {
            calcIntervals(palette, min, max);
        }
    }

    /**
     * Returns the color at the given index.
     * @param index the color index
     */
    public Color getColor(int index) {
        return palette.getColor(index);
    }

    /**
     * Sets the color at the given index in the palette.
     * @param index the color index
     * @param color the color to set
     */
    public void setColor(int index, Color color) {
        palette.setColor(index, color);
    }

    /**
     * Returns the number of colors in the palette.
     */
    public int getColorCount() {
        return palette.getColorCount();
    }

    /**
     * Returns the interval start value at the given index.
     * @param index the interval index
     * @throws Exception if index is out of bounds
     */
    public double getIntervalStart(int index) throws Exception {
        return intervals[index];
    }

    /**
     * Sets the interval start value at the given index (linear scale only).
     * @param index the interval index
     * @param start the start value
     * @throws Exception if index is out of bounds
     */
    public void setIntervalStart(int index, double start) throws Exception {
        if (this.scaleType == ScaleType.LINEAR) {
            int last = intervals.length - 1;
            if (paletteType == PaletteType.SEQUENTIAL && index > 0) {
                if (index == 0)
                    min = start;
                if (index == last)
                    max = start + (start - intervals[index - 1]);
            }
            intervals[index] = start;
            Arrays.sort(intervals);
        }
    }

    /**
     * Returns the scale type.
     */
    public ScaleType getScaleType() {
        return scaleType;
    }

    /**
     * Sets the scale type.
     * @param scaleType the scale type
     */
    public void setScaleType(ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    /**
     * Returns the format string for value display.
     */
    public String getFormatString() throws Exception {
        return formatString;
    }

    /**
     * Sets the min and max values and recalculates intervals.
     * @param min the minimum value
     * @param max the maximum value
     */
    public void setMinMax(double min, double max) {
        this.min = min;
        this.max = max;
        calcIntervals(palette, min, max);
    }

    /**
     * Sets the min and max values, optionally keeping overridden intervals.
     * @param min the minimum value
     * @param max the maximum value
     * @param keepOverridenIntervals whether to keep custom intervals
     */
    public void setMinMax(double min, double max, boolean keepOverridenIntervals) {
        this.min = min;
        this.max = max;
        if (keepOverridenIntervals) {
            if (intervals[intervals.length - 1] == 0)
                intervals[intervals.length - 1] = max;
            return;
        }
        calcIntervals(palette, min, max);
    }
}