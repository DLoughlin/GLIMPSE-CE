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

import java.awt.Cursor;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import chart.Chart;
import conversionUtil.ArrayConversion;

/**
 * The class to handle multiple charts displaying with added on functions
 * 
 * @author TWU
 *
 */
public class Thumbnail2 {
	private boolean debug = false;
	private JPanel jp;
	private Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
	private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	HashMap<String,String> unitLookup=null;

	/**
	 * Create thumb nail charts from table data.
	 *
	 * @param chartName
	 *            the name of a JFreeChart ({@code null} not permitted).
	 * @param unit
	 *            the unit label of the chart ({@code null} not permitted).
	 * @param path
	 *            the legend property file ({@code null} permitted).
	 * @param jtable
	 *            table data includes selected meta data, column and row names,
	 *            the values of column and row. ({@code null} not permitted).
	 */

	//Dan: Step 1 in thumbnail creation; generates array of charts and adds them to panel. Panel can be retrieved via public method
	public Thumbnail2(String chartName, String[] unit, String path, int cnt, JTable jtable,
			Map<String, Integer[]> metaMap, JSplitPane sp, HashMap<String,String> unitLookup) {

		sp.setCursor(waitCursor);
		this.unitLookup=unitLookup;
		if (metaMap == null)
			metaMap = ModelInterfaceUtil.getMetaIndex2(jtable, cnt);
		//ModelInterfaceUtil.getDataFromTable(jtable, 4);
		String metaCol = ArrayConversion.array2String(ModelInterfaceUtil.getColumnFromTable(jtable, cnt, 2));
		// create charts for Thumbnail
		String col = ArrayConversion.array2String(ModelInterfaceUtil.getColumnFromTable(jtable, cnt, 0));
//		Chart[] chart = ThumbnailUtil.createChart(chartName, unit, col,
//				ModelInterfaceUtil.getDataFromTable(jtable, cnt, 0), metaMap,
//				ModelInterfaceUtil.getLegend(metaMap, ModelInterfaceUtil.getDataFromTable(jtable, cnt, 1)), path,
//				metaCol);
		//new version that gets 

		Chart[] chart = ThumbnailUtil2.createChart(chartName, unit, 
				ModelInterfaceUtil.getColDataFromTable(jtable, jtable.getColumnCount()-1),
				col,
				ModelInterfaceUtil.getDataFromTable(jtable, cnt, 0), metaMap,
				ModelInterfaceUtil.getLegend2(metaMap, ModelInterfaceUtil.getDataFromTable(jtable, cnt, 1)), path,
				metaCol,unitLookup);
		int idx = ThumbnailUtil2.getFirstNonNullChart(chart);
		if (chart[idx] != null)
			jp = ThumbnailUtil2.setChartPane(chart, idx, false, true, sp);
		sp.setCursor(defaultCursor);
		if (debug)
			System.out.println("Thumbnail::Thumbnail:max memory " + Runtime.getRuntime().maxMemory() + " total: "
					+ Runtime.getRuntime().totalMemory() + " free: " + Runtime.getRuntime().freeMemory());
	}

	public JPanel getJp() {
		return jp;
	}

}
