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
package gui;

import java.io.File;
import java.util.List;

import org.controlsfx.control.StatusBar;

import glimpseBuilder.SetupMenuTools;
import glimpseBuilder.SetupMenuFile;
import glimpseBuilder.SetupMenuHelp;
import glimpseBuilder.SetupMenuView;
import glimpseBuilder.SetupMenuEdit;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEVariables;

public class Client extends Application {

	// class variables which are used in all of the subclasses and methods.
	public static Stage primaryStage;
	public static String optionsFilename = null;
	//public static String runQueueStr = "Queue is empty.";
	public static boolean exit_on_exception = false;

	// GUI Panels
	public static PaneCreateScenario paneCreateScenario;
	public static PaneScenarioLibrary paneWorkingScenarios;
	public static PaneNewScenarioComponent paneCandidateComponents;

	// Arrow buttons between the top right/left pane
	public static Button buttonRightArrow;
	public static Button buttonLeftArrow;
	public static Button buttonLeftDoubleArrow;
	public static Button buttonEditScenario;

	// Buttons on the top left pane
	public static Button buttonDeleteComponent;
	public static Button buttonRefreshComponents;
	public static Button buttonNewComponent;
	public static Button buttonEditComponent;
	public static Button buttonBrowseComponentLibrary;

	// Buttons on the top right pane
	public static Button buttonMoveComponentUp;
	public static Button buttonMoveComponentDown;
	public static Button buttonCreateScenarioConfigFile;

	// Buttons on the bottom pane
	public static Button buttonViewConfig;
	public static Button buttonViewLog;
	public static Button buttonViewExeLog;
	public static Button buttonViewErrors;
	public static Button buttonViewExeErrors;
	public static Button buttonBrowseScenarioFolder;
	public static Button buttonImportScenario;
	public static Button buttonDiffFiles;
	public static Button buttonShowRunQueue;
	public static Button buttonRefreshScenarioStatus;
	public static Button buttonDeleteScenario;
	public static Button buttonRunScenario;
	public static Button buttonResults;
	public static Button buttonResultsForSelected;
	public static Button buttonArchiveScenario;
	public static Button buttonReport;
	public static Button buttonExamineScenario;

	// GCAM threads
	public static GCAMExecutionThread gCAMExecutionThread;
	public static GCAMExecutionThread gCAMPPExecutionThread;

	//
	private ScenarioBuilder scenarioBuilder = ScenarioBuilder.getInstance();
	private GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	private GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	private GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	private glimpseUtil.GLIMPSEUtils utils = glimpseUtil.GLIMPSEUtils.getInstance();
 
	StatusBar sb=new StatusBar();

	// MAIN
	// ///////////////////////////////////////////////////////////////////////////////////
	// Main method which initiates the app and calls the start method described
	// below.
	// ///////////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		//added following line to address issue on VMs that caused JavaFX to shutdown when WM_ENDSESSION was called
		Platform.setImplicitExit(false);
		launch(args);
	}

	@Override
	public void init() throws Exception {
		System.out.println("Loading settings and initializing.");

		// gives utility/variable objects ability to talk to eachother
		vars.init(utils, vars, styles, files);
		files.init(utils, vars, styles, files);
		utils.init(utils, vars, styles, files);

		// reads command-line arguments
		processArgs();

		// loads options into the vars singleton
		vars.loadOptions(optionsFilename);
		
		String setup=vars.examineGLIMPSESetup();
		if (setup.length()>0) {
			System.out.println(setup);
		}
		
		utils.resetLogFile(utils.getComputerStatString());

		// loads data files into files singleton
		files.loadFiles();
		
		utils.sb=this.sb;

	}

	// START //
	// ///////////////////////////////////////////////////////////////////////////////////
	// this method initiates the UI. This is the root method of UI in JAVAFX. It
	// contains all of the panes and technically whatever you see in UI.
	// //////////////////////////////////////////////////////////////////////////////////
	@Override
	public void start(Stage primaryStage) {

		System.out.println("Starting GLIMPSE Graphical User Interface...");

		// Record Current stage
		Client.primaryStage = primaryStage;
		
		primaryStage.setOnCloseRequest(event -> {
		    Client.gCAMExecutionThread.status.terminate();
		    Client.gCAMPPExecutionThread.status.terminate();
			Client.gCAMExecutionThread.shutdownNow();
		    Client.gCAMPPExecutionThread.shutdownNow();
		    //System.exit(0);
		    Platform.exit();
		});
		
		// build panels
		scenarioBuilder.build();

		// Creates the menu at the top of the GUI
		// Sets the title of the main window and its size
		setMainWindow(combineAllElementsIntoOnePane(), createMenuBar());

		// Sets up execution threads
		setupExecutionThreads();

		String iconfile = "file:" + vars.getgCamGUIDir() + File.separator + "resources" + File.separator
				+ "GLIMPSE_icon.png";
		primaryStage.getIcons().add(new Image(iconfile));
		


	}
	
	

	private void processArgs() {
		final Parameters params = getParameters();
		final List<String> paramList = params.getRaw();

		if (!paramList.isEmpty()) {
			if (paramList.size() == 1) {
				optionsFilename = paramList.get(0);
			} else {
				for (int i = 0; i < paramList.size(); i++) {
					if (paramList.get(i).toLowerCase().equals("-options")) {
						optionsFilename = paramList.get(i + 1);
						break;
					}
				}
			}
		}
	}

	private MenuBar createMenuBar() {

		// /////////// Menu setup
		MenuBar menuBar = new MenuBar();

		// ////////// File menu
		Menu menuFile = new Menu("File");
		SetupMenuFile setupMenuFile = new SetupMenuFile();
		setupMenuFile.setup(menuFile);

		// ////////// Edit menu
		Menu menuEdit = new Menu("Edit");
		SetupMenuEdit setupMenuEdit = new SetupMenuEdit();
		setupMenuEdit.setup(menuEdit);
		
		// ////////// Actions menu
		Menu menuTools = new Menu("Tools");
		SetupMenuTools setupMenuTools = new SetupMenuTools();
		setupMenuTools.setup(menuTools);

		// ////////// View menu
		Menu menuView = new Menu("View");
		SetupMenuView setupMenuView = new SetupMenuView();
		setupMenuView.setup(menuView);

		// ////////// Help menu
		Menu menuHelp = new Menu("Help");
		SetupMenuHelp setupMenuHelp = new SetupMenuHelp();
		setupMenuHelp.setup(menuHelp);

		// Adds the menu elements to the menubar
		menuBar.getMenus().addAll(menuFile, menuEdit, menuView, menuTools, menuHelp);
		return menuBar;
	}
	


	private GridPane combineAllElementsIntoOnePane() {

		GridPane mainGridPane = new GridPane();
		mainGridPane.add(scenarioBuilder.getvBoxComponentLibrary(), 0, 0);
		mainGridPane.add(scenarioBuilder.getvBoxButton(), 1, 0);
		mainGridPane.add(scenarioBuilder.getvBoxCreateScenario(), 3, 0);

		HBox stack = new HBox(20);
		stack.getChildren().addAll(scenarioBuilder.getvBoxRun());
		stack.prefWidthProperty().bind(primaryStage.widthProperty().multiply(1));
		stack.setStyle(styles.style1);
		mainGridPane.add(stack, 0, 1, 4, 1);
		return mainGridPane;
	}

	private void setMainWindow(GridPane mainGridPane, MenuBar menuBar) {
		// Creates and sets the default size of the main window

		Scene scene = new Scene(new VBox(), vars.ScenarioBuilderWidth, vars.ScenarioBuilderHeight);
		// loadSplashScreen();
		
		((VBox) scene.getRoot()).getChildren().addAll(menuBar, mainGridPane,sb);

		primaryStage.setScene(scene);
		primaryStage.setTitle("GLIMPSE Scenario Builder");
		primaryStage.setMinHeight(650);
		primaryStage.setHeight(650);
		//primaryStage.setMinWidth(900);
		primaryStage.setMinWidth(955);
		primaryStage.setWidth(955);
		
		primaryStage.show();
		if (vars.getShowSplash())
			loadSplashScreen();
	}

	private void setupExecutionThreads() {
		// starting separate execution queues for GCAM and postprocessor

		gCAMExecutionThread = new GCAMExecutionThread();
		gCAMPPExecutionThread = new GCAMExecutionThread();

		gCAMExecutionThread.startUpExecutorSingle();
		gCAMPPExecutionThread.startUpExecutorMulti();
	}
	


	private boolean loadSplashScreen() {
		try {
			// Load splash screen view FXML
			// StackPane pane = FXMLLoader.load(getClass().getResource(("splash.fxml")));
			Stage splashStage = new Stage();
			Scene splashScene = new Scene(new VBox(), 383., 384.);
			splashScene.setFill(Color.TRANSPARENT);
			splashStage.setScene(splashScene);
			splashStage.centerOnScreen();
			splashStage.initOwner(primaryStage);
			splashStage.initModality(Modality.WINDOW_MODAL);
			splashStage.initStyle(StageStyle.UNDECORATED);
			splashStage.initStyle(StageStyle.TRANSPARENT);
			splashStage.setOpacity(0.9);

			GridPane pane = new GridPane();
			// pane.setStyle("-fx-background-color: rgba(255, 255, 255, 1);");

			String image_path = vars.getgCamGUIDir() + File.separator + "resources" + File.separator
					+ "glimpse-splash.png";
			
			Image image = new Image("File:" + image_path);
			if (image.isError()) {
				System.out.println("Could not find splash graphic. Continuing.");
				return false; 
			}
			
			pane.getChildren().add(new ImageView(image));

			VBox v = ((VBox) splashStage.getScene().getRoot());
			v.getChildren().add(pane);
			v.setStyle("-fx-background-color: rgba(255, 255, 255, 0);");
			// ((VBox) splashStage.getScene().getRoot()).getChildren().add(pane);

			splashStage.show();

			// Load splash screen with fade in effect
			FadeTransition fadeIn = new FadeTransition(Duration.seconds(3), pane);
			fadeIn.setFromValue(0.1);
			fadeIn.setToValue(1);
			fadeIn.setCycleCount(1);

			// Finish splash with fade out effect
			FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), pane);
			fadeOut.setFromValue(1);
			fadeOut.setToValue(0);
			fadeOut.setCycleCount(1);

			fadeIn.play();

			// After fade in, start fade out
			fadeIn.setOnFinished((e) -> {
				// Do the loading tasks
				try {
					;// Thread.sleep(5000);
				} catch (Exception ex) {
					;
				}
				fadeOut.play();
			});

			// After fade out, load actual content
			fadeOut.setOnFinished((e) -> {
				try {
					splashStage.hide();
					// splashStage=null;
				} catch (Exception ex1) {
					;
				}
			});
		} catch (Exception ex) {
			;// Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
		}
		return true;
	}
	
	protected void setStatusBarText(String text) {
		sb.setText(text);
	}

}
