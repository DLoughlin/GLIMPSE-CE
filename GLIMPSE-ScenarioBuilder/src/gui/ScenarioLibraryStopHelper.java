package gui;

import java.io.File;
import java.util.Optional;

import glimpseUtil.UtilsDialogs;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;

/**
 * Dialog helper that asks how to handle stopping active/queued GCAM runs.
 */
final class ScenarioLibraryStopHelper {

    /** User choice returned from the stop-confirmation dialog. */
    enum StopMode {
        CONTINUE,
        SOFT_STOP,
        STOP_CURRENT,
        STOP_ALL
    }

    private ScenarioLibraryStopHelper() {
    }

    /** Displays stop options and maps the button result to a {@link StopMode}. */
    static StopMode promptForStopMode() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        UtilsDialogs.initDialogOwner(alert);
        alert.setTitle("Stop GCAM run");
        alert.setHeaderText("Stop GCAM?");

        Label msg = new Label(
                "Stopping GCAM may leave partial output files in the scenario folder.\n"
              + "\n"
              + "Choose:\n"
              + " - Stop All: stop the running scenario and cancel queued scenarios\n"
              + " - Stop: stop the currently running scenario only\n"
              + " - Soft Stop: request GCAM stop after the current period ends\n"
              + " - Close: keep running\n");
        msg.setWrapText(true);

        VBox content = new VBox(10);
        content.getChildren().addAll(msg);
        alert.getDialogPane().setContent(content);

        ButtonType stopAllBtn = new ButtonType("Stop All", javafx.scene.control.ButtonBar.ButtonData.OTHER);
        ButtonType stopBtn = ScenarioLibraryModelInterfaceMiniHelper.createOkButton("Stop");
        ButtonType softStopBtn = new ButtonType("Soft Stop", javafx.scene.control.ButtonBar.ButtonData.OTHER);
        ButtonType closeBtn = ScenarioLibraryModelInterfaceMiniHelper.createCancelCloseButton("Close");
        alert.getButtonTypes().setAll(stopAllBtn, softStopBtn, stopBtn, closeBtn);

        ScenarioLibraryModelInterfaceMiniHelper.setDefaultButton(alert.getDialogPane(), closeBtn);
        applyButtonSpacing(alert, stopAllBtn, softStopBtn, stopBtn, closeBtn);

        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent() || result.get() == closeBtn) {
            return StopMode.CONTINUE;
        }
        if (result.get() == stopAllBtn) {
            return StopMode.STOP_ALL;
        }
        if (result.get() == softStopBtn) {
            return StopMode.SOFT_STOP;
        }
        return StopMode.STOP_CURRENT;
    }

    /** Writes the GCAM soft-stop marker file into the executable directory. */
    static boolean requestSoftStop() {
        try {
            String executableDir = GLIMPSEVariables.getInstance().getgCamExecutableDir();
            if (executableDir == null || executableDir.trim().isEmpty()) {
                GLIMPSEUtils.getInstance().warningMessage("Cannot request a soft stop because the GCAM executable directory is not configured.");
                return false;
            }

            File markerFile = new File(ScenarioLibraryPathHelper.exeSoftStopNowFile(executableDir.trim()));
            GLIMPSEFiles.getInstance().saveFile("", markerFile);
            if (!markerFile.exists()) {
                GLIMPSEUtils.getInstance().warningMessage("Unable to write soft-stop-now.txt in the GCAM executable folder.");
                return false;
            }
            return true;
        } catch (Exception ex) {
            GLIMPSEUtils.getInstance().warningMessage("Unable to request a GCAM soft stop: " + ex.getMessage());
            return false;
        }
    }

    /** Removes any previous soft-stop marker so a fresh GCAM run does not inherit it. */
    static void clearSoftStopRequest() {
        try {
            String executableDir = GLIMPSEVariables.getInstance().getgCamExecutableDir();
            if (executableDir == null || executableDir.trim().isEmpty()) {
                return;
            }
            File markerFile = new File(ScenarioLibraryPathHelper.exeSoftStopNowFile(executableDir.trim()));
            if (markerFile.exists()) {
                GLIMPSEFiles.getInstance().deleteFile(markerFile);
            }
        } catch (Exception ignored) {
        }
    }

    private static void applyButtonSpacing(Alert alert, ButtonType stopAllBtn, ButtonType softStopBtn, ButtonType stopBtn, ButtonType closeBtn) {
        try {
            if (alert == null || alert.getDialogPane() == null) {
                return;
            }
            javafx.scene.Node stopAllNode = alert.getDialogPane().lookupButton(stopAllBtn);
            javafx.scene.Node softStopNode = alert.getDialogPane().lookupButton(softStopBtn);
            javafx.scene.Node stopNode = alert.getDialogPane().lookupButton(stopBtn);
            javafx.scene.Node closeNode = alert.getDialogPane().lookupButton(closeBtn);

            if (stopAllNode != null) {
                HBox.setMargin(stopAllNode, new Insets(0, 8, 0, 0));
            }
            if (softStopNode != null) {
                HBox.setMargin(softStopNode, new Insets(0, 8, 0, 0));
            }
            if (stopNode != null) {
                HBox.setMargin(stopNode, new Insets(0, 14, 0, 0));
            }
            if (closeNode != null) {
                HBox.setMargin(closeNode, Insets.EMPTY);
            }
        } catch (Exception ignored) {
        }
    }
}