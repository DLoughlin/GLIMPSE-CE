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
 * Agreements 89-92423101 and 89-92549601. Contributors from PNNL include 
 * Maridee Weber, Catherine Ledna, Gokul Iyer, Page Kyle, Marshall Wise, Matthew 
 * Binsted, and Pralit Patel. Coding contributions have also been made by Aaron 
 * Parks and Yadong Xu of ARA through the EPA's Environmental Modeling and 
 * Visualization Laboratory contract. 
 */
package listener;

import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.chart.event.TitleChangeListener;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;

import chart.Chart;

/**
 * ChartTitleChangeListener handles chart title change events.
 * <p>
 * When a chart title changes, this listener updates the chart's title and border as needed.
 * </p>
 *
 * Author: TWU
 * Created: 1/2/2016
 */
public class ChartTitleChangeListener implements TitleChangeListener {

    /** Reference to the chart being listened to. */
    private final Chart chart;

    /**
     * Constructs a ChartTitleChangeListener for the specified chart.
     * @param chart the chart to listen for title changes
     */
    public ChartTitleChangeListener(Chart chart) {
        this.chart = chart;
    }

    /**
     * Invoked when a chart title changes.
     * <ul>
     *   <li>If the title is not a LegendTitle, removes its border.</li>
     *   <li>If the title is a LegendTitle, updates the chart's title.</li>
     * </ul>
     * @param e the event containing the changed title
     */
    @Override
    public void titleChanged(TitleChangeEvent e) {
        Title t = e.getTitle();
        // Check if the title is not a LegendTitle
        if (!t.getClass().getName().equals("org.jfree.chart.title.LegendTitle")) {
            // Remove border from non-legend titles
            t.setBorder(0, 0, 0, 0);
        } else {
            // For legend titles, update the chart's title
            System.out.println("LegendTitle");
            String newTitle = ((TextTitle) t).getText();
            chart.setTitles(newTitle, 0);
        }
    }
}