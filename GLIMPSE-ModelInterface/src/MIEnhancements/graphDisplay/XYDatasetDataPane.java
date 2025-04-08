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

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;

import chart.Chart;

/**
 * The class to handle displaying XY chart's data set with functions in display a chart panel. 
 * You can subset the chart by selecting data
 * Referenced classes of package graphDisplay: DataPanel
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class XYDatasetDataPane extends DataPanel {
	private static final long serialVersionUID = 1L;
	private XYDataset ds;

	public XYDatasetDataPane(JFreeChart chart)
			throws CloneNotSupportedException {
		super(chart);
		setDataset();
	}

	public XYDatasetDataPane(Chart[] charts, int id) {
		super(charts, id);
		setDataset();
	}
	
	private void setDataset() {
		getDataFromDataset();
		tableModel.setDataVector(dataValue, tableCol);
		table.getSelectionModel().addListSelectionListener(this);
		SetColumnModel();
		table.updateUI();
	}

	private void getDataFromDataset() {
		int lc = 0;
		for (int i = 0; i < copyChart[id].getXYPlot().getDatasetCount(); i++)
			lc = lc + copyChart[id].getXYPlot().getDataset(i).getSeriesCount();
		ds = copyChart[id].getXYPlot().getDataset(0);
		dataValue = new String[lc][ds.getItemCount(0) + 1];
		tableCol = new String[ds.getItemCount(0) + 1];
		tableCol[0] = copyChart[id].getXYPlot().getDomainAxis().getLabel() != null ? copyChart[id]
				.getXYPlot().getDomainAxis().getLabel()
				: "Series";
		for (int i = 0; i < lc; i++)
			dataValue[i][0] = copyChart[id].getXYPlot().getLegendItems()//.getFixedLegendItems()
					.get(lc - 1 - i).getLabel();
		for (int i = 0; i < ds.getItemCount(0); i++)
			tableCol[i + 1] = String.valueOf(ds.getXValue(0, i));

		setDigit(ds, 3);
	}

}
