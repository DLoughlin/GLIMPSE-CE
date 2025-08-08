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
* GLIMPSE-CE is a derivative of the open-source USEPA GLIMPSE software.
* For the GLIMPSE project, GCAM development, data processing, and support for 
* policy implementations has been led by Dr. Steven J. Smith of PNNL, via Interagency 
* Agreements 89-92423101 and 89-92549601. Contributors from PNNL include 
* Maridee Weber, Catherine Ledna, Gokul Iyer, Page Kyle, Marshall Wise, Matthew 
* Binsted, and Pralit Patel. 
* The lead GLIMPSE & GLIMPSE- CE developer is Dr. Dan Loughlin (formerly USEPA). 
* Contributors include Tai Wu (USEPA), Farid Alborzi (ORISE), and Aaron Parks and 
* Yadong Xu of ARA through the EPA Environmental Modeling and Visualization 
* Laboratory contract.  
* 
*/
package glimpseBuilder;

import java.util.ArrayList;

import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;

public class CsvFileWriter {
	protected GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	protected GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	protected GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	protected GLIMPSEUtils utils = GLIMPSEUtils.getInstance();
	private static CsvFileWriter instance = null;

	ArrayList<String> csvColumnList = null;
	ArrayList<String> dataList = null;
	ArrayList<String> csvList = null;
	//String csvColumnFilename = "./GLIMPSE_CSV_columns.txt";

	// GLIMPSEUtils utils = GLIMPSEUtils.getInstance();
	// GLIMPSEFiles files = GLIMPSEFiles.getInstance();

	public static void main(String[] args) {
		CsvFileWriter writer = new CsvFileWriter();
		//writer.test();
	}

	private CsvFileWriter() {
	}

	public static CsvFileWriter getInstance() {
		if (instance == null) {
			instance = new CsvFileWriter();
		}
		return instance;
	}



	public ArrayList<String> createCsvContent(ArrayList<String> colList, ArrayList<String> dataList) {
		ArrayList<String> arrayList = new ArrayList<String>();

		String sectorText = utils.getMatch(dataList, "sector", ";");
		String subsectorText = utils.getMatch(dataList, "subsector", ";");
		String technologyText = utils.getMatch(dataList, "technology", ";");
		String inputText = utils.getStringUpToChar(utils.getMatch(dataList, "input", ";"), ")");
		String outputText = utils.getStringUpToChar(utils.getMatch(dataList, "output", ";"), ")");
		String paramText = utils.getMatch(dataList, "param", ";");
		String param2Text = utils.getMatch(dataList, "param2", ";");
		String[] yearsText = utils.getMatches(dataList, "year", ";", ",");
		String[] valuesText = utils.getMatches(dataList, "value", ";", ",");
		String[] regionsText = utils.getMatches(dataList, "region", ";", ",");
		String dollarYearText = utils.getMatch(dataList, "dollarYear", ",");

		if (paramText.equals("Capital Cost"))
			valuesText = utils.convertTo1990Dollars(valuesText, dollarYearText);

		String comboText = sectorText + "/" + paramText;
		int no_years = yearsText.length;
		int no_regions = regionsText.length;

		String headerText = utils.getMatch(colList, comboText, ";");
		System.out.println("Echo header text: " + headerText);
		String[] headerAndColNames = headerText.split(",");

		String[][] csvTable = new String[no_years * no_regions + 1][headerAndColNames.length - 1];
		int row = 0;

		for (int reg = 0; reg < no_regions; reg++) {
			for (int yr = 0; yr < no_years; yr++) {
				row++;
				//TODO:  .equals may be better used with compareTo
				for (int c = 1; c < headerAndColNames.length; c++) {
					if (headerAndColNames[c].equals("region"))
						csvTable[row][c - 1] = regionsText[reg];
					if (headerAndColNames[c].equals("year"))
						csvTable[row][c - 1] = yearsText[yr];
					if (headerAndColNames[c].equals("from-to")) {
						int year = 0;
						int year2 = 0;
						try {
							year = Integer.parseInt(yearsText[yr]) - 5;
							year2 = year + 5;
						} catch (Exception e2) {
							System.out.println("Error translating year in CSV file. Attempting to continue.");
						}
						csvTable[row][c - 1] = "" + year + "," + year2;
					}
					if (headerAndColNames[c].equals("data"))
						csvTable[row][c - 1] = valuesText[yr];
					if (headerAndColNames[c].indexOf("/") > 0)
						csvTable[row][c - 1] = headerAndColNames[c].substring(headerAndColNames[c].indexOf("/") + 1);
					if (headerAndColNames[c].equals("sector"))
						csvTable[row][c - 1] = sectorText;
					if (headerAndColNames[c].equals("subsector"))
						csvTable[row][c - 1] = subsectorText;
					if (headerAndColNames[c].equals("technology"))
						csvTable[row][c - 1] = technologyText;
					if (headerAndColNames[c].equals("fuel"))
						csvTable[row][c - 1] = inputText;
					if (headerAndColNames[c].equals("market"))
						csvTable[row][c - 1] = regionsText[reg];
					if (headerAndColNames[c].equals("species"))
						csvTable[row][c - 1] = param2Text;
				}
			}
		}

		for (int c = 1; c < headerAndColNames.length; c++) {
			String temp_txt = headerAndColNames[c];
			System.out.println("c " + c + " " + temp_txt);
			if (temp_txt.trim().equals("from-to")) {
				temp_txt = "from-year,to-year";

				System.out.println(" ---->" + temp_txt);
			}
			if (temp_txt.indexOf("/") > 0)
				temp_txt = temp_txt.substring(0, temp_txt.indexOf("/"));
			csvTable[0][c - 1] = temp_txt;
		}

		// construct content of CSV file

		arrayList.add("INPUT_TABLE");
		arrayList.add("Variable ID");
		arrayList.add(headerAndColNames[0]);
		arrayList.add("");

		for (int r = 0; r < csvTable.length; r++) {
			String line = "";
			for (int c = 0; c < csvTable[r].length; c++) {
				if (c == 0) {
					line += csvTable[r][c];
				} else {
					line += "," + csvTable[r][c];
				}
			}
			arrayList.add(line);
		}

		// this.printArrayList(arrayList);
		return arrayList;
	}

//	private void test() {
//		dataList = getTestData();
//		csvColumnList = files.getStringArrayFromFile(csvColumnFilename, "#");
//		csvList = createCsvContent(csvColumnList, dataList);
//		utils.printArrayListToStdout(csvList);
//	}

//	private ArrayList<String> getTestData() {
//		//TODO:  Could we read this from somewhere?
//		ArrayList<String> arrayList = new ArrayList<String>();
//		// first item should correspond to name in header file
//		arrayList.add("region;AL,NC,MD");
//		arrayList.add("sector;electricity");
//		arrayList.add("subsector;coal");
//		arrayList.add("technology;coal (IGCC)");
//		arrayList.add("fuel;regional coal");
//		arrayList.add("year;2020,2025,2030");
//		arrayList.add("value;0.5,0.55,0.56");
//		arrayList.add("param;Efficiency");
//		return arrayList;
//	}

}
