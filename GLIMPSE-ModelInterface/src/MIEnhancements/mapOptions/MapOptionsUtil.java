package mapOptions;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.*;
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

/**
 * Utility class for map options and table data operations.
 * Provides methods for extracting and manipulating table data, color mapping, and shapefile features.
 */
public class MapOptionsUtil {
    /**
     * Returns the text of the selected button in a ButtonGroup.
     * @param myButtonGroup the ButtonGroup to check
     * @return the text of the selected button, or null if none selected
     */
    public static String getSelectedButton(ButtonGroup myButtonGroup) {
        for (Enumeration<AbstractButton> buttons = myButtonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }

    /**
     * Resets color for all non-selected buttons in a ButtonGroup.
     * @param myButtonGroup the ButtonGroup to update
     */
    public static void resetColorForNonSelectedButtons(ButtonGroup myButtonGroup) {
        for (Enumeration<AbstractButton> buttons = myButtonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (!button.isSelected()) {
                button.setBackground(new Color(240, 240, 240));
                button.setForeground(Color.BLACK);
            }
        }
    }

    /**
     * Extracts a list of year column names from a JTable.
     * Only columns whose names can be parsed as a double are considered years.
     * @param jtable the JTable to analyze
     * @return list of year strings
     */
    public static ArrayList<String> getYearListFromTableData(JTable jtable) {
        int nCols = jtable.getColumnCount();
        ArrayList<String> yearList = new ArrayList<>();
        for (int j = 0; j < nCols; j++) {
            String cls = jtable.getColumnName(j);
            try {
                Double myYear = Double.parseDouble(cls);
                String yearStr = String.valueOf(myYear.intValue());
                yearList.add(yearStr);
            } catch (Exception e) {
                // Not a year column, skip
            }
        }
        return yearList;
    }

    /**
     * Gets the unique scenario list from a JTable.
     * @param jtable the JTable to analyze
     * @return list of unique scenario strings
     */
    public static List<String> getScenarioListFromTableData(JTable jtable) {
        ArrayList<String> scenarioListInTable = new ArrayList<>();
        int scenarioColIdx = FilteredTable.getColumnByName(jtable, "scenario");
        for (int i = 0; i < jtable.getRowCount(); i++) {
            scenarioListInTable.add((String) jtable.getValueAt(i, scenarioColIdx));
        }
        return scenarioListInTable.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Checks if two string arrays are equal.
     * @param str1 first array
     * @param str2 second array
     * @return true if arrays are equal, false otherwise
     */
    public static boolean arraysEquals(String[] str1, String[] str2) {
        if (str1.length == str2.length) {
            for (int i = 0; i < str1.length; i++) {
                if (!str1[i].equals(str2[i])) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets sector plus info from the selected row in a JTable.
     * Returns a comma-separated string of column:value pairs for columns between 'region' and the first year column.
     * @param jtable the JTable to analyze
     * @return comma-separated info string
     */
    public static String getSectorPlusInfo(JTable jtable) {
        int regionIdx = FilteredTable.getColumnByName(jtable, "region");
        int selectedRowIdx = jtable.getSelectedRow();
        ArrayList<String> yearList = getYearListFromTableData(jtable);
        int firstYearIdx = FilteredTable.getColumnByName(jtable, yearList.get(0));
        String[] infoAtSelectedRow = new String[firstYearIdx - regionIdx - 1];
        for (int n = 0; n < firstYearIdx - regionIdx - 1; n++) {
            String valsAtSelectedRow = (String) jtable.getValueAt(selectedRowIdx, regionIdx + 1 + n);
            String myInfo = jtable.getColumnName(regionIdx + 1 + n) + ":" + valsAtSelectedRow;
            infoAtSelectedRow[n] = myInfo;
        }
        return String.join(",", infoAtSelectedRow);
    }

    /**
     * Gets unique regions from a JTable.
     * @param table the JTable to analyze
     * @return list of unique region strings
     */
    public static List<String> getUniqueRegionsInTable(JTable table) {
        ArrayList<String> regionListInTable = new ArrayList<>();
        int regionColIdx = FilteredTable.getColumnByName(table, "region");
        for (int i = 0; i < table.getRowCount(); i++) {
            regionListInTable.add((String) table.getValueAt(i, regionColIdx));
        }
        return regionListInTable.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Gets absolute min and max values from a table column.
     * @param jtable the JTable to analyze
     * @param yearColumnName the column name to check
     * @param normalized whether to normalize min/max
     * @return array with min and max values
     */
    public static double[] getAbsMinMaxFromTableColumn(JTable jtable, String yearColumnName, boolean normalized) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        double[] minMax = new double[2];
        int columnIdx = FilteredTable.getColumnByName(jtable, yearColumnName);
        for (int i = 0; i < jtable.getRowCount(); i++) {
            double valueInCell = Double.parseDouble((String) jtable.getValueAt(i, columnIdx));
            if (valueInCell < min) {
                min = (int) Math.floor(valueInCell);
            }
            if (valueInCell > max) {
                max = (int) Math.ceil(valueInCell);
            }
        }
        if (min == max) {
            minMax[0] = min;
            minMax[1] = max + Math.max(0.1, 0.1 * min);
        } else {
            if (max > 0 && min < 0 && normalized) {
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

    /**
     * Finds absolute min and max values from all year columns in a JTable.
     * @param jtable the JTable to analyze
     * @param normalized whether to normalize min/max
     * @return array with min and max values
     */
    public static double[] getAbsMinMaxFromTable(JTable jtable, boolean normalized) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double[] minMax = new double[2];
        ArrayList<String> yearList = getYearListFromTableData(jtable);
        int firstYearIdx = FilteredTable.getColumnByName(jtable, yearList.get(0));
        for (int i = 0; i < jtable.getRowCount(); i++) {
            for (int j = firstYearIdx; j < yearList.size() + firstYearIdx; j++) {
                double valueInCell = Double.parseDouble((String) jtable.getValueAt(i, j));
                if (valueInCell < min) {
                    min = valueInCell;
                }
                if (valueInCell > max) {
                    max = valueInCell;
                }
            }
        }
        if (min == max) {
            minMax[0] = min;
            minMax[1] = max + Math.max(0.1, 0.1 * min);
        } else {
            if (max > 0 && min < 0 && normalized) {
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

    /**
     * Gets table data for each state or country for a given year and scenario.
     * Handles multiple scenarios and selected rows.
     * @param jtable the JTable to analyze
     * @param yearCol the year column name
     * @param scenarioStr the scenario string
     * @return map of region to value
     */
    public static HashMap<String, Double> getTableDataForStateOrCountry(JTable jtable, String yearCol, String scenarioStr) {
        HashMap<String, Double> dataForState = new HashMap<>();
        List<String> regionsInTable = getUniqueRegionsInTable(jtable);
        int regionIdx = FilteredTable.getColumnByName(jtable, "region");
        int yearIdx = FilteredTable.getColumnByName(jtable, yearCol);
        List<String> scenarioList = getScenarioListFromTableData(jtable);
        ArrayList<String> yearList = getYearListFromTableData(jtable);
        int firstYearIdx = FilteredTable.getColumnByName(jtable, yearList.get(0));
        int idxDiff = firstYearIdx - regionIdx;
        boolean noRowSelected = jtable.getSelectionModel().isSelectionEmpty();
        // Only one scenario and a row is selected
        if (idxDiff > 1 && scenarioList.size() == 1 && !noRowSelected) {
            int selectedRowIdx = jtable.getSelectedRow();
            String[] valsAtSelectedRow = new String[firstYearIdx - regionIdx - 1];
            for (int n = 0; n < firstYearIdx - regionIdx - 1; n++) {
                valsAtSelectedRow[n] = (String) jtable.getValueAt(selectedRowIdx, regionIdx + 1 + n);
            }
            for (int i = 0; i < jtable.getRowCount(); i++) {
                String[] valsAtThisRow = new String[firstYearIdx - regionIdx - 1];
                for (int n = 0; n < firstYearIdx - regionIdx - 1; n++) {
                    valsAtThisRow[n] = (String) jtable.getValueAt(i, regionIdx + 1 + n);
                }
                boolean theSame = arraysEquals(valsAtThisRow, valsAtSelectedRow);
                if (theSame) {
                    String regionStr = (String) jtable.getValueAt(i, regionIdx);
                    double valForYear = 0;
                    try {
                        valForYear = Double.parseDouble((String) jtable.getValueAt(i, yearIdx));
                    } catch (Exception e) {
                        valForYear = 0;
                    }
                    dataForState.put(regionStr, valForYear);
                }
            }
        } else if (idxDiff == 1 && scenarioList.size() == 1) {
            for (int n = 0; n < jtable.getRowCount(); n++) {
                String regionStr = (String) jtable.getValueAt(n, regionIdx);
                double valForYear = Double.parseDouble((String) jtable.getValueAt(n, yearIdx));
                dataForState.put(regionStr, valForYear);
            }
        } else if (scenarioList.size() > 1 && !noRowSelected) {
            int selectedRowIdx = jtable.getSelectedRow();
            String[] valsAtSelectedRow = new String[firstYearIdx - regionIdx - 1];
            for (int n = 0; n < firstYearIdx - regionIdx - 1; n++) {
                valsAtSelectedRow[n] = (String) jtable.getValueAt(selectedRowIdx, regionIdx + 1 + n);
            }
            for (int i = 0; i < jtable.getRowCount(); i++) {
                String scenarioAtThisRow = (String) jtable.getValueAt(i, 0);
                if (scenarioAtThisRow.equals(scenarioStr)) {
                    String[] valsAtThisRow = new String[firstYearIdx - regionIdx - 1];
                    for (int n = 0; n < firstYearIdx - regionIdx - 1; n++) {
                        valsAtThisRow[n] = (String) jtable.getValueAt(i, regionIdx + 1 + n);
                    }
                    boolean theSame = arraysEquals(valsAtThisRow, valsAtSelectedRow);
                    if (theSame) {
                        String regionStr = (String) jtable.getValueAt(i, regionIdx);
                        double valForYear = Double.parseDouble((String) jtable.getValueAt(i, yearIdx));
                        dataForState.put(regionStr, valForYear);
                    }
                }
            }
        } else if (scenarioList.size() > 1 && noRowSelected) {
            for (int n = 0; n < jtable.getRowCount(); n++) {
                String scenarioAtThisRow = (String) jtable.getValueAt(n, 0);
                if (scenarioAtThisRow.equals(scenarioStr)) {
                    String regionStr = (String) jtable.getValueAt(n, regionIdx);
                    double valForYear = Double.parseDouble((String) jtable.getValueAt(n, yearIdx));
                    dataForState.put(regionStr, valForYear);
                }
            }
        }
        return dataForState;
    }

    /**
     * Gets a FeatureCollection from a shapefile path.
     * @param shpFilePath the shapefile path
     * @return FeatureCollection from the shapefile
     */
    public static FeatureCollection getCollectionFromShape(String shpFilePath) {
        ShapefileDataStore store = null;
        FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = null;
        try {
            File shpFile = new File(shpFilePath);
            shpFile.setReadOnly();
            store = new ShapefileDataStore(shpFile.toURI().toURL());
            String typeName = store.getTypeNames()[0];
            FeatureSource featureSource = store.getFeatureSource(typeName);
            featureCollection = featureSource.getFeatures();
        } catch (IOException e1) { // IOException covers MalformedURLException
            e1.printStackTrace();
        } finally {
            if (store != null) {
                store.dispose();
            }
        }
        return featureCollection;
    }

    /**
     * Removes features from a FeatureCollection based on feature IDs.
     * @param myCollection the original FeatureCollection
     * @param featuresToRemove array of feature IDs to remove
     * @return filtered FeatureCollection
     */
    public static FeatureCollection removeFeaturesFromCollection(FeatureCollection myCollection, String[] featuresToRemove) {
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        Set<FeatureId> fidSet = new HashSet<>();
        for (String featureId : featuresToRemove) {
            fidSet.add(ff.featureId(featureId));
        }
        Filter myFilter = ff.not(ff.id(fidSet));
        FeatureCollection<SimpleFeatureType, SimpleFeature> filteredCollection = myCollection.subCollection(myFilter);
        return filteredCollection;
    }

    /**
     * Finds the color for a state based on value and map color intervals.
     * @param mapColor the MapColor object
     * @param val the value to map
     * @return the corresponding Color, or null if not found
     */
    public static Color findStateColorFromMapColor(MapColor mapColor, double val) {
        Color myColor = null; // Default color for missing data
        if (mapColor != null) {
            try {
                double[] colorIntervals = mapColor.getIntervals();
                if (val > colorIntervals[colorIntervals.length - 1]) {
                    myColor = mapColor.getColor(colorIntervals.length - 1);
                } else if (val >= colorIntervals[0] && val <= colorIntervals[colorIntervals.length - 1]) {
                    for (int i = 0; i < colorIntervals.length - 1; i++) {
                        if (val >= colorIntervals[i] && val < colorIntervals[i + 1]) {
                            myColor = mapColor.getColor(i);
                            break;
                        }
                    }
                } else if (val < colorIntervals[0]) {
                    myColor = mapColor.getColor(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return myColor;
    }
}