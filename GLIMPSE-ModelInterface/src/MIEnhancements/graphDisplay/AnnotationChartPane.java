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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryAnnotation;
import org.jfree.chart.annotations.CategoryPointerAnnotation;
import org.jfree.chart.annotations.TextAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.plot.XYPlot;
//import org.jfree.chart.renderer.category.LineRenderer3D;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import chart.Chart;
import chart.ChartUtil;
import chart.DatasetUtil;
/**
 * The class to handle annotation add and remove. Annotation can be either on category 
 * or XY chart.
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	 TWU				created 					1/2/2016	
 */

public class AnnotationChartPane {

	private String[] rowKeys = null;
	private String[] columnKeys = null;
	private double[][] value = null;
	private JFreeChart jfchart;
	private Chart chart;
	private JDialog dialog = null;
	private JTable table;

	public AnnotationChartPane(Chart chart) {
		this.chart = chart;
		jfchart = chart.getChart();
		String action = "";
		String[] data = { "Add", "Remove" };
		action = (String) JOptionPane.showInputDialog(null, "Choose one", "Select an Action",
				JOptionPane.INFORMATION_MESSAGE, null, data, data[0]);

		if (action.equals("Add"))
			addChartAnnotation();
		else
			removeChartAnnotation();
	}

	private void removeChartAnnotation() {
		String[] name = null;
		String selected = null;
		TextAnnotation[] annotation = null;

		if (jfchart.getPlot().getPlotType().contains("Category")) {
			if (JOptionPane.showConfirmDialog(null, "choose one", "Select an Annotation?",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				int n = jfchart.getCategoryPlot().getAnnotations().size();
				annotation = new TextAnnotation[n];
				name = new String[n];

				Iterator<?> it = jfchart.getCategoryPlot().getAnnotations().iterator();

				int i = 0;
				while (it.hasNext()) {
					annotation[i] = (TextAnnotation) it.next();
					name[i] = annotation[i].getText();
					i++;
				}

				selected = (String) JOptionPane.showInputDialog(null, "Choose one", "Select an Annotation",
						JOptionPane.INFORMATION_MESSAGE, null, name, name[0]);
			}

			int idx = Arrays.asList(name).indexOf(selected);
			jfchart.getCategoryPlot().removeAnnotation((CategoryAnnotation) annotation[idx]);
		}
	}

	private void addChartAnnotation() {
		rowKeys = chart.getChartRow().split(",");
		columnKeys = chart.getChartColumn().split(",");
		if (jfchart.getPlot().getPlotType().contains("Category")) {
			DefaultCategoryDataset ds = (DefaultCategoryDataset) jfchart.getCategoryPlot().getDataset();
			value = new double[rowKeys.length][columnKeys.length];
			for (int i = 0; i < rowKeys.length; i++)
				for (int j = 0; j < columnKeys.length; j++)
					value[i][j] = (double) ds.getValue(i, j);
		} else if (jfchart.getPlot().getPlotType().contains("XY")) {
			XYPlot plot = jfchart.getXYPlot();
			value = DatasetUtil.getYValues(plot);
		}

		table = setAnnotationTable(rowKeys, columnKeys);
		table.setMaximumSize(new Dimension(1000, 600));
		table.setMinimumSize(new Dimension(400, 200));
		JScrollPane jsp = new JScrollPane(table);
		JPanel jp = new JPanel(new BorderLayout());
		jp.add(jsp, BorderLayout.CENTER);

		String[] options = { "Apply", "Save", "Done" };
		JButton jb;
		Box box = Box.createHorizontalBox();
		box.add(Box.createVerticalStrut(30));
		for (int i = 0; i < options.length; i++) {
			jb = crtJButton(options[i], i);
			box.add(jb);
		}
		box.add(Box.createVerticalStrut(30));
		jp.add(box, BorderLayout.SOUTH);

		dialog = new JDialog();
		dialog.setTitle("Input Annotation Text");
		dialog.setContentPane(jp);
		dialog.setSize(new Dimension(600, 200));
		dialog.setResizable(true);
		dialog.setVisible(true);
	}

	private JButton crtJButton(String name, int i) {
		JButton jb = new JButton(name);
		jb.setName(name);
		jb.setToolTipText(String.valueOf(i));
		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				JButton jb1 = (JButton) e.getSource();
				if (e.getClickCount() > 0)
					if (jb1.getName().equals("Apply")) {
						doApply();
						buildAnnotationText();
					} else if (jb1.getName().equals("Save"))
						JOptionPane.showMessageDialog(null, "Not implement yet", "Information",
								JOptionPane.INFORMATION_MESSAGE);
					else if (jb1.getName().equals("Done")) {
						dialog.dispose();
					}
			}
		};
		jb.addMouseListener(ml);
		return jb;
	}

	private void doApply() {
		double[] y = null;
		for (int i = 0; i < table.getRowCount(); i++) {
			try {
				if (jfchart.getPlot().getPlotType().contains("Category")) {
					if (jfchart.getCategoryPlot().getRenderer() instanceof StackedAreaRenderer
							|| jfchart.getCategoryPlot().getRenderer() instanceof StackedBarRenderer) {
						y = ChartUtil.getAnnotationTextTableLocation(value, i);
					}
					//no 3d charts in JFreechart 1.5
					//else {
					//	if (jfchart.getCategoryPlot().getRenderer() instanceof LineRenderer3D) {
					//		y = ChartUtil.getAnnotationTextLocation(value[i], true);
					//	} else
					//		y = ChartUtil.getAnnotationTextLocation(value[i], false);
					//}

					for (int j = 1; j < table.getColumnCount(); j++) {
						if (!table.getValueAt(i, j).equals("")) {
							CategoryPointerAnnotation categoryPointerAnnotation = ChartUtil.createAnnotation(
									(String) table.getValueAt(i, j), (String) columnKeys[j - 1], y[j - 1]);
							jfchart.getCategoryPlot().addAnnotation(categoryPointerAnnotation);
						}
					}
				} else {
					y = ChartUtil.getAnnotationTextLocation(value[i], false);
					for (int j = 1; j < table.getColumnCount(); j++) {
						if (!table.getValueAt(i, j).equals("")) {
							XYPointerAnnotation xyPointerAnnotation = ChartUtil
									.createAnnotation((String) table.getValueAt(i, j), 
									Double.valueOf(columnKeys[j - 1].trim()).doubleValue(), y[j - 1]);
							jfchart.getXYPlot().addAnnotation(xyPointerAnnotation);
						}
					}
				}
			} catch (java.lang.IllegalArgumentException e) {
				System.out.println("Apply Annotation Failed");
			}
		}

	}

	private JTable setAnnotationTable(String[] rowKeys, String[] columnKeys) {
		String[][] annotationText = new String[rowKeys.length][columnKeys.length + 1];
		String[] tableCol = new String[columnKeys.length + 1];
		tableCol[0] = "";

		for (int i = 0; i < annotationText.length; i++) {
			annotationText[i][0] = rowKeys[i];
			for (int j = 0; j < columnKeys.length; j++)
				annotationText[i][j + 1] = "";
		}
		for (int i = 0; i < columnKeys.length; i++) {
			tableCol[i + 1] = (String) columnKeys[i];
		}

		JTable table = new JTable(annotationText, tableCol);
		TableColumnModel cmodel = table.getColumnModel();
		cmodel.getColumn(0).setWidth(180);
		for (int i = 0; i < annotationText.length; i++) {
			table.setEditingRow(i);
			for (int j = 0; j < columnKeys.length; j++) {
				table.setEditingColumn(j + 1);
			}
		}
		return table;
	}

	public void buildAnnotationText() {
		String[][] annotationText = new String[table.getRowCount()][table.getColumnCount()];
		for (int i = 0; i < table.getRowCount(); i++)
			for (int j = 0; j < table.getColumnCount(); j++)
				annotationText[i][j] = (String) table.getValueAt(i, j);
		chart.setAnnotationText(annotationText);
	}

	public JFreeChart getJfchart() {
		return jfchart;
	}

}
