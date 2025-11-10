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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JList;

/**
 * ListKeyListener handles key events for a JList to enable match sequence selection.
 * <p>
 * When a key is typed, it selects the next matching item in the list.
 * </p>
 *
 * Author: TWU
 * Date: 1/2/2016
 */
public class ListKeyListener implements KeyListener {

    /**
     * The JList component to operate on.
     */
    private final JList<String> jl;
    
    /**
     * Constructs a ListKeyListener for the given JList.
     * @param jl the JList to attach the listener to
     */
    public ListKeyListener(JList<String> jl) {
        this.jl = jl;
    }

    /**
     * Invoked when a key is typed. Selects the next matching item in the list.
     * @param e the KeyEvent
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Get the character typed
        char c = e.getKeyChar();
        String s = String.valueOf(c);
        // Find the next match in the list
        int i = jl.getNextMatch(s, 0, javax.swing.text.Position.Bias.Forward);
        jl.setSelectedIndex(i);
        // Set focus and appearance properties
        jl.setFocusCycleRoot(true);
        jl.setOpaque(true);
    }

    /**
     * Invoked when a key has been pressed. Not used.
     * @param e the KeyEvent
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // No action needed
    }

    /**
     * Invoked when a key has been released. Not used.
     * @param e the KeyEvent
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // No action needed
    }
}