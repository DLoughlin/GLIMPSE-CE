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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import ModelInterface.InterfaceMain;
import glimpseElement.ScenarioRow;
import glimpseElement.ScenarioTable;
import glimpseUtil.FileChooserPlus;
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

//import ModelInterface.InterfaceMain;
//import ModelInterface.*;

// //////////////////////////////////////////////////////////////////////////////////
// this class generates objects of the lower pane of application where
// historical run records are demonstrated
// /////////////////////////////////////////////////////////////////////////////////////////
class PaneScenarioLibrary extends ScenarioBuilder {

	private GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	private GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	private GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	private GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

	ArrayList<String> runs_queued_list = new ArrayList<String>();
	ArrayList<String> runs_completed_list = new ArrayList<String>();
	
	long time_at_startup=0;

	HBox hBox = new HBox(1);

	// Constructor
	PaneScenarioLibrary(Stage stage) {

		hBox.setStyle(styles.font_style);

		hBox.setSpacing(10);
		// sets up whether buttons are disabled or not and how this is
		// changed
		ScenarioTable.tableScenariosLibrary.setOnMouseClicked(e -> {
			setArrowAndButtonStatus();
		});

		// Buttons on the bottom pane
		createScenarioLibraryButtons();

		ScenarioTable.tableScenariosLibrary.prefWidthProperty().bind(stage.widthProperty().multiply(1.0));
		ScenarioTable.tableScenariosLibrary.prefHeightProperty().bind(stage.heightProperty().multiply(0.7));

		hBox.getChildren().addAll(ScenarioTable.tableScenariosLibrary);

		if (time_at_startup==0) time_at_startup=(new Date()).getTime();
		System.out.println("time now="+(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")).format(time_at_startup));
		
		this.updateRunStatus();

	}

	PaneScenarioLibrary() {

	}

	private void createScenarioLibraryButtons() {
		// Creating buttons on the bottom pane
		Client.buttonDiffFiles = utils.createButton("Diff", styles.bigButtonWid,
				"Diff: Compare first two selected configurations", "compare");

		Client.buttonRefreshScenarioStatus = utils.createButton("Refresh", styles.bigButtonWid,
				"Refresh: Update scenario completion status", "refresh");

		Client.buttonResults = utils.createButton("Results", styles.bigButtonWid,
				"Results: Open the ModelInterface to view results", "results");

		Client.buttonResultsForSelected = utils.createButton("Results (selected)", styles.bigButtonWid,
				"Results-Selected: Open the ModelInterface to view results for selected scenario", "results-selected");

		Client.buttonRunScenario = utils.createButton("Play", styles.bigButtonWid,
				"Play: Add the selected scenarios to execution queue", "run");

		Client.buttonDeleteScenario = utils.createButton("Delete", styles.bigButtonWid,
				"Delete: Move the selected scenarios to trash", "delete");

		Client.buttonViewConfig = utils.createButton("Config", styles.bigButtonWid,
				"Open: Open configuration file for selected scenario", "edit");

		Client.buttonViewLog = utils.createButton("Log", styles.bigButtonWid, 
				"Main_Log-Selected: View main_log.txt in selected scenario folder","log2");

		Client.buttonViewExeErrors = utils.createButton("ExeError", styles.bigButtonWid,
				"Errors: View errors in main_log.txt file in exe/log folder", "exe-errors");
		
		Client.buttonViewErrors = utils.createButton("Errors", styles.bigButtonWid,
				"Errors-Selected: View errors in selected scenario main_log.txt file", "errors");

		Client.buttonViewExeLog = utils.createButton("ExeLog", styles.bigButtonWid, 
				"Main_Log: View main_log.txt in the ee/log folder","exe-log");

		Client.buttonBrowseScenarioFolder = utils.createButton("Browse", styles.bigButtonWid,
				"Browse: Open the folder of the selected scenarios", "open_folder");

		Client.buttonImportScenario = utils.createButton("Import", styles.bigButtonWid,
				"Import: Import an existing configuration file to create new scenario", "import");

		Client.buttonShowRunQueue = utils.createButton("Queue", styles.bigButtonWid,
				"Queue: List scenarios added to queue this session", "queue");

		Client.buttonArchiveScenario = utils.createButton("Archive", styles.bigButtonWid, 
				"Archive: Archive the selected scenarios","archive");

		Client.buttonReport = utils.createButton("Report", styles.bigButtonWid,
				"Report: Generate scenario execution report", "report");

		// setting initial button status
		Client.buttonRunScenario.setDisable(true);
		Client.buttonBrowseScenarioFolder.setDisable(true);
		Client.buttonImportScenario.setDisable(false);
		Client.buttonArchiveScenario.setDisable(true);
		Client.buttonDeleteScenario.setDisable(true);
		Client.buttonResultsForSelected.setDisable(true);
		Client.buttonViewConfig.setDisable(true);
		Client.buttonDiffFiles.setDisable(true);
		Client.buttonViewLog.setDisable(true);
		Client.buttonViewExeErrors.setDisable(false);
		Client.buttonViewErrors.setDisable(true);
		Client.buttonViewExeLog.setDisable(false);
		Client.buttonReport.setDisable(false);

		// call to check button status
		Client.buttonRefreshScenarioStatus.setOnAction(e -> {
			updateRunStatus();
			ScenarioTable.tableScenariosLibrary.refresh();
		});

		Client.buttonReport.setOnAction(e -> {
			generateRunReport();
		});

		// Iterates through the selected items in the bottom pane, running
		// each in GCAM
		Client.buttonRunScenario.setOnAction(e -> {

			try {
				runGcamOnSelected();
			} catch (Exception ex) {
				utils.warningMessage("Problem running GCAM.");
				System.out.println("Error trying to run GCAM.");
				System.out.println("Error: " + ex);
				utils.exitOnException();
			}
			// updates the run table file
			this.updateRunStatus();
		});

		Client.buttonArchiveScenario.setOnAction(e -> {
			if (!utils.confirmArchiveScenario()) {
				return;
			}

			ObservableList<ScenarioRow> selectedFiles2 = ScenarioTable.tableScenariosLibrary.getSelectionModel()
					.getSelectedItems();

			try {
				for (int i = 0; i < selectedFiles2.size(); i++) {
					String scenName = selectedFiles2.get(i).getScenarioName();
					String workingDir = vars.get("scenarioDir") + "/" + scenName;
					String exeDir = vars.get("gCamExecutableDir");
					String configFilename = workingDir + "/configuration_" + scenName + ".xml";
					String archiveConfigFilename = workingDir + "/configuration_" + scenName + "_archive.xml";

					archiveScenario(exeDir, workingDir, archiveConfigFilename, configFilename, scenName);

				}
			} catch (Exception e1) {
				utils.warningMessage("Problem archiving scenario files.");
				System.out.println("Error trying to archive scenario xml files");
				System.out.println("error: " + e1);
				utils.exitOnException();
			}

		});

		// deletes run record from the table
		Client.buttonDeleteScenario.setOnAction(e -> {

			// gets confirmation from the user
			if (!utils.confirmDelete())
				return;

			ObservableList<ScenarioRow> selectedFiles2 = ScenarioTable.tableScenariosLibrary.getSelectionModel()
					.getSelectedItems();

			try {
				for (int i = 0; i < selectedFiles2.size(); i++) {
					String scenName = selectedFiles2.get(i).getScenarioName();
					String xml_dir = vars.get("scenarioDir") + File.separator + scenName;

					String trash_dir_folder = vars.get("trashDir") + File.separator + scenName;
					File trashDir = new File(trash_dir_folder);

					if (trashDir.exists())
						files.deleteDirectoryStream((trashDir.toPath()));

					if (!trashDir.exists()) {
						trashDir.mkdirs();
					}

					// Moves each deleted file to a trash folder
					Files.move(Paths.get(xml_dir), Paths.get(trash_dir_folder), StandardCopyOption.REPLACE_EXISTING);

				}
			} catch (Exception e1) {
				utils.warningMessage("Problem deleting scenario(s)");
				System.out.println("error: " + e1);
				utils.exitOnException();
			}

			// updates list of runs in the bottom pane table
			ScenarioTable.removeFromListOfRunFiles(selectedFiles2);

			// updates xml file with list of GCAM runs
			// writeRunsTxtFile();

		});

		// handles the event of pressing the results button
		Client.buttonResults.setOnAction(e -> {

			if (vars.get("gCamExecutableDir").equals("")) {

				utils.warningMessage("Please specify gCamExecutableDir in options file.");

			} else {

				try {
//					runGcamPostproc(vars.get("gCamExecutableDir"));
					runORDModelInterface();
					// writeRunsTxtFile();
				} catch (Exception e1) {
					// to-do: Currently messy handling of exceptions.
					e1.printStackTrace();
					utils.exitOnException();
				}
			}
		});

		// handles the event of pressing the results button
		Client.buttonResultsForSelected.setOnAction(e -> {

			if (vars.get("gCamExecutableDir").equals("")) {

				utils.warningMessage("Please specify gCamExecutableDir in options file.");

			} else {

				try {
					// get selected scenario name
					ObservableList<ScenarioRow> selectedFiles = ScenarioTable.tableScenariosLibrary.getSelectionModel()
							.getSelectedItems();
					if (selectedFiles.size() == 1) {

						String scenName = selectedFiles.get(0).getScenarioName();
						String config_filename = vars.get("scenarioDir") + File.separator + scenName + File.separator
								+ "configuration_" + scenName + ".xml";
						System.out.println("Reading database name from " + config_filename);
						File config_file = new File(config_filename);
						String database_line = files.searchForTextInFileS(config_file, "xmldb-location", "#");
						String database_name = utils.getStringBetweenCharSequences(database_line, ">", "</");
						System.out.println("Database name: " + database_name);
						String updated_name = files.getResolvedPath(vars.getgCamExecutableDir(), database_name);
						System.out.println("database to open: " + updated_name);
						runORDModelInterfaceWhich(updated_name);

						// writeRunsTxtFile();
					}
				} catch (Exception e1) {
					// to-do: Currently messy handling of exceptions.
					e1.printStackTrace();
					utils.exitOnException();
				}
			}
		});

		// handles the event of pressing the browse button
		Client.buttonBrowseScenarioFolder.setOnAction(e -> {

			ObservableList<ScenarioRow> selectedFiles = ScenarioTable.tableScenariosLibrary.getSelectionModel()
					.getSelectedItems();

			try {
				for (int i = 0; i < selectedFiles.size(); i++) {
					String scenName = selectedFiles.get(i).getScenarioName();
					String xml_dir = vars.get("scenarioDir") + File.separator + scenName;
					files.openFileExplorer(xml_dir);
				}
			} catch (Exception e1) {
				// to-do: Currently messy handling of exceptions.
				e1.printStackTrace();
				utils.exitOnException();
			}
		});

		// handles the event of pressing the browse button
		Client.buttonImportScenario.setOnAction(e -> {
			System.out.println("Todo: add callback to buttonImport");

			String exe_folder = vars.getgCamExecutableDir();
			File new_config_file = FileChooserPlus.main("Scenario files (configuration*.xml)", "configuration*.xml",
					exe_folder, "Open");
			if (new_config_file != null) {
				String str = files.searchForTextInFileS(new_config_file, "scenarioName", "<!--");

				String scenario_name = utils.getStringBetweenCharSequences(str, ">", "</");

				String working_scenario_log = vars.get("gCamGUILogDir") + File.separator + "Runs.txt";

				File working_scenarios_file = new File(working_scenario_log);

				// does this scenario already exist in scenario list? If so, give warning and
				// don't do it
				boolean doesScenarioExist = files.searchForTextAtStartOfLinesInFile(working_scenarios_file,
						scenario_name + ",", "#");

				if (doesScenarioExist) {
					// if not, open confirm dialog to create new scenario folder
					String s = "Overwrite existing scenario " + scenario_name + "?";
					if (!utils.confirmAction(s))
						return;
				} else {
					// if so, then create new folder with name being the scenario name
					String s = "Import " + scenario_name + " into GLIMPSE?";
					if (!utils.confirmAction(s))
						return;
				}

				// make folder for scenario
				String new_scen_folder_name = vars.get("scenarioDir") + File.separator + scenario_name;
				File new_scen_folder = new File(new_scen_folder_name);

				new_scen_folder.mkdir();
				String new_scen_filename = new_scen_folder + File.separator + "configuration_" + scenario_name + ".xml";

				// copy configuration file to that folder
				files.copyFile(new_config_file.getAbsolutePath(), new_scen_filename);

				// create new scenario row
				ScenarioRow sr = new ScenarioRow(scenario_name);
				sr.setComponents("Externally-created scenario");
				sr.setCreatedDate(new Date());
				sr.setStatus("No");
				ScenarioRow[] newRun = { sr };

				// add scenario to working folder list
				ScenarioTable.addToListOfRunFiles(newRun);
				// writeRunsTxtFile();
			}
		});

		// handles the event of pressing the ViewConfig button
		Client.buttonViewConfig.setOnAction(e -> {
			// to-do: modify to use user-specified xml viewer
			ObservableList<ScenarioRow> selectedFiles = ScenarioTable.tableScenariosLibrary.getSelectionModel()
					.getSelectedItems();

			try {
				for (int i = 0; i < selectedFiles.size(); i++) {
					String scenName = selectedFiles.get(i).getScenarioName();
					String xml_file = vars.get("scenarioDir") + File.separator + scenName + File.separator
							+ "configuration_" + scenName + ".xml";
					System.out.println("File to show: " + xml_file);
					files.showFileInTextEditor(xml_file);
				}
			} catch (Exception e1) {
				// to-do: Currently messy handling of exceptions.
				e1.printStackTrace();
				utils.exitOnException();
			}
		});

		// handles the event of pressing the ViewConfig button
		Client.buttonViewLog.setOnAction(e -> {
			// to-do: modify to use user-specified xml viewer
			ObservableList<ScenarioRow> selectedFiles = ScenarioTable.tableScenariosLibrary.getSelectionModel()
					.getSelectedItems();

			try {
				if (selectedFiles.size() > 0) {
					for (int i = 0; i < selectedFiles.size(); i++) {
						String scenName = selectedFiles.get(i).getScenarioName();
						String txt_file = vars.get("scenarioDir") + "/" + scenName + "/main_log.txt";
						files.showFileInTextEditor(txt_file);
					}
				}
			} catch (Exception e1) {
				// to-do: Currently messy handling of exceptions.
				e1.printStackTrace();
				utils.exitOnException();
			}
		});

		// handles the event of pressing the ViewConfig button
		Client.buttonViewExeErrors.setOnAction(e -> {
			// to-do: modify to use user-specified xml viewer
			generateExeErrorReport();
		});
		
		// handles the event of pressing the ViewConfig button
		Client.buttonViewErrors.setOnAction(e -> {
			// to-do: modify to use user-specified xml viewer
			generateErrorReport();
		});

		// handles the event of pressing the ViewLogExe button
		Client.buttonViewExeLog.setOnAction(e -> {

			try {
				String filename = vars.getgCamExecutableDir() + File.separator + "logs" + File.separator
						+ "main_log.txt";
				files.showFileInTextEditor(filename);
			} catch (Exception e1) {
				// to-do: Currently messy handling of exceptions.
				e1.printStackTrace();
				utils.exitOnException();
			}
		});

		Client.buttonDiffFiles.setOnAction(e -> {
			// get selected items from working scenario list
			ObservableList<ScenarioRow> selectedFiles2 = ScenarioTable.tableScenariosLibrary.getSelectionModel()
					.getSelectedItems();
			if (selectedFiles2.size() == 2) {
				String sName1 = selectedFiles2.get(0).getScenarioName();
				String sName2 = selectedFiles2.get(1).getScenarioName();
				String file1 = vars.get("scenarioDir") + File.separator + sName1 + File.separator + "configuration"
						+ "_" + sName1 + ".xml";
				String file2 = vars.get("scenarioDir") + File.separator + sName2 + File.separator + "configuration"
						+ "_" + sName2 + ".xml";
				utils.diffTwoFiles(file1, file2);
			}
		});

		Client.buttonShowRunQueue.setOnAction(e -> {
			// get selected items from working scenario list
			// String txt = Client.runQueueStr;
			// ArrayList<String> txt_array = utils.createArrayListFromString(txt);
			ArrayList<String> txt_array = createSimpleQueueRpt();
			utils.displayArrayList(txt_array, "Run Queue");
		});

		// alignment
		Client.buttonResults.setAlignment(Pos.CENTER);
		Client.buttonResultsForSelected.setAlignment(Pos.CENTER);
		Client.buttonRunScenario.setAlignment(Pos.CENTER);
		Client.buttonDeleteScenario.setAlignment(Pos.CENTER);

		// Sets initial button visibility
		// setting initial button visibility
		Client.buttonRunScenario.setVisible(true);
		Client.buttonBrowseScenarioFolder.setVisible(true);
		Client.buttonImportScenario.setVisible(true);
		Client.buttonArchiveScenario.setVisible(false);
		Client.buttonDeleteScenario.setVisible(true);
		Client.buttonViewConfig.setVisible(true);
		Client.buttonDiffFiles.setVisible(true);
		Client.buttonViewLog.setVisible(true);
		Client.buttonViewExeLog.setVisible(true);
		Client.buttonReport.setVisible(true);
	}

	protected ArrayList<String> createSimpleQueueRpt() {
		ArrayList<String> rtnArray = new ArrayList<String>();

		rtnArray.add("Note: Includes only runs added to the queue since the start of this session.");

		if (this.runs_queued_list.size() > 0) {
			rtnArray.add("---");
			rtnArray.add("In queue:");
			for (int i = 0; i < runs_queued_list.size(); i++) {
				rtnArray.add(runs_queued_list.get(i));
			}
		}

		if (this.runs_completed_list.size() > 0) {
			rtnArray.add("---");
			rtnArray.add("Completed:");
			for (int i = 0; i < runs_completed_list.size(); i++) {
				rtnArray.add(runs_completed_list.get(i));
			}
		}

		return rtnArray;
	}

	protected ArrayList<String> createFancyQueueRpt(ArrayList<String> run_queue) {
		ArrayList<String> rtnArray = new ArrayList<String>();
		rtnArray.add("Note: Includes only runs added to the queue since the start of this session.");
		ArrayList<String> completedArray = new ArrayList<String>();
		completedArray.add("===");
		completedArray.add("Completed successfully:");
		ArrayList<String> issuesArray = new ArrayList<String>();
		issuesArray.add("---");
		issuesArray.add("Not completed successfully (w/Issues):");
		ArrayList<String> notCompletedArray = new ArrayList<String>();
		notCompletedArray.add("---");
		notCompletedArray.add("Running or still in queue:");

		ObservableList<ScenarioRow> allRuns = ScenarioTable.tableScenariosLibrary.getItems();
		for (int j = 0; j < allRuns.size(); j++) {
			ScenarioRow scenRow = allRuns.get(j);
			String scenName = scenRow.getScenarioName();
			String searchText = File.separator + scenName + File.separator;
			String isComplete = scenRow.getStatus();
			String runDate = "" + scenRow.getCreatedDate(); // or is it getCompletedDate
			boolean match = false;
			for (int i = 0; i < run_queue.size(); i++) {
				String run_in_queue = run_queue.get(i);
				if (run_in_queue.indexOf(searchText) > -1) {
					// run_in_queue+=" ("+isComplete+")"+vars.getEol();
					match = true;
				}
				if (match) {
					if ((isComplete.equals("Success")) || (isComplete.equals("Unsolved mkts"))) {
						completedArray.add(run_in_queue);
					} else if (isComplete.equals("")) {
						if ((runDate != null) || (runDate != "")) {
							notCompletedArray.add(run_in_queue);
						}
					} else if (isComplete.equals("DNF")) {
						issuesArray.add(run_in_queue);
					} else if (isComplete.equals("Running")) {
						run_in_queue += " (Running)";
						notCompletedArray.add(run_in_queue);
					}
					break;
				}
			}
		}
		rtnArray.addAll(completedArray);
		rtnArray.addAll(issuesArray);
		rtnArray.addAll(notCompletedArray);
		return rtnArray;
	}

	// called from PaneCreateScenario
	void deleteItemFromScenarioLibrary(String nameToDelete) {
		// This is not currently working
		ObservableList<ScenarioRow> allScenariosList = ScenarioTable.tableScenariosLibrary.getItems();
		ObservableList<ScenarioRow> deleteScenariosList = FXCollections.observableArrayList();

		for (Iterator<ScenarioRow> it = allScenariosList.iterator(); it.hasNext();) {
			// try {
			ScenarioRow mfr = it.next();

			if (mfr.getScenarioName().equals(nameToDelete)) {
				deleteScenariosList.add(mfr);

			}
		}

		ScenarioTable.removeFromListOfRunFiles(deleteScenariosList);

	}

	public void updateRunStatus() {
		//System.out.println("checking status...");
		// loads run data and populates the bottom pane table
		String current_main_log_name = vars.getgCamExecutableDir() + File.separator + "logs" + File.separator
				+ "main_log.txt";
		File current_main_log_file = new File(current_main_log_name);
		

		String running_scenario = utils.getRunningScenario(current_main_log_file);

		// ScenarioTable.listOfScenarioRuns.clear();
		ScenarioTable.tableScenariosLibrary.refresh();

		String address = vars.get("gCamGUILogDir") + File.separator + "Runs.txt";
		DateFormat format = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
		DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd: HH:mm", Locale.ENGLISH);
		
		ArrayList<String> searchArray = new ArrayList<String>();



		Platform.runLater(new Runnable() {
			//String computer_stats=utils.getComputerStatString().trim();
		    @Override
		    public void run() {	
		    	String computer_stats=utils.getComputerStatString().trim();
		    	if (computer_stats.endsWith("!!!")) {
		    	    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
		    	    Date date = new Date();  		    		
		    	    String time=formatter.format(date);  
		    		String glimpse_log_filename = vars.getgCamGUILogDir() + File.separator + "glimpse_log.txt";
		    		String log_text=running_scenario+":"+time+":"+computer_stats+vars.getEol();
		    		files.appendTextToFile(log_text,glimpse_log_filename);
		    	}
		    	utils.sb.setText(computer_stats);
		    	if (computer_stats.endsWith("!")) { 
		    		utils.sb.setStyle("-fx-text-fill: red");
		    	} else {
		    		utils.sb.setStyle("-fx-text-fill: black");
		    	}

		    }
		});

		try {
			File[] scenarioFolders = new File(vars.getScenarioDir()).listFiles(File::isDirectory);

			for (int f = 0; f < scenarioFolders.length; f++) {
				
				searchArray.clear();
				searchArray.add("Model run completed.");
				searchArray.add("Data Readin, Model Run & Write Time:");
				searchArray.add("The following model periods did not solve:");
				
				Long created_date = 0l;
				Long started_date = 0l;
				Long completed_date = 0l;

				String scenario_name = scenarioFolders[f].getName();

				String config_name = scenarioFolders[f] + File.separator + "configuration_" + scenario_name + ".xml";
				File config_file = new File(config_name);
				boolean config_exists = config_file.exists();

				if (config_exists) {

					String components = getComponentsFromConfig(config_file);

					String main_log_name = scenarioFolders[f] + File.separator + "main_log.txt";
					File main_log_file = new File(main_log_name);
					boolean main_log_exists = main_log_file.exists();

					String status = "";
					String runtime = "";
					String unsolved = "";


					created_date = config_file.lastModified();

					if (main_log_exists) {

						completed_date = main_log_file.lastModified();
						searchArray = files.getMatchingTextArrayInFile(main_log_name, searchArray);

						if (searchArray.get(0) != "") {
							status = "Success";
						} else {
							status = "DNF";
							String running_status=utils.getScenarioStatusFromMainLog(main_log_file);
							if (running_status.contains(",ERR")) {
								String temp=running_status.substring(0,running_status.indexOf(",")); 
								String error_str=running_status.substring(running_status.indexOf(",")+4);								
								unsolved=error_str;
							} 					
						}
						for (int i = 0; i < this.runs_queued_list.size(); i++) {
							String line = runs_queued_list.get(i);
							if ((line.equals(config_name)) || (line.equals(scenario_name))) {
								status = "In queue";
								// attempting to remove items from queue. This may impact fancy queue report
								if (main_log_exists) {
									this.runs_completed_list.add(line);
									this.runs_queued_list.remove(i);
								}
								break;
							}
						}

					} else {
						status = "";
					}

					if (searchArray.get(1) != "") {
						try {
							runtime = searchArray.get(1).split(":")[1].trim();
						} catch (Exception e) {
							runtime = "";
						}
						runtime = runtime.replace("seconds.", "").trim();
						try {
							int totalSecs=(int)Math.round(Float.parseFloat(runtime));
						    int hours = (totalSecs - totalSecs%3600)/3600;
						    int minutes = (totalSecs%3600 - totalSecs%3600%60)/60;
						    int seconds = totalSecs%3600%60;
						    
						    runtime=hours+" hr "+minutes+" min ";
						    
						} catch(Exception e) {
							//System.out.println("Problem converting seconds to HMS");
							runtime+="";
						}
					}

					if (searchArray.get(2) != "") {
						try {
							unsolved = searchArray.get(2).split(":")[1].trim();
							status = "Unsolved mkts";
							
						} catch (Exception e) {
							unsolved = "";
						}
					}

					String created_date_str = "";
					String completed_date_str = "";

					if (created_date != 0L)
						created_date_str = format2.format(created_date);
					if (completed_date != 0L)
						completed_date_str = format2.format(completed_date);

					//what to do if it thinks scenario might still be running...
					if ((!status.equals("Success")) && (!status.equals("Unsolved mkts")) && (!status.equals("DNF"))) {
						//System.out.println("Evaluating status for "+scenario_name+" in Scenario Library.");
						if (scenario_name.equals(running_scenario)) {
							status = "Running";

							long last_date=current_main_log_file.lastModified();
							//System.out.println("last date="+(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")).format(last_date));
							if (last_date<time_at_startup) { 
								status="Lost handle";
									//files.copyFile(current_main_log_name,main_log_name);
									//updateRunStatus();
									//break;
							} else  {
								//adds last period and whether errors have occurred
								
								String running_status=utils.getScenarioStatusFromMainLog(current_main_log_file);
								if (running_status.contains(",ERR")) {
									String temp=running_status.substring(0,running_status.indexOf(",")); 
									status=status+"("+temp+")";
									
									String error_str=running_status.substring(running_status.indexOf(",")+4);								
									unsolved=error_str;
								} else {
									String temp=running_status; 
									if (temp=="") {
										;
									} else {
										status=status+"("+temp+")";
									}
								}

							}
						} else {
							for (int i = 0; i < this.runs_queued_list.size(); i++) {
								String line = runs_queued_list.get(i);
								if ((line.equals(config_name)) || (line.equals(scenario_name))) {
									status = "In queue";
									break;
								}
							}
						}

					}

					boolean match = false;
					for (int i = 0; i < ScenarioTable.listOfScenarioRuns.size(); i++) {
						ScenarioRow s = ScenarioTable.listOfScenarioRuns.get(i);
						if (s.getScenarioName().equals(scenario_name)) {
							match = true;
							s.setStatus(status);
							s.setCreatedDate(created_date_str);
							s.setCompletedDate(completed_date_str);
							s.setComponents(components);
							s.setRuntime(runtime);
							//s.setRuntime(String.format("%.1f", Float.parseFloat(runtime)));
							s.setUnsolvedMarkets(unsolved);
						}
					}
					if (!match) {

						ScenarioRow sr = new ScenarioRow(scenario_name);
						// sr.setScenName(scenario_name);
						sr.setComponents(components);
						sr.setCreatedDate(created_date_str);
						sr.setCompletedDate(completed_date_str);
						sr.setStatus(status);
						sr.setRuntime(runtime);
						sr.setUnsolvedMarkets(unsolved);

						ScenarioTable.listOfScenarioRuns.add(sr);
					}

				}
			}

			ScenarioTable.tableScenariosLibrary.refresh();

		} catch (Exception ex) {
			System.out.println("Problem updating scenario table: " + ex);
		}
	}

	

	private String getComponentsFromConfig(File file) {
		String rtn_str = "";

		try {
			Scanner fileScanner = new Scanner(file);
			boolean start_recording = false;
			boolean stop_recording = false;
			boolean has_meta_data = false;
			int count = 0;
			while ((fileScanner.hasNext()) && (stop_recording == false)) {
				String line = fileScanner.nextLine().trim();
				if (line.equals("##################### Scenario Meta Data #####################"))
					has_meta_data = true;
				if (line.equals("###############################################################"))
					stop_recording = true;
				if ((start_recording) && (line.length() > 0) && (!stop_recording)) {
					if (count == 0) {
						count++;
						rtn_str += line;
					} else {
						rtn_str += " ; " + line;
					}
				}
				if (line.equals("Components:"))
					start_recording = true;
				if (line.equals("<Files>"))
					stop_recording = true;
			}
			fileScanner.close();
			if (!has_meta_data) {
				rtn_str = "Externally-created scenario";
			}

		} catch (Exception e) {
			System.out.println("Problem reading components from " + file.getName() + ": " + e);
		}
		return rtn_str;
	}

	public HBox gethBox() {
		return hBox;
	}

	private void runGcamModel(String[] scenarioConfigFiles) throws IOException {
		System.out.println("Running scenarios in GCAM...");

		ArrayList<String> cmdList = new ArrayList<String>();

		for (int i = 0; i < scenarioConfigFiles.length; i++) {

			if (scenarioConfigFiles[i] != null) {
				//scenarioConfigFiles[i]=utils.correctInteriorQuotes(scenarioConfigFiles[i]);
				final String dir = scenarioConfigFiles[i]
						.substring(0, scenarioConfigFiles[i].lastIndexOf(File.separator))
						.replaceAll("/", File.separator);

				System.out.println("config: " + scenarioConfigFiles[i]);
				this.runs_queued_list.add(scenarioConfigFiles[i]);

				// deleting txt and log files from folder
				Client.gCAMExecutionThread.executeCallableCmd(new Callable<String>() {
					@Override
					public String call() throws Exception {
						System.out.println("Cleaning out folder.");
						String[] filesToDelete = vars.get("gCamOutputToSave").replaceAll("/", File.separator)
								.split(";");
						for (int j = 0; j < filesToDelete.length; j++) {
							String fileToDelete = "";

							fileToDelete = dir + File.separator
									+ filesToDelete[j].substring(filesToDelete[j].lastIndexOf(File.separator) + 1);
							//fileToDelete=utils.correctInteriorQuotes(fileToDelete);
							System.out.println(" Deleting " + fileToDelete);
							File file = new File(fileToDelete);

							if (file.exists()) {
								try {
									Path pathOfFileToDelete = Paths.get(fileToDelete);

									Files.delete(pathOfFileToDelete);
								} catch (Exception e1) {
									utils.warningMessage("Error deleting " + fileToDelete);
									System.out.println("Error deleting " + fileToDelete + ":" + e1);
								}
							}
						}
						return "txt and log files deleted from scenario folder";
					}
				});

				// submitting gcam run to the queue
				boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

				String cmd = "cmd.exe /C start";
				if (isWindows) {
					cmd = "cmd.exe /C start";
				} else {
					cmd = "/bin/sh -c";
				}

				
				String cmd_str = cmd + " " + vars.get("gCamExecutable") + " " + vars.get("gCamExecutableArgs") + " "
						+ scenarioConfigFiles[i];
				Future f = Client.gCAMExecutionThread.executeRunnableCmd(cmd_str, vars.get("gCamExecutableDir"));

				// replace by adding callables to the queue
				Client.gCAMExecutionThread.executeCallableCmd(new Callable<String>() {
					@Override
					public String call() throws Exception {
						System.out.println("Moving results to scenario folder.");
						if ((vars.get("gCamOutputToSave") != null) && (vars.get("gCamOutputToSave").length() > 0)) {
							String[] filesToSave = vars.get("gCamOutputToSave").replaceAll("/", File.separator)
									.split(";");
							for (int j = 0; j < filesToSave.length; j++) {
								//filesToSave[j]=utils.correctInteriorQuotes(filesToSave[j]);
								File file = new File(filesToSave[j]);
								if (file.exists()) {

									Path source = Paths.get(filesToSave[j]);
									String destination_str = dir + File.separator
											+ filesToSave[j].substring(filesToSave[j].lastIndexOf(File.separator) + 1);
									//destination_str=utils.correctInteriorQuotes(destination_str);
									Path destination = Paths.get(destination_str);

									System.out.println(" Moving " + filesToSave[j] + " to " + destination);
									int count = 0;
									try {
										while ((!f.isDone()) && (count < 10000)) {
											Thread.sleep(10000);
											count++;
										}

										f.cancel(true);

										files.moveFile(source, destination);

									} catch (Exception e1) {
										System.out.println("Problem moving file " + filesToSave[j]);
										System.out.println("Exception " + e1);

									}
									File destf = new File(destination_str);
									if (!destf.exists()) {
										System.out.println("Problem moving file " + filesToSave[j]);
									}
									if (file.exists())
										files.deleteFile(file);
									updateRunStatus();
								} else {
									//utils.warningMessage("Unable to save " + filesToSave[j]);
									System.out.println("Unable to save " + filesToSave[j]);
								}
							}
						}
						return "moving specified files to scenario folder";
					}
				});
			}
		}
	}

	private void runGcamOnSelected() throws IOException {

		ObservableList<ScenarioRow> selectedScenarioRows = ScenarioTable.tableScenariosLibrary.getSelectionModel()
				.getSelectedItems();

		String[] configFiles = new String[selectedScenarioRows.size()];

		for (int i = 0; i < selectedScenarioRows.size(); i++) {
			ScenarioRow mfr = selectedScenarioRows.get(i);
			mfr.setCreatedDate(new Date());
			String scenName = mfr.getScenarioName();

			String main_log_file = vars.get("scenarioDir") + File.separator + scenName + File.separator
					+ "main_log.txt";

			boolean b = true;

			if (files.doesFileExist(main_log_file)) {
				String s = "main_log.txt exists for " + scenName + ". Run anyway?";
				b = utils.selectYesOrNoDialog(s);
			}

			if (b) {
				files.deleteFile(main_log_file);
				configFiles[i] = vars.get("scenarioDir") + File.separator + scenName + File.separator + "configuration"
						+ "_" + scenName + ".xml";
				mfr.setStatus("In queue");
			} else {
				configFiles[i] = null;
			}

			// check: does archive exist? If yes, ask "Run from Archive"?
			try {
				String archiveConfigFilename = configFiles[i].replace(".xml", "_archive.xml");
				File archiveConfigFile = new File(archiveConfigFilename);
				if (archiveConfigFile.exists()) {
					String s = "Run " + scenName + " from archive?";
					if (utils.selectYesOrNoDialog(s))
						configFiles[i] = archiveConfigFilename;
				}
			} catch (Exception e) {
				System.out.println(
						"Problem checking on existence of archive. Attempting to continue from non-archived files.");
			}

		}
		runGcamModel(configFiles);
	}


	
	private void runORDModelInterfaceJar() throws IOException {
		
		runORDModelInterfaceWhich(vars.get("gCamOutputDatabase"));

	}
	
	private void runGcamPostprocWhichJar(String database_name) throws IOException {
		
		System.out.println("In runGcamPostProc");

//		String[] args= {
//		"o",database_name,
//		"q",vars.get("queryFile")
//		};
//		InterfaceMain.main(args);
		
		Client.gCAMExecutionThread.executeCallableCmd(new Callable<String>() {
			public String call() throws Exception {

				System.out.println("In callable argument prior to starting InterfaceMain.main...");
				String[] args= {
						"-o",database_name,
						"-q",vars.get("queryFile")
				};
				
				try {
					//SwingUtilities.invokeLater(()->{;
					   InterfaceMain.main(args); //why is this class not being found?
					//});
				} catch (Exception e) {
					System.out.println("exception in running InterfaceMain.main... "+e);
				}
					
				return "Done with callable";
			}
		});

	}
	
	private void runORDModelInterface() throws IOException {
	// submitting gcam run to the queue
	boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

	String shell = "cmd.exe /C";
	if (isWindows) {
		shell = "cmd.exe /C";
	} else {
		shell = "/bin/sh -c";
	}

	String[] cmd = new String[1];
	String command = shell + " cd " + vars.get("gCamPPExecutableDir") + " & java -jar "
			+ vars.get("gCamPPExecutableDir") + File.separator + vars.get("gCamPPExecutable") + " -o "
			+ vars.get("gCamOutputDatabase");

	String temp = vars.get("queryFile");
	if ((temp != null)&&(temp!="")) command += " -q " + temp;

	temp = vars.get("unitConversionsFile");
	if ((temp != null)&&(temp!="")) command += " -u " + temp;

	temp = vars.get("presetRegionsFile");
	if ((temp != null)&&(temp!="")) command += " -p " + temp;
	
	cmd[0] = command;
	System.out.println("Starting "+vars.get("gCamPPExecutable")+" using database "+vars.get("gCamOutputDatabase"));
	System.out.println("   cmd:" + cmd[0]);
	try {
		Client.gCAMPPExecutionThread.addRunnableCmdsToExecuteQueue(cmd);
	} catch (Exception e) {
		utils.warningMessage("Problem starting up ModelInterface.");
		System.out.println("Error in trying to start up ModelInterface:");
		System.out.println(e);
	}
}
	
	
	private void runORDModelInterfaceAarons() throws IOException {
	// submitting gcam run to the queue
	boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

	ArrayList<String> indCmd=new ArrayList<>();
	
	String shell = null;
	if (isWindows) {
		shell = "cmd.exe /C";
		indCmd.add("cmd.exe");
		indCmd.add("/C");
	} else {
		shell = "/bin/bash -l -c ";
		//indCmd.add("/bin/bash");
		//indCmd.add("-l");
		//indCmd.add("-c");
	}
	
	String javaToUse="java";
	String java_home_folder=System.getenv("JAVA_HOME");
	if(java_home_folder != null && java_home_folder.trim().length()>0) {
		javaToUse=java_home_folder+File.separator+"bin"+File.separator+"java";
	}

	String[] cmd = new String[1];
	
//	String command = shell + " cd " + vars.get("gCamPPExecutableDir") + " & "+javaToUse+" -jar "
//			+ vars.get("gCamPPExecutableDir") + File.separator + vars.get("gCamPPExecutable") + " -o "
//			+ vars.get("gCamOutputDatabase");

	String command = javaToUse+" -jar "
			+ vars.get("gCamPPExecutableDir") + File.separator + vars.get("gCamPPExecutable") + " -o "
			+ vars.get("gCamOutputDatabase");
	indCmd.add(javaToUse);
	indCmd.add("-jar");
	indCmd.add(vars.get("gCamPPExecutableDir") + File.separator + vars.get("gCamPPExecutable"));
	indCmd.add("-o");
	indCmd.add(vars.get("gCamOutputDatabase"));
	
	String temp = vars.get("queryFile");


	
	if (temp != null &&(temp!="")) {
		command += " -q " + temp;
		indCmd.add("-q");
		indCmd.add(temp);
	}

	temp = vars.get("unitConversionsFile");
	if ((temp == null)&&(temp!="")) {
		temp=vars.get("gCamPPExecutableDir")+File.separator+"units_rules.csv";
		
	}
	command += " -u " + temp;
	indCmd.add("-u");
	indCmd.add(temp);

	temp = vars.get("presetRegionsFile");
	if ((temp != null)&&(temp!="")) {
		command += " -p " + temp;
		indCmd.add("-p");
		indCmd.add(temp);
	}

	
	cmd[0] = command;
	System.out.println("Starting "+vars.get("gCamPPExecutable")+" using database "+vars.get("gCamOutputDatabase"));
	System.out.println("   cmd:" + cmd[0]);
	try {
		//Client.gCAMPPExecutionThread.addRunnableCmdsToExecuteQueue(cmd);
		Client.gCAMPPExecutionThread.addRunnableCmdsToExecuteQueue(indCmd.toArray(new String[indCmd.size()]));
	} catch (Exception e) {
		utils.warningMessage("Problem starting up ModelInterface.");
		System.out.println("Error in trying to start up ModelInterface:");
		System.out.println(e);
	}
}

private void runORDModelInterfaceWhich(String database_name) throws IOException {
	boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

	String shell = "cmd.exe /C";
	if (isWindows) {
		shell = "cmd.exe /C";
	} else {
		shell = "/bin/sh -c";
	}

	String[] cmd = new String[1];
	String command = shell + " cd " + vars.get("gCamPPExecutableDir") + " & java -jar "
			+ vars.get("gCamPPExecutableDir") + File.separator + vars.get("gCamPPExecutable") + " -o "
			+ database_name;

	String temp = vars.get("queryFile");
	if (temp != null)
		command += " -q " + vars.get("queryFile");

	// + " -l modelinterface.log";
	// " -dbOpen "
	cmd[0] = command;
	System.out.println("Starting EnhancedModelInterface...");
	System.out.println("   cmd:" + cmd[0]);
	try {
		Client.gCAMPPExecutionThread.addRunnableCmdsToExecuteQueue(cmd);
	} catch (Exception e) {
		utils.warningMessage("Problem starting up post-processor.");
		System.out.println("Error in trying to start up post-processor:");
		System.out.println(e);
	}
}

	private void setButtonRunSelectedStatus(boolean b) {
		Client.buttonRunScenario.setDisable(!b);
	}

	// archiveScenario(exeDir,workingDir,configFilename);
	private void archiveScenario(String exeDir, String workingDir, String archiveConfigFilename, String configFilename,
			String scenName) {
		ArrayList<String> config_content = files.getStringArrayFromFile(configFilename, "#");
		ArrayList<String> new_config_content = new ArrayList<String>();

		boolean inScenarioComponents = false;

		String archiveFoldername = workingDir + "/archive";
		File archiveFolder = new File(archiveFoldername);
		if (archiveFolder.exists()) {
			String msg = "Archive already exists. Replace?";
			if (!utils.selectYesOrNoDialog(msg)) {
				return;
			} else {
				for (File file : archiveFolder.listFiles()) {
					if (!file.isDirectory())
						file.delete();
				}
			}
		}

		for (int i = 0; i < config_content.size(); i++) {

			String line = config_content.get(i);

			if (line.indexOf("<ScenarioComponents>") >= 0) {
				inScenarioComponents = true;
			}
			if (line.indexOf("</ScenarioComponents>") >= 0) {
				inScenarioComponents = false;
			}

			if (inScenarioComponents) {
				if (line.indexOf("Value") >= 0) {
					int start_index = line.indexOf('>') + 1;
					int end_index = line.lastIndexOf('<');
					System.out.println("test:" + line.substring(start_index, end_index) + ":");

					String orig_path = line.substring(start_index, end_index);
					Path origPath = Paths.get(orig_path);
					Path exePath = Paths.get(exeDir);

					Path sourcePath = exePath.resolve(origPath).normalize();

					String destFilename = workingDir + "/archive/" + sourcePath.getFileName();
					File destFile = new File(workingDir + "/archive/" + sourcePath.getFileName());

					line = line.replace(orig_path, destFilename);

					if (destFile.exists()) {
						String msg = "Multiple files named " + sourcePath.getFileName() + ". Keeping last.";
						utils.warningMessage(msg);
						destFile.delete();
					}
					destFile.getParentFile().mkdir();
					Path destPath = Paths.get(destFile.toString());

					try {
						Files.copy(sourcePath, destPath);
					} catch (IOException e) {
						System.out.println("Error during archiving:");
						e.printStackTrace();

					}
				}
			}

			new_config_content.add(line);
		}

		// files.saveFile(config_content, configFilename);
		// File archiveConfigFile=new File(archiveConfigFilename);
		// configFile.delete();
		files.saveFile(new_config_content, archiveConfigFilename);

		String destFilename = archiveFolder + File.separator + "configuration_" + scenName + "_archive.xml";
		files.saveFile(new_config_content, destFilename);

		String zipFolder = workingDir + "/archive";
		File zipDir = new File(zipFolder);

		String zipFilename = workingDir + "/archive" + utils.getCurrentTimeStamp() + ".zip";
		File zipFile = new File(zipFilename);
		if (zipFile.exists())
			files.deleteDirectory(zipFile);

		files.zipDirectory(zipDir, zipFilename);
		// files.deleteDirectory(zipDir);

		System.out.println("Done archiving.");
	}

	private void generateExeErrorReport() {

		// create report array
		ArrayList<String> report = new ArrayList<String>();

		// get list of selected scenarios
		ObservableList<ScenarioRow> selectedScenarioRows = ScenarioTable.tableScenariosLibrary.getSelectionModel()
				.getSelectedItems();

		// cycle over main_log files
		try {
			for (int i = 0; i < selectedScenarioRows.size(); i++) {
				String scenarioName = "";
				String scenarioMainLog = vars.getgCamExecutableDir() + File.separator + "logs" + File.separator
						+ "main_log.txt";
				File mainlogfile = new File(scenarioMainLog);
				if (mainlogfile.exists()) {
					ArrayList error_lines = utils.generateErrorReport(scenarioMainLog, scenarioName);
					report.addAll(error_lines);
				}
			}
		} catch (Exception e) {
			System.out.println("error developing error log:" + e);
		}
		// display
		if (report.size() == 0) {
			report.add("No errors reported.");
		}

		utils.displayArrayList(report, "Error Report", false);

	}
	
	private void generateErrorReport() {

		// create report array
		ArrayList<String> report = new ArrayList<String>();

		// get list of selected scenarios
		ObservableList<ScenarioRow> selectedScenarioRows = ScenarioTable.tableScenariosLibrary.getSelectionModel()
				.getSelectedItems();

		// cycle over main_log files
		try {
			for (int i = 0; i < selectedScenarioRows.size(); i++) {
				String scenarioName = "" + selectedScenarioRows.get(i).getScenName();
				String scenarioMainLog = vars.getScenarioDir() + File.separator + scenarioName + File.separator
						+ "main_log.txt";
				File mainlogfile = new File(scenarioMainLog);
				if (mainlogfile.exists()) {
					ArrayList error_lines = utils.generateErrorReport(scenarioMainLog, scenarioName);
//					if (error_lines.size() > 0) {
//						report.add("---------------------------------");
//						report.add(utils.processErrors(error_lines, 0.01));
//						report.add("=================================");
//					}
					report.addAll(error_lines);
				}
			}
		} catch (Exception e) {
			System.out.println("error developing error log:" + e);
		}
		// display
		if (report.size() == 0) {
			report.add("No errors reported.");
		}

		utils.displayArrayList(report, "Error Report", false);
         
	}

	private void generateRunReport() {

		ArrayList<String> report = new ArrayList<String>();

		String scenario_name = null;
		String when_created = null;
		String when_run = null;
		String model_version = null;
		String config_file = null;
		String config_path = null;
		int num_warnings = 0;
		int num_errors = 0;
		String not_solved = null;
		boolean is_completed = false;
		String solution_time = null;
		String total_time = null;
		String components = "";
		ArrayList<String> error_lines = null;

		File[] scenarioFolders = new File(vars.getScenarioDir()).listFiles(File::isDirectory);
		ArrayList<File> mainLogFiles = new ArrayList<File>();

		for (int i = 0; i < scenarioFolders.length; i++) {
			String mainLogFilename = scenarioFolders[i].getPath() + File.separator + "main_log.txt";
			File logFile = new File(mainLogFilename);
			if (logFile.exists()) {
				mainLogFiles.add(logFile);
			}
		}

		String str = "scenario,created,run,version,#warn,#err,unsolved,errors,completed?,solution(sec),total(sec),components";

		report.add(str);

		for (int i = 0; i < mainLogFiles.size(); i++) {
			File main_log = mainLogFiles.get(i);
			String folder_name = main_log.getParent();
			String scenario_pathname = main_log.getParent();
			scenario_name = scenario_pathname.substring(scenario_pathname.lastIndexOf(File.separator) + 1);
			config_file = files.searchForTextInFileS(main_log, "Configuration file:", "#")
					.replace("Configuration file:", "").trim();

			String temp = config_file;
			when_created = files.getLastModifiedInfoForFile(temp);
			when_run = files.getLastModifiedInfoForFile(main_log.toString());
			model_version = files.searchForTextInFileS(main_log, "Running GCAM model", "#")
					.replace("Running GCAM model", "").trim();
			num_warnings = files.countLinesWithTextInFile(main_log, "Warning", "#");
			num_errors = files.countLinesWithTextInFile(main_log, "ERROR", "#");
			not_solved = files.searchForTextInFileS(main_log, "The following model periods did not solve:", "#")
					.replace("The following model periods did not solve:", "").trim().replace(",", ";");
			is_completed = files.searchForTextInFile(main_log, "Model run completed.", "#");
			solution_time = files.searchForTextInFileS(main_log, "Full Scenario", "#").replace("Full Scenario", "")
					.replace(" seconds.", "").trim();
			total_time = files.searchForTextInFileS(main_log, "Data Readin, Model Run & Write Time:", "#")
					.replace("Data Readin, Model Run & Write Time:", "").replace(" seconds.", "").trim();
			components = getComponentsFromTable(scenario_name);

			error_lines = files.getStringArrayWithPrefix(main_log.getPath(), "ERROR");
			String error_rpt = utils.processErrors(error_lines, 0.01);

			String s = ",";

			str = scenario_name + s + when_created + s + when_run + s + model_version + s + num_warnings + s
					+ num_errors + s + not_solved + s + error_rpt + s + is_completed + s + solution_time + s
					+ total_time + s + components;

			report.add(str);
			if (not_solved.trim() != "")
				System.out.println(str);
		}

		String report_file = vars.getgCamGUILogDir() + File.separator + "scenario_report.csv";
		files.saveFile(report, report_file);
		// utils.displayArrayList(report, "Scenario Report");

		utils.showPopupTableOfCSVData("Scenario Run Report", report, 910, 600);

	}

	private String getComponentsFromTable(String scenName) {
		String str = "";

		TableColumn<ScenarioRow, String> scenCol = ScenarioTable.getScenNameColumn();
		TableColumn<ScenarioRow, String> compCol = ScenarioTable.getComponentsColumn();

		int num = ScenarioTable.listOfScenarioRuns.size();
		for (int i = 0; i < num; i++) {
			ScenarioRow sr = ScenarioTable.listOfScenarioRuns.get(i);
			String sname = sr.getScenarioName();
			if (sname.equals(scenName)) {
				str = sr.getComponents();
			}
		}
		return str;
	}
	


}
