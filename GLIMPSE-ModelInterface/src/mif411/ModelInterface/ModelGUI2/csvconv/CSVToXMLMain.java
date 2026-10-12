/*
* LEGAL NOTICE
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
package ModelInterface.ModelGUI2.csvconv;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import ModelInterface.ModelGUI2.csvconv.DOMTreeBuilder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A stand alone driver to run the CSV to XML conversion tool. This class serves
 * as both a static utility to to run the conversion as well as a main command
 * line program. The commandline expects at least three arguments the first of
 * which is one or more CSV files, next the header file and finally where to
 * write the resulting XML file.
 *
 * @author Pralit Patel...Modified by Dan Loughlin of EPA
 */
public class CSVToXMLMain {
	private static String firstRowFailureSummary = null;
	private static int rowErrorCount = 0;

	public static void main(String[] args) {
		if (args.length < 3) {
		}
		try {
			if (args.length == 1) {
				// Assuming we are running a batch file
				System.out.println("Running batch file: " + args[0]);
				runFromBatch(new File(args[0]));
			} else if (args.length >= 3) {
				// Assuming we are getting the conversion file names directly from the argument
				// list
				File xmlOutputFile = new File(args[args.length - 1]);
				File headerFile = new File(args[args.length - 2]);
				File[] csvFiles = new File[args.length - 2];
				for (int i = 0; i < args.length - 2; ++i) {
					csvFiles[i] = new File(args[i]);
				}

				Document doc = null;

				try {
					doc = runCSVConversion(csvFiles, headerFile, null);
				} catch (Exception ex) {
					System.out.println("error " + ex);
					System.out.println("Document model was not created successfully.");
				}
				if (doc != null)
					writeFile(xmlOutputFile, doc);

			} else {
				System.err.println("Usage: CSVToXMLMain <CSV file> [<CSV file> ..] <header file> <output XML file>");
				System.err.println("   or: CSVToXMLMain <batch file>");
				System.exit(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// System.exit(1);
		}
	}

	/**
	 * Takes a CSV file, and Headers file, then processes the files by building a
	 * new tree with the DOMTreeBuilder class. The resulting XML document is
	 * returned
	 * 
	 * @param csvFiles    the CSV files
	 * @param headerFile  the Headers file
	 * @param parentFrame A GUI frame which may be used to display error messages to
	 *                    if it is not null.
	 * @return The generated XML dom document
	 */
	public static Document runCSVConversion(File[] csvFiles, File headerFile, JFrame parentFrame) {
		clearFailureSummaries();
		StringTokenizer st;
		String intValueStr = null;
		String strToReplace;
		int counter;
		int dollarindex = 0;
		String inputLine = "";

		ArrayList<String> dataArr;
		HashMap<String, String> nickNameMap = new HashMap<String, String>(); // shortname -> long string to
		// append to end
		HashMap<String, String> tableIDMap = new HashMap<String, String>(); // tableID -> long string of headers
		DOMTreeBuilder tree = new DOMTreeBuilder();

		try {

			FileInputStream hashfis = new FileInputStream(headerFile);
			DataInputStream hashfin = new DataInputStream(hashfis);
			BufferedReader hashInput = new BufferedReader(new InputStreamReader(hashfin));
			inputLine = readNextMeaningfulLine(hashInput);
			if (inputLine == null) {
				throw new IllegalArgumentException("Header file is empty or contains only comments/blank lines: "
						+ headerFile.getAbsolutePath());
			}
			while (inputLine != null && inputLine.length() > 0 && inputLine.charAt(0) == '$') { // read in
				// header
				// nick
				// names
				st = new StringTokenizer(inputLine, ",", false);
				intValueStr = st.nextToken(); // $nickname
				inputLine = inputLine.substring(intValueStr.length() + 1).trim();
				nickNameMap.put(intValueStr, inputLine);
				if ((inputLine = hashInput.readLine()) != null) {
					inputLine = inputLine.trim();
				}
			}
			while (inputLine != null) {
				if ((!inputLine.equals(""))&&(!inputLine.startsWith("#"))) {
					st = new StringTokenizer(inputLine, ",", false);
					intValueStr = st.nextToken(); // numID
					inputLine = inputLine.substring(intValueStr.length() + 1); // everything
					// but
					// numID
					try {

						inputLine = inputLine.replaceAll("[,][\\s]*[,]", ""); // gets

						// Dan added this to get rid of multiple commas from exporting CSV from Excel
						inputLine = inputLine.replaceAll(",,", "");
						// rid
						// of
						// end
						// commas
						if (inputLine.endsWith(",")) { // gets ride of last
							// comma if there is one
							inputLine = inputLine.substring(0, inputLine.length() - 1);
						} // extra commas are now all gone

						dollarindex = 0;
						while ((dollarindex = inputLine.indexOf('$')) != -1) {
							counter = dollarindex;
							while (counter < inputLine.length() && inputLine.charAt(counter) != ',') {
								counter++;
							}
							strToReplace = inputLine.substring(dollarindex, counter);
							if (nickNameMap.containsKey(strToReplace)) {
								// strToReplace = strToReplace.substring(1);
								// strToReplace = "^[.]*"+strToReplace+"[.]*$";
								inputLine = inputLine.replaceAll("\\" + strToReplace, nickNameMap.get(strToReplace));
							} else {
								System.out.println("***Couldn't find replacement for " + strToReplace + "!***");
								if (parentFrame != null) {
									JOptionPane.showMessageDialog(parentFrame,
											"Couldn't find replacement for " + strToReplace, "Warning",
											JOptionPane.WARNING_MESSAGE);
								}
							}
						}
						tableIDMap.put(intValueStr, inputLine);
					} catch (NumberFormatException e) {
						System.out.println("*** Hashtable file formatted incorrectly ***" + e);
						if (parentFrame != null) {
							JOptionPane.showMessageDialog(parentFrame, "Hashtable file formatted incorrectly\n" + e,
									"Exception", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				if ((inputLine = hashInput.readLine()) != null) {
					inputLine = inputLine.trim();
				}
			}

			if (tableIDMap.isEmpty()) {
				throw new IllegalArgumentException(
						"No header mappings were found in header file: " + headerFile.getAbsolutePath()
						+ ". Expected lines like '<tableId>,<header mapping>' (for example 'testHeader,scenario,scenario/world,...').");
			}

			// tableIDMap should now be all set up ...

			for (int j = 0; j < csvFiles.length; ++j) {
				final File currentCsvFile = csvFiles[j];
				final int[] csvLineNumber = new int[] { 0 };
				FileInputStream fis = new FileInputStream(csvFiles[j]);
				DataInputStream fin = new DataInputStream(fis);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(fin));

				String rawLine = readLineWithCounter(stdInput, csvLineNumber);
				inputLine = rawLine == null ? null : rawLine.trim(); // read one line of input

				// Dan added to address issue of CSV-written files having lines ending in commas

				while (inputLine != null) {

					while ((inputLine != null) && (!inputLine.contains("INPUT_TABLE")) && (inputLine.startsWith("#"))) {
						inputLine = readLineWithCounter(stdInput, csvLineNumber);
					}
					if (inputLine == null) {
						break;
					}
					readLineWithCounter(stdInput, csvLineNumber); // reads/ignores "Variable ID" line
					rawLine = readLineWithCounter(stdInput, csvLineNumber);
					inputLine = rawLine == null ? null : rawLine.trim(); // should have just the

					inputLine = inputLine.replaceAll(",,", "");
					if (inputLine.endsWith(","))
						inputLine = inputLine.substring(0, inputLine.length() - 1);

					// id number
					st = new StringTokenizer(inputLine, ",", false);
					intValueStr = st.nextToken();

					if (tableIDMap.containsKey(intValueStr)) {
						String resolvedHeader = tableIDMap.get(intValueStr);
						try {
							tree.setHeader(resolvedHeader);
						} catch (Exception e) {
							String message = "Failed to parse header mapping for headerId '" + intValueStr + "' in "
									+ currentCsvFile.getAbsolutePath() + ". Header text: " + resolvedHeader;
							System.out.println(message);
							e.printStackTrace();
							throw new IllegalArgumentException(message, e);
						}
						readLineWithCounter(stdInput, csvLineNumber); // ignores this line
						readLineWithCounter(stdInput, csvLineNumber); // ignores header line

						inputLine = readLineWithCounter(stdInput, csvLineNumber);
						if (inputLine != null) {
							inputLine.trim(); // start reading in
						}
						// data
						while (inputLine != null && !inputLine.equals("") && inputLine.charAt(0) != ',') {
							st = new StringTokenizer(inputLine, ",", false);
							int NUM_COLS = st.countTokens();
							dataArr = new ArrayList<String>(NUM_COLS);
							for (int i = 0; i < NUM_COLS; i++) {

								String str = st.nextToken().trim();
								// System.out.println("processing col "+i+" of "+NUM_COLS+" ..." +str);
								dataArr.add(i, str);
							} // one line of data stores in arraylist

							// Dan: Hack that replicates entry for all states if 1st item (region) is
							// "AllUSStates"
							String str = dataArr.get(0);
							if (str.indexOf(":") > -1) {

								String[] regions = str.split(":");

								for (int k = 0; k < regions.length; k++) {
									dataArr.set(0, regions[k]);
									try {
										tree.addToTree(dataArr);
									} catch (Exception e) {
										logCsvRowError(currentCsvFile, csvLineNumber[0], intValueStr, tableIDMap.get(intValueStr), inputLine, dataArr, e);
										e.printStackTrace();
									}
								}

							} else {
								// System.out.println(inputLine+": "+inputLine);
								try {
									tree.addToTree(dataArr);
								} catch (Exception e) {
									logCsvRowError(currentCsvFile, csvLineNumber[0], intValueStr, tableIDMap.get(intValueStr), inputLine, dataArr, e);
									e.printStackTrace();
								}

							}
							// makeTree( rootElement, docName );
							dataArr.clear();
							if ((inputLine = readLineWithCounter(stdInput, csvLineNumber)) != null) {
								inputLine.trim();
								// System.out.println(inputLine);
							}
						}
					} else {
						System.out.println("***Warning: skipping table: " + intValueStr + "!***");
					}

					if ((inputLine = readLineWithCounter(stdInput, csvLineNumber)) != null) {
						inputLine.trim();

					}
				}
				fin.close();
				hashfin.close();
			}

			return tree.getDoc();

		} catch (Exception e) {
			if (firstRowFailureSummary == null || firstRowFailureSummary.trim().isEmpty()) {
				firstRowFailureSummary = "Processing stopped due to a critical error: " + e.getClass().getSimpleName()
						+ (e.getMessage() == null ? "" : " - " + e.getMessage());
			}
			System.out.println("Using header " + intValueStr);
			System.out.println("inputLine: " + inputLine);
			System.out.println("Exception thrown while trying to read csv and header files");
			e.printStackTrace();

//            if(parentFrame != null) {
//                JOptionPane.showMessageDialog(parentFrame,
//                        "Excpetion thrown while trying to read csv and header files\n"
//                        + e, "Exception", JOptionPane.ERROR_MESSAGE);
//            }
			return null;
		}
	}

	private static synchronized void clearFailureSummaries() {
		firstRowFailureSummary = null;
		rowErrorCount = 0;
	}

	private static synchronized void registerRowError() {
		rowErrorCount++;
	}

	private static void logCsvRowError(File csvFile, int csvLineNumber, String headerId, String resolvedHeader, String line,
			ArrayList<String> dataArr, Exception ex) {
		registerRowError();
		ArrayList<String> headerArgs = splitCsvArgs(resolvedHeader);
		ArrayList<String> lineArgs = splitCsvArgs(line);
		int expectedCount = headerArgs.size();
		int actualCount = lineArgs.size();
		int likelyFailingColumn = findLikelyFailingColumnIndex(headerArgs, dataArr, ex);

		System.out.println("--------- CSV to XML row failure ---------");
		if (csvFile != null) {
			System.out.println("Source: " + csvFile.getAbsolutePath() + (csvLineNumber > 0 ? " (line " + csvLineNumber + ")" : ""));
		}
		System.out.println("Header ID: " + headerId);
		System.out.println("Exception: " + ex.getClass().getSimpleName()
				+ (ex.getMessage() == null ? "" : ": " + ex.getMessage()));
		System.out.println("CSV row column count: " + actualCount + " | Expected by header mapping: " + expectedCount);
		if (likelyFailingColumn >= 0) {
			String expectedHeader = likelyFailingColumn < headerArgs.size() ? headerArgs.get(likelyFailingColumn) : "<unknown header>";
			String actualValue = likelyFailingColumn < dataArr.size() ? dataArr.get(likelyFailingColumn) : "<missing value>";
			System.out.println("Likely failing column index: " + likelyFailingColumn
					+ " | expected: " + expectedHeader
					+ " | actual: " + actualValue);
		}
		System.out.println("How to fix:");
		ArrayList<String> hints = buildRowFixHints(expectedCount, actualCount, likelyFailingColumn, headerArgs);
		for (int i = 0; i < hints.size(); i++) {
			System.out.println("  - " + hints.get(i));
		}
		System.out.println("Resolved header: " + resolvedHeader);
		System.out.println("Row data: " + line);
		System.out.println("Header/line argument mapping:\n" + formatHeaderLineArgMapping(resolvedHeader, line));
		if (firstRowFailureSummary == null || firstRowFailureSummary.trim().isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("First failing row in ")
					.append(csvFile == null ? "<unknown file>" : csvFile.getAbsolutePath());
			if (csvLineNumber > 0) {
				sb.append(" at line ").append(csvLineNumber);
			}
			sb.append(". Header ID: ").append(headerId)
					.append(". Columns found: ").append(actualCount)
					.append(", expected: ").append(expectedCount).append('.');
			if (likelyFailingColumn >= 0) {
				sb.append(" Failing column index: ").append(likelyFailingColumn).append('.');
				if (likelyFailingColumn < headerArgs.size()) {
					sb.append(" Expected token: ").append(headerArgs.get(likelyFailingColumn)).append('.');
				}
				if (likelyFailingColumn < dataArr.size()) {
					sb.append(" Actual value: ").append(dataArr.get(likelyFailingColumn)).append('.');
				}
			}
			sb.append(" Error: ").append(ex.getClass().getSimpleName())
					.append(ex.getMessage() == null ? "" : " - " + ex.getMessage());
			firstRowFailureSummary = sb.toString();
		}
		System.out.println("------------------------------------------");
	}

	private static String readLineWithCounter(BufferedReader reader, int[] lineCounter) throws Exception {
		String line = reader.readLine();
		if (line != null && lineCounter != null && lineCounter.length > 0) {
			lineCounter[0]++;
		}
		return line;
	}

	private static String readNextMeaningfulLine(BufferedReader reader) throws Exception {
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (!line.isEmpty() && !line.startsWith("#")) {
				return line;
			}
		}
		return null;
	}

	private static int findLikelyFailingColumnIndex(ArrayList<String> headerArgs, ArrayList<String> dataArr, Exception ex) {
		if (headerArgs == null || dataArr == null) {
			return -1;
		}
		if (dataArr.size() < headerArgs.size()) {
			return dataArr.size();
		}
		String message = ex == null ? null : ex.getMessage();
		if (message != null && message.contains("index")) {
			java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("index\\s+(\\d+)").matcher(message);
			if (matcher.find()) {
				try {
					return Integer.parseInt(matcher.group(1));
				} catch (NumberFormatException ignored) {
					return -1;
				}
			}
		}
		return -1;
	}

	private static ArrayList<String> buildRowFixHints(int expectedCount, int actualCount, int likelyFailingColumn,
			ArrayList<String> headerArgs) {
		ArrayList<String> hints = new ArrayList<String>();
		if (actualCount < expectedCount) {
			hints.add("This row has fewer values than expected. Add missing comma-delimited fields at the end of the row.");
			if (likelyFailingColumn >= 0 && likelyFailingColumn < headerArgs.size()) {
				hints.add("Provide a value for required column index " + likelyFailingColumn
						+ " mapped by header token: " + headerArgs.get(likelyFailingColumn));
			}
		} else if (actualCount > expectedCount) {
			hints.add("This row has extra values. Remove unintended commas or ensure text containing commas is quoted consistently for this converter.");
		} else {
			hints.add("Column counts match, so validate value formats and header mapping for the listed failing column.");
		}
		hints.add("Confirm the selected header file matches the table format of this CSV section (Header ID shown above).");
		return hints;
	}

	private static String formatHeaderLineArgMapping(String resolvedHeader, String line) {
		ArrayList<String> headerArgs = splitCsvArgs(resolvedHeader);
		ArrayList<String> lineArgs = splitCsvArgs(line);
		int max = Math.max(headerArgs.size(), lineArgs.size());
		StringBuilder sb = new StringBuilder();
		sb.append("index | header-arg | line-value").append(System.lineSeparator());
		for (int i = 0; i < max; i++) {
			String h = i < headerArgs.size() ? headerArgs.get(i) : "<missing header arg>";
			String v = i < lineArgs.size() ? lineArgs.get(i) : "<missing line value>";
			sb.append(i).append(" | ").append(h).append(" | ").append(v).append(System.lineSeparator());
		}
		if (headerArgs.size() != lineArgs.size()) {
			sb.append("Counts differ: header args=")
					.append(headerArgs.size())
					.append(", line values=")
					.append(lineArgs.size());
		}
		return sb.toString();
	}

	private static ArrayList<String> splitCsvArgs(String text) {
		ArrayList<String> args = new ArrayList<String>();
		if (text == null || text.trim().isEmpty()) {
			return args;
		}
		String[] parts = text.split(",", -1);
		for (int i = 0; i < parts.length; i++) {
			args.add(parts[i].trim());
		}
		return args;
	}

	public static synchronized boolean hadRowErrors() {
		return rowErrorCount > 0;
	}

	public static synchronized int getRowErrorCount() {
		return rowErrorCount;
	}

	public static synchronized String getFirstRowFailureSummary() {
		return firstRowFailureSummary;
	}

	public String getState(int i) {
		String state_str = "";

		return state_str;
	}

	/**
	 * Writes the DOM document to the specified file. Note this is used in many
	 * places and should really be in a utility however in this case we need that
	 * utility to have no extra library dependencies.
	 * 
	 * @param file  where the XML tree will be written to
	 * @param thDoc the tree that should be written
	 * @return whether the file was actually written or not
	 */
	public static boolean writeFile(File file, Document theDoc) {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// keep output compact but readable; many JAXP impls honor this key
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");

			DOMSource source = new DOMSource(theDoc);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
		} catch (Exception e) {
			System.err.println("Error outputing tree: " + e);
			return false;
		}
		return true;
	}

	/**
	 * Run CSV to XML conversion(s) by reading header, csv, and output filenames
	 * from a ModelInterface style batch command. This code is adapated from
	 * ModelInterface.ModelGUI2.InputViewer.runBatch which could not be called
	 * directly to avoid dependencies. Note many conversions could be specified in a
	 * single batch file. Non CSV conversion commands will produce a warning and
	 * skipped.
	 * 
	 * @param batchFile The batch file to parse and run.
	 * @throws Exception Any error from parsing the batch file or during a
	 *                   conversion.
	 */
	private static void runFromBatch(File batchFile) throws Exception {
		// Convert the file path to a URI before parsing to
		// ensure the document has a valid heirarchical URI.
		final URI docURI = batchFile.toURI();

		// Attempt to parse the document.
		DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document loadedDocument = parser.parse(docURI.getPath());

		// Parse the batch file looking for CSV file commands and parsing out the
		// header, csv,
		// and output files.
		NodeList rootChildren = loadedDocument.getDocumentElement().getChildNodes();
		for (int rootIndex = 0; rootIndex < rootChildren.getLength(); ++rootIndex) {
			Node currClassNode = rootChildren.item(rootIndex);
			// only interested in InputViewer which is the class typically used to do the
			// converion
			if (currClassNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (currClassNode.getNodeName().equals("class")
					&& ((Element) currClassNode).getAttribute("name").equals("ModelInterface.ModelGUI2.InputViewer")) {
				NodeList commands = currClassNode.getChildNodes();
				for (int commandIndex = 0; commandIndex < commands.getLength(); ++commandIndex) {
					Node command = commands.item(commandIndex);
					// Only interesed in the action CSV file
					if (command.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					String actionCommand = ((Element) command).getAttribute("name");
					if (actionCommand == null) {
						continue;
					}

					if (actionCommand.equals("CSV file")) {
						File headerFile = null;
						File outFile = null;
						ArrayList<File> csvFiles = new ArrayList<File>();
						// read file names for header file, csv files, and the output file
						NodeList fileNameChildren = command.getChildNodes();
						for (int j = 0; j < fileNameChildren.getLength(); ++j) {
							Node fileNode = fileNameChildren.item(j);
							if (fileNode.getNodeType() != Node.ELEMENT_NODE) {
								continue;
							}
							File tempFile = new File(fileNode.getTextContent());
							// find header, csv, and output files
							if (fileNode.getNodeName().equals("headerFile")) {
								headerFile = tempFile;
							} else if (fileNode.getNodeName().equals("outFile")) {
								outFile = tempFile;
							} else if (fileNode.getNodeName().equals("csvFile")) {
								csvFiles.add(tempFile);
							} else {
								System.out.println(
										"Unknown tag while parsing CSV file command: " + fileNode.getNodeName());
							}
						}
						// run the conversion and save the results
						File[] csvFilesArr = new File[csvFiles.size()];
						csvFilesArr = csvFiles.toArray(csvFilesArr);
						Document doc = runCSVConversion(csvFilesArr, headerFile, null);
						writeFile(outFile, doc);
					} else {
						System.out.println(
								"Invalid command: " + actionCommand + ", only CSV file can be run in this mode");
					}
				}
			} else {
				System.out.println("Invalid class, only ModelInterface.ModelGUI2.InputViewer can be run in this mode");
			}
		}
	}
}
