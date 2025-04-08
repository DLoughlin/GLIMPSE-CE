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
package chartOptions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import chart.LegendUtil;

/**
 * The class to handle showing supported legend lists
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class LegendHelpList extends JDialog {
	private static final long serialVersionUID = 1L;

	public LegendHelpList(String type) {
		String[] legendList = null;
		if (type.trim().equals("patternList"))
			legendList = LegendUtil.patternList.clone();
		else if (type.trim().equals("strokeList"))
			legendList = LegendUtil.strokeList.clone();

		BufferedImage[] image = new BufferedImage[legendList.length];
		for (int i = 0; i < legendList.length; i++)
			if (type.trim().equals("patternList"))
				image[i] = (BufferedImage) LegendUtil
						.getTexturePaint(Color.green, Color.darkGray, Integer.valueOf(legendList[i]).intValue(), 0)
						.getImage();
			else if (type.trim().equals("strokeList"))
				image[i] = (BufferedImage) LegendUtil
						.getTexturePaint(Color.green, Color.darkGray, 11, Integer.valueOf(legendList[i]).intValue())
						.getImage();

		JPanel jp = new JPanel(new GridLayout(legendList.length, 1));
		jp.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 5));

		for (int i = 0; i < legendList.length; i++)
			jp.add(new JLabel(legendList[i], new ImageIcon(image[i].getScaledInstance(120, 20, Image.SCALE_SMOOTH)),
					SwingConstants.LEFT));

		add(jp);
		setTitle("Legend Help for " + type);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setSize(new Dimension(220, 220));
		setResizable(false);
		setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new LegendHelpList("strokeList");
			}
		});
	}
}
