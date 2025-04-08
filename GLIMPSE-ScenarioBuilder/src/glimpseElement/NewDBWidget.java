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
import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import gui.Client;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NewDBWidget {
	protected GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	protected GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	protected GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	protected GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

	TextField newDBNameTextField = utils.createTextField();

	public void newDBWidget() {
		System.out.println("Created new DB Widget");
	}

	public void createAndShow() {
		String title = "Create new GCAM output database";

		Stage stage = new Stage();
		stage.setTitle(title);
		// stage.setWidth(200);
		// stage.setHeight(100);
		Scene scene = new Scene(new Group());
		// stage.setResizable(false);

		Label topLabel = utils
				.createLabel("If specified database does not already exist, it will be created in the output folder.");

		Label newDBNameLabel = utils.createLabel("Name:");

		GridPane grid = new GridPane();

		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(0, 10, 0, 10));

		grid.add(topLabel, 0, 0);
		grid.add(newDBNameLabel, 0, 1);
		grid.add(newDBNameTextField, 0, 2);

		grid.add(new Separator(Orientation.HORIZONTAL), 0, 3, 3, 1);

		Button createButton = utils.createButton("Create", styles.bigButtonWid, "Creates new DB or opens existing");
		Button cancelButton = utils.createButton("Cancel", styles.bigButtonWid, null);

		cancelButton.setOnAction(e -> {
			stage.close();
		});

		createButton.setOnAction(e -> {
			creator();
			stage.close();
		});

		VBox root = new VBox();
		root.setPadding(new Insets(2, 2, 2, 2));
		root.setSpacing(5);
		root.setAlignment(Pos.TOP_LEFT);

		HBox buttonBox = new HBox();
		buttonBox.setPadding(new Insets(3, 3, 3, 3));

		buttonBox.setSpacing(5);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().addAll(createButton, cancelButton);

		root.getChildren().addAll(grid, buttonBox);
		scene.setRoot(root);

		stage.setScene(scene);
		stage.show();

	}

	private void creator() {
		try {
			// runs the GCAM model postprocessor
			boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

			String shell = "cmd.exe /C";
			if (isWindows) {
				shell = "cmd.exe /C";
			} else {
				shell = "sh -c";
			}

			String[] cmd = new String[1];
			String command = shell + " cd " + vars.get("gCamPPExecutableDir") + " & dir & java -jar "
					+ vars.get("gCamPPExecutableDir") + File.separator + vars.get("gCamPPExecutable") + " -o "
					+ vars.getgCamExecutableDir() + File.separator + ".." + File.separator + "output" + File.separator
					+ newDBNameTextField.getText();
			// + " -l modelinterface.log";
			// " -dbOpen "
			cmd[0] = command;
			System.out.println("Starting ORDModelInterface...");
			System.out.println("   cmd:" + cmd[0]);
			try {
				Client.gCAMPPExecutionThread.addRunnableCmdsToExecuteQueue(cmd);
			} catch (Exception e) {
				utils.warningMessage("Problem starting up post-processor.");
				System.out.println("Error in trying to start up post-processor:");
				System.out.println(e);
			}

		} catch (Exception e) {
			System.out.println("Error creating new database " + e);
			System.out.println("  " + e);
		}
	}

}
