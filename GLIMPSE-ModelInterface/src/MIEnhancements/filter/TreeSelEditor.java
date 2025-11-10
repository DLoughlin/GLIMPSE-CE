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
package filter;

import java.awt.Color;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * TreeSelEditor is a custom tree cell editor for filter trees.
 * <p>
 * This editor sets a custom border selection color for tree nodes when editing.
 * </p>
 *
 * <p>
 * <b>Author:</b> TWU<br>
 * <b>Date:</b> 1/2/2016
 * </p>
 */
public class TreeSelEditor extends DefaultTreeCellEditor {
    /**
     * Constructs a TreeSelEditor with a specified tree and cell renderer.
     * Sets the border selection color to blue.
     *
     * @param tree        the JTree to be edited
     * @param selRenderer the renderer used to draw the tree cells
     */
    public TreeSelEditor(JTree tree, DefaultTreeCellRenderer selRenderer) {
        super(tree, selRenderer);
        // Set the border color for selected tree nodes to blue
        setBorderSelectionColor(Color.blue);
    }
}