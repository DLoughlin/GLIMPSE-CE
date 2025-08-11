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
import gui.PaneNewScenarioComponent;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.CheckBoxTreeItem.TreeModificationEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * TabMarketShare provides the user interface and logic for creating and editing market share policies
 * in the GLIMPSE Scenario Builder. This tab allows users to select subsets and supersets, filter options,
 * and configure market share policy details for scenario components.
 *
 * <p>
 * <b>Usage:</b> This class is instantiated as a tab in the scenario builder. It extends {@link PolicyTab} and implements {@link Runnable}.
 * </p>
 *
 * <p>
 * <b>Thread Safety:</b> This class is not thread-safe and should be used on the JavaFX Application Thread.
 * </p>
 */
public class TabMarketShare extends PolicyTab implements Runnable {
    // === Constants for UI text and options ===
    private static final double MIN_WIDTH = 175;
    private static final String SELECT_ONE = "Select One";
    private static final String SELECT_ONE_OR_MORE = "Select One or More";
    private static final String[] POLICY_TYPE_OPTIONS = {
        SELECT_ONE, "Renewable Portfolio Standard (RPS)", "Clean Energy Standard (CES)",
        "EV passenger cars and trucks", "EV passenger cars trucks and MCs", "EV freight light truck",
        "EV freight medium truck", "EV freight heavy truck", "EV freight all trucks", "LED lights",
        "Heat pumps", "Biofuels", "Other", "Sector:EGU", "Sector:Industry", "Sector:Industry-fuels",
        "Sector:Buildings", "Sector:Trn-Onroad", "Sector:Trn-ALM", "Sector:Other"
    };
    private static final String[] APPLIED_TO_OPTIONS = {SELECT_ONE, "All Stock", "New Purchases"};
    private static final String[] CONSTRAINT_OPTIONS = {"Lower", "Fixed"};
    private static final String[] TREATMENT_OPTIONS = {SELECT_ONE, "Each Selected Region", "Across Selected Regions"};
    private static final String[] MODIFICATION_TYPE_OPTIONS = {
        "Initial and Final %", "Initial w/% Growth/yr", "Initial w/% Growth/pd",
        "Initial w/Delta/yr", "Initial w/Delta/pd"
    };

    // === Labels and Controls ===
    private final Label labelSubsetFilter = createLabel("Subset Filter:", LABEL_WIDTH);
    private final TextField textFieldSubsetFilter = createTextField();
    private final Label labelSupersetFilter = createLabel("Superset Filter:", LABEL_WIDTH);
    private final TextField textFieldSupersetFilter = createTextField();
    private final Label labelPolicyType = createLabel("Type?", LABEL_WIDTH);
    private final ComboBox<String> comboBoxPolicyType = createComboBoxString();
    private final Label labelSubset = createLabel("Subset: ", LABEL_WIDTH);
    private final CheckComboBox<String> checkComboBoxSubset = createCheckComboBox();
    private final Label labelSuperset = createLabel("Superset: ", LABEL_WIDTH);
    private final CheckComboBox<String> checkComboBoxSuperset = createCheckComboBox();
    private final Label labelAppliedTo = createLabel("Applied to: ", LABEL_WIDTH);
    private final ComboBox<String> comboBoxAppliedTo = createComboBoxString();
    private final Label labelConstraint = createLabel("Constraint: ", LABEL_WIDTH);
    private final ComboBox<String> comboBoxConstraint = createComboBoxString();
    private final Label labelTreatment = createLabel("Treatment: ", LABEL_WIDTH);
    private final ComboBox<String> comboBoxTreatment = createComboBoxString();

    // === Constants for Metadata ===
    private static final String METADATA_HEADER = "########## Scenario Component Metadata ##########";
    private static final String METADATA_FOOTER = "#################################################";
    private static final String METADATA_SCENARIO_TYPE = "#Scenario component type: Market Share";
    private static final String METADATA_TYPE = "#Type: ";
    private static final String METADATA_SUBSET = "#Subset: ";
    private static final String METADATA_SUPERSET = "#Superset: ";
    private static final String METADATA_APPLIED_TO = "#Applied to: ";
    private static final String METADATA_TREATMENT = "#Treatment: ";
    private static final String METADATA_CONSTRAINT = "#Constraint: ";
    private static final String METADATA_POLICY_NAME = "#Policy name: ";
    private static final String METADATA_MARKET_NAME = "#Market name: ";
    private static final String METADATA_REGIONS = "#Regions: ";
    private static final String METADATA_TABLE_DATA = "#Table data:";

    /**
     * Constructs a TabMarketShare instance for the given scenario builder tab.
     * @param title The tab title.
     * @param stageX The JavaFX stage.
     * @param pane The parent pane.
     */
    public TabMarketShare(String title, Stage stageX, PaneNewScenarioComponent pane) {
        this.setText(title);
        this.setStyle(styles.getFontStyle());
        initializeUI();
        setPolicyAndMarketNames();
        VBox tabLayout = new VBox();
        tabLayout.getChildren().addAll(gridPanePresetModification);
        this.setContent(tabLayout);
    }

    /**
     * Initializes the UI components and layout for the tab.
     */
    private void initializeUI() {
        // Set up initial state
        checkBoxUseAutoNames.setSelected(true);
        textFieldPolicyName.setDisable(true);
        textFieldMarketName.setDisable(true);
        // ComboBox options
        comboBoxPolicyType.getItems().addAll(POLICY_TYPE_OPTIONS);
        comboBoxPolicyType.getSelectionModel().select(0);
        checkComboBoxSubset.getItems().addAll(SELECT_ONE_OR_MORE);
        checkComboBoxSuperset.getItems().addAll(SELECT_ONE_OR_MORE);
        checkComboBoxSubset.getCheckModel().check(0);
        checkComboBoxSuperset.getCheckModel().check(0);
        checkComboBoxSubset.setPrefWidth(70);
        checkComboBoxSuperset.setPrefWidth(70);
        checkComboBoxSubset.setMaxWidth(70);
        checkComboBoxSuperset.setMaxWidth(70);
        comboBoxAppliedTo.getItems().addAll(APPLIED_TO_OPTIONS);
        comboBoxAppliedTo.getSelectionModel().select("All Stock");
        comboBoxConstraint.getItems().addAll(CONSTRAINT_OPTIONS);
        comboBoxConstraint.getSelectionModel().selectFirst();
        comboBoxTreatment.getItems().addAll(TREATMENT_OPTIONS);
        comboBoxTreatment.getSelectionModel().selectFirst();
        comboBoxModificationType.getItems().addAll(MODIFICATION_TYPE_OPTIONS);
        comboBoxModificationType.getSelectionModel().selectFirst();
        // Sizing
        setComboBoxWidths();
        // UI Layout
        setupLeftColumn();
        setupCenterColumn();
        setupRightColumn();
        gridPanePresetModification.addColumn(0, scrollPaneLeft);
        gridPanePresetModification.addColumn(1, vBoxCenter);
        gridPanePresetModification.addColumn(2, vBoxRight);
        gridPaneLeft.setPrefWidth(325);
        gridPaneLeft.setMinWidth(325);
        vBoxCenter.setPrefWidth(300);
        vBoxRight.setPrefWidth(300);
        // Widget actions
        setupWidgetActions();
    }

    /**
     * Sets the min and max widths for all ComboBoxes.
     */
    private void setComboBoxWidths() {
        Object[] comboBoxes = {
            comboBoxPolicyType, checkComboBoxSubset, checkComboBoxSuperset,
            comboBoxAppliedTo, comboBoxModificationType, comboBoxConstraint, comboBoxTreatment
        };
        for (Object cb : comboBoxes) {
            if (cb instanceof ComboBox) {
                ((ComboBox<?>) cb).setMaxWidth(MAX_WIDTH);
                ((ComboBox<?>) cb).setMinWidth(MIN_WIDTH);
            } else if (cb instanceof CheckComboBox) {
                ((CheckComboBox<?>) cb).setMaxWidth(MAX_WIDTH);
                ((CheckComboBox<?>) cb).setMinWidth(MIN_WIDTH);
            }
        }
    }

    /**
     * Sets up the left column of the UI with labels and input controls for policy specification and population.
     */
    private void setupLeftColumn() {
        gridPaneLeft.add(utils.createLabel("Specification:"), 0, 0, 2, 1);
        gridPaneLeft.addColumn(0, labelPolicyType, labelSubsetFilter, labelSubset, labelSupersetFilter, labelSuperset, labelConstraint,
                labelAppliedTo, labelTreatment, new Separator(), labelUseAutoNames, labelPolicyName,
                labelMarketName, new Separator(), utils.createLabel("Populate:"), labelModificationType, labelStartYear, labelEndYear,
                labelInitialAmount, labelGrowth);
        gridPaneLeft.addColumn(1, comboBoxPolicyType, textFieldSubsetFilter, checkComboBoxSubset, textFieldSupersetFilter, checkComboBoxSuperset,
                comboBoxConstraint, comboBoxAppliedTo, comboBoxTreatment, new Separator(),
                checkBoxUseAutoNames, textFieldPolicyName, textFieldMarketName, new Separator(),
                new Label(), comboBoxModificationType, textFieldStartYear, textFieldEndYear, textFieldInitialAmount,
                textFieldGrowth);
        gridPaneLeft.setAlignment(Pos.TOP_LEFT);
        gridPaneLeft.setVgap(3.);
        gridPaneLeft.setStyle(styles.getStyle2());
        scrollPaneLeft.setContent(gridPaneLeft);
    }

    /**
     * Sets up the center column of the UI with value controls and action buttons.
     */
    private void setupCenterColumn() {
        hBoxHeaderCenter.getChildren().addAll(buttonPopulate, buttonDelete, buttonClear);
        hBoxHeaderCenter.setSpacing(2.);
        hBoxHeaderCenter.setStyle(styles.getStyle3());
        vBoxCenter.getChildren().addAll(labelValue, hBoxHeaderCenter, paneForComponentDetails);
        vBoxCenter.setStyle(styles.getStyle2());
    }

    /**
     * Sets up the right column of the UI with the country/state tree.
     */
    private void setupRightColumn() {
        vBoxRight.getChildren().addAll(paneForCountryStateTree);
        vBoxRight.setStyle(styles.getStyle2());
    }

    /**
     * Sets up widget actions and event handlers for UI controls.
     */
    private void setupWidgetActions() {
        setOnMouseClicked(labelSubset, e -> {
            if (!checkComboBoxSubset.isDisabled()) {
                boolean isFirstItemChecked = checkComboBoxSubset.getCheckModel().isChecked(0);
                if (e.getClickCount() == 2) {
                    if (isFirstItemChecked) {
                        checkComboBoxSubset.getCheckModel().clearChecks();
                    } else {
                        checkComboBoxSubset.getCheckModel().checkAll();
                    }
                }
            }
        });
        setOnMouseClicked(labelSuperset, e -> {
            if (!checkComboBoxSuperset.isDisabled()) {
                boolean isFirstItemChecked = checkComboBoxSuperset.getCheckModel().isChecked(0);
                if (e.getClickCount() == 2) {
                    if (isFirstItemChecked) {
                        checkComboBoxSuperset.getCheckModel().clearChecks();
                    } else {
                        checkComboBoxSuperset.getCheckModel().checkAll();
                    }
                }
            }
        });
        setOnAction(comboBoxPolicyType, e -> {
            String selectedItem = comboBoxPolicyType.getValue();
            if (selectedItem.equals(SELECT_ONE)) {
                checkComboBoxSubset.getCheckModel().clearChecks();
                checkComboBoxSubset.getItems().clear();
                checkComboBoxSuperset.getCheckModel().clearChecks();
                checkComboBoxSuperset.getItems().clear();
                checkComboBoxSubset.getItems().add(SELECT_ONE_OR_MORE);
                checkComboBoxSuperset.getItems().add(SELECT_ONE_OR_MORE);
                checkComboBoxSubset.getCheckModel().check(0);
                checkComboBoxSuperset.getCheckModel().check(0);
                checkComboBoxSubset.setDisable(true);
                checkComboBoxSuperset.setDisable(true);
            } else {
                setupCheckComboBoxes(selectedItem);
                checkComboBoxSubset.setDisable(false);
                checkComboBoxSuperset.setDisable(false);
                if ((selectedItem.contains("RPS")) || (selectedItem.contains("CES"))) {
                    showInfo("For RPS and CES options:\nTechnology selections chosen automatically. Modify as needed.", "Information");
                }
            }
            setPolicyAndMarketNames();
        });
        setOnAction(textFieldSubsetFilter, e -> setupCheckComboBoxes());
        setOnAction(textFieldSupersetFilter, e -> setupCheckComboBoxes());
        setOnAction(comboBoxAppliedTo, e -> setPolicyAndMarketNames());
        setOnAction(comboBoxTreatment, e -> setPolicyAndMarketNames());
        setOnAction(comboBoxConstraint, e -> setPolicyAndMarketNames());
        EventHandler<TreeModificationEvent<String>> ev = new EventHandler<TreeModificationEvent<String>>() {
            @Override
            public void handle(TreeModificationEvent<String> ae) {
                ae.consume();
                setPolicyAndMarketNames();
            }
        };
        paneForCountryStateTree.addEventHandlerToAllLeafs(ev);
        setOnAction(checkBoxUseAutoNames, e -> {
            boolean selected = checkBoxUseAutoNames.isSelected();
            textFieldPolicyName.setDisable(selected);
            textFieldMarketName.setDisable(selected);
        });
        setOnAction(comboBoxModificationType, e -> {
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
                case "Initial and Final %":
                    labelGrowth.setText("Final (%):");
                    break;
                default:
                    break;
            }
        });
        setOnAction(buttonClear, e -> paneForComponentDetails.clearTable());
        setOnAction(buttonDelete, e -> paneForComponentDetails.deleteItemsFromTable());
        setOnAction(buttonPopulate, e -> {
            if (qaPopulate()) {
                double[][] values = calculateValues();
                paneForComponentDetails.setValues(values);
            }
        });
    }

    /**
     * Sets up the subset and superset check combo boxes based on the selected policy type and filters.
     */
    private void setupCheckComboBoxes() {
        String item = comboBoxPolicyType.getSelectionModel().getSelectedItem();
        setupCheckComboBoxes(item);
    }

    /**
     * Sets up the subset and superset check combo boxes for a given policy type.
     * @param selectedItem The selected policy type.
     */
    private void setupCheckComboBoxes(String selectedItem) {
        String[][] techInfo = vars.getTechInfo();
        if (techInfo == null) return;
        try {
            List<String> techListSub = new ArrayList<>();
            List<String> techListSup = new ArrayList<>();
            String filterTextSub = textFieldSubsetFilter.getText() != null ? textFieldSubsetFilter.getText().trim() : "";
            String filterTextSup = textFieldSupersetFilter.getText() != null ? textFieldSupersetFilter.getText().trim() : "";
            boolean useFilterSub = !filterTextSub.isEmpty();
            boolean useFilterSup = !filterTextSup.isEmpty();
            String lastLine = "";
            for (String[] tech : techInfo) {
                String line = tech[0].trim() + " : " + tech[1] + " : " + tech[2];
                if (line.equals(lastLine)) continue;
                lastLine = line;
                if (tech.length >= 7) {
                    line += " : " + tech[6] + " : " + tech[7];
                }
                boolean showSub = !useFilterSub;
                if (useFilterSub) {
                    for (String temp : tech) {
                        if (temp.contains(filterTextSub)) {
                            showSub = true;
                            break;
                        }
                    }
                }
                if (showSub) techListSub.add(line.trim());
                boolean showSup = !useFilterSup;
                if (useFilterSup) {
                    for (String temp : tech) {
                        if (temp.contains(filterTextSup)) {
                            showSup = true;
                            break;
                        }
                    }
                }
                if (showSup) techListSup.add(line.trim());
            }
            checkComboBoxSubset.getCheckModel().clearChecks();
            checkComboBoxSubset.getItems().clear();
            checkComboBoxSuperset.getCheckModel().clearChecks();
            checkComboBoxSuperset.getItems().clear();
            boolean showEgu = false;
            boolean showLdvCar = false;
            boolean showLdvTruck = false;
            boolean showLdv4w = false;
            boolean showLdvAll = false;
            boolean showHdvAll = false;
            boolean showHdvLight = false;
            boolean showHdvMedium = false;
            boolean showHdvHeavy = false;
            boolean showLighting = false;
            boolean showHeating = false;
            boolean showRefining = false;
            boolean showSectorEgu = false;
            boolean showSectorBuildings = false;
            boolean showSectorIndustry = false;
            boolean showSectorIndustryFuels = false;
            boolean showSectorTrnOnroad = false;
            boolean showSectorTrnAlm = false;
            boolean showSectorOther = false;
            String policyType = comboBoxPolicyType.getValue();
            if (policyType.contains("CES")) {
                showEgu = true;
            }
            if (policyType.contains("RPS")) {
                showEgu = true;
            }
            if (policyType.equals("EV passenger cars and trucks"))
                showLdv4w = true;
            if (policyType.equals("EV passenger cars trucks and MCs"))
                showLdvAll = true;
            if (policyType.equals("EV freight light truck"))
                showHdvLight = true;
            if (policyType.equals("EV freight medium truck"))
                showHdvMedium = true;
            if (policyType.equals("EV freight heavy truck"))
                showHdvHeavy = true;
            if (policyType.equals("EV freight all trucks"))
                showHdvAll = true;
            if (policyType.equals("LED lights"))
                showLighting = true;
            if (policyType.equals("Heat pumps"))
                showHeating = true;
            if (policyType.equals("Biofuels"))
                showRefining = true;
            if (policyType.equals("Sector:EGU"))
                showSectorEgu = true;
            if (policyType.equals("Sector:Buildings"))
                showSectorBuildings = true;
            if (policyType.equals("Sector:Industry"))
                showSectorIndustry = true;
            if (policyType.equals("Sector:Industry-fuels"))
                showSectorIndustryFuels = true;
            if (policyType.equals("Sector:Trn-Onroad"))
                showSectorTrnOnroad = true;
            if (policyType.equals("Sector:Trn-ALM"))
                showSectorTrnAlm = true;
            if (policyType.equals("Sector:Other"))
                showSectorTrnAlm = true;
            for (String techLine : techListSub) {
                boolean showTech = false;
                String techLineLc = techLine.toLowerCase();
                if (showEgu) {
                    if (techLineLc.startsWith("electricity ")) {
                        showTech = true;
                    } else if (techLineLc.startsWith("base load")) {
                        showTech = true;
                    } else if (techLineLc.startsWith("intermediate")) {
                        showTech = true;
                    } else if (techLineLc.startsWith("peak")) {
                        showTech = true;
                    } else if (techLineLc.startsWith("subpeak")) {
                        showTech = true;
                    } else if (techLineLc.startsWith("elec_")) {
                        showTech = true;
                    } else if (techLineLc.indexOf("cogen") > -1) {
                        showTech = true;
                    }
                } else if (showLdvTruck) {
                    if (techLineLc.contains("large car and truck")) {
                        showTech = true;
                    }
                } else if (showLdvCar) {
                    if (techLineLc.contains(": car :")) {
                        showTech = true;
                    }
                } else if (showLdv4w) {
                    if (techLineLc.indexOf("ldv_4w") > -1) {
                        showTech = true;
                    }
                } else if (showLdvAll) {
                    if (techLineLc.indexOf("ldv") > -1) {
                        showTech = true;
                    }
                } else if (showHdvLight) {
                    if (techLineLc.startsWith("trn_freight_road")) {
                        if (techLineLc.contains("light"))
                            showTech = true;
                    }
                } else if (showHdvMedium) {
                    if (techLineLc.startsWith("trn_freight_road")) {
                        if (techLineLc.contains("medium"))
                            showTech = true;
                    }
                } else if (showHdvHeavy) {
                    if (techLineLc.startsWith("trn_freight_road")) {
                        if (techLineLc.contains("heavy"))
                            showTech = true;
                    }
                } else if (showHdvAll) {
                    if (techLineLc.startsWith("trn_freight_road")) {
                        showTech = true;
                    }
                } else if (showLighting) {
                    if ((techLineLc.startsWith("resid lighting")) || (techLineLc.startsWith("comm lighting"))) {
                        showTech = true;
                    }
                } else if (showHeating) {
                    if ((techLineLc.startsWith("resid heating")) || (techLineLc.startsWith("comm heating"))) {
                        showTech = true;
                    }
                } else if (showRefining) {
                    if ((techLineLc.startsWith("oil refining")) || (techLineLc.startsWith("biomass liquids"))) {
                        showTech = true;
                    }
                } else if (showSectorEgu) {
                    if (techLineLc.endsWith("egu"))
                        showTech = true;

                } else if (showSectorIndustry) {
                    if (techLineLc.endsWith("industry"))
                        showTech = true;

                } else if (showSectorIndustryFuels) {
                    if (techLineLc.endsWith("industry-fuels"))
                        showTech = true;

                } else if (showSectorBuildings) {
                    if (techLineLc.endsWith("buildings"))
                        showTech = true;

                } else if (showSectorTrnOnroad) {
                    if (techLineLc.endsWith("trn-onroad"))
                        showTech = true;

                } else if (showSectorTrnAlm) {
                    if ((techLineLc.endsWith("trn-alm")) || (techLineLc.endsWith("trn-nonroad")))
                        showTech = true;

                } else if (showSectorOther) {
                    showTech = true;

                } else {
                    showTech = true;
                }
                if (showTech) {
                    checkComboBoxSubset.getItems().add(techLine);
                }
            }
            for (String techLine : techListSup) {
                boolean showTech = false;
                String techLineLc = techLine.toLowerCase();
                if (showEgu) {
                    if (techLineLc.startsWith("electricity ")) {
                        showTech = true;
                    } else if (techLineLc.startsWith("base load")) {
                        showTech = true;
                    } else if (techLineLc.startsWith("intermediate")) {
                        showTech = true;
                    } else if (techLineLc.startsWith("peak")) {
                        showTech = true;
                    } else if (techLineLc.startsWith("subpeak")) {
                        showTech = true;
                    } else if (techLineLc.startsWith("elec_")) {
                        showTech = true;
                    } else if (techLineLc.indexOf("cogen") > -1) {
                        showTech = true;
                    }
                } else if (showLdvTruck) {
                    if (techLineLc.contains("large car and truck")) {
                        showTech = true;
                    }
                } else if (showLdvCar) {
                    if (techLineLc.contains(": car :")) {
                        showTech = true;
                    }
                } else if (showLdv4w) {
                    if (techLineLc.indexOf("ldv_4w") > -1) {
                        showTech = true;
                    }
                } else if (showLdvAll) {
                    if (techLineLc.indexOf("ldv") > -1) {
                        showTech = true;
                    }
                } else if (showHdvLight) {
                    if (techLineLc.startsWith("trn_freight_road")) {
                        if (techLineLc.contains("light"))
                            showTech = true;
                    }
                } else if (showHdvMedium) {
                    if (techLineLc.startsWith("trn_freight_road")) {
                        if (techLineLc.contains("medium"))
                            showTech = true;
                    }
                } else if (showHdvHeavy) {
                    if (techLineLc.startsWith("trn_freight_road")) {
                        if (techLineLc.contains("heavy"))
                            showTech = true;
                    }
                } else if (showHdvAll) {
                    if (techLineLc.startsWith("trn_freight_road")) {
                        showTech = true;
                    }
                } else if (showLighting) {
                    if ((techLineLc.startsWith("resid lighting")) || (techLineLc.startsWith("comm lighting"))) {
                        showTech = true;
                    }
                } else if (showHeating) {
                    if ((techLineLc.startsWith("resid heating")) || (techLineLc.startsWith("comm heating"))) {
                        showTech = true;
                    }
                } else if (showRefining) {
                    if ((techLineLc.startsWith("oil refining")) || (techLineLc.startsWith("biomass liquids"))) {
                        showTech = true;
                    }
                } else if (showSectorEgu) {
                    if (techLineLc.endsWith("egu"))
                        showTech = true;

                } else if (showSectorIndustry) {
                    if (techLineLc.endsWith("industry"))
                        showTech = true;

                } else if (showSectorIndustryFuels) {
                    if (techLineLc.endsWith("industry-fuels"))
                        showTech = true;

                } else if (showSectorBuildings) {
                    if (techLineLc.endsWith("buildings"))
                        showTech = true;

                } else if (showSectorTrnOnroad) {
                    if (techLineLc.endsWith("trn-onroad"))
                        showTech = true;

                } else if (showSectorTrnAlm) {
                    if ((techLineLc.endsWith("trn-alm")) || (techLineLc.endsWith("trn-nonroad")))
                        showTech = true;

                } else if (showSectorOther) {
                    showTech = true;

                } else {
                    showTech = true;
                }
                if (showTech) {
                    checkComboBoxSuperset.getItems().add(techLine);
                }
            }
            if ((policyType.contains("RPS")) || (policyType.contains("CES"))) {
                for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
                    String itemText = checkComboBoxSubset.getItems().get(i).toLowerCase();
                    if ((itemText.indexOf("solar") >= 0) || (itemText.indexOf("csp") >= 0)
                            || (itemText.indexOf("pv") >= 0))
                        checkComboBoxSubset.getCheckModel().check(i);
                    if (itemText.indexOf("wind") >= 0)
                        checkComboBoxSubset.getCheckModel().check(i);
                    if ((itemText.indexOf("hydro") >= 0) && (itemText.indexOf("hydrogen") < 0))
                        checkComboBoxSubset.getCheckModel().check(i);
                    if (itemText.indexOf("geothermal") >= 0)
                        checkComboBoxSubset.getCheckModel().check(i);
                    if (itemText.indexOf("biomass") >= 0)
                        checkComboBoxSubset.getCheckModel().check(i);
                }
                checkComboBoxSuperset.getCheckModel().checkAll();
            }
            if (policyType.contains("CES")) {
                for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
                    String itemText = checkComboBoxSubset.getItems().get(i).toLowerCase();
                    if (itemText.indexOf("ccs") >= 0)
                        checkComboBoxSubset.getCheckModel().check(i);
                    if (itemText.indexOf("nuclear") >= 0)
                        checkComboBoxSubset.getCheckModel().check(i);
                }
            }
            if (policyType.contains("EV")) {
                for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
                    String itemText = checkComboBoxSubset.getItems().get(i).toLowerCase();
                    if (itemText.indexOf("bev") >= 0)
                        checkComboBoxSubset.getCheckModel().check(i);
                }
                checkComboBoxSuperset.getCheckModel().checkAll();
            }
            if (policyType.startsWith("EV")) {
                for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
                    String itemText = checkComboBoxSubset.getItems().get(i).toLowerCase();
                    if (itemText.indexOf("bev") >= 0)
                        checkComboBoxSubset.getCheckModel().check(i);
                }
                checkComboBoxSuperset.getCheckModel().checkAll();
            }
            if (policyType.contains("LED")) {
                for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
                    String itemText = checkComboBoxSubset.getItems().get(i).toLowerCase();
                    if (itemText.indexOf("solid state") >= 0)
                        checkComboBoxSubset.getCheckModel().check(i);
                }
                checkComboBoxSuperset.getCheckModel().checkAll();
            }
            if (policyType.contains("Heat pump")) {
                for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
                    String itemText = checkComboBoxSubset.getItems().get(i).toLowerCase();
                    if (itemText.indexOf("heat pump") >= 0)
                        checkComboBoxSubset.getCheckModel().check(i);
                }
                checkComboBoxSuperset.getCheckModel().checkAll();
            }
            if (policyType.contains("Biofuel")) {
                for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
                    String itemText = checkComboBoxSubset.getItems().get(i).toLowerCase();
                    if (itemText.indexOf("bio") >= 0)
                        checkComboBoxSubset.getCheckModel().check(i);
                }
                checkComboBoxSuperset.getCheckModel().checkAll();
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
        Platform.runLater(() -> {
            if (checkBoxUseAutoNames.isSelected()) {
                String policyType = comboBoxPolicyType.getValue() != null ? comboBoxPolicyType.getValue().replace(" ", "-").replace("(", "-").replace(")", "").replace(":", "") : "--";
                if (policyType.equals("Other")) policyType = "Share";
                if (policyType.equals("SelectOne")) policyType = "---";
                String toWhich = "--";
                String state = "--";
                String treatment = "--";
                try {
                    String s = comboBoxAppliedTo.getValue();
                    if (s != null && s.contains("New")) toWhich = "New";
                    if (s != null && s.contains("All")) toWhich = "All";
                    s = comboBoxTreatment.getValue();
                    if (s != null && s.contains("Each")) treatment = "_Ea";
                    if (s != null && s.contains("Across")) treatment = "";
                    String[] listOfSelectedLeaves = utils.getAllSelectedRegions(paneForCountryStateTree.getTree());
                    listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
                    String stateStr = utils.returnAppendedString(listOfSelectedLeaves).replace(",", "");
                    if (stateStr.length() < 9) {
                        state = stateStr;
                    } else {
                        state = "Reg";
                    }
                    String name = policyType + "_" + toWhich + "_" + state + treatment;
                    name = name.replaceAll(" ", "_").replaceAll("--", "-");
                    textFieldMarketName.setText(name + "_Mkt");
                    textFieldPolicyName.setText(name);
                } catch (Exception e) {
                    // Ignore auto-naming errors
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
     * Saves the scenario component using the selected regions from the country/state tree.
     */
    @Override
    public void saveScenarioComponent() {
        saveScenarioComponent(paneForCountryStateTree.getTree());
    }

    /**
     * Saves the scenario component using the provided tree of selected regions.
     * @param tree The TreeView containing selected regions.
     */
    private void saveScenarioComponent(TreeView<String> tree) {
        if (!qaInputs()) {
            Thread.currentThread().destroy();
            return;
        }

        this.comboBoxConstraint.getValue().toLowerCase();
        String ID = utils.getUniqueString();
        String policyName = this.textFieldPolicyName.getText() + ID;
        String marketName = this.textFieldMarketName.getText() + ID;
        filenameSuggestion = this.textFieldPolicyName.getText().replaceAll("/", "-").replaceAll(" ", "_") + ".csv";

        String tempDirName = vars.getGlimpseDir() + File.separator + "GLIMPSE-Data" + File.separator + "temp";
        File test = new File(tempDirName);
        if (!test.exists()) test.mkdir();

        String tempFilename0 = "temp_policy_file0.txt";
        String tempFilename1 = "temp_policy_file1.txt";
        String tempFilename2 = "temp_policy_file2.txt";

        BufferedWriter bw0 = files.initializeBufferedFile(tempDirName, tempFilename0);
        BufferedWriter bw1 = files.initializeBufferedFile(tempDirName, tempFilename1);
        BufferedWriter bw2 = files.initializeBufferedFile(tempDirName, tempFilename2);

        fileContent = "use temp file";
        files.writeToBufferedFile(bw0, getMetaDataContent(tree, marketName, policyName));

        String[] listOfSelectedLeaves = utils.getAllSelectedRegions(tree);
        listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);

        checkComboBoxSubset.getCheckModel().getCheckedItems();
        ObservableList<String> supersetList = checkComboBoxSuperset.getCheckModel().getCheckedItems();

        ArrayList<String> dataArrayList = this.paneForComponentDetails.getDataYrValsArrayList();
        String[] yearList = new String[dataArrayList.size()];
        String[] valueList = new String[dataArrayList.size()];
        double[] valuefList = new double[dataArrayList.size()];

        for (int i = 0; i < dataArrayList.size(); i++) {
            String str = dataArrayList.get(i).replaceAll(" ", "").trim();
            yearList[i] = utils.splitString(str, ",")[0];
            valueList[i] = utils.splitString(str, ",")[1];
            valuefList[i] = Double.parseDouble(valueList[i]);
        }

        int startYear = 2010;
        try {
            startYear = Integer.parseInt(vars.getStartYearForShare());
        } catch (Exception e) {
            System.out.println("Problem converting startYearForShare to int. Using default value of 2010.");
        }

        StringBuilder part1Builder = new StringBuilder();
        part1Builder.append(vars.getEol())
            .append("INPUT_TABLE").append(vars.getEol())
            .append("Variable ID").append(vars.getEol())
            .append("GLIMPSEPFStdAdjCoef-v2").append(vars.getEol()).append(vars.getEol())
            .append("region,sector,subsector,tech,year,policy,adjcoef-year,adjcoef,unit-price-conv").append(vars.getEol());

        for (String state : listOfSelectedLeaves) {
            for (int t = startYear; t < 2100; t += 5) {
                String usePolicyName = policyName;
                if (comboBoxTreatment.getValue().toLowerCase().trim().equals("each selected region") && listOfSelectedLeaves.length >= 2) {
                    usePolicyName = state + "_" + policyName;
                }
                if (comboBoxAppliedTo.getValue().toLowerCase().trim().equals("new purchases")) {
                    usePolicyName += "-" + t;
                }

                for (int i = 0; i < yearList.length; i++) {
                    if (((t <= Integer.parseInt(yearList[i])) && comboBoxAppliedTo.getValue().toLowerCase().trim().equals("all stock")) ||
                        ((t == Integer.parseInt(yearList[i])) && comboBoxAppliedTo.getValue().toLowerCase().trim().equals("new purchases"))) {

                        for (String temp : supersetList) {
                            String[] tempi = utils.splitString(temp, ":");
                            String sectorName = tempi[0].trim();
                            String subsectorName = tempi[1].trim();
                            String techName = tempi[2].trim();

                            double val = valuefList[i];
                            String conv = "1.0";
                            if (sectorName.startsWith("trn")) {
                                conv = "1e-3";
                                val *= 1000.;
                            }

                            part1Builder.append(state).append(",")
                                .append(sectorName).append(",")
                                .append(subsectorName).append(",")
                                .append(techName).append(",")
                                .append(t).append(",")
                                .append(usePolicyName).append(",")
                                .append(yearList[i]).append(",")
                                .append(val).append(",")
                                .append(conv).append(vars.getEol());
                        }
                    }
                }
            }
        }

        files.writeToBufferedFile(bw1, part1Builder.toString());

        files.closeBufferedFile(bw0);
        files.closeBufferedFile(bw1);
        files.closeBufferedFile(bw2);

        System.out.println("Done");
    }

    /**
     * Generates the metadata content for the scenario component, including selected options and table data.
     * @param tree The TreeView containing selected regions.
     * @param market The market name.
     * @param policy The policy name.
     * @return The metadata content as a String.
     */
    public String getMetaDataContent(TreeView<String> tree, String market, String policy) {
        String rtn_str = "";
        rtn_str += METADATA_HEADER + vars.getEol();
        rtn_str += METADATA_SCENARIO_TYPE + vars.getEol();
        rtn_str += METADATA_TYPE + comboBoxPolicyType.getValue() + vars.getEol();
        ObservableList subset_list = checkComboBoxSubset.getCheckModel().getCheckedItems();
        String subset = utils.getStringFromList(subset_list, ";");
        rtn_str += METADATA_SUBSET + subset + vars.getEol();
        ObservableList superset_list = checkComboBoxSuperset.getCheckModel().getCheckedItems();
        String superset = utils.getStringFromList(superset_list, ";");
        rtn_str += METADATA_SUPERSET + superset + vars.getEol();
        rtn_str += METADATA_APPLIED_TO + comboBoxAppliedTo.getValue() + vars.getEol();
        rtn_str += METADATA_TREATMENT + comboBoxTreatment.getValue() + vars.getEol();
        rtn_str += METADATA_CONSTRAINT + comboBoxConstraint.getValue() + vars.getEol();
        if (policy == null)
            market = textFieldPolicyName.getText();
        rtn_str += METADATA_POLICY_NAME + policy + vars.getEol();
        if (market == null)
            market = textFieldMarketName.getText();
        rtn_str += METADATA_MARKET_NAME + market + vars.getEol();
        String[] listOfSelectedLeaves = utils.getAllSelectedRegions(tree);
        listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
        String states = utils.returnAppendedString(listOfSelectedLeaves);
        rtn_str += METADATA_REGIONS + states + vars.getEol();
        ArrayList<String> table_content = this.paneForComponentDetails.getDataYrValsArrayList();
        for (int i = 0; i < table_content.size(); i++) {
            rtn_str += METADATA_TABLE_DATA + table_content.get(i) + vars.getEol();
        }
        rtn_str += METADATA_FOOTER + vars.getEol();
        return rtn_str;
    }

    /**
     * Loads the content of a scenario component from a list of strings, updating the UI accordingly.
     * @param content The content to load.
     */
    @Override
    public void loadContent(ArrayList<String> content) {
        for (int i = 0; i < content.size(); i++) {
            String line = content.get(i);
            int pos = line.indexOf(":");
            if (line.startsWith("#") && (pos > -1)) {
                String param = line.substring(1, pos).trim().toLowerCase();
                String value = line.substring(pos + 1).trim();

                if ((param.contains("type"))&&(!param.startsWith("Scenario component"))) {
                    comboBoxPolicyType.setValue(value);
                    comboBoxPolicyType.fireEvent(new ActionEvent());
                } else 
                if (param.equals("applied to")) {
                    comboBoxAppliedTo.setValue(value);
                    comboBoxAppliedTo.fireEvent(new ActionEvent());
                } else 
                if (param.equals("treatment")) {
                    comboBoxTreatment.setValue(value);
                    comboBoxTreatment.fireEvent(new ActionEvent());
                } else 
                if (param.equals("constraint")) {
                    comboBoxConstraint.setValue(value);
                    comboBoxConstraint.fireEvent(new ActionEvent());
                } else 
                if (param.equals("policy name")) {
                    textFieldPolicyName.setText(value);
                    textFieldPolicyName.fireEvent(new ActionEvent());
                } else 
                if (param.equals("market name")) {
                    textFieldMarketName.setText(value);
                    textFieldMarketName.fireEvent(new ActionEvent());
                } else 
                if (param.equals("subset")) {
                    checkComboBoxSubset.getCheckModel().clearChecks();
                    String[] set = utils.splitString(value, ";");
                    for (int j = 0; j < set.length; j++) {
                        String item = set[j].trim();
                        //System.out.println("Attempting to check >>"+item+"<<");
                        checkComboBoxSubset.getCheckModel().check(item);
                    }
                    checkComboBoxSubset.fireEvent(new ActionEvent());
                } else 
                if (param.equals("superset")) {
                    checkComboBoxSuperset.getCheckModel().clearChecks();
                    String[] set = utils.splitString(value, ";");
                    for (int j = 0; j < set.length; j++) {
                        String item = set[j].trim();
                        checkComboBoxSuperset.getCheckModel().check(item);
                    }
                    checkComboBoxSuperset.fireEvent(new ActionEvent());
                } else 
                if (param.equals("regions")) {
                    String[] regions = utils.splitString(value, ",");
                    this.paneForCountryStateTree.selectNodes(regions);
                } else 
                if (param.equals("table data")) {
                    String[] s = utils.splitString(value, ",");
                    this.paneForComponentDetails.data.add(new DataPoint(s[0], s[1]));
                }

            }
        }
        this.paneForComponentDetails.updateTable();
    }

    /**
     * Performs QA checks to ensure all required fields for populating the table are filled.
     * @return true if all required fields are filled, false otherwise.
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
     * Performs QA checks to ensure all required inputs for saving the scenario component are valid.
     * @return true if all required inputs are valid, false otherwise.
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
            if ((checkComboBoxSubset.getCheckModel().getItemCount() == 1)
                    && (checkComboBoxSubset.getCheckModel().isChecked("Select One or More"))) {
                message += "Subset checkCombox must have at least one selection" + vars.getEol();
                error_count++;
            }
            if (checkComboBoxSubset.getCheckModel().getItemCount() == 0) {
                message += "Subset checkCombox must have at least one selection" + vars.getEol();
                error_count++;
            }
            if ((checkComboBoxSuperset.getCheckModel().getItemCount() == 1)
                    && (checkComboBoxSuperset.getCheckModel().isChecked("Select One or More"))) {
                message += "Superset checkCombox must have at least one selection" + vars.getEol();
                error_count++;
            }
            if (checkComboBoxSuperset.getCheckModel().getItemCount() == 0) {
                message += "Superset checkCombox must have at least one selection" + vars.getEol();
                error_count++;
            }
            if (comboBoxAppliedTo.getSelectionModel().getSelectedItem().equals("Select One")) {
                message += "All comboBoxes must have a selection" + vars.getEol();
                error_count++;
            }
            if (comboBoxTreatment.getSelectionModel().getSelectedItem().equals("Select One")) {
                message += "All comboBoxes must have a selection" + vars.getEol();
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
            // check to make sure units match
            if ((checkComboBoxSubset.getCheckModel().getItemCount() >= 1)
                    && (checkComboBoxSuperset.getCheckModel().getItemCount() >= 1)) {
                try {
                    ObservableList<String> checkBoxSubsetItems = checkComboBoxSubset.getCheckModel().getCheckedItems();
                    ObservableList<String> checkBoxSupersetItems = checkComboBoxSuperset.getCheckModel()
                            .getCheckedItems();
                    String[] items = checkBoxSubsetItems.get(0).split(":");
                    String units = null;
                    if (items.length == 4)
                        units = items[3].trim();
                    if (units != null) {
                        for (int i = 0; i < checkBoxSubsetItems.size(); i++) {
                            items = checkBoxSubsetItems.get(0).split(":");
                            if (!items[3].trim().equals(units)) {
                                message += "Units of selected items must match: e.g., " + items[3] + "!=" + units;
                                error_count++;
                            }
                        }
                        for (int i = 0; i < checkBoxSupersetItems.size(); i++) {
                            items = checkBoxSupersetItems.get(0).split(":");
                            if (!items[3].trim().equals(units)) {
                                message += "Units of selected items must match: e.g., " + items[3] + "!=" + units;
                                error_count++;
                            }
                        }
                    }
                } catch (Exception except) {
                    System.out.println("Unable to verify that units of selected items match");
                }
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
     * Returns the suggested filename for the scenario component file.
     * This is used when saving the scenario component to disk.
     * @return The suggested filename as a String.
     */
    @Override
    public String getFilenameSuggestion() {
        return filenameSuggestion;
    }

    /**
     * Resets the filename suggestion to null. This should be called after saving or discarding the scenario component.
     */
    @Override
    public void resetFilenameSuggestion() {
        filenameSuggestion = null;
    }

    /**
     * Returns the file content for the scenario component. This is used when saving the scenario component to disk.
     * @return The file content as a String.
     */
    @Override
    public String getFileContent() {
        return fileContent;
    }

    /**
     * Resets the file content to null. This should be called after saving or discarding the scenario component.
     */
    @Override
    public void resetFileContent() {
        fileContent = null;
    }

    /**
     * Updates the progress bar on the JavaFX Application Thread.
     * @param progress The progress value to set (between 0.0 and 1.0).
     */
    private void updateProgressBar(double progress) {
        Platform.runLater(() -> progressBar.setProgress(progress));
    }
}
