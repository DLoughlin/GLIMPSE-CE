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
import gui.PaneNewScenarioComponent;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
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

public class TabMarketShare extends PolicyTab implements Runnable {
	private GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	private GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	private GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	private GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

//	public static String descriptionText = "";
//	public static String runQueueStr = "Queue is empty.";
//	
//	public CompletionListener completion_listener; 

	// Initializing overall grid
	GridPane gridPanePresetModification = new GridPane();

	// Initializing components of left column
	GridPane gridPaneLeft = new GridPane();
	ScrollPane scrollPaneLeft = new ScrollPane();	

	double label_wid = 125;

	Label labelSubsetFilter = utils.createLabel("Subset Filter:", label_wid);
	TextField textFieldSubsetFilter = utils.createTextField();

	Label labelSupersetFilter = utils.createLabel("Superset Filter:", label_wid);
	TextField textFieldSupersetFilter = utils.createTextField();
	
	// techs
	Label labelPolicyType = utils.createLabel("Type?", label_wid);
	ComboBox<String> comboBoxPolicyType = utils.createComboBoxString();

	Label labelSubset = utils.createLabel("Subset: ", label_wid);
	CheckComboBox<String> checkComboBoxSubset = utils.createCheckComboBox();
	// Button checkAllSubsetButton = utils.createButton("(Un)Check All", 25,
	// "(Un)check all subset items","check-uncheck" );

	Label labelSuperset = utils.createLabel("Superset: ", label_wid);
	CheckComboBox<String> checkComboBoxSuperset = utils.createCheckComboBox();
	// Button checkAllSupersetButton = utils.createButton("(Un)Check All", 25,
	// "(Un)check all superset items","check-uncheck" );

	// applied to
	Label labelAppliedTo = utils.createLabel("Applied to: ", label_wid);
	ComboBox<String> comboBoxAppliedTo = utils.createComboBoxString();

	// Constraint
	Label labelConstraint = utils.createLabel("Constraint: ", label_wid);
	ComboBox<String> comboBoxConstraint = utils.createComboBoxString();

	// Treatment
	Label labelTreatment = utils.createLabel("Treatment: ", label_wid);
	ComboBox<String> comboBoxTreatment = utils.createComboBoxString();

	// Policy and market names
	Label labelPolicyName = utils.createLabel("Policy: ", label_wid);
	TextField textFieldPolicyName = new TextField("");

	Label labelMarketName = utils.createLabel("Market: ", label_wid);
	TextField textFieldMarketName = new TextField("");

	Label labelUseAutoNames = utils.createLabel("Names: ", label_wid);
	CheckBox checkBoxUseAutoNames = utils.createCheckBox("Auto?");

	// widgets for specifying table data
	Label labelModificationType = utils.createLabel("Type: ", label_wid);
	ComboBox<String> comboBoxModificationType = utils.createComboBoxString();

	Label labelStartYear = utils.createLabel("Start Year: ", label_wid);
	TextField textFieldStartYear = new TextField("2020");
	Label labelEndYear = utils.createLabel("End Year: ", label_wid);
	TextField textFieldEndYear = new TextField("2050");
	Label labelInitialAmount = utils.createLabel("Initial (%): ", label_wid);
	TextField textFieldInitialAmount = utils.createTextField();
	Label labelGrowth = utils.createLabel("Final (%): ", label_wid);
	TextField textFieldGrowth = utils.createTextField();
	Label labelPeriodLength = utils.createLabel("Period Length: ", label_wid);
	TextField textFieldPeriodLength = new TextField("5");
	

	// Initializing components of center column
	VBox vBoxCenter = new VBox();
	HBox hBoxHeaderCenter = new HBox();
	Label labelValue = utils.createLabel("Values: ", label_wid);
	Button buttonPopulate = utils.createButton("Populate", styles.bigButtonWid, null);
	Button buttonImport = utils.createButton("Import", styles.bigButtonWid, null);
	Button buttonDelete = utils.createButton("Delete", styles.bigButtonWid, null);
	Button buttonClear = utils.createButton("Clear", styles.bigButtonWid, null);
	PaneForComponentDetails paneForComponentDetails = new PaneForComponentDetails();

	// Initializing components of right column
	HBox hBoxHeaderRight = new HBox();
	HBox hBoxFooterRight = new HBox();
	VBox vBoxRight = new VBox();
	PaneForCountryStateTree paneForCountryStateTree = new PaneForCountryStateTree();

	PaneNewScenarioComponent parent_pane = null;

	public TabMarketShare(String title, Stage stageX, PaneNewScenarioComponent pane) {

		parent_pane = pane;

		this.setText(title);
		this.setStyle(styles.font_style);

		// sets up initial state of check box and policy and market textfields
		checkBoxUseAutoNames.setSelected(true);
		textFieldPolicyName.setDisable(true);
		textFieldMarketName.setDisable(true);

		// initialize subset and superset choiceComboBoxes
		checkComboBoxSubset.getItems().addAll("Select One or More");
		checkComboBoxSuperset.getItems().addAll("Select One or More");
		checkComboBoxSubset.getCheckModel().check(0);
		checkComboBoxSuperset.getCheckModel().check(0);
		checkComboBoxSubset.setPrefWidth(70);
		checkComboBoxSuperset.setPrefWidth(70);
		checkComboBoxSubset.setMaxWidth(70);
		checkComboBoxSuperset.setMaxWidth(70);

		// left column
		gridPaneLeft.add(utils.createLabel("Specification:"), 0, 0, 2, 1);
		gridPaneLeft.addColumn(0, labelPolicyType, labelSubsetFilter, labelSubset, labelSupersetFilter, labelSuperset, labelConstraint,
				labelAppliedTo, labelTreatment, /* new Label(), */ new Separator(), labelUseAutoNames, labelPolicyName,
				labelMarketName, /* new Label(), */
				new Separator(), utils.createLabel("Populate:"), labelModificationType, labelStartYear, labelEndYear,
				labelInitialAmount, labelGrowth);

		gridPaneLeft.addColumn(1, comboBoxPolicyType, textFieldSubsetFilter, checkComboBoxSubset, textFieldSupersetFilter, checkComboBoxSuperset,
				comboBoxConstraint, comboBoxAppliedTo, comboBoxTreatment, /* new Label(), */ new Separator(),
				checkBoxUseAutoNames, textFieldPolicyName, textFieldMarketName, /* new Label(), */ new Separator(),
				new Label(), comboBoxModificationType, textFieldStartYear, textFieldEndYear, textFieldInitialAmount,
				textFieldGrowth);
		gridPaneLeft.setAlignment(Pos.TOP_LEFT);

		gridPaneLeft.setVgap(3.);
		gridPaneLeft.setStyle(styles.style2);
		
		scrollPaneLeft.setContent(gridPaneLeft);		

		// center column

		hBoxHeaderCenter.getChildren().addAll(buttonPopulate, buttonDelete, buttonClear);
		hBoxHeaderCenter.setSpacing(2.);
		hBoxHeaderCenter.setStyle(styles.style3);

		vBoxCenter.getChildren().addAll(labelValue, hBoxHeaderCenter, paneForComponentDetails);
		vBoxCenter.setStyle(styles.style2);

		// right column

		vBoxRight.getChildren().addAll(paneForCountryStateTree/* ,progress_bar,progress_indicator */);
		vBoxRight.setStyle(styles.style2);

		// other setup
		gridPanePresetModification.addColumn(0, scrollPaneLeft);
		gridPanePresetModification.addColumn(1, vBoxCenter);
		gridPanePresetModification.addColumn(2, vBoxRight);

		gridPaneLeft.setPrefWidth(325);
		gridPaneLeft.setMinWidth(325);
		vBoxCenter.setPrefWidth(300);
		vBoxRight.setPrefWidth(300);
		// labelSubset.setMinWidth(90);

		comboBoxPolicyType.getItems().addAll("Select One", "Renewable Portfolio Standard (RPS)",
				"Clean Energy Standard (CES)",
				// "EV passenger cars","EV passenger large car and truck",
				"EV passenger cars and trucks", "EV passenger cars trucks and MCs", "EV freight light truck",
				"EV freight medium truck", "EV freight heavy truck", "EV freight all trucks", "LED lights",
				"Heat pumps", "Biofuels", "Other", "Sector:EGU", "Sector:Industry", "Sector:Industry-fuels",
				"Sector:Buildings", "Sector:Trn-Onroad", "Sector:Trn-ALM", "Sector:Other");
		comboBoxPolicyType.getSelectionModel().select(0);

		checkComboBoxSubset.setDisable(true);
		checkComboBoxSuperset.setDisable(true);

		// appliedTo
		comboBoxAppliedTo.getItems().addAll("Select One", "All Stock", "New Purchases");
		comboBoxAppliedTo.getSelectionModel().select("All Stock");
		// comboBoxAppliedTo.setDisable(true);

		// constraint
		comboBoxConstraint.getItems().addAll("Lower", "Fixed");
		comboBoxConstraint.getSelectionModel().selectFirst();

		comboBoxTreatment.getItems().addAll("Select One", "Each Selected Region", "Across Selected Regions");
		comboBoxTreatment.getSelectionModel().select("Across Selected Regions");
		// comboBoxTreatment.setDisable(true);

		// widgets for populating tables
		comboBoxModificationType.getItems().addAll("Initial and Final %", "Initial w/% Growth/yr",
				"Initial w/% Growth/pd", "Initial w/Delta/yr", "Initial w/Delta/pd");
		comboBoxModificationType.getSelectionModel().selectFirst();

		// default sizing
		double max_wid = 175;
		comboBoxPolicyType.setMaxWidth(max_wid);
		checkComboBoxSubset.setMaxWidth(max_wid);
		checkComboBoxSuperset.setMaxWidth(max_wid);
		comboBoxAppliedTo.setMaxWidth(max_wid);
		comboBoxModificationType.setMaxWidth(max_wid);
		comboBoxConstraint.setMaxWidth(max_wid);
		comboBoxTreatment.setMaxWidth(max_wid);

		double min_wid = 175;
		comboBoxPolicyType.setMinWidth(min_wid);
		checkComboBoxSubset.setMinWidth(min_wid);
		checkComboBoxSuperset.setMinWidth(min_wid);
		comboBoxAppliedTo.setMinWidth(min_wid);
		comboBoxModificationType.setMinWidth(min_wid);
		comboBoxConstraint.setMinWidth(min_wid);
		comboBoxTreatment.setMaxWidth(min_wid);

		// widget actions

		labelSubset.setOnMouseClicked(e -> {
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

		labelSuperset.setOnMouseClicked(e -> {
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

		comboBoxPolicyType.setOnAction(e -> {
			String selectedItem = comboBoxPolicyType.getValue();
			if (selectedItem.equals("Select One")) {
				checkComboBoxSubset.getCheckModel().clearChecks();
				checkComboBoxSubset.getItems().clear();
				checkComboBoxSuperset.getCheckModel().clearChecks();
				checkComboBoxSuperset.getItems().clear();

				checkComboBoxSubset.getItems().add("Select One or More");
				checkComboBoxSuperset.getItems().add("Select One or More");
				checkComboBoxSubset.getCheckModel().check(0);
				checkComboBoxSuperset.getCheckModel().check(0);

				checkComboBoxSubset.setDisable(true);
				checkComboBoxSuperset.setDisable(true);

			} else {
				setupCheckComboBoxes(selectedItem);
				checkComboBoxSubset.setDisable(false);
				checkComboBoxSuperset.setDisable(false);
				if ((selectedItem.contains("RPS")) || (selectedItem.contains("CES"))) {
					utils.showInformationDialog("Information", "For RPS and CES options:",
							"Technology selections chosen automatically. Modify as needed.");
				}
			}

			setPolicyAndMarketNames();
		});

		textFieldSubsetFilter.setOnAction(e -> {
			setupCheckComboBoxes();
		});

		textFieldSupersetFilter.setOnAction(e -> {
			setupCheckComboBoxes();
		});
		
		comboBoxAppliedTo.setOnAction(e -> {
			setPolicyAndMarketNames();
		});

		comboBoxTreatment.setOnAction(e -> {
			setPolicyAndMarketNames();
		});

		comboBoxConstraint.setOnAction(e -> {
			setPolicyAndMarketNames();
		});

		EventHandler<TreeModificationEvent> ev = new EventHandler<TreeModificationEvent>() {
			@Override
			public void handle(TreeModificationEvent ae) {
				ae.consume();
				setPolicyAndMarketNames();
			}
		};
		paneForCountryStateTree.addEventHandlerToAllLeafs(ev);

		checkBoxUseAutoNames.setOnAction(e -> {
			if (!checkBoxUseAutoNames.isSelected()) {
				textFieldPolicyName.setDisable(false);
				textFieldMarketName.setDisable(false);
			} else {
				textFieldMarketName.setDisable(true);
				textFieldPolicyName.setDisable(true);
			}
		});

		comboBoxModificationType.setOnAction(e -> {

			switch (comboBoxModificationType.getSelectionModel().getSelectedItem()) {
			case "Initial w/% Growth/yr":
				this.labelGrowth.setText("Growth (%):");
				break;
			case "Initial w/% Growth/pd":
				this.labelGrowth.setText("Growth (%):");
				break;
			case "Initial w/Delta/yr":
				this.labelGrowth.setText("Delta:");
				break;
			case "Initial w/Delta/pd":
				this.labelGrowth.setText("Delta:");
				break;
			case "Initial and Final %":
				this.labelGrowth.setText("Final (%):");
				break;
			}
		});

		buttonClear.setOnAction(e -> {
			this.paneForComponentDetails.clearTable();
		});

		buttonDelete.setOnAction(e -> {
			this.paneForComponentDetails.deleteItemsFromTable();
		});

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

	private void setupCheckComboBoxes() {
		String item = this.comboBoxPolicyType.getSelectionModel().getSelectedItem();
		setupCheckComboBoxes(item);
	}

	private void setupCheckComboBoxes(String selected_item) {

		String[][] tech_info = vars.getTechInfo();

		try {

			ArrayList<String> techListSub = new ArrayList<String>();
			ArrayList<String> techListSup = new ArrayList<String>();

			String filterTextSub = textFieldSubsetFilter.getText().trim();
			String filterTextSup = textFieldSupersetFilter.getText().trim();
			
			boolean useFilterSub = false;
			boolean useFilterSup = false;
			
			if ((filterTextSub != null) && (filterTextSub.length() > 0))
				useFilterSub = true;

			if ((filterTextSup != null) && (filterTextSup.length() > 0))
				useFilterSup = true;
			
			String last_line = "";
			
			//setting up content of techLists
			for (int i = 0; i < tech_info.length; i++) {
				String line = tech_info[i][0].trim() + " : " + tech_info[i][1] + " : " + tech_info[i][2];
				if (line.equals(last_line)) {
					;
				} else {

					last_line = line;
					if (tech_info[i].length >= 7) {
						line += " : " + tech_info[i][6] + " : " + tech_info[i][7];
					}

					//setting up subset list: 
					boolean showSub = true;
					if (useFilterSub) {
						showSub = false;
						for (int j = 0; j < tech_info[i].length; j++) {
							String temp = tech_info[i][j];
							if (temp.contains(filterTextSub))
								showSub = true;
						}
					}
					if (showSub)
						techListSub.add(line.trim());

					//setting up superset list: 					
					boolean showSup = true;
					if (useFilterSup) {
						showSup = false;
						for (int j = 0; j < tech_info[i].length; j++) {
							String temp = tech_info[i][j];
							if (temp.contains(filterTextSup))
								showSup = true;
						}
					}
					if (showSup)
						techListSup.add(line.trim());

				}
			}
			
			// clear check combo boxes
			checkComboBoxSubset.getCheckModel().clearChecks();
			checkComboBoxSubset.getItems().clear();
			checkComboBoxSuperset.getCheckModel().clearChecks();
			checkComboBoxSuperset.getItems().clear();

			boolean show_egu = false;
			boolean show_ldv_car = false;
			boolean show_ldv_truck = false;
			boolean show_ldv_4w = false;
			boolean show_ldv_all = false;
			boolean show_hdv_all = false;
			boolean show_hdv_light = false;
			boolean show_hdv_medium = false;
			boolean show_hdv_heavy = false;
			boolean show_lighting = false;
			boolean show_heating = false;
			boolean show_refining = false;

			boolean show_sector_egu = false;
			boolean show_sector_buildings = false;
			boolean show_sector_industry = false;
			boolean show_sector_industry_fuels = false;
			boolean show_sector_trn_onroad = false;
			boolean show_sector_trn_alm = false;
			boolean show_sector_other = false;

			String policy_type = comboBoxPolicyType.getValue();
			if (policy_type.contains("CES")) {
				show_egu = true;
			}
			if (policy_type.contains("RPS")) {
				show_egu = true;
			}

			if (policy_type.equals("EV passenger cars and trucks"))
				show_ldv_4w = true;
			if (policy_type.equals("EV passenger cars trucks and MCs"))
				show_ldv_all = true;
			if (policy_type.equals("EV freight light truck"))
				show_hdv_light = true;
			if (policy_type.equals("EV freight medium truck"))
				show_hdv_medium = true;
			if (policy_type.equals("EV freight heavy truck"))
				show_hdv_heavy = true;
			if (policy_type.equals("EV freight all trucks"))
				show_hdv_all = true;
			if (policy_type.equals("LED lights"))
				show_lighting = true;
			if (policy_type.equals("Heat pumps"))
				show_heating = true;
			if (policy_type.equals("Biofuels"))
				show_refining = true;
			if (policy_type.equals("Sector:EGU"))
				show_sector_egu = true;
			if (policy_type.equals("Sector:Buildings"))
				show_sector_buildings = true;
			if (policy_type.equals("Sector:Industry"))
				show_sector_industry = true;
			if (policy_type.equals("Sector:Industry-fuels"))
				show_sector_industry_fuels = true;
			if (policy_type.equals("Sector:Trn-Onroad"))
				show_sector_trn_onroad = true;
			if (policy_type.equals("Sector:Trn-ALM"))
				show_sector_trn_alm = true;
			if (policy_type.equals("Sector:Other"))
				show_sector_trn_alm = true;

			for (int i = 0; i < techListSub.size(); i++) {
				boolean show_tech = false;
				String tech_line = techListSub.get(i).trim();
				String tech_line_lc = tech_line.toLowerCase();
				if (show_egu) {
					if (tech_line_lc.startsWith("electricity ")) {
						show_tech = true;
					} else if (tech_line_lc.startsWith("base load")) {
						show_tech = true;
					} else if (tech_line_lc.startsWith("intermediate")) {
						show_tech = true;
					} else if (tech_line_lc.startsWith("peak")) {
						show_tech = true;
					} else if (tech_line_lc.startsWith("subpeak")) {
						show_tech = true;
					} else if (tech_line_lc.startsWith("elec_")) {
						show_tech = true;
					} else if (tech_line_lc.indexOf("cogen") > -1) {
						show_tech = true;
					}
				} else if (show_ldv_truck) {
					if (tech_line_lc.contains("large car and truck")) {
						show_tech = true;
					}
				} else if (show_ldv_car) {
					if (tech_line_lc.contains(": car :")) {
						show_tech = true;
					}
				} else if (show_ldv_4w) {
					if (tech_line_lc.indexOf("ldv_4w") > -1) {
						show_tech = true;
					}
				} else if (show_ldv_all) {
					if (tech_line_lc.indexOf("ldv") > -1) {
						show_tech = true;
					}
				} else if (show_hdv_light) {
					if (tech_line_lc.startsWith("trn_freight_road")) {
						if (tech_line_lc.contains("light"))
							show_tech = true;
					}
				} else if (show_hdv_medium) {
					if (tech_line_lc.startsWith("trn_freight_road")) {
						if (tech_line_lc.contains("medium"))
							show_tech = true;
					}
				} else if (show_hdv_heavy) {
					if (tech_line_lc.startsWith("trn_freight_road")) {
						if (tech_line_lc.contains("heavy"))
							show_tech = true;
					}
				} else if (show_hdv_all) {
					if (tech_line_lc.startsWith("trn_freight_road")) {
						show_tech = true;
					}
				} else if (show_lighting) {
					if ((tech_line_lc.startsWith("resid lighting")) || (tech_line_lc.startsWith("comm lighting"))) {
						show_tech = true;
					}
				} else if (show_heating) {
					if ((tech_line_lc.startsWith("resid heating")) || (tech_line_lc.startsWith("comm heating"))) {
						show_tech = true;
					}
				} else if (show_refining) {
					if ((tech_line_lc.startsWith("oil refining")) || (tech_line_lc.startsWith("biomass liquids"))) {
						show_tech = true;
					}
				} else if (show_sector_egu) {
					if (tech_line_lc.endsWith("egu"))
						show_tech = true;

				} else if (show_sector_industry) {
					if (tech_line_lc.endsWith("industry"))
						show_tech = true;

				} else if (show_sector_industry_fuels) {
					if (tech_line_lc.endsWith("industry-fuels"))
						show_tech = true;

				} else if (show_sector_buildings) {
					if (tech_line_lc.endsWith("buildings"))
						show_tech = true;

				} else if (show_sector_trn_onroad) {
					if (tech_line_lc.endsWith("trn-onroad"))
						show_tech = true;

				} else if (show_sector_trn_alm) {
					if ((tech_line_lc.endsWith("trn-alm")) || (tech_line_lc.endsWith("trn-nonroad")))
						show_tech = true;

				} else if (show_sector_other) {
					show_tech = true;

				} else {
					show_tech = true;
				}
				if (show_tech) {
					checkComboBoxSubset.getItems().add(tech_line);
				}
			}

			for (int i = 0; i < techListSup.size(); i++) {
				boolean show_tech = false;
				String tech_line = techListSup.get(i).trim();
				String tech_line_lc = tech_line.toLowerCase();
				if (show_egu) {
					if (tech_line_lc.startsWith("electricity ")) {
						show_tech = true;
					} else if (tech_line_lc.startsWith("base load")) {
						show_tech = true;
					} else if (tech_line_lc.startsWith("intermediate")) {
						show_tech = true;
					} else if (tech_line_lc.startsWith("peak")) {
						show_tech = true;
					} else if (tech_line_lc.startsWith("subpeak")) {
						show_tech = true;
					} else if (tech_line_lc.startsWith("elec_")) {
						show_tech = true;
					} else if (tech_line_lc.indexOf("cogen") > -1) {
						show_tech = true;
					}
				} else if (show_ldv_truck) {
					if (tech_line_lc.contains("large car and truck")) {
						show_tech = true;
					}
				} else if (show_ldv_car) {
					if (tech_line_lc.contains(": car :")) {
						show_tech = true;
					}
				} else if (show_ldv_4w) {
					if (tech_line_lc.indexOf("ldv_4w") > -1) {
						show_tech = true;
					}
				} else if (show_ldv_all) {
					if (tech_line_lc.indexOf("ldv") > -1) {
						show_tech = true;
					}
				} else if (show_hdv_light) {
					if (tech_line_lc.startsWith("trn_freight_road")) {
						if (tech_line_lc.contains("light"))
							show_tech = true;
					}
				} else if (show_hdv_medium) {
					if (tech_line_lc.startsWith("trn_freight_road")) {
						if (tech_line_lc.contains("medium"))
							show_tech = true;
					}
				} else if (show_hdv_heavy) {
					if (tech_line_lc.startsWith("trn_freight_road")) {
						if (tech_line_lc.contains("heavy"))
							show_tech = true;
					}
				} else if (show_hdv_all) {
					if (tech_line_lc.startsWith("trn_freight_road")) {
						show_tech = true;
					}
				} else if (show_lighting) {
					if ((tech_line_lc.startsWith("resid lighting")) || (tech_line_lc.startsWith("comm lighting"))) {
						show_tech = true;
					}
				} else if (show_heating) {
					if ((tech_line_lc.startsWith("resid heating")) || (tech_line_lc.startsWith("comm heating"))) {
						show_tech = true;
					}
				} else if (show_refining) {
					if ((tech_line_lc.startsWith("oil refining")) || (tech_line_lc.startsWith("biomass liquids"))) {
						show_tech = true;
					}
				} else if (show_sector_egu) {
					if (tech_line_lc.endsWith("egu"))
						show_tech = true;

				} else if (show_sector_industry) {
					if (tech_line_lc.endsWith("industry"))
						show_tech = true;

				} else if (show_sector_industry_fuels) {
					if (tech_line_lc.endsWith("industry-fuels"))
						show_tech = true;

				} else if (show_sector_buildings) {
					if (tech_line_lc.endsWith("buildings"))
						show_tech = true;

				} else if (show_sector_trn_onroad) {
					if (tech_line_lc.endsWith("trn-onroad"))
						show_tech = true;

				} else if (show_sector_trn_alm) {
					if ((tech_line_lc.endsWith("trn-alm")) || (tech_line_lc.endsWith("trn-nonroad")))
						show_tech = true;

				} else if (show_sector_other) {
					show_tech = true;

				} else {
					show_tech = true;
				}
				if (show_tech) {
					checkComboBoxSuperset.getItems().add(tech_line);
				}
			}

			
			// setting up RPS and CES checks
			if ((policy_type.contains("RPS")) || (policy_type.contains("CES"))) {
				for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
					String item_text = checkComboBoxSubset.getItems().get(i).toLowerCase();
					if ((item_text.indexOf("solar") >= 0) || (item_text.indexOf("csp") >= 0)
							|| (item_text.indexOf("pv") >= 0))
						checkComboBoxSubset.getCheckModel().check(i);
					if (item_text.indexOf("wind") >= 0)
						checkComboBoxSubset.getCheckModel().check(i);
					if ((item_text.indexOf("hydro") >= 0) && (item_text.indexOf("hydrogen") < 0))
						checkComboBoxSubset.getCheckModel().check(i);
					if (item_text.indexOf("geothermal") >= 0)
						checkComboBoxSubset.getCheckModel().check(i);
					if (item_text.indexOf("biomass") >= 0)
						checkComboBoxSubset.getCheckModel().check(i);
				}
				checkComboBoxSuperset.getCheckModel().checkAll();

			}
			if (policy_type.contains("CES")) {
				for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
					String item_text = checkComboBoxSubset.getItems().get(i).toLowerCase();
					if (item_text.indexOf("ccs") >= 0)
						checkComboBoxSubset.getCheckModel().check(i);
					if (item_text.indexOf("nuclear") >= 0)
						checkComboBoxSubset.getCheckModel().check(i);
				}
			}

			// setting up LDV-EV checks
			if (policy_type.contains("EV")) {
				for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
					String item_text = checkComboBoxSubset.getItems().get(i).toLowerCase();
					if (item_text.indexOf("bev") >= 0)
						checkComboBoxSubset.getCheckModel().check(i);
				}
				checkComboBoxSuperset.getCheckModel().checkAll();

			}

			// setting up HDV-EV checks
			if (policy_type.startsWith("EV")) {
				for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
					String item_text = checkComboBoxSubset.getItems().get(i).toLowerCase();
					if (item_text.indexOf("bev") >= 0)
						checkComboBoxSubset.getCheckModel().check(i);
				}
				checkComboBoxSuperset.getCheckModel().checkAll();

			}

			// setting up LED checks
			if (policy_type.contains("LED")) {
				for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
					String item_text = checkComboBoxSubset.getItems().get(i).toLowerCase();
					if (item_text.indexOf("solid state") >= 0)
						checkComboBoxSubset.getCheckModel().check(i);
				}
				checkComboBoxSuperset.getCheckModel().checkAll();

			}

			// setting up heat pump checks
			if (policy_type.contains("Heat pump")) {
				for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
					String item_text = checkComboBoxSubset.getItems().get(i).toLowerCase();
					if (item_text.indexOf("heat pump") >= 0)
						checkComboBoxSubset.getCheckModel().check(i);
				}
				checkComboBoxSuperset.getCheckModel().checkAll();

			}

			// setting up refining checks
			if (policy_type.contains("Biofuel")) {
				for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
					String item_text = checkComboBoxSubset.getItems().get(i).toLowerCase();
					if (item_text.indexOf("bio") >= 0)
						checkComboBoxSubset.getCheckModel().check(i);
				}
				checkComboBoxSuperset.getCheckModel().checkAll();

			}

		} catch (Exception e) {
			utils.warningMessage("Problem reading tech list.");
			System.out.println("Error reading tech list from " + vars.get("tchBndListFile") + ":");
			System.out.println("  ---> " + e);

		}

	}

//	private void setupCheckComboBoxesOld(String selected_item) {
//
//		String[][] tech_info = vars.getTechInfo();
//
//		try {
//
//			ArrayList<String> techList = new ArrayList<String>();
//
//			String filterText = textFieldFilter.getText().trim();
//			boolean useFilter = false;
//			if ((filterText != null) && (filterText.length() > 0))
//				useFilter = true;
//
//			String last_line = "";
//			for (int i = 0; i < tech_info.length; i++) {
//				String line = tech_info[i][0].trim() + " : " + tech_info[i][1] + " : " + tech_info[i][2];
//				if (line.equals(last_line)) {
//					;
//				} else {
//					last_line = line;
//					if (tech_info[i].length >= 7) {
//						line += " : " + tech_info[i][6] + " : " + tech_info[i][7];
//					}
//
//					boolean show = true;
//					if (useFilter) {
//						show = false;
//						for (int j = 0; j < tech_info[i].length; j++) {
//							String temp = tech_info[i][j];
//							if (temp.contains(filterText))
//								show = true;
//						}
//					}
//					if (show)
//						techList.add(line.trim());
//
//				}
//			}
//
//			// clear check combo boxes
//			checkComboBoxSubset.getCheckModel().clearChecks();
//			checkComboBoxSubset.getItems().clear();
//			checkComboBoxSuperset.getCheckModel().clearChecks();
//			checkComboBoxSuperset.getItems().clear();
//
//			boolean show_egu = false;
//			boolean show_ldv_car = false;
//			boolean show_ldv_truck = false;
//			boolean show_ldv_4w = false;
//			boolean show_ldv_all = false;
//			boolean show_hdv_all = false;
//			boolean show_hdv_light = false;
//			boolean show_hdv_medium = false;
//			boolean show_hdv_heavy = false;
//			boolean show_lighting = false;
//			boolean show_heating = false;
//			boolean show_refining = false;
//
//			boolean show_sector_egu = false;
//			boolean show_sector_buildings = false;
//			boolean show_sector_industry = false;
//			boolean show_sector_industry_fuels = false;
//			boolean show_sector_trn_onroad = false;
//			boolean show_sector_trn_alm = false;
//			boolean show_sector_other = false;
//
//			String policy_type = comboBoxPolicyType.getValue();
//			if (policy_type.contains("CES")) {
//				show_egu = true;
//			}
//			if (policy_type.contains("RPS")) {
//				show_egu = true;
//			}
////			if (policy_type.equals("EV passenger car"))
////				show_ldv_car = true;
////			if (policy_type.equals("EV passenger large car and truck"))
////				show_ldv_truck = true;
//			if (policy_type.equals("EV passenger cars and trucks"))
//				show_ldv_4w = true;
//			if (policy_type.equals("EV passenger cars trucks and MCs"))
//				show_ldv_all = true;
//			if (policy_type.equals("EV freight light truck"))
//				show_hdv_light = true;
//			if (policy_type.equals("EV freight medium truck"))
//				show_hdv_medium = true;
//			if (policy_type.equals("EV freight medium truck"))
//				show_hdv_heavy = true;
//			if (policy_type.equals("EV freight all truck"))
//				show_hdv_all = true;
//			if (policy_type.equals("LED lights"))
//				show_lighting = true;
//			if (policy_type.equals("Heat pumps"))
//				show_heating = true;
//			if (policy_type.equals("Biofuels"))
//				show_refining = true;
//			if (policy_type.equals("Sector:EGU"))
//				show_sector_egu = true;
//			if (policy_type.equals("Sector:Buildings"))
//				show_sector_buildings = true;
//			if (policy_type.equals("Sector:Industry"))
//				show_sector_industry = true;
//			if (policy_type.equals("Sector:Industry-fuels"))
//				show_sector_industry_fuels = true;
//			if (policy_type.equals("Sector:Trn-Onroad"))
//				show_sector_trn_onroad = true;
//			if (policy_type.equals("Sector:Trn-ALM"))
//				show_sector_trn_alm = true;
//			if (policy_type.equals("Sector:Other"))
//				show_sector_trn_alm = true;
//
//			for (int i = 0; i < techList.size(); i++) {
//				boolean show_tech = false;
//				String tech_line = techList.get(i).trim();
//				String tech_line_lc = tech_line.toLowerCase();
//				if (show_egu) {
//					if (tech_line_lc.startsWith("electricity ")) {
//						show_tech = true;
//					} else if (tech_line_lc.startsWith("base load")) {
//						show_tech = true;
//					} else if (tech_line_lc.startsWith("intermediate")) {
//						show_tech = true;
//					} else if (tech_line_lc.startsWith("peak")) {
//						show_tech = true;
//					} else if (tech_line_lc.startsWith("subpeak")) {
//						show_tech = true;
//					} else if (tech_line_lc.startsWith("elec_")) {
//						show_tech = true;
//					} else if (tech_line_lc.indexOf("cogen") > -1) {
//						show_tech = true;
//					}
//				} else if (show_ldv_truck) {
//					if (tech_line_lc.contains("large car and truck")) {
//						show_tech = true;
//					}
//				} else if (show_ldv_car) {
//					if (tech_line_lc.contains(": car :")) {
//						show_tech = true;
//					}
//				} else if (show_ldv_4w) {
//					if (tech_line_lc.indexOf("ldv_4w") > -1) {
//						show_tech = true;
//					}
//				} else if (show_ldv_all) {
//					if (tech_line_lc.indexOf("ldv") > -1) {
//						show_tech = true;
//					}
//				} else if (show_hdv_light) {
//					if (tech_line_lc.startsWith("trn_freight_road")) {
//						if (tech_line_lc.contains("light"))
//							show_tech = true;
//					}
//				} else if (show_hdv_medium) {
//					if (tech_line_lc.startsWith("trn_freight_road")) {
//						if (tech_line_lc.contains("medium"))
//							show_tech = true;
//					}
//				} else if (show_hdv_heavy) {
//					if (tech_line_lc.startsWith("trn_freight_road")) {
//						if (tech_line_lc.contains("heavy"))
//							show_tech = true;
//					}
//				} else if (show_hdv_all) {
//					if (tech_line_lc.startsWith("trn_freight_road")) {
//						show_tech = true;
//					}
//				} else if (show_lighting) {
//					if ((tech_line_lc.startsWith("resid lighting")) || (tech_line_lc.startsWith("comm lighting"))) {
//						show_tech = true;
//					}
//				} else if (show_heating) {
//					if ((tech_line_lc.startsWith("resid heating")) || (tech_line_lc.startsWith("comm heating"))) {
//						show_tech = true;
//					}
//				} else if (show_refining) {
//					if ((tech_line_lc.startsWith("oil refining")) || (tech_line_lc.startsWith("biomass liquids"))) {
//						show_tech = true;
//					}
//				} else if (show_sector_egu) {
//					if (tech_line_lc.endsWith("egu"))
//						show_tech = true;
//
//				} else if (show_sector_industry) {
//					if (tech_line_lc.endsWith("industry"))
//						show_tech = true;
//
//				} else if (show_sector_industry_fuels) {
//					if (tech_line_lc.endsWith("industry-fuels"))
//						show_tech = true;
//
//				} else if (show_sector_buildings) {
//					if (tech_line_lc.endsWith("buildings"))
//						show_tech = true;
//
//				} else if (show_sector_trn_onroad) {
//					if (tech_line_lc.endsWith("trn-onroad"))
//						show_tech = true;
//
//				} else if (show_sector_trn_alm) {
//					if ((tech_line_lc.endsWith("trn-alm")) || (tech_line_lc.endsWith("trn-nonroad")))
//						show_tech = true;
//
//				} else if (show_sector_other) {
//					show_tech = true;
//
//				} else {
//					show_tech = true;
//				}
//				if (show_tech) {
//					checkComboBoxSubset.getItems().add(tech_line);
//					checkComboBoxSuperset.getItems().add(tech_line);
//				}
//			}
//
//			// setting up RPS and CES checks
//			if ((policy_type.contains("RPS")) || (policy_type.contains("CES"))) {
//				for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
//					String item_text = checkComboBoxSubset.getItems().get(i).toLowerCase();
//					if ((item_text.indexOf("solar") >= 0) || (item_text.indexOf("csp") >= 0)
//							|| (item_text.indexOf("pv") >= 0))
//						checkComboBoxSubset.getCheckModel().check(i);
//					if (item_text.indexOf("wind") >= 0)
//						checkComboBoxSubset.getCheckModel().check(i);
//					if ((item_text.indexOf("hydro") >= 0) && (item_text.indexOf("hydrogen") < 0))
//						checkComboBoxSubset.getCheckModel().check(i);
//					if (item_text.indexOf("geothermal") >= 0)
//						checkComboBoxSubset.getCheckModel().check(i);
//					if (item_text.indexOf("biomass") >= 0)
//						checkComboBoxSubset.getCheckModel().check(i);
//				}
//				checkComboBoxSuperset.getCheckModel().checkAll();
//
//			}
//			if (policy_type.contains("CES")) {
//				for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
//					String item_text = checkComboBoxSubset.getItems().get(i).toLowerCase();
//					if (item_text.indexOf("ccs") >= 0)
//						checkComboBoxSubset.getCheckModel().check(i);
//					if (item_text.indexOf("nuclear") >= 0)
//						checkComboBoxSubset.getCheckModel().check(i);
//				}
//			}
//
//			// setting up LDV-EV checks
//			if (policy_type.contains("EV")) {
//				for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
//					String item_text = checkComboBoxSubset.getItems().get(i).toLowerCase();
//					if (item_text.indexOf("bev") >= 0)
//						checkComboBoxSubset.getCheckModel().check(i);
//				}
//				checkComboBoxSuperset.getCheckModel().checkAll();
//
//			}
//
//			// setting up HDV-EV checks
//			if (policy_type.startsWith("EV")) {
//				for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
//					String item_text = checkComboBoxSubset.getItems().get(i).toLowerCase();
//					if (item_text.indexOf("bev") >= 0)
//						checkComboBoxSubset.getCheckModel().check(i);
//				}
//				checkComboBoxSuperset.getCheckModel().checkAll();
//
//			}
//
//			// setting up LED checks
//			if (policy_type.contains("LED")) {
//				for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
//					String item_text = checkComboBoxSubset.getItems().get(i).toLowerCase();
//					if (item_text.indexOf("solid state") >= 0)
//						checkComboBoxSubset.getCheckModel().check(i);
//				}
//				checkComboBoxSuperset.getCheckModel().checkAll();
//
//			}
//
//			// setting up heat pump checks
//			if (policy_type.contains("Heat pump")) {
//				for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
//					String item_text = checkComboBoxSubset.getItems().get(i).toLowerCase();
//					if (item_text.indexOf("heat pump") >= 0)
//						checkComboBoxSubset.getCheckModel().check(i);
//				}
//				checkComboBoxSuperset.getCheckModel().checkAll();
//
//			}
//
//			// setting up refining checks
//			if (policy_type.contains("Biofuel")) {
//				for (int i = 0; i < checkComboBoxSubset.getItems().size(); i++) {
//					String item_text = checkComboBoxSubset.getItems().get(i).toLowerCase();
//					if (item_text.indexOf("bio") >= 0)
//						checkComboBoxSubset.getCheckModel().check(i);
//				}
//				checkComboBoxSuperset.getCheckModel().checkAll();
//
//			}
//
//		} catch (Exception e) {
//			utils.warningMessage("Problem reading tech list.");
//			System.out.println("Error reading tech list from " + vars.get("tchBndListFile") + ":");
//			System.out.println("  ---> " + e);
//
//		}
//
//	}

	
	private void setPolicyAndMarketNames() {
		if (this.checkBoxUseAutoNames.isSelected()) {

			String policy_type = "--";
			String to_which = "--";
			String state = "--";
			String treatment = "--";

			policy_type = comboBoxPolicyType.getValue().replace(" ", "-").replace("(", "-").replace(")", "")
					.replace(":", "");
			if (policy_type.equals("Other"))
				policy_type = "Share";
			if (policy_type.equals("SelectOne"))
				policy_type = "---";

			try {
				String s = comboBoxAppliedTo.getValue();
				if (s.indexOf("New") >= 0)
					to_which = "New";
				if (s.indexOf("All") >= 0)
					to_which = "All";

				s = comboBoxTreatment.getValue();
				if (s.indexOf("Each") >= 0)
					treatment = "_Ea";
				if (s.indexOf("Across") >= 0)
					treatment = "";

				String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(paneForCountryStateTree.getTree());
				if (listOfSelectedLeaves.length > 0) {
					listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
					String state_str = utils.returnAppendedString(listOfSelectedLeaves).replace(",", "");
					if (state_str.length() < 9) {
						state = state_str;
					} else {
						state = "Reg";
					}
				}

				String name = policy_type + "_" + to_which + "_" + state + treatment;
				name=name.replaceAll(" ","_").replaceAll("--","-");
				textFieldMarketName.setText(name + "_Mkt");
				textFieldPolicyName.setText(name);

			} catch (Exception e) {
				//System.out.println("Cannot auto-name market. Continuing.");
			}

		}
	}

	private double[][] calculateValues() {
		String calc_type = comboBoxModificationType.getSelectionModel().getSelectedItem();
		int start_year = Integer.parseInt(textFieldStartYear.getText());
		int end_year = Integer.parseInt(textFieldEndYear.getText());
		double initial_value = Double.parseDouble(this.textFieldInitialAmount.getText());
		double growth = Double.parseDouble(textFieldGrowth.getText());
		int period_length = Integer.parseInt(this.textFieldPeriodLength.getText());
		ObservableList<DataPoint> data;
		double[][] returnMatrix = utils.calculateValues(calc_type, start_year, end_year, initial_value, growth,
				period_length);
		return returnMatrix;
	}

	@Override
	public void run() {
		saveScenarioComponent();
	}

	@Override
	public void saveScenarioComponent() {
		saveScenarioComponent(paneForCountryStateTree.getTree());
	}

	private void saveScenarioComponent(TreeView<String> tree) {

		if (!qaInputs()) {
			Thread.currentThread().destroy();
		} else {

			String which = this.comboBoxConstraint.getValue().toLowerCase();

			// String ID = this.getUniqueMarketName(textFieldMarketName.getText());
			String ID = utils.getUniqueString();
			String policy_name = this.textFieldPolicyName.getText() + ID;
			String market_name = this.textFieldMarketName.getText() + ID;
			filename_suggestion = this.textFieldPolicyName.getText().replaceAll("/", "-").replaceAll(" ", "_") + ".csv";

			String tempDirName = vars.getGlimpseDir() + File.separator + "GLIMPSE-Data" + File.separator + "temp"; // vars.getGlimpseDir();
			File test = new File(tempDirName);
			if (!test.exists())
				test.mkdir();
			String tempFilename0 = "temp_policy_file0.txt";
			String tempFilename1 = "temp_policy_file1.txt";
			String tempFilename2 = "temp_policy_file2.txt";

			BufferedWriter bw0 = files.initializeBufferedFile(tempDirName, tempFilename0);
			BufferedWriter bw1 = files.initializeBufferedFile(tempDirName, tempFilename1);
			BufferedWriter bw2 = files.initializeBufferedFile(tempDirName, tempFilename2);

			int no_nested = 0;
			int no_non_nested = 0;

			file_content = "use temp file";
			files.writeToBufferedFile(bw0, getMetaDataContent(tree, market_name, policy_name));

			String treatment = comboBoxTreatment.getValue().toLowerCase().trim();

			//// -----------getting selected regions info from GUI
			String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);
			listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
			String states = utils.returnAppendedString(listOfSelectedLeaves);

			// tech list
			ObservableList<String> subset_list = checkComboBoxSubset.getCheckModel().getCheckedItems();
			ObservableList<String> superset_list = checkComboBoxSuperset.getCheckModel().getCheckedItems();

			ObservableList<String> tech_list1 = subset_list;
			ObservableList<String> tech_list2 = superset_list;

			// applied to
			String applied_to = comboBoxAppliedTo.getSelectionModel().getSelectedItem().toLowerCase().trim();

			// data
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
			String sss = vars.getStartYearForShare();
			if (!sss.equals("2010")) {
				try {
					start_year = Integer.parseInt(sss);
				} catch (Exception e1) {
					System.out.println(
							"Problem converting startYearForShare (" + sss + ") to int. Using default value of 2010.");
				}
			}

			// boolean includes_trn_techs=false;
			ArrayList<String> list_of_policy_sector_combos = new ArrayList<String>();

			// sets up the content of the CSV file to store the scenario component data

			// -------------------------------------------
			// part 1 - apply constraint fraction targets
			// -------------------------------------------
			files.writeToBufferedFile(bw1, vars.getEol());
			files.writeToBufferedFile(bw1, "INPUT_TABLE" + vars.getEol());
			files.writeToBufferedFile(bw1, "Variable ID" + vars.getEol());
			// TODO: Need to handle case with nested
			files.writeToBufferedFile(bw1, "GLIMPSEPFStdAdjCoef-v2" + vars.getEol() + vars.getEol());

			// note: market in the line below is the market where the input comes from and
			// should be the same as the state

			files.writeToBufferedFile(bw1,
					"region,sector,subsector,tech,year,policy,adjcoef-year,adjcoef,unit-price-conv" + vars.getEol());

			files.writeToBufferedFile(bw2, vars.getEol());
			files.writeToBufferedFile(bw2, "INPUT_TABLE" + vars.getEol());
			files.writeToBufferedFile(bw2, "Variable ID" + vars.getEol());

			files.writeToBufferedFile(bw2, "GLIMPSEPFStdAdjCoef-Nest" + vars.getEol() + vars.getEol());
			// note: market in the line below is the market where the input comes from and
			// should be the same as the state

			files.writeToBufferedFile(bw2,
					"region,sector,nested-subsector,tech,year,policy,adjcoef-year,adjcoef" + vars.getEol());

			for (int s = 0; s < listOfSelectedLeaves.length; s++) {

				String state = listOfSelectedLeaves[s];
				System.out.println("Creating part 1 of 3 of csv file for " + state + " : " + s + " of "
						+ (listOfSelectedLeaves.length - 1));

				// iterates over all model years
				for (int t = start_year; t < 2100; t += 5) {

					String use_this_market_name = market_name;
					String use_this_policy_name = policy_name;
					if (treatment.equals("each selected region")) {
						if (listOfSelectedLeaves.length >= 2) {
							use_this_market_name = state + "_" + market_name;
							use_this_policy_name = state + "_" + policy_name;
						}
					}

					if (applied_to.equals("new purchases")) {
						use_this_market_name += "-" + t;
						use_this_policy_name += "-" + t;
					}

					String last_subsector = "";
					boolean is_subsector_in_region = true;

					// iterates over lines in constraint table
					for (int i = 0; i < year_list.length; i++) {

						// if constraint applies to current year
						if (((t <= Integer.parseInt(year_list[i])) && (applied_to.equals("all stock")))
								|| ((t == Integer.parseInt(year_list[i])) && (applied_to.equals("new purchases")))) {
							// iterates over selected technologies
							for (int j = 0; j < tech_list2.size(); j++) {
								String temp = tech_list2.get(j);
								// for tech, extracts sector and subsector
								if (temp.indexOf(":") >= 0) {
									String[] tempi = utils.splitString(temp, ":");
									String sector_name = tempi[0].trim();
									String subsector_name = tempi[1].trim();

									String tech_name = tempi[2].trim();

									// special handling for trn sectors
									// does not include technologies not in region (e.g., minicars in US states)
									if (sector_name.startsWith("trn")) {
										if (last_subsector.equals(subsector_name)) {
											is_subsector_in_region = true;
										} else {
											is_subsector_in_region = utils.isSubsectorInRegion(state, sector_name,
													subsector_name);
										}
									}
									last_subsector = subsector_name;

									// keeps track of policy-sector combos
									String ss = use_this_policy_name + ":" + sector_name;
									list_of_policy_sector_combos = utils
											.addToArrayListIfUnique(list_of_policy_sector_combos, ss);

									// if not nested, write to bw1
									BufferedWriter bw = bw1;
									// special handling if nested subsector
									if (subsector_name.indexOf("=>") > -1) {
										// write to bw2
										bw = bw2;
										subsector_name = subsector_name.replace("=>", ",");
										no_nested++;
									} else {
										no_non_nested++;
									}

									if ((vars.isGcamUSA()) && (state.toLowerCase().equals("usa"))
											&& (listOfSelectedLeaves.length > 1)) {
										; // don't output for USA region if using GCAM-USA and more than one region is
											// selected
											// If you want to make a constraint on USA region, construct that constraint
											// on its own
									} else {
										// uses value from table
										if (is_subsector_in_region) {
											Double val = valuef_list[i];
											String conv = "1.0";

											if (sector_name.startsWith("trn")) {
												conv = "1e-3";
												val *= 1000.;
											}

											// TODO: correct for transportation?

											String line = "";

											line = state + "," + sector_name + "," + subsector_name + "," + tech_name
													+ "," + t + "," + use_this_policy_name + "," + year_list[i] + ","
													+ val + "," + conv + vars.getEol();

											files.writeToBufferedFile(bw, line);

										}
									}
								}
							}
						}
					}
				}
				double progress = s / (listOfSelectedLeaves.length - 1.);
				progress_bar.setProgress(progress);
			}

			int max_year = utils.getMaxValFromStringArray(year_list);
			int min_year = utils.getMinValFromStringArray(year_list);

			// part 2 - constructing constraint by providing secondary output ratio and
			// p-multiplier
			files.writeToBufferedFile(bw1, vars.getEol());
			files.writeToBufferedFile(bw1, "INPUT_TABLE" + vars.getEol());
			files.writeToBufferedFile(bw1, "Variable ID" + vars.getEol());
			files.writeToBufferedFile(bw1, "GLIMPSEPFStd2ndOut" + vars.getEol() + vars.getEol());
			files.writeToBufferedFile(bw1,
					"region,sector,subsector,tech,year,policy,output-ratio,pMultiplier" + vars.getEol());

			files.writeToBufferedFile(bw2, vars.getEol());
			files.writeToBufferedFile(bw2, "INPUT_TABLE" + vars.getEol());
			files.writeToBufferedFile(bw2, "Variable ID" + vars.getEol());
			files.writeToBufferedFile(bw2, "GLIMPSEPFStd2ndOut-Nest" + vars.getEol() + vars.getEol());
			files.writeToBufferedFile(bw2,
					"region,sector,nested-subsector,subsector,tech,year,policy,output-ratio,pMultiplier"
							+ vars.getEol());

			for (int s = 0; s < listOfSelectedLeaves.length; s++) {
				String state = listOfSelectedLeaves[s];
				System.out.println("Creating part 2 of 3 of csv file for " + state + " : " + s + " of "
						+ (listOfSelectedLeaves.length - 1));

				int year_int = -1;

				for (int t = start_year; t < 2100; t += 5) {

					year_int++;

					String use_this_market_name = market_name;
					String use_this_policy_name = policy_name;
					if (treatment.equals("each selected region")) {
						if (listOfSelectedLeaves.length >= 2) {
							use_this_market_name = state + "_" + market_name;
							use_this_policy_name = state + "_" + policy_name;
						}
					}

					if (applied_to.equals("new purchases")) {
						use_this_market_name += "-" + t;
						use_this_policy_name += "-" + t;
					}

					// if (t<=max_year) {
					if (((t <= max_year) && (applied_to.equals("all stock")))
							|| ((t <= max_year) && (t >= min_year) && (applied_to.equals("new purchases")))) {

						for (int j = 0; j < tech_list1.size(); j++) {
							String temp = tech_list1.get(j);
							if (temp.indexOf(":") >= 0) {
								String sector_name = utils.splitString(temp, ":")[0].trim();
								String subsector_name = utils.splitString(temp, ":")[1].trim();
								String tech_name = utils.splitString(temp, ":")[2].trim();

								BufferedWriter bw = bw1;
								if (subsector_name.indexOf("=>") > -1) {
									bw = bw2;
									subsector_name = subsector_name.replace("=>", ",");
									no_nested++;
								} else {
									no_non_nested++;
								}

								double val = 1.0;

								String conversions = utils.getSubsectorConversions(val, state, sector_name,
										subsector_name, t);

								if ((vars.isGcamUSA()) && (state.toLowerCase().equals("usa"))
										&& (listOfSelectedLeaves.length > 1)) {
									;
								} else if (conversions != null) {
									if (conversions.startsWith(","))
										conversions = conversions.substring(1);
									String line = state + "," + sector_name + "," + subsector_name + "," + tech_name
											+ "," + t + "," + use_this_policy_name + "," + conversions + vars.getEol();
									files.writeToBufferedFile(bw, line);
								}

							}
						}

					}
				}
				double progress = s / (listOfSelectedLeaves.length - 1.);
				progress_bar.setProgress(progress);
			}

			// part 3 - turn on markets in and/or across regions
			// files.writeToBufferedFile(bw0,vars.getEol());
			files.writeToBufferedFile(bw0, "INPUT_TABLE" + vars.getEol());
			files.writeToBufferedFile(bw0, "Variable ID" + vars.getEol());
			String header = "GLIMPSEPFStdActivate";
			String colnames = "region,policy,market,type,year,constrained";
			if (which.equals("fixed")) {
				header += "Fx";
				colnames += ",min-price-yr,min-price";
			}
			files.writeToBufferedFile(bw0, header + vars.getEol() + vars.getEol());
			files.writeToBufferedFile(bw0, colnames + vars.getEol());

			for (int s = 0; s < listOfSelectedLeaves.length; s++) {
				String state = listOfSelectedLeaves[s];
				System.out.println("Creating part 3  of 3 of csv file for " + state + " : " + s + " of "
						+ (listOfSelectedLeaves.length - 1));

				for (int i = 0; i < year_list.length; i++) {

					String use_this_market_name = market_name;
					String use_this_policy_name = policy_name;
					if (treatment.equals("each selected region")) {
						if (listOfSelectedLeaves.length >= 2) {
							use_this_market_name = state + "_" + market_name;
							use_this_policy_name = state + "_" + policy_name;
						}
					}

					if (applied_to.equals("new purchases")) {
						use_this_market_name += "-" + year_list[i];
						use_this_policy_name += "-" + year_list[i];
					}

					if ((vars.isGcamUSA()) && (state.toLowerCase().equals("usa"))
							&& (listOfSelectedLeaves.length > 1)) {
						; // don't output for USA region if using GCAM-USA and more than one region is
							// selected
							// If you want to make a constraint on USA region, construct that constraint on
							// its own
					} else {
						String line = "";
						if (which.equals("fixed")) {
							line = state + "," + use_this_policy_name + "," + use_this_market_name + ",RES,"
									+ year_list[i] + "," + "1," + year_list[i] + ",-100" + vars.getEol();
						} else {
							line = state + "," + use_this_policy_name + "," + use_this_market_name + ",RES,"
									+ year_list[i] + "," + "1" + vars.getEol();
						}
						files.writeToBufferedFile(bw0, line);
					}
				}

				double progress = s / (listOfSelectedLeaves.length - 1.);
				progress_bar.setProgress(progress);
			}

			files.closeBufferedFile(bw0);
			files.closeBufferedFile(bw1);
			files.closeBufferedFile(bw2);

			// TODO: store temp file name in options file and vars?
			String temp_file = tempDirName + File.separator + "temp_policy_file.txt";

			files.deleteFile(tempDirName);

			String temp_file0 = tempDirName + File.separator + tempFilename0;
			String temp_file1 = tempDirName + File.separator + tempFilename1;
			String temp_file2 = tempDirName + File.separator + tempFilename2;

//		//try { //causes problem because needs full name on tempFilename0,1,2...
			ArrayList<String> tempfiles = new ArrayList<String>();
			tempfiles.add(temp_file0);

			if (no_non_nested > 0)
				tempfiles.add(temp_file1);
			if (no_nested > 0)
				tempfiles.add(temp_file2);

			files.concatDestSources(temp_file, tempfiles);

			System.out.println("Done");
		}

	}

	public String getMetaDataContent(TreeView<String> tree, String market, String policy) {
		String rtn_str = "";

		rtn_str += "########## Scenario Component Metadata ##########" + vars.getEol();
		rtn_str += "#Scenario component type: Market Share" + vars.getEol();
		rtn_str += "#Type: " + comboBoxPolicyType.getValue() + vars.getEol();

		ObservableList subset_list = checkComboBoxSubset.getCheckModel().getCheckedItems();
		String subset = utils.getStringFromList(subset_list, ";");
		rtn_str += "#Subset: " + subset + vars.getEol();

		ObservableList superset_list = checkComboBoxSuperset.getCheckModel().getCheckedItems();
		String superset = utils.getStringFromList(superset_list, ";");
		rtn_str += "#Superset: " + superset + vars.getEol();

		rtn_str += "#Applied to: " + comboBoxAppliedTo.getValue() + vars.getEol();
		rtn_str += "#Treatment: " + comboBoxTreatment.getValue() + vars.getEol();
		rtn_str += "#Constraint: " + comboBoxConstraint.getValue() + vars.getEol();
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
		for (int i = 0; i < table_content.size(); i++) {
			rtn_str += "#Table data:" + table_content.get(i) + vars.getEol();
		}
		rtn_str += "#################################################" + vars.getEol();
		return rtn_str;
	}

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
				boolean match = false;

				String listOfAllowableYears = vars.getAllowablePolicyYears();
				ObservableList<DataPoint> data = this.paneForComponentDetails.table.getItems();
				String year = "";

				for (int i = 0; i < data.size(); i++) {
					year = data.get(i).getYear().trim();
					if (listOfAllowableYears.contains(year))
						match = true;
				}
				if (!match) {
					message += "Years specified in table must match allowable policy years (" + listOfAllowableYears
							+ ")" + vars.getEol();
					error_count++;
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

	@Override
	public String getFilenameSuggestion() {
		return filename_suggestion;
	}

	@Override
	public void resetFilenameSuggestion() {
		filename_suggestion = null;
	}

	@Override
	public String getFileContent() {
		return file_content;
	}

	@Override
	public void resetFileContent() {
		file_content = null;
	}

//	public void setCompletionListener(CompletionListener completion_listener2) {
//		this.completion_listener=(CompletionListener) completion_listener2;
//	}
//	
//    @FunctionalInterface
//    public interface CompletionListener {
//        String onCompleted(int count);
//    }

}