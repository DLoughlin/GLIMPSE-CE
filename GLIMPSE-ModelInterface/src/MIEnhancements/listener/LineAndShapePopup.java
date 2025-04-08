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
package listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import chart.Chart;
import chartOptions.SetModifyChanges;

/**
 * The class handles line and shape options. Referenced classes of package listener:
 *            JPopupMenuShower
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class LineAndShapePopup implements ActionListener {

	private MouseListener mouseListener;
	private boolean lineAndShape;
	private Chart chart;
	public JPopupMenu popup;

	public LineAndShapePopup(JTextField jtf, Chart chart) {
		this.chart = chart;
		popup = new JPopupMenu();
		popup.add(createMenuItem("Line and Shape"));
		popup.add(createMenuItem("Line without Shape"));
		mouseListener = new JPopupMenuShower(popup);
		jtf.addMouseListener(mouseListener);
	}

	public boolean isLineAndShape() {
		return lineAndShape;
	}

	JMenuItem createMenuItem(String name) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.addActionListener(this);
		return menuItem;
	}

	public void actionPerformed(ActionEvent e) {
		if (chart.getChart().getPlot().getPlotType().contains("Category"))
			if (!(chart.getChart().getCategoryPlot().getRenderer() instanceof LineAndShapeRenderer)) {
				JOptionPane.showMessageDialog(null, "Support for Line Chart", "Information",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

		if (chart.getChart().getPlot().getPlotType().contains("XY"))
			if (!(chart.getChart().getXYPlot().getRenderer() instanceof XYLineAndShapeRenderer)) {
				JOptionPane.showMessageDialog(null, "Support for Line Chart", "Information",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

		JMenuItem source = (JMenuItem) e.getSource();
		if (source.getText().equalsIgnoreCase("Line and Shape"))
			lineAndShape = true;
		else
			lineAndShape = false;
		chart.setShowLineAndShape(lineAndShape);
		SetModifyChanges.setLineAndShapeChanges(chart.getChart(), lineAndShape);
	}

}
