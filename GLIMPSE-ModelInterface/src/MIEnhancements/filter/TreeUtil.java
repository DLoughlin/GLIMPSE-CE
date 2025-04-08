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
* Parks and Yadong Xu of ARA through the EPA’s Environmental Modeling and 
* Visualization Laboratory contract. 
* 
*/
package filter;

import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * The class to handle utility functions for filter package.
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class TreeUtil {
	public static TreePath find(JTree tree, Object nodes[]) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		return find2(tree, new TreePath(root), nodes, 0, false);
	}

	public static TreePath findByName(JTree tree, String names[]) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		return find2(tree, new TreePath(root), names, 0, true);
	}

	private static TreePath find2(JTree tree, TreePath parent, Object nodes[], int depth, boolean byName) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		Object o = node;
		if (byName)
			o = o.toString();
		if (o.equals(nodes[depth])) {
			if (depth == nodes.length - 1)
				return parent;
			if (node.getChildCount() >= 0) {
				for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
					TreeNode n = (TreeNode) e.nextElement();
					TreePath path = parent.pathByAddingChild(n);
					TreePath result = find2(tree, path, nodes, depth + 1, byName);
					if (result != null)
						return result;
				}

			}
		}
		return null;
	}

}
