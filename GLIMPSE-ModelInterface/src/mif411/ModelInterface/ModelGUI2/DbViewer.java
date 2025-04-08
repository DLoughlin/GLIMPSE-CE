/** LEGAL NOTICE
* This computer software was prepared by Battelle Memorial Institute,
* hereinafter the Contractor, under Contract No. DE-AC05-76RL0 1830
* with the Department of Energy (DOE). NEITHER THE GOVERNMENT NOR THE
* CONTRACTOR MAKES ANY WARRANTY, EXPRESS OR IMPLIED, OR ASSUMES ANY
* LIABILITY FOR THE USE OF THIS SOFTWARE. This notice including this
* sentence must appear on any copies of this computer software.
* 
* Copyright 2012 Battelle Memorial Institute.  All Rights Reserved.
* Distributed as open-source under the terms of the Educational Community 
* License version 2.0 (ECL 2.0). http://www.opensource.org/licenses/ecl2.php
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
*/
package ModelInterface.ModelGUI2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoableEdit;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.basex.api.dom.BXDoc;
import org.basex.api.dom.BXNode;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.iter.Iter;
import org.basex.query.value.item.Item;
import org.basex.query.value.node.ANode;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSParserFilter;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.traversal.NodeFilter;

import ModelInterface.BatchRunner;
import ModelInterface.InterfaceMain;
import ModelInterface.MenuAdder;
import ModelInterface.ConfigurationEditor.guihelpers.XMLFileFilter;
import ModelInterface.InterfaceMain.MenuManager;
import ModelInterface.ModelGUI2.QueryTreeModel.QueryGroup;
import ModelInterface.ModelGUI2.queries.QueryGenerator;
import ModelInterface.ModelGUI2.queries.SingleQueryExtension;
import ModelInterface.ModelGUI2.tables.BaseTableModel;
import ModelInterface.ModelGUI2.tables.TableSorter;
import ModelInterface.ModelGUI2.tables.TableTransferHandler;
import ModelInterface.ModelGUI2.undo.RenameScenarioUndoableEdit;
import ModelInterface.ModelGUI2.xmldb.QueryBinding;
import ModelInterface.ModelGUI2.xmldb.XMLDB;
import ModelInterface.common.DataPair;
import ModelInterface.common.FileChooser;
import ModelInterface.common.FileChooserFactory;
import ModelInterface.common.RecentFilesList.RecentFile;

import filter.FilterTreePaneYears;

import graphDisplay.SankeyDiagramPanel;

/***
 * * Author Action Date Flag
 * ======================================================================= TWU
 * Add capability to allow 1/2/2017 @1 to invoke from command line Allow DBView
 * to accept filtered JTable which is in different table model and container Add
 * Copy to Clip board capability to allow large data paste to excel file Add a
 * monitor to large data drag
 * 
 */

public class DbViewer implements ActionListener, MenuAdder, BatchRunner {
	private Document queriesDoc;

	private static String controlStr = "DbViewer";

	private JTable jTable; // does this still need to be a field?

	private DOMImplementationLS implls;

	protected Vector<ScenarioListItem> scns;
	protected JList scnList;
	protected JList regionList;
	protected Vector regions;
	protected BaseTableModel bt; // does this still need to be a field?
	protected JScrollPane jsp; // does this still need to be a field?
	protected QueryTreeModel queries;
	private JTabbedPane tablesTabs = new JTabbedPane();
	private JSplitPane scenarioRegionSplit;
	private JSplitPane queriesSplit;
	private JSplitPane tableCreatorSplit;
	private JMenuItem queriesLockMenu; // YD added
	private JMenuItem queriesCreateMenu; // YD added
	private JMenuItem queriesEditMenu; // YD added
	private JMenuItem queriesRemoveMenu; // YD added
	private JMenuItem queriesUpdateMenu; // YD added
	private JMenuItem significantDigitsMenu; // YD added
	private JMenuItem enableUnitConversionsMenu;
	private JMenuItem createFavoritesMenu;// YD Feb-2024
	private JMenuItem loadFavoritesMenu;
	private JMenuItem appendFavoritesMenu;// YD Feb-2024
	private JMenuItem betaMn;

	private JMenuItem menuExpPrn;

	private JMenuItem toolsSankeyMenu; //YD Sep-2024
	private JMenuItem loadMenu; // YD added
	private JMenuItem displayMenu; // YD added
	private SankeyDiagramPanel sankeyDiagram; //YD added

	private String filteringText; // YD added
	private Enumeration<TreePath> expansionState;// YD added
	private boolean AllCollapsed = false; // YD added
	ArrayList<String> region_list = new ArrayList<String>();// YD added,Feb-2024
	ArrayList<String> subregion_list = new ArrayList<String>();// YD added,Feb-2024
	ArrayList<String> preset_region_list = new ArrayList<String>();// YD added,Feb-2024
	private JComboBox<String> comboBoxPresetRegions; // YD added,Feb-2024
	private String[] preset_choices; // YD added,Feb-2024
	private JScrollPane listScrollRegions; // YD added,Feb-2024
	private JScrollPane listScrollQueries; // YD added,Feb-2024
	public static boolean queryTreeLocked = true; // YD added
	public static boolean disable3Digits = false; // YD added
	public static boolean enableUnitConversions = true;

	public static final String SCENARIO_LIST_NAME = "scenario list";
	public static final String REGION_LIST_NAME = "region list";
	
	
	private static Map<String, String> selectedYears=null;
	
	public static ArrayList openWindows=new ArrayList();
	
	
	ArrayList<Object> windowList=new ArrayList<>();

	public DbViewer() {
		final InterfaceMain main = InterfaceMain.getInstance();
		final JFrame parentFrame = main.getFrame();
		final DbViewer thisViewer = this;
		//getSelectedYearsFromPropFile();
		//getAllYearListFromPropFIle();

		try {
			DOMImplementationRegistry reg = DOMImplementationRegistry.newInstance();
			implls = (DOMImplementationLS) reg.getDOMImplementation("XML 3.0");
			if (implls == null) {
				System.out.println("Could not find a DOM3 Load-Save compliant parser.");
				InterfaceMain.getInstance().showMessageDialog("Could not find a DOM3 Load-Save compliant parser.",
						"Initialization Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} catch (Exception e) {
			System.err.println("Couldn't initialize DOMImplementation: " + e);
			InterfaceMain.getInstance().showMessageDialog("Couldn't initialize DOMImplementation\n" + e,
					"Initialization Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
//System.out.println("inside DbViewer()");
		if (parentFrame == null) {
			// no gui components available such as in batch mode.
			return;
		}

		parentFrame.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("Control")) {
					if (evt.getOldValue().equals(controlStr) || evt.getOldValue().equals(controlStr + "Same")) {
						// make sure all queries get killed before we close the database
						for (int tab = 0; tab < tablesTabs.getTabCount(); ++tab) {
							((QueryResultsPanel) tablesTabs.getComponentAt(tab)).killThreadAndWait();
						}

						if (queries.hasChanges() && InterfaceMain.getInstance().showConfirmDialog(
								"The Queries have been modified.  Do you want to save them?", "Confirm Save Queries",
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
								JOptionPane.YES_OPTION) == JOptionPane.YES_OPTION) {
							writeQueries();
						}
						Properties prop = main.getProperties();
						prop.setProperty("scenarioRegionSplit",
								String.valueOf(scenarioRegionSplit.getDividerLocation()));
						prop.setProperty("queriesSplit", String.valueOf(queriesSplit.getDividerLocation()));
						prop.setProperty("tableCreatorSplit", String.valueOf(tableCreatorSplit.getDividerLocation()));
						main.getUndoManager().discardAllEdits();
						main.refreshUndoRedo();
						main.getSaveMenu().removeActionListener(thisViewer);
						main.getSaveAsMenu().removeActionListener(thisViewer);
						main.getSaveAsMenu().setEnabled(false);
						main.getSaveMenu().setEnabled(false);
						parentFrame.getContentPane().removeAll();

						// closing the db should be the last thing to do in case
						// other things have pointers to db objects.
						XMLDB.closeDatabase();
					}
					if (evt.getNewValue().equals(controlStr)) {
						Properties prop = main.getProperties();
						String queryFileName = prop.getProperty("queryFile", null);
						File queryFile = queryFileName != null ? new File(queryFileName) : null;
						if (queryFile == null || !queryFile.exists()) {
							FileChooser fc = FileChooserFactory.getFileChooser();
							final FileFilter xmlFilter = new XMLFilter();
							File[] xmlFiles = fc.doFilePrompt(parentFrame,
									"Could not find query file.  Please select one.", FileChooser.LOAD_DIALOG,
									new File(main.getProperties().getProperty("lastDirectory", ".")), xmlFilter);
							if (xmlFiles == null && xmlFiles.length > 0) {
								// user hit cancel just create a new query file
								queryFileName = "Main_queries.xml";
								queryFile = new File(queryFileName);
							} else {
								queryFile = xmlFiles[0];
								queryFileName = queryFile.getAbsolutePath();
							}
							prop.setProperty("queryFile", queryFileName);
						}

						// TODO: move to load preferences
						scenarioRegionSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
						scenarioRegionSplit.setResizeWeight(.5);
						queriesSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
						// queriesSplit.setLeftComponent(scenarioRegionSplit);
						queriesSplit.setResizeWeight(.5);
						tableCreatorSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
						String tempInt;
						try {
							if ((tempInt = prop.getProperty("scenarioRegionSplit")) != null) {
								scenarioRegionSplit.setDividerLocation(Integer.valueOf(tempInt));
							}
							if ((tempInt = prop.getProperty("queriesSplit")) != null) {
								queriesSplit.setDividerLocation(Integer.valueOf(tempInt));
							}
							if ((tempInt = prop.getProperty("tableCreatorSplit")) != null) {
								tableCreatorSplit.setDividerLocation(Integer.valueOf(tempInt));
							}
						} catch (NumberFormatException nfe) {
							System.out.println("Invalid split location preference: " + nfe);
						}
						main.getSaveMenu().addActionListener(thisViewer);
						main.getSaveAsMenu().addActionListener(thisViewer);
						// main.getSaveAsMenu().setEnabled(true);
						queriesDoc = readQueries(queryFile);
						// String path = prop.getProperty("paramPath");
						// if (path != null) {
						// //System.out.println("path: " + path);
						// File afile = new File(path);
						// doOpenDB(afile);
						// }
					}
				}
			}
		});

	}
	
	public static Map<String, String> getSelectedYearsFromPropFile() {
		
		
		if(DbViewer.selectedYears==null) {
			
		
			DbViewer.selectedYears=new HashMap<>();
		if(InterfaceMain.getInstance() != null) {InterfaceMain.getInstance().getProperties();
		       Properties globalProperties = InterfaceMain.getInstance().getProperties();
		       Object rsp=globalProperties.get("selectedYearList");
		       if(rsp!=null) {
		       String defaultYearStr=rsp.toString();
		       
		       String[] yearsArr = defaultYearStr.split(";");
		       DbViewer.selectedYears = new HashMap<>();
		       if(!(yearsArr.length == 1 && yearsArr[0].equals(""))) {
			       for(String year : yearsArr ) {
			    	   DbViewer.selectedYears.put(year+"", year+"");
			       }
		      }
		       }
	       } 
		
		if(DbViewer.selectedYears.size()==0) {
			DbViewer.selectedYears.put("2015", "2015");
	    	DbViewer.selectedYears.put("2020", "2020");
	    	DbViewer.selectedYears.put("2025", "2025");
	    	DbViewer.selectedYears.put("2030", "2030");
	    	DbViewer.selectedYears.put("2035", "2035");
	    	DbViewer.selectedYears.put("2040", "2040");
	    	DbViewer.selectedYears.put("2045", "2045");
	    	DbViewer.selectedYears.put("2050", "2050");
	       }
		}
		return DbViewer.selectedYears;
		
	}
	
	public static List getAllYearListFromPropFile() {
	       // the default year list could go in a preference dialog as well
	       // WARNING: not thread safe
		 List<String> allYearList=new ArrayList<String>();
	           if(InterfaceMain.getInstance() != null) {
			       Properties globalProperties = InterfaceMain.getInstance().getProperties();
			       
			       String allYearStr=(String)globalProperties.get("allYearsList");
			    		  
			       //globalProperties.setProperty("defaultYearList", defaultYearStr = 
					//       globalProperties.getProperty("defaultYearList", 
					//	       "1990;2005;2020;2035;2050;2065;2080;2095"));
			       if(allYearStr!=null) {
				       String[] yearsArr = allYearStr.split(";");
				       allYearList = new ArrayList<String>(yearsArr.length);
				       if(!(yearsArr.length == 1 && yearsArr[0].equals(""))) {
					       for(String year : yearsArr ) {
					    	   allYearList.add(year);
					       }
				      }
			       }
		       } 
	           
	           if(allYearList.size()==0) {
		    	   allYearList = new ArrayList<String>();
		    	   allYearList.add("1990");
			       for(int i=2005;i<2101;i+=5) {
			    	   allYearList.add(String.valueOf(i));
			       }
		       }
			       
			       
	       
	       return allYearList;
 }

	private JMenuItem makeMenuItem(String title) {
		JMenuItem ret = new JMenuItem(title);
		ret.addActionListener(this);
		return ret;
	}

	public void addMenuItems(InterfaceMain.MenuManager menuMan) {
		final InterfaceMain main = InterfaceMain.getInstance();
		final JFrame parentFrame = main.getFrame();
		// YD edits, August-2023
		JMenuItem menuItem = new JMenuItem("Open DB"); // YD edits,"DB Open" to "Open DB"
		menuItem.addActionListener(this);

		// menuMan.getSubMenuManager(InterfaceMain.FILE_MENU_POS).getSubMenuManager(InterfaceMain.FILE_OPEN_SUBMENU_POS)
		// .addMenuItem(menuItem, 30); //YD commented out
		menuMan.getSubMenuManager(InterfaceMain.FILE_MENU_POS).addMenuItem(menuItem, 5); // YD added this line

		final JMenuItem menuManage = makeMenuItem("Manage DB");
		menuMan.getSubMenuManager(InterfaceMain.FILE_MENU_POS).addMenuItem(menuManage, 10);
		menuManage.setEnabled(false);
		// TODO: why are there two property change listeners
		final ActionListener thisListener = this;
		parentFrame.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("Control")) {
					if (evt.getOldValue().equals(controlStr) || evt.getOldValue().equals(controlStr + "Same")) {
						menuManage.setEnabled(false);
						// menuBatch.setEnabled(false);
						// TODO: have the interface main handle all batch files including
						// this ones
						JMenuItem batchMenu = main.getBatchMenu();
						batchMenu.removeActionListener(thisListener);
						batchMenu.addActionListener(main);
					}
					if (evt.getNewValue().equals(controlStr)) {
						menuManage.setEnabled(true);
						// menuBatch.setEnabled(true);
						// TODO: have the interface main handle all batch files including
						// this ones
						JMenuItem batchMenu = main.getBatchMenu();
						batchMenu.removeActionListener(main);
						batchMenu.addActionListener(thisListener);
					}
				}
			}
		});
		//menuExpPrn = makeMenuItem("Export Tabs as CSVs");
		//menuMan.getSubMenuManager(InterfaceMain.FILE_MENU_POS).addMenuItem(menuExpPrn, 20);
		//JMenu tabctrl=new JMenu("Tabs");
		JMenuItem tabCl=new JMenuItem("Close All Tabs");
		tabCl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeAllTabs();
			}

			
		});
		
		JMenuItem winCl=new JMenuItem("Close All Windows");
		winCl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeAllWindows();
			}

			
		});
		menuExpPrn=makeMenuItem("Export Tabs as CSVs");
		menuExpPrn.setEnabled(false);
		menuMan.getSubMenuManager(InterfaceMain.VIEW_MENU_POS).addMenuItem(tabCl,1);
		menuMan.getSubMenuManager(InterfaceMain.VIEW_MENU_POS).addMenuItem(winCl,2);
		menuMan.getSubMenuManager(InterfaceMain.VIEW_MENU_POS).addSeparator(3);
		
		
		// YD added lines, moved "Disable 3 Significant Digits" to the top
				significantDigitsMenu = new JMenuItem("Disable 3 Significant Digits");
				significantDigitsMenu.addActionListener(this);
		menuMan.getSubMenuManager(InterfaceMain.VIEW_MENU_POS).addMenuItem(significantDigitsMenu, 10);
		significantDigitsMenu.setEnabled(true);

		enableUnitConversionsMenu = new JMenuItem("Disable Unit Conversions");
		enableUnitConversionsMenu.addActionListener(this);
		enableUnitConversionsMenu.setEnabled(true);
		menuMan.getSubMenuManager(InterfaceMain.VIEW_MENU_POS).addMenuItem(enableUnitConversionsMenu, 11);
		

		JMenuItem yearsMn=new JMenuItem("Select Years");
		yearsMn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new FilterTreePaneYears();
			}

			
		});
		
		menuMan.getSubMenuManager(InterfaceMain.VIEW_MENU_POS).addSeparator(20);
		menuMan.getSubMenuManager(InterfaceMain.VIEW_MENU_POS).addMenuItem(yearsMn, 21);
		
		
		
		
		menuMan.getSubMenuManager(InterfaceMain.FILE_MENU_POS).addSeparator(InterfaceMain.FILE_MENU_SEPERATOR);
		menuMan.getSubMenuManager(InterfaceMain.FILE_MENU_POS).addMenuItem(menuExpPrn, 35);
		menuMan.getSubMenuManager(InterfaceMain.FILE_MENU_POS).addSeparator(37);
		
		
		parentFrame.addPropertyChangeListener(new PropertyChangeListener() {
			private int numQueries = 0;

			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("Control")) {
					if (evt.getOldValue().equals(controlStr) || evt.getOldValue().equals(controlStr + "Same")) {
						menuExpPrn.setEnabled(false);
					}
				} else if (evt.getPropertyName().equals("Query") && evt.getOldValue() == null) {
					menuExpPrn.setEnabled(true);
					++numQueries;
				} else if (evt.getPropertyName().equals("Query") && evt.getNewValue() == null) {
					if (--numQueries == 0) {
						menuExpPrn.setEnabled(false);
					}
				}
			}
		});
		
		
		
		
		
		queriesLockMenu = new JMenuItem("Unlock Query Tree");
		queriesLockMenu.addActionListener(this);
		menuMan.getSubMenuManager(InterfaceMain.ADVANCED_MENU_POS)
				.getSubMenuManager(InterfaceMain.ADVANCED_SUBMENU1_POS).addMenuItem(queriesLockMenu, 2);

		queriesUpdateMenu = new JMenuItem("Update Single Query");
		queriesUpdateMenu.addActionListener(this);
		menuMan.getSubMenuManager(InterfaceMain.ADVANCED_MENU_POS)
				.getSubMenuManager(InterfaceMain.ADVANCED_SUBMENU1_POS).addMenuItem(queriesUpdateMenu, 5);
		queriesUpdateMenu.setEnabled(false);

		queriesCreateMenu = new JMenuItem("Create");
		queriesCreateMenu.addActionListener(this);
		menuMan.getSubMenuManager(InterfaceMain.ADVANCED_MENU_POS)
				.getSubMenuManager(InterfaceMain.ADVANCED_SUBMENU1_POS).addMenuItem(queriesCreateMenu, 10);
		queriesCreateMenu.setEnabled(false);

		queriesEditMenu = new JMenuItem("Edit");
		queriesEditMenu.addActionListener(this);
		menuMan.getSubMenuManager(InterfaceMain.ADVANCED_MENU_POS)
				.getSubMenuManager(InterfaceMain.ADVANCED_SUBMENU1_POS).addMenuItem(queriesEditMenu, 15);
		queriesEditMenu.setEnabled(false);

		// YD edits GUI changes second round
		queriesRemoveMenu = new JMenuItem("Remove");
		queriesRemoveMenu.addActionListener(this);
		menuMan.getSubMenuManager(InterfaceMain.ADVANCED_MENU_POS)
				.getSubMenuManager(InterfaceMain.ADVANCED_SUBMENU1_POS).addMenuItem(queriesRemoveMenu, 20);
		queriesRemoveMenu.setEnabled(false);

		loadFavoritesMenu = new JMenuItem("Load Favorite Queries File");
		// createFavoritesMenu.addActionListener(this);
		menuMan.getSubMenuManager(InterfaceMain.ADVANCED_MENU_POS)
				.getSubMenuManager(InterfaceMain.ADVANCED_SUBMENU1_POS).addMenuItem(loadFavoritesMenu, 45);

		// YD added,Feb-2024
		createFavoritesMenu = new JMenuItem("Create Favorite Queries File");
		// createFavoritesMenu.addActionListener(this);
		menuMan.getSubMenuManager(InterfaceMain.ADVANCED_MENU_POS)
				.getSubMenuManager(InterfaceMain.ADVANCED_SUBMENU1_POS).addMenuItem(createFavoritesMenu, 45);

		appendFavoritesMenu = new JMenuItem("Append Favorite Queries File");
		// appendFavoritesMenu.addActionListener(this);
		menuMan.getSubMenuManager(InterfaceMain.ADVANCED_MENU_POS)
				.getSubMenuManager(InterfaceMain.ADVANCED_SUBMENU1_POS).addMenuItem(appendFavoritesMenu, 48);

		
		menuMan.getSubMenuManager(InterfaceMain.ADVANCED_MENU_POS).addSeparator(99);
		String toUse="Enable Beta Features";
		if(InterfaceMain.enableMapping==true || InterfaceMain.enableSankey==true) {
			toUse="Disable Beta Features";
		}
		betaMn=new JMenuItem(toUse);
		betaMn.addActionListener(this);
		menuMan.getSubMenuManager(InterfaceMain.ADVANCED_MENU_POS).addMenuItem(betaMn, 100);

	}

	public void actionPerformed(ActionEvent e) {
		final InterfaceMain main = InterfaceMain.getInstance();
		final JFrame parentFrame = main.getFrame();
		if (e.getActionCommand().equals("Open DB")) { // YD edits,"DB Open" to "Open DB"
			File[] dbFiles;
			if (e.getSource() instanceof RecentFile) {
				dbFiles = ((RecentFile) e.getSource()).getFiles();
			} else {
				final FileFilter dbFilter = (new javax.swing.filechooser.FileFilter() {
					public boolean accept(File f) {
						return f.isDirectory();
					}

					public String getDescription() {
						return "Directory for a BaseX DB";
					}
				});
				FileChooser fc = FileChooserFactory.getFileChooser();
				// Now open chooser
				dbFiles = fc.doFilePrompt(parentFrame, "Choose BaseX Database", FileChooser.LOAD_DIALOG,
						new File(main.getProperties().getProperty("lastDirectory", ".")), dbFilter, this, "Open DB");
			}

			if (dbFiles != null) {
				main.fireControlChange(controlStr);
				doOpenDB(dbFiles[0]);
			}
		} else if (e.getActionCommand().equals("Manage DB")) {
			manageDB();
		}else if(e.getActionCommand().equals("Enable Beta Features")) {
			betaMn.setText("Disable Beta Features");
			InterfaceMain.enableMapping=true;
			InterfaceMain.enableSankey=true;
			Properties prop = main.getProperties();
			prop.setProperty("enableMapping",
					String.valueOf(InterfaceMain.enableMapping));
			prop.setProperty("enableSankey",
					String.valueOf(InterfaceMain.enableSankey));
		}
		else if(e.getActionCommand().equals("Disable Beta Features")) {
			betaMn.setText("Enable Beta Features");
			InterfaceMain.enableMapping=false;
			InterfaceMain.enableSankey=false;
			Properties prop = main.getProperties();
			prop.setProperty("enableMapping",
					String.valueOf(InterfaceMain.enableMapping));
			prop.setProperty("enableSankey",
					String.valueOf(InterfaceMain.enableSankey));
			
		}else if (e.getActionCommand().equals("Batch Query File")) { // YD edits, changed "Batch File" to "Batch Query
																		// File"
			FileChooser fc = FileChooserFactory.getFileChooser();
			// Now open chooser
			final FileFilter xmlFilter = new XMLFilter();
			File[] batchFiles = fc.doFilePrompt(parentFrame, "Open batch Query File", FileChooser.LOAD_DIALOG,
					new File(main.getProperties().getProperty("lastDirectory", ".")), xmlFilter);

			if (batchFiles == null) {
				return;
			} else {
				main.getProperties().setProperty("lastDirectory", batchFiles[0].getParent());

				final FileFilter xlsFilter = (new javax.swing.filechooser.FileFilter() {
					public boolean accept(File f) {
						return f.getName().toLowerCase().endsWith(".xls") || f.getName().toLowerCase().endsWith(".csv")
								|| f.isDirectory();
					}

					public String getDescription() {
						return "Microsoft Excel File(*.xls) or CSV (*.csv)";
					}
				});
				File[] xlsFiles = fc.doFilePrompt(parentFrame, "Select Where to Save Output", FileChooser.SAVE_DIALOG,
						new File(main.getProperties().getProperty("lastDirectory", ".")), xlsFilter);
				if (xlsFiles == null) {
					return;
				} else {
					for (int i = 0; i < xlsFiles.length; ++i) {
						if (!xlsFiles[i].getName().endsWith(".xls") && !xlsFiles[i].getName().endsWith(".csv")) {
							xlsFiles[i] = new File(xlsFiles[i].getAbsolutePath() + ".xls");
						}
					}
					main.getProperties().setProperty("lastDirectory", xlsFiles[0].getParent());
					batchQuery(batchFiles[0], xlsFiles[0]);
				}
			}
		} else if (e.getActionCommand().equals("Export Tabs as CSVs")) {
			exportTabs();
			// YD edits second round, add "queriesLockMenu" and toggle its "lock" and
			// "unlock" functionality
		} else if (e.getActionCommand().equals("Lock Query Tree")) {
			queriesUpdateMenu.setEnabled(false);
			queriesEditMenu.setEnabled(false);
			queriesCreateMenu.setEnabled(false);
			queriesRemoveMenu.setEnabled(false);
			main.getSaveMenu().setEnabled(false);
			main.getSaveAsMenu().setEnabled(false);
			main.getUndoMenu().setEnabled(false);
			main.getRedoMenu().setEnabled(false);
			// YD edits, make the queriesDoc not editable, means cannot be dragged or moved
			// around
			// parentFrame.getContentPane().getTreeLock(); //YD edits, did not work
			queryTreeLocked = true;
			queriesLockMenu.setText("Unlock Query Tree");
		} else if (e.getActionCommand().equals("Unlock Query Tree")) {
			queriesUpdateMenu.setEnabled(true);
			queriesEditMenu.setEnabled(true);
			queriesCreateMenu.setEnabled(true);
			queriesRemoveMenu.setEnabled(true);
			queryTreeLocked = false;
			queriesLockMenu.setText("Lock Query Tree");

		} else if (e.getActionCommand().equals("Save")) {
			writeQueries();
		} else if (e.getActionCommand().equals("Save As")) {
			final FileFilter xmlFilter = new XMLFilter();
			FileChooser fc = FileChooserFactory.getFileChooser();
			File[] result = fc.doFilePrompt(parentFrame, null, FileChooser.SAVE_DIALOG,
					new File(main.getProperties().getProperty("queryFile", ".")), xmlFilter);
			if (result != null) {
				File file = result[0];
				if (file.getName().indexOf('.') == -1) {
					if (!(file.getAbsolutePath().endsWith(".xml"))) {
						file = new File(file.getAbsolutePath() + ".xml");
					}
				}
				if (!file.exists() || InterfaceMain.getInstance().showConfirmDialog("Overwrite existing file?",
						"Confirm Overwrite", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
						JOptionPane.YES_OPTION) == JOptionPane.YES_OPTION) {
					main.getProperties().setProperty("queryFile", file.getAbsolutePath());
					writeQueries();
				}
			}
			// YD edits second round, add "significantDigitsMenu" and toggle its "Disable"
			// and "Enable" functionality
		} else if (e.getActionCommand().equals("Disable 3 Significant Digits")) {
			significantDigitsMenu.setText("Enable 3 Significant Digits");
			disable3Digits = true;
			// TODO: add method to disable using 3 significant digits in the table
		} else if (e.getActionCommand().equals("Enable 3 Significant Digits")) {
			significantDigitsMenu.setText("Disable 3 Significant Digits");
			disable3Digits = false;
			// TODO: add method to enable using 3 significant digits in the table
		} else if (e.getActionCommand().equals("Disable Unit Conversions")) {
			enableUnitConversionsMenu.setText("Enable Unit Conversions");
			enableUnitConversions = false;
			// TODO: add method to disable using 3 significant digits in the table
		} else if (e.getActionCommand().equals("Enable Unit Conversions")) {
			enableUnitConversionsMenu.setText("Disable Unit Conversions");
			enableUnitConversions = true;
			// TODO: add method to enable using 3 significant digits in the table
		}
	}

	public void doOpenDB(File dbFile) {
		final InterfaceMain main = InterfaceMain.getInstance();
		final JFrame parentFrame = main.getFrame();
		main.getProperties().setProperty("lastDirectory", dbFile.getParent());
		// put up a wait cursor so that the user knows things are happening while the
		// database loads
		parentFrame.getGlassPane().setVisible(true);
		try {
			XMLDB.openDatabase(dbFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
			parentFrame.getGlassPane().setVisible(false);
			// tell the user it didn't open.
			InterfaceMain.getInstance().showMessageDialog("Could not open the xml database.", "Open DB Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		tablesTabs.setTransferHandler(new TableTransferHandler());
		TabDragListener dragListener = new TabDragListener();
		tablesTabs.addMouseListener(dragListener);
		tablesTabs.addMouseMotionListener(dragListener);

		createTableSelector();
		parentFrame.setTitle("GLIMPSE-ModelInterface [" + dbFile + "]");
	}
	
	
	public static Vector<ScenarioListItem> getScenarios() {
		return getScenarios(XMLDB.getInstance());
	}

	public static Vector<ScenarioListItem> getScenarios(XMLDB xmldb) {
		Vector<ScenarioListItem> ret = new Vector<ScenarioListItem>();
		QueryProcessor queryProc = xmldb.createQuery("/scenario", null, null, null);
		try {
			Iter res = queryProc.iter();
			ANode temp;
			while ((temp = (ANode) res.next()) != null) {
				BXNode tempNode = BXNode.get(temp);
				BXDoc doc = new BXDoc(temp.parent());
				String docName = "";
				try {
					docName = new File(new URI(doc.getDocumentURI())).getName();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Map<String, String> scnAttrMap = XMLDB.getAttrMap(tempNode);
				ret.add(new ScenarioListItem(docName, scnAttrMap.get("name"), scnAttrMap.get("date")));
			}
		} catch (Exception e) {
			System.out.println("Could not load database: "+e.toString());
			JOptionPane.showMessageDialog(null, "Could not load selected database, probably due to file corruption. Please review console for futher details.  All loading will stop.");
			return null;
		} finally {
			queryProc.close();
		}
		return ret;
	}

	public void resetScenarioList() {
		scns = getScenarios();
		scnList.setListData(scns);
	}

	protected Vector getRegions() {

		Vector funcTemp = new Vector<String>(1, 0);
		funcTemp.add("distinct-values");
		Vector ret = new Vector();
		QueryProcessor queryProc = XMLDB.getInstance().createQuery(
				"/scenario/world/" + ModelInterface.ModelGUI2.queries.QueryBuilder.regionQueryPortion + "/@name",
				funcTemp, null, null);
		try {
			Iter res = queryProc.iter();
			Item temp;
			while ((temp = res.next()) != null) {
				ret.add(temp.toJava());
			}
		} catch (QueryException e) {
			System.out.println("Error loading regions: "+e.toString());
			return null;
		} finally {
			queryProc.close();
		}
		ret.add("Global");
		return ret;
	}

	protected QueryTreeModel getQueries() {
		return new QueryTreeModel(queriesDoc.getDocumentElement());
	}

	JTree queryList = null;

	protected void createTableSelector() {
		JPanel listPane = new JPanel();
		JLabel listLabel;
		JPanel allLists = new JPanel();
		// final JSplitPane all = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		scns = getScenarios();
		regions = getRegions();
		queries = getQueries();

		scnList = new JList(scns);
		scnList.setName(SCENARIO_LIST_NAME);
		regionList = new JList(regions);
		regionList.setName(REGION_LIST_NAME);

		// TODO: get real icons
		final Icon queryIcon = new ImageIcon(TabCloseIcon.class.getResource("icons/group-query.png"));
		final Icon singleQueryIcon = new ImageIcon(TabCloseIcon.class.getResource("icons/single-query.png"));

		// filter="Transport";
		// filter=null;
		// if(filter!=null) {
		// queries=getFilteredQueries(queries,filter);

		// }

		// initialize the queries tree
		queryList = new JTree(queries);
		queryList.setTransferHandler(new QueryTransferHandler(queriesDoc, implls));
		queryList.setDragEnabled(true);
		queryList.getSelectionModel()
				.setSelectionMode(javax.swing.tree.TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		queryList.setSelectionRow(0);
		for (int i = 0; i < queryList.getRowCount(); ++i) {
			queryList.expandRow(i);
		}
		//Dan: testing some font options to avoid scaling problems; setup below works on EPA VM
		//queryList.setFont(queryList.getFont().deriveFont(14F));
		queryList.setRowHeight(queryList.getFont().getSize()+5);

		ToolTipManager.sharedInstance().registerComponent(queryList);
		queryList.setCellRenderer(new DefaultTreeCellRenderer() {
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
					boolean leaf, int row, boolean hasFocus) {
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				if (value instanceof QueryGenerator) {
					setToolTipText(createCommentTooltip(new TreePath(value)));
					setIcon(queryIcon);
				} else if (value instanceof SingleQueryExtension.SingleQueryValue) {
					Object[] tp = new Object[] { "root", // will be skipped
							((SingleQueryExtension.SingleQueryValue) value).getParent(), value };
					setToolTipText(createCommentTooltip(new TreePath(tp)));
					setIcon(singleQueryIcon);
				}
				return this;
			}
		});

		listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));
		listLabel = new JLabel("Scenario");
		listPane.add(listLabel);
		JScrollPane listScroll = new JScrollPane(scnList);
		listScroll.setPreferredSize(new Dimension(150, 150));
		listPane.add(listScroll);

		allLists.setLayout(new BoxLayout(allLists, BoxLayout.X_AXIS));
		allLists.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		scenarioRegionSplit.setLeftComponent(listPane);
		// allLists.add(listPane);
		// allLists.add(Box.createHorizontalStrut(10));

		listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));
		listLabel = new JLabel("Regions");
		listPane.add(listLabel);
		listScrollRegions = new JScrollPane(regionList); // YD edited,Feb-2024
		listScrollRegions.setPreferredSize(new Dimension(150, 100));// YD edited,2024 150 -> 110
		listPane.add(listScrollRegions);
		scenarioRegionSplit.setRightComponent(listPane);
		allLists.add(scenarioRegionSplit);
		// allLists.add(listPane);
		// allLists.add(Box.createHorizontalStrut(10));

		// YD added for Regions,Feb-2024
		// this method should be re-factored later
		loadRegionListToDropdown();
		// System.out.println("check preset region choices
		// first:"+preset_choices.toString());
		
		if(preset_choices!=null && preset_choices.length>0) {
			JPanel presetRegionsPanel = new JPanel();
			presetRegionsPanel.setLayout(new BoxLayout(presetRegionsPanel, BoxLayout.X_AXIS));
			presetRegionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			listLabel = new JLabel("Presets:");
			comboBoxPresetRegions = new JComboBox<String>(preset_choices);// YD added,Feb-2024
			comboBoxPresetRegions.setVisible(true);
			comboBoxPresetRegions.setMaximumSize(comboBoxPresetRegions.getPreferredSize());
			presetRegionsPanel.add(listLabel);
			presetRegionsPanel.add(comboBoxPresetRegions);
			listPane.add(presetRegionsPanel);
			
			// YD added this listener,Feb-2024
			comboBoxPresetRegions.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectPresetRegions();
				} // actionEvent end
			}); // comboBoxPresetRegions listener ends
		}

		// YD added end

		listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));
		listLabel = new JLabel("Queries");
		listPane.add(listLabel);
		listScrollQueries = new JScrollPane(queryList);
		listScrollQueries.setPreferredSize(new Dimension(150, 100));
		listPane.add(listScrollQueries);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);// YD added this line to make the buttons align to the left
		// YD commented lines 625,626,630,631,634,644-647 out because
		// "Create","Remove","Edit","Update Single Queries" options
		// need to be re-arranged to be under "Advanced" >> "Queries"
		// final JButton createButton = new JButton("Create");
		// final JButton removeButton = new JButton("Remove");
		final JButton runQueryButton = new JButton("Run Query");
		final JButton diffQueryButton = new JButton("Diff Query");
		// final JButton sumQueryButton = new JButton("Sum Query");
		// final JButton editButton = new JButton("Edit");
		// final JButton getSingleQueryButton = new JButton("Update Single Queries"); //
		// TODO: better name
		final JButton listCollapseButton = new JButton("Collapse"); // Dan
		final JCheckBox doTotalCheckBox = new JCheckBox("Total"); // Dan
		final JButton queryFilterButton = new JButton("Search"); // YD,2024
		final JButton favoriteQueryButton = new JButton("Favorites"); // YD added,Feb-2024
		
		
		queriesEditMenu.setEnabled(false); // YD added
		runQueryButton.setEnabled(false);
		// sumQueryButton.setEnabled(false);
		diffQueryButton.setEnabled(false);
		buttonPanel.add(runQueryButton);
		// buttonPanel.add(sumQueryButton);
		buttonPanel.add(diffQueryButton);
		buttonPanel.add(doTotalCheckBox);
		// buttonPanel.add(Box.createHorizontalGlue()); //YD commented it out because we
		// want "Collapse" button to be right next to "Total" checkbox
		buttonPanel.add(listCollapseButton);// Dan
		buttonPanel.add(queryFilterButton); // YD,2024
		buttonPanel.add(favoriteQueryButton); // YD added,Feb-2024
		//buttonPanel.add(jby);
		//buttonPanel.add(closeAllTabsButton);
		// buttonPanel.add(getSingleQueryButton);
		// buttonPanel.add(createButton);
		// buttonPanel.add(removeButton);
		// buttonPanel.add(editButton);
		listPane.add(buttonPanel);

		queriesSplit.setLeftComponent(scenarioRegionSplit);
		queriesSplit.setRightComponent(listPane);
		allLists.add(queriesSplit);
		// allLists.add(listPane);
		// all.setLayout( new BoxLayout(all, BoxLayout.Y_AXIS));
		// all.add(allLists, BorderLayout.PAGE_START);
		tableCreatorSplit.setLeftComponent(allLists);
		/*
		 * final JPanel tablePanel = new JPanel(); tablePanel.setLayout( new
		 * BoxLayout(tablePanel, BoxLayout.X_AXIS));
		 */
		// final JTabbedPane tablesTabs = new JTabbedPane();
		tableCreatorSplit.setRightComponent(tablesTabs);

		// I have to do this after the lists are in there scrollpanes so I
		// can ensure they are visible
		if (scns.size() != 0) {
			scnList.setSelectedIndex(scns.size() - 1);
			scnList.ensureIndexIsVisible(scns.size() - 1);
		}
		if (regions.size() != 0) {
			regionList.setSelectedIndex(0);
		}

		// YD added to save the initial tree expansion state, 2024
		expansionState = saveExpansionState(queryList);
		// TreePath treePath = expansionState.nextElement();
		// while (expansionState.hasMoreElements()) {
		// System.out.println(expansionState.nextElement());
		// } //while loop end

		queryList.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				boolean canEdit = true;
				boolean canRun = true;
				boolean canCreate = true;
				boolean canRemove = true;
				// YD edits,to lock all other editing actions if user clicked "Lock Query Tree"
				if (queryTreeLocked) {
					canEdit = false;
					canCreate = false;
					canRemove = false;
					queryList.setDragEnabled(false); // YD added to stop dragging
					queriesUpdateMenu.setEnabled(false);
					InterfaceMain.getInstance().getSaveMenu().setEnabled(false);
					InterfaceMain.getInstance().getSaveAsMenu().setEnabled(false);
				}
				if (queryList.getSelectionPaths() != null) {
					for (TreePath path : queryList.getSelectionPaths()) {
						Object selectedObj = path.getLastPathComponent();
						if (selectedObj instanceof QueryGenerator) {
							if (!((QueryGenerator) selectedObj).hasSingleQueryExtension()) {
								// only add the listeners the first time.
								SingleQueryExtension se = ((QueryGenerator) selectedObj).getSingleQueryExtension();
								// could be null if it is not to
								// build list
								// AP here is where we add advanced menu option
								se = null; // Dan added this so there would not be "Could not generate data" messages on
											// tree. To-do: Revisit in future.
								if (se != null) {
									queryList.addTreeSelectionListener(se);
									scnList.addListSelectionListener(se);
									regionList.addListSelectionListener(se);

									// make sure it doesn't miss this event
									Object[] selObjScen = scnList.getSelectedValues();
									ScenarioListItem[] selScenarios = new ScenarioListItem[selObjScen.length];
									System.arraycopy(selObjScen, 0, selScenarios, 0, selObjScen.length);

									Object[] selObjRegion = regionList.getSelectedValues();
									String[] selRegions = new String[selObjRegion.length];
									System.arraycopy(selObjRegion, 0, selRegions, 0, selObjRegion.length);
									se.setSelection(selScenarios, selRegions);

								}
							}
						} else if (selectedObj instanceof SingleQueryExtension.SingleQueryValue) {
							canEdit = false;
							if (!((SingleQueryExtension.SingleQueryValue) selectedObj).canExecute()) {
								canRun = false;
							}
							canCreate = false;
							canRemove = false;
						} else {
							canEdit = false;
							canRun = false;
						}
					}
				}
				// YD commented lines 780,782,784,785 out because "Create","Remove","Edit"
				// options are moved to be under "Advanced" >> "Queries"
				// editButton.setEnabled(canEdit);
				runQueryButton.setEnabled(canRun);
				// sumQueryButton.setEnabled(canRun);
				diffQueryButton.setEnabled(canRun);
				// createButton.setEnabled(canCreate);
				// removeButton.setEnabled(canRemove);
				// YD added these lines
				significantDigitsMenu.setEnabled(canRun);
				queriesEditMenu.setEnabled(canEdit);
				queriesCreateMenu.setEnabled(canCreate);
				queriesRemoveMenu.setEnabled(canRemove);
			}
		});
		queries.addTreeModelListener(new TreeModelListener() {
			public void treeNodesInserted(TreeModelEvent e) {
				// right now this is the only one I care about
				// so that I can set selection after a node is
				// inserted
				// YD edits, added logic to remind user that the query editing is locked
				if (queryTreeLocked) {
					InterfaceMain.getInstance().showMessageDialog(
							"Query tree is locked for editing,please unlock it first.", "Edit Query Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					if (!(e.getChildren()[0] instanceof SingleQueryExtension.SingleQueryValue)) {
						TreePath pathWithNewChild = e.getTreePath().pathByAddingChild(e.getChildren()[0]);
						queryList.setSelectionPath(pathWithNewChild);
						queryList.scrollPathToVisible(pathWithNewChild);
					} // inner if loop end
				} // outer if else loop end
			}

			public void treeNodesChanged(TreeModelEvent e) {
				// do nothing..
			}

			public void treeNodesRemoved(TreeModelEvent e) {
				// do nothing..
			}

			public void treeStructureChanged(TreeModelEvent e) {
				// do nothing..
			}
		});

		// YD commented lines 757-766 out because "Create" option is moved to be under
		// the dropdown menu "Advanced" >> "Queries"
		// TODO: this "createButton" ActionListener need to be implemented with the
		// "Create" menuItem
		/*
		 * createButton.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent e) { if (queryList.getSelectionCount() != 1) {
		 * InterfaceMain.getInstance().showMessageDialog(
		 * "Please select one Query or Query Group before creating",
		 * "Create Query Error", JOptionPane.ERROR_MESSAGE); return; }
		 * 
		 * QueryGenerator qg = new QueryGenerator(); if (qg.getXPath().equals("")) {
		 * return; } else if (qg.getXPath().equals("Query Group")) {
		 * queries.add(queryList.getSelectionPath(), qg.toString()); } else {
		 * queries.add(queryList.getSelectionPath(), qg); } queryList.updateUI(); } });
		 */

		// YD commented lines 772-779 out because "Remove" option is moved to be under
		// the dropdown menu "Advanced" >> "Queries"
		// TODO: this "removeButton" ActionListener need to be implemented with the
		// "Remove" menuItem
		/*
		 * removeButton.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent e) { if (queryList.getSelectionCount() == 0) {
		 * InterfaceMain.getInstance().
		 * showMessageDialog("Please select a Query or Query Group to Remove",
		 * "Query Remove Error", JOptionPane.ERROR_MESSAGE); } else { TreePath[]
		 * selPaths = queryList.getSelectionPaths(); for (int i = 0; i <
		 * selPaths.length; ++i) { queries.remove(selPaths[i]); } queryList.updateUI();
		 * } } });
		 */
		// YD added these lines for "queriesCreateMenu" and "queriesRemoveMenu"
		queriesCreateMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (queryList.getSelectionCount() != 1) {
					InterfaceMain.getInstance().showMessageDialog(
							"Please select one Query or Query Group before creating", "Create Query Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				QueryGenerator qg = new QueryGenerator();
				if (qg.getXPath().equals("")) {
					return;
				} else if (qg.getXPath().equals("Query Group")) {
					queries.add(queryList.getSelectionPath(), qg.toString());
				} else {
					queries.add(queryList.getSelectionPath(), qg);
				}
				queryList.updateUI();
			}
		});

		queriesRemoveMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (queryList.getSelectionCount() == 0) {
					InterfaceMain.getInstance().showMessageDialog("Please select a Query or Query Group to Remove",
							"Query Remove Error", JOptionPane.ERROR_MESSAGE);
				} else {
					TreePath[] selPaths = queryList.getSelectionPaths();
					for (int i = 0; i < selPaths.length; ++i) {
						queries.remove(selPaths[i]);
					}
					queryList.updateUI();
					// YD edits, to make "Save" and "Save As" enabled after removing something
					InterfaceMain.getInstance().getSaveMenu().setEnabled(true);
					InterfaceMain.getInstance().getSaveAsMenu().setEnabled(true);
					// YD edits, when no query is selected (after removed something) disable "Edit"
					// and "Remove"
					if (queryList.getSelectionCount() == 0) {
						queriesRemoveMenu.setEnabled(false);
						queriesEditMenu.setEnabled(false);
					}

				}
			}
		});

		runQueryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// read the file everytime, allows for dynamic changing of units.
				int[] scnSel = scnList.getSelectedIndices();
				int[] regionSel = regionList.getSelectedIndices();
				// Checks to make sure at least one scenario, region, and query has been
				// selected
				if (scnSel.length == 0) {
					InterfaceMain.getInstance().showMessageDialog("Please select Scenarios to run the query against",
							"Run Query Error", JOptionPane.ERROR_MESSAGE);
				} else if (regionSel.length == 0) {
					InterfaceMain.getInstance().showMessageDialog("Please select Regions to run the query against",
							"Run Query Error", JOptionPane.ERROR_MESSAGE);
				} else if (queryList.getSelectionCount() == 0) {
					InterfaceMain.getInstance().showMessageDialog("Please select a query to run", "Run Query Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					// selection criteria met
					// identifies path in query file to the selected query(ies)
					TreePath[] selPaths = queryList.getSelectionPaths();
					boolean movedTabAlready = false;
					// iterates over selected queries by path
					for (int i = 0; i < selPaths.length; ++i) {
						try {
							QueryGenerator qg = null;
							QueryBinding singleBinding = null;
							if (selPaths[i].getLastPathComponent() instanceof QueryGenerator) {
								qg = (QueryGenerator) selPaths[i].getLastPathComponent();
							} else {
								singleBinding = ((SingleQueryExtension.SingleQueryValue) selPaths[i]
										.getLastPathComponent()).getAsQueryBinding();
								qg = (QueryGenerator) selPaths[i].getParentPath().getLastPathComponent();
							}
							// add loading icon to QueryResultsPanel
							TabCloseIcon loadingIcon = new TabCloseIcon(tablesTabs);
							// creating new panel for holding the results of the queries
							JComponent ret = new QueryResultsPanel(qg, singleBinding, scnList.getSelectedValues(),
									regionList.getSelectedValues(), loadingIcon, doTotalCheckBox.isSelected());

							// adds new tab for query results panel
							tablesTabs.addTab(qg.toString(), loadingIcon, ret, createCommentTooltip(selPaths[i]));
							if (!movedTabAlready) {
								tablesTabs.setSelectedIndex(tablesTabs.getTabCount() - 1);
								movedTabAlready = true;
							}

						} catch (ClassCastException cce) {
							System.out.println("Warning: Caught " + cce + " likely a QueryGroup was in the selection");
						}
					}
					// need old value/new value?
					// fire off property or something we did query
				}
			}

		});

		diffQueryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] scnSel = scnList.getSelectedIndices();
				int[] regionSel = regionList.getSelectedIndices();
				// Checks to make sure at least one scenaro, region, and query has been selected
				if (scnSel.length == 0) {
					InterfaceMain.getInstance().showMessageDialog("Please select Scenarios to run the query against",
							"Run Query Error", JOptionPane.ERROR_MESSAGE);
				} else if (regionSel.length == 0) {
					InterfaceMain.getInstance().showMessageDialog("Please select Regions to run the query against",
							"Run Query Error", JOptionPane.ERROR_MESSAGE);
				} else if (queryList.getSelectionCount() == 0) {
					InterfaceMain.getInstance().showMessageDialog("Please select a query to run", "Run Query Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					// selection criteria met
					// identifies path in query file to the selected query(ies)
					TreePath[] selPaths = queryList.getSelectionPaths();
					boolean movedTabAlready = false;
					// iterates over selected queries by path
					for (int i = 0; i < selPaths.length; ++i) {
						try {
							QueryGenerator qg = null;
							QueryBinding singleBinding = null;
							if (selPaths[i].getLastPathComponent() instanceof QueryGenerator) {
								qg = (QueryGenerator) selPaths[i].getLastPathComponent();
							} else {
								singleBinding = ((SingleQueryExtension.SingleQueryValue) selPaths[i]
										.getLastPathComponent()).getAsQueryBinding();
								qg = (QueryGenerator) selPaths[i].getParentPath().getLastPathComponent();
							}
							// add loading icon to QueryResultsPanel
							TabCloseIcon loadingIcon = new TabCloseIcon(tablesTabs);
							// creating new panel for holding the results of the queries
							JComponent ret = new DiffResultsPanel(qg, singleBinding, scnList.getSelectedValues(),
									regionList.getSelectedValues(), loadingIcon, doTotalCheckBox.isSelected());
							// adds new tab for query results panel
							tablesTabs.addTab("Diff: " + qg.toString(), loadingIcon, ret,
									createCommentTooltip(selPaths[i]));
							if (!movedTabAlready) {
								tablesTabs.setSelectedIndex(tablesTabs.getTabCount() - 1);
								movedTabAlready = true;
							}

						} catch (ClassCastException cce) {
							System.out.println("Warning: Caught " + cce + " likely a QueryGroup was in the selection");
						}
					}
					// need old value/new value?
					// fire off property or something we did query
				}
			}
		});

		// YD commented lines 894-901 out because "Edit" option is moved to be under the
		// dropdown menu "Advanced" >> "Queries"
		// TODO: this "editButton" ActionListener need to be implemented with the "Edit"
		// menuItem
		/*
		 * editButton.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent e) { if (queryList.getSelectionCount() == 0) {
		 * InterfaceMain.getInstance().showMessageDialog("Please select a query to edit"
		 * , "Edit Query Error", JOptionPane.ERROR_MESSAGE); } else { TreePath[]
		 * selPaths = queryList.getSelectionPaths(); for (int i = 0; i <
		 * selPaths.length; ++i) { // QueryGenerator tempQG =
		 * (QueryGenerator)selPaths[i].getLastPathComponent(); // tempQG.editDialog();
		 * queries.doEdit(selPaths[i]); } } } });
		 */

		// YD commented lines 907-914 out because "Update Single Queries" option is
		// moved to be under the dropdown menu "Advanced" >> "Queries"
		// TODO: this "getSingleQueryButton" ActionListener need to be implemented with
		// the "Update Single Queries" menuItem
		/*
		 * getSingleQueryButton.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent e) { queryList.setSelectionRow(0);
		 * List<QueryGenerator> qgList = new ArrayList<QueryGenerator>(); for (int row =
		 * 0; row < queryList.getRowCount(); ++row) { TreePath currPath =
		 * queryList.getPathForRow(row); if (currPath.getLastPathComponent() instanceof
		 * QueryGenerator) { qgList.add((QueryGenerator)
		 * currPath.getLastPathComponent()); } } createAndShowGetSingleQueries(qgList,
		 * scns, regions); } });
		 */

		// YD added these lines for "queriesEditMenu" and "queriesUpdateMenu"

		queriesEditMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (queryList.getSelectionCount() == 0) {
					InterfaceMain.getInstance().showMessageDialog("Please select a query to edit", "Edit Query Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					TreePath[] selPaths = queryList.getSelectionPaths();
					for (int i = 0; i < selPaths.length; ++i) {
						// QueryGenerator tempQG = (QueryGenerator)selPaths[i].getLastPathComponent();
						// tempQG.editDialog();
						queries.doEdit(selPaths[i]);
					} // for loop end
				} // inner if else loop end
			} // actionPerformed end
		});

		queriesUpdateMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queryList.setSelectionRow(0);
				List<QueryGenerator> qgList = new ArrayList<QueryGenerator>();
				for (int row = 0; row < queryList.getRowCount(); ++row) {
					TreePath currPath = queryList.getPathForRow(row);
					if (currPath.getLastPathComponent() instanceof QueryGenerator) {
						qgList.add((QueryGenerator) currPath.getLastPathComponent());
					}
				}
				createAndShowGetSingleQueries(qgList, scns, regions);
			}
		});

		listCollapseButton.addActionListener(new ActionListener() { // Dan
			public void actionPerformed(ActionEvent e) {

				// System.out.println("expand!");

				QueryTreeModel model = (QueryTreeModel) queryList.getModel();
				QueryGroup root = (QueryGroup) model.getRoot();

				ArrayList query_list = root.getQueryList();
				System.out.println(query_list); // YD added
				System.out.println("check allCollapsed now:" + AllCollapsed);

				for (int i = 0; i < query_list.size(); i++) {

					if (query_list.get(i) instanceof QueryGroup) {
						QueryGroup group = (QueryGroup) query_list.get(i);
						System.out.println(group.getName()); // YD uncommented it
						// YD added,2024
						System.out.println(queryList.getRowCount());
						ArrayList sub_query_list = group.getQueryList();
						System.out.println(sub_query_list); // YD added
						System.out.println(queryList.isCollapsed(1)); // YD added
						System.out.println(queryList.isCollapsed(2)); // YD added
						System.out.println(queryList.isCollapsed(3)); // YD added
						TreePath path = getTreePathFromNode(group);

						// This does not work corrected, but will collapse tree fully
						if (!queryList.isCollapsed(3)) {
							queryList.collapseRow(3);
						} else if (!queryList.isCollapsed(2)) {
							queryList.collapseRow(2);
						} else if (!queryList.isCollapsed(1)) {
							queryList.collapseRow(1);
						} else {
							// eventually will want to make this expand to full
							// YD added, 2024
							// YD added, need to check if all groups are collapsed
							System.out.println(queryList.getRowCount());
							AllCollapsed = true;
						} // inner if else loop end

//						if (queryList.isCollapsed(1)) {
//							queryList.expandPath(path);
//						} else {
//							queryList.collapsePath(path);
//						}

						// Dan: Note: I was unable to get the tree to collapse at level 2 instead of 1.
						// Worth reinvestigating.
//						ArrayList query_list2=group.getQueryList();
//						
//						for (int j=0;j<query_list2.size();j++) {
//							
//							if (query_list2.get(j) instanceof QueryGroup) {
//								QueryGroup group2=(QueryGroup)query_list2.get(j);	
//								//System.out.println(group2.getName());
//								
//								
//								
//								TreePath path=getTreePathFromNode(group2);
//								queryList.collapsePath(path);
//															
//							}
//										
//						}
//						

					} // outer if loop end

				} // outer for loop end
				System.out.println("has changed?" + model.hasChanges());
				model.fireTreeStructureChanged(e, null, null, null);
				queryList.updateUI();

				// YD added this to restore the initial expansion State
				if (AllCollapsed) {
					System.out.println("inside restore expansionState now."); // YD added
					restoreExpansionState(expansionState, queryList);
				}

			} // actionPerformed end
		}); // listener end

		// YD added,2024
		queryFilterButton.addActionListener(new ActionListener() { // YD,2024
			public void actionPerformed(ActionEvent e) {

				JPanel box = new JPanel();
				box.setPreferredSize(new Dimension(400, 50));
				box.setBackground(Color.GRAY);
				box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
				JLabel keyLabel = new JLabel("Please type the filtering text here:");
				keyLabel.setFont(new Font("Arial", Font.BOLD, 16));
				keyLabel.setForeground(Color.white);
				JTextField field = new JTextField(20);
				box.add(keyLabel);
				box.add(field);

				// int result = JOptionPane.showConfirmDialog(null, box, "Query Filter",
				// JOptionPane.OK_CANCEL_OPTION,
				// JOptionPane.PLAIN_MESSAGE);

				String[] buttons = { "Apply", "Clear", "Cancel" };
				int returnValue = JOptionPane.showOptionDialog(null, box, "Query Filter",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
				// System.out.println(returnValue);
				queries = getQueries();
				switch (returnValue) {
				case 0:
					// YD added,2024
					// Process the filter results...

					filteringText = field.getText();
					if (filteringText != null && filteringText.length() > 0) {
						queries = getFilteredQueries(queries, filteringText);

					}

					// queryList.updateUI();
					/*
					 * QueryTreeModel model = (QueryTreeModel) queryList.getModel();
					 * 
					 * QueryGroup root = (QueryGroup) model.getRoot(); ArrayList query_list =
					 * root.getQueryList();
					 * 
					 * for (int i = 0; i < query_list.size(); i++) {
					 * 
					 * if (query_list.get(i) instanceof QueryGroup) { QueryGroup group =
					 * (QueryGroup) query_list.get(i);
					 * 
					 * System.out.println("this querygroup is:" + group);
					 * 
					 * ArrayList sub_query_list = group.getQueryList();
					 * System.out.println("these are inside the sub_query_list:");
					 * System.out.println(sub_query_list.toString());
					 * System.out.println("the size for the sub_query_list is:" +
					 * sub_query_list.size()); int leafCountLevel1 = 0; for (int j = 0; j <
					 * sub_query_list.size(); j++) { if (sub_query_list.get(j) instanceof
					 * QueryGroup) { QueryGroup subGroup = (QueryGroup) sub_query_list.get(j);
					 * System.out.println("the subGroup name is:" + subGroup.getName()); boolean
					 * noMoreGroupInside = checkNoMoreQueryGroupInside(subGroup);
					 * System.out.println( "check if more query group inside this subGroup:" +
					 * noMoreGroupInside); if (noMoreGroupInside) { // when all leaves inside
					 * collapseGroupWhenNotContainKeyWords(queryList, subGroup, filteringText); }
					 * else { // when 'noMoreGroupInside' == false ArrayList sub_2_query_list =
					 * subGroup.getQueryList(); for (int j2 = 0; j2 < sub_2_query_list.size(); j2++)
					 * { if (sub_2_query_list.get(j2) instanceof QueryGroup) { QueryGroup sub2Group
					 * = (QueryGroup) sub_2_query_list.get(j2);
					 * System.out.println("the sub2Group name is:" + sub2Group.getName()); boolean
					 * noMoreGroupInside2 = checkNoMoreQueryGroupInside(sub2Group);
					 * System.out.println( "check if more query group inside this level 2 subGroup:"
					 * + noMoreGroupInside2); if (noMoreGroupInside2) {
					 * collapseGroupWhenNotContainKeyWords(queryList, sub2Group, filteringText); }
					 * else { // when 'noMoreGroupInside2' ==false ArrayList sub_3_query_list =
					 * sub2Group.getQueryList(); for (int j3 = 0; j3 < sub_3_query_list.size();
					 * j3++) { if (sub_3_query_list.get(j3) instanceof QueryGroup) { QueryGroup
					 * sub3Group = (QueryGroup) sub_3_query_list .get(j3); System.out.println(
					 * "the sub3Group name is:" + sub3Group.getName()); boolean noMoreGroupInside3 =
					 * checkNoMoreQueryGroupInside( sub3Group); if ((noMoreGroupInside3)) {
					 * collapseGroupWhenNotContainKeyWords(queryList, sub3Group, filteringText); } }
					 * // if 'sub_3_query_list.get()' a query group end } // for 'sub_3_query_list'
					 * loop end } // if else 'noMoreGroupInside2' end } // if 'sub_2_query_list'
					 * loop end } // for 'sub_2_query_list' loop end } // if else
					 * 'noMoreGroupInside' loop end } else if (sub_query_list.get(j) instanceof
					 * QueryGenerator) { QueryGenerator queryLeaf = (QueryGenerator)
					 * sub_query_list.get(j); System.out.println("found a query leaf at level 1:" +
					 * sub_query_list.get(j)); boolean leafContainKeyWords =
					 * queryLeaf.toString().contains(filteringText);
					 * System.out.println("this leaf contains my keywords:" + leafContainKeyWords);
					 * if (!leafContainKeyWords) { leafCountLevel1 = leafCountLevel1 + 1; // need to
					 * figure out how to set a leaf invisible }
					 * 
					 * } // inner if loop for 'sub_query_list' end
					 * 
					 * } // for loop for 'sub_query_list' end
					 * 
					 * // if everything under this group are leaves and none of them contain the key
					 * // words if (leafCountLevel1 == sub_query_list.size()) { System.out.println(
					 * "this group only have leaves that don't contain key words:" +
					 * group.getName()); TreePath groupTreePath = getFullTreePath(queryList,
					 * group.getName()); queryList.collapsePath(groupTreePath); }
					 * 
					 * } else if (query_list.get(i) instanceof QueryGenerator) {
					 * System.out.println("found a query leaf at root:" + query_list.get(i));
					 * QueryGenerator queryLeafAtRoot = (QueryGenerator) query_list.get(i); boolean
					 * leafAtRootContainKeyWords =
					 * queryLeafAtRoot.toString().contains(filteringText);
					 * System.out.println("this leaf contains my keywords:" +
					 * leafAtRootContainKeyWords); if (!leafAtRootContainKeyWords) { // need to
					 * figure out how to set a leaf invisible
					 * 
					 * } } // outer if else loop for 'query_list.get(i)' end } // for loop for
					 * 'query_list' end
					 */

					break;
				case 2:
				case -1:
					return;
				// case 'OK_OPTION' end here
				} // switch 'result' end
				queryList.setModel(queries);
				queryList.setSelectionRow(0);
				for (int i = 0; i < queryList.getRowCount(); ++i) {
					queryList.expandRow(i);
				}
			} // actionEvent end
		}); // queryFilterButton listener ends

		

		// YD added this listener,Feb-2024
		favoriteQueryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectFavoriteQueries(queryList);
			}
		}); // favoriteQueryButton listener ends
		
		
		
		

		// YD added this listener,Feb-2024
		createFavoritesMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createFavoriteQueriesFile(queryList);

			}
		});// createFavoritesMenu listener ends

		loadFavoritesMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadFavoriteQueriesFile();

			}
		});// loadFavoritesMenu listener ends

		// YD added this listener,Mar-2024
		appendFavoritesMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				appendFavoriteQueries(queryList);

			}
		});// appendFavoritesMenu listener ends

		JFrame parentFrame = InterfaceMain.getInstance().getFrame();
		Container contentPane = parentFrame.getContentPane();
		contentPane.add(tableCreatorSplit);

		// have to get rid of the wait cursor
		parentFrame.getGlassPane().setVisible(false);

		parentFrame.setVisible(true);
	}

	private QueryTreeModel getFilteredQueries(QueryTreeModel model, String query) {

		QueryGroup root = (QueryGroup) model.getRoot();
		ArrayList query_list = root.getQueryList();

		ArrayList toRemove = new ArrayList<>();

		for (int i = 0; i < query_list.size(); i++) {
			if (query_list.get(i) instanceof QueryGroup) {
				QueryGroup group = (QueryGroup) query_list.get(i);
				toRemove.addAll(getMatchingNodes(group, query));
			}
		}

		for (int i = 0; i < toRemove.size(); i++) {
			// boolean removed=query_list.remove(toRemove.get(i));
			QueryGenerator qg = (QueryGenerator) toRemove.get(i);
			Node myNode = qg.getMyNode();
			Node result = qg.getMyNode().getParentNode().removeChild(myNode);
			// System.out.println("HI "+result);
		}

		return model;
	}

	private ArrayList getMatchingNodes(QueryGroup groupTop, String query) {
		ArrayList query_list = groupTop.getQueryList();
		ArrayList toRemove = new ArrayList();
		for (int i = 0; i < query_list.size(); i++) {
			if (query_list.get(i) instanceof QueryGroup) {
				QueryGroup group = (QueryGroup) query_list.get(i);
				getMatchingNodes(group, query);
			} else {
				if (query_list.get(i).toString().toLowerCase().contains(query.toLowerCase())) {
					// System.out.println("found a match: " + query_list.get(i).toString());
				} else {
					QueryGenerator queryLeaf = (QueryGenerator) query_list.get(i);
					toRemove.add(query_list.get(i));
				}
			}
		}
		for (int i = 0; i < toRemove.size(); i++) {
			boolean gone = query_list.remove(toRemove.get(i));

		}
		return toRemove;
	}

	// YD added,2024
	public static Enumeration<TreePath> saveExpansionState(JTree tree) {
		return tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot()));
	}

	// YD added,2024
	public void restoreExpansionState(Enumeration enumeration, JTree tree) {
		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				TreePath treePath = (TreePath) enumeration.nextElement();
				tree.expandPath(treePath);
			}
		}
	}

	// YD added,2024
	private void collapseGroupWhenNotContainKeyWords(JTree myTree, QueryGroup mySubGroup, String keyWords) {
		ArrayList leaf_inside = mySubGroup.getQueryList();
		boolean containKeyWords = leaf_inside.toString().contains(filteringText);
		if (!containKeyWords) {
			TreePath myTreePath = getTreePathFromNode(mySubGroup);
			TreePath myFullTreePath = getFullTreePath(myTree, mySubGroup.getName());
			myTree.collapsePath(myFullTreePath);
		} else {
			int myIndex = leaf_inside.toString().indexOf(filteringText);
			// get index, but don't know how to set others invisible
		} // inner if else loop end
	}

	// YD added,2024
	// this method is to get the full tree path for a query group name,YD added
	private TreePath getFullTreePath(JTree tree, String groupName) {
		Enumeration<TreePath> allPath = tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot()));
		TreePath myTreePath = null;
		if (allPath != null) {
			while (allPath.hasMoreElements()) {
				TreePath treePath = (TreePath) allPath.nextElement();
				String treePathStr = treePath.toString();
				String[] splitsTreePath = treePathStr.replace("[", "").replace("]", "").split(",");
				String currentGroup = splitsTreePath[splitsTreePath.length - 1];
				// handle when "," within groupName such as 'Markets, prices, and costs' first
				boolean groupNameHasComma = groupName.contains(",");
				if (groupNameHasComma && treePath.toString().contains(groupName)) {
					myTreePath = treePath;
				} else if (currentGroup.trim().equalsIgnoreCase(groupName)) {
					myTreePath = treePath;
				}
				if (myTreePath != null) {
					break;
				}
			}
		}
		return (myTreePath);
	}

	// this method is to get the full tree path for a query group name
	// considering that the same query group name can appear multiple times in the
	// same tree
	// but at different locations,YD added
	public static ArrayList<TreePath> getFullTreePath2(JTree tree, String groupName, int pathCount) {
		Enumeration<TreePath> allPath = tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot()));
		ArrayList<TreePath> myTreePath = new ArrayList<TreePath>();
		if (allPath != null) {
			while (allPath.hasMoreElements()) {
				TreePath treePath = (TreePath) allPath.nextElement();
				String treePathStr = treePath.toString();
				String[] splitsTreePath = treePathStr.replace("[", "").replace("]", "").split(",");
				String currentGroup = splitsTreePath[splitsTreePath.length - 1];
				int checkPathCount = treePath.getPathCount();
				// handle when "," within groupName such as 'Markets, prices, and costs' first
				boolean groupNameHasComma = groupName.contains(",");
				if (groupNameHasComma && treePath.toString().contains(groupName)) {
					myTreePath.add(treePath);
				} else if (checkPathCount == pathCount & currentGroup.trim().equals(groupName)) {
					System.out.println("found for Sankey diagrams group:"+treePathStr);
					myTreePath.add(treePath);
				}
			}
		}
		return (myTreePath);
	}

	// YD added,2024
	// this method is to check if there are more levels of query group under a query
	// group
	private boolean checkNoMoreQueryGroupInside(QueryGroup mySubGroup) {
		boolean noMoreGroup = true;
		ArrayList group_inside = mySubGroup.getQueryList();
		for (int j = 0; j < group_inside.size(); j++) {
			if (group_inside.get(j) instanceof QueryGroup) {
				noMoreGroup = false;
				break;
			}
		} // for loop end
		return (noMoreGroup);
	}

	// YD added, Feb-2024
	// this method is to load the preset region list from the control file to the
	// dropdown menu
	public void loadRegionListToDropdown() {
		// String preset_region_list_filename =
		// "C:/Users/yxu01/glimpse/src/glimpse/ORDModelInterface/exe/"+"preset_region_list.txt";
		
		if(InterfaceMain.presetRegionListLocation==null || InterfaceMain.presetRegionListLocation.trim().length()==0) {
			System.out.println("No regions list specified, skipping.");
			return;
		}
		
		String preset_region_list_filename = InterfaceMain.presetRegionListLocation;

		
		// System.out.println(
		// "inside loadRegionListToDropdown now,check the preset region filename: " +
		// preset_region_list_filename);

		// preset region list
		try {
			ArrayList<String> contents = getStringArrayFromFile(preset_region_list_filename, "#");

			for (int i = 0; i < contents.size(); i++) {
				String line = contents.get(i);
				if (line.length() > 0) {
					preset_region_list.add(line);
				}
			}
		} catch (Exception e) {
			/*
			 * String[] strs = {
			 * "North America:USA,Canada,Mexico,Central America and Caribbean",
			 * "South America:Argentina,Brazil,Colombia,South America_Northern,South America_Southern"
			 * , "Africa:Africa_Northern,Africa_Southern,Africa_Eastern,Africa_Western",
			 * "EU:EU-15,EU-12",
			 * "Europe:EU-15,EU-12,Europe_Eastern,European Free Trade Association,Europe_Non_EGU"
			 * ,
			 * "Asia:Japan,Central Asia,Russia,China,Middle East,Indonesia,Pakistan,South Asia,Southeast Asia,Taiwan,South Korea,India"
			 * , "East Asia:Japan,China,Taiwan,South Korea",
			 * "Southeast Asia:Indonesia,Southeast Asia",
			 * "South Asia:Pakistan,India,South Asia", "West Asia:Middle East" }; for
			 * (String s : strs) preset_region_list.add(s);
			 */

			System.out.println("Unable to load region list: " + e.toString());
			//JOptionPane.showMessageDialog(null, "Unable to load regions file, please see console for error",
			//		"Error Saving File", JOptionPane.ERROR_MESSAGE);

		} // try-catch-end

		preset_choices = new String[preset_region_list.size() + 1];
		preset_choices[0] = "Select (optional)";
		for (int i = 0; i < preset_region_list.size(); i++) {
			String s = preset_region_list.get(i);
			int index_of_semicolon = s.indexOf(":");
			if (index_of_semicolon > -1) {
				s = s.substring(0, index_of_semicolon);
				preset_choices[i + 1] = s;
			}
		}

	} // "loadRegionListToDropdown()" method end

	public void closeAllTabs() {
		//need to disable the export tabs option
		menuExpPrn.setEnabled(false);
		
		//grab the panel
		if (tablesTabs.getTabCount() == 0) {
			//noting to do
			return;
		}
		//iterate over children
		tablesTabs.removeAll();
		//close each one
	}
	
	public void closeAllWindows() {
		//need to disable the export tabs option
		//menuExpPrn.setEnabled(false);
		
		//grab the panel
		if (openWindows.size() == 0) {
			//noting to do
			return;
		}
		//iterate over children
		for(Object o:openWindows) {
			//cast it
			if(o instanceof JDialog) {
				JDialog win=(JDialog)o;
				win.dispose();
			}
			if(o instanceof JFrame) {
				JFrame instance=(JFrame)o;
				instance.dispose();
			}
			//close it
		}
		//clear out list
		openWindows.clear();
		
	}
	
	// YD added this method,Feb-2024
	public void selectFavoriteQueries(JTree queryList) {

		ArrayList<String> favorite_query_list = new ArrayList<String>();// YD added,Feb-2024
		ArrayList<TreePath> favorite_query_treePath = new ArrayList<TreePath>();// YD added,Feb-2024
		ArrayList<String> favorite_query_names = new ArrayList<String>();// YD added,Feb-2024

		String favorite_query_filename = InterfaceMain.favoriteQueriesFileLocation;
		// preset region list
		try {
			ArrayList<String> contents = getStringArrayFromFile(favorite_query_filename, "#");

			for (int i = 0; i < contents.size(); i++) {
				String line = contents.get(i);
				if (line.length() > 0) {
					favorite_query_list.add(line);
				}
			}
		} catch (Exception e) {
			System.out.println("Could not read favorite queries file: " + e.toString());
			JOptionPane.showMessageDialog(null, "Unable to load favorites file, please see console for error",
					"Error Saving File", JOptionPane.ERROR_MESSAGE);
			return;
		} // try-catch-end

		for (int i = 0; i < favorite_query_list.size(); i++) {
			String line = favorite_query_list.get(i);
			TreePath myPath = getTreePathForEachLine(queryList, line);
			if (myPath != null) {
				favorite_query_treePath.add(myPath);
				// System.out.println("this is the treePath I found for this line: " + myPath);
			} else {
				System.out.println("Unable to find path for " + myPath);
			}
			String[] splitLine = line.split(">");
			String favoriteQueryName = splitLine[splitLine.length - 1];
			favorite_query_names.add(favoriteQueryName);
		}

		// TreePath[] arr = new TreePath[favorite_query_treePath.size()];
		// arr = favorite_query_treePath.toArray(arr);
		// queryList.setSelectionPaths(arr);

		QueryTreeModel model = (QueryTreeModel) queryList.getModel();

		ArrayList<String> notFound = new ArrayList<>();

		QueryGroup root = (QueryGroup) model.getRoot();
		ArrayList query_list = root.getQueryList();
		int[] rowsToSelect = new int[favorite_query_list.size()];
		for (int n = 0; n < favorite_query_list.size(); n++) {
			TreePath treePath_to_find = favorite_query_treePath.get(n);
			String leaf_name_to_find = favorite_query_names.get(n);
			int rowNum = getRowNumberForLeaf(queryList, treePath_to_find, leaf_name_to_find);
			rowsToSelect[n] = rowNum;
			if (rowNum == -1) {
				notFound.add(leaf_name_to_find);

			}
		} // for n< favorite_query_list end

		// if none of rows were found, no reason to update selection
		if (notFound.size() != rowsToSelect.length) {
			queryList.clearSelection();
			queryList.setSelectionRows(rowsToSelect);

			// scroll to the last found one of my favorite query
			for (int i = 0; i < rowsToSelect.length; i++) {
				if (rowsToSelect[i] != -1) {
					java.awt.Rectangle bounds = queryList.getRowBounds(rowsToSelect[i]);
					listScrollQueries.getVerticalScrollBar().setValue((int) bounds.getMinY());
				}
			}
		}
		if (notFound.size() > 0) {
			String errorMessage = "The following queries were not found:\n\n";
			for (String s : notFound) {
				errorMessage += s + "\n";
			}
			JOptionPane.showMessageDialog(null, errorMessage);
		}

	}// selectFavoriteQueries method end

	// YD added,Feb-2024, this method is adopted from "GLIMPSEFiles.java"
	public ArrayList<String> getStringArrayFromFile(String filename, String commentChar) throws IOException {
		ArrayList<String> arrayList = new ArrayList<String>();

		BufferedReader br = new BufferedReader(new FileReader(filename));
		for (String line; (line = br.readLine()) != null;) {
			line = line.trim();
			if (line.length() > 0) {
				if (commentChar != null && !line.startsWith(commentChar)) {
					arrayList.add(line);
				}
			}
		}
		br.close();

		return arrayList;
	}

	// YD added,Feb-2024, this method is adopted from "GLIMPSEUtils.java"
	public ArrayList<String> createArrayListFromString(String line, String delim) {
		ArrayList<String> linesList = new ArrayList<String>();
		String[] lines = splitString(line, delim);
		for (int i = 0; i < lines.length; i++) {
			lines[i] = lines[i];// + vars.getEol();
			linesList.add(lines[i]);
		}
		return linesList;
	}

	// YD added,Feb-2024, this method is adopted from "GLIMPSEUtils.java"
	public String[] splitString(String str, String delim) {
		String s[] = str.split(delim);
		return s;
	}

	// YD added,Feb-2024, this method is to find out what sub-regions should be
	// selected in regionList
	// then set those sub-regions selected in the regionList
	public void selectPresetRegions() {
		boolean verbose=false;
		String selection = (String) comboBoxPresetRegions.getSelectedItem();
		int idx = comboBoxPresetRegions.getSelectedIndex();
		if (verbose) System.out.println("this is my selection: " + selection);
		if (idx > 0) {// YD added,Apr-2024
			for (int i = 0; i < preset_region_list.size(); i++) {
				String line = preset_region_list.get(i);
				int index = line.indexOf(":");
				if (index > 0) {
					String name = line.substring(0, index).toLowerCase();
					if (selection.toLowerCase().equals(name)) {
						String[] subregions = splitString(line.substring(index + 1), ",");
						if (verbose) System.out.println("number of items in this subregion is: " + subregions.length);
						selectItemsFromRegionList(subregions);
					}
				}
			} // for loop end
		} else { // YD added,Apr-2024
			regionList.setSelectedIndex(0);
		} // outer if else loop end
	}// selectPresetRegions method end

	// YD added,Feb-2024, this method is to select those sub-regions from the above
	// regionList
	public void selectItemsFromRegionList(String[] subregions) {

		ArrayList<Integer> regionIndices = new ArrayList<Integer>();

		for (int i = 0; i < subregions.length; i++) {

			for (int j = 0; j < regions.size(); j++) {

				String st_str = subregions[i].trim();
				String regionName = regions.get(j).toString();
				if (st_str.equals(regionName)) {
					regionIndices.add(j);
				}
			}
		}

		int[] regionIndicesArray = new int[regionIndices.size()];
		for (int n = 0; n < regionIndices.size(); n++) {
			regionIndicesArray[n] = regionIndices.get(n);
		}
		// set selected sub-regions in the regionList
		if (regionIndicesArray.length > 0) {
			regionList.setSelectedIndices(regionIndicesArray);
			// scroll to the selected items
			java.awt.Rectangle bounds = regionList.getCellBounds(regionIndicesArray[0],
					regionIndicesArray[regionIndices.size() - 1]);
			listScrollRegions.getVerticalScrollBar().setValue((int) bounds.getMinY());
		}
	} // selectItemsFromRegionList method end

	// YD added,Feb-2024, this method is to get the row number for a leaf under a
	// query group
	public static int getRowNumberForLeaf(JTree myTree, TreePath myPath, String leafName) {
		int rowNumForLeaf = -1;
		int rowNumForSubgroup = myTree.getRowForPath(myPath);
		QueryGroup myChildGroup = (QueryGroup) myPath.getLastPathComponent();
		ArrayList leaves = myChildGroup.getQueryList();
		for (int m = 0; m < leaves.size(); m++) {
			// if (leafName.contains(leaves.get(m).toString())) {
			if (leafName.replace("\"", "").trim().compareToIgnoreCase(leaves.get(m).toString().trim()) == 0) {
				// System.out.println("found my favorite query name here:" +
				// leaves.get(m).toString());
				int myIndex = ((TreeModel) myTree.getModel()).getIndexOfChild(myChildGroup, leaves.get(m));
				rowNumForLeaf = rowNumForSubgroup + myIndex + 1;
				break;
			}
		} // for loop end
		return rowNumForLeaf;
	} // get RowNumberForLeaf method end

	// YD added,Feb-2024, this method is to get the full tree path for each line in
	// the favorite query list
	private TreePath getTreePathForEachLine(JTree myTree, String myLine) {
		TreePath theFullPath = null;
		String[] splitLine = myLine.split(">");
		String[] splitLineForGroups = Arrays.copyOfRange(splitLine, 0, splitLine.length - 1);
		String child_group_to_find = splitLine[splitLine.length - 2]; // get the last child group before the leaf
		String group_to_find_trim = child_group_to_find.substring(1, child_group_to_find.length() - 1); // remove double
																										// quotes
		int pathCount = splitLine.length - 1;
		ArrayList<TreePath> allPaths = getFullTreePath2(myTree, group_to_find_trim, pathCount);
		if (allPaths.size() == 1) {
			theFullPath = allPaths.get(0);
		} else if ((allPaths.size() > 1)) { // when queryGroup appears multiple times in a tree
			for (int i = 0; i < allPaths.size(); i++) {
				TreePath testPath = allPaths.get(i);
				boolean checkMatchAll = matchsAllGroups(testPath, splitLineForGroups);
				if (checkMatchAll) {
					theFullPath = allPaths.get(i);
					// System.out.println("this is the treePath that matches :" + theFullPath);
				}

			}
		}
		return theFullPath;
	} // getTreePathForEachLine method end

	// YD edits,Feb-2024, this method is to check if a string contains all elements
	// of a string array
	public boolean matchsAllGroups(TreePath myPath, String[] allStrs) {
		boolean matchsAll = true;
		for (int i = 0; i < allStrs.length; i++) {
			// need to remove the double quotes for each group
			String groupName = allStrs[i];
			String groupQuoteRemoved = groupName.substring(1, groupName.length() - 1);
			String myPathStr = (String) myPath.getPathComponent(i).toString();
			if (!myPathStr.equals(groupQuoteRemoved)) {
				matchsAll = false;
				break;
			}
		}
		return matchsAll;
	}

	// YD edits end

	public TreePath getTreePathFromNode(TreeNode treeNode) {
		List<Object> nodes = new ArrayList<Object>();
		if (treeNode != null) {
			nodes.add(treeNode);
			treeNode = treeNode.getParent();
			while (treeNode != null) {
				nodes.add(0, treeNode);
				treeNode = treeNode.getParent();
			}
		}

		return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
	}

	/**
	 * A class which represents a dirty bit.
	 * 
	 * @author Josh Lurz
	 *
	 */
	private class DirtyBit {
		/**
		 * Whether or not the dirty bit is set.
		 */
		private boolean mIsDirty;

		/**
		 * Constructor which initializes the dirty bit to false.
		 */
		public DirtyBit() {
			mIsDirty = false;
		}

		/**
		 * Set the dirty bit.
		 */
		public void setDirty() {
			mIsDirty = true;
		}

		/**
		 * Get the value of the dirty bit.
		 * 
		 * @return Whether the dirty bit is set.
		 */
		public boolean isDirty() {
			return mIsDirty;
		}
	}

	private void manageDB() {
		final InterfaceMain main = InterfaceMain.getInstance();
		final JFrame parentFrame = main.getFrame();
		final JDialog filterDialog = new JDialog(parentFrame, "Manage Database", true);
		filterDialog.getGlassPane().addMouseListener(new MouseAdapter() {
		});
		filterDialog.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		final DbViewer thisViewer = this;
		JPanel listPane = new JPanel();
		JPanel buttonPane = new JPanel();
		final JButton addButton = new JButton("Add");
		final JButton removeButton = new JButton("Remove");
		final JButton renameButton = new JButton("Rename");
		final JButton exportButton = new JButton("Export");
		final JButton doneButton = new JButton("Done");
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));
		Container contentPane = filterDialog.getContentPane();
		removeButton.setEnabled(false);
		renameButton.setEnabled(false);
		exportButton.setEnabled(false);

		// Vector scns = getScenarios();
		final JList list = new JList(scns);

		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (list.getSelectedIndex() == -1) {
					removeButton.setEnabled(false);
					renameButton.setEnabled(false);
					exportButton.setEnabled(false);
				} else {
					removeButton.setEnabled(true);
					renameButton.setEnabled(true);
					exportButton.setEnabled(true);
				}
			}
		});

		final DirtyBit dirtyBit = new DirtyBit();
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				FileChooser fc = FileChooserFactory.getFileChooser();
				final FileFilter xmlFilter = new XMLFilter();
				// Dan: parentFrame seems to introduce some dialog blocking issues. Trying to
				// set parent of filechooser as the filterDialog

				SwingUtilities.invokeLater(() -> {
					final File[] xmlFiles = fc.doFilePrompt(/* parentFrame */filterDialog, "Open XML File",
							FileChooser.LOAD_DIALOG, new File(main.getProperties().getProperty("lastDirectory", ".")),
							xmlFilter);

					if (xmlFiles != null) {
						
						
						dirtyBit.setDirty();
						main.getProperties().setProperty("lastDirectory", xmlFiles[0].getParent());
						
						//final Runnable incProgress = (new Runnable() {
						//	public void run() {
						//		progBar.setValue(progBar.getValue() + 1);
						//	}
						//});
						//jd.setVisible(true);
						// run the import off the gui thread which ensures progress updates correctly
						// and keeps the gui responsive
						new Thread(new Runnable() {
							public void run() {
								for (int addFileIndex = 0; addFileIndex < xmlFiles.length; ++addFileIndex) {
									XMLDB.getInstance().addFile(xmlFiles[addFileIndex].getAbsolutePath(),addFileIndex,xmlFiles.length);
									//SwingUtilities.invokeLater(incProgress);
								}
								scns = getScenarios();
								list.setListData(scns);
								//jd.setVisible(false);
							}
						}).start();
					}
				});
			}

		});
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] remList = list.getSelectedValues();
				filterDialog.getGlassPane().setVisible(true);
				for (int i = 0; i < remList.length; ++i) {
					dirtyBit.setDirty();
					XMLDB.getInstance().removeDoc(((ScenarioListItem) remList[i]).getDocName());
				}
				scns = getScenarios();
				list.setListData(scns);
				filterDialog.getGlassPane().setVisible(false);
			}
		});
		renameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final Object[] renameList = list.getSelectedValues();
				if (renameList.length == 0) {
					return;
				}
				final JDialog renameScenarioDialog = new JDialog(parentFrame, "Rename Scenarios", true);
				renameScenarioDialog.setResizable(false);
				final List<JTextField> renameBoxes = new ArrayList<JTextField>(renameList.length);
				JPanel renameBoxPanel = new JPanel();
				renameBoxPanel.setLayout(new BoxLayout(renameBoxPanel, BoxLayout.Y_AXIS));
				Component verticalSeparator = Box.createVerticalStrut(5);

				for (int i = 0; i < renameList.length; ++i) {
					ScenarioListItem currItem = (ScenarioListItem) renameList[i];
					JPanel currPanel = new JPanel();
					currPanel.setLayout(new BoxLayout(currPanel, BoxLayout.X_AXIS));
					JLabel currLabel = new JLabel("<html>Rename <b>" + currItem.getScnName() + "</b> on <b>"
							+ currItem.getScnDate() + "</b> to:</html>");
					JTextField currTextBox = new JTextField(currItem.getScnName(), 20);
					currTextBox.setMaximumSize(currTextBox.getPreferredSize());
					renameBoxes.add(currTextBox);
					currPanel.add(currLabel);
					currPanel.add(Box.createHorizontalGlue());
					renameBoxPanel.add(currPanel);
					currPanel = new JPanel();
					currPanel.setLayout(new BoxLayout(currPanel, BoxLayout.X_AXIS));
					currPanel.add(currTextBox);
					currPanel.add(Box.createHorizontalGlue());
					renameBoxPanel.add(currPanel);
					renameBoxPanel.add(verticalSeparator);
				}

				JPanel renameButtonPanel = new JPanel();
				final JButton renameOK = new JButton("  OK  ");
				final JButton renameCancel = new JButton("Cancel");
				renameButtonPanel.setLayout(new BoxLayout(renameButtonPanel, BoxLayout.X_AXIS));
				renameButtonPanel.add(Box.createHorizontalGlue());
				renameButtonPanel.add(renameOK);
				renameButtonPanel.add(renameCancel);
				ActionListener renameButtonListener = new ActionListener() {
					public void actionPerformed(ActionEvent renameEvt) {
						if (renameEvt.getSource() == renameOK) {
							for (int i = 0; i < renameList.length; ++i) {
								ScenarioListItem currItem = (ScenarioListItem) renameList[i];
								String currText = renameBoxes.get(i).getText();
								// only do it if the name really is different
								if (!currItem.getScnName().equals(currText)) {
									// the undoable edit will take care of doing
									// the rename
									UndoableEdit renameEdit = new RenameScenarioUndoableEdit(thisViewer, currItem,
											currText);
									InterfaceMain.getInstance().getUndoManager().addEdit(renameEdit);
									InterfaceMain.getInstance().refreshUndoRedo();
								}
							}
						}
						// scns = getScenarios();
						list.setListData(scns);
						renameScenarioDialog.dispose();
					}
				};
				renameOK.addActionListener(renameButtonListener);
				renameCancel.addActionListener(renameButtonListener);

				renameBoxPanel.add(renameButtonPanel);
				renameBoxPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
				renameScenarioDialog.getContentPane().add(renameBoxPanel);
				renameScenarioDialog.pack();
				renameScenarioDialog.setVisible(true);
			}
		});

		exportButton.addActionListener(new ActionListener() {
			/**
			 * Method called when the export button is clicked which allows the user to
			 * select a location to export the scenario to and exports the scenario.
			 * 
			 * @param aEvent The event received.
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent aEvent) {
				final Object[] selectedList = list.getSelectedValues();

				// !!!Dan: updated this so isSingleSelection is always false
				final boolean isSingleSelection = false; // selectedList.length == 1;
				FileFilter fileFilter;
				String saveDialogTitle;

				if (isSingleSelection) {
					fileFilter = new XMLFileFilter();
					saveDialogTitle = "Save As XML";
				} else {
					fileFilter = new FileFilter() {
						public boolean accept(File f) {
							return f.isDirectory();
						}

						public String getDescription() {
							return "Directory to export into";
						}
					};
					saveDialogTitle = "Select Export Directory";
				}
				FileChooser fc = FileChooserFactory.getFileChooser();

				final File[] exportLocation = fc.doFilePrompt(/* parentFrame */filterDialog, saveDialogTitle,
						FileChooser.SAVE_DIALOG, new File(main.getProperties().getProperty("lastDirectory", ".")),
						fileFilter);
				if (isSingleSelection && !exportLocation[0].getName().endsWith(".xml")) {
					exportLocation[0] = new File(exportLocation[0].getParentFile(),
							exportLocation[0].getName() + ".xml");
				}
				if (exportLocation == null) {
					// user canceled, nothing to do
					return;
				}
				final JProgressBar progBar = new JProgressBar(0, selectedList.length);
				final JLabel curLabel=new JLabel("Exporting runs from the database");
				final JDialog jd = XMLDB.createProgressBarGUI(progBar, "Exporting Runs", curLabel);
				final Runnable incProgress = (new Runnable() {
					public void run() {
						progBar.setValue(progBar.getValue() + 1);
					}
				});
				jd.setVisible(true);

				// run the export off the gui thread which ensures progress updates correctly
				// and keeps the gui responsive
				new Thread(new Runnable() {
					public void run() {
						boolean success = true;
						for (int i = 0; i < selectedList.length; ++i) {
							ScenarioListItem currItem = (ScenarioListItem) selectedList[i];
							File exportFile;

							if (isSingleSelection) {
								exportFile = exportLocation[0];
							} else {
								String exportFileName = currItem.getScnName() + "_"
										+ currItem.getScnDate().replaceAll(":", "_") + ".xml";
								exportFile = new File(exportLocation[0], exportFileName);
							}
							success = success && XMLDB.getInstance().exportDoc(currItem.getDocName(), exportFile);
							SwingUtilities.invokeLater(incProgress);
						}
						jd.setVisible(false);
						if (success) {
							InterfaceMain.getInstance().showMessageDialog("Scenario export succeeded.",
									"Scenario Export", JOptionPane.INFORMATION_MESSAGE);
						} else {
							InterfaceMain.getInstance().showMessageDialog("Scenario export failed.", null,
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}).start();
			}

		});

		doneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dirtyBit.isDirty()) {
					// meta data now set on demand
					// xmlDB.addVarMetaData(parentFrame);
					scnList.setListData(scns);
					regions = getRegions();
					regionList.setListData(regions);
				}
				filterDialog.setVisible(false);
			}
		});

		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPane.add(addButton);
		buttonPane.add(Box.createHorizontalStrut(10));
		buttonPane.add(removeButton);
		buttonPane.add(Box.createHorizontalStrut(10));
		buttonPane.add(renameButton);
		buttonPane.add(Box.createHorizontalStrut(10));
		buttonPane.add(exportButton);
		buttonPane.add(Box.createHorizontalStrut(10));
		buttonPane.add(doneButton);
		buttonPane.add(Box.createHorizontalGlue());

		JScrollPane sp = new JScrollPane(list);
		sp.setPreferredSize(new Dimension(300, 300));
		listPane.add(new JLabel("Scenarios in Database:"));
		listPane.add(Box.createVerticalStrut(10));
		listPane.add(sp);
		listPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		contentPane.add(listPane, BorderLayout.PAGE_START);
		contentPane.add(buttonPane, BorderLayout.PAGE_END);
		filterDialog.pack();
		filterDialog.setVisible(true);
	}

	public void exportTabs() {
		final InterfaceMain main = InterfaceMain.getInstance();

		if (tablesTabs.getTabCount() == 0) {
			// error?
			return;
		}

		// select export location
		String exportDialogTitle = "Select Export Directory";
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory();
			}

			public String getDescription() {
				return "Directory to export into";
			}
		};

		FileChooser fc = FileChooserFactory.getFileChooser();
		final File[] exportLocation = fc.doFilePrompt(null, exportDialogTitle, FileChooser.SAVE_DIALOG,
				new File(main.getProperties().getProperty("lastDirectory", ".")), fileFilter);

		if (exportLocation == null) {
			// user canceled, nothing to do
			return;
		}

		ArrayList<String> tab_titles = new ArrayList<String>();

		int tab_count = tablesTabs.getTabCount();
		for (int i = 0; i < tab_count; i++) {
			String tab_title = tablesTabs.getTitleAt(i).replaceAll(" ", "_").replaceAll(":", "");

			//System.out.println("Title:" + tab_title);

			int k = 0;
			for (int j = 0; j < tab_titles.size(); j++) {
				if (tab_titles.get(j).equals(tab_title))
					k++;
			}
			tab_titles.add(tab_title);
			if (k > 0)
				tab_title += ("_" + k);
			

			Component c = tablesTabs.getComponentAt(i);

			JTable jTable = getJTableFromComponent(c);

			TableModel tm = jTable.getModel();

			if (tm != null) {
				String filename = exportLocation[0].getAbsolutePath() + File.separator + tab_title + ".csv";
				writeTableModelToFile(filename, tm);
			}
			
			
		}
		JOptionPane.showMessageDialog(null, tab_titles.size() + " files exported to " + exportLocation[0].getAbsolutePath());
	}

	protected boolean arrayListIncludes(ArrayList<String> array, String val) {
		for (int i = 0; i < array.size(); i++) {
			if (val.equals(array.get(i)))
				return true;
		}
		return false;
	}

	/*
	 * jTable = getJTableFromComponent(tablesTabs.getSelectedComponent()); jsp = new
	 * JScrollPane(tablesTabs.getSelectedComponent());
	 * JFreeReportBoot.getInstance().start(); JFreeReport report = new
	 * JFreeReport(); java.awt.print.PageFormat pageFormat = new
	 * java.awt.print.PageFormat();
	 * pageFormat.setOrientation(java.awt.print.PageFormat.LANDSCAPE);
	 * report.setPageDefinition(new
	 * org.jfree.report.SimplePageDefinition(pageFormat));
	 * DrawableFieldElementFactory factory = new DrawableFieldElementFactory();
	 * Group g = new Group(); float div = 1; int numRows = 0; if(jTable.getModel()
	 * instanceof MultiTableModel) { numRows = (int)jTable.getRowCount()/2; div =
	 * (float)(jTable.getRowCount()/2); } factory.setAbsolutePosition(new
	 * Point2D.Float(0, 0)); factory.setMinimumSize(new FloatDimension((float)800,
	 * (float)(jsp.getVerticalScrollBar().getMaximum()/div)));
	 * factory.setMaximumSize(new FloatDimension((float)800,
	 * (float)(jsp.getVerticalScrollBar().getMaximum()/div)));
	 * factory.setFieldname("0"); g.addField("0");
	 * g.getHeader().addElement(factory.createElement());
	 * g.getHeader().setPagebreakBeforePrint(true); report.addGroup(g); final Vector
	 * fieldList = new Vector(numRows+1); fieldList.add("0"); for(int i = 1; i <
	 * numRows; ++i) { g = new Group(); factory.setFieldname(String.valueOf(i));
	 * fieldList.add(String.valueOf(i)); g.setFields(fieldList);
	 * g.getHeader().addElement(factory.createElement());
	 * g.getHeader().setPagebreakBeforePrint(true); report.addGroup(g); }
	 * 
	 * report.setData(new javax.swing.table.AbstractTableModel() { public int
	 * findColumn(String cName) { return Integer.parseInt(cName); } public String
	 * getColumnName(int col) { return String.valueOf(col); } public int
	 * getColumnCount() { return fieldList.size(); } public int getRowCount() {
	 * return 1; } public Object getValueAt(int row, int col) { final int colf =
	 * col; return (new org.jfree.ui.Drawable() { public void
	 * draw(java.awt.Graphics2D graphics, java.awt.geom.Rectangle2D bounds) { double
	 * scaleFactor = bounds.getWidth() / jsp.getHorizontalScrollBar().getMaximum();
	 * graphics.scale(scaleFactor, scaleFactor); graphics.translate((double)0,
	 * 0-bounds.getHeight()*colf); if(!(jTable.getModel() instanceof
	 * MultiTableModel)) { jsp.printAll(graphics); } else {
	 * jTable.printAll(graphics); }
	 * 
	 * graphics.setColor(Color.WHITE); graphics.fillRect(0,
	 * (int)bounds.getHeight()*(1+colf), (int)graphics.getClipBounds().getWidth(),
	 * (int)bounds.getHeight()); } }); } });
	 * 
	 * try { report.getReportConfiguration().setConfigProperty(
	 * "org.jfree.report.modules.gui.xls.Enable", "false");
	 * report.getReportConfiguration().setConfigProperty(
	 * "org.jfree.report.modules.gui.plaintext.Enable", "false");
	 * report.getReportConfiguration().setConfigProperty(
	 * "org.jfree.report.modules.gui.csv.Enable", "false");
	 * report.getReportConfiguration().setConfigProperty(
	 * "org.jfree.report.modules.gui.html.Enable", "false");
	 * report.getReportConfiguration().setConfigProperty(
	 * "org.jfree.report.modules.gui.rtf.Enable", "false");
	 * report.getReportConfiguration().setConfigProperty(MyExcelExportPlugin.
	 * enableKey, "true"); ExportPluginFactory epf =
	 * ExportPluginFactory.getInstance(); //MyExcelExportPlugin.bt = bt;
	 * MyExcelExportPlugin.bt = (BaseTableModel)jTable.getModel();
	 * epf.registerPlugin(MyExcelExportPlugin.class, "20",
	 * MyExcelExportPlugin.enableKey); PreviewDialog preview = new
	 * PreviewDialog(report, parentFrame, true);
	 * preview.setTitle(parentFrame.getTitle()+" - Export Preview"); preview.pack();
	 * preview.setVisible(true); } catch(ReportProcessingException e) {
	 * e.printStackTrace(); }
	 */
	protected void writeTableModelToFile(String filename, TableModel tm) {
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(filename));

			int no_rows = tm.getRowCount();
			int no_cols = tm.getColumnCount();

			String header = "";
			String s = "";
			for (int j = 0; j < no_cols; j++) {
				if (s != "")
					s += ",";
				s += tm.getColumnName(j);
			}
			pw.println(s);

			for (int i = 0; i < no_rows; i++) {
				s = "";
				for (int j = 0; j < no_cols; j++) {
					if (s != "")
						s += ",";
					s += tm.getValueAt(i, j);
				}
				pw.println(s);
			}

			pw.close();

		} catch (Exception e) {
			System.out.println("Problem writing tab to CSV file:" + e);
		}

	}

	protected void batchQuery(File queryFile, final File excelFile) {
		final JFrame parentFrame = InterfaceMain.getInstance().getFrame();
		final Vector<ScenarioListItem> tempScns = getScenarios();
		final String singleSheetCheckBoxPropName = "batchQueryResultsInDifferentSheets";
		final String includeChartsPropName = "batchQueryIncludeCharts";
		final String splitRunsPropName = "batchQuerySplitRunsInDifferentSheets";
		final String replaceResultsPropName = "batchQueryReplaceResults";
		Properties prop = InterfaceMain.getInstance().getProperties();

		// determine the proper number of threads to use for queries by
		// checking the configuration parameter which defaults to the
		// total number of cores on the system
		final String coresToUsePropertyName = "coresToUse";
		final int numSystemCores = Runtime.getRuntime().availableProcessors();
		// TODO: set the default cores to use low until we figure out how to get BaseX
		// to
		// perform better with many parallel queries.
		// final int numCoresToUse =
		// Integer.valueOf(prop.getProperty(coresToUsePropertyName,
		// Integer.toString(numSystemCores)));
		final int defaultNumCoresToUse = Integer.valueOf(prop.getProperty(coresToUsePropertyName, Integer.toString(2)));
		prop.setProperty(coresToUsePropertyName, Integer.toString(defaultNumCoresToUse));

		// Create a Select Scenarios dialog to get which scenarios to run
		final JList scenarioList = new JList(tempScns);
		final JDialog scenarioDialog = new JDialog(parentFrame, "Select Scenarios to Run", true);
		JPanel listPane = new JPanel();
		JPanel buttonPane = new JPanel();
		final JCheckBox singleSheetCheckBox = new JCheckBox("Place all results in different sheets",
				Boolean.parseBoolean(prop.getProperty(singleSheetCheckBoxPropName, "false")));
		final JCheckBox drawPicsCheckBox = new JCheckBox("Include charts with results",
				Boolean.parseBoolean(prop.getProperty(includeChartsPropName, "true")));
		final JCheckBox seperateRunsCheckBox = new JCheckBox("Split runs into different sheets",
				Boolean.parseBoolean(prop.getProperty(splitRunsPropName, "false")));
		final JButton okButton = new JButton("Ok");
		okButton.setEnabled(false);
		JButton cancelButton = new JButton("Cancel");
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));
		Container contentPane = scenarioDialog.getContentPane();
		final JCheckBox overwriteCheckBox = new JCheckBox("Overwrite selected file if it exists",
				Boolean.parseBoolean(prop.getProperty(replaceResultsPropName, "false")));

		scenarioList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (scenarioList.isSelectionEmpty()) {
					okButton.setEnabled(false);
				} else {
					okButton.setEnabled(true);
				}
			}
		});

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scenarioDialog.dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scenarioList.clearSelection();
				scenarioDialog.dispose();
			}
		});

		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(okButton);
		buttonPane.add(Box.createHorizontalStrut(10));
		buttonPane.add(cancelButton);

		JScrollPane sp = new JScrollPane(scenarioList);
		sp.setPreferredSize(new Dimension(300, 300));
		listPane.add(new JLabel("Select Scenarios:"));
		listPane.add(Box.createVerticalStrut(10));
		listPane.add(sp);
		listPane.add(singleSheetCheckBox);
		listPane.add(overwriteCheckBox);
		listPane.add(drawPicsCheckBox);
		listPane.add(seperateRunsCheckBox);
		listPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		contentPane.add(listPane, BorderLayout.PAGE_START);
		contentPane.add(buttonPane, BorderLayout.PAGE_END);
		scenarioDialog.pack();
		scenarioDialog.setVisible(true);

		if (scenarioList.isSelectionEmpty()) {
			return;
		}
		// save the check box options back into the properties
		prop.setProperty(singleSheetCheckBoxPropName, Boolean.toString(singleSheetCheckBox.isSelected()));
		prop.setProperty(includeChartsPropName, Boolean.toString(drawPicsCheckBox.isSelected()));
		prop.setProperty(splitRunsPropName, Boolean.toString(seperateRunsCheckBox.isSelected()));
		prop.setProperty(replaceResultsPropName, Boolean.toString(overwriteCheckBox.isSelected()));

		// read the batch query file
		Document queries = readQueries(queryFile);
		final NodeList res;
		try {
			res = (NodeList) XPathFactory.newInstance().newXPath().evaluate("/queries/node()", queries,
					XPathConstants.NODESET);
		} catch (Exception e) {
			InterfaceMain.getInstance().showMessageDialog("Could not find queries to run in batch file:\n" + queryFile,
					"Batch Query Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return;
		}
		final int numQueries = res.getLength();
		if (numQueries == 0) {
			InterfaceMain.getInstance().showMessageDialog("Could not find queries to run in batch file:\n" + queryFile,
					"Batch Query Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		final Vector<Object[]> toRunScns = new Vector<Object[]>();
		if (!seperateRunsCheckBox.isSelected()) {
			toRunScns.add(scenarioList.getSelectedValues());
		} else {
			for (Object currScn : scenarioList.getSelectedValues()) {
				Object[] temp = new Object[1];
				temp[0] = currScn;
				toRunScns.add(temp);
			}
		}
		// create window

		// Provide the default set of all regions which does not include Global
		Vector<String> allRegions = new Vector<String>(regions);
		allRegions.remove("Global");

		final BatchWindow bWindow = new BatchWindow(excelFile, toRunScns, allRegions, singleSheetCheckBox.isSelected(),
				drawPicsCheckBox.isSelected(), numQueries, res, overwriteCheckBox.isSelected(), defaultNumCoresToUse);
		// create listener for window
	}

	public boolean writeFile(File file, Document theDoc) {
		LSSerializer serializer = implls.createLSSerializer();
		// specify output formatting properties
		/*
		 * OutputFormat format = new OutputFormat(theDoc); format.setEncoding("UTF-8");
		 * format.setLineSeparator("\r\n"); format.setIndenting(true);
		 * format.setIndent(3); format.setLineWidth(0); format.setPreserveSpace(false);
		 * format.setOmitDocumentType(true);
		 */
		DOMConfiguration domConfig = serializer.getDomConfig();
		boolean prettyPrint = Boolean.parseBoolean(System.getProperty("ModelInterface.pretty-print", "true"));
		domConfig.setParameter("format-pretty-print", prettyPrint);
		// create the serializer and have it print the document

		try {
			/*
			 * FileWriter fw = new FileWriter(file); XMLSerializer serializer = new
			 * XMLSerializer(fw, format); serializer.asDOMSerializer();
			 * serializer.serialize(theDoc); fw.close(); } catch (java.io.IOException e) {
			 */
			LSOutput lsOut = implls.createLSOutput();
			lsOut.setByteStream(new FileOutputStream(file));
			serializer.write(theDoc, lsOut);
		} catch (Exception e) {
			System.err.println("Error outputting tree: " + e);
			return false;
		}
		return true;
	}

	public Document readQueries(File queryFile) {
		if (queryFile.exists()) {
			LSInput lsInput = implls.createLSInput();
			try {
				lsInput.setByteStream(new FileInputStream(queryFile));
			} catch (FileNotFoundException e) {
				// is it even possible to get here
				e.printStackTrace();
			}
			LSParser lsParser = implls.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
			lsParser.setFilter(new ParseFilter());
			return lsParser.parse(lsInput);
		} else {
			// DocumentType DOCTYPE = impl.createDocumentType("recent", "", "");
			return ((DOMImplementation) implls).createDocument("", "queries", null);
		}
	}

	/**
	 * Filter and existing DOM subtree with an LSParserFilter. This traverses the
	 * child nodes of the given node recursively removing any rejected nodes in the
	 * same manner that the LSParser would.
	 * 
	 * @param parentNode The current node who's children will be processed
	 *                   recursively.
	 * @param filter     The LSParserFilter to apply.
	 */
	public void filterNodes(Node parentNode, LSParserFilter filter) {
		Node currNode = parentNode.getFirstChild();
		final int whatToShow = filter.getWhatToShow();
		while (currNode != null) {
			Node nextNode = currNode.getNextSibling();
			// First determine if we should even inspect this kind of node.
			boolean showNode;
			switch (currNode.getNodeType()) {
			case Node.ATTRIBUTE_NODE: {
				showNode = (whatToShow & NodeFilter.SHOW_ATTRIBUTE) != 0;
				break;
			}
			case Node.COMMENT_NODE: {
				showNode = (whatToShow & NodeFilter.SHOW_COMMENT) != 0;
				break;
			}
			case Node.ELEMENT_NODE: {
				showNode = (whatToShow & NodeFilter.SHOW_ELEMENT) != 0;
				break;
			}
			case Node.TEXT_NODE: {
				showNode = (whatToShow & NodeFilter.SHOW_TEXT) != 0;
				break;
			}
			default: {
				showNode = (whatToShow & NodeFilter.SHOW_ALL) != 0;
				break;
			}
			}
			if (showNode && filter.acceptNode(currNode) == LSParserFilter.FILTER_REJECT) {
				// the node was rejected so remove it
				parentNode.removeChild(currNode);
			} else {
				// either the node should not be tested or it was accepted so we
				// keep and and recursively process from this node
				filterNodes(currNode, filter);
			}
			currNode = nextNode;
		}
	}

	private void writeQueries() {
		try {
			Document tempDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation()
					.createDocument(null, "queries", null);
			queries.getAsNode(tempDoc);
			// writeDocument(tempDoc, queryFile);
			writeFile(new File(InterfaceMain.getInstance().getProperties().getProperty("queryFile")), tempDoc);
			queries.resetChanges();
		} catch (ParserConfigurationException pce) {
			// TODO: error to the sceen that it could no save..
			pce.printStackTrace();
		}
	}

	// YD added to match filtering strings, 2024
	private boolean matchesFilterInGroup(ArrayList myList) {
		return myList.toString().contains(filteringText);
	}

	// YD added to match tree node children with filtering strings, 2024
	private boolean containsMatchingChild(QueryGroup group) {
		int nChildren = group.getChildCount();
		boolean isMatched = false;

		return isMatched;
	}

	public void loadFavoriteQueriesFile() {
		String PathToUse = null;
		if (InterfaceMain.favoriteQueriesFileLocation != null) {
			File f = new File(InterfaceMain.favoriteQueriesFileLocation);
			if (f.exists()) {
				PathToUse = f.getParent();
			}
		}

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select a query file to load.");
		if (PathToUse != null) {
			fileChooser.setCurrentDirectory(new File(PathToUse));
		}
		int returnVal = fileChooser.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			InterfaceMain.favoriteQueriesFileLocation = fileChooser.getSelectedFile().getAbsolutePath();
			String messageFileSaved = fileChooser.getSelectedFile().toString()
					+ " has been loaded.\n\n Would you like to apply it now?";
			int answer = JOptionPane.showConfirmDialog(null, messageFileSaved, "Switch?", JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.YES_OPTION) {
				selectFavoriteQueries(queryList);
			}
		}
	}

	// YD added to create a file that contains the selected queries as user's
	// favorite queries, Feb-2024
	public void createFavoriteQueriesFile(JTree myTree) {
		TreePath[] selectedTreePath = myTree.getSelectionPaths();
		// boolean append = false;
		// String fileName = "./exe/favorite_queries_list.txt";
		// File favoritesFile = new File(fileName);
		// int overWriteAnswer = -1;
		// if (favoritesFile.exists()) {
		// String messageOverwriteFile = "this file " + fileName + " exists. Overwrite
		// it?";
		// overWriteAnswer = JOptionPane.showConfirmDialog(null, messageOverwriteFile,
		// null,
		// JOptionPane.OK_CANCEL_OPTION);
		// //System.out.println("my overWrite answer is:" + overWriteAnswer);
		// }
		if (selectedTreePath.length == 0) {
			JOptionPane.showMessageDialog(null, "Please select at least one query to save.");
			return;
		}

		boolean hasLeaf = false;
		for (TreePath tp : selectedTreePath) {
			if (tp.getLastPathComponent() instanceof QueryGenerator)
				hasLeaf = true;
		}
		if (!hasLeaf) {
			JOptionPane.showMessageDialog(null, "Please select at least one query to save.");
			return;
		}

		String PathToUse = null;
		if (InterfaceMain.favoriteQueriesFileLocation != null) {
			File f = new File(InterfaceMain.favoriteQueriesFileLocation);
			if (f.exists()) {
				PathToUse = f.getParent();
			}
		}

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a favorite queries file to save");
		if (PathToUse != null) {
			fileChooser.setCurrentDirectory(new File(PathToUse));
		}
		int userSelection = fileChooser.showSaveDialog(null);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			try {
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(fileChooser.getSelectedFile(), false)));
				String convertedLine = "";
				for (int i = 0; i < selectedTreePath.length; i++) {

					TreePath treePathNow = selectedTreePath[i];
					if (treePathNow.getLastPathComponent() instanceof QueryGenerator) {
						int treePathCount = treePathNow.getPathCount();
						String pathStr = selectedTreePath[i].toString();
						String lineStr = pathStr.substring(1, pathStr.length() - 1);// remove the square brackets
						int commaCount = countCommaInPath(lineStr);
						if (commaCount > treePathCount - 1) { // there are commas inside queryGroup
							convertedLine = convertPathWithCommaToLine(treePathNow);
						} else {
							convertedLine = convertPathToLine(lineStr);
						}
					}
					// System.out.println("converted line is:" + convertedLine);
					writer.write(convertedLine);
					writer.newLine();
				}
				writer.close();
				String messageFileSaved = fileChooser.getSelectedFile().toString()
						+ " has been saved.\n\n Would you like to make this the active favorites file?";
				int answer = JOptionPane.showConfirmDialog(null, messageFileSaved, "Switch?",
						JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION) {
					InterfaceMain.favoriteQueriesFileLocation = fileChooser.getSelectedFile().getAbsolutePath();
				}
			} catch (IOException e) {
				System.out.println("Could not save file: " + e.toString());
				JOptionPane.showMessageDialog(null, "Unable to save file, please see console for error",
						"Error Saving File", JOptionPane.ERROR_MESSAGE);
			}

		}

	}// createFavoriteQueriesFile method end

	// YD added to append the selected queries to a file that contains user's
	// favorite queries, Mar-2024
	public void appendFavoriteQueries(JTree myTree) {
		TreePath[] selectedTreePath = myTree.getSelectionPaths();
		// boolean append = true;
		// String fileName = "./exe/favorite_queries_list.txt";
		// File favoritesFile = new File(fileName);
		File favoritesFile = new File(InterfaceMain.favoriteQueriesFileLocation);
		if (favoritesFile.exists()) {
			try {
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(favoritesFile, true)));
				for (int i = 0; i < selectedTreePath.length; i++) {
					TreePath treePathNow = selectedTreePath[i];
					int treePathCount = treePathNow.getPathCount();
					String pathStr = selectedTreePath[i].toString();
					String lineStr = pathStr.substring(1, pathStr.length() - 1);// remove the square brackets
					int commaCount = countCommaInPath(lineStr);
					String convertedLine = "";
					if (commaCount > treePathCount - 1) { // there are commas inside queryGroup
						convertedLine = convertPathWithCommaToLine(treePathNow);
					} else {
						convertedLine = convertPathToLine(lineStr);
					}
					// System.out.println("converted line is:" + convertedLine);
					writer.write(convertedLine);
					writer.newLine();
				}
				writer.close();
				String messageQueriesAppended = "The selected queries have been appended to "
						+ InterfaceMain.favoriteQueriesFileLocation;
				JOptionPane.showMessageDialog(null, messageQueriesAppended);
			} catch (IOException e) {
				System.out.println("Could not append to favorite queries: " + e.toString());
			} // try and catch loop end
		} else {
			String messageFileNotFound = "Favorite queries list file " + InterfaceMain.favoriteQueriesFileLocation
					+ " could not be found to be appended to.\n" + "Please load or create a file first.";
			JOptionPane.showMessageDialog(null, messageFileNotFound);
		} // if else loop end
	}// appendFavoriteQueries method end

	// YD added this method to convert the path string into each line with
	// ">",Feb-2024
	public static String convertPathToLine(String pathStr) {
		return Arrays.stream(pathStr.trim().split("\\s*,\\s*")).map(s -> s.isEmpty() ? s : '"' + s + '"')
				.collect(Collectors.joining(">"));
	}

	// YD added this method to count number of commas in a TreePath,Mar-2024
	public int countCommaInPath(String pathStr) {
		int numCommas = pathStr.length() - pathStr.replace(",", "").length();
		return (numCommas);
	}

	// YD added this method to count number of commas in a TreePath,Mar-2024
	public String convertPathWithCommaToLine(TreePath treePathNow) {
		int treePathCount = treePathNow.getPathCount();
		String myStr = "";
		for (int i = 0; i < treePathCount - 1; i++) {
			QueryGroup queryGroupNow = (QueryGroup) treePathNow.getPathComponent(i);
			String strNow = "\"" + queryGroupNow + "\"" + ">";
			myStr = myStr + strNow;
		}
		Object queryName = treePathNow.getPathComponent(treePathCount - 1);
		String lastPart = "\"" + queryName.toString() + "\"";
		String myLine = myStr + lastPart;
		return (myLine);
	}

	// YD edits end here

	public static BaseTableModel getTableModelFromComponent(java.awt.Component comp) {
		// Dan: This method seems to be very problematic, but in most instances, maybe
		// it is OK to return null
		Object c;
		try {
			c = ((QueryResultsPanel) comp).getComponent(0);
			// If a JPanel is returned, QueryResultsPanel returned a Panel with text,
			// so no table can be extracted

			if (c instanceof JPanel) {
				return null;
			}
			if (c instanceof JSplitPane) {
				// Dan-Debug - Causes error javax.swing.JPanel cannot be cast to
				// javax.swing.JScrollPane
				return (BaseTableModel) ((TableSorter) ((JTable) ((JScrollPane) ((JSplitPane) c).getLeftComponent())
						.getViewport().getView()).getModel()).getTableModel();
			} else {
				return (BaseTableModel) ((JTable) ((JScrollPane) c).getViewport().getView()).getModel();
			}
		} catch (ClassCastException e) {
			// Dan:This seems to be OK to comment out. Definitely saves a bunch of stack
			// traces that don't seem useful
			// e.printStackTrace();
			// System.out.println("--->problems with JPanel casting in
			// getTableModelFromComponent. Returning null");
			return null;
		}
	}

	public static JTable getJTableFromComponent(java.awt.Component comp) {
		Object c;
		try {
			QueryResultsPanel qPanel = (QueryResultsPanel) comp;
			c = qPanel.getComponent(0);

			if (c instanceof JPanel) {
				return null;
			}
			if (c instanceof JSplitPane) {
				JSplitPane jsp = (JSplitPane) c;
				Component c1 = jsp.getLeftComponent();

				if (c1 instanceof JScrollPane) {
					return (JTable) ((JScrollPane) (c1)).getViewport().getView();
				} else {
					// if not a JScrollPane, assumes it is a JPanel
					JPanel jp = (JPanel) c1;
					JScrollPane jscp = (JScrollPane) jp.getComponent(1);
					return (JTable) jscp.getViewport().getView();
				}
			} else {
				return (JTable) ((JScrollPane) c).getViewport().getView();
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String createCommentTooltip(TreePath path) {
		QueryGenerator qg;
		if (path.getLastPathComponent() instanceof QueryGenerator) {
			qg = (QueryGenerator) path.getLastPathComponent();
		} else {
			// SingleQueryValue..
			qg = (QueryGenerator) path.getParentPath().getLastPathComponent();
		}
		StringBuilder ret = new StringBuilder("<html><table cellpadding=\"2\"><tr><td>");
		for (int i = 1; i < path.getPathCount() - 1; ++i) {
			ret.append(path.getPathComponent(i)).append(":<br>");
		}
		ret.append(path.getLastPathComponent()).append("<br><br>Comments:<br>").append(qg.getComments())
				.append("</td></tr></table></html>");
		return ret.toString();
	}

	private class TabDragListener implements MouseListener, MouseMotionListener {
		MouseEvent firstMouseEvent = null;
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();

		public void mousePressed(MouseEvent e) {
			JComponent c = (JComponent) e.getSource();
			if (tablesTabs.getTabCount() > 0
					&& tablesTabs.getBoundsAt(tablesTabs.getSelectedIndex()).contains(e.getPoint())) {
				if (e.getButton() == 3) {
					// Tell the transfer handler to initiate the copy.
					c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					tablesTabs.getTransferHandler().exportToClipboard(tablesTabs, clip, TransferHandler.COPY);
					c.setCursor(Cursor.getDefaultCursor());
				}
				firstMouseEvent = e;
				e.consume();
			}

		}

		public void mouseDragged(MouseEvent e) {
			// make sure that there was a press first and that that tab has not
			// since been closed
			if (firstMouseEvent != null && tablesTabs.getTabCount() > 0
					&& tablesTabs.getBoundsAt(tablesTabs.getSelectedIndex()).contains(e.getPoint())) {
				e.consume();

				// TODO: maybe cut would be possible, for now just copy
				// if we do cut we would probably want to do ctrl mask
				// for cut not paste.
				int action = TransferHandler.COPY;

				int dx = Math.abs(e.getX() - firstMouseEvent.getX());
				int dy = Math.abs(e.getY() - firstMouseEvent.getY());
				// Arbitrarily define a 5-pixel shift as the
				// official beginning of a drag.
				if (dx > 5 || dy > 5) {
					JComponent c = (JComponent) e.getSource();
					c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

					tablesTabs.getTransferHandler().exportAsDrag(tablesTabs, firstMouseEvent, action);
					firstMouseEvent = null;
					c.setCursor(Cursor.getDefaultCursor());
				}

			}
		}

		// all the events we don't care about..
		public void mouseMoved(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}
	}

	/**
	 * Creates a dialog which will ask for scenarios, regions, and queries to scan
	 * for SingleQueryValues. When finished selecting these values it will do the
	 * scan. This could take some time and the rest of the GUI should be inoperable
	 * while the scan is occuring. A progress bar will be displayed.
	 * 
	 * @param queries   All of the queries in the tree
	 * @param scenarios All of the scenarios.
	 * @param regions   All of the regions.
	 */
	private void createAndShowGetSingleQueries(final List<QueryGenerator> queries,
			final List<ScenarioListItem> scenarios, final List<String> regions) {
		// create the dialog which will block the rest of the gui until it is done
		final JFrame parentFrame = InterfaceMain.getInstance().getFrame();
		final JDialog scanDialog = new JDialog(parentFrame, "Update Single Query Cache", true);
		final JTabbedPane selectionTabs = new JTabbedPane();

		// JLists expects these as arrays so create them now
		final ScenarioListItem[] scenariosArr = new ScenarioListItem[scenarios.size()];
		final String[] regionsArr = new String[regions.size()];
		final QueryGenerator[] queriesArr = new QueryGenerator[queries.size()];
		scenarios.toArray(scenariosArr);
		regions.toArray(regionsArr);
		queries.toArray(queriesArr);

		// create the display components
		final JList selectScenarios = new JList(scenariosArr);
		final JList selectRegions = new JList(regionsArr);
		final JList selectQueries = new JList(queriesArr);
		final JButton scanButton = new JButton("Scan");
		final JButton cancelButton = new JButton("Cancel");
		final JPanel all = new JPanel();
		final Component seperator = Box.createRigidArea(new Dimension(20, 10));

		// create the progress bar
		final JProgressBar scanProgress = new JProgressBar(0, queries.size());
		final JLabel progLabel = new JLabel("Label");
		// processing should be done off of the gui thread to ensure responsiveness
		final Thread scanThread = new Thread(new Runnable() {
			public void run() {
				// increasing progress should be run on the gui thread so I will create
				// this runnable and use the SwingUtilities.invokeLater to run it on there
				final Runnable incProgress = new Runnable() {
					public void run() {
						scanProgress.setValue(scanProgress.getValue() + 1);
					}
				};

				// make lists of the selected values only
				int[] selIndexes = selectScenarios.getSelectedIndices();
				final ScenarioListItem[] selScenarios = new ScenarioListItem[selIndexes.length];
				int pos = 0;
				for (int selIndex : selIndexes) {
					selScenarios[pos++] = scenariosArr[selIndex];
				}

				selIndexes = selectRegions.getSelectedIndices();
				final String[] selRegions = new String[selIndexes.length];
				pos = 0;
				for (int selIndex : selIndexes) {
					selRegions[pos++] = regionsArr[selIndex];
				}

				selIndexes = selectQueries.getSelectedIndices();
				final List<QueryGenerator> selQueries = new ArrayList<QueryGenerator>(selIndexes.length);
				for (int selIndex : selIndexes) {
					selQueries.add(queriesArr[selIndex]);
				}
				scanProgress.setMaximum(selIndexes.length);

				// get the cache document, if there is an exception getting it then it
				// may not exsist so we can try to create it
				XMLDB xmldbInstance = XMLDB.getInstance();
				QueryProcessor queryProc = xmldbInstance.createQuery("/singleQueryListCache", null, null, null);
				ANode doc = null;
				try {
					Iter res = queryProc.iter();
					doc = (ANode) res.next();
					if (doc == null) {
						// Try to create it then get the doc
						xmldbInstance.addFile("cache.xml", "<singleQueryListCache />",1,1);
						queryProc = xmldbInstance.createQuery("/singleQueryListCache", null, null, null);
						res = queryProc.iter();
						doc = (ANode) res.next();
					}
				} catch (QueryException e) {
					// TODO: put error to screen?
					e.printStackTrace();
				} finally {
					queryProc.close();
				}

				// a final check if we were not able to get the doc then do not scan
				boolean wasInterrupted = doc == null;

				// for each query that is enabled have the extension create and cache it's
				// single query list. The cache will be set as metadata on the cache doc
				// if we got interrupted we must stop now
				for (Iterator<QueryGenerator> it = selQueries.iterator(); it.hasNext() && !wasInterrupted;) {
					QueryGenerator currQG = it.next();
					progLabel.setText("Scanning " + currQG.toString());
					SingleQueryExtension se = currQG.getSingleQueryExtension();
					// could be null if the extension is not enabled
					if (se != null) {
						se.createSingleQueryListCache(doc, selScenarios, selRegions);
					}
					SwingUtilities.invokeLater(incProgress);
					wasInterrupted = Thread.interrupted();
				}

				// clean up and take down the progress bar
				scanDialog.setVisible(false);
			}
		});

		// default is to select all
		selectScenarios.setSelectionInterval(0, scenariosArr.length - 1);
		selectRegions.setSelectionInterval(0, regionsArr.length - 1);
		selectQueries.setSelectionInterval(0, queriesArr.length - 1);

		// create the tabs for the selections
		selectionTabs.addTab("Scenarios", new JScrollPane(selectScenarios));
		selectionTabs.addTab("Regions", new JScrollPane(selectRegions));
		selectionTabs.addTab("Queries", new JScrollPane(selectQueries));
		// have it take as much room as possible
		selectionTabs.setPreferredSize(new Dimension(400, 400));

		// need to make sure the label will align to the left
		final JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.add(progLabel);
		labelPanel.add(Box.createHorizontalGlue());

		// buttons need to be layouted out horizontally
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(scanButton);
		buttonPanel.add(seperator);
		buttonPanel.add(cancelButton);

		// the cancel button will interrupt the can if it is running
		// or just close the dialog if it is not. note that if the
		// users cancels NONE of the scan will be written back to the
		// database
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (scanThread.isAlive()) {
					progLabel.setText("Canceling Scan");
					scanThread.interrupt();
					// this is in effect interrupting all single queries create list
					// queries
					int[] selIndexes = selectQueries.getSelectedIndices();
					for (int selIndex : selIndexes) {
						queriesArr[selIndex].getSingleQueryExtension().interruptGatherThread();
					}
					// will let the scan thread hide the dialog
				} else {
					// has not started yet so just hide it
					scanDialog.setVisible(false);
				}
			}
		});

		// when the scan button is hit we will switch the content of the dialog
		// from the selection lists to a progress bar to let the user know how
		// things are going. The user will still be able to cancel once the scan
		// starts
		scanButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scanButton.setEnabled(false);

				// set up the new content pane
				final JPanel progPanel = new JPanel();
				progPanel.setLayout(new BoxLayout(progPanel, BoxLayout.Y_AXIS));
				progPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
				progPanel.add(scanProgress);
				progPanel.add(labelPanel);
				// make sure it is atleast 200 accross
				progPanel.add(Box.createHorizontalStrut(300));
				progPanel.add(Box.createVerticalGlue());
				progPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
				progPanel.add(seperator);
				progPanel.add(buttonPanel);

				// start scanning to cache queries
				scanThread.start();

				// display the new pane and shrink down any unnessary space
				scanDialog.setContentPane(progPanel);
				scanDialog.pack();
			}
		});

		// create the layout which will be tabbed pane on top and buttons on the
		// bottom
		all.setLayout(new BoxLayout(all, BoxLayout.Y_AXIS));
		all.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		all.add(selectionTabs);
		all.add(seperator);
		all.add(new JSeparator(SwingConstants.HORIZONTAL));
		all.add(seperator);
		all.add(buttonPanel);

		// set the content pane for the dialog and show it
		scanDialog.setSize(400, 400);
		scanDialog.setResizable(false);
		scanDialog.setContentPane(all);
		scanDialog.setVisible(true);
	}

	public void runBatch(Node command) {
		Properties prop = InterfaceMain.getInstance().getProperties();
		final String singleSheetCheckBoxPropName = "batchQueryResultsInDifferentSheets";
		final String includeChartsPropName = "batchQueryIncludeCharts";
		final String splitRunsPropName = "batchQuerySplitRunsInDifferentSheets";
		final String replaceResultsPropName = "batchQueryReplaceResults";

		// determine the proper number of threads to use for queries by
		// checking the configuration parameter which defaults to the
		// total number of cores on the system
		final String coresToUsePropertyName = "coresToUse";
		final int numSystemCores = Runtime.getRuntime().availableProcessors();
		// TODO: set the default cores to use low until we figure out how to get BaseX
		// to
		// perform better with many parallel queries.
		// final int numCoresToUse =
		// Integer.valueOf(prop.getProperty(coresToUsePropertyName,
		// Integer.toString(numSystemCores)));
		final int defaultNumCoresToUse = Integer.valueOf(prop.getProperty(coresToUsePropertyName, Integer.toString(2)));
		prop.setProperty(coresToUsePropertyName, Integer.toString(defaultNumCoresToUse));

		NodeList children = command.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			// TODO: put in a parse filter for this
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			String actionCommand = ((Element) child).getAttribute("name");
			if (actionCommand == null) {
				continue;
			}
			if (actionCommand.equals("XMLDB Batch File")) {
				File queryFile = null;
				Node queriesNode = null;
				File outFile = null;
				String dbFile = null;
				boolean didOpenDB = false;
				List<DataPair<String, String>> scenariosNames = new ArrayList<DataPair<String, String>>();
				boolean singleSheet = Boolean.parseBoolean(prop.getProperty(singleSheetCheckBoxPropName, "false"));
				boolean includeCharts = Boolean.parseBoolean(prop.getProperty(includeChartsPropName, "true"));
				boolean splitRuns = Boolean.parseBoolean(prop.getProperty(splitRunsPropName, "false"));
				boolean replaceResults = Boolean.parseBoolean(prop.getProperty(replaceResultsPropName, "false"));
				int numCoresToUse = defaultNumCoresToUse;
				// read file names for header file, csv files, and the output file
				NodeList fileNameChildren = child.getChildNodes();
				for (int j = 0; j < fileNameChildren.getLength(); ++j) {
					Node fileNode = fileNameChildren.item(j);
					if (fileNode.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					if (fileNode.getNodeName().equals("queryFile")) {
						queryFile = new File(fileNode.getTextContent());
					} else if (fileNode.getNodeName().equals("queries")) {
						queriesNode = fileNode;
					} else if (fileNode.getNodeName().equals("outFile")) {
						outFile = new File(fileNode.getTextContent());
					} else if (fileNode.getNodeName().equals("xmldbLocation")) {
						dbFile = fileNode.getTextContent();
					} else if (fileNode.getNodeName().equals("scenario")) {
						scenariosNames.add(new DataPair<String, String>(((Element) fileNode).getAttribute("name"),
								((Element) fileNode).getAttribute("date")));

					} else if (fileNode.getNodeName().equals(singleSheetCheckBoxPropName)) {
						singleSheet = Boolean.parseBoolean(fileNode.getFirstChild().getNodeValue());
					} else if (fileNode.getNodeName().equals(includeChartsPropName)) {
						includeCharts = Boolean.parseBoolean(fileNode.getFirstChild().getNodeValue());
					} else if (fileNode.getNodeName().equals(splitRunsPropName)) {
						splitRuns = Boolean.parseBoolean(fileNode.getFirstChild().getNodeValue());
					} else if (fileNode.getNodeName().equals(replaceResultsPropName)) {
						replaceResults = Boolean.parseBoolean(fileNode.getFirstChild().getNodeValue());
					} else if (fileNode.getNodeName().equals(coresToUsePropertyName)) {
						numCoresToUse = Integer.parseInt(fileNode.getFirstChild().getNodeValue());
					} else {
						System.out.println("Unknown tag: " + fileNode.getNodeName());
						// should I print this error to the screen?
					}
				}
				try {
					// make sure we have enough to run the batch query
					// which means we have a query file, output file, and
					// a database location
					if ((queryFile == null && queriesNode == null) || outFile == null || dbFile == null) {
						throw new Exception("Not enough information provided to run batch query.");
					}
					// The database may have already been opened by a calling implementation to
					// for instance connect to an in memory database.
					if (XMLDB.getInstance() == null) {
						XMLDB.openDatabase(dbFile);
						didOpenDB = true;
					}

					Vector<ScenarioListItem> scenariosInDb = getScenarios();
					Vector<ScenarioListItem> scenariosToRun = new Vector<ScenarioListItem>();
					if (scenariosNames.isEmpty() && !scenariosInDb.isEmpty()) {
						scenariosToRun.add(scenariosInDb.lastElement());
					} else {
						for (DataPair<String, String> currScn : scenariosNames) {
							String scen = currScn.getKey();
							String date = currScn.getValue();
							if (date == "") {
								date = null; // null date results in match; "" does not
							}
							ScenarioListItem found = ScenarioListItem.findClosestScenario(scenariosInDb, scen, date);
							if (found != null) {
								scenariosToRun.add(found);
							}
						}
					}
					if (scenariosToRun.isEmpty()) {
						throw new Exception("Could not find scenarios to run.");
					}

					// Figure out where to get the queries they may have been specified as a
					// seperate query
					// file we need to load or inline in which case we already have the XML parsed.
					// Note a user can only specify the queries one way or the other not both.
					if (queryFile != null && queriesNode != null) {
						throw new Exception("Setting both a queryFile and inline queries is not allowed.");
					} else if (queryFile != null) {
						// read the batch query file
						queriesNode = readQueries(queryFile).getDocumentElement();
					} else {
						// filter the nodes taken directly from the batch file in the same way they
						// would
						// have been in readQueries since none of the query parsing methods that will
						// look
						// at these nodes are expecting empty text() nodes.
						filterNodes(queriesNode, new ParseFilter());
					}

					final NodeList res = (NodeList) XPathFactory.newInstance().newXPath().evaluate("./aQuery",
							queriesNode, XPathConstants.NODESET);

					final int numQueries = res.getLength();
					if (numQueries == 0) {
						throw new Exception("Could not find queries to run.");
					}
					final Vector<Object[]> toRunScns = new Vector<Object[]>();
					if (!splitRuns) {
						toRunScns.add(scenariosToRun.toArray());
					} else {
						for (Iterator<ScenarioListItem> scnIt = scenariosToRun.iterator(); scnIt.hasNext();) {
							Object[] temp = new Object[1];
							temp[0] = scnIt.next();
							toRunScns.add(temp);
						}
					}

					// Provide the default set of all regions which does not include Global
					Vector<String> allRegions = getRegions();
					allRegions.remove("Global");

					// run the queries and wait for them to finish so that we
					// can close the database
					BatchWindow runner = new BatchWindow(outFile, toRunScns, allRegions, singleSheet, includeCharts,
							numQueries, res, replaceResults, numCoresToUse);
					if (runner != null) {
						runner.waitForFinish();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (didOpenDB) {
						XMLDB.closeDatabase();
					}
				}
			} else {
				System.out.println("Unknown command: " + actionCommand);
			}
		}
	}
}
