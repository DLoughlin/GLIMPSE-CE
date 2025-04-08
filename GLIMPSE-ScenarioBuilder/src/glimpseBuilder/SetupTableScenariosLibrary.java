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

import java.io.File;
import java.util.Date;

import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import glimpseElement.ScenarioTable;
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import glimpseElement.ScenarioRow;

public class SetupTableScenariosLibrary {

	// initiates the singleton that holds program parameters
	GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	GLIMPSEUtils utils = GLIMPSEUtils.getInstance();
	GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	
	public SetupTableScenariosLibrary() {
		
	}
	
	public void setup() {//TableView<ScenarioRow>

		//TableView<ScenarioRow> 
		ScenarioTable.tableScenariosLibrary = new TableView<>(ScenarioTable.listOfScenarioRuns);

		TableColumn<ScenarioRow, String> scenNameCol = ScenarioTable.getScenNameColumn();
		scenNameCol.prefWidthProperty().bind(ScenarioTable.tableScenariosLibrary.widthProperty().divide(4.));

		TableColumn<ScenarioRow, String> runComponentsCol = ScenarioTable.getComponentsColumn();
		runComponentsCol.prefWidthProperty().bind(ScenarioTable.tableScenariosLibrary.widthProperty().divide(2.));

		TableColumn<ScenarioRow, Date> createdCol = ScenarioTable.getCreatedDateColumn();
		createdCol.prefWidthProperty().bind(ScenarioTable.tableScenariosLibrary.widthProperty().divide(5.));

		TableColumn<ScenarioRow, Date> startedCol = ScenarioTable.getStartedDateColumn();
		startedCol.prefWidthProperty().bind(ScenarioTable.tableScenariosLibrary.widthProperty().divide(5.));
		
		TableColumn<ScenarioRow, Date> completedCol = ScenarioTable.getCompletedDateColumn();
		completedCol.prefWidthProperty().bind(ScenarioTable.tableScenariosLibrary.widthProperty().divide(5.));
		
		TableColumn<ScenarioRow, String> statusCol = ScenarioTable.getStatusColumn();
		statusCol.prefWidthProperty().bind(ScenarioTable.tableScenariosLibrary.widthProperty().divide(10.));

		TableColumn<ScenarioRow, String> noErrCol = ScenarioTable.getNoErrColumn();
		noErrCol.prefWidthProperty().bind(ScenarioTable.tableScenariosLibrary.widthProperty().divide(10.));
		
		TableColumn<ScenarioRow, String> unsolvedMarketsCol = ScenarioTable.getUnsolvedMarketsColumn();
		unsolvedMarketsCol.prefWidthProperty().bind(ScenarioTable.tableScenariosLibrary.widthProperty().divide(8.));
		
		TableColumn<ScenarioRow, String> runtimeCol = ScenarioTable.getRuntimeColumn();
		runtimeCol.prefWidthProperty().bind(ScenarioTable.tableScenariosLibrary.widthProperty().divide(9.));
		
		ScenarioTable.tableScenariosLibrary.getColumns().addAll(scenNameCol, createdCol, /*startedCol,*/ completedCol, statusCol,unsolvedMarketsCol,runtimeCol);
		ScenarioTable.tableScenariosLibrary.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		ScenarioTable.tableScenariosLibrary.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {

					ScenarioRow mfr = ScenarioTable.tableScenariosLibrary.getSelectionModel().getSelectedItem();
					String filename = vars.get("scenarioDir") + File.separator+ mfr.getScenarioName()
							+ File.separator+"configuration_" + mfr.getScenarioName() + ".xml";

					files.showFileInXmlEditor(filename);
					
//					try {
//						String cmd = vars.get("xmlEditor") + " " + filename;
//
//						java.lang.Runtime rt = java.lang.Runtime.getRuntime();
//						@SuppressWarnings("unused")
//						java.lang.Process p = rt.exec(cmd);
//					} catch (Exception e) {
//						System.out.println("Could not use xml editor specified in options file. Using system default.");
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
					//String componentList=mfr.getComponents().replace(";",vars.getEol());
					//ScenarioTable.tableRunScenarios.setTooltip(new Tooltip(componentList));
				}

				if (event.isPrimaryButtonDown()) {
					ScenarioRow mfr = ScenarioTable.tableScenariosLibrary.getSelectionModel().getSelectedItem();
					
					if (ScenarioTable.tableScenariosLibrary.getSelectionModel().getSelectedCells().size()==1) {
						String componentList=mfr.getComponents().replace(";",vars.getEol());
						ScenarioTable.tableScenariosLibrary.setTooltip(new Tooltip(componentList));	
					} else {
						ScenarioTable.tableScenariosLibrary.setTooltip(null);
					}
					
					
					
				}
				

			}
		});

	}

}
