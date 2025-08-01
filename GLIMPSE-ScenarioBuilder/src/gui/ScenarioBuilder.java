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
 * and that User is not otherwise prohibited
 * under the Export Laws from receiving the Software.
 *
 * SUPPORT
 * For the GLIMPSE project, GCAM development, data processing, and support for
 * policy implementations has been led by Dr. Steven J. Smith of PNNL, via Interagency
 * Agreements 89-92423101 and 89-92549601. Contributors * from PNNL include
 * Maridee Weber, Catherine Ledna, Gokul Iyer, Page Kyle, Marshall Wise, Matthew
 * Binsted, and Pralit Patel. Coding contributions have also been made by Aaron
 * Parks and Yadong Xu of ARA through the EPAs Environmental Modeling and
 * Visualization Laboratory contract.
 *
 */
package gui;

import glimpseBuilder.SetupTableComponentLibrary;
import glimpseBuilder.SetupTableCreateScenario;
import glimpseBuilder.SetupTableScenariosLibrary;
import glimpseElement.ComponentLibraryTable;
import glimpseElement.ComponentRow;
import glimpseElement.ScenarioRow;
import glimpseElement.ScenarioTable;
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Builds the main user interface for the Scenario Builder application.
 * This class sets up all the primary JavaFX panes, tables, buttons, and event handlers.
 *
 * This version has been updated to reflect Java 8+ best practices, including
 * the use of lambda expressions, method references, and the Streams API.
 */
public class ScenarioBuilder {

	// Constants for UI Labels and Tooltips
	private static final String LABEL_COMPONENT_LIBRARY = "Component Library";
	private static final String LABEL_CREATE_SCENARIO = "Create Scenario";
	private static final String LABEL_SCENARIO_LIBRARY = "Scenario Library";
	private static final String LABEL_SEARCH = "Search:";
	private static final String TOOLTIP_FILTER = "Enter text to begin filtering";
	private static final String TOOLTIP_REMOVE_SELECTED_COMPONENTS = "Remove selected component(s) from scenario";
	private static final String TOOLTIP_REMOVE_ALL_COMPONENTS = "Remove all components from scenario";
	private static final String TOOLTIP_ADD_SELECTED_COMPONENTS = "Add selected component(s) to scenario";
	private static final String TOOLTIP_EDIT_SCENARIO = "Edit: Move selected scenario from working list to scenario edit pane";

	// Constants for Internal Logic
	private static final String FILE_TYPE_XML = "xml";
	private static final String FILE_TYPE_PRESET = "preset";
	private static final String FILE_TYPE_INPUT_TABLE = "INPUT_TABLE";
	private static final String EXTERNALLY_CREATED_SCENARIO_PREFIX = "Externally-created scenario";

	// Singleton Instance
	public static final ScenarioBuilder instance = new ScenarioBuilder();

	// UI Panes
	protected VBox vBoxComponentLibrary;
	protected VBox vBoxCreateScenario;
	protected VBox vBoxButton;
	protected VBox vBoxRun;

	// UI Labels
	protected Label labelComponentLibrary;
	protected Label labelSearchComponentLibrary;
	protected Label labelSearchScenarios;
	protected Label labelScenarioLibrary;
	protected Label labelScenarioName;

	// GLIMPSE Utilities
	protected final GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	protected final GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	protected final GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	protected final GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

	public static ScenarioBuilder getInstance() {
		return instance;
	}

	public ScenarioBuilder() {
		// Private constructor for singleton pattern
	}

	public void build() {
		vars.init(utils, vars, styles, files);
		files.init(utils, vars, styles, files);
		utils.init(utils, vars, styles, files);

		createTables();
		createArrowButtons();
		createComponentLibraryPane();
		createCreateScenarioPane();
		createScenarioLibraryPane();
		resizeLabels();
	}

	private void createTables() {
		new SetupTableComponentLibrary().setup();
		ComponentLibraryTable.getFilterComponentsTextField().setTooltip(new Tooltip(TOOLTIP_FILTER));

		new SetupTableCreateScenario().setup();
		new SetupTableScenariosLibrary().setup();
	}

	private void createComponentLibraryPane() {
		labelComponentLibrary = utils.createLabel(LABEL_COMPONENT_LIBRARY, 1.7 * styles.getBigButtonWidth());
		labelSearchComponentLibrary = utils.createLabel(LABEL_SEARCH, styles.getBigButtonWidth());

		HBox paneObjects = new HBox();
		Client.paneCandidateComponents = new PaneNewScenarioComponent();

		paneObjects.getChildren().addAll(
			labelComponentLibrary, utils.getSeparator(Orientation.VERTICAL, 15, false),
			labelSearchComponentLibrary, ComponentLibraryTable.getFilterComponentsTextField(),
			utils.getSeparator(Orientation.VERTICAL, 10, false), Client.buttonNewComponent,
			utils.getSeparator(Orientation.VERTICAL, 10, false), Client.buttonEditComponent,
			utils.getSeparator(Orientation.VERTICAL, 10, false), Client.buttonBrowseComponentLibrary,
			utils.getSeparator(Orientation.VERTICAL, 10, false), Client.buttonDeleteComponent,
			utils.getSeparator(Orientation.VERTICAL, 10, false), Client.buttonRefreshComponents
		);

		vBoxComponentLibrary = new VBox(5, paneObjects, Client.paneCandidateComponents.getvBox());
		vBoxComponentLibrary.setStyle(styles.getStyle1());
	}

	private void createCreateScenarioPane() {
		labelScenarioName = utils.createLabel(LABEL_CREATE_SCENARIO, 2 * styles.getBigButtonWidth());
		Client.paneCreateScenario = new PaneCreateScenario(Client.primaryStage);
		Client.paneCreateScenario.getvBox().setStyle(styles.getFontStyle());

		vBoxCreateScenario = new VBox(5, Client.paneCreateScenario.getvBox());
		vBoxCreateScenario.setStyle(styles.getStyle1());
	}

	private void createScenarioLibraryPane() {
		labelScenarioLibrary = utils.createLabel(LABEL_SCENARIO_LIBRARY, styles.getBigButtonWidth() * 1.75);

		TextField filterScenarioTextField = utils.createTextField();
		filterScenarioTextField.setMinWidth(styles.getBigButtonWidth());
		filterScenarioTextField.setPrefWidth(styles.getBigButtonWidth() * 1.75);
		filterScenarioTextField.setTooltip(new Tooltip(TOOLTIP_FILTER));

		ScenarioTable.filteredScenarios = new FilteredList<>(ScenarioTable.tableScenariosLibrary.getItems(), p -> true);

		filterScenarioTextField.textProperty().addListener((observable, oldValue, newValue) ->
			ScenarioTable.filteredScenarios.setPredicate(scenarioRow -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				String lowerCaseFilter = newValue.toLowerCase();
				return scenarioRow.getScenarioName().toLowerCase().contains(lowerCaseFilter);
			})
		);

		SortedList<ScenarioRow> sortedScenarios = new SortedList<>(ScenarioTable.filteredScenarios);
		sortedScenarios.comparatorProperty().bind(ScenarioTable.tableScenariosLibrary.comparatorProperty());
		ScenarioTable.tableScenariosLibrary.setItems(sortedScenarios);

		Client.paneWorkingScenarios = new PaneScenarioLibrary(Client.primaryStage);
		Client.paneWorkingScenarios.gethBox().setStyle(styles.getFontStyle());

		HBox buttonHBox = new HBox();
		labelSearchScenarios = utils.createLabel(LABEL_SEARCH, styles.getBigButtonWidth());
		labelSearchScenarios.setTextAlignment(TextAlignment.LEFT);

		buttonHBox.getChildren().addAll(
			labelSearchScenarios, filterScenarioTextField,
			utils.getSeparator(Orientation.VERTICAL, 6, false), Client.buttonEditScenario,
			utils.getSeparator(Orientation.VERTICAL, 6, false), Client.buttonViewConfig,
			utils.getSeparator(Orientation.VERTICAL, 6, false), Client.buttonBrowseScenarioFolder,
			utils.getSeparator(Orientation.VERTICAL, 6, false), Client.buttonRunScenario,
			Client.buttonDeleteScenario, utils.getSeparator(Orientation.VERTICAL, 6, false),
			Client.buttonResults, Client.buttonResultsForSelected,
			utils.getSeparator(Orientation.VERTICAL, 6, false), Client.buttonDiffFiles,
			Client.buttonShowRunQueue, utils.getSeparator(Orientation.VERTICAL, 6, false),
			Client.buttonViewExeLog, Client.buttonViewLog, Client.buttonViewExeErrors, Client.buttonViewErrors,
			utils.getSeparator(Orientation.VERTICAL, 6, false), Client.buttonReport, Client.buttonRefreshScenarioStatus
		);

		HBox bottomPane = new HBox(60, labelScenarioLibrary, buttonHBox);
		vBoxRun = new VBox(5, bottomPane, Client.paneWorkingScenarios.gethBox());
	}

	private void createArrowButtons() {
		Client.buttonLeftArrow = utils.createButton(null, styles.getBigButtonWidth(), TOOLTIP_REMOVE_SELECTED_COMPONENTS, "leftArrow7");
		Client.buttonLeftArrow.setDisable(true);
		Client.buttonLeftArrow.setOnAction(this::removeSelectedComponents);

		Client.buttonLeftDoubleArrow = utils.createButton(null, styles.getBigButtonWidth(), TOOLTIP_REMOVE_ALL_COMPONENTS, "leftDoubleArrow7");
		Client.buttonLeftDoubleArrow.setDisable(true);
		Client.buttonLeftDoubleArrow.setOnAction(this::removeAllComponents);

		Client.buttonRightArrow = utils.createButton(null, styles.getBigButtonWidth(), TOOLTIP_ADD_SELECTED_COMPONENTS, "rightArrow7");
		Client.buttonRightArrow.setDisable(true);
		Client.buttonRightArrow.setOnAction(this::addSelectedComponents);

		Client.buttonEditScenario = utils.createButton("Edit", styles.getBigButtonWidth(), TOOLTIP_EDIT_SCENARIO, "up_right_arrow");
		Client.buttonEditScenario.setDisable(true);
		Client.buttonEditScenario.setOnAction(this::loadSelectedScenarioForEditing);

		vBoxButton = new VBox(10, Client.buttonRightArrow, Client.buttonLeftArrow, Client.buttonLeftDoubleArrow);
		vBoxButton.setAlignment(Pos.CENTER);
		vBoxButton.prefWidthProperty().bind(Client.primaryStage.widthProperty().multiply(0.5 / 7.0));
	}

	// --- Event Handlers for Buttons ---

	private void removeSelectedComponents(ActionEvent event) {
		ObservableList<ComponentRow> selectedItems = ComponentLibraryTable.getTableCreateScenario().getSelectionModel().getSelectedItems();
		ComponentLibraryTable.removeFromListOfFilesCreatePolicyScenario(selectedItems);
		setArrowAndButtonStatus();
	}

	private void removeAllComponents(ActionEvent event) {
		ObservableList<ComponentRow> allItems = ComponentLibraryTable.getTableCreateScenario().getItems();
		ComponentLibraryTable.removeFromListOfFilesCreatePolicyScenario(allItems);
		setArrowAndButtonStatus();
	}

	private void addSelectedComponents(ActionEvent event) {
		ObservableList<ComponentRow> selectedItems = ComponentLibraryTable.getTableComponents().getSelectionModel().getSelectedItems();
		ComponentLibraryTable.addToListOfFilesCreatePolicyScenario(selectedItems);
		setArrowAndButtonStatus();
	}

	private void loadSelectedScenarioForEditing(ActionEvent event) {
		ObservableList<ScenarioRow> selectedScenarios = ScenarioTable.tableScenariosLibrary.getSelectionModel().getSelectedItems();
		if (selectedScenarios.size() != 1) {
			return;
		}

		ScenarioRow selectedScenario = selectedScenarios.get(0);
		String scenarioName = selectedScenario.getScenarioName().trim();
		String components = selectedScenario.getComponents().trim();

		if (components.startsWith(EXTERNALLY_CREATED_SCENARIO_PREFIX)) {
			utils.showInformationDialog("Information", "Function not supported.", "Cannot modify scenario components in a scenario created outside of the ScenarioBuilder.");
			return;
		}

		Client.paneCreateScenario.setScenarioName(scenarioName);
		Client.buttonCreateScenarioConfigFile.setDisable(false);

		if (components.endsWith(";")) {
			components = components.substring(0, components.length() - 1);
		}

		ComponentRow[] componentRows = new ComponentRow[0];
		if (!components.isEmpty()) {
			componentRows = Arrays.stream(components.split(";"))
				.map(String::trim)
				.filter(name -> !name.isEmpty())
				.map(this::createComponentRowFromFilename)
				.toArray(ComponentRow[]::new);
		}

		try {
			ComponentLibraryTable.createListOfFilesCreatePolicyScenario(componentRows);
		} catch (Exception e) {
			utils.warningMessage("Problem trying to modify list order.");
			System.err.println("Non-fatal error when adding files to list: " + e);
			System.err.println("Attempting to continue...");
		}

		setArrowAndButtonStatus();
	}

	private ComponentRow createComponentRowFromFilename(String filename) {
		String fullFilename = vars.getScenarioComponentsDir() + File.separator + filename;
		File file = new File(fullFilename);
		Date lastModified = new Date(file.lastModified());
		return new ComponentRow(filename, fullFilename, lastModified);
	}

	private void resizeLabels() {
		labelComponentLibrary = utils.resizeLabelText(labelComponentLibrary);
		labelSearchComponentLibrary = utils.resizeLabelText(labelSearchComponentLibrary);
		labelSearchScenarios = utils.resizeLabelText(labelSearchScenarios);
		labelScenarioLibrary = utils.resizeLabelText(labelScenarioLibrary);
		labelScenarioName = utils.resizeLabelText(labelScenarioName);
	}

	protected String getFileType(String filename, String typeString) {
		if (filename.endsWith(".xml")) {
			return FILE_TYPE_XML;
		}

		try (Stream<String> lines = Files.lines(Paths.get(filename))) {
			Optional<String> componentType = lines.map(String::trim)
				.filter(line -> line.contains(typeString) || line.contains(FILE_TYPE_INPUT_TABLE))
				.map(line -> {
					if (line.contains(typeString)) {
						return line.substring(line.lastIndexOf("=") + 1).trim();
					} else {
						return FILE_TYPE_INPUT_TABLE;
					}
				})
				.findFirst();

			if (componentType.isPresent()) {
				return componentType.get();
			}

		} catch (IOException e) {
			utils.warningMessage("Problem reading component file " + filename + " to determine type.");
			System.err.println("Error reading scenario component file to determine type: " + e);
		}

		System.err.println("File does not include " + typeString + "=. Assuming file of type preset.");
		return FILE_TYPE_PRESET;
	}

	protected void setArrowAndButtonStatus() {
		int numSelectedScenarios = ScenarioTable.tableScenariosLibrary.getSelectionModel().getSelectedItems().size();
		int numSelectedCreate = ComponentLibraryTable.getTableCreateScenario().getSelectionModel().getSelectedItems().size();
		int numSelectedCandidate = ComponentLibraryTable.getTableComponents().getSelectionModel().getSelectedItems().size();

		// Scenario Library buttons
		boolean hasScenariosSelected = numSelectedScenarios >= 1;
		Client.buttonBrowseScenarioFolder.setDisable(!hasScenariosSelected);
		Client.buttonDeleteScenario.setDisable(!hasScenariosSelected);
		Client.buttonViewConfig.setDisable(!hasScenariosSelected);
		Client.buttonArchiveScenario.setDisable(!hasScenariosSelected);
		Client.buttonViewLog.setDisable(!hasScenariosSelected);
		Client.buttonViewErrors.setDisable(!hasScenariosSelected);
		Client.buttonRunScenario.setDisable(!hasScenariosSelected);
		Client.buttonEditScenario.setDisable(numSelectedScenarios != 1);
		Client.buttonResultsForSelected.setDisable(numSelectedScenarios != 1);
		Client.buttonDiffFiles.setDisable(numSelectedScenarios != 2);

		// Create Scenario (middle) buttons
		Client.buttonLeftDoubleArrow.setDisable(ComponentLibraryTable.getTableCreateScenario().getItems().isEmpty());
		boolean hasCreateItemsSelected = numSelectedCreate >= 1;
		Client.buttonLeftArrow.setDisable(!hasCreateItemsSelected);
		Client.buttonMoveComponentUp.setDisable(!hasCreateItemsSelected);
		Client.buttonMoveComponentDown.setDisable(!hasCreateItemsSelected);
		boolean hasScenarioName = Client.paneCreateScenario.getTextFieldScenarioName().getText().length() > 0;
		Client.buttonCreateScenarioConfigFile.setDisable(!hasScenarioName);

		// Component Library (candidate) buttons
		boolean hasCandidatesSelected = numSelectedCandidate >= 1;
		Client.buttonRightArrow.setDisable(!hasCandidatesSelected);
		Client.buttonDeleteComponent.setDisable(!hasCandidatesSelected);
		Client.buttonEditComponent.setDisable(numSelectedCandidate != 1);
	}


	// --- Getters for main layout panes ---

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