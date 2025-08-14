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

import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.CheckComboBox;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * TabFuelPriceAdj provides the UI and logic for creating/editing fuel price
 * adjustment policies.
 * <p>
 * This class manages the user interface for specifying, editing, and saving
 * fuel price adjustment policies in the GLIMPSE Scenario Builder. It handles
 * user input, validation, and the generation of scenario component files for
 * downstream processing. UI updates must be performed on the JavaFX Application
 * Thread.
 */
public class TabFuelPriceAdj extends PolicyTab implements Runnable {
    // === UI constants ===
    private static final String LABEL_FUEL = "Fuel: ";
    private static final String LABEL_UNITS = "Units: ";
    private static final String LABEL_UNITS_VALUE = "1975$s per GJ";

    // === UI components ===
    private final Label labelFuel = createLabel(LABEL_FUEL, LABEL_WIDTH);
    private final CheckComboBox<String> checkComboBoxFuel = createCheckComboBox();
    private final Label labelUnits = createLabel(LABEL_UNITS, LABEL_WIDTH);
    private final Label labelUnitsValue = createLabel(LABEL_UNITS_VALUE, 225.);

    private ArrayList<String> fuelList = new ArrayList<>();

    /**
     * Constructs a new TabFuelPriceAdj instance and initializes the UI components
     * for the Fuel Price Adjustment tab. Sets up event handlers and populates
     * controls with available data.
     *
     * @param title  The title of the tab
     * @param stageX The JavaFX stage
     */
    public TabFuelPriceAdj(String title, Stage stageX) {
        // --- Set up region tree and tab title ---
        TreeItem<String> ti = paneForCountryStateTree != null && paneForCountryStateTree.getTree() != null
                ? paneForCountryStateTree.getTree().getRoot()
                : null;
        if (ti != null)
            ti.setExpanded(true);
        this.setText(title);
        if (styles != null)
            this.setStyle(styles.getFontStyle());

        // --- Set up initial state of check box and text fields ---
        if (checkBoxUseAutoNames != null)
            checkBoxUseAutoNames.setSelected(true);
        if (textFieldPolicyName != null)
            textFieldPolicyName.setDisable(true);
        if (textFieldMarketName != null)
            textFieldMarketName.setDisable(true);
        if (comboBoxConvertFrom != null) {
            comboBoxConvertFrom.getItems().addAll(CONVERT_FROM_OPTIONS);
            comboBoxConvertFrom.getSelectionModel().selectFirst();
        }

        // --- Layout: Left column (specification and populate controls) ---
        gridPaneLeft.add(createLabel("Specification:"), 0, 0, 2, 1);
        gridPaneLeft.addColumn(0, labelFuel, new Label(), labelUnits, new Label(), new Separator(),
                labelUseAutoNames, labelPolicyName, labelMarketName, new Label(), new Separator(),
                createLabel("Populate:"), labelModificationType, labelStartYear, labelEndYear, labelInitialAmount,
                labelGrowth, labelConvertFrom);
        gridPaneLeft.addColumn(1, checkComboBoxFuel, new Label(), labelUnitsValue, new Label(),
                new Separator(), checkBoxUseAutoNames, textFieldPolicyName, textFieldMarketName, new Label(),
                new Separator(), new Label(), comboBoxModificationType, textFieldStartYear, textFieldEndYear,
                textFieldInitialAmount, textFieldGrowth, comboBoxConvertFrom);
        gridPaneLeft.setVgap(3.);
        gridPaneLeft.setStyle(styles.getStyle2());
        scrollPaneLeft.setContent(gridPaneLeft);

        // --- Layout: Center column (table and buttons) ---
        hBoxHeaderCenter.getChildren().addAll(buttonPopulate, buttonDelete, buttonClear);
        hBoxHeaderCenter.setSpacing(2.);
        hBoxHeaderCenter.setStyle(styles.getStyle3());
        vBoxCenter.getChildren().addAll(labelValue, hBoxHeaderCenter, paneForComponentDetails);
        vBoxCenter.setStyle(styles.getStyle2());

        // --- Layout: Right column (region tree) ---
        vBoxRight.getChildren().addAll(paneForCountryStateTree);
        vBoxRight.setStyle(styles.getStyle2());

        // --- Add columns to main grid ---
        gridPanePresetModification.addColumn(0, scrollPaneLeft);
        gridPanePresetModification.addColumn(1, vBoxCenter);
        gridPanePresetModification.addColumn(2, vBoxRight);
        gridPaneLeft.setPrefWidth(325);
        gridPaneLeft.setMinWidth(325);
        vBoxCenter.setPrefWidth(300);
        vBoxRight.setPrefWidth(300);

        // --- Set default sizing for controls ---
        double max_wid = 180, min_wid = 100, pref_wid = 180;
        checkComboBoxFuel.setMaxWidth(max_wid);
        checkComboBoxFuel.setMinWidth(min_wid);
        checkComboBoxFuel.setPrefWidth(pref_wid);
        textFieldStartYear.setMaxWidth(max_wid);
        textFieldStartYear.setMinWidth(min_wid);
        textFieldStartYear.setPrefWidth(pref_wid);
        textFieldEndYear.setMaxWidth(max_wid);
        textFieldEndYear.setMinWidth(min_wid);
        textFieldEndYear.setPrefWidth(pref_wid);
        textFieldInitialAmount.setMaxWidth(max_wid);
        textFieldInitialAmount.setMinWidth(min_wid);
        textFieldInitialAmount.setPrefWidth(pref_wid);
        textFieldGrowth.setMaxWidth(max_wid);
        textFieldGrowth.setMinWidth(min_wid);
        textFieldGrowth.setPrefWidth(pref_wid);
        textFieldPeriodLength.setMaxWidth(max_wid);
        textFieldPeriodLength.setMinWidth(min_wid);
        textFieldPeriodLength.setPrefWidth(pref_wid);
        textFieldPolicyName.setMaxWidth(max_wid);
        textFieldPolicyName.setMinWidth(min_wid);
        textFieldPolicyName.setPrefWidth(pref_wid);
        textFieldMarketName.setMaxWidth(max_wid);
        textFieldMarketName.setMinWidth(min_wid);
        textFieldMarketName.setPrefWidth(pref_wid);
        comboBoxConvertFrom.setMaxWidth(max_wid);
        comboBoxConvertFrom.setMinWidth(min_wid);
        comboBoxConvertFrom.setPrefWidth(min_wid);

        // --- Populate fuel and modification type options ---
        String[][] tech_list = vars.getTechInfo();
        extractInfoFromTechList(tech_list);
        if (checkComboBoxFuel != null && fuelList != null)
            checkComboBoxFuel.getItems().addAll(fuelList);

        if (comboBoxModificationType != null) {
            comboBoxModificationType.getItems().addAll(MODIFICATION_TYPE_OPTIONS);
            comboBoxModificationType.getSelectionModel().selectFirst();
        }

        // --- Event handlers for UI controls ---
        checkComboBoxFuel.getCheckModel().getCheckedItems()
                .addListener((javafx.collections.ListChangeListener<String>) change -> {
                    setPolicyAndMarketNames();
        });
        
        registerComboBoxEvent(comboBoxModificationType, e -> {
            if (comboBoxModificationType.getSelectionModel().getSelectedItem() == null)
                return;
            switch (comboBoxModificationType.getSelectionModel().getSelectedItem()) {
                case "Initial w/% Growth/yr":
                case "Initial w/% Growth/pd":
                    labelGrowth.setText("Growth (%):");
                    break;
                case "Initial w/Delta/yr":
                case "Initial w/Delta/pd":
                    labelGrowth.setText("Delta:");
                    break;
                case "Initial and Final":
                    labelGrowth.setText("Final Val:");
                    break;
            }
        });
        registerCheckBoxEvent(checkBoxUseAutoNames, e -> {
            if (!checkBoxUseAutoNames.isSelected()) {
                textFieldPolicyName.setDisable(false);
                textFieldMarketName.setDisable(false);
            } else {
                textFieldMarketName.setDisable(true);
                textFieldPolicyName.setDisable(true);
            }
        });
        registerButtonEvent(buttonClear, e -> paneForComponentDetails.clearTable());
        registerButtonEvent(buttonDelete, e -> paneForComponentDetails.deleteItemsFromTable());
        registerButtonEvent(buttonPopulate, e -> {
            if (qaPopulate() && paneForComponentDetails != null) {
                double[][] values = calculateValues();
                paneForComponentDetails.setValues(values);
            }
        });
        
        
        // --- Finalize layout ---
        setPolicyAndMarketNames();
        VBox tabLayout = new VBox();
        tabLayout.getChildren().addAll(gridPanePresetModification);
        this.setContent(tabLayout);
        
        paneForCountryStateTree.getTree().addEventHandler(ActionEvent.ACTION, e -> {
			setPolicyAndMarketNames();
		});
    }

    private void extractInfoFromTechList(String[][] tech_list) {
        for (int row = 0; row < tech_list.length; row++) {
            String str_cat = tech_list[row][7].trim();
            String str_tech = tech_list[row][2].trim();
            if (str_cat.equals("Energy-Carrier")) {
                fuelList.add(str_tech);
            }
        }
        fuelList = utils.getUniqueItemsFromStringArrayList(fuelList);
    }

    /**
     * Sets the policy and market names automatically based on selected fuels and
     * regions. If auto-naming is enabled, updates the text fields accordingly. This
     * method should be called on the JavaFX Application Thread.
     */
    private void setPolicyAndMarketNames() {
        if (checkBoxUseAutoNames != null && checkBoxUseAutoNames.isSelected()) {
            String policy_type = "FuelPriceAdj";
            String fuel = "----";
            String state = "--";
            try {
                // Determine selected fuel(s)
                if (checkComboBoxFuel.getCheckModel().getCheckedItems() != null) {
                    fuel = "Misc";
                    if (checkComboBoxFuel.getCheckModel().getCheckedItems().size() == 1) {
                        fuel = checkComboBoxFuel.getCheckModel().getCheckedItems().get(0).toLowerCase().replaceAll(" ", "_");
                    }
                    // Simplify fuel name for auto-naming
//                    if (fuel.contains("gas"))
//                        fuel = "gas";
//                    else if (fuel.contains("elect"))
//                        fuel = "elec";
//                    else if (fuel.contains("oil"))
//                        fuel = "oil";
//                    else if (fuel.contains("refined liquids"))
//                        fuel = "refined liquids";
//                    else if (fuel.contains("unconv"))
//                        fuel = "uncvoil";
//                    else if (fuel.contains("coal"))
//                        fuel = "coal";
//                    else if (fuel.contains("bio"))
//                        fuel = "bio";
//                    else if (fuel.contains("corn"))
//                        fuel = "corn";
                }
                // Determine selected region(s)
                String[] listOfSelectedLeaves = (paneForCountryStateTree != null
                        && paneForCountryStateTree.getTree() != null)
                                ? utils.getAllSelectedRegions(paneForCountryStateTree.getTree())
                                : new String[0];
                if (listOfSelectedLeaves.length > 0) {
                    listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
                    String state_str = utils.returnAppendedString(listOfSelectedLeaves).replace(",", "");
                    if (state_str.length() < 9)
                        state = state_str;
                    else
                        state = "Reg";
                }
                // Set names
                String name = policy_type + "-" + fuel + "-" + state;
                name = name.replaceAll(" ", "_").replaceAll("--", "-");
                if (textFieldMarketName != null)
                    textFieldMarketName.setText(name + "_Mkt");
                if (textFieldPolicyName != null)
                    textFieldPolicyName.setText(name);
            } catch (Exception e) {
                System.out.println("Cannot auto-name market. Continuing.");
            }
        }
    }

    /**
     * Runnable implementation. Triggers saving of the scenario component. This
     * method is intended to be called in a separate thread, but any UI updates must
     * be wrapped in Platform.runLater.
     */
    @Override
    public void run() {
        saveScenarioComponent();
    }

    /**
     * Saves the scenario component using the current UI state and selected regions.
     * This method delegates to saveScenarioComponent(TreeView<String> tree).
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
        } else {
            String[][] tech_list = vars.getTechInfo();
            String[] listOfSelectedLeaves = utils.getAllSelectedRegions(tree);
            listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
            utils.returnAppendedString(listOfSelectedLeaves);

            filenameSuggestion = "";

            String ID = utils.getUniqueString();

            filenameSuggestion = textFieldPolicyName.getText().replaceAll("/", "-").replaceAll(" ", "_") + ".csv";
            String policyName = textFieldPolicyName.getText() + ID;
            String marketName = textFieldMarketName.getText() + ID;

            StringBuilder fileContentBuilder = new StringBuilder();
            fileContentBuilder.append(getMetaDataContent(tree, marketName, policyName));
            boolean firstitem = true;

            List<String> selectedFuels = checkComboBoxFuel.getCheckModel().getCheckedItems();

            for (String fuel : selectedFuels) {
                for (int t = 0; t < tech_list.length; t++) {
                    String cat = tech_list[t][7].trim();
                    if (cat.equals("Energy-Carrier")) {
                        String tech = tech_list[t][2].trim();
                        if (tech.contains(fuel)) {
                            String sector = tech_list[t][0].trim();
                            String subsector = tech_list[t][1].trim();

                            if (!firstitem) {
                                fileContentBuilder.append(vars.getEol());
                            }
                            firstitem = false;

                            String header = "GLIMPSEFuelPriceAdj";

                            fileContentBuilder.append("INPUT_TABLE").append(vars.getEol()).append("Variable ID")
                                    .append(vars.getEol()).append(header).append(vars.getEol()).append(vars.getEol())
                                    .append("region,supplysector,subsector,technology,param,year,adjustment")
                                    .append(vars.getEol());

                            for (String state : listOfSelectedLeaves) {
                                ArrayList<String> data = paneForComponentDetails.getDataYrValsArrayList();
                                for (String dataStr : data) {
                                    String[] splitData = utils.splitString(dataStr, ",");
                                    String year = splitData[0];
                                    String val = splitData[1];
                                    fileContentBuilder.append(state).append(",").append(sector).append(",")
                                            .append(subsector).append(",").append(tech).append(",").append(year)
                                            .append(",regional price adjustment,").append(val).append(vars.getEol());
                                }
                            }
                        }
                    }
                }
            }

            fileContent = fileContentBuilder.toString();
        }
    }

    /**
     * Generates the metadata content string for the scenario component, including
     * selected fuels, units, policy/market names, and table data.
     *
     * @param tree   The TreeView of regions
     * @param market The market name
     * @param policy The policy name
     * @return Metadata content string
     */
    public String getMetaDataContent(TreeView<String> tree, String market, String policy) {
        String rtn_str = "";
        rtn_str += "########## Scenario Component Metadata ##########" + vars.getEol();
        rtn_str += "#Scenario component type: Fuel Price Adj" + vars.getEol();
        String fuel = fuelList != null ? utils.getStringFromList(checkComboBoxFuel.getCheckModel().getCheckedItems(), ";") : "";
        rtn_str += "#Fuel: " + fuel + vars.getEol();
        rtn_str += "#Units: " + (labelUnitsValue != null ? labelUnitsValue.getText() : "") + vars.getEol();
        if (policy == null && textFieldPolicyName != null)
            market = textFieldPolicyName.getText();
        rtn_str += "#Policy name: " + policy + vars.getEol();
        if (market == null && textFieldMarketName != null)
            market = textFieldMarketName.getText();
        rtn_str += "#Market name: " + market + vars.getEol();
        String[] listOfSelectedLeaves = tree != null ? utils.getAllSelectedRegions(tree) : new String[0];
        listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
        String states = utils.returnAppendedString(listOfSelectedLeaves);
        rtn_str += "#Regions: " + states + vars.getEol();
        ArrayList<String> table_content = paneForComponentDetails != null
                ? this.paneForComponentDetails.getDataYrValsArrayList()
                : new ArrayList<>();
        for (int i = 0; i < table_content.size(); i++) {
            rtn_str += "#Table data:" + table_content.get(i) + vars.getEol();
        }
        rtn_str += "#################################################" + vars.getEol();
        return rtn_str;
    }

    /**
     * Loads content from a list of strings (typically from a file) and populates
     * the UI fields accordingly.
     *
     * @param content The list of content lines to load
     */
    @Override
    public void loadContent(ArrayList<String> content) {
        for (int i = 0; i < content.size(); i++) {
            String line = content.get(i);
            int pos = line.indexOf(":");
            if (line.startsWith("#") && (pos > -1)) {
                String param = line.substring(1, pos).trim().toLowerCase();
                String value = line.substring(pos + 1).trim();
                // --- Populate UI fields based on metadata ---
                if (param.equals("fuel") && checkComboBoxFuel != null) {
                    String[] set = utils.splitString(value, ";");
                    for (int j = 0; j < set.length; j++) {
                        String item = set[j].trim();
                        checkComboBoxFuel.getCheckModel().check(item);
                    }
                }
                if (param.equals("units") && labelUnitsValue != null) {
                    labelUnitsValue.setText(value);
                }
                if (param.equals("policy name") && textFieldPolicyName != null) {
                    textFieldPolicyName.setText(value);
                    textFieldPolicyName.fireEvent(new ActionEvent());
                }
                if (param.equals("market name") && textFieldMarketName != null) {
                    textFieldMarketName.setText(value);
                    textFieldMarketName.fireEvent(new ActionEvent());
                }
                if (param.equals("regions") && paneForCountryStateTree != null) {
                    String[] regions = utils.splitString(value, ",");
                    this.paneForCountryStateTree.selectNodes(regions);
                }
                if (param.equals("table data") && paneForComponentDetails != null) {
                    parseAndAddTableData(value);
                }
            }
        }
        if (paneForComponentDetails != null)
            this.paneForComponentDetails.updateTable();
    }

    /**
     * Helper method to parse table data from a string and add to the component
     * details.
     *
     * @param value The string containing year and value, comma-separated
     */
    private void parseAndAddTableData(String value) {
        String[] s = utils.splitString(value, ",");
        if (s.length >= 2 && paneForComponentDetails != null) {
            this.paneForComponentDetails.data.add(new DataPoint(s[0], s[1]));
        }
    }

    /**
     * Helper method to validate table data years against allowable policy years.
     * 
     * @return true if at least one year matches allowable years, false otherwise
     */
    private boolean validateTableDataYears() {
        List<Integer> listOfAllowableYears = vars.getAllowablePolicyYears();
        ObservableList<DataPoint> data = paneForComponentDetails != null ? this.paneForComponentDetails.table.getItems()
                : null;
        if (data == null)
            return false;
        for (DataPoint dp : data) {
            Integer year = Integer.parseInt(dp.getYear().trim());
            if (listOfAllowableYears.contains(year)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Performs a quick QA check to ensure required fields for populating values are
     * filled.
     *
     * @return true if all required fields are filled, false otherwise
     */
    public boolean qaPopulate() {
        boolean is_correct = true;
        if (textFieldStartYear == null || textFieldStartYear.getText().isEmpty())
            is_correct = false;
        if (textFieldEndYear == null || textFieldEndYear.getText().isEmpty())
            is_correct = false;
        if (textFieldInitialAmount == null || textFieldInitialAmount.getText().isEmpty())
            is_correct = false;
        if (textFieldGrowth == null || textFieldGrowth.getText().isEmpty())
            is_correct = false;
        return is_correct;
    }

    /**
     * Performs QA checks on the current UI state to ensure all required inputs are
     * valid. Displays warnings or error messages as needed.
     *
     * @return true if all inputs are valid, false otherwise
     */
    protected boolean qaInputs() {
        TreeView<String> tree = paneForCountryStateTree != null ? paneForCountryStateTree.getTree() : null;
        int error_count = 0;
        String message = "";
        try {
            if (tree == null || utils.getAllSelectedRegions(tree).length < 1) {
                message += "Must select at least one region from tree" + vars.getEol();
                error_count++;
            }
            if (paneForComponentDetails == null || paneForComponentDetails.table.getItems().size() == 0) {
                message += "Data table must have at least one entry" + vars.getEol();
                error_count++;
            } else {
                boolean match = validateTableDataYears();
                if (!match) {
                    message += "Years specified in table must match allowable policy years ("
                            + vars.getAllowablePolicyYears() + ")" + vars.getEol();
                    error_count++;
                }
            }
            if (checkComboBoxFuel != null && (checkComboBoxFuel.getCheckModel().getItemCount() == 0)) {
                message += "Fuel checkComboBox must have at least one selection" + vars.getEol();
                error_count++;
            }
            if (textFieldPolicyName == null || textFieldPolicyName.getText().equals("")) {
                message += "A policy name must be provided" + vars.getEol();
                error_count++;
            }
            if (textFieldMarketName == null || textFieldMarketName.getText().equals("")) {
                message += "A market name must be provided" + vars.getEol();
            }
        } catch (Exception e1) {
            System.out.println("error " + e1);
            error_count++;
            message += "Error in QA of entries" + vars.getEol();
        }
        if (error_count > 0) {
            try {
                if (error_count == 1)
                    utils.warningMessage(message);
                else if (error_count > 1)
                    utils.displayString(message, "Parsing Errors");
            } catch (Exception e1) {
                System.out.println(message);
                throw (e1);
            }
        }
        return error_count == 0;
    }

    /**
     * Helper method to set min, max, and preferred widths for multiple Controls.
     */
    private void setWidths(Control[] controls, double min, double max, double pref) {
        for (Control c : controls) {
            c.setMinWidth(min);
            c.setMaxWidth(max);
            c.setPrefWidth(pref);
        }
    }

    /**
     * Helper method to add items to a ComboBox<String>.
     *
     * @param comboBox the ComboBox to add items to
     * @param items    the array of items to add
     */
    private void addItemsToComboBox(ComboBox<String> comboBox, String[] items) {
        comboBox.getItems().addAll(items);
    }

    /**
     * Helper method to add items to a CheckComboBox<String>.
     *
     * @param checkComboBox the CheckComboBox to add items to
     * @param items         the array of items to add
     */
    private void addItemsToCheckComboBox(CheckComboBox<String> checkComboBox, String[] items) {
        checkComboBox.getItems().addAll(items);
    }

    /**
     * Registers an event handler for a ComboBox's ActionEvent.
     *
     * @param comboBox the ComboBox to register the event for
     * @param handler  the event handler
     */
    private void registerComboBoxEvent(ComboBox<String> comboBox, javafx.event.EventHandler<ActionEvent> handler) {
        comboBox.setOnAction(handler);
    }

    /**
     * Registers an event handler for a CheckBox's ActionEvent.
     *
     * @param checkBox the CheckBox to register the event for
     * @param handler  the event handler
     */
    private void registerCheckBoxEvent(CheckBox checkBox, javafx.event.EventHandler<ActionEvent> handler) {
        checkBox.setOnAction(handler);
    }

    /**
     * Registers an event handler for a Button's ActionEvent.
     *
     * @param button  the Button to register the event for
     * @param handler the event handler
     */
    private void registerButtonEvent(Button button, javafx.event.EventHandler<ActionEvent> handler) {
        button.setOnAction(handler);
    }

}