/*
 * LEGAL NOTICE
 * This computer software was prepared by Battelle Memorial Institute,
 * hereinafter the Contractor, under Contract No. DE-AC05-76RL0 1830
 * with the Department of Energy (DOE). NEITHER THE GOVERNMENT NOR THE
 * CONTRACTOR MAKES ANY WARRANTY, EXPRESS OR IMPLIED, OR ASSUMES ANY
 * LIABILITY FOR THE USE OF THIS SOFTWARE. This notice including this
 * sentence must appear on any copies of this computer software.
 *
 * Copyright 2012 Battelle Memorial Institute. All Rights Reserved.
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
 * and that User is not otherwise prohibited
 * under the Export Laws from receiving the Software.
 *
 */
package glimpseUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A standalone utility to convert CSV files to XML.
 * This class can be run from the command line, processing one or more CSV files
 * against a header definition file to produce a final XML output.
 *
 * This version has been refactored to use modern Java (8+) practices.
 *
 * @author Pralit Patel...Modified by Dan Loughlin of EPA
 */
public class CSVToXMLMain {

	private static final String CSV_DELIMITER = ",";
	private static final String ALL_STATES_KEYWORD_1 = "all states";
	private static final String ALL_STATES_KEYWORD_2 = "allstates";
	private static final String ALL_STATES_KEYWORD_3 = "all-states";
	private static final List<String> US_STATES = Collections.unmodifiableList(Arrays.asList(
		"AK", "AL", "AR", "AZ", "CA", "CO", "CT", "DC", "DE", "FL", "GA", "HI", "IA", "ID",
		"IL", "IN", "KS", "KY", "LA", "MA", "MD", "ME", "MI", "MN", "MO", "MS", "MT", "NC",
		"ND", "NE", "NH", "NJ", "NM", "NV", "NY", "OH", "OK", "OR", "PA", "RI", "SC", "SD",
		"TN", "TX", "UT", "VA", "VT", "WA", "WI", "WV", "WY"));

	public static void main(String[] args) {
		try {
			if (args.length == 1) {
				System.out.println("Running batch file: " + args[0]);
				runFromBatch(new File(args[0]));
			} else if (args.length >= 3) {
				File xmlOutputFile = new File(args[args.length - 1]);
				File headerFile = new File(args[args.length - 2]);
				File[] csvFiles = Arrays.stream(args, 0, args.length - 2)
					.map(File::new)
					.toArray(File[]::new);

				runCSVConversion(csvFiles, headerFile, null)
					.ifPresent(doc -> {
						writeFile(xmlOutputFile, doc);
						replaceTextInFile(xmlOutputFile.toPath(), "DELETE", "");
					});
			} else {
				System.err.println("Usage: CSVToXMLMain <CSV file> [<CSV file> ..] <header file> <output XML file>");
				System.err.println("   or: CSVToXMLMain <batch file>");
				System.exit(1);
			}
		} catch (Exception e) {
			System.err.println("!!!!!!!!!!!! Exception in GLIMPSE CSVtoXML utility !!!!!!!!!!!!!!!!!");
			e.printStackTrace();
		}
	}

	public static boolean replaceTextInFile(Path filePath, String oldText, String newText) {
		try {
			String content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
			content = content.replace(oldText, newText);
			Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
			System.out.println("Text replacement successful in " + filePath);
			return true;
		} catch (IOException e) {
			System.err.println("Error replacing text in file: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public static Optional<Document> runCSVConversion(File[] csvFiles, File headerFile, JFrame parentFrame) {
		DOMTreeBuilder tree = new DOMTreeBuilder();
		try {
			Map<String, String> nickNameMap = new HashMap<>();
			Map<String, String> tableIDMap = parseHeaderFile(headerFile.toPath(), nickNameMap, parentFrame);

			for (File csvFile : csvFiles) {
				processCsvFile(csvFile.toPath(), tableIDMap, tree, parentFrame);
			}
			return Optional.of(tree.getDoc());
		} catch (Exception e) {
			String errorMessage = "A critical error occurred during CSV to XML processing.";
			System.err.println("========= Error in CSV2XML processing ==========");
			System.err.println(errorMessage);
			e.printStackTrace();
			System.err.println("================================================");
			if (parentFrame != null) {
				JOptionPane.showMessageDialog(parentFrame, errorMessage + "\n" + e.getMessage(), "Processing Error", JOptionPane.ERROR_MESSAGE);
			}
			GLIMPSEUtils.getInstance().warningMessage(errorMessage);
			return Optional.empty();
		}
	}

	private static Map<String, String> parseHeaderFile(Path headerPath, Map<String, String> nickNameMap, JFrame parentFrame) throws IOException {
		Map<String, String> tableIDMap = new HashMap<>();
		List<String> lines = Files.readAllLines(headerPath, StandardCharsets.UTF_8);

		// Process nicknames ($vars)
		lines.stream()
			.map(CSVToXMLMain::trimString)
			.filter(line -> line.startsWith("$"))
			.forEach(line -> {
				String[] parts = line.split(CSV_DELIMITER, 2);
				if (parts.length == 2) {
					nickNameMap.put(parts[0], parts[1]);
				}
			});

		// Process table IDs
		lines.stream()
			.map(CSVToXMLMain::trimString)
			.filter(line -> !line.isEmpty() && !line.startsWith("#") && !line.startsWith("$"))
			.forEach(line -> {
				String[] parts = line.split(CSV_DELIMITER, 2);
				if (parts.length == 2) {
					String tableID = parts[0];
					String resolvedHeader = resolveNicknames(parts[1], nickNameMap, parentFrame);
					tableIDMap.put(tableID, resolvedHeader);
				}
			});

		return tableIDMap;
	}

	private static String resolveNicknames(String headerLine, Map<String, String> nickNameMap, JFrame parentFrame) {
		String result = headerLine;
		for (Map.Entry<String, String> entry : nickNameMap.entrySet()) {
			// Use \\ to escape the $ for regex replacement
			result = result.replaceAll("\\" + entry.getKey(), entry.getValue());
		}
		// Clean up any extraneous commas from replacements
		result = result.replaceAll("[,][\\s]*[,]", ",").replaceAll(",+", ",");
		return trimString(result);
	}

	private static void processCsvFile(Path csvPath, Map<String, String> tableIDMap, DOMTreeBuilder tree, JFrame parentFrame) throws IOException {
		List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
		Iterator<String> iterator = lines.iterator();

		while (iterator.hasNext()) {
			String line = trimString(iterator.next());
			if (line.contains("INPUT_TABLE")) {
				// Skip lines until we find the Variable ID indicator
				while (iterator.hasNext() && !line.contains("Variable ID")) {
					line = trimString(iterator.next());
				}

				if (!iterator.hasNext()) break;
				String headerId = trimString(iterator.next());

				if (tableIDMap.containsKey(headerId)) {
					try {
						tree.setHeader(tableIDMap.get(headerId));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Using header: " + headerId);
					
					// Skip blank line and read column names (currently unused but good for context)
					if (iterator.hasNext()) iterator.next(); // blank line
					if (iterator.hasNext()) iterator.next(); // column names

					// Process data rows
					while (iterator.hasNext()) {
						line = trimString(iterator.next());
						if (line.isEmpty() || line.startsWith(",")) break;
						processCsvDataRow(line, tree);
					}
				} else {
					System.err.println("***** Warning: could not find header (" + headerId + ") in header file. Skipping table! *****");
				}
			}
		}
	}

	private static void processCsvDataRow(String line, DOMTreeBuilder tree) {
		// Split by comma, preserving empty strings between delimiters
		List<String> dataArr = Arrays.stream(line.split(CSV_DELIMITER, -1))
			.map(String::trim)
			.collect(Collectors.toList());

		if (dataArr.isEmpty()) return;

		String regionIdentifier = dataArr.get(0).toLowerCase();
		if (ALL_STATES_KEYWORD_1.equals(regionIdentifier) || ALL_STATES_KEYWORD_2.equals(regionIdentifier) || ALL_STATES_KEYWORD_3.equals(regionIdentifier)) {
			US_STATES.forEach(state -> {
				dataArr.set(0, state);
				try {
					tree.addToTree(dataArr);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		} else if (regionIdentifier.contains(":")) {
			String[] regions = dataArr.get(0).split(":");
			Arrays.stream(regions).forEach(region -> {
				dataArr.set(0, region);
				try {
					tree.addToTree(dataArr);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		} else {
			try {
				tree.addToTree(dataArr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static String trimString(String s) {
		if (s == null) {
			return "";
		}
		// Removes trailing commas and then trims whitespace
		return s.replaceAll(",+$", "").trim();
	}

	public static boolean writeFile(File file, Document theDoc) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			// Mitigate security risks, see OWASP guidelines
			transformerFactory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);

			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");

			DOMSource source = new DOMSource(theDoc);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);

			return true;
		} catch (TransformerException e) {
			System.err.println("Error outputting XML tree: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	private static void runFromBatch(File batchFile) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser = factory.newDocumentBuilder();
		Document loadedDocument = parser.parse(batchFile);

		streamNodeList(loadedDocument.getElementsByTagName("class"))
			.filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
			.map(node -> (Element) node)
			.filter(elem -> "ModelInterface.ModelGUI2.InputViewer".equals(elem.getAttribute("name")))
			.flatMap(elem -> streamNodeList(elem.getChildNodes()))
			.filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
			.map(node -> (Element) node)
			.forEach(command -> {
				String actionCommand = command.getAttribute("name");
				if ("CSV file".equals(actionCommand)) {
					Map<String, List<File>> fileMap = streamNodeList(command.getChildNodes())
						.filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
						.collect(Collectors.groupingBy(
							Node::getNodeName,
							Collectors.mapping(node -> new File(node.getTextContent()), Collectors.toList())
						));

					File headerFile = fileMap.getOrDefault("headerFile", Collections.emptyList()).stream().findFirst().orElse(null);
					File outFile = fileMap.getOrDefault("outFile", Collections.emptyList()).stream().findFirst().orElse(null);
					List<File> csvFiles = fileMap.getOrDefault("csvFile", Collections.emptyList());

					if (headerFile != null && outFile != null && !csvFiles.isEmpty()) {
						runCSVConversion(csvFiles.toArray(new File[0]), headerFile, null)
							.ifPresent(doc -> writeFile(outFile, doc));
					}
				} else {
					System.out.println("Invalid command: " + actionCommand + ", only 'CSV file' can be run in this mode.");
				}
			});
	}

	private static Stream<Node> streamNodeList(NodeList nodeList) {
		return IntStream.range(0, nodeList.getLength())
			.mapToObj(nodeList::item);
	}
}