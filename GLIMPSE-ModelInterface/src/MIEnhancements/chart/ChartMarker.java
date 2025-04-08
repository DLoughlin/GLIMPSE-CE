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
package chart;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;

import listener.ListMouseListener;

/**
 * The class handle to create Markers for JFreeChart. 
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class ChartMarker {
	private final String[] pos = { "Bottom", "Bottom-Left", "Bottom-Right", "Center", "Left", "Right", "Top",
			"Top-Left", "Top-Right" };
	private final String[] textPos = { "Baseline", "Baseline-Left", "Baseline-Right", "Bottom", "Bottom-Left",
			"Bottom-Right", "Half-Ascent-Center", "Half-Ascent-Left", "Half-Ascent-Right", "Center", "Center-Left",
			"Center-Right", "Top-Center", "Top-Left", "Top-Right" };
	private final String markerType[] = { "X-Axis Marker", "Y-Axis Marker", "Interval Marker", "Edit Marker",
			"Remove Marker" };
	private final String valueOpt[] = { "", "Max", "Min", "Averge" };
	private String selectedMarkerType;
	private Paint selectedMarkerColor = null;
	private double selectedMarkerValue = -99999;
	private Chart chart;
	private JFreeChart jfchart;
	private Map<String, Marker> markerMap;
	private JDialog dialog; // Parent dialog
	private JDialog dialog1;

	public ChartMarker(Chart chart, JDialog dialog) {
		this.dialog = dialog;
		this.chart = chart;
		this.jfchart = chart.getChart();
		this.markerMap = chart.getMarkerMap();
		selectMarkerType();
	}

	// Marker Type
	protected void selectMarkerType() {
		JList<String> list = new JList<String>(markerType);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionListener listener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				JList<?> list = (JList<?>) e.getSource();
				boolean adjust = e.getValueIsAdjusting();
				if (!adjust) {
					selectedMarkerType = (String) list.getSelectedValue();
				}
			}
		};
		list.addListSelectionListener(listener);
		list.addMouseListener(new ListMouseListener(list));
		list.setSelectedIndex(0);
		JScrollPane jsp = new JScrollPane(list);
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

		dialog1 = new JDialog(dialog);
		dialog1.setTitle("Perform Marker Operation");
		dialog1.setContentPane(jp);
		dialog1.setSize(new Dimension(200, 160));
		dialog1.setResizable(true);
		dialog1.setVisible(true);
	}

	private JButton crtJButton(String name, int i) {
		JButton jb = new JButton(name);
		jb.setName(name);
		jb.setToolTipText(String.valueOf(i));
		MouseListener ml = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JButton jb1 = (JButton) e.getSource();
				if (e.getClickCount() > 0)
					if (jb1.getName().equals("Apply")) {
						doApply();
						chart.setMarkerMap(markerMap);
					} else if (jb1.getName().equals("Save"))
						JOptionPane.showMessageDialog(null, "Not implement yet", "Information",
								JOptionPane.INFORMATION_MESSAGE);
					else if (jb1.getName().equals("Done")) {
						dialog1.dispose();
					}
			}
		};
		jb.addMouseListener(ml);
		return jb;
	}

	private void doApply() {
		if (selectedMarkerType != null) {
			if (selectedMarkerType.equals(markerType[0]))
				createCategoryMarker();
			else if (selectedMarkerType.equals(markerType[1]))
				createValueMarker();
			else if (selectedMarkerType.equals(markerType[2]))
				createIntervalMarker();
			else if (selectedMarkerType.equals(markerType[3]))
				editMarker();
			else if (selectedMarkerType.equals(markerType[4]))
				removeMarker();
		}
	}

	private void createCategoryMarker() {
		String selectedCategory = selectCategoryList();
		Object o = null;
		if (selectedCategory != null)
			o = selectMarkerColor();

		if (o != null && o.equals("Ok") && selectedMarkerColor != null) {
			CategoryMarker categoryMarker = null;
			try {
				if (jfchart.getPlot().getPlotType().equalsIgnoreCase("Category Plot")) {
					categoryMarker = new CategoryMarker(selectedCategory, selectedMarkerColor, new BasicStroke(2.0F));
					int optionSelected = getMarkerLabel(categoryMarker);
					if (optionSelected == 0) {
						categoryMarker.setDrawAsLine(true);
						categoryMarker.setLabelAnchor(RectangleAnchor.CENTER);
						categoryMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
						jfchart.getCategoryPlot().addDomainMarker(categoryMarker);
					}
					markerMap.put(selectedMarkerType + "_" + categoryMarker.getLabel(), categoryMarker);
				} else if (jfchart.getPlot().getPlotType().equalsIgnoreCase("XY Plot")) {
					ValueMarker valueMarker = new ValueMarker(
							Double.valueOf(selectedCategory.substring(0, selectedCategory.lastIndexOf('.'))),
							selectedMarkerColor, new BasicStroke(2.0F));
					int optionSelected = getMarkerLabel(valueMarker);
					if (optionSelected == 0) {
						valueMarker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
						valueMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
						jfchart.getXYPlot().addDomainMarker(valueMarker);
					}
					markerMap.put(selectedMarkerType + "_" + valueMarker.getLabel(), valueMarker);
				}
			} catch (java.lang.IllegalArgumentException e) {
				System.out.println("Apply Chart Marker Failed");
			}
		}
	}

	private void createIntervalMarker() {
		Object o = null;
		o = selectMarkerColor();

		if (o != null && o.equals("Ok") && selectedMarkerColor != null)
			o = selectMarkerValue("Input a Start value");
		else
			return;

		double start = -99999;
		if (o != null && o.equals("Ok")) {
			start = selectedMarkerValue;
			if (Double.valueOf(start) != -99999)
				selectMarkerValue("Input a End value");
			else {
				JOptionPane.showMessageDialog(null, "Invalid data Inputted", "Information",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		double end = -99999;
		if (o != null && o.equals("Ok")) {
			end = selectedMarkerValue;
			if (Double.valueOf(start) != -99999) {
				IntervalMarker intervalMarker = new IntervalMarker(start, end, selectedMarkerColor,
						new BasicStroke(1.0F), selectedMarkerColor, new BasicStroke(1.0F), 0.5f);
				int optionSelected = getMarkerLabel(intervalMarker);
				if (optionSelected == 0) {
					intervalMarker.setLabelAnchor(RectangleAnchor.CENTER);
					intervalMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
					if (jfchart.getPlot().getPlotType().contains("Category"))
						jfchart.getCategoryPlot().addRangeMarker(intervalMarker);
					else if (jfchart.getPlot().getPlotType().contains("XY"))
						jfchart.getXYPlot().addRangeMarker(intervalMarker);
				}
				markerMap.put(selectedMarkerType + "_" + intervalMarker.getLabel(), intervalMarker);
			} else {
				JOptionPane.showMessageDialog(null, "Invalid data Inputted", "Information",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	private void createValueMarker() {
		Object o = null;
		o = selectMarkerColor();

		if (o != null && o.equals("Ok") && selectedMarkerColor != null)
			o = selectMarkerValue("Input a Start value");
		else
			return;

		if (o != null && o.equals("Ok")) {
			if (Double.valueOf(selectedMarkerValue) != -99999) {
				ValueMarker valueMarker = new ValueMarker(selectedMarkerValue, selectedMarkerColor,
						new BasicStroke(2.0F));
				int optionSelected = getMarkerLabel(valueMarker);
				if (optionSelected == 0) {
					valueMarker.setLabelAnchor(RectangleAnchor.CENTER);
					valueMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
					if (jfchart.getPlot().getPlotType().contains("Category"))
						jfchart.getCategoryPlot().addRangeMarker(valueMarker);
					else if (jfchart.getPlot().getPlotType().contains("XY"))
						jfchart.getXYPlot().addRangeMarker(valueMarker);
				}
				markerMap.put(selectedMarkerType + "_" + valueMarker.getLabel(), valueMarker);
			} else
				JOptionPane.showMessageDialog(null, "Invalid data Inputted", "Information",
						JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void editMarker() {
		if (markerMap == null) {
			JOptionPane.showMessageDialog(null, "No Marker to Edit", "Information", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		String[] keys = markerMap.keySet().toArray(new String[0]);
		String selectedMarker = (String) JOptionPane.showInputDialog(null, "Choose one", "Select a Category",
				JOptionPane.INFORMATION_MESSAGE, null, keys, keys[0]);
		Marker m = markerMap.get(selectedMarker);

		if (JOptionPane.showConfirmDialog(null, "choose one", "Modify Label?",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			getMarkerLabel(m);

		if (JOptionPane.showConfirmDialog(null, "choose one", "Modify Label Position?",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			getMarkerLabelPosition(m);

		if (JOptionPane.showConfirmDialog(null, "choose one", "Modify Text Label Position?",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			getMarkerTextLabelPosition(m);
	}

	private void removeMarker() {
		if (markerMap == null) {
			JOptionPane.showMessageDialog(null, "No Marker to Remove", "Information", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		String[] keys = markerMap.keySet().toArray(new String[0]);
		String selectedMarker = (String) JOptionPane.showInputDialog(null, "Choose one", "Select a Category",
				JOptionPane.INFORMATION_MESSAGE, null, keys, keys[0]);
		Marker m = markerMap.get(selectedMarker);
		if (jfchart.getPlot().getPlotType().equalsIgnoreCase("Category Plot")) {
			if (selectedMarker.contains("X-Axis"))
				jfchart.getCategoryPlot().removeDomainMarker(m);
			else if (selectedMarker.contains("Y-Axis"))
				jfchart.getCategoryPlot().removeRangeMarker(m);
			else if (selectedMarker.contains("Interval"))
				jfchart.getCategoryPlot().removeRangeMarker(m);
		} else if (jfchart.getPlot().getPlotType().equalsIgnoreCase("XY Plot")) {
			if (selectedMarker.contains("X-Axis"))
				jfchart.getXYPlot().removeDomainMarker(m);
			else if (selectedMarker.contains("Y-Axis"))
				jfchart.getXYPlot().removeRangeMarker(m);
			else if (selectedMarker.contains("Interval"))
				jfchart.getXYPlot().removeRangeMarker(m);
		}
		markerMap.remove(selectedMarker);
	}

	// Category list
	protected String selectCategoryList() {
		List<?> l = null;
		if (jfchart.getPlot().getPlotType().equalsIgnoreCase("Category Plot")) {
			CategoryPlot plot = jfchart.getCategoryPlot();
			l = plot.getDataset().getColumnKeys();
		} else if (jfchart.getPlot().getPlotType().equalsIgnoreCase("XY Plot")) {
			XYPlot plot = jfchart.getXYPlot();
			String[] x = new String[plot.getDataset().getItemCount(0)];
			for (int i = 0; i < plot.getDataset().getItemCount(0); i++) {
				x[i] = String.valueOf(plot.getDataset().getX(0, i));
			}
			l = Arrays.asList(x);
		}
		String[] data = conversionUtil.ArrayConversion.list2Array(l);
		return (String) JOptionPane.showInputDialog(null, "Choose one", "Select a Category, Then Click OK",
				JOptionPane.INFORMATION_MESSAGE, null, data, data[0]);
	}

	protected Object selectMarkerColor() {
		final JColorChooser tcc = new JColorChooser();
		ChangeListener cl = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				selectedMarkerColor = tcc.getColor();
			}
		};
		tcc.getSelectionModel().addChangeListener(cl);
		tcc.setBorder(BorderFactory.createTitledBorder("Choose Marker Color"));

		Object[] options = { "Ok", "Cancel" };
		JOptionPane pane = new JOptionPane(tcc, -1, 0, null, options, options[0]);

		JDialog dialog = pane.createDialog("Select a Marker Paint, Then Click OK");
		dialog.setLayout(null);
		dialog.setResizable(true);
		dialog.setVisible(true);
		return pane.getValue();
	}

	protected Object selectMarkerValue(String name) {
		JComboBox<String> valueList = new JComboBox<String>(valueOpt);
		valueList.setEditable(true);
		valueList.setSelectedIndex(0);
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<?> cb = (JComboBox<?>) e.getSource();
				int selected = cb.getSelectedIndex();
				String s = (String) cb.getSelectedItem();
				if (selected == -1) {
					if (s.trim().equals("") || Double.valueOf(s.trim()).isNaN()) {
						JOptionPane.showMessageDialog(null, "Invalid data Inputted", "Information",
								JOptionPane.INFORMATION_MESSAGE);
					} else
						selectedMarkerValue = Double.valueOf(s).doubleValue();
				} else
					selectedMarkerValue = Double.valueOf(getTheValue(selected)).doubleValue();
			}
		};
		valueList.addActionListener(al);
		Object[] options = { "Ok", "Cancel" };
		JOptionPane pane = new JOptionPane(valueList, -1, 0, null, options, options[0]);
		JDialog dialog = pane.createDialog(name);
		dialog.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						valueList.requestFocusInWindow();
					}
				});
			}
		});
		dialog.setFocusable(true);
		dialog.setLayout(null);
		dialog.setResizable(true);
		dialog.setVisible(true);
		return pane.getValue();
	}

	private int getTheValue(int idx) {
		int value = 0;
		switch (idx) {
		case 1:
			if (jfchart.getPlot().getPlotType().equalsIgnoreCase("Category Plot"))
				value = (int) jfchart.getCategoryPlot().getRangeAxis().getUpperBound();
			else if (jfchart.getPlot().getPlotType().equalsIgnoreCase("XY Plot"))
				value = (int) jfchart.getXYPlot().getRangeAxis().getUpperBound();
			break;
		case 2:
			if (jfchart.getPlot().getPlotType().equalsIgnoreCase("Category Plot"))
				value = (int) jfchart.getCategoryPlot().getRangeAxis().getLowerBound();
			else if (jfchart.getPlot().getPlotType().equalsIgnoreCase("XY Plot"))
				value = (int) jfchart.getXYPlot().getRangeAxis().getLowerBound();
			value = (int) jfchart.getCategoryPlot().getRangeAxis().getLowerBound();
			break;
		case 3:
			if (jfchart.getPlot().getPlotType().equalsIgnoreCase("Category Plot"))
				value = ((int) jfchart.getCategoryPlot().getRangeAxis().getUpperBound()
						- (int) jfchart.getCategoryPlot().getRangeAxis().getLowerBound()) / 2;
			else if (jfchart.getPlot().getPlotType().equalsIgnoreCase("XY Plot"))
				value = ((int) jfchart.getXYPlot().getRangeAxis().getUpperBound()
						- (int) jfchart.getXYPlot().getRangeAxis().getLowerBound()) / 2;
			break;
		}
		return value;

	}

	protected int getMarkerLabel(final Marker m) {
		JOptionPane pane = new JOptionPane("Please Enter Label String", JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);
		pane.setWantsInput(true);
		pane.setInitialSelectionValue("");
		JDialog dialog = pane.createDialog("Input Marker Label");
		dialog.setVisible(true);
		String markerLabel = ((String) pane.getInputValue()).trim();

		if ((int) pane.getValue() == JOptionPane.CANCEL_OPTION)
			return JOptionPane.CANCEL_OPTION;
		else if ((int) pane.getValue() == JOptionPane.OK_OPTION) {
			if (markerLabel.equals("")) {
				JOptionPane.showMessageDialog(null, "No Label Inputted", "Information",
						JOptionPane.INFORMATION_MESSAGE);
				return JOptionPane.CANCEL_OPTION;
			} else {
				m.setLabel(markerLabel);
				m.setLabelFont(new Font("Verdana", 1, 12));
				return JOptionPane.OK_OPTION;
			}
		} else
			return JOptionPane.CANCEL_OPTION;
	}

	public Map<String, Marker> getMarkerMap() {
		return markerMap;
	}

	protected void getMarkerLabelPosition(final Marker m) {
		String selectedPos = (String) JOptionPane.showInputDialog(null, "Choose one", "Select a Category",
				JOptionPane.INFORMATION_MESSAGE, null, pos, pos[0]);
		m.setLabelOffset(new RectangleInsets(0, 16, 0, 16));
		m.setLabelAnchor(MarkerUtil.getMarkerLabelPosition(selectedPos));
	}

	protected void getMarkerTextLabelPosition(final Marker m) {
		String selectedTextPos = (String) JOptionPane.showInputDialog(null, "Choose one", "Select a Category",
				JOptionPane.INFORMATION_MESSAGE, null, textPos, textPos[0]);
		m.setLabelTextAnchor(MarkerUtil.getMarkerTextLabelPosition(selectedTextPos));
	}

	public JFreeChart getJfchart() {
		return jfchart;
	}

}
