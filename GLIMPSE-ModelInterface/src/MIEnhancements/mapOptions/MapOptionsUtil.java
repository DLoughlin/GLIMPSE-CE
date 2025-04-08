package mapOptions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

import filter.FilteredTable;

public class MapOptionsUtil {
	
	public static String getSelectedButton(ButtonGroup myButtonGroup) {
		for (Enumeration<AbstractButton> buttons = myButtonGroup.getElements();buttons.hasMoreElements();) {
			AbstractButton button = buttons.nextElement();
			if (button.isSelected()) {
				return button.getText();
			}
		}
		return null;
	}
	
	public static void resetColorForNonSelectedButtons(ButtonGroup myButtonGroup) {
		for (Enumeration<AbstractButton> buttons = myButtonGroup.getElements();buttons.hasMoreElements();) {
			AbstractButton button = buttons.nextElement();
			if (!button.isSelected()) {
				button.setBackground(new Color(240,240,240));
				button.setForeground(Color.BLACK);
			}
		}
	}
	
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
	
	//YD edits,August-2024
	// add this method to get the scenario list from the table
	
	public static List<String> getScenarioListFromTableData(JTable jtable) {
		
		 ArrayList<String> scenarioListInTable = new ArrayList();
	      int scenarioColIdx = FilteredTable.getColumnByName(jtable,"scenario");
	      for (int i=0;i<jtable.getRowCount();i++) {
	    	  scenarioListInTable.add((String) jtable.getValueAt(i, scenarioColIdx));
	      }
	      List<String> uniqueScenarios = scenarioListInTable.stream().distinct().collect(Collectors.toList());
	      return uniqueScenarios;
	}
	
	
	public static boolean arraysEquals(String[] str1, String[] str2) {
		if(str1.length == str2.length) {
			for(int i=0;i<str1.length;i++) {
				if (str1[i].equals(str2[i])) {
					//do nothing
				}else {
					return false;
				}
			}
			return true;
		}else {
			return false;
		}
	}
	
	public static String getSectorPlusInfo(JTable jtable) {
		//what is the column index for "region"
		int regionIdx = FilteredTable.getColumnByName(jtable, "region");
		int selectedRowIdx = jtable.getSelectedRow();
		//what is the column index for the first year column
		ArrayList<String> yearList = getYearListFromTableData(jtable);
		int firstYearIdx = FilteredTable.getColumnByName(jtable, yearList.get(0));
		String[] infoAtSelectedRow = new String[firstYearIdx-regionIdx-1];
		for (int n = 0; n < firstYearIdx-regionIdx-1; n++) {
		String valsAtSelectedRow = (String) jtable.getValueAt(selectedRowIdx, regionIdx+1+n);
		String myInfo = jtable.getColumnName(regionIdx+1+n).toString()+":"+valsAtSelectedRow;
		infoAtSelectedRow[n] = myInfo;
		}
		String allInfo = String.join(",",infoAtSelectedRow);
		return allInfo;
	}
	
	
	public static List<String> getUniqueRegionsInTable(JTable table) {
	      ArrayList<String> regionListInTable = new ArrayList();
	      int regionColIdx = FilteredTable.getColumnByName(table,"region");
	      for (int i=0;i<table.getRowCount();i++) {
	    	  regionListInTable.add((String) table.getValueAt(i, regionColIdx));
	      }
	      List<String> uniqueRegions = regionListInTable.stream().distinct().collect(Collectors.toList());
	      return uniqueRegions;
	}
	
	
	public static double[] getAbsMinMaxFromTableColumn(JTable jtable, String yearColumnName, boolean normalized) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		double[] minMax = new double[2];
		ArrayList<String> yearList = getYearListFromTableData(jtable);
		int columnIdx = FilteredTable.getColumnByName(jtable, yearColumnName);
		for (int i = 0; i < jtable.getRowCount(); i++) {
				double valueInCell = Double.parseDouble((String)jtable.getValueAt(i, columnIdx));
				if (valueInCell < min) {
					min = (int)Math.floor(valueInCell);
				}
				if(valueInCell > max) {
					max = (int)Math.ceil(valueInCell);
				}	
		}
		if(min==max) {
			minMax[0]=min;
			minMax[1]=max+Math.max(0.1,0.1*min);
		}else {
			if (max>0 & min<0 & normalized) {
				if(Math.abs(min)>= max) {
					max = Math.abs(min);
				}else {
					min = -max;	
			    }
				minMax[0]=min;
				minMax[1]=max;
		    }else {
		    	minMax[0]=min;
				minMax[1]=max;
		    }
	    }
		return minMax;
	}
	
	//YD added, June-2024
		//this method is to find the maximum and minimum values from the JTable
		//round them to the closest integers
		// when min is negative but max is positive
		// we get the absolute values
	public static double[] getAbsMinMaxFromTable(JTable jtable, boolean normalized) {
		double min = Double.MAX_VALUE;
		double max = Double.MAX_VALUE*-1.0;
		double[] minMax = new double[2];
		ArrayList<String> yearList = MapOptionsUtil.getYearListFromTableData(jtable);
		int firstYearIdx = FilteredTable.getColumnByName(jtable, yearList.get(0));
			for (int i = 0; i < jtable.getRowCount(); i++) {
				for (int j=firstYearIdx;j<yearList.size()+firstYearIdx;j++) {
					double valueInCell = Double.parseDouble((String)jtable.getValueAt(i, j));
					if (valueInCell < min) {
						//min = (int)Math.floor(valueInCell);
						min = valueInCell;
					}
					if(valueInCell > max) {
						//max = (int)Math.ceil(valueInCell);
						max = valueInCell;
					}
				}	
			}
			if(min==max) {
				minMax[0]=min;
				minMax[1]=max+Math.max(0.1,0.1*min);
			}else {
				if (max>0 & min<0 & normalized) {
					if(Math.abs(min)>= max) {
						max = Math.abs(min);
					}else {
						min = -max;	
				    }
					minMax[0]=min;
					minMax[1]=max;
			    }else {
			    	minMax[0]=min;
					minMax[1]=max;
			    }
		    }
		return minMax;
	}
		
	public static HashMap<String,Double> getTableDataForStateOrCountry(JTable jtable, String yearCol,String scenarioStr) {
		
		//check how many unique states in the region column first
		HashMap<String,Double> dataForState= new HashMap<>();
		List<String> regionsInTable = getUniqueRegionsInTable(jtable);
		//what is the column index for "region"
		
		int regionIdx = FilteredTable.getColumnByName(jtable, "region");
		int yearIdx = FilteredTable.getColumnByName(jtable, yearCol);
		//YD edits,August-2024, consider that there are multiple scenarios in a single table
		List<String> scenarioList = getScenarioListFromTableData(jtable);
		//System.out.println("MapOptionsUtil::getTableDataForState: scenarioStr: " + scenarioStr);
		//what is the column index for the first year column
		ArrayList<String> yearList = getYearListFromTableData(jtable);
		int firstYearIdx = FilteredTable.getColumnByName(jtable, yearList.get(0));
		int idxDiff = firstYearIdx-regionIdx;
		// if there are other columns in between the "region" column and the first year column
		// and only one scenario in the table
		boolean noRowSelected = jtable.getSelectionModel().isSelectionEmpty();
		if (idxDiff > 1 && scenarioList.size()==1 && !noRowSelected) { //YD edits
			int selectedRowIdx = jtable.getSelectedRow();
			String[] valsAtSelectedRow = new String[firstYearIdx-regionIdx-1];
			for (int n = 0; n < firstYearIdx-regionIdx-1; n++) {
				valsAtSelectedRow[n] = (String) jtable.getValueAt(selectedRowIdx, regionIdx+1+n);
			}
			//System.out.println("MapOptionsUtil::getTableDataForState: valsAtSelectedRow: " + valsAtSelectedRow[0]);
			for (int i = 0; i < jtable.getRowCount(); i++) {
				//only need to check those rows with the same values from the column after "region" 
				//and before the first year column
				String[] valsAtThisRow = new String[firstYearIdx-regionIdx-1];
				for (int n = 0; n < firstYearIdx-regionIdx-1; n++) {
					valsAtThisRow[n] = (String) jtable.getValueAt(i, regionIdx+1+n);
				}
				boolean theSame = arraysEquals(valsAtThisRow,valsAtSelectedRow);
				//if this row's "technology" and/or "sector"  are the same as the selected row
				//then keep this row 
				if (theSame) {
					String regionStr = (String) jtable.getValueAt(i, regionIdx);
					double valForYear = Double.parseDouble((String) jtable.getValueAt(i, yearIdx));
					dataForState.put(regionStr, valForYear);
				}
			}
		}else if (idxDiff ==1 && scenarioList.size()==1){ //YD edits
			for (int n=0; n<jtable.getRowCount();n++) {
				String regionStr = (String) jtable.getValueAt(n, regionIdx);
				double valForYear = Double.parseDouble((String) jtable.getValueAt(n, yearIdx));
				dataForState.put(regionStr, valForYear);
			}
		}else if(scenarioList.size()>1 && !noRowSelected) {
			int selectedRowIdx = jtable.getSelectedRow();
			String[] valsAtSelectedRow = new String[firstYearIdx-regionIdx-1];
			for (int n = 0; n < firstYearIdx-regionIdx-1; n++) {
				valsAtSelectedRow[n] = (String) jtable.getValueAt(selectedRowIdx, regionIdx+1+n);
			}
			//System.out.println("MapOptionsUtil::getTableDataForState: valsAtSelectedRow: " + valsAtSelectedRow[0]);
			for (int i = 0; i < jtable.getRowCount(); i++) {
				//YD Mar-2025 added,check if the scenario match the selected scenario first
				String scenarioAtThisRow = (String)jtable.getValueAt(i, 0);
				if (scenarioAtThisRow.equals(scenarioStr)) {
					//only need to check those rows with the same values from the column after "region" 
					//and before the first year column
					//System.out.println("MapOptionsUtil::getTableDataForState: scenarioStr:inside the scenario match loop now... " + scenarioStr);
					String[] valsAtThisRow = new String[firstYearIdx-regionIdx-1];
					for (int n = 0; n < firstYearIdx-regionIdx-1; n++) {
						valsAtThisRow[n] = (String) jtable.getValueAt(i, regionIdx+1+n);
					}
					boolean theSame = arraysEquals(valsAtThisRow,valsAtSelectedRow);
					//if this row's "technology" and/or "sector"  are the same as the selected row
					//then keep this row 
					if (theSame) {
						String regionStr = (String) jtable.getValueAt(i, regionIdx);
						double valForYear = Double.parseDouble((String) jtable.getValueAt(i, yearIdx));
						dataForState.put(regionStr, valForYear);
					}
				}
			} //for loop end	
		}else if(scenarioList.size()>1 && noRowSelected) {
			//System.out.println("MapOptionsUtil::getTableDataForState: scenarioStr:inside the else loop now... " + scenarioStr);
			for (int n=0; n<jtable.getRowCount();n++) {
				String scenarioAtThisRow = (String)jtable.getValueAt(n, 0);
				if (scenarioAtThisRow.equals(scenarioStr)) {
				String regionStr = (String) jtable.getValueAt(n, regionIdx);
				double valForYear = Double.parseDouble((String) jtable.getValueAt(n, yearIdx));
				dataForState.put(regionStr, valForYear);
				}
			}	
		}//check "idxDiff" loop end
		return dataForState;
	}
	
	//YD added,Apr-2024 this method is to get the FeatureCollection
    // from a set of shapeFiles
	public static FeatureCollection getCollectionFromShape(String shpFilePath){
		ShapefileDataStore store = null;
		FeatureCollection<SimpleFeatureType,SimpleFeature> featureCollection = null;
		try {
			File shpFile = new File(shpFilePath);
			shpFile.setReadOnly();
			store = new ShapefileDataStore(shpFile.toURI().toURL());
			String typeName =store.getTypeNames()[0];
			//System.out.println("inside getCollectionFromShape method now, open shapefile datasource:"+typeName);
			FeatureSource featureSource = store.getFeatureSource(typeName);
		    // how to get features from featureSource ?
			
			SimpleFeatureType schema = (SimpleFeatureType) featureSource.getSchema();
			featureCollection = featureSource.getFeatures(); 
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally {
			store.dispose();		
		}
	    return featureCollection;
	}
	
	//YD added,Apr-2024 this method is to remove some features from a FeatureCollection
    // based on the featureIDs stored in String[]
	public static FeatureCollection removeFeaturesFromCollection(FeatureCollection myCollection,String[] featuresToRemove){
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
		Set<FeatureId> fidSet = new HashSet<>();
		for (int n=0;n<featuresToRemove.length;n++) {
			fidSet.add(ff.featureId(featuresToRemove[n]));
		}
		Filter myFilter = ff.not(ff.id(fidSet));
	    FeatureCollection<SimpleFeatureType, SimpleFeature> filteredCollection = myCollection.subCollection(myFilter);
	    return filteredCollection;
	}
	
	public static Color findStateColorFromMapColor(MapColor mapColor, double val) {
		 Color myColor = null; //default color for missing data
		//check mapColor class here
		if (mapColor != null) {
			try {
				double[] colorIntervals = mapColor.getIntervals();
				if (val>colorIntervals[colorIntervals.length-1]) {
					myColor = mapColor.getColor(colorIntervals.length-1);
				}else if (val>=colorIntervals[0] & val<=colorIntervals[colorIntervals.length-1]) {
					for (int i=0;i<colorIntervals.length-1;i++) {
						
						if (val >= colorIntervals[i] & val < colorIntervals[i+1]) {
							myColor = mapColor.getColor(i);
							break;
						}
					}
				}else if (val<colorIntervals[0]) {
					myColor = mapColor.getColor(0);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	  } // if loop end
		return myColor;
    }
	//YD edits "findStateColorFromMapColor" end
	
}