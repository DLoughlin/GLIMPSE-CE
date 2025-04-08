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

import java.util.ArrayList;

import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import glimpseUtil.TableUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class PaneForComponentDetails extends VBox {
	private GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	private GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	private GLIMPSEUtils utils = GLIMPSEUtils.getInstance();
	public TableView<DataPoint> table = new TableView<DataPoint>();
	public ObservableList<DataPoint> data = FXCollections.observableArrayList();
	HBox inputHBox = new HBox();
	TextField textFieldCol0 = utils.createTextField();
	TextField textFieldCol1 = utils.createTextField();
	Button buttonAdd = utils.createButton("Add", styles.bigButtonWid, null);
	boolean enforceYrValPair = true;
//
	TableColumn<DataPoint,String> col0;
	TableColumn<DataPoint,String> col1;

	public PaneForComponentDetails() {

		this.setStyle(styles.font_style);

		col0 = new TableColumn<DataPoint, String>("Year");
		col1 = new TableColumn<DataPoint, String>("Value");
		
		table.getColumns().addAll(col0, col1);
		table.setEditable(true);
		
		col0.setCellValueFactory(new PropertyValueFactory<DataPoint, String>("year"));
		col0.setCellFactory(TextFieldTableCell.forTableColumn());
		col0.prefWidthProperty().bind(table.widthProperty().divide(8. / 3.)); 
		col0.setStyle(styles.style5);
		col0.setEditable(true);


		
		col1.setCellValueFactory(new PropertyValueFactory<DataPoint, String>("value"));
		col1.setCellFactory(TextFieldTableCell.forTableColumn());
		col1.prefWidthProperty().bind(table.widthProperty().divide(8. / 5.));
		col1.setStyle(styles.style5);
		col1.setEditable(true);


		col0.setOnEditCommit(new EventHandler<CellEditEvent<DataPoint, String>>() {
			@Override
			public void handle(CellEditEvent<DataPoint, String> t) {
				t.getTableView().getItems().get(t.getTablePosition().getRow()).setYear(t.getNewValue());
			}
		});
		col1.setOnEditCommit(new EventHandler<CellEditEvent<DataPoint, String>>() {
			@Override
			public void handle(CellEditEvent<DataPoint, String> t) {
				t.getTableView().getItems().get(t.getTablePosition().getRow()).setValue(t.getNewValue());
			}
		});


		table.setItems(data);
		
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		TableUtils.installCopyPasteHandler(table);

		textFieldCol0.prefWidthProperty().bind(table.widthProperty().divide(8. / 2.75));
		textFieldCol1.prefWidthProperty().bind(table.widthProperty().divide(8. / 3.75));
		
		inputHBox.getChildren().addAll(textFieldCol0, textFieldCol1, buttonAdd);
		inputHBox.setSpacing(3.);
		inputHBox.setPadding(new Insets(3., 0., 0., 0.));

		// Action
		buttonAdd.setOnAction(e -> {
			DataPoint dp = new DataPoint(textFieldCol0.getText(), textFieldCol1.getText());
			if (dp.qaDataPoint(enforceYrValPair))
				data.add(dp);
		});


		this.getChildren().addAll(table, inputHBox);

		// echoData("end of constructor");
	}

	public void addItem(String name1) {
		DataPoint dp = new DataPoint(null, name1);
		data.add(dp);
	}

	public void addItem(String name0, String name1) {
		DataPoint dp = new DataPoint(name0, name1);
		data.add(dp);
	}

	public void setColumnFormatting(String s0, String s1) {
		col0.setStyle(s0);
		col1.setStyle(s1);
	}

	public void setColumnNames(String name0, String name1) {
		if (name0 != null) {
			col0.setText(name0);
			if (name1 == null) {
				col0.prefWidthProperty().bind(table.widthProperty().divide(1.));
				hideValColumn();
			}

		}
		if (name1 != null) {
			col1.setText(name1);
			if (name0 == null) {
				col1.prefWidthProperty().bind(table.widthProperty().divide(1.));
				hideYrColumn();
			}
		}
	}

	public void hideYrColumn() {
		col0.setVisible(false);
	}

	public void hideValColumn() {
		col1.setVisible(false);
	}

	public void setAddItemVisible(boolean b) {
		inputHBox.setVisible(b);
	}

	public void setEnforceYrValPair(boolean b) {
		enforceYrValPair = b;
	}

	public DataPoint createDataPoint(String year, String value) {
		DataPoint dp = new DataPoint(year, value);
		return dp;
	}

	public boolean isEmpty() {
		boolean empty = true;
		if (table.getItems().size() > 0)
			empty = false;
		return empty;
	}

	public void deleteItemsFromTable() {

		if (!utils.confirmDelete())
			return;

		ObservableList<DataPoint> selectedDataPoints = table.getSelectionModel().getSelectedItems();

		for (DataPoint i : selectedDataPoints) {
			data.remove(i);
		}

	}

	public void moveItemUpInTable() {
		ObservableList<DataPoint> allItems = table.getItems();

		ObservableList<DataPoint> selectedItems = table.getSelectionModel().getSelectedItems();
		if (selectedItems.size() == 1) {
			int n = table.getSelectionModel().getSelectedIndex();
			if (n - 1 >= 0) {
				DataPoint dataA = allItems.get(n);
				DataPoint dataB = allItems.get(n - 1);
				allItems.set(n - 1, dataA);
				allItems.set(n, dataB);
				table.setItems(allItems);
			}
		}
	}

	public void moveItemDownInTable() {
		ObservableList<DataPoint> allItems = table.getItems();

		ObservableList<DataPoint> selectedItems = table.getSelectionModel().getSelectedItems();
		if (selectedItems.size() == 1) {
			int n = table.getSelectionModel().getSelectedIndex();
			if (n < allItems.size() - 1) {
				DataPoint dataA = allItems.get(n);
				DataPoint dataB = allItems.get(n + 1);
				allItems.set(n + 1, dataA);
				allItems.set(n, dataB);
				table.setItems(allItems);
			}
		}
	}

	public String dataOutput() {
		String str_data = "";

		ObservableList<DataPoint> tableData = table.getItems();

		for (int i = 0; i < tableData.size(); i++) {
			str_data += "Item " + i + " = " + tableData.get(i).getYear() + " , " + tableData.get(i).getValue()
					+ vars.getEol();
		}

		return str_data;
	}
	

	public ArrayList<String> getDataYrValsArrayList() {
		String str_data = "";

		ArrayList<String> data = new ArrayList<String>();
		
		ObservableList<DataPoint> tableData = table.getItems();

		for (int i = 0; i < tableData.size(); i++) {
			str_data = tableData.get(i).getYear() + " , " + tableData.get(i).getValue();
			data.add(str_data);
		}

		return data;
	}

	public ArrayList<String> getValues() {
		ArrayList<String> column = new ArrayList<String>();
		ObservableList<DataPoint> tableData = table.getItems();

		for (int i = 0; i < tableData.size(); i++) {
			String s = tableData.get(i).getValue().trim();

			if (s != null)
				column.add(s);

		}

		return column;
	}

	public void clearTable() {
		data.clear();
	}

	public void updateTable() {
		table.setItems(data);

		//echoData("end of updateTable");
	}

	public void echoData(String str) {
		System.out.println(str);
		for (int i = 0; i < data.size(); i++) {
			System.out.println(" i: " + i + " " + data.get(i).getYear() + " " + data.get(i).getValue());
		}
	}

	public void setValues(double[][] values) {
		data.clear();

		for (int i = 0; i < values[0].length; i++) {
			int yr = (int) values[0][i];
			double val = values[1][i];
			data.add(new DataPoint(yr, val));
		}
		updateTable();
	}

	public void setValues(String[][] values) {
		data.clear();
		for (int i = 0; i < values[0].length; i++) {
			String col0 = values[0][i];
			String col1 = values[1][i];
			data.add(new DataPoint(col0, col1));
		}
		updateTable();
	}

	public class DragSelectionCellFactory implements
			Callback<TableColumn<DataPoint, String>, TableCell<DataPoint, String>> {

		@Override
		public TableCell<DataPoint, String> call(final TableColumn<DataPoint, String> col) {
			return new DragSelectionCell();
		}

	}

	public class DragSelectionCell extends TextFieldTableCell<DataPoint, String> {

		private TextField textField;

		public DragSelectionCell() {
			setOnDragDetected(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					startFullDrag();
					getTableColumn().getTableView().getSelectionModel().select(getIndex(), getTableColumn());
				}
			});
			setOnMouseDragEntered(new EventHandler<MouseDragEvent>() {

				@Override
				public void handle(MouseDragEvent event) {
					getTableColumn().getTableView().getSelectionModel().select(getIndex(), getTableColumn());
				}

			});

		}

		@Override
		public void startEdit() {
			if (!isEmpty()) {
				super.startEdit();
				TextField textField=utils.createTextField();
				setText(null);
				setGraphic(textField);
				textField.selectAll();
			}
		}

		@Override
		public void cancelEdit() {
			super.cancelEdit();

			setText(getItem());
			setGraphic(null);
		}

		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					if (textField != null) {
						textField.setText(getString());
					}
					setText(null);
					setGraphic(textField);
				} else {
					setText(getString());
					setGraphic(null);
				}
			}
		}

		private String getString() {
			return getItem() == null ? "" : getItem().toString();
		}

	}
}