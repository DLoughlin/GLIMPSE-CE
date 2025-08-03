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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * TabXMLList provides the user interface and logic for managing lists of XML files
 * in the GLIMPSE Scenario Builder. This tab allows users to add, remove, and reorder
 * XML scenario components, and view or edit their details.
 *
 * <p>
 * <b>Usage:</b> This class is instantiated as a tab in the scenario builder. It extends {@link PolicyTab}.
 * </p>
 *
 * <p>
 * <b>Thread Safety:</b> This class is not thread-safe and should be used on the JavaFX Application Thread.
 * </p>
 */
public class TabXMLList extends PolicyTab {
    // === Constants ===
    private static final String LABEL_XML_FILES = "XML Files: ";
    private static final String BUTTON_ADD_TEXT = "Add";
    private static final String BUTTON_DELETE_TEXT = "Delete";
    private static final String BUTTON_CLEAR_TEXT = "Clear";
    private static final String BUTTON_MOVE_UP_TEXT = "Move Up";
    private static final String BUTTON_MOVE_DOWN_TEXT = "Move Down";
    private static final String XML_LIST_FILENAME = "xml_list.txt";
    private static final String XML_LIST_TYPE = "@type=xmllist";
    private static final String XML_FILE_EXTENSION = "*.xml";
    private static final String XML_FILE_DESCRIPTION = "XML files (*.xml)";
    private static final String FILECHOOSER_TITLE = "Select xml files";
    private static final String COLUMN_XML_FILENAME = "XML Filename";
    private static final String COLUMN_FORMAT = "-fx-alignment: CENTER-LEFT; -fx-padding: 5 20 5 5;";

    // === UI Components ===
    private final TableView<ComponentRow> tableIncludeXMLList = new TableView<>(ComponentLibraryTable.getListOfFiles());
    private final VBox paneIncludeXMLList = new VBox();
    private final VBox vBoxCenter = new VBox();
    private final HBox hBoxHeaderCenter = new HBox();
    private final Label labelValue = utils.createLabel(LABEL_XML_FILES);
    private final Button buttonAdd = utils.createButton(BUTTON_ADD_TEXT, styles.getBigButtonWidth(), null);
    private final Button buttonDelete = utils.createButton(BUTTON_DELETE_TEXT, styles.getBigButtonWidth(), null);
    private final Button buttonClear = utils.createButton(BUTTON_CLEAR_TEXT, styles.getBigButtonWidth(), null);
    private final Button buttonMoveUp = utils.createButton(BUTTON_MOVE_UP_TEXT, styles.getBigButtonWidth(), null);
    private final Button buttonMoveDown = utils.createButton(BUTTON_MOVE_DOWN_TEXT, styles.getBigButtonWidth(), null);
    private final PaneForComponentDetails paneForXMLList = new PaneForComponentDetails();

    /**
     * Constructs a new TabXMLList instance and initializes the UI components for the XML list tab.
     * Sets up event handlers and populates controls with available data.
     *
     * @param title The title of the tab
     * @param stageX The JavaFX stage
     * @param tableComponents The table of scenario components
     */
    public TabXMLList(String title, Stage stageX, TableView<ComponentRow> tableComponents) {
        // Set tab title and style
        this.setText(title);
        this.setStyle(styles.getFontStyle());

        setupPaneForXMLList();
        setupHeaderButtons();
        setupVBoxCenter();
        setupMainPane();
        setupButtonActions(stageX);

        VBox tabLayout = new VBox();
        tabLayout.getChildren().addAll(paneIncludeXMLList);
        this.setContent(tabLayout);
    }

    /**
     * Sets up the pane for XML list details.
     */
    private void setupPaneForXMLList() {
        paneForXMLList.setColumnNames(null, COLUMN_XML_FILENAME);
        paneForXMLList.setAddItemVisible(false);
        paneForXMLList.setColumnFormatting(COLUMN_FORMAT, COLUMN_FORMAT);
    }

    /**
     * Sets up the header buttons for the tab.
     */
    private void setupHeaderButtons() {
        hBoxHeaderCenter.getChildren().addAll(buttonAdd, buttonMoveUp, buttonMoveDown, buttonDelete, buttonClear);
        hBoxHeaderCenter.setSpacing(2.0);
        hBoxHeaderCenter.setStyle(styles.getStyle3());
    }

    /**
     * Sets up the center VBox layout.
     */
    private void setupVBoxCenter() {
        vBoxCenter.getChildren().addAll(labelValue, hBoxHeaderCenter, paneForXMLList);
        vBoxCenter.setStyle(styles.getStyle2());
        vBoxCenter.setFillWidth(true);
    }

    /**
     * Adds the center VBox to the main pane.
     */
    private void setupMainPane() {
        paneIncludeXMLList.getChildren().addAll(vBoxCenter);
    }

    /**
     * Sets up button actions for the tab.
     * @param stageX The JavaFX stage
     */
    private void setupButtonActions(Stage stageX) {
        buttonClear.setOnAction(e -> paneForXMLList.clearTable());

        buttonAdd.setOnAction(e -> {
            File initialDir = new File(vars.getXmlLibrary());
            FileChooser fileChooser = new FileChooser();
            try {
                if (initialDir != null && initialDir.exists() && initialDir.isDirectory()) {
                    fileChooser.setInitialDirectory(initialDir);
                } else {
                    throw new Exception("Initial directory is invalid");
                }
            } catch (Exception e1) {
                utils.warningMessage("Could not find xmlLibrary.");
                System.out.println("Could not find xmlLibrary " + vars.getXmlLibrary() + ". Defaulting to " + vars.getgCamExecutableDir());
                File fallbackDir = new File(vars.getgCamExecutableDir());
                if (fallbackDir != null && fallbackDir.exists() && fallbackDir.isDirectory()) {
                    fileChooser.setInitialDirectory(fallbackDir);
                }
            }
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(XML_FILE_DESCRIPTION, XML_FILE_EXTENSION);
            fileChooser.setSelectedExtensionFilter(filter);
            fileChooser.setTitle(FILECHOOSER_TITLE);
            List<File> filesSelected = fileChooser.showOpenMultipleDialog(stageX);
            if (filesSelected != null && !filesSelected.isEmpty()) {
                for (File file : filesSelected) {
                    if (file != null && file.toString() != null) {
                        String relPath = files.getRelativePath(vars.getgCamExecutableDir(), file.toString().trim());
                        if (relPath != null) {
                            paneForXMLList.addItem(relPath);
                        }
                    }
                }
            }
        });

        buttonDelete.setOnAction(e -> paneForXMLList.deleteItemsFromTable());
        buttonMoveUp.setOnAction(e -> paneForXMLList.moveItemUpInTable());
        buttonMoveDown.setOnAction(e -> paneForXMLList.moveItemDownInTable());
    }

    /**
     * Loads content into the tab from the provided list of strings.
     *
     * @param content List of content lines to load
     */
    @Override
    public void loadContent(ArrayList<String> content) {
        if (content == null) return;
        int i = 0;
        for (String line : content) {
            if (line != null && !line.startsWith("@")) {
                String str = String.valueOf(i);
                i++;
                paneForXMLList.addItem(str, line);
            }
        }
        if (ComponentLibraryTable.getTableComponents() != null) {
            ComponentLibraryTable.getTableComponents().refresh();
        }
    }

    /**
     * Loads XML list info from a file.
     *
     * @param filename The file to load from
     * @param typeString The type string
     */
    public void loadInfoFromFile(String filename, String typeString) {
        if (filename == null || typeString == null) return;
        ArrayList<String> fileList = files.loadFileListFromFile(filename, typeString);
        if (fileList != null) {
            loadContent(fileList);
        }
    }

    /**
     * Runs the save scenario component logic.
     */
    public void run() {
        saveScenarioComponent();
    }

    /**
     * Saves the scenario component by generating the filename suggestion and file content.
     */
    @Override
    public void saveScenarioComponent() {
        filenameSuggestion = XML_LIST_FILENAME;
        fileContent = XML_LIST_TYPE + vars.getEol();
        ArrayList<String> fileList = paneForXMLList.getValues();
        if (fileList != null) {
            for (String file : fileList) {
                if (file != null) {
                    fileContent += file + vars.getEol();
                }
            }
        }
    }
}
