package glimpseUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import gui.ScenarioBuilder;
import gui.ScenarioLibraryReportHelper;
import gui.ScenarioLibraryReportHelper.ErrorTextReport;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Error-report dialog helpers extracted from {@link GLIMPSEUtils}.
 */
public final class UtilsErrors {

	private static final UtilsErrors INSTANCE = new UtilsErrors();

	private final Pattern intPattern = Pattern.compile("-?[0-9]+");
	private final Pattern doublePattern = Pattern.compile("-?(([0-9]+)|([0-9]*\\.[0-9]+))");
	/** Markets with both supply and demand at or below this value are treated as "small markets". */
	private static final double SMALL_MARKET_DEMAND_CUTOFF = 1e-3;
	private static final String[] LOG_LINE_LEVEL_PREFIXES = {
			"TRACE", "DEBUG", "INFO", "NOTICE", "WARN", "WARNING"
	};

	private GLIMPSEVariables vars;
	private GLIMPSEStyles styles;
	private GLIMPSEFiles files;
	private GLIMPSEUtils utils;

	private UtilsErrors() {
	}

	/**
	 * Returns the shared error-report helper instance.
	 *
	 * @return singleton {@link UtilsErrors} instance
	 */
	public static UtilsErrors getInstance() {
		return INSTANCE;
	}

	/**
	 * Initializes the helper with shared GLIMPSE services.
	 *
	 * @param vars shared variables instance
	 * @param styles shared style definitions
	 * @param files shared file utilities
	 * @param utils shared utility facade
	 */
	public void init(GLIMPSEVariables vars, GLIMPSEStyles styles, GLIMPSEFiles files, GLIMPSEUtils utils) {
		this.vars = vars;
		this.styles = styles;
		this.files = files;
		this.utils = utils;
	}

	/**
	 * Displays a list of lines in a text dialog without line wrapping.
	 *
	 * @param arrayListArg lines to display
	 * @param title dialog title
	 */
	public void displayArrayList(ArrayList<String> arrayListArg, String title) {
		Platform.runLater(() -> displayArrayList(arrayListArg, title, false));
	}

	/**
	 * Displays a list of lines in a text dialog.
	 *
	 * @param arrayListArg lines to display
	 * @param title dialog title
	 * @param doWrap whether line wrapping should be enabled
	 */
	public void displayArrayList(ArrayList<String> arrayListArg, String title, boolean doWrap) {
		if (styles == null || vars == null)
			return;
		final String finalTitle = title;
		Runnable displayTask = () -> {
			BorderPane border = new BorderPane();
			String usedTitle = finalTitle == null ? GLIMPSEUtils.LABEL_DISPLAY : finalTitle;
			Stage stage = new Stage();
			initializeStageOwnerAndCentering(stage);
			stage.setTitle(usedTitle);
			stage.setWidth(900);
			stage.setHeight(800);
			stage.setResizable(true);
			stage.setAlwaysOnTop(true);
			TextArea textArea = new TextArea();
			textArea.setEditable(false);
			textArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			textArea.setMinHeight(0);
			textArea.setWrapText(doWrap);
			Button closeButton = utils.createButton(GLIMPSEUtils.LABEL_CLOSE, styles.getBigButtonWidth(), null);
			closeButton.setOnAction(e -> stage.close());
			StringBuilder text = new StringBuilder();
			if (arrayListArg != null) {
				for (String str : arrayListArg) {
					if (str.indexOf(vars.getEol()) < 0)
						text.append(str).append(vars.getEol());
					else
						text.append(str);
				}
				textArea.setText(text.toString());
				HBox buttonBox = new HBox();
				buttonBox.setPadding(new Insets(4, 4, 4, 4));
				buttonBox.setSpacing(5);
				buttonBox.setAlignment(Pos.CENTER);
				buttonBox.getChildren().addAll(closeButton);
				border.setCenter(textArea);
				border.setBottom(buttonBox);
				Scene scene = new Scene(border);
				ScenarioBuilder.applyModernTheme(scene);
				stage.setScene(scene);
				stage.show();
			}
		};
		displayTask.run();
	}

	/**
	 * Shows the scenario-library error report using the richer text report view.
	 *
	 * @param title popup title
	 * @param csvData CSV rows describing the report
	 * @param wd popup width
	 * @param ht popup height
	 */
	public void showPopupTableOfErrorReport(String title, ArrayList<String> csvData, int wd, int ht) {
		if (styles == null) {
			return;
		}
		if (csvData == null || csvData.isEmpty()) {
			utils.showPopupTableOfCSVData(title, csvData, wd, ht);
			return;
		}
		ScenarioLibraryReportHelper.ErrorTextReport report = buildTextReportFromCsv(title, csvData);
		showTextErrorReport(report, wd, ht);
	}

	/**
	 * Displays a formatted error text report with filtering and export controls.
	 *
	 * @param report report model to display
	 * @param wd popup width
	 * @param ht popup height
	 */
	public void showTextErrorReport(ScenarioLibraryReportHelper.ErrorTextReport report, int wd, int ht) {
		if (styles == null || report == null) {
			return;
		}
		Runnable popupTask = () -> {
			String usedTitle = report.getTitle() == null || report.getTitle().trim().isEmpty()
					? GLIMPSEUtils.LABEL_DISPLAY
					: report.getTitle();
			Stage stage = new Stage();
			initializeStageOwnerAndCentering(stage);
			stage.setTitle(usedTitle);
			stage.setWidth(wd);
			stage.setHeight(ht);
			stage.setResizable(true);

			BorderPane border = new BorderPane();
			TextArea textArea = new TextArea();
			textArea.setEditable(false);
			textArea.setWrapText(false);
			textArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			textArea.setMinHeight(0);

			ChoiceBox<String> viewSelector = new ChoiceBox<>(FXCollections.observableArrayList(report.getFilterOptions()));
			if (viewSelector.getItems().isEmpty()) {
				viewSelector.getItems().add("All lines");
			}
			viewSelector.getSelectionModel().select(0);
			viewSelector.setTooltip(new Tooltip("Choose which lines to display"));

			TextField searchTextField = utils.createTextField();
			searchTextField.setMinWidth(styles.getBigButtonWidth());
			searchTextField.setTooltip(new Tooltip("Enter text to filter visible lines"));

			Runnable refreshText = () -> {
				String selectedFilter = viewSelector.getValue();
				String baseText = report.buildText(selectedFilter);
				if ((baseText == null || baseText.trim().isEmpty()) && !report.hasVisibleContent(selectedFilter)) {
					baseText = "No errors reported" + System.lineSeparator();
				}
				String text = filterLinesForSearch(baseText, searchTextField.getText());
				textArea.setText(text == null ? "" : text);
				textArea.positionCaret(0);
				textArea.setScrollTop(0);
				textArea.setScrollLeft(0);
			};
			refreshText.run();
			viewSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> refreshText.run());
			searchTextField.textProperty().addListener((obs, oldV, newV) -> refreshText.run());

			Button saveAsBtn = new Button("Save As...");
			saveAsBtn.setTooltip(new Tooltip("Save the currently visible error report as text"));
			saveAsBtn.setOnAction(ev -> {
				try {
					File initialDir = new File(vars.getGlimpseLogDir());
					FileChooser.ExtensionFilter txtFilter = FileChooserPlus.createExtensionFilter("Text files (*.txt)", "txt");
					String defaultName = report.getDefaultSaveFileName();
					if (defaultName == null || defaultName.trim().isEmpty()) {
						defaultName = "error_report.txt";
					}
					File chosen = FileChooserPlus.showSaveDialog(stage, "Save Error Report", initialDir, defaultName, txtFilter);
					if (chosen != null) {
						ArrayList<String> exportRows = new ArrayList<>();
						String text = textArea.getText();
						if (text != null && !text.isEmpty()) {
							Collections.addAll(exportRows, text.split("\\R", -1));
						}
						files.saveFile(exportRows, chosen.getPath());
						utils.showInformationDialog("Information", "Export successful", "Saved report to: " + chosen.getPath());
					}
				} catch (Exception ex) {
					utils.showInformationDialog("Information", "Export failed", "Could not save report: " + ex.getMessage());
				}
			});

			Button closeButton = utils.createButton(GLIMPSEUtils.LABEL_CLOSE, styles.getBigButtonWidth(), null);
			closeButton.setOnAction(e -> stage.close());

			HBox controls = new HBox(10,
					new Label("View:"),
					viewSelector,
					new Label("Search:"),
					searchTextField,
					saveAsBtn);
			controls.setPadding(new Insets(6, 10, 6, 10));
			controls.setAlignment(Pos.CENTER_LEFT);

			HBox buttonBox = new HBox();
			buttonBox.setPadding(new Insets(4, 4, 4, 4));
			buttonBox.setSpacing(5);
			buttonBox.setAlignment(Pos.CENTER);
			buttonBox.getChildren().addAll(closeButton);

			border.setTop(controls);
			border.setCenter(textArea);
			border.setBottom(buttonBox);

			Scene scene = new Scene(border);
			ScenarioBuilder.applyModernTheme(scene);
			stage.setScene(scene);
			stage.show();
		};
		popupTask.run();
	}

	private String filterLinesForSearch(String text, String query) {
		if (text == null) {
			return "";
		}
		String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
		if (normalizedQuery.isEmpty()) {
			return text;
		}
		StringBuilder filtered = new StringBuilder();
		String[] lines = text.split("\\R", -1);
		for (String line : lines) {
			if (line != null && line.toLowerCase().contains(normalizedQuery)) {
				if (filtered.length() > 0) {
					filtered.append(System.lineSeparator());
				}
				filtered.append(line);
			}
		}
		return filtered.toString();
	}

	private ScenarioLibraryReportHelper.ErrorTextReport buildTextReportFromCsv(String title, ArrayList<String> csvData) {
		ArrayList<ScenarioLibraryReportHelper.ErrorReportLine> reportLines = new ArrayList<>();
		if (csvData != null && !csvData.isEmpty()) {
			String[][] rawData = utils.getDataMatrixFromArrayList(csvData);
			String[] headerRow = rawData.length > 0 ? rawData[0] : new String[0];
			int classificationIndex = findColumnIndex(headerRow, "Classification");
			for (int rowIndex = 1; rowIndex < rawData.length; rowIndex++) {
				String[] row = rawData[rowIndex];
				if (row == null) {
					continue;
				}
				String classification = classificationIndex >= 0 && classificationIndex < row.length ? row[classificationIndex] : "";
				StringBuilder lineText = new StringBuilder();
				for (int col = 0; col < row.length; col++) {
					if (classificationIndex == col) {
						continue;
					}
					String value = row[col] == null ? "" : row[col].trim();
					if (lineText.length() > 0) {
						lineText.append('\t');
					}
					lineText.append(value);
				}
				reportLines.add(new ScenarioLibraryReportHelper.ErrorReportLine(classification, lineText.toString()));
			}
		}
		return new ScenarioLibraryReportHelper.ErrorTextReport(
				title,
				defaultErrorFilters(),
				reportLines,
				"error_report.txt");
	}

	private ArrayList<String> defaultErrorFilters() {
		ArrayList<String> filters = new ArrayList<>();
		filters.add("All lines");
		filters.add("Major errors");
		filters.add("Moderate errors");
		filters.add("Minor errors");
		return filters;
	}

	private int findColumnIndex(String[] headerRow, String headerName) {
		if (headerRow == null || headerName == null)
			return -1;
		for (int i = 0; i < headerRow.length; i++) {
			String header = headerRow[i] == null ? "" : headerRow[i].trim();
			if (headerName.equalsIgnoreCase(header))
				return i;
		}
		return -1;
	}

	private String getCellString(List<Object> row, int colIndex) {
		if (row == null || colIndex < 0 || colIndex >= row.size())
			return "";
		Object value = row.get(colIndex);
		return value == null ? "" : value.toString();
	}

	private String[] extractColumn(String[][] data, int columnIndex) {
		if (data == null || columnIndex < 0)
			return new String[0];
		String[] column = new String[data.length];
		for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
			if (data[rowIndex] != null && columnIndex < data[rowIndex].length && data[rowIndex][columnIndex] != null) {
				column[rowIndex] = data[rowIndex][columnIndex];
			} else {
				column[rowIndex] = "";
			}
		}
		return column;
	}

	private Class<?> deduceColumnType(String[] column) {
		if (column == null || column.length <= 1)
			return String.class;
		boolean hasValue = false;
		boolean allIntegers = true;
		boolean allNumeric = true;
		for (int rowIndex = 1; rowIndex < column.length; rowIndex++) {
			String value = column[rowIndex];
			if (value == null)
				continue;
			value = value.trim();
			if (value.isEmpty())
				continue;
			hasValue = true;
			if (!intPattern.matcher(value).matches()) {
				allIntegers = false;
			}
			if (!doublePattern.matcher(value).matches()) {
				allNumeric = false;
				break;
			}
		}
		if (!hasValue)
			return String.class;
		if (allIntegers)
			return Integer.class;
		if (allNumeric)
			return Double.class;
		return String.class;
	}

	private String getColumnHeader(String[][] data, int columnIndex) {
		if (columnIndex < 0)
			return "";
		String fallback = "Column " + (columnIndex + 1);
		if (data == null || data.length == 0 || data[0] == null || columnIndex >= data[0].length)
			return fallback;
		String header = data[0][columnIndex];
		if (header == null)
			return fallback;
		header = header.trim();
		return header.isEmpty() ? fallback : header;
	}

	private Object getDataAsType(String[] row, Class<?> type, int columnIndex) {
		try {
			if (type == Integer.class) {
				if (columnIndex < row.length) {
					return Integer.valueOf(row[columnIndex]);
				}
				return Integer.valueOf(0);
			} else if (type == Double.class) {
				if (columnIndex < row.length) {
					return Double.valueOf(row[columnIndex]);
				}
				return Double.valueOf(0.0);
			} else {
				if (columnIndex < row.length) {
					return row[columnIndex];
				}
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}

	private TableColumn<List<Object>, String> createColumn(Class<?> type, int index, String name) {
		String text = name;
		TableColumn<List<Object>, String> col = new TableColumn<>(text);
		col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(index).toString()));
		return col;
	}

	private void installErrorReportOverflowBehavior(TableColumn<List<Object>, String> col, int colIndex,
			int totalColumns) {
		col.setCellFactory(column -> new TableCell<List<Object>, String>() {
			private final Text textNode = new Text();
			private final StackPane textPane = new StackPane(textNode);
			private final Rectangle clip = new Rectangle();

			{
				textNode.fontProperty().bind(fontProperty());
				textNode.fillProperty().bind(textFillProperty());
				textPane.setAlignment(Pos.CENTER_LEFT);
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				clip.widthProperty().bind(widthProperty());
				clip.heightProperty().bind(heightProperty());
			}

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
					setText(null);
					setClip(clip);
					toBack();
					return;
				}

				textNode.setText(item);
				setText(null);
				setGraphic(textPane);

				boolean allowOverflow = shouldAllowOverflow(item, colIndex, totalColumns);
				if (allowOverflow) {
					setClip(null);
					toFront();
				} else {
					setClip(clip);
					toBack();
				}
			}

			private boolean shouldAllowOverflow(String text, int columnIndex, int columnCount) {
				if (text == null || text.trim().isEmpty()) {
					return false;
				}
				Object rowObj = getTableRow() == null ? null : getTableRow().getItem();
				if (!(rowObj instanceof List)) {
					return false;
				}
				@SuppressWarnings("unchecked")
				List<Object> rowItem = (List<Object>) rowObj;
				if (!areRightCellsEmpty(rowItem, columnIndex, columnCount)) {
					return false;
				}
				double available = getWidth();
				if (available <= 0) {
					available = col.getWidth();
				}
				if (getInsets() != null) {
					available -= (getInsets().getLeft() + getInsets().getRight());
				}
				if (available <= 0) {
					return false;
				}
				Font font = getFont();
				if (font == null) {
					return false;
				}
				Text measure = new Text(text);
				measure.setFont(font);
				double textWidth = measure.getLayoutBounds().getWidth();
				return textWidth > (available + 1.0);
			}
		});
	}

	private boolean areRightCellsEmpty(List<Object> rowItem, int columnIndex, int columnCount) {
		if (rowItem == null) {
			return false;
		}
		for (int i = columnIndex + 1; i < columnCount; i++) {
			String value = i < rowItem.size() && rowItem.get(i) != null ? rowItem.get(i).toString() : "";
			if (!value.trim().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private String sanitizeCsvField(String value) {
		if (value == null)
			return "";
		return value.replace(",", ";").replace("\r", " ").replace("\n", " ").trim();
	}

	private String buildCsvRow(List<String> fields, int columnCount) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < columnCount; i++) {
			if (i > 0)
				sb.append(",");
			String value = i < fields.size() ? fields.get(i) : "";
			sb.append(value == null ? "" : value);
		}
		return sb.toString();
	}

	private void initializeStageOwnerAndCentering(Stage stage) {
		if (stage == null) {
			return;
		}
		try {
			Window owner = UtilsDialogs.getPrimaryOwnerWindow();
			if (owner != null) {
				stage.initOwner(owner);
			}
		} catch (Exception ignored) {
		}
		stage.setOnShown(e -> centerStageOverPrimaryOwner(stage));
	}

	private void centerStageOverPrimaryOwner(Stage stage) {
		if (stage == null) {
			return;
		}
		try {
			Window owner = UtilsDialogs.getPrimaryOwnerWindow();
			if (owner == null || !owner.isShowing()) {
				return;
			}
			double stageWidth = stage.getWidth();
			double stageHeight = stage.getHeight();
			if (stageWidth <= 0 || stageHeight <= 0) {
				return;
			}
			stage.setX(owner.getX() + ((owner.getWidth() - stageWidth) / 2.0));
			stage.setY(owner.getY() + ((owner.getHeight() - stageHeight) / 2.0));
		} catch (Exception ignored) {
		}
	}

	/**
	 * Generates CSV-style error-report rows from a GCAM main log.
	 *
	 * @param mainLogFile path to the main log file
	 * @param scenario scenario name to include in the report rows
	 * @return report rows without a header line
	 */
	public ArrayList<String> generateErrorReport(String mainLogFile, String scenario) {
		if (files == null || vars == null)
			return new ArrayList<>();
		DecimalFormat formatter = new DecimalFormat("0.###");
		formatter.setGroupingUsed(false);
		double minDmd = SMALL_MARKET_DEMAND_CUTOFF;
		double minRed = 0.01;
		int totalFails = 0;
		int minorFails = 0;
		int smallMarketFails = 0;
		int majorFails = 0;
		int majorNonSmallFails = 0;
		int moderateFails = 0;
		String scenarioLabel = (scenario == null || scenario.trim().isEmpty()) ? "exe/main_log.txt" : scenario;
		ArrayList<String> report = new ArrayList<>();
		ArrayList<String[]> tokenRows = new ArrayList<>();
		ArrayList<MarketSignal> marketSignals = new ArrayList<>();
		ArrayList<String> classifications = new ArrayList<>();
		ArrayList<String> smallMarkets = new ArrayList<>();
		int maxTokenCount = 0;
		File mainlogfile = new File(mainLogFile);
		if (mainlogfile.exists()) {
			String[] prefixes = { "ERROR", "SEVERE", "Period" };
			ArrayList<String> logLines = files.getStringArrayFromFile(mainlogfile.getPath(), "#");
			Integer lastSeenPeriod = null;
			for (String rawLine : logLines) {
				String errorLine = stripKnownLogLinePrefixes(rawLine);
				if (errorLine.isEmpty() || !startsWithAnyPrefix(errorLine, prefixes))
					continue;
				String normalized = errorLine.replace(":", ",");
				String[] tokens = normalized.split(",");
				Integer linePeriod = extractPeriodFromTokens(tokens);
				if (linePeriod != null) {
					lastSeenPeriod = linePeriod;
				}
				maxTokenCount = Math.max(maxTokenCount, tokens.length);
				String classification = "";
				String smallMarket = "";
				try {
					if (tokens.length > 12) {
						double red = Double.parseDouble(tokens[7].trim());
						double dmd = Double.parseDouble(tokens[9].trim());
						String marketName = tokens[12].trim();
						String marketType = extractMarketType(tokens);
						// tokens[9] and tokens[10] are the supply/demand pair; small only if both are tiny
						double other = Double.parseDouble(tokens[10].trim());
						boolean isSmall = Math.abs(dmd) <= minDmd && Math.abs(other) <= minDmd;
						totalFails++;
						if (isSmall)
							smallMarketFails++;
						if (red > minRed) {
							if (red > minRed * 5.0) {
								classification = "MAJOR";
								majorFails++;
								if (!isSmall)
									majorNonSmallFails++;
							} else {
								classification = "MODERATE";
								moderateFails++;
							}
						} else {
							classification = "MINOR";
							minorFails++;
						}
						smallMarket = isSmall ? "true" : "false";
						marketSignals.add(new MarketSignal(marketName, red, dmd, classification,
								"true".equals(smallMarket), lastSeenPeriod, marketType));
					}
				} catch (Exception e) {
					// Ignore parse errors for robustness
				}
				tokenRows.add(tokens);
				classifications.add(classification);
				smallMarkets.add(smallMarket);
			}
			if (tokenRows.isEmpty()) {
				maxTokenCount = Math.max(maxTokenCount, 1);
				tokenRows.add(new String[] { "No errors reported" });
				classifications.add("");
				smallMarkets.add("");
			}
			if (totalFails > 0) {
				maxTokenCount = Math.max(maxTokenCount, 1);
				String verdict;
				if (totalFails == 0) {
					verdict = "Verdict: Pass (no errors)";
				} else if (totalFails == minorFails) {
					verdict = "Verdict: Pass? (all errors are minor)";
				} else if (totalFails == minorFails + moderateFails) {
					verdict = "Verdict: Pass? (all errors are minor or moderate)";
				} else if (totalFails == smallMarketFails) {
					verdict = "Verdict: Pass? (all fails are in small markets)";
				} else if (majorNonSmallFails == 0) {
					verdict = "Verdict: Pass? (all major fails are in small markets)";
				} else {
					verdict = "Verdict: Fail? (major, non-small market failures)";
				}
				String summary = "Total errors=" + totalFails + "; Major errors=" + majorFails + "; Moderate errors="
						+ moderateFails + "; Small market errors=" + smallMarketFails + "; "
						+ verdict + " (" + formatter.format(minRed * 100.0) + "-"
						+ formatter.format(minRed * 5.0 * 100.0) + "% thresholds)";
				tokenRows.add(new String[] { "Summary", summary });
				classifications.add("");
				smallMarkets.add("");

				ArrayList<String> narrativeLines = buildExperimentalNarrative(marketSignals, totalFails, majorFails,
						moderateFails, minorFails, smallMarketFails, minRed, formatter);
				if (!narrativeLines.isEmpty()) {
					tokenRows.add(new String[] { "Experimental new feature:", "" });
					classifications.add("");
					smallMarkets.add("");
					for (String narrativeLine : narrativeLines) {
						String displayLine = narrativeLine == null ? "" : narrativeLine.trim();
						if (!displayLine.isEmpty() && !displayLine.startsWith("- ")) {
							displayLine = "- " + displayLine;
						}
						tokenRows.add(new String[] { "", displayLine });
						classifications.add("");
						smallMarkets.add("");
					}
				}
			}
		} else {
			maxTokenCount = Math.max(maxTokenCount, 1);
			tokenRows.add(new String[] { "Main log not found" });
			classifications.add("");
			smallMarkets.add("");
		}

		int columnCount = 1 + Math.max(1, maxTokenCount) + 2;
		for (int i = 0; i < tokenRows.size(); i++) {
			ArrayList<String> fields = new ArrayList<>();
			fields.add(sanitizeCsvField(scenarioLabel));
			String[] tokens = tokenRows.get(i);
			for (int t = 0; t < Math.max(1, maxTokenCount); t++) {
				String token = (tokens != null && t < tokens.length) ? tokens[t] : "";
				fields.add(sanitizeCsvField(token));
			}
			fields.add(sanitizeCsvField(classifications.get(i)));
			fields.add(sanitizeCsvField(smallMarkets.get(i)));
			report.add(buildCsvRow(fields, columnCount));
		}
		return report;
	}

	private ArrayList<String> buildExperimentalNarrative(List<MarketSignal> marketSignals, int totalFails,
			int majorFails, int moderateFails, int minorFails, int smallMarketFails, double minRed,
			DecimalFormat formatter) {
		ArrayList<String> lines = new ArrayList<>();
		lines.add("Diagnostic narrative generated from unsolved-market patterns.");
		if (marketSignals == null || marketSignals.isEmpty()) {
			lines.add("No structured market rows were parsed; check main_log formatting and inspect raw ERROR lines.");
			return lines;
		}

		MarketSignal worst = marketSignals.get(0);
		for (MarketSignal signal : marketSignals) {
			if (signal.red > worst.red) {
				worst = signal;
			}
		}

		Map<String, Integer> perMarketCount = new HashMap<>();
		for (MarketSignal signal : marketSignals) {
			String key = signal.marketName == null || signal.marketName.trim().isEmpty() ? "<unknown market>" : signal.marketName;
			Integer prev = perMarketCount.get(key);
			perMarketCount.put(key, prev == null ? 1 : prev + 1);
		}

		String worstName = (worst.marketName == null || worst.marketName.trim().isEmpty()) ? "<unknown market>"
				: worst.marketName;
		Integer firstFailingPeriod = null;
		for (MarketSignal signal : marketSignals) {
			if (signal.period == null) {
				continue;
			}
			if (firstFailingPeriod == null || signal.period.intValue() < firstFailingPeriod.intValue()) {
				firstFailingPeriod = signal.period;
			}
		}
		if (firstFailingPeriod != null) {
			lines.add("Likely first-failing period: " + firstFailingPeriod
					+ " (start debugging there before later periods). ");
		}
		lines.add("Start with the worst market: " + worstName + " (RED=" + formatter.format(worst.red * 100.0)
				+ "%, demand=" + formatter.format(worst.demand) + ").");

		Map<Integer, ArrayList<MarketSignal>> byPeriod = new TreeMap<>();
		for (MarketSignal signal : marketSignals) {
			if (signal.period == null) {
				continue;
			}
			ArrayList<MarketSignal> periodRows = byPeriod.get(signal.period);
			if (periodRows == null) {
				periodRows = new ArrayList<>();
				byPeriod.put(signal.period, periodRows);
			}
			periodRows.add(signal);
		}
		if (!byPeriod.isEmpty()) {
			lines.add("Period grouping (parseable):");
			for (Map.Entry<Integer, ArrayList<MarketSignal>> entry : byPeriod.entrySet()) {
				int period = entry.getKey();
				ArrayList<MarketSignal> rows = entry.getValue();
				MarketSignal periodWorst = rows.get(0);
				for (MarketSignal row : rows) {
					if (row.red > periodWorst.red) {
						periodWorst = row;
					}
				}
				String periodWorstName = periodWorst.marketName == null || periodWorst.marketName.trim().isEmpty()
						? "<unknown market>"
						: periodWorst.marketName;
				lines.add("  - Period " + period + ": " + rows.size() + " unsolved market(s); worst is "
						+ periodWorstName + " (RED=" + formatter.format(periodWorst.red * 100.0) + "%).");
			}
		}

		String typeSummary = buildDetectedMarketTypeSummary(marketSignals);
		if (!typeSummary.isEmpty()) {
			lines.add("Detected market types: " + typeSummary + ".");
		}

		if (majorFails > 0) {
			lines.add("Major failures exist (" + majorFails + "). This usually points to structural issues (missing supply/"
					+ "demand links, infeasible policy settings, or bad inputs), not just solver tolerance.");
		} else if (moderateFails > 0) {
			lines.add("No major failures, but moderate failures exist (" + moderateFails
					+ "). Check market setup before only adjusting solver settings.");
		} else {
			lines.add("All failures are minor. This often indicates near-solution behavior; consider relaxing tolerance or increasing iterations.");
		}

		TreeSet<String> smallMarketNames = new TreeSet<>();
		for (MarketSignal signal : marketSignals) {
			if (signal.smallMarket) {
				smallMarketNames.add(signal.marketName == null || signal.marketName.trim().isEmpty()
						? "<unknown market>"
						: signal.marketName);
			}
		}
		String cutoffText = formatter.format(SMALL_MARKET_DEMAND_CUTOFF);
		if (smallMarketFails == totalFails && totalFails > 0) {
			lines.add("All failures are in very small markets (demand <= " + cutoffText + "): "
					+ String.join(", ", smallMarketNames)
					+ ". These markets may be running into solver tolerance or precision limits.");
		} else if (smallMarketFails > 0) {
			lines.add(smallMarketFails + " of " + totalFails + " failures are in very small markets (supply and demand <= "
					+ cutoffText + "): " + String.join(", ", smallMarketNames)
					+ ". Prioritize the non-small-market failures first.");
		}
		if (smallMarketFails > 0) {
			lines.add("Tip: when demand is near zero, RED can be huge even for a tiny absolute imbalance. "
					+ "Check the absolute excess demand and whether the market matters for your results.");
			if (majorFails > 0 && smallMarketFails == totalFails) {
				lines.add("Because all failures are in tiny markets, a structural cause is less likely; "
						+ "try solver tolerance/iteration changes or a minimum market-size floor first.");
			}
		}

		ArrayList<String> persistentMarkets = new ArrayList<>();
		for (Map.Entry<String, Integer> e : perMarketCount.entrySet()) {
			if (e.getValue() != null && e.getValue() > 1) {
				persistentMarkets.add(e.getKey() + " (" + e.getValue() + " occurrences)");
			}
		}
		if (!persistentMarkets.isEmpty()) {
			Collections.sort(persistentMarkets);
			String first = persistentMarkets.get(0);
			lines.add("Persistent unsolved market pattern detected: " + first
					+ ". Repeated failures usually indicate a structural cause rather than solver noise.");
		}

		ArrayList<String> typeHints = buildMarketTypeHints(marketSignals);
		if (!typeHints.isEmpty()) {
			lines.add("Market-type-specific hints:");
			lines.addAll(typeHints);
		}

		double nearTolThreshold = minRed * 10.0;
		if (worst.red <= nearTolThreshold) {
			lines.add("Worst RED is within ~10x tolerance (" + formatter.format(nearTolThreshold * 100.0)
					+ "%). Try solver iteration/tolerance adjustments before deeper input changes.");
		}

		lines.add("Recommended next steps: inspect supply-demand curve output for " + worstName
				+ ", then compare its XML inputs against a known-good scenario.");
		return lines;
	}

	private String buildDetectedMarketTypeSummary(List<MarketSignal> marketSignals) {
		if (marketSignals == null || marketSignals.isEmpty()) {
			return "";
		}
		Set<String> found = new HashSet<>();
		for (MarketSignal signal : marketSignals) {
			String type = signal.marketType == null ? "" : signal.marketType.trim();
			if (!type.isEmpty() && !"NORMAL".equals(type)) {
				found.add(type);
			}
		}
		if (found.isEmpty()) {
			return "NORMAL/UNSPECIFIED";
		}
		ArrayList<String> ordered = new ArrayList<>(found);
		Collections.sort(ordered);
		return String.join(", ", ordered);
	}

	private ArrayList<String> buildMarketTypeHints(List<MarketSignal> marketSignals) {
		ArrayList<String> hints = new ArrayList<>();
		if (marketSignals == null || marketSignals.isEmpty()) {
			return hints;
		}
		boolean hasTax = false;
		boolean hasCalibration = false;
		boolean hasTrial = false;
		boolean hasDemandLike = false;
		boolean hasResSubsidy = false;
		for (MarketSignal signal : marketSignals) {
			String type = signal.marketType == null ? "" : signal.marketType.trim();
			if ("TAX".equals(type)) {
				hasTax = true;
			}
			if ("CALIBRATION".equals(type)) {
				hasCalibration = true;
			}
			if ("TRIAL".equals(type)) {
				hasTrial = true;
			}
			if ("DEMAND_PRICE".equals(type)) {
				hasDemandLike = true;
			}
			if ("RES_SUBSIDY".equals(type)) {
				hasResSubsidy = true;
			}
		}
		if (hasTax) {
			hints.add("  - Tax/emissions markets are failing: check policy trajectory feasibility and abatement availability.");
		}
		if (hasCalibration) {
			hints.add("  - Calibration markets are failing: verify base-year calibration inputs and consistency across sectors.");
		}
		if (hasTrial) {
			hints.add("  - Trial markets are failing: inspect simultaneity loops (e.g., land/biomass/water linkages).");
		}
		if (hasDemandLike) {
			hints.add("  - Demand/price-linked markets are failing: ensure counterpart linked markets clear in the same period.");
		}
		if (hasResSubsidy) {
			hints.add("  - RES/subsidy-style policy markets are failing: check target stringency versus achievable qualifying supply.");
		}
		return hints;
	}

	private Integer extractPeriodFromTokens(String[] tokens) {
		if (tokens == null) {
			return null;
		}
		for (String token : tokens) {
			if (token == null) {
				continue;
			}
			String normalized = token.trim();
			if (normalized.isEmpty()) {
				continue;
			}
			String lower = normalized.toLowerCase();
			if (lower.startsWith("period")) {
				String digits = normalized.replaceAll("[^0-9]", "");
				if (!digits.isEmpty()) {
					try {
						return Integer.valueOf(digits);
					} catch (NumberFormatException ignored) {
						return null;
					}
				}
			}
		}
		return null;
	}

	private String extractMarketType(String[] tokens) {
		if (tokens == null) {
			return "";
		}
		int[] candidateIndices = { 11, 10, 12, 9, 8 };
		for (int index : candidateIndices) {
			if (index < 0 || index >= tokens.length || tokens[index] == null) {
				continue;
			}
			String candidate = tokens[index].trim();
			if (candidate.isEmpty()) {
				continue;
			}
			String canonicalType = canonicalizeMarketType(candidate);
			if (!canonicalType.isEmpty()) {
				return canonicalType;
			}
		}
		for (String token : tokens) {
			if (token == null) {
				continue;
			}
			String canonicalType = canonicalizeMarketType(token);
			if (!canonicalType.isEmpty()) {
				return canonicalType;
			}
		}
		return "";
	}

	private String canonicalizeMarketType(String value) {
		if (value == null) {
			return "";
		}
		String lower = value.trim().toLowerCase();
		if (lower.isEmpty()) {
			return "";
		}
		if (lower.contains("tax") || lower.contains("emissioncap") || lower.contains("ghgpolicy")
				|| lower.contains("co2") && lower.contains("policy")) {
			return "TAX";
		}
		if (lower.contains("inverse-calibration") || lower.contains("inverse calibration")
				|| lower.contains("calibration")) {
			return "CALIBRATION";
		}
		if (lower.contains("trial") || lower.contains("use-trial-market")) {
			return "TRIAL";
		}
		if (lower.contains("portfolio-standard") || lower.contains("portfolio standard")
				|| lower.contains("res") || lower.contains("subsidy") || lower.contains("input-subsidy")) {
			return "RES_SUBSIDY";
		}
		if (lower.contains("demand") || lower.contains("price")) {
			return "DEMAND_PRICE";
		}
		if (lower.equals("normal") || lower.contains("normal market")) {
			return "NORMAL";
		}
		return "";
	}

	private static final class MarketSignal {
		private final String marketName;
		private final double red;
		private final double demand;
		private final String classification;
		private final boolean smallMarket;
		private final Integer period;
		private final String marketType;

		private MarketSignal(String marketName, double red, double demand, String classification,
				boolean smallMarket, Integer period, String marketType) {
			this.marketName = marketName;
			this.red = red;
			this.demand = demand;
			this.classification = classification;
			this.smallMarket = smallMarket;
			this.period = period;
			this.marketType = marketType;
		}
	}

	/**
	 * Converts raw report rows into a rectangular CSV table with a header row.
	 *
	 * @param rows raw report rows generated from the log
	 * @return padded table rows suitable for popup display
	 */
	public ArrayList<String> buildErrorReportTable(ArrayList<String> rows) {
		ArrayList<String> table = new ArrayList<>();
		int maxCols = 0;
		if (rows != null) {
			for (String row : rows) {
				if (row == null)
					continue;
				int len = row.split(",", -1).length;
				if (len > maxCols)
					maxCols = len;
			}
		}
		if (maxCols < 4)
			maxCols = 4;
		int tokenCols = Math.max(1, maxCols - 3);
		ArrayList<String> header = new ArrayList<>();
		header.add("Scenario");
		for (int i = 1; i <= tokenCols; i++) {
			header.add("Field" + i);
		}
		header.add("Classification");
		header.add("SmallMarket");
		table.add(buildCsvRow(header, header.size()));

		if (rows == null || rows.isEmpty()) {
			ArrayList<String> noErr = new ArrayList<>();
			noErr.add(" ");
			noErr.add("No errors reported");
			table.add(buildCsvRow(noErr, header.size()));
			return table;
		}
		for (String row : rows) {
			String[] parts = row == null ? new String[0] : row.split(",", -1);
			ArrayList<String> fields = new ArrayList<>();
			Collections.addAll(fields, parts);
			table.add(buildCsvRow(fields, header.size()));
		}
		return table;
	}

	/**
	 * Summarizes error severities from parsed GCAM error lines.
	 *
	 * @param errors error lines to classify
	 * @param minRed minimum relative difference threshold for non-minor errors
	 * @return compact summary string, or an empty string when no errors were found
	 */
	public String processErrors(ArrayList<String> errors, double minRed) {
		if (errors == null)
			return "";
		double minDmd = SMALL_MARKET_DEMAND_CUTOFF;
		int total = 0;
		int major = 0;
		int moderate = 0;
		int minor = 0;
		int smallMarkets = 0;
		for (String errorLine : errors) {
			String normalizedLine = stripKnownLogLinePrefixes(errorLine);
			if (normalizedLine.isEmpty())
				continue;
			String normalized = normalizedLine.replace(":", ",");
			String[] tokens = normalized.split(",");
			try {
				if (tokens.length > 12) {
					double red = Double.parseDouble(tokens[7].trim());
					double dmd = Double.parseDouble(tokens[9].trim());
					String mkt = tokens[12].trim();
					total++;
					double other = Double.parseDouble(tokens[10].trim());
					if (Math.abs(dmd) <= minDmd && Math.abs(other) <= minDmd)
						smallMarkets++;
					if ((red > minRed) && (!mkt.contains("water consumption"))) {
						if (red > minRed * 5.0) {
							major++;
						} else {
							moderate++;
						}
					} else {
						minor++;
					}
				}
			} catch (Exception e) {
				// ignore parse errors
			}
		}
		if (total == 0)
			return "";
		return "total=" + total + ";major=" + major + ";moderate=" + moderate + ";minor=" + minor
				+ ";small=" + smallMarkets;
	}

	private String stripKnownLogLinePrefixes(String line) {
		if (line == null) {
			return "";
		}
		String normalized = line.trim();
		if (normalized.isEmpty()) {
			return "";
		}
		boolean changed;
		do {
			changed = false;
			for (String prefix : LOG_LINE_LEVEL_PREFIXES) {
				if (prefix == null || prefix.isEmpty()) {
					continue;
				}
				if (startsWithIgnoreCase(normalized, prefix + ":")) {
					normalized = normalized.substring(prefix.length() + 1).trim();
					changed = true;
					break;
				}
			}
		} while (changed && !normalized.isEmpty());
		return normalized;
	}

	private boolean startsWithAnyPrefix(String line, String[] prefixes) {
		if (line == null || prefixes == null) {
			return false;
		}
		for (String prefix : prefixes) {
			if (prefix != null && !prefix.isEmpty() && startsWithIgnoreCase(line, prefix)) {
				return true;
			}
		}
		return false;
	}

	private boolean startsWithIgnoreCase(String value, String prefix) {
		if (value == null || prefix == null || value.length() < prefix.length()) {
			return false;
		}
		return value.regionMatches(true, 0, prefix, 0, prefix.length());
	}
}
