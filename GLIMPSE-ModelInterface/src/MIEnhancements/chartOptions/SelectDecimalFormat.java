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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import graphDisplay.DataPanel;
import listener.ListMouseListener;

/**
 * The class to handle a decimal degit of a data has been modified.
 * 
 * Author Action Date Flag
 * ======================================================================= TWU
 * created 1/2/2016
 */

public class SelectDecimalFormat extends JOptionPane {
	private static final long serialVersionUID = 1L;
	private String digits[] = { "*.####", "*.###", "*.##", "*.#", "*" };
	private int maxDigit = 4;
	protected DataPanel dataPane;
	protected String dataValue[][];
	protected int doubleIndex;

	public SelectDecimalFormat(DataPanel dataPane) {
		this.dataPane = dataPane;
		setSelectDecimalFormatUI();
	}

	public SelectDecimalFormat(final String sValue[][], int doubleIndex) {
		this.dataValue = sValue.clone();
		this.doubleIndex = doubleIndex;
		setSelectDecimalFormatUI();
	}

	private void setSelectDecimalFormatUI() {
		this.setMessage(selectDecimalFormatBox());
		this.messageType = -1;
		this.optionType = 0;
		JDialog dialog = this.createDialog("Decimal Format");
		dialog.setLayout(null);
		dialog.setResizable(true);
		dialog.setVisible(true);
		dialog.setSize(new Dimension(400, 300));
	}

	JPanel selectDecimalFormatBox() {
		JPanel jp = new JPanel(new BorderLayout());
		JLabel jl = new JLabel("Select a Decimal Format", JLabel.LEADING);
		jl.setFont(new Font("Verdana", 1, 12));
		jl.setPreferredSize(new Dimension(200, 30));
		jl.setBorder(BorderFactory.createEmptyBorder(10, 1, 5, 1));
		jp.add(jl, BorderLayout.NORTH);
		JList<String> list = new JList<String>(digits);
		list.setName("digits");
		// list.setSelectedIndex(2);
		list.setFont(new Font("Verdana", 0, 12));
		list.setVisibleRowCount(1);
		ListSelectionListener lsl = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				JList<?> list = (JList<?>) e.getSource();
				boolean adjust = e.getValueIsAdjusting();

				if (!adjust) {
					if (dataPane != null) {
						if (dataPane.getChart().getPlot().getPlotType().contains("Category")) {
							dataPane.setDigit(dataPane.getCds(), maxDigit - list.getSelectedIndex());
						} else if (dataPane.getChart().getPlot().getPlotType().contains("XY"))
							dataPane.setDigit(dataPane.getDs(), maxDigit - list.getSelectedIndex());
					} else {
						for (int i = 0; i < dataValue.length; i++)
							for (int j = doubleIndex; j < dataValue[i].length - 1; j++)
								dataValue[i][j] = String.valueOf(conversionUtil.DataConversion.roundDouble(
										Double.valueOf(dataValue[i][j]), maxDigit - list.getSelectedIndex()));
					}
				}
			}
		};
		list.addListSelectionListener(lsl);
		list.addMouseListener(new ListMouseListener(list));
		JScrollPane jsp = new JScrollPane(list);
		jsp.setAutoscrolls(true);
		JViewport jvp = jsp.getViewport();
		java.awt.Point p = list.indexToLocation(2);
		jvp.setViewPosition(p);
		jsp.setViewport(jvp);
		jsp.updateUI();
		jsp.setBorder(BorderFactory.createEmptyBorder(10, 1, 5, 80));
		jsp.setPreferredSize(new Dimension(150, 120));
		jp.add(jsp, BorderLayout.CENTER);
		return jp;
	}

	public String[][] getDataValue() {
		return dataValue;
	}

}
