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
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
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

public class TabPollutantTaxCap extends PolicyTab implements Runnable {
	private GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	private GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	private GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	private GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

	public static String descriptionText = "";
	public static String runQueueStr = "Queue is empty.";

	int check_count = 0;

	double label_wid = 125;

	// String file_content=null;

	// Initializing overall grid
	GridPane gridPanePresetModification = new GridPane();

	// Initializing components of left column
	GridPane gridPaneLeft = new GridPane();

	Label labelComboBoxMeasure = utils.createLabel("Measure: ", label_wid);
	ComboBox<String> comboBoxMeasure = utils.createComboBoxString();

	Label labelComboBoxSector = utils.createLabel("Sector: ", label_wid);
	ComboBox<String> comboBoxSector = utils.createComboBoxString();
	Label labelComboBoxPollutant = utils.createLabel("Pollutant: ", label_wid);
	ComboBox<String> comboBoxPollutant = utils.createComboBoxString();

	Label labelModificationType = utils.createLabel("Type: ", label_wid);
	ComboBox<String> comboBoxModificationType = utils.createComboBoxString();

	Label labelUseAutoNames = utils.createLabel("Names: ", label_wid);
	CheckBox checkBoxUseAutoNames = utils.createCheckBox("Auto?");

	Label labelPolicyName = utils.createLabel("Policy: ", label_wid);
	TextField textFieldPolicyName = new TextField("");

	Label labelMarketName = utils.createLabel("Market: ", label_wid);
	TextField textFieldMarketName = new TextField("");

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
	
	Label labelConvertFrom = utils.createLabel("Convert $s from: ",label_wid);
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

	public TabPollutantTaxCap(String title, Stage stageX) {
		// sets tab title
		this.setText(title);
		this.setStyle(styles.font_style);

		// sets up initial state of check box and policy and market textfields
		checkBoxUseAutoNames.setSelected(true);
		textFieldPolicyName.setDisable(true);
		textFieldMarketName.setDisable(true);

		// left column

		gridPaneLeft.add(utils.createLabel("Specification:"), 0, 0, 2, 1);
		gridPaneLeft.addColumn(0, labelComboBoxMeasure, labelComboBoxPollutant, labelComboBoxSector, new Label(),
				new Separator(), labelUseAutoNames, /* labelPolicyName, */
				labelPolicyName, labelMarketName, new Label(), new Separator(), utils.createLabel("Populate:"),
				labelModificationType, labelStartYear, labelEndYear, labelInitialAmount, labelGrowth,labelConvertFrom);

		gridPaneLeft.addColumn(1, comboBoxMeasure, comboBoxPollutant,  comboBoxSector, new Label(), new Separator(),
				checkBoxUseAutoNames, textFieldPolicyName, textFieldMarketName, new Label(), new Separator(),
				new Label(), comboBoxModificationType, textFieldStartYear, textFieldEndYear, textFieldInitialAmount,
				textFieldGrowth,comboBoxConvertFrom);

		gridPaneLeft.setVgap(3.);
		gridPaneLeft.setStyle(styles.style2);

		// center column

		hBoxHeaderCenter.getChildren().addAll(buttonPopulate, buttonDelete, buttonClear);
		hBoxHeaderCenter.setSpacing(2.);
		hBoxHeaderCenter.setStyle(styles.style3);

		vBoxCenter.getChildren().addAll(labelValue, hBoxHeaderCenter, paneForComponentDetails);
		vBoxCenter.setStyle(styles.style2);

		// right column
		vBoxRight.getChildren().addAll(paneForCountryStateTree);
		vBoxRight.setStyle(styles.style2);

		gridPanePresetModification.addColumn(0, gridPaneLeft);
		gridPanePresetModification.addColumn(1, vBoxCenter);
		gridPanePresetModification.addColumn(2, vBoxRight);

		gridPaneLeft.setPrefWidth(370);
		gridPaneLeft.setMinWidth(370);
		vBoxCenter.setPrefWidth(300);
		vBoxRight.setPrefWidth(300);

		// default sizing
		double max_wid = 225;
		comboBoxSector.setMaxWidth(max_wid);
		comboBoxMeasure.setMaxWidth(max_wid);
		comboBoxModificationType.setMaxWidth(max_wid);
		comboBoxPollutant.setMaxWidth(max_wid);

		double min_wid = 115;
		comboBoxSector.setMinWidth(min_wid);
		comboBoxMeasure.setMinWidth(min_wid);
		comboBoxModificationType.setMinWidth(min_wid);
		comboBoxPollutant.setMinWidth(min_wid);

		double pref_wid = 225;
		comboBoxSector.setPrefWidth(pref_wid);
		comboBoxMeasure.setPrefWidth(pref_wid);
		comboBoxModificationType.setPrefWidth(pref_wid);
		comboBoxPollutant.setPrefWidth(pref_wid);

		comboBoxMeasure.getItems().addAll("Select One", "Emission Cap (Mt)", "Emission Tax ($/t)");

		comboBoxPollutant.getItems().addAll("Select One", "CO2 (MT C)", "CO2 (MT CO2)", "GHG (MT CO2E)", "NOx (Tg)",
				"SO2 (Tg)", "PM2.5 (Tg)", "NMVOC (Tg)", "CO (Tg)", "NH3 (Tg)", "CH4 (Tg)", "N2O (Tg)");

		comboBoxSector.getItems().addAll("Select One", "All", "EGU", "Industry-All", "Industry-Fuels", "Buildings",
				"Trn-All", "Trn-Onroad", "Trn-Nonroad");

		comboBoxModificationType.getItems().addAll("Initial w/% Growth/yr", "Initial w/% Growth/pd",
				"Initial w/Delta/yr", "Initial w/Delta/pd", "Initial and Final");

		comboBoxMeasure.getSelectionModel().selectFirst();
		comboBoxPollutant.getSelectionModel().selectFirst();
		comboBoxSector.getSelectionModel().selectFirst();
		comboBoxModificationType.getSelectionModel().selectFirst();

		comboBoxPollutant.setDisable(true);
		comboBoxSector.setDisable(true);

		comboBoxConvertFrom.getItems().addAll("None","2023$s","2020$s","2015$s","2010$s","2005$s","2000$s");
		comboBoxConvertFrom.getSelectionModel().selectFirst();
		
		labelConvertFrom.setVisible(false);
		comboBoxConvertFrom.setVisible(false);
		
		// Action

		comboBoxMeasure.setOnAction(e -> {
			if (comboBoxMeasure.getSelectionModel().getSelectedIndex() > 0) {
				comboBoxPollutant.setDisable(false);
				comboBoxSector.setDisable(true);
			} else {
				comboBoxPollutant.setDisable(true);				
				comboBoxSector.setDisable(true);		
			}

			comboBoxPollutant.getSelectionModel().selectFirst();
			comboBoxSector.getSelectionModel().selectFirst();
			
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

			if (selectedItem != "Select One") {
				comboBoxSector.setDisable(true);
				comboBoxSector.getSelectionModel().select("All");
				if (selectedItem.startsWith("CO2")) {
					comboBoxSector.setDisable(false);
					comboBoxSector.getSelectionModel().selectFirst();
				}
			}

			setPolicyAndMarketNames();

		});

		comboBoxSector.setOnAction(e -> {
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

		setPolicyAndMarketNames();

		VBox tabLayout = new VBox();
		tabLayout.getChildren().addAll(gridPanePresetModification);

		this.setContent(tabLayout);
	}

	private void setPolicyAndMarketNames() {
		if (this.checkBoxUseAutoNames.isSelected()) {

			String policy_type = "--";
			String pollutant = "--";
			String sector = "--";
			String state = "--";

			try {
				String s = comboBoxMeasure.getValue();
				if (s.indexOf("Tax") >= 0)
					policy_type = "Tax";
				if (s.indexOf("Cap") >= 0)
					policy_type = "Cap";
				s = comboBoxSector.getValue();
				if (!s.startsWith("Select"))
					sector = s;
				s = comboBoxPollutant.getValue();
				if (!s.equals("Select One")) {
					pollutant = utils.splitString(s, " ")[0];
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

				String name = policy_type + "_" + sector + "_" + pollutant + "_" + state;
				textFieldMarketName.setText(name + "_Mkt");
				textFieldPolicyName.setText(name);

			} catch (Exception e) {
				System.out.println("Error trying to auto-name market");
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
		double factor=1.0;
		String convertYear=this.comboBoxConvertFrom.getValue();
		if (convertYear!="None") {
			factor=utils.getConversionFactor(convertYear,"1990$s");
		}
		double[][] returnMatrix = utils.calculateValues(calc_type, false, start_year, end_year, initial_value, growth,
				period_length,factor);

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

			int start_year = 2010;

			String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);

			// Dan: messy approach to make sure inclusion of USA is intentional
			listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
			String states = utils.returnAppendedString(listOfSelectedLeaves);

			// String ID=this.getUniqueMarketName(textFieldMarketName.getText());
			String ID = utils.getUniqueString();
			String policy_name = this.textFieldPolicyName.getText() + ID;
			String market_name = this.textFieldMarketName.getText() + ID;
			filename_suggestion = this.textFieldPolicyName.getText().replaceAll("/", "-").replaceAll(" ", "_") + ".csv";

			String sector = comboBoxSector.getValue();

			String type = this.comboBoxMeasure.getValue();
			if (type.indexOf("Cap") > -1) {
				type = "Cap";
			} else {
				type = "Tax";
			}

			String pol_selection = comboBoxPollutant.getSelectionModel().getSelectedItem().trim() + " ";
			String pol = pol_selection.substring(0, pol_selection.indexOf(" ")).trim();

			// sets up the content of the CSV file to store the scenario component data
			file_content = getMetaDataContent(tree, market_name, policy_name);

			String file_content2 = "";

			boolean generate_links = false;

			if (pol_selection.startsWith("CO2") && (sector.equals("All")) &&(type.equals("Cap"))) {
				//implementing new cap approach suggested by Yang Ou for more robust solution process
				// when applied to complex scenarios
				saveScenarioComponentCO2Cap(listOfSelectedLeaves, pol_selection, sector, market_name, policy_name);
				return;

			} else if (!pol.startsWith("GHG")) {

				String pol_orig = pol;

				// part 0 - sets up pollutant species to tax or cap
				if (!sector.equals("All")) {
					pol = market_name;
				}
				// cycle through selected regions and tech list ... link pol to each tech

				// part 1 - sets up cap or tax values

				file_content += "INPUT_TABLE" + vars.getEol();
				file_content += "Variable ID" + vars.getEol();
				if (type.equals("Cap")) {
					file_content += "GLIMPSEEmissionCap" + vars.getEol() + vars.getEol();
					file_content += "region,pollutant,market,year,cap" + vars.getEol();
					;
				} else if (type.equals("Tax")) {
					file_content += "GLIMPSEEmissionTax" + vars.getEol() + vars.getEol();
					file_content += "region,pollutant,market,year,tax" + vars.getEol();
					;
				}

				if (listOfSelectedLeaves.length > 0) {
					String state = listOfSelectedLeaves[0];
					ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();
					for (int i = 0; i < data.size(); i++) {
						String data_str = data.get(i).replaceAll(" ", "");
						file_content += state + "," + pol + "," + market_name + "," + data_str + vars.getEol();
					}
				}

				// part 2 - set up the regions in the market
				if (listOfSelectedLeaves.length > 1) {
					file_content += vars.getEol();
					file_content += "INPUT_TABLE" + vars.getEol();
					file_content += "Variable ID" + vars.getEol();
					file_content += "GLIMPSEEmissionMarket" + vars.getEol();
					file_content += vars.getEol();
					file_content += "region,pollutant,market" + vars.getEol();
					for (int s = 1; s < listOfSelectedLeaves.length; s++) {
						file_content += listOfSelectedLeaves[s] + "," + pol + "," + market_name + vars.getEol();
						double progress = s / (listOfSelectedLeaves.length - 1.);
						progress_bar.setProgress(progress);
					}

				}

				// part 3 - setting up sector-specific pollutant definitions
				if (!sector.equals("All")) {

					ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();

					String file_content_nest = vars.getEol();
					file_content_nest += "INPUT_TABLE" + vars.getEol();
					file_content_nest += "Variable ID" + vars.getEol();
					file_content_nest += "GLIMPSEAddCO2Subspecies-Nest" + vars.getEol();
					file_content_nest += vars.getEol();
					file_content_nest += "region,supplysector,nesting-subsector,subsector,technology,year,pollutant"
							+ vars.getEol();
					int nest_count = 0;

					String file_content_nonest = vars.getEol();
					file_content_nonest += "INPUT_TABLE" + vars.getEol();
					file_content_nonest += "Variable ID" + vars.getEol();
					file_content_nonest += "GLIMPSEAddCO2Subspecies" + vars.getEol();
					file_content_nonest += vars.getEol();
					file_content_nonest += "region,supplysector,subsector,technology,year,pollutant" + vars.getEol();
					int nonest_count = 0;

					int max_year = 0;
					for (int m = 0; m < data.size(); m++) {
						int year = Integer.parseInt(data.get(m).split(",")[0].trim());
						if (year > max_year)
							max_year = year;
					}

					if (listOfSelectedLeaves.length > 0) {
						for (int s = 0; s < listOfSelectedLeaves.length; s++) {

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
											|| ((sector_lwc.equals("industry-all")) || (sector_lwc.equals("ind-all")))
													&& (cat_r_lwc.startsWith("ind")))
											|| ((sector_lwc.equals("trn-all")) && (cat_r_lwc.startsWith("trn")))) {
										String line = listOfSelectedLeaves[s] + "," + sector_r + ","
												+ subsector_r.replace("=>", ",") + "," + tech_r + "," + y + "," + pol
												+ vars.getEol();
										;
										if (subsector_r.indexOf("=>") > -1) {
											file_content_nest += line;
											nest_count++;
										} else {
											file_content_nonest += line;
											nonest_count++;
										}
									}

								}
							}

							double progress = s / (listOfSelectedLeaves.length - 1.);
							progress_bar.setProgress(progress);
						}
					}
					if (nest_count > 0)
						file_content += file_content_nest;
					if (nonest_count > 0)
						file_content += file_content_nonest;
				}
				// cycle through selected regions and tech list ... link pol to each tech

			} else { // pol does start with GHG
				// GHG target (must be system-wide)
				// part 1 - sets up cap or tax values
				file_content += "INPUT_TABLE" + vars.getEol();
				file_content += "Variable ID" + vars.getEol();
				if (type.equals("Cap")) {
					file_content += "GLIMPSEGHGEmissionCap" + vars.getEol();
					file_content += vars.getEol();
					file_content += "region,GHG-Policy,GHG-Market,year,cap" + vars.getEol();

				} else if (type.equals("Tax")) {
					file_content += "GLIMPSEGHGEmissionTax" + vars.getEol();
					file_content += vars.getEol();
					file_content += "region,GHG-Policy,GHG-Market,year,tax" + vars.getEol();

				}

				if (listOfSelectedLeaves.length > 0) {
					String state = listOfSelectedLeaves[0];
					ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();
					for (int i = 0; i < data.size(); i++) {
						String data_str = data.get(i).replace(" ", "");
						file_content += state + "," + policy_name + "," + market_name + "," + data_str + vars.getEol();
					}
				}

				// part 2 // part 2 - set up the regions in the market
				if (listOfSelectedLeaves.length > 1) {
					file_content += vars.getEol();
					file_content += "INPUT_TABLE" + vars.getEol();
					file_content += "Variable ID" + vars.getEol();
					file_content += "GLIMPSEEmissionMarket" + vars.getEol();
					file_content += vars.getEol();
					file_content += "region,pollutant,market" + vars.getEol();
					for (int s = 1; s < listOfSelectedLeaves.length; s++) {
						file_content += listOfSelectedLeaves[s] + "," + policy_name + "," + market_name + vars.getEol();
						double progress = s / (listOfSelectedLeaves.length - 1.);
						progress_bar.setProgress(progress);
					}
					// file_content += vars.getEol();
				}


				// part 3 - set up the linked GHG part regions in the market
				file_content2 += vars.getEol();
				file_content2 += "INPUT_TABLE" + vars.getEol();
				file_content2 += "Variable ID" + vars.getEol();
				file_content2 += "GLIMPSELinkedGHGEmissionMarketP1" + vars.getEol();
				file_content2 += vars.getEol();
				file_content2 += "region,pollutant,GHG-market,GHG-Policy,price-adjust,demand-adjust,price-unit,output-unit"
						+ vars.getEol();

				String[] GHGs = new String[] { "CO2", "CH4", "N2O", "C2F6", "CF4", "HFC125", "HFC134a", "HRC245fa",
						"SF6", "CH4_AWB", "CH4_AGR", "N2O_AWB", "N2O_AGR" };
				String[] price_adjust = new String[] { "1", "5.728", "84.55", "0", "0", "0", "0", "0", "0", "5.727",
						"5.727", "84.55", "84.55" };
				String[] demand_adjust = new String[] { "3.667", "21", "310", "9.2", "6.5", "2.8", "1.3", "1.03",
						"23.9", "21", "21", "310", "310" };
				String[] price_unit = new String[] { "1990$/tC", "1990$/GgCH4", "1990$/GgN2O", "1990$/MgC2F6",
						"1990$/MgCF4", "1990$/MgHFC125", "1990$/MgHFC13a", "1990$/MgHFC245fa", "1990$/MgSF6",
						"1990$/GgCH4", "1990$/GgCH4", "1990$/GgN2O", "1990$/GgN2O" };
				String[] output_unit = new String[] { "MtC", "TgCH4", "TgN2O", "GgC2F6", "GgCF4", "GgHFC125",
						"GgHFC134a", "GgHFC245fa", "GgSF6", "TgCH4", "TgCH4", "TgN2O", "TgN2O" };

				for (int j = 0; j < listOfSelectedLeaves.length; j++) {
					String state = listOfSelectedLeaves[j];

					for (int i = 0; i < GHGs.length; i++) {
						if ((pol.equals("GHG")) || ((pol.equals("CO2")) && (GHGs[i].equals("CO2")))) {
							file_content2 += state + "," + GHGs[i] + "," + market_name + "," + policy_name + ","
									+ price_adjust[i] + "," + demand_adjust[i] + "," + price_unit[i] + ","
									+ output_unit[i] + vars.getEol();
						}
					}
				}

				if (listOfSelectedLeaves.length > 1) {
					file_content2 += vars.getEol();
					file_content2 += "INPUT_TABLE" + vars.getEol();
					file_content2 += "Variable ID" + vars.getEol();
					file_content2 += "GLIMPSELinkedGHGEmissionMarketP2" + vars.getEol();
					file_content2 += vars.getEol();
					file_content2 += "region,pollutant,GHG-market,GHG-Policy" + vars.getEol();
					for (int s = 1; s < listOfSelectedLeaves.length; s++) {
						for (int i = 0; i < GHGs.length; i++) {
							if ((pol.equals("GHG")) || ((pol.equals("CO2")) && (GHGs[i].equals("CO2")))) {
								String state = listOfSelectedLeaves[s];
								file_content2 += state + "," + GHGs[i] + "," + market_name + "," + policy_name
										+ vars.getEol();
							}
						}
						double progress = s / (listOfSelectedLeaves.length - 1.);
						progress_bar.setProgress(progress);
					}
				}
			
			}
			if (file_content2.length() > 0)
				file_content += file_content2;
			// files.saveFile(file_content, file);

		}
	}

	private void saveScenarioComponentCO2Cap(String[] listOfSelectedRegions, String pol, String sector,
			String market_name, String policy_name) {

		// new CO2 cap implementation based on Yang Ou's suggestions to improve
		// robustness for complex scenarios
		// work for economy-wide CO2, but will need to be modified before it can be
		// applied to a specific

		// part 1 - sets up cap or tax values
		// -------------------------------------------
		file_content += "INPUT_TABLE" + vars.getEol();
		file_content += "Variable ID" + vars.getEol();
		file_content += "GLIMPSEEmissionCap-PPS-P1" + vars.getEol() + vars.getEol();
		file_content += "region,policy,policy-type,min-price,market,year,cap" + vars.getEol();

		if (listOfSelectedRegions.length > 0) {
			// String state = listOfSelectedRegions[0];
			for (int s = 0; s < listOfSelectedRegions.length; s++) {
				// this part was not working when I was not replicating for all matching states
				String state = listOfSelectedRegions[s];
				ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();
				for (int i = 0; i < data.size(); i++) {
					String data_str = data.get(i).replaceAll(" ", "");
					file_content += state + "," + policy_name + ",tax,1," + market_name + "," + data_str
							+ vars.getEol();
				}
			}
		}

		// part 2 - set up the regions in the market
		// -------------------------------------------
		String dmdAdj = "1";
		if (pol.contains("(MT CO2)"))
			dmdAdj = "3.667";
		pol = pol.substring(0, pol.indexOf(" ")).trim();

		if (listOfSelectedRegions.length >= 1) {
			file_content += vars.getEol();
			file_content += "INPUT_TABLE" + vars.getEol();
			file_content += "Variable ID" + vars.getEol();
			file_content += "GLIMPSEEmissionCap-PPS-P2" + vars.getEol();
			file_content += vars.getEol();
			file_content += "region,linked-ghg-policy,price-adjust0,demand-adjust0,market,linked-policy,price-unit,output-unit,price-adjust1,demandAdjust1"
					+ vars.getEol();
			for (int s = 0; s < listOfSelectedRegions.length; s++) {
				String region = listOfSelectedRegions[s];
				file_content += region + "," + pol + ",0,0," + market_name + "," + policy_name + ",1990$/Tg,Tg,1,"
						+ dmdAdj + vars.getEol();
				double progress = s / (listOfSelectedRegions.length - 1.);
				progress_bar.setProgress(progress);
			}

		}

	}

	private void saveScenarioComponentWorking(TreeView<String> tree) {
		if (qaInputs()) {

			int start_year = 2010;

			String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);

			// Dan: messy approach to make sure inclusion of USA is intentional
			listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
			String states = utils.returnAppendedString(listOfSelectedLeaves);

			String ID = this.getUniqueMarketName(textFieldMarketName.getText());
			String policy_name = this.textFieldPolicyName.getText() + ID;
			String market_name = this.textFieldMarketName.getText() + ID;

			String sector = comboBoxSector.getValue();

			filename_suggestion = policy_name;
			String type = this.comboBoxMeasure.getValue();
			if (type.indexOf("Cap") > -1) {
				type = "Cap";
			} else {
				type = "Tax";
			}

			String temp = comboBoxPollutant.getSelectionModel().getSelectedItem().trim() + " ";
			String pol = temp.substring(0, temp.indexOf(" ")).trim();

			filename_suggestion = policy_name + ".csv";

			// sets up the content of the CSV file to store the scenario component data
			file_content = getMetaDataContent(tree, market_name, policy_name);

			String file_content2 = "";

			if (!pol.startsWith("GHG")) {

				String pol_orig = pol;

				// part 0 - sets up pollutant species to tax or cap
				if (!sector.equals("All")) {
					pol = market_name;
				}
				// cycle through selected regions and tech list ... link pol to each tech

				// part 1 - sets up cap or tax values

				file_content += "INPUT_TABLE" + vars.getEol();
				file_content += "Variable ID" + vars.getEol();
				if (type.equals("Cap")) {
					file_content += "GLIMPSEEmissionCap" + vars.getEol() + vars.getEol();
					file_content += "region,pollutant,market,year,cap" + vars.getEol();
					;
				} else if (type.equals("Tax")) {
					file_content += "GLIMPSEEmissionTax" + vars.getEol() + vars.getEol();
					file_content += "region,pollutant,market,year,tax" + vars.getEol();
					;
				}

				if (listOfSelectedLeaves.length > 0) {
					String state = listOfSelectedLeaves[0];
					ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();
					for (int i = 0; i < data.size(); i++) {
						String data_str = data.get(i).replaceAll(" ", "");
						file_content += state + "," + pol + "," + market_name + "," + data_str + vars.getEol();
					}
				}

				// part 2 - set up the regions in the market
				if (listOfSelectedLeaves.length > 1) {
					file_content += vars.getEol();
					file_content += "INPUT_TABLE" + vars.getEol();
					file_content += "Variable ID" + vars.getEol();
					file_content += "GLIMPSEEmissionMarket" + vars.getEol();
					file_content += vars.getEol();
					file_content += "region,pollutant,market" + vars.getEol();
					for (int s = 1; s < listOfSelectedLeaves.length; s++) {
						file_content += listOfSelectedLeaves[s] + "," + pol + "," + market_name + vars.getEol();
						double progress = s / (listOfSelectedLeaves.length - 1.);
						progress_bar.setProgress(progress);
					}

				}

				// part 3 - setting up sector-specific pollutant definitions
				if (!sector.equals("All")) {

					ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();

					String file_content_nest = vars.getEol();
					file_content_nest += "INPUT_TABLE" + vars.getEol();
					file_content_nest += "Variable ID" + vars.getEol();
					file_content_nest += "GLIMPSEAddCO2Subspecies-Nest" + vars.getEol();
					file_content_nest += vars.getEol();
					file_content_nest += "region,supplysector,nesting-subsector,subsector,technology,year,pollutant"
							+ vars.getEol();
					int nest_count = 0;

					String file_content_nonest = vars.getEol();
					file_content_nonest += "INPUT_TABLE" + vars.getEol();
					file_content_nonest += "Variable ID" + vars.getEol();
					file_content_nonest += "GLIMPSEAddCO2Subspecies" + vars.getEol();
					file_content_nonest += vars.getEol();
					file_content_nonest += "region,supplysector,subsector,technology,year,pollutant" + vars.getEol();
					int nonest_count = 0;

					int max_year = 0;
					for (int m = 0; m < data.size(); m++) {
						int year = Integer.parseInt(data.get(m).split(",")[0].trim());
						if (year > max_year)
							max_year = year;
					}

					if (listOfSelectedLeaves.length > 0) {
						for (int s = 0; s < listOfSelectedLeaves.length; s++) {

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

									if (((sector_lwc.equals(cat_r))
											|| ((sector_lwc.equals("industry-all")) || (sector_lwc.equals("ind-all")))
													&& (cat_r_lwc.startsWith("ind")))
											|| ((sector_lwc.equals("trn-all")) && (cat_r_lwc.startsWith("trn")))) {
										String line = listOfSelectedLeaves[s] + "," + sector_r + ","
												+ subsector_r.replace("=>", ",") + "," + tech_r + "," + y + "," + pol
												+ vars.getEol();
										;
										if (subsector_r.indexOf("=>") > -1) {
											file_content_nest += line;
											nest_count++;
										} else {
											file_content_nonest += line;
											nonest_count++;
										}
									}

								}
							}

							double progress = s / (listOfSelectedLeaves.length - 1.);
							progress_bar.setProgress(progress);
						}
					}
					if (nest_count > 0)
						file_content += file_content_nest;
					if (nonest_count > 0)
						file_content += file_content_nonest;
				}
				// cycle through selected regions and tech list ... link pol to each tech

			} else {
				// GHG target (must be system-wide)
				// part 1 - sets up cap or tax values
				file_content += "INPUT_TABLE" + vars.getEol();
				file_content += "Variable ID" + vars.getEol();
				if (type.equals("Cap")) {
					file_content += "GLIMPSEGHGEmissionCap" + vars.getEol();
					file_content += vars.getEol();
					file_content += "region,GHG-Policy,GHG-Market,year,cap" + vars.getEol();

				} else if (type.equals("Tax")) {
					file_content += "GLIMPSEGHGEmissionTax" + vars.getEol();
					file_content += vars.getEol();
					file_content += "region,GHG-Policy,GHG-Market,year,tax" + vars.getEol();

				}

				if (listOfSelectedLeaves.length > 0) {
					String state = listOfSelectedLeaves[0];
					ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();
					for (int i = 0; i < data.size(); i++) {
						String data_str = data.get(i).replace(" ", "");
						file_content += state + "," + policy_name + "," + market_name + "," + data_str + vars.getEol();
					}
				}

				// part 2 // part 2 - set up the regions in the market
				if (listOfSelectedLeaves.length > 1) {
					file_content += vars.getEol();
					file_content += "INPUT_TABLE" + vars.getEol();
					file_content += "Variable ID" + vars.getEol();
					file_content += "GLIMPSEEmissionMarket" + vars.getEol();
					file_content += vars.getEol();
					file_content += "region,pollutant,market" + vars.getEol();
					for (int s = 1; s < listOfSelectedLeaves.length; s++) {
						file_content += listOfSelectedLeaves[s] + "," + policy_name + "," + market_name + vars.getEol();
						double progress = s / (listOfSelectedLeaves.length - 1.);
						progress_bar.setProgress(progress);
					}
					file_content += vars.getEol();
				}

				// part 3 - set up the linked GHG part regions in the market

				file_content2 += vars.getEol();
				file_content2 += "INPUT_TABLE" + vars.getEol();
				file_content2 += "Variable ID" + vars.getEol();
				file_content2 += "GLIMPSELinkedGHGEmissionMarketP1" + vars.getEol();
				file_content2 += vars.getEol();
				file_content2 += "region,pollutant,GHG-market,GHG-Policy,price-adjust,demand-adjust,price-unit,output-unit"
						+ vars.getEol();

				String[] GHGs = new String[] { "CO2", "CH4", "N2O", "C2F6", "CF4", "HFC125", "HFC134a", "HRC245fa",
						"SF6", "CH4_AWB", "CH4_AGR", "N2O_AWB", "N2O_AGR" };
				String[] price_adjust = new String[] { "1", "5.728", "84.55", "0", "0", "0", "0", "0", "0", "5.727",
						"5.727", "84.55", "84.55" };
				String[] demand_adjust = new String[] { "3.667", "21", "310", "9.2", "6.5", "2.8", "1.3", "1.03",
						"23.9", "21", "21", "310", "310" };
				String[] price_unit = new String[] { "1990$/tC", "1990$/GgCH4", "1990$/GgN2O", "1990$/MgC2F6",
						"1990$/MgCF4", "1990$/MgHFC125", "1990$/MgHFC13a", "1990$/MgHFC245fa", "1990$/MgSF6",
						"1990$/GgCH4", "1990$/GgCH4", "1990$/GgN2O", "1990$/GgN2O" };
				String[] output_unit = new String[] { "MtC", "TgCH4", "TgN2O", "GgC2F6", "GgCF4", "GgHFC125",
						"GgHFC134a", "GgHFC245fa", "GgSF6", "TgCH4", "TgCH4", "TgN2O", "TgN2O" };

				String state = listOfSelectedLeaves[0];

				for (int i = 0; i < GHGs.length; i++) {
					file_content2 += state + "," + GHGs[i] + "," + market_name + "," + policy_name + ","
							+ price_adjust[i] + "," + demand_adjust[i] + "," + price_unit[i] + "," + output_unit[i]
							+ vars.getEol();
				}

				if (listOfSelectedLeaves.length > 1) {
					file_content2 += vars.getEol();
					file_content2 += "INPUT_TABLE" + vars.getEol();
					file_content2 += "Variable ID" + vars.getEol();
					file_content2 += "GLIMPSELinkedGHGEmissionMarketP2" + vars.getEol();
					file_content2 += vars.getEol();
					file_content2 += "region,pollutant,GHG-market,GHG-Policy" + vars.getEol();
					for (int s = 1; s < listOfSelectedLeaves.length; s++) {
						for (int i = 0; i < GHGs.length; i++) {
							state = listOfSelectedLeaves[s];
							file_content2 += state + "," + GHGs[i] + "," + market_name + "," + policy_name
									+ vars.getEol();
						}
						double progress = s / (listOfSelectedLeaves.length - 1.);
						progress_bar.setProgress(progress);
					}
				}
			}

			if (file_content2.length() > 0)
				file_content += file_content2;
			// files.saveFile(file_content, file);

		}
	}

	public String getMetaDataContent(TreeView<String> tree, String market, String policy) {
		String rtn_str = "";

		rtn_str += "########## Scenario Component Metadata ##########" + vars.getEol();
		rtn_str += "#Scenario component type: Pollutant Tax/Cap" + vars.getEol();
		rtn_str += "#Measure: " + comboBoxMeasure.getValue() + vars.getEol();
		rtn_str += "#Pollutant: " + comboBoxPollutant.getValue() + vars.getEol();
		rtn_str += "#Sector: " + comboBoxSector.getValue() + vars.getEol();
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

				if (param.equals("measure")) {
					comboBoxMeasure.setValue(value);
					comboBoxMeasure.fireEvent(new ActionEvent());
				}
				if (param.equals("pollutant")) {
					comboBoxPollutant.setValue(value);
					comboBoxPollutant.fireEvent(new ActionEvent());
				}
				if (param.equals("sector")) {
					comboBoxSector.setValue(value);
					comboBoxSector.fireEvent(new ActionEvent());
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
			}
			if (comboBoxMeasure.getSelectionModel().getSelectedItem().equals("Select One")) {
				message += "Action comboBox must have a selection" + vars.getEol();
				error_count++;
			}
			if (comboBoxSector.getSelectionModel().getSelectedItem().equals("Select One")) {
				message += "Sector comboBox must have a selection" + vars.getEol();
				error_count++;
			}
			if (comboBoxPollutant.getSelectionModel().getSelectedItem().equals("Select One")) {
				message += "Parameter comboBox must have a selection" + vars.getEol();
				error_count++;
			}
			if (textFieldPolicyName.getText().equals("")) {
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

}