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
package graphDisplay;

import java.awt.Cursor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import chart.Chart;
import conversionUtil.ArrayConversion;

/**
 * The class to handle multiple charts displaying with added on functions.
 * Provides thumbnail chart creation and display from table data and metadata.
 * Handles chart pane setup and unit lookup for enhanced chart display.
 *
 * @author TWU
 */
public class Thumbnail {
    private static final Logger LOGGER = Logger.getLogger(Thumbnail.class.getName());
    private static final int DEFAULT_CURSOR_TYPE = Cursor.DEFAULT_CURSOR;
    private static final int WAIT_CURSOR_TYPE = Cursor.WAIT_CURSOR;
    private boolean debug = false;
    private JPanel jp;
    private final Cursor waitCursor = new Cursor(WAIT_CURSOR_TYPE);
    private final Cursor defaultCursor = new Cursor(DEFAULT_CURSOR_TYPE);
    private HashMap<String, String> unitLookup;

    /**
     * Constructs a Thumbnail object and creates thumbnail charts from table data.
     * Sets up the chart pane in the provided JSplitPane.
     *
     * @param chartName the name of a JFreeChart (not null)
     * @param unit the unit label of the chart (not null)
     * @param path the legend property file (nullable)
     * @param cnt index for table data extraction
     * @param jtable table data including meta, column, row names, and values
     * @param metaMap map of metadata keys to row indices
     * @param sp JSplitPane for chart pane display
     * @param unitLookup lookup for units
     * @throws IllegalArgumentException if required arguments are null
     */
    public Thumbnail(String chartName, String[] unit, String path, int cnt, JTable jtable,
            Map<String, Integer[]> metaMap, JSplitPane sp, HashMap<String, String> unitLookup) {
        Objects.requireNonNull(chartName, "chartName must not be null");
        Objects.requireNonNull(unit, "unit must not be null");
        Objects.requireNonNull(jtable, "jtable must not be null");
        Objects.requireNonNull(sp, "JSplitPane must not be null");
        sp.setCursor(waitCursor);
        this.unitLookup = unitLookup;
        if (metaMap == null) {
            metaMap = ModelInterfaceUtil.getMetaIndex2(jtable, cnt);
        }
        String metaCol = ArrayConversion.array2String(ModelInterfaceUtil.getColumnFromTable(jtable, cnt, 2));
        String col = ArrayConversion.array2String(ModelInterfaceUtil.getColumnFromTable(jtable, cnt, 0));
        Chart[] chart = ThumbnailUtilNew.createChart(chartName, unit,
                ModelInterfaceUtil.getColDataFromTable(jtable, jtable.getColumnCount() - 1),
                col,
                ModelInterfaceUtil.getDataFromTable(jtable, cnt, 0), metaMap,
                ModelInterfaceUtil.getLegend2(metaMap, ModelInterfaceUtil.getDataFromTable(jtable, cnt, 1)), path,
                metaCol, unitLookup);
        int idx = ThumbnailUtilNew.getFirstNonNullChart(chart);
        if (idx != -1 && chart[idx] != null) {
            jp = ThumbnailUtilNew.setChartPane(chart, idx, false, true, sp);
        } else {
            LOGGER.log(Level.WARNING, "No valid chart found for thumbnail creation.");
            jp = new JPanel();
        }
        sp.setCursor(defaultCursor);
        logDebugMemory();
    }

    /**
     * Returns the JPanel containing the chart thumbnails.
     * @return JPanel with chart thumbnails
     */
    public JPanel getJp() {
        return jp;
    }

    /**
     * Logs debug memory information if debug is enabled.
     */
    private void logDebugMemory() {
        if (debug) {
            LOGGER.log(Level.INFO, String.format("Thumbnail::Thumbnail:max memory %d total: %d free: %d",
                    Runtime.getRuntime().maxMemory(),
                    Runtime.getRuntime().totalMemory(),
                    Runtime.getRuntime().freeMemory()));
        }
    }
}