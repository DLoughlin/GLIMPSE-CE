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
package glimpseElement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.CheckComboBox;
import glimpseBuilder.CsvFileWriter;
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * TabTechParam provides the user interface and logic for creating and editing technology parameter policies
 * in the GLIMPSE Scenario Builder. This tab allows users to select sectors, filter technologies, specify parameters,
 * and configure input/output values for scenario components.
 *
 * <p>
 * <b>Usage:</b> This class is instantiated as a tab in the scenario builder. It extends {@link PolicyTab} and implements {@link Runnable}.
 * </p>
 *
 * <p>
 * <b>Thread Safety:</b> This class is not thread-safe and should be used on the JavaFX Application Thread.
 * </p>
 */
public class TabTechParam extends PolicyTab implements Runnable {
    // === Constants for UI Texts and Options ===
    private static final String SELECT_ONE = "Select One";
    private static final String SELECT_ONE_OR_MORE = "Select One or More";
    private static final String ALL = "All";
    private static final String[] PARAM_OPTIONS = {
            "Shareweight", "Subsector Shareweight", "Nested-Subsector Shareweight", "Levelized Non-Energy Cost",
            "Capacity Factor", "Fixed Output", "Lifetime", "Halflife"
    };
    private static final String[] EMISSION_OPTIONS = {
            "Select One", "NOx", "SO2", "PM10", "PM2.5", "CO", "NH3", "NMVOC", "BC", "OC"
    };
    private static final String WARNING_UNITS_MISMATCH = "Warning - Units do not match!";
    private static final String UNIT_UNITLESS = "unitless";
    private static final String UNIT_YEARS = "years";
    private static final String UNIT_UNITLESS_CAPACITY = "Unitless";
    private static final String LABEL_VALUES = "Values: ";
    private static final String LABEL_SPECIFICATION = "Specification:";
    private static final String LABEL_POPULATE = "Populate:";
    private static final String LABEL_SECTOR = "Sector:";
    private static final String LABEL_FILTER = "Filter:";
    private static final String LABEL_TECHS = "Tech(s): ";
    private static final String LABEL_PARAMETER = "Parameter: ";
    private static final String LABEL_PARAMETER2 = "Parameter 2: ";
    private static final String LABEL_INPUT = "Input: ";
    private static final String LABEL_OUTPUT = "Output: ";
    private static final String LABEL_UNITS = "Units: ";
    private static final String LABEL_FINAL_VAL = "Final Val: ";
    private static final String LABEL_GROWTH = "Growth (%):";
    private static final String LABEL_DELTA = "Delta:";

    // === Constants for Metadata ===
    private static final String SCENARIO_COMPONENT_TYPE = "Tech Param";
    private static final String METADATA_HEADER = "########## Scenario Component Metadata ##########";
    private static final String METADATA_FOOTER = "#################################################";
    private static final String METADATA_SCENARIO_TYPE = "#Scenario component type: ";
    private static final String METADATA_SECTOR = "#Sector: ";
    private static final String METADATA_TECHNOLOGIES = "#Technologies: ";
    private static final String METADATA_PARAMETER = "#Parameter: ";
    private static final String METADATA_REGIONS = "#Regions: ";
    private static final String METADATA_TABLE_DATA = "#Table data:";

    // === Layout and UI Components ===
    private final GridPane gridPanePresetModification = new GridPane();
    private final ScrollPane scrollPaneLeft = new ScrollPane();
    private final GridPane gridPaneLeft = new GridPane();
    private final VBox vBoxCenter = new VBox();

    // === UI Controls ===
    private final Label labelSector = createLabel(LABEL_SECTOR, LABEL_WIDTH);
    private final ComboBox<String> comboBoxSector = createComboBoxString();
    private final Label labelFilter = createLabel(LABEL_FILTER, LABEL_WIDTH);
    private final TextField textFieldFilter = createTextField();
    private final Label labelCheckComboBoxTech = createLabel(LABEL_TECHS, LABEL_WIDTH);
    private final CheckComboBox<String> checkComboBoxTech = utils.createCheckComboBox();
    private final Label labelComboBoxParam = createLabel(LABEL_PARAMETER, LABEL_WIDTH);
    private final ComboBox<String> comboBoxParam = createComboBoxString();
    private final Label labelComboBoxParam2 = createLabel(LABEL_PARAMETER2, LABEL_WIDTH);
    private final ComboBox<String> comboBoxParam2 = createComboBoxString();
    private final Label labelTextFieldInput = createLabel(LABEL_INPUT, LABEL_WIDTH);
    private final Label labelTextFieldInput2 = createLabel("");
    private final Label labelTextFieldOutput = createLabel(LABEL_OUTPUT, LABEL_WIDTH);
    private final Label labelTextFieldOutput2 = createLabel("", LABEL_WIDTH);
    private final Label labelTextFieldUnits = createLabel(LABEL_UNITS, LABEL_WIDTH);
    private final Label labelTextFieldUnits2 = createLabel("", LABEL_WIDTH);
    private final Label labelValue = createLabel(LABEL_VALUES);

    // === Data ===
    private String[][] techInfo = null;

    /**
     * Constructs a new TabTechParam instance and initializes the UI components for the Technology Parameter tab.
     * Sets up event handlers and populates controls with available data.
     *
     * @param title The title of the tab
     * @param stageX The JavaFX stage
     */
    public TabTechParam(String title, Stage stageX) {
        // sets tab title
        this.setText(title);
        this.setStyle(styles.getFontStyle());

        setupUIControls();
        setupUILayout();
        setupEventHandlers();
        techInfo = vars.getTechInfo();
        setupComboBoxSector();
        comboBoxSector.getItems().add(SELECT_ONE);
        comboBoxSector.getSelectionModel().select(0);
        checkComboBoxTech.setDisable(true);
    }

    /**
     * Sets up UI controls with options and default values.
     */
    private void setupUIControls() {
        checkComboBoxTech.getItems().clear();
        checkComboBoxTech.getItems().add(SELECT_ONE_OR_MORE);
        checkComboBoxTech.getCheckModel().check(0);

        comboBoxConvertFrom.getItems().addAll(CONVERT_FROM_OPTIONS);
        comboBoxConvertFrom.getSelectionModel().selectFirst();
        comboBoxConvertFrom.setVisible(false);
        labelConvertFrom.setVisible(false);

        labelComboBoxParam2.setVisible(false);
        comboBoxParam2.setVisible(false);

        comboBoxParam.getItems().addAll(PARAM_OPTIONS);
        comboBoxParam.getSelectionModel().selectFirst();
        comboBoxParam.setDisable(false);

        comboBoxParam2.getItems().addAll(SELECT_ONE);
        comboBoxParam2.getSelectionModel().selectFirst();
        comboBoxParam2.setDisable(true);

        comboBoxModificationType.getItems().addAll(MODIFICATION_TYPE_OPTIONS);
        comboBoxModificationType.getSelectionModel().selectFirst();

        setComponentWidths();
    }

    /**
     * Sets preferred, min, and max widths for UI components.
     */
    private void setComponentWidths() {
        // Set widths for all relevant UI components
        Label[] labels = { labelTextFieldInput, labelTextFieldInput2, labelTextFieldOutput, labelTextFieldOutput2 };
        for (Label label : labels) {
            if (label != null) {
                label.setMaxWidth(MAX_WIDTH);
                label.setMinWidth(MIN_WIDTH);
                label.setPrefWidth(PREF_WIDTH);
            }
        }
        ComboBox<?>[] comboBoxes = { comboBoxModificationType, comboBoxParam, comboBoxParam2 };
        for (ComboBox<?> comboBox : comboBoxes) {
            if (comboBox != null) {
                comboBox.setMaxWidth(MAX_WIDTH);
                comboBox.setMinWidth(MIN_WIDTH);
                comboBox.setPrefWidth(PREF_WIDTH);
            }
        }
        checkComboBoxTech.setMaxWidth(MAX_WIDTH);
        checkComboBoxTech.setMinWidth(MIN_WIDTH);
        checkComboBoxTech.setPrefWidth(PREF_WIDTH);
        textFieldStartYear.setMaxWidth(MAX_WIDTH);
        textFieldEndYear.setMaxWidth(MAX_WIDTH);
        textFieldInitialAmount.setMaxWidth(MAX_WIDTH);
        textFieldGrowth.setMaxWidth(MAX_WIDTH);
        textFieldPeriodLength.setMaxWidth(MAX_WIDTH);
        textFieldFilter.setMaxWidth(MAX_WIDTH);
        textFieldStartYear.setMinWidth(MIN_WIDTH);
        textFieldEndYear.setMinWidth(MIN_WIDTH);
        textFieldInitialAmount.setMinWidth(MIN_WIDTH);
        textFieldGrowth.setMinWidth(MIN_WIDTH);
        textFieldPeriodLength.setMinWidth(MIN_WIDTH);
        textFieldFilter.setMinWidth(MIN_WIDTH);
        textFieldStartYear.setPrefWidth(PREF_WIDTH);
        textFieldEndYear.setPrefWidth(PREF_WIDTH);
        textFieldInitialAmount.setPrefWidth(PREF_WIDTH);
        textFieldGrowth.setPrefWidth(PREF_WIDTH);
        textFieldPeriodLength.setPrefWidth(PREF_WIDTH);
        textFieldFilter.setPrefWidth(PREF_WIDTH);
    }

    /**
     * Sets up the layout of the tab.
     */
    private void setupUILayout() {
        gridPaneLeft.add(utils.createLabel(LABEL_SPECIFICATION), 0, 0, 2, 1);
        gridPaneLeft.addColumn(0, labelFilter, labelSector, labelCheckComboBoxTech, labelComboBoxParam, labelComboBoxParam2,
                labelTextFieldInput, labelTextFieldOutput, labelTextFieldUnits, new Separator(),
                utils.createLabel(LABEL_POPULATE), labelModificationType, labelStartYear, labelEndYear, labelInitialAmount,
                labelGrowth, labelConvertFrom);
        gridPaneLeft.addColumn(1, textFieldFilter, comboBoxSector, checkComboBoxTech, comboBoxParam, comboBoxParam2,
                labelTextFieldInput2, labelTextFieldOutput2, labelTextFieldUnits2, new Separator(), new Label(),
                comboBoxModificationType, textFieldStartYear, textFieldEndYear, textFieldInitialAmount,
                textFieldGrowth, comboBoxConvertFrom);
        gridPaneLeft.setVgap(3.);
        gridPaneLeft.setStyle(styles.getStyle2());
        scrollPaneLeft.setContent(gridPaneLeft);
        hBoxHeaderCenter.getChildren().addAll(buttonPopulate, buttonDelete, buttonClear);
        hBoxHeaderCenter.setSpacing(2.);
        hBoxHeaderCenter.setStyle(styles.getStyle3());
        vBoxCenter.getChildren().addAll(labelValue, hBoxHeaderCenter, paneForComponentDetails);
        vBoxCenter.setStyle(styles.getStyle2());
        vBoxRight.getChildren().addAll(paneForCountryStateTree);
        vBoxRight.setStyle(styles.getStyle2());
        gridPanePresetModification.addColumn(0, scrollPaneLeft);
        gridPanePresetModification.addColumn(1, vBoxCenter);
        gridPanePresetModification.addColumn(2, vBoxRight);
        gridPaneLeft.setPrefWidth(325);
        gridPaneLeft.setMinWidth(325);
        vBoxCenter.setPrefWidth(300);
        vBoxRight.setPrefWidth(300);
        VBox tabLayout = new VBox();
        tabLayout.getChildren().addAll(gridPanePresetModification);
        this.setContent(tabLayout);
        

    }

    /**
     * Registers an event handler for a Button's ActionEvent.
     */
    private void registerButtonEvent(Button button, javafx.event.EventHandler<ActionEvent> handler) {
        button.setOnAction(handler);
    }
    /**
     * Registers an event handler for a ComboBox's ActionEvent.
     */
    private void registerComboBoxEvent(ComboBox<String> comboBox, javafx.event.EventHandler<ActionEvent> handler) {
        comboBox.setOnAction(handler);
    }
    /**
     * Registers an event handler for a TextField's ActionEvent.
     */
    private void registerTextFieldEvent(TextField textField, javafx.event.EventHandler<ActionEvent> handler) {
        textField.setOnAction(handler);
    }

    /**
     * Sets up event handlers for UI controls.
     */
    private void setupEventHandlers() {


    	registerTextFieldEvent(textFieldFilter, e -> Platform.runLater(() -> setupComboBoxSector()));
        labelCheckComboBoxTech.setOnMouseClicked(e -> Platform.runLater(() -> {
            if (!checkComboBoxTech.isDisabled()) {
                boolean isFirstItemChecked = checkComboBoxTech.getCheckModel().isChecked(0);
                if (e.getClickCount() == 2) {
                    if (isFirstItemChecked) {
                        checkComboBoxTech.getCheckModel().clearChecks();
                    } else {
                        checkComboBoxTech.getCheckModel().checkAll();
                    }
                }
            }
        }));
        registerComboBoxEvent(comboBoxSector, e -> Platform.runLater(() -> {
            String selectedItem = comboBoxSector.getSelectionModel().getSelectedItem();
            if (selectedItem == null) return;
            if (selectedItem.equals(SELECT_ONE)) {
                checkComboBoxTech.getCheckModel().clearChecks();
                checkComboBoxTech.getItems().clear();
                checkComboBoxTech.getItems().add(SELECT_ONE_OR_MORE);
                checkComboBoxTech.getCheckModel().check(0);
                checkComboBoxTech.setDisable(true);
                labelTextFieldUnits2.setText("");
            } else {
                updateCheckComboTechs();
                checkComboBoxTech.setDisable(false);
            }
            setUnitsLabel();
        }));
        registerComboBoxEvent(comboBoxParam, e -> Platform.runLater(() -> {
            comboBoxParam2.getSelectionModel().selectFirst();
            comboBoxParam2.setDisable(true);
            comboBoxParam2.setVisible(false);
            try {
                String selectedItem = comboBoxParam.getSelectionModel().getSelectedItem();
                if (selectedItem != null && selectedItem.contains("Emis")) {
                    comboBoxParam2.getItems().clear();
                    for (String option : EMISSION_OPTIONS) {
                        comboBoxParam2.getItems().add(option);
                    }
                    comboBoxParam2.getSelectionModel().select(0);
                    comboBoxParam2.setDisable(false);
                    comboBoxParam2.setVisible(true);
                } else {
                    comboBoxParam2.getSelectionModel().select(0);
                    comboBoxParam2.setDisable(true);
                }
                if (selectedItem != null && selectedItem.equals("Levelized Non-Energy Cost")) {
                    labelConvertFrom.setVisible(true);
                    comboBoxConvertFrom.setVisible(true);
                }
                setUnitsLabel();
            } catch (Exception ex) {
                // ignore
            }
        }));
        registerComboBoxEvent(comboBoxModificationType, e -> Platform.runLater(() -> {
            String selectedType = comboBoxModificationType.getSelectionModel().getSelectedItem();
            if (selectedType == null) return;
            switch (selectedType) {
                case "Initial w/% Growth/yr":
                case "Initial w/% Growth/pd":
                    labelGrowth.setText(LABEL_GROWTH);
                    break;
                case "Initial w/Delta/yr":
                case "Initial w/Delta/pd":
                    labelGrowth.setText(LABEL_DELTA);
                    break;
                case "Initial and Final":
                    labelGrowth.setText(LABEL_FINAL_VAL);
                    break;
                default:
                    labelGrowth.setText(LABEL_GROWTH);
            }
        }));
        registerButtonEvent(buttonClear, e -> Platform.runLater(() -> paneForComponentDetails.clearTable()));
        registerButtonEvent(buttonDelete, e -> Platform.runLater(() -> paneForComponentDetails.deleteItemsFromTable()));
        registerButtonEvent(buttonPopulate, e -> Platform.runLater(() -> {
            if (qaPopulate()) {
                double[][] values = calculateValues();
                paneForComponentDetails.setValues(values);
            }
        }));
        checkComboBoxTech.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> Platform.runLater(() -> {
            updateInputOutputUnits();
            setUnitsLabel();
        }));
    }

    /**
     * Updates the input and output unit labels based on the selected technologies.
     * Sets labelTextFieldInput2 and labelTextFieldOutput2 based on checked technologies.
     */
    private void updateInputOutputUnits() {
        ObservableList<String> checkedItems = checkComboBoxTech.getCheckModel().getCheckedItems();
        String input = "";
        String output = "";
        if (checkedItems == null || checkedItems.isEmpty()) {
            labelTextFieldInput2.setText("");
            labelTextFieldOutput2.setText("");
            return;
        }
        for (String line : checkedItems) {
            String[] words = utils.splitString(line.trim(), ":");
            if (words.length >= 3) {
                String sector = words[0].trim();
                String subsector = words[1].trim();
                String tech = words[2].trim();
                for (String[] techRow : techInfo) {
                    if (sector.equals(techRow[0]) && subsector.equals(techRow[1]) && tech.equals(techRow[2])) {
                        String thisInput = techRow[3] + "(" + techRow[4] + ")";
                        String thisOutput = techRow[5] + "(" + techRow[6] + ")";
                        if (input.isEmpty()) {
                            input = thisInput;
                        } else if (!input.equals("various") && !input.equals(thisInput)) {
                            input = "various";
                        }
                        if (output.isEmpty()) {
                            output = thisOutput;
                        } else if (!output.equals("various") && !output.equals(thisOutput)) {
                            output = "various";
                        }
                    }
                }
            }
        }
        labelTextFieldInput2.setText(input);
        labelTextFieldOutput2.setText(output);
    }

    /**
     * Populates the sector combo box based on available technology info and filter.
     * Adds sectors to comboBoxSector, optionally filtered by textFieldFilter.
     */
    private void setupComboBoxSector() {
        comboBoxSector.getItems().clear();
        try {
            String[][] techInfoArr = vars.getTechInfo();
            List<String> sectorList = new ArrayList<>();
            String filterText = textFieldFilter.getText() != null ? textFieldFilter.getText().trim() : "";
            boolean useFilter = !filterText.isEmpty();
            if (!useFilter) sectorList.add(SELECT_ONE);
            sectorList.add(ALL);
            for (String[] tech : techInfoArr) {
                String text = tech[0].trim();
                boolean match = false;
                for (String sector : sectorList) {
                    if (text.equals(sector)) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    boolean show = true;
                    if (useFilter) {
                        show = false;
                        for (String temp : tech) {
                            if (temp.contains(filterText)) {
                                show = true;
                                break;
                            }
                        }
                    }
                    if (show) sectorList.add(text);
                }
            }
            for (String sector : sectorList) {
                comboBoxSector.getItems().add(sector.trim());
            }
        } catch (Exception e) {
            utils.warningMessage("Problem reading tech list.");
            System.out.println("Error reading tech list from " + vars.getTchBndListFilename() + ":");
            System.out.println("  ---> " + e);
        }
    }

    /**
     * Updates the technology check combo box based on the selected sector and filter.
     * Populates the checkComboBoxTech with technologies matching the selected sector and filter.
     */
    private void updateCheckComboTechs() {
        String sector = comboBoxSector.getValue();
        String[][] techInfoArr = vars.getTechInfo();
        boolean isAllSectors = ALL.equals(sector);
        try {
            if (checkComboBoxTech.getItems().size() > 0) {
                checkComboBoxTech.getCheckModel().clearChecks();
                checkComboBoxTech.getItems().clear();
            }
            if (sector != null) {
                String lastLine = "";
                String filterText = textFieldFilter.getText() != null ? textFieldFilter.getText().trim() : "";
                for (String[] tech : techInfoArr) {
                    String line = tech[0].trim() + " : " + tech[1] + " : " + tech[2];
                    if (filterText.isEmpty() || line.contains(filterText)) {
                        if (tech.length >= 7) line += " : " + tech[6];
                        if (!line.equals(lastLine)) {
                            lastLine = line;
                            if (isAllSectors || line.startsWith(sector)) {
                                checkComboBoxTech.getItems().add(line);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            utils.warningMessage("Problem reading tech list.");
            System.out.println("Error reading tech list from " + vars.getTchBndListFilename() + ":");
            System.out.println("  ---> " + e);
        }
    }

    /**
     * Generates the metadata content string for the scenario component, including selected sector, technologies, parameter, regions, and table data.
     *
     * @param tree The TreeView of regions
     * @return Metadata content string
     */
    public String getMetaDataContent(TreeView<String> tree) {
        StringBuilder rtnStr = new StringBuilder();
        rtnStr.append(METADATA_HEADER).append(vars.getEol());
        rtnStr.append(METADATA_SCENARIO_TYPE).append(SCENARIO_COMPONENT_TYPE).append(vars.getEol());
        rtnStr.append(METADATA_SECTOR).append(comboBoxSector.getValue()).append(vars.getEol());
        ObservableList<String> techList = checkComboBoxTech.getCheckModel().getCheckedItems();
        String techs = utils.getStringFromList(techList, ";");
        rtnStr.append(METADATA_TECHNOLOGIES).append(techs).append(vars.getEol());
        rtnStr.append(METADATA_PARAMETER).append(comboBoxParam.getValue()).append(vars.getEol());
        String[] listOfSelectedLeaves = utils.getAllSelectedRegions(tree);
        listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
        String states = utils.returnAppendedString(listOfSelectedLeaves);
        rtnStr.append(METADATA_REGIONS).append(states).append(vars.getEol());
        ArrayList<String> tableContent = this.paneForComponentDetails.getDataYrValsArrayList();
        for (String tableLine : tableContent) {
            rtnStr.append(METADATA_TABLE_DATA).append(tableLine).append(vars.getEol());
        }
        rtnStr.append(METADATA_FOOTER).append(vars.getEol());
        return rtnStr.toString();
    }

    /**
     * Loads content from a list of strings (typically from a file) and populates the UI fields accordingly.
     * Parses each line for sector, technologies, parameter, regions, and table data.
     *
     * @param content The list of content lines to load
     */
    @Override
    public void loadContent(ArrayList<String> content) {
        for (String line : content) {
            int pos = line.indexOf(":");
            if (line.startsWith("#") && (pos > -1)) {
                String param = line.substring(1, pos).trim().toLowerCase();
                String value = line.substring(pos + 1).trim();
                if (param.equals("sector")) {
                    comboBoxSector.setValue(value);
                    comboBoxSector.fireEvent(new ActionEvent());
                }
                if (param.equals("technologies")) {
                    checkComboBoxTech.getCheckModel().clearChecks();
                    String[] set = utils.splitString(value, ";");
                    for (String item : set) {
                        checkComboBoxTech.getCheckModel().check(item.trim());
                        checkComboBoxTech.fireEvent(new ActionEvent());
                    }
                }
                if (param.equals("parameter")) {
                    comboBoxParam.setValue(value);
                    comboBoxParam.fireEvent(new ActionEvent());
                }
                if (param.equals("regions")) {
                    String[] regions = utils.splitString(value, ",");
                    this.paneForCountryStateTree.selectNodes(regions);
                }
                if (param.equals("table data")) {
                    String[] s = utils.splitString(value, ",");
                    this.paneForComponentDetails.data.add(new DataPoint(s[0], s[1]));
                }
            }
        }
        updateInputOutputUnits();
        this.paneForComponentDetails.updateTable();
    }

    /**
     * Performs a quick QA check to ensure required fields for populating values are filled.
     * Checks that start year, end year, initial amount, and growth fields are not empty.
     *
     * @return true if all required fields are filled, false otherwise
     */
    public boolean qaPopulate() {
        return !(textFieldStartYear.getText().isEmpty() || textFieldEndYear.getText().isEmpty()
                || textFieldInitialAmount.getText().isEmpty() || textFieldGrowth.getText().isEmpty());
    }

    /**
     * Adds input and output information to the text fields for a given technology row and prefix.
     *
     * @param techInfo The technology info array
     * @param row The row index
     * @param prefix The prefix array to match
     */
    public void addIOToTextFields(String[][] techInfo, int row, String[] prefix) {
        for (String[] tech : techInfo) {
            if (doesPrefixMatch(tech, prefix)) {
                String text = tech[3];
                if (tech.length > 3)
                    text += " (" + tech[4] + ")";
                labelTextFieldInput2.setText(text);
                if (tech.length > 4) {
                    text = tech[5];
                    if (tech.length > 5)
                        text += " (" + tech[6] + ")";
                    labelTextFieldOutput2.setText(text);
                } else {
                    labelTextFieldOutput2.setText("");
                }
            }
        }
    }

    /**
     * Adds non-duplicate items to a combo box from the technology info array, matching a prefix.
     *
     * @param comboBox The combo box to add items to
     * @param techInfo The technology info array
     * @param row The row index to use for item
     * @param prefix The prefix array to match
     */
    public void addNonDuplicatesToComboBox(ComboBox<String> comboBox, String[][] techInfo, int row, String[] prefix) {
        for (String[] tech : techInfo) {
            if (doesPrefixMatch(tech, prefix)) {
                if (!comboBox.getItems().contains(tech[row])) {
                    comboBox.getItems().add(tech[row]);
                }
            }
        }
    }

    /**
     * Adds non-duplicate items to a check combo box from the technology info array, matching a prefix.
     *
     * @param checkComboBox The check combo box to add items to
     * @param techInfo The technology info array
     * @param row The row index to use for item
     * @param prefix The prefix array to match
     */
    public void addNonDuplicatesToCheckComboBox(CheckComboBox<String> checkComboBox, String[][] techInfo, int row, String[] prefix) {
        for (String[] tech : techInfo) {
            if (doesPrefixMatch(tech, prefix)) {
                if (!checkComboBox.getItems().contains(tech[row])) {
                    checkComboBox.getItems().add(tech[row]);
                }
            }
        }
    }

    /**
     * Checks if the given item array matches the prefix array.
     *
     * @param item The item array to check
     * @param prefix The prefix array to match against
     * @return true if the prefix matches, false otherwise
     */
    public boolean doesPrefixMatch(String[] item, String[] prefix) {
        if (prefix == null) return true;
        for (int i = 0; i < prefix.length; i++) {
            if (!item[i].equals(prefix[i])) return false;
        }
        return true;
    }

    /**
     * Returns the units string for the selected technologies.
     * If multiple technologies are selected and their units do not match, returns "No match".
     * If no technologies are selected, returns an empty string.
     *
     * @return The units string, or "No match" if units are inconsistent
     */
    public String getUnits() {
        ObservableList<String> techList = checkComboBoxTech.getCheckModel().getCheckedItems();
        String unit = "";
        for (String line : techList) {
            try {
                String item = line.substring(line.lastIndexOf(":") + 1).trim();
                if (unit.isEmpty()) {
                    unit = item;
                } else if (!unit.equals(item)) {
                    unit = "No match";
                }
            } catch (Exception e) {
                // ignore
            }
        }
        if (unit.equals(SELECT_ONE_OR_MORE)) unit = "";
        return unit;
    }

    /**
     * Sets the units label based on the selected technologies and parameter.
     * Updates the labelTextFieldUnits2 with the appropriate units or warning.
     */
    public void setUnitsLabel() {
        String s = getUnits();
        String label = "";
        String selectedParam = this.comboBoxParam.getSelectionModel().getSelectedItem();
        if (this.checkComboBoxTech.getCheckModel().getCheckedIndices().size() > 0) {
            switch (selectedParam) {
                case "Levelized Non-Energy Cost":
                    if (s.equals("No match")) {
                        label = WARNING_UNITS_MISMATCH;
                    } else if (s.equals("million pass-km")) {
                        label = "1990$ per veh-km";
                    } else if (s.equals("million ton-km")) {
                    	label = "1990$ per veh-km";
                    } else if (s.equals("")) {
                        label = "";
                    } else {
                        String s2 = "GJ";
                        if (s.equals("EJ")) s2 = "GJ";
                        if (s.equals("petalumen-hours")) s2 = "megalumen-hours";
                        if (s.equals("million km3")) s2 = "million m3";
                        if (s.equals("billion cycles")) s2 = "cycle";
                        if (s.equals("Mt")) s2 = "kg";
                        if (s.equals("km^3")) s2 = "m^3";
                        label = "1975$s per " + s2;
                    }
                    break;
                case "Capacity Factor":
                    label = UNIT_UNITLESS_CAPACITY;
                    break;
                case "Fixed Output":
                    String s2 = this.labelTextFieldOutput2.getText();
                    label = utils.getParentheticString(s2);
                    break;
                case "Lifetime":
                case "Halflife":
                    label = UNIT_YEARS;
                    break;
                default:
                    label = UNIT_UNITLESS;
            }
        }
        labelTextFieldUnits2.setText(label);
    }

    /**
     * Runs background tasks or updates for this tab. Implementation of Runnable interface.
     */
    @Override
    public void run() {
        saveScenarioComponent();
    }

    /**
     * Saves the scenario component using the current UI state and selected regions.
     */
    @Override
    public void saveScenarioComponent() {
        saveScenarioComponent(paneForCountryStateTree.getTree());
    }

    /**
     * Saves the scenario component for the specified tree of regions.
     *
     * @param tree The TreeView of regions
     */
    private void saveScenarioComponent(TreeView<String> tree) {
        if (!qaInputs()) {
            Thread.currentThread().destroy();
            return;
        } else {
            CsvFileWriter cfw = CsvFileWriter.getInstance();
            ArrayList<String> dataList = loadDataFromGUI(tree);
            ArrayList<String> colList = files.getStringArrayFromFile(vars.getCsvColumnFilename(), "#");
            ArrayList<String> csvContent = cfw.createCsvContent(colList, dataList);
            fileContent = getMetaDataContent(tree);
            fileContent += utils.createStringFromArrayList(csvContent);
            filenameSuggestion = "" + utils.getMatch(dataList, "type", ";") + "techParam.csv";
            filenameSuggestion = filenameSuggestion.replaceAll("/", "-").replaceAll(" ", "_");
        }
    }

    /**
     * Loads data from the GUI for saving to file.
     *
     * @param tree The TreeView of regions
     * @return ArrayList of data strings
     */
    private ArrayList<String> loadDataFromGUI(TreeView<String> tree) {
        ArrayList<String> dataList = new ArrayList<>();
        ObservableList<String> checkedItems = checkComboBoxTech.getCheckModel().getCheckedItems();
        for (String line : checkedItems) {
            String[] words = utils.splitString(line, ":");
            String sector = "sector:" + words[0].trim();
            String subsector = "subsector:" + words[1].trim();
            String tech = "technology:" + words[2].trim();
            String region = "region:" + getSelectedLeaves(tree);
            String input = "input:" + labelTextFieldInput2.getText().trim();
            String output = "output:" + labelTextFieldOutput2.getText().trim();
            String param = "param:" + this.comboBoxParam.getValue();
            String param2 = "param2:" + this.comboBoxParam2.getValue();
            ObservableList<DataPoint> data = this.paneForComponentDetails.table.getItems();
            StringBuilder year = new StringBuilder("year:");
            StringBuilder value = new StringBuilder("value:");
            for (int i = 0; i < data.size(); i++) {
                if (i != 0) {
                    year.append(",");
                    value.append(",");
                }
                year.append(data.get(i).getYear());
                value.append(data.get(i).getValue());
            }
            String group = "all";
            if (sector.indexOf("trn") >= 0) group = "trn";
            if (sector.indexOf("generation") >= 0) group = "egu";
            String type = "type:" + group + File.separator + this.comboBoxParam.getValue();
            String dataStr = type + ";" + sector + ";" + subsector + ";" + tech + ";" + region + ";" + input + ";"
                    + output + ";" + param + ";" + param2 + ";" + year + ";" + value;
            dataList.add(dataStr);
        }
        return dataList;
    }

    /**
     * Gets a comma-separated string of selected leaves (regions) from the tree.
     *
     * @param tree The TreeView of regions
     * @return Comma-separated string of selected regions
     */
    private String getSelectedLeaves(TreeView<String> tree) {
        String[] listOfSelectedLeaves = utils.getAllSelectedRegions(tree);
        String states = utils.returnAppendedString(listOfSelectedLeaves);
        if ((states.contains("USA")) && (vars.isGcamUSA())) {
            states = states.replace(",USA", "");
        }
        return states;
    }

    /**
     * Helper method to validate table data years against allowable policy years.
     * @return true if at least one year matches allowable years, false otherwise
     */
    private boolean validateTableDataYears() {
        List<Integer> listOfAllowableYears = vars.getAllowablePolicyYears();
        ObservableList<DataPoint> data = paneForComponentDetails != null ? this.paneForComponentDetails.table.getItems() : null;
        if (data == null) return false;
        for (DataPoint dp : data) {
            Integer year = Integer.parseInt(dp.getYear().trim());
            if (listOfAllowableYears.contains(year)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Performs QA checks on the current UI state to ensure all required inputs are valid.
     * Displays warnings or error messages as needed.
     *
     * @return true if all inputs are valid, false otherwise
     */
    protected boolean qaInputs() {
        TreeView<String> tree = paneForCountryStateTree.getTree();
        int errorCount = 0;
        StringBuilder message = new StringBuilder();
        try {
            if (utils.getAllSelectedRegions(tree).length < 1) {
                message.append("Must select at least one region from tree").append(vars.getEol());
                errorCount++;
            }
            if (paneForComponentDetails == null || paneForComponentDetails.table.getItems().size() == 0) {
                message.append("Data table must have at least one entry").append(vars.getEol());
                errorCount++;
            } else {
                boolean match = validateTableDataYears();
                if (!match) {
                    message.append("Years specified in table must match allowable policy years (").append(vars.getAllowablePolicyYears()).append(")").append(vars.getEol());
                    errorCount++;
                }
            }
            if (comboBoxSector.getSelectionModel().getSelectedItem().equals(SELECT_ONE)) {
                message.append("Sector comboBox must have a selection").append(vars.getEol());
                errorCount++;
            }
            if (comboBoxParam.getSelectionModel().getSelectedItem().equals(SELECT_ONE)) {
                message.append("Parameter comboBox must have a selection").append(vars.getEol());
                errorCount++;
            }
            if (comboBoxParam2.isVisible()) {
                if (comboBoxParam2.getSelectionModel().getSelectedItem().equals(SELECT_ONE)) {
                    message.append("Parameter2 comboBox must have a selection").append(vars.getEol());
                    errorCount++;
                }
            }
        } catch (Exception e1) {
            errorCount++;
            message.append("Error in QA of entries").append(vars.getEol());
        }
        if (errorCount > 0) {
            if (errorCount == 1) {
                utils.warningMessage(message.toString());
            } else if (errorCount > 1) {
                utils.displayString(message.toString(), "Parsing Errors");
            }
        }
        return errorCount == 0;
    }
}
