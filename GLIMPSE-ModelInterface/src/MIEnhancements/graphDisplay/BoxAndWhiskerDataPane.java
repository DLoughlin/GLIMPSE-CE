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
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import chart.DatasetUtil;
import chart.LegendUtil;
import listener.ListMouseListener;

/**
 * The class to handle statistics data. It can be displayed in subset of series 
 * or data range.
 * Referenced classes of package graphDisplay: DataPanel
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */


// Referenced classes of package graphDisplay:
//            DataPanel

public class BoxAndWhiskerDataPane extends DataPanel {
	private static final long serialVersionUID = 1L;
	protected String tableCol[] = { " ", "Mean", "Median", "Q1", "Q3", "Min",
			"Max", "MinOutlier", "MaxOutlier" };
	protected ArrayList<JList<String>> lists;

	public BoxAndWhiskerDataPane(JFreeChart chart)
			throws CloneNotSupportedException {
		super(chart);
		lists = new ArrayList<JList<String>>();
		setDataset();
		crtFootBox();
	}

	private void setDataset() {
		tableModel.setDataVector(dataValue, tableCol);
		SetColumnModel();
		table.updateUI();
		add(setStatisticsSelectionBox(), "North");
	}

	private void crtFootBox() {
		JButton jb = new JButton("Restore");
		jb.setAlignmentX(0.0F);
		jb.setFont(new Font("Verdana", 1, 11));
		jb.setName("Restore");
		jb.setPreferredSize(new Dimension(200, 20));
		java.awt.event.MouseListener bml = new MouseAdapter() {

			public void mousePressed(MouseEvent e1) {
				DefaultBoxAndWhiskerCategoryDataset dataset = (DefaultBoxAndWhiskerCategoryDataset) copyChart[id]
						.getCategoryPlot().getDataset();
				chart[id].getCategoryPlot().setDataset(dataset);
				int r[] = new int[dataset.getRowCount()];
				for (int i = 0; i < r.length; i++)
					r[i] = i;

				GraphDisplayUtil.showSelectRow(r, chart[id]);
				org.jfree.chart.LegendItemCollection lc = copyChart[id]
						.getCategoryPlot().getFixedLegendItems();
				chart[id].getCategoryPlot().setFixedLegendItems(
						LegendUtil.adjLenend(r, lc));
				ChartUtils.applyCurrentTheme(chart[id]);
				((JList<String>) lists.get(0)).clearSelection();
				((JList<String>) lists.get(1)).clearSelection();
				((JList<String>) lists.get(0)).updateUI();
				((JList<String>) lists.get(1)).updateUI();
				initTable(dataset);
				tableModel.setDataVector(dataValue, tableCol);
			}

		};
		jb.addMouseListener(bml);
		jb.setBorder(BorderFactory.createRaisedBevelBorder());
		Box box = Box.createHorizontalBox();
		box.add(new JLabel(" "));
		box.add(Box.createHorizontalStrut(getWidth()));
		box.add(jb, Float.valueOf(0.5F));
		box.setPreferredSize(new Dimension(700, 20));
		add(box, "South");
	}

	private void initTable(DefaultBoxAndWhiskerCategoryDataset dataset) {
		int loop1 = dataset.getColumnCount();
		int loop2 = dataset.getRowCount();
		int k = 0;
		dataValue = new String[(loop2 + 1) * loop1 - 1][tableCol.length];
		tableCol[0] = (String) dataset.getColumnKey(0);
		for (int i = 0; i < loop1; i++) {
			for (int j = 0; j < loop2; j++) {
				dataValue[k][0] = ((String) dataset.getRowKey(j)).trim();
				BoxAndWhiskerItem item = dataset.getItem(j, i);
				setTableValue(k, item);
				k++;
			}

			String temp[] = (String[]) tableCol.clone();
			if (i < loop1 - 1) {
				temp[0] = (String) dataset.getColumnKey(i + 1);
				dataValue[k] = temp;
				k++;
			}
		}

		tableModel.setDataVector(dataValue, tableCol);
	}

	private Box setStatisticsSelectionBox() {
		DefaultBoxAndWhiskerCategoryDataset dataset = (DefaultBoxAndWhiskerCategoryDataset) copyChart[id]
				.getCategoryPlot().getDataset();
		initTable(dataset);
		Box box = Box.createHorizontalBox();
		box.add(getStatisticsListBox("row",
				conversionUtil.DataConversion.object2String(dataset.getRowKeys().toArray())));
		box.add(Box.createHorizontalStrut(20));
		box.add(getStatisticsListBox("column",
				conversionUtil.DataConversion.object2String(dataset.getColumnKeys().toArray())));
		return box;
	}

	private Box getStatisticsListBox(String name, String objects[]) {
		Box box = Box.createVerticalBox();
		JLabel jl = new JLabel(name, 2);
		jl.setFont(new Font("Verdana", 1, 12));
		box.add(jl);
		box.add(Box.createHorizontalStrut(80));
		JList<String> list = getStatisticsList(name, objects);
		lists.add(list);
		JScrollPane jsp = new JScrollPane(list);
		jsp.setPreferredSize(new Dimension(300, 40));
		box.add(jsp);
		return box;
	}

	private JList<String> getStatisticsList(String name, String objects[]) {
		JList<String> list = new JList<String>(objects);
		list.setName(name);
		list.setFont(new Font("Verdana", 0, 10));
		list.setVisibleRowCount(3);
		list.setSelectionMode(0);
		ListSelectionListener lsl = new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				JList<?> l = (JList<?>) e.getSource();
				boolean adjust = e.getValueIsAdjusting();
				if (!adjust && !l.isSelectionEmpty())
					setStatisticsTable(l.getName(), l.getSelectedIndex());
			}

		};
		list.addListSelectionListener(lsl);
		list.addMouseListener(new ListMouseListener(list));
		return list;
	}

	void setStatisticsTable(String selected, int selectedItem) {
		DefaultBoxAndWhiskerCategoryDataset dataset = (DefaultBoxAndWhiskerCategoryDataset) copyChart[id]
				.getCategoryPlot().getDataset();
		dataValue = new String[dataset.getColumnCount()][tableCol.length];
		int selectedC[] = { selectedItem };
		if (selected.trim().equals("row")) {
			tableCol[0] = (String) dataset.getRowKey(selectedItem);
			dataValue = new String[dataset.getColumnCount()][tableCol.length];
			int loop = dataset.getColumnCount();
			for (int j = 0; j < loop; j++) {
				dataValue[j][0] = (String) dataset.getColumnKey(j);
				BoxAndWhiskerItem item = dataset.getItem(selectedItem, j);
				setTableValue(j, item);
			}

			((JList<String>) lists.get(1)).clearSelection();
			((JList<String>) lists.get(1)).updateUI();
			chart[id].getCategoryPlot().setDataset(dataset);
			GraphDisplayUtil.showSelectRow(selectedC, chart[id]);
			org.jfree.chart.LegendItemCollection lc = copyChart[id]
					.getCategoryPlot().getFixedLegendItems();
			chart[id].getCategoryPlot().setFixedLegendItems(
					LegendUtil.adjLenend(selectedC, lc));
		} else {
			tableCol[0] = (String) dataset.getColumnKey(selectedItem);
			dataValue = new String[dataset.getRowCount()][tableCol.length];
			int loop = dataset.getRowCount();
			for (int j = 0; j < loop; j++) {
				dataValue[j][0] = (String) dataset.getRowKey(j);
				BoxAndWhiskerItem item = dataset.getItem(j, selectedItem);
				setTableValue(j, item);
			}
			((JList<String>) lists.get(0)).clearSelection();
			((JList<String>) lists.get(0)).updateUI();
			chart[id].getCategoryPlot().setDataset(
					DatasetUtil.getSubsetColumnStaticsDataset(selectedC,
							copyChart[id]));
			GraphDisplayUtil.showSelectColumn(chart[id]);
			chart[id].getCategoryPlot().setFixedLegendItems(
					copyChart[id].getCategoryPlot().getFixedLegendItems());
		}
		ChartUtils.applyCurrentTheme(chart[id]);
		tableModel.setDataVector(dataValue, tableCol);
	}

	private void setTableValue(int j, BoxAndWhiskerItem item) {
		dataValue[j][1] = String.valueOf(item.getMean());
		dataValue[j][2] = String.valueOf(item.getMedian());
		dataValue[j][3] = String.valueOf(item.getQ1());
		dataValue[j][4] = String.valueOf(item.getQ3());
		dataValue[j][5] = String.valueOf(item.getMinRegularValue());
		dataValue[j][6] = String.valueOf(item.getMaxRegularValue());
		dataValue[j][7] = String.valueOf(item.getMinOutlier());
		dataValue[j][8] = String.valueOf(item.getMinOutlier());
	}

}
