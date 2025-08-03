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
 * Parks and Yadong Xu of ARA through the EPA’s Environmental Modeling and 
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

/**
 * TabTechAvailable provides the user interface and logic for managing technology availability
 * in the GLIMPSE Scenario Builder. This tab allows users to filter, select, and configure
 * technology bounds and availability for scenario components.
 *
 * <p>
 * <b>Usage:</b> This class is instantiated as a tab in the scenario builder. It extends {@link PolicyTab} and implements {@link Runnable}.
 * </p>
 *
 * <p>
 * <b>Thread Safety:</b> This class is not thread-safe and should be used on the JavaFX Application Thread.
 * </p>
 *
 * <p>
 * <b>Main Features:</b>
 * <ul>
 *   <li>Displays a table of available technologies with options to set bounds and years.</li>
 *   <li>Allows filtering by technology type and text search.</li>
 *   <li>Supports selection of all or a range of technologies for scenario constraints.</li>
 *   <li>Handles both nested and non-nested technology structures for scenario export.</li>
 *   <li>Integrates with a region/country selection tree for scenario targeting.</li>
 *   <li>Provides methods for saving scenario components and shareweights to file.</li>
 *   <li>Supports loading of scenario component content from file.</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Key Methods:</b>
 * <ul>
 *   <li>{@link #TabTechAvailable(String, Stage)} - Constructor, sets up UI and event handlers.</li>
 *   <li>{@link #saveScenarioComponent()} - Saves the current scenario component to file.</li>
 *   <li>{@link #saveScenarioComponentShareweight()} - Saves shareweight scenario data to file.</li>
 *   <li>{@link #loadContent(ArrayList)} - Loads scenario component data from file.</li>
 *   <li>{@link #qaInputs()} - Validates user input before saving.</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Dependencies:</b>
 * <ul>
 *   <li>JavaFX for UI components</li>
 *   <li>GLIMPSE utility classes for file, style, and variable management</li>
 *   <li>{@link TechBound} for technology data representation</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Author:</b> GLIMPSE Project Team
 * </p>
 */
public class TabTechAvailable extends PolicyTab implements Runnable {
    // === Utility Instances ===
    private final GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
    private final GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
    private final GLIMPSEFiles files = GLIMPSEFiles.getInstance();
    private final GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

    // === Table and Layout Components ===
    public final TableView<TechBound> tableTechBounds = new TableView<>();
    private final GridPane gridPaneTechBound = new GridPane();
    private final PaneForCountryStateTree paneForCountryStateTree = new PaneForCountryStateTree();

    // === Data Lists ===
    private ObservableList<TechBound> orig_list;
    private ObservableList<TechBound> table_list;

    // === Filter and Control UI ===
    private final Label filterByTypeLabel = utils.createLabel("Filter by Type: ");
    private final ComboBox<String> comboBoxTypeFilter = utils.createComboBoxString();
    private final Label filterByTextLabel = utils.createLabel(" Text: ");
    private final TextField filterTextField = utils.createTextField();
    private final Label firstYrLabel = utils.createLabel(" First yr: ");
    private final TextField firstYrTextField = utils.createTextField();
    private final Label lastYrLabel = utils.createLabel(" Last yr: ");
    private final TextField lastYrTextField = utils.createTextField();
    private final Button setFirstLastYrsButton = utils.createButton("Set Years", styles.getBigButtonWidth(), "Set first, last years for visible technologies");
    private final Label selectLabel = utils.createLabel("Select: ");
    private final Button selectAllButton = utils.createButton("Never", styles.getBigButtonWidth(), "Selects All? for visible technologies");
    private final Button selectRangeButton = utils.createButton("Range", styles.getBigButtonWidth(), "Selects Range? for visible technologies");

	
	/**
	 * Constructor for TabTechAvailable. Sets up the UI, event handlers, and initializes the technology bounds table.
	 *
	 * @param title  the title of the tab
	 * @param stageX the JavaFX Stage
	 */
	public TabTechAvailable(String title, Stage stageX) {

		this.setStyle(styles.getFontStyle());

		firstYrTextField.setText("1975");
		firstYrTextField.setPrefWidth(styles.getBigButtonWidth());
		lastYrTextField.setText("2015");
		lastYrTextField.setPrefWidth(styles.getBigButtonWidth());

		TableColumn<TechBound, Boolean> isBoundAll = new TableColumn<TechBound, Boolean>("Never?");
		TableColumn<TechBound, Boolean> isBoundRange = new TableColumn<TechBound, Boolean>("Range?");
		
		TableColumn<TechBound, String> techNameCol = new TableColumn<TechBound, String>(
				"Sector : Subsector : Technology : Units // Type");
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
						tb.setIsBoundAll(newValue);
					
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
						tb.setIsBoundRange(newValue);

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

		filterLayout.getChildren().addAll(filterByTypeLabel,comboBoxTypeFilter,filterByTextLabel,filterTextField);
		setupComboBoxType();
		
		HBox resetYrLayout = new HBox();
		resetYrLayout.setPadding(new Insets(5,5,5,5));
		resetYrLayout.setSpacing(5.);
		resetYrLayout.getChildren().addAll(selectLabel,selectAllButton,selectRangeButton,firstYrLabel,firstYrTextField,lastYrLabel,lastYrTextField,setFirstLastYrsButton);
				
		leftPanel.getChildren().addAll(filterLayout,tableTechBounds,resetYrLayout);

		paneForCountryStateTree.setMinWidth(275.);
		
		tabLayout.getChildren().addAll(leftPanel, paneForCountryStateTree);

		this.setContent(tabLayout);

	}
	
	private void setupComboBoxType() {
		comboBoxTypeFilter.getItems().addAll(vars.getTypesFromTechBnd());
		comboBoxTypeFilter.getSelectionModel().selectFirst();
	}
	
	private void setupComboBoxSectorOld() {

		try {
			String[][] tech_info = vars.getTechInfo();

			ArrayList<String> sectorList = new ArrayList<String>();
			sectorList.add("Filter by Sector?");
			sectorList.add("All");

			for (String[] tech : tech_info) {
				String text = tech[0].trim();

				boolean match = false;
				for (String sector : sectorList) {
					if (text.equals(sector))
						match = true;
				}
				if (!match)
					sectorList.add(text);
			}

			for (String sector : sectorList) {
				comboBoxTypeFilter.getItems().add(sector.trim());
			}
			comboBoxTypeFilter.getSelectionModel().selectFirst();

		} catch (Exception e) {
			utils.warningMessage("Problem reading tech list.");
			System.out.println("Error reading tech list from " + vars.getTchBndListFilename() + ":");
			System.out.println("  ---> " + e);

		}
	}
	
	private ObservableList<TechBound> hideNestedSubsectorFromTechList() {
		ObservableList<TechBound> rtn_list=FXCollections.observableArrayList();
		
		for (TechBound tb0 : orig_list) {
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
	
		for (TechBound orig_tb : orig_list) {
			String orig_line=orig_tb.getTechName();
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

		for (TechBound tb : visibleComponents) {
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
		
		for (TechBound tb : visibleComponents) {
			if (tb.isBoundAll()) b=false;
			tb.setIsBoundAll(b);
		}
		String text=filterTextField.getText();
		filterTextField.setText("Resetting...");
		filterTextField.setText(text);
	}
	
	private void selectRangeVisibleItems() {
		FilteredList<TechBound> visibleComponents = new FilteredList<>(tableTechBounds.getItems(),p->true);

		boolean b=true;
		
		for (TechBound tb : visibleComponents) {
			if (tb.isBoundRange()) b=false;
			tb.setIsBoundRange(b);
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
				String type=comboBoxTypeFilter.getSelectionModel().getSelectedItem().toLowerCase().trim();
				if (type.equals("")||type.equals("all")||type.equals("filter by type?")){
					;
				} else if (!techBound.getTechName().toLowerCase().trim().endsWith(type)) {
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
		
		comboBoxTypeFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
			filteredComponents.setPredicate(techBound -> {
								
				// If user hasn't typed anything into the search bar
					if (newValue.equals("Filter by Type?") || (newValue.equals("All"))) {
						// Display all items						
						return true;
					}

					// Compare items with filter text
					// Comparison is not case sensitive
					String lowerCaseFilter = newValue.toLowerCase();

					if (techBound.getTechName().toLowerCase().trim().endsWith(lowerCaseFilter)) {
						// Displays results that match
						return true;
					}
					return false; // Does not match.
				});

			//replace with task implementation

			String filterText=filterTextField.getText();
			filterTextField.setText("");
			
			 if (!newValue.equals("Filter by Type?")) {
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
	

	/**
	 * Runs background tasks or updates for this tab. Implementation of Runnable interface.
	 */
	@Override
	public void run() {
		  saveScenarioComponent();
	}
	


    /**
     * Saves the current scenario component to file, including both nested and non-nested technology bounds.
     */
	public void saveScenarioComponent() {//_part2() {
		
	
		if (qaInputs()) {
			
			TreeView<String> tree = this.paneForCountryStateTree.getTree();

			String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);

			listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
			String states = utils.returnAppendedString(listOfSelectedLeaves);

			filenameSuggestion = "TechAvailBnd";

			String region = states.replace(",", "");
			if (region.length() > 6) {
				region = "Reg";
			}
			filenameSuggestion += region;
			filenameSuggestion = filenameSuggestion.replaceAll("/", "-").replaceAll(" ", "");

			// sets up the content of the CSV file to store the scenario component data
			fileContent = getMetaDataContent(tree);
			String fileContent1 = "";
			String fileContent2 = "";

			String header_1 = "GLIMPSETechAvailBnd";
			String header_2 = "GLIMPSETechAvailBnd-Nest";
			
			int num_non_nest=0;
			int num_nest=0;

			// Setting up non-nested-inputs 
			fileContent1 += "INPUT_TABLE" + vars.getEol();
			fileContent1 += "Variable ID" + vars.getEol();
			fileContent1 += header_1 + vars.getEol() + vars.getEol();
			fileContent1 += "region,sector,subsector,tech,init-year,final-year" + vars.getEol();

			fileContent2 += "INPUT_TABLE" + vars.getEol();
			fileContent2 += "Variable ID" + vars.getEol();
			fileContent2 += header_2 + vars.getEol() + vars.getEol();
			fileContent2 += "region,sector,nesting-subsector,subsector,tech,init-year,final-year" + vars.getEol();

			
			int count = 0;
			boolean isNested=false;
			for (int j = 0; j < table_list/*techList*/.size(); j++) {
				TechBound techBound = table_list/*techList*/.get(j);
				
				if ((techBound.isBoundAll())||(techBound.isBoundRange())) {
					String tblName = techBound.getTechName();
					String name=getMatchingLineFromTechList(tblName);
					
					if (name.indexOf("=>")>-1) { 
						isNested=true;
					} else {
						isNested=false;
					}
					
					String firstYear = techBound.getFirstYear();
					String lastYear = techBound.getLastYear();

					String[] info = name.split(":");
					name=info[0].trim()+","+info[1].trim()+","+info[2].trim();
								
					if (techBound.isBoundAll()) {
						firstYear = "3000";
						lastYear = "3005";
					}
					if (isNested) name=name.replace("=>",",").trim();
					
					for (int s = 0; s < listOfSelectedLeaves.length; s++) {
						String state = listOfSelectedLeaves[s];						
						
						String line = state + "," + name + "," + firstYear + ","
							+ lastYear + vars.getEol();
						
						if (!isNested) {
							num_non_nest++;
							fileContent1+=line;
						} else {
							num_nest++;
							fileContent2+=line;
						}
					}
				}
			}
			if ((num_non_nest>0)&&(num_nest>0)) fileContent2=vars.getEol()+fileContent2;
			if (num_non_nest>0) fileContent+=fileContent1;
			if (num_nest>0) fileContent+=fileContent2;
			
			//System.out.println("Exciting tab save code. fileContent ..."+fileContent.length()+" characters");
			
		}
	}

	/**
     * Saves shareweight scenario data to file, handling both nested and non-nested technologies.
     */
	public void saveScenarioComponentShareweight() {//_part2() {
		
		
		if (!qaInputs()){
//			Thread.currentThread().destroy();
//		} else {
			
			TreeView<String> tree = this.paneForCountryStateTree.getTree();

			String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);

			listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
			String states = utils.returnAppendedString(listOfSelectedLeaves);

			filenameSuggestion = "TechAvailBnd";

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
			fileContent = "use temp file";
			files.writeToBufferedFile(bw0, getMetaDataContent(tree));
			/////////////////
			
			String region = states.replace(",", "");
			if (region.length() > 6) {
				region = "Reg";
			}
			filenameSuggestion += region;
			filenameSuggestion = filenameSuggestion.replaceAll("/", "-").replaceAll(" ", "_");

			// sets up the content of the CSV file to store the scenario component data			
			String fileContent1 = "";
			String fileContent2 = "";

			String header_1 = "GLIMPSEStubTechShrwt";
			String header_2 = "GLIMPSEStubTechShrwt-Nest";
			
			int num_non_nest=0;
			int num_nest=0;

			// Setting up non-nested-inputs 
			fileContent1 += "INPUT_TABLE" + vars.getEol();
			fileContent1 += "Variable ID" + vars.getEol();
			fileContent1 += header_1 + vars.getEol() + vars.getEol();
			fileContent1 += "region,sector,subsector,tech,year,value" + vars.getEol();

			fileContent2 += "INPUT_TABLE" + vars.getEol();
			fileContent2 += "Variable ID" + vars.getEol();
			fileContent2 += header_2 + vars.getEol() + vars.getEol();
			fileContent2 += "region,sector,nesting-subsector,subsector,tech,year,value" + vars.getEol();

			files.writeToBufferedFile(bw1,fileContent1);
			files.writeToBufferedFile(bw2,fileContent2);
			fileContent1="";
			fileContent2="";
			
			
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
									fileContent1+=line;
								} else {
									num_nest++;
									fileContent2+=line;
								}
							}
						}
					}
					fileContent1+=vars.getEol();
					fileContent2+=vars.getEol();
					files.writeToBufferedFile(bw1,fileContent1);
					files.writeToBufferedFile(bw2,fileContent2);
					fileContent1="";
					fileContent2="";
					
				}
			}
			
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
	
	
	/**
	 * Generates metadata content for the scenario component, including technology bounds and selected regions.
	 *
	 * @param tree the TreeView containing region selections
	 * @return a String containing the metadata content
	 */
	public String getMetaDataContent(TreeView<String> tree) {
		String rtn_str="############ Scenario Component Meta-Data ############"+vars.getEol();				
		rtn_str+="#Scenario component type: Tech Avail"+vars.getEol();
		
		for (int i=0;i<table_list.size();i++) {
			TechBound bnd=table_list.get(i);
			if (bnd.isBoundAll()||bnd.isBoundRange()) {
				rtn_str+="#Bound:Never>"+bnd.isBoundAll()+",Range>"+bnd.getLastYear()+",First>"+bnd.getFirstYear()+",Last>"+bnd.getLastYear()+",Tech>"+bnd.getTechName()+vars.getEol();;
			}
		}
	
		String[] listOfSelectedLeaves = utils.getAllSelectedLeaves(tree);
		listOfSelectedLeaves = utils.removeUSADuplicate(listOfSelectedLeaves);
		String states = utils.returnAppendedString(listOfSelectedLeaves);
		rtn_str+="#Regions: "+states+vars.getEol();
		rtn_str+="######################################################"+vars.getEol();
		return rtn_str;
	}
	
	/**
     * Loads scenario component data from file and updates the UI accordingly.
     *
     * @param content the list of content lines to load
     */
	@Override
	public void loadContent(ArrayList<String> content) {
		ObservableList<TechBound> techList=tableTechBounds.getItems();
		for (String line : content) {
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
					for (String str : attributes) {
						//System.out.println("i:"+i+" "+str);
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
					for(TechBound tb : techList) {
						if (tb.getTechName().toLowerCase().equals(tech)) {
							if (never.equals("true")) tb.setIsBoundAll(true);
							if (range.equals("true")) tb.setIsBoundRange(true);
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

	private ObservableList<TechBound> getBoundList() {
		ObservableList<TechBound> list = FXCollections.observableArrayList();
		int num = 0;

		try {
			String[][] tech_info = vars.getTechInfo();
			String last_line="";
			for (String[] tech : tech_info) {
				String line = tech[0].trim() + " : " + tech[1] + " : " + tech[2];
				if (line.equals(last_line)) {
					;
				} else {
					last_line=line;
					if (tech.length >= 7)
						line += " : " + tech[6];
					if (tech.length >= 8)
						line += " // " + tech[7];
					if (line.length() > 0) {
						list.add(new TechBound("1975", "2015", line, new Boolean(false),new Boolean(false)));
					}
				}
			}
			num++;

		} catch (Exception e) {
			utils.warningMessage("Problem reading tech list. Attempting to use defaults.");
			System.out.println("Error reading tech list from " + vars.getTchBndListFilename() + ":");
			System.out.println("  ---> " + e);
			if (num == 0)
				System.out.println("Stopping with " + num + " read in.");
		}
		return list;
	}
	

	/**
     * Validates user input before saving the scenario component. Checks for at least one region and one technology bound.
     *
     * @return true if all required fields are valid, false otherwise
     */
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
