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

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

import chart.LegendUtil;

/**
 * The class to handle the status change of a tree (select/unselect/partial select) ont a branch.
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class TristateCheckBox extends JCheckBox {

	private static final long serialVersionUID = 1L;
	private boolean halfState;
	private static Icon halfselected = new ImageIcon(LegendUtil.getBufferedImage(Color.white, Color.black, 10));
	private static Icon unselected = new ImageIcon(LegendUtil.getBufferedImage(Color.white, Color.black));

	@Override
	public void paint(Graphics g) {
		if (isSelected()) {
			halfState = false;
		}
		setIcon(halfState ? halfselected : isSelected() ? super.getSelectedIcon() : unselected);
		super.paint(g);
	}

	public boolean isHalfSelected() {
		return halfState;
	}

	public void setHalfSelected(boolean halfState) {
		this.halfState = halfState;
		if (halfState) {
			setSelected(false);
			repaint();
		}
	}

}