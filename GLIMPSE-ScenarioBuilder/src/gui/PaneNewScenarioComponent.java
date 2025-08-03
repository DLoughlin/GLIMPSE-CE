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

import static javafx.stage.Modality.APPLICATION_MODAL;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import glimpseElement.PolicyTab;
import glimpseElement.ScenarioRow;
import glimpseElement.ScenarioTable;
import glimpseElement.TabCafeStd;
import glimpseElement.ComponentRow;
import glimpseElement.TabMarketShare;
import glimpseElement.TabFuelPriceAdj;
import glimpseElement.ComponentLibraryTable;
import glimpseElement.TabXMLList;
import glimpseElement.TabPollutantTaxCap;
import glimpseElement.TabTechTax;
import glimpseUtil.FileChooserPlus;
import glimpseElement.TabTechAvailable;
import glimpseElement.TabTechBound;
import glimpseElement.TabTechParam;
import glimpseElement.TabFixedDemand;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

// /////////////////////////////////////////////////////////////////////////////////////////
// this class generates the top-left pane
// /////////////////////////////////////////////////////////////////////////////////////////
/**
 * PaneNewScenarioComponent generates the top-left pane for scenario component management in the GLIMPSE Scenario Builder.
 * It provides UI and logic for creating, editing, deleting, and saving scenario components.
 */
public class PaneNewScenarioComponent extends gui.ScenarioBuilder {// VBox {

	// === UI Components ===
	// Main layout containers
	VBox vBox = createMainVBox();
	HBox hBoxButton = createButtonHBox();

	// Dialog UI elements
	ProgressBar progressBar;
	Button buttonSaveComponent;
	Button buttonClose;
	HBox hBoxButtons;
	HBox hBoxProgress;

	// === Tab Components ===
	TabPollutantTaxCap pollTaxCapTab;
	TabMarketShare techMarketShareTab;
	TabTechBound techBoundTab;
	TabTechAvailable techAvailTab;
	TabFixedDemand fixedDemandTab;
	TabTechParam techParamTab;
	TabTechTax techTaxTab;
	TabFuelPriceAdj fuelPriceAdjTab;
	TabCafeStd cafeStdTab;
	TabXMLList xmlListTab;
	PolicyTab tab;

	// === State and Task Management ===
	Thread computationalThread;
	Task<?> saveTask;
	Thread saveThread;
	Stage stageWithTabs;
	double progress = 0.0;
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd: HH:mm", Locale.ENGLISH);

	// === Tab name constants to avoid magic strings ===
	private static final String TAB_MARKET_SHARE = "Market Share";
	private static final String TAB_FLEX_SHARE = "Flex Share";
	private static final String TAB_MPG_TARGET = "MPG Target";
	private static final String TAB_TECH_BOUND = "Tech Bound";
	private static final String TAB_TECH_AVAIL = "Tech Avail";
	private static final String TAB_TECH_PARAM = "Tech Param";
	private static final String TAB_TECH_TAX = "Tech Tax/Subsidy";
	private static final String TAB_XML_LIST = "XML List";
	private static final String TAB_POLLUTANT_TAX_CAP = "Pollutant Tax/Cap";
	private static final String TAB_FUEL_PRICE_ADJ = "Fuel Price Adj";
	private static final String TAB_FIXED_DEMAND = "Fixed Demand";

	/**
	 * Constructor for PaneNewScenarioComponent. Initializes UI components and event handlers.
	 */
	public PaneNewScenarioComponent() {
		vBox.setStyle(styles.getFontStyle());
		initializeButtons();
		initializeComponentLibraryTable();
		setupEventHandlers();
		vBox.getChildren().addAll(ComponentLibraryTable.getTableComponents());
		vBox.prefWidthProperty().bind(Client.primaryStage.widthProperty().multiply(4.0 / 7.0));
	}

	// === Initialization Methods ===
	private void initializeButtons() {
		Client.buttonNewComponent = utils.createButton("New", styles.getBigButtonWidth(),
				"New: Open dialog to create new scenario component", "add");
		Client.buttonEditComponent = utils.createButton("Edit", styles.getBigButtonWidth(),
				"Edit: Edit selected scenario component", "edit");
		Client.buttonEditComponent.setDisable(true);
		Client.buttonBrowseComponentLibrary = utils.createButton("Browse", styles.getBigButtonWidth(),
				"Browse: Open scenario component library folder", "open_folder");
		Client.buttonDeleteComponent = utils.createButton("Delete", styles.getBigButtonWidth(),
				"Delete: Remove selected scenario component", "delete");
		Client.buttonDeleteComponent.setDisable(true);
		Client.buttonRefreshComponents = utils.createButton("Refresh", styles.getBigButtonWidth(),
				"Refresh: Reload list of candidate scenario components", "refresh");
	}

	private void initializeComponentLibraryTable() {
		try {
			refreshComponentLibraryTable();
		} catch (Exception exception) {
			utils.warningMessage("Problem loading scenario component files.");
			System.out.println("Error loading scenario component files from:");
			System.out.println("    " + vars.getScenarioComponentsDir());
			System.out.println("Error: " + exception);
			utils.exitOnException();
		}
	}

	private void setupEventHandlers() {
		ComponentLibraryTable.getTableComponents().setOnMouseClicked(event -> setArrowAndButtonStatus());
		Client.buttonDeleteComponent.setDisable(true);
		Client.buttonNewComponent.setOnAction(event -> showComponentDialog(null, null, Client.primaryStage, null, null));
		Client.buttonEditComponent.setOnAction(event -> handleEditComponent());
		Client.buttonRefreshComponents.setOnAction(event -> refreshComponentLibraryTable());
		Client.buttonBrowseComponentLibrary.setOnAction(event -> handleBrowseComponentLibrary());
		Client.buttonDeleteComponent.setOnAction(event -> handleDeleteComponent());
	}

	// === Event Handlers ===
	/**
	 * Handles the logic for editing a scenario component.
	 * - Only allows editing if exactly one component is selected.
	 * - If the selected file is an XML, it opens in the XML editor.
	 * - Otherwise, attempts to determine the component type from the file's contents
	 *   and opens the appropriate dialog for editing.
	 */
	private void handleEditComponent() {
		ObservableList<ComponentRow> selectedComponentRows = ComponentLibraryTable.getTableComponents().getSelectionModel().getSelectedItems();
		// Only allow editing if exactly one component is selected
		if (selectedComponentRows.size() != 1) {
			utils.showInformationDialog("Information", "Unsupported action",
					"Editing requires exactly one scenario component to be selected.");
			return;
		}
		String componentFilePath = selectedComponentRows.get(0).getAddress();
		System.out.println("Editing component " + componentFilePath);
		// If the file is an XML, open it in the XML editor
		if (componentFilePath.toLowerCase().endsWith(".xml")) {
			String xmlFilePath = vars.getScenarioComponentsDir() + File.separator + componentFilePath;
			files.showFileInXmlEditor(xmlFilePath);
		} else {
			String tabType = null;
			ArrayList<String> fileContents = files.getStringArrayFromFile(componentFilePath, null);
			// Try to determine the tab type from the file's first line or header
			if (fileContents.size() > 1) {
				String firstLine = fileContents.get(0);
				if (firstLine.indexOf("xmllist") > -1) {
					tabType = "XML List";
				}
			}
			// If not found, search for a header line indicating the scenario component type
			if (tabType == null) {
				for (String line : fileContents) {
					if (line.startsWith("#Scenario component type:")) {
						tabType = line.substring(line.indexOf(":") + 1).trim();
					}
				}
			}
			// Open the dialog for editing the component with the detected tab type
			showComponentDialog(null, null, Client.primaryStage, tabType, fileContents);
		}
	}

	private void handleBrowseComponentLibrary() {
		try {
			String scenarioComponentsDirectory = vars.getScenarioComponentsDir();
			files.openFileExplorer(scenarioComponentsDirectory);
		} catch (Exception exception) {
			exception.printStackTrace();
			utils.exitOnException();
		}
	}

	private void handleDeleteComponent() {
		ObservableList<ComponentRow> selectedComponentRows = ComponentLibraryTable.getTableComponents().getSelectionModel().getSelectedItems();
		// Prevent deletion if any selected component is used in a scenario
		if (checkIfComponentsAreUsed(selectedComponentRows)) {
			String message = "Cannot delete selected scenario component since it is used in a scenario.";
			utils.warningMessage(message);
			return;
		}
		// Confirm with the user before deleting
		if (!utils.confirmDelete())
			return;
		List<ComponentRow> componentsToRemove = new ArrayList<>();
		for (ComponentRow componentRow : selectedComponentRows) {
			String componentFilePath = componentRow.getAddress();
			String trashFileName = componentRow.getFileName();
			// Remove any directory path from the filename for trash
			if (trashFileName.indexOf(File.separator) > 0)
				trashFileName = trashFileName.substring(trashFileName.lastIndexOf(File.separator) + 1);
			String trashFilePath = vars.getTrashDir() + File.separator + trashFileName;
			try {
				// Move the file to the trash directory, replacing if it already exists
				Files.move(Paths.get(componentFilePath), Paths.get(trashFilePath),
						StandardCopyOption.REPLACE_EXISTING);
				componentsToRemove.add(componentRow);
			} catch (Exception exception) {
				utils.warningMessage("Problem moving file " + componentFilePath + " to trash");
				System.out.println("error:" + exception);
				utils.exitOnException();
			}
		}
		// Remove the deleted components from the table after all moves are complete
		ComponentLibraryTable.removeFromListOfFiles(javafx.collections.FXCollections.observableArrayList(componentsToRemove));
	}

	/**
	 * Checks if any of the selected scenario components are used in existing scenarios.
	 * @param selectedFiles List of selected ComponentRow objects
	 * @return true if any component is used, false otherwise
	 */
	private boolean checkIfComponentsAreUsed(ObservableList<ComponentRow> selectedFiles) {
		boolean b = false;

		// todo: add logic to check to see if items for deletion exist in any of the
		// scenarios in the scenario library
		ObservableList<ScenarioRow> scenario_library = ScenarioTable.listOfScenarioRuns;
		for (int s = 0; s < scenario_library.size(); s++) {
			if (!b) {
				ScenarioRow scenario = scenario_library.get(s);
				String components = scenario.getComponents();
				if (components.length() > 0) {
					for (int c = 0; c < selectedFiles.size(); c++) {
						String file_to_delete = selectedFiles.get(c).getFileName();
						if (components.indexOf(file_to_delete) > -1)
							b = true;
						break;
					}
				}
			}
		}

		return b;
	}

	/**
	 * Shows the dialog for creating or editing a scenario component.
	 * - Sets up the dialog UI, initializes all tab panes, and binds event handlers for save and close actions.
	 * - Handles the logic for saving the component file and updating the component library table.
	 * - Manages the dialog's lifecycle and ensures proper cleanup on close or cancel.
	 *
	 * @param name Name of the component (optional)
	 * @param filename Filename of the component (optional)
	 * @param mainStage The main application stage
	 * @param whichTab The tab to select (optional)
	 * @param contentToLoad Content to load into the tab (optional)
	 */
	private void showComponentDialog(String name, String filename, Stage mainStage, String whichTab,
			ArrayList<String> contentToLoad) {
		stageWithTabs = new Stage();
		double dialogWidth = 950;
		double dialogHeight = 635;

		// === Grouped Dialog UI Elements ===
		hBoxButtons = createButtonHBox();
		buttonSaveComponent = createDialogButton("Save");
		buttonClose = createDialogButton("Close");
		hBoxButtons.getChildren().addAll(buttonSaveComponent, buttonClose);
		hBoxButtons.setStyle(styles.getStyle4());
		hBoxButtons.setSpacing(5.);
		hBoxButtons.setAlignment(javafx.geometry.Pos.CENTER);

		progressBar = createProgressBar(dialogWidth - 25);
		hBoxProgress = createProgressHBox(progressBar);

		xmlListTab = new TabXMLList(TAB_XML_LIST, stageWithTabs, ComponentLibraryTable.getTableComponents());
		xmlListTab.setClosable(false);
		pollTaxCapTab = new TabPollutantTaxCap(TAB_POLLUTANT_TAX_CAP, stageWithTabs);
		pollTaxCapTab.setClosable(false);
		fuelPriceAdjTab = new TabFuelPriceAdj(TAB_FUEL_PRICE_ADJ, stageWithTabs);
		fuelPriceAdjTab.setClosable(false);
		techMarketShareTab = new TabMarketShare(TAB_MARKET_SHARE, stageWithTabs, this);
		techMarketShareTab.setClosable(false);
		techBoundTab = new TabTechBound(TAB_TECH_BOUND, stageWithTabs);
		techBoundTab.setClosable(false);
		cafeStdTab = new TabCafeStd(TAB_MPG_TARGET, stageWithTabs);
		cafeStdTab.setClosable(false);
		techAvailTab = new TabTechAvailable(TAB_TECH_AVAIL, stageWithTabs);
		techAvailTab.setClosable(false);
		techParamTab = new TabTechParam(TAB_TECH_PARAM, stageWithTabs);
		techParamTab.setClosable(false);
		techTaxTab = new TabTechTax(TAB_TECH_TAX, stageWithTabs);
		techTaxTab.setClosable(false);
		fixedDemandTab = new TabFixedDemand(TAB_FIXED_DEMAND, stageWithTabs);
		fixedDemandTab.setClosable(false);

		TabPane addComponentTabPane = new TabPane();

		// hiding techParamTab for now until more testing
		addComponentTabPane.getTabs().addAll(xmlListTab, pollTaxCapTab, techAvailTab, techMarketShareTab, techBoundTab,
				techTaxTab, cafeStdTab, techParamTab, fuelPriceAdjTab, fixedDemandTab);

		addComponentTabPane.setStyle(styles.getStyle1b());
		addComponentTabPane.setPrefHeight(dialogHeight - 25);

		VBox dialogPane = new VBox();
		dialogPane.getChildren().addAll(addComponentTabPane, hBoxProgress, hBoxButtons);

		stageWithTabs.initOwner(mainStage);
		stageWithTabs.initModality(APPLICATION_MODAL);

		stageWithTabs.setScene(new Scene(dialogPane, dialogWidth, dialogHeight));

		stageWithTabs.setTitle("New Scenario Component Creator");

		stageWithTabs.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				if (saveTask != null) {
					saveTask.cancel();
				}
				if (saveThread != null && saveThread.isAlive()) {
					saveThread.interrupt();
				}
				if (stageWithTabs != null) {
					stageWithTabs.hide();
					stageWithTabs.setOnCloseRequest(null);
					stageWithTabs = null;
				}
			}
		});

		buttonClose.setOnAction(e -> {
			if (stageWithTabs != null) {
				stageWithTabs.close();
				stageWithTabs.setOnCloseRequest(null);
				stageWithTabs = null;
			}
		});

		buttonSaveComponent.setOnAction(e -> {

			String which = addComponentTabPane.getSelectionModel().getSelectedItem().getText();
			tab = getTabByName(which);
			if (tab == null) {
				utils.warningMessage("Unknown tab selected: " + which);
				return;
			}
			progressBar.progressProperty().bind(tab.progress_bar.progressProperty());

			saveTask = new Task<Integer>() {

				@Override
				public Integer call() throws Exception {
					tab.saveScenarioComponent();
					return 1;
				}

				@Override
				protected void succeeded() {
					super.succeeded();
					Platform.runLater(() -> {
						System.out.println("Done!");
						saveComponentFile(tab);
					});
				}

				@Override
				protected void cancelled() {
					super.cancelled();
					Platform.runLater(() -> {
						System.out.println("Cancelled!");
						utils.warningMessage("Process of building scenario component cancelled.");
						enableButtons();
						tab.resetFileContent();
						tab.resetFilenameSuggestion();
						tab.resetProgressBar();
					});
				}

				@Override
				protected void failed() {
					super.failed();
					Platform.runLater(() -> {
						System.out.println("Failed!");
					 utils.warningMessage("Process of building scenario component failed.");
						enableButtons();
						tab.resetFileContent();
						tab.resetFilenameSuggestion();
						tab.resetProgressBar();
					});
				}
			};

			saveThread = new Thread(saveTask);
			saveThread.setDaemon(true);
			saveThread.start();

			disableButtons();
		});

		if (whichTab != null) {
			tab = getTabByName(whichTab);
			if (tab != null) {
				selectTabAndLoadContent(whichTab, contentToLoad, addComponentTabPane);
			}
		}

		stageWithTabs.setResizable(false);
		stageWithTabs.show();
	}

	/**
	 * Returns the PolicyTab instance corresponding to the given tab name.
	 * @param tabName The name of the tab
	 * @return The PolicyTab instance, or null if not found
	 */
	private PolicyTab getTabByName(String tabName) {
		switch (tabName) {
			case TAB_MARKET_SHARE:
			case TAB_FLEX_SHARE:
				return techMarketShareTab;
			case TAB_MPG_TARGET:
				return cafeStdTab;
			case TAB_TECH_BOUND:
				return techBoundTab;
			case TAB_TECH_AVAIL:
				return techAvailTab;
			case TAB_TECH_PARAM:
				return techParamTab;
			case TAB_TECH_TAX:
				return techTaxTab;
			case TAB_XML_LIST:
				return xmlListTab;
			case TAB_POLLUTANT_TAX_CAP:
				return pollTaxCapTab;
			case TAB_FUEL_PRICE_ADJ:
				return fuelPriceAdjTab;
			case TAB_FIXED_DEMAND:
				return fixedDemandTab;
			default:
				return null;
		}
	}

	/**
	 * Loads content into the specified PolicyTab and selects it in the TabPane.
	 * @param tab The PolicyTab to load content into
	 * @param contentToLoad The content to load
	 * @param tp The TabPane containing the tabs
	 */
	private void selectTabAndLoadContent(String whichTab, ArrayList<String> contentToLoad, TabPane tp) {
		if (whichTab == null) return;
		for (Tab t : tp.getTabs()) {
			if (t instanceof PolicyTab && t.getText().equals(whichTab)) {
				PolicyTab policyTab = (PolicyTab) t;
				policyTab.loadContent(contentToLoad);
				tp.getSelectionModel().select(policyTab);
				break;
			}
		}
	}

	/**
	 * Saves the scenario component file using the provided PolicyTab's content and filename suggestion.
	 * @param tab The PolicyTab containing the file content and filename suggestion
	 */
	public void saveComponentFile(PolicyTab tab) {
		String filename_suggestion = tab.getFilenameSuggestion();
		String file_content = tab.getFileContent();
		boolean use_temp_file = false;
		if (file_content.equals("use temp file")) {
			use_temp_file = true;
		}

		if ((filename_suggestion != null) && (!filename_suggestion.equals(""))) {
			// opens the browser for saving
			enableButtons();
			tab.resetFileContent();
			tab.resetFilenameSuggestion();
			tab.resetProgressBar();

			String filter1 = "";
			String filter2 = "";

			if (file_content.indexOf("xmllist") >= 0) {
				filter1 = "TXT files (*.txt)";
				filter2 = "txt";
				if ((!filename_suggestion.endsWith(".txt")) && (!filename_suggestion.endsWith(".TXT")))
					filename_suggestion += ".txt";
			} else {
				filter1 = "CSV files (*.csv)";
				filter2 = "csv";
				if ((!filename_suggestion.endsWith(".csv")) && (!filename_suggestion.endsWith(".CSV")))
					filename_suggestion += ".csv";
			}

			File file = FileChooserPlus.showSaveDialog(stageWithTabs, "Save Scenario Component",
					new File(vars.getScenarioComponentsDir()), filename_suggestion,
					FileChooserPlus.createExtensionFilter(filter1, filter2));

			if (file == null)
				return;
			if (!use_temp_file) {
				files.saveFile(file_content, file);
			} else {
				String temp_policy_filename = vars.getGlimpseDir() + File.separator + "GLIMPSE-Data" + File.separator
						+ "temp" + File.separator + "temp_policy_file.txt";
				try {
					Files.move(Paths.get(temp_policy_filename), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception e) {
					System.out.println("Error creating policy file: " + e);
				}
			}

			ComponentRow p1 = new ComponentRow(file.getName(), file.getPath(), new Date());
			ComponentRow[] fileArr = { p1 };

			// Dan: testing something new
			ComponentLibraryTable.addOrUpdateFiles(fileArr);
		}

		refreshComponentLibraryTable();

	}

	/**
	 * Disables the Save and Close buttons in the component dialog.
	 */
	public void disableButtons() {
		buttonSaveComponent.setDisable(true);
		buttonClose.setDisable(true);
	}

	/**
	 * Enables the Save and Close buttons in the component dialog.
	 */
	public void enableButtons() {
		buttonSaveComponent.setDisable(false);
		buttonClose.setDisable(false);
	}

	/**
	 * Refreshes the scenario component library table by scanning the components directory.
	 */
	public void refreshComponentLibraryTable() {

		File folder = new File(vars.getScenarioComponentsDir());

		ArrayList<File> fileList = new ArrayList<File>();
		fileList = buildFileList(folder.toPath());
		ComponentRow[] fileArr = new ComponentRow[fileList.size()];

		int k = 0;
		for (int i = 0; i < fileList.size(); i++) {
			String relative_name = files.getRelativePath(folder.toString(), fileList.get(i).getAbsolutePath());
			ComponentRow p1 = new ComponentRow(relative_name, fileList.get(i).getPath(),
					new Date(fileList.get(i).lastModified()));
			fileArr[k] = p1;
			k++;
		}

		ComponentLibraryTable.createListOfFiles(fileArr);
	}

	/**
	 * Recursively builds a list of files from the given directory path.
	 * @param path The root directory path
	 * @return ArrayList of File objects found in the directory and subdirectories
	 */
	public ArrayList<File> buildFileList(Path path) {
		ArrayList<File> rtn_array = new ArrayList<File>();
		File root = path.toFile();
		File[] list = root.listFiles();

		if (list == null)
			return rtn_array;

		for (File f : list) {
			if (f.isDirectory()) {
				rtn_array.addAll(buildFileList(f.toPath()));

			} else {

				rtn_array.add(f);
			}
		}
		return rtn_array;
	}

	/**
	 * Loads the given list of files into the component library table.
	 * @param file List of File objects to load
	 */
	public void loadFile(List<File> file) {
		int k = 0;
		ComponentRow[] fileArr = new ComponentRow[file.size()];
		for (File i : file) {
			ComponentRow p1 = new ComponentRow(i.getName(), i.getPath(), new Date(i.lastModified()));
			fileArr[k] = p1;
			k++;
		}
		ComponentLibraryTable.addToListOfFiles(fileArr);
	}

	/**
	 * Returns the VBox containing the component library table.
	 * @return VBox with the component library table
	 */
	public VBox getvBox() {
		return vBox;
	}

	private VBox createMainVBox() {
		VBox vbox = new VBox(1);
		vbox.setStyle(styles.getFontStyle());
		return vbox;
	}

	private HBox createButtonHBox() {
		HBox hbox = new HBox(1);
		return hbox;
	}

	private ProgressBar createProgressBar(double width) {
		ProgressBar bar = new ProgressBar(0.0);
		bar.setPrefWidth(width);
		return bar;
	}

	private HBox createProgressHBox(ProgressBar bar) {
		HBox hbox = new HBox();
		hbox.setAlignment(javafx.geometry.Pos.CENTER);
		hbox.getChildren().add(bar);
		return hbox;
	}

	private Button createDialogButton(String text) {
		return utils.createButton(text, styles.getBigButtonWidth(), null);
	}
}
