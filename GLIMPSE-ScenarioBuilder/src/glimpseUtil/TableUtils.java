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
package glimpseUtil;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.StringTokenizer;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

public class TableUtils {

	public static NumberFormat numberFormatter = NumberFormat.getNumberInstance();

	/**
	 * Install the keyboard handler: + CTRL + C = copy to clipboard + CTRL + V =
	 * paste to clipboard
	 * 
	 * @param table
	 */
	public static void installCopyPasteHandler(TableView<?> table) {

		// install copy/paste keyboard handler
		table.setOnKeyPressed(new TableKeyEventHandler());

	}

	/**
	 * Copy/Paste keyboard event handler. The handler uses the keyEvent's source
	 * for the clipboard data. The source must be of type TableView.
	 */
	public static class TableKeyEventHandler implements EventHandler<KeyEvent> {

		KeyCodeCombination copyKeyCodeCompination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
		KeyCodeCombination pasteKeyCodeCompination = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY);

		@Override
		public void handle(final KeyEvent keyEvent) {

			if (copyKeyCodeCompination.match(keyEvent)) {

				if (keyEvent.getSource() instanceof TableView) {

					// copy to clipboard
					copySelectionToClipboard((TableView<?>) keyEvent.getSource());

					// event is handled, consume it
					keyEvent.consume();

				}

			} else if (pasteKeyCodeCompination.match(keyEvent)) {

				if (keyEvent.getSource() instanceof TableView) {

					// copy to clipboard
					pasteFromClipboard((TableView<?>) keyEvent.getSource());

					// event is handled, consume it
					keyEvent.consume();

				}

			}

		}

	}

	/**
	 * Get table selection and copy it to the clipboard.
	 * 
	 * @param table
	 */
	public static void copySelectionToClipboard(TableView<?> table) {

		StringBuilder clipboardString = new StringBuilder();

		ObservableList<Integer> positionList = table.getSelectionModel().getSelectedIndices();

		int item_no = 0;

		for (Integer position : positionList) {

			int row = position.intValue();

			for (int col = 0; col < table.getColumns().size(); col++) {

				if (col == 0) {
					if (item_no != 0)
						clipboardString.append('\n');
					item_no++;
				} else {
					clipboardString.append('\t');
				}

				// create string from cell
				String text = "";

				Object observableValue = table.getColumns().get(col).getCellObservableValue(row);

				// null-check: provide empty string for nulls
				if (observableValue == null) {
					text = "";
				} else if (observableValue instanceof DoubleProperty) {

					text = numberFormatter.format(((DoubleProperty) observableValue).get());

				} else if (observableValue instanceof IntegerProperty) {

					text = numberFormatter.format(((IntegerProperty) observableValue).get());

				} else if (observableValue instanceof StringProperty) {

					text = ((StringProperty) observableValue).get();

				} else {
					System.out.println("Unsupported observable value: " + observableValue);
				}

				// add new item to clipboard
				clipboardString.append(text);
			}
		}

		// create clipboard content
		final ClipboardContent clipboardContent = new ClipboardContent();
		clipboardContent.putString(clipboardString.toString());

		// set clipboard content
		Clipboard.getSystemClipboard().setContent(clipboardContent);

	}

	public static void pasteFromClipboard(TableView<?> table) {

		int table_size = table.getItems().size();
		System.out.println("Table size: " + table_size);

		// abort if there's not cell selected to start with
		if (table.getSelectionModel().getSelectedCells().size() == 0) {
			return;
		}

		int row = table.getSelectionModel().getSelectedIndices().get(0);

		System.out.println("Pasting starting at row " + row);

		String pasteString = Clipboard.getSystemClipboard().getString();

		System.out.println(pasteString);

		int rowClipboard = -1;

		StringTokenizer rowTokenizer = new StringTokenizer(pasteString, "\n");
		while (rowTokenizer.hasMoreTokens()) {

			rowClipboard++;

			String rowString = rowTokenizer.nextToken();

			StringTokenizer columnTokenizer = new StringTokenizer(rowString, "\t");

			int colClipboard = -1;

			while (columnTokenizer.hasMoreTokens()) {

				colClipboard++;

				// get next cell data from clipboard
				String clipboardCellContent = columnTokenizer.nextToken();
				clipboardCellContent = clipboardCellContent.replace('\t', ' ').replace('\n', ' ').trim();

				// calculate the position in the table cell
				int rowTable = row + rowClipboard;
				int colTable = 0 + colClipboard;

				// skip if we reached the end of the table
				if (rowTable >= table.getItems().size()) {
					int extra=rowTable - table.getItems().size();
					System.out.println("More rows being pasted than in table: " + extra + ".");
					continue;
				}
				if (colTable >= table.getColumns().size()) {
					System.out.println("More columns being pasted than in table: " + (colTable - table.getItems().size()) + ".");
					continue;
				}

				// System.out.println( rowClipboard + "/" + colClipboard + ": "
				// + cell);

				// get cell
				TableColumn tableColumn = table.getColumns().get(colTable);
				ObservableValue observableValue = tableColumn.getCellObservableValue(rowTable);

				System.out.println(rowTable + "/" + colTable + ": " + observableValue);

				// TODO: handle boolean, etc
				if (observableValue instanceof DoubleProperty) {

					try {

						double value = numberFormatter.parse(clipboardCellContent).doubleValue();
						((DoubleProperty) observableValue).set(value);

					} catch (ParseException e) {
						e.printStackTrace();
					}

				} else if (observableValue instanceof IntegerProperty) {

					try {

						int value = NumberFormat.getInstance().parse(clipboardCellContent).intValue();
						((IntegerProperty) observableValue).set(value);

					} catch (ParseException e) {
						e.printStackTrace();
					}

				} else if (observableValue instanceof StringProperty) {

					((StringProperty) observableValue).set(clipboardCellContent);

				} else {

					System.out.println("Unsupported observable value: " + observableValue);

				}

				System.out.println(rowTable + "/" + colTable);
				
			}

		}

	}

}