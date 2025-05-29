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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.CheckBoxTreeItem.TreeModificationEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TabFuelPriceAdj extends PolicyTab implements Runnable {
	private GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	private GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	private GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	private GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

	public static String descriptionText = "";
	public static String runQueueStr = "Queue is empty.";
	
	double label_wid=125;

	// Initializing overall grid
	GridPane gridPanePresetModification = new GridPane();
	// Label labelComponent = utils.createLabel("Specification: ");

	// Initializing components of left column
	GridPane gridPaneLeft = new GridPane();
	ScrollPane scrollPaneLeft = new ScrollPane();

	Label labelCheckComboBoxFuel = utils.createLabel("Fuel: ",label_wid);
	Label labelUnits = utils.createLabel("Units: ",label_wid);
	Label labelUnits2 = utils.createLabel("1975$s per GJ",225.);

	CheckComboBox<String> checkComboBoxFuel = utils.createCheckComboBox();

	Label labelPolicyName = utils.createLabel("Policy: ",label_wid);
	TextField textFieldPolicyName = new TextField("");

	Label labelMarketName = utils.createLabel("Market: ",label_wid);
	TextField textFieldMarketName = new TextField("");

	Label labelUseAutoNames = utils.createLabel("Names: ",label_wid);
	CheckBox checkBoxUseAutoNames = utils.createCheckBox("Auto?");

	Label labelModificationType = utils.createLabel("Type: ",label_wid);
	ComboBox<String> comboBoxModificationType = utils.createComboBoxString();

	Label labelStartYear = utils.createLabel("Start Year: ",label_wid);
	TextField textFieldStartYear = new TextField("2020");
	Label labelEndYear = utils.createLabel("End Year: ",label_wid);
	TextField textFieldEndYear = new TextField("2050");
	Label labelInitialAmount = utils.createLabel("Initial Val:   ",label_wid);
	TextField textFieldInitialAmount = utils.createTextField();
	Label labelGrowth = utils.createLabel("Growth (%): ",label_wid);
	TextField textFieldGrowth = utils.createTextField();
	Label labelPeriodLength = utils.createLabel("Period Length: ",label_wid);
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
	
	public TabFuelPriceAdj(String title, Stage stageX) {
		// sets tab title
		TreeItem<String> ti=paneForCountryStateTree.getTree().getRoot();
		ti.setExpanded(true);
		
		this.setText(title);
		this.setStyle(styles.font_style);

		// sets up initial state of check box and policy and market textfields
		checkBoxUseAutoNames.setSelected(true);
		textFieldPolicyName.setDisable(true);
		textFieldMarketName.setDisable(true);
		
		comboBoxConvertFrom.getItems().addAll("None","2023$s","2020$s","2015$s","2010$s","2005$s","2000$s");
		comboBoxConvertFrom.getSelectionModel().selectFirst();

		// left column
		gridPaneLeft.add(utils.createLabel("Specification:"), 0, 0, 2, 1);
		gridPaneLeft.addColumn(0, labelCheckComboBoxFuel, new Label(), labelUnits, new Label(),
				new Separator(), labelUseAutoNames, labelPolicyName, labelMarketName, new Label(), new Separator(),
				utils.createLabel("Populate:"), labelModificationType, labelStartYear, labelEndYear, labelInitialAmount,
				labelGrowth,labelConvertFrom);

		gridPaneLeft.addColumn(1, checkComboBoxFuel, new Label(), labelUnits2, new Label(), new Separator(),
				checkBoxUseAutoNames, textFieldPolicyName, textFieldMarketName, new Label(), new Separator(),
				new Label(), comboBoxModificationType, textFieldStartYear, textFieldEndYear, textFieldInitialAmount,
				textFieldGrowth,comboBoxConvertFrom);

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
		double max_wid = 180;
		checkComboBoxFuel.setMaxWidth(max_wid);
		textFieldStartYear.setMaxWidth(max_wid);
		textFieldEndYear.setMaxWidth(max_wid);
		textFieldInitialAmount.setMaxWidth(max_wid);
		textFieldGrowth.setMaxWidth(max_wid);
		textFieldPeriodLength.setMaxWidth(max_wid);
		textFieldPolicyName.setMaxWidth(max_wid);
		textFieldMarketName.setMaxWidth(max_wid);
		comboBoxConvertFrom.setMaxWidth(max_wid);

		double min_wid = 100;
		checkComboBoxFuel.setMinWidth(min_wid);
		textFieldStartYear.setMinWidth(min_wid);
		textFieldEndYear.setMinWidth(min_wid);
		textFieldInitialAmount.setMinWidth(min_wid);
		textFieldGrowth.setMinWidth(min_wid);
		textFieldPeriodLength.setMinWidth(min_wid);
		textFieldPolicyName.setMinWidth(min_wid);
		textFieldMarketName.setMinWidth(min_wid);
		comboBoxConvertFrom.setMinWidth(min_wid);
		
		double pref_wid = 180;
		checkComboBoxFuel.setPrefWidth(pref_wid);
		textFieldStartYear.setPrefWidth(pref_wid);
		textFieldEndYear.setPrefWidth(pref_wid);
		textFieldInitialAmount.setPrefWidth(pref_wid);
		textFieldGrowth.setPrefWidth(pref_wid);
		textFieldPeriodLength.setPrefWidth(pref_wid);
		textFieldPolicyName.setPrefWidth(pref_wid);
		textFieldMarketName.setPrefWidth(pref_wid);
		comboBoxConvertFrom.setPrefWidth(min_wid);
		
		String[][] tech_list=vars.getTechInfo(); 
		ArrayList<String> fuel_list=extractFuelsFromTechList(tech_list);
		
		checkComboBoxFuel.getItems().addAll(fuel_list);
		
		//checkComboBoxFuel.getItems().addAll("Coal","Natural Gas","Crude Oil","Unconv Oil","Corn for Ethanol","Sugar for Ethanol","Oil for Biodiesel","Other Bioenergy");
		//checkComboBoxFuel.getCheckModel().check(0);
		
		comboBoxModificationType.getItems().addAll("Initial w/% Growth/yr", "Initial w/% Growth/pd",
				"Initial w/Delta/yr", "Initial w/Delta/pd", "Initial and Final");
		

		comboBoxModificationType.getSelectionModel().selectFirst();

		labelCheckComboBoxFuel.setOnMouseClicked(e -> {
			if (!checkComboBoxFuel.isDisabled()) {
			boolean isFirstItemChecked=checkComboBoxFuel.getCheckModel().isChecked(0);
			if (e.getClickCount()==2) {
				if (isFirstItemChecked) {
					checkComboBoxFuel.getCheckModel().clearChecks();
				} else {
					checkComboBoxFuel.getCheckModel().checkAll();
				}
			}
			}
		});
		
		checkComboBoxFuel.setOnMouseExited(e -> {
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

	
	private ArrayList<String> extractFuelsFromTechList(String[][] tech_list) {
		ArrayList<String> fuels=new ArrayList<String>();
		
		for (int row=0;row<tech_list.length;row++) {
			String str_col0=tech_list[row][0];
			if (str_col0.startsWith("regional ")) {
				String str=tech_list[row][0]+","+tech_list[row][1]+","+tech_list[row][2];//+","+tech_list[row][3];
				fuels.add(str);
			}
		}
		
		fuels=utils.getUniqueItemsFromStringArrayList(fuels);
		
		return fuels; 
	}
	
	private void setPolicyAndMarketNames() {
		if (this.checkBoxUseAutoNames.isSelected()) {

			String policy_type = "FuelPriceAdj";
			String fuel = "----";
			String state = "--";

			try {

				int no_selected_fuels=checkComboBoxFuel.getCheckModel().getCheckedItems().size();
				if (no_selected_fuels==1) {
					ObservableList<String> selected_items=checkComboBoxFuel.getCheckModel().getCheckedItems();
					fuel = selected_items.get(0);
					if (fuel.contains("gas")) {
						fuel="gas";
					} else if (fuel.contains("oil")) {
						fuel="oil";
					} else if (fuel.contains("unconv")) {
							fuel="uncvoil";
					} else if (fuel.contains("coal")) {
						fuel="coal";
					} else if (fuel.contains("bio")) {
						fuel="bio";
					} else if (fuel.contains("corn")) {
						fuel="corn";
					} else {
						fuel="oth";
					}
				} else if (no_selected_fuels>1) {
					fuel="mult";
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

				String name = policy_type + "-" + fuel + "-" + state;
				name=name.replaceAll(" ","_").replaceAll("--","-");
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
		double factor=1.0;
		String convertYear=this.comboBoxConvertFrom.getValue();
		if (convertYear!="None") {
			factor=utils.getConversionFactor(convertYear,"1975$s");
		}
		double[][] returnMatrix = utils.calculateValues(calc_type, start_year, end_year, initial_value, growth,
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


		
		if (!qaInputs()){
			Thread.currentThread().destroy();
		} else {

			String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);

			listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
			String states = utils.returnAppendedString(listOfSelectedLeaves);

			filename_suggestion = "";

			// constructs a filename suggestion for the scenario component
			ObservableList<String> fuel_list = checkComboBoxFuel.getCheckModel().getCheckedItems();

			
			//String ID=this.getUniqueMarketName(textFieldMarketName.getText());
			String ID=utils.getUniqueString();
			filename_suggestion=this.textFieldPolicyName.getText().replaceAll("/", "-").replaceAll(" ", "_")+".csv";
			String policy_name = this.textFieldPolicyName.getText()+ID;
			String market_name = this.textFieldMarketName.getText()+ID;
			
			file_content = getMetaDataContent(tree,market_name,policy_name);

			for (int f = 0; f < fuel_list.size(); f++) {
				
				String fuel_line=fuel_list.get(f);
				
				ArrayList<String> temp=utils.createArrayListFromString(fuel_line,",");
				
				String sector = temp.get(0);
				String subsector = temp.get(1);
				String tech= temp.get(2);
				//String fuel= temp.get(3);

				if (f!=0) file_content+=vars.getEol();

				//filename_suggestion = policy_name.replaceAll("/", "-").replaceAll(" ", "_").replaceAll("+","-")+".csv";

				String region = states.replace(",", "");
				if (region.length() > 6) {
					region = "Reg";
				}

				// sets up the content of the CSV file to store the scenario component data


				String header = "GLIMPSEFuelPriceAdj";

				// part 1
				file_content += "INPUT_TABLE" + vars.getEol();
				file_content += "Variable ID" + vars.getEol();
				file_content += header + vars.getEol() + vars.getEol();
				file_content += "region,supplysector,subsector,technology,param,year,adjustment" + vars.getEol();

				for (int s = 0; s < listOfSelectedLeaves.length; s++) {
					String state = listOfSelectedLeaves[s];

					ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();
					for (int i = 0; i < data.size(); i++) {
						String data_str = data.get(i).replace(" ", "");
						String year = utils.splitString(data_str, ",")[0];
						String val = utils.splitString(data_str, ",")[1];
						file_content += state + "," + sector + "," + subsector + "," + tech + ","
								+ year + ",regional price adjustment," +val + vars.getEol();
					}

				}
			}

		}
	
	}

	private void saveScenarioComponentOld(TreeView<String> tree) {

		if (!qaInputs()){
			Thread.currentThread().destroy();
		} else {

			String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);

			listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
			String states = utils.returnAppendedString(listOfSelectedLeaves);

			filename_suggestion = "";

			// constructs a filename suggestion for the scenario component
			ObservableList<String> fuel_list = checkComboBoxFuel.getCheckModel().getCheckedItems();

			String sector = "";
			String subsector = "";
			String fuel= "";
			String tech= "";
			
			//String ID=this.getUniqueMarketName(textFieldMarketName.getText());
			String ID=utils.getUniqueString();
			filename_suggestion=this.textFieldPolicyName.getText().replaceAll("/", "-").replaceAll(" ", "_")+".csv";
			String policy_name = this.textFieldPolicyName.getText()+ID;
			String market_name = this.textFieldMarketName.getText()+ID;
			
			file_content = getMetaDataContent(tree,market_name,policy_name);

			for (int f = 0; f < fuel_list.size(); f++) {
				
				fuel=fuel_list.get(f);
				if (f!=0) file_content+=vars.getEol();

				if (fuel.equals("Coal")) {
					sector="regional coal";
					subsector="regional coal";
					tech="regional coal";
				} else if(fuel.equals("Natural Gas")) {
					sector="regional natural gas";
					subsector="regional natural gas";
					tech="regional natural gas";
				} else if (fuel.equals("Crude Oil")) {
					sector="regional oil";
					subsector="crude oil";
					tech="crude oil";
				} else if (fuel.equals("Unconv Oil")) {
					sector="regional oil";
					subsector="unconventional oil";
					tech="unconventional oil";				 
				} else if (fuel.equals("Corn for Ethanol")) {
					sector="regional corn for ethanol";
					subsector="regional corn for ethanol";
					tech="regional corn for ethanol";
				} else if (fuel.equals("Sugar for Ethanol")) {
					sector="regional sugar for ethanol";
					subsector="regional sugar for ethanol";
					tech="regional sugar for ethanol";
				}else if (fuel.equals("Oil for Biodiesel")) {
					sector="regional biomassOil";
					subsector="regional biomassOil";
					tech="OilCrop";
				} else if (fuel.equals("Other Bioenergy")) {
					sector="regional biomass";
					subsector="regional biomass";
					tech="regional biomass";
				} else {
					System.out.println("Modifications to "+fuel+" price not currently supported.");
					return;
				}
				


				//filename_suggestion = policy_name.replaceAll("/", "-").replaceAll(" ", "_").replaceAll("+","-")+".csv";

				String region = states.replace(",", "");
				if (region.length() > 6) {
					region = "Reg";
				}

				// sets up the content of the CSV file to store the scenario component data


				String header = "GLIMPSEFuelPriceAdj";

				// part 1
				file_content += "INPUT_TABLE" + vars.getEol();
				file_content += "Variable ID" + vars.getEol();
				file_content += header + vars.getEol() + vars.getEol();
				file_content += "region,supplysector,subsector,technology,param,year,adjustment" + vars.getEol();

				for (int s = 0; s < listOfSelectedLeaves.length; s++) {
					String state = listOfSelectedLeaves[s];

					ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();
					for (int i = 0; i < data.size(); i++) {
						String data_str = data.get(i).replace(" ", "");
						String year = utils.splitString(data_str, ",")[0];
						String val = utils.splitString(data_str, ",")[1];
						file_content += state + "," + sector + "," + subsector + "," + tech + ","
								+ year + ",regional price adjustment," +val + vars.getEol();
					}

				}
			}

		}
	
	}

	
	public String getMetaDataContent(TreeView<String> tree,String market,String policy) {
		String rtn_str="";
		
		rtn_str+="########## Scenario Component Metadata ##########"+vars.getEol();
		rtn_str+="#Scenario component type: Fuel Price Adj"+vars.getEol();
		
		ObservableList fuel_list=checkComboBoxFuel.getCheckModel().getCheckedItems();
		String fuel=utils.getStringFromList(fuel_list,";");		
		rtn_str+="#Fuel: "+fuel+vars.getEol();		
		rtn_str+="#Units: "+labelUnits2.getText()+vars.getEol();
		if (policy==null) market=textFieldPolicyName.getText();
		rtn_str+="#Policy name: "+policy+vars.getEol();
		if (market==null) market=textFieldMarketName.getText();
		rtn_str+="#Market name: "+market+vars.getEol();
		
		String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);
		listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
		String states = utils.returnAppendedString(listOfSelectedLeaves);
		rtn_str+="#Regions: "+states+vars.getEol();
		
		ArrayList<String> table_content = this.paneForComponentDetails.getDataYrValsArrayList();
		for (int i=0;i<table_content.size();i++) {
			rtn_str+="#Table data:"+table_content.get(i)+vars.getEol();
		}
		rtn_str+="#################################################"+vars.getEol();
		
		return rtn_str;
	}

	@Override
	public void loadContent(ArrayList<String> content) {
		for (int i=0;i<content.size();i++) {
			String line=content.get(i);
			int pos = line.indexOf(":");
			if (line.startsWith("#")&&(pos>-1)){
				String param=line.substring(1,pos).trim().toLowerCase();
				String value=line.substring(pos+1).trim();
				
				if (param.equals("fuel")) { 
					String[] set=utils.splitString(value,";");
					for (int j=0;j<set.length;j++) {
						String item=set[j].trim();
						checkComboBoxFuel.getCheckModel().check(item);
						checkComboBoxFuel.fireEvent(new ActionEvent());
					}
				}
				if (param.equals("units")) { 
					labelUnits2.setText(value);
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
					String[] regions=utils.splitString(value,",");
					this.paneForCountryStateTree.selectNodes(regions);
				}
				if (param.equals("table data")) { 
					String[] s=utils.splitString(value, ",");
					this.paneForComponentDetails.data.add(new DataPoint(s[0],s[1]));
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
			
			if ((checkComboBoxFuel.getCheckModel().getItemCount() == 1)
					&& (checkComboBoxFuel.getCheckModel().isChecked("Select One or More"))) {
				message += "Fuel checkComboBox must have at least one selection" + vars.getEol();
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
			if (vars.isGcamUSA()) {
				String[] selected_leaves=utils.getAllSelectedLeaves(tree);
				boolean applied_to_a_state=false;
				boolean is_usa_selected=false;
				
				for (int s=0;s<selected_leaves.length;s++) {
					String region=selected_leaves[s];
					if (utils.isState(region)) { 
						applied_to_a_state=true;
					} else if (region.equals("USA")) {
						is_usa_selected=true;
					}
				}
				
//				ObservableList<String> fuel_list = checkComboBoxFuel.getCheckModel().getCheckedItems();	
//				String fuel="";
//				for (int f = 0; f < fuel_list.size(); f++) {					
//					fuel=fuel_list.get(f);	
//					if ((fuel.contains("coal"))||(fuel.contains("gas"))||(fuel.contains("oil"))) {
//						if (applied_to_a_state) { 
//							message += "Note: Price adjustments for "+fuel+" cannot be applied at the state level since this is a national fuel market." + vars.getEol();
//						    //error_count++;	
//						}
//					} 
////					else {
////						if ((!applied_to_a_state)&&(is_usa_selected)) { 
////							message += "Price adjustments for "+fuel+" cannot be applied at the national level for the US." + vars.getEol();
////						    //error_count++;	
////						}
////					}
//				}
			}

		} catch (Exception e1) {
			System.out.println("error "+e1);
			error_count++;
			message += "Error in QA of entries" + vars.getEol();
		}
		if (error_count > 0) {
			try {
				if (error_count == 1) {
					utils.warningMessage(message);
				} else if (error_count > 1) {
					utils.displayString(message, "Parsing Errors");
				}
			} catch(Exception e1) {
				System.out.println(message);
				throw(e1);
				
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