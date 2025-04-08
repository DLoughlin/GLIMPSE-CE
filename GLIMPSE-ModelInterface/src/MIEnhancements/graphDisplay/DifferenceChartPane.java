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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import chart.Chart;
import chart.DatasetUtil;
import chart.MyChartFactory;
import chart.MyDataset;

/**
 * The class to handle the difference of two chart in display a chart panel by
 * minus one chart data from another chart dataset and missing attributes will
 * be assumed to zero.
 * 
 * Author Action Date Flag
 * ======================================================================= TWU
 * created 1/2/2016
 */

public class DifferenceChartPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private Chart charts[];
	private Chart chart;
	private int selectedIndex;
	private int selected[] = { -1, -1 };
	private List<String> rowList = new LinkedList<String>();
	private String[] legend = null;
	private int[] color = null;
	private int[] pColor = null;
	private int[] pattern = null;
	private int[] lineStrokes = null;
	
	boolean shouldStop=false;

	public DifferenceChartPane(Chart charts[]) throws ClassNotFoundException {
		selectedIndex = -1;
		this.charts = charts.clone();
		init();
		if(shouldStop) {
			return;
		}
		createDifferenceChart();
	}

	private void init() {
		setLayout(new BorderLayout());
		JList jc = difference();
		Object options[] = { "Ok", "Cancel" };
		boolean madeValidSelection=false;
		
		while(!madeValidSelection) {
			JOptionPane pane0 = new JOptionPane(new JScrollPane(jc), -1, 0, null, options, options[0]);
			JDialog dialog = pane0.createDialog("Select first chart to compare");
			dialog.setPreferredSize(new Dimension(600, 200));
			dialog.setLayout(null);
			dialog.setResizable(true);
			dialog.setVisible(true);
			if(pane0.getValue()==null || pane0.getValue().toString().compareToIgnoreCase("cancel")==0) {
				//user closed window with no selection or hit cancel
				shouldStop=true;
				return;
			}
			
			//now see if value is good
			if (selectedIndex != -1) {
				selected[0] = selectedIndex;
				dialog.dispose();
				selectedIndex = -1;
				madeValidSelection=true;
				dialog.dispose();
				
			}else {
				JOptionPane.showMessageDialog(null,"Please make a selection before pressing OK, or cancel to exit.","Additional Selection Required",JOptionPane.ERROR_MESSAGE);
				
			}
		}
		
		jc.clearSelection();
		madeValidSelection=false;
		
		while(!madeValidSelection) {
			JOptionPane pane0 = new JOptionPane(new JScrollPane(jc), -1, 0, null, options, options[0]);
			JDialog dialog = pane0.createDialog("Select second chart to compare");
			dialog.setPreferredSize(new Dimension(600, 200));
			dialog.setLayout(null);
			dialog.setResizable(true);
			dialog.setVisible(true);
			if(pane0.getValue()==null || pane0.getValue().toString().compareToIgnoreCase("cancel")==0) {
				//user closed window with no selection or hit cancel
				shouldStop=true;
				return;
			}
			
			//now see if value is good
			if (selectedIndex != -1) {
				selected[1] = selectedIndex;
				dialog.dispose();
				//selectedIndex = -1;
				madeValidSelection=true;
				dialog.dispose();
				
			}else {
				JOptionPane.showMessageDialog(null,"Please make a selection before pressing OK, or cancel to exit.","Additional Selection Required",JOptionPane.ERROR_MESSAGE);
				
			}
		}
		
	/*	dialog = pane0.createDialog("Select second chart to compare");
		dialog.setPreferredSize(new Dimension(780, 200));
		dialog.setLayout(null);
		dialog.setResizable(true);
		dialog.setVisible(true);
		if (temp != -1) {
			selected[1] = temp;
			dialog.dispose();
			temp = -1;
		}*/
	}

	@SuppressWarnings("unchecked")
	private void createDifferenceChart() {
		String[][] data = null;
		
		// error message here
		if(shouldStop || selected[0]==-1 || selected[1]==-1) {
			JOptionPane.showMessageDialog(null,"Both dialog windows must have a selection for comparison tool to work","Additional Selections Required",JOptionPane.ERROR_MESSAGE);
			return;
		}

		String id = charts[selected[0]].getMeta() + "|" + charts[selected[0]].getMetaCol() + ","
				+ charts[selected[1]].getMeta() + "|" + charts[selected[1]].getMetaCol();

		if (selected[0] != -1 && selected[1] != -1) {
			try {
				if (charts[selected[0]].getChart().getPlot() instanceof CategoryPlot) {
					DefaultCategoryDataset ds0 = (DefaultCategoryDataset) charts[selected[0]].getChart()
							.getCategoryPlot().getDataset();
					DefaultCategoryDataset ds1 = (DefaultCategoryDataset) charts[selected[1]].getChart()
							.getCategoryPlot().getDataset();

					for (int i = 0; i < ds0.getRowCount(); i++)
						rowList.add(((String) ds0.getRowKey(i)).trim());

					for (int i = 0; i < ds1.getRowCount(); i++)
						if (!rowList.contains(((String) ds1.getRowKey(i))))
							rowList.add(((String) ds1.getRowKey(i)).trim());

					String[] ks = rowList.toArray(new String[0]);
					List<String> l1 = ds0.getRowKeys();
					List<String> l2 = ds1.getRowKeys();
					fillLegends(ks, l1.toArray(new String[0]), l2.toArray(new String[0]));

					data = DatasetUtil.getDiffData(charts[selected[0]].getChart().getCategoryPlot().getDataset(),
							charts[selected[1]].getChart().getCategoryPlot().getDataset(), rowList);

					DefaultCategoryDataset dataset = new MyDataset().createCategoryDataset(data, ks,
							charts[selected[0]].getChartColumn().split(","));
					
					String title=id;
					String subtitle[]=getsubTitle();
					for (int i=0;i<subtitle.length;i++) {
						if (subtitle[i]!=null) {
						subtitle[i]=subtitle[i].replaceAll("\nregion:","");
						}
					}
					
					chart = MyChartFactory.createChart(charts[selected[0]].getChartClassName(),
							charts[selected[0]].getPath(),
							"Difference: "
									+ charts[selected[0]].getGraphName(),
							title, subtitle, charts[selected[0]].getAxis_name_unit(),
							Arrays.toString(legend).replace("[", "").replace("]", ""), color, pColor, pattern,
							lineStrokes, null, dataset, charts[selected[0]].getRelativeColIndex(),
							charts[selected[0]].isShowLineAndShape(),"");
					chart.setUnitsLookup(charts[selected[0]].getUnitsLookup());
				} 
//				else if (charts[selected[0]].getChart().getPlot() instanceof XYPlot) {
//					DefaultXYDataset ds0 = (DefaultXYDataset) charts[selected[0]].getChart().getXYPlot().getDataset();
//					DefaultXYDataset ds1 = (DefaultXYDataset) charts[selected[1]].getChart().getXYPlot().getDataset();
//
//					for (int i = 0; i < ds0.getSeriesCount(); i++)
//						rowList.add(((String) ds0.getSeriesKey(i)).trim());
//					for (int i = 0; i < ds1.getSeriesCount(); i++)
//						if (!rowList.contains(((String) ds1.getSeriesKey(i))))
//						rowList.add(((String) ds1.getSeriesKey(i)).trim());
//
//					String[] ks = rowList.toArray(new String[0]);
//					String[] l1 = DatasetUtil.getChartRows(ds0);
//					String[] l2 = DatasetUtil.getChartRows(ds1);
//					fillLegends(ks, l1, l2);
//					
//					data = DatasetUtil.getDiffData(charts[selected[0]].getChart().getXYPlot().getDataset(),
//							charts[selected[1]].getChart().getXYPlot().getDataset(), ks);
//
//					DefaultXYDataset dataset = (DefaultXYDataset) new MyDataset().createXYDefaultDataset(data, ks,
//							charts[selected[0]].getChartColumn().split(","));
//					
//					chart = MyChartFactory.createChart(charts[selected[0]].getChartClassName(), charts[0].getPath(),
//							"Chart" + selected[0] + "-" + "Chart" + selected[1] + "_"
//									+ charts[selected[0]].getGraphName(),
//							id, getsubTitle(), charts[selected[0]].getAxis_name_unit(),
//							Arrays.toString(legend).replace("[", "").replace("]", ""), color, pColor, pattern,
//							lineStrokes, null, dataset, charts[selected[0]].getRelativeColIndex(),
//							charts[selected[0]].isShowLineAndShape());
//				}
				
				for (int j = 0; j < chart.getChart().getSubtitleCount()
						&& !(chart.getChart().getSubtitle(j) instanceof org.jfree.chart.title.LegendTitle); j++) {
					((TextTitle) chart.getChart().getSubtitle(j)).setFont(new Font("Arial", 1, 12));
					chart.getChart().getSubtitle(j).setVisible(true);
				}
			} catch (ClassNotFoundException e) {
				System.out.println("Experiencing ClassNotFoundException in creating DifferencePlot!");
			}
		}

	}
	
	private void fillLegends(String[] ks, String[] l1, String[] l2) {
		legend = new String[ks.length];
		color = new int[ks.length];
		pColor = new int[ks.length];
		pattern = new int[ks.length];
		lineStrokes = new int[ks.length];
		
		int idx = 0;
		int i1 = 0;
		
		for (int i = 0; i < ks.length; i++) {// l1.size(); i++) {
			if (Arrays.asList(l1).contains(ks[i]) && i1 < l1.length) {
				int j = Arrays.asList(l1).indexOf(ks[i].trim());
				legend[idx] = charts[selected[0]].getLegend().split(",")[j];// [map.get(ks[i])];
				color[idx] = charts[selected[0]].getColor()[j];// [map.get(ks[i])];
				pColor[idx] = charts[selected[0]].getpColor()[j];// [map.get(ks[i])];
				pattern[idx] = charts[selected[0]].getPattern()[j];// [map.get(ks[i])];
				lineStrokes[idx] = charts[selected[0]].getLineStrokes()[j];// [map.get(ks[i])];
				i1++;
				idx++;
			} else if (Arrays.asList(l2).contains(ks[i]) && i1 < ks.length) {
				int j = Arrays.asList(l2).indexOf(ks[i]);
				legend[idx] = charts[selected[1]].getLegend().split(",")[j];// [map.get(ks[i])];
				color[idx] = charts[selected[1]].getColor()[j];// [map.get(ks[i])];
				pColor[idx] = charts[selected[1]].getpColor()[j];// [map.get(ks[i])];
				pattern[idx] = charts[selected[1]].getPattern()[j];// [map.get(ks[i])];
				lineStrokes[idx] = charts[selected[1]].getLineStrokes()[j];// [map.get(ks[i])];
				idx++;
			} else
				System.out.println("error: " + ks[i]);
		}	
	}
	
	private JList<Object> difference() {
		JList<Object> list = GraphDisplayUtil.metaList(charts);
		list.setSelectionMode(0);
		ListSelectionListener listener = new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				JList<?> list = (JList<?>) e.getSource();
				boolean adjust = e.getValueIsAdjusting();
				selectedIndex=-1;
				if (!adjust && list.getSelectedValue()!=null)
					selectedIndex = Integer.valueOf(((String) list.getSelectedValue()).split(",")[1]);// .getSelectedIndex();
			}
		};
		list.addListSelectionListener(listener);
		return list;
	}

	private String[] getsubTitle() {
		String[] st = new String[charts[selected[0]].getTitles().length];
		for (int i = 1; i < charts[selected[0]].getTitles().length; i++) {
			st[i] = charts[selected[0]].getTitles()[i] + " - " + charts[selected[1]].getTitles()[i];// .split(":")[0];
		}
		return st;
	}

	public Chart getChart() {
		return chart;
	}

}