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

import java.util.Iterator;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.TextAnchor;

/**
 * Utility class for handling JFreeChart Marker operations.
 * <p>
 * Author: TWU
 * Date: 1/2/2016
 */
public class MarkerUtil {
    /**
     * Adds markers from the provided map to the given JFreeChart instance.
     * Handles both CategoryPlot and XYPlot types.
     *
     * @param jfchart   the JFreeChart to add markers to
     * @param markerMap a map of marker names to Marker objects
     */
    public static void createMarker(JFreeChart jfchart, Map<String, Marker> markerMap) {
        Iterator<String> it = markerMap.keySet().iterator();
        while (it.hasNext()) {
            Marker m = markerMap.get(it.next());
            String plotType = jfchart.getPlot().getPlotType();
            // Handle CategoryPlot markers
            if (plotType.equalsIgnoreCase("Category Plot")) {
                if (m instanceof CategoryMarker) {
                    jfchart.getCategoryPlot().addDomainMarker((CategoryMarker) m);
                } else if (m instanceof org.jfree.chart.plot.IntervalMarker || m instanceof org.jfree.chart.plot.ValueMarker) {
                    jfchart.getCategoryPlot().addRangeMarker(m);
                }
            // Handle XYPlot markers
            } else if (plotType.equalsIgnoreCase("XY Plot")) {
                if (m instanceof CategoryMarker) {
                    jfchart.getXYPlot().addDomainMarker(m);
                } else if (m instanceof org.jfree.chart.plot.IntervalMarker || m instanceof org.jfree.chart.plot.ValueMarker) {
                    jfchart.getXYPlot().addRangeMarker(m);
                }
            }
        }
    }

    /**
     * Returns the RectangleAnchor position for marker labels based on the given name.
     *
     * @param name the position name (e.g., "Bottom", "Top-Left")
     * @return the corresponding RectangleAnchor, or null if not found
     */
    public static RectangleAnchor getMarkerLabelPosition(String name) {
        switch (name) {
            case "Bottom": return RectangleAnchor.BOTTOM;
            case "Bottom-Left": return RectangleAnchor.BOTTOM_LEFT;
            case "Bottom-Right": return RectangleAnchor.BOTTOM_RIGHT;
            case "Center": return RectangleAnchor.CENTER;
            case "Left": return RectangleAnchor.LEFT;
            case "Right": return RectangleAnchor.RIGHT;
            case "Top": return RectangleAnchor.TOP;
            case "Top-Left": return RectangleAnchor.TOP_LEFT;
            case "Top-Right": return RectangleAnchor.TOP_RIGHT;
            default: return null;
        }
    }

    /**
     * Returns the TextAnchor position for marker text labels based on the given name.
     *
     * @param name the text anchor name (e.g., "Center", "Top-Right")
     * @return the corresponding TextAnchor, or null if not found
     */
    public static TextAnchor getMarkerTextLabelPosition(String name) {
        switch (name) {
            case "Baseline-Center": return TextAnchor.BASELINE_CENTER;
            case "Baseline-Left": return TextAnchor.BASELINE_LEFT;
            case "Baseline-Right": return TextAnchor.BASELINE_RIGHT;
            case "Bottom-Center": return TextAnchor.BOTTOM_CENTER;
            case "Bottom-Left": return TextAnchor.BOTTOM_LEFT;
            case "Bottom-Right": return TextAnchor.BOTTOM_RIGHT;
            case "Center": return TextAnchor.CENTER;
            case "Center-Left": return TextAnchor.CENTER_LEFT;
            case "Center-Right": return TextAnchor.CENTER_RIGHT;
            case "Top-Center": return TextAnchor.TOP_CENTER;
            case "Top-Left": return TextAnchor.TOP_LEFT;
            case "Top-Right": return TextAnchor.TOP_RIGHT;
            case "Half-Ascent-Center": return TextAnchor.HALF_ASCENT_CENTER;
            case "Half-Ascent-Left": return TextAnchor.HALF_ASCENT_LEFT;
            case "Half-Ascent-Right": return TextAnchor.HALF_ASCENT_RIGHT;
            default: return null;
        }
    }
}