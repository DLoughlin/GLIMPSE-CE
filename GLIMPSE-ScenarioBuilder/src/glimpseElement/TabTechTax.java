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
 */
package glimpseElement;

import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.CheckComboBox;

import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.CheckBoxTreeItem.TreeModificationEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * TabTechTax provides the user interface and logic for creating and editing technology tax or subsidy policies
 * in the GLIMPSE Scenario Builder. This tab allows users to select sectors, filter technologies, specify policy
 * names, and configure market and tax/subsidy details.
 *
 * <p>
 * <b>Usage:</b> This class is instantiated as a tab in the scenario builder. It extends {@link PolicyTab} and implements {@link Runnable}.
 * </p>
 *
 * <p>
 * <b>Thread Safety:</b> This class is not thread-safe and should be used on the JavaFX Application Thread.
 * </p>
 */
public class TabTechTax extends PolicyTab implements Runnable {
    // === Constants for UI Texts and Options ===
    private static final double LABEL_WIDTH = 125;
    private static final double MAX_WIDTH = 175;
    private static final double MIN_WIDTH = 105;
    private static final double PREF_WIDTH = 175;
    private static final String LABEL_UNITS_DEFAULT = "1975$s per GJ";
    private static final String LABEL_UNITS_WARNING = "Warning - Units do not match!";
    private static final String LABEL_UNITS_PASSKM = "1990$ per veh-km";
    private static final String SELECT_ONE = "Select One";
    private static final String SELECT_ONE_OR_MORE = "Select One or More";
    private static final String ALL = "All";
    private static final String TAX = "Tax";
    private static final String SUBSIDY = "Subsidy";
    private static final String NONE = "None";
    private static final String[] TAX_OR_SUBSIDY_OPTIONS = {SELECT_ONE, TAX, SUBSIDY};
    private static final String[] MODIFICATION_TYPE_OPTIONS = {
            "Initial w/% Growth/yr", "Initial w/% Growth/pd",
            "Initial w/Delta/yr", "Initial w/Delta/pd", "Initial and Final"
    };
    private static final String[] CONVERT_FROM_OPTIONS = {
            NONE, "2023$s", "2020$s", "2015$s", "2010$s", "2005$s", "2000$s"
    };

    // --- Labels and Strings ---
    private static final String LABEL_POLICY = "Policy:";
    private static final String LABEL_MARKET = "Market:";
    private static final String LABEL_TECHS = "Tech(s): ";
    private static final String LABEL_SECTOR = "Sector:";
    private static final String LABEL_FILTER = "Filter:";
    private static final String LABEL_TYPE = "Type:";
    private static final String LABEL_START_YEAR = "Start Year:";
    private static final String LABEL_END_YEAR = "End Year:";
    private static final String LABEL_INITIAL_VAL = "Initial Val:";
    private static final String LABEL_FINAL_VAL = "Final Val:";
    private static final String LABEL_PERIOD_LENGTH = "Period Length:";
    private static final String LABEL_VALUES = "Values:";
    private static final String LABEL_POPULATE = "Populate:";
    private static final String LABEL_UNITS = "Units:";
    private static final String WARNING_UNITS_MISMATCH = "Warning - Units do not match!";
    private static final String UNIT_UNITLESS = "unitless";
    private static final String UNIT_YEARS = "years";
    private static final String UNIT_UNITLESS_CAPACITY = "Unitless";
    private static final String DEFAULT_START_YEAR = "2025";
    private static final String DEFAULT_END_YEAR = "2050";
    private static final String DEFAULT_PERIOD_LENGTH = "5";
    
    // === Static Fields ===
    public static String descriptionText = "";
    public static String runQueueStr = "Queue is empty.";

    // === UI Components ===
    private final GridPane gridPanePresetModification = new GridPane();
    private final ScrollPane scrollPaneLeft = new ScrollPane();
    private final GridPane gridPaneLeft = new GridPane();
    private final VBox vBoxCenter = new VBox();
    private final HBox hBoxHeaderCenter = new HBox();
    private final VBox vBoxRight = new VBox();
    private final HBox hBoxHeaderRight = new HBox();
    private final PaneForComponentDetails paneForComponentDetails = new PaneForComponentDetails();
    private final PaneForCountryStateTree paneForCountryStateTree = new PaneForCountryStateTree();

    // --- Left Column Components ---
    private final Label labelComboBoxSector = utils.createLabel("Sector: ", LABEL_WIDTH);
    private final Label labelFilter = utils.createLabel("Filter:", LABEL_WIDTH);
    private final TextField textFieldFilter = utils.createTextField();
    private final ComboBox<String> comboBoxSector = utils.createComboBoxString();
    private final Label labelCheckComboBoxTech = utils.createLabel("Tech(s): ", LABEL_WIDTH);
    private final CheckComboBox<String> checkComboBoxTech = utils.createCheckComboBox();
    private final Label labelComboBoxTaxOrSubsidy = utils.createLabel("Type: ", LABEL_WIDTH);
    private final ComboBox<String> comboBoxTaxOrSubsidy = utils.createComboBoxString();
    private final Label labelPolicyName = utils.createLabel("Policy: ", LABEL_WIDTH);
    private final TextField textFieldPolicyName = new TextField("");
    private final Label labelMarketName = utils.createLabel("Market: ", LABEL_WIDTH);
    private final TextField textFieldMarketName = new TextField("");
    private final Label labelUseAutoNames = utils.createLabel("Names: ", LABEL_WIDTH);
    private final CheckBox checkBoxUseAutoNames = utils.createCheckBox("Auto?");
    private final Label labelModificationType = utils.createLabel("Type: ", LABEL_WIDTH);
    private final ComboBox<String> comboBoxModificationType = utils.createComboBoxString();
    private final Label labelUnits = utils.createLabel("Units: ", LABEL_WIDTH);
    private final Label labelUnits2 = utils.createLabel(LABEL_UNITS_DEFAULT, 225.);
    private final Label labelStartYear = utils.createLabel("Start Year: ", LABEL_WIDTH);
    private final TextField textFieldStartYear = new TextField(DEFAULT_START_YEAR);
    private final Label labelEndYear = utils.createLabel("End Year: ", LABEL_WIDTH);
    private final TextField textFieldEndYear = new TextField(DEFAULT_END_YEAR);
    private final Label labelInitialAmount = utils.createLabel("Initial Val:   ", LABEL_WIDTH);
    private final TextField textFieldInitialAmount = utils.createTextField();
    private final Label labelGrowth = utils.createLabel("Growth (%): ", LABEL_WIDTH);
    private final TextField textFieldGrowth = utils.createTextField();
    private final Label labelPeriodLength = utils.createLabel("Period Length: ", LABEL_WIDTH);
    private final TextField textFieldPeriodLength = new TextField(DEFAULT_PERIOD_LENGTH);
    private final Label labelConvertFrom = utils.createLabel("Convert $s from: ", LABEL_WIDTH);
    private final ComboBox<String> comboBoxConvertFrom = utils.createComboBoxString();
    private final Label labelValue = utils.createLabel("Values: ");
    private final Button buttonPopulate = utils.createButton("Populate", styles.getBigButtonWidth(), null);
    private final Button buttonImport = utils.createButton("Import", styles.getBigButtonWidth(), null);
    private final Button buttonDelete = utils.createButton("Delete", styles.getBigButtonWidth(), null);
    private final Button buttonClear = utils.createButton("Clear", styles.getBigButtonWidth(), null);

    /**
     * Constructs the TabTechTax UI and logic.
     * @param title Tab title
     * @param stageX JavaFX stage
     */
    public TabTechTax(String title, Stage stageX) {
        this.setText(title);
        this.setStyle(styles.getFontStyle());
        checkBoxUseAutoNames.setSelected(true);
        textFieldPolicyName.setDisable(true);
        textFieldMarketName.setDisable(true);
        setupLeftColumn();
        setupCenterColumn();
        setupRightColumn();
        setupLayout();
        setupSizing();
        setupComboBoxSector();
        comboBoxSector.getSelectionModel().selectFirst();
        checkComboBoxTech.getItems().add(SELECT_ONE_OR_MORE);
        checkComboBoxTech.getCheckModel().check(0);
        checkComboBoxTech.setDisable(true);
        for (String option : TAX_OR_SUBSIDY_OPTIONS) {
            comboBoxTaxOrSubsidy.getItems().add(option);
        }
        comboBoxTaxOrSubsidy.getSelectionModel().selectFirst();
        for (String option : MODIFICATION_TYPE_OPTIONS) {
            comboBoxModificationType.getItems().add(option);
        }
        comboBoxModificationType.getSelectionModel().selectFirst();
        for (String option : CONVERT_FROM_OPTIONS) {
            comboBoxConvertFrom.getItems().add(option);
        }
        comboBoxConvertFrom.getSelectionModel().selectFirst();
        setupEventHandlers();
        setPolicyAndMarketNames();
        setUnitsLabel();
        VBox tabLayout = new VBox();
        tabLayout.getChildren().addAll(gridPanePresetModification);
        this.setContent(tabLayout);
    }

    // === UI Setup Methods ===
    private void setupLeftColumn() {
        gridPaneLeft.add(utils.createLabel("Specification:"), 0, 0, 2, 1);
        gridPaneLeft.addColumn(0, labelFilter, labelComboBoxSector, labelCheckComboBoxTech, labelComboBoxTaxOrSubsidy,
                new Label(), labelUnits, new Label(), new Separator(), labelUseAutoNames, labelPolicyName,
                labelMarketName, new Label(), new Separator(), utils.createLabel("Populate:"), labelModificationType,
                labelStartYear, labelEndYear, labelInitialAmount, labelGrowth, labelConvertFrom);
        gridPaneLeft.addColumn(1, textFieldFilter, comboBoxSector, checkComboBoxTech, comboBoxTaxOrSubsidy, new Label(),
                labelUnits2, new Label(), new Separator(), checkBoxUseAutoNames, textFieldPolicyName,
                textFieldMarketName, new Label(), new Separator(), new Label(), comboBoxModificationType,
                textFieldStartYear, textFieldEndYear, textFieldInitialAmount, textFieldGrowth, comboBoxConvertFrom);
        gridPaneLeft.setVgap(3.);
        gridPaneLeft.setStyle(styles.getStyle2());
        scrollPaneLeft.setContent(gridPaneLeft);
    }

    private void setupCenterColumn() {
        hBoxHeaderCenter.getChildren().addAll(buttonPopulate, buttonDelete, buttonClear);
        hBoxHeaderCenter.setSpacing(2.);
        hBoxHeaderCenter.setStyle(styles.getStyle3());
        vBoxCenter.getChildren().addAll(labelValue, hBoxHeaderCenter, paneForComponentDetails);
        vBoxCenter.setStyle(styles.getStyle2());
    }

    private void setupRightColumn() {
        vBoxRight.getChildren().addAll(paneForCountryStateTree);
        vBoxRight.setStyle(styles.getStyle2());
    }

    private void setupLayout() {
        gridPanePresetModification.addColumn(0, scrollPaneLeft);
        gridPanePresetModification.addColumn(1, vBoxCenter);
        gridPanePresetModification.addColumn(2, vBoxRight);
        gridPaneLeft.setPrefWidth(325);
        gridPaneLeft.setMinWidth(325);
        vBoxCenter.setPrefWidth(300);
        vBoxRight.setPrefWidth(300);
    }

    private void setupSizing() {
        setComponentWidths(comboBoxSector, checkComboBoxTech, comboBoxTaxOrSubsidy, comboBoxConvertFrom,
                textFieldStartYear, textFieldEndYear, textFieldInitialAmount, textFieldGrowth, textFieldPeriodLength,
                textFieldFilter, textFieldPolicyName, textFieldMarketName);
    }

    private void setComponentWidths(Control... controls) {
        for (Control c : controls) {
            c.setMaxWidth(MAX_WIDTH);
            c.setMinWidth(MIN_WIDTH);
            c.setPrefWidth(PREF_WIDTH);
        }
    }

    private void setupEventHandlers() {
        labelCheckComboBoxTech.setOnMouseClicked(e -> {
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
        });
        comboBoxSector.setOnAction(e -> {
            String selectedItem = comboBoxSector.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                if (selectedItem.equals(SELECT_ONE)) {
                    checkComboBoxTech.getItems().clear();
                    checkComboBoxTech.getItems().add(SELECT_ONE_OR_MORE);
                    checkComboBoxTech.getCheckModel().check(0);
                    checkComboBoxTech.setDisable(true);
                } else {
                    checkComboBoxTech.setDisable(false);
                    updateCheckComboBoxTech();
                }
            }
            setPolicyAndMarketNames();
        });
        checkComboBoxTech.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> {
            while (c.next()) {
                setUnitsLabel();
            }
        });
        comboBoxTaxOrSubsidy.setOnAction(e -> setPolicyAndMarketNames());
        EventHandler<TreeModificationEvent<String>> ev = ae -> {
            ae.consume();
            setPolicyAndMarketNames();
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
        textFieldFilter.setOnAction(e -> setupComboBoxSector());
    }

    /**
     * Sets up the sector ComboBox with available sectors, applying any filter entered by the user.
     */
    private void setupComboBoxSector() {
        comboBoxSector.getItems().clear();
        try {
            String[][] techInfo = vars.getTechInfo();
            List<String> sectorList = new ArrayList<>();
            String filterText = textFieldFilter.getText() != null ? textFieldFilter.getText().trim() : "";
            boolean useFilter = !filterText.isEmpty();
            if (!useFilter) sectorList.add(SELECT_ONE);
            sectorList.add(ALL);
            for (String[] tech : techInfo) {
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
            comboBoxSector.getSelectionModel().select(0);
        } catch (Exception e) {
            utils.warningMessage("Problem reading tech list.");
            System.out.println("Error reading tech list from " + vars.getTchBndListFilename() + ":");
            System.out.println("  ---> " + e);
        }
    }

    /**
     * Updates the technology CheckComboBox based on the selected sector and filter.
     */
    private void updateCheckComboBoxTech() {
        String sector = comboBoxSector.getValue();
        String[][] techInfo = vars.getTechInfo();
        boolean isAllSectors = ALL.equals(sector);
        try {
            if (!checkComboBoxTech.getItems().isEmpty()) {
                checkComboBoxTech.getCheckModel().clearChecks();
                checkComboBoxTech.getItems().clear();
            }
            if (sector != null) {
                String lastLine = "";
                String filterText = textFieldFilter.getText() != null ? textFieldFilter.getText().trim() : "";
                for (String[] tech : techInfo) {
                    String lineSector = tech[0].trim();
                    String line = lineSector + " : " + tech[1] + " : " + tech[2];
                    if (filterText.isEmpty() || line.contains(filterText)) {
                        if (tech.length >= 7) line += " : " + tech[6];
                        if (!line.equals(lastLine)) {
                            lastLine = line;
                            if (isAllSectors || lineSector.equals(sector)) {
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
     * Sets the policy and market names automatically based on current selections if auto-naming is enabled.
     */
    private void setPolicyAndMarketNames() {
        if (checkBoxUseAutoNames.isSelected()) {
            String policyType = "---";
            String technology = "Tech";
            String sector = "---";
            String state = "--";
            try {
                String s = comboBoxTaxOrSubsidy.getValue();
                if (s != null && s.contains(TAX)) policyType = TAX;
                if (s != null && s.contains(SUBSIDY)) policyType = SUBSIDY;
                s = comboBoxSector.getValue();
                if (s != null && !s.equals(SELECT_ONE)) {
                    s = s.replace(" ", "_");
                    s = utils.capitalizeOnlyFirstLetterOfString(s);
                    sector = s;
                }
                String[] selectedLeaves = utils.getAllSelectedRegions(paneForCountryStateTree.getTree());
                if (selectedLeaves.length > 0) {
                    selectedLeaves = utils.removeUSADuplicate(selectedLeaves);
                    String stateStr = utils.returnAppendedString(selectedLeaves).replace(",", "");
                    state = stateStr.length() < 9 ? stateStr : "Reg";
                }
                String name = policyType + sector + technology + state;
                name = name.replaceAll(" ", "_").replaceAll("--", "-");
                textFieldMarketName.setText(name + "_Mkt");
                textFieldPolicyName.setText(name);
            } catch (Exception e) {
                System.out.println("Cannot auto-name market. Continuing.");
            }
        }
    }

    /**
     * Calculates the values for the policy based on user input and conversion factors.
     *
     * @return a 2D array of calculated values
     */
    private double[][] calculateValues() {
        String calcType = comboBoxModificationType.getSelectionModel().getSelectedItem();
        int startYear = Integer.parseInt(textFieldStartYear.getText());
        int endYear = Integer.parseInt(textFieldEndYear.getText());
        double initialValue = Double.parseDouble(textFieldInitialAmount.getText());
        double growth = Double.parseDouble(textFieldGrowth.getText());
        int periodLength = Integer.parseInt(textFieldPeriodLength.getText());
        double factor = 1.0;
        String convertYear = comboBoxConvertFrom.getValue();
        String tempUnitsVal = labelUnits2.getText();
        String toYear = tempUnitsVal.contains("1990") ? "1990$s" : "1975$s";
        if (!NONE.equals(convertYear)) {
            factor = utils.getConversionFactor(convertYear, toYear);
        }
        return utils.calculateValues(calcType, startYear, endYear, initialValue, growth, periodLength, factor);
    }

    /**
     * Runs background tasks or updates for this tab. Implementation of Runnable interface.
     */
    @Override
    public void run() {
        saveScenarioComponent();
    }

    /**
     * Saves the scenario component using the current country/state tree selection.
     */
    @Override
    public void saveScenarioComponent() {
        saveScenarioComponent(paneForCountryStateTree.getTree());
    }

    /**
     * Saves the scenario component using the provided tree.
     *
     * @param tree the TreeView containing region selections
     */
    private void saveScenarioComponent(TreeView<String> tree) {
        if (!qaInputs()) {
            Thread.currentThread().destroy();
            return;
        } else {
            String[] listOfSelectedLeaves = utils.getAllSelectedRegions(tree);
            listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
            String states = utils.returnAppendedString(listOfSelectedLeaves);
            filenameSuggestion = "";
            String taxOrSubsidy = comboBoxTaxOrSubsidy.getSelectionModel().getSelectedItem().trim().toLowerCase();
            String ID = utils.getUniqueString();
            String policyName = this.textFieldPolicyName.getText() + ID;
            String marketName = this.textFieldMarketName.getText() + ID;
            filenameSuggestion = this.textFieldPolicyName.getText().replaceAll("/", "-").replaceAll(" ", "_") + ".csv";
            fileContent = getMetaDataContent(paneForCountryStateTree.getTree(), marketName, policyName);
            for (int iter = 0; iter < 2; iter++) {
                String iterType = (iter == 0) ? "Std" : "Tran";
                String which = "tax";
                String headerPart1 = "GLIMPSEPF" + iterType + "TechTaxP1";
                String headerPart2 = "GLIMPSEPF" + iterType + "TechTaxP2";
                String headerPart3 = "GLIMPSEPF" + iterType + "TechTaxP3";
                if (taxOrSubsidy.equals("subsidy")) {
                    which = "subsidy";
                    headerPart1 = "GLIMPSEPF" + iterType + "TechSubsidyP1";
                    headerPart2 = "GLIMPSEPF" + iterType + "TechSubsidyP2";
                    headerPart3 = "GLIMPSEPF" + iterType + "TechSubsidyP3";
                }
                ObservableList<String> techLines = checkComboBoxTech.getCheckModel().getCheckedItems();
                for (String techLine : techLines) {
                    String[] temp = utils.splitString(techLine.trim(), ":");
                    String sector = temp[0].trim();
                    String subsector = temp[1].trim();
                    String tech = temp[2].trim();
                    if (((iter == 0) && (!sector.startsWith("trn"))) || ((iter == 1) && (sector.startsWith("trn")))) {
                        // part 1
                        fileContent += "INPUT_TABLE" + vars.getEol();
                        fileContent += "Variable ID" + vars.getEol();
                        if (subsector.indexOf("=>") > -1) {
                            fileContent += headerPart1 + "-Nest" + vars.getEol() + vars.getEol();
                            fileContent += "region,sector,nesting-subsector,subsector,tech,year,policy-name" + vars.getEol();
                            subsector = subsector.replace("=>", ",");
                        } else {
                            fileContent += headerPart1 + vars.getEol() + vars.getEol();
                            fileContent += "region,sector,subsector,tech,year,policy-name" + vars.getEol();
                        }
                        for (String state : listOfSelectedLeaves) {
                            ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();
                            for (String dataStr : data) {
                                String year = utils.splitString(dataStr.replace(" ", ""), ",")[0];
                                fileContent += state + "," + sector + "," + subsector + "," + tech + "," + year + "," + policyName + vars.getEol();
                            }
                        }
                        // part 2
                        fileContent += vars.getEol();
                        fileContent += "INPUT_TABLE" + vars.getEol();
                        fileContent += "Variable ID" + vars.getEol();
                        fileContent += headerPart2 + vars.getEol() + vars.getEol();
                        fileContent += "region,policy-name,market,type,policy-yr,policy-val" + vars.getEol();
                        if (listOfSelectedLeaves.length > 0) {
                            String state = listOfSelectedLeaves[0];
                            ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();
                            for (String dataStr : data) {
                                String[] split = utils.splitString(dataStr.replace(" ", ""), ",");
                                String year = split[0];
                                String val = split[1];
                                fileContent += state + "," + policyName + "," + marketName + "," + which + "," + year + "," + val + vars.getEol();
                            }
                        }
                        // part 3
                        fileContent += vars.getEol();
                        fileContent += "INPUT_TABLE" + vars.getEol();
                        fileContent += "Variable ID" + vars.getEol();
                        fileContent += headerPart3 + vars.getEol() + vars.getEol();
                        fileContent += "region,policy-name,market,type" + vars.getEol();
                        for (String state : listOfSelectedLeaves) {
                            fileContent += state + "," + policyName + "," + marketName + "," + which + vars.getEol();
                        }
                        fileContent += vars.getEol();
                    }
                }
            }
        }
    }

    /**
     * Generates metadata content for the scenario component, including selected technologies, type, policy, market, regions, and table data.
     *
     * @param tree   the TreeView containing region selections
     * @param market the market name
     * @param policy the policy name
     * @return a String containing the metadata content
     */
    public String getMetaDataContent(TreeView<String> tree, String market, String policy) {
        StringBuilder rtnStr = new StringBuilder();
        rtnStr.append("########## Scenario Component Metadata ##########").append(vars.getEol());
        rtnStr.append("#Scenario component type: Tech Tax/Subsidy").append(vars.getEol());
        ObservableList<String> techList = checkComboBoxTech.getCheckModel().getCheckedItems();
        String techs = utils.getStringFromList(techList, ";");
        rtnStr.append("#Technologies: ").append(techs).append(vars.getEol());
        rtnStr.append("#Type: ").append(comboBoxModificationType.getSelectionModel().getSelectedItem()).append(vars.getEol());
        if (policy == null) market = textFieldPolicyName.getText();
        rtnStr.append("#Policy name: ").append(policy).append(vars.getEol());
        if (market == null) market = textFieldMarketName.getText();
        rtnStr.append("#Market name: ").append(market).append(vars.getEol());
        String[] listOfSelectedLeaves = utils.getAllSelectedRegions(tree);
        listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
        String states = utils.returnAppendedString(listOfSelectedLeaves);
        rtnStr.append("#Regions: ").append(states).append(vars.getEol());
        ArrayList<String> tableContent = this.paneForComponentDetails.getDataYrValsArrayList();
        for (String row : tableContent) {
            rtnStr.append("#Table data:").append(row).append(vars.getEol());
        }
        rtnStr.append("#################################################").append(vars.getEol());
        return rtnStr.toString();
    }

    /**
     * Loads content from a list of strings into the tab, updating UI components accordingly.
     *
     * @param content the list of content lines to load
     */
    @Override
    public void loadContent(ArrayList<String> content) {
        for (String line : content) {
            int pos = line.indexOf(":");
            if (line.startsWith("#") && (pos > -1)) {
                String param = line.substring(1, pos).trim().toLowerCase();
                String value = line.substring(pos + 1).trim();
                switch (param) {
                    case "sector":
                        comboBoxSector.setValue(value);
                        comboBoxSector.fireEvent(new ActionEvent());
                        break;
                    case "technologies":
                        checkComboBoxTech.getCheckModel().clearChecks();
                        String[] set = utils.splitString(value, ";");
                        for (String item : set) {
                            checkComboBoxTech.getCheckModel().check(item.trim());
                            checkComboBoxTech.fireEvent(new ActionEvent());
                        }
                        break;
                    case "type":
                        comboBoxModificationType.setValue(value);
                        comboBoxModificationType.fireEvent(new ActionEvent());
                        break;
                    case "regions":
                        String[] regions = utils.splitString(value, ",");
                        this.paneForCountryStateTree.selectNodes(regions);
                        break;
                    case "table data":
                        String[] s = utils.splitString(value, ",");
                        this.paneForComponentDetails.data.add(new DataPoint(s[0], s[1]));
                        break;
                }
            }
        }
        this.paneForComponentDetails.updateTable();
    }

    /**
     * Checks if the required fields for populating values are filled in.
     *
     * @return true if all required fields are filled, false otherwise
     */
    public boolean qaPopulate() {
        return !textFieldStartYear.getText().isEmpty() &&
                !textFieldEndYear.getText().isEmpty() &&
                !textFieldInitialAmount.getText().isEmpty() &&
                !textFieldGrowth.getText().isEmpty();
    }

    /**
     * Performs quality assurance checks on user inputs and displays warnings if any issues are found.
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
            if (paneForComponentDetails.table.getItems().size() == 0) {
                message.append("Data table must have at least one entry").append(vars.getEol());
                errorCount++;
            } else {
                boolean match = false;
                String listOfAllowableYears = vars.getAllowablePolicyYears();
                ObservableList<DataPoint> data = this.paneForComponentDetails.table.getItems();
                for (DataPoint dp : data) {
                    String year = dp.getYear().trim();
                    if (listOfAllowableYears.contains(year)) match = true;
                }
                if (!match) {
                    message.append("Years specified in table must match allowable policy years (")
                            .append(listOfAllowableYears).append(")").append(vars.getEol());
                    errorCount++;
                }
            }
            if (comboBoxSector.getSelectionModel().getSelectedItem().equals(SELECT_ONE)) {
                message.append("Sector comboBox must have a selection").append(vars.getEol());
                errorCount++;
            }
            if (checkComboBoxTech.getCheckModel().getCheckedItems().size() <= 0) {
                message.append("Tech checkComboBox must have a selection").append(vars.getEol());
                errorCount++;
            }
            if (comboBoxTaxOrSubsidy.getSelectionModel().getSelectedItem().equals(SELECT_ONE)) {
                message.append("Type comboBox must have a selection").append(vars.getEol());
                errorCount++;
            }
            if (textFieldPolicyName.getText().equals("")) {
                message.append("A policy name must be provided").append(vars.getEol());
                errorCount++;
            }
            if (textFieldMarketName.getText().equals("")) {
                message.append("A market name must be provided").append(vars.getEol());
                errorCount++;
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

    /**
     * Sets the units label based on the selected technologies and their units.
     */
    public void setUnitsLabel() {
        String s = getUnits();
        String label;
        switch (s) {
            case "No match":
                label = LABEL_UNITS_WARNING;
                break;
            case "million pass-km":
            case "million ton-km":
                label = LABEL_UNITS_PASSKM;
                break;
            case "":
                label = "";
                break;
            default:
                String s2 = "GJ";
                if (s.equals("EJ")) s2 = "GJ";
                if (s.equals("petalumen-hours")) s2 = "megalumen-hours";
                if (s.equals("million km3")) s2 = "million m3";
                if (s.equals("billion cycles")) s2 = "cycle";
                if (s.equals("Mt")) s2 = "kg";
                if (s.equals("km^3")) s2 = "m^3";
                label = "1975$s per " + s2;
        }
        labelUnits2.setText(label);
    }

    /**
     * Gets the units for the currently selected technologies.
     *
     * @return the units as a String, or "No match" if units differ
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
        if (unit.trim().equals(SELECT_ONE_OR_MORE)) unit = "";
        return unit;
    }
}
