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
package glimpseUtil;

import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * A utility class for creating and displaying JavaFX FileChooser dialogs.
 * This class provides a simplified and robust way to prompt the user to
 * select a file for opening or saving.
 *
 * This version has been refactored for clarity, correctness, and modern
 * Java 8+ best practices.
 */
public final class FileChooserPlus {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private FileChooserPlus() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Shows a "Save File" dialog.
     *
     * @param ownerWindow      The parent window for the dialog.
     * @param title            The title for the dialog window.
     * @param initialDirectory The directory to open initially.
     * @param initialFileName  The suggested name for the file.
     * @param filter           The extension filter to apply.
     * @return An Optional containing the selected file, or an empty Optional if canceled.
     */
    public static File showSaveDialog(Window ownerWindow, String title, File initialDirectory, String initialFileName, FileChooser.ExtensionFilter filter) {
        FileChooser chooser = createAndConfigureChooser(title, initialDirectory, initialFileName, filter);
        File result = chooser.showSaveDialog(ownerWindow);
        return result;
    }

    /**
     * Shows an "Open File" dialog.
     *
     * @param ownerWindow      The parent window for the dialog.
     * @param title            The title for the dialog window.
     * @param initialDirectory The directory to open initially.
     * @param filter           The extension filter to apply.
     * @return An Optional containing the selected file, or an empty Optional if canceled.
     */
    public static File showOpenDialog(Window ownerWindow, String title, File initialDirectory, FileChooser.ExtensionFilter filter) {
        FileChooser chooser = createAndConfigureChooser(title, initialDirectory, null, filter);
        File result = chooser.showOpenDialog(ownerWindow);
        return result;
    }

    /**
     * Private helper method to create and configure a FileChooser instance.
     */
    private static FileChooser createAndConfigureChooser(String title, File initialDirectory, String initialFileName, FileChooser.ExtensionFilter filter) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        if (initialDirectory != null && initialDirectory.isDirectory()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }

        if (initialFileName != null && !initialFileName.isEmpty()) {
            fileChooser.setInitialFileName(initialFileName);
        }

        if (filter != null) {
            fileChooser.getExtensionFilters().add(filter);
        }

        return fileChooser;
    }

    /**
     * Creates an ExtensionFilter for use with a FileChooser.
     *
     * @param description The textual description for the filter (e.g., "Image Files").
     * @param extensions  The file extensions to include, without the dot (e.g., "jpg", "png").
     * @return A configured FileChooser.ExtensionFilter instance.
     */
    public static FileChooser.ExtensionFilter createExtensionFilter(String description, String... extensions) {
        // Prepend "*." to each extension to create the pattern.
        for (int i = 0; i < extensions.length; i++) {
            extensions[i] = "*." + extensions[i];
        }
        return new FileChooser.ExtensionFilter(description, extensions);
    }
}
