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
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import chart.Chart;
import chart.DatasetUtil;
import conversionUtil.ArrayConversion;

/**
 * The class to handle transpose row and column of a data set then display chars
 * in a panel
 * 
 * Author Action Date Flag
 * ======================================================================= TWU
 * created 1/2/2016
 */

public class Transpose {

	// Dan: to-do: rework this so graphics don't have to have the same number of
	// series
	private Chart[] transChart;
	private static boolean debug = false;

	public Transpose(Chart[] chart, int w, int gridWidth, boolean sameScale, JSplitPane sp) {
		String meta = ArrayConversion.array2String(getMetaArray(chart));
		ArrayList<String[][]> al = new ArrayList<String[][]>();
		ArrayList<String> master_legend=getMasterLegend(chart);
		
		String[] legend=convertArrayListToArray(master_legend);
		
		try {
			//al = getTransposeData(chart, transChart);
			al = getTransposeData(master_legend,transChart);
			// al =
			// getTransposeDataOrig(chart[ThumbnailUtil.getFirstNonNullChart(chart)].getLegend().split(","),
			// transChart);
		} catch (java.lang.NullPointerException e1) {
			al.clear();
		} catch (java.lang.IndexOutOfBoundsException e1) {
			al.clear();
		}

		if (al.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No Support for diferent number of technologies for each chart",
					"Information", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		//Dan: Using modified version (2)
		int idx = ThumbnailUtil2.getFirstNonNullChart(chart);
		Chart[] chart1 = ThumbnailUtil.createChart(chart[idx].getGraphName(), chart[idx].getAxis_name_unit(), meta,
				chart[idx].getChartColumn(), al, legend);

		if (debug)
			System.out.println("Transpose::Transpose:input " + chart1.length + " trans: " + transChart.length
					+ " transpose: " + chart1.length);

		//Dan: Using modified version (2)
		JPanel jp = ThumbnailUtil2.setChartPane(chart1, 0, sameScale, true, sp);
		JDialog dialog = CreateComponent.crtJDialog("Transpose Thumbnails: " + chart1[0].getGraphName());
		dialog.setContentPane(new JScrollPane(jp));// new JScrollPane(chartPane)
		dialog.pack();
		dialog.setSize(new Dimension(705, 805));
		dialog.setVisible(true);

	}
	
	private String[] convertArrayListToArray(ArrayList<String> from_list) {
		String[] to_list=new String[from_list.size()];
		for (int i=0;i<from_list.size();i++) {
			to_list[i]=from_list.get(i);
		}
		return to_list;
	}
	
	private ArrayList<String> getMasterLegend(Chart[] chart_array) {
		ArrayList<String> master_legend = new ArrayList<String>();

		for (int c = 0; c < chart_array.length; c++) {
			// get legend from chart
			String[] legend = chart_array[c].getLegend().split(",");

			// builds list of legend items across graphics
			for (int i = 0; i < legend.length; i++) {

				boolean has_match = false;
				for (int j = 0; j < master_legend.size(); j++) {
					if (master_legend.get(j).equals(legend[i].trim())) {
						has_match = true;
					}
				}
				if (!has_match)
					master_legend.add(legend[i].trim());
			}
		}
		
		return master_legend;
	}
	
	private ArrayList<String[][]> getTransposeData(ArrayList<String> master_legend, Chart[] chart) {
		ArrayList<String[][]> al = new ArrayList<String[][]>();
		
		//iterates through the series and pulls data for the legend item
		for (int i = 0; i < master_legend.size(); i++) {
			//String[][] s = DatasetUtil.oneSeriesDataset2Data(chart, i);
			String[][] s = getDataset2Data(chart,i,master_legend.get(i),master_legend);
			if (s != null) {
				al.add(s);
				if (debug)
				  System.out.println("Legend item: "+master_legend.get(i)+" "+i+" of "+master_legend.size()+ " data: " + al.get(i).length + "  "
						+ Arrays.toString(al.get(i)[0]));
			}
		}

		return al;
	}

	
	
	private String[][] getDataset2Data(Chart[] chart,int series_no,String series,ArrayList<String> series_list){
		String[][] data=new String[chart.length][series_list.size()];
		//initialize data to zeroes
		for (int i=0;i<chart.length;i++) {
			for (int j=0;j<series_list.size();j++) {
				data[i][j]="0.0";
			}
		}
		
		//iteratates over all the charts
		int k=0;
		for (int idx=0;idx < chart.length && chart[idx].getChart() !=null; idx++) {
			
			k++;
			//for chart idx, get legend
			String[] chart_legend = chart[idx].getLegend().split(",");
			
			//gets the legend number that matches the series
			int legend_no=-1;
			for (int l=0;l<chart_legend.length;l++) {
				if (chart_legend[l].trim().equals(series.trim())) { 
					legend_no=l;
					break;
				}
			}
			
			//if it found a match
			if (legend_no>-1) {
			   data[idx] = DatasetUtil.dataset2Data(chart[idx].getChart(),legend_no)[0];
			}
		}
			
		return Arrays.copyOfRange(data, 0, k);
	}

//	private ArrayList<String[][]> getTransposeDataOrig(String[] legend, Chart[] chart) {
//		ArrayList<String[][]> al = new ArrayList<String[][]>();
//		for (int i = 0; i < legend.length; i++) {
//			String[][] s = DatasetUtil.oneSeriesDataset2Data(chart, i);
//			al.add(s);
//			// if (debug)
//			System.out.println("Transpose::getTransposeData:i " + i + " : " + " data: " + al.get(i).length + "  "
//					+ Arrays.toString(al.get(i)[0]));
//		}
//		return al;
//	}

	private String[] getMetaArray(Chart[] chart) {
		String[] meta = new String[chart.length];
		ArrayList<Chart> chartL = new ArrayList<Chart>();
		int k = 0;
		for (int i = 0; i < chart.length; i++) {
			if (chart[i].getMeta() != null) {
				meta[k] = chart[i].getMeta().replace(",", "_");
				chartL.add(chart[i]);
				if (debug)
					System.out.println("Transpose::getMetaArray:i " + i + " : " + " meta: " + meta[k] + " : "
							+ chart[i].getMeta());
				k++;
			} else
				System.out
						.println("Transpose::getMetaArray:i " + i + " k: " + k + " title: " + chart[i].getTitles()[1]);
		}
		transChart = new Chart[chartL.size()];
		if (chartL.size() > 0)
			for (int i = 0; i < transChart.length; i++)
				transChart[i] = chartL.get(i);
		return Arrays.copyOfRange(meta, 0, k);
	}

}
