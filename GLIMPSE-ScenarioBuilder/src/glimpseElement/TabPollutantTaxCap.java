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
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem.TreeModificationEvent;
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
 * TabPollutantTaxCap provides the user interface and logic for creating and editing pollutant tax/cap policies
 * in the GLIMPSE Scenario Builder.
 * <p>
 * <b>Main responsibilities:</b>
 * <ul>
 *   <li>Allow users to select measure type (tax or cap), pollutant, and sector</li>
 *   <li>Configure policy and market names (auto/manual)</li>
 *   <li>Specify and populate cap/tax values over time</li>
 *   <li>Validate, import, and export scenario component data as CSV</li>
 * </ul>
 * </p>
 *
 * <b>Features:</b>
 * <ul>
 *   <li>Support for multiple pollutants (CO2, GHG, NOx, SO2, etc.)</li>
 *   <li>Automatic and manual naming for policy and market</li>
 *   <li>Dynamic enabling/disabling of UI controls based on selections</li>
 *   <li>Validation of user input and units</li>
 *   <li>Progress tracking for file generation</li>
 * </ul>
 *
 * <b>Usage:</b>
 * <pre>
 * TabPollutantTaxCap tab = new TabPollutantTaxCap("Pollutant Tax/Cap", stage);
 * // Add to TabPane, interact via UI
 * </pre>
 *
 * <b>Thread Safety:</b> This class is not thread-safe and should be used only on the JavaFX Application Thread.
 */
public class TabPollutantTaxCap extends PolicyTab implements Runnable {
    // === Constants for UI Texts and Options ===
    private static final double LABEL_WIDTH = 125;
    private static final double MAX_WIDTH = 225;
    private static final double MIN_WIDTH = 115;
    private static final double PREF_WIDTH = 225;
    private static final String[] MEASURE_OPTIONS = {"Select One", "Emission Cap (Mt)", "Emission Tax ($/t)"};
    private static final String[] POLLUTANT_OPTIONS = {"Select One", "CO2 (MT C)", "CO2 (MT CO2)", "GHG (MT CO2E)", "NOx (Tg)",
            "SO2 (Tg)", "PM2.5 (Tg)", "NMVOC (Tg)", "CO (Tg)", "NH3 (Tg)", "CH4 (Tg)", "N2O (Tg)"};
    private static final String[] MODIFICATION_TYPE_OPTIONS = {"Initial w/% Growth/yr", "Initial w/% Growth/pd",
            "Initial w/Delta/yr", "Initial w/Delta/pd", "Initial and Final"};
    private static final String[] CONVERT_FROM_OPTIONS = {"None", "2023$s", "2020$s", "2015$s", "2010$s", "2005$s", "2000$s"};
    private static final String LABEL_MEASURE = "Measure: ";
    private static final String LABEL_TYPE = "Type: ";
    private static final String LABEL_POLLUTANT = "Pollutant: ";
    private static final String LABEL_MODIFICATION_TYPE = "Source Type: ";
    private static final String LABEL_USE_AUTO_NAMES = "Names: ";
    private static final String LABEL_POLICY_NAME = "Policy: ";
    private static final String LABEL_MARKET_NAME = "Market: ";
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

    // === State ===
    public static String descriptionText = "";
    public static String runQueueStr = "Queue is empty.";
    private int check_count = 0;

    // === Layout and UI Components ===
    private final GridPane gridPanePresetModification = new GridPane();
    private final GridPane gridPaneLeft = new GridPane();
    private final VBox vBoxCenter = new VBox();
    private final HBox hBoxHeaderCenter = new HBox();
    private final VBox vBoxRight = new VBox();
    private final HBox hBoxHeaderRight = new HBox();
    private final PaneForComponentDetails paneForComponentDetails = new PaneForComponentDetails();
    private final PaneForCountryStateTree paneForCountryStateTree = new PaneForCountryStateTree();

    // === UI Controls ===
    private final Label labelComboBoxMeasure = utils.createLabel(LABEL_MEASURE, LABEL_WIDTH);
    private final ComboBox<String> comboBoxMeasure = utils.createComboBoxString();
    private final Label labelComboBoxType = utils.createLabel(LABEL_TYPE, LABEL_WIDTH);
    private final ComboBox<String> comboBoxType = utils.createComboBoxString();
    private final Label labelComboBoxPollutant = utils.createLabel(LABEL_POLLUTANT, LABEL_WIDTH);
    private final ComboBox<String> comboBoxPollutant = utils.createComboBoxString();
    private final Label labelModificationType = utils.createLabel(LABEL_MODIFICATION_TYPE, LABEL_WIDTH);
    private final ComboBox<String> comboBoxModificationType = utils.createComboBoxString();
    private final Label labelUseAutoNames = utils.createLabel(LABEL_USE_AUTO_NAMES, LABEL_WIDTH);
    private final CheckBox checkBoxUseAutoNames = utils.createCheckBox("Auto?");
    private final Label labelPolicyName = utils.createLabel(LABEL_POLICY_NAME, LABEL_WIDTH);
    private final TextField textFieldPolicyName = new TextField("");
    private final Label labelMarketName = utils.createLabel(LABEL_MARKET_NAME, LABEL_WIDTH);
    private final TextField textFieldMarketName = new TextField("");
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
    private final Label labelValue = utils.createLabel(LABEL_VALUES);
    private final Button buttonPopulate = utils.createButton(BUTTON_POPULATE, styles.getBigButtonWidth(), null);
    private final Button buttonImport = utils.createButton(BUTTON_IMPORT, styles.getBigButtonWidth(), null);
    private final Button buttonDelete = utils.createButton(BUTTON_DELETE, styles.getBigButtonWidth(), null);
    private final Button buttonClear = utils.createButton(BUTTON_CLEAR, styles.getBigButtonWidth(), null);

    /**
     * Constructs a TabPollutantTaxCap for the given title and stage.
     * Sets up all UI controls, listeners, and default values for the pollutant tax/cap policy tab.
     * @param title Tab title
     * @param stageX JavaFX Stage (not used directly)
     */
    public TabPollutantTaxCap(String title, Stage stageX) {
        this.setText(title);
        this.setStyle(styles.getFontStyle());
        checkBoxUseAutoNames.setSelected(true);
        textFieldPolicyName.setDisable(true);
        textFieldMarketName.setDisable(true);

        // left column
        gridPaneLeft.add(utils.createLabel("Specification:"), 0, 0, 2, 1);
        gridPaneLeft.addColumn(0, labelComboBoxMeasure, labelComboBoxPollutant, labelComboBoxType, new Label(),
                new Separator(), labelUseAutoNames, labelPolicyName, labelMarketName, new Label(), new Separator(),
                utils.createLabel("Populate:"), labelModificationType, labelStartYear, labelEndYear, labelInitialAmount, labelGrowth, labelConvertFrom);
        gridPaneLeft.addColumn(1, comboBoxMeasure, comboBoxPollutant, comboBoxType, new Label(), new Separator(),
                checkBoxUseAutoNames, textFieldPolicyName, textFieldMarketName, new Label(), new Separator(),
                new Label(), comboBoxModificationType, textFieldStartYear, textFieldEndYear, textFieldInitialAmount,
                textFieldGrowth, comboBoxConvertFrom);
        gridPaneLeft.setVgap(3.);
        gridPaneLeft.setStyle(styles.getStyle2());

        // center column
        hBoxHeaderCenter.getChildren().addAll(buttonPopulate, buttonDelete, buttonClear);
        hBoxHeaderCenter.setSpacing(2.);
        hBoxHeaderCenter.setStyle(styles.getStyle3());
        vBoxCenter.getChildren().addAll(labelValue, hBoxHeaderCenter, paneForComponentDetails);
        vBoxCenter.setStyle(styles.getStyle2());

        // right column
        vBoxRight.getChildren().addAll(paneForCountryStateTree);
        vBoxRight.setStyle(styles.getStyle2());

        gridPanePresetModification.addColumn(0, gridPaneLeft);
        gridPanePresetModification.addColumn(1, vBoxCenter);
        gridPanePresetModification.addColumn(2, vBoxRight);
        gridPaneLeft.setPrefWidth(370);
        gridPaneLeft.setMinWidth(370);
        vBoxCenter.setPrefWidth(300);
        vBoxRight.setPrefWidth(300);

        // default sizing
        setComboBoxWidths(comboBoxType);
        setComboBoxWidths(comboBoxMeasure);
        setComboBoxWidths(comboBoxModificationType);
        setComboBoxWidths(comboBoxPollutant);

        for (String option : MEASURE_OPTIONS) {
            comboBoxMeasure.getItems().add(option);
        }
        for (String option : POLLUTANT_OPTIONS) {
            comboBoxPollutant.getItems().add(option);
        }
        for (String option : vars.getTypesFromTechBnd()) {
            comboBoxType.getItems().add(option);
        }
        for (String option : MODIFICATION_TYPE_OPTIONS) {
            comboBoxModificationType.getItems().add(option);
        }
        for (String option : CONVERT_FROM_OPTIONS) {
            comboBoxConvertFrom.getItems().add(option);
        }
        comboBoxMeasure.getSelectionModel().selectFirst();
        comboBoxPollutant.getSelectionModel().selectFirst();
        comboBoxType.getSelectionModel().selectFirst();
        comboBoxModificationType.getSelectionModel().selectFirst();
        comboBoxConvertFrom.getSelectionModel().selectFirst();
        comboBoxPollutant.setDisable(true);
        comboBoxType.setDisable(true);
        labelConvertFrom.setVisible(false);
        comboBoxConvertFrom.setVisible(false);

        // Action
        comboBoxMeasure.setOnAction(e -> {
            if (comboBoxMeasure.getSelectionModel().getSelectedIndex() > 0) {
                comboBoxPollutant.setDisable(false);
                comboBoxType.setDisable(true);
            } else {
                comboBoxPollutant.setDisable(true);
                comboBoxType.setDisable(true);
            }
            comboBoxPollutant.getSelectionModel().selectFirst();
            comboBoxType.getSelectionModel().selectFirst();
            if (comboBoxMeasure.getSelectionModel().getSelectedItem().startsWith("Emission Tax")) {
                labelConvertFrom.setVisible(true);
                comboBoxConvertFrom.setVisible(true);
            } else {
                labelConvertFrom.setVisible(false);
                comboBoxConvertFrom.setVisible(false);
            }
            setPolicyAndMarketNames();
        });
        comboBoxPollutant.setOnAction(e -> {
            String selectedItem = comboBoxPollutant.getSelectionModel().getSelectedItem();
            if (!"Select One".equals(selectedItem)) {
                comboBoxType.setDisable(true);
                comboBoxType.getSelectionModel().select("All");
                if (selectedItem.startsWith("CO2")) {
                    comboBoxType.setDisable(false);
                    comboBoxType.getSelectionModel().selectFirst();
                }
            }
            setPolicyAndMarketNames();
        });
        comboBoxType.setOnAction(e -> setPolicyAndMarketNames());
        EventHandler<TreeModificationEvent> ev = new EventHandler<TreeModificationEvent>() {
            @Override
            public void handle(TreeModificationEvent ae) {
                ae.consume();
                setPolicyAndMarketNames();
            }
        };
        paneForCountryStateTree.addEventHandlerToAllLeafs(ev);
        checkBoxUseAutoNames.setOnAction(e -> {
            boolean selected = checkBoxUseAutoNames.isSelected();
            textFieldPolicyName.setDisable(selected);
            textFieldMarketName.setDisable(selected);
        });
        comboBoxModificationType.setOnAction(e -> {
            String selected = comboBoxModificationType.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            switch (selected) {
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
        buttonClear.setOnAction(e -> paneForComponentDetails.clearTable());
        buttonDelete.setOnAction(e -> paneForComponentDetails.deleteItemsFromTable());
        buttonPopulate.setOnAction(e -> {
            if (qaPopulate()) {
                double[][] values = calculateValues();
                paneForComponentDetails.setValues(values);
            }
        });
        setPolicyAndMarketNames();
        VBox tabLayout = new VBox();
        tabLayout.getChildren().addAll(gridPanePresetModification);
        this.setContent(tabLayout);
    }

    /**
     * Sets the widths for a ComboBox for consistency.
     * @param comboBox ComboBox to set widths for
     */
    private void setComboBoxWidths(ComboBox<String> comboBox) {
        comboBox.setMaxWidth(MAX_WIDTH);
        comboBox.setMinWidth(MIN_WIDTH);
        comboBox.setPrefWidth(PREF_WIDTH);
    }

    /**
     * Automatically sets the policy and market names based on current selections and options.
     * If auto-naming is enabled, updates the text fields accordingly.
     */
    private void setPolicyAndMarketNames() {
        if (checkBoxUseAutoNames.isSelected()) {
            String policy_type = "--";
            String pollutant = "--";
            String sector = "--";
            String state = "--";
            try {
                String s = comboBoxMeasure.getValue();
                if (s != null && s.contains("Tax")) policy_type = "Tax";
                if (s != null && s.contains("Cap")) policy_type = "Cap";
                s = comboBoxType.getValue();
                if (s != null && !s.startsWith("Select")) sector = s;
                s = comboBoxPollutant.getValue();
                if (s != null && !s.equals("Select One")) {
                    pollutant = utils.splitString(s, " ")[0];
                }
                String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(paneForCountryStateTree.getTree());
                if (listOfSelectedLeaves != null && listOfSelectedLeaves.length > 0) {
                    listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
                    String state_str = utils.returnAppendedString(listOfSelectedLeaves).replace(",", "");
                    if (state_str.length() < 9) {
                        state = state_str;
                    } else {
                        state = "Reg";
                    }
                }
                String name = policy_type + "_" + sector + "_" + pollutant + "_" + state;
                textFieldMarketName.setText(name + "_Mkt");
                textFieldPolicyName.setText(name);
            } catch (Exception e) {
                System.out.println("Error trying to auto-name market");
            }
        }
    }

    /**
     * Calculates the values matrix for the cap/tax table based on user input and modification type.
     * Applies currency conversion if needed.
     * @return 2D array of calculated values for each year/period
     */
    private double[][] calculateValues() {
        String calc_type = comboBoxModificationType.getSelectionModel().getSelectedItem();
        int start_year = Integer.parseInt(textFieldStartYear.getText());
        int end_year = Integer.parseInt(textFieldEndYear.getText());
        double initial_value = Double.parseDouble(textFieldInitialAmount.getText());
        double growth = Double.parseDouble(textFieldGrowth.getText());
        int period_length = Integer.parseInt(textFieldPeriodLength.getText());
        double factor = 1.0;
        String convertYear = comboBoxConvertFrom.getValue();
        if (!"None".equals(convertYear)) {
            factor = utils.getConversionFactor(convertYear, "1975$s");
        }
        return utils.calculateValues(calc_type, false, start_year, end_year, initial_value, growth, period_length, factor);
    }

    /**
     * Runnable implementation: triggers saving the scenario component.
     * Calls saveScenarioComponent().
     */
    @Override
    public void run() {
        // If called from a non-JavaFX thread, wrap in Platform.runLater
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::saveScenarioComponent);
        } else {
            saveScenarioComponent();
        }
    }

    /**
     * Saves the scenario component by generating metadata and CSV content.
     * Uses selected regions, pollutant, sector, and cap/tax values.
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
        if (!qaInputs()) {
            Thread.currentThread().interrupt();
        } else {
            int start_year = 2010;
            String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);
            listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
            String states = utils.returnAppendedString(listOfSelectedLeaves);
            String ID = utils.getUniqueString();
            String policy_name = textFieldPolicyName.getText() + ID;
            String market_name = textFieldMarketName.getText() + ID;
            filenameSuggestion = textFieldPolicyName.getText().replaceAll("/", "-").replaceAll(" ", "_") + ".csv";
            String sector = comboBoxType.getValue();
            String type = comboBoxMeasure.getValue();
            if (type.contains("Cap")) {
                type = "Cap";
            } else {
                type = "Tax";
            }
            String pol_selection = comboBoxPollutant.getSelectionModel().getSelectedItem().trim() + " ";
            String pol = pol_selection.substring(0, pol_selection.indexOf(" ")).trim();
            fileContent = getMetaDataContent(tree, market_name, policy_name);
            String fileContent2 = "";
            boolean generate_links = false;
            if (pol_selection.startsWith("CO2") && ("All".equals(sector)) && ("Cap".equals(type))) {
                saveScenarioComponentCO2Cap(listOfSelectedLeaves, pol_selection, sector, market_name, policy_name);
                return;
            } else if (!pol.startsWith("GHG")) {
                String pol_orig = pol;
                if (!"All".equals(sector)) {
                    pol = market_name;
                }
                fileContent += "INPUT_TABLE" + vars.getEol();
                fileContent += "Variable ID" + vars.getEol();
                if ("Cap".equals(type)) {
                    fileContent += "GLIMPSEEmissionCap" + vars.getEol() + vars.getEol();
                    fileContent += "region,pollutant,market,year,cap" + vars.getEol();
                } else if ("Tax".equals(type)) {
                    fileContent += "GLIMPSEEmissionTax" + vars.getEol() + vars.getEol();
                    fileContent += "region,pollutant,market,year,tax" + vars.getEol();
                }
                if (listOfSelectedLeaves != null && listOfSelectedLeaves.length > 0) {
                    String state = listOfSelectedLeaves[0];
                    ArrayList<String> data = paneForComponentDetails.getDataYrValsArrayList();
                    for (String data_str : data) {
                        data_str = data_str.replaceAll(" ", "");
                        fileContent += state + "," + pol + "," + market_name + "," + data_str + vars.getEol();
                    }
                }
                if (listOfSelectedLeaves != null && listOfSelectedLeaves.length > 1) {
                    fileContent += vars.getEol();
                    fileContent += "INPUT_TABLE" + vars.getEol();
                    fileContent += "Variable ID" + vars.getEol();
                    fileContent += "GLIMPSEEmissionMarket" + vars.getEol();
                    fileContent += vars.getEol();
                    fileContent += "region,pollutant,market" + vars.getEol();
                    for (int s = 1; s < listOfSelectedLeaves.length; s++) {
                        fileContent += listOfSelectedLeaves[s] + "," + pol + "," + market_name + vars.getEol();
                        double progress = (double) s / (listOfSelectedLeaves.length - 1);
                        updateProgressBar(progress);
                    }
                }
                if (!"All".equals(sector)) {
                    ArrayList<String> data = paneForComponentDetails.getDataYrValsArrayList();
                    String fileContent_nest = vars.getEol();
                    fileContent_nest += "INPUT_TABLE" + vars.getEol();
                    fileContent_nest += "Variable ID" + vars.getEol();
                    fileContent_nest += "GLIMPSEAddCO2Subspecies-Nest" + vars.getEol();
                    fileContent_nest += vars.getEol();
                    fileContent_nest += "region,supplysector,nesting-subsector,subsector,technology,year,pollutant" + vars.getEol();
                    int nest_count = 0;
                    String fileContent_nonest = vars.getEol();
                    fileContent_nonest += "INPUT_TABLE" + vars.getEol();
                    fileContent_nonest += "Variable ID" + vars.getEol();
                    fileContent_nonest += "GLIMPSEAddCO2Subspecies" + vars.getEol();
                    fileContent_nonest += vars.getEol();
                    fileContent_nonest += "region,supplysector,subsector,technology,year,pollutant" + vars.getEol();
                    int nonest_count = 0;
                    int max_year = 0;
                    for (String d : data) {
                        int year = Integer.parseInt(d.split(",")[0].trim());
                        if (year > max_year) max_year = year;
                    }
                    if (listOfSelectedLeaves != null && listOfSelectedLeaves.length > 0) {
                        for (String region : listOfSelectedLeaves) {
                            String[][] tech_list = vars.getTechInfo();
                            int cols = tech_list[0].length;
                            int rows = tech_list.length;
                            String sector_lwc = sector.toLowerCase();
                            for (int r = 0; r < rows; r++) {
                                String sector_r = tech_list[r][0];
                                String subsector_r = tech_list[r][1];
                                String tech_r = tech_list[r][2];
                                String cat_r = tech_list[r][cols - 1];
                                for (int y = start_year; y <= max_year; y += 5) {
                                    String cat_r_lwc = cat_r.toLowerCase();
                                    if (((sector_lwc.equals(cat_r_lwc))
                                            || ((sector_lwc.equals("industry-all")) || (sector_lwc.equals("ind-all"))) && (cat_r_lwc.startsWith("ind")))
                                            || ((sector_lwc.equals("trn-all")) && (cat_r_lwc.startsWith("trn")))) {
                                        String line = region + "," + sector_r + "," + subsector_r.replace("=>", ",") + "," + tech_r + "," + y + "," + pol + vars.getEol();
                                        if (subsector_r.contains("=>")) {
                                            fileContent_nest += line;
                                            nest_count++;
                                        } else {
                                            fileContent_nonest += line;
                                            nonest_count++;
                                        }
                                    }
                                }
                            }
                            double progress = (double) 1 / (listOfSelectedLeaves.length - 1);
                            updateProgressBar(progress);
                        }
                    }
                    if (nest_count > 0) fileContent += fileContent_nest;
                    if (nonest_count > 0) fileContent += fileContent_nonest;
                }
            } else {
                fileContent += "INPUT_TABLE" + vars.getEol();
                fileContent += "Variable ID" + vars.getEol();
                if ("Cap".equals(type)) {
                    fileContent += "GLIMPSEGHGEmissionCap" + vars.getEol();
                    fileContent += vars.getEol();
                    fileContent += "region,GHG-Policy,GHG-Market,year,cap" + vars.getEol();
                } else if ("Tax".equals(type)) {
                    fileContent += "GLIMPSEGHGEmissionTax" + vars.getEol();
                    fileContent += vars.getEol();
                    fileContent += "region,GHG-Policy,GHG-Market,year,tax" + vars.getEol();
                }
                if (listOfSelectedLeaves != null && listOfSelectedLeaves.length > 0) {
                    String state = listOfSelectedLeaves[0];
                    ArrayList<String> data = paneForComponentDetails.getDataYrValsArrayList();
                    for (String data_str : data) {
                        data_str = data_str.replace(" ", "");
                        fileContent += state + "," + policy_name + "," + market_name + "," + data_str + vars.getEol();
                    }
                }
                if (listOfSelectedLeaves != null && listOfSelectedLeaves.length > 1) {
                    fileContent += vars.getEol();
                    fileContent += "INPUT_TABLE" + vars.getEol();
                    fileContent += "Variable ID" + vars.getEol();
                    fileContent += "GLIMPSEEmissionMarket" + vars.getEol();
                    fileContent += vars.getEol();
                    fileContent += "region,pollutant,market" + vars.getEol();
                    for (int s = 1; s < listOfSelectedLeaves.length; s++) {
                        fileContent += listOfSelectedLeaves[s] + "," + policy_name + "," + market_name + vars.getEol();
                        double progress = (double) s / (listOfSelectedLeaves.length - 1);
                        updateProgressBar(progress);
                    }
                }
                fileContent2 += vars.getEol();
                fileContent2 += "INPUT_TABLE" + vars.getEol();
                fileContent2 += "Variable ID" + vars.getEol();
                fileContent2 += "GLIMPSELinkedGHGEmissionMarketP1" + vars.getEol();
                fileContent2 += vars.getEol();
                fileContent2 += "region,pollutant,GHG-market,GHG-Policy,price-adjust,demand-adjust,price-unit,output-unit" + vars.getEol();
                String[] GHGs = {"CO2", "CH4", "N2O", "C2F6", "CF4", "HFC125", "HFC134a", "HRC245fa", "SF6", "CH4_AWB", "CH4_AGR", "N2O_AWB", "N2O_AGR"};
                String[] price_adjust = {"1", "5.728", "84.55", "0", "0", "0", "0", "0", "0", "5.727", "5.727", "84.55", "84.55"};
                String[] demand_adjust = {"3.667", "21", "310", "9.2", "6.5", "2.8", "1.3", "1.03", "23.9", "21", "21", "310", "310"};
                String[] price_unit = {"1990$/tC", "1990$/GgCH4", "1990$/GgN2O", "1990$/MgC2F6", "1990$/MgCF4", "1990$/MgHFC125", "1990$/MgHFC13a", "1990$/MgHFC245fa", "1990$/MgSF6", "1990$/GgCH4", "1990$/GgCH4", "1990$/GgN2O", "1990$/GgN2O"};
                String[] output_unit = {"MtC", "TgCH4", "TgN2O", "GgC2F6", "GgCF4", "GgHFC125", "GgHFC134a", "GgHFC245fa", "GgSF6", "TgCH4", "TgCH4", "TgN2O", "TgN2O"};
                if (listOfSelectedLeaves != null) {
                    for (String state : listOfSelectedLeaves) {
                        for (int i = 0; i < GHGs.length; i++) {
                            if ((pol.equals("GHG")) || ((pol.equals("CO2")) && (GHGs[i].equals("CO2")))) {
                                fileContent2 += state + "," + GHGs[i] + "," + market_name + "," + policy_name + ","
                                        + price_adjust[i] + "," + demand_adjust[i] + "," + price_unit[i] + "," + output_unit[i] + vars.getEol();
                            }
                        }
                    }
                }
                if (listOfSelectedLeaves != null && listOfSelectedLeaves.length > 1) {
                    fileContent2 += vars.getEol();
                    fileContent2 += "INPUT_TABLE" + vars.getEol();
                    fileContent2 += "Variable ID" + vars.getEol();
                    fileContent2 += "GLIMPSELinkedGHGEmissionMarketP2" + vars.getEol();
                    fileContent2 += vars.getEol();
                    fileContent2 += "region,pollutant,GHG-market,GHG-Policy" + vars.getEol();
                    for (int s = 1; s < listOfSelectedLeaves.length; s++) {
                        for (int i = 0; i < GHGs.length; i++) {
                            if ((pol.equals("GHG")) || ((pol.equals("CO2")) && (GHGs[i].equals("CO2")))) {
                                String state = listOfSelectedLeaves[s];
                                fileContent2 += state + "," + GHGs[i] + "," + market_name + "," + policy_name + vars.getEol();
                            }
                        }
                        double progress = (double) s / (listOfSelectedLeaves.length - 1);
                        updateProgressBar(progress);
                    }
                }
            }
            if (fileContent2.length() > 0) fileContent += fileContent2;
        }
    }

    /**
     * Special implementation for CO2 cap policies, generating robust scenario files for complex scenarios.
     * @param listOfSelectedRegions Array of selected region names
     * @param pol Pollutant string
     * @param sector Sector string
     * @param market_name Market name
     * @param policy_name Policy name
     */
    private void saveScenarioComponentCO2Cap(String[] listOfSelectedRegions, String pol, String sector,
            String market_name, String policy_name) {
        fileContent += "INPUT_TABLE" + vars.getEol();
        fileContent += "Variable ID" + vars.getEol();
        fileContent += "GLIMPSEEmissionCap-PPS-P1" + vars.getEol() + vars.getEol();
        fileContent += "region,policy,policy-type,min-price,market,year,cap" + vars.getEol();
        if (listOfSelectedRegions != null && listOfSelectedRegions.length > 0) {
            for (String state : listOfSelectedRegions) {
                ArrayList<String> data = paneForComponentDetails.getDataYrValsArrayList();
                for (String data_str : data) {
                    data_str = data_str.replaceAll(" ", "");
                    fileContent += state + "," + policy_name + ",tax,1," + market_name + "," + data_str + vars.getEol();
                }
            }
        }
        String dmdAdj = "1";
        if (pol.contains("(MT CO2)")) dmdAdj = "3.667";
        pol = pol.substring(0, pol.indexOf(" ")).trim();
        if (listOfSelectedRegions != null && listOfSelectedRegions.length >= 1) {
            fileContent += vars.getEol();
            fileContent += "INPUT_TABLE" + vars.getEol();
            fileContent += "Variable ID" + vars.getEol();
            fileContent += "GLIMPSEEmissionCap-PPS-P2" + vars.getEol();
            fileContent += vars.getEol();
            fileContent += "region,linked-ghg-policy,price-adjust0,demand-adjust0,market,linked-policy,price-unit,output-unit,price-adjust1,demandAdjust1" + vars.getEol();
            for (String region : listOfSelectedRegions) {
                fileContent += region + "," + pol + ",0,0," + market_name + "," + policy_name + ",1990$/Tg,Tg,1," + dmdAdj + vars.getEol();
                double progress = (double) 1 / (listOfSelectedRegions.length - 1);
                updateProgressBar(progress);
            }
        }
    }

    /**
     * Returns metadata content for the scenario component file, including measure, pollutant, sector, regions, and table data.
     * @param tree TreeView of selected regions
     * @param market Market name
     * @param policy Policy name
     * @return Metadata string for file header
     */
    public String getMetaDataContent(TreeView<String> tree, String market, String policy) {
        StringBuilder rtn_str = new StringBuilder();
        rtn_str.append("########## Scenario Component Metadata ##########").append(vars.getEol());
        rtn_str.append("#Scenario component type: Pollutant Tax/Cap").append(vars.getEol());
        rtn_str.append("#Measure: ").append(comboBoxMeasure.getValue()).append(vars.getEol());
        rtn_str.append("#Pollutant: ").append(comboBoxPollutant.getValue()).append(vars.getEol());
        rtn_str.append("#Sector: ").append(comboBoxType.getValue()).append(vars.getEol());
        if (policy == null) market = textFieldPolicyName.getText();
        rtn_str.append("#Policy name: ").append(policy).append(vars.getEol());
        if (market == null) market = textFieldMarketName.getText();
        rtn_str.append("#Market name: ").append(market).append(vars.getEol());
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
     * Populates measure, pollutant, sector, regions, and table data from file content.
     * @param content List of file lines to load
     */
    @Override
    public void loadContent(ArrayList<String> content) {
        for (String line : content) {
            int pos = line.indexOf(":");
            if (line.startsWith("#") && (pos > -1)) {
                String param = line.substring(1, pos).trim().toLowerCase();
                String value = line.substring(pos + 1).trim();
                switch (param) {
                    case "measure":
                        comboBoxMeasure.setValue(value);
                        comboBoxMeasure.fireEvent(new ActionEvent());
                        break;
                    case "pollutant":
                        comboBoxPollutant.setValue(value);
                        comboBoxPollutant.fireEvent(new ActionEvent());
                        break;
                    case "sector":
                        comboBoxType.setValue(value);
                        comboBoxType.fireEvent(new ActionEvent());
                        break;
                    case "policy name":
                        textFieldPolicyName.setText(value);
                        textFieldPolicyName.fireEvent(new ActionEvent());
                        break;
                    case "market name":
                        textFieldMarketName.setText(value);
                        textFieldMarketName.fireEvent(new ActionEvent());
                        break;
                    case "regions":
                        String[] regions = utils.splitString(value, ",");
                        paneForCountryStateTree.selectNodes(regions);
                        break;
                    case "table data":
                        String[] s = utils.splitString(value, ",");
                        paneForComponentDetails.data.add(new DataPoint(s[0], s[1]));
                        break;
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
        return !textFieldStartYear.getText().isEmpty()
                && !textFieldEndYear.getText().isEmpty()
                && !textFieldInitialAmount.getText().isEmpty()
                && !textFieldGrowth.getText().isEmpty();
    }

    /**
     * Validates all required inputs before saving the scenario component.
     * Checks for at least one region, at least one table entry, and required selections.
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
            if (comboBoxMeasure.getSelectionModel().getSelectedItem().equals("Select One")) {
                message.append("Action comboBox must have a selection").append(vars.getEol());
                error_count++;
            }
            if (comboBoxType.getSelectionModel().getSelectedItem().equals("Select One")) {
                message.append("Sector comboBox must have a selection").append(vars.getEol());
                error_count++;
            }
            if (comboBoxPollutant.getSelectionModel().getSelectedItem().equals("Select One")) {
                message.append("Parameter comboBox must have a selection").append(vars.getEol());
                error_count++;
            }
            if (textFieldPolicyName.getText().isEmpty()) {
                message.append("A market name must be provided").append(vars.getEol());
                error_count++;
            }
        } catch (Exception e1) {
            error_count++;
            message.append("Error in QA of entries").append(vars.getEol());
        }
        if (error_count > 0) {
            if (error_count == 1) {
                utils.warningMessage(message.toString());
            } else if (error_count > 1) {
                utils.displayString(message.toString(), "Parsing Errors");
            }
        }
        return error_count == 0;
    }

    /**
     * Updates the progress bar in a thread-safe way.
     * @param progress Progress value between 0.0 and 1.0
     */
    private void updateProgressBar(double progress) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> progressBar.setProgress(progress));
        } else {
            progressBar.setProgress(progress);
        }
    }
}
