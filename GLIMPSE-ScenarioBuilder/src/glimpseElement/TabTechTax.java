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

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
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
 *
 * <p>
 * <b>UI Features:</b>
 * <ul>
 *   <li>Sector selection and filtering</li>
 *   <li>Technology multi-selection with checkboxes</li>
 *   <li>Tax/Subsidy type selection</li>
 *   <li>Policy and market name auto-generation</li>
 *   <li>Units display and validation</li>
 *   <li>Region selection via tree</li>
 *   <li>Data entry and validation for policy years and values</li>
 *   <li>Support for loading and saving scenario component data</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Key Methods:</b>
 * <ul>
 *   <li>{@link #setupUIControls()} - Initializes all UI controls</li>
 *   <li>{@link #setupEventHandlers()} - Sets up all event listeners for UI controls</li>
 *   <li>{@link #setupComboBoxSector()} - Populates the sector ComboBox with available sectors</li>
 *   <li>{@link #updateCheckComboBoxTech()} - Updates the technology CheckComboBox based on sector/filter</li>
 *   <li>{@link #setPolicyAndMarketNames()} - Auto-generates policy and market names</li>
 *   <li>{@link #saveScenarioComponent()} - Saves the scenario component to fileContent</li>
 *   <li>{@link #qaInputs()} - Validates all user inputs before saving</li>
 *   <li>{@link #setUnitsLabel()} - Updates the units label based on selected technologies</li>
 *   <li>{@link #getUnits()} - Gets the units for the selected technologies</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Dependencies:</b> Relies on utility classes for UI creation, string manipulation, and data validation.
 * </p>
 */
public class TabTechTax extends PolicyTab implements Runnable {
    private static final String LABEL_UNITS_WARNING = "Warning - Units do not match!";
    private static final String LABEL_UNITS_PASSKM = "1990$ per veh-km";
    private static final String SELECT_ONE = "Select One";
    private static final String SELECT_ONE_OR_MORE = "Select One or More";
    private static final String ALL = "All";
    private static final String TAX = "Tax";
    private static final String SUBSIDY = "Subsidy";
    private static final String[] TAX_OR_SUBSIDY_OPTIONS = {SELECT_ONE, TAX, SUBSIDY};

    // --- Left Column Components ---
    private final Label labelComboBoxSector = utils.createLabel("Sector: ", LABEL_WIDTH);
    private final Label labelFilter = utils.createLabel("Filter:", LABEL_WIDTH);
    private final TextField textFieldFilter = utils.createTextField();
    private final ComboBox<String> comboBoxSector = utils.createComboBoxString();
    private final Label labelCheckComboBoxTech = utils.createLabel("Tech(s): ", LABEL_WIDTH);
    private final CheckComboBox<String> checkComboBoxTech = utils.createCheckComboBox();
    private final Label labelComboBoxTaxOrSubsidy = utils.createLabel("Type: ", LABEL_WIDTH);
    private final ComboBox<String> comboBoxTaxOrSubsidy = utils.createComboBoxString();
    private final Label labelUnits = utils.createLabel("Units: ", LABEL_WIDTH);

    /**
     * Constructs the TabTechTax UI and logic.
     *
     * @param title Tab title to display
     * @param stageX JavaFX stage reference
     */
    public TabTechTax(String title, Stage stageX) {
        this.setText(title);
        this.setStyle(styles.getFontStyle());
        checkBoxUseAutoNames.setSelected(true);
        textFieldPolicyName.setDisable(true);
        textFieldMarketName.setDisable(true);
        setupUIControls();
        setComponentWidths(comboBoxSector, checkComboBoxTech, comboBoxTaxOrSubsidy, comboBoxConvertFrom,
                textFieldStartYear, textFieldEndYear, textFieldInitialAmount, textFieldGrowth, textFieldPeriodLength,
                textFieldFilter, textFieldPolicyName, textFieldMarketName);
        setupUILayout();
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

    /**
     * Sets up all UI controls for the tab (labels, combo boxes, etc.).
     * This method initializes the left, center, and right columns of the UI.
     */
    private void setupUIControls() {
        setupLeftColumn();
        setupCenterColumn();
        setupRightColumn();
    }

    // === UI Setup Methods ===
    /**
     * Sets up the left column of the UI, adding labels and controls to the grid pane.
     * This includes filter, sector, technology, type, units, and other input fields.
     */
    private void setupLeftColumn() {
        gridPaneLeft.add(utils.createLabel("Specification:"), 0, 0, 2, 1);
        gridPaneLeft.addColumn(0, labelFilter, labelComboBoxSector, labelCheckComboBoxTech, labelComboBoxTaxOrSubsidy,
                new Label(), labelUnits, new Label(), new Separator(), this.labelUseAutoNames, this.labelPolicyName, this.labelMarketName , new Label(), new Separator() , labelModificationType,
                labelStartYear, labelEndYear, labelInitialAmount, labelGrowth, labelConvertFrom);
        gridPaneLeft.addColumn(1, textFieldFilter, comboBoxSector, checkComboBoxTech, comboBoxTaxOrSubsidy, new Label(),
                labelUnits2, new Label(), new Separator(), this.checkBoxUseAutoNames , textFieldPolicyName, textFieldMarketName, new Label(), new Separator(),
                comboBoxModificationType, textFieldStartYear, textFieldEndYear, textFieldInitialAmount, textFieldGrowth, comboBoxConvertFrom);
        gridPaneLeft.setAlignment(Pos.TOP_LEFT);
        gridPaneLeft.setVgap(3.);
        gridPaneLeft.setStyle(styles.getStyle2());
        scrollPaneLeft.setContent(gridPaneLeft);
    }

    /**
     * Sets the sizing for UI components in the tab.
     * This method sets min, max, and preferred widths for all controls.
     */
    private void setupSizing() {
        setComponentWidths(comboBoxSector, checkComboBoxTech, comboBoxTaxOrSubsidy, comboBoxConvertFrom,
                textFieldStartYear, textFieldEndYear, textFieldInitialAmount, textFieldGrowth, textFieldPeriodLength,
                textFieldFilter, textFieldPolicyName, textFieldMarketName);
    }

    /**
     * Sets the width properties for the provided controls.
     *
     * @param controls Controls to set width for
     */
    private void setComponentWidths(Control... controls) {
        for (Control c : controls) {
            c.setMaxWidth(MAX_WIDTH);
            c.setMinWidth(MIN_WIDTH);
            c.setPrefWidth(PREF_WIDTH);
        }
    }

    /**
     * Sets up event handlers for UI components in the tab.
     * This includes listeners for combo boxes, checkboxes, buttons, and filter fields.
     * All UI updates are wrapped in Platform.runLater for thread safety.
     */
    protected void setupEventHandlers() {
    	
    	super.setupEventHandlers();
		
		// Set up the filter text field to update the sector combo box
		textFieldFilter.setPromptText("Filter techs");
		textFieldFilter.setOnAction(e -> Platform.runLater(() -> setupComboBoxSector()));

		// Set up the sector combo box to update the technology check combo box
		comboBoxSector.setPromptText("Select a sector");
		comboBoxSector.setOnAction(e -> Platform.runLater(() -> {
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
		}));
    	
    	// Wrap all UI updates in Platform.runLater
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
        comboBoxSector.setOnAction(e -> Platform.runLater(() -> {
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
        }));
        checkComboBoxTech.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> Platform.runLater(() -> {
            while (c.next()) {
                setUnitsLabel();
            }
        }));
        comboBoxTaxOrSubsidy.setOnAction(e -> Platform.runLater(() -> setPolicyAndMarketNames()));
        textFieldFilter.setOnAction(e -> Platform.runLater(() -> setupComboBoxSector()));
    }

    /**
     * Sets up the sector ComboBox with available sectors, applying any filter entered by the user.
     * This method reads technology info and populates the sector list, including 'All' and filter support.
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
     * This method clears and repopulates the technology list for the selected sector.
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
     * The name is constructed from type, sector, technology, and region selections.
     */
    protected void setPolicyAndMarketNames() {
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
                name = name.replaceAll(" ", "_").replaceAll("-", "_").replaceAll("--", "_").replaceAll("_-_", "_").replaceAll("---", "");
                textFieldMarketName.setText(name + "_Mkt");
                textFieldPolicyName.setText(name);
            } catch (Exception e) {
                System.out.println("Cannot auto-name market. Continuing.");
            }
        }
    }

    /**
     * Runs background tasks or updates for this tab. Implementation of Runnable interface.
     * This method triggers saving the scenario component.
     */
    @Override
    public void run() {
        saveScenarioComponent();
    }

    /**
     * Saves the scenario component using the current country/state tree selection.
     * This method delegates to saveScenarioComponent(TreeView).
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
        // Validate all user inputs before proceeding
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
            filenameSuggestion = this.textFieldPolicyName.getText().replaceAll("/", "_").replaceAll(" ", "_") + ".csv";
            fileContent = getMetaDataContent(paneForCountryStateTree.getTree(), marketName, policyName);
            ObservableList<String> techLines = checkComboBoxTech.getCheckModel().getCheckedItems();
            ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();
            for (int iter = 0; iter < 2; iter++) {
                // iter=0: Standard, iter=1: Transport
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
                StringBuilder sb = new StringBuilder(fileContent);
                for (String techLine : techLines) {
                    // Parse sector, subsector, and technology from techLine
                    String[] temp = utils.splitString(techLine.trim(), ":");
                    String sector = temp[0].trim();
                    String subsector = temp[1].trim();
                    String tech = temp[2].trim();
                    boolean isTran = sector.startsWith("trn");
                    if (((iter == 0) && (!isTran)) || ((iter == 1) && (isTran))) {
                        // part 1: Write technology mapping table
                        sb.append("INPUT_TABLE").append(vars.getEol());
                        sb.append("Variable ID").append(vars.getEol());
                        if (subsector.indexOf("=>") > -1) {
                            sb.append(headerPart1).append("-Nest").append(vars.getEol()).append(vars.getEol());
                            sb.append("region,sector,nesting-subsector,subsector,tech,year,policy-name").append(vars.getEol());
                            subsector = subsector.replace("=>", ",");
                        } else {
                            sb.append(headerPart1).append(vars.getEol()).append(vars.getEol());
                            sb.append("region,sector,subsector,tech,year,policy-name").append(vars.getEol());
                        }
                        for (String state : listOfSelectedLeaves) {
                            for (String dataStr : data) {
                                // Write each year for each region
                                String year = utils.splitString(dataStr.replace(" ", ""), ",")[0];
                                sb.append(state).append(",").append(sector).append(",").append(subsector).append(",").append(tech).append(",").append(year).append(",").append(policyName).append(vars.getEol());
                            }
                        }
                        // part 2: Write policy value table
                        sb.append(vars.getEol());
                        sb.append("INPUT_TABLE").append(vars.getEol());
                        sb.append("Variable ID").append(vars.getEol());
                        sb.append(headerPart2).append(vars.getEol()).append(vars.getEol());
                        sb.append("region,policy-name,market,type,policy-yr,policy-val").append(vars.getEol());
                        if (listOfSelectedLeaves.length > 0) {
                            String state = listOfSelectedLeaves[0];
                            for (String dataStr : data) {
                                String[] split = utils.splitString(dataStr.replace(" ", ""), ",");
                                String year = split[0];
                                String val = split[1];
                                sb.append(state).append(",").append(policyName).append(",").append(marketName).append(",").append(which).append(",").append(year).append(",").append(val).append(vars.getEol());
                            }
                        }
                        // part 3: Write policy/market mapping table
                        sb.append(vars.getEol());
                        sb.append("INPUT_TABLE").append(vars.getEol());
                        sb.append("Variable ID").append(vars.getEol());
                        sb.append(headerPart3).append(vars.getEol()).append(vars.getEol());
                        sb.append("region,policy-name,market,type").append(vars.getEol());
                        for (String state : listOfSelectedLeaves) {
                            sb.append(state).append(",").append(policyName).append(",").append(marketName).append(",").append(which).append(vars.getEol());
                        }
                        sb.append(vars.getEol());
                    }
                }
                fileContent = sb.toString();
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
     * This method parses the content and sets the UI state for sector, technologies, type, regions, and table data.
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
     * Helper method to validate table data years against allowable policy years.
     *
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
     * Performs quality assurance checks on user inputs and displays warnings if any issues are found.
     * This method checks for region, data, sector, technology, type, and name validity.
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
     * This method updates the labelUnits2 control with the correct units or warning.
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
     * If multiple units are found, returns "No match".
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
