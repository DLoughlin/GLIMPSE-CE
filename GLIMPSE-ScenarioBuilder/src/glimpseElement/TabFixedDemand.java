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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.CheckBoxTreeItem.TreeModificationEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TabFixedDemand extends PolicyTab implements Runnable {
	private GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	private GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	private GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

	public static String descriptionText = "";
	public static String runQueueStr = "Queue is empty.";

	// Initializing overall grid
	GridPane gridPanePresetModification = new GridPane();

	// Initializing components of left column
	GridPane gridPaneLeft = new GridPane();

	// Bound type
	// Label labelComboBoxType = utils.createLabel("Type: ");
	// ComboBox<String> comboBoxType = utils.createComboBoxString();
	double label_wid = 125;

	// sector
	Label labelSector = utils.createLabel("Sector: ", label_wid);
	ComboBox<String> comboBoxSector = utils.createComboBoxString();
	
	Label labelTextFieldUnits = utils.createLabel("Units: ", label_wid);
	Label labelTextFieldUnits2 = utils.createLabel("", label_wid);

	// convenience area
	Label labelModificationType = utils.createLabel("Type: ", label_wid);
	ComboBox<String> comboBoxModificationType = utils.createComboBoxString();
	Label labelStartYear = utils.createLabel("Start Year: ", label_wid);
	TextField textFieldStartYear = new TextField("2020");
	Label labelEndYear = utils.createLabel("End Year: ", label_wid);
	TextField textFieldEndYear = new TextField("2050");
	Label labelInitialAmount = utils.createLabel("Initial: ", label_wid);
	TextField textFieldInitialAmount = utils.createTextField();
	Label labelGrowth = utils.createLabel("Final: ", label_wid);
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
	VBox vBoxRight = new VBox();
	PaneForCountryStateTree paneForCountryStateTree = new PaneForCountryStateTree();
	
	// tech bound list for populating output and units
	String[][] sector_info = null;

	public TabFixedDemand(String title, Stage stageX) {
		// sets tab title
		this.setText(title);
		this.setStyle(styles.font_style);

		// gets or creates list of sectors and their outputs
		sector_info = vars.getSectorInfo();
		
		// left column
		gridPaneLeft.add(utils.createLabel("Specification:"), 0, 0, 2, 1);
		gridPaneLeft.addColumn(0, labelSector, new Label(), this.labelTextFieldUnits,  
				new Separator(), utils.createLabel("Populate:"), labelModificationType, labelStartYear, labelEndYear, labelInitialAmount,
				labelGrowth);

		gridPaneLeft.addColumn(1, comboBoxSector, new Label(),this.labelTextFieldUnits2,  
				new Separator(), new Label(), comboBoxModificationType, textFieldStartYear, textFieldEndYear, textFieldInitialAmount,
				textFieldGrowth);
		gridPaneLeft.setAlignment(Pos.TOP_LEFT);

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

		gridPaneLeft.setPrefWidth(325);
		gridPaneLeft.setMinWidth(325);
		vBoxCenter.setPrefWidth(300);
		vBoxRight.setPrefWidth(300);
		// labelSector.setMinWidth(90);

		// widget setup
		// sector
		setupComboBoxSector();
		comboBoxSector.getSelectionModel().selectFirst();

		// widgets for populating tables
		comboBoxModificationType.getItems().addAll("Initial and Final", "Initial w/% Growth/yr",
				"Initial w/% Growth/pd", "Initial w/Delta/yr", "Initial w/Delta/pd");
		comboBoxModificationType.getSelectionModel().selectFirst();

		// default sizing
		double max_wid = 195;
		comboBoxSector.setMaxWidth(max_wid);
		comboBoxModificationType.setMaxWidth(max_wid);

		double min_wid = 105;
		comboBoxSector.setMinWidth(min_wid);
		comboBoxModificationType.setMinWidth(min_wid);

		double pref_wid = 195;
		comboBoxSector.setPrefWidth(pref_wid);
		comboBoxModificationType.setPrefWidth(pref_wid);

		// widget actions
		comboBoxSector.setOnAction(e -> {
			String selectedItem = comboBoxSector.getSelectionModel().getSelectedItem();
			if (selectedItem == null)
				return;
			if (selectedItem=="Other") {
				//set other sector box to visible and enable
			} else {
				this.updateSectorOutputAndUnits();
			}
		});
		
		comboBoxSector.fireEvent(new ActionEvent());

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
				this.labelGrowth.setText("Final:");
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

		VBox tabLayout = new VBox();
		tabLayout.getChildren().addAll(gridPanePresetModification);

		this.setContent(tabLayout);
	}

	private void setupComboBoxSector() {

		try {
			
			for (int i=0;i<sector_info.length;i++) {
				comboBoxSector.getItems().add(sector_info[i][0]);
			}
			
			comboBoxSector.getItems().add("Other");

		} catch (Exception e) {
			utils.warningMessage("Problem reading sector list.");
			System.out.println("  ---> " + e);

		}
	}
	
	private void updateSectorOutputAndUnits() {
		String selectedSector=this.comboBoxSector.getValue();
		this.labelTextFieldUnits2.setText("");		
		
		for (int i=0;i<sector_info.length;i++) {
			if (selectedSector==sector_info[i][0]) {
				this.labelTextFieldUnits2.setText(sector_info[i][2]);
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

		if (qaInputs()) {
			file_content=getMetaDataContent(tree,"","");
			file_content+="INPUT_TABLE"+vars.getEol()+"Variable ID"+vars.getEol();
			file_content+="GLIMPSEFixedDemand"+vars.getEol()+vars.getEol();
			//// -----------getting info from GUI
			// selected regions
			String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);
			listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);

			// sector
			String sector_name = comboBoxSector.getSelectionModel().getSelectedItem().trim();
			filename_suggestion = sector_name + "fxDMD";

			ArrayList<String> dataArrayList = this.paneForComponentDetails.getDataYrValsArrayList();
			String[] year_list = new String[dataArrayList.size()];
			String[] value_list = new String[dataArrayList.size()];

			for (int i = 0; i < dataArrayList.size(); i++) {
				String str = dataArrayList.get(i).replace(" ", "").trim();
				year_list[i] = utils.splitString(str, ",")[0];
				value_list[i] = utils.splitString(str, ",")[1];
			}

			// sets up the content of the CSV file to store the scenario component data
			file_content+="region,sector,sector,year,value"+vars.getEol();
			
			for (int s=0;s<listOfSelectedLeaves.length;s++) {
				for (int i=0;i<year_list.length;i++) {
					file_content+=listOfSelectedLeaves[s]+","+sector_name+","+sector_name+","+year_list[i]+","+value_list[i]+vars.getEol();
				}
			}
					
			System.out.println("here it would construct csv file.");
		}

	}

	public String getMetaDataContent(TreeView<String> tree, String market, String policy) {
		String rtn_str = "";

		rtn_str += "########## Scenario Component Metadata ##########" + vars.getEol();
		rtn_str += "#Scenario component type: Fixed Demand" + vars.getEol();
		rtn_str += "#Sector:" + comboBoxSector.getValue() + vars.getEol();

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
//			if (utils.getAllSelectedLeaves(tree).length > 1) {
//				message += "Note: Demand level will be used for each selected region" + vars.getEol();
//			}
			if (paneForComponentDetails.table.getItems().size() == 0) {
				message += "Data table must have at least one entry" + vars.getEol();
				error_count++;
			}
			if (comboBoxSector.getSelectionModel().getSelectedItem().equals("Select One")) {
				message += "Sector comboBox must have a selection" + vars.getEol();
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
				utils.warningMessage("More than one issue with inputs");
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