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
package gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;

import glimpseBuilder.XMLModifier;
import glimpseElement.ComponentRow;
import glimpseElement.ComponentLibraryTable;
import glimpseElement.ScenarioRow;
import glimpseElement.ScenarioTable;
//import ModelInterface.ModelGUI2.csvconv.CSVToXMLMain;
import glimpseUtil.CSVToXMLMain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

class PaneCreateScenario extends ScenarioBuilder {// VBox {
	// private GlimpseXML gXML = GlimpseXML.getInstance();

	private String descriptionText = "";

	private VBox vBox;
	private TextField textFieldScenarioName;

	PaneCreateScenario(Stage stage) {
		vBox = new VBox(1);
		textFieldScenarioName = utils.createTextField(2.5 * styles.bigButtonWid);

		textFieldScenarioName.setTooltip(new Tooltip("Enter name of scenario being constructed"));
		vBox.setStyle(styles.font_style);

		Label labelName = utils.createLabel("Name: ");
		Label labelComponents = utils.createLabel("Components: ");

		HBox hBox = new HBox(30);

		HBox hBoxRun = new HBox(/* 50 */);

		Client.buttonMoveComponentUp = utils.createButton(null, styles.smallButtonWid, "Move selected item up in list",
				"upArrow7");

		Client.buttonMoveComponentDown = utils.createButton(null, styles.smallButtonWid,
				"Move selected item down in list", "downArrow7");

		Client.buttonCreateScenarioConfigFile = utils.createButton("Create", styles.bigButtonWid,
				"Create: Contruct scenario from template and selected scenario components", "add2");

		Client.buttonCreateScenarioConfigFile.setDisable(true);
		Client.buttonMoveComponentUp.setDisable(true);
		Client.buttonMoveComponentDown.setDisable(true);

		Client.buttonMoveComponentUp.setOnAction(e -> {
			ObservableList<ComponentRow> allFiles1 = ComponentLibraryTable.tableCreateScenario.getItems();

			ObservableList<ComponentRow> selectedFiles1 = ComponentLibraryTable.tableCreateScenario.getSelectionModel()
					.getSelectedItems();
			if (selectedFiles1.size() == 1) {
				int n = ComponentLibraryTable.tableCreateScenario.getSelectionModel().getSelectedIndex();
				if (n - 1 >= 0) {
					ComponentRow filea = allFiles1.get(n);
					ComponentRow fileb = allFiles1.get(n - 1);
					allFiles1.set(n - 1, filea);
					allFiles1.set(n, fileb);
					ComponentLibraryTable.tableCreateScenario.setItems(allFiles1);
				}
			}
		});

		Client.buttonMoveComponentDown.setOnAction(e -> {
			ObservableList<ComponentRow> allFiles1 = ComponentLibraryTable.tableCreateScenario.getItems();
			System.out.println("allFiles1: " + allFiles1.toString());
			ObservableList<ComponentRow> selectedFiles1 = ComponentLibraryTable.tableCreateScenario.getSelectionModel()
					.getSelectedItems();
			System.out.println("allFiles1: " + selectedFiles1.toString());
			if (selectedFiles1.size() == 1) {
				int n = ComponentLibraryTable.tableCreateScenario.getSelectionModel().getSelectedIndex();
				if (n + 1 < allFiles1.size()) {
					ComponentRow filea = allFiles1.get(n);
					ComponentRow fileb = allFiles1.get(n + 1);
					allFiles1.set(n + 1, filea);
					allFiles1.set(n, fileb);
					ComponentLibraryTable.tableCreateScenario.setItems(allFiles1);
				}
			}
		});

		Client.buttonCreateScenarioConfigFile.setOnAction(e -> {
			//if (utils.setCreateScenarioDialog(textFieldScenarioName.getText())==null) return;
			processScenarioComponentList(stage, false);
			Client.buttonRefreshScenarioStatus.fire();
		});
		
		ComponentLibraryTable.tableCreateScenario.setOnMouseClicked(e -> {
			setArrowAndButtonStatus();
		});
		textFieldScenarioName.setOnKeyPressed(e -> {
			setArrowAndButtonStatus();
		});

		labelScenarioName = utils.createLabel("Create Scenario", 1.5 * styles.bigButtonWid);
		hBox.getChildren().addAll(labelScenarioName, textFieldScenarioName);

		hBoxRun.getChildren().addAll(Client.buttonCreateScenarioConfigFile,
				utils.getSeparator(Orientation.VERTICAL, 3, false), Client.buttonMoveComponentUp,
				Client.buttonMoveComponentDown);
		// hBoxRun.setSpacing(3.0);
		hBox.setPadding(new Insets(0, 0, 5, 0));
		hBoxRun.setPadding(new Insets(5, 0, 0, 0));

		hBoxRun.setAlignment(Pos.CENTER);

		// hBox.getChildren().addAll(labelName, textFieldScenarioName);
		vBox.getChildren().addAll(hBox, ComponentLibraryTable.tableCreateScenario, hBoxRun);
		vBox.prefWidthProperty().bind(stage.widthProperty().multiply(2.0 / 7.0));
	}

	public void setScenarioName(String scenarioName) {
		textFieldScenarioName.setText(scenarioName);
	}


	public void processScenarioComponentList(Stage stage, boolean b) {

		String scenName = textFieldScenarioName.getText().replace("/", "-").replace("\\", "-").replace(" ", "_");

		boolean fix_name=false;
		
        if (utils.hasSpecialCharacter(scenName)) fix_name=true;
		
		if ((scenName.length() < 1)||(fix_name)) {

			utils.warningMessage("Please specify a name for the scenario. The name should not include any of these special characters: [! @#$%&*()+=|<>?{}[]~]\\//");

		} else {

			// creates two lists (not sure why two are needed)
			ObservableList<ComponentRow> copy1 = FXCollections.observableArrayList();
			ObservableList<ComponentRow> copy2 = FXCollections.observableArrayList();

			// for each list, adds info from each row from the Construct or
			// Edit ScenarioRow table
			for (ComponentRow i : ComponentLibraryTable.listOfFilesCreateScenario) {
				copy1.add(i);
				copy2.add(i);
			}

			try {
				// creates xml file for each component of scenario and
				// generates configuration file
				processScenario(scenName,copy1, copy2, scenName, scenName, b);
			} catch (Exception e1) {
				e1.printStackTrace();
				utils.exitOnException();
			}
			

		}

	};

	// this method processes the files existing in the table of the right pane to create a scenario
	@SuppressWarnings("static-access")
	protected void processScenario(String scenName,ObservableList<ComponentRow> list, ObservableList<ComponentRow> list1,
			String runName, String scenarioName, boolean execute) throws IOException {

		String message="";
		// checks to see if the scenario already exist. If so, does not
		// continue
		if (checkInList(scenName, ScenarioTable.tableScenariosLibrary)) {
			String s = "Overwrite scenario " + scenName + "?";
			boolean overwrite = utils.confirmAction(s);
			if (overwrite == false) {
				return;
			}
		}
		
		message=createScenarioDialog(scenarioName);
		if (message==null) return;
			
		
		if (checkInList(scenName, ScenarioTable.tableScenariosLibrary)) {
			String main_log_file = vars.get("scenarioDir") + File.separator + scenName + File.separator
					+ "main_log.txt";
			files.deleteFile(main_log_file);

			//Client.paneWorkingScenarios.deleteItemFromScenarioLibrary(textFieldScenarioName.getText());
		}
		
		if (list.size() > 0) {
			String list_of_components = "";
			//message ="";
			for (ComponentRow f : list) {
				list_of_components += f.getFileName() + vars.getEol();
			}

			message = message + vars.getEol() + "Components:" + vars.getEol() +list_of_components+ vars.getEol();
		}
		message+="###############################################################"+vars.getEol();

		String newDescription = message;//utils.commentLinesInString(message, "", "");
		newDescription = "<!--" + vars.getEol()+ newDescription + vars.getEol()+ "-->";

		// cleans scenario folder of all txt and xml files when scenario is created
		String main_log_file = vars.get("scenarioDir") + File.separator + scenarioName + File.separator
				+ "main_log.txt";
		File file = new File(main_log_file);

		if (file.exists()) {
			files.deleteFiles(vars.get("scenarioDir"), ".txt");
			files.deleteFiles(vars.get("scenarioDir"), ".xml");
		}
		
		// creates name of folder where scenario info will be stored
		String workingDir = vars.get("scenarioDir") + File.separator + scenarioName;

		// if the working directory does not exist, it is created

		// Attempts to access scenario working directory
		File dir = new File(workingDir);

		try {
			// if it exists, delete
			if (dir.exists()) {
				dir.delete();
			}
			// make or re-make the directory
			new File(workingDir).mkdir();
		} catch (Exception e) {
			utils.warningMessage("Difficulty creating directory for xml code.");
			System.out.println("error:" + e);
			utils.exitOnException();
		}

		// gets name of configuration template copies it to the working
		// scenario directory, re-naming it
		String templateConfigFileAddress = vars.get("configurationTemplate");
		String savedConfigFileAddress = workingDir + File.separator + "configuration" + "_" + scenarioName + ".xml";
		files.copyFile(templateConfigFileAddress, savedConfigFileAddress);

		// inserts the description into the top of the scenario
		// configuration file
		utils.insertLinesIntoFile(savedConfigFileAddress, newDescription, 2);

		// creates the xml document interface to the scenario configuration
		// file
		Document xmlDoc = XMLModifier.openXmlDocument(savedConfigFileAddress);

		// initialization of a time/date variable used later
		Date now = null;

		// creates a path instance pointing to the GCAM distribution exe
		// directory
		Path gcamexepath = Paths.get(vars.get("gCamExecutableDir"));

		// iterates over the list of selected scenario components in the
		// ScenarioRow pane
		for (ComponentRow f : list) {

			// reads the scenario component file type identifier (e.g.,
			// present, techbound, techparam...)
			String fileType = getFileType(f.getAddress(), "@type");

			// biforcates to handle files of xmllist vs. others
			if ((fileType.equals("preset")) || (fileType.equals("techbound")) || (fileType.equals("techparam"))
					|| (fileType.equals("INPUT_TABLE"))) {

				// for these types, GLIMPSE creates a new xml file and
				// places it in the scenario working directory
				String xmlFileAddress = workingDir + File.separator + f.getFileName().substring(0,
						f.getFileName().lastIndexOf('.'))/*
															 * + "_" + f.getBirthDate().toString().replaceAll("\\s+",
															 * "_").replaceAll(":", "-")
															 */ + ".xml";
				System.out.println("---"+vars.getEol()+"Creating new xml file:\n  " + xmlFileAddress);

				// Gets the path to the working directory then gets the
				// relative path from the exe directory
				Path xmlPath = Paths.get(workingDir);
				Path relativePath = gcamexepath.relativize(xmlPath);

				// gets the filename and address for the new configuration
				// file and does some formatting changes
				String xmlFileAddressForConfig = relativePath.toString() + File.separator + f.getFileName().substring(0,
						f.getFileName().lastIndexOf('.')) /*
															 * + "_" + f.getBirthDate().toString().replaceAll("\\s+",
															 * "_").replaceAll(":", "-") +
															 */ + ".xml";
				;

				// loads key-value pairs from the scenario component files
				// ArrayList<String[]> keyValuePairs =
				// files.loadKeyValuePairsFromFile(f.getAddress(), "=");

				// does the magic to create a scenario component xml file
				if (fileType.equals("INPUT_TABLE")) {
					try {
						String[] s = { f.getAddress(), vars.getXmlHeaderFile(), xmlFileAddress };
						s=utils.getRidOfTrailingCommasInStringArray(s);
						System.out.println("csv to xml conversion commencing:");
						System.out.println("    csv file: " + f.getAddress());
						System.out.println("    header file: " + vars.getXmlHeaderFile());
						System.out.println("    xml file: " + xmlFileAddress);
						String header = utils.getRidOfTrailingCommasInString(files.getLineXFromFile(f.getAddress(), 3, "#").trim());
						System.out.println("header specified in csv file: " + header);

						// Dan modified on 3-6-2021 to check to see if the header is in the header file.
						// Warning pops up otherwise
						String header1 = header + ",";
						String header2 = header + " ";
						int header_in_file = files.countLinesWithTextInFile(new File(vars.getXmlHeaderFile()), header,
								"#");
						if (header_in_file == 0) {
							header_in_file = files.countLinesWithTextInFile(new File(vars.getXmlHeaderFile()), header1,
									"#");
						}
						if (header_in_file == 0) {
						header_in_file = files.countLinesWithTextInFile(new File(vars.getXmlHeaderFile()), header2,
								"#");
						}
						
						if (header_in_file > 0) {
							//System.out.println("Found header in header file:" + header1);
							//TODO: Add something here to manage market names to avoid duplicates?
							CSVToXMLMain.main(s);
						} else {
							utils.warningMessage("Could not find header in header file");
						}
					} catch (Exception e) {
						// Dan added error handling on 3-5-2021
						utils.warningMessage("Error converting "+f.getFileName()+" using CSV->XML. Please check formatting.");
						System.out.println("Error converting CSV->XML: " + e);
						System.out.println("Attempting to continue, but conversion unsuccessful.");
					}
				} else {
					utils.warningMessage("Only CSV-type and XML-list-type scenario components are currently supported.");
					System.out.println("Only CSV-type and XML-list-type scenario components are currently supported.");
					// gXML.createComponentXml(fileType, scenarioName, keyValuePairs,
					// xmlFileAddress);
				}

				// adds a reference to the new file to the configuration
				// file
				System.out.println("Adding new xml file (" + f.getFileName() + ") to configuration file");
				XMLModifier.addElement(xmlDoc, "ScenarioComponents", "Value", f.getFileName(), xmlFileAddressForConfig);

				// or, if the type is xmllist...
			} else if ((fileType.equals("xmllist")) || (fileType.equals("list"))) {
				System.out.println("adding files from list...");

				ArrayList<String> fileList = files.loadFileListFromFile(f.getAddress(), "@type");
				int num=0;
				for (String temp : fileList) {
					num++;
					String filename=temp;
					String relative_pathname=files.getRelativePath(gcamexepath.toString(), filename);
					String identifier=f.getFileName();
					if (fileList.size()>1) identifier+="-"+num;
					XMLModifier.addElement(xmlDoc, "ScenarioComponents", "Value", identifier, relative_pathname);
				}

			} else if (fileType.equals("xml")) {

				String filename=vars.getScenarioComponentsDir()+File.separator+f.getFileName();
				String relative_pathname=files.getRelativePath(gcamexepath.toString(), filename);
				XMLModifier.addElement(xmlDoc, "ScenarioComponents", "Value", f.getFileName(), relative_pathname);
				
			} else {
				utils.warningMessage("Unable to process scenario component "+f.getFileName());
			}
		}

		XMLModifier.updateElementValue(xmlDoc, "Strings", "Value", "scenarioName", scenarioName);

		if (vars.get("stopPeriod") != null)
			XMLModifier.updateElementValue(xmlDoc, "Ints", "Value", "stop-period", vars.get("stopPeriod"));

		String s=vars.get("useAllAvailableProcessors");
		if (s.equals("false")) {
			XMLModifier.updateElementValue(xmlDoc, "Ints", "Value", "max-parallelism", "1");
		}
			
		if (vars.get("debugRegion") != null)
			XMLModifier.updateElementValue(xmlDoc, "Strings", "Value", "debug-region", vars.get("debugRegion"));

		if (vars.get("debugCreate") != null)
			XMLModifier.updateAttributeValue(xmlDoc, "Files", "Value", "xmlDebugFileName", "write-output",
					vars.get("debugCreate"));
		
		if (vars.get("debugRename") != null)
			XMLModifier.updateAttributeValue(xmlDoc, "Files", "Value", "xmlDebugFileName", "append-scenario-name",
					vars.get("debugRename"));

		if (vars.get("gCamSolver") != null) {
			try {
				File solverFile = new File(vars.get("gCamSolver"));
				Path solverPath = Paths.get(solverFile.getPath());
				XMLModifier.updateElementValue(xmlDoc, "ScenarioComponents", "Value", "solver",
						gcamexepath.relativize(solverPath).toString()); // gCamSolver);
			} catch (Exception e) {
				System.out.println("Could not set solver path in config file. Using full path.");
				System.out.println("  error: " + e);
				XMLModifier.updateElementValue(xmlDoc, "ScenarioComponents", "Value", "solver", vars.get("gCamSolver"));
			}
		}
		if (vars.get("gCamOutputDatabase") != null) {
			try {
				File databaseDir = new File(vars.get("gCamOutputDatabase"));
				Path databasePath = Paths.get(databaseDir.getPath());
				XMLModifier.updateElementValue(xmlDoc, "Files", "Value", "xmldb-location",
						gcamexepath.relativize(databasePath).toString()); // gCamSolver);
			} catch (Exception e) {
				System.out.println("Could not set relative database path in config file. Using full path.");
				System.out.println("  error: " + e);
				XMLModifier.updateElementValue(xmlDoc, "Files", "Value", "xmldb-location",
						vars.get("gCamOutputDatabase"));
			}
		}

		XMLModifier.writeXmlDocument(xmlDoc, savedConfigFileAddress);

		//createRunTxtFile(list1, runName, now);

	}

	public boolean checkInList(String name, TableView<ScenarioRow> table) {

		ObservableList<ScenarioRow> list = table.getItems();

		boolean match = false;
		for (int i = 0; i < list.size(); i++) {
			String str = list.get(i).getScenarioName();
			if (str.equals(name)) {
				match = true;
				break;
			}
		}

		return match;
	}



	public TextField getTextFieldScenarioName() {
		return textFieldScenarioName;
	}

	public VBox getvBox() {
		return vBox;
	}
	
	public String createScenarioDialog(String scenName) {

		int height=550;
		int width=400;
		
		String title = "Creating Scenario";

		Label scenarioNameLabel = new Label("Scenario name:");
		Label scenarioName = new Label(scenName);

		Label stopYearLabel = new Label("Final model year:");
		ComboBox<String> stopYearComboBox = new ComboBox<String>();
		stopYearComboBox.getItems().addAll("2020", "2025", "2030", "2035", "2040", "2045", "2050", "2055", "2060",
				"2065", "2070", "2075", "2080", "2085", "2090", "2095", "2100");
		stopYearComboBox.getSelectionModel().select(utils.getYearForPeriod(Integer.parseInt(vars.getStopPeriod())));
		stopYearComboBox.setDisable(false);
		stopYearComboBox.setOnAction(e -> {
			vars.setStopPeriod(utils.getPeriodForYear(stopYearComboBox.getSelectionModel().getSelectedItem()));
		});
		
		Label databaseNameLabel = new Label("Database:");
		String database_name = vars.getgCamOutputDatabase();
		File database_folder=new File(database_name);
		Path database_path=database_folder.toPath();
		long database_size=files.getDirectorySize(database_path)/1000000000;
		String database_size_str = " ("+database_size+" GB)";
		System.out.println("database size: "+database_size_str);
		String database_name_short = database_name.substring(database_name.lastIndexOf(File.separator) + 1);
		Label databaseNameAndSize = new Label(database_name_short+database_size_str);

		if (database_size>=vars.maxDatabaseSizeGB) {
			boolean b=utils.confirmAction("Database size is dangerously high. See User's Manual for instructions. Continue?");
			if (!b) return null;
		}

		CheckBox createDebugCheckBox = new CheckBox("Create debug file?");
		boolean isChecked = false;
		String strIsChecked = vars.getDebugCreate().toLowerCase();
		if (strIsChecked.equals("true") || strIsChecked.equals("yes") || strIsChecked.equals("1"))
			isChecked = true;
		createDebugCheckBox.setSelected(isChecked);
		//createDebugCheckBox.setDisable(true);
		

		ComboBox<String> debugRegionComboBox = new ComboBox<String>();
		if (vars.isGcamUSA()) {
			debugRegionComboBox.getItems().addAll("AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI",
					"ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV",
					"NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT",
					"VA", "WA", "WV", "WI", "WY", "USA", "Canada", "EU-15", "Europe_Non_EU",
					"European Free Trade Association", "Japan", "Australia_NZ", "Central Asia", "Russia", "China",
					"Middle East", "Africa_Eastern", "Africa_Northern", "Africa_Southern", "Africa_Western", "South Africa",
					"Brazil", "Central America and Caribbean", "Mexico", "South America_Northern", "South America_Southern",
					"Argentina", "Colombia", "Indonesia", "Pakistan", "South Asia", "Southeast Asia", "Taiwan",
					"Europe_Eastern", "EU-12", "South Korea", "India");			
		} else {
			debugRegionComboBox.getItems().addAll("USA", "Canada", "EU-15", "Europe_Non_EU",
					"European Free Trade Association", "Japan", "Australia_NZ", "Central Asia", "Russia", "China",
					"Middle East", "Africa_Eastern", "Africa_Northern", "Africa_Southern", "Africa_Western", "South Africa",
					"Brazil", "Central America and Caribbean", "Mexico", "South America_Northern", "South America_Southern",
					"Argentina", "Colombia", "Indonesia", "Pakistan", "South Asia", "Southeast Asia", "Taiwan",
					"Europe_Eastern", "EU-12", "South Korea", "India");				
		}

		debugRegionComboBox.getSelectionModel().select(vars.getDebugRegion());
		debugRegionComboBox.setDisable(false);
		debugRegionComboBox.setOnAction(e -> {
			vars.setDebugRegion(debugRegionComboBox.getSelectionModel().getSelectedItem());
		});
		
		CheckBox useAllAvailableProcessors = new CheckBox("Use all available processors?");
		strIsChecked = vars.get("useAllAvailableProcessors");
		if (strIsChecked.equals("true") || strIsChecked.equals("yes") || strIsChecked.equals("1"))
			isChecked = true;
		useAllAvailableProcessors.setSelected(isChecked);
		
		Label filesToSaveLabel = new Label("Save files in scenario folder: (global setting)");
				
		CheckBox saveMainLogCheckBox = new CheckBox("Main log");
		saveMainLogCheckBox.setSelected(true);
		saveMainLogCheckBox.setDisable(true);
		
		CheckBox saveCalibrationLogCheckBox = new CheckBox("Calibration log");
		saveCalibrationLogCheckBox.setSelected(false);
		saveCalibrationLogCheckBox.setDisable(false);
		
		CheckBox saveSolverLogCheckBox = new CheckBox("Solver log");
		saveSolverLogCheckBox.setSelected(false);
		saveSolverLogCheckBox.setDisable(false);
	
		CheckBox saveDebugFileCheckBox = new CheckBox("Debug file");
		saveDebugFileCheckBox.setSelected(false);	
		saveDebugFileCheckBox.setDisable(false);
		
		String filesToSave=vars.getFilesToSave().toLowerCase();
		if (filesToSave.indexOf("debug")>=0) saveDebugFileCheckBox.setSelected(true);
		if (filesToSave.indexOf("solver")>=0) saveSolverLogCheckBox.setSelected(true);
		if (filesToSave.indexOf("calibration")>=0) saveCalibrationLogCheckBox.setSelected(true);
		
		Label commentLabel = new Label("Comments:");

		TextArea textArea = new TextArea();
		textArea.setEditable(true);
		textArea.setPrefSize(385, 375);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(0, 10, 0, 10));
		//col,row
		grid.add(scenarioNameLabel, 0, 0);
		grid.add(scenarioName, 1, 0);
		grid.add(databaseNameLabel, 0, 1);
		grid.add(databaseNameAndSize, 1, 1);
		grid.add(stopYearLabel, 0, 2);
		grid.add(stopYearComboBox, 1, 2);
		grid.add(createDebugCheckBox, 0, 3);
		grid.add(debugRegionComboBox, 1, 3);
		grid.add(useAllAvailableProcessors, 0, 4, 2, 1);
		grid.add(filesToSaveLabel,0,5,2,1);
		grid.add(saveMainLogCheckBox, 0, 6);
		grid.add(saveDebugFileCheckBox, 1, 6);
		grid.add(saveCalibrationLogCheckBox, 0, 7);
		grid.add(saveSolverLogCheckBox, 1, 7);

		grid.add(commentLabel, 0, 8,2,1);
		grid.add(textArea, 0, 9, 2, 1);

		Stage stage = new Stage();

		stage.setTitle(title);
		stage.setWidth(width);
		stage.setHeight(height);
		Scene scene = new Scene(new Group());
		stage.setResizable(false);
		stage.setAlwaysOnTop(true);

		Button okButton = utils.createButton("OK", styles.bigButtonWid, null);
		Button cancelButton = utils.createButton("Cancel", styles.bigButtonWid, null);

		final int status;

		okButton.setOnAction(e -> {
			String isSelected="false";
			if (createDebugCheckBox.isSelected()) isSelected="true";
			vars.setDebugCreate(isSelected);
			
			isSelected="false";
			if (useAllAvailableProcessors.isSelected()) isSelected="true";
			vars.setUseAllAvailableProcessors(isSelected);
			
			vars.setFilesToSave(adjustFilesToSave(saveCalibrationLogCheckBox.isSelected(),saveSolverLogCheckBox.isSelected(),saveDebugFileCheckBox.isSelected()));
			
			stage.close();
		});
		cancelButton.setOnAction(e -> {
			utils.clearTextArea(textArea);
			stage.close();
		});

		VBox root = new VBox();
		root.setPadding(new Insets(4, 4, 4, 4));
		root.setSpacing(5);
		root.setAlignment(Pos.TOP_LEFT);

		String text = "";

		textArea.setText(text);

		HBox buttonBox = new HBox();
		buttonBox.setPadding(new Insets(4, 4, 4, 4));
		buttonBox.setSpacing(5);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().addAll(okButton, cancelButton);

		root.getChildren().addAll(/* textArea */grid, buttonBox);
		scene.setRoot(root);

		stage.setScene(scene);
		stage.showAndWait();

		if (textArea.getText() == null) {
			text = null;
		} else {
			text = "##################### Scenario Meta Data #####################"+vars.getEol();
			text += "Scenario name: " + scenarioName.getText()+ vars.getEol();
			text += "Database: " + database_name_short + vars.getEol();
			text += "Debug region: " + debugRegionComboBox.getSelectionModel().getSelectedItem()+ vars.getEol();
			text += "Stop year:" + stopYearComboBox.getSelectionModel().getSelectedItem()+ vars.getEol();
			text += "Comments:"+ vars.getEol();
			text += textArea.getText()+vars.getEol();
			//text += "##############################################################"+vars.getEol();
		}
		
		if (text!=null) text=text.replaceAll(vars.getEol()+""+vars.getEol(),vars.getEol());
	
		return text;
	}

	private String adjustFilesToSave(boolean saveCalibLog,boolean saveSolverLog,boolean saveDebugFile) {
		String rtn_str="";
		
		ArrayList<String> filesToSave=utils.createArrayListFromString(vars.getFilesToSave(),";");
		
		String foundCalib=null;
		String foundSolver=null;
		String foundDebug=null;
		
		for (int i=0;i<filesToSave.size();i++) {
			String filename=filesToSave.get(i);
			String filenamelc=filename.toLowerCase();
			if (filenamelc.indexOf("debug")>=0) foundDebug=filename;
			if (filenamelc.indexOf("calib")>=0) foundCalib=filename;
			if (filenamelc.indexOf("solver")>=0) foundSolver=filename;
		}
		
		if ((!saveCalibLog)&&(foundCalib!=null)) filesToSave.remove(foundCalib);
		if ((!saveSolverLog)&&(foundSolver!=null)) filesToSave.remove(foundSolver);
		if ((!saveDebugFile)&&(foundDebug!=null)) filesToSave.remove(foundDebug);
		
		if ((saveCalibLog)&&(foundCalib==null)) filesToSave.add(vars.getgCamExecutableDir()+File.separator+"logs"+File.separator+"calibration_log.txt");
		if ((saveSolverLog)&&(foundSolver==null)) filesToSave.add(vars.getgCamExecutableDir()+File.separator+"logs"+File.separator+"solver_log.csv");
		if ((saveDebugFile)&&(foundDebug==null)) filesToSave.add(vars.getgCamExecutableDir()+File.separator+"debug.xml");
		
		rtn_str=utils.createStringFromArrayList(filesToSave,";");
		
		return rtn_str;
	}

}