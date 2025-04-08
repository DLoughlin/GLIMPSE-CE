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
package glimpseBuilder;

import java.util.Date;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import glimpseElement.ComponentLibraryTable;
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import glimpseElement.ComponentRow;

public class SetupTableComponentLibrary {

	private GLIMPSEUtils utils = GLIMPSEUtils.getInstance();
	private GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	private GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	private GLIMPSEStyles styles = GLIMPSEStyles.getInstance();

	public SetupTableComponentLibrary() {
		
	}
	
	public void setup() { //TableView<ComponentRow>

		//TableView<ComponentRow> 
		ComponentLibraryTable.tableComponents = new TableView<>(ComponentLibraryTable.listOfFiles);

		// Creates the _ column and sets its width to be 1/4 of the full table
		TableColumn<ComponentRow, String> nameCol = ComponentLibraryTable.getFileNameColumn();
		nameCol.prefWidthProperty().bind(ComponentLibraryTable.tableComponents.widthProperty().divide(1./(2./3.)));

		// Creates the _ column and sets its width to be 1/2 of the full table
		// Not currently shown in table!!!!
		TableColumn<ComponentRow, String> addressCol = ComponentLibraryTable.getAddressColumn();
		addressCol.prefWidthProperty().bind(ComponentLibraryTable.tableComponents.widthProperty().divide(2.));

		// Creates the _ column and sets its width to be 1/4 of the full table
		TableColumn<ComponentRow, Date> dateCol = ComponentLibraryTable.getBirthDateColumn();
		dateCol.prefWidthProperty().bind(ComponentLibraryTable.tableComponents.widthProperty().divide(3.));
		ComponentLibraryTable.tableComponents.getColumns().addAll(nameCol, /*addressCol,*/ dateCol);

		ComponentLibraryTable.tableComponents.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		showComponentDetailsOnDoubleClick();//ComponentTable.tableComponents);

		addFiltering();//ComponentTable.tableComponents);
		//System.out.println("   filterComponentsTextField: " + ComponentLibraryTable.filterComponentsTextField.getLength());
		//return tableComponents;

	}

	private void showComponentDetailsOnDoubleClick() {//TableView<ComponentRow> tableComponents) {

		ComponentLibraryTable.tableComponents.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					// If the table is double clicked
					ComponentRow mf1 = ComponentLibraryTable.tableComponents.getSelectionModel().getSelectedItem();
					String filename = mf1.getAddress();
					
					files.showFileInTextEditor(filename);

//					try {
//						String cmd = vars.getTextEditor() + " " + filename;
//
//						java.lang.Runtime rt = java.lang.Runtime.getRuntime();
//						@SuppressWarnings("unused")
//						
//						java.lang.Process p = rt.exec(cmd);
//					} catch (Exception e) {
//						System.out.println("Could not use text editor specified in options file. Using system default.");						
//						try {
//							File file=new File(filename);
//							java.awt.Desktop.getDesktop().edit(file);
//						} catch (Exception e1) {						
//							utils.warningMessage("Problem trying to open file with editor.");
//							System.out.println("Error trying to open file to view with editor.");
//							System.out.println("   file: " + filename);
//							System.out.println("   editor: " + vars.getTextEditor());
//							System.out.println("Error: " + e);
//						}
//					}
				}
				if (event.isPrimaryButtonDown()) {
					ComponentRow mf1 = ComponentLibraryTable.tableComponents.getSelectionModel().getSelectedItem();
					
					if (ComponentLibraryTable.tableComponents.getSelectionModel().getSelectedCells().size()==1) {
						String componentList=mf1.getAddress().replace(";",vars.getEol());
						ComponentLibraryTable.tableComponents.setTooltip(new Tooltip(componentList));	
					} else {
						ComponentLibraryTable.tableComponents.setTooltip(null);
					}
				}
			}
		});

	}

	private void addFiltering() {//TableView<ComponentRow> table) {

		ComponentLibraryTable.filterComponentsTextField = utils.createTextField();
		ComponentLibraryTable.filterComponentsTextField.setMinWidth(styles.bigButtonWid);

		FilteredList<ComponentRow> filteredComponents = new FilteredList<>(ComponentLibraryTable.tableComponents.getItems(), p -> true);

		ComponentLibraryTable.filterComponentsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredComponents.setPredicate(myfile1 -> {
				// If user hasn't typed anything into the search bar
					if (newValue == null || newValue.isEmpty()) {
						// Display all items
						return true;
					}

					// Compare items with filter text
					// Comparison is not case sensitive
					String lowerCaseFilter = newValue.toLowerCase();

					if (myfile1.getFileName().toLowerCase().contains(lowerCaseFilter)) {
						// Displays results that match
						return true;
					}
					return false; // Does not match.
				});
		});

		// Adds the ability to sort the list after being filtered
		SortedList<ComponentRow> sortedComponents = new SortedList<>(filteredComponents);
		sortedComponents.comparatorProperty().bind(ComponentLibraryTable.tableComponents.comparatorProperty());
		ComponentLibraryTable.tableComponents.setItems(sortedComponents);

	}

}
