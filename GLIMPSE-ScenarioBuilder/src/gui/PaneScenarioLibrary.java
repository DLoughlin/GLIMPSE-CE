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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

/**
 * PaneScenarioLibrary is responsible for managing the lower pane of the application
 * where historical run records are displayed and scenario-related actions are handled.
 * It provides UI controls for scenario management and status updates.
 */
class PaneScenarioLibrary extends ScenarioBuilder {

    // Constants for UI labels and tooltips
    private static final String DIFF_LABEL = "Diff";
    private static final String DIFF_TOOLTIP = "Diff: Compare first two selected configurations";
    private static final String REFRESH_LABEL = "Refresh";
    private static final String REFRESH_TOOLTIP = "Refresh: Update scenario completion status";
    private static final String RESULTS_LABEL = "Results";
    private static final String RESULTS_TOOLTIP = "Results: Open the ModelInterface to view results";
    private static final String RESULTS_SELECTED_LABEL = "Results (selected)";
    private static final String RESULTS_SELECTED_TOOLTIP = "Results-Selected: Open the ModelInterface to view results for selected scenario";
    private static final String PLAY_LABEL = "Play";
    private static final String PLAY_TOOLTIP = "Play: Add the selected scenarios to execution queue";
    private static final String DELETE_LABEL = "Delete";
    private static final String DELETE_TOOLTIP = "Delete: Move the selected scenarios to trash";
    private static final String CONFIG_LABEL = "Config";
    private static final String CONFIG_TOOLTIP = "Open: Open configuration file for selected scenario";
    private static final String LOG_LABEL = "Log";
    private static final String LOG_TOOLTIP = "Main_Log-Selected: View main_log.txt in selected scenario folder";
    private static final String EXE_ERRORS_LABEL = "ExeError";
    private static final String EXE_ERRORS_TOOLTIP = "Errors: View errors in main_log.txt file in exe/log folder";
    private static final String ERRORS_LABEL = "Errors";
    private static final String ERRORS_TOOLTIP = "Errors-Selected: View errors in selected scenario main_log.txt file";
    private static final String EXE_LOG_LABEL = "ExeLog";
    private static final String EXE_LOG_TOOLTIP = "Main_Log: View main_log.txt in the ee/log folder";
    private static final String BROWSE_LABEL = "Browse";
    private static final String BROWSE_TOOLTIP = "Browse: Open the folder of the selected scenarios";
    private static final String IMPORT_LABEL = "Import";
    private static final String IMPORT_TOOLTIP = "Import: Import an existing configuration file to create new scenario";
    private static final String QUEUE_LABEL = "Queue";
    private static final String QUEUE_TOOLTIP = "Queue: List scenarios added to queue this session";
    private static final String ARCHIVE_LABEL = "Archive";
    private static final String ARCHIVE_TOOLTIP = "Archive: Archive the selected scenarios";
    private static final String REPORT_LABEL = "Report";
    private static final String REPORT_TOOLTIP = "Report: Generate scenario execution report";

    private static final String XML_FILE_FILTER_LABEL = "XML files (*.xml)";
    private static final String XML_FILE_FILTER_EXT = "xml";

    private final GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
    private final GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
    private final GLIMPSEFiles files = GLIMPSEFiles.getInstance();
    private final GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

    private final ArrayList<String> runsQueuedList = new ArrayList<>();
    private final ArrayList<String> runsCompletedList = new ArrayList<>();
    private long timeAtStartup = 0;
    private final HBox hBox = new HBox(1);

    /**
     * Constructs the scenario library pane and sets up UI controls and event handlers.
     * @param stage the main application stage
     */
    PaneScenarioLibrary(Stage stage) {
        hBox.setStyle(styles.getFontStyle());
        hBox.setSpacing(10);
        ScenarioTable.tableScenariosLibrary.setOnMouseClicked(e -> setArrowAndButtonStatus());
        createScenarioLibraryButtons();
        ScenarioTable.tableScenariosLibrary.prefWidthProperty().bind(stage.widthProperty().multiply(1.0));
        ScenarioTable.tableScenariosLibrary.prefHeightProperty().bind(stage.heightProperty().multiply(0.7));
        hBox.getChildren().addAll(ScenarioTable.tableScenariosLibrary);
        if (timeAtStartup == 0) timeAtStartup = (new Date()).getTime();
        System.out.println("time now=" + (new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")).format(timeAtStartup));
        updateRunStatus();
    }

    PaneScenarioLibrary() {}

    /**
     * Sets up scenario library buttons and their event handlers.
     */
    private void createScenarioLibraryButtons() {
        // Creating buttons on the bottom pane
        Client.buttonDiffFiles = utils.createButton(DIFF_LABEL, styles.getBigButtonWidth(), DIFF_TOOLTIP, "compare");
        Client.buttonRefreshScenarioStatus = utils.createButton(REFRESH_LABEL, styles.getBigButtonWidth(), REFRESH_TOOLTIP, "refresh");
        Client.buttonResults = utils.createButton(RESULTS_LABEL, styles.getBigButtonWidth(), RESULTS_TOOLTIP, "results");
        Client.buttonResultsForSelected = utils.createButton(RESULTS_SELECTED_LABEL, styles.getBigButtonWidth(), RESULTS_SELECTED_TOOLTIP, "results-selected");
        Client.buttonRunScenario = utils.createButton(PLAY_LABEL, styles.getBigButtonWidth(), PLAY_TOOLTIP, "run");
        Client.buttonDeleteScenario = utils.createButton(DELETE_LABEL, styles.getBigButtonWidth(), DELETE_TOOLTIP, "delete");
        Client.buttonViewConfig = utils.createButton(CONFIG_LABEL, styles.getBigButtonWidth(), CONFIG_TOOLTIP, "edit");
        Client.buttonViewLog = utils.createButton(LOG_LABEL, styles.getBigButtonWidth(), LOG_TOOLTIP, "log2");
        Client.buttonViewExeErrors = utils.createButton(EXE_ERRORS_LABEL, styles.getBigButtonWidth(), EXE_ERRORS_TOOLTIP, "exe-errors");
        Client.buttonViewErrors = utils.createButton(ERRORS_LABEL, styles.getBigButtonWidth(), ERRORS_TOOLTIP, "errors");
        Client.buttonViewExeLog = utils.createButton(EXE_LOG_LABEL, styles.getBigButtonWidth(), EXE_LOG_TOOLTIP, "exe-log");
        Client.buttonBrowseScenarioFolder = utils.createButton(BROWSE_LABEL, styles.getBigButtonWidth(), BROWSE_TOOLTIP, "open_folder");
        Client.buttonImportScenario = utils.createButton(IMPORT_LABEL, styles.getBigButtonWidth(), IMPORT_TOOLTIP, "import");
        Client.buttonShowRunQueue = utils.createButton(QUEUE_LABEL, styles.getBigButtonWidth(), QUEUE_TOOLTIP, "queue");
        Client.buttonArchiveScenario = utils.createButton(ARCHIVE_LABEL, styles.getBigButtonWidth(), ARCHIVE_TOOLTIP, "archive");
        Client.buttonReport = utils.createButton(REPORT_LABEL, styles.getBigButtonWidth(), REPORT_TOOLTIP, "report");

        // Set initial button status
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

        // Event handlers
        Client.buttonRefreshScenarioStatus.setOnAction(e -> {
            updateRunStatus();
            ScenarioTable.tableScenariosLibrary.refresh();
        });
        Client.buttonReport.setOnAction(e -> generateRunReport());
        Client.buttonRunScenario.setOnAction(e -> {
            try {
                runGcamOnSelected();
            } catch (Exception ex) {
                utils.warningMessage("Problem running GCAM.");
                System.out.println("Error trying to run GCAM.");
                System.out.println("Error: " + ex);
                utils.exitOnException();
            }
            updateRunStatus();
        });
        Client.buttonArchiveScenario.setOnAction(e -> handleArchiveScenario());
        Client.buttonDeleteScenario.setOnAction(e -> handleDeleteScenario());
        Client.buttonResults.setOnAction(e -> handleResults());
        Client.buttonResultsForSelected.setOnAction(e -> handleResultsForSelected());
        Client.buttonBrowseScenarioFolder.setOnAction(e -> handleBrowseScenarioFolder());
        Client.buttonImportScenario.setOnAction(e -> handleImportScenario());
        Client.buttonViewConfig.setOnAction(e -> handleViewConfig());
        Client.buttonViewLog.setOnAction(e -> handleViewLog());
        Client.buttonViewExeErrors.setOnAction(e -> generateExeErrorReport());
        Client.buttonViewErrors.setOnAction(e -> generateErrorReport());
        Client.buttonViewExeLog.setOnAction(e -> handleViewExeLog());
        Client.buttonDiffFiles.setOnAction(e -> handleDiffFiles());
        Client.buttonShowRunQueue.setOnAction(e -> handleShowRunQueue());

        // Alignment
        Client.buttonResults.setAlignment(Pos.CENTER);
        Client.buttonResultsForSelected.setAlignment(Pos.CENTER);
        Client.buttonRunScenario.setAlignment(Pos.CENTER);
        Client.buttonDeleteScenario.setAlignment(Pos.CENTER);

        // Initial button visibility
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

    // --- UI Event Handlers ---
    private void handleArchiveScenario() {
        if (!utils.confirmArchiveScenario()) return;
        ObservableList<ScenarioRow> selectedFiles = ScenarioTable.tableScenariosLibrary.getSelectionModel().getSelectedItems();
        for (ScenarioRow row : selectedFiles) {
            String scenName = row.getScenarioName();
            String workingDir = vars.getScenarioDir() + File.separator + scenName;
            String exeDir = vars.getgCamExecutableDir();
            String configFilename = workingDir + File.separator + "configuration_" + scenName + ".xml";
            String archiveConfigFilename = workingDir + File.separator + "configuration_" + scenName + "_archive.xml";
            archiveScenario(exeDir, workingDir, archiveConfigFilename, configFilename, scenName);
        }
    }

    private void handleDeleteScenario() {
        if (!utils.confirmDelete()) return;
        ObservableList<ScenarioRow> selectedFiles = ScenarioTable.tableScenariosLibrary.getSelectionModel().getSelectedItems();
        for (ScenarioRow row : selectedFiles) {
            String scenName = row.getScenarioName();
            String xmlDir = vars.getScenarioDir() + File.separator + scenName;
            String trashDirFolder = vars.getTrashDir() + File.separator + scenName;
            File trashDir = new File(trashDirFolder);
            if (trashDir.exists())
				try {
					files.deleteDirectoryStream(trashDir.toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            if (!trashDir.exists()) trashDir.mkdirs();
            try {
                Files.move(Paths.get(xmlDir), Paths.get(trashDirFolder), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                utils.warningMessage("Problem deleting scenario(s)");
                System.out.println("error: " + e);
                utils.exitOnException();
            }
        }
        ScenarioTable.removeFromListOfRunFiles(selectedFiles);
    }

    private void handleResults() {
        if (vars.getgCamExecutableDir().isEmpty()) {
            utils.warningMessage("Please specify gCamExecutableDir in options file.");
        } else {
            try {
                runModelInterface();
            } catch (Exception e) {
                e.printStackTrace();
                utils.exitOnException();
            }
        }
    }

    private void handleResultsForSelected() {
        if (vars.getgCamExecutableDir().isEmpty()) {
            utils.warningMessage("Please specify gCamExecutableDir in options file.");
        } else {
            ObservableList<ScenarioRow> selectedFiles = ScenarioTable.tableScenariosLibrary.getSelectionModel().getSelectedItems();
            if (selectedFiles.size() == 1) {
                String scenName = selectedFiles.get(0).getScenarioName();
                String configFilename = vars.getScenarioDir() + File.separator + scenName + File.separator + "configuration_" + scenName + ".xml";
                File configFile = new File(configFilename);
                String databaseLine = files.searchForTextInFileS(configFile, "xmldb-location", "#");
                String databaseName = utils.getStringBetweenCharSequences(databaseLine, ">", "</");
                String updatedName = files.getResolvedPath(vars.getgCamExecutableDir(), databaseName);
                try {
                    runORDModelInterfaceWhich(updatedName);
                } catch (Exception e) {
                    e.printStackTrace();
                    utils.exitOnException();
                }
            }
        }
    }

    private void handleBrowseScenarioFolder() {
        ObservableList<ScenarioRow> selectedFiles = ScenarioTable.tableScenariosLibrary.getSelectionModel().getSelectedItems();
        for (ScenarioRow row : selectedFiles) {
            String scenName = row.getScenarioName();
            String xmlDir = vars.getScenarioDir() + File.separator + scenName;
            files.openFileExplorer(xmlDir);
        }
    }

    private void handleImportScenario() {
        File newConfigFile = FileChooserPlus.showOpenDialog(null, "Select scenario configuration file", new File(vars.getgCamExecutableDir()), FileChooserPlus.createExtensionFilter(XML_FILE_FILTER_LABEL, XML_FILE_FILTER_EXT));
        if (newConfigFile != null) {
            String str = files.searchForTextInFileS(newConfigFile, "scenarioName", "<!--");
            String scenarioName = utils.getStringBetweenCharSequences(str, ">", "</");
            String workingScenarioLog = vars.getGlimpseLogDir() + File.separator + "Runs.txt";
            File workingScenariosFile = new File(workingScenarioLog);
            boolean doesScenarioExist = files.searchForTextAtStartOfLinesInFile(workingScenariosFile, scenarioName + ",", "#");
            String confirmMsg = doesScenarioExist ? "Overwrite existing scenario " + scenarioName + "?" : "Import " + scenarioName + " into GLIMPSE?";
            if (!utils.confirmAction(confirmMsg)) return;
            String newScenFolderName = vars.getScenarioDir() + File.separator + scenarioName;
            File newScenFolder = new File(newScenFolderName);
            newScenFolder.mkdir();
            String newScenFilename = newScenFolder + File.separator + "configuration_" + scenarioName + ".xml";
            files.copyFile(newConfigFile.getAbsolutePath(), newScenFilename);
            ScenarioRow sr = new ScenarioRow(scenarioName);
            sr.setComponents("Externally-created scenario");
            sr.setCreatedDate(new Date());
            sr.setStatus("No");
            ScenarioRow[] newRun = { sr };
            ScenarioTable.addToListOfRunFiles(newRun);
        }
    }

    private void handleViewConfig() {
        ObservableList<ScenarioRow> selectedFiles = ScenarioTable.tableScenariosLibrary.getSelectionModel().getSelectedItems();
        for (ScenarioRow row : selectedFiles) {
            String scenName = row.getScenarioName();
            String xmlFile = vars.getScenarioDir() + File.separator + scenName + File.separator + "configuration_" + scenName + ".xml";
            files.showFileInTextEditor(xmlFile);
        }
    }

    private void handleViewLog() {
        ObservableList<ScenarioRow> selectedFiles = ScenarioTable.tableScenariosLibrary.getSelectionModel().getSelectedItems();
        for (ScenarioRow row : selectedFiles) {
            String scenName = row.getScenarioName();
            String txtFile = vars.getScenarioDir() + File.separator + scenName + File.separator + "main_log.txt";
            files.showFileInTextEditor(txtFile);
        }
    }

    private void handleViewExeLog() {
        String filename = vars.getgCamExecutableDir() + File.separator + "logs" + File.separator + "main_log.txt";
        files.showFileInTextEditor(filename);
    }

    private void handleDiffFiles() {
        ObservableList<ScenarioRow> selectedFiles = ScenarioTable.tableScenariosLibrary.getSelectionModel().getSelectedItems();
        if (selectedFiles.size() == 2) {
            String sName1 = selectedFiles.get(0).getScenarioName();
            String sName2 = selectedFiles.get(1).getScenarioName();
            String file1 = vars.getScenarioDir() + File.separator + sName1 + File.separator + "configuration_" + sName1 + ".xml";
            String file2 = vars.getScenarioDir() + File.separator + sName2 + File.separator + "configuration_" + sName2 + ".xml";
            utils.diffTwoFiles(file1, file2);
        }
    }

    private void handleShowRunQueue() {
        ArrayList<String> txtArray = createSimpleQueueRpt();
        utils.displayArrayList(txtArray, "Run Queue");
    }

    /**
     * Returns the HBox containing the scenario library table.
     * @return the HBox
     */
    public HBox gethBox() {
        return hBox;
    }

    /**
     * Returns a simple report of the run queue and completed runs.
     * @return ArrayList of report lines
     */
    protected ArrayList<String> createSimpleQueueRpt() {
        ArrayList<String> rtnArray = new ArrayList<>();
        rtnArray.add("Note: Includes only runs added to the queue since the start of this session.");
        if (!runsQueuedList.isEmpty()) {
            rtnArray.add("---");
            rtnArray.add("In queue:");
            rtnArray.addAll(runsQueuedList);
        }
        if (!runsCompletedList.isEmpty()) {
            rtnArray.add("---");
            rtnArray.add("Completed:");
            rtnArray.addAll(runsCompletedList);
        }
        return rtnArray;
    }

    /**
     * Returns a detailed report of the run queue, completed, and not completed runs.
     * @param runQueue the run queue
     * @return ArrayList of report lines
     */
    protected ArrayList<String> createFancyQueueRpt(ArrayList<String> runQueue) {
        ArrayList<String> rtnArray = new ArrayList<>();
        rtnArray.add("Note: Includes only runs added to the queue since the start of this session.");
        ArrayList<String> completedArray = new ArrayList<>();
        completedArray.add("===");
        completedArray.add("Completed successfully:");
        ArrayList<String> issuesArray = new ArrayList<>();
        issuesArray.add("---");
        issuesArray.add("Not completed successfully (w/Issues):");
        ArrayList<String> notCompletedArray = new ArrayList<>();
        notCompletedArray.add("---");
        notCompletedArray.add("Running or still in queue:");
        ObservableList<ScenarioRow> allRuns = ScenarioTable.tableScenariosLibrary.getItems();
        for (ScenarioRow scenRow : allRuns) {
            String scenName = scenRow.getScenarioName();
            String searchText = File.separator + scenName + File.separator;
            String isComplete = scenRow.getStatus();
            String runDate = String.valueOf(scenRow.getCreatedDate());
            boolean match = false;
            for (String runInQueue : runQueue) {
                if (runInQueue.contains(searchText)) {
                    match = true;
                }
                if (match) {
                    if ((isComplete.equals("Success")) || (isComplete.equals("Unsolved mkts"))) {
                        completedArray.add(runInQueue);
                    } else if (isComplete.isEmpty()) {
                        if (runDate != null && !runDate.isEmpty()) {
                            notCompletedArray.add(runInQueue);
                        }
                    } else if (isComplete.equals("DNF")) {
                        issuesArray.add(runInQueue);
                    } else if (isComplete.equals("Running")) {
                        runInQueue += " (Running)";
                        notCompletedArray.add(runInQueue);
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

    /**
     * Removes a scenario from the scenario library by name.
     * @param nameToDelete the scenario name
     */
    void deleteItemFromScenarioLibrary(String nameToDelete) {
        ObservableList<ScenarioRow> allScenariosList = ScenarioTable.tableScenariosLibrary.getItems();
        ObservableList<ScenarioRow> deleteScenariosList = FXCollections.observableArrayList();
        for (ScenarioRow mfr : allScenariosList) {
            if (mfr.getScenarioName().equals(nameToDelete)) {
                deleteScenariosList.add(mfr);
            }
        }
        ScenarioTable.removeFromListOfRunFiles(deleteScenariosList);
    }

    /**
     * Updates the run status for all scenarios and refreshes the table.
     */
    public void updateRunStatus() {
        String currentMainLogName = vars.getgCamExecutableDir() + File.separator + "logs" + File.separator + "main_log.txt";
        File currentMainLogFile = new File(currentMainLogName);
        String runningScenario = utils.getRunningScenario(currentMainLogFile);
        ScenarioTable.tableScenariosLibrary.refresh();
        String address = vars.getGlimpseLogDir() + File.separator + "Runs.txt";
        DateFormat format = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd: HH:mm", Locale.ENGLISH);
        ArrayList<String> searchArray = new ArrayList<>();
        Platform.runLater(() -> {
            String computerStats = utils.getComputerStatString().trim();
            if (computerStats.endsWith("!!!")) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String time = formatter.format(date);
                String glimpseLogFilename = vars.getGlimpseLogDir() + File.separator + "glimpse_log.txt";
                String logText = runningScenario + ":" + time + ":" + computerStats + vars.getEol();
                files.appendTextToFile(logText, glimpseLogFilename);
            }
            utils.sb.setText(computerStats);
            if (computerStats.endsWith("!")) {
                utils.sb.setStyle("-fx-text-fill: red");
            } else {
                utils.sb.setStyle("-fx-text-fill: black");
            }
        });
        try {
            File[] scenarioFolders = new File(vars.getScenarioDir()).listFiles(File::isDirectory);
            if (scenarioFolders == null) return;
            for (File scenarioFolder : scenarioFolders) {
                searchArray.clear();
                searchArray.add("Model run completed.");
                searchArray.add("Data Readin, Model Run & Write Time:");
                searchArray.add("The following model periods did not solve:");
                Long createdDate = 0L;
                Long completedDate = 0L;
                String scenarioName = scenarioFolder.getName();
                String configName = scenarioFolder + File.separator + "configuration_" + scenarioName + ".xml";
                File configFile = new File(configName);
                if (!configFile.exists()) continue;
                String components = getComponentsFromConfig(configFile);
                String mainLogName = scenarioFolder + File.separator + "main_log.txt";
                File mainLogFile = new File(mainLogName);
                boolean mainLogExists = mainLogFile.exists();
                String status = "";
                String runtime = "";
                String unsolved = "";
                createdDate = configFile.lastModified();
                if (mainLogExists) {
                    completedDate = mainLogFile.lastModified();
                    searchArray = files.getMatchingTextArrayInFile(mainLogName, searchArray);
                    if (!searchArray.get(0).isEmpty()) {
                        status = "Success";
                    } else {
                        status = "DNF";
                        String runningStatus = utils.getScenarioStatusFromMainLog(mainLogFile);
                        if (runningStatus.contains(",ERR")) {
                            String errorStr = runningStatus.substring(runningStatus.indexOf(",") + 4);
                            unsolved = errorStr;
                        }
                    }
                    for (int i = 0; i < runsQueuedList.size(); i++) {
                        String line = runsQueuedList.get(i);
                        if ((line.equals(configName)) || (line.equals(scenarioName))) {
                            status = "In queue";
                            if (mainLogExists) {
                                runsCompletedList.add(line);
                                runsQueuedList.remove(i);
                            }
                            break;
                        }
                    }
                }
                if (!searchArray.get(1).isEmpty()) {
                    try {
                        runtime = searchArray.get(1).split(":")[1].trim();
                    } catch (Exception e) {
                        runtime = "";
                    }
                    runtime = runtime.replace("seconds.", "").trim();
                    try {
                        int totalSecs = (int) Math.round(Float.parseFloat(runtime));
                        int hours = (totalSecs - totalSecs % 3600) / 3600;
                        int minutes = (totalSecs % 3600 - totalSecs % 3600 % 60) / 60;
                        runtime = hours + " hr " + minutes + " min ";
                    } catch (Exception e) {
                        runtime += "";
                    }
                }
                if (!searchArray.get(2).isEmpty()) {
                    try {
                        unsolved = searchArray.get(2).split(":")[1].trim();
                        status = "Unsolved mkts";
                    } catch (Exception e) {
                        unsolved = "";
                    }
                }
                String createdDateStr = createdDate != 0L ? format2.format(createdDate) : "";
                String completedDateStr = completedDate != 0L ? format2.format(completedDate) : "";
                if ((!status.equals("Success")) && (!status.equals("Unsolved mkts")) && (!status.equals("DNF"))) {
                    if (scenarioName.equals(runningScenario)) {
                        status = "Running";
                        long lastDate = currentMainLogFile.lastModified();
                        if (lastDate < timeAtStartup) {
                            status = "Lost handle";
                        } else {
                            String runningStatus = utils.getScenarioStatusFromMainLog(currentMainLogFile);
                            if (runningStatus.contains(",ERR")) {
                                String temp = runningStatus.substring(0, runningStatus.indexOf(","));
                                status = status + "(" + temp + ")";
                                String errorStr = runningStatus.substring(runningStatus.indexOf(",") + 4);
                                unsolved = errorStr;
                            } else {
                                String temp = runningStatus;
                                if (!temp.isEmpty()) {
                                    status = status + "(" + temp + ")";
                                }
                            }
                        }
                    } else {
                        for (String line : runsQueuedList) {
                            if ((line.equals(configName)) || (line.equals(scenarioName))) {
                                status = "In queue";
                                break;
                            }
                        }
                    }
                }
                boolean match = false;
                for (ScenarioRow s : ScenarioTable.listOfScenarioRuns) {
                    if (s.getScenarioName().equals(scenarioName)) {
                        match = true;
                        s.setStatus(status);
                        s.setCreatedDate(createdDateStr);
                        s.setCompletedDate(completedDateStr);
                        s.setComponents(components);
                        s.setRuntime(runtime);
                        s.setUnsolvedMarkets(unsolved);
                    }
                }
                if (!match) {
                    ScenarioRow sr = new ScenarioRow(scenarioName);
                    sr.setComponents(components);
                    sr.setCreatedDate(createdDateStr);
                    sr.setCompletedDate(completedDateStr);
                    sr.setStatus(status);
                    sr.setRuntime(runtime);
                    sr.setUnsolvedMarkets(unsolved);
                    ScenarioTable.listOfScenarioRuns.add(sr);
                }
            }
            ScenarioTable.tableScenariosLibrary.refresh();
        } catch (Exception ex) {
            System.out.println("Problem updating scenario table: " + ex);
        }
    }

    /**
     * Reads scenario components from a configuration file.
     * @param file the configuration file
     * @return the components string
     */
    private String getComponentsFromConfig(File file) {
        String rtnStr = "";
        try (Scanner fileScanner = new Scanner(file)) {
            boolean startRecording = false;
            boolean stopRecording = false;
            boolean hasMetaData = false;
            int count = 0;
            while (fileScanner.hasNext() && !stopRecording) {
                String line = fileScanner.nextLine().trim();
                if (line.equals("##################### Scenario Meta Data #####################"))
                    hasMetaData = true;
                if (line.equals("###############################################################"))
                    stopRecording = true;
                if (startRecording && (line.length() > 0) && !stopRecording) {
                    if (count == 0) {
                        count++;
                        rtnStr += line;
                    } else {
                        rtnStr += " ; " + line;
                    }
                }
                if (line.equals("Components:"))
                    startRecording = true;
                if (line.equals("<Files>"))
                    stopRecording = true;
            }
            if (!hasMetaData) {
                rtnStr = "Externally-created scenario";
            }
        } catch (Exception e) {
            System.out.println("Problem reading components from " + file.getName() + ": " + e);
        }
        return rtnStr;
    }

    private void runGcamModel(String[] scenarioConfigFiles) throws IOException {
        System.out.println("Running scenarios in GCAM...");
        ArrayList<String> cmdList = new ArrayList<String>();
        for (String scenarioConfigFile : scenarioConfigFiles) {
            if (scenarioConfigFile != null) {
                final String dir = scenarioConfigFile.substring(0, scenarioConfigFile.lastIndexOf(File.separator)).replaceAll("/", File.separator);
                System.out.println("config: " + scenarioConfigFile);
                this.runsQueuedList.add(scenarioConfigFile);
                Client.gCAMExecutionThread.executeCallableCmd(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        System.out.println("Cleaning out folder.");
                        String[] filesToDelete = vars.getFilesToSave().replaceAll("/", File.separator).split(";");
                        for (String fileToDelete : filesToDelete) {
                            String file = dir + File.separator + fileToDelete.substring(fileToDelete.lastIndexOf(File.separator) + 1);
                            System.out.println(" Deleting " + file);
                            File f = new File(file);
                            if (f.exists()) {
                                try {
                                    Path pathOfFileToDelete = Paths.get(file);
                                    Files.delete(pathOfFileToDelete);
                                } catch (Exception e1) {
                                    utils.warningMessage("Error deleting " + file);
                                    System.out.println("Error deleting " + file + ":" + e1);
                                }
                            }
                        }
                        return "txt and log files deleted from scenario folder";
                    }
                });
                boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
                String cmd = isWindows ? "cmd.exe /C start" : "/bin/sh -c";
                String cmdStr = cmd + " " + vars.getgCamExecutable() + " " + vars.getgCamExecutableArgs() + " " + scenarioConfigFile;
                Future f = Client.gCAMExecutionThread.submitCommandWithDirectory(cmdStr, vars.getgCamExecutableDir());
                Client.gCAMExecutionThread.executeCallableCmd(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        System.out.println("Moving results to scenario folder.");
                        if ((vars.getFilesToSave() != null) && (vars.getFilesToSave().length() > 0)) {
                            String[] filesToSave = vars.getFilesToSave().replaceAll("/", File.separator).split(";");
                            for (String fileToSave : filesToSave) {
                                File file = new File(fileToSave);
                                if (file.exists()) {
                                    Path source = Paths.get(fileToSave);
                                    String destinationStr = dir + File.separator + fileToSave.substring(fileToSave.lastIndexOf(File.separator) + 1);
                                    Path destination = Paths.get(destinationStr);
                                    System.out.println(" Moving " + fileToSave + " to " + destination);
                                    int count = 0;
                                    try {
                                        while ((!f.isDone()) && (count < 10000)) {
                                            Thread.sleep(10000);
                                            count++;
                                        }
                                        f.cancel(true);
                                        files.moveFile(source, destination);
                                    } catch (Exception e1) {
                                        System.out.println("Problem moving file " + fileToSave);
                                        System.out.println("Exception " + e1);
                                    }
                                    File destf = new File(destinationStr);
                                    if (!destf.exists()) {
                                        System.out.println("Problem moving file " + fileToSave);
                                    }
                                    if (file.exists())
                                        files.deleteFile(file);
                                    updateRunStatus();
                                } else {
                                    System.out.println("Unable to save " + fileToSave);
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
        ObservableList<ScenarioRow> selectedScenarioRows = ScenarioTable.tableScenariosLibrary.getSelectionModel().getSelectedItems();
        String[] configFiles = new String[selectedScenarioRows.size()];
        for (int i = 0; i < selectedScenarioRows.size(); i++) {
            ScenarioRow mfr = selectedScenarioRows.get(i);
            mfr.setCreatedDate(new Date());
            String scenName = mfr.getScenarioName();
            String mainLogFile = vars.getScenarioDir() + File.separator + scenName + File.separator + "main_log.txt";
            boolean b = true;
            if (files.doesFileExist(mainLogFile)) {
                String s = "main_log.txt exists for " + scenName + ". Run anyway?";
                b = utils.selectYesOrNoDialog(s);
            }
            if (b) {
                files.deleteFile(mainLogFile);
                configFiles[i] = vars.getScenarioDir() + File.separator + scenName + File.separator + "configuration" + "_" + scenName + ".xml";
                mfr.setStatus("In queue");
            } else {
                configFiles[i] = null;
            }
            try {
                String archiveConfigFilename = configFiles[i].replace(".xml", "_archive.xml");
                File archiveConfigFile = new File(archiveConfigFilename);
                if (archiveConfigFile.exists()) {
                    String s = "Run " + scenName + " from archive?";
                    if (utils.selectYesOrNoDialog(s))
                        configFiles[i] = archiveConfigFilename;
                }
            } catch (Exception e) {
                System.out.println("Problem checking on existence of archive. Attempting to continue from non-archived files.");
            }
        }
        runGcamModel(configFiles);
    }

    private void runORDModelInterfaceJar() throws IOException {
        runORDModelInterfaceWhich(vars.getgCamOutputDatabase());
    }

    private void runGcamPostprocWhichJar(String database_name) throws IOException {
        Client.gCAMExecutionThread.executeCallableCmd(new Callable<String>() {
            public String call() throws Exception {
                String[] args = { 
                        "-o", database_name, 
                        "-q", vars.getQueryFilename(),
                        "-u", vars.getUnitConversionsFilename(),
                        "-f", vars.getFavoriteQueryFilename(),
                        "-p", vars.getPresetRegionListFilename(),
                        };
                try {
                    InterfaceMain.main(args);
                } catch (Exception e) {
                    System.out.println("exception in running InterfaceMain.main... " + e);
                }
                return "Done with callable";
            }
        });
    }

    private void runModelInterface() throws IOException {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        String shell = isWindows ? "cmd.exe /C" : "/bin/sh -c";
        String[] cmd = new String[1];
        String command = shell + " cd " + vars.getModelInterfaceJarDir() + " & java -jar "
                + vars.getModelInterfaceJarDir() + File.separator + vars.getModelInterfaceJar() + " -o "
                + vars.getgCamOutputDatabase();
        String temp = vars.getQueryFilename();
        if ((temp != null) && (temp != ""))
            command += " -q " + temp;
        temp = vars.getUnitConversionsFilename();
        if ((temp != null) && (temp != ""))
            command += " -u " + temp;
        temp = vars.getPresetRegionListFilename();
        if ((temp != null) && (temp != ""))
            command += " -p " + temp;
        temp = vars.getFavoriteQueryFilename();
        if ((temp != null) && (temp != ""))
            command += " -f " + temp;
        temp = vars.getModelInterfaceDir() + File.separator + "map_resources";
        if ((temp != null) && (temp != ""))
            command += " -m " + temp;
        cmd[0] = command;
        System.out
                .println("Starting " + vars.getModelInterfaceJar() + " using database " + vars.getgCamOutputDatabase());
        System.out.println("   cmd:" + cmd[0]);
        try {
            Client.modelInterfaceExecutionThread.submitCommands(cmd);
        } catch (Exception e) {
            utils.warningMessage("Problem starting up ModelInterface.");
            System.out.println("Error in trying to start up ModelInterface:");
            System.out.println(e);
        }
    }

    private void runORDModelInterfaceWhich(String database_name) throws IOException {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        String shell = isWindows ? "cmd.exe /C" : "/bin/sh -c";
        String[] cmd = new String[1];
        String command = shell + " cd " + vars.getModelInterfaceJarDir() + " & java -jar "
                + vars.getModelInterfaceJarDir() + File.separator + vars.getModelInterfaceJar() + " -o "
                + database_name;
        String temp = vars.getQueryFilename();
        if (temp != null)
            command += " -q " + vars.getQueryFilename();
        cmd[0] = command;
        System.out.println("Starting GLIMPSE-ModelInterface...");
        System.out.println("   cmd:" + cmd[0]);
        try {
            Client.modelInterfaceExecutionThread.submitCommands(cmd);
        } catch (Exception e) {
            utils.warningMessage("Problem starting up post-processor.");
            System.out.println("Error in trying to start up post-processor:");
            System.out.println(e);
        }
    }

    private void runORDModelInterfaceAarons() throws IOException {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        ArrayList<String> indCmd = new ArrayList<>();
        String shell = isWindows ? "cmd.exe /C" : "/bin/bash -l -c ";
        String javaToUse = "java";
        String java_home_folder = System.getenv("JAVA_HOME");
        if (java_home_folder != null && java_home_folder.trim().length() > 0) {
            javaToUse = java_home_folder + File.separator + "bin" + File.separator + "java";
        }
        String[] cmd = new String[1];
        String command = javaToUse + " -jar " + vars.getModelInterfaceJarDir() + File.separator
                + vars.getModelInterfaceJar() + " -o " + vars.getgCamOutputDatabase();
        indCmd.add(javaToUse);
        indCmd.add("-jar");
        indCmd.add(vars.getModelInterfaceJarDir() + File.separator + vars.getModelInterfaceJar());
        indCmd.add("-o");
        indCmd.add(vars.getgCamOutputDatabase());
        String temp = vars.getQueryFilename();
        if (temp != null && (temp != "")) {
            command += " -q " + temp;
            indCmd.add("-q");
            indCmd.add(temp);
        }
        temp = vars.getUnitConversionsFilename();
        if ((temp == null) && (temp != "")) {
            temp = vars.getModelInterfaceJarDir() + File.separator + "units_rules.csv";
        }
        command += " -u " + temp;
        indCmd.add("-u");
        indCmd.add(temp);
        temp = vars.getPresetRegionListFilename();
        if ((temp != null) && (temp != "")) {
            command += " -p " + temp;
            indCmd.add("-p");
            indCmd.add(temp);
        }
        cmd[0] = command;
        System.out
                .println("Starting " + vars.getModelInterfaceJar() + " using database " + vars.getgCamOutputDatabase());
        System.out.println("   cmd:" + cmd[0]);
        try {
            Client.modelInterfaceExecutionThread.submitCommands(indCmd.toArray(new String[indCmd.size()]));
        } catch (Exception e) {
            utils.warningMessage("Problem starting up ModelInterface.");
            System.out.println("Error in trying to start up ModelInterface:");
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
        String archiveFoldername = workingDir + File.separator + "archive";
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
        for (String line : config_content) {
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
                    String orig_path = line.substring(start_index, end_index);
                    Path origPath = Paths.get(orig_path);
                    Path exePath = Paths.get(exeDir);
                    Path sourcePath = exePath.resolve(origPath).normalize();
                    String destFilename = workingDir + File.separator + "archive" + File.separator + sourcePath.getFileName();
                    File destFile = new File(workingDir + File.separator + "archive" + File.separator + sourcePath.getFileName());
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
        files.saveFile(new_config_content, archiveConfigFilename);
        String destFilename = archiveFolder + File.separator + "configuration_" + scenName + "_archive.xml";
        files.saveFile(new_config_content, destFilename);
        String zipFolder = workingDir + File.separator + "archive";
        File zipDir = new File(zipFolder);
        String zipFilename = workingDir + File.separator + "archive" + utils.getCurrentTimeStamp() + ".zip";
        File zipFile = new File(zipFilename);
        if (zipFile.exists())
            files.deleteDirectory(zipFile);
        files.zipDirectory(zipDir, zipFilename);
        System.out.println("Done archiving.");
    }

    private void generateExeErrorReport() {
        ArrayList<String> report = new ArrayList<String>();
        ObservableList<ScenarioRow> selectedScenarioRows = ScenarioTable.tableScenariosLibrary.getSelectionModel().getSelectedItems();
        try {
            for (ScenarioRow row : selectedScenarioRows) {
                String scenarioName = "";
                String scenarioMainLog = vars.getgCamExecutableDir() + File.separator + "logs" + File.separator + "main_log.txt";
                File mainlogfile = new File(scenarioMainLog);
                if (mainlogfile.exists()) {
                    ArrayList error_lines = utils.generateErrorReport(scenarioMainLog, scenarioName);
                    report.addAll(error_lines);
                }
            }
        } catch (Exception e) {
            System.out.println("error developing error log:" + e);
        }
        if (report.size() == 0) {
            report.add("No errors reported.");
        }
        utils.displayArrayList(report, "Error Report", false);
    }

    private void generateErrorReport() {
        ArrayList<String> report = new ArrayList<String>();
        ObservableList<ScenarioRow> selectedScenarioRows = ScenarioTable.tableScenariosLibrary.getSelectionModel().getSelectedItems();
        try {
            for (ScenarioRow row : selectedScenarioRows) {
                String scenarioName = "" + row.getScenName();
                String scenarioMainLog = vars.getScenarioDir() + File.separator + scenarioName + File.separator + "main_log.txt";
                File mainlogfile = new File(scenarioMainLog);
                if (mainlogfile.exists()) {
                    ArrayList error_lines = utils.generateErrorReport(scenarioMainLog, scenarioName);
                    report.addAll(error_lines);
                }
            }
        } catch (Exception e) {
            System.out.println("error developing error log:" + e);
        }
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
        for (File scenarioFolder : scenarioFolders) {
            String mainLogFilename = scenarioFolder.getPath() + File.separator + "main_log.txt";
            File logFile = new File(mainLogFilename);
            if (logFile.exists()) {
                mainLogFiles.add(logFile);
            }
        }
        String str = "scenario,created,run,version,#warn,#err,unsolved,errors,completed?,solution(sec),total(sec),components";
        report.add(str);
        for (File main_log : mainLogFiles) {
            String folder_name = main_log.getParent();
            String scenario_pathname = main_log.getParent();
            scenario_name = scenario_pathname.substring(scenario_pathname.lastIndexOf(File.separator) + 1);
            config_file = files.searchForTextInFileS(main_log, "Configuration file:", "#").replace("Configuration file:", "").trim();
            String temp = config_file;
            when_created = files.getLastModifiedInfoForFile(temp);
            when_run = files.getLastModifiedInfoForFile(main_log.toString());
            model_version = files.searchForTextInFileS(main_log, "Running GCAM model", "#").replace("Running GCAM model", "").trim();
            num_warnings = files.countLinesWithTextInFile(main_log, "Warning", "#");
            num_errors = files.countLinesWithTextInFile(main_log, "ERROR", "#");
            not_solved = files.searchForTextInFileS(main_log, "The following model periods did not solve:", "#").replace("The following model periods did not solve:", "").trim().replace(",", ";");
            is_completed = files.searchForTextInFile(main_log, "Model run completed.", "#");
            solution_time = files.searchForTextInFileS(main_log, "Full Scenario", "#").replace("Full Scenario", "").replace(" seconds.", "").trim();
            total_time = files.searchForTextInFileS(main_log, "Data Readin, Model Run & Write Time:", "#").replace("Data Readin, Model Run & Write Time:", "").replace(" seconds.", "").trim();
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
        String report_file = vars.getGlimpseLogDir() + File.separator + "scenario_report.csv";
        files.saveFile(report, report_file);
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
