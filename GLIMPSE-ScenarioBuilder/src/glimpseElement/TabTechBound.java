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

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;

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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.CheckBoxTreeItem.TreeModificationEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * TabTechBound provides the user interface and logic for creating and editing technology bound policies
 * in the GLIMPSE Scenario Builder. This tab allows users to select sectors, filter technologies, specify constraints,
 * and configure policy details for scenario components.
 *
 * <p>
 * <b>Usage:</b> This class is instantiated as a tab in the scenario builder. It extends {@link PolicyTab} and implements {@link Runnable}.
 * </p>
 *
 * <p>
 * <b>Thread Safety:</b> This class is not thread-safe and should be used on the JavaFX Application Thread.
 * </p>
 */
public class TabTechBound extends PolicyTab implements Runnable {
    // === Constants for UI text and options ===
    private static final double LABEL_WIDTH = 125;
    private static final double LABEL_UNITS2_WIDTH = 225.0;
    private static final double MAX_WIDTH = 175;
    private static final double MIN_WIDTH = 105;
    private static final double PREF_WIDTH = 175;
    private static final String LABEL_FILTER = "Filter:";
    private static final String LABEL_SECTOR = "Sector: ";
    private static final String LABEL_TECHS = "Tech(s): ";
    private static final String LABEL_CONSTRAINT = "Constraint: ";
    private static final String LABEL_TREATMENT = "Treatment: ";
    private static final String LABEL_POLICY = "Policy: ";
    private static final String LABEL_MARKET = "Market: ";
    private static final String LABEL_NAMES = "Names: ";
    private static final String LABEL_TYPE = "Type: ";
    private static final String LABEL_UNITS = "Units: ";
    private static final String LABEL_START_YEAR = "Start Year: ";
    private static final String LABEL_END_YEAR = "End Year: ";
    private static final String LABEL_INITIAL_VAL = "Initial Val:   ";
    private static final String LABEL_FINAL_VAL = "Final Val: ";
    private static final String LABEL_PERIOD_LENGTH = "Period Length: ";
    private static final String LABEL_VALUES = "Values: ";
    private static final String LABEL_POPULATE = "Populate:";
    private static final String LABEL_WARNING_UNITS = "Warning - Units do not match!";
    private static final String UNITS_DEFAULT = "EJ";
    private static final String SELECT_ONE = "Select One";
    private static final String SELECT_ONE_OR_MORE = "Select One or More";
    private static final String ALL = "All";
    private static final String[] TREATMENT_OPTIONS = {"Select One", "Each Selected Region", "Across Selected Regions"};
    private static final String[] CONSTRAINT_OPTIONS = {"Select One", "Upper", "Lower", "Fixed"};
    private static final String[] MODIFICATION_TYPE_OPTIONS = {
            "Initial and Final", "Initial w/% Growth/yr",
            "Initial w/% Growth/pd", "Initial w/Delta/yr", "Initial w/Delta/pd"
    };

    // === UI Components ===
    private final GridPane gridPanePresetModification = new GridPane();
    private final GridPane gridPaneLeft = new GridPane();
    private final ScrollPane scrollPaneLeft = new ScrollPane();
    private final Label labelFilter = utils.createLabel(LABEL_FILTER, LABEL_WIDTH);
    private final TextField textFieldFilter = utils.createTextField();
    private final Label labelComboBoxSector = utils.createLabel(LABEL_SECTOR, LABEL_WIDTH);
    private final ComboBox<String> comboBoxSector = utils.createComboBoxString();
    private final Label labelCheckComboBoxTech = utils.createLabel(LABEL_TECHS, LABEL_WIDTH);
    private final CheckComboBox<String> checkComboBoxTech = utils.createCheckComboBox();
    private final Label labelComboBoxConstraint = utils.createLabel(LABEL_CONSTRAINT, LABEL_WIDTH);
    private final ComboBox<String> comboBoxConstraint = utils.createComboBoxString();
    private final Label labelTreatment = utils.createLabel(LABEL_TREATMENT, LABEL_WIDTH);
    private final ComboBox<String> comboBoxTreatment = utils.createComboBoxString();
    private final Label labelPolicyName = utils.createLabel(LABEL_POLICY, LABEL_WIDTH);
    private final TextField textFieldPolicyName = new TextField("");
    private final Label labelMarketName = utils.createLabel(LABEL_MARKET, LABEL_WIDTH);
    private final TextField textFieldMarketName = new TextField("");
    private final Label labelUseAutoNames = utils.createLabel(LABEL_NAMES, LABEL_WIDTH);
    private final CheckBox checkBoxUseAutoNames = utils.createCheckBox("Auto?");
    private final Label labelModificationType = utils.createLabel(LABEL_TYPE, LABEL_WIDTH);
    private final ComboBox<String> comboBoxModificationType = utils.createComboBoxString();
    private final Label labelUnits = utils.createLabel(LABEL_UNITS, LABEL_WIDTH);
    private final Label labelUnits2 = utils.createLabel(UNITS_DEFAULT, LABEL_UNITS2_WIDTH);
    private final Label labelStartYear = utils.createLabel(LABEL_START_YEAR, LABEL_WIDTH);
    private final TextField textFieldStartYear = new TextField("2020");
    private final Label labelEndYear = utils.createLabel(LABEL_END_YEAR, LABEL_WIDTH);
    private final TextField textFieldEndYear = new TextField("2050");
    private final Label labelInitialAmount = utils.createLabel(LABEL_INITIAL_VAL, LABEL_WIDTH);
    private final TextField textFieldInitialAmount = utils.createTextField();
    private final Label labelGrowth = utils.createLabel(LABEL_FINAL_VAL, LABEL_WIDTH);
    private final TextField textFieldGrowth = utils.createTextField();
    private final Label labelPeriodLength = utils.createLabel(LABEL_PERIOD_LENGTH, LABEL_WIDTH);
    private final TextField textFieldPeriodLength = new TextField("5");
    private final VBox vBoxCenter = new VBox();
    private final HBox hBoxHeaderCenter = new HBox();
    private final Label labelValue = utils.createLabel(LABEL_VALUES);
    private final Button buttonPopulate = utils.createButton("Populate", styles.getBigButtonWidth(), null);
    private final Button buttonImport = utils.createButton("Import", styles.getBigButtonWidth(), null);
    private final Button buttonDelete = utils.createButton("Delete", styles.getBigButtonWidth(), null);
    private final Button buttonClear = utils.createButton("Clear", styles.getBigButtonWidth(), null);
    private final PaneForComponentDetails paneForComponentDetails = new PaneForComponentDetails();
    private final HBox hBoxHeaderRight = new HBox();
    private final VBox vBoxRight = new VBox();
    private final PaneForCountryStateTree paneForCountryStateTree = new PaneForCountryStateTree();

    /**
     * Constructs a new TabTechBound instance and initializes the UI components for the technology bound tab.
     * Sets up event handlers and populates controls with available data.
     *
     * @param title The title of the tab
     * @param stageX The JavaFX stage
     */
    public TabTechBound(String title, Stage stageX) {
        this.setText(title);
        this.setStyle(styles.getFontStyle());
        checkBoxUseAutoNames.setSelected(true);
        textFieldPolicyName.setDisable(true);
        textFieldMarketName.setDisable(true);
        setupLeftColumn();
        setupComboBoxSector();
        setupTechComboBox();
        setupComboBoxOptions();
        setupSizing();
        setupEventHandlers();
        setPolicyAndMarketNames();
        setUnitsLabel();
        VBox tabLayout = new VBox();
        tabLayout.getChildren().addAll(gridPanePresetModification);
        this.setContent(tabLayout);
    }

    /**
     * Sets up the left column UI layout.
     */
    private void setupLeftColumn() {
        gridPaneLeft.add(utils.createLabel("Specification:"), 0, 0, 2, 1);
        gridPaneLeft.addColumn(0, labelFilter, labelComboBoxSector, labelCheckComboBoxTech, labelComboBoxConstraint,
                labelTreatment, new Label(), labelUnits, new Label(), new Separator(), labelUseAutoNames, labelPolicyName, labelMarketName,
                new Label(), new Separator(), utils.createLabel(LABEL_POPULATE), labelModificationType, labelStartYear,
                labelEndYear, labelInitialAmount, labelGrowth);
        gridPaneLeft.addColumn(1, textFieldFilter, comboBoxSector, checkComboBoxTech, comboBoxConstraint,
                comboBoxTreatment, new Label(), labelUnits2, new Label(), new Separator(), checkBoxUseAutoNames, textFieldPolicyName,
                textFieldMarketName, new Label(), new Separator(), new Label(), comboBoxModificationType,
                textFieldStartYear, textFieldEndYear, textFieldInitialAmount, textFieldGrowth);
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
    }

    /**
     * Sets up the technology combo box with default values.
     */
    private void setupTechComboBox() {
        checkComboBoxTech.getItems().add(SELECT_ONE_OR_MORE);
        checkComboBoxTech.getCheckModel().checkAll();
        checkComboBoxTech.setDisable(true);
    }

    /**
     * Sets up combo box options for treatment, constraint, and modification type.
     */
    private void setupComboBoxOptions() {
        comboBoxTreatment.getItems().addAll(TREATMENT_OPTIONS);
        comboBoxTreatment.getSelectionModel().select("Each Selected Region");
        comboBoxConstraint.getItems().addAll(CONSTRAINT_OPTIONS);
        comboBoxModificationType.getItems().addAll(MODIFICATION_TYPE_OPTIONS);
        comboBoxSector.getSelectionModel().selectFirst();
        comboBoxConstraint.getSelectionModel().selectFirst();
        comboBoxModificationType.getSelectionModel().selectFirst();
    }

    /**
     * Sets up sizing for UI components.
     */
    private void setupSizing() {
        ComboBox<?>[] comboBoxes = {comboBoxSector, comboBoxModificationType, comboBoxConstraint, comboBoxTreatment};
        for (ComboBox<?> cb : comboBoxes) {
            cb.setMaxWidth(MAX_WIDTH);
            cb.setMinWidth(MIN_WIDTH);
            cb.setPrefWidth(PREF_WIDTH);
        }
        checkComboBoxTech.setMaxWidth(MAX_WIDTH);
        checkComboBoxTech.setMinWidth(MIN_WIDTH);
        checkComboBoxTech.setPrefWidth(PREF_WIDTH);
        TextField[] textFields = {textFieldStartYear, textFieldEndYear, textFieldInitialAmount, textFieldGrowth, textFieldPeriodLength, textFieldPolicyName, textFieldMarketName, textFieldFilter};
        for (TextField tf : textFields) {
            tf.setMaxWidth(MAX_WIDTH);
            tf.setMinWidth(MIN_WIDTH);
            tf.setPrefWidth(PREF_WIDTH);
        }
    }

    /**
     * Sets up event handlers for UI components.
     */
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
        comboBoxConstraint.setOnAction(e -> setPolicyAndMarketNames());
        comboBoxTreatment.setOnAction(e -> setPolicyAndMarketNames());
        EventHandler<TreeModificationEvent> ev = ae -> {
            ae.consume();
            setPolicyAndMarketNames();
        };
        paneForCountryStateTree.addEventHandlerToAllLeafs(ev);
        checkBoxUseAutoNames.setOnAction(e -> {
            boolean selected = checkBoxUseAutoNames.isSelected();
            textFieldPolicyName.setDisable(selected);
            textFieldMarketName.setDisable(selected);
        });
        buttonClear.setOnAction(e -> paneForComponentDetails.clearTable());
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
                    labelGrowth.setText(LABEL_FINAL_VAL);
                    break;
            }
        });
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
     * Populates the sector combo box based on the technology info and filter text.
     * Handles filtering and ensures no duplicate sectors are added.
     */
    private void setupComboBoxSector() {
        comboBoxSector.getItems().clear();
        try {
            String[][] techInfo = vars.getTechInfo();
            if (techInfo == null) return;
            ArrayList<String> sectorList = new ArrayList<>();
            String filterText = textFieldFilter.getText() != null ? textFieldFilter.getText().trim() : "";
            boolean useFilter = !filterText.isEmpty();
            if (!useFilter) sectorList.add(SELECT_ONE);
            sectorList.add(ALL);
            for (String[] techRow : techInfo) {
                if (techRow == null || techRow.length == 0) continue;
                String text = techRow[0] != null ? techRow[0].trim() : "";
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
                        for (String temp : techRow) {
                            if (temp != null && temp.contains(filterText)) show = true;
                        }
                    }
                    if (show) {
                        sectorList.add(text);
                    }
                }
            }
            for (String sector : sectorList) {
                if (sector != null) comboBoxSector.getItems().add(sector.trim());
            }
            comboBoxSector.getSelectionModel().select(0);
        } catch (NullPointerException e) {
            utils.warningMessage("Problem reading tech list: Null value encountered.");
            System.out.println("NullPointerException reading tech list from " + vars.getTchBndListFilename() + ":");
            System.out.println("  ---> " + e);
        } catch (Exception e) {
            utils.warningMessage("Problem reading tech list.");
            System.out.println("Error reading tech list from " + vars.getTchBndListFilename() + ":");
            System.out.println("  ---> " + e);
        }
    }

    /**
     * Updates the technology check combo box based on the selected sector and filter text.
     * Only technologies matching the filter and sector are shown.
     */
    private void updateCheckComboBoxTech() {
        String sector = comboBoxSector.getValue();
        if (sector == null) return;
        String[][] techInfo = vars.getTechInfo();
        if (techInfo == null) return;
        boolean isAllSectors = sector.equals(ALL);
        try {
            if (!checkComboBoxTech.getItems().isEmpty()) {
                checkComboBoxTech.getCheckModel().clearChecks();
                checkComboBoxTech.getItems().clear();
            }
            if (sector != null) {
                String lastLine = "";
                String filterText = textFieldFilter.getText() != null ? textFieldFilter.getText().trim() : "";
                for (String[] techRow : techInfo) {
                    if (techRow == null || techRow.length < 3) continue;
                    String line = (techRow[0] != null ? techRow[0].trim() : "") + " : " + (techRow[1] != null ? techRow[1] : "") + " : " + (techRow[2] != null ? techRow[2] : "");
                    if (filterText.isEmpty() || line.contains(filterText)) {
                        if (techRow.length >= 7 && techRow[6] != null) line += " : " + techRow[6];
                        if (!line.equals(lastLine)) {
                            lastLine = line;
                            if (isAllSectors || line.startsWith(sector)) {
                                checkComboBoxTech.getItems().add(line);
                            }
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            utils.warningMessage("Problem reading tech list: Null value encountered.");
            System.out.println("NullPointerException reading tech list from " + vars.getTchBndListFilename() + ":");
            System.out.println("  ---> " + e);
        } catch (Exception e) {
            utils.warningMessage("Problem reading tech list.");
            System.out.println("Error reading tech list from " + vars.getTchBndListFilename() + ":");
            System.out.println("  ---> " + e);
        }
    }

    /**
     * Automatically sets the policy and market names based on the current selections and auto-naming rules.
     */
    private void setPolicyAndMarketNames() {
        if (checkBoxUseAutoNames.isSelected()) {
            String policyType = "--";
            String technology = "Tech";
            String sector = "--";
            String state = "--";
            String treatment = "--";
            try {
                String s = comboBoxConstraint.getValue();
                if (s.contains("Upper")) policyType = "Up";
                if (s.contains("Lower")) policyType = "Lo";
                if (s.contains("Fixed")) policyType = "Fx";
                s = comboBoxSector.getValue();
                if (!s.equals(SELECT_ONE)) {
                    s = s.replace(" ", "_");
                    s = utils.capitalizeOnlyFirstLetterOfString(s);
                    sector = s;
                }
                s = comboBoxTreatment.getValue();
                if (s.contains("Each")) treatment = "_Ea";
                if (s.contains("Across")) treatment = "";
                String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(paneForCountryStateTree.getTree());
                if (listOfSelectedLeaves.length > 0) {
                    listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
                    String stateStr = utils.returnAppendedString(listOfSelectedLeaves).replace(",", "");
                    if (stateStr.length() < 9) {
                        state = stateStr;
                    } else {
                        state = "Reg";
                    }
                }
                String name = policyType + "_" + sector + "_" + technology + "_" + state + treatment;
                name = name.replaceAll(" ", "_").replaceAll("--", "-");
                textFieldMarketName.setText(name + "_Mkt");
                textFieldPolicyName.setText(name);
            } catch (Exception e) {
                System.out.println("Cannot auto-name market. Continuing.");
            }
        }
    }

    /**
     * Calculates the values for the policy based on the selected modification type and input fields.
     *
     * @return a 2D array of calculated values for the policy
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
     * Runs background tasks or updates for this tab. Implementation of Runnable interface.
     */
    @Override
    public void run() {
        saveScenarioComponent();
    }

    /**
     * Saves the scenario component using the current region tree.
     */
    @Override
    public void saveScenarioComponent() {
        saveScenarioComponent(paneForCountryStateTree.getTree());
    }


    /**
     * Saves the scenario component using the provided region tree.
     * Writes metadata and constraint tables to temporary files.
     *
     * @param tree The region selection tree
     */
    private void saveScenarioComponent(TreeView<String> tree) {

        if (!qaInputs()){
            Thread.currentThread().destroy();
        } else {

            String bound_type = comboBoxConstraint.getSelectionModel().getSelectedItem().trim().toLowerCase();

            //String ID=this.getUniqueMarketName(textFieldMarketName.getText());
            String ID=utils.getUniqueString();
            String policy_name = this.textFieldPolicyName.getText() + ID;
            String market_name = this.textFieldMarketName.getText() + ID;
            filenameSuggestion=this.textFieldPolicyName.getText().replaceAll("/", "-").replaceAll(" ", "_")+".csv";

            String tempDirName = vars.getGlimpseDir() + File.separator + "GLIMPSE-Data" + File.separator + "temp"; // vars.getGlimpseDir();
            File test = new File(tempDirName);
            if (!test.exists())
                test.mkdir();
            String tempFilename0 = "temp_policy_file0.txt";
            String tempFilename1 = "temp_policy_file1.txt";
            String tempFilename2 = "temp_policy_file2.txt";
            String tempFilename3 = "temp_policy_file3.txt";
            
            BufferedWriter bw0 = files.initializeBufferedFile(tempDirName, tempFilename0);
            BufferedWriter bw1 = files.initializeBufferedFile(tempDirName, tempFilename1);
            BufferedWriter bw2 = files.initializeBufferedFile(tempDirName, tempFilename2);
            BufferedWriter bw3 = files.initializeBufferedFile(tempDirName, tempFilename3);

            int no_nested = 0;
            int no_non_nested = 0;

            fileContent = "use temp file";
            files.writeToBufferedFile(bw0, getMetaDataContent(tree, market_name, policy_name));

            String treatment = comboBoxTreatment.getValue().toLowerCase();

            //// -----------getting selected regions info from GUI
            String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);
            // Dan: messy approach to make sure inclusion of USA is intentional
            listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);

            String states = utils.returnAppendedString(listOfSelectedLeaves);

            String which = "tax";
            String header_part1 = "GLIMPSEPFStdTechUpBoundP1";
            String header_part2 = "GLIMPSEPFStdTechUpBoundP2";
            if (bound_type.equals("fixed")) {
                header_part2 = "GLIMPSEPFStdTechFxBoundP2";
            }

            if (bound_type.equals("lower")) {
                which = "subsidy";
                header_part1 = "GLIMPSEPFStdTechLoBoundP1";
                header_part2 = "GLIMPSEPFStdTechLoBoundP2";
            }

            ObservableList<String> tech_list = checkComboBoxTech.getCheckModel().getCheckedItems();

            // setting up input table for nested sources
            files.writeToBufferedFile(bw1,"INPUT_TABLE" + vars.getEol());
            files.writeToBufferedFile(bw1,"Variable ID" + vars.getEol());
            files.writeToBufferedFile(bw1,header_part1 + "-Nest" + vars.getEol() + vars.getEol());
            files.writeToBufferedFile(bw1,"region,sector,nested-subsector,subsector,tech,year,policy-name" + vars.getEol());

            files.writeToBufferedFile(bw2,"INPUT_TABLE" + vars.getEol());
            files.writeToBufferedFile(bw2,"Variable ID" + vars.getEol());
            files.writeToBufferedFile(bw2,header_part1 + vars.getEol() + vars.getEol());
            files.writeToBufferedFile(bw2,"region,sector,subsector,tech,year,policy-name" + vars.getEol());

            //getting values for constraint
            ArrayList<String> dataArrayList = this.paneForComponentDetails.getDataYrValsArrayList();
            String[] year_list = new String[dataArrayList.size()];
            String[] value_list = new String[dataArrayList.size()];
            double[] valuef_list = new double[dataArrayList.size()];					

            //setting up dates for iteration
            for (int i = 0; i < dataArrayList.size(); i++) {
                String str = dataArrayList.get(i).replaceAll(" ", "").trim();
                year_list[i] = utils.splitString(str, ",")[0];
                value_list[i] = utils.splitString(str, ",")[1];
                valuef_list[i] = Double.parseDouble(value_list[i]);
            }
            int start_year = 2010;
            int last_year = Integer.parseInt(year_list[year_list.length-1]);
            String sss = vars.getStartYearForShare();
            if (!sss.equals("2010")) {
                try {
                    start_year = Integer.parseInt(sss);
                } catch (Exception e1) {
                    System.out.println(
                            "Problem converting startYearForShare (" + sss + ") to int. Using default value of 2010.");
                }
            }
            
            for (int t = 0; t < tech_list.size(); t++) {

                //gets tech info from tech list
                String[] temp = utils.splitString(tech_list.get(t).trim(), ":");
                
                String sector=temp[0].trim();
                String subsector=temp[1].trim();
                String tech=temp[2].trim();

                boolean is_nested = false;

                //checks to see if sector is nested
                if (subsector.indexOf("=>") > -1) {
                    is_nested = true;
                    no_nested += 1;
                    subsector = subsector.replaceAll("=>", ",");
                } else {
                    is_nested = false;
                    no_non_nested += 1;
                }

                // writes data
                for (int s = 0; s < listOfSelectedLeaves.length; s++) {
                    String state = listOfSelectedLeaves[s];

                    String use_this_policy_name = policy_name;
                    if (treatment.equals("each selected region")) {
                        if (listOfSelectedLeaves.length >= 2) {
                            use_this_policy_name = state + "_" + policy_name;
                        }
                    }

                    // iterates over lines in constraint table
                    for (int y = start_year; y <= last_year; y +=5) {
                    
////					for (int i = 0; i < dataArrayList.size(); i++) {
//						String data_str = data.get(i).replace(" ", "");
//						String year = utils.splitString(data_str, ",")[0];

                        if (is_nested) {
                            files.writeToBufferedFile(bw1,state + "," + sector + "," + subsector + "," + tech + "," + y + ","
                                    + use_this_policy_name + vars.getEol());
                        } else {
                            files.writeToBufferedFile(bw2,state + "," + sector + "," + subsector + "," + tech + "," + y + ","
                                    + use_this_policy_name + vars.getEol());
                        }

                    }
                    double progress = (double) s / listOfSelectedLeaves.length;
                    progressBar.setProgress(progress);
                }
            }
            files.writeToBufferedFile(bw1,""+vars.getEol());
            files.writeToBufferedFile(bw2,""+vars.getEol());

            // if (t == 0) {
            files.writeToBufferedFile(bw3,"INPUT_TABLE" + vars.getEol());
            files.writeToBufferedFile(bw3,"Variable ID" + vars.getEol());
            files.writeToBufferedFile(bw3,header_part2 + vars.getEol() + vars.getEol());

            if (bound_type.equals("fixed")) {
                files.writeToBufferedFile(bw3,"region,policy-name,market,type,constraint-yr,constraint-val,min-price-yr,min-price-val"
                        + vars.getEol());
            } else {
                files.writeToBufferedFile(bw3,"region,policy-name,market,type,constraint-yr,constraint-val" + vars.getEol());
            }

            for (int s = 0; s < listOfSelectedLeaves.length; s++) {
                String state = listOfSelectedLeaves[s];

                String use_this_market_name = market_name;
                String use_this_policy_name = policy_name;
                if (treatment.equals("each selected region")) {
                    if (listOfSelectedLeaves.length >= 2) {
                        use_this_market_name = state + "_" + market_name;
                        use_this_policy_name = state + "_" + policy_name;
                    }
                }

                ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();
                for (int i = 0; i < data.size(); i++) {

                    String data_str = data.get(i).replace(" ", "");
                    String year = utils.splitString(data_str, ",")[0];
                    String val = utils.splitString(data_str, ",")[1];

                    if (!bound_type.equals("fixed")) {
                        files.writeToBufferedFile(bw3,state + "," + use_this_policy_name + "," + use_this_market_name + "," + which
                                + "," + year + "," + val + vars.getEol());
                    } else {
                        files.writeToBufferedFile(bw3,state + "," + use_this_policy_name + "," + use_this_market_name + "," + which
                                + "," + year + "," + val + "," + year + ",-100" + vars.getEol());
                    }
                }
                double progress = (double) s / listOfSelectedLeaves.length;
                progressBar.setProgress(progress);
            }


            files.closeBufferedFile(bw0);
            files.closeBufferedFile(bw1);
            files.closeBufferedFile(bw2);
            files.closeBufferedFile(bw3);
            
            // TODO: store temp file name in options file and vars?
            String temp_file = tempDirName + File.separator + "temp_policy_file.txt";

            files.deleteFile(tempDirName);

            String temp_file0 = tempDirName + File.separator + tempFilename0;
            String temp_file1 = tempDirName + File.separator + tempFilename1;
            String temp_file2 = tempDirName + File.separator + tempFilename2;
            String temp_file3 = tempDirName + File.separator + tempFilename3;
            
            ArrayList<String> tempfiles = new ArrayList<String>();
            tempfiles.add(temp_file0);

            if (no_nested > 0) {
                tempfiles.add(temp_file1);
            }
            if (no_non_nested > 0) {
                tempfiles.add(temp_file2);
            }
            tempfiles.add(temp_file3);

            files.concatDestSources(temp_file, tempfiles);

            System.out.println("Done");
        }

    }

    /**
     * Returns a string containing the metadata content for the scenario component.
     *
     * @param tree The region selection tree
     * @param market The market name
     * @param policy The policy name
     * @return Metadata content as a string
     */
    public String getMetaDataContent(TreeView<String> tree, String market, String policy) {
        String rtn_str = "";

        rtn_str += "########## Scenario Component Metadata ##########" + vars.getEol();
        rtn_str += "#Scenario component type: Tech Bound" + vars.getEol();
        rtn_str += "#Sector: " + comboBoxSector.getValue() + vars.getEol();

        ObservableList<String> tech_list = checkComboBoxTech.getCheckModel().getCheckedItems();
        String techs = utils.getStringFromList(tech_list, ";");
        rtn_str += "#Technologies: " + techs + vars.getEol();

        rtn_str += "#Constraint: " + comboBoxConstraint.getValue() + vars.getEol();
        rtn_str += "#Treatment: " + comboBoxTreatment.getValue() + vars.getEol();
        if (policy == null)
            market = textFieldPolicyName.getText();
        rtn_str += "#Policy name: " + policy + vars.getEol();
        if (market == null)
            market = textFieldMarketName.getText();
        rtn_str += "#Market name: " + market + vars.getEol();

        String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);
        listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
        String states = utils.returnAppendedString(listOfSelectedLeaves);
        rtn_str += "#Regions: " + states + vars.getEol();

        ArrayList<String> table_content = this.paneForComponentDetails.getDataYrValsArrayList();
        // Enhanced for-loop for table_content
        for (String tableLine : table_content) {
            rtn_str += "#Table data:" + tableLine + vars.getEol();
        }
        rtn_str += "#################################################" + vars.getEol();

        return rtn_str;
    }

    /**
     * Loads content into the tab from the provided list of strings.
     *
     * @param content List of content lines to load
     */
    @Override
    public void loadContent(ArrayList<String> content) {
        // Enhanced for-loop for content
        int i = 0;
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
                    // Enhanced for-loop for set
                    for (String item : set) {
                        if (item != null) {
                            checkComboBoxTech.getCheckModel().check(item.trim());
                            checkComboBoxTech.fireEvent(new ActionEvent());
                        }
                    }
                }
                if (param.equals("constraint")) {
                    comboBoxConstraint.setValue(value);
                    comboBoxConstraint.fireEvent(new ActionEvent());
                }
                if (param.equals("treatment")) {
                    comboBoxTreatment.setValue(value);
                    comboBoxTreatment.fireEvent(new ActionEvent());
                }
                if (param.equals("policy name")) {
                    textFieldPolicyName.setText(value);
                    textFieldPolicyName.fireEvent(new ActionEvent());
                }
                if (param.equals("market name")) {
                    textFieldMarketName.setText(value);
                    textFieldMarketName.fireEvent(new ActionEvent());
                }
                if (param.equals("regions")) {
                    String[] regions = utils.splitString(value, ",");
                    this.paneForCountryStateTree.selectNodes(regions);
                }
                if (param.equals("table data")) {
                    String[] s = utils.splitString(value, ",");
                    if (s.length >= 2) {
                        this.paneForComponentDetails.data.add(new DataPoint(s[0], s[1]));
                    }
                }
            }
            i++;
        }
        this.setUnitsLabel();
        this.paneForComponentDetails.updateTable();
    }

    /**
     * Validates that all required fields for populating values are filled in.
     *
     * @return true if all required fields are filled, false otherwise
     */
    public boolean qaPopulate() {
        boolean is_correct = true;

        if (textFieldStartYear.getText().isEmpty())
            is_correct = false;
        if (textFieldEndYear.getText().isEmpty())
            is_correct = false;
        if (textFieldInitialAmount.getText().isEmpty())
            is_correct = false;
        if (textFieldGrowth.getText().isEmpty())
            is_correct = false;

        return is_correct;
    }

    /**
     * Validates that all required inputs for saving the scenario component are present and correct.
     *
     * @return true if all required inputs are valid, false otherwise
     */
    protected boolean qaInputs() {

        TreeView<String> tree = paneForCountryStateTree.getTree();

        int error_count = 0;
        String message = "";

        try {

            if (utils.getAllSelectedLeaves(tree).length < 1) {
                message += "Must select at least one region from tree" + vars.getEol();
                error_count++;
            }
            if (paneForComponentDetails.table.getItems().size() == 0) {
                message += "Data table must have at least one entry" + vars.getEol();
                error_count++;
            } else {
                boolean match=false;
                
                String listOfAllowableYears=vars.getAllowablePolicyYears();
                ObservableList<DataPoint> data = this.paneForComponentDetails.table.getItems();
                String year = "";

                for (int i = 0; i < data.size(); i++) {
                    year = data.get(i).getYear().trim();
                    if (listOfAllowableYears.contains(year)) match=true;
                }
                if (!match) {
                    message += "Years specified in table must match allowable policy years ("+listOfAllowableYears+")" + vars.getEol();
                    error_count++;					
                }
            }
            if (comboBoxSector.getSelectionModel().getSelectedItem().equals("Select One")) {
                message += "Sector comboBox must have a selection" + vars.getEol();
                error_count++;
            }
            if (checkComboBoxTech.getCheckModel().getCheckedItems().size() == 0) {
                message += "Tech checkComboBox must have a selection" + vars.getEol();
                error_count++;
            }
            if (comboBoxConstraint.getSelectionModel().getSelectedItem().equals("Select One")) {
                message += "Constraint comboBox must have a selection" + vars.getEol();
                error_count++;
            }
            if (comboBoxTreatment.getSelectionModel().getSelectedItem().equals("Select One")) {
                message += "Treatment comboBox must have a selection" + vars.getEol();
                error_count++;
            }
            if (textFieldMarketName.getText().equals("")) {
                message += "A market name must be provided" + vars.getEol();
                error_count++;
            }
            if (textFieldPolicyName.getText().equals("")) {
                message += "A policy name must be provided" + vars.getEol();
                error_count++;
            }

        } catch (Exception e1) {
            error_count++;
            message += "Error in QA of entries" + vars.getEol();
        }
        if (error_count > 0) {
            if (error_count == 1) {
                utils.warningMessage(message);
            } else if (error_count > 1) {
                utils.displayString(message, "Parsing Errors");
            }
        }

        boolean is_correct;
        if (error_count == 0) {
            is_correct = true;
        } else {
            is_correct = false;
        }
        return is_correct;
    }

    /**
     * Sets the units label based on the selected technologies.
     * Displays a warning if units do not match.
     */
    public void setUnitsLabel() {
        String s = getUnits();
        String label = (s != null && s.equals("No match")) ? LABEL_WARNING_UNITS : s;
        if (labelUnits2 != null) {
            Runnable update = () -> {
                if (!label.equals(labelUnits2.getText())) {
                    labelUnits2.setText(label);
                }
            };
            if (Platform.isFxApplicationThread()) {
                update.run();
            } else {
                Platform.runLater(update);
            }
        }
    }

    /**
     * Returns the units for the selected technologies, or a warning if units do not match.
     *
     * @return The units string or a warning if units do not match
     */
    public String getUnits() {
        ObservableList<String> techList = checkComboBoxTech.getCheckModel().getCheckedItems();
        if (techList == null) return "";
        String unit = "";
        for (String line : techList) {
            if (line == null) continue;
            String item = "";
            try {
                int idx = line.lastIndexOf(":");
                if (idx >= 0 && idx < line.length() - 1) {
                    item = line.substring(idx + 1).trim();
                }
                if (unit.equals("")) {
                    unit = item;
                } else if (!unit.equals(item)) {
                    unit = "No match";
                }
            } catch (NullPointerException e) {
                item = "";
                utils.warningMessage("Null value encountered while parsing units.");
            } catch (Exception e) {
                item = "";
                utils.warningMessage("Error while parsing units: " + e.getMessage());
            }
        }
        if (unit != null && unit.trim().equals(SELECT_ONE_OR_MORE)) unit = "";
        return unit;
    }

}
