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

public class TabTechBound extends PolicyTab implements Runnable {
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
	
	Label labelFilter = utils.createLabel("Filter:", label_wid);
	TextField textFieldFilter = utils.createTextField();

	Label labelComboBoxSector = utils.createLabel("Sector: ", label_wid);
	ComboBox<String> comboBoxSector = utils.createComboBoxString();

	Label labelCheckComboBoxTech = utils.createLabel("Tech(s): ", label_wid);
	CheckComboBox<String> checkComboBoxTech = utils.createCheckComboBox();

	Label labelComboBoxConstraint = utils.createLabel("Constraint: ", label_wid);
	ComboBox<String> comboBoxConstraint = utils.createComboBoxString();

	//Label labelAppliedTo = utils.createLabel("Applied to: ", label_wid);
	//ComboBox<String> comboBoxAppliedTo = utils.createComboBoxString();

	Label labelTreatment = utils.createLabel("Treatment: ", label_wid);
	ComboBox<String> comboBoxTreatment = utils.createComboBoxString();

	Label labelPolicyName = utils.createLabel("Policy: ", label_wid);
	TextField textFieldPolicyName = new TextField("");

	Label labelMarketName = utils.createLabel("Market: ", label_wid);
	TextField textFieldMarketName = new TextField("");

	Label labelUseAutoNames = utils.createLabel("Names: ", label_wid);
	CheckBox checkBoxUseAutoNames = utils.createCheckBox("Auto?");

	Label labelModificationType = utils.createLabel("Type: ", label_wid);
	ComboBox<String> comboBoxModificationType = utils.createComboBoxString();
	
	Label labelUnits = utils.createLabel("Units: ",label_wid);
	Label labelUnits2 = utils.createLabel("EJ",225.);

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

	public TabTechBound(String title, Stage stageX) {
		// sets tab title
		this.setText(title);
		this.setStyle(styles.font_style);

		// sets up initial state of check box and policy and market textfields
		checkBoxUseAutoNames.setSelected(true);
		textFieldPolicyName.setDisable(true);
		textFieldMarketName.setDisable(true);

		// left column
		gridPaneLeft.add(utils.createLabel("Specification:"), 0, 0, 2, 1);
		gridPaneLeft.addColumn(0, labelFilter ,labelComboBoxSector, labelCheckComboBoxTech, labelComboBoxConstraint, 
				labelTreatment, new Label(), labelUnits, new Label(), new Separator(), labelUseAutoNames, labelPolicyName, labelMarketName,
				new Label(), new Separator(), utils.createLabel("Populate:"), labelModificationType, labelStartYear,
				labelEndYear, labelInitialAmount, labelGrowth);

		gridPaneLeft.addColumn(1, textFieldFilter,comboBoxSector, checkComboBoxTech, comboBoxConstraint, 
				comboBoxTreatment, new Label(), labelUnits2, new Label(), new Separator(), checkBoxUseAutoNames, textFieldPolicyName,
				textFieldMarketName, new Label(), new Separator(), new Label(), comboBoxModificationType,
				textFieldStartYear, textFieldEndYear, textFieldInitialAmount, textFieldGrowth);

		gridPaneLeft.setVgap(3.);
		gridPaneLeft.setStyle(styles.style2);
		
		scrollPaneLeft.setContent(gridPaneLeft);

		// treatment
		comboBoxTreatment.getItems().addAll("Select One", "Each Selected Region", "Across Selected Regions");
		// currently only "Each Selected" is supported
		comboBoxTreatment.getSelectionModel().select("Each Selected Region");
		// comboBoxTreatment.setDisable(true);

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

		setupComboBoxSector();

		// techs
		checkComboBoxTech.getItems().add("Select One or More");
		checkComboBoxTech.getCheckModel().checkAll();
		checkComboBoxTech.setDisable(true);

		comboBoxConstraint.getItems().addAll("Select One", "Upper", "Lower", "Fixed");

//		comboBoxAppliedTo.getItems().addAll("Select One", "All Stock", "New Purchases");
//		comboBoxAppliedTo.getSelectionModel().select("All Stock");

		comboBoxModificationType.getItems().addAll("Initial and Final", "Initial w/% Growth/yr",
				"Initial w/% Growth/pd", "Initial w/Delta/yr", "Initial w/Delta/pd");

		comboBoxSector.getSelectionModel().selectFirst();
		// comboBoxTech.getSelectionModel().selectFirst();
		comboBoxConstraint.getSelectionModel().selectFirst();
		comboBoxModificationType.getSelectionModel().selectFirst();

		// default sizing
		double max_wid = 175;
		comboBoxSector.setMaxWidth(max_wid);
		checkComboBoxTech.setMaxWidth(max_wid);
		comboBoxModificationType.setMaxWidth(max_wid);
		comboBoxConstraint.setMaxWidth(max_wid);
		comboBoxTreatment.setMaxWidth(max_wid);
		textFieldStartYear.setMaxWidth(max_wid);
		textFieldEndYear.setMaxWidth(max_wid);
		textFieldInitialAmount.setMaxWidth(max_wid);
		textFieldGrowth.setMaxWidth(max_wid);
		textFieldPeriodLength.setMaxWidth(max_wid);
		textFieldPolicyName.setMaxWidth(max_wid);
		textFieldMarketName.setMaxWidth(max_wid);
		textFieldFilter.setMaxWidth(max_wid);
		
		double min_wid = 105;
		comboBoxSector.setMinWidth(min_wid);
		checkComboBoxTech.setMinWidth(min_wid);
		comboBoxModificationType.setMinWidth(min_wid);
		comboBoxConstraint.setMinWidth(min_wid);
		comboBoxTreatment.setMinWidth(max_wid);
		textFieldStartYear.setMinWidth(min_wid);
		textFieldEndYear.setMinWidth(min_wid);
		textFieldInitialAmount.setMinWidth(min_wid);
		textFieldGrowth.setMinWidth(min_wid);
		textFieldPeriodLength.setMinWidth(min_wid);
		textFieldPolicyName.setMinWidth(min_wid);
		textFieldMarketName.setMinWidth(min_wid);
		textFieldFilter.setMinWidth(min_wid);
		
		double pref_wid = 175;
		comboBoxSector.setPrefWidth(pref_wid);
		checkComboBoxTech.setPrefWidth(pref_wid);
		comboBoxModificationType.setPrefWidth(pref_wid);
		comboBoxConstraint.setPrefWidth(pref_wid);
		comboBoxTreatment.setPrefWidth(pref_wid);
		textFieldStartYear.setPrefWidth(pref_wid);
		textFieldEndYear.setPrefWidth(pref_wid);
		textFieldInitialAmount.setPrefWidth(pref_wid);
		textFieldGrowth.setPrefWidth(pref_wid);
		textFieldPeriodLength.setPrefWidth(pref_wid);
		textFieldPolicyName.setPrefWidth(pref_wid);
		textFieldMarketName.setPrefWidth(pref_wid);
		textFieldFilter.setPrefWidth(pref_wid);
		
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
			if (selectedItem != null) {
				updateCheckComboBoxTech();
			}
			setPolicyAndMarketNames();
			//setUnitsLabel();

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
			//setUnitsLabel();

		});


		 checkComboBoxTech.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
		     public void onChanged(ListChangeListener.Change<? extends String> c) {
		          while(c.next()) {
		              setUnitsLabel();
		          }
		     }
		 });
		 
		
		comboBoxConstraint.setOnAction(e -> {
			setPolicyAndMarketNames();
			//setUnitsLabel();
		});

		comboBoxTreatment.setOnAction(e -> {
			setPolicyAndMarketNames();
			//setUnitsLabel();
		});

		EventHandler<TreeModificationEvent> ev = new EventHandler<TreeModificationEvent>() {
			@Override
			public void handle(TreeModificationEvent ae) {
				ae.consume();
				setPolicyAndMarketNames();
				//setUnitsLabel();
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

		buttonClear.setOnAction(e -> {
			this.paneForComponentDetails.clearTable();
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

		buttonDelete.setOnAction(e -> {
			this.paneForComponentDetails.deleteItemsFromTable();
		});

		buttonPopulate.setOnAction(e -> {
			if (qaPopulate()) {
				double[][] values = calculateValues();
				paneForComponentDetails.setValues(values);
			}
		});
		
		textFieldFilter.setOnAction(e->{ 
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
	
//	private void updateCheckComboBoxTech() {
//		String sector = comboBoxSector.getValue();
//		String[][] tech_info = vars.getTechInfo();
//
//		try {
//			if (checkComboBoxTech.getItems().size() > 0) {
//				checkComboBoxTech.getCheckModel().clearChecks();
//				checkComboBoxTech.getItems().clear();
//			}
//
//			if (sector != null) {
//				String last_line = "";
//				for (int i = 0; i < tech_info.length; i++) {
//					String line = tech_info[i][0].trim() + " : " + tech_info[i][1] + " : " + tech_info[i][2];
//					if (line.equals(last_line)) {
//						;
//					} else {
//						last_line = line;
//						if ((line.startsWith(sector)) || (sector.equals("All"))) {
//							if (tech_info[i].length >= 7)
//								line += " : " + tech_info[i][6];
//							if (line.length() > 0) {
//								String line2 = line;
//								//Dan: commented out so all lines include the sector name regardless of sector choice box selection
////								if (line.startsWith(sector)) {
////									line2 = line.substring(line.indexOf(":") + 1).trim();
////								}
//								checkComboBoxTech.getItems().add(line2);
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
	
	private void setPolicyAndMarketNames() {

		if (this.checkBoxUseAutoNames.isSelected()) {

			String policy_type = "--";
			String technology = "Tech";
			String sector = "--";
			String state = "--";
			String treatment = "--";

			try {
				String s = comboBoxConstraint.getValue();
				if (s.indexOf("Upper") >= 0)
					policy_type = "Up";
				if (s.indexOf("Lower") >= 0)
					policy_type = "Lo";
				if (s.indexOf("Fixed") >= 0)
					policy_type = "Fx";

				s = comboBoxSector.getValue();
				if (!s.equals("Select One")) {
					s = s.replace(" ", "_");
					s = utils.capitalizeOnlyFirstLetterOfString(s);
					sector = s;
				}

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

				String name = policy_type + "_" + sector + "_" + technology + "_" + state + treatment;
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

		if (!qaInputs()){
			Thread.currentThread().destroy();
		} else {

			String bound_type = comboBoxConstraint.getSelectionModel().getSelectedItem().trim().toLowerCase();

			//String ID=this.getUniqueMarketName(textFieldMarketName.getText());
			String ID=utils.getUniqueString();
			String policy_name = this.textFieldPolicyName.getText() + ID;
			String market_name = this.textFieldMarketName.getText() + ID;
			filename_suggestion=this.textFieldPolicyName.getText().replaceAll("/", "-").replaceAll(" ", "_")+".csv";

			String tempDirName = vars.getGlimpseDir() + File.separator + "GLIMPSE-Data" + File.separator + "temp"; // vars.getGlimpseDir();
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

			file_content = "use temp file";
			files.writeToBufferedFile(bw0, getMetaDataContent(tree, market_name, policy_name));

			String treatment = comboBoxTreatment.getValue().toLowerCase();

			//// -----------getting selected regions info from GUI
			String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);
			// Dan: messy approach to make sure inclusion of USA is intentional
			listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);

			String states = utils.returnAppendedString(listOfSelectedLeaves);

			String which = "tax";
			String header_part1 = "GLIMPSEPFStdTechUpBoundP1";
			String header_part2 = "GLIMPSEPFStdTechUpBoundP2";
			if (bound_type.equals("fixed")) {
				header_part2 = "GLIMPSEPFStdTechFxBoundP2";
			}

			if (bound_type.equals("lower")) {
				which = "subsidy";
				header_part1 = "GLIMPSEPFStdTechLoBoundP1";
				header_part2 = "GLIMPSEPFStdTechLoBoundP2";
			}

			ObservableList<String> tech_list = checkComboBoxTech.getCheckModel().getCheckedItems();

			// setting up input table for nested sources
			files.writeToBufferedFile(bw1,"INPUT_TABLE" + vars.getEol());
			files.writeToBufferedFile(bw1,"Variable ID" + vars.getEol());
			files.writeToBufferedFile(bw1,header_part1 + "-Nest" + vars.getEol() + vars.getEol());
			files.writeToBufferedFile(bw1,"region,sector,nested-subsector,subsector,tech,year,policy-name" + vars.getEol());

			files.writeToBufferedFile(bw2,"INPUT_TABLE" + vars.getEol());
			files.writeToBufferedFile(bw2,"Variable ID" + vars.getEol());
			files.writeToBufferedFile(bw2,header_part1 + vars.getEol() + vars.getEol());
			files.writeToBufferedFile(bw2,"region,sector,subsector,tech,year,policy-name" + vars.getEol());

			//getting values for constraint
			ArrayList<String> dataArrayList = this.paneForComponentDetails.getDataYrValsArrayList();
			String[] year_list = new String[dataArrayList.size()];
			String[] value_list = new String[dataArrayList.size()];
			double[] valuef_list = new double[dataArrayList.size()];					

			//setting up dates for iteration
			for (int i = 0; i < dataArrayList.size(); i++) {
				String str = dataArrayList.get(i).replaceAll(" ", "").trim();
				year_list[i] = utils.splitString(str, ",")[0];
				value_list[i] = utils.splitString(str, ",")[1];
				valuef_list[i] = Double.parseDouble(value_list[i]);
			}
			int start_year = 2010;
			int last_year = Integer.parseInt(year_list[year_list.length-1]);
			String sss = vars.getStartYearForShare();
			if (!sss.equals("2010")) {
				try {
					start_year = Integer.parseInt(sss);
				} catch (Exception e1) {
					System.out.println(
							"Problem converting startYearForShare (" + sss + ") to int. Using default value of 2010.");
				}
			}
			
			for (int t = 0; t < tech_list.size(); t++) {

				//gets tech info from tech list
				String[] temp = utils.splitString(tech_list.get(t).trim(), ":");
				
				String sector=temp[0].trim();
				String subsector=temp[1].trim();
				String tech=temp[2].trim();

				boolean is_nested = false;

				//checks to see if sector is nested
				if (subsector.indexOf("=>") > -1) {
					is_nested = true;
					no_nested += 1;
					subsector = subsector.replaceAll("=>", ",");
				} else {
					is_nested = false;
					no_non_nested += 1;
				}

				// writes data
				for (int s = 0; s < listOfSelectedLeaves.length; s++) {
					String state = listOfSelectedLeaves[s];

					String use_this_policy_name = policy_name;
					if (treatment.equals("each selected region")) {
						if (listOfSelectedLeaves.length >= 2) {
							use_this_policy_name = state + "_" + policy_name;
						}
					}

					// iterates over lines in constraint table
					for (int y = start_year; y <= last_year; y +=5) {
					
////					for (int i = 0; i < dataArrayList.size(); i++) {
//						String data_str = data.get(i).replace(" ", "");
//						String year = utils.splitString(data_str, ",")[0];

						if (is_nested) {
							files.writeToBufferedFile(bw1,state + "," + sector + "," + subsector + "," + tech + "," + y + ","
									+ use_this_policy_name + vars.getEol());
						} else {
							files.writeToBufferedFile(bw2,state + "," + sector + "," + subsector + "," + tech + "," + y + ","
									+ use_this_policy_name + vars.getEol());
						}

					}
					double progress = (double) s / listOfSelectedLeaves.length;
					progress_bar.setProgress(progress);
				}
			}
			files.writeToBufferedFile(bw1,""+vars.getEol());
			files.writeToBufferedFile(bw2,""+vars.getEol());

			// if (t == 0) {
			files.writeToBufferedFile(bw3,"INPUT_TABLE" + vars.getEol());
			files.writeToBufferedFile(bw3,"Variable ID" + vars.getEol());
			files.writeToBufferedFile(bw3,header_part2 + vars.getEol() + vars.getEol());

			if (bound_type.equals("fixed")) {
				files.writeToBufferedFile(bw3,"region,policy-name,market,type,constraint-yr,constraint-val,min-price-yr,min-price-val"
						+ vars.getEol());
			} else {
				files.writeToBufferedFile(bw3,"region,policy-name,market,type,constraint-yr,constraint-val" + vars.getEol());
			}

			for (int s = 0; s < listOfSelectedLeaves.length; s++) {
				String state = listOfSelectedLeaves[s];

				String use_this_market_name = market_name;
				String use_this_policy_name = policy_name;
				if (treatment.equals("each selected region")) {
					if (listOfSelectedLeaves.length >= 2) {
						use_this_market_name = state + "_" + market_name;
						use_this_policy_name = state + "_" + policy_name;
					}
				}

				ArrayList<String> data = this.paneForComponentDetails.getDataYrValsArrayList();
				for (int i = 0; i < data.size(); i++) {

					String data_str = data.get(i).replace(" ", "");
					String year = utils.splitString(data_str, ",")[0];
					String val = utils.splitString(data_str, ",")[1];

					if (!bound_type.equals("fixed")) {
						files.writeToBufferedFile(bw3,state + "," + use_this_policy_name + "," + use_this_market_name + "," + which
								+ "," + year + "," + val + vars.getEol());
					} else {
						files.writeToBufferedFile(bw3,state + "," + use_this_policy_name + "," + use_this_market_name + "," + which
								+ "," + year + "," + val + "," + year + ",-100" + vars.getEol());
					}
				}
				double progress = (double) s / listOfSelectedLeaves.length;
				progress_bar.setProgress(progress);
			}


			files.closeBufferedFile(bw0);
			files.closeBufferedFile(bw1);
			files.closeBufferedFile(bw2);
			files.closeBufferedFile(bw3);
			
			// TODO: store temp file name in options file and vars?
			String temp_file = tempDirName + File.separator + "temp_policy_file.txt";

			files.deleteFile(tempDirName);

			String temp_file0 = tempDirName + File.separator + tempFilename0;
			String temp_file1 = tempDirName + File.separator + tempFilename1;
			String temp_file2 = tempDirName + File.separator + tempFilename2;
			String temp_file3 = tempDirName + File.separator + tempFilename3;
			
			ArrayList<String> tempfiles = new ArrayList<String>();
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

	public void setUnitsLabel() {
		String s=getUnits();
		
		String label="";
		
		if (s=="No match") { 
			label="Warning - Units do not match!";
		} else {
			label=s;
		}
		
		labelUnits2.setText(label);
		
	}
	
	public String getUnits() {
		
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