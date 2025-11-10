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

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Represents a node in the filter tree pane. Stores the node's name, type, selection state,
 * and key string for identification. Supports partial selection for parent nodes.
 *
 * <p>Author: TWU
 * <p>Date: 1/2/2016
 */
public class TrNode extends DefaultMutableTreeNode {

    private static final long serialVersionUID = 1L;
    /** Display name of the node. */
    protected String nodeName;
    /** Type of the node (e.g., category, value). */
    protected String type;
    /** Key string representing the node's path in the tree. */
    protected String keyStr;
    /** Selection state of the node. */
    protected boolean isSelected;
    /** Indicates if the parent is partially selected. */
    protected boolean isPartialSelectedForParent = false;
    /** Reference to the top node in the tree. */
    protected DefaultMutableTreeNode topNode;

    /**
     * Constructs a TrNode with the specified properties.
     *
     * @param nodename Name of the node
     * @param type Type of the node
     * @param isSelected Selection state
     * @param topNode Reference to the top node
     */
    protected TrNode(String nodename, String type, boolean isSelected, DefaultMutableTreeNode topNode) {
        keyStr = "";
        nodeName = nodename;
        this.type = type;
        this.isSelected = isSelected;
        this.topNode = topNode;
        setKeyStr();
    }

    /**
     * Returns whether the parent is partially selected.
     * @return true if parent is partially selected, false otherwise
     */
    public boolean isPartialSelectedForParent() {
        return isPartialSelectedForParent;
    }

    /**
     * Sets the partial selection state for the parent.
     * @param isPartialSelectedForParent true if parent is partially selected
     */
    public void setPartialSelectedForParent(boolean isPartialSelectedForParent) {
        this.isPartialSelectedForParent = isPartialSelectedForParent;
    }

    /**
     * Returns the display name of the node.
     * @return node name
     */
    @Override
    public String toString() {
        return nodeName;
    }

    /**
     * Sets the key string for this node based on its position in the tree.
     */
    protected void setKeyStr() {
        // If topNode is not null and not a heading, build keyStr based on type and parent
        if (topNode != null && !topNode.toString().equals("Heading")) {
            if (!type.trim().equals("value")) {
                if (topNode.toString().trim().equals("Filter All")) {
                    keyStr = nodeName;
                } else {
                    keyStr = ((TrNode) topNode.getUserObject()).keyStr.trim() + "|" + nodeName.trim();
                }
            } else {
                keyStr = ((TrNode) topNode.getUserObject()).keyStr.trim();
            }
        } else {
            keyStr = " ";
        }
    }

    /**
     * Sets the selection state of the node.
     * @param isSelected true if selected, false otherwise
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * Returns the selection state of the node.
     * @return true if selected, false otherwise
     */
    public boolean isSelected() {
        return isSelected;
    }

}