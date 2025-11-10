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
package chartOptions;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * LegendHelpButton is a custom JButton that displays a help icon ('?') for legend modification.
 * When clicked, it opens a LegendHelpList dialog for the specified legend name.
 * The button also visually responds to mouse hover events by painting its border.
 *
 * Author: TWU
 * Date: 1/2/2016
 */
public class LegendHelpButton extends JButton {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a LegendHelpButton with the given name and help content.
     *
     * @param name the legend name associated with this help button
     * @param s    the help content (currently unused)
     */
    public LegendHelpButton(String name, String[] s) {
        super();
        this.setName(name); // Set the button's name for identification
        this.setText("?"); // Display '?' as the button label
        // Add mouse listener to show help dialog on click
        MouseListener ml = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() > 0) {
                    new LegendHelpList(name.trim()); // Show help dialog
                }
            }
        };
        this.addMouseListener(ml);
        setContentAreaFilled(false); // Make button background transparent
        setFocusable(false); // Prevent button from gaining focus
        setBorder(BorderFactory.createEtchedBorder()); // Set etched border
        addMouseListener(buttonMouseListener); // Add hover effect listener
        setRolloverEnabled(true); // Enable rollover effects
        this.setPreferredSize(new Dimension(10, 10)); // Set button size
    }

    /**
     * MouseListener for hover effects: paints border on mouse enter, removes on exit.
     */
    protected static final MouseListener buttonMouseListener = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true); // Show border on hover
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false); // Hide border when not hovered
            }
        }
    };

}