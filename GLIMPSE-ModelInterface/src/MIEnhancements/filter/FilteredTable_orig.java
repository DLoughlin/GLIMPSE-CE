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
* Parks and Yadong Xu of ARA through the EPA�s Environmental Modeling and 
* Visualization Laboratory contract. 
* 
*/
package filter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.geotools.swing.tool.AbstractZoomTool;
import org.geotools.swing.tool.PanTool;
import org.geotools.swing.tool.ZoomInTool;
import org.geotools.swing.tool.ZoomOutTool;
import org.geotools.util.CheckedHashSet;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import ModelInterface.InterfaceMain;
import ModelInterface.ModelGUI2.DbViewer;
import chart.LegendUtil;
import chartOptions.SelectDecimalFormat;
import graphDisplay.StateMapPanel;
import graphDisplay.ModelInterfaceUtil;
import graphDisplay.SankeyDiagramFromTable;
import graphDisplay.Thumbnail;
import graphDisplay.WorldMapPanel;

/**
 * The class to handle a JTable filtered by meta data of other JTable, then
 * displaying on the splitpane.
 * 
 * Author Action Date Flag
 * ======================================================================= TWU
 * created 1/2/2016
 */

public class FilteredTable_orig {

	private TableModel tableModel;
	private TableRowSorter<TableModel> sorter;
	private int doubleIndex;
	private String[][] newData;
	private JTable jtable;
	private JSplitPane sp;
	private String[] tableColumnData;
	private Thumbnail tn;
	private StateMapPanel mp;
	private WorldMapPanel worldMap;
	private SankeyDiagramFromTable sankeyP;
	private boolean debug = false;
	private int sigfigs = 3;

	public FilteredTable_orig(Map<String, String> sel, String chartName, String[] unit, String path,
			final JTable jTable, JSplitPane sp) {

		this.sp = sp;
		JPanel jp = new JPanel((new BorderLayout()));
		Component c = sp.getRightComponent();
		if (c != null)
			sp.remove(c);

		if (sel == null)
			Var.origYRange = ModelInterfaceUtil.getColumnFromTable(jTable, 0);

		tableColumnData = ModelInterfaceUtil.getColumnFromTable(jTable, 4);

		String[] cls = new String[tableColumnData.length];
		for (int j = 0; j < tableColumnData.length; j++) {
			//cls[j] = jTable.getColumnClass(j).getName();
		    cls[j] = jTable.getColumnName(j);
		}

		doubleIndex = ModelInterfaceUtil.getDoubleTypeColIndex(cls);
		String[] qualifier = ModelInterfaceUtil.getColumnFromTable(jTable, 5);
		ArrayList<String> al = new ArrayList<String>();
		ArrayList<Integer> alI = new ArrayList<Integer>();

		Integer[] tableColumnIndex = getTableColumnIndex(sel);

		if (debug)
			System.out.println("FilteredTable: colidx: " + Arrays.toString(tableColumnIndex));

		for (int i = 0; i < doubleIndex; i++) {
			al.add(tableColumnData[i]);
			alI.add(new Integer(i));
		}
	
		for (int i = 0; i < tableColumnIndex.length; i++) {
			al.add(tableColumnData[tableColumnIndex[i]]);
			alI.add(new Integer(tableColumnIndex[i]));
		}

		al.add(tableColumnData[tableColumnData.length - 1]);
		alI.add(new Integer(tableColumnData.length - 1));

		if (debug) {
			System.out.println("FilteredTable: col: " + Arrays.toString(tableColumnData));
			System.out.println("FilteredTable: colidx: " + Arrays.toString(alI.toArray(new Integer[0])));
		}

		String[][] tData = getTableData(jTable, alI.toArray(new Integer[0]));
		Comparator<String> columnDoubleComparator =
			    (String v1, String v2) -> {
			    
			    	Double val1=null;
			    	try {
					    //cast v1 to double
					    val1=Double.parseDouble(v1);
			    	}catch(NumberFormatException e) {
			    	}
			    	Double val2=null;
			    	try {
					    //cast v2 to double
					    val2=Double.parseDouble(v2);
					 	
				    }catch(NumberFormatException e) {
				    	//only care that it happened
				    }
			    	if(val1==null && val2==null) {
			    		return 0;
			    	}else if(val1==null) {
			    		return 1;
			    	}else if(val2==null) {
			    		return -1;
			    	}else {
			    		return Double.compare(val1, val2);
			    	}
    		


			};
		
		if (sel == null || sel.isEmpty())
			newData = tData.clone();
		else
			newData = getfilterTableData(tData, getFilterData(qualifier, sel));
		try {
			DefaultTableModel dtm = new DefaultTableModel(newData, al.toArray(new String[0])) {

				@Override
				public boolean isCellEditable(int row, int column) {
				//all cells false
				return false;
				}
				};
			//jtable = new JTable(newData, al.toArray(new String[0]));// tableColumnData);
			jtable=new JTable(dtm);
			jtable.setDragEnabled(true);

			//Dan: testing some font options to avoid scaling problems; setup below works on EPA VM
			//jtable.setFont(jtable.getFont().deriveFont(14F));
			jtable.setRowHeight(jtable.getFont().getSize()+5);
			
			tableModel = jtable.getModel();
			sorter = new TableRowSorter<TableModel>(tableModel);
			jtable.setRowSorter(sorter);
			// add custom sorters to columns that are numbers
			for (int colC = 0; colC < jtable.getColumnCount(); colC++) {
				String clsName = jtable.getColumnName(colC);
				boolean isDouble = false;
				try {
					Double.parseDouble(clsName);
					//if we get here it is a numeric col
					//jtable.getColumnModel().getColumn(colC).setCo
					sorter.setComparator(colC, columnDoubleComparator);
					
					//tc.setCom
				} catch (Exception e) {
					;
				}
			}
			// TableColumnModelListener tableColumnModelListener = new
			// TableSorterColumnModelListener();
			// jtable.getTableHeader().getColumnModel().addColumnModelListener(tableColumnModelListener);
		} catch (Exception e) {
			System.out.println("FilteredTable Caught: ");
			e.printStackTrace();
		}

		Box box = Box.createHorizontalBox();
		
		
		JButton jb = new JButton("Filter");
		jb.setBackground(LegendUtil.getRGB(-8205574));
		java.awt.event.MouseListener ml = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				new FilterTreePane(chartName, unit, path, jTable, sel, sp);
			}
		};
		jb.addMouseListener(ml);
		box.add(jb);

		jb = new JButton("Graph");
		jb.setBackground(LegendUtil.getRGB(-8205574));
		java.awt.event.MouseListener ml1 = new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				if (debug)
					System.out.println("FilteredTable: graph press: " + chartName + " " + Arrays.toString(unit) + " "
							+ path + " " + doubleIndex + " " + jtable.getColumnCount() + "  " + jtable.getRowCount());
				if (tn == null) {
					//need to modify to get correct units for thumbnail
					Map<String, Integer[]> metaMap = ModelInterfaceUtil.getMetaIndex2(jtable, doubleIndex);
					HashMap<String, String> unitsMap = ModelInterfaceUtil.getUnitDataFromTableByLastNamedCol(jTable);

					tn = new Thumbnail(chartName, unit, path, doubleIndex, jtable, metaMap, sp, unitsMap);
				}
				JPanel jp = tn.getJp();
				if (jp != null)
					setRightComponent(jp);
				else {
					tn = null;
					System.gc();
				}
			}
		};
		jb.addMouseListener(ml1);
		box.add(jb);
		
		JLabel s=new JLabel(" ");
		box.add(s);
		
		//YD added,Apr-2024
		jb = new JButton("Mapping");
		jb.setBackground(LegendUtil.getRGB(-8205574));
		jb.setToolTipText("Beta feature");
		Font f=jb.getFont();
		Font f1=new Font(f.getFontName(),Font.ITALIC,f.getSize());
		jb.setFont(f1);
		
		//jb.setEnabled(InterfaceMain.enableMapping);
		java.awt.event.MouseListener mlmap = new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				if (debug) {
					//System.out.println("FilteredTable: mapping press: " + chartName + " " + Arrays.toString(units[0]) + " "
					//		+ path + " " + doubleIndex + " " + jtable.getColumnCount() + "  " + jtable.getRowCount());
				}
				//if (mp == null) {
					Map<String, Integer[]> metaMap = ModelInterfaceUtil.getMetaIndex2(jtable, doubleIndex);
					HashMap<String,String> unitsMap=ModelInterfaceUtil.getUnitDataFromTableByLastNamedCol(jtable);
					boolean checkStates = checkContainAnyState(jtable);
					boolean checkCountries = checkContainAnyCountryRegion(jtable);
					boolean noRowSelected = jtable.getSelectionModel().isSelectionEmpty();
					boolean containOtherColumns = checkContainOtherColumns(jtable);
					
					if (checkStates & !checkCountries) {
						if (noRowSelected&containOtherColumns) {
							JOptionPane.showMessageDialog(null, "Please select a row in the table first.");
							return;	
						}else {
							mp = new StateMapPanel(chartName,jtable);
						}
					}else if (checkCountries & !checkStates) {
						if (noRowSelected&containOtherColumns) {
							JOptionPane.showMessageDialog(null, "Please select a row in the table first.");
							return;	
						}else {
							boolean statesIncluded = false;	
						    worldMap = new WorldMapPanel(chartName,jtable,statesIncluded);
						}	
					}else if(checkCountries & checkStates) {
						if (noRowSelected&containOtherColumns) {
							JOptionPane.showMessageDialog(null, "Please select a row in the table first.");
							return;	
						}else {
							boolean statesIncluded = true;
							worldMap = new WorldMapPanel(chartName,jtable,statesIncluded);
						}
					}
				}
				
			//}
		};
		jb.addMouseListener(mlmap);
		if(InterfaceMain.enableMapping) {
			box.add(jb);
		}
		//YD edits end
		
		jb = new JButton("Sankey");
		jb.setBackground(LegendUtil.getRGB(-8205574));
		jb.setToolTipText("Beta feature");
		jb.setFont(f1);
		
		java.awt.event.MouseListener mlSankey = new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				//if (debug)
					//System.out.println("FilteredTable: Sankey press: " + chartName + " " + Arrays.toString(units[0]) + " "
					//		+ path + " " + doubleIndex + " " + jtable.getColumnCount() + "  " + jtable.getRowCount());
				
				//if (sankeyP == null) {
					
					boolean noRowSelected = jtable.getSelectionModel().isSelectionEmpty();
					boolean containOtherColumns = checkContainOtherColumns(jtable);
					
						if (!containOtherColumns) {
							JOptionPane.showMessageDialog(null, "the query results cannot generate a flow dataset.");
							return;	
						}else {
					     try {
							sankeyP = new SankeyDiagramFromTable(chartName,jtable);
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						}
					
				//}
				
			}
		};
		jb.addMouseListener(mlSankey);
		if(InterfaceMain.enableSankey) {
			box.add(jb);
		}
		
		jb = new JButton("Format");
		jb.setBackground(LegendUtil.getRGB(-8205574));
		java.awt.event.MouseListener ml2 = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				String[][] data = getTableData(jTable, alI.toArray(new Integer[0]));

				if (sel == null || sel.isEmpty())
					newData = data.clone();
				else
					newData = getfilterTableData(data, getFilterData(qualifier, sel));

				Object dataValue[][] = new SelectDecimalFormat(newData, doubleIndex).getDataValue();
				for (int i = 0; i < jtable.getRowCount(); i++)
					for (int j = doubleIndex; j < jtable.getColumnCount() - 1; j++)
						jtable.setValueAt(dataValue[i][j], i, j);
			}
		};
		jb.addMouseListener(ml2);
		// box.add(jb);

		box.setSize(new Dimension(80, 20));

		jp.add(box, BorderLayout.NORTH);
		jp.add(new JScrollPane(jtable), BorderLayout.CENTER);
		jp.updateUI();

		c = sp.getLeftComponent();
		if (c != null)
			sp.remove(c);

		sp.setLeftComponent(jp);
		if (debug)
			System.out.println("FilteredTable::FilteredTable:max memory " + Runtime.getRuntime().maxMemory()
					+ " total: " + Runtime.getRuntime().totalMemory() + " free: " + Runtime.getRuntime().freeMemory());
		
		
		
	}
	
//	private void modifyDataToSigFigs(JTable jTable,ArrayList<Integer> alI,Map<String, String> sel,String[] qualifier) {
//		String[][] data = getTableData(jTable, alI.toArray(new Integer[0]));
//
//		if (sel == null || sel.isEmpty())
//			newData = data.clone();
//		else
//			newData = getfilterTableData(data, getFilterData(qualifier, sel));
//
//		String dataValue[][] = new setToSigFigs(newData, doubleIndex);
//		for (int i = 0; i < jtable.getRowCount(); i++) {
//			for (int j = doubleIndex; j < jtable.getColumnCount() - 1; j++) {
//				jtable.setValueAt(dataValue[i][j], i, j);		
//			}
//		}
//	}
//	
//	public static String setToSigFig(double value, int significantDigits) {
//	    if (significantDigits < 0) throw new IllegalArgumentException();
//
//	    BigDecimal bd = new BigDecimal(value, MathContext.DECIMAL64);
//	    bd = bd.round(new MathContext(significantDigits, RoundingMode.HALF_UP));
//	    final int precision = bd.precision();
//	    if (precision < significantDigits)
//	    bd = bd.setScale(bd.scale() + (significantDigits-precision));
//	    return bd.toPlainString();
//	}
//	

	private Integer[] getTableColumnIndex(Map<String, String> sel) {

		Integer[] tableColumnIndex = null;
		Map<String, Integer> tableColumnDataIndex = new LinkedHashMap<String, Integer>();

		if (sel != null && !sel.isEmpty()) {
			String[] keys = sel.keySet().toArray(new String[0]);

			for (int k = 0; k < keys.length; k++) {
				String[] temp = keys[k].split("\\|");
				if (temp[0].contains("Year")) {
					tableColumnDataIndex.put(temp[1],
							Integer.valueOf(Arrays.asList(tableColumnData).indexOf(temp[1].trim())));
					if (debug)
						System.out.println("FilteredTable::getTableColumnIndex:col " + temp[0] + "  " + temp[1] + "  "
								+ Arrays.toString(tableColumnData));

				}
			}
			String[] k = tableColumnDataIndex.keySet().toArray(new String[0]);
			Var.sectionYRange = k.clone();
		}

		if (!tableColumnDataIndex.isEmpty())
			tableColumnIndex = tableColumnDataIndex.values().toArray(new Integer[0]);
		else {
			if (Var.sectionYRange == null)
				Var.sectionYRange = Var.defaultYRange.clone();

			ArrayList<Integer> temp = new ArrayList<Integer>();
			for (int k = 0; k < Var.sectionYRange.length; k++) {
				int i = Arrays.asList(Var.origYRange).indexOf(Var.sectionYRange[k]);
				if (i > -1)
					temp.add(Arrays.asList(Var.origYRange).indexOf(Var.sectionYRange[k]));
			}

			tableColumnIndex = new Integer[temp.size()];
			for (int k = 0; k < tableColumnIndex.length; k++)
				tableColumnIndex[k] = doubleIndex + temp.get(k);
		}

		Arrays.sort(tableColumnIndex);

		if (debug)
			System.out.println("FilteredTable::getTableColumnIndex::col" + Arrays.toString(tableColumnIndex) + " sec: "
					+ Arrays.toString(Var.sectionYRange));
		return tableColumnIndex;
	}
	
	//YD added,May-2024,this method is to check if there are other columns inbetween "region" column and the first year column
	private boolean checkContainOtherColumns(JTable jtable) {
	boolean containOtherColumns = true;
	int regionIdx = getColumnByName(jtable, "region");
	ArrayList<String> yearList = getYearListFromTableData(jtable);
	int firstYearIdx = FilteredTable.getColumnByName(jtable, yearList.get(0));
	int idxDiff = firstYearIdx-regionIdx;
	if (idxDiff==1) {
		containOtherColumns = false;
	}
	return containOtherColumns;
	}
	
	//YD added, May-2024
	//this method is to get the year list to put in the dropdown menu in the toolbar
	public static ArrayList<String> getYearListFromTableData(JTable jtable) {
			
			int nCols = jtable.getColumnCount();
			//find out how many column names are query results for the years first
			ArrayList<String> yearList = new ArrayList<String>();
			for (int j = 0; j < nCols; j++) {
						
				String cls = jtable.getColumnName(j);
				boolean isDouble=false;
				try {
					Double myYear = Double.parseDouble(cls);
					isDouble=true;
					String yearStr = String.valueOf(myYear.intValue());
					yearList.add(yearStr);
				} catch (Exception e) {
							;
				}
			}
			return yearList;
		}
	
	//YD added,Apr-2024,this method is to check if jtable "region" column has states info
	private boolean checkContainAnyState(JTable table) {
	       boolean containStates = false;
	       String stringStates = "AK,AL,AR,AZ,CA,CO,CT,DC,DE,FL,GA,HI,IA,ID,IL,IN,KS,KY,LA,MA,MD,ME,MI,MN,MO,MS,MT,NC,ND,NE,NH,NJ,NM,NV,NY,OH,OK,OR,PA,RI,SC,SD,TN,TX,UT,VA,VT,WA,WI,WV,WY";
	       Integer[] cols = new Integer[1];
	       int regionColIdx = getColumnByName(table,"region");
	       cols[0]= regionColIdx;
	       String[][] regionColData = getTableData(table,cols);
	       String[] regions = new String[regionColData.length];
	       for (int i=0; i<regionColData.length;i++) {
	    	   regions[i] = regionColData[i][0];
	       }
	       Set regionSet = new HashSet(Arrays.asList(regions));
	       String[] uniqueRegions = null;
	       if (regions.length == regionSet.size()) {
	    	   // all regions are distinct elements
	    	    uniqueRegions = regions;
	       }else {
	    	   uniqueRegions = new String[regionSet.size()];
	    	   regionSet.toArray(uniqueRegions);
	       }
	       containStates = stringContainsItemFromArray(stringStates,uniqueRegions);
	       return containStates;
	}
	
	//YD added,July-2024,this method is to check if jtable "region" column has countries info
		private boolean checkContainAnyCountryRegion(JTable table) {
			   boolean verbose=false;
		       boolean containCountry = false;
		       String[] stringCountries = {/*"USA",*/"Africa_Eastern","Africa_Northern","Africa_Southern","Africa_Western","Australia_NZ",
		    		   "Brazil","Canada","Central America and Caribbean","Central Asia","China","EU_12","EU_15","Europe_Eastern","Europe_Non_EU",
		    		   "European Free Trade Association","India","Indonesia","Japan","Mexico","Middle East","Pakistan","Russia","South Africa",
		    		   "South America_Northern","South America_Southern","South Asia","South Korea","Southeast Asia","Taiwan","Argentina","Colombia"};
		       Integer[] cols = new Integer[1];
		       int regionColIdx = getColumnByName(table,"region");
		       cols[0]= regionColIdx;
		       String[][] regionColData = getTableData(table,cols);
		       String[] regions = new String[regionColData.length];
		       for (int i=0; i<regionColData.length;i++) {
		    	   regions[i] = regionColData[i][0];
		       }
		       //System.out.println("in checkContainAnyCountry: "+regions.length);
		       Set regionSet = new HashSet(Arrays.asList(regions));
		       String[] uniqueRegions = null;
		       if (regions.length == regionSet.size()) {
		    	   // all regions are distinct elements
		    	    uniqueRegions = regions;
		       }else {
		    	   uniqueRegions = new String[regionSet.size()];
		    	   regionSet.toArray(uniqueRegions);
		       }
		       if (verbose) System.out.println("in checkContainAnyCountry: "+uniqueRegions.length);
		       containCountry = arrayContainsItemFromArray(stringCountries,uniqueRegions);
		       if (verbose) System.out.println("in checkContainAnyCountry: "+containCountry);
		   return containCountry;
		}
	
		
	//YD added, Apr-2024,this method is to get the column index by match the column name
	public static int getColumnByName(JTable table, String name) {
			for (int i=0;i<table.getColumnCount();++i)
				if (table.getColumnName(i).equals(name))
					return i;
			return -1;
	}
	//YD added, Apr-2024,this method is to check if a string contains any of the strings from an array
	private boolean stringContainsItemFromArray(String inputStr,String[] items) {
		return Arrays.stream(items).anyMatch(inputStr::contains);
	}
	
	private boolean arrayContainsItemFromArray(String[] arrayStr,String[] items) {
		List<String> itemsAsList = Arrays.asList(items);
		for (int i=0; i<arrayStr.length;i++) {
			if (itemsAsList.contains(arrayStr[i])) {
				return true;
			}
		}
		return false;
	}
	
		//YD edits end

	private String[][] getTableData(JTable jtable, Integer[] col) {
		if (debug)
			System.out.println("FilteredTable::getTableData: colIdx: " + Arrays.toString(col));
		String[][] tData = new String[jtable.getRowCount()][col.length];
		for (int i = 0; i < jtable.getRowCount(); i++) {
			for (int j = 0; j < col.length; j++) {
				//Dan modified to work with new Diff Results panel
				//String cls = jtable.getColumnClass(col[j].intValue()).getName();
				String cls = jtable.getColumnName(col[j].intValue());
				boolean isDouble = false;
				try {
					Double.parseDouble(cls);
					isDouble = true;
				} catch (Exception e) {
					;
				}
				
				if (debug)
					System.out.println(
							"FilteredTable:getTableData: colData: " + jtable.getValueAt(i, col[j]) + "  " + cls);
				if (isDouble) {
					Double val=null;
					try {
						val=Double.parseDouble(jtable.getValueAt(i, col[j].intValue()).toString());
					}catch(NumberFormatException e){
						//slightly redundant, null anyway
						val=null;
					}
					if(val.isInfinite() || val.isNaN()) {
						val=null;
					}
						//double d = ((Double) jtable.getValueAt(i, col[j].intValue())).doubleValue();
					if(val==null /*|| Math.random()<0.5*/) {
						tData[i][j]="N/A";
					}else {
						tData[i][j] = toSigFigs(val,sigfigs);
					}
				} else {
					tData[i][j] = (String) jtable.getValueAt(i, col[j].intValue());
				}
			}
		}
		return tData;
	}
	
	public static String toSigFigs(double value, int significantDigits) {
	    if (significantDigits < 0) throw new IllegalArgumentException();
	    if(DbViewer.disable3Digits) {
	    	Double d=value;
	    	return d.toString();
	    }

	    // this is more precise than simply doing "new BigDecimal(value);"
	    
	    BigDecimal bd = new BigDecimal(0.0);
	    try{
	    	bd=new BigDecimal(value, MathContext.DECIMAL64);
	    }catch(Exception e) {
	    	System.out.println("Could not create Decimal: "+e.toString());
	    }
	    bd = bd.round(new MathContext(significantDigits, RoundingMode.HALF_UP));
	    final int precision = bd.precision();
	    if (precision < significantDigits)
	    bd = bd.setScale(bd.scale() + (significantDigits-precision));
	    return bd.toPlainString();
	}

	public void setRightComponent(JPanel jpc) {

		if (debug)
			System.out.println("FilteredTable::setRightComponent: jpc: " + jpc.getName());

		JScrollPane chartScrollPane = new JScrollPane(jpc);
		chartScrollPane.getViewport().setBackground(Color.cyan);// jpc.getBackground());
		if (sp.getRightComponent() != null)
			sp.remove(sp.getRightComponent());
		sp.setRightComponent(chartScrollPane);
		sp.setDividerLocation(0.678);
		sp.updateUI();
	}

	private String[][] getfilterTableData(String[][] source, ArrayList<String[]> filter) {
		ArrayList<String[]> al = new ArrayList<String[]>();

		for (int i = 0; i < source.length; i++) {
			boolean found = false;

			for (int j = 0; j < filter.size(); j++) {
				for (int k = 0; k < filter.get(j).length; k++) {
					//since units are at end, handle them differntly
					if(j==filter.size()-1) {
						if (source[i][source[0].length-1].trim().equals(filter.get(j)[k].trim())) {
							found = true;
							break;
						} else
							found = false;
					}else if (source[i][j].trim().equals(filter.get(j)[k].trim())) {
						found = true;
						break;
					} else
						found = false;
				}
				if (!found)
					break;
			}
			if (found) {
				al.add(source[i]);
				if (debug)
					System.out.println("getfilterTableData: " + i + "  " + Arrays.toString(source[i]));
			}
		}
		return al.toArray(new String[0][0]);
	}

	private ArrayList<String[]> getFilterData(String[] qualifier, Map<String, String> sel) {
		ArrayList<String[]> filter = new ArrayList<String[]>();
		String[] s = sel.values().toArray(new String[0]);

		for (int j = 0; j < qualifier.length; j++) {
			String key = qualifier[j].trim();
			ArrayList<String> uni = new ArrayList<String>();
			for (int i = 0; i < s.length; i++) {
				String[] temp = s[i].split("\\|");
				String q = temp[0].trim();
				if (debug)
					System.out.println("FilteredTable::getfilterData:QualiferIndex: " + i + " key: " + key + " sel: "
							+ Arrays.toString(temp));
				if (q.equals(key))
					if (!uni.contains(temp[1].trim()))
						uni.add(temp[1].trim());
			}

			if (debug)
				System.out.println("FilteredTable::getfilterData:RowIndex: " + j + "  "
						+ Arrays.toString(uni.toArray(new String[0])));
			filter.add(j, uni.toArray(new String[0]));
		}
		return filter;
	}

	/*
	private class TableSorterColumnModelListener implements TableColumnModelListener {

		@Override
		public void columnAdded(TableColumnModelEvent arg0) {
		}

		@Override
		public void columnMarginChanged(ChangeEvent arg0) {
		}

		@Override
		public void columnMoved(TableColumnModelEvent e) {
			TableColumnModel columnModel = (TableColumnModel) e.getSource();
			int end = ModelInterfaceUtil.getDoubleTypeColIndex(tableModel);
			int[] columns = new int[end];
			for (int i = 0; i < end; i++)
				columns[i] = columnModel.getColumn(i).getModelIndex();
			Arrays.sort(columns);
			setSortingStatus(columnModel, columns, 1);
		}

		@Override
		public void columnRemoved(TableColumnModelEvent arg0) {
		}

		@Override
		public void columnSelectionChanged(ListSelectionEvent arg0) {
		}

		public void setSortingStatus(TableColumnModel columnModel, int[] columns, int status) {
			List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
			for (int i = 0; i < columns.length; i++) {
				int column = columnModel.getColumn(columns[i]).getModelIndex();
				sortKeys.add(new RowSorter.SortKey(column, SortOrder.ASCENDING));
			}
			sorter.setSortKeys(sortKeys);
			sorter.sort();
		}
	}*/

}
