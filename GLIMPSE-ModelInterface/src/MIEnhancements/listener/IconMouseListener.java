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
package listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import chart.Chart;
import graphDisplay.AChartDisplay;

/**
 * Handles mouse events for chart icons in the thumbnail panel.
 * When the icon is clicked, displays the chart in a new window.
 *
 * <p>Author: TWU
 * <p>Date: 1/2/2016
 */
public class IconMouseListener extends MouseAdapter {
    /** Array of Chart objects to display. */
    private Chart[] chart;
    /** Index of the chart to display. */
    private int id;

    /**
     * Constructs an IconMouseListener for a chart array and chart index.
     *
     * @param chart Array of Chart objects
     * @param id Index of the chart to display
     */
    public IconMouseListener(Chart[] chart, final int id) {
        this.chart = chart;
        this.id = id;
    }

    /**
     * Invoked when the mouse is clicked on the icon.
     * If left mouse button is clicked, opens the chart display.
     *
     * @param e MouseEvent triggered by the click
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            // Open the chart display window for the selected chart
            new AChartDisplay(chart, id);
        }
    }

    /**
     * Invoked when the mouse exits the icon area.
     * Currently not used.
     *
     * @param mouseevent MouseEvent triggered by mouse exit
     */
    @Override
    public void mouseExited(MouseEvent mouseevent) {
        // No action needed on mouse exit
    }

    /**
     * Invoked when the mouse button is pressed on the icon.
     * Currently not used.
     *
     * @param e MouseEvent triggered by mouse press
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // No action needed on mouse press
    }

    /**
     * Invoked when the mouse button is released on the icon.
     * Currently not used.
     *
     * @param e MouseEvent triggered by mouse release
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // No action needed on mouse release
    }
}