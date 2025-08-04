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
package glimpseElement;

// Grouped imports for clarity
import java.util.ArrayList;
import org.controlsfx.control.CheckComboBox;
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.CheckBoxTreeItem.TreeModificationEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * TabFuelPriceAdj provides the UI and logic for creating/editing fuel price adjustment policies.
 * <p>
 * This class manages the user interface for specifying, editing, and saving fuel price adjustment policies
 * in the GLIMPSE Scenario Builder. It handles user input, validation, and the generation of scenario
 * component files for downstream processing. UI updates must be performed on the JavaFX Application Thread.
 */
public class TabFuelPriceAdj extends PolicyTab implements Runnable {
    // === UI constants ===
    private static final double LABEL_WIDTH = 125;
    private static final double FIELD_WIDTH = 180;

    // === Label and ComboBox option constants ===
    private static final String LABEL_FUEL = "Fuel: ";
    private static final String LABEL_UNITS = "Units: ";
    private static final String LABEL_UNITS_VALUE = "1975$s per GJ";
    private static final String LABEL_POLICY_NAME = "Policy: ";
    private static final String LABEL_MARKET_NAME = "Market: ";
    private static final String LABEL_USE_AUTO_NAMES = "Names: ";
    private static final String LABEL_MODIFICATION_TYPE = "Type: ";
    private static final String LABEL_START_YEAR = "Start Year: ";
    private static final String LABEL_END_YEAR = "End Year: ";
    private static final String LABEL_INITIAL_AMOUNT = "Initial Val:   ";
    private static final String LABEL_GROWTH = "Growth (%): ";
    private static final String LABEL_PERIOD_LENGTH = "Period Length: ";
    private static final String LABEL_CONVERT_FROM = "Convert $s from: ";
    private static final String LABEL_VALUES = "Values: ";
    private static final String BUTTON_POPULATE = "Populate";
    private static final String BUTTON_IMPORT = "Import";
    private static final String BUTTON_DELETE = "Delete";
    private static final String BUTTON_CLEAR = "Clear";
    private static final String CHECKBOX_AUTO = "Auto?";
    private static final String[] CONVERT_FROM_OPTIONS = {"None","2023$s","2020$s","2015$s","2010$s","2005$s","2000$s"};
    private static final String[] MODIFICATION_TYPE_OPTIONS = {
        "Initial w/% Growth/yr", "Initial w/% Growth/pd",
        "Initial w/Delta/yr", "Initial w/Delta/pd", "Initial and Final"
    };

    // === UI components ===
    private final GridPane gridPanePresetModification = new GridPane();
    private final GridPane gridPaneLeft = new GridPane();
    private final ScrollPane scrollPaneLeft = new ScrollPane();
    private final Label labelFuel = utils.createLabel(LABEL_FUEL, LABEL_WIDTH);
    private final Label labelUnits = utils.createLabel(LABEL_UNITS, LABEL_WIDTH);
    private final Label labelUnitsValue = utils.createLabel(LABEL_UNITS_VALUE, 225.);
    private final CheckComboBox<String> comboBoxFuel = utils.createCheckComboBox();
    private final Label labelPolicyName = utils.createLabel(LABEL_POLICY_NAME, LABEL_WIDTH);
    private final TextField textFieldPolicyName = new TextField("");
    private final Label labelMarketName = utils.createLabel(LABEL_MARKET_NAME, LABEL_WIDTH);
    private final TextField textFieldMarketName = new TextField("");
    private final Label labelUseAutoNames = utils.createLabel(LABEL_USE_AUTO_NAMES, LABEL_WIDTH);
    private final CheckBox checkBoxUseAutoNames = utils.createCheckBox(CHECKBOX_AUTO);
    private final Label labelModificationType = utils.createLabel(LABEL_MODIFICATION_TYPE, LABEL_WIDTH);
    private final ComboBox<String> comboBoxModificationType = utils.createComboBoxString();
    private final Label labelStartYear = utils.createLabel(LABEL_START_YEAR, LABEL_WIDTH);
    private final TextField textFieldStartYear = new TextField("2020");
    private final Label labelEndYear = utils.createLabel(LABEL_END_YEAR, LABEL_WIDTH);
    private final TextField textFieldEndYear = new TextField("2050");
    private final Label labelInitialAmount = utils.createLabel(LABEL_INITIAL_AMOUNT, LABEL_WIDTH);
    private final TextField textFieldInitialAmount = utils.createTextField();
    private final Label labelGrowth = utils.createLabel(LABEL_GROWTH, LABEL_WIDTH);
    private final TextField textFieldGrowth = utils.createTextField();
    private final Label labelPeriodLength = utils.createLabel(LABEL_PERIOD_LENGTH, LABEL_WIDTH);
    private final TextField textFieldPeriodLength = new TextField("5");
    private final Label labelConvertFrom = utils.createLabel(LABEL_CONVERT_FROM, LABEL_WIDTH);
    private final ComboBox<String> comboBoxConvertFrom = utils.createComboBoxString();
    private final VBox vBoxCenter = new VBox();
    private final HBox hBoxHeaderCenter = new HBox();
    private final Label labelValue = utils.createLabel(LABEL_VALUES);
    private final Button buttonPopulate = utils.createButton(BUTTON_POPULATE, styles.getBigButtonWidth(), null);
    private final Button buttonImport = utils.createButton(BUTTON_IMPORT, styles.getBigButtonWidth(), null);
    private final Button buttonDelete = utils.createButton(BUTTON_DELETE, styles.getBigButtonWidth(), null);
    private final Button buttonClear = utils.createButton(BUTTON_CLEAR, styles.getBigButtonWidth(), null);
    private final PaneForComponentDetails paneForComponentDetails = new PaneForComponentDetails();
    private final HBox hBoxHeaderRight = new HBox();
    private final VBox vBoxRight = new VBox();
    private final PaneForCountryStateTree paneForCountryStateTree = new PaneForCountryStateTree();
    
    /**
     * Constructs a new TabFuelPriceAdj instance and initializes the UI components for the Fuel Price Adjustment tab.
     * Sets up event handlers and populates controls with available data.
     *
     * @param title The title of the tab
     * @param stageX The JavaFX stage
     */
    public TabFuelPriceAdj(String title, Stage stageX) {
        // --- Set up region tree and tab title ---
        TreeItem<String> ti = paneForCountryStateTree != null && paneForCountryStateTree.getTree() != null ? paneForCountryStateTree.getTree().getRoot() : null;
        if (ti != null) ti.setExpanded(true);
        this.setText(title);
        if (styles != null) this.setStyle(styles.getFontStyle());

        // --- Set up initial state of check box and text fields ---
        if (checkBoxUseAutoNames != null) checkBoxUseAutoNames.setSelected(true);
        if (textFieldPolicyName != null) textFieldPolicyName.setDisable(true);
        if (textFieldMarketName != null) textFieldMarketName.setDisable(true);
        if (comboBoxConvertFrom != null) {
            comboBoxConvertFrom.getItems().addAll(CONVERT_FROM_OPTIONS);
            comboBoxConvertFrom.getSelectionModel().selectFirst();
        }

        // --- Layout: Left column (specification and populate controls) ---
        gridPaneLeft.add(utils.createLabel("Specification:"), 0, 0, 2, 1);
        gridPaneLeft.addColumn(0, labelFuel, new Label(), labelUnits, new Label(),
                new Separator(), labelUseAutoNames, labelPolicyName, labelMarketName, new Label(), new Separator(),
                utils.createLabel("Populate:"), labelModificationType, labelStartYear, labelEndYear, labelInitialAmount,
                labelGrowth, labelConvertFrom);
        gridPaneLeft.addColumn(1, comboBoxFuel, new Label(), labelUnitsValue, new Label(), 
        		new Separator(), checkBoxUseAutoNames, textFieldPolicyName, textFieldMarketName, new Label(), new Separator(),
                new Label(), comboBoxModificationType, textFieldStartYear, textFieldEndYear, textFieldInitialAmount,
                textFieldGrowth, comboBoxConvertFrom);
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
        comboBoxFuel.setMaxWidth(max_wid); comboBoxFuel.setMinWidth(min_wid); comboBoxFuel.setPrefWidth(pref_wid);
        textFieldStartYear.setMaxWidth(max_wid); textFieldStartYear.setMinWidth(min_wid); textFieldStartYear.setPrefWidth(pref_wid);
        textFieldEndYear.setMaxWidth(max_wid); textFieldEndYear.setMinWidth(min_wid); textFieldEndYear.setPrefWidth(pref_wid);
        textFieldInitialAmount.setMaxWidth(max_wid); textFieldInitialAmount.setMinWidth(min_wid); textFieldInitialAmount.setPrefWidth(pref_wid);
        textFieldGrowth.setMaxWidth(max_wid); textFieldGrowth.setMinWidth(min_wid); textFieldGrowth.setPrefWidth(pref_wid);
        textFieldPeriodLength.setMaxWidth(max_wid); textFieldPeriodLength.setMinWidth(min_wid); textFieldPeriodLength.setPrefWidth(pref_wid);
        textFieldPolicyName.setMaxWidth(max_wid); textFieldPolicyName.setMinWidth(min_wid); textFieldPolicyName.setPrefWidth(pref_wid);
        textFieldMarketName.setMaxWidth(max_wid); textFieldMarketName.setMinWidth(min_wid); textFieldMarketName.setPrefWidth(pref_wid);
        comboBoxConvertFrom.setMaxWidth(max_wid); comboBoxConvertFrom.setMinWidth(min_wid); comboBoxConvertFrom.setPrefWidth(min_wid);

        // --- Populate fuel and modification type options ---
        String[][] tech_list = vars.getTechInfo();
        ArrayList<String> fuelList = extractFuelsFromTechList(tech_list);
        if (comboBoxFuel != null && fuelList != null) comboBoxFuel.getItems().addAll(fuelList);
        if (comboBoxModificationType != null) {
            comboBoxModificationType.getItems().addAll(MODIFICATION_TYPE_OPTIONS);
            comboBoxModificationType.getSelectionModel().selectFirst();
        }

        // --- Event handlers for UI controls ---
        labelFuel.setOnMouseClicked(e -> {
            if (comboBoxFuel != null && !comboBoxFuel.isDisabled()) {
                boolean isFirstItemChecked = comboBoxFuel.getCheckModel().isChecked(0);
                if (e.getClickCount() == 2) {
                    if (isFirstItemChecked) {
                        comboBoxFuel.getCheckModel().clearChecks();
                    } else {
                        comboBoxFuel.getCheckModel().checkAll();
                    }
                }
            }
        });
        if (comboBoxFuel != null) {
            comboBoxFuel.setOnMouseExited(e -> setPolicyAndMarketNames());
        }
        EventHandler<TreeModificationEvent> ev = new EventHandler<TreeModificationEvent>() {
            @Override
            public void handle(TreeModificationEvent ae) {
                ae.consume();
                setPolicyAndMarketNames();
            }
        };
        if (paneForCountryStateTree != null) {
            paneForCountryStateTree.addEventHandlerToAllLeafs(ev);
        }
        if (checkBoxUseAutoNames != null) {
            checkBoxUseAutoNames.setOnAction(e -> {
                if (!checkBoxUseAutoNames.isSelected()) {
                    if (textFieldPolicyName != null) textFieldPolicyName.setDisable(false);
                    if (textFieldMarketName != null) textFieldMarketName.setDisable(false);
                } else {
                    if (textFieldMarketName != null) textFieldMarketName.setDisable(true);
                    if (textFieldPolicyName != null) textFieldPolicyName.setDisable(true);
                }
            });
        }
        if (comboBoxModificationType != null) {
            comboBoxModificationType.setOnAction(e -> {
                if (comboBoxModificationType.getSelectionModel().getSelectedItem() == null) return;
                switch (comboBoxModificationType.getSelectionModel().getSelectedItem()) {
                    case "Initial w/% Growth/yr":
                    case "Initial w/% Growth/pd":
                        if (labelGrowth != null) labelGrowth.setText("Growth (%):");
                        break;
                    case "Initial w/Delta/yr":
                    case "Initial w/Delta/pd":
                        if (labelGrowth != null) labelGrowth.setText("Delta:");
                        break;
                    case "Initial and Final":
                        if (labelGrowth != null) labelGrowth.setText("Final Val:");
                        break;
                }
            });
        }
        if (buttonClear != null) {
            buttonClear.setOnAction(e -> {
                if (paneForComponentDetails != null) paneForComponentDetails.clearTable();
            });
        }
        if (buttonDelete != null) {
            buttonDelete.setOnAction(e -> {
                if (paneForComponentDetails != null) paneForComponentDetails.deleteItemsFromTable();
            });
        }
        if (buttonPopulate != null) {
            buttonPopulate.setOnAction(e -> {
                if (qaPopulate() && paneForComponentDetails != null) {
                    double[][] values = calculateValues();
                    paneForComponentDetails.setValues(values);
                }
            });
        }

        // --- Finalize layout ---
        setPolicyAndMarketNames();
        VBox tabLayout = new VBox();
        tabLayout.getChildren().addAll(gridPanePresetModification);
        this.setContent(tabLayout);
    }

    /**
     * Extracts a list of unique fuel strings from the technology list.
     *
     * @param tech_list The 2D array of technology information
     * @return ArrayList of unique fuel strings
     */
    private ArrayList<String> extractFuelsFromTechList(String[][] tech_list) {
        ArrayList<String> fuels = new ArrayList<>();
        for (int row = 0; row < tech_list.length; row++) {
            String str_col0 = tech_list[row][0];
            // Only add relevant fuel types
            if ((str_col0.startsWith("regional ")) || (str_col0.contains("wholesale")) || (str_col0.contains("delivered")) || (str_col0.contains("elect_td"))) {
                String str = tech_list[row][0] + "," + tech_list[row][1] + "," + tech_list[row][2];
                fuels.add(str);
            }
        }
        fuels = utils.getUniqueItemsFromStringArrayList(fuels);
        return fuels;
    }
    
    /**
     * Sets the policy and market names automatically based on selected fuels and regions.
     * If auto-naming is enabled, updates the text fields accordingly.
     * This method should be called on the JavaFX Application Thread.
     */
    private void setPolicyAndMarketNames() {
        if (checkBoxUseAutoNames != null && checkBoxUseAutoNames.isSelected()) {
            String policy_type = "FuelPriceAdj";
            String fuel = "----";
            String state = "--";
            try {
                // Determine selected fuel(s)
                int no_selected_fuels = (comboBoxFuel != null) ? comboBoxFuel.getCheckModel().getCheckedItems().size() : 0;
                if (no_selected_fuels == 1 && comboBoxFuel != null) {
                    ObservableList<String> selected_items = comboBoxFuel.getCheckModel().getCheckedItems();
                    fuel = selected_items.get(0);
                    // Simplify fuel name for auto-naming
                    if (fuel.contains("gas")) fuel = "gas";
                    else if (fuel.contains("oil")) fuel = "oil";
                    else if (fuel.contains("unconv")) fuel = "uncvoil";
                    else if (fuel.contains("coal")) fuel = "coal";
                    else if (fuel.contains("bio")) fuel = "bio";
                    else if (fuel.contains("corn")) fuel = "corn";
                    else fuel = "oth";
                } else if (no_selected_fuels > 1) {
                    fuel = "mult";
                }
                // Determine selected region(s)
                String[] listOfSelectedLeaves = (paneForCountryStateTree != null && paneForCountryStateTree.getTree() != null) ? utils.getAllSelectedLeaves(paneForCountryStateTree.getTree()) : new String[0];
                if (listOfSelectedLeaves.length > 0) {
                    listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
                    String state_str = utils.returnAppendedString(listOfSelectedLeaves).replace(",", "");
                    if (state_str.length() < 9) state = state_str;
                    else state = "Reg";
                }
                // Set names
                String name = policy_type + "-" + fuel + "-" + state;
                name = name.replaceAll(" ", "_").replaceAll("--", "-");
                if (textFieldMarketName != null) textFieldMarketName.setText(name + "_Mkt");
                if (textFieldPolicyName != null) textFieldPolicyName.setText(name);
            } catch (Exception e) {
                System.out.println("Cannot auto-name market. Continuing.");
            }
        }
    }

    /**
     * Calculates the values for the policy based on user input and selected calculation type.
     *
     * @return 2D array of calculated values
     */
    private double[][] calculateValues() {
        String calc_type = comboBoxModificationType != null ? comboBoxModificationType.getSelectionModel().getSelectedItem() : null;
        int start_year = textFieldStartYear != null ? Integer.parseInt(textFieldStartYear.getText()) : 0;
        int end_year = textFieldEndYear != null ? Integer.parseInt(textFieldEndYear.getText()) : 0;
        double initial_value = textFieldInitialAmount != null ? Double.parseDouble(this.textFieldInitialAmount.getText()) : 0.0;
        double growth = textFieldGrowth != null ? Double.parseDouble(textFieldGrowth.getText()) : 0.0;
        int period_length = textFieldPeriodLength != null ? Integer.parseInt(this.textFieldPeriodLength.getText()) : 0;
        double factor = 1.0;
        String convertYear = comboBoxConvertFrom != null ? comboBoxConvertFrom.getValue() : null;
        if (convertYear != null && !"None".equals(convertYear)) {
            factor = utils.getConversionFactor(convertYear, "1975$s");
        }
        double[][] returnMatrix = utils.calculateValues(calc_type, start_year, end_year, initial_value, growth, period_length, factor);
        return returnMatrix;
    }

    /**
     * Runnable implementation. Triggers saving of the scenario component.
     * This method is intended to be called in a separate thread, but any UI updates must be wrapped in Platform.runLater.
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
        if (!qaInputs()){
            Thread.currentThread().destroy();
        } else {

            String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);

            listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
            String states = utils.returnAppendedString(listOfSelectedLeaves);

            filenameSuggestion = "";

            // constructs a filename suggestion for the scenario component
            ObservableList<String> fuel_list = comboBoxFuel.getCheckModel().getCheckedItems();

            
            //String ID=this.getUniqueMarketName(textFieldMarketName.getText());
            String ID=utils.getUniqueString();
            filenameSuggestion=this.textFieldPolicyName.getText().replaceAll("/", "-").replaceAll(" ", "_")+".csv";
            String policy_name = this.textFieldPolicyName.getText()+ID;
            String market_name = this.textFieldMarketName.getText()+ID;
            
            fileContent = getMetaDataContent(tree,market_name,policy_name);

            for (int f = 0; f < fuel_list.size(); f++) {
                
                String fuel_line=fuel_list.get(f);
                
                ArrayList<String> temp=utils.createArrayListFromString(fuel_line,",");
                
                String sector = temp.get(0);
                String subsector = temp.get(1);
                String tech= temp.get(2);
                //String fuel= temp.get(3);

                if (f!=0) fileContent+=vars.getEol();

                //filenameSuggestion = policy_name.replaceAll("/", "-").replaceAll(" ", "_").replaceAll("+","-")+".csv";

                String region = states.replace(",", "");
                if (region.length() > 6) {
                    region = "Reg";
                }

                // sets up the content of the CSV file to store the scenario component data


                String header = "GLIMPSEFuelPriceAdj";

                // part 1
                fileContent += "INPUT_TABLE" + vars.getEol();
                fileContent += "Variable ID" + vars.getEol();
                fileContent += header + vars.getEol() + vars.getEol();
                fileContent += "region,supplysector,subsector,technology,param,year,adjustment" + vars.getEol();

                for (int s = 0; s < listOfSelectedLeaves.length; s++) {
                    String state = listOfSelectedLeaves[s];

                    ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();
                    for (int i = 0; i < data.size(); i++) {
                        String data_str = data.get(i).replace(" ", "");
                        String year = utils.splitString(data_str, ",")[0];
                        String val = utils.splitString(data_str, ",")[1];
                        fileContent += state + "," + sector + "," + subsector + "," + tech + ","
                                + year + ",regional price adjustment," +val + vars.getEol();
                    }

                }
            }

        }
    }


    /**
     * Generates the metadata content string for the scenario component, including selected fuels, units, policy/market names, and table data.
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
        ObservableList<String> fuel_list = comboBoxFuel != null ? comboBoxFuel.getCheckModel().getCheckedItems() : null;
        String fuel = fuel_list != null ? utils.getStringFromList(fuel_list, ";") : "";
        rtn_str += "#Fuel: " + fuel + vars.getEol();
        rtn_str += "#Units: " + (labelUnitsValue != null ? labelUnitsValue.getText() : "") + vars.getEol();
        if (policy == null && textFieldPolicyName != null) market = textFieldPolicyName.getText();
        rtn_str += "#Policy name: " + policy + vars.getEol();
        if (market == null && textFieldMarketName != null) market = textFieldMarketName.getText();
        rtn_str += "#Market name: " + market + vars.getEol();
        String[] listOfSelectedLeaves = tree != null ? utils.getAllSelectedLeaves(tree) : new String[0];
        listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
        String states = utils.returnAppendedString(listOfSelectedLeaves);
        rtn_str += "#Regions: " + states + vars.getEol();
        ArrayList<String> table_content = paneForComponentDetails != null ? this.paneForComponentDetails.getDataYrValsArrayList() : new ArrayList<>();
        for (int i = 0; i < table_content.size(); i++) {
            rtn_str += "#Table data:" + table_content.get(i) + vars.getEol();
        }
        rtn_str += "#################################################" + vars.getEol();
        return rtn_str;
    }

    /**
     * Loads content from a list of strings (typically from a file) and populates the UI fields accordingly.
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
                if (param.equals("fuel") && comboBoxFuel != null) {
                    String[] set = utils.splitString(value, ";");
                    for (int j = 0; j < set.length; j++) {
                        String item = set[j].trim();
                        comboBoxFuel.getCheckModel().check(item);
                        comboBoxFuel.fireEvent(new ActionEvent());
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
        if (paneForComponentDetails != null) this.paneForComponentDetails.updateTable();
    }
    
    /**
     * Helper method to parse table data from a string and add to the component details.
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
     * @return true if at least one year matches allowable years, false otherwise
     */
    private boolean validateTableDataYears() {
        String listOfAllowableYears = vars.getAllowablePolicyYears();
        ObservableList<DataPoint> data = paneForComponentDetails != null ? this.paneForComponentDetails.table.getItems() : null;
        if (data == null) return false;
        for (DataPoint dp : data) {
            String year = dp.getYear().trim();
            if (listOfAllowableYears.contains(year)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Performs a quick QA check to ensure required fields for populating values are filled.
     *
     * @return true if all required fields are filled, false otherwise
     */
    public boolean qaPopulate() {
        boolean is_correct = true;
        if (textFieldStartYear == null || textFieldStartYear.getText().isEmpty()) is_correct = false;
        if (textFieldEndYear == null || textFieldEndYear.getText().isEmpty()) is_correct = false;
        if (textFieldInitialAmount == null || textFieldInitialAmount.getText().isEmpty()) is_correct = false;
        if (textFieldGrowth == null || textFieldGrowth.getText().isEmpty()) is_correct = false;
        return is_correct;
    }

    /**
     * Performs QA checks on the current UI state to ensure all required inputs are valid.
     * Displays warnings or error messages as needed.
     *
     * @return true if all inputs are valid, false otherwise
     */
    protected boolean qaInputs() {

        TreeView<String> tree = paneForCountryStateTree != null ? paneForCountryStateTree.getTree() : null;

        int error_count = 0;
        String message = "";

        try {

            if (tree == null || utils.getAllSelectedLeaves(tree).length < 1) {
                message += "Must select at least one region from tree" + vars.getEol();
                error_count++;
            }
            if (paneForComponentDetails == null || paneForComponentDetails.table.getItems().size() == 0) {
                message += "Data table must have at least one entry" + vars.getEol();
                error_count++;
            } else {
                boolean match = validateTableDataYears();
                if (!match) {
                    message += "Years specified in table must match allowable policy years (" + vars.getAllowablePolicyYears() + ")" + vars.getEol();
                    error_count++;
                }
            }
            if (comboBoxFuel != null && (comboBoxFuel.getCheckModel().getItemCount() == 1)
                    && (comboBoxFuel.getCheckModel().isChecked("Select One or More"))) {
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
            if (vars.isGcamUSA()) {
                String[] selected_leaves = tree != null ? utils.getAllSelectedLeaves(tree) : new String[0];
                boolean applied_to_a_state = false;
                boolean is_usa_selected = false;
                for (int s = 0; s < selected_leaves.length; s++) {
                    String region = selected_leaves[s];
                    if (utils.isState(region)) applied_to_a_state = true;
                    else if (region.equals("USA")) is_usa_selected = true;
                }
                if (applied_to_a_state && is_usa_selected) {
                    message += "Cannot apply policy to both individual states and the entire USA. Please select one." + vars.getEol();
                    error_count++;
                }
            }

        } catch (Exception e1) {
            System.out.println("error " + e1);
            error_count++;
            message += "Error in QA of entries" + vars.getEol();
        }
        if (error_count > 0) {
            try {
                if (error_count == 1) utils.warningMessage(message);
                else if (error_count > 1) utils.displayString(message, "Parsing Errors");
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
     * Helper method to add items to a ComboBox<String> or CheckComboBox<String>.
     */
    private void addItemsToComboBox(ComboBox<String> comboBox, String[] items) {
        comboBox.getItems().addAll(items);
    }
    private void addItemsToCheckComboBox(CheckComboBox<String> checkComboBox, String[] items) {
        checkComboBox.getItems().addAll(items);
    }

}