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
package gui;

import static javafx.stage.Modality.APPLICATION_MODAL;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import glimpseElement.PolicyTab;
import glimpseElement.ScenarioRow;
import glimpseElement.ScenarioTable;
import glimpseElement.TabCafeStd;
import glimpseElement.ComponentRow;
import glimpseElement.TabMarketShare;
import glimpseElement.TabFuelPriceAdj;
import glimpseElement.ComponentLibraryTable;
import glimpseElement.TabXMLList;
import glimpseElement.TabPollutantTaxCap;
import glimpseElement.TabTechTax;
import glimpseUtil.FileChooserPlus;
import glimpseElement.TabTechAvailable;
import glimpseElement.TabTechBound;
import glimpseElement.TabTechParam;
import glimpseElement.TabFixedDemand;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

// /////////////////////////////////////////////////////////////////////////////////////////
// this class generates the top-left pane
// /////////////////////////////////////////////////////////////////////////////////////////
public class PaneNewScenarioComponent extends gui.ScenarioBuilder {// VBox {

	Thread computational_tread;
	
	double dialog_width=950;
	double dialog_height=635;
	
	VBox vBox = new VBox(1);

	TabPollutantTaxCap pollTaxCapTab;
	//TabTechSectorShare techSectorShareTab;
	TabMarketShare techMarketShareTab;

	TabTechBound techBoundTab;
	TabTechAvailable techAvailTab;
	TabFixedDemand fixedDemandTab;
	TabTechParam techParamTab;
	TabTechTax techTaxTab;
	TabFuelPriceAdj fuelPriceAdjTab;
	TabCafeStd cafeStdTab;
	TabXMLList xmlListTab;
	PolicyTab tab;
	
	ProgressBar progress_bar; 

	HBox hBoxButton = new HBox(1);
	
	Button buttonSaveComponent;
	Button buttonClose;
	
	Task task;
	Thread thread;
	
	Stage stageWithTabs;
	
	double progress=0.0;
	
	DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd: HH:mm", Locale.ENGLISH);

	public PaneNewScenarioComponent() {
		// Client.tableComponents = Client.tableComponents;
		vBox.setStyle(styles.font_style);
		// Buttons on the top left pane
		Client.buttonNewComponent = utils.createButton("New", styles.bigButtonWid,
				"New: Open dialog to create new scenario component", "add");
		
		Client.buttonEditComponent = utils.createButton("Edit", styles.bigButtonWid,
				"Edit: Edit selected scenario component", "edit");
		Client.buttonEditComponent.setDisable(true);

		Client.buttonBrowseComponentLibrary = utils.createButton("Browse", styles.bigButtonWid,
				"Browse: Open scenario component library folder", "open_folder");
		
		Client.buttonDeleteComponent = utils.createButton("Delete", styles.bigButtonWid,
				"Delete: Remove selected scenario component", "delete");
		Client.buttonDeleteComponent.setDisable(true);
		
		Client.buttonRefreshComponents = utils.createButton("Refresh", styles.bigButtonWid,
				"Refresh: Reload list of candidate scenario components", "refresh");

		try {
			refreshComponentLibraryTable();
		} catch (Exception e) {
			utils.warningMessage("Problem loading scenario component files.");
			System.out.println("Error loading scenario component files from:");
			System.out.println("    " + vars.get("scenarioComponentsDir"));
			System.out.println("Error: " + e);
			utils.exitOnException();
		}
		//System.out.println("tableComponents " + ComponentLibraryTable.tableComponents.getItems().size());
		ComponentLibraryTable.tableComponents.setOnMouseClicked(e -> {
			setArrowAndButtonStatus();
		});

		//System.out.println("buttonDeleteComponent " + Client.buttonDeleteComponent.isVisible());
		Client.buttonDeleteComponent.setDisable(true);

		// what happens when button new is clicked
		Client.buttonNewComponent.setOnAction(e -> {
			showComponentDialog(null, null, Client.primaryStage, null,null);
		});
		
		// what happens when button new is clicked
		Client.buttonEditComponent.setOnAction(e -> {
			ObservableList<ComponentRow> selectedFiles = ComponentLibraryTable.tableComponents.getSelectionModel()
					.getSelectedItems();
			String which=null;
			if (selectedFiles.size()!=1) {
				//System.out.println("Editing requires exactly one scenario component to be selected.");
				utils.showInformationDialog("Information","Unsupported action", "Editing requires exactly one scenario component to be selected.");
				return;
			} 
			
			String component_to_edit=selectedFiles.get(0).getAddress();
			System.out.println("Editing component "+component_to_edit);
			
			if (component_to_edit.toLowerCase().endsWith(".xml")) {
				String filename_to_edit=vars.getScenarioComponentsDir()+File.separator+component_to_edit;
				files.showFileInXmlEditor(filename_to_edit);
			} else {
			
			String tabType=null;
			
			ArrayList<String> contents=files.getStringArrayFromFile(component_to_edit,null);

			if (contents.size()>1) {
				String first_line=contents.get(0);
				if (first_line.indexOf("xmllist")>-1) {
					tabType="XML List";
				}
			}
			if (tabType==null) {
			  for (int i=0;i<contents.size();i++){
				String line=contents.get(i);
				if (line.startsWith("#Scenario component type:")){
					tabType=line.substring(line.indexOf(":")+1).trim();
				}
			  }
			}	
			
			showComponentDialog(null, null, Client.primaryStage, tabType, contents);
			}
		});



		// what happens when button new is clicked
		Client.buttonRefreshComponents.setOnAction(e -> {
			refreshComponentLibraryTable();
		});


		// what happens when button delete is clicked
		Client.buttonBrowseComponentLibrary.setOnAction(e -> {

			try {
					String scen_dir = vars.getScenarioComponentsDir();
					files.openFileExplorer(scen_dir);
			} catch (Exception e1) {
				// to-do: Currently messy handling of exceptions.
				e1.printStackTrace();
				utils.exitOnException();
			}
		});
		
		// what happens when button delete is clicked
		Client.buttonDeleteComponent.setOnAction(e -> {


			ObservableList<ComponentRow> selectedFiles = ComponentLibraryTable.tableComponents.getSelectionModel()
					.getSelectedItems();

			if (checkIfComponentsAreUsed(selectedFiles)) {
				String msg="Cannot delete selected scenario component since it is used in a scenario.";
				utils.warningMessage(msg);
				return;
			}
				
			if (!utils.confirmDelete())
				return;
			
			
			for (int i = 0; i < selectedFiles.size(); i++) {
				
				String componentFilename = selectedFiles.get(i).getAddress();
				String trashFilename = selectedFiles.get(i).getFileName();
				if (trashFilename.indexOf(File.separator)>0) trashFilename=trashFilename.substring(trashFilename.lastIndexOf(File.separator)+1);
				String trashpathname = vars.get("trashDir") + File.separator + trashFilename;
				
				try {
					Files.move(Paths.get(componentFilename), Paths.get(trashpathname),
							StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception e1) {
					utils.warningMessage("Problem moving file "+componentFilename+" to trash");
					System.out.println("error:" + e1);
					utils.exitOnException();
				}
			}

			ComponentLibraryTable.removeFromListOfFiles(selectedFiles);

		});
			
		vBox.getChildren().addAll(ComponentLibraryTable.tableComponents);
		vBox.prefWidthProperty().bind(Client.primaryStage.widthProperty().multiply(4.0 / 7.0));

	}
	
	private boolean checkIfComponentsAreUsed(ObservableList<ComponentRow> selectedFiles) {
		boolean b=false;
		
		//todo: add logic to check to see if items for deletion exist in any of the scenarios in the scenario library
		ObservableList<ScenarioRow> scenario_library=ScenarioTable.listOfScenarioRuns;
		for (int s=0;s<scenario_library.size();s++) {
			if (!b) {
			ScenarioRow scenario=scenario_library.get(s);
			String components=scenario.getComponents();
			if (components.length()>0) {
			for (int c=0;c<selectedFiles.size();c++) {
				String file_to_delete=selectedFiles.get(c).getFileName();
				if (components.indexOf(file_to_delete)>-1) b=true;
				break;
			}
			}
			}
		}
		
		return b;
	}

	private void showComponentDialog(String name, String filename, Stage mainStage, String whichTab, ArrayList<String> contentToLoad) {
		stageWithTabs = new Stage();

		// Initializing components of button area
		HBox hBoxButtons = new HBox();
		buttonSaveComponent = utils.createButton("Save", styles.bigButtonWid, null);
		buttonClose = utils.createButton("Close", styles.bigButtonWid, null);
		
		hBoxButtons.getChildren().addAll(buttonSaveComponent, buttonClose);
		hBoxButtons.setStyle(styles.style4);
		hBoxButtons.setSpacing(5.);
		hBoxButtons.setAlignment(javafx.geometry.Pos.CENTER);

		// Status Bar area
		progress_bar=new ProgressBar(0.0);

		progress_bar.setPrefWidth(dialog_width - 25);
		HBox hBoxProgress = new HBox();
		hBoxProgress.setAlignment(javafx.geometry.Pos.CENTER);
		hBoxProgress.getChildren().add(progress_bar);
		
		
		xmlListTab = new TabXMLList("XML List", stageWithTabs, ComponentLibraryTable.tableComponents);
		xmlListTab.setClosable(false);
		pollTaxCapTab = new TabPollutantTaxCap("Pollutant Tax/Cap", stageWithTabs);
		pollTaxCapTab.setClosable(false);
		fuelPriceAdjTab = new TabFuelPriceAdj("Fuel Price Adj", stageWithTabs);
		fuelPriceAdjTab.setClosable(false);
		//note: sector share has been replaced by market share
		//techSectorShareTab = new TabTechSectorShare("Sector Share", stageWithTabs);
		//techSectorShareTab.setClosable(false);
		techMarketShareTab = new TabMarketShare("Market Share", stageWithTabs, this);
		techMarketShareTab.setClosable(false);
		techBoundTab = new TabTechBound("Tech Bound", stageWithTabs);
		techBoundTab.setClosable(false);
		cafeStdTab = new TabCafeStd("MPG Target", stageWithTabs);
		cafeStdTab.setClosable(false);
		techAvailTab = new TabTechAvailable("Tech Avail", stageWithTabs);
		techAvailTab.setClosable(false);
		techParamTab = new TabTechParam("Tech Param", stageWithTabs);
		techParamTab.setClosable(false);
		techTaxTab = new TabTechTax("Tech Tax/Subsidy", stageWithTabs);
		techTaxTab.setClosable(false);
		fixedDemandTab = new TabFixedDemand("Fixed Demand",stageWithTabs);
		fixedDemandTab.setClosable(false);
		
		TabPane addComponentTabPane = new TabPane();
		//addComponentTabPane.setSide(Side.LEFT);
		//addComponentTabPane.setRotateGraphic(true);
		

		//hiding techParamTab for now until more testing
		addComponentTabPane.getTabs().addAll(xmlListTab,pollTaxCapTab,techAvailTab,techMarketShareTab,techBoundTab,techTaxTab,cafeStdTab,techParamTab,fuelPriceAdjTab,fixedDemandTab);
		
		addComponentTabPane.setStyle(styles.style1b);
		addComponentTabPane.setPrefHeight(dialog_height - 25);

		VBox dialogPane = new VBox();
		

		dialogPane.getChildren().addAll(addComponentTabPane, hBoxProgress, hBoxButtons);

		stageWithTabs.initOwner(mainStage);
		stageWithTabs.initModality(APPLICATION_MODAL);

		stageWithTabs.setScene(new Scene(dialogPane, dialog_width, dialog_height));

		stageWithTabs.setTitle("New Scenario Component Creator");
		
		stageWithTabs.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				if (task!=null) task.cancel();
				if (thread!=null) thread.stop();
			}
		});

		buttonClose.setOnAction(e -> stageWithTabs.close());

		buttonSaveComponent.setOnAction(e -> {
						
			  String which = addComponentTabPane.getSelectionModel().getSelectedItem().getText();

			  if (which.equals("Market Share")) tab=techMarketShareTab;
			  if (which.equals("Flex Share")) tab=techMarketShareTab;
			  //if (which.equals("Sector Share")) tab=techSectorShareTab;
			  if (which.equals("MPG Target")) tab=this.cafeStdTab;
			  if (which.equals("Tech Bound")) tab=this.techBoundTab;
			  if (which.equals("Tech Avail")) tab=this.techAvailTab;
			  if (which.equals("Tech Param")) tab=this.techParamTab;
			  if (which.equals("Tech Tax/Subsidy")) tab=this.techTaxTab;
			  if (which.equals("XML List")) tab=this.xmlListTab;
			  if (which.equals("Pollutant Tax/Cap")) tab=this.pollTaxCapTab;
			  if (which.equals("Fuel Price Adj")) tab=this.fuelPriceAdjTab;
			  if (which.equals("Fixed Demand")) tab=this.fixedDemandTab;
			  progress_bar.progressProperty().bind(tab.progress_bar.progressProperty());
			  
		  task = new Task<Integer>() {
	
		  
			 @Override public Integer call() throws Exception {
				 tab.saveScenarioComponent();
				 return 1;
			 }
			 
			 @Override protected void succeeded() {
			      super.succeeded();
			      System.out.println("Done!");
			      //Platform.runLater(()->saveComponentFile(tab));
			      saveComponentFile(tab);
			 }

			 @Override protected void cancelled() {
				 super.cancelled();
				 System.out.println("Cancelled!");
				 utils.warningMessage("Process of building scenario component cancelled.");
				 enableButtons();
				 tab.resetFileContent();
				 tab.resetFilenameSuggestion();
				 tab.resetProgressBar();			     
			 }

			@Override protected void failed() {
			    super.failed();
			    System.out.println("Failed!");
			    utils.warningMessage("Process of building scenario component failed.");
				enableButtons();
				tab.resetFileContent();
				tab.resetFilenameSuggestion();
				tab.resetProgressBar();
			}			 			 
		  };
		  
		  thread=new Thread(task);
		  thread.setDaemon(false);
		  thread.start();
			

		  disableButtons();
	    });		
		
		if (whichTab!=null) { 
			selectTabAndLoadContent(whichTab,contentToLoad,addComponentTabPane);
		}
			
		stageWithTabs.setResizable(false);
		stageWithTabs.show();
	}
	
	private void selectTabAndLoadContent(String whichTab,ArrayList<String> contentToLoad,TabPane tp) {
		ObservableList<Tab> tabs=tp.getTabs();
		//Tab tab=null;
		if (whichTab.equals("Flex Share")) whichTab="Market Share";
		for (int i=0;i<tabs.size();i++) {
			PolicyTab tempTab=(PolicyTab) tabs.get(i);
			if (tempTab.getText().equals(whichTab)) {
				tab=tempTab;
				tab.loadContent(contentToLoad);
				tp.getSelectionModel().select(tab);
				break;
			}
		}
	}
	
	
	
	
	public void saveComponentFile(PolicyTab tab) {
		String filename_suggestion=tab.getFilenameSuggestion();
		String file_content=tab.getFileContent();
		boolean use_temp_file=false;
		if (file_content.equals("use temp file")) {
			use_temp_file=true;
		}
		
		if ((filename_suggestion!=null)||(!filename_suggestion.equals(""))) {
			// opens the browser for saving
			enableButtons();
			tab.resetFileContent();
			tab.resetFilenameSuggestion();
			tab.resetProgressBar();
			
			String filter1="CSV files (*.csv)";
			String filter2="*.csv";
			
			if (file_content.indexOf("xmllist")>=0) {
				filter1="TXT files (*.txt)";
				filter2="*.txt";
			}
			
			File file = FileChooserPlus.main(filter1,filter2, vars.get("scenarioComponentsDir"),
					filename_suggestion, "Save");

			if (file == null)
				return;
			if (!use_temp_file) {
				System.out.println("Attempting to save file_content... "+file_content.length()+" characters");
			    files.saveFile(file_content, file);
			    System.out.println("Done.");
			} else {
				String temp_policy_filename=vars.getGlimpseDir()+File.separator+"GLIMPSE-Data"+File.separator+"temp"+File.separator+"temp_policy_file.txt";
				try {
					Files.move(Paths.get(temp_policy_filename), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch(Exception e) {
					System.out.println("Error creating policy file: "+e);
				}
			}
			
			ComponentRow p1 = new ComponentRow(file.getName(), file.getPath(), new Date());
			ComponentRow[] fileArr = { p1 };

			ComponentLibraryTable.addToListOfFiles(fileArr);					
		}
		
		
		refreshComponentLibraryTable();
		
	}
	
	public void disableButtons() {
		buttonSaveComponent.setDisable(true);
		buttonClose.setDisable(true);
	}
	
	public void enableButtons() {
		buttonSaveComponent.setDisable(false);
		buttonClose.setDisable(false);
	}

	// called from TabIncludeXMLList
	public void refreshComponentLibraryTable() {

		File folder = new File(vars.get("scenarioComponentsDir"));
		
		ArrayList<File> fileList=new ArrayList<File>();		
		fileList=buildFileList(folder.toPath());
		ComponentRow[] fileArr = new ComponentRow[fileList.size()];
		
		int k = 0;
		for (int i = 0; i < fileList.size(); i++) {
			String relative_name=files.getRelativePath(folder.toString(), fileList.get(i).getAbsolutePath());
			ComponentRow p1 = new ComponentRow(relative_name, fileList.get(i).getPath(),
					new Date(fileList.get(i).lastModified()));
			fileArr[k] = p1;
			k++;	
		}

		ComponentLibraryTable.createListOfFiles(fileArr);
	}


	
	public ArrayList<File> buildFileList(Path path){
		ArrayList<File> rtn_array=new ArrayList<File>();
		File root=path.toFile();
		File[] list=root.listFiles();

        if (list == null) return rtn_array;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
                rtn_array.addAll(buildFileList( f.toPath()));

            }
            else {

                rtn_array.add(f);
            }
        }
        return rtn_array;
	}
	

	private void loadFile(List<File> file) {
		int k = 0;
		ComponentRow[] fileArr = new ComponentRow[file.size()];
		for (File i : file) {
			ComponentRow p1 = new ComponentRow(i.getName(), i.getPath(), new Date(i.lastModified()));
			fileArr[k] = p1;
			k++;
		}
		ComponentLibraryTable.addToListOfFiles(fileArr);
	}

	public VBox getvBox() {
		return vBox;
	}

}
