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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// the following class defines the General tab when the New button of the
// left pane is clicked.
public class TabXMLList extends PolicyTab {
	private GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	private GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	private GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	private GLIMPSEUtils utils = GLIMPSEUtils.getInstance();
	// private XmlElements xmlEle = XmlElements.getInstance();
	// Defining the table at the left (candidate scenario components)
	TableView<ComponentRow> tableIncludeXMLList = new TableView<>(ComponentLibraryTable.listOfFiles);
	// Initializing overall grid
	VBox paneIncludeXMLList = new VBox();
	// Initializing components of center column
	VBox vBoxCenter = new VBox();
	HBox hBoxHeaderCenter = new HBox();
	Label labelValue = utils.createLabel("XML Files: ");
	Button buttonAdd = utils.createButton("Add", styles.bigButtonWid, null);
	Button buttonDelete = utils.createButton("Delete", styles.bigButtonWid, null);
	Button buttonClear = utils.createButton("Clear", styles.bigButtonWid, null);
	Button buttonMoveUp = utils.createButton("Move Up", styles.bigButtonWid, null);
	Button buttonMoveDown = utils.createButton("Move Down", styles.bigButtonWid, null);

	PaneForComponentDetails paneForXMLList = new PaneForComponentDetails();

	public TabXMLList(String title, Stage stageX, TableView<ComponentRow> tableComponents) {
		// sets tab title
		this.setText(title);
		this.setStyle(styles.font_style);

		paneForXMLList.setColumnNames(null, "XML Filename");
		paneForXMLList.setAddItemVisible(false);
		String fmt = "-fx-alignment: CENTER-LEFT; -fx-padding: 5 20 5 5;";
		paneForXMLList.setColumnFormatting(fmt, fmt);

		// center column
		hBoxHeaderCenter.getChildren().addAll(buttonAdd, buttonMoveUp, buttonMoveDown, buttonDelete, buttonClear);
		hBoxHeaderCenter.setSpacing(2.);
		hBoxHeaderCenter.setStyle(styles.style3);

		vBoxCenter.getChildren().addAll(labelValue, hBoxHeaderCenter, paneForXMLList);
		vBoxCenter.setStyle(styles.style2);
		vBoxCenter.setFillWidth(true);

		// ---adding components to the overall grid---
		paneIncludeXMLList.getChildren().addAll(vBoxCenter);

		// ----buttons ------
		buttonClear.setOnAction(e -> {
			// tab.
			paneForXMLList.clearTable();
		});

		buttonAdd.setOnAction(e -> {
			File f = new File(vars.get("xmlLibrary"));
			FileChooser fc = new FileChooser();
			try {
				fc.setInitialDirectory(f);
			} catch (Exception e1) {
				utils.warningMessage("Could not find xmlLibrary.");
				System.out.println("Could not find xmlLibrary " + vars.get("xmlLibrary") + ". Defaulting to "
						+ vars.get("gCamExecutableDir"));
				fc.setInitialDirectory(new File(vars.get("gCamExecutableDir")));
			}

			FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
			fc.setSelectedExtensionFilter(filter);
			fc.setTitle("Select xml files");

			List<File> file = fc.showOpenMultipleDialog(stageX);

			if ((file != null) && (file.size() > 0)) {
				for (int i = 0; i < file.size(); i++) {
					paneForXMLList.addItem(
							files.getRelativePath(vars.get("gCamExecutableDir"), file.get(i).toString().trim()));
				}
			}
		});

		buttonDelete.setOnAction(e -> {
			// tab.
			paneForXMLList.deleteItemsFromTable();
		});

		buttonMoveUp.setOnAction(e -> {
			this.paneForXMLList.moveItemUpInTable();
		});

		buttonMoveDown.setOnAction(e -> {
			this.paneForXMLList.moveItemDownInTable();
		});

		VBox tabLayout = new VBox();
		tabLayout.getChildren().addAll(paneIncludeXMLList);// ,
															// hBoxButtons);

		this.setContent(tabLayout);
	}
	
	@Override
	public void loadContent(ArrayList<String> content) {
		
		int i = 0;
		for (String temp : content) {
			if (!temp.startsWith("@")) {
			  String str = "" + i;
			  i++;
			  paneForXMLList.addItem(str, temp);
			}
		}
		ComponentLibraryTable.tableComponents.refresh();

	}

	public void loadInfoFromFile(String filename, String typeString) {

		ArrayList<String> fileList = files.loadFileListFromFile(filename, typeString);
		
		loadContent(fileList);
		
	}



	public void run() {
		saveScenarioComponent();
	}

	@Override
	public void saveScenarioComponent() {

		filename_suggestion = "xml_list.txt";
		file_content = "@type=xmllist" + vars.getEol();
		ArrayList<String> fileList = paneForXMLList.getValues();
		for (int i = 0; i < fileList.size(); i++) {
			file_content += fileList.get(i) + vars.getEol();
		}

	}

}