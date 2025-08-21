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

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.CheckComboBox;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.CheckBoxTreeItem.TreeModificationEvent;
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
    private static final String LABEL_FILTER = "Filter:";
    private static final String LABEL_SECTOR = "Sector: ";
    private static final String LABEL_TECHS = "Tech(s): ";
    private static final String LABEL_CONSTRAINT = "Constraint: ";
    private static final String LABEL_TREATMENT = "Treatment: ";
    private static final String LABEL_UNITS = "Units: ";
    private static final String LABEL_FINAL_VAL = "Final Val: ";
    private static final String LABEL_POPULATE = "Populate:";
    private static final String SELECT_ONE = "Select One";
    private static final String SELECT_ONE_OR_MORE = "Select One or More";
    private static final String ALL = "All";
    private static final String CONSTRAINT_UPPER = "Upper Bound";
    private static final String CONSTRAINT_LOWER = "Lower Bound";
    private static final String CONSTRAINT_FIXED = "Fixed Bound";
    private static final String[] CONSTRAINT_OPTIONS = {CONSTRAINT_UPPER, CONSTRAINT_LOWER, CONSTRAINT_FIXED};
    private static final String[] TREATMENT_OPTIONS = {"Each Selected Region", "Across Selected Regions"};
    private static final String UNITS_DEFAULT = "";
    // === UI Components ===
    private final Label labelFilter = createLabel(LABEL_FILTER, LABEL_WIDTH);
    private final TextField textFieldFilter = createTextField();
    private final Label labelComboBoxSector = createLabel(LABEL_SECTOR, LABEL_WIDTH);
    private final ComboBox<String> comboBoxSector = createComboBoxString();
    private final Label labelCheckComboBoxTech = createLabel(LABEL_TECHS, LABEL_WIDTH);
    private final CheckComboBox<String> checkComboBoxTech = utils.createCheckComboBox();
    private final Label labelComboBoxConstraint = createLabel(LABEL_CONSTRAINT, LABEL_WIDTH);
    private final ComboBox<String> comboBoxConstraint = createComboBoxString();
    private final Label labelTreatment = createLabel(LABEL_TREATMENT, LABEL_WIDTH);
    private final ComboBox<String> comboBoxTreatment = createComboBoxString();
    private final Label labelUnits = createLabel(LABEL_UNITS, LABEL_WIDTH);

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
        setupUIControls();
        setComponentWidths();
        setupUILayout();
        setupComboBoxSector();
        setupTechComboBox();
        setupComboBoxOptions();
        setupEventHandlers();
        setPolicyAndMarketNames();
        setUnitsLabel();
    }

    /**
     * Sets up UI controls with options and default values.
     */
    private void setupUIControls() {
        checkBoxUseAutoNames.setSelected(true);
        textFieldPolicyName.setDisable(true);
        textFieldMarketName.setDisable(true);
    }

    /**
     * Sets preferred, min, and max widths for UI components.
     */
    private void setComponentWidths() {
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
     * Sets up the layout of the tab.
     */
    public void setupUILayout() {
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
        VBox tabLayout = new VBox();
        tabLayout.getChildren().addAll(gridPanePresetModification);
        this.setContent(tabLayout);
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
        comboBoxTreatment.getSelectionModel().selectFirst();
        comboBoxConstraint.getItems().addAll(CONSTRAINT_OPTIONS);
        comboBoxModificationType.getItems().addAll(MODIFICATION_TYPE_OPTIONS);
        comboBoxSector.getSelectionModel().selectFirst();
        comboBoxConstraint.getSelectionModel().selectFirst();
        comboBoxModificationType.getSelectionModel().selectFirst();
    }

    /**
     * Sets up event handlers for UI components.
     */
    private void setupEventHandlers() {
		
//    	paneForCountryStateTree.getTree().addEventHandler(ActionEvent.ACTION, e -> {
//    			setPolicyAndMarketNames();
//    	});
		setEventHandler(textFieldFilter, e -> {
			String filterText = textFieldFilter.getText().trim();
			if (filterText.isEmpty()) {
				checkComboBoxTech.getItems().clear();
				checkComboBoxTech.getItems().add(SELECT_ONE_OR_MORE);
				checkComboBoxTech.getCheckModel().check(0);
				checkComboBoxTech.setDisable(true);
			} else {
				checkComboBoxTech.setDisable(false);
				updateCheckComboBoxTech();
			}
		});
		setEventHandler(checkComboBoxTech, e -> setPolicyAndMarketNames());
    	
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
        setEventHandler(comboBoxSector, e -> {
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
        setEventHandler(comboBoxConstraint, e -> setPolicyAndMarketNames());
        setEventHandler(comboBoxTreatment, e -> setPolicyAndMarketNames());
        setEventHandler(textFieldFilter, e -> setupComboBoxSector());
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
        Platform.runLater(() -> {
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
        });
    }

    /**
     * Automatically sets the policy and market names based on the current selections and auto-naming rules.
     */
    protected void setPolicyAndMarketNames() {
        Platform.runLater(() -> {
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
                    String[] listOfSelectedLeaves = utils.getAllSelectedRegions(paneForCountryStateTree.getTree());
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
        });
    }

    /**
     * Runs background tasks or updates for this tab. Implementation of Runnable interface.
     */
    @Override
    public void run() {
        Platform.runLater(this::saveScenarioComponent);
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

            String ID = utils.getUniqueString();
            String policy_name = this.textFieldPolicyName.getText() + ID;
            String market_name = this.textFieldMarketName.getText() + ID;
            filenameSuggestion = this.textFieldPolicyName.getText().replaceAll("/", "-").replaceAll(" ", "_") + ".csv";

            String tempDirName = vars.getGlimpseDir() + File.separator + "GLIMPSE-Data" + File.separator + "temp";
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

            files.writeToBufferedFile(bw0, getMetaDataContent(tree, market_name, policy_name));

            String treatment = comboBoxTreatment.getValue().toLowerCase();

            String[] listOfSelectedLeaves = utils.getAllSelectedRegions(tree);
            listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);

            String header_part1 = "GLIMPSEPFStdTechUpBoundP1";
            String header_part2 = "GLIMPSEPFStdTechUpBoundP2";
            if (bound_type.equals("fixed")) {
                header_part2 = "GLIMPSEPFStdTechFxBoundP2";
            }

            if (bound_type.equals("lower")) {
                header_part1 = "GLIMPSEPFStdTechLoBoundP1";
                header_part2 = "GLIMPSEPFStdTechLoBoundP2";
            }

            ObservableList<String> tech_list = checkComboBoxTech.getCheckModel().getCheckedItems();

            files.writeToBufferedFile(bw1, "INPUT_TABLE" + vars.getEol());
            files.writeToBufferedFile(bw1, "Variable ID" + vars.getEol());
            files.writeToBufferedFile(bw1, header_part1 + "-Nest" + vars.getEol() + vars.getEol());
            files.writeToBufferedFile(bw1, "region,sector,nested-subsector,subsector,tech,year,policy-name" + vars.getEol());

            files.writeToBufferedFile(bw2, "INPUT_TABLE" + vars.getEol());
            files.writeToBufferedFile(bw2, "Variable ID" + vars.getEol());
            files.writeToBufferedFile(bw2, header_part1 + vars.getEol() + vars.getEol());
            files.writeToBufferedFile(bw2, "region,sector,subsector,tech,year,policy-name" + vars.getEol());

            ArrayList<String> dataArrayList = this.paneForComponentDetails.getDataYrValsArrayList();
            String[] year_list = new String[dataArrayList.size()];
            String[] value_list = new String[dataArrayList.size()];
            double[] valuef_list = new double[dataArrayList.size()];

            for (int i = 0; i < dataArrayList.size(); i++) {
                String str = dataArrayList.get(i).replaceAll(" ", "").trim();
                year_list[i] = utils.splitString(str, ",")[0];
                value_list[i] = utils.splitString(str, ",")[1];
                valuef_list[i] = Double.parseDouble(value_list[i]);
            }

            int start_year = 2010;
            int last_year = Integer.parseInt(year_list[year_list.length - 1]);
            String sss = vars.getStartYearForShare();
            if (!sss.equals("2010")) {
                try {
                    start_year = Integer.parseInt(sss);
                } catch (Exception e1) {
                    System.out.println("Problem converting startYearForShare (" + sss + ") to int. Using default value of 2010.");
                }
            }

            StringBuilder nestedBuffer = new StringBuilder();
            StringBuilder nonNestedBuffer = new StringBuilder();

            for (String techItem : tech_list) {
                String[] temp = utils.splitString(techItem.trim(), ":");

                String sector = temp[0].trim();
                String subsector = temp[1].trim();
                String tech = temp[2].trim();

                boolean is_nested = subsector.contains("=>");
                if (is_nested) {
                    no_nested++;
                    subsector = subsector.replaceAll("=>", ",");
                } else {
                    no_non_nested++;
                }

                for (String state : listOfSelectedLeaves) {
                    String use_this_policy_name = policy_name;
                    if (treatment.equals("each selected region") && listOfSelectedLeaves.length >= 2) {
                        use_this_policy_name = state + "_" + policy_name;
                    }

                    for (int y = start_year; y <= last_year; y += 5) {
                        if (is_nested) {
                            nestedBuffer.append(state).append(",").append(sector).append(",").append(subsector).append(",").append(tech).append(",").append(y).append(",").append(use_this_policy_name).append(vars.getEol());
                        } else {
                            nonNestedBuffer.append(state).append(",").append(sector).append(",").append(subsector).append(",").append(tech).append(",").append(y).append(",").append(use_this_policy_name).append(vars.getEol());
                        }
                    }
                }
            }

            files.writeToBufferedFile(bw1, nestedBuffer.toString());
            files.writeToBufferedFile(bw2, nonNestedBuffer.toString());

            files.writeToBufferedFile(bw3, "INPUT_TABLE" + vars.getEol());
            files.writeToBufferedFile(bw3, "Variable ID" + vars.getEol());
            files.writeToBufferedFile(bw3, header_part2 + vars.getEol() + vars.getEol());

            if (bound_type.equals("fixed")) {
                files.writeToBufferedFile(bw3, "region,policy-name,market,type,constraint-yr,constraint-val,min-price-yr,min-price-val" + vars.getEol());
            } else {
                files.writeToBufferedFile(bw3, "region,policy-name,market,type,constraint-yr,constraint-val" + vars.getEol());
            }

            StringBuilder constraintBuffer = new StringBuilder();

            for (String state : listOfSelectedLeaves) {
                String use_this_market_name = market_name;
                String use_this_policy_name = policy_name;
                if (treatment.equals("each selected region") && listOfSelectedLeaves.length >= 2) {
                    use_this_market_name = state + "_" + market_name;
                    use_this_policy_name = state + "_" + policy_name;
                }

                for (String data : dataArrayList) {
                    String data_str = data.replace(" ", "");
                    String year = utils.splitString(data_str, ",")[0];
                    String val = utils.splitString(data_str, ",")[1];

                    if (!bound_type.equals("fixed")) {
                        constraintBuffer.append(state).append(",").append(use_this_policy_name).append(",").append(use_this_market_name).append(",tax,").append(year).append(",").append(val).append(vars.getEol());
                    } else {
                        constraintBuffer.append(state).append(",").append(use_this_policy_name).append(",").append(use_this_market_name).append(",tax,").append(year).append(",").append(val).append(",").append(year).append(",-100").append(vars.getEol());
                    }
                }
            }

            files.writeToBufferedFile(bw3, constraintBuffer.toString());

            files.closeBufferedFile(bw0);
            files.closeBufferedFile(bw1);
            files.closeBufferedFile(bw2);
            files.closeBufferedFile(bw3);

            String temp_file = tempDirName + File.separator + "temp_policy_file.txt";

            files.deleteFile(tempDirName);

            String temp_file0 = tempDirName + File.separator + tempFilename0;
            String temp_file1 = tempDirName + File.separator + tempFilename1;
            String temp_file2 = tempDirName + File.separator + tempFilename2;
            String temp_file3 = tempDirName + File.separator + tempFilename3;

            ArrayList<String> tempfiles = new ArrayList<>();
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

        String[] listOfSelectedLeaves = utils.getAllSelectedRegions(tree);
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
        }
        this.setUnitsLabel();
        this.paneForComponentDetails.updateTable();
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
     * Validates that all required inputs for saving the scenario component are present and correct.
     *
     * @return true if all required inputs are valid, false otherwise
     */
    protected boolean qaInputs() {

        TreeView<String> tree = paneForCountryStateTree.getTree();

        int error_count = 0;
        String message = "";

        try {

            if (utils.getAllSelectedRegions(tree).length < 1) {
                message += "Must select at least one region from tree" + vars.getEol();
                error_count++;
            }
            if (paneForComponentDetails.table.getItems().size() == 0) {
                message += "Data table must have at least one entry" + vars.getEol();
                error_count++;
            } else {
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
     * Utility method to set an event handler for a control (Button, ComboBox, CheckBox, TextField).
     *
     * @param control The control to set the event handler for
     * @param handler The event handler to assign
     * @param <T> The type of the control
     */
    private <T extends javafx.scene.Node> void setEventHandler(T control, EventHandler<ActionEvent> handler) {
        if (control instanceof Button) {
            ((Button) control).setOnAction(handler);
        } else if (control instanceof ComboBox) {
            ((ComboBox<?>) control).setOnAction(handler);
        } else if (control instanceof CheckBox) {
            ((CheckBox) control).setOnAction(handler);
        } else if (control instanceof TextField) {
            ((TextField) control).setOnAction(handler);
        }
    }

    /**
     * Updates the units label based on the selected technologies.
     */
    private void setUnitsLabel() {
        ObservableList<String> selectedTechs = checkComboBoxTech.getCheckModel().getCheckedItems();
        String units = UNITS_DEFAULT;
        if (selectedTechs != null && !selectedTechs.isEmpty()) {
            // Example: extract units from selected techs if available
            // This logic should be replaced with actual units extraction as needed
            String firstTech = selectedTechs.get(0);
            String[] parts = firstTech.split(":");
            if (parts.length >= 4) {
                units = parts[3].trim();
            }
        }
        labelUnits2.setText(units);
    }
}
