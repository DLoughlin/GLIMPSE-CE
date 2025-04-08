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
import glimpseBuilder.TechBound;
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

// the following class defines the General tab when the New button of the
// left pane is clicked.//
public class TabTechAvailable extends PolicyTab implements Runnable {
	private GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	private GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	private GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	private GLIMPSEUtils utils = GLIMPSEUtils.getInstance();
	public TableView<TechBound> tableTechBounds = new TableView<>();
	// Initializing overall grid
	GridPane gridPaneTechBound = new GridPane();
	PaneForCountryStateTree paneForCountryStateTree = new PaneForCountryStateTree();

	ObservableList<TechBound> orig_list;
	ObservableList<TechBound> table_list;
	
	Label filterBySectorLabel=utils.createLabel("Filter by Sector: ");
	ComboBox<String> comboBoxSectorFilter = utils.createComboBoxString();
	
	Label filterByTextLabel=utils.createLabel(" Text: ");
	TextField filterTextField=utils.createTextField();
	Label firstYrLabel=utils.createLabel(" First yr: ");
	TextField firstYrTextField=utils.createTextField();
	Label lastYrLabel=utils.createLabel(" Last yr: ");
	TextField lastYrTextField=utils.createTextField();
	Button setFirstLastYrsButton=utils.createButton("Set Years",styles.bigButtonWid, "Set first, last years for visible technologies");
	Label selectLabel=utils.createLabel("Select: ");
	Button selectAllButton=utils.createButton("Never",styles.bigButtonWid, "Selects All? for visible technologies");
	Button selectRangeButton=utils.createButton("Range",styles.bigButtonWid, "Selects Range? for visible technologies");

	
	public TabTechAvailable(String title, Stage stageX) {

		this.setStyle(styles.font_style);

		firstYrTextField.setText("1975");
		firstYrTextField.setPrefWidth(styles.bigButtonWid);
		lastYrTextField.setText("2015");
		lastYrTextField.setPrefWidth(styles.bigButtonWid);

		TableColumn<TechBound, Boolean> isBoundAll = new TableColumn<TechBound, Boolean>("Never?");
		TableColumn<TechBound, Boolean> isBoundRange = new TableColumn<TechBound, Boolean>("Range?");
		
		TableColumn<TechBound, String> techNameCol = new TableColumn<TechBound, String>(
				"Sector : Subsector : Technology : Units");
		TableColumn<TechBound, String> firstYearCol = new TableColumn<TechBound, String>("First");
		TableColumn<TechBound, String> lastYearCol = new TableColumn<TechBound, String>("Last");

		tableTechBounds.getColumns().addAll(isBoundAll, isBoundRange, firstYearCol, lastYearCol, techNameCol);
		
		setFirstLastYrsButton.setOnAction(e->{
			String firstYr = firstYrTextField.getText();
			String lastYr = lastYrTextField.getText();
			updateFirstAndLastYears(firstYr,lastYr);
		});
		
		selectAllButton.setOnAction(e->{
			selectAllVisibleItems();
		});
		
		selectRangeButton.setOnAction(e->{
			selectRangeVisibleItems();
		});
		
		isBoundAll.setCellValueFactory(new Callback<CellDataFeatures<TechBound, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(CellDataFeatures<TechBound, Boolean> param) {
				TechBound tb = param.getValue();

				SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(tb.isBoundAll());

				// When "Active?" column change.
				booleanProp.addListener(new ChangeListener<Boolean>() {

					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
							Boolean newValue) {
						tb.setBoundAll(newValue);

					}
				});
				return booleanProp;
			}
		});

		isBoundAll.setCellFactory(new Callback<TableColumn<TechBound, Boolean>, //
				TableCell<TechBound, Boolean>>() {
			@Override
			public TableCell<TechBound, Boolean> call(TableColumn<TechBound, Boolean> p) {
				CheckBoxTableCell<TechBound, Boolean> cell = new CheckBoxTableCell<TechBound, Boolean>();
				cell.setAlignment(Pos.CENTER);
				return cell;
			}
		});

		isBoundRange.setCellValueFactory(new Callback<CellDataFeatures<TechBound, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(CellDataFeatures<TechBound, Boolean> param) {
				TechBound tb = param.getValue();

				SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(tb.isBoundRange());

				// When "Active?" column change.
				booleanProp.addListener(new ChangeListener<Boolean>() {

					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
							Boolean newValue) {
						tb.setBoundRange(newValue);

					}
				});
				return booleanProp;
			}
		});

		isBoundRange.setCellFactory(new Callback<TableColumn<TechBound, Boolean>, //
				TableCell<TechBound, Boolean>>() {
			@Override
			public TableCell<TechBound, Boolean> call(TableColumn<TechBound, Boolean> p) {
				CheckBoxTableCell<TechBound, Boolean> cell = new CheckBoxTableCell<TechBound, Boolean>();
				cell.setAlignment(Pos.CENTER);
				return cell;
			}
		});
		
		tableTechBounds.setEditable(true);

		isBoundAll.setEditable(true);

		techNameCol.setCellValueFactory(new PropertyValueFactory<>("techName"));
		techNameCol.setCellFactory(TextFieldTableCell.<TechBound>forTableColumn());
		techNameCol.setMinWidth(410);
		techNameCol.setPrefWidth(500);

		techNameCol.setEditable(false);

		firstYearCol.setCellFactory(TextFieldTableCell.<TechBound>forTableColumn());
		firstYearCol.setEditable(true);
		//firstYearCol.setPrefWidth(50.);

		firstYearCol.setCellValueFactory(new Callback<CellDataFeatures<TechBound, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<TechBound, String> param) {
				TechBound tb = param.getValue();

				SimpleStringProperty strProp = new SimpleStringProperty(tb.getFirstYear());

				// When "Active?" column change.
				strProp.addListener(new ChangeListener<String>() {

					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						tb.setFirstYear(newValue);

					}
				});
				return strProp;
			}
		});

		lastYearCol.setCellFactory(TextFieldTableCell.<TechBound>forTableColumn());
		lastYearCol.setEditable(true);
		//lastYearCol.setPrefWidth(50.);
		lastYearCol.setCellValueFactory(new Callback<CellDataFeatures<TechBound, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<TechBound, String> param) {
				TechBound tb = param.getValue();

				SimpleStringProperty strProp = new SimpleStringProperty(tb.getLastYear());

				// When "Active?" column change.
				strProp.addListener(new ChangeListener<String>() {

					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						tb.setLastYear(newValue);

					}
				});
				return strProp;
			}
		});

		//get list of technologies and adds them to the table
		if (orig_list==null) orig_list = getBoundList();
		ObservableList<TechBound> list2= hideNestedSubsectorFromTechList();

		tableTechBounds.setItems(list2);
		table_list=tableTechBounds.getItems();
		
		addFiltering();

		this.setText(title);

		HBox tabLayout = new HBox();
		tabLayout.autosize();

		VBox leftPanel = new VBox();

		leftPanel.setPadding(new Insets(10, 10, 10, 10));
		// leftPanel.getChildren().add(utils.createLabel(""));
		leftPanel.getChildren().add(
				utils.createLabel("Select technologies and specify all, first, or last years to constrain new purchases:"));
		//leftPanel.getChildren().add(utils.createLabel(""));
		
		HBox filterLayout = new HBox();
		filterLayout.setPadding(new Insets(10,10,10,10));

		filterLayout.getChildren().addAll(filterBySectorLabel,comboBoxSectorFilter,filterByTextLabel,filterTextField);
		setupComboBoxSector();
		
		HBox resetYrLayout = new HBox();
		resetYrLayout.setPadding(new Insets(5,5,5,5));
		resetYrLayout.setSpacing(5.);
		resetYrLayout.getChildren().addAll(selectLabel,selectAllButton,selectRangeButton,firstYrLabel,firstYrTextField,lastYrLabel,lastYrTextField,setFirstLastYrsButton);
				
		leftPanel.getChildren().addAll(filterLayout,tableTechBounds,resetYrLayout);

		paneForCountryStateTree.setMinWidth(275.);
		
		tabLayout.getChildren().addAll(leftPanel, paneForCountryStateTree);

		this.setContent(tabLayout);

	}
	
	private void setupComboBoxSector() {

		try {
			String[][] tech_info = vars.getTechInfo();

			ArrayList<String> sectorList = new ArrayList<String>();
			sectorList.add("Filter by Sector?");
			sectorList.add("All");

			for (int i = 0; i < tech_info.length; i++) {

				String text = tech_info[i][0].trim();

				boolean match = false;
				for (int j = 0; j < sectorList.size(); j++) {
					if (text.equals(sectorList.get(j)))
						match = true;
				}
				if (!match)
					sectorList.add(text);
			}

			for (int i = 0; i < sectorList.size(); i++) {
				comboBoxSectorFilter.getItems().add(sectorList.get(i).trim());
			}
			comboBoxSectorFilter.getSelectionModel().selectFirst();

		} catch (Exception e) {
			utils.warningMessage("Problem reading tech list.");
			System.out.println("Error reading tech list from " + vars.get("tchBndListFile") + ":");
			System.out.println("  ---> " + e);

		}
	}
	
	private ObservableList<TechBound> hideNestedSubsectorFromTechList() {
		ObservableList<TechBound> rtn_list=FXCollections.observableArrayList();
		
		for (int i=0;i<orig_list.size();i++) {
			TechBound tb0=orig_list.get(i);
			TechBound tb=new TechBound(tb0.getFirstYear(),tb0.getLastYear(),tb0.getTechName(),tb0.isBoundAll(),tb0.isBoundRange());
			String name=tb.getTechName();
			String component[]=name.split(":");
			String modified_name="";
			for (int j=0;j<component.length;j++) {
				if (component[j].indexOf("=>")>-1) {
					component[j]=component[j].split("=>")[1];
				}
				modified_name+=component[j];
				if (j!=component.length-1) modified_name+=" : ";
			}
			tb.setTechName(modified_name);
			rtn_list.add(tb);
		}
		
		return rtn_list;
	}
	
	private String getMatchingLineFromTechList(String line) {
		String matching_line="";
		
		String[] words=line.split(":");
		for (int i=0;i<words.length;i++) words[i]=words[i].trim();
		boolean match=false;
	
		for (int i=0;i<orig_list.size();i++) {
			String orig_line=orig_list.get(i).getTechName();
			if (!match) {
				for (int j=0;j<words.length;j++) {
					match=true;
					String txt=null;
					String txt1=null;
					if (j==0) { 
						txt=txt1=words[j].trim()+" :";
					} else {
						txt=": "+words[j].trim();
						txt1=">"+words[j].trim();
					}
					if ((orig_line.indexOf(txt)==-1)&&(orig_line.indexOf(txt1)==-1)) {
						match=false;
						break;
					}
				}
			}
			if (match) {
				matching_line=orig_line;
				break;
			}
		}
		
		if (!match) System.out.println("Error adding constraint to "+line);
		
		return matching_line;
	}
	
	private void updateFirstAndLastYears(String firstYr,String lastYr) {
		FilteredList<TechBound> visibleComponents = new FilteredList<>(tableTechBounds.getItems(),p->true);

		for (int i=0;i<visibleComponents.size();i++) {
			TechBound tb=visibleComponents.get(i);
			tb.setFirstYear(firstYr);
			tb.setLastYear(lastYr);
		}
		String text=filterTextField.getText();
		filterTextField.setText("Resetting...");
		filterTextField.setText(text);
	}
	
	private void selectAllVisibleItems() {
		FilteredList<TechBound> visibleComponents = new FilteredList<>(tableTechBounds.getItems(),p->true);

		boolean b=true;
		
		for (int i=0;i<visibleComponents.size();i++) {
			TechBound tb=visibleComponents.get(i);
			if (tb.isBoundAll()) b=false;
			tb.setBoundAll(b);
		}
		String text=filterTextField.getText();
		filterTextField.setText("Resetting...");
		filterTextField.setText(text);
	}
	
	private void selectRangeVisibleItems() {
		FilteredList<TechBound> visibleComponents = new FilteredList<>(tableTechBounds.getItems(),p->true);

		boolean b=true;
		
		for (int i=0;i<visibleComponents.size();i++) {
			TechBound tb=visibleComponents.get(i);
			if (tb.isBoundRange()) b=false;
			tb.setBoundRange(b);
		}
		String text=filterTextField.getText();
		filterTextField.setText("Resetting...");
		filterTextField.setText(text);
	}
	
	private void addFiltering() {

		FilteredList<TechBound> filteredComponents = new FilteredList<>(tableTechBounds.getItems(), p -> true);
		
		filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredComponents.setPredicate(techBound -> {
				
				//check sector first
				String sector=comboBoxSectorFilter.getSelectionModel().getSelectedItem().toLowerCase().trim();
				if (sector.equals("all")||sector.equals("filter by sector?")){
					;
				} else if (!techBound.getTechName().toLowerCase().startsWith(sector)) {
					return false;
				}
				
				// If user hasn't typed anything into the search bar
					if (newValue == null || newValue.isEmpty()) {
						// Display all items
						return true;
					}

					// Compare items with filter text
					// Comparison is not case sensitive
					String lowerCaseFilter = newValue.toLowerCase().trim();
					
					boolean rtn_val=true;

					if (techBound.getTechName().toLowerCase().contains(lowerCaseFilter)) {
						// Displays results that match
						//return true;
						rtn_val=true;
					} else {
						rtn_val=false;
					}
					return rtn_val; // Does not match.
				});
		});
		
		comboBoxSectorFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
			filteredComponents.setPredicate(techBound -> {
								
				// If user hasn't typed anything into the search bar
					if (newValue.equals("Filter by Sector?") || (newValue.equals("All"))) {
						// Display all items						
						return true;
					}

					// Compare items with filter text
					// Comparison is not case sensitive
					String lowerCaseFilter = newValue.toLowerCase();

					if (techBound.getTechName().toLowerCase().startsWith(lowerCaseFilter)) {
						// Displays results that match
						return true;
					}
					return false; // Does not match.
				});

			//replace with task implementation

			String filterText=filterTextField.getText();
			filterTextField.setText("");
			
			 if (!newValue.equals("Filter by Sector?")) {
				 //Platform.runLater(()->
				 filterTextField.setText(filterText);
				 //);
			 }
			 
		});

		// Adds the ability to sort the list after being filtered
		SortedList<TechBound> sortedComponents = new SortedList<>(filteredComponents);
		sortedComponents.comparatorProperty().bind(tableTechBounds.comparatorProperty());
		tableTechBounds.setItems(sortedComponents);
		
		FilteredList<TechBound> techList = new FilteredList<>(tableTechBounds.getItems(),p -> true);
		techList.setPredicate(techBound -> {
			return true;
		});
		//System.out.println("techlist size: "+techList.size());
	}
	

	@Override
	public void run() {
		  saveScenarioComponent();
	}
	


//	@Override
//	public void saveScenarioComponent() {//_part2() {
//		
//	
//		if (qaInputs()) {
//			
//			TreeView<String> tree = this.paneForCountryStateTree.getTree();
//
//			String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);
//
//			listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
//			String states = utils.returnAppendedString(listOfSelectedLeaves);
//
//			filename_suggestion = "TechAvailBnd";
//
//			String region = states.replace(",", "");
//			if (region.length() > 6) {
//				region = "Reg";
//			}
//			filename_suggestion += region;
//			filename_suggestion = filename_suggestion.replaceAll("/", "-").replaceAll(" ", "");
//
//			// sets up the content of the CSV file to store the scenario component data
//			file_content = getMetaDataContent(tree);
//			String file_content1 = "";
//			String file_content2 = "";
//
//			String header_1 = "GLIMPSETechAvailBnd";
//			String header_2 = "GLIMPSETechAvailBnd-Nest";
//			
//			int num_non_nest=0;
//			int num_nest=0;
//
//			// Setting up non-nested-inputs 
//			file_content1 += "INPUT_TABLE" + vars.getEol();
//			file_content1 += "Variable ID" + vars.getEol();
//			file_content1 += header_1 + vars.getEol() + vars.getEol();
//			file_content1 += "region,sector,subsector,tech,init-year,final-year" + vars.getEol();
//
//			file_content2 += "INPUT_TABLE" + vars.getEol();
//			file_content2 += "Variable ID" + vars.getEol();
//			file_content2 += header_2 + vars.getEol() + vars.getEol();
//			file_content2 += "region,sector,nesting-subsector,subsector,tech,init-year,final-year" + vars.getEol();
//
//			
//			int count = 0;
//			boolean isNested=false;
//			for (int j = 0; j < table_list/*techList*/.size(); j++) {
//				TechBound techBound = table_list/*techList*/.get(j);
//				
//				if ((techBound.isBoundAll())||(techBound.isBoundRange())) {
//					String tblName = techBound.getTechName();
//					String name=getMatchingLineFromTechList(tblName);
//					
//					if (name.indexOf("=>")>-1) { 
//						isNested=true;
//					} else {
//						isNested=false;
//					}
//					
//					String firstYear = techBound.getFirstYear();
//					String lastYear = techBound.getLastYear();
//
//					String[] info = name.split(":");
//					name=info[0].trim()+","+info[1].trim()+","+info[2].trim();
//								
//					if (techBound.isBoundAll()) {
//						firstYear = "3000";
//						lastYear = "3005";
//					}
//					if (isNested) name=name.replace("=>",",").trim();
//					
//					for (int s = 0; s < listOfSelectedLeaves.length; s++) {
//						String state = listOfSelectedLeaves[s];						
//						
//						String line = state + "," + name + "," + firstYear + ","
//							+ lastYear + vars.getEol();
//						
//						if (!isNested) {
//							num_non_nest++;
//							file_content1+=line;
//						} else {
//							num_nest++;
//							file_content2+=line;
//						}
//					}
//				}
//			}
//			if ((num_non_nest>0)&&(num_nest>0)) file_content2=vars.getEol()+file_content2;
//			if (num_non_nest>0) file_content+=file_content1;
//			if (num_nest>0) file_content+=file_content2;
//			
//			System.out.println("Exciting tab save code. file_content ..."+file_content.length()+" characters");
//			
//		}
//	}
	
	public void saveScenarioComponent() {//_part2() {
		
		
		if (!qaInputs()){
			Thread.currentThread().destroy();
		} else {
			
			TreeView<String> tree = this.paneForCountryStateTree.getTree();

			String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);

			listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
			String states = utils.returnAppendedString(listOfSelectedLeaves);

			filename_suggestion = "TechAvailBnd";

			/// setup of temp file to speed component creation; writes lines to disk instead of holding lines in memory
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
			file_content = "use temp file";
			files.writeToBufferedFile(bw0, getMetaDataContent(tree));
			/////////////////
			
			String region = states.replace(",", "");
			if (region.length() > 6) {
				region = "Reg";
			}
			filename_suggestion += region;
			filename_suggestion = filename_suggestion.replaceAll("/", "-").replaceAll(" ", "_");

			// sets up the content of the CSV file to store the scenario component data			
			String file_content1 = "";
			String file_content2 = "";

			String header_1 = "GLIMPSEStubTechShrwt";
			String header_2 = "GLIMPSEStubTechShrwt-Nest";
			
			int num_non_nest=0;
			int num_nest=0;

			// Setting up non-nested-inputs 
			file_content1 += "INPUT_TABLE" + vars.getEol();
			file_content1 += "Variable ID" + vars.getEol();
			file_content1 += header_1 + vars.getEol() + vars.getEol();
			file_content1 += "region,sector,subsector,tech,year,value" + vars.getEol();

			file_content2 += "INPUT_TABLE" + vars.getEol();
			file_content2 += "Variable ID" + vars.getEol();
			file_content2 += header_2 + vars.getEol() + vars.getEol();
			file_content2 += "region,sector,nesting-subsector,subsector,tech,year,value" + vars.getEol();

			files.writeToBufferedFile(bw1,file_content1);
			files.writeToBufferedFile(bw2,file_content2);
			file_content1="";
			file_content2="";
			
			
			int firstSimulationYear = vars.getSimulationStartYear();
			int lastSimulationYear = vars.getSimulationLastYear();
			int yearIncrement = vars.getSimulationYearIncrement();
			
			int count = 0;
			boolean isNested=false;
			for (int j = 0; j < table_list.size(); j++) {
				TechBound techBound = table_list.get(j);

				if ((techBound.isBoundAll())||(techBound.isBoundRange())) {
					String tblName = techBound.getTechName();
					String name=getMatchingLineFromTechList(tblName);

					if (name.indexOf("=>")>-1) { 
						isNested=true;
					} else {
						isNested=false;
					}

					// for this technology, identifies first and last years available
					int firstAvailYear = utils.convertStringToInt(techBound.getFirstYear());
					int lastAvailYear = utils.convertStringToInt(techBound.getLastYear());
					if (techBound.isBoundAll()) {
						firstAvailYear = 3000;
						lastAvailYear = 3005;
					}

					String[] info = name.split(":");
					name=info[0].trim()+","+info[1].trim()+","+info[2].trim();

					if (isNested) name=name.replace("=>",",").trim();

					for (int s = 0; s < listOfSelectedLeaves.length; s++) {
						String state = listOfSelectedLeaves[s];						

						for (int yr=firstSimulationYear;yr<=lastSimulationYear;yr+=yearIncrement) {

							if ((yr<firstAvailYear)||(yr>lastAvailYear)) {
								String line = state + "," + name + "," + yr + ",0.0"
										+ vars.getEol();

								if (!isNested) {
									num_non_nest++;
									file_content1+=line;
								} else {
									num_nest++;
									file_content2+=line;
								}
							}
						}
					}
					file_content1+=vars.getEol();
					file_content2+=vars.getEol();
					files.writeToBufferedFile(bw1,file_content1);
					files.writeToBufferedFile(bw2,file_content2);
					file_content1="";
					file_content2="";
					
				}
			}
			//if ((num_non_nest>0)&&(num_nest>0)) file_content2=vars.getEol()+file_content2;
			//if (num_non_nest>0) file_content+=file_content1;
			//if (num_nest>0) file_content+=file_content2;
			
			//final management steps to close buffered files, then concatenate contents
			files.closeBufferedFile(bw0);
			files.closeBufferedFile(bw1);
			files.closeBufferedFile(bw2);

			String temp_file = tempDirName + File.separator + "temp_policy_file.txt";

			files.deleteFile(tempDirName);

			String temp_file0 = tempDirName + File.separator + tempFilename0;
			String temp_file1 = tempDirName + File.separator + tempFilename1;
			String temp_file2 = tempDirName + File.separator + tempFilename2;

			ArrayList<String> tempfiles = new ArrayList<String>();
			tempfiles.add(temp_file0);

			if (num_non_nest > 0)
				tempfiles.add(temp_file1);
			if (num_nest > 0)
				tempfiles.add(temp_file2);

			files.concatDestSources(temp_file, tempfiles);

			System.out.println("Done");

			
		}
	}
	
	@Override
	public String getMetaDataContent(TreeView<String> tree) {
		String rtn_str="############ Scenario Component Meta-Data ############"+vars.getEol();				
		rtn_str+="#Scenario component type: Tech Avail"+vars.getEol();
		
		for (int i=0;i<table_list.size();i++) {
			TechBound bnd=table_list.get(i);
			if (bnd.isBoundAll()||bnd.isBoundRange()) {
				rtn_str+="#Bound:Never>"+bnd.isBoundAll()+",Range>"+bnd.isBoundRange()+",First>"+bnd.getFirstYear()+",Last>"+bnd.getLastYear()+",Tech>"+bnd.getTechName()+vars.getEol();;
			}
		}
	
		String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);
		listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
		String states = utils.returnAppendedString(listOfSelectedLeaves);
		rtn_str+="#Regions: "+states+vars.getEol();
		rtn_str+="######################################################"+vars.getEol();
		return rtn_str;
	}
	
	@Override
	public void loadContent(ArrayList<String> content) {
		ObservableList<TechBound> techList=tableTechBounds.getItems();
		for (int i=0;i<content.size();i++) {
			String line=content.get(i);
			int pos = line.indexOf(":");
			if (line.startsWith("#")&&(pos>-1)){
				String param=line.substring(1,pos).trim().toLowerCase();
				String value=line.substring(pos+1).trim();
				if (param.equals("bound")) {
					String[] attributes=utils.splitString(value, ",");
					String never="";
					String range="";
					String first="";
					String last="";
					String tech="";
					for (int j=0;j<attributes.length;j++) {
						String str=attributes[j];
						System.out.println("i:"+i+" "+str);
						int pos2=str.indexOf(">");
						String att=str.substring(0,pos2).trim().toLowerCase();
						String val=str.substring(pos2+1).trim();
						if (att.equals("never")) {
							never=val;
						} else if (att.equals("range")) {
							range=val;
						} else if (att.equals("first")) {
							first=val;
						} else if (att.equals("last")) {
							last=val;
						} else if (att.equals("tech")) {
							tech=val.toLowerCase();
						}
					}
					for(int k=0;k<techList.size();k++) {
						TechBound tb=techList.get(k);
						if (tb.getTechName().toLowerCase().equals(tech)) {
							if (never.equals("true")) tb.setBoundAll(true);
							if (range.equals("true")) tb.setBoundRange(true);
							if (!first.equals("")) tb.setFirstYear(first);
							if (!last.equals("")) tb.setLastYear(last);
							break;
						}
					}
				}
				if (param.equals("regions")) {
					String[] regions=utils.splitString(value,",");
					this.paneForCountryStateTree.selectNodes(regions);
				}			
			}
		}
	}



//	private void clearActive() {
//
//		ObservableList<TechBound> techList = tableTechBounds.getItems();
//
//		for (int j = 0; j < techList.size(); j++) {
//			TechBound techBound = techList.get(j);
//			if (techBound.isBoundAll())
//				techBound.setBoundAll(false);
//		}
//	}

//	private void setTechInfo(String tech, String firstYear, String lastYear) {
//
//		ObservableList<TechBound> techList = tableTechBounds.getItems();
//
//		for (int j = 0; j < techList.size(); j++) {
//			TechBound techBound = techList.get(j);
//			if (techBound.getTechName().trim().equals(tech)) {
//				techBound.setBoundAll(true);
//				techBound.setFirstYear(firstYear);
//				techBound.setLastYear(lastYear);
//			}
//		}
//	}

	private ObservableList<TechBound> getBoundList() {
		ObservableList<TechBound> list = FXCollections.observableArrayList();
		int num = 0;

		try {
			String[][] tech_info = vars.getTechInfo();
			String last_line="";
			for (int i = 0; i < tech_info.length; i++) {
				String line = tech_info[i][0].trim() + " : " + tech_info[i][1] + " : " + tech_info[i][2];
				if (line.equals(last_line)) {
					;
				} else {
					last_line=line;
					if (tech_info[i].length >= 7)
						line += " : " + tech_info[i][6];
					if (line.length() > 0) {
						list.add(new TechBound("1975", "2015", line, new Boolean(false),new Boolean(false)));
					}
				}
			}
			num++;

		} catch (Exception e) {
			utils.warningMessage("Problem reading tech list. Attempting to use defaults.");
			System.out.println("Error reading tech list from " + vars.get("tchBndListFile") + ":");
			System.out.println("  ---> " + e);
			if (num == 0)
				System.out.println("Stopping with " + num + " read in.");
		}
		return list;
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

			ObservableList<TechBound> techList = tableTechBounds.getItems();
			boolean at_least_one_active = false;

			for (int j = 0; j < techList.size(); j++) {
				TechBound techBound = techList.get(j);
				if (techBound.isBound()) {
					at_least_one_active = true;
				    break;
				}
			}

			if (!at_least_one_active) {
				message += "At least one technology must be bound" + vars.getEol();
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

