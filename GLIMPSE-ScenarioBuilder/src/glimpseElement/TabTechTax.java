
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

import java.util.ArrayList;
import org.controlsfx.control.CheckComboBox;

import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.CheckBoxTreeItem.TreeModificationEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TabTechTax extends PolicyTab implements Runnable {
	private GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	private GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	private GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	private GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

	public static String descriptionText = "";
	public static String runQueueStr = "Queue is empty.";

	double label_wid = 125;

	// Initializing overall grid
	GridPane gridPanePresetModification = new GridPane();
	ScrollPane scrollPaneLeft = new ScrollPane();

	// Label labelComponent = utils.createLabel("Specification: ");

	// Initializing components of left column
	GridPane gridPaneLeft = new GridPane();

	Label labelComboBoxSector = utils.createLabel("Sector: ", label_wid);

	Label labelFilter = utils.createLabel("Filter:", label_wid);
	TextField textFieldFilter = utils.createTextField();

	ComboBox<String> comboBoxSector = utils.createComboBoxString();

	Label labelCheckComboBoxTech = utils.createLabel("Tech(s): ", label_wid);
	CheckComboBox<String> checkComboBoxTech = utils.createCheckComboBox();

	Label labelComboBoxTaxOrSubsidy = utils.createLabel("Type: ", label_wid);
	ComboBox<String> comboBoxTaxOrSubsidy = utils.createComboBoxString();

	Label labelPolicyName = utils.createLabel("Policy: ", label_wid);
	TextField textFieldPolicyName = new TextField("");

	Label labelMarketName = utils.createLabel("Market: ", label_wid);
	TextField textFieldMarketName = new TextField("");

	Label labelUseAutoNames = utils.createLabel("Names: ", label_wid);
	CheckBox checkBoxUseAutoNames = utils.createCheckBox("Auto?");

	Label labelModificationType = utils.createLabel("Type: ", label_wid);
	ComboBox<String> comboBoxModificationType = utils.createComboBoxString();

	Label labelUnits = utils.createLabel("Units: ", label_wid);
	Label labelUnits2 = utils.createLabel("1975$s per GJ", 225.);

	Label labelStartYear = utils.createLabel("Start Year: ", label_wid);
	TextField textFieldStartYear = new TextField("2020");
	Label labelEndYear = utils.createLabel("End Year: ", label_wid);
	TextField textFieldEndYear = new TextField("2050");
	Label labelInitialAmount = utils.createLabel("Initial Val:   ", label_wid);
	TextField textFieldInitialAmount = utils.createTextField();
	Label labelGrowth = utils.createLabel("Growth (%): ", label_wid);
	TextField textFieldGrowth = utils.createTextField();
	Label labelPeriodLength = utils.createLabel("Period Length: ", label_wid);
	TextField textFieldPeriodLength = new TextField("5");

	Label labelConvertFrom = utils.createLabel("Convert $s from: ", label_wid);
	ComboBox<String> comboBoxConvertFrom = utils.createComboBoxString();

	// Initializing components of center column
	VBox vBoxCenter = new VBox();
	HBox hBoxHeaderCenter = new HBox();
	Label labelValue = utils.createLabel("Values: ");
	Button buttonPopulate = utils.createButton("Populate", styles.bigButtonWid, null);
	Button buttonImport = utils.createButton("Import", styles.bigButtonWid, null);
	Button buttonDelete = utils.createButton("Delete", styles.bigButtonWid, null);
	Button buttonClear = utils.createButton("Clear", styles.bigButtonWid, null);
	PaneForComponentDetails paneForComponentDetails = new PaneForComponentDetails();

	// Initializing components of right column
	HBox hBoxHeaderRight = new HBox();
	VBox vBoxRight = new VBox();
	PaneForCountryStateTree paneForCountryStateTree = new PaneForCountryStateTree();

	public TabTechTax(String title, Stage stageX) {
		// sets tab title
		this.setText(title);
		this.setStyle(styles.font_style);

		// sets up initial state of check box and policy and market textfields
		checkBoxUseAutoNames.setSelected(true);
		textFieldPolicyName.setDisable(true);
		textFieldMarketName.setDisable(true);

		// left column
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
		gridPaneLeft.setStyle(styles.style2);

		scrollPaneLeft.setContent(gridPaneLeft);

		// center column

		hBoxHeaderCenter.getChildren().addAll(buttonPopulate, buttonDelete, buttonClear);
		hBoxHeaderCenter.setSpacing(2.);
		hBoxHeaderCenter.setStyle(styles.style3);

		vBoxCenter.getChildren().addAll(labelValue, hBoxHeaderCenter, paneForComponentDetails);
		vBoxCenter.setStyle(styles.style2);

		// right column
		vBoxRight.getChildren().addAll(paneForCountryStateTree);
		vBoxRight.setStyle(styles.style2);

		gridPanePresetModification.addColumn(0, scrollPaneLeft);
		gridPanePresetModification.addColumn(1, vBoxCenter);
		gridPanePresetModification.addColumn(2, vBoxRight);

		gridPaneLeft.setPrefWidth(325);
		gridPaneLeft.setMinWidth(325);
		vBoxCenter.setPrefWidth(300);
		vBoxRight.setPrefWidth(300);

		// default sizing
		double max_wid = 175;
		comboBoxSector.setMaxWidth(max_wid);
		checkComboBoxTech.setMaxWidth(max_wid);
		comboBoxTaxOrSubsidy.setMaxWidth(max_wid);
		comboBoxConvertFrom.setMaxWidth(max_wid);
		textFieldStartYear.setMaxWidth(max_wid);
		textFieldEndYear.setMaxWidth(max_wid);
		textFieldInitialAmount.setMaxWidth(max_wid);
		textFieldGrowth.setMaxWidth(max_wid);
		textFieldPeriodLength.setMaxWidth(max_wid);
		textFieldFilter.setMaxWidth(max_wid);
		textFieldPolicyName.setMaxWidth(max_wid);
		textFieldMarketName.setMaxWidth(max_wid);

		double min_wid = 105;
		comboBoxSector.setMinWidth(min_wid);
		checkComboBoxTech.setMinWidth(min_wid);
		comboBoxTaxOrSubsidy.setMinWidth(min_wid);
		comboBoxConvertFrom.setMinWidth(min_wid);
		textFieldStartYear.setMinWidth(min_wid);
		textFieldEndYear.setMinWidth(min_wid);
		textFieldInitialAmount.setMinWidth(min_wid);
		textFieldGrowth.setMinWidth(min_wid);
		textFieldPeriodLength.setMinWidth(min_wid);
		textFieldFilter.setMinWidth(min_wid);
		textFieldPolicyName.setMinWidth(min_wid);
		textFieldMarketName.setMinWidth(min_wid);

		double pref_wid = 175;
		comboBoxSector.setPrefWidth(pref_wid);
		checkComboBoxTech.setPrefWidth(pref_wid);
		comboBoxTaxOrSubsidy.setPrefWidth(pref_wid);
		comboBoxConvertFrom.setPrefWidth(pref_wid);
		textFieldStartYear.setPrefWidth(pref_wid);
		textFieldEndYear.setPrefWidth(pref_wid);
		textFieldInitialAmount.setPrefWidth(pref_wid);
		textFieldGrowth.setPrefWidth(pref_wid);
		textFieldPeriodLength.setPrefWidth(pref_wid);
		textFieldFilter.setPrefWidth(pref_wid);
		textFieldPolicyName.setPrefWidth(pref_wid);
		textFieldMarketName.setPrefWidth(pref_wid);

		setupComboBoxSector();
		comboBoxSector.getSelectionModel().selectFirst();

		checkComboBoxTech.getItems().add("Select One or More");
		checkComboBoxTech.getCheckModel().check(0);
		checkComboBoxTech.setDisable(true);

		comboBoxTaxOrSubsidy.getItems().addAll("Select One", "Tax", "Subsidy");
		comboBoxTaxOrSubsidy.getSelectionModel().selectFirst();

		comboBoxModificationType.getItems().addAll("Initial w/% Growth/yr", "Initial w/% Growth/pd",
				"Initial w/Delta/yr", "Initial w/Delta/pd", "Initial and Final");
		comboBoxModificationType.getSelectionModel().selectFirst();

		comboBoxConvertFrom.getItems().addAll("None", "2023$s", "2020$s", "2015$s", "2010$s", "2005$s", "2000$s");
		comboBoxConvertFrom.getSelectionModel().selectFirst();

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
			// setUnitsLabel();
		});

		comboBoxSector.setOnAction(e -> {
			String selectedItem = comboBoxSector.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				if (selectedItem.equals("Select One")) {
					checkComboBoxTech.getItems().clear();
					checkComboBoxTech.getItems().add("Select One or More");
					checkComboBoxTech.getCheckModel().check(0);
					checkComboBoxTech.setDisable(true);
				} else {
					checkComboBoxTech.setDisable(false);
					updateCheckComboBoxTech();
				}
			}
			setPolicyAndMarketNames();
			// setUnitsLabel();
		});

		checkComboBoxTech.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
			public void onChanged(ListChangeListener.Change<? extends String> c) {
				while (c.next()) {
					setUnitsLabel();
				}
			}
		});

		comboBoxTaxOrSubsidy.setOnAction(e -> {
			setPolicyAndMarketNames();
			// setUnitsLabel();
		});

		EventHandler<TreeModificationEvent> ev = new EventHandler<TreeModificationEvent>() {
			@Override
			public void handle(TreeModificationEvent ae) {
				ae.consume();
				setPolicyAndMarketNames();
				// setUnitsLabel();
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
			case "Initial and Final":
				this.labelGrowth.setText("Final Val:");
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

		textFieldFilter.setOnAction(e -> {
			setupComboBoxSector();
		});

		setPolicyAndMarketNames();
		setUnitsLabel();

		VBox tabLayout = new VBox();
		tabLayout.getChildren().addAll(gridPanePresetModification);

		this.setContent(tabLayout);
	}

	//	private void setupComboBoxSector() {
	//
	//		try {
	//			String[][] tech_info = vars.getTechInfo();
	//
	//			ArrayList<String> sectorList = new ArrayList<String>();
	//			sectorList.add("Select One");
	//			sectorList.add("All");
	//
	//			for (int i = 0; i < tech_info.length; i++) {
	//
	//				String text = tech_info[i][0].trim();
	//
	//				boolean match = false;
	//				for (int j = 0; j < sectorList.size(); j++) {
	//					if (text.equals(sectorList.get(j)))
	//						match = true;
	//				}
	//				if (!match)
	//					sectorList.add(text);
	//			}
	//
	//			for (int i = 0; i < sectorList.size(); i++) {
	//				comboBoxSector.getItems().add(sectorList.get(i).trim());
	//			}
	//
	//		} catch (Exception e) {
	//			utils.warningMessage("Problem reading tech list.");
	//			System.out.println("Error reading tech list from " + vars.get("tchBndListFile") + ":");
	//			System.out.println("  ---> " + e);
	//
	//		}
	//	}

	private void setupComboBoxSector() {
		comboBoxSector.getItems().clear();

		try {
			String[][] tech_info = vars.getTechInfo();

			ArrayList<String> sectorList = new ArrayList<String>();

			String filterText = textFieldFilter.getText().trim();
			boolean useFilter = false;
			if ((filterText != null) && (filterText.length() > 0))
				useFilter = true;

			if (!useFilter)
				sectorList.add("Select One");
			sectorList.add("All");

			for (int i = 0; i < tech_info.length; i++) {

				String text = tech_info[i][0].trim();

				boolean match = false;
				for (int j = 0; j < sectorList.size(); j++) {
					if (text.equals(sectorList.get(j)))
						match = true;
				}
				if (!match) {
					boolean show = true;
					if (useFilter) {
						show = false;
						for (int j = 0; j < tech_info[i].length; j++) {
							String temp = tech_info[i][j];
							if (temp.contains(filterText))
								show = true;
						}
					}
					if (show) {
						sectorList.add(text);
					}
				}
			}

			for (int i = 0; i < sectorList.size(); i++) {
				comboBoxSector.getItems().add(sectorList.get(i).trim());
			}

			comboBoxSector.getSelectionModel().select(0);

		} catch (Exception e) {
			utils.warningMessage("Problem reading tech list.");
			System.out.println("Error reading tech list from " + vars.get("tchBndListFile") + ":");
			System.out.println("  ---> " + e);

		}
	}

	//	private void updateCheckComboBoxTech() {
	//		String sector = comboBoxSector.getValue();
	//		if (sector.equals("Electric Sector"))
	//			sector = "electricity";
	//		if (sector.equals("Light Duty Vehicles"))
	//			sector = "trn_pass_road_LDV_4W";
	//		if (sector.equals("Heavy Duty Vehicles"))
	//			sector = "trn_freight_road";
	//
	//		try {
	//
	//			String[][] tech_info = vars.getTechInfo();
	//
	//			checkComboBoxTech.getItems().clear();
	////			checkComboBoxTech.getItems().add("Select One");
	////			checkComboBoxTech.getCheckModel().check(0);
	//		
	//			if (sector != null) {
	//				String last_line="";
	//				for (int i = 0; i < tech_info.length; i++) {
	//					String line = tech_info[i][0].trim() + " : " + tech_info[i][1] + " : " + tech_info[i][2];
	//					if (line.equals(last_line)) {
	//						;
	//					} else {
	//						last_line=line;
	//						if ((line.startsWith(sector))||(sector.equals("All"))) {
	//							if (tech_info[i].length >= 7)
	//								line += " : " + tech_info[i][6];
	//							if (line.length() > 0) {
	//								checkComboBoxTech.getItems().add(line);
	//							}
	//						}
	//					}
	//				}
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

	private void updateCheckComboBoxTech() {
		String sector = comboBoxSector.getValue();
		String[][] tech_info = vars.getTechInfo();

		boolean is_all_sectors = false;
		if (sector.equals("All"))
			is_all_sectors = true;

		try {
			if (checkComboBoxTech.getItems().size() > 0) {
				checkComboBoxTech.getCheckModel().clearChecks();
				checkComboBoxTech.getItems().clear();
			}

			if (sector != null) {

				String last_line = "";
				String filter_text = this.textFieldFilter.getText().trim();

				for (int i = 0; i < tech_info.length; i++) {
					String line_sector=tech_info[i][0].trim();
					String line = line_sector + " : " + tech_info[i][1] + " : " + tech_info[i][2];
					if ((filter_text.length() == 0) || (line.contains(filter_text))) {

						if (tech_info[i].length >= 7)
							line += " : " + tech_info[i][6];

						if (line.equals(last_line)) {
							;
						} else {
							last_line = line;
							if ((is_all_sectors) || (line_sector.equals(sector))) {
								checkComboBoxTech.getItems().add(line);
							}
						}
					}
				}
			}

		} catch (Exception e) {
			utils.warningMessage("Problem reading tech list.");
			System.out.println("Error reading tech list from " + vars.get("tchBndListFile") + ":");
			System.out.println("  ---> " + e);

		}

	}

	private void setPolicyAndMarketNames() {

		if (this.checkBoxUseAutoNames.isSelected()) {

			String policy_type = "---";
			String technology = "Tech";
			String sector = "---";
			String state = "--";

			try {
				String s = comboBoxTaxOrSubsidy.getValue();
				if (s.indexOf("Tax") >= 0)
					policy_type = "Tax";
				if (s.indexOf("Sub") >= 0)
					policy_type = "Sub";

				s = comboBoxSector.getValue();
				if (!s.equals("Select One")) {
					s = s.replace(" ", "_");
					s = utils.capitalizeOnlyFirstLetterOfString(s);
					sector = s;
				}

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

				String name = policy_type + sector + technology + state;
				name = name.replaceAll(" ", "_").replaceAll("--", "-");
				textFieldMarketName.setText(name + "_Mkt");
				textFieldPolicyName.setText(name);

			} catch (Exception e) {
				System.out.println("Cannot auto-name market. Continuing.");
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
		double factor = 1.0;
		String convertYear = this.comboBoxConvertFrom.getValue();
		String tempUnitsVal = this.labelUnits2.getText();
		String toYear = "1975$s";
		if (tempUnitsVal.contains("1990"))
			toYear = "1990$s";
		if (convertYear != "None") {
			factor = utils.getConversionFactor(convertYear, toYear);
		}
		double[][] returnMatrix = utils.calculateValues(calc_type, start_year, end_year, initial_value, growth,
				period_length, factor);
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
		if (!qaInputs()){
			Thread.currentThread().destroy();
		} else {

			String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);

			listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
			String states = utils.returnAppendedString(listOfSelectedLeaves);

			filename_suggestion = "";

			// constructs a filename suggestion for the scenario component

			String tax_or_subsidy = comboBoxTaxOrSubsidy.getSelectionModel().getSelectedItem().trim().toLowerCase();

			//String ID=this.getUniqueMarketName(textFieldMarketName.getText());
			String ID=utils.getUniqueString();
			String policy_name = this.textFieldPolicyName.getText()+ID;
			String market_name = this.textFieldMarketName.getText()+ID;
			filename_suggestion=this.textFieldPolicyName.getText().replaceAll("/", "-").replaceAll(" ", "_")+".csv";

			// sets up the content of the CSV file to store the scenario component data
			file_content = getMetaDataContent(paneForCountryStateTree.getTree(),market_name,policy_name);
			String file_content_trn=""+vars.getEol();

			for (int iter=0;iter<2;iter++) {

				String content="";
				String iter_type="";

				if (iter==0) {
					iter_type="Std";
				} else {
					iter_type="Tran";
				}

				String which = "tax";
				String header_part1 = "GLIMPSEPF"+iter_type+"TechTaxP1";
				String header_part2 = "GLIMPSEPF"+iter_type+"TechTaxP2";
				String header_part3 = "GLIMPSEPF"+iter_type+"TechTaxP3";

				if (tax_or_subsidy.equals("subsidy")) {
					which = "subsidy";
					header_part1 = "GLIMPSEPF"+iter_type+"TechSubsidyP1";
					header_part2 = "GLIMPSEPF"+iter_type+"TechSubsidyP2";
					header_part3 = "GLIMPSEPF"+iter_type+"TechSubsidyP3";
				}

				//String sector = comboBoxSector.getSelectionModel().getSelectedItem().trim();

				ObservableList<String> tech_lines=checkComboBoxTech.getCheckModel().getCheckedItems();

				for (int k=0;k<tech_lines.size();k++) {

					String[] temp = utils.splitString(tech_lines.get(k).trim(), ":");
					String sector =temp[0].trim();
					String subsector = temp[1].trim();
					String tech = temp[2].trim();				

					if (((iter==0)&&(!sector.startsWith("trn")))||(((iter==1)&&(sector.startsWith("trn"))))){

						// part 1
						file_content += "INPUT_TABLE" + vars.getEol();
						file_content += "Variable ID" + vars.getEol();
						if (subsector.indexOf("=>")>-1) {
							file_content += header_part1+"-Nest" + vars.getEol() + vars.getEol();
							file_content += "region,sector,nesting-subsector,subsector,tech,year,policy-name" + vars.getEol();
							subsector=subsector.replace("=>",",");
						} else {
							file_content += header_part1 + vars.getEol() + vars.getEol();
							file_content += "region,sector,subsector,tech,year,policy-name" + vars.getEol();
						}
						for (int s = 0; s < listOfSelectedLeaves.length; s++) {
							String state = listOfSelectedLeaves[s];

							ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();
							for (int i = 0; i < data.size(); i++) {
								String data_str = data.get(i).replace(" ", "");
								String year = utils.splitString(data_str, ",")[0];
								file_content += state + "," + sector + "," + subsector + "," + tech + "," + year + "," + policy_name
										+ vars.getEol();
							}

						}

						// part 2
						file_content += vars.getEol();
						file_content += "INPUT_TABLE" + vars.getEol();
						file_content += "Variable ID" + vars.getEol();
						file_content += header_part2 + vars.getEol() + vars.getEol();
						file_content += "region,policy-name,market,type,policy-yr,policy-val" + vars.getEol();

						if (listOfSelectedLeaves.length > 0) {
							String state = listOfSelectedLeaves[0];
							ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();
							for (int i = 0; i < data.size(); i++) {
								String data_str = data.get(i).replace(" ", "");
								String year = utils.splitString(data_str, ",")[0];
								String val = utils.splitString(data_str, ",")[1];

								file_content += state + "," + policy_name + "," + market_name + "," + which + "," + year + "," + val
										+ vars.getEol();
							}
						}

						// part 3
						file_content += vars.getEol();
						file_content += "INPUT_TABLE" + vars.getEol();
						file_content += "Variable ID" + vars.getEol();
						file_content += header_part3 + vars.getEol() + vars.getEol();
						file_content += "region,policy-name,market,type" + vars.getEol();

						for (int s = 0; s < listOfSelectedLeaves.length; s++) {
							String state = listOfSelectedLeaves[s];

							file_content += state + "," + policy_name + "," + market_name + "," + which + vars.getEol();

						}
						file_content += vars.getEol();
					}
				}
			}
		}
	}

	private void saveScenarioComponentWorks(TreeView<String> tree) {
		if (!qaInputs()) {
			Thread.currentThread().destroy();
		} else {

			String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);

			listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
			String states = utils.returnAppendedString(listOfSelectedLeaves);

			filename_suggestion = "";

			// constructs a filename suggestion for the scenario component

			String tax_or_subsidy = comboBoxTaxOrSubsidy.getSelectionModel().getSelectedItem().trim().toLowerCase();

			// String ID=this.getUniqueMarketName(textFieldMarketName.getText());
			String ID = utils.getUniqueString();
			String policy_name = this.textFieldPolicyName.getText() + ID;
			String market_name = this.textFieldMarketName.getText() + ID;
			filename_suggestion = this.textFieldPolicyName.getText().replaceAll("/", "-").replaceAll(" ", "_") + ".csv";

			// sets up the content of the CSV file to store the scenario component data
			file_content = getMetaDataContent(paneForCountryStateTree.getTree(), market_name, policy_name);

			String which = "tax";
			String header_part1 = "GLIMPSEPFStdTechTaxP1";
			String header_part2 = "GLIMPSEPFStdTechTaxP2";
			String header_part3 = "GLIMPSEPFStdTechTaxP3";

			if (tax_or_subsidy.equals("subsidy")) {
				which = "subsidy";
				header_part1 = "GLIMPSEPFStdTechSubsidyP1";
				header_part2 = "GLIMPSEPFStdTechSubsidyP2";
				header_part3 = "GLIMPSEPFStdTechSubsidyP3";
			}

			String sector = comboBoxSector.getSelectionModel().getSelectedItem().trim();

			ObservableList<String> tech_lines = checkComboBoxTech.getCheckModel().getCheckedItems();

			for (int k = 0; k < tech_lines.size(); k++) {

				String[] temp = utils.splitString(tech_lines.get(k).trim(), ":");
				String subsector = temp[1].trim();
				String tech = temp[2].trim();

				// part 1
				file_content += "INPUT_TABLE" + vars.getEol();
				file_content += "Variable ID" + vars.getEol();
				if (subsector.indexOf("=>") > -1) {
					file_content += header_part1 + "-Nest" + vars.getEol() + vars.getEol();
					file_content += "region,sector,nesting-subsector,subsector,tech,year,policy-name" + vars.getEol();
					subsector = subsector.replace("=>", ",");
				} else {
					file_content += header_part1 + vars.getEol() + vars.getEol();
					file_content += "region,sector,subsector,tech,year,policy-name" + vars.getEol();
				}
				for (int s = 0; s < listOfSelectedLeaves.length; s++) {
					String state = listOfSelectedLeaves[s];

					ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();
					for (int i = 0; i < data.size(); i++) {
						String data_str = data.get(i).replace(" ", "");
						String year = utils.splitString(data_str, ",")[0];
						file_content += state + "," + sector + "," + subsector + "," + tech + "," + year + ","
								+ policy_name + vars.getEol();
					}

				}

				// part 2
				file_content += vars.getEol();
				file_content += "INPUT_TABLE" + vars.getEol();
				file_content += "Variable ID" + vars.getEol();
				file_content += header_part2 + vars.getEol() + vars.getEol();
				file_content += "region,policy-name,market,type,policy-yr,policy-val" + vars.getEol();

				if (listOfSelectedLeaves.length > 0) {
					String state = listOfSelectedLeaves[0];
					ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();
					for (int i = 0; i < data.size(); i++) {
						String data_str = data.get(i).replace(" ", "");
						String year = utils.splitString(data_str, ",")[0];
						String val = utils.splitString(data_str, ",")[1];

						file_content += state + "," + policy_name + "," + market_name + "," + which + "," + year + ","
								+ val + vars.getEol();
					}
				}

				// part 3
				file_content += vars.getEol();
				file_content += "INPUT_TABLE" + vars.getEol();
				file_content += "Variable ID" + vars.getEol();
				file_content += header_part3 + vars.getEol() + vars.getEol();
				file_content += "region,policy-name,market,type" + vars.getEol();

				for (int s = 0; s < listOfSelectedLeaves.length; s++) {
					String state = listOfSelectedLeaves[s];

					file_content += state + "," + policy_name + "," + market_name + "," + which + vars.getEol();

				}
				file_content += vars.getEol();
			}
		}
	}

	public String getMetaDataContent(TreeView<String> tree, String market, String policy) {
		String rtn_str = "";

		rtn_str += "########## Scenario Component Metadata ##########" + vars.getEol();
		rtn_str += "#Scenario component type: Tech Tax/Subsidy" + vars.getEol();

		ObservableList tech_list = checkComboBoxTech.getCheckModel().getCheckedItems();
		String techs = utils.getStringFromList(tech_list, ";");
		rtn_str += "#Technologies: " + techs + vars.getEol();
		rtn_str += "#Type: " + comboBoxModificationType.getSelectionModel().getSelectedItem() + vars.getEol();
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

				if (param.equals("sector")) {
					comboBoxSector.setValue(value);
					comboBoxSector.fireEvent(new ActionEvent());
				}
				if (param.equals("technologies")) {
					checkComboBoxTech.getCheckModel().clearChecks();
					String[] set = utils.splitString(value, ";");
					for (int j = 0; j < set.length; j++) {
						String item = set[j].trim();
						checkComboBoxTech.getCheckModel().check(item);
						checkComboBoxTech.fireEvent(new ActionEvent());
					}
				}
				if (param.equals("type")) {
					comboBoxModificationType.setValue(value);
					comboBoxModificationType.fireEvent(new ActionEvent());
				}
				if (param.equals("regions")) {
					String[] regions = utils.splitString(value, ",");
					this.paneForCountryStateTree.selectNodes(regions);
				}
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

			if (comboBoxSector.getSelectionModel().getSelectedItem().equals("Select One")) {
				message += "Sector comboBox must have a selection" + vars.getEol();
				error_count++;
			}
			if (checkComboBoxTech.getCheckModel().getCheckedItems().size() <= 0) {
				message += "Tech checkComboBox must have a selection" + vars.getEol();
				error_count++;
			}
			if (comboBoxTaxOrSubsidy.getSelectionModel().getSelectedItem().equals("Select One")) {
				message += "Type comboBox must have a selection" + vars.getEol();
				error_count++;
			}
			if (textFieldPolicyName.getText().equals("")) {
				message += "A policy name must be provided" + vars.getEol();
				error_count++;
			}
			if (textFieldMarketName.getText().equals("")) {
				message += "A market name must be provided" + vars.getEol();
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

	public void setUnitsLabel() {
		String s = getUnits();

		String label = "";

		if (s.equals("No match")) {
			label = "Warning - Units do not match!";
		} else if (s.equals("million pass-km")) {
			label = "1990$ per veh-km";
		} else if (s.equals("million ton-km")) {
			label = "1990$ per veh-km";
		} else if (s.equals("")) {
			label = "";
		} else {
			String s2 = "GJ";
			if (s.equals("EJ"))
				s2 = "GJ";
			if (s.equals("petalumen-hours"))
				s2 = "megalumen-hours";
			if (s.equals("million km3"))
				s2 = "million m3";
			if (s.equals("billion cycles"))
				s2 = "cycle";
			if (s.equals("Mt"))
				s2 = "kg";
			if (s.equals("km^3"))
				s2 = "m^3";

			label = "1975$s per " + s2;
		}

		labelUnits2.setText(label);

	}

	public String getUnits() {
		String rtn_str = "";

		ObservableList<String> tech_list = checkComboBoxTech.getCheckModel().getCheckedItems();

		String unit = "";

		for (int t = 0; t < tech_list.size(); t++) {
			String line = tech_list.get(t);
			String item = "";
			try {
				item = line.substring(line.lastIndexOf(":") + 1).trim();
				if (unit.equals("")) {
					unit = item;
				} else if (!unit.equals(item)) {
					unit = "No match";
				}
			} catch (Exception e) {
				item = "";
			}
		}
		if (unit.trim().equals("Select One or More"))
			unit = "";
		return unit;
	}

}