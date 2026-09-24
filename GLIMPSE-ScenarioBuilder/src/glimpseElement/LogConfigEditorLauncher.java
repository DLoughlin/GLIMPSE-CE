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
 */
package glimpseElement;

import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import java.io.File;
import java.util.List;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Standalone launcher for the log configuration editor.
 * <p>
 * This lets the editor be packaged as its own runnable JAR instead of only
 * being opened from the main Scenario Builder application.
 */
public final class LogConfigEditorLauncher extends Application {

    private final GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
    private final GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
    private final GLIMPSEFiles files = GLIMPSEFiles.getInstance();
    private final GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            initializeSharedServices();
            File initialFile = resolveRequestedLogConfigFile(getParameters().getRaw());
            new LogConfigEditorWidget().createAndShow(initialFile);
        } catch (Throwable t) {
            System.err.println("Unable to start the log configuration editor.");
            t.printStackTrace();
            primaryStage.close();
            javafx.application.Platform.exit();
        }
    }

    private void initializeSharedServices() {
        vars.init(utils, vars, styles, files);
        files.init(utils, vars, styles, files);
        utils.init(utils, vars, styles, files);

        String optionsFile = resolveOptionsFilename(getParameters().getRaw());
        if (optionsFile != null && !optionsFile.trim().isEmpty()) {
            vars.loadOptions(optionsFile.trim());
        }
    }

    private String resolveOptionsFilename(List<String> rawArgs) {
        if (rawArgs == null || rawArgs.isEmpty()) {
            return null;
        }
        for (int i = 0; i < rawArgs.size(); i++) {
            String arg = rawArgs.get(i);
            if (arg == null) {
                continue;
            }
            String trimmed = arg.trim();
            if (trimmed.equalsIgnoreCase("-options") && i + 1 < rawArgs.size()) {
                return rawArgs.get(i + 1);
            }
        }
        return null;
    }

    private File resolveRequestedLogConfigFile(List<String> rawArgs) {
        if (rawArgs == null || rawArgs.isEmpty()) {
            return null;
        }
        for (int i = 0; i < rawArgs.size(); i++) {
            String arg = rawArgs.get(i);
            if (arg == null) {
                continue;
            }
            String trimmed = arg.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("-")) {
                continue;
            }
            File candidate = new File(trimmed);
            if (candidate.isFile()) {
                return candidate;
            }
        }
        return null;
    }
}
