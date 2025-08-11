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
* GLIMPSE-CE is a derivative of the open-source USEPA GLIMPSE software.
* For the GLIMPSE project, GCAM development, data processing, and support for 
* policy implementations has been led by Dr. Steven J. Smith of PNNL, via Interagency 
* Agreements 89-92423101 and 89-92549601. Contributors from PNNL include 
* Maridee Weber, Catherine Ledna, Gokul Iyer, Page Kyle, Marshall Wise, Matthew 
* Binsted, and Pralit Patel. 
* The lead GLIMPSE & GLIMPSE- CE developer is Dr. Dan Loughlin (formerly USEPA). 
* Contributors include Tai Wu (USEPA), Farid Alborzi (ORISE), and Aaron Parks and 
* Yadong Xu of ARA through the EPA Environmental Modeling and Visualization 
* Laboratory contract.
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
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import org.controlsfx.control.CheckComboBox;

/**
 * Abstract base class for policy-related tabs in the GLIMPSE Scenario Builder.
 * <p>
 * Provides shared functionality for all scenario component tabs, including:
 * <ul>
 *   <li>Progress tracking for long-running operations (e.g., file generation)</li>
 *   <li>File content and filename suggestion management for scenario component export</li>
 *   <li>Market name uniqueness checking to avoid naming conflicts</li>
 *   <li>Access to shared utility singletons (styles, variables, file and utility helpers)</li>
 * </ul>
 * <p>
 * Subclasses must implement {@link #saveScenarioComponent()} and {@link #loadContent(ArrayList)}
 * to define how scenario components are saved and loaded for each policy type.
 * </p>
 *
 * <h2>Usage Example</h2>
 * <pre>
 * public class TabTechTax extends PolicyTab {
 *     // ... implement abstract methods ...
 * }
 * </pre>
 *
 * <h2>Thread Safety</h2>
 * <p>This class is <b>not</b> thread-safe and should be used only on the JavaFX Application Thread.</p>
 */
public abstract class PolicyTab extends Tab {
    // === Fields for scenario component file management and progress ===
    protected final ProgressBar progressBar = new ProgressBar(0.0); // Progress bar for UI
    protected String filenameSuggestion = null; // Suggested filename for saving
    protected String fileContent = null;        // Content of the file to be saved
    protected List<String> marketList;          // List of unique market names

    // === Singleton utility instances for use by subclasses ===
    protected final GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
    protected final GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
    protected final GLIMPSEFiles files = GLIMPSEFiles.getInstance();
    protected final GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

    /**
     * Save the scenario component. Implemented by subclasses to define how the component is saved.
     */
    public abstract void saveScenarioComponent();

    /**
     * Load content into the tab. Implemented by subclasses to define how content is loaded.
     * @param content List of content lines to load (e.g., from a file)
     */
    public abstract void loadContent(ArrayList<String> content);

    /**
     * Set the progress bar value for long-running operations.
     * @param progress Progress value between 0.0 and 1.0
     */
    public void setProgress(double progress) {
        Platform.runLater(() -> getProgressBar().setProgress(progress));
    }

    /**
     * Get the suggested filename for saving the scenario component.
     * @return Suggested filename, or null if not set
     */
    public String getFilenameSuggestion() {
        return filenameSuggestion;
    }

    /**
     * Get the file content for the scenario component.
     * @return File content string, or null if not set
     */
    public String getFileContent() {
        if (fileContent == null) {
            System.out.println("File content is null.");
            return null;
        }
        //System.out.println("Getting file content... length:" + fileContent.length());
        return fileContent;
    }

    /**
     * Reset the file content to null after saving or cancelling.
     */
    public void resetFileContent() {
        fileContent = null;
    }

    /**
     * Reset the filename suggestion to null after saving or cancelling.
     */
    public void resetFilenameSuggestion() {
        filenameSuggestion = null;
    }

    /**
     * Reset the progress bar to 0 after an operation completes.
     */
    public void resetProgressBar() {
        Platform.runLater(() -> getProgressBar().setProgress(0.0));
    }

    /**
     * Protected constructor for subclassing. Prevents direct instantiation.
     * Subclasses should call this constructor.
     */
    protected PolicyTab() {
        // No-op constructor for subclassing
    }

    /**
     * Generate a unique market name if the given name already exists in the market list.
     * <p>
     * This method checks the scenario components directory for existing market names and appends a numeric suffix if needed.
     * </p>
     * @param marketName The original market name
     * @return A unique market name suffix (e.g., "2"), or empty string if not needed
     */
    public String getUniqueMarketName(String marketName) {
        String result = "";
        File folder = new File(vars.getScenarioComponentsDir());
        String[] fileList = folder.list();
        if (fileList == null) {
            return result;
        }
        if (marketList == null) {
            marketList = new ArrayList<>();
            for (String fileName : fileList) {
                String filePath = vars.getScenarioComponentsDir() + File.separator + fileName;
                File file = new File(filePath);
                if (!file.isDirectory()) {
                    ArrayList<String> lines = files.searchForTextInFileA(filePath, "Mkt", "#");
                    for (String line : lines) {
                        String mktName = utils.getTokenWithText(line, "Mkt", ",");
                        if (!utils.getMatch(mktName, marketList)) {
                            marketList.add(mktName);
                        }
                    }
                }
            }
        }
        int id = 0;
        for (String marketFromList : marketList) {
            if (marketFromList != null && marketFromList.startsWith(marketName)) {
                id++;
            }
        }
        if (id != 0) {
            String uniqueName = marketName + id;
            marketList.add(uniqueName);
            result = String.valueOf(id);
        }
        return result;
    }

    /**
     * Display a warning message to the user (centralized for all tabs).
     * @param message The warning message to display
     */
    public void showWarning(String message) {
        utils.warningMessage(message);
    }

    /**
     * Display an informational message to the user (centralized for all tabs).
     * @param message The info message to display
     * @param title The title for the message dialog
     */
    public void showInfo(String message, String title) {
        utils.displayString(message, title);
    }

    /**
     * Get the progress bar associated with this tab for UI binding.
     * @return ProgressBar instance for this tab
     */
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * Standardized UI component creation methods for tab subclasses.
     */
    protected javafx.scene.control.Label createLabel(String text) {
        return utils.createLabel(text);
    }
    protected Label createLabel(String text, double width) {
        Label label = utils.createLabel(text, width);
        return label;
    }
    protected TextField createTextField() {
        return utils.createTextField();
    }
    protected ComboBox<String> createComboBoxString() {
        return utils.createComboBoxString();
    }
    protected CheckComboBox<String> createCheckComboBox() {
        return utils.createCheckComboBox();
    }
    protected CheckBox createCheckBox(String text) {
        return utils.createCheckBox(text);
    }
    protected Button createButton(String text, int width, EventHandler<ActionEvent> handler) {
        Button button = utils.createButton(text, width, handler);
        if (handler != null) button.setOnAction(handler);
        return button;
    }
    /**
     * Standardized event handler registration for tab subclasses.
     */
    protected void setOnAction(ComboBox<?> comboBox, EventHandler<ActionEvent> handler) {
        comboBox.setOnAction(handler);
    }
    protected void setOnAction(Button button, EventHandler<ActionEvent> handler) {
        button.setOnAction(handler);
    }
    protected void setOnAction(TextField textField, EventHandler<ActionEvent> handler) {
        textField.setOnAction(handler);
    }
    protected void setOnMouseClicked(Label label, EventHandler<javafx.scene.input.MouseEvent> handler) {
        label.setOnMouseClicked(handler);
    }
    protected void setOnAction(CheckBox checkBox, EventHandler<ActionEvent> handler) {
        checkBox.setOnAction(handler);
    }
}