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

public class SetupMenuView {

	GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	GLIMPSEUtils utils = GLIMPSEUtils.getInstance();
	GLIMPSEFiles files = GLIMPSEFiles.getInstance();

	public void setup(Menu menuView) {

		//Dan: Created 'edit' menu and moved many of the setup/configuration files there
		//Menu menuAdvancedFiles = new Menu("Advanced");		
		Menu menuResourceLogs = new Menu("Resource Logs");
		Menu menuBrowseFolders = new Menu("Browse Folder");
		
		MenuItem menuItemViewDebugFile = new MenuItem("Debug File");
		menuItemViewDebugFile.setOnAction(e -> {
			String filename=vars.getgCamExecutableDir() + File.separator + "debug.xml";
			if ((vars.getDebugRename()=="1")||(vars.getDebugRename().toLowerCase()=="true")||(vars.getDebugRename().toLowerCase()=="yes")) {
			   filename = vars.getgCamExecutableDir() + File.separator + "debug"+vars.getDebugRegion().trim()+".xml";			   
			}
			files.showFileInTextEditor(filename);
		});

		MenuItem menuItemViewCurrentResourceLog = new MenuItem("Current Session");
		menuItemViewCurrentResourceLog.setOnAction(e -> {
			String filename=vars.getgCamGUILogDir() + File.separator + "glimpse_log.txt";
			files.showFileInTextEditor(filename);
		});
		
		MenuItem menuItemViewPriorResourceLog = new MenuItem("Prior Session");
		menuItemViewPriorResourceLog.setOnAction(e -> {
			String filename=vars.getgCamGUILogDir() + File.separator + "glimpse_log_prior.txt";
			files.showFileInTextEditor(filename);
		});
		
		MenuItem menuItemViewMainLog = new MenuItem("Current Main Log");
		menuItemViewMainLog.setOnAction(e -> {
			String filename = vars.getgCamExecutableDir() + File.separator+"logs"+File.separator+"main_log.txt";
			files.showFileInTextEditor(filename);
		});
		MenuItem menuItemViewErrorsInMainLog = new MenuItem("Errors in Main Log");
		menuItemViewErrorsInMainLog.setOnAction(e -> {
			String filename = vars.getgCamExecutableDir() + File.separator+"logs"+File.separator+"main_log.txt";
			ArrayList<String> errors=utils.generateErrorReport(filename,null);
//			errors.add("----------------");
//			errors.add(utils.processErrors(errors, 0.01));
//			errors.add("----------------");
			utils.displayArrayList(errors, "Error Report", false);
		});
		MenuItem menuItemViewSolverLog = new MenuItem("Current Solver Log");
		menuItemViewSolverLog.setOnAction(e -> {
			String filename = vars.getgCamExecutableDir() + File.separator+"logs"+File.separator+"solver_log.csv";
			files.showFileInTextEditor(filename);
		});		
		MenuItem menuItemViewWorstMarketLog = new MenuItem("Current Worst Market Log");
		menuItemViewWorstMarketLog.setOnAction(e -> {
			String filename = vars.getgCamExecutableDir() + File.separator+"logs"+File.separator+"worst_market_log.txt";
			files.showFileInTextEditor(filename);
		});
		MenuItem menuItemViewCalibrationLog = new MenuItem("Current Calibration Log");
		menuItemViewCalibrationLog.setOnAction(e -> {
			String filename = vars.getgCamExecutableDir() + File.separator+"logs"+File.separator+"calibration_log.txt";
			files.showFileInTextEditor(filename);
		});
		
		MenuItem menuItemBrowseGLIMPSEFolder = new MenuItem("GLIMPSE Folder");
		menuItemBrowseGLIMPSEFolder.setOnAction(e -> {
			String pathname = vars.getGlimpseDir();
			files.openFileExplorer(pathname);
		});
		
		MenuItem menuItemBrowseGLIMPSEScenarioFolder = new MenuItem("GLIMPSE Scenario Folder");
		menuItemBrowseGLIMPSEScenarioFolder.setOnAction(e -> {
			String pathname = vars.getScenarioDir();
			files.openFileExplorer(pathname);
		});

		MenuItem menuItemBrowseGLIMPSEScenarioComponentFolder = new MenuItem("GLIMPSE Scenario Component Folder");
		menuItemBrowseGLIMPSEScenarioComponentFolder.setOnAction(e -> {
			String pathname = vars.getScenarioComponentsDir();
			files.openFileExplorer(pathname);
		});
		
		MenuItem menuItemBrowseGLIMPSEContribFolder = new MenuItem("GLIMPSE Contrib Folder");
		menuItemBrowseGLIMPSEContribFolder.setOnAction(e -> {
			String pathname = vars.getGlimpseDir()+File.separator+"Contrib";
			files.openFileExplorer(pathname);
		});
		
		MenuItem menuItemBrowseGLIMPSETrashFolder = new MenuItem("GLIMPSE Trash Folder");
		menuItemBrowseGLIMPSETrashFolder.setOnAction(e -> {
			String pathname = vars.getTrashDir();
			files.openFileExplorer(pathname);
		});
		
		MenuItem menuItemBrowseGcamExeFolder = new MenuItem("GCAM exe Folder");
		menuItemBrowseGcamExeFolder.setOnAction(e -> {
			String pathname = vars.getgCamExecutableDir();
			files.openFileExplorer(pathname);
		});

		MenuItem menuItemBrowseGcamLogFolder = new MenuItem("GCAM log Folder");
		menuItemBrowseGcamLogFolder.setOnAction(e -> {
			String pathname = vars.getgCamExecutableDir()+File.separator+"logs";
			files.openFileExplorer(pathname);
		});
		
		MenuItem menuItemBrowseGcamInputFolder = new MenuItem("GCAM input Folder");
		menuItemBrowseGcamInputFolder.setOnAction(e -> {
			String pathname=new File(vars.getgCamExecutableDir()).getParentFile().getPath()+File.separator+"input";
			files.openFileExplorer(pathname);
		});

		MenuItem menuItemBrowseGcamOutputFolder = new MenuItem("GCAM output Folder");
		menuItemBrowseGcamOutputFolder.setOnAction(e -> {
			String pathname=new File(vars.getgCamExecutableDir()).getParentFile().getPath()+File.separator+"output";
			files.openFileExplorer(pathname);
		});
		
		//created new Edit menu and re-ordered items
		//menuAdvancedFiles.getItems().addAll(menuItemViewScenarioTemplate, new SeparatorMenuItem(),menuItemViewTchBndList,menuItemViewPresetRegionFile, menuItemViewXmlDescriptor, menuItemViewCsvDescriptor,menuItemViewSolverFile, menuItemViewLogConfigFile, menuItemViewQueryFile);

		menuResourceLogs.getItems().addAll(menuItemViewCurrentResourceLog,menuItemViewPriorResourceLog);
		
		menuBrowseFolders.getItems().addAll(menuItemBrowseGLIMPSEFolder,menuItemBrowseGLIMPSEScenarioFolder,menuItemBrowseGLIMPSEScenarioComponentFolder,menuItemBrowseGLIMPSEContribFolder,menuItemBrowseGLIMPSETrashFolder,new SeparatorMenuItem(),
				menuItemBrowseGcamExeFolder,menuItemBrowseGcamLogFolder,menuItemBrowseGcamInputFolder,menuItemBrowseGcamOutputFolder);
		
		menuView.getItems().addAll(menuItemViewMainLog,menuItemViewErrorsInMainLog,menuItemViewSolverLog,
				menuItemViewWorstMarketLog, menuItemViewCalibrationLog,menuItemViewDebugFile,new SeparatorMenuItem(),menuResourceLogs,new SeparatorMenuItem(),menuBrowseFolders);//,menuAdvancedFiles);

	}

}
