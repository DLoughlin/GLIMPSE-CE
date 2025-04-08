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
import java.util.ArrayList;

import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class SetupMenuEdit {

	GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	GLIMPSEUtils utils = GLIMPSEUtils.getInstance();
	GLIMPSEFiles files = GLIMPSEFiles.getInstance();

	public void setup(Menu menuEdit) {

		Menu gcamSetupFiles = new Menu("GCAM Setup Files");
		Menu scenarioBuilderSetupFiles = new Menu("Scenario Builder Files");
		Menu modelInterfaceSetupFiles = new Menu("ModelInterface Files");
		Menu menuAdvancedFiles = new Menu("Advanced");		
		
		
		MenuItem menuItemEditPresetRegionFile = new MenuItem("Preset Region File");
		menuItemEditPresetRegionFile.setOnAction(e -> {
			String filename=vars.getPresetRegionsFilename();
			if (filename==null) filename=vars.getgCamGUIDir()+ File.separator +"resources"+ File.separator +"preset_region_list.txt";
			files.showFileInTextEditor(filename);
		});
		
		MenuItem menuItemEditScenarioTemplate = new MenuItem("Scenario Template");
		menuItemEditScenarioTemplate.setOnAction(e -> {
			String filename = vars.getConfigurationTemplate();
			files.showFileInTextEditor(filename);
		});
		MenuItem menuItemEditTchBndList = new MenuItem("Tech List for Bounds");
		menuItemEditTchBndList.setOnAction(e -> {
			String filename = vars.getTchBndListFile();
			files.showFileInTextEditor(filename);
		});

		MenuItem menuItemEditTrnVehInfo = new MenuItem("Transportation Vehicle Data");
		menuItemEditTrnVehInfo.setOnAction(e -> {
			String filename = vars.getTrnVehInfoFile();
			files.showFileInTextEditor(filename);
		});
		
		MenuItem menuItemEditXMLHeaderFile = new MenuItem("XML Header File");
		menuItemEditXMLHeaderFile.setOnAction(e -> {
			String filename = vars.getXmlHeaderFile();
			files.showFileInTextEditor(filename);
		});

		MenuItem menuItemEditCsvColumnNameFile = new MenuItem("CSV Column File");
		menuItemEditCsvColumnNameFile.setOnAction(e -> {
			String filename = vars.getCsvColumnFile();
			files.showFileInTextEditor(filename);
		});
		
		MenuItem menuItemEditQueryFile = new MenuItem("Query File");
		menuItemEditQueryFile.setOnAction(e -> {
			String filename=vars.getQueryFile();
			if (filename==null) filename=vars.getgCamPPExecutableDir() + File.separator +"Main_queries_GLIMPSE.xml";
			
			files.showFileInXmlEditor(filename);
		});

		MenuItem menuItemEditSolverFile = new MenuItem("Solver Config File");
		menuItemEditSolverFile.setOnAction(e -> {
			String filename=vars.getgCamSolver();
			files.showFileInTextEditor(filename);
		});
		
		MenuItem menuItemEditLogConfigFile = new MenuItem("Log Config File");
		menuItemEditLogConfigFile.setOnAction(e -> {
			String filename=vars.getgCamExecutableDir()+ File.separator +"log_conf.xml";
			files.showFileInTextEditor(filename);
		});
		
		MenuItem menuItemEditUnitConvFile = new MenuItem("MI Unit Conversions File");
		menuItemEditUnitConvFile.setOnAction(e -> {
			String filename=vars.getUnitConversionsFile();
			files.showFileInTextEditor(filename);
		});
		
		
		gcamSetupFiles.getItems().addAll(menuItemEditSolverFile,menuItemEditLogConfigFile);
		scenarioBuilderSetupFiles.getItems().addAll(menuItemEditScenarioTemplate,menuItemEditTchBndList,menuItemEditTrnVehInfo,menuItemEditPresetRegionFile,menuItemEditXMLHeaderFile,menuItemEditCsvColumnNameFile);
		modelInterfaceSetupFiles.getItems().addAll(menuItemEditQueryFile,menuItemEditUnitConvFile);
		
		//menuAdvancedFiles.getItems().addAll(menuItemEditScenarioTemplate,new SeparatorMenuItem(),menuItemEditSolverFile,menuItemEditLogConfigFile, new SeparatorMenuItem(),menuItemEditTchBndList,menuItemEditPresetRegionFile, menuItemEditXMLHeaderFile, menuItemEditCsvColumnNameFile, new SeparatorMenuItem(),menuItemEditQueryFile,menuItemEditUnitConvFile);

		
		menuEdit.getItems().addAll(scenarioBuilderSetupFiles,modelInterfaceSetupFiles,gcamSetupFiles);

	}

	private Path Paths(String getgCamExecutableDir) {
		// TODO Auto-generated method stub
		return null;
	}

}
