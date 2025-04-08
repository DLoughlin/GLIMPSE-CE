package graphDisplay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.flow.FlowPlot;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.flow.DefaultFlowDataset;
import org.jfree.data.flow.FlowDatasetUtils;
import org.jfree.data.flow.NodeKey;

import ModelInterface.InterfaceMain;
import ModelInterface.ModelGUI2.DbViewer;

import chart.Chart;
import chart.LegendUtil;
import chartOptions.FileUtil;

import filter.FilteredTable;
import mapOptions.MapOptionsUtil;

/**
 * The class to display a Sankey Diagram by constructing a flow dataset from
 * user selected input from a query result table
 * 
 * Author Action Date Flag
 * =======================================================================
 * Yadong created September/03/2024
 */

public class SankeyDiagramFromTable extends JFrame implements ComponentListener {
	private static final long serialVersionUID = 1L;
	private String chartName;
	private JTable jtable;
	private JFrame frame;
	private JToolBar toolBar;
	private JPanel scenarioMenuPanel;
	private JPanel regionMenuPanel;
	private JPanel yearMenuPanel;
	private JPanel sankeyPanel;
	private JPanel sankeyLabelPanel;
	private JPanel barChartPanel;
	private JPanel summaryPanel;
	private JButton nextYearButton;
	private JButton prevYearButton;
	private CategoryPlot barPlot;
	private LegendItemCollection barLegendItems = null;
	private String bundlePath = null;
	private StackedBarRenderer barRenderer;
	private FlowPlot myPlot;
	private Set<NodeKey> mySet;

	private JLabel scenarioListLabel;
	private JLabel regionListLabel;
	private JLabel listLabel;
	private JComboBox<String> scenarioListMenu;
	private JComboBox<String> regionListMenu;
	private JComboBox<String> yearListMenu;
	// private int[] barFillColor;
	private double defaultNodeWidth = 200.0;
	private double defaultNodeMargin = 0.02;
	private boolean replaceWithBarChart = false;
	protected boolean debug = false;

	public SankeyDiagramFromTable(String chartName, JTable jtable) throws ClassNotFoundException {
		this.chartName = chartName;
		this.jtable = jtable;
		initialize();

	}

	private void initialize() {
		frame = new JFrame("Sankey for " + chartName);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		//bundlePath = System.getProperty("user.dir") + "\\config\\LegendBundle.properties";
		bundlePath = InterfaceMain.legendBundlesLoc;
		int regionIdx = FilteredTable.getColumnByName(jtable, "region");
		ArrayList<String> yearList = FilteredTable.getYearListFromTableData(jtable);
		int firstYearIdx = FilteredTable.getColumnByName(jtable, yearList.get(0));
		// check if we need to replace Sankey with StackedBarChart if only one column
		// available
		// between "region" and the columns of query results
		if (firstYearIdx - regionIdx == 2) {
			replaceWithBarChart = true;
		}
		frame.getContentPane().add(createToolBar(), BorderLayout.WEST);
		if (replaceWithBarChart) {
			frame.getContentPane().add(createStackedBarPlot(), BorderLayout.CENTER);
		} else {
			frame.getContentPane().add(createSankeyPlot(), BorderLayout.CENTER);
		}
		frame.getContentPane().add(createSummary(), BorderLayout.EAST);
		frame.validate();
		frame.pack();
		Dimension preferredD = new Dimension(1200, 800);
		frame.setSize(preferredD);
		frame.setMinimumSize(new Dimension(500, 300));
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		DbViewer.openWindows.add(frame);
		// frame.setAlwaysOnTop(true);
	}

	protected JComponent createToolBar() {
		// create a toolBar on the left
		toolBar = new JToolBar();
		toolBar.setBackground(Color.LIGHT_GRAY);
		toolBar.setBorder(new EmptyBorder(5, 5, 5, 5));
		toolBar.setLayout(new GridLayout(10, 1));
		toolBar.setFloatable(false);
		// add scenario dropdown menu inside the JToolBar
		scenarioMenuPanel = new JPanel();
		scenarioMenuPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		scenarioMenuPanel.setLayout(new BoxLayout(scenarioMenuPanel, BoxLayout.Y_AXIS));
		scenarioMenuPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		scenarioListLabel = new JLabel("Scenario:", SwingConstants.LEFT);
		scenarioListLabel.setFont(new Font("Arial", Font.BOLD, 16));
		scenarioListLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		List<String> scenarioListFromTable = MapOptionsUtil.getScenarioListFromTableData(jtable);
		// System.out.println("SankeyDiagramFromTable::this is the first
		// scenario:"+scenarioListFromTable.get(0));
		DefaultComboBoxModel<String> dmlScenario = new DefaultComboBoxModel<String>();
		for (int i = 0; i < scenarioListFromTable.size(); i++) {
			dmlScenario.addElement(scenarioListFromTable.get(i));
		}
		scenarioListMenu = new JComboBox<String>();
		scenarioListMenu.setModel(dmlScenario);
		scenarioListMenu.setVisible(true);
		scenarioListMenu.setFont(new Font("Arial", Font.BOLD, 14));
		scenarioListMenu.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		scenarioListMenu.setMaximumSize(new Dimension(300, 25));
		scenarioListMenu.addActionListener(new UpdateSankeyOrBarChart());
		scenarioMenuPanel.add(scenarioListLabel);
		scenarioMenuPanel.add(scenarioListMenu);
		toolBar.add(scenarioMenuPanel);
		// add region dropdown menu inside the JToolBar
		regionMenuPanel = new JPanel();
		regionMenuPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		regionMenuPanel.setLayout(new BoxLayout(regionMenuPanel, BoxLayout.X_AXIS));
		regionMenuPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		regionListLabel = new JLabel("Region:", SwingConstants.LEFT);
		regionListLabel.setFont(new Font("Arial", Font.BOLD, 16));
		regionListLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		List<String> regionListFromTable = MapOptionsUtil.getUniqueRegionsInTable(jtable);
		// System.out.println("SankeyDiagramFromTable::this is the first
		// region:"+regionListFromTable.get(0));
		DefaultComboBoxModel<String> dmlRegion = new DefaultComboBoxModel<String>();
		for (int i = 0; i < regionListFromTable.size(); i++) {
			dmlRegion.addElement(regionListFromTable.get(i));
		}
		regionListMenu = new JComboBox<String>();
		regionListMenu.setModel(dmlRegion);
		regionListMenu.setVisible(true);
		regionListMenu.setFont(new Font("Arial", Font.BOLD, 14));
		regionListMenu.setMaximumSize(new Dimension(100, 25));
		regionListMenu.addActionListener(new UpdateSankeyOrBarChart());
		regionMenuPanel.add(regionListLabel);
		regionMenuPanel.add(regionListMenu);
		toolBar.add(regionMenuPanel);
		// add year dropdown menu inside the JToolBar
		yearMenuPanel = new JPanel();
		yearMenuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		yearMenuPanel.setLayout(new BoxLayout(yearMenuPanel, BoxLayout.X_AXIS));
		yearMenuPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		listLabel = new JLabel("Year:", SwingConstants.LEFT);
		listLabel.setFont(new Font("Arial", Font.BOLD, 16));
		listLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		ArrayList<String> yearListFromTable = MapOptionsUtil.getYearListFromTableData(jtable);
		DefaultComboBoxModel<String> dml = new DefaultComboBoxModel<String>();
		for (int i = 0; i < yearListFromTable.size(); i++) {
			dml.addElement(yearListFromTable.get(i));
		}
		yearListMenu = new JComboBox<String>();
		yearListMenu.setModel(dml);
		yearListMenu.setVisible(true);
		// yearListMenu.setMaximumSize(yearListMenu.getPreferredSize());
		yearListMenu.setFont(new Font("Arial", Font.BOLD, 14));
		yearListMenu.setMaximumSize(new Dimension(150, 25));
		yearListMenu.addActionListener(new UpdateSankeyOrBarChart());

		nextYearButton = new JButton(">");
		nextYearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int y = yearListMenu.getSelectedIndex();
				if (y < yearListMenu.getModel().getSize() - 1) {
					yearListMenu.setSelectedIndex(y + 1);
				}
			}
		});
		nextYearButton.setVisible(true);
		prevYearButton = new JButton("<");
		prevYearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int y = yearListMenu.getSelectedIndex();
				if (y > 0) {
					yearListMenu.setSelectedIndex(y - 1);
				}
			}
		});
		prevYearButton.setVisible(true);

		yearMenuPanel.add(listLabel);
		yearMenuPanel.add(prevYearButton);
		yearMenuPanel.add(yearListMenu);
		yearMenuPanel.add(nextYearButton);

		toolBar.add(yearMenuPanel);

		toolBar.add(yearMenuPanel);
		return toolBar;
	};

	protected JComponent createSankeyPlot() {

		String selectedScenario = (String) scenarioListMenu.getSelectedItem();
		String selectedRegion = (String) regionListMenu.getSelectedItem();
		String selectedYear = (String) yearListMenu.getSelectedItem();
		DefaultFlowDataset myDataset = createFlowDatasetFromTable(jtable, selectedScenario, selectedRegion,
				selectedYear);
		mySet = myDataset.getAllNodes();

		sankeyPanel = new JPanel();
		sankeyPanel.setLayout(new BoxLayout(sankeyPanel, BoxLayout.Y_AXIS));
		sankeyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		sankeyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		Iterator<NodeKey> nodeIterator = mySet.iterator();

		myPlot = new FlowPlot(myDataset);
		HashMap<String, Color> myColors = new HashMap();
		while (nodeIterator.hasNext()) {
			NodeKey myKey = nodeIterator.next();
			// next few lines create internally consistent colors
			// Color myColor=null;
			// if(!myColors.containsKey(myKey.getNode().toString())) {
			// Random r=new Random();
			// myColor=new Color(r.nextInt(200)+50,r.nextInt(200)+50,r.nextInt(200)+50);
			// myColors.put(myKey.getNode().toString(), myColor);
			// }

			// myColor=myColors.get(myKey.getNode().toString());
			// myPlot.setNodeFillColor(myKey, myColor);

			// System.out.println("Check each of the NodeKey: "+myKey.toString());
			// System.out.println("Check getNode of the NodeKey: "+myKey.getNode());
			/*
			 * if (myKey.getNode().toString().equalsIgnoreCase("Electricity")) {
			 * myPlot.setNodeFillColor(myKey, Color.ORANGE); double inFlowE =
			 * FlowDatasetUtils.calculateInflow(myDataset, myKey.getNode(),1);
			 * System.out.println("Check inflow for electricity: "+inFlowE); }else if
			 * (myKey.getNode().toString().equalsIgnoreCase("Gas")) {
			 * myPlot.setNodeFillColor(myKey, Color.BLUE); }else if
			 * (myKey.getNode().toString().equalsIgnoreCase("Biomass")) {
			 * myPlot.setNodeFillColor(myKey, Color.GREEN); }
			 */
		}
		myPlot.setNodeLabelOffsetX(-170.0);
		myPlot.setNodeLabelOffsetY(-170.0);
		myPlot.setNodeWidth(defaultNodeWidth);
		myPlot.setNodeMargin(defaultNodeMargin);
		myPlot.setDefaultNodeLabelFont(new Font("Arial", Font.BOLD, 16));
		myPlot.setOutlineVisible(true);

		setFlowPlotColorFromBundle();

		String chartTitle = chartName + " for " + selectedRegion + " in " + selectedYear;
		JFreeChart chart = new JFreeChart(chartTitle, myPlot);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.validate();
		// chartPanel.setBorder(BorderFactory.createLineBorder(Color.blue,5));
		chartPanel.setPreferredSize(new Dimension(800, 600));
		sankeyPanel.add(chartPanel, BorderLayout.CENTER);

		sankeyLabelPanel = new JPanel();
		sankeyLabelPanel.setLayout(new BoxLayout(sankeyLabelPanel, BoxLayout.X_AXIS));
		sankeyLabelPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		sankeyLabelPanel.setMaximumSize(new Dimension(10000, 100));
		sankeyLabelPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		// Node is displayed from left to right
		// get the first column name right after "region"
		int regionIdx = FilteredTable.getColumnByName(jtable, "region");
		ArrayList<String> yearList = FilteredTable.getYearListFromTableData(jtable);
		int firstYearIdx = FilteredTable.getColumnByName(jtable, yearList.get(0));
		int totalNodes = firstYearIdx - regionIdx - 1;
		// String firstNode = jtable.getColumnName(firstYearIdx-1);
		// JTextField nodeFromColumn = new JTextField(firstNode);
		// nodeFromColumn.setFont(new Font("Arial",Font.BOLD,16));
		int gapWidth = (int) Math.round(4 * myPlot.getNodeWidth() / totalNodes);
		if (totalNodes == 3) {
			gapWidth = (int) Math.round(2 * myPlot.getNodeWidth() / totalNodes);
		} else if (totalNodes >= 4) {
			gapWidth = (int) Math.round(myPlot.getNodeWidth() / totalNodes);
		}
		// nodeFromColumn.setSize(new Dimension((int)myPlot.getNodeWidth()/2,100));
		// nodeFromColumn.setHorizontalAlignment(JTextField.CENTER);
		// nodeFromColumn.setBackground(Color.GRAY);
		// sankeyLabelPanel.add(nodeFromColumn);
		for (int i = 0; i < totalNodes; i++) {
			String nextNode = jtable.getColumnName(firstYearIdx - i - 1);
			JTextField nextNodeFromColumn = new JTextField(nextNode);
			nextNodeFromColumn.setFont(new Font("Arial", Font.BOLD, 16));
			nextNodeFromColumn.setSize(new Dimension((int) myPlot.getNodeWidth() / 2, 100));
			nextNodeFromColumn.setHorizontalAlignment(JTextField.CENTER);
			nextNodeFromColumn.setBackground(Color.GRAY);
			sankeyLabelPanel.add(nextNodeFromColumn);
			// sankeyLabelPanel.add(Box.createRigidArea(new Dimension(gapWidth,0)));

		}

		sankeyPanel.add(sankeyLabelPanel);
		return sankeyPanel;
	}

	protected JComponent createStackedBarPlot() {

		barChartPanel = new JPanel();
		barChartPanel.setLayout(new BoxLayout(barChartPanel, BoxLayout.Y_AXIS));
		barChartPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		barChartPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		String selectedScenario = (String) scenarioListMenu.getSelectedItem();
		String selectedRegion = (String) regionListMenu.getSelectedItem();
		String selectedYear = (String) yearListMenu.getSelectedItem();
		int unitColIdx = FilteredTable.getColumnByName(jtable, "Units");
		String unitForYAxis = (String) jtable.getValueAt(0, unitColIdx);
		DefaultCategoryDataset myDataset = createCategoryDatasetFromTable(jtable, selectedScenario, selectedRegion,
				selectedYear);
		JFreeChart barChart = ChartFactory.createStackedBarChart("", "", unitForYAxis, myDataset,
				PlotOrientation.VERTICAL, true, true, false);
		LegendTitle legend = barChart.getLegend();
		legend.setItemFont(new Font("Arial", Font.BOLD, 14));

		barPlot = (CategoryPlot) barChart.getPlot();
		barPlot.setDataset(0, myDataset);
		barRenderer = (StackedBarRenderer) barPlot.getRenderer();
		barRenderer.setMaximumBarWidth(0.2);
		barRenderer.setDefaultItemLabelFont(new Font("Arial", Font.BOLD, 16));
		barRenderer.setSeriesItemLabelFont(0, new Font("Arial", Font.BOLD, 16));
		// remove the shining white stripe from the stackedBarChart
		barRenderer.setBarPainter(new StandardBarPainter());

		if (bundlePath != null) {
			setLegendColorFromBundle();
		} else {
			barPlot.setRenderer(0, barRenderer);
		}

		ChartPanel chartPanel = new ChartPanel(barChart);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.validate();
		chartPanel.setPreferredSize(new Dimension(800, 600));
		barChartPanel.add(chartPanel, BorderLayout.CENTER);
		return barChartPanel;
	}

	// YD edits, Jan-2025
	private void setLegendColorFromBundle() {

		Object[] temp = getLegendInfoFromProperties(bundlePath);
		if (temp.length == 0)
			return;
		String queryNameForChart = (String) "\"" + chartName + "\"";
		barLegendItems = barPlot.getLegendItems();
		String[] legends = new String[barLegendItems.getItemCount()];

		if (barLegendItems != null) {
			for (int i = 0; i < barLegendItems.getItemCount(); i++) {
				LegendItem item = barLegendItems.get(i);
				legends[i] = item.getLabel();
				// System.out.println("before setlegendColorFromBundle:"+
				// item.getLabel()+":"+item.getFillPaint());
			}
		}
		// read in saved legend items/colors and query names in bundle file
		String[] tempStr = new String[temp.length];
		String[] queryStr = new String[temp.length];
		tempStr = readLegendItemsFromProperties();
		queryStr = readQueryInfoFromProperties();
		// Prioritize to use colors for a specific query name
		// then search through those global ones
		for (int i = 0; i < legends.length; i++) {
			// System.out.println("SankeyDiagramFromTable::setlegendColorFromBundle:check
			// this legend:"+legends[i]);
			int idx = Arrays.asList(tempStr).indexOf(legends[i].trim());
			// System.out.println("SankeyDiagramFromTable::setlegendColorFromBundle:first
			// idx is:"+idx);
			// check if this legend item only occur once in the legendBundle file
			int idx_last = Arrays.asList(tempStr).lastIndexOf(legends[i].trim());
			// System.out.println("SankeyDiagramFromTable::setlegendColorFromBundle:last idx
			// is:"+idx_last);
			String[] o = new String[4];
			if (idx > -1 & idx == idx_last) {
				String queryNameInLine = Arrays.asList(queryStr).get(idx);
				if (queryNameInLine.equals(queryNameForChart)) {
					//System.out.println(
					//		"SankeyDiagramFromTable::setlegendColorFromBundle:check if this item only occur once:"
					//				+ idx_last);
					o = ((String) temp[idx]).split("=")[1].split(",");
					int barFillColor = Integer.valueOf(o[0].trim());
					Color myColor = LegendUtil.getRGB(barFillColor);
					barRenderer.setSeriesPaint(i, myColor);
					if (debug)
						System.out
								.println("SankeyDiagramFromTable::setlegendColorFromBundle:use this color:" + myColor);
				} else if (queryNameInLine.equals("*")) {
					o = ((String) temp[idx]).split("=")[1].split(",");
					int barFillColor = Integer.valueOf(o[0].trim());
					Color myColor = LegendUtil.getRGB(barFillColor);
					barRenderer.setSeriesPaint(i, myColor);
				}
			} else if (idx > -1 & idx != idx_last) {

				for (int idxN = idx; idxN <= idx_last; idxN++) {
					String queryNameAtIdx = Arrays.asList(queryStr).get(idxN);
					String legendInFile = Arrays.asList(tempStr).get(idxN);
					if (queryNameAtIdx.equals(queryNameForChart) & legendInFile.equals(legends[i].trim())) {
						//System.out.println(
						//		"SankeyDiagramFromTable::setlegendColorFromBundle:when the item occur more than once:"
						//				+ queryNameAtIdx + ":" + legendInFile);
						o = ((String) temp[idxN]).split("=")[1].split(",");
						int barFillColor = Integer.valueOf(o[0].trim());
						Color myColor = LegendUtil.getRGB(barFillColor);
						barRenderer.setSeriesPaint(i, myColor);
					} else if (queryNameAtIdx.equals("*") & legendInFile.equals(legends[i].trim())) {
						// the global line contains the legend item
						o = ((String) temp[idxN]).split("=")[1].split(",");
						int barFillColor = Integer.valueOf(o[0].trim());
						Color myColor = LegendUtil.getRGB(barFillColor);
						barRenderer.setSeriesPaint(i, myColor);
					}
				}
			}
		}
		barPlot.setRenderer(0, barRenderer);
	}

	// YD edits, Feb-2025
	private void setFlowPlotColorFromBundle() {
		Object[] temp = getLegendInfoFromProperties(bundlePath);
		if (temp.length == 0)
			return;
		// read in saved colors in bundle file
		String queryNameForChart = (String) "\"" + chartName + "\"";
		String[] tempStr = new String[temp.length];
		String[] queryStr = new String[temp.length];
		tempStr = readLegendItemsFromProperties();
		queryStr = readQueryInfoFromProperties();
		Iterator<NodeKey> nodeIterator = mySet.iterator();
		while (nodeIterator.hasNext()) {
			NodeKey myKey = nodeIterator.next();
			// System.out.println("SankeyDiagramFromTable::setFlowPlotColorFromBundle:check
			// this Nodekey :" + myKey.getNode().toString());
			int idx = Arrays.asList(tempStr).indexOf(myKey.getNode().toString());
			int idx_last = Arrays.asList(tempStr).lastIndexOf(myKey.getNode().toString());
			String[] o = new String[4];
			if (idx > -1 & idx == idx_last) {
				if (queryStr == null) {
					return;
				}
				String queryNameInLine = Arrays.asList(queryStr).get(idx);
				if (queryNameInLine == null) {
					return;
				}
				if (queryNameInLine.equals(queryNameForChart)) {
					o = ((String) temp[idx]).split("=")[1].split(",");
					Color useThisColor = LegendUtil.getRGB(Integer.valueOf(o[0].trim()));
					// YD edits, Feb-2025
					myPlot.setNodeFillColor(myKey, useThisColor);
					// System.out.println("SankeyDiagramFromTable::setFlowPlotColorFromBundle:set
					// this color:"+useThisColor+" for this key:"+myKey.toString());
					if (debug)
						System.out.println(
								"SankeyDiagramFromTable::setFlowPlotColorFromBundle:use this color:" + useThisColor);
				} else if (queryNameInLine.equals("*")) {
					o = ((String) temp[idx]).split("=")[1].split(",");
					Color useThisColor = LegendUtil.getRGB(Integer.valueOf(o[0].trim()));
					myPlot.setNodeFillColor(myKey, useThisColor);
				} // the condition of a legend item only occur once finished
			} else if (idx > -1 & idx != idx_last) {
				for (int idxN = idx; idxN <= idx_last; idxN++) {
					if (queryStr == null) {
						return;
					}
					String queryNameAtIdx = Arrays.asList(queryStr).get(idxN);
					if (tempStr == null) {
						return;
					}
					String legendInFile = Arrays.asList(tempStr).get(idxN);
					if (legendInFile == null || queryNameAtIdx == null) {
						return;
					}
					if (queryNameAtIdx.equals(queryNameForChart) & legendInFile.equals(myKey.getNode().toString())) {
						System.out.println(
								"SankeyDiagramFromTable::setFlowPlotColorFromBundle:when the item occur more than once:"
										+ queryNameAtIdx + ":" + legendInFile);
						o = ((String) temp[idxN]).split("=")[1].split(",");
						Color useThisColor = LegendUtil.getRGB(Integer.valueOf(o[0].trim()));
						myPlot.setNodeFillColor(myKey, useThisColor);
					} else if (queryNameAtIdx.equals("*") & legendInFile.equals(myKey.getNode().toString())) {
						// the global line contains the legend item
						o = ((String) temp[idxN]).split("=")[1].split(",");
						Color useThisColor = LegendUtil.getRGB(Integer.valueOf(o[0].trim()));
						myPlot.setNodeFillColor(myKey, useThisColor);
					}
				} // for loop end
			}
		}
	}

	// YD edits, Feb-2025
	private String[] readLegendItemsFromProperties() {
		Object[] temp = getLegendInfoFromProperties(bundlePath);
		String[] tempStr = new String[temp.length];

		// System.out.println("SankeyDiagramFromTable::readLegendItemsFromProperties:" +
		// queryNameForChart);
		for (int i = 0; i < temp.length; i++) {
			String myLine = (String) temp[i];
			if (myLine.contains(":") & !myLine.contains("*")) {
				String secondPart = ((String) temp[i]).split(":")[1].trim();
				tempStr[i] = ((String) secondPart).split("=")[0].trim();
				if (debug)
					System.out.println(
							"SankeyDiagramFromTable::readLegendItemsFromProperties:a local line and queryName in File & Chart matched!!!");
			} else {
				tempStr[i] = ((String) temp[i]).split("=")[0].trim();
				String firstPart = tempStr[i];
				if (firstPart.startsWith("*:")) {
					tempStr[i] = firstPart.replace("*:", "");
				}
				//else {
				//	  //this is for backwards compatibility with original format
				//	  tempStr[i] = myLine;
				 // }
			}
		}
		return tempStr;
	}

	private String[] readQueryInfoFromProperties() {
		Object[] temp = getLegendInfoFromProperties(bundlePath);
		String[] queryStr = new String[temp.length];
		for (int i = 0; i < temp.length; i++) {
			String myLine = (String) temp[i];
			if (myLine.contains(":") & !myLine.contains("*")) {

				String queryNameInFile = ((String) temp[i]).split(":")[0].trim();
				queryStr[i] = queryNameInFile;

			} else {
				String firstPart = ((String) temp[i]).split("=")[0].trim();

				if (firstPart.startsWith("*:")) {
					queryStr[i] = "*";
				} else {
					// this  the case of the older formatted files:
					// global: a oil=-43691,-43691,0,0, in this case, just use global form "*:
					queryStr[i] = "*";
					// this is will only be needed until all old color files are replaced.

				}
			}
		}
		return queryStr;
	}

	// YD edits,Jan-2025
	private Object[] getLegendInfoFromProperties(String path) {
		LineNumberReader lineReader = null;
		Object[] temp = null;
		try {
			DataInputStream dis = FileUtil.initInFile(path);
			lineReader = new LineNumberReader(new InputStreamReader(dis));
			temp = lineReader.lines().toArray();
			lineReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not read legend info (FNF): " + e.toString());
		} catch (IOException e) {
			System.out.println("Could not read legend info (IO): " + e.toString());
		}
		return temp;
	}

	protected JComponent createSummary() {
		// add information display field at the bottom
		summaryPanel = new JPanel();
		summaryPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.X_AXIS));
		summaryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		summaryPanel.add(Box.createHorizontalGlue());
		summaryPanel.add(Box.createHorizontalGlue());
		return summaryPanel;
	}

	public class UpdateSankeyOrBarChart extends JPanel implements ActionListener {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (replaceWithBarChart) {
				RedrawBarChart();
			} else {
				RedrawSankeyPlot();
			}

		}
	}

	public void RedrawSankeyPlot() {
		// clear up the previous displayed panels
		frame.remove(sankeyPanel);
		frame.getContentPane().add(createSankeyPlot(), BorderLayout.CENTER);
		frame.revalidate();
		frame.repaint();
	}

	public void RedrawBarChart() {
		// clear up the previous displayed panels
		frame.remove(barChartPanel);
		frame.getContentPane().add(createStackedBarPlot(), BorderLayout.CENTER);
		frame.revalidate();
		frame.repaint();
	}

	private DefaultFlowDataset createFlowDatasetFromTable(JTable jtable, String scenarioStr, String regionStr,
			String yearStr) {

		// remove those rows not matching scenarioStr and regionStr from jtable first
		int regionIdx = FilteredTable.getColumnByName(jtable, "region");
		int scenarioIdx = FilteredTable.getColumnByName(jtable, "scenario");
		ArrayList<String> yearList = FilteredTable.getYearListFromTableData(jtable);
		int firstYearIdx = FilteredTable.getColumnByName(jtable, yearList.get(0));
		int yearIdx = FilteredTable.getColumnByName(jtable, yearStr);
		// int nodeN = firstYearIdx-regionIdx-1;
		DefaultFlowDataset dataset = new DefaultFlowDataset();
		int curStage = 0;
		for (int row = 0; row < jtable.getRowCount(); row++) {
			curStage = 0;
			// check if the scenario and region match the selected
			boolean scenario2Keep = ((String) jtable.getValueAt(row, scenarioIdx)).equals(scenarioStr);
			boolean region2Keep = ((String) jtable.getValueAt(row, regionIdx)).equals(regionStr);
			if (scenario2Keep && region2Keep) {
				// for (int j = regionIdx+1; j < firstYearIdx-1; j++) {
				for (int j = firstYearIdx - 1; j > regionIdx + 1; j--) {
					String fromSource = (String) jtable.getValueAt(row, j);
					String toDes = (String) jtable.getValueAt(row, j - 1);
					double flowRate = Double.parseDouble((String) jtable.getValueAt(row, yearIdx));
					// System.out.println("SankeyDiagramFromTable::at this node number:
					// "+(j-regionIdx));
					// System.out.println("SankeyDiagramFromTable::from this source: "+fromSource);
					// System.out.println("SankeyDiagramFromTable::to this destination: "+toDes);
					if (flowRate != 0) {
						dataset.setFlow(curStage, fromSource, toDes, flowRate);
					}
					curStage = curStage + 1;
				}
			}
		}
		return dataset;
	}

	private DefaultCategoryDataset createCategoryDatasetFromTable(JTable jtable, String scenarioStr, String regionStr,
			String yearStr) {
		int yearIdx = FilteredTable.getColumnByName(jtable, yearStr);
		int scenarioIdx = FilteredTable.getColumnByName(jtable, "scenario");
		int regionIdx = FilteredTable.getColumnByName(jtable, "region");
		String colName = jtable.getColumnName(regionIdx + 1);
		DefaultCategoryDataset myDataset = new DefaultCategoryDataset();
		// look into "CategoryStackedBarChart.java" ;
		for (int row = 0; row < jtable.getRowCount(); row++) {
			// check if the scenario and region match the selected
			boolean scenario2Keep = ((String) jtable.getValueAt(row, scenarioIdx)).equals(scenarioStr);
			boolean region2Keep = ((String) jtable.getValueAt(row, regionIdx)).equals(regionStr);
			if (scenario2Keep && region2Keep) {
				String myStr = (String) jtable.getValueAt(row, regionIdx + 1);
				double myNum = Double.parseDouble((String) jtable.getValueAt(row, yearIdx));
				myDataset.addValue(myNum, myStr, colName);
			}
		}
		return myDataset;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub

	}
}
