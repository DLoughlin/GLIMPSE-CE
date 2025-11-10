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

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Handles rendering a node's checkbox and label in the filter tree pane.
 * <p>
 * Displays a tristate checkbox and a label for each tree node, reflecting selection state.
 * </p>
 *
 * Author: TWU
 * Date: 1/2/2016
 */
class TreeSelCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;
    private final JLabel label; // Used for non-TrNode objects
    private final TristateCheckBox checkBox; // Checkbox for selection state
    private final JTextField textField; // Displays node name
    private final JPanel panel; // Panel containing checkbox and text field

    /**
     * Constructs the cell renderer, initializing UI components.
     */
    public TreeSelCellRenderer() {
        label = new JLabel();

        checkBox = new TristateCheckBox();
        checkBox.setBackground(UIManager.getColor("Tree.background"));
        checkBox.setBorder(null);

        textField = new JTextField();
        textField.setEditable(false);
        textField.setBackground(UIManager.getColor("Tree.background"));
        textField.setBorder(null);

        panel = new JPanel();
        panel.setOpaque(false);
        panel.add(checkBox, 0);
        panel.add(textField, 1);
        panel.setMaximumSize(new Dimension(300, 18));
    }

    /**
     * Returns the component used for drawing the cell. Configures the checkbox and label
     * based on the node's selection state and type.
     *
     * @param tree the JTree
     * @param value the value to be rendered (tree node)
     * @param selected whether the node is selected
     * @param expanded whether the node is expanded
     * @param leaf whether the node is a leaf
     * @param row the row index
     * @param hasFocus whether the node has focus
     * @return the component for rendering
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        checkBox.setHalfSelected(false); // Reset half-selected state

        // If node contains a TrNode, render with checkbox and text field
        if (node.getUserObject() instanceof TrNode) {
            TrNode trNode = (TrNode) node.getUserObject();
            // Set checkbox state based on TrNode selection logic
            if (trNode.isPartialSelectedForParent() && !node.isLeaf()) {
                checkBox.setSelected(false);
                checkBox.setHalfSelected(true);
            } else if (selected) {
                checkBox.setHalfSelected(false);
                checkBox.setSelected(trNode.isSelected());
            } else {
                checkBox.setSelected(trNode.isSelected());
            }
            textField.setText(trNode.nodeName);
            return panel;
        } else {
            // For other node types, just show the label
            label.setBackground(UIManager.getColor("Tree.background"));
            label.setText(node.toString());
            return label;
        }
    }
}