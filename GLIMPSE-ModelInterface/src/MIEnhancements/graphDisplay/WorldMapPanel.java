package graphDisplay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;

import org.geotools.swing.JMapPane;
import org.geotools.swing.tool.PanTool;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Filter;
import ModelInterface.InterfaceMain;
import ModelInterface.ModelGUI2.DbViewer;
import filter.FilteredTable;
import mapOptions.LegendPanel;
import mapOptions.MapColor;
import mapOptions.MapColorPalette;
import mapOptions.MapOptionsUtil;

public class WorldMapPanel extends JFrame implements ComponentListener {
	private boolean debug = false;
	private String chartName;
	private boolean statesIncluded;
	private JFrame frame;
	private JMapPane jmap;
	private MapContent stateMap;
	private JToolBar toolBar;
	private JPanel scenarioMenuPanel;
	private JPanel yearMenuPanel;
	private JPanel colorSchemePanel;
	private JPanel colorChoicePanel;
	private JPanel changeNumberPanel;
	private JPanel refreshMapPanel;
	private JPanel reverseColorPanel;
	private JPanel colorConfigPanel;
	private JPanel exportMapPanel;
	private ButtonGroup choiceGroup;
	private int numColorChoice;
	private int numColorClass;
	private String paletteChoice;
	private JPanel addMapPanel;
	private JPanel addLegendPanel;
	private JPanel sectorDisplayPanel;
	HashMap<String,String> unitLookup=null;
	private JTable jtable;
	private JComboBox<String> scenarioListMenu;
	private	JComboBox<String> yearListMenu;
	private JButton nextYearButton;
	private JButton prevYearButton;
	private JLabel scenarioListLabel;
	private	JLabel listLabel;
	private JLabel legendLabel;
	private JTextField sectorText;
	private JFormattedTextField minField;
	private JFormattedTextField maxField;
	private double previousMin;
	private double previousMax;
	private double[] minMaxFromTable = new double[2];
	private MapColorPalette usePalette;
	private MapColor useMapColor;
	private JComboBox comboBoxPalette;
	private JComboBox comboBoxNumClasses;
	private static final String[] paletteType = {"SEQUENTIAL", "DIVERGING"};
	private static final Integer[] numClasses = {1,2,3,4,5,6,7,8,9,10,11};
	private IntervalType intervalType = IntervalType.AUTOMATIC; 
	private boolean reverseColors;
	private boolean normalizeScale;
	public enum IntervalType {
		CUSTOM, AUTOMATIC 
	}

	public WorldMapPanel(String chartName, JTable jtable, boolean statesIncluded) {
		this.chartName = chartName;
		this.jtable = jtable;
		this.statesIncluded = statesIncluded;
		initialize();
	}

	protected void initialize() {
		//initiate a JFrame
		frame = new JFrame("Map for "+chartName);
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    frame.setLayout(new BorderLayout());
		frame.getContentPane().add(createToolBar(),BorderLayout.WEST);
		//get min and max from the jtable, these values will be used to generate mapColor and color legend
		normalizeScale = true;
		boolean noRowSelected = jtable.getSelectionModel().isSelectionEmpty();
		if (noRowSelected) {
			minMaxFromTable = MapOptionsUtil.getAbsMinMaxFromTableColumn(jtable,(String)yearListMenu.getSelectedItem(),normalizeScale);
		}else {

			minMaxFromTable = getAbsMinMaxForAllYears(normalizeScale);
			}
		reverseColors = false;
		//usePalette = MapColorPalette.getusePalette();
		usePalette = MapColorPalette.getMapColorPalette("DIVERGING",4,10,reverseColors);

		useMapColor = new MapColor(usePalette,minMaxFromTable[0],minMaxFromTable[1]);	
		frame.getContentPane().add(createWorldMapContent(),BorderLayout.CENTER);
		frame.getContentPane().add(createFooter(),BorderLayout.PAGE_END);
		frame.getContentPane().add(addLegendPanel(),BorderLayout.EAST);
		frame.pack();
		//YD edits,August-2024,to set the size for map window
		Dimension preferredD = new Dimension(1200,800);
		frame.setSize(preferredD);
		frame.setMinimumSize(new Dimension(500,300));
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
			// YD edits, August-2024
			//System.out.println("inside initialize componentResized listener now... ");
				RedrawWorldMapLayout();		
			}
		});
		DbViewer.openWindows.add(frame);
		//YD Nov-2024
		//jtable.getSelectionModel().addListSelectionListener(new RowSelectionListener());
	}
	
	protected JComponent createToolBar() {
		//create a toolBar on the left
		toolBar = new JToolBar();
		toolBar.setBackground(Color.LIGHT_GRAY);
		toolBar.setBorder(new EmptyBorder(5,5,5,5));
		toolBar.setPreferredSize(new Dimension(330,800));
		toolBar.setLayout(new GridLayout(9,1));
		toolBar.setFloatable(false);
		//YD edits,August-2024,add scenario dropdown menu inside the JToolBar 
		scenarioMenuPanel = new JPanel();
		scenarioMenuPanel.setBorder(new EmptyBorder(5,5,5,5));
		scenarioMenuPanel.setLayout(new BoxLayout(scenarioMenuPanel,BoxLayout.Y_AXIS));
		scenarioMenuPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		scenarioListLabel = new JLabel("Scenario:",SwingConstants.CENTER);
		scenarioListLabel.setFont(new Font("Arial",Font.PLAIN,16));
		scenarioListLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		List<String> scenarioListFromTable = MapOptionsUtil.getScenarioListFromTableData(jtable);
		//System.out.println("WorldMapPanel::this is the first scenario:"+scenarioListFromTable.get(0));
		DefaultComboBoxModel<String> dmlScenario = new DefaultComboBoxModel<String>();
			for (int i=0;i< scenarioListFromTable.size();i++) {
				dmlScenario.addElement(scenarioListFromTable.get(i));	
			}
		scenarioListMenu = new JComboBox<String>();
		scenarioListMenu.setModel(dmlScenario);
		scenarioListMenu.setVisible(true);
		scenarioListMenu.setFont(new Font("Arial",Font.PLAIN,14));
		scenarioListMenu.setMaximumSize(new Dimension(400,25));
		scenarioListMenu.addActionListener(new UpdateMap());
		scenarioMenuPanel.add(scenarioListLabel);
		scenarioMenuPanel.add(scenarioListMenu);
		toolBar.add(scenarioMenuPanel);
		//add year dropdown menu inside the JToolBar 
		yearMenuPanel = new JPanel();
		yearMenuPanel.setBorder(new EmptyBorder(5,5,5,5));
		yearMenuPanel.setLayout(new BoxLayout(yearMenuPanel,BoxLayout.X_AXIS));
		yearMenuPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		listLabel = new JLabel("Year:",SwingConstants.LEFT);
		listLabel.setFont(new Font("Arial",Font.PLAIN,16));
		listLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		ArrayList<String> yearListFromTable = MapOptionsUtil.getYearListFromTableData(jtable);
		//System.out.println("WorldMapPanel::this is the first year column name:"+yearListFromTable.get(0));
		DefaultComboBoxModel<String> dml = new DefaultComboBoxModel<String>();
			for (int i=0;i< yearListFromTable.size();i++) {
				dml.addElement(yearListFromTable.get(i));	
			}
		//selects last item
		//dml.setSelectedItem(yearListFromTable.get(yearListFromTable.size()-1));
		//selects first item
		dml.setSelectedItem(yearListFromTable.get(0));
		yearListMenu = new JComboBox<String>();
		yearListMenu.setModel(dml);
		yearListMenu.setVisible(true);
		//yearListMenu.setMaximumSize(yearListMenu.getPreferredSize());
		yearListMenu.setFont(new Font("Arial",Font.PLAIN,14));
		yearListMenu.setMaximumSize(new Dimension(75,25));
		yearListMenu.addActionListener(new UpdateMap());

		nextYearButton = new JButton(">");
		nextYearButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int y=yearListMenu.getSelectedIndex();
				if (y<yearListMenu.getModel().getSize()-1) {
					yearListMenu.setSelectedIndex(y+1);
				}
		}
		});
		nextYearButton.setVisible(true);
		prevYearButton = new JButton("<");
		prevYearButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
					int y=yearListMenu.getSelectedIndex();
					if (y>0) {
						yearListMenu.setSelectedIndex(y-1);
					}
			}
		});
		prevYearButton.setVisible(true);
		
		yearMenuPanel.add(listLabel);
		yearMenuPanel.add(prevYearButton);
		yearMenuPanel.add(yearListMenu);
		yearMenuPanel.add(nextYearButton);		
		
		toolBar.add(yearMenuPanel);		
		//add color configuration panel inside the JToolBar
		JLabel selectColorLabel = new JLabel("Palette type:",SwingConstants.LEFT);
		selectColorLabel.setFont(new Font("Arial",Font.PLAIN,16));
		colorChoicePanel = new JPanel();
		colorChoicePanel.setBorder(new EmptyBorder(1,1,1,1));
		colorChoicePanel.setLayout(new BoxLayout(colorChoicePanel,BoxLayout.X_AXIS));
		colorChoicePanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		//selectColorLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		colorSchemePanel = new JPanel();
		colorSchemePanel.setBorder(new EmptyBorder(1,1,1,1));
		colorSchemePanel.setLayout(new BoxLayout(colorSchemePanel,BoxLayout.X_AXIS));
		colorSchemePanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		
		comboBoxPalette = new JComboBox<String>(paletteType);
		comboBoxPalette.setFont(new Font("Arial",Font.PLAIN,14));
		comboBoxPalette.setMaximumSize(new Dimension(110,25));
		comboBoxPalette.setSelectedIndex(1);
		colorSchemePanel.add(selectColorLabel);
		colorSchemePanel.add(comboBoxPalette);	
		toolBar.add(colorSchemePanel);
		comboBoxPalette.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				limitNumClassesAndChangeChoices();
				RedrawWorldMap();
			}
		});
		
		//add color choices for each palette type
		addDivergingColorChoices();
        toolBar.add(colorChoicePanel);
		
		//add number of color classes panel inside the JToolBar
		JLabel changeNumberLabel = new JLabel("Number of color classes:",SwingConstants.LEFT);
		changeNumberLabel.setFont(new Font("Arial",Font.PLAIN,16));
		//changeNumberLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		changeNumberPanel = new JPanel();
		changeNumberPanel.setBorder(new EmptyBorder(5,5,5,5));
		changeNumberPanel.setLayout(new BoxLayout(changeNumberPanel,BoxLayout.X_AXIS));
		changeNumberPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		changeNumberPanel.add(changeNumberLabel);
		comboBoxNumClasses = new JComboBox<Integer>(numClasses);
		comboBoxNumClasses.setFont(new Font("Arial",Font.PLAIN,14));
		comboBoxNumClasses.setMaximumSize(new Dimension(50,25));
		comboBoxNumClasses.setAlignmentX(RIGHT_ALIGNMENT);
		comboBoxNumClasses.setSelectedIndex(chooseNumCombos()-1);
		comboBoxNumClasses.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				RedrawWorldMap();
			}
		});
		
		changeNumberPanel.add(comboBoxNumClasses);
		toolBar.add(changeNumberPanel);
		
		//add reverse color button inside the JToolBar
		reverseColorPanel = new JPanel();
		reverseColorPanel.setBorder(new EmptyBorder(5,5,5,5));
		reverseColorPanel.setLayout(new BoxLayout(reverseColorPanel,BoxLayout.X_AXIS));
		reverseColorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		JButton reverseBtn = new JButton("Reverse Colors");
		reverseBtn.setFont(new Font("Arial",Font.PLAIN,16));
		reverseBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				reverseColors = !reverseColors;
				RedrawWorldMap();
				
			}
		});
		reverseColorPanel.add(reverseBtn);
		toolBar.add(reverseColorPanel);
		
		//add refresh button inside the JToolBar
		refreshMapPanel = new JPanel();
		refreshMapPanel.setBorder(new EmptyBorder(5,5,5,5));
		refreshMapPanel.setLayout(new BoxLayout(refreshMapPanel,BoxLayout.X_AXIS));
		refreshMapPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		JButton refreshBtn = new JButton("Refresh Map");
		refreshBtn.setFont(new Font("Arial",Font.PLAIN,16));
		refreshBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				RedrawWorldMap();
			}
		});
		refreshMapPanel.add(refreshBtn);
		//toolBar.add(refreshMapPanel);
		
		//add color configuration button inside the JToolBar
		colorConfigPanel = new JPanel();
		colorConfigPanel.setBorder(new EmptyBorder(5,5,5,5));
		colorConfigPanel.setLayout(new BoxLayout(colorConfigPanel,BoxLayout.X_AXIS));
		colorConfigPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		JButton configBtn = new JButton("Modify Color Scale");
		configBtn.setFont(new Font("Arial",Font.PLAIN,16));
		configBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
						colorScaleOptions();
				}
		});

		colorConfigPanel.add(configBtn);
		toolBar.add(colorConfigPanel);
		
		exportMapPanel = new JPanel();
		exportMapPanel.setBorder(new EmptyBorder(5,5,5,5));
		exportMapPanel.setLayout(new BoxLayout(exportMapPanel,BoxLayout.X_AXIS));
		exportMapPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		JButton saveBtn = new JButton("Export Map");
		saveBtn.setFont(new Font("Arial",Font.BOLD,16));
		saveBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
						saveMap();
				}
		});

		
		exportMapPanel.add(saveBtn);
		//toolBar.add(exportMapPanel);
		return toolBar;
	}
	
	private int chooseNumCombos() {
		int intToReturn=10;
		if(minMaxFromTable[0]<0 && minMaxFromTable[1]>0) {
			intToReturn=11;
		}
		
		return intToReturn;
	}

	protected JComponent addLegendPanel() {
		//add legend panel on the right
		addLegendPanel = new JPanel();
		addLegendPanel.setLayout(new BoxLayout(addLegendPanel,BoxLayout.Y_AXIS));
		addLegendPanel.setBorder(new EmptyBorder(5,5,5,5));
		addLegendPanel.setPreferredSize(new Dimension(200,150));
		addLegendPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		legendLabel = new JLabel("Legend");
		legendLabel.setFont(new Font("Dialog",Font.PLAIN,14));
		legendLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        //Do we need to use unitLookup?
		int unitColIdx = FilteredTable.getColumnByName(jtable, "Units");
		String unitForLegend = (String) jtable.getValueAt(0, unitColIdx);
		//System.out.println("check unitLookup:"+unitLookup.get("AK"));
		//System.out.println("check unitLookup size:"+unitLookup.size());
		LegendPanel useLegend = new LegendPanel(useMapColor,unitForLegend);
		addLegendPanel.add(legendLabel);
		addLegendPanel.add(useLegend);
		return addLegendPanel;
	}

	protected JComponent createWorldMapContent() {
		addMapPanel = new JPanel();
		addMapPanel.setLayout(new BoxLayout(addMapPanel, BoxLayout.X_AXIS));
		addMapPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		addMapPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		stateMap = new MapContent();
		stateMap = createWorldBoundaryMapLayer();
		jmap = new JMapPane(stateMap);
		jmap.setBorder(new EmptyBorder(50, 50, 50, 50));

		PanTool myPanTool = new PanTool();
		myPanTool.onMouseDragged(null);
		jmap.setCursorTool(myPanTool);

//		ZoomInTool zoomIn = new ZoomInTool();
//		zoomIn.setZoom(AbstractZoomTool.DEFAULT_ZOOM_FACTOR);
//		jmap.setCursorTool(zoomIn);
//		jmap.setBackground(Color.BLACK);
//		
		addMapPanel.add(jmap);
		return addMapPanel;
	}

	protected JComponent createFooter() {
		//add information display field at the bottom
		sectorDisplayPanel = new JPanel();
		sectorDisplayPanel.setBorder(new EmptyBorder(10,10,10,10));
		sectorDisplayPanel.setLayout(new BoxLayout(sectorDisplayPanel,BoxLayout.X_AXIS));
		sectorDisplayPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		String mapSectorInfo = MapOptionsUtil.getSectorPlusInfo(jtable);
		sectorText = new JTextField("Displayed in this map:" + mapSectorInfo);
		if (mapSectorInfo.length()>0) {
			sectorText.setVisible(true);
		}else {
			sectorText.setVisible(false);
		}
		sectorText.setFont(new Font("Arial",Font.BOLD,16));
		sectorText.setBackground(Color.GRAY);
		sectorText.setMaximumSize(sectorText.getPreferredSize());
		//sectorText.setHorizontalAlignment(JTextField.CENTER);
		sectorDisplayPanel.add(Box.createHorizontalGlue());
		sectorDisplayPanel.add(sectorText);
		sectorDisplayPanel.add(Box.createHorizontalGlue());
		return sectorDisplayPanel;
	}

	public MapContent createWorldBoundaryMapLayer() {
		boolean verbose=false;
		FeatureLayer countryLayer = null;
		FeatureIterator<SimpleFeature> iterator = null;
		FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = null;
		Collection<Property> properties;
		String propertyVal;
		Color fillerColor;
		FilterFactory myFilter = CommonFactoryFinder.getFilterFactory(null);
		HashMap<String, Double> dataForCountry = new HashMap<>();
		MapContent map = new MapContent();
		String shpFilePath = InterfaceMain.gcamReg32ShapeFileLocation;
		if (statesIncluded) {
			shpFilePath = InterfaceMain.gcamReg32US52ShapeFileLocation;
		} else {
			shpFilePath = InterfaceMain.gcamReg32ShapeFileLocation;
		}
		// String shpFilePath = InterfaceMain.stateShapeFileLocation;
		featureCollection = MapOptionsUtil.getCollectionFromShape(shpFilePath);
		// extract table data
		// extract table data
		int selectedRowIdx = -1;
		try {
			ArrayList<String> yearColInTable = MapOptionsUtil.getYearListFromTableData(jtable);

			//System.out.println("this is the first year column name:"+yearColInTable.get(0));
			//System.out.println("this is the last year column name:"+yearColInTable.get(yearColInTable.size()-1));
			dataForCountry = MapOptionsUtil.getTableDataForStateOrCountry(jtable,(String) yearListMenu.getSelectedItem(),(String)scenarioListMenu.getSelectedItem());
			//int minVal = Collections.min(dataForState.values().stream());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// find the value for each state and create that polygon layer
		iterator = featureCollection.features();
		// check the features inside, can remove these lines later
		while (iterator.hasNext()) {
			SimpleFeature f = (SimpleFeature) iterator.next();
			SimpleFeatureType type = f.getType();
			properties = f.getProperties();
			for (Property property : properties) {
				String propName = property.getName().getLocalPart();

				if (propName.equals("subRegn")) {
					// System.out.println("check property name in this feature:"+propName);
					propertyVal = property.getValue().toString();
					if (verbose) System.out.println("check the value for subRegn attribute for this feature:" + propertyVal);
					if (dataForCountry.get(propertyVal) != null) {
						// fillerColor =
						// findMyColor(minMaxFromTable[0],minMaxFromTable[1],dataForState.get(propertyVal));
						fillerColor = MapOptionsUtil.findStateColorFromMapColor(useMapColor,
								dataForCountry.get(propertyVal));
						// System.out.println("found color for this feature
						// is:"+fillerColor.toString());
					} else {
						fillerColor = null;
					}
					countryLayer = new FeatureLayer(featureCollection,
							SLD.createPolygonStyle(new Color(1, 1, 1), fillerColor, (float) 0.9));
					Filter fil = myFilter.equals(myFilter.property("subRegn"),
							myFilter.literal(f.getProperty("subRegn").getValue()));
					countryLayer.setQuery(new Query(type.getName().getLocalPart(), fil));
					countryLayer.setVisible(true);
					map.addLayer(countryLayer);
				}
			}
			// System.out.println("state short name is:"+f.getAttribute("subRegn"));
		}

		iterator.close();
		return map;
	}

	public class UpdateMap extends JPanel implements ActionListener {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			RedrawWorldMap();
		}
	}
	
	//YD edits,August-2024
		public void RedrawWorldMapLayout() {
			
			//clear up the previous displayed panels and MapContent
			frame.remove(sectorDisplayPanel);
			frame.remove(addLegendPanel);
			stateMap.layers().clear();
			frame.remove(jmap);
			frame.getContentPane().add(createWorldMapContent(),BorderLayout.CENTER);
			frame.getContentPane().add(createFooter(),BorderLayout.PAGE_END);
			frame.getContentPane().add(addLegendPanel(),BorderLayout.EAST);
			//frame.pack();
			frame.setVisible(true);
		}


	
	public void RedrawWorldMap() {
		if(comboBoxNumClasses.getSelectedItem()==null) {
			return;
		}
		//get the updated user's choices
		numColorClass = (int) comboBoxNumClasses.getSelectedItem();
		//System.out.println("numColorClass has changed to:" + numColorClass);
		numColorChoice = Integer.parseInt(MapOptionsUtil.getSelectedButton(choiceGroup)) - 1;
		//System.out.println("colorChoice has changed to:" + numColorChoice);
		// paletteChoice = getSelectedButton(colorGroups);
		paletteChoice = (String) comboBoxPalette.getSelectedItem();
		//System.out.println("paletteChoice has changed to:" + paletteChoice);
		usePalette = MapColorPalette.getMapColorPalette(paletteChoice, numColorChoice, numColorClass, reverseColors);
		//System.out.println("the palette description is changed to:" + usePalette.getDescription());
		if (intervalType == IntervalType.AUTOMATIC) {
		  useMapColor = new MapColor(usePalette,minMaxFromTable[0],minMaxFromTable[1]);
		}else if (intervalType == IntervalType.CUSTOM) {
		  //double minCustom = Double.parseDouble(minField.getText());
		  //double maxCustom = Double.parseDouble(maxField.getText());
		  double minCustom = ((Number)minField.getValue()).doubleValue();
		  double maxCustom = ((Number)maxField.getValue()).doubleValue();
		  useMapColor = new MapColor(usePalette,minCustom,maxCustom);	
		}
		//clear up the previous displayed panels and MapContent
		frame.remove(sectorDisplayPanel);
		frame.remove(addLegendPanel);
		stateMap.layers().clear();
		frame.remove(jmap);
		frame.getContentPane().add(createWorldMapContent(), BorderLayout.CENTER);
		frame.getContentPane().add(createFooter(), BorderLayout.PAGE_END);
		frame.getContentPane().add(addLegendPanel(), BorderLayout.EAST);
		frame.revalidate();
		frame.repaint();
	}

	private void saveMap() {

		BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		frame.remove(toolBar);
		frame.printAll(g2d);
		g2d.dispose();
		frame.getContentPane().add(createToolBar(),BorderLayout.WEST);
		//frame.revalidate();
		//frame.repaint();
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a file name to save the map");
		fileChooser.setFileFilter(new FileNameExtensionFilter("*.png","png"));
		int userSelection = fileChooser.showSaveDialog(null);
		if (userSelection == JFileChooser.APPROVE_OPTION){
			try {
				ImageIO.write(image, "png", fileChooser.getSelectedFile());
				String myString = "map is saved to " + fileChooser.getSelectedFile().getAbsolutePath();
				JOptionPane.showMessageDialog(null, myString, "map is saved", JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e) {
				System.out.println("Could not save map: " + e.toString());
				JOptionPane.showMessageDialog(null, "Unable to save map, please see console for error",
						"Error Saving Map", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void limitNumClassesAndChangeChoices() {
		//get the updated user's choices
		paletteChoice = (String) comboBoxPalette.getSelectedItem();
		//System.out.println("inside limitNumClasses method, paletteChoice has changed to:"+paletteChoice);
		Integer[] newChoices = {1,2,3,4,5,6,7,8,9};
		if (paletteChoice.equalsIgnoreCase("SEQUENTIAL")) {
			if (colorChoicePanel.getComponentCount()>1) {
			    colorChoicePanel.removeAll();
			}	
			addSequentialColorChoices();
			//toolBar.add(colorChoicePanel,2);
			//toolBar.remove(3);
			comboBoxNumClasses.removeAllItems();
			for (int i=0;i<newChoices.length;i++) {
				comboBoxNumClasses.addItem(newChoices[i]);
			}
			comboBoxNumClasses.setSelectedIndex(8);
		}else if (paletteChoice.equalsIgnoreCase("DIVERGING")) {
			if (colorChoicePanel.getComponentCount()>1) {
				colorChoicePanel.removeAll();
			}
			addDivergingColorChoices();
			//toolBar.add(colorChoicePanel,2);
			//toolBar.remove(3);
			comboBoxNumClasses.removeAllItems();
			for (int i=0;i<numClasses.length;i++) {
				comboBoxNumClasses.addItem(numClasses[i]);
			}
			comboBoxNumClasses.setSelectedIndex(chooseNumCombos()-1);
		}
		 
		
	}
	
	private void addDivergingColorChoices() {
		
		//colorChoicePanel = new JPanel();
		//colorChoicePanel.setBorder(new EmptyBorder(1,1,1,1));
		//colorChoicePanel.setLayout(new BoxLayout(colorChoicePanel,BoxLayout.X_AXIS));
		//colorChoicePanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		choiceGroup = new ButtonGroup();
		
		for (int i=0;i<9;i++) {
			String fileStr = InterfaceMain.shapeFileLocationPrefix+File.separator+"Diverging" + i + ".png";
			File imageFile = new File(fileStr);
			String imagePath = imageFile.getAbsolutePath();
			//System.out.println("found the diverging image path: " + imagePath);
			ImageIcon imageIcon = new ImageIcon(imagePath);
			Image image = imageIcon.getImage();
			Image newImg = image.getScaledInstance(15,150,java.awt.Image.SCALE_SMOOTH);
			imageIcon = new ImageIcon(newImg);
			String buttonStr =  String.valueOf(i+1);
			JRadioButton radioButton = new JRadioButton(buttonStr,imageIcon); 
			radioButton.setFont(new Font("Arial",Font.BOLD,14));
			if (i == 4) {
				radioButton.setSelected(true);
				//System.out.println("check radioButton background color: " + radioButton.getBackground());
				radioButton.setBackground(Color.LIGHT_GRAY);
				radioButton.setForeground(Color.BLACK);
				}
			choiceGroup.add(radioButton);
			colorChoicePanel.add(radioButton);
			radioButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (radioButton.isSelected()) {
						radioButton.setBackground(Color.LIGHT_GRAY);;
						radioButton.setForeground(Color.BLACK);
						MapOptionsUtil.resetColorForNonSelectedButtons(choiceGroup);
						RedrawWorldMap();
					}else {
						//do nothing;
					}
				}
			});
		}
}

private void addSequentialColorChoices() {
	    
		//colorChoicePanel = new JPanel();
		//colorChoicePanel.setBorder(new EmptyBorder(1,1,1,1));
		//colorChoicePanel.setLayout(new BoxLayout(colorChoicePanel,BoxLayout.X_AXIS));
		//colorChoicePanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		choiceGroup = new ButtonGroup();
		for (int i=0;i<8;i++) {
			String fileStr = InterfaceMain.shapeFileLocationPrefix+File.separator+"Sequential" + i + ".png";
			File imageFile = new File(fileStr);
			String imagePath = imageFile.getAbsolutePath();
			//System.out.println("found the sequential image path: " + imagePath);
			ImageIcon imageIcon = new ImageIcon(imagePath);
			Image image = imageIcon.getImage();
			Image newImg = image.getScaledInstance(15,100,java.awt.Image.SCALE_SMOOTH);
			imageIcon = new ImageIcon(newImg);
		    String buttonStr =  String.valueOf(i+1);
			JRadioButton radioButton = new JRadioButton(buttonStr,imageIcon);
			radioButton.setFont(new Font("Arial",Font.BOLD,14));				
			if (i == 1) {
				radioButton.setSelected(true);
				radioButton.setBackground(Color.LIGHT_GRAY);;
				radioButton.setForeground(Color.BLACK);
			}
			choiceGroup.add(radioButton);
			colorChoicePanel.add(radioButton);
			radioButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (radioButton.isSelected()) {
						radioButton.setBackground(Color.LIGHT_GRAY);;
						radioButton.setForeground(Color.BLACK);
						MapOptionsUtil.resetColorForNonSelectedButtons(choiceGroup);
						RedrawWorldMap();
					}else {
						//do nothing
					}
				}
			});
			
		}	
}
	
	public void colorScaleOptions() {
		
		final JDialog colorDialog = new JDialog(frame, "Change map color scale", true);
		colorDialog.setSize(new Dimension(650, 300));
		colorDialog.getGlassPane().addMouseListener(new MouseAdapter() {
		});
		colorDialog.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Container contentPane = colorDialog.getContentPane();
		//add UI for user's color scale values
		JPanel scalePane = new JPanel();
		scalePane.setBorder(new EmptyBorder(10,10,10,10));
		scalePane.setLayout(new BoxLayout(scalePane,BoxLayout.Y_AXIS));
		JPanel minScalePane = new JPanel();
		minScalePane.setBorder(new EmptyBorder(5,5,5,5));
		minScalePane.setLayout(new BoxLayout(minScalePane,BoxLayout.X_AXIS));
		JLabel minLabel = new JLabel("Min:",SwingConstants.LEFT);
		minLabel.setFont(new Font("Arial",Font.PLAIN,16));
		NumberFormat numClassFormat = NumberFormat.getNumberInstance();
		numClassFormat.setMaximumFractionDigits(5);
		minField = new JFormattedTextField(numClassFormat);
		minField.setFont(new Font("Arial",Font.PLAIN,14));
		minField.setColumns(7);
		minScalePane.add(minLabel);
		minScalePane.add(minField);
		JPanel maxScalePane = new JPanel();
		maxScalePane.setBorder(new EmptyBorder(5,5,5,5));
		maxScalePane.setLayout(new BoxLayout(maxScalePane,BoxLayout.X_AXIS));
		JLabel maxLabel = new JLabel("Max:",SwingConstants.LEFT);
		maxLabel.setFont(new Font("Arial",Font.PLAIN,16));
		maxField = new JFormattedTextField(numClassFormat);
		maxField.setFont(new Font("Arial",Font.PLAIN,14));
		maxField.setColumns(7);
		maxScalePane.add(maxLabel);
		maxScalePane.add(maxField);
		if (intervalType == IntervalType.AUTOMATIC){
			minField.setValue(minMaxFromTable[0]);
			maxField.setValue(minMaxFromTable[1]);
		}else if (intervalType == IntervalType.CUSTOM) {
			minField.setValue(previousMin);
			maxField.setValue(previousMax);
		}
		scalePane.add(maxScalePane, BorderLayout.PAGE_START);
		scalePane.add(minScalePane, BorderLayout.PAGE_END);
		
		//add UI for user's decision on customizing color scale
		JPanel buttonPane = new JPanel();
		
		final JButton cancelButton = new JButton("Cancel");
		cancelButton.setFont(new Font("Arial",Font.PLAIN,14));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				colorDialog.setVisible(false);
			}
		});
		
		final JButton confirmButton = new JButton("Confirm");
		confirmButton.setFont(new Font("Arial",Font.PLAIN,14));
		confirmButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//previousMin = Double.parseDouble(minField.getText());
				//previousMax = Double.parseDouble(maxField.getText());
				previousMin = ((Number)minField.getValue()).doubleValue();
				previousMax = ((Number)maxField.getValue()).doubleValue();
				intervalType = IntervalType.CUSTOM;
				colorDialog.setVisible(false);
				RedrawWorldMap();
			}
		});
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPane.add(confirmButton);
		buttonPane.add(Box.createHorizontalStrut(20));
		buttonPane.add(cancelButton);
		buttonPane.add(Box.createHorizontalGlue());	
		//add other choices on customizing color scale
		
		
		JPanel choiceHolderPane = new JPanel();
		choiceHolderPane.setBorder(new EmptyBorder(10,10,10,10));
		choiceHolderPane.setLayout(new BoxLayout(choiceHolderPane,BoxLayout.Y_AXIS));
		JPanel selPane = new JPanel();
		JLabel selLabel = new JLabel("Select:",SwingConstants.LEFT);
		selPane.add(selLabel);
		maxLabel.setFont(new Font("Arial",Font.PLAIN,16));
		JPanel choicePane = new JPanel();
		final JButton useLocalButton = new JButton("Within-year min/max");
		useLocalButton.setFont(new Font("Arial", Font.PLAIN, 14));
		useLocalButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				minMaxFromTable = getAbsMinMaxFromLocal(normalizeScale);
				minField.setValue(minMaxFromTable[0]);
				maxField.setValue(minMaxFromTable[1]);
			}
		});
		
		final JButton useAllYEarButton = new JButton("Across-year min/max");
		useAllYEarButton.setFont(new Font("Arial", Font.PLAIN, 14));
		useAllYEarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				minMaxFromTable = getAbsMinMaxForAllYears(normalizeScale);
				minField.setValue(minMaxFromTable[0]);
				maxField.setValue(minMaxFromTable[1]);
			}
		});

		final JButton useGlobalButton = new JButton("Global min/max");
		useGlobalButton.setFont(new Font("Arial", Font.PLAIN, 14));
		useGlobalButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				minMaxFromTable = MapOptionsUtil.getAbsMinMaxFromTable(jtable, normalizeScale);
				minField.setValue(minMaxFromTable[0]);
				maxField.setValue(minMaxFromTable[1]);
			}
		});
		
		choicePane.setLayout(new BoxLayout(choicePane, BoxLayout.X_AXIS));
		choicePane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		choicePane.add(useLocalButton);
		choicePane.add(Box.createHorizontalStrut(5));
		choicePane.add(useAllYEarButton);
		choicePane.add(Box.createHorizontalStrut(5));
		choicePane.add(useGlobalButton);
		choicePane.add(Box.createHorizontalGlue());

		JPanel normPane = new JPanel();
		normPane.setLayout(new BoxLayout(normPane, BoxLayout.X_AXIS));
		normPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		final JCheckBox checkNormalizeScaleRatio = new JCheckBox("Normalize Scale Ratio");
		checkNormalizeScaleRatio.setSelected(true);
		normalizeScale = true;
		checkNormalizeScaleRatio.setFont(new Font("Arial", Font.PLAIN, 14));
		checkNormalizeScaleRatio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkNormalizeScaleRatio.isSelected()) {
					normalizeScale = true;
				} else {
					normalizeScale = false;
				}
			}
		});
		normPane.add(Box.createHorizontalStrut(5));
		normPane.add(checkNormalizeScaleRatio);
		normPane.add(Box.createHorizontalGlue());
		
		choiceHolderPane.add(selPane);
		choicePane.add(Box.createVerticalStrut(5));
		choiceHolderPane.add(choicePane);
		choicePane.add(Box.createVerticalStrut(5));
		choiceHolderPane.add(normPane);
		choicePane.add(Box.createVerticalGlue());
		

		
		
		contentPane.add(scalePane, BorderLayout.PAGE_START);
		contentPane.add(choiceHolderPane, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.PAGE_END);
		
		colorDialog.setVisible(true);
	} //colorScaleOptions() end
	
	public double[] getAbsMinMaxForAllYears(boolean normalizeScale) {

		double[] minMax = new double[2];
		HashMap<String,Double> dataForState = null;
		double minD = Double.MAX_VALUE;
		double maxD = Double.MAX_VALUE*-1.0;

		for(int i=0;i<yearListMenu.getComponents().length;i++) {
			String curItem=yearListMenu.getItemAt(i).toString();
			dataForState = MapOptionsUtil.getTableDataForStateOrCountry(jtable,curItem,(String)scenarioListMenu.getSelectedItem());
			double newMin=Collections.min(dataForState.values());
			double newMax=Collections.max(dataForState.values());
			if(newMin<minD) {
				minD=newMin;
			}
			if(newMax>maxD) {
				maxD=newMax;
			}
		}
		
		
		
		
		

		double min = minD;
		double max = maxD;;		
		

		// in case the minimum is equal to maximum
		if (min == max) {
			minMax[0] = min;
			minMax[1] = max + Math.max(0.1, 0.1 * min);
		} else {
			if (max > 0 & min < 0 & normalizeScale) {
				if (Math.abs(min) >= max) {
					max = Math.abs(min);
				} else {
					min = -max;
				}
				minMax[0] = min;
				minMax[1] = max;
			} else {
				minMax[0] = min;
				minMax[1] = max;
			}
		}
		return minMax;
	}

	public double[] getAbsMinMaxFromLocal(boolean normalizeScale) {

		double[] minMax = new double[2];
		HashMap<String,Double> dataForState = new HashMap<>();
		dataForState = MapOptionsUtil.getTableDataForStateOrCountry(jtable,(String) yearListMenu.getSelectedItem(),(String)scenarioListMenu.getSelectedItem());
		double minD = Collections.min(dataForState.values());
		double maxD = Collections.max(dataForState.values());
		
		//int min = (int)Math.floor(minD);
		//int max = (int)Math.ceil(maxD);	
		
		double min = minD;
		double max = maxD;		
		

		// in case the minimum is equal to maximum
		if (min == max) {
			minMax[0] = min;
			minMax[1] = max + Math.max(0.1, 0.1 * min);
		} else {
			if (max > 0 & min < 0 & normalizeScale) {
				if (Math.abs(min) >= max) {
					max = Math.abs(min);
				} else {
					min = -max;
				}
				minMax[0] = min;
				minMax[1] = max;
			} else {
				minMax[0] = min;
				minMax[1] = max;
			}
		}
		return minMax;
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
	
	//YD revision, Nov-2024
		public class RowSelectionListener implements ListSelectionListener{
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (jtable.getSelectedRow()>-1 & frame.isDisplayable()) {
			//		System.out.println("inside RowSelectionListener valueChanged loop now: " + jtable.getSelectedRow());
					RedrawWorldMap();
			    }else if (jtable.getSelectedRow()>-1 & !frame.isDisplayable()){	
			   // 	System.out.println("inside RowSelectionListener valueChanged else loop now: " + jtable.getSelectedRow());
			    	initialize();
			    }// if loop end
		  }
		} //RowSelectionListener class end

}
