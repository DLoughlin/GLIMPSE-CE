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
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
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
    // Utility singletons for style, variables, and helpers
    private final GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
    private final GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
    private final GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

    // === UI layout containers ===
    private final GridPane gridPanePresetModification = new GridPane();
    private final GridPane gridPaneLeft = new GridPane();
    private final VBox vBoxCenter = new VBox();
    private final HBox hBoxHeaderCenter = new HBox();
    private final HBox hBoxHeaderRight = new HBox();
    private final VBox vBoxRight = new VBox();

    // === UI controls ===
    private final Label labelSector = utils.createLabel("Sector: ", 125);
    private final ComboBox<String> comboBoxSector = utils.createComboBoxString();
    private final Label labelTextFieldUnits = utils.createLabel("Units: ", 125);
    private final Label labelTextFieldUnits2 = utils.createLabel("", 125);
    private final Label labelModificationType = utils.createLabel("Type: ", 125);
    private final ComboBox<String> comboBoxModificationType = utils.createComboBoxString();
    private final Label labelStartYear = utils.createLabel("Start Year: ", 125);
    private final TextField textFieldStartYear = new TextField("2020");
    private final Label labelEndYear = utils.createLabel("End Year: ", 125);
    private final TextField textFieldEndYear = new TextField("2050");
    private final Label labelInitialAmount = utils.createLabel("Initial: ", 125);
    private final TextField textFieldInitialAmount = utils.createTextField();
    private final Label labelGrowth = utils.createLabel("Final: ", 125);
    private final TextField textFieldGrowth = utils.createTextField();
    private final Label labelPeriodLength = utils.createLabel("Period Length: ", 125);
    private final TextField textFieldPeriodLength = new TextField("5");
    private final Label labelValue = utils.createLabel("Values: ", 125);
    private final Button buttonPopulate = utils.createButton("Populate", styles.getBigButtonWidth(), null);
    private final Button buttonDelete = utils.createButton("Delete", styles.getBigButtonWidth(), null);
    private final Button buttonClear = utils.createButton("Clear", styles.getBigButtonWidth(), null);
    private final PaneForComponentDetails paneForComponentDetails = new PaneForComponentDetails();
    private final PaneForCountryStateTree paneForCountryStateTree = new PaneForCountryStateTree();

    // Sector info for populating output and units (sector_info[i][0]=name, [2]=units)
    private final String[][] sector_info;

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
        sector_info = vars.getSectorInfo();
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
        gridPaneLeft.add(utils.createLabel("Specification:"), 0, 0, 2, 1);
        gridPaneLeft.addColumn(0, labelSector, new Label(), labelTextFieldUnits, new Separator(),
                utils.createLabel("Populate:"), labelModificationType, labelStartYear, labelEndYear, labelInitialAmount, labelGrowth);
        gridPaneLeft.addColumn(1, comboBoxSector, new Label(), labelTextFieldUnits2, new Separator(), new Label(),
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
     */
    private void setupActions() {
        setupComboBoxSector();
        comboBoxSector.getSelectionModel().selectFirst();
        comboBoxModificationType.getItems().addAll(
                "Initial and Final", "Initial w/% Growth/yr", "Initial w/% Growth/pd",
                "Initial w/Delta/yr", "Initial w/Delta/pd");
        comboBoxModificationType.getSelectionModel().selectFirst();
        double max_wid = 195, min_wid = 105, pref_wid = 195;
        comboBoxSector.setMaxWidth(max_wid);
        comboBoxModificationType.setMaxWidth(max_wid);
        comboBoxSector.setMinWidth(min_wid);
        comboBoxModificationType.setMinWidth(min_wid);
        comboBoxSector.setPrefWidth(pref_wid);
        comboBoxModificationType.setPrefWidth(pref_wid);
        comboBoxSector.setOnAction(e -> {
            String selectedItem = comboBoxSector.getSelectionModel().getSelectedItem();
            if (selectedItem == null) return;
            if ("Other".equals(selectedItem)) {
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
        switch (type) {
            case "Initial w/% Growth/yr":
            case "Initial w/% Growth/pd":
                labelGrowth.setText("Growth (%):");
                break;
            case "Initial w/Delta/yr":
            case "Initial w/Delta/pd":
                labelGrowth.setText("Delta:");
                break;
            case "Initial and Final":
            default:
                labelGrowth.setText("Final:");
                break;
        }
    }

    /**
     * Populates the sector combo box with available sectors from sector_info.
     * Adds an "Other" option for custom sectors.
     */
    private void setupComboBoxSector() {
        try {
            for (String[] sector : sector_info) {
                comboBoxSector.getItems().add(sector[0]);
            }
            comboBoxSector.getItems().add("Other");
        } catch (Exception e) {
            utils.warningMessage("Problem reading sector list.");
            System.out.println("  ---> " + e);
        }
    }

    /**
     * Updates the units label based on the selected sector.
     * Looks up units in sector_info and displays them next to the sector.
     */
    private void updateSectorOutputAndUnits() {
        String selectedSector = comboBoxSector.getValue();
        labelTextFieldUnits2.setText("");
        for (String[] sector : sector_info) {
            if (selectedSector.equals(sector[0])) {
                labelTextFieldUnits2.setText(sector[2]);
            }
        }
    }

    /**
     * Calculates the values matrix for the demand table based on user input and modification type.
     * @return 2D array of calculated values for each year/period
     */
    private double[][] calculateValues() {
        String calc_type = comboBoxModificationType.getSelectionModel().getSelectedItem();
        int start_year = Integer.parseInt(textFieldStartYear.getText());
        int end_year = Integer.parseInt(textFieldEndYear.getText());
        double initial_value = Double.parseDouble(textFieldInitialAmount.getText());
        double growth = Double.parseDouble(textFieldGrowth.getText());
        int period_length = Integer.parseInt(textFieldPeriodLength.getText());
        return utils.calculateValues(calc_type, start_year, end_year, initial_value, growth, period_length);
    }

    /**
     * Runnable implementation: triggers saving the scenario component.
     */
    @Override
    public void run() {
        saveScenarioComponent();
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
            String sector_name = comboBoxSector.getSelectionModel().getSelectedItem().trim();
            filenameSuggestion = sector_name + "fxDMD";
            ArrayList<String> dataArrayList = paneForComponentDetails.getDataYrValsArrayList();
            String[] year_list = new String[dataArrayList.size()];
            String[] value_list = new String[dataArrayList.size()];
            for (int i = 0; i < dataArrayList.size(); i++) {
                String str = dataArrayList.get(i).replace(" ", "").trim();
                year_list[i] = utils.splitString(str, ",")[0];
                value_list[i] = utils.splitString(str, ",")[1];
            }
            // CSV content
            fileContent += "region,sector,sector,year,value" + vars.getEol();
            for (String region : listOfSelectedLeaves) {
                for (int i = 0; i < year_list.length; i++) {
                    fileContent += region + "," + sector_name + "," + sector_name + "," + year_list[i] + "," + value_list[i] + vars.getEol();
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
        StringBuilder rtn_str = new StringBuilder();
        rtn_str.append("########## Scenario Component Metadata ##########").append(vars.getEol());
        rtn_str.append("#Scenario component type: Fixed Demand").append(vars.getEol());
        rtn_str.append("#Sector:").append(comboBoxSector.getValue()).append(vars.getEol());
        String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);
        listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
        String states = utils.returnAppendedString(listOfSelectedLeaves);
        rtn_str.append("#Regions: ").append(states).append(vars.getEol());
        ArrayList<String> table_content = paneForComponentDetails.getDataYrValsArrayList();
        for (String row : table_content) {
            rtn_str.append("#Table data:").append(row).append(vars.getEol());
        }
        rtn_str.append("#################################################").append(vars.getEol());
        return rtn_str.toString();
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
        int error_count = 0;
        StringBuilder message = new StringBuilder();
        try {
            if (utils.getAllSelectedLeaves(tree).length < 1) {
                message.append("Must select at least one region from tree").append(vars.getEol());
                error_count++;
            }
            if (paneForComponentDetails.table.getItems().size() == 0) {
                message.append("Data table must have at least one entry").append(vars.getEol());
                error_count++;
            }
            if (comboBoxSector.getSelectionModel().getSelectedItem().equals("Select One")) {
                message.append("Sector comboBox must have a selection").append(vars.getEol());
                error_count++;
            }
        } catch (Exception e1) {
            error_count++;
            message.append("Error in QA of entries").append(vars.getEol());
        }
        if (error_count > 0) {
            if (error_count == 1) {
                utils.warningMessage(message.toString());
            } else {
                utils.warningMessage("More than one issue with inputs");
                utils.displayString(message.toString(), "Parsing Errors");
            }
        }
        return error_count == 0;
    }
}
