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
package graphDisplay;

import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import chart.Chart;
import chart.DatasetUtil;

/**
 * The class to handle the integration of two or more charts' data by sum the
 * dataset of those charts then display in a chart panel
 * 
 * Author Action Date Flag
 * ======================================================================= 
 * TWU    created 1/2/2016
 */

public class SumAcrossChartPane {

	private Chart charts[]; // Input
	private Chart chart[]; // output
	private String name;
	private Map<String, int[]> selectedMeta;

	public SumAcrossChartPane(Chart charts[]) {
		this.charts = charts;
		init();
		createRegionChart();
	}

	void init() {
		selectedMeta = new LinkedHashMap<String, int[]>();
		boolean more = true;
		while (more) {
			JOptionPane pane = new JOptionPane("Please enter a Name", JOptionPane.PLAIN_MESSAGE,
					JOptionPane.OK_CANCEL_OPTION);
			pane.setWantsInput(true);
			JDialog dialog = pane.createDialog("Input Region Name");
			dialog.setVisible(true);
			name = (String) pane.getInputValue();
			JComponent jc = metaSelection();
			Object options[] = { "More", "Done" };
			JOptionPane pane0 = new JOptionPane(new JScrollPane(jc), -1, 0, null, options, options[0]);
			dialog = pane0.createDialog("Select charts to be grouped");
			dialog.setPreferredSize(new Dimension(600, 200));
			dialog.setLayout(null);
			dialog.setResizable(true);
			dialog.setVisible(true);
			Object selectedValue = pane0.getValue();
			if (selectedValue.equals("Done"))
				more = false;
		}
	}

	void createRegionChart() {
		String[] key = selectedMeta.keySet().toArray(new String[0]);
		chart = new Chart[key.length];
		for (int i = 0; i < key.length; i++) {
			String[][] data = null;
			try {
				data = DatasetUtil.dataset2Data(selectCharts(selectedMeta.get(key[i])));
			} catch (java.lang.IndexOutOfBoundsException e1) {
				data = null;
			}
			if (data == null) {
				JOptionPane.showMessageDialog(null, "No Support for diferent number of technologies for each chart",
						"Information", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			//Dan: Using modified version (2)
			chart[i] = ThumbnailUtil2.createChart(charts[0].getChartClassName(), charts[0].getPath(),
					key[i] + "_" + charts[0].getGraphName(), charts[0].getMeta().split(",")[0] + "| ",
					charts[0].getTitles()[0] + "|" + key[i], charts[0].getAxis_name_unit(), charts[0].getLegend(),
					charts[i].getChartColumn(), data,charts[i].getUnitsLookup())[0];
		}
	}

	private JList<Object> metaSelection() {
		JList<Object> list = GraphDisplayUtil.metaList(charts);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		ListSelectionListener listener = new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				JList<?> list = (JList<?>) e.getSource();
				boolean adjust = e.getValueIsAdjusting();
				if (!adjust) {
					Object[] o = list.getSelectedValuesList().toArray();
					int[] idx = new int[o.length];
					for (int i = 0; i < o.length; i++) {
						String s = (String) o[i];
						idx[i] = Integer.valueOf(s.split(",")[1]);
					}
					selectedMeta.put(name, idx);// list.getSelectedIndices());
				}
			}

		};
		list.addListSelectionListener(listener);
		return list;
	}

	private Chart[] selectCharts(int[] idx) {
		Chart[] selChart = new Chart[idx.length];
		for (int i = 0; i < idx.length; i++)
			selChart[i] = charts[idx[i]];
		return selChart;
	}

	public Chart[] getChart() {
		return chart;
	}

}
