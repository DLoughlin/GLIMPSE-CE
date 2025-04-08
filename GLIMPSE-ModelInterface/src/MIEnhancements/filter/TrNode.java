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

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * The class to handle a node on the filter tree pane. It carrys parent node value 
 * as key String, as well partial selection flag.
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

class TrNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	protected String nodeName;
	protected String type;
	protected String keyStr;
	protected boolean isSelected;
	protected boolean isPartialSelectedForParent = false;
	protected DefaultMutableTreeNode topNode;

	protected TrNode(String nodename, String type, boolean isSelected, DefaultMutableTreeNode topNode) {
		keyStr = "";
		nodeName = nodename;
		this.type = type;
		this.isSelected = isSelected;
		this.topNode = topNode;
		setKeyStr();
	}

	public boolean isPartialSelectedForParent() {
		return isPartialSelectedForParent;
	}

	public void setPartialSelectedForParent(boolean isPartialSelectedForParent) {
		this.isPartialSelectedForParent = isPartialSelectedForParent;
	}

	public String toString() {
		return nodeName;
	}

	protected void setKeyStr() {
		if (topNode != null && !topNode.toString().equals("Heading")) {
			if (!type.trim().equals("value")) {
				if (topNode.toString().trim().equals("Filter All"))
					keyStr = nodeName;
				else
					keyStr = ((TrNode) topNode.getUserObject()).keyStr.trim() + "|" + nodeName.trim();
			} else
				keyStr = ((TrNode) topNode.getUserObject()).keyStr.trim();
		} else
			keyStr = " ";
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isSelected() {
		return isSelected;
	}

}
