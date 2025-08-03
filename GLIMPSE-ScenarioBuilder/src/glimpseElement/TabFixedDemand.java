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
package glimpseElement;

import java.util.ArrayList;
import java.util.List;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * TabFixedDemand provides the user interface and logic for creating or editing
 * Fixed Demand scenario components in the GLIMPSE Scenario Builder.
 * <p>
 * This tab allows users to specify demand for a sector in selected regions over a time period,
 * using various modification types (e.g., initial/final, growth rates, deltas).
 * Users can populate, edit, and clear demand values, and save the scenario component as a CSV file.
 * </p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Sector selection with automatic unit display</li>
 *   <li>Region selection via a tree view</li>
 *   <li>Demand value specification by year, initial/final, growth, or delta</li>
 *   <li>Populate, clear, and delete value table</li>
 *   <li>Validation and CSV export of scenario component</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>
 * TabFixedDemand tab = new TabFixedDemand("Fixed Demand", stage);
 * // Add to TabPane, interact via UI
 * </pre>
 *
 * <h2>Thread Safety</h2>
 * <p>This class is not thread-safe and should be used only on the JavaFX Application Thread.</p>
 */
public class TabFixedDemand extends PolicyTab implements Runnable {
    // === Constants for UI Strings and Options ===
    private static final String LABEL_SECTOR = "Sector: ";
    private static final String LABEL_UNITS = "Units: ";
    private static final String LABEL_TYPE = "Type: ";
    private static final String LABEL_START_YEAR = "Start Year: ";
    private static final String LABEL_END_YEAR = "End Year: ";
    private static final String LABEL_INITIAL = "Initial: ";
    private static final String LABEL_FINAL = "Final: ";
    private static final String LABEL_PERIOD_LENGTH = "Period Length: ";
    private static final String LABEL_VALUES = "Values: ";
    private static final String LABEL_SPECIFICATION = "Specification:";
    private static final String LABEL_POPULATE = "Populate:";
    private static final String BUTTON_POPULATE = "Populate";
    private static final String BUTTON_DELETE = "Delete";
    private static final String BUTTON_CLEAR = "Clear";
    private static final String MOD_TYPE_INITIAL_FINAL = "Initial and Final";
    private static final String MOD_TYPE_GROWTH_YR = "Initial w/% Growth/yr";
    private static final String MOD_TYPE_GROWTH_PD = "Initial w/% Growth/pd";
    private static final String MOD_TYPE_DELTA_YR = "Initial w/Delta/yr";
    private static final String MOD_TYPE_DELTA_PD = "Initial w/Delta/pd";
    private static final String[] MODIFICATION_TYPES = {
            MOD_TYPE_INITIAL_FINAL, MOD_TYPE_GROWTH_YR, MOD_TYPE_GROWTH_PD, MOD_TYPE_DELTA_YR, MOD_TYPE_DELTA_PD
    };
    private static final String SECTOR_OTHER = "Other";
    private static final String SECTOR_SELECT_ONE = "Select One";
    private static final double MAX_WIDTH = 195;
    private static final double MIN_WIDTH = 105;
    private static final double PREF_WIDTH = 195;

    // === Utility singletons ===
    private final GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
    private final GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
    private final GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

    // === UI layout containers ===
    private final GridPane gridPanePresetModification = new GridPane();
    private final GridPane gridPaneLeft = new GridPane();
    private final VBox vBoxCenter = new VBox();
    private final HBox hBoxHeaderCenter = new HBox();
    private final VBox vBoxRight = new VBox();

    // === UI controls ===
    private final Label labelSector = utils.createLabel(LABEL_SECTOR, 125);
    private final ComboBox<String> comboBoxSector = utils.createComboBoxString();
    private final Label labelUnits = utils.createLabel(LABEL_UNITS, 125);
    private final Label labelUnitsValue = utils.createLabel("", 125);
    private final Label labelModificationType = utils.createLabel(LABEL_TYPE, 125);
    private final ComboBox<String> comboBoxModificationType = utils.createComboBoxString();
    private final Label labelStartYear = utils.createLabel(LABEL_START_YEAR, 125);
    private final TextField textFieldStartYear = new TextField("2020");
    private final Label labelEndYear = utils.createLabel(LABEL_END_YEAR, 125);
    private final TextField textFieldEndYear = new TextField("2050");
    private final Label labelInitialAmount = utils.createLabel(LABEL_INITIAL, 125);
    private final TextField textFieldInitialAmount = utils.createTextField();
    private final Label labelGrowth = utils.createLabel(LABEL_FINAL, 125);
    private final TextField textFieldGrowth = utils.createTextField();
    private final Label labelPeriodLength = utils.createLabel(LABEL_PERIOD_LENGTH, 125);
    private final TextField textFieldPeriodLength = new TextField("5");
    private final Label labelValue = utils.createLabel(LABEL_VALUES, 125);
    private final Button buttonPopulate = utils.createButton(BUTTON_POPULATE, styles.getBigButtonWidth(), null);
    private final Button buttonDelete = utils.createButton(BUTTON_DELETE, styles.getBigButtonWidth(), null);
    private final Button buttonClear = utils.createButton(BUTTON_CLEAR, styles.getBigButtonWidth(), null);
    private final PaneForComponentDetails paneForComponentDetails = new PaneForComponentDetails();
    private final PaneForCountryStateTree paneForCountryStateTree = new PaneForCountryStateTree();

    // === Data ===
    private final String[][] sectorInfo;

    /**
     * Description text for the tab (not currently used in UI).
     */
    public static String descriptionText = "";
    /**
     * Run queue string for status display (not currently used in UI).
     */
    public static String runQueueStr = "Queue is empty.";

    /**
     * Constructs a TabFixedDemand for the given title and stage.
     * @param title Tab title
     * @param stageX JavaFX Stage (not used directly)
     */
    public TabFixedDemand(String title, Stage stageX) {
        this.setText(title);
        this.setStyle(styles.getFontStyle());
        sectorInfo = vars.getSectorInfo();
        setupLayout();
        setupActions();
        VBox tabLayout = new VBox(gridPanePresetModification);
        this.setContent(tabLayout);
    }

    /**
     * Sets up the layout of the tab UI, arranging controls in left, center, and right columns.
     */
    private void setupLayout() {
        // Left column
        gridPaneLeft.add(utils.createLabel(LABEL_SPECIFICATION), 0, 0, 2, 1);
        gridPaneLeft.addColumn(0, labelSector, new Label(), labelUnits, new Separator(),
                utils.createLabel(LABEL_POPULATE), labelModificationType, labelStartYear, labelEndYear, labelInitialAmount, labelGrowth);
        gridPaneLeft.addColumn(1, comboBoxSector, new Label(), labelUnitsValue, new Separator(), new Label(),
                comboBoxModificationType, textFieldStartYear, textFieldEndYear, textFieldInitialAmount, textFieldGrowth);
        gridPaneLeft.setAlignment(Pos.TOP_LEFT);
        gridPaneLeft.setVgap(3.);
        gridPaneLeft.setStyle(styles.getStyle2());
        gridPaneLeft.setPrefWidth(325);
        gridPaneLeft.setMinWidth(325);

        // Center column
        hBoxHeaderCenter.getChildren().addAll(buttonPopulate, buttonDelete, buttonClear);
        hBoxHeaderCenter.setSpacing(2.);
        hBoxHeaderCenter.setStyle(styles.getStyle3());
        vBoxCenter.getChildren().addAll(labelValue, hBoxHeaderCenter, paneForComponentDetails);
        vBoxCenter.setStyle(styles.getStyle2());
        vBoxCenter.setPrefWidth(300);

        // Right column
        vBoxRight.getChildren().addAll(paneForCountryStateTree);
        vBoxRight.setStyle(styles.getStyle2());
        vBoxRight.setPrefWidth(300);

        // Add columns to main grid
        gridPanePresetModification.addColumn(0, gridPaneLeft);
        gridPanePresetModification.addColumn(1, vBoxCenter);
        gridPanePresetModification.addColumn(2, vBoxRight);
    }

    /**
     * Sets up actions and listeners for UI controls (combo boxes, buttons).
     * Uses Platform.runLater for thread safety if called off JavaFX thread.
     */
    private void setupActions() {
        setupComboBoxSector();
        comboBoxSector.getSelectionModel().selectFirst();
        comboBoxModificationType.getItems().addAll(MODIFICATION_TYPES);
        comboBoxModificationType.getSelectionModel().selectFirst();
        comboBoxSector.setMaxWidth(MAX_WIDTH);
        comboBoxModificationType.setMaxWidth(MAX_WIDTH);
        comboBoxSector.setMinWidth(MIN_WIDTH);
        comboBoxModificationType.setMinWidth(MIN_WIDTH);
        comboBoxSector.setPrefWidth(PREF_WIDTH);
        comboBoxModificationType.setPrefWidth(PREF_WIDTH);
        comboBoxSector.setOnAction(e -> {
            String selectedItem = comboBoxSector.getSelectionModel().getSelectedItem();
            if (selectedItem == null) return;
            if (SECTOR_OTHER.equals(selectedItem)) {
                // set other sector box to visible and enable
            } else {
                updateSectorOutputAndUnits();
            }
        });
        comboBoxSector.fireEvent(new ActionEvent());
        comboBoxModificationType.setOnAction(e -> updateGrowthLabel());
        buttonClear.setOnAction(e -> paneForComponentDetails.clearTable());
        buttonDelete.setOnAction(e -> paneForComponentDetails.deleteItemsFromTable());
        buttonPopulate.setOnAction(e -> {
            if (qaPopulate()) {
                double[][] values = calculateValues();
                paneForComponentDetails.setValues(values);
            }
        });
    }

    /**
     * Updates the label for the growth/final value based on modification type selection.
     * E.g., switches between "Final", "Growth (%)", or "Delta".
     */
    private void updateGrowthLabel() {
        String type = comboBoxModificationType.getSelectionModel().getSelectedItem();
        if (type == null) return;
        switch (type) {
            case MOD_TYPE_GROWTH_YR:
            case MOD_TYPE_GROWTH_PD:
                labelGrowth.setText("Growth (%):");
                break;
            case MOD_TYPE_DELTA_YR:
            case MOD_TYPE_DELTA_PD:
                labelGrowth.setText("Delta:");
                break;
            case MOD_TYPE_INITIAL_FINAL:
            default:
                labelGrowth.setText(LABEL_FINAL);
                break;
        }
    }

    /**
     * Populates the sector combo box with available sectors from sectorInfo.
     * Adds an "Other" option for custom sectors.
     */
    private void setupComboBoxSector() {
        try {
            for (String[] sector : sectorInfo) {
                comboBoxSector.getItems().add(sector[0]);
            }
            comboBoxSector.getItems().add(SECTOR_OTHER);
        } catch (Exception e) {
            utils.warningMessage("Problem reading sector list.");
            System.out.println("  ---> " + e);
        }
    }

    /**
     * Updates the units label based on the selected sector.
     * Looks up units in sectorInfo and displays them next to the sector.
     */
    private void updateSectorOutputAndUnits() {
        String selectedSector = comboBoxSector.getValue();
        labelUnitsValue.setText("");
        if (selectedSector == null) return;
        for (String[] sector : sectorInfo) {
            if (selectedSector.equals(sector[0])) {
                labelUnitsValue.setText(sector[2]);
            }
        }
    }

    /**
     * Calculates the values matrix for the demand table based on user input and modification type.
     * @return 2D array of calculated values for each year/period
     */
    private double[][] calculateValues() {
        String calcType = comboBoxModificationType.getSelectionModel().getSelectedItem();
        int startYear = Integer.parseInt(textFieldStartYear.getText());
        int endYear = Integer.parseInt(textFieldEndYear.getText());
        double initialValue = Double.parseDouble(textFieldInitialAmount.getText());
        double growth = Double.parseDouble(textFieldGrowth.getText());
        int periodLength = Integer.parseInt(textFieldPeriodLength.getText());
        return utils.calculateValues(calcType, startYear, endYear, initialValue, growth, periodLength);
    }

    /**
     * Runnable implementation: triggers saving the scenario component.
     */
    @Override
    public void run() {
        Platform.runLater(this::saveScenarioComponent);
    }

    /**
     * Saves the scenario component by generating metadata and CSV content.
     * Uses selected regions, sector, and demand values.
     */
    @Override
    public void saveScenarioComponent() {
        saveScenarioComponent(paneForCountryStateTree.getTree());
    }

    /**
     * Saves the scenario component using the provided region tree.
     * Validates inputs, generates metadata and CSV, and sets fileContent/filenameSuggestion.
     * @param tree TreeView of selected regions
     */
    private void saveScenarioComponent(TreeView<String> tree) {
        if (qaInputs()) {
            fileContent = getMetaDataContent(tree, "", "");
            fileContent += "INPUT_TABLE" + vars.getEol() + "Variable ID" + vars.getEol();
            fileContent += "GLIMPSEFixedDemand" + vars.getEol() + vars.getEol();
            // Selected regions
            String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);
            listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
            // Sector
            String sectorName = comboBoxSector.getSelectionModel().getSelectedItem();
            if (sectorName != null) sectorName = sectorName.trim();
            filenameSuggestion = sectorName + "fxDMD";
            List<String> dataArrayList = paneForComponentDetails.getDataYrValsArrayList();
            List<String> yearList = new ArrayList<>();
            List<String> valueList = new ArrayList<>();
            for (String str : dataArrayList) {
                String s = str.replace(" ", "").trim();
                String[] split = utils.splitString(s, ",");
                if (split.length > 1) {
                    yearList.add(split[0]);
                    valueList.add(split[1]);
                }
            }
            // CSV content
            fileContent += "region,sector,sector,year,value" + vars.getEol();
            for (String region : listOfSelectedLeaves) {
                for (int i = 0; i < yearList.size(); i++) {
                    fileContent += region + "," + sectorName + "," + sectorName + "," + yearList.get(i) + "," + valueList.get(i) + vars.getEol();
                }
            }
            System.out.println("here it would construct csv file.");
        }
    }

    /**
     * Returns metadata content for the scenario component file, including sector, regions, and table data.
     * @param tree TreeView of selected regions
     * @param market Market name (not used)
     * @param policy Policy name (not used)
     * @return Metadata string for file header
     */
    public String getMetaDataContent(TreeView<String> tree, String market, String policy) {
        StringBuilder rtnStr = new StringBuilder();
        rtnStr.append("########## Scenario Component Metadata ##########").append(vars.getEol());
        rtnStr.append("#Scenario component type: Fixed Demand").append(vars.getEol());
        rtnStr.append("#Sector:").append(comboBoxSector.getValue()).append(vars.getEol());
        String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);
        listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
        String states = utils.returnAppendedString(listOfSelectedLeaves);
        rtnStr.append("#Regions: ").append(states).append(vars.getEol());
        List<String> tableContent = paneForComponentDetails.getDataYrValsArrayList();
        for (String row : tableContent) {
            rtnStr.append("#Table data:").append(row).append(vars.getEol());
        }
        rtnStr.append("#################################################").append(vars.getEol());
        return rtnStr.toString();
    }

    /**
     * Loads content into the tab from a list of strings (e.g., when editing a component).
     * Populates sector, regions, and table data from file content.
     * @param content List of file lines to load
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
                if (param.equals("regions")) {
                    String[] regions = utils.splitString(value, ",");
                    paneForCountryStateTree.selectNodes(regions);
                }
                if (param.equals("table data")) {
                    String[] s = utils.splitString(value, ",");
                    paneForComponentDetails.data.add(new DataPoint(s[0], s[1]));
                }
            }
        }
        paneForComponentDetails.updateTable();
    }

    /**
     * Checks if all required fields for populating values are filled.
     * @return true if all required fields are filled, false otherwise
     */
    public boolean qaPopulate() {
        return !textFieldStartYear.getText().isEmpty() &&
               !textFieldEndYear.getText().isEmpty() &&
               !textFieldInitialAmount.getText().isEmpty() &&
               !textFieldGrowth.getText().isEmpty();
    }

    /**
     * Validates all required inputs before saving the scenario component.
     * Checks for at least one region, at least one table entry, and sector selection.
     * @return true if all inputs are valid, false otherwise
     */
    protected boolean qaInputs() {
        TreeView<String> tree = paneForCountryStateTree.getTree();
        int errorCount = 0;
        StringBuilder message = new StringBuilder();
        try {
            if (utils.getAllSelectedLeaves(tree).length < 1) {
                message.append("Must select at least one region from tree").append(vars.getEol());
                errorCount++;
            }
            if (paneForComponentDetails.table.getItems().size() == 0) {
                message.append("Data table must have at least one entry").append(vars.getEol());
                errorCount++;
            }
            String selected = comboBoxSector.getSelectionModel().getSelectedItem();
            if (selected == null || selected.equals(SECTOR_SELECT_ONE)) {
                message.append("Sector comboBox must have a selection").append(vars.getEol());
                errorCount++;
            }
        } catch (Exception e1) {
            errorCount++;
            message.append("Error in QA of entries").append(vars.getEol());
        }
        if (errorCount > 0) {
            if (errorCount == 1) {
                utils.warningMessage(message.toString());
            } else {
                utils.warningMessage("More than one issue with inputs");
                utils.displayString(message.toString(), "Parsing Errors");
            }
        }
        return errorCount == 0;
    }
}
