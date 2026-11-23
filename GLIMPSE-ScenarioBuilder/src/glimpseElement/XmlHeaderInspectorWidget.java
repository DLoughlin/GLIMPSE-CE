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

import glimpseUtil.DOMTreeBuilder;
import glimpseUtil.FileChooserPlus;
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEVariables;
import glimpseUtil.Header;
import glimpseUtil.UtilsDialogs;
import gui.Client;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.util.converter.DefaultStringConverter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

/**
 * Editor/inspector for CSV-to-XML header definitions used by ScenarioBuilder.
 */
public class XmlHeaderInspectorWidget {

    private static final double INITIAL_DIALOG_WIDTH = 1120;
    private static final double INITIAL_DIALOG_HEIGHT = 720;
    private static final double MIN_DIALOG_WIDTH = 570;
    private static final double MIN_DIALOG_HEIGHT = 480;
    private static final String WINDOW_PREF_KEY_PREFIX = "xml.header.inspector.window";

    private final GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
    private final GLIMPSEFiles files = GLIMPSEFiles.getInstance();
    private final GLIMPSEStyles styles = GLIMPSEStyles.getInstance();

    private final ObservableList<HeaderEntry> allHeaderEntries = FXCollections.observableArrayList();
    private final ObservableList<HeaderEntry> filteredHeaderEntries = FXCollections.observableArrayList();
    private final ListView<HeaderEntry> headerListView = new ListView<HeaderEntry>(filteredHeaderEntries);
    private final TextField headerFilterField = new TextField();

    private final Label loadedFileLabel = new Label("Loaded file: --");
    private final Label statusLabel = new Label("Status: --");

    private final TextArea headerTextArea = new TextArea();
    private final TableView<ObservableList<String>> tableView = new TableView<ObservableList<String>>();
    private final TextArea previewHeaderTextArea = new TextArea();
    private final TextArea previewArea = new TextArea();

    private final List<String> fileLines = new ArrayList<String>();

    private Stage stage;
    private File currentFile;
    private boolean updatingUi;
    private int dragAnchorRow = -1;
    private int dragAnchorCol = -1;

    public void createAndShow() {
        stage = new Stage();
        stage.setTitle("XML Header Inspector");
        try {
            Window owner = UtilsDialogs.getPrimaryOwnerWindow();
            if (owner != null) {
                stage.initOwner(owner);
                stage.initModality(Modality.WINDOW_MODAL);
            } else {
                stage.initModality(Modality.NONE);
            }
        } catch (Exception ignored) {
            stage.initModality(Modality.NONE);
        }

        BorderPane root = buildRoot();
        applyAppFontStyle(root);

        Scene scene = new Scene(root, INITIAL_DIALOG_WIDTH, INITIAL_DIALOG_HEIGHT);
        gui.ScenarioBuilder.applyModernTheme(scene);
        Client.registerSceneForRuntimeFontSize(scene);

        stage.setScene(scene);
        stage.setMinWidth(MIN_DIALOG_WIDTH);
        stage.setMinHeight(MIN_DIALOG_HEIGHT);
        Client.applyPersistedDialogBounds(stage, WINDOW_PREF_KEY_PREFIX,
                MIN_DIALOG_WIDTH, MIN_DIALOG_HEIGHT,
                INITIAL_DIALOG_WIDTH, INITIAL_DIALOG_HEIGHT);
        stage.setOnCloseRequest(e -> {
            if (!confirmDiscardIfDirty("Close inspector and discard unsaved header expression changes?")) {
                e.consume();
            }
        });
        stage.setOnHidden(e -> Client.persistDialogBounds(stage, WINDOW_PREF_KEY_PREFIX,
                MIN_DIALOG_WIDTH, MIN_DIALOG_HEIGHT));

        File defaultFile = resolveDefaultHeaderFile();
        if (defaultFile != null) {
            loadFromFile(defaultFile);
        } else {
            setStatus("No default XML header file found. Use Open to load one.", false);
            updateHeaderLabels();
        }

        stage.show();
    }

    private BorderPane buildRoot() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        root.setTop(buildHeader());
        root.setCenter(buildMainArea());
        root.setBottom(buildFooter());

        return root;
    }

    private VBox buildHeader() {
        Button openButton = new Button("Open");
        Button revertButton = new Button("Revert");
        Button openTextButton = new Button("Open in Text Editor");
        Button saveButton = new Button("Save");
        Button saveAsButton = new Button("Save As");

        openButton.setOnAction(e -> openHeaderFile());
        saveButton.setOnAction(e -> saveCurrentFile());
        saveAsButton.setOnAction(e -> saveAsNewFile());
        revertButton.setOnAction(e -> {
            if (currentFile != null && confirmDiscardIfDirty("Revert to disk version and discard unsaved header expression changes?")) {
                loadFromFile(currentFile);
            }
        });
        openTextButton.setOnAction(e -> {
            if (currentFile != null) {
                files.showFileInTextEditor(currentFile.getAbsolutePath());
            }
        });

        HBox buttonRow = new HBox(8, openButton, revertButton, openTextButton, saveButton, saveAsButton);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        VBox header = new VBox(6, buttonRow, loadedFileLabel, statusLabel);
        header.setPadding(new Insets(0, 0, 10, 0));
        return header;
    }

    private BorderPane buildMainArea() {
        BorderPane pane = new BorderPane();
        pane.setLeft(buildHeaderListPane());
        pane.setCenter(buildRightPane());
        return pane;
    }

    private VBox buildHeaderListPane() {
        headerFilterField.setPromptText("Filter headers");
        headerFilterField.textProperty().addListener((obs, oldVal, newVal) -> applyHeaderFilter());

        headerListView.setCellFactory(list -> new ListCell<HeaderEntry>() {
            @Override
            protected void updateItem(HeaderEntry item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.tableId);
            }
        });

        headerListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            persistCurrentSelectionState(oldSel);
            showSelectedHeader(newSel);
        });

        VBox pane = new VBox(6, new Label("Headers"), headerFilterField, headerListView);
        pane.setPadding(new Insets(0, 10, 0, 0));
        pane.setPrefWidth(320);
        VBox.setVgrow(headerListView, Priority.ALWAYS);
        return pane;
    }

    private void applyHeaderFilter() {
        HeaderEntry selected = headerListView.getSelectionModel().getSelectedItem();
        String query = safe(headerFilterField.getText()).trim().toLowerCase(Locale.ROOT);

        filteredHeaderEntries.clear();
        for (HeaderEntry entry : allHeaderEntries) {
            if (entry == null) {
                continue;
            }
            String haystack = (safe(entry.tableId) + " " + safe(entry.headerExpression)).toLowerCase(Locale.ROOT);
            if (query.isEmpty() || haystack.contains(query)) {
                filteredHeaderEntries.add(entry);
            }
        }

        if (filteredHeaderEntries.isEmpty()) {
            headerListView.getSelectionModel().clearSelection();
            clearDetailPanels();
            return;
        }

        if (selected != null && filteredHeaderEntries.contains(selected)) {
            headerListView.getSelectionModel().select(selected);
        } else {
            headerListView.getSelectionModel().select(0);
        }
    }

    private TabPane buildRightPane() {
        headerTextArea.setWrapText(true);
        headerTextArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!updatingUi) {
                markHeaderExpressionChanged("Header expression updated. Click 'Update Table' to rebuild expected columns.");
            }
        });

        tableView.setEditable(true);
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // Keep all columns at equal width and fill the full table width.
        tableView.widthProperty().addListener((obs, oldVal, newVal) -> applyEqualColumnWidths());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableView.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if ((e.getCode() == KeyCode.V && e.isControlDown())
                    || (e.getCode() == KeyCode.INSERT && e.isShiftDown())) {
                if (pasteIntoTableFromClipboard()) {
                    e.consume();
                }
            } else if ((e.getCode() == KeyCode.C && e.isControlDown())
                    || (e.getCode() == KeyCode.INSERT && e.isControlDown())) {
                if (copySelectionToClipboard()) {
                    e.consume();
                }
            } else if (e.getCode() == KeyCode.X && e.isControlDown()) {
                if (cutSelectionToClipboard()) {
                    e.consume();
                }
            } else if (e.getCode() == KeyCode.DELETE || e.getCode() == KeyCode.BACK_SPACE) {
                if (deleteSelectedRows()) {
                    e.consume();
                }
            } else if (e.getCode() == KeyCode.A && e.isControlDown()) {
                tableView.getSelectionModel().selectAll();
                e.consume();
            }
        });
        tableView.setContextMenu(createTableContextMenu());

        previewArea.setEditable(false);
        previewArea.setWrapText(false);
        previewHeaderTextArea.setEditable(false);
        previewHeaderTextArea.setWrapText(true);

        Button updateTableButton = new Button("Update Table");
        updateTableButton.setOnAction(e -> applyHeaderTextAndRebuildTable());

        Button addRowButton = new Button("Add Row");
        addRowButton.setOnAction(e -> {
            addEmptyRow();
            markDirty("Added a table row.");
        });

        Button saveTableButton = new Button("Save Table");
        saveTableButton.setOnAction(e -> saveTableAsCsv());

        Button deleteRowsButton = new Button("Delete");
        deleteRowsButton.setOnAction(e -> deleteSelectedRows());

        Button clearTableButton = new Button("Clear");
        clearTableButton.setOnAction(e -> clearTable());

        HBox tableActionRow = new HBox(8, updateTableButton, addRowButton, saveTableButton, deleteRowsButton, clearTableButton);
        tableActionRow.setAlignment(Pos.CENTER_LEFT);
        tableActionRow.getChildren().remove(saveTableButton);
        tableActionRow.getChildren().add(saveTableButton);

        VBox topPanel = new VBox(6, new Label("Selected header expression"), headerTextArea, tableActionRow);
        VBox.setVgrow(headerTextArea, Priority.ALWAYS);

        VBox bottomPanel = new VBox(6, new Label("Sample table (editable)"), tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        VBox inspectTabContent = new VBox(10, topPanel, bottomPanel);
        inspectTabContent.setPadding(new Insets(10));
        VBox.setVgrow(topPanel, Priority.ALWAYS);
        VBox.setVgrow(bottomPanel, Priority.ALWAYS);

        Button updateXmlButton = new Button("Update XML");
        updateXmlButton.setOnAction(e -> refreshXmlPreview());
        Button saveXmlButton = new Button("Save XML");
        saveXmlButton.setOnAction(e -> saveXmlPreviewAsFile());

        HBox xmlActions = new HBox(8, updateXmlButton, saveXmlButton);
        xmlActions.setAlignment(Pos.CENTER_LEFT);
        VBox previewHeaderPanel = new VBox(6, new Label("Selected header expression"), previewHeaderTextArea);
        VBox previewTabContent = new VBox(8, previewHeaderPanel, xmlActions, previewArea);
        previewTabContent.setPadding(new Insets(10));
        // Match the Header + Table tab behavior: top expression pane grows with the tab.
        VBox.setVgrow(previewHeaderPanel, Priority.ALWAYS);
        VBox.setVgrow(previewHeaderTextArea, Priority.ALWAYS);
        VBox.setVgrow(previewArea, Priority.ALWAYS);

        Tab inspectTab = new Tab("Header + Table", inspectTabContent);
        inspectTab.setClosable(false);

        Tab xmlTab = new Tab("Header + XML", previewTabContent);
        xmlTab.setClosable(false);

        TabPane tabs = new TabPane(inspectTab, xmlTab);
        return tabs;
    }

    private HBox buildFooter() {
        Button closeButton = new Button("Close");
        closeButton.setCancelButton(true);
        closeButton.setOnAction(e -> {
            if (confirmDiscardIfDirty("Close inspector and discard unsaved header expression changes?")) {
                stage.close();
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox footer = new HBox(8, spacer, closeButton);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10, 0, 0, 0));
        return footer;
    }

    private void applyAppFontStyle(Region region) {
        if (region == null) {
            return;
        }
        region.setStyle(mergeStyle(region.getStyle(), styles.getFontStyle()));
    }

    private String mergeStyle(String existingStyle, String styleToAdd) {
        String existing = safe(existingStyle).trim();
        String addition = safe(styleToAdd).trim();
        if (addition.isEmpty()) {
            return existing;
        }
        if (existing.isEmpty()) {
            return addition;
        }
        if (existing.endsWith(";")) {
            return existing + " " + addition;
        }
        return existing + "; " + addition;
    }

    private void openHeaderFile() {
        if (!confirmDiscardIfDirty("Open another file and discard unsaved header expression changes?")) {
            return;
        }

        File initialDir = currentFile != null ? currentFile.getParentFile() : resolveDefaultConfigDir();
        File selected = FileChooserPlus.showOpenDialog(
                UtilsDialogs.getPrimaryOwnerWindow(),
                "Open XML header file",
                initialDir,
                FileChooserPlus.createExtensionFilter("Text files", "*.txt", "*.csv", "*.*"));

        if (selected != null) {
            loadFromFile(selected);
        }
    }

    private void loadFromFile(File file) {
        if (file == null || !file.isFile()) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            fileLines.clear();
            fileLines.addAll(lines);
            allHeaderEntries.clear();
            filteredHeaderEntries.clear();

            for (int i = 0; i < fileLines.size(); i++) {
                String rawLine = fileLines.get(i);
                String trimmed = rawLine == null ? "" : rawLine.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("$")) {
                    continue;
                }

                int firstComma = trimmed.indexOf(',');
                if (firstComma <= 0) {
                    continue;
                }

                String tableId = trimmed.substring(0, firstComma).trim();
                String headerExpression = trimmed.substring(firstComma + 1).trim();
                if (tableId.isEmpty() || headerExpression.isEmpty()) {
                    continue;
                }

                String normalizedHeader = normalizeHeaderForReadColumns(headerExpression);
                allHeaderEntries.add(new HeaderEntry(i, tableId, normalizedHeader));
            }

            headerFilterField.setText("");
            applyHeaderFilter();

            currentFile = file;
            updateHeaderLabels();

            if (!allHeaderEntries.isEmpty()) {
                setStatus("Loaded " + allHeaderEntries.size() + " header definitions.", false);
            } else {
                clearDetailPanels();
                setStatus("No usable header definitions were found in this file.", true);
            }
        } catch (Exception ex) {
            showError("Unable to open header file", "Could not parse: " + file.getAbsolutePath() + "\n\n" + ex.getMessage());
        }
    }

    private void saveCurrentFile() {
        if (currentFile == null) {
            saveAsNewFile();
            return;
        }
        persistToFile(currentFile);
    }

    private void saveAsNewFile() {
        File initialDir = currentFile != null ? currentFile.getParentFile() : resolveDefaultConfigDir();
        String initialName = currentFile != null ? currentFile.getName() : "xml_headers.txt";

        File selected = FileChooserPlus.showSaveDialog(
                UtilsDialogs.getPrimaryOwnerWindow(),
                "Save XML header file",
                initialDir,
                initialName,
                FileChooserPlus.createExtensionFilter("Text files", "*.txt", "*.csv", "*.*"));

        if (selected != null) {
            persistToFile(selected);
        }
    }

    private void persistToFile(File outFile) {
        if (outFile == null) {
            return;
        }

        HeaderEntry selected = headerListView.getSelectionModel().getSelectedItem();
        persistCurrentSelectionState(selected);

        List<String> outputLines = new ArrayList<String>(fileLines);
        for (HeaderEntry entry : allHeaderEntries) {
            if (entry.lineIndex >= 0 && entry.lineIndex < outputLines.size()) {
                outputLines.set(entry.lineIndex, entry.tableId + ", " + safe(entry.headerExpression).trim());
            }
        }

        try {
            Files.write(outFile.toPath(), outputLines, StandardCharsets.UTF_8);
            currentFile = outFile;
            fileLines.clear();
            fileLines.addAll(outputLines);
            for (HeaderEntry entry : allHeaderEntries) {
                entry.originalHeaderExpression = normalizeHeaderForReadColumns(entry.headerExpression);
                entry.headerExpression = entry.originalHeaderExpression;
            }
            updateHeaderLabels();
            setStatus("Saved: " + outFile.getAbsolutePath(), false);
        } catch (Exception ex) {
            showError("Unable to save header file", ex.getMessage());
        }
    }

    private void showSelectedHeader(HeaderEntry entry) {
        updatingUi = true;
        try {
            if (entry == null) {
                clearDetailPanels();
                return;
            }

            String normalizedHeader = normalizeHeaderForReadColumns(entry.headerExpression);
            entry.headerExpression = normalizedHeader;
            headerTextArea.setText(normalizedHeader);
            previewHeaderTextArea.setText(normalizedHeader);
             rebuildTable(entry, false);
             refreshXmlPreview();
        } finally {
            updatingUi = false;
        }
    }

    private void applyHeaderTextAndRebuildTable() {
        HeaderEntry selected = headerListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        String normalizedHeader = normalizeHeaderForReadColumns(headerTextArea.getText());
        selected.headerExpression = normalizedHeader;
        updatingUi = true;
        try {
            headerTextArea.setText(normalizedHeader);
            previewHeaderTextArea.setText(normalizedHeader);
        } finally {
            updatingUi = false;
        }
         rebuildTable(selected, true);
         refreshXmlPreview();
     }

    private void rebuildTable(HeaderEntry entry, boolean markDirty) {
        List<String> expectedColumns = deriveExpectedColumns(entry == null ? null : entry.headerExpression);

        List<List<String>> existingRows = entry == null ? new ArrayList<List<String>>() : entry.rows;
        ObservableList<ObservableList<String>> tableData = FXCollections.observableArrayList();
        for (List<String> row : existingRows) {
            ObservableList<String> rowValues = FXCollections.observableArrayList(row);
            normalizeRowWidth(rowValues, expectedColumns.size());
            tableData.add(rowValues);
        }

        if (tableData.isEmpty()) {
            tableData.add(createSampleRow(expectedColumns.size()));
        }

        tableView.getColumns().clear();
        for (int i = 0; i < expectedColumns.size(); i++) {
            final int columnIndex = i;
            String title = expectedColumns.get(i);
            TableColumn<ObservableList<String>, String> column = new TableColumn<ObservableList<String>, String>(title);
            column.setEditable(true);
            column.setCellValueFactory(cellData -> {
                ObservableList<String> row = cellData.getValue();
                normalizeRowWidth(row, expectedColumns.size());
                return new ReadOnlyStringWrapper(row.get(columnIndex));
            });
            column.setCellFactory(col -> {
                TextFieldTableCell<ObservableList<String>, String> cell =
                        new TextFieldTableCell<ObservableList<String>, String>(new DefaultStringConverter());

                cell.setOnMousePressed(e -> {
                    if (e.getButton() != MouseButton.PRIMARY || cell.isEmpty()) {
                        return;
                    }
                    dragAnchorRow = cell.getIndex();
                    dragAnchorCol = columnIndex;
                });

                // Enable JavaFX full-drag gestures so drag-enter events are fired for each traversed cell.
                cell.setOnDragDetected(e -> {
                    if (e.getButton() == MouseButton.PRIMARY && !cell.isEmpty()) {
                        cell.startFullDrag();
                        e.consume();
                    }
                });

                cell.setOnMouseDragEntered(e -> {
                    if (dragAnchorRow < 0 || dragAnchorCol < 0 || cell.isEmpty()) {
                        return;
                    }
                    selectCellRange(dragAnchorRow, dragAnchorCol, cell.getIndex(), columnIndex);
                    e.consume();
                });

                cell.setOnMouseReleased(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        dragAnchorRow = -1;
                        dragAnchorCol = -1;
                    }
                });

                return cell;
            });
            column.setOnEditCommit(event -> {
                ObservableList<String> row = event.getRowValue();
                normalizeRowWidth(row, expectedColumns.size());
                row.set(columnIndex, safe(event.getNewValue()));
                if (!updatingUi) {
                    markDirty("Updated table value.");
                }
            });
            tableView.getColumns().add(column);
        }
        tableView.setItems(tableData);
        applyEqualColumnWidths();

        if (markDirty) {
            markHeaderExpressionChanged("Updated expected table columns from header.");
        }
    }

    private void applyEqualColumnWidths() {
        int count = tableView.getColumns().size();
        if (count <= 0) {
            return;
        }
        // Small subtraction avoids clipping against borders/scrollbars.
        double available = Math.max(0.0, tableView.getWidth() - 18.0);
        double each = available / count;
        for (TableColumn<ObservableList<String>, ?> col : tableView.getColumns()) {
            col.setPrefWidth(each);
            col.setMinWidth(40.0);
        }
    }

    private ObservableList<String> createSampleRow(int colCount) {
        ObservableList<String> row = FXCollections.observableArrayList();
        for (int i = 0; i < colCount; i++) {
            row.add(toAlphaToken(i));
        }
        return row;
    }

    private String toAlphaToken(int index) {
        int value = index + 1;
        StringBuilder out = new StringBuilder();
        while (value > 0) {
            int rem = (value - 1) % 26;
            out.insert(0, (char) ('A' + rem));
            value = (value - 1) / 26;
        }
        return out.toString();
    }

    private List<String> deriveExpectedColumns(String headerExpression) {
        List<String> columns = new ArrayList<String>();
        if (headerExpression == null || headerExpression.trim().isEmpty()) {
            return columns;
        }

        String[] tokens = headerExpression.split(",");
        for (int i = 0; i < tokens.length; i++) {
            String token = safe(tokens[i]).trim();
            if (token.isEmpty()) {
                continue;
            }

            try {
                Header header = new Header(token, i);
                if (header.getReadColumnPos() >= 0) {
                    columns.add(uniqueColumnName(columns, extractReadableNameFromToken(token)));
                }
            } catch (Exception ignored) {
                // Best effort: if one token is malformed, skip it and keep rendering.
            }
        }

        return columns;
    }

    private String extractReadableNameFromToken(String token) {
        String t = safe(token).trim();
        int slash = t.lastIndexOf('/');
        if (slash >= 0 && slash + 1 < t.length()) {
            t = t.substring(slash + 1).trim();
        }

        String rawLeaf = t;

        // If this token reads an attribute value from CSV (for example +{year}fixedTax),
        // use attr-suffix as the column title (for example +{name}tech -> name-tech).
        if (rawLeaf.startsWith("+{")) {
            int close = rawLeaf.indexOf('}');
            if (close > 2) {
                String attrs = rawLeaf.substring(2, close).trim();
                String suffix = safe(rawLeaf.substring(close + 1)).trim();
                String[] parts = attrs.split(";");
                for (String p : parts) {
                    String attr = safe(p).trim();
                    if (!attr.isEmpty() && attr.indexOf('=') < 0) {
                        if (!suffix.isEmpty()) {
                            return attr + "-" + suffix;
                        }
                        return attr;
                    }
                }
            }
        }

        while (t.startsWith("+") || t.startsWith("@")) {
            t = t.substring(1).trim();
        }
        if (t.startsWith("{")) {
            int close = t.indexOf('}');
            if (close >= 0 && close + 1 < t.length()) {
                t = t.substring(close + 1).trim();
            }
        }
        int colon = t.indexOf(':');
        if (colon > 0) {
            t = t.substring(0, colon).trim();
        }
        return t.isEmpty() ? "value" : t;
    }

    private String uniqueColumnName(List<String> existing, String base) {
        if (base == null || base.trim().isEmpty()) {
            base = "value";
        }
        String candidate = base;
        int suffix = 2;
        while (existing.contains(candidate)) {
            candidate = base + "_" + suffix;
            suffix++;
        }
        return candidate;
    }

    private void refreshXmlPreview() {
        HeaderEntry selected = headerListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            previewHeaderTextArea.setText("");
            previewArea.setText("");
            return;
        }

        String normalizedHeader = normalizeHeaderForReadColumns(headerTextArea.getText());
        selected.headerExpression = normalizedHeader;
        if (!normalizedHeader.equals(safe(headerTextArea.getText()).trim())) {
            updatingUi = true;
            try {
                headerTextArea.setText(normalizedHeader);
                previewHeaderTextArea.setText(normalizedHeader);
            } finally {
                updatingUi = false;
            }
        } else {
            previewHeaderTextArea.setText(normalizedHeader);
        }
        String rootHeaderError = validateRequiredRootHeaders(normalizedHeader);
        if (rootHeaderError != null) {
            previewArea.setText("Unable to generate preview:\n" + rootHeaderError);
            setStatus("Preview contains parsing or data errors.", true);
            return;
        }
         selected.rows = snapshotRowsFromTable();

         try {
             DOMTreeBuilder builder = new DOMTreeBuilder();
            builder.setHeader(normalizedHeader);
             for (List<String> row : selected.rows) {
                 builder.addToTree(row);
             }
             Document doc = builder.getDoc();
             previewArea.setText(doc == null ? "" : toPrettyXml(doc));
             setStatus("Preview updated.", false);
         } catch (Exception ex) {
             previewArea.setText("Unable to generate preview:\n" + ex.getMessage());
             setStatus("Preview contains parsing or data errors.", true);
         }
    }

    private String validateRequiredRootHeaders(String headerExpression) {
        String src = safe(headerExpression).trim();
        if (src.isEmpty()) {
            return "Invalid set of headers: Missing root header (scenario and scenario/world are required).";
        }

        String[] split = src.split(",");
        boolean hasScenario = false;
        boolean hasScenarioWorld = false;
        for (int i = 0; i < split.length; i++) {
            String token = safe(split[i]).trim();
            if (token.equals("scenario")) {
                hasScenario = true;
            }
            if (token.equals("scenario/world")) {
                hasScenarioWorld = true;
            }
        }

        if (!hasScenario || !hasScenarioWorld) {
            return "Invalid set of headers: Missing root header (requires both 'scenario' and 'scenario/world').";
        }
        return null;
    }

    private String toPrettyXml(Document document) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.toString();
    }

    private String normalizeHeaderForReadColumns(String headerExpression) {
        String src = safe(headerExpression).trim();
        if (src.isEmpty()) {
            return src;
        }

        String[] split = src.split(",");
        List<String> readTokens = new ArrayList<String>();
        List<String> nonReadTokens = new ArrayList<String>();

        for (int i = 0; i < split.length; i++) {
            String token = safe(split[i]).trim();
            if (token.isEmpty()) {
                continue;
            }
            if (isReadToken(token, i)) {
                readTokens.add(token);
            } else {
                nonReadTokens.add(token);
            }
        }

        List<String> reordered = new ArrayList<String>(readTokens);
        reordered.addAll(nonReadTokens);
        return String.join(", ", reordered);
    }

    private boolean isReadToken(String token, int index) {
        try {
            Header h = new Header(token, index);
            return h.getReadColumnPos() >= 0;
        } catch (Exception ignored) {
            String leaf = safe(token).trim();
            int slash = leaf.lastIndexOf('/');
            if (slash >= 0 && slash + 1 < leaf.length()) {
                leaf = leaf.substring(slash + 1).trim();
            }
            return leaf.startsWith("+");
        }
    }

    private void addEmptyRow() {
        int colCount = tableView.getColumns().size();
        tableView.getItems().add(createEmptyRow(colCount));
    }

    private void saveTableAsCsv() {
        HeaderEntry selected = headerListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No header selected", "Select a header before saving table data.");
            return;
        }

        persistCurrentSelectionState(selected);

        File initialDir = currentFile != null ? currentFile.getParentFile() : resolveDefaultConfigDir();
        String defaultName = safe(selected.tableId).trim();
        if (defaultName.isEmpty()) {
            defaultName = "table";
        }
        defaultName = defaultName.replaceAll("[^a-zA-Z0-9_\\-]", "_") + ".csv";

        File selectedFile = FileChooserPlus.showSaveDialog(
                UtilsDialogs.getPrimaryOwnerWindow(),
                "Save table as CSV",
                initialDir,
                defaultName,
                FileChooserPlus.createExtensionFilter("CSV files", "*.csv", "*.*"));

        if (selectedFile == null) {
            return;
        }

        try {
            List<String> lines = new ArrayList<String>();
            lines.add("INPUT_TABLE");
            lines.add("Variable ID");
            lines.add(safe(selected.tableId).trim());
            lines.add("");

            List<String> headers = new ArrayList<String>();
            for (TableColumn<ObservableList<String>, ?> col : tableView.getColumns()) {
                headers.add(safe(col.getText()));
            }
            lines.add(toCsvLine(headers));

            for (ObservableList<String> row : tableView.getItems()) {
                List<String> values = new ArrayList<String>(row);
                normalizeRowWidth(values, headers.size());
                lines.add(toCsvLine(values));
            }

            Files.write(selectedFile.toPath(), lines, StandardCharsets.UTF_8);
            setStatus("Saved table CSV: " + selectedFile.getAbsolutePath(), false);
        } catch (Exception ex) {
            showError("Unable to save table CSV", ex.getMessage());
        }
    }

    private void saveXmlPreviewAsFile() {
        refreshXmlPreview();
        String xmlText = safe(previewArea.getText());
        if (xmlText.trim().isEmpty()) {
            showError("No XML to save", "Generate XML preview before saving.");
            return;
        }

        HeaderEntry selected = headerListView.getSelectionModel().getSelectedItem();
        String defaultName = selected == null ? "preview.xml" : safe(selected.tableId).trim() + ".xml";
        defaultName = defaultName.replaceAll("[^a-zA-Z0-9_\\-.]", "_");
        if (!defaultName.toLowerCase().endsWith(".xml")) {
            defaultName = defaultName + ".xml";
        }

        File initialDir = currentFile != null ? currentFile.getParentFile() : resolveDefaultConfigDir();
        File selectedFile = FileChooserPlus.showSaveDialog(
                UtilsDialogs.getPrimaryOwnerWindow(),
                "Save generated XML",
                initialDir,
                defaultName,
                FileChooserPlus.createExtensionFilter("XML files", "*.xml", "*.*"));

        if (selectedFile == null) {
            return;
        }

        try {
            String xmlDecl = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
            String content = xmlDecl + System.lineSeparator() + xmlText;
            Files.write(selectedFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
            setStatus("Saved XML preview: " + selectedFile.getAbsolutePath(), false);
        } catch (Exception ex) {
            showError("Unable to save XML", ex.getMessage());
        }
    }

    private String toCsvLine(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                out.append(',');
            }
            out.append(csvEscape(values.get(i)));
        }
        return out.toString();
    }

    private String csvEscape(String value) {
        String v = safe(value);
        boolean mustQuote = v.indexOf(',') >= 0 || v.indexOf('"') >= 0 || v.indexOf('\n') >= 0 || v.indexOf('\r') >= 0;
        if (!mustQuote) {
            return v;
        }
        return '"' + v.replace("\"", "\"\"") + '"';
    }

    private void selectCellRange(int anchorRow, int anchorCol, int row, int col) {
        int minRow = Math.min(anchorRow, row);
        int maxRow = Math.max(anchorRow, row);
        int minCol = Math.min(anchorCol, col);
        int maxCol = Math.max(anchorCol, col);

        tableView.getSelectionModel().clearSelection();
        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minCol; c <= maxCol; c++) {
                if (c >= 0 && c < tableView.getColumns().size()) {
                    tableView.getSelectionModel().select(r, tableView.getColumns().get(c));
                }
            }
        }
    }

    private boolean pasteIntoTableFromClipboard() {
        if (tableView.getColumns().isEmpty()) {
            return false;
        }

        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard == null || !clipboard.hasString()) {
            return false;
        }

        String raw = safe(clipboard.getString());
        if (raw.trim().isEmpty()) {
            return false;
        }

        List<List<String>> rowsToPaste = parseClipboardRows(raw);
        if (rowsToPaste.isEmpty()) {
            return false;
        }

        int startRow = tableView.getSelectionModel().getSelectedIndex();
        if (startRow < 0) {
            startRow = 0;
        }

        int startCol = 0;
        if (!tableView.getSelectionModel().getSelectedCells().isEmpty()) {
            startCol = Math.max(0, tableView.getSelectionModel().getSelectedCells().get(0).getColumn());
        }

        int totalCols = tableView.getColumns().size();
        ObservableList<ObservableList<String>> items = tableView.getItems();

        for (int r = 0; r < rowsToPaste.size(); r++) {
            int targetRow = startRow + r;
            while (targetRow >= items.size()) {
                items.add(createEmptyRow(totalCols));
            }

            ObservableList<String> row = items.get(targetRow);
            normalizeRowWidth(row, totalCols);
            List<String> source = rowsToPaste.get(r);
            for (int c = 0; c < source.size(); c++) {
                int targetCol = startCol + c;
                if (targetCol >= totalCols) {
                    break;
                }
                row.set(targetCol, safe(source.get(c)));
            }
        }

        markDirty("Pasted clipboard data into table.");
        refreshXmlPreview();
        return true;
    }

    private boolean copySelectionToClipboard() {
        List<TablePosition> selectedCells = tableView.getSelectionModel().getSelectedCells();
        if (selectedCells == null || selectedCells.isEmpty()) {
            return false;
        }

        int minRow = Integer.MAX_VALUE;
        int maxRow = Integer.MIN_VALUE;
        int minCol = Integer.MAX_VALUE;
        int maxCol = Integer.MIN_VALUE;
        Set<String> selectedKeys = new HashSet<String>();

        for (TablePosition<?, ?> pos : selectedCells) {
            if (pos == null) {
                continue;
            }
            int r = pos.getRow();
            int c = pos.getColumn();
            if (r < 0 || c < 0) {
                continue;
            }
            minRow = Math.min(minRow, r);
            maxRow = Math.max(maxRow, r);
            minCol = Math.min(minCol, c);
            maxCol = Math.max(maxCol, c);
            selectedKeys.add(r + ":" + c);
        }

        if (selectedKeys.isEmpty()) {
            return false;
        }

        StringBuilder out = new StringBuilder();
        ObservableList<ObservableList<String>> items = tableView.getItems();

        for (int r = minRow; r <= maxRow; r++) {
            if (r > minRow) {
                out.append('\n');
            }
            for (int c = minCol; c <= maxCol; c++) {
                if (c > minCol) {
                    out.append('\t');
                }

                String value = "";
                if (selectedKeys.contains(r + ":" + c)
                        && r >= 0 && r < items.size()) {
                    ObservableList<String> row = items.get(r);
                    if (row != null && c >= 0 && c < row.size()) {
                        value = safe(row.get(c));
                    }
                }
                out.append(toSingleLineCell(value));
            }
        }

        ClipboardContent content = new ClipboardContent();
        content.putString(out.toString());
        Clipboard.getSystemClipboard().setContent(content);
        setStatus("Copied selected table cells to clipboard.", false);
        return true;
    }

    private boolean cutSelectionToClipboard() {
        if (!copySelectionToClipboard()) {
            return false;
        }
        if (clearSelectedCells()) {
            setStatus("Cut selected table cells to clipboard.", false);
            return true;
        }
        return false;
    }

    private boolean clearSelectedCells() {
        List<TablePosition> selectedCells = tableView.getSelectionModel().getSelectedCells();
        if (selectedCells == null || selectedCells.isEmpty()) {
            return false;
        }

        ObservableList<ObservableList<String>> items = tableView.getItems();
        int totalCols = tableView.getColumns().size();
        boolean changed = false;
        for (TablePosition<?, ?> pos : selectedCells) {
            if (pos == null) {
                continue;
            }
            int row = pos.getRow();
            int col = pos.getColumn();
            if (row < 0 || col < 0 || row >= items.size() || col >= totalCols) {
                continue;
            }
            ObservableList<String> dataRow = items.get(row);
            normalizeRowWidth(dataRow, totalCols);
            if (!safe(dataRow.get(col)).isEmpty()) {
                dataRow.set(col, "");
                changed = true;
            }
        }

        if (!changed) {
            return false;
        }
        markDirty("Cleared selected table cells.");
        refreshXmlPreview();
        return true;
    }

    private boolean deleteSelectedRows() {
        TreeSet<Integer> selectedRows = new TreeSet<Integer>(tableView.getSelectionModel().getSelectedIndices());
        if (selectedRows.isEmpty()) {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                selectedRows.add(selectedIndex);
            }
        }
        if (selectedRows.isEmpty()) {
            return false;
        }

        List<Integer> descending = new ArrayList<Integer>(selectedRows);
        Collections.reverse(descending);
        ObservableList<ObservableList<String>> items = tableView.getItems();
        for (Integer rowIndex : descending) {
            if (rowIndex != null && rowIndex >= 0 && rowIndex < items.size()) {
                items.remove((int) rowIndex);
            }
        }

        if (items.isEmpty()) {
            items.add(createEmptyRow(tableView.getColumns().size()));
        }
        markDirty("Deleted selected table rows.");
        refreshXmlPreview();
        return true;
    }

    private void clearTable() {
        ObservableList<ObservableList<String>> items = tableView.getItems();
        items.clear();
        items.add(createEmptyRow(tableView.getColumns().size()));
        tableView.getSelectionModel().clearSelection();
        markDirty("Cleared table rows.");
        refreshXmlPreview();
    }

    private ContextMenu createTableContextMenu() {
        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setOnAction(e -> copySelectionToClipboard());

        MenuItem pasteItem = new MenuItem("Paste");
        pasteItem.setOnAction(e -> pasteIntoTableFromClipboard());

        MenuItem cutItem = new MenuItem("Cut");
        cutItem.setOnAction(e -> cutSelectionToClipboard());

        MenuItem clearItem = new MenuItem("Clear Table");
        clearItem.setOnAction(e -> clearTable());

        MenuItem addRowItem = new MenuItem("Add Row");
        addRowItem.setOnAction(e -> {
            addEmptyRow();
            markDirty("Added a table row.");
        });

        MenuItem deleteRowsItem = new MenuItem("Delete Selected Rows");
        deleteRowsItem.setOnAction(e -> deleteSelectedRows());

        return new ContextMenu(copyItem, pasteItem, cutItem, clearItem, addRowItem, deleteRowsItem);
    }

    private String toSingleLineCell(String value) {
        return safe(value).replace("\r\n", " ").replace('\r', ' ').replace('\n', ' ').replace('\t', ' ');
    }

    private List<List<String>> parseClipboardRows(String raw) {
        String normalized = safe(raw).replace("\r\n", "\n").replace('\r', '\n');
        String[] lines = normalized.split("\\n", -1);
        List<List<String>> rows = new ArrayList<List<String>>();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (i == lines.length - 1 && line.isEmpty()) {
                continue;
            }

            List<String> row;
            if (line.indexOf('\t') >= 0) {
                String[] split = line.split("\\t", -1);
                row = new ArrayList<String>(split.length);
                for (String cell : split) {
                    row.add(cell);
                }
            } else {
                row = parseCsvLine(line);
            }
            rows.add(row);
        }

        return rows;
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<String>();
        String src = safe(line);
        StringBuilder cell = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < src.length(); i++) {
            char ch = src.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < src.length() && src.charAt(i + 1) == '"') {
                    cell.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                values.add(cell.toString());
                cell.setLength(0);
            } else {
                cell.append(ch);
            }
        }

        values.add(cell.toString());
        return values;
    }

    private ObservableList<String> createEmptyRow(int colCount) {
        ObservableList<String> row = FXCollections.observableArrayList();
        normalizeRowWidth(row, colCount);
        return row;
    }

    private void normalizeRowWidth(List<String> row, int width) {
        if (row == null) {
            return;
        }
        while (row.size() < width) {
            row.add("");
        }
        while (row.size() > width) {
            row.remove(row.size() - 1);
        }
    }

    private List<List<String>> snapshotRowsFromTable() {
        List<List<String>> rows = new ArrayList<List<String>>();
        for (ObservableList<String> row : tableView.getItems()) {
            rows.add(new ArrayList<String>(row));
        }
        return rows;
    }

    private void persistCurrentSelectionState(HeaderEntry entry) {
        if (entry == null) {
            return;
        }
        entry.headerExpression = safe(headerTextArea.getText()).trim();
        entry.rows = snapshotRowsFromTable();
    }

    private void clearDetailPanels() {
        updatingUi = true;
        try {
            headerTextArea.setText("");
            previewHeaderTextArea.setText("");
            tableView.getColumns().clear();
            tableView.getItems().clear();
            previewArea.setText("");
        } finally {
            updatingUi = false;
        }
    }

    private void updateHeaderLabels() {
        loadedFileLabel.setText("Loaded file: " + (currentFile == null ? "--" : currentFile.getAbsolutePath()));
    }

    private void markDirty(String message) {
        setStatus(message, false);
    }

    private void markHeaderExpressionChanged(String message) {
        setStatus(message, false);
    }

    private String canonicalHeaderExpression(String value) {
        return safe(normalizeHeaderForReadColumns(value)).trim();
    }

    private boolean hasUnsavedHeaderExpressionChanges() {
        persistCurrentSelectionState(headerListView.getSelectionModel().getSelectedItem());
        for (HeaderEntry entry : allHeaderEntries) {
            if (entry == null) {
                continue;
            }
            String current = canonicalHeaderExpression(entry.headerExpression);
            String original = canonicalHeaderExpression(entry.originalHeaderExpression);
            if (!current.equals(original)) {
                return true;
            }
        }
        return false;
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText("Status: " + safe(message));
        statusLabel.setStyle(isError ? "-fx-text-fill: #b00020;" : "-fx-text-fill: black;");
    }

    private void showError(String header, String message) {
        Alert alert = new Alert(AlertType.ERROR, safe(message), ButtonType.OK);
        UtilsDialogs.initDialogOwner(alert);
        UtilsDialogs.applyDialogTheme(alert);
        applyAppFontStyle(alert.getDialogPane());
        alert.setTitle("XML Header Inspector");
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    private boolean confirmDiscardIfDirty(String message) {
        if (!hasUnsavedHeaderExpressionChanges()) {
            return true;
        }
        ButtonType discardButton = new ButtonType("Discard", ButtonBar.ButtonData.OK_DONE);
        Alert alert = new Alert(AlertType.CONFIRMATION, message, discardButton, ButtonType.CANCEL);
        UtilsDialogs.initDialogOwner(alert);
        UtilsDialogs.applyDialogTheme(alert);
        applyAppFontStyle(alert.getDialogPane());
        alert.setTitle("Unsaved Changes");
        alert.setHeaderText("You have unsaved header expression changes");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == discardButton;
    }

    private File resolveDefaultHeaderFile() {
        String filename = vars.getXmlHeaderFilename();
        if (filename == null || filename.trim().isEmpty()) {
            return null;
        }
        File file = new File(filename);
        return file.isFile() ? file : null;
    }

    private File resolveDefaultConfigDir() {
        String configDir = vars.getScenarioBuilderConfigDir();
        if (configDir != null && !configDir.trim().isEmpty()) {
            File dir = new File(configDir);
            if (dir.isDirectory()) {
                return dir;
            }
        }

        File fallbackFromHeader = resolveDefaultHeaderFile();
        if (fallbackFromHeader != null && fallbackFromHeader.getParentFile() != null) {
            return fallbackFromHeader.getParentFile();
        }

        return new File(System.getProperty("user.home", "."));
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private static final class HeaderEntry {
        private final int lineIndex;
        private final String tableId;
        private String headerExpression;
        private String originalHeaderExpression;
        private List<List<String>> rows;

        private HeaderEntry(int lineIndex, String tableId, String headerExpression) {
            this.lineIndex = lineIndex;
            this.tableId = tableId;
            this.headerExpression = headerExpression;
            this.originalHeaderExpression = headerExpression;
            this.rows = new ArrayList<List<String>>();
        }
    }
}
