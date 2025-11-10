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
import javax.swing.JPopupMenu;

/**
 * Handles showing and hiding a JPopupMenu in response to mouse events.
 * <p>
 * Usage: Attach this listener to a component and provide the popup menu to show.
 * </p>
 *
 * Author: TWU
 * Date: 1/2/2016
 */
public class JPopupMenuShower extends MouseAdapter {
    /**
     * The popup menu to show on popup trigger events.
     */
    private final JPopupMenu popup;

    /**
     * Constructs a JPopupMenuShower for the given popup menu.
     *
     * @param popup the JPopupMenu to show
     */
    public JPopupMenuShower(JPopupMenu popup) {
        this.popup = popup;
        // Ensure popup is hidden initially
        popup.setVisible(false);
    }

    /**
     * Shows the popup menu if the event is a popup trigger, otherwise hides it.
     *
     * @param mouseEvent the mouse event to check
     */
    private void showIfPopupTrigger(MouseEvent mouseEvent) {
        if (mouseEvent.isPopupTrigger()) {
            // Show popup at mouse location
            popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        } else {
            // Hide popup if not a trigger
            popup.setVisible(false);
        }
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     * @param mouseEvent the mouse event
     */
    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        showIfPopupTrigger(mouseEvent);
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * @param mouseEvent the mouse event
     */
    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        showIfPopupTrigger(mouseEvent);
    }
}