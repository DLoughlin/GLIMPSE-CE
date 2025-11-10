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

import java.util.Enumeration;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Utility class for tree-related operations in the filter package.
 * <p>
 * Provides methods to search for nodes in a JTree by object or by name.
 * </p>
 *
 * @author TWU
 * @since 1/2/2016
 */
public class TreeUtil {
    /**
     * Finds a TreePath in the given JTree that matches the specified sequence of nodes.
     *
     * @param tree  the JTree to search
     * @param nodes the sequence of nodes to match
     * @return the TreePath if found, otherwise null
     */
    public static TreePath find(JTree tree, Object[] nodes) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        return findRecursive(tree, new TreePath(root), nodes, 0, false);
    }

    /**
     * Finds a TreePath in the given JTree that matches the specified sequence of node names.
     *
     * @param tree  the JTree to search
     * @param names the sequence of node names to match
     * @return the TreePath if found, otherwise null
     */
    public static TreePath findByName(JTree tree, String[] names) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        return findRecursive(tree, new TreePath(root), names, 0, true);
    }

    /**
     * Recursively searches for a TreePath matching the given nodes or node names.
     *
     * @param tree   the JTree being searched
     * @param parent the current TreePath
     * @param nodes  the sequence of nodes or names to match
     * @param depth  the current depth in the sequence
     * @param byName true to match by node name, false to match by object
     * @return the TreePath if found, otherwise null
     */
    private static TreePath findRecursive(JTree tree, TreePath parent, Object[] nodes, int depth, boolean byName) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        Object current = node;
        if (byName) {
            current = current.toString(); // Use node's name for comparison
        }
        // Check if current node matches the target node or name
        if (current.equals(nodes[depth])) {
            // If this is the last node in the sequence, return the path
            if (depth == nodes.length - 1) {
                return parent;
            }
            // Otherwise, recursively search children
            if (node.getChildCount() > 0) {
                for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
                    TreeNode child = (TreeNode) e.nextElement();
                    TreePath childPath = parent.pathByAddingChild(child);
                    TreePath result = findRecursive(tree, childPath, nodes, depth + 1, byName);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        // No match found
        return null;
    }
}