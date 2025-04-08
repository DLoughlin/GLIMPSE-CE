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

import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import glimpseElement.ComponentLibraryTable;
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import glimpseElement.ComponentRow;

public class SetupTableCreateScenario {

	// initiates the singleton that holds program parameters
	GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	GLIMPSEUtils utils = GLIMPSEUtils.getInstance();
	GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	
	// TableView<ComponentRow> tableCreateScenario;
	public SetupTableCreateScenario() {

	}

	public void setup() {// TableView<ComponentRow> setup() {

		ComponentLibraryTable.tableCreateScenario = new TableView<>(ComponentLibraryTable.listOfFilesCreateScenario);

		TableColumn<ComponentRow, String> polScenNameCol = ComponentLibraryTable.getFileNameColumn();
		polScenNameCol.prefWidthProperty().bind(ComponentLibraryTable.tableCreateScenario.widthProperty());// .divide(2));

		TableColumn<ComponentRow, Date> polScenDateCol = ComponentLibraryTable.getBirthDateColumn();
		polScenDateCol.prefWidthProperty().bind(ComponentLibraryTable.tableCreateScenario.widthProperty().divide(2));

		ComponentLibraryTable.tableCreateScenario.getColumns().addAll(polScenNameCol);
		ComponentLibraryTable.tableCreateScenario.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		enableDoubleClickForInfo();

		// return tableCreateScenario;
	}

	private void enableDoubleClickForInfo() {
		ComponentLibraryTable.tableCreateScenario.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				try {
					if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {

						ComponentRow mf1 = ComponentLibraryTable.tableCreateScenario.getSelectionModel().getSelectedItem();
						String filename = mf1.getAddress();

						files.showFileInTextEditor(filename);
						
//						try {
//							File f = new File(filename);
//							if (f.exists()) {
//								String cmd = vars.get("textEditor") + " " + filename;
//
//								java.lang.Runtime rt = java.lang.Runtime.getRuntime();
//								@SuppressWarnings("unused")
//								java.lang.Process p = rt.exec(cmd);
//							}
//						} catch (Exception e) {
//							utils.warningMessage("Problem opening file in editor.");
//							System.out.println("Error trying to open file to view with editor.");
//							System.out.println("   file: " + filename);
//							System.out.println("   editor: " + vars.get("textEditor"));
//							System.out.println("Error: " + e);
//
//						}
					}
				} catch (Exception ex) {
					;
				}
			}
		});
	}

}
