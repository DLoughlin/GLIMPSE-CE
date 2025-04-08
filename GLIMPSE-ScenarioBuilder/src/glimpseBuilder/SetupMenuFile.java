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
* Parks and Yadong Xu of ARA through the EPA�s Environmental Modeling and 
* Visualization Laboratory contract. 
* 
*/
package glimpseBuilder;

import java.util.ArrayList;

import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import gui.Client;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class SetupMenuFile {

	Menu menuFile;
	GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	GLIMPSEUtils utils = GLIMPSEUtils.getInstance();
	GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	GLIMPSEStyles styles = GLIMPSEStyles.getInstance();

	public void setup(Menu menuFile) {
		this.menuFile = menuFile;
		createShowOptionsMainMenuItem();
		//createCheckOptionsMainMenuItem();
		createEditOptionsMainMenuItem();
		createReloadOptionsMainMenuItem();
		menuFile.getItems().addAll(new SeparatorMenuItem());
		createImportScenarioMainMenuItem();
		menuFile.getItems().addAll(new SeparatorMenuItem());
		createExitMainMenuItem();

	}

	private void createShowOptionsMainMenuItem() {
		MenuItem menuItemShowOptions = new MenuItem("Show Options");
		menuItemShowOptions.setOnAction(e -> {
			showOptions();
		});
		menuFile.getItems().addAll(menuItemShowOptions);
	}
	
	private void createEditOptionsMainMenuItem() {
		MenuItem menuItemEditOptions = new MenuItem("Edit Options");
		menuItemEditOptions.setOnAction(e -> {
			files.showFileInTextEditor(vars.getOptionsFilename());
		});
		menuFile.getItems().addAll(menuItemEditOptions);
	}

	private void createReloadOptionsMainMenuItem() {
		MenuItem menuItemReloadOptions = new MenuItem("Reload Options");
		menuItemReloadOptions.setOnAction(e -> {
			loadOptions();
			utils.showInformationDialog("Information","Caution", "Existing scenarios must be re-created (+) for changes in the options file to be reflected in their configuration file.");
		});
		menuFile.getItems().addAll(menuItemReloadOptions);
	}
	
	private void createImportScenarioMainMenuItem() {
		MenuItem menuItemImportScenario = new MenuItem("Import Scenario");
		menuItemImportScenario.setOnAction(e -> {
			Client.buttonImportScenario.fire();
		});
		menuFile.getItems().addAll(menuItemImportScenario);
	} 

	private void createExitMainMenuItem() {
		MenuItem menuItemExit = new MenuItem("Exit");
		menuItemExit.setOnAction(e -> {
			System.exit(0);
		});
		menuFile.getItems().addAll(menuItemExit);
	}

	private void showOptions() {
		ArrayList<String> optionsList = vars.getArrayListOfOptions();
		displayOptions(optionsList);
	}

	private void displayOptions(ArrayList<String> arrayListArg) {
		utils.displayArrayList(arrayListArg, "Options");
	}

	public void loadOptions() {
		vars.loadOptions();
	}

}
