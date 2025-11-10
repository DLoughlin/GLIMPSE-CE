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
package chartOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Handles exporting tabular data to an Excel file (.xlsx) using Apache POI.
 * <p>
 * Usage examples:
 * <ul>
 *   <li>ExportExcel(fileName, columns, rows)</li>
 *   <li>ExportExcel(chartName, rows, columns)</li>
 *   <li>ExportExcel(fileName, columns, rows, title, metaCol, meta, unit)</li>
 * </ul>
 * </p>
 *
 * Author: TWU
 * Created: 1/2/2016
 */
public class ExportExcel {

    private FileInputStream fis;
    private FileOutputStream fos;
    private XSSFWorkbook book;
    private XSSFSheet sheet;
    private Map<String, Object[]> newData;
    private int key;
    private String path;
    private boolean debug = true;

    /**
     * Constructor for exporting data with column headers and rows.
     * @param fileName Path to the Excel file.
     * @param col Column headers.
     * @param rs Data rows.
     */
    public ExportExcel(String fileName, Object col[], String rs[][]) {
        init(fileName);
        packSingleRow(col);
        packContent(rs);
        writetofile();
    }

    /**
     * Constructor for exporting chart data with chart name, rows, and columns.
     * @param chartName Name of the chart.
     * @param rs Data rows.
     * @param col Column headers.
     */
    public ExportExcel(String chartName, String rs[][], Object col[]) {
        packSingleRow(new String[] {chartName});
        packSingleRow(col);
        packContent(rs);
        writetofile();
    }
    
    /**
     * Constructor for exporting data with metadata and units.
     * @param fileName Path to the Excel file.
     * @param col Column headers.
     * @param rs Data rows.
     * @param title Title for the sheet.
     * @param metaCol Metadata column names (comma-separated).
     * @param meta Metadata values (space-separated).
     * @param unit Unit description.
     */
    public ExportExcel(String fileName, String col[], String rs[][], String title, String metaCol, String meta,
            String unit) {
        if (debug)
            System.out.println("ExportExcel::col: " + Arrays.toString(col));
        init(fileName);
        packSingleRow(new Object[] { "TITLE:  " + title });
        packMeta(metaCol, meta);
        packSingleRow(new Object[] { "UNIT:  " + unit });
        packSingleRow(new Object[] { " " });
        packSingleRow(col);
        packContent(rs);
        writetofile();
    }

    /**
     * Packs metadata rows into the Excel sheet.
     * @param metaCol Comma-separated metadata column names.
     * @param meta Space-separated metadata values.
     */
    private void packMeta(String metaCol, String meta) {
        String[] mCol = metaCol.split(",");
        String[] mData = meta.split(" ");
        for (int i = 0; i < mCol.length; i++) {
            packSingleRow(new Object[] { mCol[i].trim().toUpperCase() + ":  " + mData[i] });
        }
    }

    /**
     * Packs a single row of data into the newData map.
     * @param obj Array of objects representing a row.
     */
    private void packSingleRow(Object[] obj) {
        if (debug)
            System.out.println("ExportExcel::packSingleRow:key: " + key + " obj: " + Arrays.toString(obj));
        newData.put(String.valueOf(key), obj);
        key++;
    }

    /**
     * Packs multiple rows of data into the newData map.
     * @param rs 2D array of data rows.
     */
    private void packContent(String[][] rs) {
        for (int i = 0; i < rs.length - 1; i++) {
            packSingleRow(rs[i]);
        }
    }

    /**
     * Initializes the Excel workbook and sheet, creating or opening the file as needed.
     * @param fileName Path to the Excel file.
     */
    private void init(String fileName) {
        if (fileName.equals("")) {
            fileName = FileUtil.getSaveFilePathFromChooser("excel file (*.xlsx)", "xlsx");
            if (!fileName.toLowerCase().endsWith(".xlsx"))
                fileName += ".xlsx";
        }

        File excel = new File(fileName);
        try {
            if (excel.length() > 0) {
                fis = new FileInputStream(excel);
                book = new XSSFWorkbook(fis);
                sheet = book.getSheetAt(0);
            } else {
                fos = new FileOutputStream(excel);
                book = new XSSFWorkbook();
                sheet = book.createSheet();
            }
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        path = fileName;
        newData = new HashMap<String, Object[]>();
        key = sheet.getLastRowNum();
        if (debug)
            System.out.print("ExportExcel::init:key: " + key);
    }

    /**
     * Reads data from the Excel file into a map.
     * @return Map of row numbers to row data arrays.
     */
    protected Map<String, Object[]> readFromfile() {
        Iterator<Row> itr = sheet.iterator();
        while (itr.hasNext()) {
            Row row = itr.next();
            Object[] obj = new Object[row.getLastCellNum()];
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (cell.getCellType()) {
                    case STRING:
                        if (debug)
                            System.out.print(cell.getStringCellValue() + "\t");
                        obj[cell.getColumnIndex()] = cell.getStringCellValue();
                        break;
                    case NUMERIC:
                        if (debug)
                            System.out.print(cell.getNumericCellValue() + "\t");
                        obj[cell.getColumnIndex()] = cell.getNumericCellValue();
                        break;
                    case BOOLEAN:
                        if (debug)
                            System.out.print(cell.getBooleanCellValue() + "\t");
                        obj[cell.getColumnIndex()] = cell.getBooleanCellValue();
                        break;
                    default:
                        // Other cell types are ignored
                }
            }
            newData.put(String.valueOf(row.getRowNum()), obj);
            if (debug)
                System.out.println("");
        }
        return newData;
    }

    /**
     * Writes the packed data to the Excel file and opens it.
     */
    private void writetofile() {
        try {
            sheet = book.getSheetAt(0);
            int rownum = sheet.getLastRowNum();
            Set<String> newRows = newData.keySet();
            Iterator<String> itr = newRows.iterator();
            while (itr.hasNext()) {
                String dataRow = itr.next();
                Row row = sheet.createRow(rownum + Integer.valueOf(dataRow.trim()).intValue());
                Object[] objArr = newData.get(dataRow);
                int cellnum = 0;
                for (Object obj : objArr) {
                    Cell cell = row.createCell(cellnum++);
                    if (obj instanceof String) {
                        cell.setCellValue((String) obj);
                    } else if (obj instanceof Boolean) {
                        cell.setCellValue((Boolean) obj);
                    } else if (obj instanceof Date) {
                        cell.setCellValue((Date) obj);
                    } else if (obj instanceof Double) {
                        cell.setCellValue((Double) obj);
                    }
                }
                if (debug)
                    System.out.println("ExportExcel::writetofile:key: " + dataRow + " obj: " + Arrays.toString(objArr));
            }
            book.write(fos);
            if (debug)
                System.out.println("Writing on Excel file Finished ...");
            fos.close();
            ShowDocument.openURL((new StringBuilder("file:///")).append(path).toString());
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

}