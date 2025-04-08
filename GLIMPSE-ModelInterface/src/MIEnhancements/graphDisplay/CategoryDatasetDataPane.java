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

import java.util.ArrayList;
import java.util.HashMap;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import chart.Chart;

/**
 * The class to handle displaying category chart's data set with functions in display a chart panel. 
 * You can subset the chart by selecting data
 * Referenced classes of package graphDisplay: DataPanel
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class CategoryDatasetDataPane extends DataPanel {
	private static final long serialVersionUID = 1L;
	private HashMap<String,String> unitLookup=null;

	public CategoryDatasetDataPane(JFreeChart chart)// ChartPanel
			throws CloneNotSupportedException {
		super(chart);
		CategoryDatasetData();
	}


	public CategoryDatasetDataPane(Chart[] charts, int id, HashMap<String,String> unitLookup) {
		super(charts, id);
		this.unitLookup=unitLookup;
		CategoryDatasetData();
	}
	
	
//	@SuppressWarnings("unchecked")
	protected void CategoryDatasetData() {
		int dsCount = copyChart[id].getCategoryPlot().getDatasetCount();
		cds = (DefaultCategoryDataset[]) new DefaultCategoryDataset[dsCount];
		cds[0] = (DefaultCategoryDataset) copyChart[id].getCategoryPlot().getDataset(0);
		int rc = cds[0].getRowCount();
		int cc = cds[0].getColumnCount();

		ArrayList<String> c = new ArrayList<String>();
		c.addAll(cds[0].getColumnKeys());
		for (int i = 1; i < copyChart[id].getCategoryPlot().getDatasetCount(); i++) {
			cds[i] = (DefaultCategoryDataset) copyChart[id].getCategoryPlot().getDataset(i);
			if (cds[1].getColumnCount() != cc && !c.get(0).equals(cds[i].getColumnKey(0))) {
				addRow = false;
				cc = cc + cds[i].getRowCount();
				c.addAll(cds[i].getRowKeys());
			} else {
				rc = rc + cds[i].getRowCount();
			}
		}

		String[] col = c.toArray(new String[0]);
		tableCol = new String[cc + 2];
		setDataset();

		tableCol[0] = copyChart[id].getCategoryPlot().getDomainAxis().getLabel() != null
				? copyChart[id].getCategoryPlot().getDomainAxis().getLabel() : "Series";
		tableCol[cc + 1] = "";
		System.arraycopy(col, 0, tableCol, 1, col.length);
		tableCol[tableCol.length-1]="units";

		dataValue = new String[rc][cc + 2];

		for (int i = 0; i < rc; i++) {
				dataValue[i][0] = (String) chart[id].getCategoryPlot().getDataset().getRowKey(i);
				if(unitLookup!=null) {
					dataValue[i][dataValue[0].length - 1] = unitLookup.get(dataValue[i][0]);
				}
		}
		setDigit(cds, 3);
	}

	public int getVisibleRowCount(CategoryPlot plot) {
		int cnt = 0;
		for (int i = 0; i < plot.getDataset().getRowCount(); i++)
			if (plot.getRenderer().getSeriesVisible(i)) {
				cnt++;
			}
		return cnt;
	}

	private void setDataset() {
		tableModel.setDataVector(dataValue, tableCol);
		table.getSelectionModel().addListSelectionListener(this);
		SetColumnModel();
		table.updateUI();
	}

}
