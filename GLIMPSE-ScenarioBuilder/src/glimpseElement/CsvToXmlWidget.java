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
* GLIMPSE-CE is a derivative of the open-source USEPA GLIMPSE software.
* For the GLIMPSE project, GCAM development, data processing, and support for 
* policy implementations has been led by Dr. Steven J. Smith of PNNL, via Interagency 
* Agreements 89-92423101 and 89-92549601. Contributors from PNNL include 
* Maridee Weber, Catherine Ledna, Gokul Iyer, Page Kyle, Marshall Wise, Matthew 
* Binsted, and Pralit Patel. 
* The lead GLIMPSE & GLIMPSE- CE developer is Dr. Dan Loughlin (formerly USEPA). 
* Contributors include Tai Wu (USEPA), Farid Alborzi (ORISE), and Aaron Parks and 
* Yadong Xu of ARA through the EPA Environmental Modeling and Visualization 
* Laboratory contract.
* 
*/
package glimpseElement;

import java.io.File;
import java.util.Optional;

import glimpseUtil.FileChooserPlus;
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import glimpseUtil.UtilsDialogs;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.w3c.dom.Document;

public class CsvToXmlWidget {
	protected GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	protected GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	protected GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	protected GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

	TextField csvFileTextField=utils.createTextField();
	TextField headerFileTextField=utils.createTextField();
	CheckBox usePresetRegionListCheckBox = new CheckBox("Use preset region list");
	String csvFilename="";
	String headersFilename=vars.getXmlHeaderFilename();
	String xmlFilename="";
	
	public void csvToXmlWidget() {
		System.out.println("Created CSV to XML Widget");
	}

	public void createAndShow() {
		String title="CSV to XML Converter";

		Stage stage = new Stage();
		try {
			Window owner = UtilsDialogs.getPrimaryOwnerWindow();
			if (owner != null) {
				stage.initOwner(owner);
			}
		} catch (Exception ignored) {}
		stage.setTitle(title);
		//stage.setWidth(200);
		//stage.setHeight(100);
		Scene scene = new Scene(new Group());
		gui.ScenarioBuilder.applyModernTheme(scene);
		//stage.setResizable(false);
		
		Label topLabel=utils.createLabel("Input files:");
		
		Label csvFileLabel=utils.createLabel("CSV file");
		//TextField csvFileTextField=utils.createTextField();
		Button browseForCsvFile=utils.createButton("Browse", styles.getBigButtonWidth(),"Locate CSV file",null);
		Label headerFileLabel=utils.createLabel("Header file");
		//TextField headerFileTextField=utils.createTextField();
		Button browseForHeaderFile=utils.createButton("Browse", styles.getBigButtonWidth(),"Locate header file",null);
		Button editPresetRegionListButton = utils.createButton("Edit", styles.getBigButtonWidth(),"Edit the preset region list",null);

		headerFileTextField.setText(headersFilename);
		usePresetRegionListCheckBox.setSelected(false);

		browseForCsvFile.setOnAction(e -> {
			csvFilename=browseForFile("CSV file (*.csv)","*.csv",vars.getGlimpseDir(),"Open");	
			csvFileTextField.setText(csvFilename);

			System.out.println("Updated "+csvFilename);
			System.out.println(csvFileTextField.getText());			
		});
		
		browseForHeaderFile.setOnAction(e -> {
			File headerFile=new File(headersFilename);
			String temp=browseForFile("TXT file (*.txt)","*.txt",headerFile.getParent(),"Open");	
			headersFilename=temp;
			headerFileTextField.setText(temp);
		});	
		
		editPresetRegionListButton.setOnAction(e -> {
			String presetFilename = vars.getPresetRegionListFilename();
			if (presetFilename == null || presetFilename.trim().isEmpty()) {
				utils.warningMessage("Preset region list filename is not set.");
				return;
			}
			// Prefer configured text editor; fall back to system default if needed.
			files.showFileInTextEditor(presetFilename);
		});
		
		GridPane grid = new GridPane();

		
		grid.setHgap(10);
		grid.setVgap(10);
		// Use centralized horizontal padding for grids
		grid.setPadding(styles.getHorizontalPadding10());
		grid.setMaxWidth(Double.MAX_VALUE);

		ColumnConstraints labelColumn = new ColumnConstraints();
		ColumnConstraints fieldColumn = new ColumnConstraints();
		fieldColumn.setHgrow(Priority.ALWAYS);
		fieldColumn.setFillWidth(true);
		ColumnConstraints buttonColumn = new ColumnConstraints();
		buttonColumn.setHalignment(HPos.RIGHT);
		grid.getColumnConstraints().setAll(labelColumn, fieldColumn, buttonColumn);
		
		grid.add(topLabel, 0, 0);
		grid.add(csvFileLabel, 0, 1);
		grid.add(headerFileLabel, 0, 2);

		csvFileTextField.setMaxWidth(Double.MAX_VALUE);
		headerFileTextField.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(csvFileTextField, Priority.ALWAYS);
		GridPane.setHgrow(headerFileTextField, Priority.ALWAYS);

		grid.add(csvFileTextField, 1, 1);
		grid.add(headerFileTextField, 1, 2);

		grid.add(browseForCsvFile, 2, 1);
		grid.add(browseForHeaderFile, 2, 2);

		// place checkbox after 'Header file' row
		grid.add(usePresetRegionListCheckBox, 1, 3, 1, 1);
		grid.add(editPresetRegionListButton, 2, 3);
		grid.add(new Separator(Orientation.HORIZONTAL), 0, 4, 3,1);
		
		Button convertButton = utils.createButton("Convert", styles.getBigButtonWidth(),"Converts CSV to XML using header",null);
		Button closeButton = utils.createButton("Close", styles.getBigButtonWidth(),null);
		convertButton.setPrefWidth(Region.USE_COMPUTED_SIZE);
		convertButton.setMinWidth(Region.USE_PREF_SIZE);
		convertButton.setMaxWidth(Region.USE_PREF_SIZE);
		closeButton.setPrefWidth(Region.USE_COMPUTED_SIZE);
		closeButton.setMinWidth(Region.USE_PREF_SIZE);
		closeButton.setMaxWidth(Region.USE_PREF_SIZE);

		closeButton.setOnAction(e -> {
			stage.close();
			});
		
		convertButton.setOnAction(e -> {
			converter();
			});

		
		VBox root = new VBox();
		// Use centralized tiny padding for compact roots
		root.setPadding(styles.getTinyPadding());
		root.setSpacing(5);
         root.setAlignment(Pos.TOP_LEFT);

         HBox buttonBox = new HBox();
         // Use centralized button box padding
         buttonBox.setPadding(styles.getButtonBoxPadding());

         buttonBox.setSpacing(5);
         buttonBox.setAlignment(Pos.CENTER);
		 buttonBox.getChildren().addAll(convertButton,closeButton);


		
 
 	root.getChildren().addAll(grid,buttonBox);
		scene.setRoot(root);

		stage.setScene(scene);
		stage.setOnShown(e -> {
			try {
				double initialWidth = stage.getWidth();
				if (initialWidth > 0) {
					stage.setWidth(initialWidth * 1.5);
				}

				Window owner = stage.getOwner();
				if (owner == null || !owner.isShowing()) {
					owner = UtilsDialogs.getPrimaryOwnerWindow();
				}
				if (owner != null && owner.isShowing()) {
					double w = stage.getWidth();
					double h = stage.getHeight();
					if (w > 0 && h > 0) {
						stage.setX(owner.getX() + ((owner.getWidth() - w) / 2.0));
						stage.setY(owner.getY() + ((owner.getHeight() - h) / 2.0));
					}
				}
			} catch (Exception ignored) {}
		});
	
		
		stage.show();
		
		
    }

//    /**
//     * Shows an "Open File" dialog.
//     *
//     * @param ownerWindow      The parent window for the dialog.
//     * @param title            The title for the dialog window.
//     * @param initialDirectory The directory to open initially.
//     * @param filter           The extension filter to apply.
//     * @return An Optional containing the selected file, or an empty Optional if canceled.
//     */
//    public static File showOpenDialog(Window ownerWindow, String title, File initialDirectory, FileChooser.ExtensionFilter filter) {
//        FileChooser chooser = createAndConfigureChooser(title, initialDirectory, null, filter);
//        File result = chooser.showOpenDialog(ownerWindow);
//        return result;
//    }
	
	private String browseForFile(String filter1,String filter2,String path,String which) {
		String tmp="";
			try {
				File file = FileChooserPlus.showOpenDialog(null, which,new File(path),FileChooserPlus.createExtensionFilter(filter1,filter2));
				tmp=file.getPath();
			} catch(Exception ex) {
				System.out.println("Error in "+which+" "+filter2+" file:");
				System.out.println("  "+ex);
			}
			return tmp;
	}

	
	private void converter() {
		try {
			csvFilename=csvFileTextField.getText();
			headersFilename=headerFileTextField.getText();
			boolean usePresetRegionList = usePresetRegionListCheckBox.isSelected();
			
			File csvFile=new File(csvFilename);
			File outputFile = deriveXmlOutputFile(csvFile);
			if (outputFile == null) {
				showConversionErrorDialog();
				return;
			}
			xmlFilename = outputFile.getPath();
			System.out.println("csv to xml conversion commencing:");
			System.out.println("    csv file: " + csvFilename);
			System.out.println("    header file: " + headersFilename);
			System.out.println("    xml file: " + xmlFilename);
			System.out.println("    use preset region list: " + usePresetRegionList);
			Optional<Document> docOpt = glimpseUtil.CSVToXMLMain.runCSVConversion(
					new File[] { csvFile },
					new File(headersFilename),
					null,
					usePresetRegionList);
			if (!docOpt.isPresent()) {
				showConversionErrorDialog();
				return;
			}

			boolean wroteOutput = glimpseUtil.CSVToXMLMain.writeFile(outputFile, docOpt.get());
			boolean replacedDeleteTokens = wroteOutput
					&& glimpseUtil.CSVToXMLMain.replaceTextInFile(outputFile.toPath(), "DELETE", "");
			if (!wroteOutput || !replacedDeleteTokens) {
				showConversionErrorDialog();
				return;
			}

			boolean hadRowErrors = glimpseUtil.CSVToXMLMain.hadRowErrors();
			String summary = glimpseUtil.CSVToXMLMain.getFirstRowFailureSummary();
			int rowErrorCount = glimpseUtil.CSVToXMLMain.getRowErrorCount();

			String dialogMessage;
			if (hadRowErrors) {
				StringBuilder warning = new StringBuilder();
				warning.append("Warning: XML file was created, but conversion reported ")
						.append(rowErrorCount)
						.append(rowErrorCount == 1 ? " row-level issue." : " row-level issues.")
						.append("\n\nGenerated file:\n")
						.append(xmlFilename);
				if (summary != null && !summary.trim().isEmpty()) {
					warning.append("\n\nFirst issue:\n").append(summary);
				}
				warning.append("\n\nCheck the console for details. Open XML file now?");
				dialogMessage = warning.toString();
			} else {
				dialogMessage = "Successfully created XML file:\n" + xmlFilename + "\n\nOpen XML file now?";
			}

			boolean openNow = utils.selectYesOrNoDialog(dialogMessage);
			if (openNow) {
				files.showFileInTextEditor(xmlFilename);
			}
		} catch (Exception e) {
			System.out.println("Error saving or opening xml file "+xmlFilename);
			System.out.println("  "+e);
			showConversionErrorDialog();
		}
	}

	private File deriveXmlOutputFile(File csvFile) {
		if (csvFile == null) {
			return null;
		}
		String name = csvFile.getName();
		if (name == null || name.trim().isEmpty()) {
			return null;
		}
		String xmlName;
		if (name.toLowerCase().endsWith(".csv")) {
			xmlName = name.substring(0, name.length() - 4) + ".xml";
		} else {
			xmlName = name + ".xml";
		}
		File parent = csvFile.getParentFile();
		if (parent == null) {
			parent = new File(".").getAbsoluteFile();
		}
		return new File(parent, xmlName);
	}

	private void showConversionErrorDialog() {
		String summary = glimpseUtil.CSVToXMLMain.getFirstRowFailureSummary();
		StringBuilder msg = new StringBuilder("CSV to XML conversion encountered errors.");
		if (summary != null && !summary.trim().isEmpty()) {
			msg.append("\n\n").append(summary);
		}
		msg.append("\n\nPlease check the console for additional information.");
		utils.warningMessage(msg.toString());
	}
	
}