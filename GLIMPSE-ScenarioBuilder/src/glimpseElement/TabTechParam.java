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

import glimpseBuilder.CsvFileWriter;
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// the following class defines the General tab when the New button of the
// left pane is clicked.
public class TabTechParam extends PolicyTab implements Runnable {
	private GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	private GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	private GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	private GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

	// Initializing overall grid
	GridPane gridPanePresetModification = new GridPane();
	ScrollPane scrollPaneLeft = new ScrollPane();	
	
	// Initializing components of left column
	GridPane gridPaneLeft = new GridPane();

	double label_wid = 125;

	Label labelSector = utils.createLabel("Sector:", label_wid);
	ComboBox<String> comboBoxSector = utils.createComboBoxString();
	
	Label labelFilter = utils.createLabel("Filter:", label_wid);
	TextField textFieldFilter = utils.createTextField();

	Label labelCheckComboBoxTech = utils.createLabel("Tech(s): ", label_wid);
	CheckComboBox<String> checkComboBoxTech = utils.createCheckComboBox();

	Label labelComboBoxParam = utils.createLabel("Parameter: ", label_wid);
	ComboBox<String> comboBoxParam = utils.createComboBoxString();

	// used for pollutant species or dollar year
	Label labelComboBoxParam2 = utils.createLabel("Parameter 2: ", label_wid);
	ComboBox<String> comboBoxParam2 = utils.createComboBoxString();

	Label labelTextFieldInput = utils.createLabel("Input: ", label_wid);
	Label labelTextFieldInput2 = utils.createLabel("");
	Label labelTextFieldOutput = utils.createLabel("Output: ", label_wid);
	Label labelTextFieldOutput2 = utils.createLabel("", label_wid);
	Label labelTextFieldUnits = utils.createLabel("Units: ", label_wid);
	Label labelTextFieldUnits2 = utils.createLabel("", label_wid);
	
	/*
	 * Label labelTextFieldDollarYear = utils.createLabel("$ Year: ", label_wid);
	 * TextField textFieldDollarYear = new TextField("2010");
	 */
	// -----------------------------
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

	String[][] tech_info = null;

	public TabTechParam(String title, Stage stageX) {
		// sets tab title
		this.setText(title);
		this.setStyle(styles.font_style);

		checkComboBoxTech.getItems().clear();
		checkComboBoxTech.getItems().add("Select One or More");
		checkComboBoxTech.getCheckModel().check(0);

		comboBoxConvertFrom.getItems().addAll("None","2023$s","2020$s","2015$s","2010$s","2005$s","2000$s");
		comboBoxConvertFrom.getSelectionModel().selectFirst();
		comboBoxConvertFrom.setVisible(false);
		labelConvertFrom.setVisible(false);
		
		/*
		 * labelTextFieldDollarYear.setVisible(false);
		 * textFieldDollarYear.setVisible(false);
		 */
		labelComboBoxParam2.setVisible(false);
		comboBoxParam2.setVisible(false);
		
		//this.setUnitsLabel();

		gridPaneLeft.add(utils.createLabel("Specification:"), 0, 0, 2, 1);
		gridPaneLeft.addColumn(0, labelFilter, labelSector, labelCheckComboBoxTech, labelComboBoxParam, labelComboBoxParam2,
				labelTextFieldInput, labelTextFieldOutput, labelTextFieldUnits, /*labelTextFieldDollarYear,*/ new Separator(),
				utils.createLabel("Populate:"), labelModificationType, labelStartYear, labelEndYear, labelInitialAmount,
				labelGrowth,labelConvertFrom);

		gridPaneLeft.addColumn(1, textFieldFilter, comboBoxSector, checkComboBoxTech, comboBoxParam, comboBoxParam2,
				labelTextFieldInput2, labelTextFieldOutput2, labelTextFieldUnits2, /*textFieldDollarYear,*/ new Separator(), new Label(),
				comboBoxModificationType, textFieldStartYear, textFieldEndYear, textFieldInitialAmount,
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

		// ---adding components to the overall grid---
		gridPanePresetModification.addColumn(0, scrollPaneLeft);
		gridPanePresetModification.addColumn(1, vBoxCenter);
		gridPanePresetModification.addColumn(2, vBoxRight);

		gridPaneLeft.setPrefWidth(325);
		gridPaneLeft.setMinWidth(325);
		vBoxCenter.setPrefWidth(300);
		vBoxRight.setPrefWidth(300);

		// default sizing
		double max_wid = 195;
		checkComboBoxTech.setMaxWidth(max_wid);
		comboBoxModificationType.setMaxWidth(max_wid);
		comboBoxParam.setMaxWidth(max_wid);
		comboBoxParam2.setMaxWidth(max_wid);
		labelTextFieldInput.setMaxWidth(max_wid);
		labelTextFieldInput2.setMaxWidth(max_wid);
		labelTextFieldOutput.setMaxWidth(max_wid);
		labelTextFieldOutput2.setMaxWidth(max_wid);
		//textFieldDollarYear.setMaxWidth(max_wid);
		textFieldStartYear.setMaxWidth(max_wid);
		textFieldEndYear.setMaxWidth(max_wid);
		textFieldInitialAmount.setMaxWidth(max_wid);
		textFieldGrowth.setMaxWidth(max_wid);
		textFieldPeriodLength.setMaxWidth(max_wid);
		textFieldFilter.setMaxWidth(max_wid);
		
		double min_wid = 105;
		checkComboBoxTech.setMinWidth(min_wid);
		comboBoxModificationType.setMinWidth(min_wid);
		comboBoxParam.setMinWidth(min_wid);
		comboBoxParam2.setMinWidth(min_wid);
		// labelTextFieldInput.setMinWidth(min_wid);
		labelTextFieldInput2.setMinWidth(min_wid);
		// labelTextFieldOutput.setMinWidth(min_wid);
		labelTextFieldOutput2.setMinWidth(min_wid);
		//textFieldDollarYear.setMinWidth(min_wid);
		textFieldStartYear.setMinWidth(min_wid);
		textFieldEndYear.setMinWidth(min_wid);
		textFieldInitialAmount.setMinWidth(min_wid);
		textFieldGrowth.setMinWidth(min_wid);
		textFieldPeriodLength.setMinWidth(min_wid);
		textFieldFilter.setMinWidth(min_wid);
		
		double pref_wid = 195;
		checkComboBoxTech.setPrefWidth(pref_wid);
		comboBoxModificationType.setPrefWidth(pref_wid);
		comboBoxParam.setPrefWidth(pref_wid);
		comboBoxParam2.setPrefWidth(pref_wid);
		// labelTextFieldInput.setPrefWidth(pref_wid);
		labelTextFieldInput2.setPrefWidth(pref_wid);
		// labelTextFieldInput.setPrefWidth(pref_wid);
		labelTextFieldInput2.setPrefWidth(pref_wid);
		//textFieldDollarYear.setPrefWidth(pref_wid);
		textFieldStartYear.setPrefWidth(pref_wid);
		textFieldEndYear.setPrefWidth(pref_wid);
		textFieldInitialAmount.setPrefWidth(pref_wid);
		textFieldGrowth.setPrefWidth(pref_wid);
		textFieldPeriodLength.setPrefWidth(pref_wid);
		textFieldFilter.setPrefWidth(pref_wid);
		
		//
		tech_info = vars.getTechInfo();

		setupComboBoxSector();

		comboBoxSector.getItems().add("Select One");
		comboBoxSector.getSelectionModel().select(0);
		checkComboBoxTech.setDisable(true);

//		comboBoxParam.getItems().addAll("Capacity Factor", "Capital Cost", "Efficiency", "Fixed Output", "Shareweight",
//				"Subsector Shareweight", "Lifetime", "Halflife", "EF(in)", "EF(out)");
		comboBoxParam.getItems().addAll("Shareweight", "Subsector Shareweight", "Nested-Subsector Shareweight", "Levelized Non-Energy Cost", "Capacity Factor","Fixed Output",  "Lifetime",
				"Halflife");

		comboBoxParam.getSelectionModel().selectFirst();
		comboBoxParam.setDisable(false);

		comboBoxParam2.getItems().addAll("Select One");
		comboBoxParam2.getSelectionModel().selectFirst();
		comboBoxParam2.setDisable(true);

		comboBoxModificationType.getItems().addAll("Initial and Final", "Initial w/% Growth/yr",
				"Initial w/% Growth/pd", "Initial w/Delta/yr", "Initial w/Delta/pd");
		comboBoxModificationType.getSelectionModel().selectFirst();

//		comboBoxSector.setOnAction(e -> {
//			String selectedItem = comboBoxSector.getSelectionModel().getSelectedItem();
//			if (selectedItem != null) {
//				updateCheckComboTechs();
//				checkComboBoxTechs.setDisable(false);
//			}			
//		});
		
		textFieldFilter.setOnAction(e->{ 
			setupComboBoxSector(); 
	    });

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
			if (selectedItem == null)
				return;
			if (selectedItem.equals("Select One")) {
				checkComboBoxTech.getCheckModel().clearChecks();
				checkComboBoxTech.getItems().clear();
				checkComboBoxTech.getItems().add("Select One or More");
				checkComboBoxTech.getCheckModel().check(0);
				checkComboBoxTech.setDisable(true);
				labelTextFieldUnits2.setText("");
			} else {
				updateCheckComboTechs();
				checkComboBoxTech.setDisable(false);				
			}
			this.setUnitsLabel();
			// setPolicyAndMarketNames();
		});

		comboBoxParam.setOnAction(e -> {

			// parameter box 2
			comboBoxParam2.getSelectionModel().selectFirst();
			comboBoxParam2.setDisable(true);
			comboBoxParam2.setVisible(false);

			// other options
			//labelTextFieldDollarYear.setVisible(false);
			//textFieldDollarYear.setVisible(false);

			try {
				String selectedItem = comboBoxParam.getSelectionModel().getSelectedItem();
				if (selectedItem.indexOf("Emis") >= 0) {
					comboBoxParam2.getItems().clear();
					comboBoxParam2.getItems().addAll("Select One", "NOx", "SO2", "PM10", "PM2.5", "CO", "NH3", "NMVOC",
							"BC", "OC");
					comboBoxParam2.getSelectionModel().select(0);
					comboBoxParam2.setDisable(false);
					comboBoxParam2.setVisible(true);
				} else {
					comboBoxParam2.getSelectionModel().select(0);
					comboBoxParam2.setDisable(true);
				}

				if (comboBoxParam.getSelectionModel().getSelectedItem().equals("Levelized Non-Energy Cost")) {
					//labelTextFieldDollarYear.setVisible(true);
					//textFieldDollarYear.setVisible(true);
					labelConvertFrom.setVisible(true);
					comboBoxConvertFrom.setVisible(true);
				}
				
				setUnitsLabel();

			} catch (Exception ex) {
				;
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

		checkComboBoxTech.getCheckModel().getCheckedItems().addListener(new ListChangeListener() {
			@Override
			public void onChanged(ListChangeListener.Change c) {
				updateInputOutputUnits();
				setUnitsLabel();
			}
		});

		VBox tabLayout = new VBox();
		tabLayout.getChildren().addAll(gridPanePresetModification);

		this.setContent(tabLayout);
	}

	private void updateInputOutputUnits() {
		ObservableList<String> checkedItems = checkComboBoxTech.getCheckModel().getCheckedItems();

		String input = "";
		String output = "";

		if (checkedItems.size() == 0) {
			labelTextFieldInput2.setText("");
			labelTextFieldOutput2.setText("");
			return;
		}

		String[] words = null;
		;
		for (int c = 0; c < checkedItems.size(); c++) {
			String line = checkedItems.get(c).trim();
			words = utils.splitString(line, ":");

			if (words.length>=2) {
			String sector = words[0].trim();
			String subsector = words[1].trim();
			String tech = words[2].trim();

			for (int i = 0; i < tech_info.length; i++) {
				if ((sector.equals(tech_info[i][0])) && (subsector.equals(tech_info[i][1]))
						&& (tech.equals(tech_info[i][2]))) {
					String this_input = tech_info[i][3] + "(" + tech_info[i][4] + ")";
					String this_output = tech_info[i][5] + "(" + tech_info[i][6] + ")";

					if ((input == null)||(input.equals(""))) {
						input = this_input;
					} else {
						if ((!input.equals("various")) && (!input.equals(this_input)))
							input = "various";
					}
					if ((output == null)||(output.equals(""))) { 
						output = this_output;
					} else {
						if ((!output.equals("various")) && (!output.equals(this_output)))
							output = "various";
					}
				}
			}
			}
		}
		labelTextFieldInput2.setText(input);
		labelTextFieldOutput2.setText(output);

	}

	private void setupComboBoxSector() {
		comboBoxSector.getItems().clear();

		try {
			String[][] tech_info = vars.getTechInfo();

			ArrayList<String> sectorList = new ArrayList<String>();

			String filterText=textFieldFilter.getText().trim();
			boolean useFilter=false;
			if ((filterText!=null)&&(filterText.length()>0)) useFilter=true;
			
			if (!useFilter) sectorList.add("Select One");
			sectorList.add("All");
			
			for (int i = 0; i < tech_info.length; i++) {

				String text = tech_info[i][0].trim();

				boolean match = false;
				for (int j = 0; j < sectorList.size(); j++) {
					if (text.equals(sectorList.get(j)))
						match = true;
				}
				if (!match) {
					boolean show=true;
					if (useFilter) {
						show=false;
						for (int j=0;j<tech_info[i].length;j++) {
							String temp=tech_info[i][j];
							if (temp.contains(filterText)) show=true;
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
	
	private void updateCheckComboTechs() {
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
				String filter_text=this.textFieldFilter.getText().trim();
				
				for (int i = 0; i < tech_info.length; i++) {

					String line = tech_info[i][0].trim() + " : " + tech_info[i][1] + " : " + tech_info[i][2];
					if ((filter_text.length()==0)||(line.contains(filter_text))) {
					
					if (tech_info[i].length>=7) line+= " : "+tech_info[i][6];

					if (line.equals(last_line)) {
						;
					} else {
						last_line = line;
						if ((is_all_sectors) || (line.startsWith(sector))) {
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

	@Override
	public String getMetaDataContent(TreeView<String> tree) {
		String rtn_str = "";

		rtn_str += "########## Scenario Component Metadata ##########" + vars.getEol();
		rtn_str += "#Scenario component type: Tech Param" + vars.getEol();
		rtn_str += "#Sector: " + comboBoxSector.getValue() + vars.getEol();

		ObservableList tech_list = checkComboBoxTech.getCheckModel().getCheckedItems();
		String techs = utils.getStringFromList(tech_list, ";");
		rtn_str += "#Technologies: " + techs + vars.getEol();

		rtn_str += "#Parameter: " + comboBoxParam.getValue() + vars.getEol();

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
				if (param.equals("parameter")) {
					comboBoxParam.setValue(value);
					comboBoxParam.fireEvent(new ActionEvent());
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
		updateInputOutputUnits();
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

	public void addIOToTextFields(String[][] tech_info, int row, String[] prefix) {
		boolean match = false;

		for (int i = 0; i < tech_info.length; i++) {

			if (doesPrefixMatch(tech_info[i], prefix)) {
				String text = tech_info[i][3];
				if (tech_info[i].length > 3)
					text += " (" + tech_info[i][4] + ")";
				labelTextFieldInput2.setText(text);

				if (tech_info[i].length > 4) {
					text = tech_info[i][5];
					if (tech_info[i].length > 5)
						text += " (" + tech_info[i][6] + ")";
					labelTextFieldOutput2.setText(text);
				} else {
					labelTextFieldOutput2.setText("");
				}
			}

		}

	}

	public void addNonDuplicatesToComboBox(ComboBox<String> comboBox, String[][] tech_info, int row, String[] prefix) {

		boolean match = false;

		for (int i = 0; i < tech_info.length; i++) {

			if (doesPrefixMatch(tech_info[i], prefix)) {

				if (comboBox.getItems().indexOf(tech_info[i][row]) < 0) {
					comboBox.getItems().add(tech_info[i][row]);
				}

			}

		}

	}

	public void addNonDuplicatesToCheckComboBox(CheckComboBox<String> checkComboBox, String[][] tech_info, int row,
			String[] prefix) {

		boolean match = false;

		for (int i = 0; i < tech_info.length; i++) {

			if (doesPrefixMatch(tech_info[i], prefix)) {

				if (checkComboBox.getItems().indexOf(tech_info[i][row]) < 0) {
					checkComboBox.getItems().add(tech_info[i][row]);
				}

			}

		}

	}

	public boolean doesPrefixMatch(String[] item, String[] prefix) {
		boolean match = true;

		if (prefix != null) {

			for (int i = 0; i < prefix.length; i++) {

				if (!item[i].equals(prefix[i]))
					match = false;
			}
		}
		return match;
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
		String tempUnitsVal=this.labelTextFieldUnits2.getText();
		String toYear="1975$s";
		if (tempUnitsVal.contains("1990")) toYear="1990$s";
		if (convertYear!="None") {
			factor=utils.getConversionFactor(convertYear,toYear);
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
		if (!qaInputs()) {
			Thread.currentThread().destroy();
		} else {
			CsvFileWriter cfw = CsvFileWriter.getInstance();

			ArrayList<String> dataList = loadDataFromGUI(tree);

			ArrayList<String> colList = files.getStringArrayFromFile(vars.get("csvColumnFile"), "#");
			ArrayList<String> csvContent = cfw.createCsvContent2(colList, dataList);

			file_content = getMetaDataContent(tree);

			file_content += utils.createStringFromArrayList(csvContent);

			filename_suggestion = "" + utils.getMatch(dataList, "type", ";") + "techParam.csv";
			filename_suggestion = filename_suggestion.replaceAll("/", "-").replaceAll(" ", "_");
		}

	}

	private ArrayList<String> loadDataFromGUI(TreeView<String> tree) {

		ArrayList<String> dataList = new ArrayList<String>();

		ObservableList<String> checkedItems = checkComboBoxTech.getCheckModel().getCheckedItems();

		String[] words = null;
		;
		for (int c = 0; c < checkedItems.size(); c++) {
			String line = checkedItems.get(c).trim();
			words = utils.splitString(line, ":");

			String sector = "sector:" + words[0].trim();
			String subsector = "subsector:" + words[1].trim();
			String tech = "technology:" + words[2].trim();

			String region = "region:" + getSelectedLeaves(tree);
			String input = "input:" + labelTextFieldInput2.getText().trim();
			String output = "output:" + labelTextFieldOutput2.getText().trim();
			String param = "param:" + this.comboBoxParam.getValue();
			String param2 = "param2:" + this.comboBoxParam2.getValue();
			//String dollarYear = "dolarYear:" + this.textFieldDollarYear.getText();

			ObservableList<DataPoint> data = this.paneForComponentDetails.table.getItems();
			String year = "year:";
			String value = "value:";

			for (int i = 0; i < data.size(); i++) {
				if (i != 0) {
					year += ",";
					value += ",";
				}
				year += data.get(i).getYear();
				value += data.get(i).getValue();
			}

			String group = "all";
			if (sector.indexOf("trn") >= 0)
				group = "trn";
			if (sector.indexOf("generation") >= 0)
				group = "egu";
			String type = "type:" + group + "/" + this.comboBoxParam.getValue();

			String data_str = type + ";" + sector + ";" + subsector + ";" + tech + ";" + region + ";" + input + ";"
					+ output + ";" + param + ";" + param2 + ";" + /*dollarYear + ";" +*/ year + ";" + value;

			dataList.add(data_str);
		}

		return dataList;
	}

	private String getSelectedLeaves(TreeView<String> tree) {
		String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);
		String states = utils.returnAppendedString(listOfSelectedLeaves);
		if ((states.contains("USA")) && (vars.isGcamUSA())) {
			states = states.replace(",USA", "");
		}
		return states;
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
//			if (comboBoxSubSector.getSelectionModel().getSelectedItem().equals("Select One")) {
//				message += "SubSector comboBox must have a selection" + vars.getEol();
//				error_count++;
//			}
//			if (comboBoxTechName.getSelectionModel().getSelectedItem().equals("Select One")) { //DAN!!!
//				message += "Tech comboBox must have a selection" + vars.getEol();
//				error_count++;
//			}
			if (comboBoxParam.getSelectionModel().getSelectedItem().equals("Select One")) {
				message += "Parameter comboBox must have a selection" + vars.getEol();
				error_count++;
			}
			if (comboBoxParam2.isVisible()) {
				if (comboBoxParam2.getSelectionModel().getSelectedItem().equals("Select One")) {
					message += "Parameter2 comboBox must have a selection" + vars.getEol();
					error_count++;
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
	
	public void setUnitsLabel() {
		String s=getUnits();
		
		String label="";
		
		String selected_param=this.comboBoxParam.getSelectionModel().getSelectedItem();
		
		if (this.checkComboBoxTech.getCheckModel().getCheckedIndices().size()>0){
			
		
		switch(selected_param) {
		   
		    case "Levelized Non-Energy Cost":
				if (s.equals("No match")) { 
					label="Warning - Units do not match!";
				} else if (s.equals("million pass-km")){
					label="1990$ per veh-km";
				} else if (s.equals("million ton-km")){
					label="1990$ per veh-km";
				} else if (s.equals("")){
					label="";
				} else {
					String s2="GJ";
					if (s.equals("EJ")) s2="GJ";
					if (s.equals("petalumen-hours")) s2="megalumen-hours";
					if (s.equals("million km3")) s2="million m3";
					if (s.equals("billion cycles")) s2="cycle";
					if (s.equals("Mt")) s2="kg";
					if (s.equals("km^3")) s2="m^3";
					
					label="1975$s per "+s2; 
				}		         	
		    	break;
		    case "Capacity Factor":
		    	label="Unitless";
		    	break;
		    case "Fixed Output":
		    	String s2=this.labelTextFieldOutput2.getText();
		    	label=utils.getParentheticString(s2);
		    	break;
		    case "Lifetime":
		    	label="years";
		    	break;
		    case "Halflife":
		    	label="years";
		    	break;
		    default:
		    	label="unitless";
		    	
		}   
		
		}
		labelTextFieldUnits2.setText(label);
		
	}

	public String getUnits() {
		String rtn_str="";
		
		ObservableList<String> tech_list = checkComboBoxTech.getCheckModel().getCheckedItems();
		
		String unit="";
		
		for (int t=0;t<tech_list.size();t++) {
			String line=tech_list.get(t);
			String item="";
			try{
				item=line.substring(line.lastIndexOf(":")+1).trim();
				if (unit.equals("")) { 
					unit=item;
				} else if (!unit.equals(item)) {
					unit="No match";
				}
			} catch(Exception e) {
				item="";
			}
		}
		if (unit.trim().equals("Select One or More")) unit="";		
		return unit;
	}

}

