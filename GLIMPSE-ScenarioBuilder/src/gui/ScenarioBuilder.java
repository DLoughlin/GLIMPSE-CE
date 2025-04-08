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
* Parks and Yadong Xu of ARA through the EPA’s Environmental Modeling and 
* Visualization Laboratory contract. 
* 
*/
package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import glimpseBuilder.SetupTableComponentLibrary;
import glimpseBuilder.SetupTableCreateScenario;
import glimpseBuilder.SetupTableScenariosLibrary;
import glimpseElement.ComponentRow;
import glimpseElement.ComponentLibraryTable;
import glimpseElement.ScenarioRow;
import glimpseElement.ScenarioTable;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;

public class ScenarioBuilder {

	protected VBox vBoxComponentLibrary;
	protected VBox vBoxCreateScenario;
	protected VBox vBoxButton;
	protected VBox vBoxRun;

	// labels on main interface
	protected Label labelComponentLibrary;
	protected Label labelSearchComponentLibrary;
	protected Label labelSearchScenarios;
	protected Label labelScenarioLibrary;
	protected Label labelScenarioName;

	protected GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	protected GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	protected GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	protected GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

	public static final ScenarioBuilder instance = new ScenarioBuilder();

	public static ScenarioBuilder getInstance() {
		return instance;
	}

	public ScenarioBuilder() {
	}

	public void build() {
		vars.init(utils, vars, styles, files);
		files.init(utils, vars, styles, files);
		utils.init(utils, vars, styles, files);

		// Creates the tables in which the components are stored
		createTables();

		// Adds the arrow buttons in between the top-right and the top-left pane
		createArrowButtons();

		// Adds the components pane to the GUI
		createComponentLibraryPane();

		// Adds the create scenario pane to the GUI
		createCreateScenarioPane();

		// Creates the bottom pane
		createScenarioLibraryPane();

		// Resizes labels if necessary
		resizeLabels();

	}

	private void createTables() {
		// Components Table
		new SetupTableComponentLibrary().setup();
		ComponentLibraryTable.filterComponentsTextField.setTooltip(new Tooltip("Enter text to begin filtering"));

		// Create ScenarioRow Table
		new SetupTableCreateScenario().setup();

		// Run Scenarios Table
		new SetupTableScenariosLibrary().setup();
	}

	private void createComponentLibraryPane() {
		// Labels
		labelComponentLibrary = utils.createLabel("Component Library", 1.7 * styles.bigButtonWid);
		labelSearchComponentLibrary = utils.createLabel("Search:", styles.bigButtonWid);
		
		// Creates the HBox in which the search bar, buttons, etc are stored
		HBox paneObjects = new HBox();
		Client.paneCandidateComponents = new PaneNewScenarioComponent();

		// Adds all the components to the HBox
		paneObjects.getChildren().addAll(labelComponentLibrary, utils.getSeparator(Orientation.VERTICAL, 15, false),
				labelSearchComponentLibrary, ComponentLibraryTable.filterComponentsTextField,
				utils.getSeparator(Orientation.VERTICAL, 10, false), Client.buttonNewComponent,
				utils.getSeparator(Orientation.VERTICAL, 10, false), Client.buttonEditComponent,
				utils.getSeparator(Orientation.VERTICAL, 10, false), Client.buttonBrowseComponentLibrary,
				utils.getSeparator(Orientation.VERTICAL, 10, false),
				Client.buttonDeleteComponent, utils.getSeparator(Orientation.VERTICAL, 10, false),
				Client.buttonRefreshComponents);

		// adds top-left pane to the main window
		vBoxComponentLibrary = new VBox(5);
		vBoxComponentLibrary.getChildren().addAll(paneObjects, Client.paneCandidateComponents.getvBox());
		vBoxComponentLibrary.setStyle(styles.style1);

	}

	private void createCreateScenarioPane() {
		labelScenarioName = utils.createLabel("Create Scenario", 2 * styles.bigButtonWid);
		vBoxCreateScenario = new VBox(5);
		Client.paneCreateScenario = new PaneCreateScenario(Client.primaryStage);
		Client.paneCreateScenario.getvBox().setStyle(styles.font_style);
		vBoxCreateScenario.getChildren().addAll(Client.paneCreateScenario.getvBox());
		vBoxCreateScenario.setStyle(styles.style1);
	}

	private void createScenarioLibraryPane() {
		// Labels
		labelScenarioLibrary = utils.createLabel("Scenario Library", styles.bigButtonWid * 1.75);

		TextField filterScenarioTextField = utils.createTextField();
		filterScenarioTextField.setMinWidth(styles.bigButtonWid);
		filterScenarioTextField.setPrefWidth(styles.bigButtonWid * 1.75);
		filterScenarioTextField.setTooltip(new Tooltip("Enter text to begin filtering"));

		ScenarioTable.filteredScenarios = new FilteredList<>(ScenarioTable.tableScenariosLibrary.getItems(), p -> true);

		filterScenarioTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			ScenarioTable.filteredScenarios.setPredicate(myfilerun -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				String lowerCaseFilter = newValue.toLowerCase();

				if (myfilerun.getScenarioName().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				}
				return false;
			});
		});

		SortedList<ScenarioRow> sortedScenarios = new SortedList<>(ScenarioTable.filteredScenarios);
		sortedScenarios.comparatorProperty().bind(ScenarioTable.tableScenariosLibrary.comparatorProperty());
		ScenarioTable.tableScenariosLibrary.setItems(sortedScenarios);

		Client.paneWorkingScenarios = new PaneScenarioLibrary(Client.primaryStage);
		Client.paneWorkingScenarios.gethBox().setStyle(styles.font_style);

		// Produces the bottom pane and adds components
		HBox buttonHBox = new HBox();
		HBox bottomPane = new HBox(60);
		labelSearchScenarios = utils.createLabel("Search:",styles.bigButtonWid);
		labelSearchScenarios.setTextAlignment(TextAlignment.LEFT);

		
		buttonHBox.getChildren().addAll(labelSearchScenarios, filterScenarioTextField,
				utils.getSeparator(Orientation.VERTICAL, 6, false), Client.buttonEditScenario,
				utils.getSeparator(Orientation.VERTICAL, 6, false), Client.buttonViewConfig,
				utils.getSeparator(Orientation.VERTICAL, 6, false), Client.buttonBrowseScenarioFolder,
				/*utils.getSeparator(Orientation.VERTICAL, 6, false), Client.buttonImport,*/
				utils.getSeparator(Orientation.VERTICAL, 6, false), Client.buttonRunScenario, Client.buttonDeleteScenario,
				utils.getSeparator(Orientation.VERTICAL, 6, false), Client.buttonResults, Client.buttonResultsForSelected, utils.getSeparator(Orientation.VERTICAL, 6, false), Client.buttonDiffFiles,
				Client.buttonShowRunQueue, utils.getSeparator(Orientation.VERTICAL, 6, false), Client.buttonViewExeLog,Client.buttonViewLog, Client.buttonViewExeErrors, Client.buttonViewErrors, utils.getSeparator(Orientation.VERTICAL, 6, false),
				Client.buttonReport,Client.buttonRefreshScenarioStatus);

		bottomPane.getChildren().addAll(labelScenarioLibrary,buttonHBox);

		vBoxRun = new VBox(5);
		vBoxRun.getChildren().addAll(bottomPane, Client.paneWorkingScenarios.gethBox());

	}

	private void resizeLabels() {
		labelComponentLibrary = utils.resizeLabelText(labelComponentLibrary);
		labelSearchComponentLibrary = utils.resizeLabelText(labelSearchComponentLibrary);
		labelSearchScenarios = utils.resizeLabelText(labelSearchScenarios);
		labelScenarioLibrary = utils.resizeLabelText(labelScenarioLibrary);
		labelScenarioName = utils.resizeLabelText(labelScenarioName);
	}

	private void createArrowButtons() {
		// Creates the left arrow button that removes selected scenario components
		Client.buttonLeftArrow = utils.createButton(null, styles.bigButtonWid,
				"Remove selected component(s) from scenario", "leftArrow7");

		Client.buttonLeftArrow.setDisable(true);
		Client.buttonLeftArrow.setOnAction(e -> {
			ObservableList<ComponentRow> selectedFiles1 = ComponentLibraryTable.tableCreateScenario.getSelectionModel()
					.getSelectedItems();
			ComponentLibraryTable.removeFromListOfFilesCreatePolicyScenario(selectedFiles1);
			setArrowAndButtonStatus();
		});

		// Creates the double left arrow button that removes all scenario components
		Client.buttonLeftDoubleArrow = utils.createButton(null, styles.bigButtonWid,
				"Remove all components from scenario", "leftDoubleArrow7");

		Client.buttonLeftDoubleArrow.setDisable(true);
		Client.buttonLeftDoubleArrow.setOnAction(e -> {
			ObservableList<ComponentRow> selectedFiles1 = ComponentLibraryTable.tableCreateScenario.getItems();
			ComponentLibraryTable.removeFromListOfFilesCreatePolicyScenario(selectedFiles1);
			setArrowAndButtonStatus();
		});

		// Creates the left arrow button that removes scenarios		
		Client.buttonRightArrow = utils.createButton(null, styles.bigButtonWid, "Add selected component(s) to scenario",
				"rightArrow7");

		Client.buttonRightArrow.setDisable(true);
		Client.buttonRightArrow.setOnAction(e -> {
			ObservableList<ComponentRow> selectedFiles = ComponentLibraryTable.tableComponents.getSelectionModel()
					.getSelectedItems();
			ComponentLibraryTable.addToListOfFilesCreatePolicyScenario(selectedFiles);
			setArrowAndButtonStatus();
		});

		// Creates the up arrow button that loads the components from a chosen
		// run on the bottom table
		Client.buttonEditScenario = utils.createButton("Edit", styles.bigButtonWid,
				"Edit: Move selected scenario from working list to scenario edit pane", "up_right_arrow");

		// the up-arrow button moves the scenario components to the
		// top-right that correspond to the selected scenario
		Client.buttonEditScenario.setDisable(true);
		Client.buttonEditScenario.setOnAction(e -> {
			ObservableList<ScenarioRow> selectedFiles = ScenarioTable.tableScenariosLibrary.getSelectionModel()
					.getSelectedItems();
			if (selectedFiles.size() == 1) {
				ScenarioRow selectedFile = ScenarioTable.tableScenariosLibrary.getSelectionModel().getSelectedItem();
				String scenarioName = selectedFile.getScenarioName().trim();
				String components = selectedFile.getComponents().trim();
				
				if (components.startsWith("Externally-created scenario")) {
					utils.showInformationDialog("Information", "Function not supported.", "Cannot modify scenario components in a scenario created outside of the ScenarioBuilder.");
					return;
				}

				Client.paneCreateScenario.setScenarioName(scenarioName);
				Client.buttonCreateScenarioConfigFile.setDisable(false);

				if (components.endsWith(";"))
					components = components.substring(0, components.length() - 1);
				
				ComponentRow[] mf1Array=null;

				if (components.length() > 0) {
					String[] items = components.split(";");

					mf1Array = new ComponentRow[items.length];

					for (int i = 0; i < items.length; i++) {
						String filename = items[i].trim();
						if (!filename.equals("")) {
							String fullFilename = vars.get("scenarioComponentsDir") + File.separator + filename;
							File f = new File(fullFilename);
							long lastMod = f.lastModified();
							Date date = new Date(lastMod);

							ComponentRow mf1 = new ComponentRow(filename, fullFilename, date);

							mf1Array[i] = mf1;
						}
					}

				}
				
				try {
					//if (mf1Array.length > 0) {
						ComponentLibraryTable.createListOfFilesCreatePolicyScenario(mf1Array);
					//} 
				} catch (Exception exx) {
					utils.warningMessage("Problem trying to modify list order.");
					System.out.println("Non-fatal error when adding files to list: " + exx);
					System.out.println("Attempting to continue...");
				}

			}
			// updates the disabled status of various buttons
			setArrowAndButtonStatus();
		});

		// adds the arrow buttons to the button box
		vBoxButton = new VBox(10);

		vBoxButton.setAlignment(Pos.CENTER);
		vBoxButton.getChildren().addAll(Client.buttonRightArrow, Client.buttonLeftArrow, Client.buttonLeftDoubleArrow);
		vBoxButton.prefWidthProperty().bind(Client.primaryStage.widthProperty().multiply(0.5 / 7.0));

	}

	protected String getFileType(String filename, String typeString) {
		String componentFileType = null;

		if (filename.endsWith(".xml")) {
			componentFileType="xml";
		} else {

			try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
				for (String line; (line = br.readLine()) != null;) {
					if (line.indexOf(typeString) >= 0) {
						componentFileType = line.substring(line.lastIndexOf("=") + 1).trim();
					} else if (line.indexOf("INPUT_TABLE") >= 0) {
						componentFileType = "INPUT_TABLE";
					}
				}
				br.close();
			} catch (Exception e) {
				utils.warningMessage("Problem reading component file "+filename+" to determine type.");
				System.out.println("Error reading scenario component file to determine typ:" + e);

			}

			if (componentFileType == null) {
				utils.warningMessage("Problem reading component file "+filename+" to determine type.");
				System.out.println("file does not include " + typeString + "=. Assuming file of type preset");
				componentFileType = "preset";
			}
		}
		return componentFileType;
	}

	protected void setArrowAndButtonStatus() {
		int num_ScenarioRuns = ScenarioTable.tableScenariosLibrary.getSelectionModel().getSelectedIndices().size();
		int num_CreateScenario = ComponentLibraryTable.tableCreateScenario.getSelectionModel().getSelectedIndices().size();
		int num_CandidateScenarioComponents = ComponentLibraryTable.tableComponents.getSelectionModel().getSelectedIndices()
				.size();

		// if (true) {
		// runTable
		if (ComponentLibraryTable.tableCreateScenario.getItems().size()>0) { 
			Client.buttonLeftDoubleArrow.setDisable(false);
		} else {
			Client.buttonLeftDoubleArrow.setDisable(true);
		}

		if (num_ScenarioRuns >= 1) {
			Client.buttonBrowseScenarioFolder.setDisable(false);
			Client.buttonDeleteScenario.setDisable(false);
			Client.buttonViewConfig.setDisable(false);
			Client.buttonArchiveScenario.setDisable(false);
			Client.buttonViewLog.setDisable(false);
			Client.buttonViewErrors.setDisable(false);

			if (num_ScenarioRuns == 1) {
				Client.buttonEditScenario.setDisable(false);
				Client.buttonRunScenario.setDisable(false);
				Client.buttonViewConfig.setDisable(false);
				Client.buttonDiffFiles.setDisable(true);
				Client.buttonResultsForSelected.setDisable(false);
			}
			if (num_ScenarioRuns > 1) {
				Client.buttonEditScenario.setDisable(true);
				Client.buttonRunScenario.setDisable(false);
				Client.buttonViewConfig.setDisable(false);
				Client.buttonDiffFiles.setDisable(true);
				Client.buttonResultsForSelected.setDisable(true);
			}
			if (num_ScenarioRuns == 2) {
				Client.buttonDiffFiles.setDisable(false);
			}
		} else {
			Client.buttonBrowseScenarioFolder.setDisable(true);
			Client.buttonRunScenario.setDisable(true);
			Client.buttonDeleteScenario.setDisable(true);
			Client.buttonViewConfig.setDisable(true);
			Client.buttonArchiveScenario.setDisable(true);
			Client.buttonEditScenario.setDisable(true);
			Client.buttonDiffFiles.setDisable(true);
			Client.buttonViewLog.setDisable(true);
			Client.buttonViewErrors.setDisable(true);
			Client.buttonResultsForSelected.setDisable(true);
		}
		
		

		// create scenario
		if (num_CreateScenario >= 1) {
			Client.buttonLeftArrow.setDisable(false);
			if (num_CreateScenario == 1) {
				Client.buttonMoveComponentUp.setDisable(false);
				Client.buttonMoveComponentDown.setDisable(false);
			}
		} else {
			Client.buttonLeftArrow.setDisable(true);
			Client.buttonMoveComponentUp.setDisable(true);
			Client.buttonMoveComponentDown.setDisable(true);
		}

		if (Client.paneCreateScenario.getTextFieldScenarioName().getText().length() > 0) {
			Client.buttonCreateScenarioConfigFile.setDisable(false);
		} else {
			Client.buttonCreateScenarioConfigFile.setDisable(true);
		}

		// candidate scenario components
		if (num_CandidateScenarioComponents >= 1) {
			Client.buttonRightArrow.setDisable(false);
			Client.buttonDeleteComponent.setDisable(false);
			if (num_CandidateScenarioComponents==1) {
				Client.buttonEditComponent.setDisable(false);
			} else {
				Client.buttonEditComponent.setDisable(true);
			}

		} else {
			Client.buttonRightArrow.setDisable(true);
			Client.buttonDeleteComponent.setDisable(true);
			Client.buttonEditComponent.setDisable(true);
		}
		// }

	}

//	protected void writeRunsTxtFile() {// two pans used
//
//		Predicate<? super ScenarioRow> predicate = ScenarioTable.filteredScenarios.getPredicate();
//
//		ScenarioTable.filteredScenarios.setPredicate(p -> true);
//
//		String runsFilename = vars.get("gCamGUILogDir") + File.separator+"Runs.txt";
//		File runsFile = new File(runsFilename);
//		runsFile.delete();
//
//		ArrayList<String> content = new ArrayList<String>();
//
//		for (int i = 0; i < ScenarioTable.filteredScenarios.size(); i++) {
//
//			String name = ScenarioTable.filteredScenarios.get(i).getScenarioName();
//			String components = ScenarioTable.filteredScenarios.get(i).getComponents();
//			Date date = ScenarioTable.filteredScenarios.get(i).getRunDate();
//			String isComplete = ScenarioTable.filteredScenarios.get(i).getIsComplete();
//			String line = name.trim() + "," + components.trim() + "," + date + "," + isComplete;
//			content.add(line);
//		}
//		try {
//			files.saveFile(content, runsFilename);
//		} catch (Exception e) {
//			utils.warningMessage("Problem writing xml file.");
//			System.out.println("Error writing to xml file: " + e);
//			utils.exitOnException();
//		}
//
//		ScenarioTable.filteredScenarios.setPredicate(predicate);
//
//	}

	public VBox getvBoxComponentLibrary() {
		return vBoxComponentLibrary;
	}

	public VBox getvBoxCreateScenario() {
		return vBoxCreateScenario;
	}

	public VBox getvBoxButton() {
		return vBoxButton;
	}

	public VBox getvBoxRun() {
		return vBoxRun;
	}

}
