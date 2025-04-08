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
import java.nio.file.Path;
import java.util.Optional;

import glimpseElement.CsvToXmlWidget;
import glimpseElement.NewDBWidget;
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import gui.Client;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class SetupMenuTools {

	Menu menuTools;
	GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	GLIMPSEUtils utils = GLIMPSEUtils.getInstance();
	GLIMPSEFiles files = GLIMPSEFiles.getInstance();

	public void setup(Menu menuActions) {
		
		this.menuTools = menuActions;

		createNewDBItem(menuTools);
		createCheckInstallationItem(menuTools);
		createCheckDBSizeItem(menuTools);
		createArchiveItem(menuTools);
		createFixLostHandle(menuTools);

		menuActions.getItems().addAll(new SeparatorMenuItem());	
		createViewTrashFolderItem(menuTools);
		createEmptyTrashItem(menuTools);
		
		Menu menuAdvanced=new Menu("Advanced");
		
		menuActions.getItems().addAll(new SeparatorMenuItem(),menuAdvanced);		
		createXMLItem(menuAdvanced);
		createCleanupItem(menuAdvanced);
			
		//Dan: Termination options doesn't yet work correctly; Hidden
//		menuActions.getItems().addAll(new SeparatorMenuItem());
//		createTerminateAllJobsItem();
	}

	private void createCheckInstallationItem(Menu menu) {
		MenuItem menuItemCheckOptions = new MenuItem("Check Installation");
		menuItemCheckOptions.setOnAction(e -> {
			//utils.showInformationDialog("Analysis of GLIMPSE Setup","",vars.examineGLIMPSESetup());
			utils.displayString(vars.examineGLIMPSESetup(),"Analysis of GLIMPSE setup");
		});
		menu.getItems().addAll(menuItemCheckOptions);
	}
	
	private void createXMLItem(Menu menu) {
		MenuItem menuItemCreateXML = new MenuItem("CSV to XML");
		menuItemCreateXML.setOnAction(e -> {
			//utils.warningMessage("CreateXML functionality not implemented yet.");
			CsvToXmlWidget csv2xml = new CsvToXmlWidget();
			csv2xml.createAndShow();
		});
		menu.getItems().addAll(menuItemCreateXML);
	}

	private void createCheckDBSizeItem(Menu menu) {
		MenuItem menuItemCheckDBSize = new MenuItem("Check Current DB Size");
		menuItemCheckDBSize.setOnAction(e -> {
			String database_name = vars.getgCamOutputDatabase();
			String database_short_name = database_name.substring(database_name.lastIndexOf(File.separator) + 1);
			File database_folder=new File(database_name);
			Path database_path=database_folder.toPath();
			float database_size=files.getDirectorySize(database_path)/1073741824;
			String database_size_str = " "+database_size+" GB";
			String message="Size is "+database_size_str+vars.getEol();
			if (database_size>vars.maxDatabaseSizeGB*0.75) {
			    message+="WARNING! Max advisable size is " +vars.maxDatabaseSizeGB +" GB. Please see Users Guide on managing database size."+vars.getEol();	
			} else {
				message+="Max advisable size is "+vars.maxDatabaseSizeGB+" GB"+vars.getEol();
			}
			String subtitle="Current database: "+database_short_name;
			utils.showInformationDialog("Check current DB size",subtitle, message);
		});
		menu.getItems().addAll(menuItemCheckDBSize);
	}
	
	private void createCleanupItem(Menu menu) {
		MenuItem menuItemCleanup = new MenuItem("Cleanup Saved Files");
		menuItemCleanup.setOnAction(e -> {
			if (utils.confirmAction("Move saved debug and solver_log files to trash?")) {
				String scenario_folder = vars.getScenarioDir();
				File[] sub_folders=new File(scenario_folder).listFiles(File::isDirectory);
				for (int i=0;i<sub_folders.length;i++) {
					File[] contents=new File(scenario_folder).listFiles(File::isFile);
					for (int j=0;j<contents.length;j++) {
						String name=contents[j].getName();
						if (name.indexOf("debug")>=0) {
							files.trash(contents[j]);
						}
						if (name.indexOf("solver_log")>=0) {
							files.trash(contents[j]);
						}
						if (name.indexOf("worst_market_log")>=0) {
							files.trash(contents[j]);
						}
					}
				}
			}
		});
		menu.getItems().addAll(menuItemCleanup);
	}
	
	private void createNewDBItem(Menu menu) {
		MenuItem menuItemCreateNewDB = new MenuItem("Create or Open Output Database");
		menuItemCreateNewDB.setOnAction(e -> {
			//utils.warningMessage("CreateXML functionality not implemented yet.");
			NewDBWidget newDBWidget = new NewDBWidget();
			newDBWidget.createAndShow();
		});
		//creating a DB using this approach has been buggy. Removing option from menu for now
		//menu.getItems().addAll(menuItemCreateNewDB);
	}
	
	private void createViewTrashFolderItem(Menu menu) {
		MenuItem menuItemViewTrashFolder = new MenuItem("Browse Trash");
		menuItemViewTrashFolder.setOnAction(e -> {
			files.openFileExplorer(vars.getTrashDir());
		});
		menu.getItems().addAll(menuItemViewTrashFolder);
	}

	private void createEmptyTrashItem(Menu menu) {
		MenuItem menuItemEmptyTrash = new MenuItem("Empty Trash");
		menuItemEmptyTrash.setOnAction(e -> {
			if (!confirmDeleteTrash()) {
				return;
			} else {
				emptyTrash();
			}

		});
		menu.getItems().addAll(menuItemEmptyTrash);
	}
	
	private void createArchiveItem(Menu menu) {
		MenuItem menuItemArchiveScenario = new MenuItem("Archive Scenario");
		menuItemArchiveScenario.setOnAction(e -> {
			Client.buttonArchiveScenario.fire();
		});
		menu.getItems().addAll(menuItemArchiveScenario);
	}

	private void createFixLostHandle(Menu menu) {
		MenuItem menuItemFixLostHandle = new MenuItem("Fix Lost Handle");
		menuItemFixLostHandle.setOnAction(e -> {
			utils.fixLostHandle();
			Client.buttonRefreshScenarioStatus.fire();
		});
		menu.getItems().addAll(menuItemFixLostHandle);
	}
	
	private void createExamineScenarioItem(Menu menu) {
		MenuItem menuItemExamineScenario = new MenuItem("Examine Scenario for Issues");
		menuItemExamineScenario.setOnAction(e -> {
			Client.buttonExamineScenario.fire();
		});
		menu.getItems().addAll(menuItemExamineScenario);
	}
	
//	private void createTerminateAllJobsItem(Menu menu) {
//		MenuItem menuItemTerminateJobs = new MenuItem("Terminate All Jobs");
//		menuItemTerminateJobs.setOnAction(e -> {
//			try {
//				System.out.println("Attempting to shut down ModelInterface and any executing GCAM runs.");
//			Client.gCAMExecutionThread.shutdownNow();
//			Client.gCAMPPExecutionThread.shutdownNow();
//			Client.gCAMExecutionThread.isExecuting();
//
//			} catch(Exception ex) {
//				System.out.println("Exception terminating all jobs: "+e);
//			}
//		});
//		menu.getItems().addAll(menuItemTerminateJobs);
//	}
	
	private boolean confirmDeleteTrash() {
		// asks the user to confirm that they want to delete the trash
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Delete all items from trash folder?");
		alert.setContentText("Please confirm deletion.");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.CANCEL) {
			return false;
		}
		return true;
	}

	private void emptyTrash() {
		System.out.println("Attempting to delete files from trash: " + vars.getTrashDir());

		File trashFolder = new File(vars.getTrashDir());
		File contents[] = trashFolder.listFiles();

		if (contents != null) {
			for (File f : contents) {
				System.out.println("Deleting " + f.getName());
				deleteDir(f);
			}
		}
	}

	public void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				deleteDir(f);
			}
		}
		file.delete();
	}
	
	/*private boolean confirmArchiveScenario() {
		// asks the user to confirm that they want to delete the trash
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Archive selected scenario(s) by copying all files to scenario folder(s)?");
		alert.setContentText("Please confirm archive.");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.CANCEL) {
			return false;
		}
		return true;
	}*/

}
