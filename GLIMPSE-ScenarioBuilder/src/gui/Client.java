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
 * and that User is not otherwise prohibited
 * under the Export Laws from receiving the Software.
 *
 * SUPPORT
 * For the GLIMPSE project, GCAM development, data processing, and support for
 * policy implementations has been led by Dr. Steven J. Smith of PNNL, via Interagency
 * Agreements 89-92423101 and 89-92549601. Contributors * from PNNL include
 * Maridee Weber, Catherine Ledna, Gokul Iyer, Page Kyle, Marshall Wise, Matthew
 * Binsted, and Pralit Patel. Coding contributions have also been made by Aaron
 * Parks and Yadong Xu of ARA through the EPAs Environmental Modeling and
 * Visualization Laboratory contract.
 *
 */
package gui;

import glimpseBuilder.SetupMenuEdit;
import glimpseBuilder.SetupMenuFile;
import glimpseBuilder.SetupMenuHelp;
import glimpseBuilder.SetupMenuTools;
import glimpseBuilder.SetupMenuView;
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEVariables;
import java.io.File;
import java.util.List;
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
import org.controlsfx.control.StatusBar;

public class Client extends Application {

    // region Constants
    private static final double MIN_WINDOW_HEIGHT = 650;
    private static final double MIN_WINDOW_WIDTH = 955;
    private static final double SPLASH_WIDTH = 383.0;
    private static final double SPLASH_HEIGHT = 384.0;
    private static final String OPTIONS_ARG_FLAG = "-options";
    // endregion

    // region Static Fields
    static Stage primaryStage;
    private static String optionsFilename = null;
    public static boolean exit_on_exception = false; // Retained public for potential external access
    // endregion

    // region GUI Panels
    static PaneCreateScenario paneCreateScenario;
    static PaneScenarioLibrary paneWorkingScenarios;
    static PaneNewScenarioComponent paneCandidateComponents;
    // endregion

    // region GUI Buttons
    // Arrow buttons between the top right/left pane
    static Button buttonRightArrow;
    static Button buttonLeftArrow;
    static Button buttonLeftDoubleArrow;
    static Button buttonEditScenario;

    // Buttons on the top left pane
    static Button buttonDeleteComponent;
    static Button buttonRefreshComponents;
    static Button buttonNewComponent;
    static Button buttonEditComponent;
    static Button buttonBrowseComponentLibrary;

    // Buttons on the top right pane
    static Button buttonMoveComponentUp;
    static Button buttonMoveComponentDown;
    static Button buttonCreateScenarioConfigFile;

    // Buttons on the bottom pane
    static Button buttonViewConfig;
    static Button buttonViewLog;
    static Button buttonViewExeLog;
    static Button buttonViewErrors;
    static Button buttonViewExeErrors;
    static Button buttonBrowseScenarioFolder;
    public static Button buttonImportScenario;
    static Button buttonDiffFiles;
    static Button buttonShowRunQueue;
    public static Button buttonRefreshScenarioStatus;
    static Button buttonDeleteScenario;
    static Button buttonRunScenario;
    static Button buttonResults;
    static Button buttonResultsForSelected;
    public static Button buttonArchiveScenario;
    static Button buttonReport;
    public static Button buttonExamineScenario;
    // endregion

    // region GCAM Threads
    public static GCAMExecutionThread gCAMExecutionThread;
    public static GCAMExecutionThread modelInterfaceExecutionThread;
    // endregion

    // region Instance Variables
    private final ScenarioBuilder scenarioBuilder = ScenarioBuilder.getInstance();
    private final GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
    private final GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
    private final GLIMPSEFiles files = GLIMPSEFiles.getInstance();
    private final glimpseUtil.GLIMPSEUtils utils = glimpseUtil.GLIMPSEUtils.getInstance();
    private final StatusBar sb = new StatusBar();
    // endregion

    /**
     * Main method which initiates the app and calls the start method.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Added following line to address issue on VMs that caused JavaFX to shutdown when WM_ENDSESSION was called.
        Platform.setImplicitExit(false);
        launch(args);
    }

    @Override
    public void init() throws Exception {
        System.out.println("Loading settings and initializing.");

        // Gives utility/variable objects ability to talk to each other.
        vars.init(utils, vars, styles, files);
        files.init(utils, vars, styles, files);
        utils.init(utils, vars, styles, files);

        // Reads command-line arguments.
        processArgs();

        // Loads options into the vars singleton.
        vars.loadOptions(optionsFilename);

        final String setup = vars.examineGLIMPSESetup();
        if (setup.length() > 0) {
            System.out.println(setup);
        }

        utils.resetLogFile(utils.getComputerStatString());

        // Loads data files into files singleton.
        files.loadFiles();
        utils.sb = this.sb;
    }

    /**
     * This method initiates the UI. This is the root method of UI in JavaFX.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        System.out.println("Starting GLIMPSE Graphical User Interface...");

        Client.primaryStage = primaryStage;

        primaryStage.setOnCloseRequest(event -> {
            Client.gCAMExecutionThread.status.terminate();
            Client.modelInterfaceExecutionThread.status.terminate();
            Client.gCAMExecutionThread.shutdownNow();
            Client.modelInterfaceExecutionThread.shutdownNow();
            Platform.exit();
        });

        // Build GUI panels.
        scenarioBuilder.build();

        // Creates the menu at the top of the GUI and sets up the main window.
        setMainWindow(combineAllElementsIntoOnePane(), createMenuBar());

        // Sets up execution threads.
        setupExecutionThreads();

        final String iconFile = "file:" + vars.getGlimpseResourceDir() + File.separator + "GLIMPSE_icon.png";
        primaryStage.getIcons().add(new Image(iconFile));
    }

    private void processArgs() {
        final Parameters params = getParameters();
        final List<String> paramList = params.getRaw();

        if (paramList.isEmpty()) {
            return;
        }

        if (paramList.size() == 1) {
            optionsFilename = paramList.get(0);
        } else {
            for (int i = 0; i < paramList.size(); i++) {
                if (OPTIONS_ARG_FLAG.equalsIgnoreCase(paramList.get(i)) && i + 1 < paramList.size()) {
                    optionsFilename = paramList.get(i + 1);
                    break;
                }
            }
        }
    }

    private MenuBar createMenuBar() {
        final MenuBar menuBar = new MenuBar();

        // File menu
        final Menu menuFile = new Menu("File");
        new SetupMenuFile().setup(menuFile);

        // Edit menu
        final Menu menuEdit = new Menu("Edit");
        new SetupMenuEdit().setup(menuEdit);

        // Tools menu
        final Menu menuTools = new Menu("Tools");
        new SetupMenuTools().setup(menuTools);

        // View menu
        final Menu menuView = new Menu("View");
        new SetupMenuView().setup(menuView);

        // Help menu
        final Menu menuHelp = new Menu("Help");
        new SetupMenuHelp().setup(menuHelp);

        // Adds the menu elements to the menubar.
        menuBar.getMenus().addAll(menuFile, menuEdit, menuView, menuTools, menuHelp);
        return menuBar;
    }

    private GridPane combineAllElementsIntoOnePane() {
        final GridPane mainGridPane = new GridPane();
        mainGridPane.add(scenarioBuilder.getvBoxComponentLibrary(), 0, 0);
        mainGridPane.add(scenarioBuilder.getvBoxButton(), 1, 0);
        mainGridPane.add(scenarioBuilder.getvBoxCreateScenario(), 3, 0);

        final HBox stack = new HBox(20);
        stack.getChildren().addAll(scenarioBuilder.getvBoxRun());
        stack.prefWidthProperty().bind(primaryStage.widthProperty());
        stack.setStyle(styles.getStyle1());
        mainGridPane.add(stack, 0, 1, 4, 1);

        return mainGridPane;
    }

    private void setMainWindow(GridPane mainGridPane, MenuBar menuBar) {
        final VBox root = new VBox(menuBar, mainGridPane, sb);
        final Scene scene = new Scene(root, vars.ScenarioBuilderWidth, vars.ScenarioBuilderHeight);

        primaryStage.setScene(scene);
        primaryStage.setTitle("GLIMPSE Scenario Builder");
        primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);
        primaryStage.setHeight(MIN_WINDOW_HEIGHT);
        primaryStage.setMinWidth(MIN_WINDOW_WIDTH);
        primaryStage.setWidth(MIN_WINDOW_WIDTH);
        primaryStage.show();

        if (vars.getShowSplash()) {
            loadSplashScreen();
        }
    }

    private void setupExecutionThreads() {
        // Starting separate execution queues for GCAM and post-processor.
        gCAMExecutionThread = new GCAMExecutionThread();
        modelInterfaceExecutionThread = new GCAMExecutionThread();

        gCAMExecutionThread.startUpExecutorSingle();
        modelInterfaceExecutionThread.startUpExecutorMulti();
    }

    private boolean loadSplashScreen() {
        try {
            final Stage splashStage = new Stage();
            final VBox splashRoot = new VBox();
            final Scene splashScene = new Scene(splashRoot, SPLASH_WIDTH, SPLASH_HEIGHT, Color.TRANSPARENT);

            splashStage.setScene(splashScene);
            splashStage.centerOnScreen();
            splashStage.initOwner(primaryStage);
            splashStage.initModality(Modality.WINDOW_MODAL);
            splashStage.initStyle(StageStyle.TRANSPARENT);
            splashStage.setOpacity(0.9);

            final GridPane pane = new GridPane();
            final String imagePath = "File:" + vars.getGlimpseDir() + File.separator + "resources" + File.separator + "glimpse-splash.png";
            final Image image = new Image(imagePath);

            if (image.isError()) {
                System.err.println("Could not find splash graphic. Continuing without splash screen.");
                return false;
            }

            pane.getChildren().add(new ImageView(image));
            splashRoot.getChildren().add(pane);
            splashRoot.setStyle("-fx-background-color: transparent;");
            splashStage.show();

            // Fade in effect
            final FadeTransition fadeIn = new FadeTransition(Duration.seconds(3), pane);
            fadeIn.setFromValue(0.1);
            fadeIn.setToValue(1);

            // Fade out effect
            final FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), pane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            // After fade in, start fade out
            fadeIn.setOnFinished(e -> fadeOut.play());

            // After fade out, hide the splash screen
            fadeOut.setOnFinished(e -> splashStage.hide());

            fadeIn.play();

        } catch (Exception ex) {
            System.err.println("An error occurred while loading the splash screen.");
            ex.printStackTrace();
        }
        return true;
    }

    protected void setStatusBarText(String text) {
        sb.setText(text);
    }
    
    // region Getters for private static fields
    public static Stage getPrimaryStage() { return primaryStage; }
    public static String getOptionsFilename() { return optionsFilename; }
    public static PaneCreateScenario getPaneCreateScenario() { return paneCreateScenario; }
    public static PaneScenarioLibrary getPaneWorkingScenarios() { return paneWorkingScenarios; }
    public static PaneNewScenarioComponent getPaneCandidateComponents() { return paneCandidateComponents; }
    public static Button getButtonRightArrow() { return buttonRightArrow; }
    public static Button getButtonLeftArrow() { return buttonLeftArrow; }
    public static Button getButtonLeftDoubleArrow() { return buttonLeftDoubleArrow; }
    public static Button getButtonEditScenario() { return buttonEditScenario; }
    public static Button getButtonDeleteComponent() { return buttonDeleteComponent; }
    public static Button getButtonRefreshComponents() { return buttonRefreshComponents; }
    public static Button getButtonNewComponent() { return buttonNewComponent; }
    public static Button getButtonEditComponent() { return buttonEditComponent; }
    public static Button getButtonBrowseComponentLibrary() { return buttonBrowseComponentLibrary; }
    public static Button getButtonMoveComponentUp() { return buttonMoveComponentUp; }
    public static Button getButtonMoveComponentDown() { return buttonMoveComponentDown; }
    public static Button getButtonCreateScenarioConfigFile() { return buttonCreateScenarioConfigFile; }
    public static Button getButtonViewConfig() { return buttonViewConfig; }
    public static Button getButtonViewLog() { return buttonViewLog; }
    public static Button getButtonViewExeLog() { return buttonViewExeLog; }
    public static Button getButtonViewErrors() { return buttonViewErrors; }
    public static Button getButtonViewExeErrors() { return buttonViewExeErrors; }
    public static Button getButtonBrowseScenarioFolder() { return buttonBrowseScenarioFolder; }
    public static Button getButtonImportScenario() { return buttonImportScenario; }
    public static Button getButtonDiffFiles() { return buttonDiffFiles; }
    public static Button getButtonShowRunQueue() { return buttonShowRunQueue; }
    public static Button getButtonRefreshScenarioStatus() { return buttonRefreshScenarioStatus; }
    public static Button getButtonDeleteScenario() { return buttonDeleteScenario; }
    public static Button getButtonRunScenario() { return buttonRunScenario; }
    public static Button getButtonResults() { return buttonResults; }
    public static Button getButtonResultsForSelected() { return buttonResultsForSelected; }
    public static Button getButtonArchiveScenario() { return buttonArchiveScenario; }
    public static Button getButtonReport() { return buttonReport; }
    public static Button getButtonExamineScenario() { return buttonExamineScenario; }
    public static GCAMExecutionThread getgCAMExecutionThread() { return gCAMExecutionThread; }
    public static GCAMExecutionThread getgCAMPPExecutionThread() { return modelInterfaceExecutionThread; }
    // endregion
}
