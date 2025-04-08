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

public class TabCafeStd extends PolicyTab implements Runnable {
	private GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	private GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	private GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	private GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

	public static String descriptionText = "";
	public static String runQueueStr = "Queue is empty.";

	// Initializing overall grid
	GridPane gridPanePresetModification = new GridPane();
	// Label labelComponent = utils.createLabel("Specification: ");

	// Initializing components of left column
	GridPane gridPaneLeft = new GridPane();
	ScrollPane scrollPaneLeft = new ScrollPane();

	double label_wid = 125;

	Label labelComboBoxSubsector = utils.createLabel("Onroad subsector? ", label_wid);
	ComboBox<String> comboBoxSubsector = utils.createComboBoxString();

	Label labelCheckComboBoxTech = utils.createLabel("Tech(s): ", label_wid);
	CheckComboBox<String> checkComboBoxTech = utils.createCheckComboBox();

	Label labelWhichUnits = utils.createLabel("Units? ", label_wid);
	ComboBox<String> comboBoxWhichUnits = utils.createComboBoxString();

	Label labelPolicyName = utils.createLabel("Policy: ", label_wid);
	TextField textFieldPolicyName = new TextField("");

	Label labelMarketName = utils.createLabel("Market: ", label_wid);
	TextField textFieldMarketName = new TextField("");

	Label labelUseAutoNames = utils.createLabel("Names: ", label_wid);
	CheckBox checkBoxUseAutoNames = utils.createCheckBox("Auto?");

	Label labelModificationType = utils.createLabel("Type: ", label_wid);
	ComboBox<String> comboBoxModificationType = utils.createComboBoxString();

	Label labelStartYear = utils.createLabel("Start Year: ", label_wid);
	TextField textFieldStartYear = new TextField("2020");
	Label labelEndYear = utils.createLabel("End Year: ", label_wid);
	TextField textFieldEndYear = new TextField("2050");
	Label labelInitialAmount = utils.createLabel("Initial Val:   ", label_wid);
	TextField textFieldInitialAmount = utils.createTextField();
	Label labelGrowth = utils.createLabel("Final Val: ", label_wid);
	TextField textFieldGrowth = utils.createTextField();
	Label labelPeriodLength = utils.createLabel("Period Length: ", label_wid);
	TextField textFieldPeriodLength = new TextField("5");

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
	
	String selected_sector="";
	String selected_subsector=""; 

	public TabCafeStd(String title, Stage stageX) {
		// sets tab title
		this.setText(title);
		this.setStyle(styles.font_style);

		// sets up initial state of check box and policy and market textfields
		checkBoxUseAutoNames.setSelected(true);
		textFieldPolicyName.setDisable(true);
		textFieldMarketName.setDisable(true);

		// left column
		gridPaneLeft.add(utils.createLabel("Specification:"), 0, 0, 2, 1);
		gridPaneLeft.addColumn(0, labelComboBoxSubsector, labelCheckComboBoxTech,  
				labelWhichUnits, new Label(),  new Separator(), labelUseAutoNames, labelPolicyName, labelMarketName,
				new Label(), new Separator(), utils.createLabel("Populate:"), labelModificationType, labelStartYear,
				labelEndYear, labelInitialAmount, labelGrowth);

		gridPaneLeft.addColumn(1, comboBoxSubsector, checkComboBoxTech,  
				comboBoxWhichUnits, new Label(), new Separator(), checkBoxUseAutoNames, textFieldPolicyName,
				textFieldMarketName, new Label(), new Separator(), new Label(), comboBoxModificationType,
				textFieldStartYear, textFieldEndYear, textFieldInitialAmount, textFieldGrowth);

		gridPaneLeft.setVgap(3.);
		gridPaneLeft.setStyle(styles.style2);
		
		scrollPaneLeft.setContent(gridPaneLeft);

		comboBoxSubsector.getItems().addAll("Select One","Car","Large Car and Truck","Light Truck","Medium Truck","Heavy Truck");
		comboBoxSubsector.getSelectionModel().select("Select One");
		
		checkComboBoxTech.getItems().addAll("BEV","FCEV","Hybrid Liquids","Liquids","NG");
		checkComboBoxTech.getCheckModel().checkAll();
		checkComboBoxTech.setDisable(true);
		
		comboBoxWhichUnits.getItems().addAll("Select One", "MPG", "MJ/vkt");
		//comboBoxWhichUnits.getSelectionModel().select("Select One");
		comboBoxWhichUnits.getSelectionModel().select("MPG");
		comboBoxWhichUnits.setDisable(true);
		
		comboBoxModificationType.getItems().addAll("Initial and Final", "Initial w/% Growth/yr",
				"Initial w/% Growth/pd", "Initial w/Delta/yr", "Initial w/Delta/pd");
		comboBoxModificationType.getSelectionModel().selectFirst();
		
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

		// techs
		checkComboBoxTech.getCheckModel().checkAll();
		//checkComboBoxTech.setDisable(true);

		comboBoxSubsector.getSelectionModel().selectFirst();

		// default sizing
		double max_wid = 175;
		comboBoxSubsector.setMaxWidth(max_wid);
		checkComboBoxTech.setMaxWidth(max_wid);
		comboBoxWhichUnits.setMaxWidth(max_wid);
		textFieldStartYear.setMaxWidth(max_wid);
		textFieldEndYear.setMaxWidth(max_wid);
		textFieldInitialAmount.setMaxWidth(max_wid);
		textFieldGrowth.setMaxWidth(max_wid);
		textFieldPeriodLength.setMaxWidth(max_wid);
		textFieldPolicyName.setMaxWidth(max_wid);
		textFieldMarketName.setMaxWidth(max_wid);
		
		double min_wid = 105;
		comboBoxSubsector.setMinWidth(min_wid);
		checkComboBoxTech.setMinWidth(min_wid);
		comboBoxWhichUnits.setMinWidth(max_wid);
		textFieldStartYear.setMinWidth(min_wid);
		textFieldEndYear.setMinWidth(min_wid);
		textFieldInitialAmount.setMinWidth(min_wid);
		textFieldGrowth.setMinWidth(min_wid);
		textFieldPeriodLength.setMinWidth(min_wid);
		textFieldPolicyName.setMinWidth(min_wid);
		textFieldMarketName.setMinWidth(min_wid);
		
		double pref_wid = 175;
		comboBoxSubsector.setPrefWidth(pref_wid);
		checkComboBoxTech.setPrefWidth(pref_wid);
		comboBoxWhichUnits.setPrefWidth(pref_wid);
		textFieldStartYear.setPrefWidth(pref_wid);
		textFieldEndYear.setPrefWidth(pref_wid);
		textFieldInitialAmount.setPrefWidth(pref_wid);
		textFieldGrowth.setPrefWidth(pref_wid);
		textFieldPeriodLength.setPrefWidth(pref_wid);
		textFieldPolicyName.setPrefWidth(pref_wid);
		textFieldMarketName.setPrefWidth(pref_wid);
		
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

		comboBoxSubsector.setOnAction(e -> {
			if (comboBoxSubsector.getSelectionModel().getSelectedIndex()>0) {
				this.checkComboBoxTech.setDisable(false);
			} else {
				this.checkComboBoxTech.setDisable(true);
			}
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
		
		checkBoxUseAutoNames.setOnAction(e -> {
			if (!checkBoxUseAutoNames.isSelected()) {
				textFieldPolicyName.setDisable(false);
				textFieldMarketName.setDisable(false);
			} else {
				textFieldMarketName.setDisable(true);
				textFieldPolicyName.setDisable(true);
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
		setUnitsLabel();

		VBox tabLayout = new VBox();
		tabLayout.getChildren().addAll(gridPanePresetModification);

		this.setContent(tabLayout);
	}


	private void setPolicyAndMarketNames() {

		if (this.checkBoxUseAutoNames.isSelected()) {

			String policy_type = "CAFE_--";
			String technology = "Tech";
			String sector = "--";
			String state = "--";
			String treatment = "--";

			try {
				String s = comboBoxSubsector.getValue();
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

				String name = policy_type + "_" + sector + "_" + technology + "_" + state + treatment;
				name = name.replaceAll(" ", "_").replaceAll("--", "-").replaceAll("_-_", "-");
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

			//// setting up policy name and suggested file name
			
			
			String ID = utils.getUniqueString();
			String policy_name = this.textFieldPolicyName.getText() + ID;
			String market_name = this.textFieldMarketName.getText() + ID;
			filename_suggestion = this.textFieldPolicyName.getText().replaceAll("/", "-").replaceAll(" ", "_") + ".csv";

			//clearing info to save to file
			file_content = this.getMetaDataContent(tree, market_name, policy_name);
			String content_p1="";
			String content_p2="";

			//// -----------getting selected regions info from GUI
			String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);
			listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
			String states = utils.returnAppendedString(listOfSelectedLeaves);

			//// -----------getting constraint data from GUI
			
			// getting values for constraint
			ArrayList<String> dataArrayList = this.paneForComponentDetails.getDataYrValsArrayList();
			String[] year_list = new String[dataArrayList.size()];
			String[] value_list = new String[dataArrayList.size()];
			double[] valuef_list = new double[dataArrayList.size()];

			// setting up dates for iteration
			
			
			//// ------------ setting up headers
			String header_part1 = "GLIMPSECAFETargets";
			String header_part2 = "GLIMPSEPFStdActivate";
			
			//header 1:
			content_p1+="INPUT_TABLE" + vars.getEol();
			content_p1+="Variable ID" + vars.getEol();
			content_p1+=header_part1 + vars.getEol() + vars.getEol();
			content_p1+="region,sector,subsector,tech,year,input,coefficient,policy,output-ratio,pMultiplier" + vars.getEol();

			//header 2:
			content_p2+="INPUT_TABLE" + vars.getEol();
			content_p2+="Variable ID" + vars.getEol();
			content_p2+=header_part2 + vars.getEol() + vars.getEol();
			content_p2+="region,policy,market,type,year,constrained" + vars.getEol();			
			

			/////----- Constructing data components			
			
			//loop over regions
			for (int r = 0; r < listOfSelectedLeaves.length; r++) {
				String region = listOfSelectedLeaves[r];
			    
				//for each region, sector/subsector, get list of techs
				String subsector=comboBoxSubsector.getValue(); 
				String sector="";
				if ((subsector.equals("Light Truck"))||(subsector.equals("Medium Truck"))||(subsector.equals("Heavy Truck"))) {
					sector="trn_freight_road";
				} else {
					sector="trn_pass_road_LDV_4W";
				}

				
				for (int i = 0; i < dataArrayList.size(); i++) {
					String str = dataArrayList.get(i).replaceAll(" ", "").trim();
					year_list[i] = utils.splitString(str, ",")[0];
					value_list[i] = utils.splitString(str, ",")[1];
					valuef_list[i] = Double.parseDouble(value_list[i]);
					
				    String yr=year_list[i];
				    double val=valuef_list[i];
					
					ObservableList<String> tech_list=this.checkComboBoxTech.getCheckModel().getCheckedItems();
					
					for (int t=0;t<tech_list.size();t++) {
						String tech=tech_list.get(t);
						
					    String load_str=utils.getTrnVehInfo("load", region, sector, subsector, tech, yr);
					    if (load_str==null) {
					    	System.out.println("why null?");
					    }
					    double load=Double.parseDouble(load_str);
					
					    String coef_str=utils.getTrnVehInfo("coefficient", region, sector, subsector, tech, yr);
					    if (coef_str==null) {
					    	//hack since NG vehicles are not in the coef list
					    	coef_str="5000";
					    	load=0.0;
					    }
					    double coef=Double.parseDouble(coef_str);
			
					    String io=yr+"_"+policy_name;
					    String iom=io+"Mkt";
					    
					    String outputratio=Double.toString((float)(1.0/val/1.61*131.76/1e6));
					    String pMultiplier=Double.toString((float)(load*1e9)); 
					    
					    content_p1+=region+","+sector+","+subsector+","+tech+","+yr+","+io+","+coef+","+io+","+outputratio+","+pMultiplier+ vars.getEol();
					    if (t==0) content_p2+=region+","+io+","+iom+",RES,"+yr+",1"+ vars.getEol();
					}
				}

				file_content+=content_p1+ vars.getEol();
				file_content+=content_p2;

			System.out.println("Done");
			}}

	}

	public String getMetaDataContent(TreeView<String> tree, String market, String policy) {
		String rtn_str = "";

		rtn_str += "########## Scenario Component Metadata ##########" + vars.getEol();
		rtn_str += "#Scenario component type: CAFE Std" + vars.getEol();
		rtn_str += "#Subsector: " + comboBoxSubsector.getValue() + vars.getEol();

		ObservableList tech_list = checkComboBoxTech.getCheckModel().getCheckedItems();
		String techs = utils.getStringFromList(tech_list, ";");
		rtn_str += "#Technologies: " + techs + vars.getEol();

		rtn_str += "#Units: " + comboBoxWhichUnits.getValue() + vars.getEol();
//		if (policy == null)
//			market = textFieldPolicyName.getText();
//		rtn_str += "#Policy name: " + policy + vars.getEol();
//		if (market == null)
//			market = textFieldMarketName.getText();
		rtn_str += "#Policy name: " + policy + vars.getEol();
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

				if (param.equals("subsector")) {
					comboBoxSubsector.setValue(value);
					comboBoxSubsector.fireEvent(new ActionEvent());
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
				if (param.equals("units")) {
					comboBoxWhichUnits.setValue(value);
					comboBoxWhichUnits.fireEvent(new ActionEvent());
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
		this.setUnitsLabel();
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
			if (comboBoxSubsector.getSelectionModel().getSelectedItem().equals("Select One")) {
				message += "Sector comboBox must have a selection" + vars.getEol();
				error_count++;
			}
			if (checkComboBoxTech.getCheckModel().getCheckedItems().size() == 0) {
				message += "Tech checkComboBox must have a selection" + vars.getEol();
				error_count++;
			}
			if (comboBoxWhichUnits.getSelectionModel().getSelectedItem().equals("Select One")) {
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

	public void setUnitsLabel() {
		String s = getUnits();

		String label = "";

		if (s == "No match") {
			label = "Warning - Units do not match!";
		} else {
			label = s;
		}

	}

	public String getUnits() {

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