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

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class SetupMenuHelp {

	Menu menuHelp;
	GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	GLIMPSEUtils utils = GLIMPSEUtils.getInstance();
	GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	
	public void setup(Menu menuHelp) {

		this.menuHelp = menuHelp;
		createHelpGCAMDocs();
		createHelpGCAMUSADocs();
		menuHelp.getItems().addAll(new SeparatorMenuItem());
		createHelpGlimpseWebpage();
		createHelpGlimpseUsersGuide(); 
		createHelpGlimpseDocs();
		menuHelp.getItems().addAll(new SeparatorMenuItem());
		createAboutSB();
		createAboutEMI();
		createAboutOM();
		menuHelp.getItems().addAll(new SeparatorMenuItem());
		createAbout();

	}

	private void createAbout() {
		String gCamUSADocsURL = "http://jgcri.github.io/gcam-doc/";
		MenuItem menuItemAbout = new MenuItem("About");
		menuItemAbout.setOnAction(e -> {
			try {
				String filename=vars.getgCamGUIDir()+File.separator+"resources"+File.separator+"About-text.txt";
				ArrayList<String> about_lines=files.getStringArrayFromFile(filename,"#");
				utils.showInformationDialog("About GLIMPSE", "Information about the GLIMPSE prototype", utils.createStringFromArrayList(about_lines));
			} catch (Exception e1) {
				utils.warningMessage("Problem trying to display the About information.");
				System.out.println("Error trying to display About information.");
				e1.printStackTrace();
			}
		});
		menuHelp.getItems().addAll(menuItemAbout);
	}
	
	private void createHelpGCAMDocs() {
		String gCamUSADocsURL = "http://jgcri.github.io/gcam-doc/";
		MenuItem menuItemViewGcamDocs = new MenuItem("GCAM Docs (web)");
		menuItemViewGcamDocs.setOnAction(e -> {
			try {
				Desktop.getDesktop().browse(new URI(gCamUSADocsURL));
			} catch (Exception e1) {
				utils.warningMessage("Problem trying to display documentation information.");
				System.out.println("Error trying to display GLIMPSE information.");
				e1.printStackTrace();
			}
		});
		menuHelp.getItems().addAll(menuItemViewGcamDocs);
	}

	private void createHelpGCAMUSADocs() {
		String gCamUSADocsURL = "http://jgcri.github.io/gcam-doc/gcam-usa.html";
		MenuItem menuItemViewGcamUSADocs = new MenuItem("GCAM-USA Docs (web)");
		menuItemViewGcamUSADocs.setOnAction(e -> {
			try {
				Desktop.getDesktop().browse(new URI(gCamUSADocsURL));
			} catch (Exception e1) {
				utils.warningMessage("Problem trying to display documentation information.");
				System.out.println("Error trying to display GLIMPSE information.");
				e1.printStackTrace();
			}
		});
		menuHelp.getItems().addAll(menuItemViewGcamUSADocs);
	}

	private void createHelpGlimpseWebpage() {
		String glimpseWebpageURL = "https://epa.gov/glimpse";
		MenuItem menuItemGlimpse = new MenuItem("GLIMPSE Information (web)");
		menuItemGlimpse.setOnAction(e -> {
			try {
				Desktop.getDesktop().browse(new URI(glimpseWebpageURL));
			} catch (Exception e1) {
				utils.warningMessage("Problem trying to display documentation information.");
				System.out.println("Error trying to display GLIMPSE information.");
				e1.printStackTrace();
			}
		});
		menuHelp.getItems().addAll(menuItemGlimpse);
	}

	private void createHelpGlimpseUsersGuide() {
		
		String str=vars.getGlimpseDocDir();
		if (str==null) str=vars.getgCamGUIDir()+File.separator+"Docs"; 
		
		final String glimpseUsersGuide = str+File.separator+"index.htm";

		MenuItem menuItemGlimpseUsersGuide = new MenuItem("GLIMPSE Users Guide (html)");
		menuItemGlimpseUsersGuide.setOnAction(e -> {
			try {
				File guide=new File(glimpseUsersGuide);
				java.awt.Desktop.getDesktop().browse(guide.toURI());
				
			} catch (Exception e1) {
				utils.warningMessage("Problem trying to open HTML GLIMPSE Users Guide.");
				System.out.println("Problem trying to open HTML GLIMPSE Users Guide.");
				e1.printStackTrace();
			}
		});
		//menuHelp.getItems().addAll(menuItemGlimpseUsersGuide);
	}
	
	private void createHelpGlimpseDocs() {
		String glimpseDocsFolder = vars.getGlimpseDocDir();
		MenuItem menuItemGlimpseDocs = new MenuItem("GLIMPSE Document Folder");
		menuItemGlimpseDocs.setOnAction(e -> {
			try {
				files.openFileExplorer(glimpseDocsFolder);
			} catch (Exception e1) {
				utils.warningMessage("Problem trying to display documentation folder.");
				System.out.println("Error trying to display GLIMPSE documentation folder.");
				e1.printStackTrace();
			}
		});
		menuHelp.getItems().addAll(menuItemGlimpseDocs); 
	}
	
	private void createAboutSB() {
		//Modified to hard-code version date
		//String file_name = vars.getgCamGUIJarDir()  + File.separator+vars.getgCamGUIJar();
		//System.out.println("Getting lastModifiedDate for " + file_name+": "+ files.getLastModifiedInfoForFile(file_name));
		//String txt_SBVersion = "ScenarioBuilder v" + files.getLastModifiedInfoForFile(file_name);
		String txt_SBVersion = vars.getGLIMPSEVersion();
		MenuItem menuItemAboutSB = new MenuItem(txt_SBVersion);
		menuHelp.getItems().addAll(menuItemAboutSB);
	}

	private void createAboutEMI() {
		return; //not displaying this currently since it was showing unzipped date as opposed to creation date
		//String file_name = vars.getgCamPPExecutableDir() + File.separator + vars.getgCamPPExecutable();
		//System.out.println("Getting lastModifiedDate for " + file_name+": "+ files.getLastModifiedInfoForFile(file_name));
		//String txt_EMIVersion = "ModelInterface v" + files.getLastModifiedInfoForFile(file_name);
		//String txt_EMIVersion = "ModelInterface v2024.02.08";
		//MenuItem menuItemAboutEMI = new MenuItem(txt_EMIVersion);
		//menuHelp.getItems().addAll(menuItemAboutEMI);
	}

	private void createAboutOM() {
		return; //not displaying this currently since it was showing unzipped date as opposed to creation date
		//String file_name = vars.getgCamExecutableDir() + File.separator + vars.getgCamExecutable();
		//System.out.println("Getting lastModifiedDate for " + file_name+": "+ files.getLastModifiedInfoForFile(file_name));
		//String txt_OMVersion = "GCAM build " + files.getLastModifiedInfoForFile(file_name);
		//MenuItem menuItemAboutOM = new MenuItem(txt_OMVersion);
		//menuHelp.getItems().addAll(menuItemAboutOM);
	}


}